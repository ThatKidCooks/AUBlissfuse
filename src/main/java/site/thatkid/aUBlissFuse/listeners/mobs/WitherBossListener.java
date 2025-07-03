package site.thatkid.aUBlissFuse.custom.mobs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
                new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0)
        );

        // Optional: send feedback or play sound
        player.sendMessage(ChatColor.DARK_PURPLE + "You have been blinded by The Ancient Guardian!");
    }
}
