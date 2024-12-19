package top.mrxiaom.sweet.drops.mythic;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Mythic5 implements IMythic {
    MythicBukkit mythic = MythicBukkit.inst();

    @Nullable
    @Override
    public ItemStack getItem(String item) {
        return mythic.getItemManager().getItemStack(item);
    }
}
