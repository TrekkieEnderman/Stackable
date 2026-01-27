package net.hynse.stackable.listener;

import me.nahu.scheduler.wrapper.WrappedScheduler;
import net.hynse.stackable.manager.StackSizeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;

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
        stackSizeManager.applyCustomStackSize(e.getCurrentItem(), e.getWhoClicked());
        if (e.getWhoClicked() instanceof Player player) {
            // Fixes the visual bug that sometimes occur when splitting the stack or shift clicking into full inventory
            if (e.isRightClick() || e.isShiftClick()) {
                scheduler.runTaskAtEntity(player, player::updateInventory);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPickup(EntityPickupItemEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItem().getItemStack(), e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryCreative(InventoryCreativeEvent e) {
        stackSizeManager.applyCustomStackSize(e.getCurrentItem(), e.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketFill(final PlayerBucketFillEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItemStack(), e.getPlayer());
        // Get rid of the ghost bucket caused by client desync
        scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(final PlayerBucketEmptyEvent e) {
        stackSizeManager.applyCustomStackSize(e.getItemStack(), e.getPlayer());
        // Get rid of the ghost bucket caused by client desync
        scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEntity(PlayerBucketEntityEvent e) {
        stackSizeManager.applyCustomStackSize(e.getEntityBucket(), e.getPlayer());
        // Get rid of the ghost bucket caused by client desync
        scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowPickup(final PlayerPickupArrowEvent e) {
        stackSizeManager.applyCustomStackSize(e.getArrow().getItemStack(), e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemConsume(final PlayerItemConsumeEvent e) {
        // Known issue - Does not work for milk bucket or soups because #getReplacement() only returns CUSTOM replacements.
        if (e.getReplacement() != null) {
            stackSizeManager.applyCustomStackSize(e.getReplacement(), e.getPlayer());
            // Get rid of the ghost bucket caused by client desync
            scheduler.runTaskAtEntity(e.getPlayer(), () -> e.getPlayer().updateInventory());
        }
    }
}