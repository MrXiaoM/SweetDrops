package top.mrxiaom.sweet.drops.func.entry.drop;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.sweet.drops.func.entry.AbstractDropItem;

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
