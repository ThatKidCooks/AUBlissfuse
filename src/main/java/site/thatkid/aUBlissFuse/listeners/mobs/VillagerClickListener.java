package site.thatkid.aUBlissFuse.listeners.mobs;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
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

public class VillagerClickListener implements Listener {
    private final AUBlissFuse plugin;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 3000;

    public VillagerClickListener(AUBlissFuse plugin) {
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
        if (!(clicked instanceof Villager)) return;

        Villager villager = (Villager) clicked;
        Byte isGuide = villager.getPersistentDataContainer()
                              .get(plugin.GUIDE_KEY, PersistentDataType.BYTE);

        if (isGuide != null && isGuide == (byte) 1) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD) {
                player.getInventory().setItemInMainHand(null);
                player.sendMessage("§eHello adventurer! I'm hun-");
                player.sendMessage("§6Ohh, you got me one.");
                player.sendMessage("§9I guess I have to tell you where the mace is...");
                player.sendMessage("§eAsk the chicken at 100, 100 and leave me to eat this in piece.");
                Connections.connectionsMap.put(playerId, new EntityConnections(true, Connections.isConnected(playerId, "chicken"), Connections.isConnected(playerId, "ironGolem")));
            } else {
                player.sendMessage("§eHello adventurer! I'm hungry!");
                player.sendMessage("§6Maybe I won't kill you");
                player.sendMessage("§6If you bring me the head of another.");
                player.sendMessage("§6Now go. Bring me back my desire and I will tell you the secret");
                player.sendMessage("§eOf the §9Mace!!!");
                event.setCancelled(true);
            }
        }
    }
    private void cleanupOldCooldowns() {
        long currentTime = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> currentTime - entry.getValue() > COOLDOWN_TIME * 2);
    }
}
