package top.mrxiaom.sweet.drops;
        
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.drops.mythic.IMythic;
import top.mrxiaom.sweet.drops.mythic.Mythic4;
import top.mrxiaom.sweet.drops.mythic.Mythic5;

public class SweetDrops extends BukkitPlugin {
    public static SweetDrops getInstance() {
        return (SweetDrops) BukkitPlugin.getInstance();
    }

    public SweetDrops() {
        super(options()
                .bungee(false)
                .adventure(true)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .scanIgnore("top.mrxiaom.sweet.drops.libs")
        );
    }
    public static boolean debug = false;
    private static boolean supportNamespacedKeys = Util.isPresent("org.bukkit.NamespacedKey");
    IMythic mythic;

    public static boolean isSupportNamespacedKeys() {
        return supportNamespacedKeys;
    }

    public IMythic getMythic() {
        return mythic;
    }

    @Override
    protected void beforeEnable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (plugin != null) {
            String version = plugin.getDescription().getVersion();
            if (version.startsWith("5.")) {
                mythic = new Mythic5();
                getLogger().info("支持 MythicMobs " + version);
            } else if (version.startsWith("4.")) {
                mythic = new Mythic4();
                getLogger().info("支持 MythicMobs " + version);
            } else {
                getLogger().warning("不支持的 MythicMobs 版本 " + version);
            }
        } else {
            getLogger().info("未安装 MythicMobs");
        }
        LanguageManager.inst()
                .setLangFile("messages.yml")
                .register(Messages.class, Messages::holder);
    }

    @Override
    protected void afterEnable() {
        getLogger().info("SweetDrops 加载完毕");
    }

    public static String format(IntRange range) {
        int min = range.getMinimumInteger();
        int max = range.getMaximumInteger();
        if (min == max) return String.valueOf(min);
        return min + "-" + max;
    }
}
