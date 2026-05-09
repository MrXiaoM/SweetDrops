package top.mrxiaom.sweet.drops.mythic;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IMythic {
    @Nullable
    ItemStack getItem(String item);

    @Nullable
    String getMythicId(ItemStack item);
}
