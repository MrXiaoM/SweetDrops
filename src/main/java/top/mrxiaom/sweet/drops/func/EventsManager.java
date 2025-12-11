package top.mrxiaom.sweet.drops.func;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.drops.SweetDrops;
import top.mrxiaom.sweet.drops.func.entry.Event;
import top.mrxiaom.sweet.drops.func.entry.drop.IDropItem;
import top.mrxiaom.sweet.drops.func.entry.item.IMatcher;

import java.io.File;
import java.util.*;

@AutoRegister
public class EventsManager extends AbstractModule implements Listener {
    final Map<String, Event> eventsById = new HashMap<>();
    final Map<String, Map<Material, List<Event>>> eventsByWorld = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static boolean supportKey;
    public EventsManager(SweetDrops plugin) {
        super(plugin);
        registerEvents();
        supportKey = Util.isPresent("org.bukkit.NamespacedKey");
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        List<String> folders = config.getStringList("events-folders");
        eventsById.clear();
        for (String s : folders) {
            File folder = s.startsWith("./") ? new File(plugin.getDataFolder(), s.substring(2)) : new File(s);
            if (s.equals("./events") && !folder.exists()) {
                Util.mkdirs(folder);
                plugin.saveResource("events/example.yml", new File(folder, "example.yml"));
            }
            if (folder.isDirectory() && !new File(folder, ".ignore").exists()) {
                reloadConfig(folder);
            }
        }
        eventsByWorld.clear();
        Set<Material> blocks = new TreeSet<>();
        for (Event event : eventsById.values()) {
            for (String world : event.worlds) {
                Map<Material, List<Event>> map = eventsByWorld.get(world);
                if (map == null) map = new TreeMap<>();
                for (Material block : event.blocks) {
                    blocks.add(block);
                    List<Event> list = map.get(block);
                    if (list == null) list = new ArrayList<>();
                    list.add(event);
                    map.put(block, list);
                }
                eventsByWorld.put(world, map);
            }
        }
        info(String.format(
                "共加载 %d 个事件配置，其范围覆盖了 %d 个世界，共 %d 种方块。",
                eventsById.size(), eventsByWorld.size(), blocks.size()));
    }

