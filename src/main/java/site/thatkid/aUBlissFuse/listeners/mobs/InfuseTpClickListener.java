package site.thatkid.aUBlissFuse.listeners.mobs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import site.thatkid.aUBlissFuse.AUBlissFuse;
import site.thatkid.aUBlissFuse.custom.items.MaceKey;
import site.thatkid.aUBlissFuse.listeners.mobs.connections.Connections;
import site.thatkid.aUBlissFuse.listeners.mobs.connections.EntityConnections;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfuseTpClickListener implements Listener {
    private final AUBlissFuse plugin;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 3000;

    public InfuseTpClickListener(AUBlissFuse plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = false)
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
        Byte isInfuseTp = villager.getPersistentDataContainer()
                .get(plugin.INFUSE_SERVER_TP, PersistentDataType.BYTE);

        if (isInfuseTp != null && isInfuseTp == (byte) 1) {
            String command = "mvtp " + player.getName() + " world";
            boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            if (!success) {
                player.sendMessage("Failed to run command!");
            }
        }

    }

    private void cleanupOldCooldowns() {
        long currentTime = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> currentTime - entry.getValue() > COOLDOWN_TIME * 2);
    }
}
