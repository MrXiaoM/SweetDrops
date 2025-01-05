package top.mrxiaom.sweet.drops.func.entry.drop;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static top.mrxiaom.sweet.drops.SweetDrops.format;

public class DropVanilla extends AbstractDropItem {
    public final Material material;
    public DropVanilla(double rate, Material material, IntRange amount, boolean end) {
        super(rate, amount, end);
        this.material = material;
    }

    @Override
    public String toString() {
        return "mc " + rate + " " + material.name() + " " + format(amount) + (end ? " end" : "");
    }

    @Override
    public List<ItemStack> getItems(Player player, int amount) {
        List<ItemStack> items = new ArrayList<>();
        int maxSize = material.getMaxStackSize();
        int current = amount;
        while (current > 0) {
            if (current > maxSize) {
                items.add(new ItemStack(material, maxSize));
                current -= maxSize;
            } else {
                items.add(new ItemStack(material, current));
                current = 0;
            }
        }
        return items;
    }
}
