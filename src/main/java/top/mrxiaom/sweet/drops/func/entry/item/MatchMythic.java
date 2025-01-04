package top.mrxiaom.sweet.drops.func.entry.item;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MatchMythic implements IMatcher {
    String mythicItem;
    public MatchMythic(String mythicItem) {
        this.mythicItem = mythicItem;
    }

    @Override
    public boolean match(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR) || item.getAmount() == 0) return false;
        return NBT.get(item, nbt -> nbt.hasTag("MYTHIC_TYPE") && nbt.getString("MYTHIC_TYPE").equals(mythicItem));
    }
}
