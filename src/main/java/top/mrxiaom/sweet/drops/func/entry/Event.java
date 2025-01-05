package top.mrxiaom.sweet.drops.func.entry;

import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permissible;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.drops.func.entry.drop.DropMythic;
import top.mrxiaom.sweet.drops.func.entry.drop.DropPrefeb;
import top.mrxiaom.sweet.drops.func.entry.drop.DropVanilla;
import top.mrxiaom.sweet.drops.func.entry.drop.IDropItem;
import top.mrxiaom.sweet.drops.func.entry.item.IMatcher;
import top.mrxiaom.sweet.drops.func.entry.item.MatchMMO;
import top.mrxiaom.sweet.drops.func.entry.item.MatchMythic;
import top.mrxiaom.sweet.drops.func.entry.item.MatchVanilla;
import top.mrxiaom.sweet.drops.func.entry.round.IRound;
import top.mrxiaom.sweet.drops.func.entry.round.RoundCeil;
import top.mrxiaom.sweet.drops.func.entry.round.RoundFloor;
import top.mrxiaom.sweet.drops.func.entry.round.RoundRound;

import java.util.*;

public class Event {
    public final String id;
    public final Set<String> worlds;
    public final Set<Material> blocks;
    public final List<IMatcher> tools;
    public final Map<String, Integer> enchantments;
    public final boolean cancelAll,cancelIfDropAny, requirePreferredTool;
    public final String permToInventory;
    public final List<IDropItem> items;
    public final IRound fortuneRounding;
    public final Map<Integer, DoubleRange> fortuneMultiples;

    Event(String id, Set<String> worlds, Set<Material> blocks, List<IMatcher> tools, Map<String, Integer> enchantments, boolean requirePreferredTool, boolean cancelAll, boolean cancelIfDropAny, String permToInventory, List<IDropItem> items, IRound fortuneRounding, Map<Integer, DoubleRange> fortuneMultiples) {
        this.id = id;
        this.worlds = worlds;
        this.blocks = blocks;
        this.tools = tools;
        this.enchantments = enchantments;
        this.requirePreferredTool = requirePreferredTool;
        this.cancelAll = cancelAll;
        this.cancelIfDropAny = cancelIfDropAny;
        this.permToInventory = permToInventory;
        this.items = items;
        this.fortuneRounding = fortuneRounding;
        this.fortuneMultiples = fortuneMultiples;
    }

    public boolean needToInv(Permissible permissible) {
        return permToInventory != null && permissible.hasPermission(permToInventory);
    }

    public double randomFortuneMultipler(int fortune) {
        DoubleRange range = fortuneMultiples.get(fortune);
        if (range == null) return 1.0;
        double rand = new Random().nextInt(1919810) / 1919809.0;
        double len = range.getMaximumDouble() - range.getMinimumDouble();
        return range.getMinimumDouble() + rand * len;
    }

    public static Event load(ConfigurationSection config, String id) {
        Set<String> worlds = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        worlds.addAll(config.getStringList("worlds"));
        Set<Material> blocks = new TreeSet<>();
        for (String s : config.getStringList("blocks")) {
            Material material = Util.valueOr(Material.class, s, null);
            if (material != null) {
                blocks.add(material);
            }
        }
        List<IMatcher> tools = new ArrayList<>();
        for (String s : config.getStringList("tools")) {
            if (s.startsWith("mythic:")) {
                String mythicItem = s.substring(7);
                tools.add(new MatchMythic(mythicItem));
                continue;
            }
            if (s.startsWith("mmo:")) {
                String[] split = s.substring(4).split(":", 2);
                if (split.length != 2) continue;
                String type = split[0];
                String itemId = split[1];
                tools.add(new MatchMMO(type, itemId));
                continue;
            }
            Material material = Util.valueOr(Material.class, s, null);
            if (material != null) {
                tools.add(new MatchVanilla(material));
                continue;
            }
        }
        Map<String, Integer> enchantments = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String s : config.getStringList("enchantments")) {
            String[] split = s.split(":", 2);
            String enchant = split[0];
            int level = split.length == 1 ? 0 : Math.max(0, Util.parseInt(split[1]).orElse(0));
            enchantments.put(enchant, level);
        }
        boolean requirePreferredTool = config.getBoolean("require-preferred-tool", false),
                cancelAll = config.getBoolean("cancel.all", false),
                cancelIfDropAny = config.getBoolean("cancel.if-drop-any", false);
        String permToInventory = config.getString("perm-to-inventory");
        List<IDropItem> items = new ArrayList<>();
        for (String s : config.getStringList("items")) {
            String[] split = s.split(" ");
            if (split.length < 3) continue;
            String type = split[0];
            String rateNum = split[1].replace("%", "");
            Double rate = (split[1].endsWith("%")
                    ? Util.parseDouble(rateNum).map(it -> it / 100.0)
                    : Util.parseDouble(rateNum)).orElse(null);
            if (rate == null) continue;
            String item = split[2];
            IntRange amount = getIntRange(split.length >= 4 ? split[3] : null).orElseGet(() -> new IntRange(1));
            boolean end = split.length >= 5 && split[4].equals("end");
            if (type.equalsIgnoreCase("mc")) {
                Material material = Util.valueOr(Material.class, item, null);
                if (material != null) {
                    items.add(new DropVanilla(rate, material, amount, end));
                    continue;
                }
            }
            if (type.equalsIgnoreCase("prefeb")) {
                items.add(new DropPrefeb(rate, item, amount, end));
                continue;
            }
            if (type.equalsIgnoreCase("mythic")) {
                items.add(new DropMythic(rate, item, amount, end));
                continue;
            }
        }
        String roundingType = config.getString("fortune.rounding", "");
        IRound rounding;
        if (roundingType.equals("ceil")) rounding = RoundCeil.INSTANCE;
        else if (roundingType.equals("floor")) rounding = RoundFloor.INSTANCE;
        else rounding = RoundRound.INSTANCE;
        Map<Integer, DoubleRange> multiples = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("fortune.multiples");
        if (section != null) for (String key : section.getKeys(false)) {
            Integer level = Util.parseInt(key).orElse(null);
            if (level == null) continue;
            DoubleRange multipler = getDoubleRange(section.getString(key)).orElseGet(() -> new DoubleRange(1.0));
            multiples.put(level, multipler);
        }
        return new Event(id, worlds, blocks, tools, enchantments, requirePreferredTool, cancelAll, cancelIfDropAny, permToInventory, items, rounding, multiples);
    }

    public static Optional<IntRange> getIntRange(String s) {
        if (s == null) return Optional.empty();
        if (s.contains("-")) {
            String[] split = s.split("-", 2);
            if (split.length != 2) return Optional.empty();
            Integer low = Util.parseInt(split[0]).orElse(null);
            Integer high = Util.parseInt(split[1]).orElse(null);
            if (low == null || high == null) return Optional.empty();
            return Optional.of(new IntRange(low, high));
        } else {
            return Util.parseInt(s).map(IntRange::new);
        }
    }

    public static Optional<DoubleRange> getDoubleRange(String s) {
        if (s == null) return Optional.empty();
        if (s.contains("-")) {
            String[] split = s.split("-", 2);
            if (split.length != 2) return Optional.empty();
            Double low = Util.parseDouble(split[0]).orElse(null);
            Double high = Util.parseDouble(split[1]).orElse(null);
            if (low == null || high == null) return Optional.empty();
            return Optional.of(new DoubleRange(low, high));
        } else {
            return Util.parseDouble(s).map(DoubleRange::new);
        }
    }
}
