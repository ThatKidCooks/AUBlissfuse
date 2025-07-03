package site.thatkid.aUBlissFuse.listeners.blocks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import site.thatkid.aUBlissFuse.custom.blocks.TeleporterBlock;
import site.thatkid.aUBlissFuse.custom.mobs.WitherBoss;

public class TeleporterBlockListener implements Listener {
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        
        if (TeleporterBlock.isTeleporterItem(item)) {
            TeleporterBlock.onTeleporterPlace(event.getPlayer(), event.getBlockPlaced(), item);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (TeleporterBlock.isTeleporterBlock(event.getBlock())) {
            event.setDropItems(false);

            TeleporterBlock.onTeleporterBreak(event.getPlayer(), event.getBlock());
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && TeleporterBlock.isTeleporterBlock(event.getClickedBlock())) {
            event.setCancelled(true);

            if (TeleporterBlock.onTeleporterInteract(event.getPlayer(), event.getClickedBlock())) {
                WitherBoss.createBoss(event.getPlayer());
            }
        }
    }
}