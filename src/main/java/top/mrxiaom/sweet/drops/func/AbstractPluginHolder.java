package top.mrxiaom.sweet.drops.func;
        
import top.mrxiaom.sweet.drops.SweetDrops;

@SuppressWarnings({"unused"})
public abstract class AbstractPluginHolder extends top.mrxiaom.pluginbase.func.AbstractPluginHolder<SweetDrops> {
    public AbstractPluginHolder(SweetDrops plugin) {
        super(plugin);
    }

    public AbstractPluginHolder(SweetDrops plugin, boolean register) {
        super(plugin, register);
    }
}
