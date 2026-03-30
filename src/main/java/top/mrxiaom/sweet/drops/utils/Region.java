package top.mrxiaom.sweet.drops.utils;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.List;

public class Region {
    private final List<String> worldsWhiteList, worldsBlackList;
    private final int fromX, fromY, fromZ;
    private final int toX, toY, toZ;

    public Region(List<String> worldsWhiteList, List<String> worldsBlackList, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        this.worldsWhiteList = worldsWhiteList;
        this.worldsBlackList = worldsBlackList;
        this.fromX = Math.min(fromX, toX);
        this.fromY = Math.min(fromY, toY);
        this.fromZ = Math.min(fromZ, toZ);
        this.toX = Math.max(fromX, toX);
        this.toY = Math.max(fromY, toY);
        this.toZ = Math.max(fromZ, toZ);
    }

    public int fromX() {
        return fromX;
    }

    public int fromY() {
        return fromY;
    }

    public int fromZ() {
        return fromZ;
    }

    public int toX() {
        return toX;
    }

    public int toY() {
        return toY;
    }

    public int toZ() {
        return toZ;
    }

    public boolean isForWorld(World world) {
        return isForWorld(world.getName());
    }

    public boolean isForWorld(String world) {
        if (!worldsWhiteList.isEmpty() && !worldsWhiteList.contains(world)) {
            return false;
        }
        return !worldsBlackList.contains(world);
    }

    public boolean isInRegion(Block block) {
        return isInRegion(block.getX(), block.getY(), block.getZ());
    }

    public boolean isInRegion(double x, double y, double z) {
        return x >= fromX && x <= toX
                && y >= fromY && y <= toY
                && z >= fromZ && z <= toZ;
    }

    public static Region load(ConfigurationSection config) {
        String[] fromSplit = config.getString("from", "").split(",");
        String[] toSplit = config.getString("to", "").split(",");
        if (fromSplit.length != 3 || toSplit.length != 3) return null;
        Integer fromX = Util.parseInt(fromSplit[0].trim()).orElse(null);
        Integer fromY = Util.parseInt(fromSplit[1].trim()).orElse(null);
        Integer fromZ = Util.parseInt(fromSplit[2].trim()).orElse(null);
        if (fromX == null || fromY == null || fromZ == null) return null;
        Integer toX = Util.parseInt(toSplit[0].trim()).orElse(null);
        Integer toY = Util.parseInt(toSplit[1].trim()).orElse(null);
        Integer toZ = Util.parseInt(toSplit[2].trim()).orElse(null);
        if (toX == null || toY == null || toZ == null) return null;
        List<String> worldsWhiteList = config.getStringList("worlds-whitelist");
        List<String> worldsBlackList = config.getStringList("worlds-blacklist");
        return new Region(worldsWhiteList, worldsBlackList, fromX, fromY, fromZ, toX, toY, toZ);
    }
}
