package net.hynse.stackable.manager;

import net.hynse.stackable.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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
     * @param user The user interacting with the item
     */
    public void applyCustomStackSize(ItemStack item, Entity user) {
        if (item == null) return;

        if (user == null) {
            return;
        }

        Material material = item.getType();

        if (!user.hasPermission("stackable.enable")) {
            //user.sendMessage("false");
            setMaxStackSize(item, material.getMaxStackSize()); // Reset to default stack size
            return;
        }

        //user.sendMessage("true");
        setMaxStackSize(item, configManager.getMaxStackSize(material));
    }

    private void setMaxStackSize(ItemStack item, int max) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setMaxStackSize(max);
            item.setItemMeta(meta);
        }
    }
}