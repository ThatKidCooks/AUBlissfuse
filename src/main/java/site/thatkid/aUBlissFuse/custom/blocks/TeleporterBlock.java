package site.thatkid.aUBlissFuse.custom.blocks;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import site.thatkid.aUBlissFuse.AUBlissFuse;
import site.thatkid.aUBlissFuse.custom.items.MaceKey;
import site.thatkid.aUBlissFuse.listeners.mobs.connections.Connections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class TeleporterBlock {

    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 3000; // 3 seconds

    public static final NamespacedKey TELEPORTER_KEY = new NamespacedKey(AUBlissFuse.getInstance(), "teleporter_block");

    public static ItemStack createTeleporterItem() {
        ItemStack item = new ItemStack(Material.JUKEBOX);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§d§lTeleporter Block");
        meta.setLore(Arrays.asList(
                "§7Right-click when placed to teleport to spawn!",
                "§7Cooldown: 3 seconds",
                "§8§oMagical transportation device"
        ));

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(TELEPORTER_KEY, PersistentDataType.STRING, "spawn_teleporter");

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isTeleporterItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        return dataContainer.has(TELEPORTER_KEY, PersistentDataType.STRING);
    }

    public static boolean isTeleporterBlock(Block block) {
        if (block.getType() != Material.JUKEBOX) return false;
        if (!(block.getState() instanceof org.bukkit.block.TileState)) return false;

        org.bukkit.block.TileState tileState = (org.bukkit.block.TileState) block.getState();
        PersistentDataContainer dataContainer = tileState.getPersistentDataContainer();
        return dataContainer.has(TELEPORTER_KEY, PersistentDataType.STRING);
    }

    public static void onTeleporterPlace(Player player, Block block, ItemStack item) {
        if (block.getState() instanceof org.bukkit.block.TileState) {
            org.bukkit.block.TileState tileState = (org.bukkit.block.TileState) block.getState();
            PersistentDataContainer blockData = tileState.getPersistentDataContainer();
            blockData.set(TELEPORTER_KEY, PersistentDataType.STRING, "spawn_teleporter");
            tileState.update();
        }

        Location loc = block.getLocation().add(0.5, 1, 0.5);
        block.getWorld().spawnParticle(Particle.PORTAL, loc, 20, 0.5, 0.5, 0.5, 0.01);
        player.playSound(block.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 2.0f);
        player.sendMessage("§d✦ Teleporter placed! Right-click to use it!");
    }

    public static void onTeleporterBreak(Player player, Block block) {
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().spawnParticle(Particle.CLOUD, loc, 10, 0.3, 0.3, 0.3, 0.05);
        player.playSound(block.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.5f, 1.5f);
        player.sendMessage("§d✦ Teleporter broken!");

        ItemStack teleporterItem = createTeleporterItem();
        block.getWorld().dropItemNaturally(block.getLocation(), teleporterItem);
    }

    public static boolean onTeleporterInteract(Player player, Block block) {
        if (!player.getInventory().getItemInMainHand().equals(MaceKey.createMaceStack())) {
            player.sendMessage("§cGet the Correct Item");
            return false;
        }

        if (!Connections.isConnected(player.getUniqueId(), "ironGolem")) {
            player.sendMessage(ChatColor.RED + "How did you get the mace key??");
            return false;
        }

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cooldowns.containsKey(playerId)) {
            long lastUse = cooldowns.get(playerId);
            if (currentTime - lastUse < COOLDOWN_TIME) {
                long remaining = (COOLDOWN_TIME - (currentTime - lastUse)) / 1000;
                player.sendMessage("§cTeleporter on cooldown! Wait " + remaining + " more seconds.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f);
                return false;
            }
        }
            player.getInventory().removeItem(MaceKey.createMaceStack());
            performTeleportation(player, block);

        cooldowns.put(playerId, currentTime);

        cleanupOldCooldowns();

        return true;
    }
    private static void performTeleportation(Player player, Block teleporterBlock) {
        World world = player.getWorld();
        Location finalBoss = new Location(world, -5440.5, -25, 224.5, 90, 0);
        finalBoss.add(0, 1, 0);

        Location playerLoc = player.getLocation();
        player.getWorld().spawnParticle(Particle.PORTAL, playerLoc, 50, 1, 2, 1, 0.5);
        player.playSound(playerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        player.teleport(finalBoss);

        player.getWorld().spawnParticle(Particle.PORTAL, finalBoss, 50, 1, 2, 1, 0.5);
        player.playSound(finalBoss, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);

        Location blockLoc = teleporterBlock.getLocation().add(0.5, 1, 0.5);
        teleporterBlock.getWorld().spawnParticle(Particle.ENCHANT, blockLoc, 30, 0.5, 0.5, 0.5, 0.5);

        Bukkit.getServer().broadcastMessage("§d ✦ " + player.getName() + " teleported to the Final Boss! ✦");
        player.sendTitle("§d§lFINAL BOSS", "§7Get ready for a beatdown!", 10, 40, 10);
    }

    private static void cleanupOldCooldowns() {
        long currentTime = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> currentTime - entry.getValue() > COOLDOWN_TIME * 2);
    }
}