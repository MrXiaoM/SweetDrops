package top.mrxiaom.sweet.drops.func.entry.drop;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.sweet.drops.func.PrefebManager;
import top.mrxiaom.sweet.drops.func.entry.Prefeb;

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
        Prefeb prefeb = PrefebManager.inst().get(id);
        if (prefeb != null) {
            if (prefeb.unique) for (int i = 0; i < amount; i++) {
                items.add(prefeb.generate(player));
            } else {
                ItemStack template = prefeb.generate(player);
                int need = amount;
                int stack = template.getType().getMaxStackSize();
                while (need > 0) {
                    int count;
                    if (need > stack) {
                        count = stack;
                        need -= stack;
                    } else {
                        count = need;
                        need = 0;
                    }
                    ItemStack item = template.clone();
                    item.setAmount(count);
                    items.add(item);
                }
            }
        }
        return items;
    }
}
