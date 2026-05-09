package top.mrxiaom.sweet.drops.mythic;

import de.tr7zw.changeme.nbtapi.NBT;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Mythic4 implements IMythic {
    MythicMobs mythic = MythicMobs.inst();

    @Nullable
    @Override
    public ItemStack getItem(String item) {
        return mythic.getItemManager().getItemStack(item);
    }

    @Nullable
    @Override
    public String getMythicId(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) return null;
        return NBT.get(item, nbt -> {
            if (nbt.hasTag("MYTHIC_TYPE")) {
                return nbt.getString("MYTHIC_TYPE");
            } else {
                return null;
            }
        });
    }
}
