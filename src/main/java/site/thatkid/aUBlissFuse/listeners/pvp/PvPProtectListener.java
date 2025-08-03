package site.thatkid.aUBlissFuse.listeners.pvp;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPProtectListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();

        if (target instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (!PvPToggleManager.isToggled()) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
