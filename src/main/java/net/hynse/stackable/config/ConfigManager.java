package net.hynse.stackable.config;

import net.hynse.stackable.Stackable;
import net.hynse.stackable.util.RegexUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private final Map<Material, Integer> stackableItems = new ConcurrentHashMap<>();
    private final Map<String, Integer> wildcardPatterns = new ConcurrentHashMap<>();

    public void loadConfig() {
        // Clear existing data
        stackableItems.clear();
        wildcardPatterns.clear();
        RegexUtil.clearCache(); // Clear the match cache
        
        FileConfiguration config = Stackable.instance.getConfig();
        
        if (config.contains("items") && config.isConfigurationSection("items")) {
            Set<String> keys = Objects.requireNonNull(config.getConfigurationSection("items")).getKeys(false);
            for (String key : keys) {
                int maxStackSize = config.getInt("items." + key);
                
                if (isWildcardPattern(key)) {
                    wildcardPatterns.put(key, maxStackSize);
                    if (Stackable.instance.getConfig().getBoolean("debug", false)) {
                        Stackable.instance.getLogger().info("[DEBUG] Added wildcard pattern: '" + key + "' with max stack size: " + maxStackSize);
                    } else {
                        Stackable.instance.getLogger().fine("Added wildcard pattern: " + key + " with max stack size: " + maxStackSize);
                    }
                } else {
                    try {
                        Material mat = Material.valueOf(key.toUpperCase());
                        stackableItems.put(mat, maxStackSize);
                        Stackable.instance.getLogger().fine("Added material: " + key + " with max stack size: " + maxStackSize);
                    } catch (IllegalArgumentException e) {
                        Stackable.instance.getLogger().warning("Invalid material in config: " + key);
                    }
                }
            }
        }

        Stackable.instance.getLogger().info("Loaded " + stackableItems.size() + " direct materials and " +
                   wildcardPatterns.size() + " wildcard patterns for custom stack sizes.");
    }

    /**
     * Reloads the configuration from disk
     */
    public void reloadConfig() {
        Stackable.instance.reloadConfig();
        loadConfig();
        Stackable.instance.getLogger().info("Stackable configuration reloaded: " + stackableItems.size() + " items and " +
                   wildcardPatterns.size() + " wildcard patterns loaded.");
    }
    
    /**
     * Determines if a string is a wildcard pattern
     * @param str The string to check
     * @return true if the string contains wildcard characters or is not a valid material name
     */
    private boolean isWildcardPattern(String str) {
        // Check if it contains wildcard character
        if (str.contains("*")) {
            return true;
        }
        
        // Check if it's a valid material name
        try {
            Material.valueOf(str.toUpperCase());
            return false; // It's a valid material name
        } catch (IllegalArgumentException e) {
            return true; // Not a valid material, treat as wildcard pattern
        }
    }
    
    /**
     * Checks if an item is configured as stackable
     * @param material The material to check
     * @return true if the material is stackable according to config
     */
    public boolean isStackableItem(Material material) {
        if (stackableItems.containsKey(material)) {
            return true;
        }
        return RegexUtil.getMatchOrDefault(material, wildcardPatterns, -1) != -1;
    }
    
    /**
     * Gets the configured max stack size for a material
     * @param material The material to check
     * @return The configured max stack size, or the default if not configured
     */
    public int getMaxStackSize(Material material) {
        if (stackableItems.containsKey(material)) {
            return stackableItems.get(material);
        }

        return RegexUtil.getMatchOrDefault(material, wildcardPatterns, material.getMaxStackSize());
    }
    
    /**
     * Gets the number of direct material entries in the configuration
     * @return Count of direct material entries
     */
    public int getDirectMaterialCount() {
        return stackableItems.size();
    }
    
    /**
     * Gets the number of wildcard pattern entries in the configuration
     * @return Count of wildcard pattern entries
     */
    public int getWildcardPatternCount() {
        return wildcardPatterns.size();
    }
}