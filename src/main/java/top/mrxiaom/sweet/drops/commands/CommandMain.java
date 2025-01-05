package top.mrxiaom.sweet.drops.commands;
        
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.drops.SweetDrops;
import top.mrxiaom.sweet.drops.func.AbstractModule;
import top.mrxiaom.sweet.drops.func.PrefebManager;
import top.mrxiaom.sweet.drops.func.entry.Prefeb;
import top.mrxiaom.sweet.drops.func.entry.drop.DropPrefeb;

import java.util.*;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(SweetDrops plugin) {
        super(plugin);
        registerCommand("sweetdrops", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.reloadConfig();
            return t(sender, "&a配置文件已重载");
        }
        if (args.length == 1 && "debug".equalsIgnoreCase(args[0]) && sender.isOp()) {
            plugin.debug = !plugin.debug;
            return t(sender, "&f调试模式已" + (plugin.debug ? "&a开启" : "&c关闭"));
        }
        if (args.length >= 2 && "prefeb".equalsIgnoreCase(args[0]) && sender.isOp()) {
            String id = args[1];
            Prefeb prefeb = PrefebManager.inst().get(id);
            if (prefeb == null) {
                return t(sender, "&e找不到预制体 &b" + prefeb);
            }
            int count;
            if (args.length >= 3) {
                count = Util.parseInt(args[2]).orElse(0);
                if (count <= 0) {
                    return t(sender, "&e请输入正确的数量");
                }
            } else {
                count = 1;
            }
            Player player = sender instanceof Player ? (Player) sender : null;
            if (args.length >= 4) {
                player = Util.getOnlinePlayer(args[3]).orElse(null);
                if (player == null) {
                    return t(sender, "&e玩家 &b" + args[3] + " &e不在线");
                }
            }
            if (player == null) {
                return t(sender, "&e该命令只能由玩家执行");
            }
            PlayerInventory inv = player.getInventory();
            List<ItemStack> items = DropPrefeb.getItems(player, prefeb, count);
            List<ItemStack> dropItems = new ArrayList<>();
            for (ItemStack itemStack : items) {
                Collection<ItemStack> last = inv.addItem(itemStack).values();
                dropItems.addAll(last);
            }
            if (!dropItems.isEmpty()) {
                World world = player.getWorld();
                Location loc = player.getLocation().clone().add(0, 0.5, 0);
                for (ItemStack dropItem : dropItems) {
                    world.dropItem(loc, dropItem);
                }
            }
            return t(sender, "&e已给予玩家 &b" + player + "&e 预制体 &b" + id + "&e 共 &b" + count + " &e个");
        }
        return true;
    }

    private static final List<String> emptyList = Lists.newArrayList();
    private static final List<String> listArg0 = Lists.newArrayList();
    private static final List<String> listOpArg0 = Lists.newArrayList(
            "debug", "prefeb", "reload");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return startsWith(sender.isOp() ? listOpArg0 : listArg0, args[0]);
        }
        if (args.length == 2) {
            if (sender.isOp()) {
                if ("prefeb".equalsIgnoreCase(args[0])) {
                    return startsWith(PrefebManager.inst().prefebs(), args[1]);
                }
            }
        }
        if (args.length == 4) {
            if (sender.isOp()) {
                if ("prefeb".equalsIgnoreCase(args[0])) {
                    return null;
                }
            }
        }
        return emptyList;
    }

    public List<String> startsWith(Collection<String> list, String s) {
        return startsWith(null, list, s);
    }
    public List<String> startsWith(String[] addition, Collection<String> list, String s) {
        String s1 = s.toLowerCase();
        List<String> stringList = new ArrayList<>(list);
        if (addition != null) stringList.addAll(0, Lists.newArrayList(addition));
        stringList.removeIf(it -> !it.toLowerCase().startsWith(s1));
        return stringList;
    }
}
