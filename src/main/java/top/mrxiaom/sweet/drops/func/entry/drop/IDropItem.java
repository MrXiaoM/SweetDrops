package top.mrxiaom.sweet.drops.func.entry.drop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.sweet.drops.func.entry.Event;

import java.util.List;

public interface IDropItem {
    boolean checkRate();
    boolean isEnd();
    @Deprecated
    List<ItemStack> generateItems(Player player, double multiple);
    List<ItemStack> generateItems(Player player, Event evnet, double multiple);
    List<ItemStack> getItems(Player player, int amount);
    void executeCommands(Player player, int itemAmount, int fortune);
}