    private void reloadConfig(File folder) {
        File[] files = folder.listFiles();
        if (files != null) for (File file : files) {
            if (file.isDirectory()) {
                reloadConfig(file);
                continue;
            }
            String name = file.getName();
            if (name.endsWith(".yml")) {
                String id = name.substring(0, name.length() - 4);
                if (eventsById.containsKey(id)) {
                    warn("事件ID重复：" + file.getAbsolutePath());
                    continue;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Event loaded = Event.load(config, id);
                eventsById.put(id, loaded);
            }
        }
    }

    @Nullable
    public List<Event> get(Block block) {
        Map<Material, List<Event>> map = eventsByWorld.get(block.getWorld().getName());
        return map == null ? null : map.get(block.getType());
    }

    public static String getKey(Enchantment enchant) {
        if (!supportKey) {
            return enchant.getName();
        }
        try {
            return enchant.getKey().getKey();
        } catch (Throwable ignored) {
            return enchant.getName();
        }
    }

    private void cancelDrops(BlockBreakEvent e) {
        try {
            e.setExpToDrop(0);
            e.setDropItems(false);
        } catch (Throwable ignored) {
            e.setCancelled(true);
            // TODO: 为低版本播放方块破坏音效
            e.getBlock().setType(Material.AIR);
        }
    }

    private boolean isPreferredTool(Block block, ItemStack item) {
        try {
            return block.isPreferredTool(item);
        } catch (Throwable ignored) {
            return true;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.getBlock().setMetadata("SweetDrops_PlayerPlaced", new FixedMetadataValue(plugin, true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (!gameMode.equals(GameMode.SURVIVAL) && !gameMode.equals(GameMode.ADVENTURE)) return;
        Block block = e.getBlock();
        if (block.hasMetadata("SweetDrops_PlayerPlaced")) return;
        List<Event> events = get(block);
        if (events == null) return;
        PlayerInventory inv = player.getInventory();
        ItemStack mainHand = inv.getItemInHand();
        int fortune;
        boolean preferredTool = isPreferredTool(block, mainHand);
        if (preferredTool) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null) {
                fortune = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
            } else {
                fortune = 0;
            }
        } else {
            fortune = 0;
        }
        if (plugin.debug && player.isOp()) {
            t(player, "[挖掘方块][" + block.getType() + "] 有事件 " + events.size() + " 个，时运等级: " + fortune);
        }
        ItemMeta meta = mainHand.getItemMeta();
        Map<Enchantment, Integer> enchants = meta == null ? null : meta.getEnchants();
        for (Event event : events) {
            if (plugin.debug && player.isOp()) {
                t(player, "  处理事件 " + event.id);
            }
            if (event.requirePreferredTool && !preferredTool) {
                if (plugin.debug && player.isOp()) {
                    t(player, "    工具不合适 &7(require-preferred-tool)");
                }
                continue;
            }
            boolean matchTool = event.tools.isEmpty();
            for (IMatcher tool : event.tools) {
                if (tool.match(mainHand)) {
                    matchTool = true;
                    break;
                }
            }
            if (!matchTool) {
                if (plugin.debug && player.isOp()) {
                    t(player, "    工具不匹配 &7(tools)");
                }
                continue;
            }
            if (!event.enchantments.isEmpty()) {
                boolean matchEnchant = true;
                for (Map.Entry<String, Integer> entry : event.enchantments.entrySet()) {
                    boolean match = false;
                    for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
                        String key = getKey(enchant.getKey());
                        if (entry.getKey().equalsIgnoreCase(key)) {
                            int requireLevel = entry.getValue();
                            match = requireLevel == 0 || enchant.getValue() >= requireLevel;
                            break;
                        }
                    }
                    if (!match) {
                        if (plugin.debug && player.isOp()) {
                            t(player, "    附魔不匹配 &7(enchantments)&f: " + entry.getKey());
                        }
                        matchEnchant = false;
                        break;
                    }
                }
                if (!matchEnchant) {
                    continue;
                }
            }
            if (!event.bannedEnchantments.isEmpty()) {
                boolean matchEnchant = false;
                for (String enchant : event.bannedEnchantments) {
                    for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                        String key = getKey(entry.getKey());
                        if (enchant.equalsIgnoreCase(key)) {
                            if (plugin.debug && player.isOp()) {
                                t(player, "    附魔存在匹配 &7(banned-enchantments)&f: " + key);
                            }
                            matchEnchant = true;
                            break;
                        }
                    }
                }
                if (matchEnchant) {
                    continue;
                }
            }
            if (plugin.debug && player.isOp()) {
                t(player, "    &f一切条件&a匹配&f，正在进行判定");
            }
            if (event.cancelAll) {
                cancelDrops(e);
            }
            boolean toInv = event.needToInv(player, mainHand);
            for (IDropItem item : event.items) {
                boolean success = item.checkRate();
                if (plugin.debug && player.isOp()) {
                    t(player, "      &7[&f掉落物判定" + (success ? "&a成功" : "&c失败") + "&7] &f" + item);
                }
                if (success) {
                    boolean executeCommand = !event.overflowDisappear;
                    int itemAmount = 0;
                    double multipler = event.randomFortuneMultipler(fortune);
                    List<ItemStack> list = item.generateItems(player, event, multipler);
                    if (list != null && !list.isEmpty()) {
                        List<ItemStack> dropItems = new ArrayList<>();
                        if (toInv) {
                            for (ItemStack itemStack : list) {
                                if (itemStack.getType().equals(Material.AIR)) {
                                    executeCommand = true;
                                    continue;
                                }
                                int amount = itemStack.getAmount();
                                Collection<ItemStack> last = inv.addItem(itemStack).values();
                                if (!last.isEmpty()) {
                                    ItemStack next = last.iterator().next();
                                    if (next.getAmount() < amount) {
                                        executeCommand = true;
                                        if (event.overflowDisappear) {
                                            itemAmount += amount - next.getAmount();
                                        }
                                    } else if (!event.overflowDisappear) {
                                        itemAmount += amount;
                                    }
                                } else {
                                    itemAmount += amount;
                                    executeCommand = true;
                                }
                                if (!event.overflowDisappear) {
                                    dropItems.addAll(last);
                                }
                                last.clear();
                                last = null;
                            }
                        } else {
                            for (ItemStack itemStack : list) {
                                if (itemStack.getType().equals(Material.AIR)) continue;
                                dropItems.add(itemStack);
                            }
                        }
                        list.clear();
                        list = null;
                        if (!dropItems.isEmpty()) {
                            World world = block.getWorld();
                            Location loc = block.getLocation().clone().add(0, 0.5, 0);
                            for (ItemStack dropItem : dropItems) {
                                world.dropItem(loc, dropItem);
                            }
                            dropItems.clear();
                            dropItems = null;
                        }
                    } else if (plugin.debug && player.isOp()) {
                        t(player, "      生成的物品列表为空 &7(multipler=" + multipler + ")");
                    }
                    if (executeCommand) {
                        item.executeCommands(player, itemAmount, fortune);
                    }
                    if (item.isEnd()) {
                        break;
                    }
                }
            }
        }
    }
}
