package top.mrxiaom.sweet.drops;
        
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.EconomyHolder;

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
                .vaultEconomy(false)
                .scanIgnore("top.mrxiaom.sweet.drops.libs")
        );
    }


    @Override
    protected void afterEnable() {
        getLogger().info("SweetDrops 加载完毕");
    }
}
