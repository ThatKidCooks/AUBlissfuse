package site.thatkid.aUBlissFuse.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import site.thatkid.aUBlissFuse.AUBlissFuse;

public class MobProtectListener implements Listener {

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();

        if (target == null || !(target instanceof Villager)) {
            return;
        }

        Villager v = (Villager) target;
        Byte isGuide = v.getPersistentDataContainer()
                .get(AUBlissFuse.GUIDE_KEY, PersistentDataType.BYTE);

        if (isGuide != null && isGuide == (byte) 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();

        if (target instanceof Villager) {
            Villager v = (Villager) target;
            Byte isGuide = v.getPersistentDataContainer()
                    .get(AUBlissFuse.GUIDE_KEY, PersistentDataType.BYTE);

            if (isGuide != null && isGuide == (byte) 1) {
                event.setCancelled(true);
            }
        }
        else if (target instanceof Chicken) {
            Chicken chicken = (Chicken) target;
            Byte isChicken = chicken.getPersistentDataContainer()
                    .get(AUBlissFuse.CHICKEN_KEY, PersistentDataType.BYTE);

            if (isChicken != null && isChicken == (byte) 1) {
                event.setCancelled(true);
            }
        }

        if (target instanceof IronGolem) {
            IronGolem ironGolem = (IronGolem) target;
            Byte isIronGolem = ironGolem.getPersistentDataContainer()
                    .get(AUBlissFuse.IRON_GOLEM_KEY, PersistentDataType.BYTE);

            if (isIronGolem != null && isIronGolem == (byte) 1) {
                event.setCancelled(true);
            }
        }
    }
}