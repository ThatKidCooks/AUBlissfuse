package site.thatkid.aUBlissFuse.listeners.mobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import site.thatkid.aUBlissFuse.AUBlissFuse;

public class WitherBossListener implements Listener {

    @EventHandler
    public void onBossHit(EntityDamageByEntityEvent event) {
        // Check if the damager is a WitherSkeleton
        if (!(event.getDamager() instanceof WitherSkeleton)) {
            return;
        }

        WitherSkeleton boss = (WitherSkeleton) event.getDamager();

        // Confirm this skeleton is your custom boss via your BOSS_KEY
        Byte isBoss = boss.getPersistentDataContainer()
                .get(AUBlissFuse.BOSS_KEY, PersistentDataType.BYTE);
        if (isBoss == null || isBoss != (byte) 1) {
            return;
        }

        // Ensure the target is a player
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Apply blindness: duration in ticks (20 ticks = 1 second), amplifier 1
        player.addPotionEffect(
                new PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 0)
        );

        // send feedback or play sound
        player.sendMessage(ChatColor.DARK_PURPLE + "You have been blinded by The Ancient Guardian!");
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 1);
    }

    // check if it dies
    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton)) {
            return;
        }

        if (event.getEntity().getPersistentDataContainer().get(AUBlissFuse.BOSS_KEY, PersistentDataType.BYTE) == (byte) 1) {
            Player player = (Player) event.getEntity().getKiller();

            player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
            player.teleport(new Location(player.getWorld(), 0, 65, 0));
        }
    }
}
