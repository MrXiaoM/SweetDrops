package top.mrxiaom.sweet.drops.func;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.drops.SweetDrops;
import top.mrxiaom.sweet.drops.func.entry.Event;
import top.mrxiaom.sweet.drops.func.entry.Prefeb;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@AutoRegister
public class PrefebManager extends AbstractModule {
    private final Map<String, Prefeb> prefebsById = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public PrefebManager(SweetDrops plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        List<String> folders = config.getStringList("prefebs-folders");
        prefebsById.clear();
        for (String s : folders) {
            File folder = s.startsWith("./") ? new File(plugin.getDataFolder(), s.substring(2)) : new File(s);
            if (s.equals("./prefebs") && !folder.exists()) {
                Util.mkdirs(folder);
                plugin.saveResource("prefebs/example.yml", new File(folder, "example.yml"));
            }
            if (folder.isDirectory() && !new File(folder, ".ignore").exists()) {
                reloadConfig(folder);
            }
        }
        info("加载了 " + prefebsById.size() + " 个预制体");
    }

    private void reloadConfig(File folder) {
        File[] files = folder.listFiles();
        if (files != null) for (File file : files) {
            if (file.isDirectory()) {
                reloadConfig(file);
                continue;
            }
            String name = file.getName();
            if (name.endsWith(".yml")) {
                String id = name.substring(0, name.length() - 4);
                if (prefebsById.containsKey(id)) {
                    warn("预制体ID重复：" + file.getAbsolutePath());
                    continue;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Prefeb loaded = Prefeb.load(config, id);
                if (loaded != null) {
                    prefebsById.put(id, loaded);
                }
            }
        }
    }

    @Nullable
    public Prefeb get(String id) {
        return prefebsById.get(id);
    }

    public static PrefebManager inst() {
        return instanceOf(PrefebManager.class);
    }
}
