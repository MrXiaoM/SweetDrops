package top.mrxiaom.sweet.drops.func.entry.drop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IDropItem {
    boolean checkRate();
    boolean isEnd();
    List<ItemStack> generateItems(Player player, double multiple);
    List<ItemStack> getItems(Player player, int amount);
    void executeCommands(Player player, int itemAmount, int fortune);
}
