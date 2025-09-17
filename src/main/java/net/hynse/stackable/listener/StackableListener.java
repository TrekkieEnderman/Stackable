package net.hynse.stackable.listener;

import me.nahu.scheduler.wrapper.WrappedScheduler;
import net.hynse.stackable.manager.StackSizeManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Handles all events related to stackable items
 */
public class StackableListener implements Listener {
    private final StackSizeManager stackSizeManager;
    private final WrappedScheduler scheduler;
    
    public StackableListener(StackSizeManager stackSizeManager, WrappedScheduler scheduler) {
        this.stackSizeManager = stackSizeManager;
        this.scheduler = scheduler;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent e) {
        scheduler.runTaskAtEntity(e.getWhoClicked(), () ->
            stackSizeManager.applyCustomStackSize(e.getCurrentItem())
        );
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPickup(EntityPickupItemEvent e) {
        scheduler.runTaskAtEntity(e.getEntity(), () ->
            stackSizeManager.applyCustomStackSize(e.getItem().getItemStack())
        );
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryMove(InventoryMoveItemEvent e) {
        Location location = e.getDestination().getLocation();
        if (location != null) {
            scheduler.runTaskAtLocation(location, () ->
                stackSizeManager.applyCustomStackSize(e.getItem())
            );
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDispense(BlockDispenseEvent e) {
        scheduler.runTaskAtLocation(e.getBlock().getLocation(), () ->
            stackSizeManager.applyCustomStackSize(e.getItem())
        );
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent e) {
        scheduler.runTaskAtEntity(e.getEntity(), () -> {
            ItemStack item = e.getEntity().getItemStack();
            stackSizeManager.applyCustomStackSize(item);
        });
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareCrafter(CrafterCraftEvent e) {
        scheduler.runTaskAtLocation(e.getBlock().getLocation(), () ->
            stackSizeManager.applyCustomStackSize(e.getResult())
        );
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent e) {
        scheduler.runTaskAtLocation(e.getBlock().getLocation(), () ->
            stackSizeManager.applyCustomStackSize(e.getResult())
        );
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryPickupItem(InventoryPickupItemEvent e) {
        scheduler.runTaskAtLocation(e.getItem().getLocation(), () ->
            stackSizeManager.applyCustomStackSize(e.getItem().getItemStack())
        );
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryCreative(InventoryCreativeEvent e) {
        scheduler.runTaskAtEntity(e.getWhoClicked(), () ->
            stackSizeManager.applyCustomStackSize(e.getCurrentItem())
        );
    }
    
    /**
     * Applies custom stack sizes to all items in an inventory
     * @param inv The inventory to process
     */
    public void fixInventory(Inventory inv) {
        if (inv == null) return;
        stackSizeManager.applyCustomStackSizeToAll(inv.getContents());
    }
}