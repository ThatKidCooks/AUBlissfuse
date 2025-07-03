package site.thatkid.aUBlissFuse.custom.mobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import site.thatkid.aUBlissFuse.AUBlissFuse;

public class WitherBoss implements Listener {

    public static void createBoss(Player player) {
        // 1) Prep the player
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, Integer.MAX_VALUE, 0));

        // 2) Spawn & name the boss
        WitherSkeleton boss = (WitherSkeleton) player.getWorld()
                .spawnEntity(new Location(player.getWorld(), 277.5, 70, 35.5, 90, 10),
                        EntityType.WITHER_SKELETON);

        boss.setCustomName(ChatColor.LIGHT_PURPLE + "The Ancient Guardian");
        boss.setCustomNameVisible(true);
        boss.setGlowing(true);
        boss.getPersistentDataContainer().set(AUBlissFuse.BOSS_KEY,
                PersistentDataType.BYTE, (byte) 1);

        // 3) Set up Attributes
        AttributeInstance maxHealth = boss.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        maxHealth.setBaseValue(200.0);
        boss.setHealth(200.0);

        AttributeInstance damage = boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        damage.setBaseValue(40.0);

        // 4) Core buffs
        boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2));

        // 5) Simulate regeneration
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!boss.isValid() || boss.isDead()) {
                    cancel();
                    return;
                }
                double health = boss.getHealth();
                double max = boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                boss.setHealth(Math.min(max, health + 2.0));
            }
        }
        .runTaskTimer(AUBlissFuse.getInstance(), 0L, 10L);
    }



}
