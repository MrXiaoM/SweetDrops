package top.mrxiaom.sweet.drops.func.entry;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import top.mrxiaom.pluginbase.utils.AdventureItemStack;
import top.mrxiaom.pluginbase.utils.PAPI;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.drops.SweetDrops;
import top.mrxiaom.sweet.drops.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Prefeb {
    public final String id;
    public final Material material;
    public final int data;
    public final String name;
    public final List<String> lore;
    public final Integer color;
    public final Map<Enchantment, Integer> enchantments;
    public final Integer customModelData;
    public final boolean unique;
    public final Map<String, String> nbtStrings;
    public final Map<String, String> nbtInts;

    public Prefeb(String id, Material material, int data, String name, List<String> lore, Integer color, Map<Enchantment, Integer> enchantments, Integer customModelData, boolean unique, Map<String, String> nbtStrings, Map<String, String> nbtInts) {
        this.id = id;
        this.material = material;
        this.data = data;
        this.name = name;
        this.lore = lore;
        this.color = color;
        this.enchantments = enchantments;
        this.customModelData = customModelData;
        this.unique = unique;
        this.nbtStrings = nbtStrings;
        this.nbtInts = nbtInts;
    }

    public ItemStack generate(Player player) {
        if (material.equals(Material.AIR)) return null;
        ItemStack item = data == 0 ? new ItemStack(material) : new ItemStack(material, 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        if (color != null && meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(color));
        }
        if (customModelData != null) try {
            meta.setCustomModelData(customModelData);
        } catch (Throwable ignored) {
        }
        item.setItemMeta(meta);
        if (name != null) {
            String displayName = PAPI.setPlaceholders(player, name);
            AdventureItemStack.setItemDisplayName(item, displayName);
        }
        if (lore != null) {
            List<String> parsedLore = PAPI.setPlaceholders(player, lore);
            AdventureItemStack.setItemLore(item, parsedLore);
        }
        if (!nbtStrings.isEmpty() || !nbtInts.isEmpty() || unique) {
            NBT.modify(item, nbt -> {
                if (unique) {
                    nbt.setUUID("SWEET_DROPS_UNIQUE", UUID.randomUUID());
                }
                if (!nbtStrings.isEmpty()) {
                    for (Map.Entry<String, String> entry : nbtStrings.entrySet()) {
                        String value = PAPI.setPlaceholders(player, entry.getValue());
                        nbt.setString(entry.getKey(), value);
                    }
                }
                if (!nbtInts.isEmpty()) {
                    for (Map.Entry<String, String> entry : nbtInts.entrySet()) {
                        String value = PAPI.setPlaceholders(player, entry.getValue());
                        Integer integer = Util.parseInt(value).orElse(null);
                        if (integer != null) {
                            nbt.setInteger(entry.getKey(), integer);
                        }
                    }
                }
            });
        }
        return item;
    }

    public static Prefeb load(ConfigurationSection config, String id) {
        Material material = Util.valueOr(Material.class, config.getString("material"), null);
        if (material == null) return null;
        int data = config.getInt("data", 0);
        String name = config.getString("name", null);
        List<String> lore = config.getStringList("lore");
        Integer color = Utils.decode(config.getString("color"));
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("enchantments");
        if (section != null) for (String key : section.getKeys(false)) {
            int level = section.getInt(key);
            if (level <= 0) continue;
            Enchantment enchantment = null;
            boolean flag = SweetDrops.isSupportNamespacedKeys();
            for (Enchantment enchant : Enchantment.values()) {
                if (flag) {
                    if (enchant.getKey().getKey().equalsIgnoreCase(key)) {
                        enchantment = enchant;
                        break;
                    }
                } else {
                    if (enchant.getName().equalsIgnoreCase(key)) {
                        enchantment = enchant;
                        break;
                    }
                }
            }
            if (enchantment != null) {
                enchantments.put(enchantment, level);
            }
        }
        Integer customModelData = config.contains("custom_model_data")
                ? Integer.valueOf(config.getInt("custom_model_data"))
                : null;
        boolean unique = config.getBoolean("unique", false);
        Map<String, String> nbtStrings = new HashMap<>();
        Map<String, String> nbtInts = new HashMap<>();
        section = config.getConfigurationSection("nbt_strings");
        if (section != null) for (String key : section.getKeys(false)) {
            nbtStrings.put(key, section.getString(key));
        }
        section = config.getConfigurationSection("nbt_ints");
        if (section != null) for (String key : section.getKeys(false)) {
            nbtInts.put(key, section.getString(key));
        }
        return new Prefeb(id, material, data, name, lore.isEmpty() ? null : lore, color, enchantments, customModelData, unique, nbtStrings, nbtInts);
    }
}
