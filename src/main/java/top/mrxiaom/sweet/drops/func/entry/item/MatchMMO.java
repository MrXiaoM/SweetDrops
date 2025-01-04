package top.mrxiaom.sweet.drops.func.entry.item;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MatchMMO implements IMatcher {
    String type;
    String id;
    public MatchMMO(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public boolean match(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR) || item.getAmount() == 0) return false;
        return NBT.get(item, nbt -> {
            if (!nbt.hasTag("MMOITEMS_ITEM_TYPE") || !nbt.hasTag("MMOITEMS_ITEM_ID")) return false;
            return nbt.getString("MMOITEMS_ITEM_TYPE").equals(type) && nbt.getString("MMOITEMS_ITEM_ID").equals(id);
        });
    }
}
