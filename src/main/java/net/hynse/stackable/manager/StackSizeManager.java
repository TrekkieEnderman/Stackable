package net.hynse.stackable.manager;

import net.hynse.stackable.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Manages stack size operations for items
 */
public class StackSizeManager {
    private final ConfigManager configManager;
    
    public StackSizeManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Applies custom stack size to an item if configured
     * @param item The item to modify
     */
    public void applyCustomStackSize(ItemStack item) {
        if (item == null) return;
        
        Material material = item.getType();
        if (!configManager.isStackableItem(material)) return;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setMaxStackSize(configManager.getMaxStackSize(material));
            item.setItemMeta(meta);
        }
    }
    
    /**
     * Applies custom stack sizes to all items in an inventory
     * @param items Array of items to process
     */
    public void applyCustomStackSizeToAll(ItemStack[] items) {
        if (items == null) return;
        
        for (ItemStack item : items) {
            applyCustomStackSize(item);
        }
    }
}