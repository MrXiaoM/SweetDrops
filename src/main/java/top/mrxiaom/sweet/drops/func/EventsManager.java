package top.mrxiaom.sweet.drops.func;

import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.drops.SweetDrops;
import top.mrxiaom.sweet.drops.func.entry.Event;

import java.io.File;
import java.util.*;

@AutoRegister
public class EventsManager extends AbstractModule {
    final Map<String, Event> eventsById = new HashMap<>();
    final Map<String, Map<Material, List<Event>>> eventsByWorld = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public EventsManager(SweetDrops plugin) {
        super(plugin);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        List<String> folders = config.getStringList("events-folders");
        eventsById.clear();
        for (String s : folders) {
            File folder = s.startsWith("./") ? new File(plugin.getDataFolder(), s.substring(2)) : new File(s);
            if (folder.isDirectory() && !new File(folder, ".ignore").exists()) {
                reloadConfig(folder);
            }
        }
        eventsByWorld.clear();
        Set<Material> blocks = new TreeSet<>();
        for (Event event : eventsById.values()) {
            for (String world : event.worlds) {
                Map<Material, List<Event>> map = eventsByWorld.get(world);
                if (map == null) map = new TreeMap<>();
                for (Material block : event.blocks) {
                    blocks.add(block);
                    List<Event> list = map.get(block);
                    if (list == null) list = new ArrayList<>();
                    list.add(event);
                    map.put(block, list);
                }
                eventsByWorld.put(world, map);
            }
        }
        info(String.format(
                "共加载 %d 个事件配置，其范围覆盖了 %d 个世界，共 %d 种方块。",
                eventsById.size(), eventsByWorld.size(), blocks.size()));
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
                if (eventsById.containsKey(id)) {
                    warn("事件ID重复：" + file.getAbsolutePath());
                    continue;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Event loaded = Event.load(config, id);
                eventsById.put(id, loaded);
            }
        }
    }
}
