package top.mrxiaom.sweet.drops.func.entry.drop;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.sweet.drops.func.entry.AbstractDropItem;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.sweet.drops.SweetDrops.format;

public class DropPrefeb extends AbstractDropItem {
    public final String id;
    public DropPrefeb(double rate, String id, IntRange amount, boolean end) {
        super(rate, amount, end);
        this.id = id;
    }

    @Override
    public String toString() {
        return "prefeb " + rate + " " + id + " " + format(amount) + (end ? " end" : "");
    }

    @Override
    public List<ItemStack> getItems(Player player, int amount) {
        List<ItemStack> items = new ArrayList<>();
        // TODO: 暂未编写预制体管理器
        return items;
    }
}
