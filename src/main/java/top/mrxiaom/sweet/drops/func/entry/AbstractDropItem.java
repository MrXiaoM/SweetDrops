package top.mrxiaom.sweet.drops.func.entry;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.sweet.drops.func.entry.drop.IDropItem;

import java.util.List;
import java.util.Random;

public abstract class AbstractDropItem implements IDropItem {
    public final double rate;
    public final IntRange amount;
    public final boolean end;
    protected AbstractDropItem(double rate, IntRange amount, boolean end) {
        this.rate = Math.max(0.0, Math.min(1.0, rate));
        this.amount = amount;
        this.end = end;
    }

    @Override
    public boolean checkRate() {
        double rand = new Random().nextDouble();
        return rand > 1.0 - rate;
    }

    @Override
    public boolean isEnd() {
        return end;
    }

    @Override
    public List<ItemStack> generateItems(Player player, double multiple) {
        int i = amount.getMaximumInteger() - amount.getMinimumInteger() + 1;
        int finalAmount = amount.getMinimumInteger() + new Random().nextInt(i);
        return generateItems(player, finalAmount);
    }
}
