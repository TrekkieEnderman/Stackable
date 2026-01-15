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
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        stackSizeManager.applyCustomStackSize(e.getCurrentItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPickup(EntityPickupItemEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItem().getItemStack());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent e) {
        Location location = e.getDestination().getLocation();
        if (location != null) {
            stackSizeManager.applyCustomStackSize(e.getItem());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent e) {
        stackSizeManager.applyCustomStackSize(e.getEntity().getItemStack());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareCrafter(CrafterCraftEvent e) {
        stackSizeManager.applyCustomStackSize(e.getResult());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceSmelt(FurnaceSmeltEvent e) {
        stackSizeManager.applyCustomStackSize(e.getResult());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryPickupItem(InventoryPickupItemEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItem().getItemStack());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryCreative(InventoryCreativeEvent e) {
        stackSizeManager.applyCustomStackSize(e.getCurrentItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketFill(final PlayerBucketFillEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItemStack());
        // Get rid of the ghost bucket caused by client desync
        scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(final PlayerBucketEmptyEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItemStack());
        // Get rid of the ghost bucket caused by client desync
        scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEntity(PlayerBucketEntityEvent e) {
        stackSizeManager.applyCustomStackSize(e.getEntityBucket());
        // Get rid of the ghost bucket caused by client desync
        scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowPickup(final PlayerPickupArrowEvent e) {
        stackSizeManager.applyCustomStackSize(e.getArrow().getItemStack());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemConsume(final PlayerItemConsumeEvent e) {
        // Known issue - Does not work for milk bucket or soups because #getReplacement() only returns CUSTOM replacements.
        if (e.getReplacement() != null) {
            stackSizeManager.applyCustomStackSize(e.getReplacement());
            // Get rid of the ghost bucket caused by client desync
            scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
        }
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