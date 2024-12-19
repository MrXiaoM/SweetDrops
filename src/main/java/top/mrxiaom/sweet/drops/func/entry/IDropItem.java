package top.mrxiaom.sweet.drops.func.entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IDropItem {
    boolean checkRate();
    List<ItemStack> generateItems(Player player, double multiple);
    List<ItemStack> getItems(Player player, int amount);
}
