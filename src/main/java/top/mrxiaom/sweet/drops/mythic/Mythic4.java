package top.mrxiaom.sweet.drops.mythic;

import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Mythic4 implements IMythic {
    MythicMobs mythic = MythicMobs.inst();

    @Nullable
    @Override
    public ItemStack getItem(String item) {
        return mythic.getItemManager().getItemStack(item);
    }
}
