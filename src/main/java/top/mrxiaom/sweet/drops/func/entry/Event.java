package top.mrxiaom.sweet.drops.func.entry;

import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.*;

public class Event {
    public final String id;
    public final Set<String> worlds;
    public final Set<Material> blocks;
    public final boolean cancelAll,cancelIfDropAny;
    public final String permToInventory;
    public final List<IDropItem> items;
    public final IRound fortuneRounding;
    public final Map<Integer, DoubleRange> fortuneMultiples;

    Event(String id, Set<String> worlds, Set<Material> blocks, boolean cancelAll, boolean cancelIfDropAny, String permToInventory, List<IDropItem> items, IRound fortuneRounding, Map<Integer, DoubleRange> fortuneMultiples) {
        this.id = id;
        this.worlds = worlds;
        this.blocks = blocks;
        this.cancelAll = cancelAll;
        this.cancelIfDropAny = cancelIfDropAny;
        this.permToInventory = permToInventory;
        this.items = items;
        this.fortuneRounding = fortuneRounding;
        this.fortuneMultiples = fortuneMultiples;
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
        boolean cancelAll = config.getBoolean("cancel.all"),
                cancelIfDropAny = config.getBoolean("cancel.if-drop-any");
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
        return new Event(id, worlds, blocks, cancelAll, cancelIfDropAny, permToInventory, items, rounding, multiples);
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
