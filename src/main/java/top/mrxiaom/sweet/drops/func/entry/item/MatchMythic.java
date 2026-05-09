package top.mrxiaom.sweet.drops.func.entry.item;

import org.bukkit.inventory.ItemStack;
import top.mrxiaom.sweet.drops.mythic.IMythic;

public class MatchMythic implements IMatcher {
    private final IMythic mythic;
    private final String mythicItem;
    public MatchMythic(IMythic mythic, String mythicItem) {
        this.mythic = mythic;
        this.mythicItem = mythicItem;
    }

    @Override
    public boolean match(ItemStack item) {
        return mythicItem.equals(mythic.getMythicId(item));
    }
}
