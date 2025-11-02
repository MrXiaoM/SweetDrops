package top.mrxiaom.sweet.drops.func.entry.drop;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pluginbase.utils.depend.PAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static top.mrxiaom.pluginbase.func.AbstractPluginHolder.t;

public abstract class AbstractDropItem implements IDropItem {
    public final double rate;
    public final IntRange amount;
    public final boolean end;
    public final List<String> commands;
    protected AbstractDropItem(double rate, IntRange amount, boolean end, List<String> commands) {
        this.rate = Math.max(0.0, Math.min(1.0, rate));
        this.amount = amount;
        this.end = end;
        this.commands = commands;
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
        return getItems(player, finalAmount);
    }

    @Override
    public void executeCommands(Player player, int itemAmount, int fortune) {
        if (commands.isEmpty()) return;
        List<String> list = new ArrayList<>();
        for (String command : commands) {
            list.add(command
                    .replace("%amount%", String.valueOf(itemAmount))
                    .replace("%fortune%", String.valueOf(fortune)));
        }
        for (String s : PAPI.setPlaceholders(player, list)) {
            if (s.startsWith("[console]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.substring(9));
            } else if (s.startsWith("[player]")) {
                Bukkit.dispatchCommand(player, s.substring(8));
            } else if (s.startsWith("[message]")) {
                t(player, s.substring(9));
            }
        }
    }
}
