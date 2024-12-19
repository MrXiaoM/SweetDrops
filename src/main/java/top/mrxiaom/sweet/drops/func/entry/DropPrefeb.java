package top.mrxiaom.sweet.drops.func.entry;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DropPrefeb extends AbstractDropItem {
    protected DropPrefeb(double rate, IntRange amount, boolean end) {
        super(rate, amount, end);
    }

    @Override
    public List<ItemStack> getItems(Player player, int amount) {
        List<ItemStack> items = new ArrayList<>();
        // TODO: 暂未编写预制体管理器
        return items;
    }
}
