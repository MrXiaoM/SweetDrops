package top.mrxiaom.sweet.drops.func.entry.drop;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.sweet.drops.SweetDrops;
import top.mrxiaom.sweet.drops.mythic.IMythic;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.sweet.drops.SweetDrops.format;

public class DropMythic extends AbstractDropItem {
    public final String id;
    public DropMythic(double rate, String id, IntRange amount, boolean end) {
        super(rate, amount, end);
        this.id = id;
    }

    @Override
    public String toString() {
        return "mythic " + rate + " " + id + " " + format(amount) + (end ? " end" : "");
    }

    @Override
    public List<ItemStack> getItems(Player player, int amount) {
        List<ItemStack> items = new ArrayList<>();
        IMythic mythic = SweetDrops.getInstance().getMythic();
        ItemStack template = mythic.getItem(id);
        if (template != null) {
            int maxSize = template.getType().getMaxStackSize();
            int current = amount;
            while (current > 0) {
                if (current > maxSize) {
                    ItemStack item = template.clone();
                    item.setAmount(maxSize);
                    items.add(item);
                    current -= maxSize;
                } else {
                    ItemStack item = template.clone();
                    item.setAmount(current);
                    items.add(item);
                    current = 0;
                }
            }
        }
        return items;
    }
}
