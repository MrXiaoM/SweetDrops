package top.mrxiaom.sweet.drops.func.entry.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MatchVanilla implements IMatcher {
    Material material;
    public MatchVanilla(Material material) {
        this.material = material;
    }

    @Override
    public boolean match(ItemStack item) {
        return item != null && item.getType().equals(material);
    }
}
