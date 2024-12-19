package top.mrxiaom.sweet.drops.func;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.drops.SweetDrops;
import top.mrxiaom.sweet.drops.func.entry.Event;
import top.mrxiaom.sweet.drops.func.entry.IDropItem;

import java.io.File;
import java.util.*;

@AutoRegister
public class EventsManager extends AbstractModule {
    final Map<String, Event> eventsById = new HashMap<>();
    final Map<String, Map<Material, List<Event>>> eventsByWorld = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public EventsManager(SweetDrops plugin) {
        super(plugin);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (!gameMode.equals(GameMode.SURVIVAL) && !gameMode.equals(GameMode.ADVENTURE)) return;
        Block block = e.getBlock();
        List<Event> events = get(block);
        if (events == null) return;
        PlayerInventory inv = player.getInventory();
        ItemStack mainHand = inv.getItemInMainHand();
        int fortune;
        if (block.isPreferredTool(mainHand)) {
            ItemMeta meta = mainHand.getItemMeta();
            if (meta != null) {
                fortune = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
            } else {
                fortune = 0;
            }
        } else {
            fortune = 0;
        }
        for (Event event : events) {
            if (event.cancelAll) {
                e.setDropItems(false);
                e.setExpToDrop(0);
            }
            boolean toInv = event.needToInv(player);
            for (IDropItem item : event.items) {
                if (item.checkRate()) {
                    double multipler = event.randomFortuneMultipler(fortune);
                    List<ItemStack> list = item.generateItems(player, multipler);
                    if (list != null) {
                        List<ItemStack> dropItems = new ArrayList<>();
                        if (toInv) {
                            for (ItemStack itemStack : list) {
                                Collection<ItemStack> last = inv.addItem(itemStack).values();
                                dropItems.addAll(last);
                            }
                        } else {
                            dropItems.addAll(list);
                        }
                        if (!dropItems.isEmpty()) {
                            World world = block.getWorld();
                            Location loc = block.getLocation().clone().add(0, 0.5, 0);
                            for (ItemStack dropItem : dropItems) {
                                world.dropItem(loc, dropItem);
                            }
                        }
                    }
                    if (item.isEnd()) {
                        break;
                    }
                }
            }
        }
    }
}
