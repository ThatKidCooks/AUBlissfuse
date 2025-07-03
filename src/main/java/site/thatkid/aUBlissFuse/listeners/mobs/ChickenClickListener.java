package site.thatkid.aUBlissFuse.listeners.mobs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import site.thatkid.aUBlissFuse.AUBlissFuse;
import site.thatkid.aUBlissFuse.custom.items.MaceKey;
import site.thatkid.aUBlissFuse.listeners.mobs.connections.Connections;
import site.thatkid.aUBlissFuse.listeners.mobs.connections.EntityConnections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChickenClickListener implements Listener {
    private final AUBlissFuse plugin;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 3000;

    public ChickenClickListener(AUBlissFuse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = (Player) event.getPlayer();

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long lastUse = cooldowns.get(playerId);
            if (currentTime - lastUse < COOLDOWN_TIME) {
                long remaining = (COOLDOWN_TIME - (currentTime - lastUse)) / 1000;
                return;
            }
        }
        player.getInventory().removeItem(MaceKey.createMaceStack());

        cooldowns.put(playerId, currentTime);

        cleanupOldCooldowns();

        Entity clicked = event.getRightClicked();
        if (!(clicked instanceof Chicken)) return;

        Chicken chicken = (Chicken) clicked;
        Byte isChicken = chicken.getPersistentDataContainer()
                .get(plugin.CHICKEN_KEY, PersistentDataType.BYTE);

        if (isChicken != null && isChicken == (byte) 1) {
            if (!Connections.isConnected(playerId, "villager")) {
                player.sendMessage(ChatColor.YELLOW + "Who in this world sent you");
                player.sendMessage(ChatColor.YELLOW + "Talk to someone worthy, BEFORE TALKING TO ME.");
            } else if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_INGOT) {
                player.getInventory().setItemInMainHand(null);
                player.sendMessage(ChatColor.YELLOW + "Oh finally took you long enough");
                player.sendMessage(ChatColor.GREEN + "Now I can upgrade my armour");
                player.sendMessage(ChatColor.GREEN + "Here ya go");
                player.sendMessage(ChatColor.RED + "The iron golem know where it is at *** ***");
                player.sendMessage(ChatColor.RED + "Now SCRAM!!!");
                Connections.connectionsMap.put(playerId, new EntityConnections(true, true, Connections.isConnected(playerId, "ironGolem")));
                event.setCancelled(true);
            } else if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.TORCHFLOWER_SEEDS) {
                player.getInventory().setItemInMainHand(null);
                player.sendMessage(ChatColor.GREEN + "Oh finally, now what do you want?");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage(ChatColor.GREEN + "Hmm, interesting. Fine, fine, I will tell you what I know if you get me a Netherite Ingot");
                player.sendMessage(ChatColor.YELLOW + "Now Scram");
                event.setCancelled(true);
            } else if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WHEAT_SEEDS) {
            player.spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation().add(0.5, 0.5, 0.5), 50);
                player.sendMessage(ChatColor.YELLOW + "NO TUPID!");
                player.sendMessage(ChatColor.BLUE + "I want Torchflower Seeds");
            } else {
                player.sendMessage(ChatColor.RED + "GIVE ME SEEDS! NOW!!! THEN WE CAN TALK!");
                event.setCancelled(true);
            }

        }
    }

    public void cleanupOldCooldowns() {
        long currentTime = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> currentTime - entry.getValue() > COOLDOWN_TIME * 2);
    }
}
