package site.thatkid.aUBlissFuse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import site.thatkid.aUBlissFuse.custom.items.MaceKey;
import site.thatkid.aUBlissFuse.custom.blocks.TeleporterBlock;
import site.thatkid.aUBlissFuse.custom.jsonsaver.PlayerEntry;
import site.thatkid.aUBlissFuse.listeners.MobProtectListener;
import site.thatkid.aUBlissFuse.listeners.mobs.ChickenClickListener;
import site.thatkid.aUBlissFuse.listeners.mobs.IronGolemClickListener;
import site.thatkid.aUBlissFuse.listeners.mobs.VillagerClickListener;
import site.thatkid.aUBlissFuse.listeners.blocks.TeleporterBlockListener;
import site.thatkid.aUBlissFuse.listeners.mobs.WitherBossListener;
import site.thatkid.aUBlissFuse.listeners.mobs.connections.Connections;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class AUBlissFuse extends JavaPlugin {

    private static AUBlissFuse instance;

    public static NamespacedKey GUIDE_KEY;
    public static NamespacedKey BOSS_KEY;
    public static NamespacedKey CHICKEN_KEY;
    public static NamespacedKey IRON_GOLEM_KEY;

    private final File listFile = new File(getDataFolder(), "players.json");
    private final File connectionsFile = new File(getDataFolder(), "connections.json");

    private List<PlayerEntry> playerEntries;

    @Override
    public void onEnable() {
        instance = this;
        GUIDE_KEY = new NamespacedKey(this, "is_guide");
        CHICKEN_KEY = new NamespacedKey(this, "is_chicken");
        BOSS_KEY = new NamespacedKey(this, "is_boss");
        IRON_GOLEM_KEY = new NamespacedKey(this, "is_iron_golem");

        getServer().getPluginManager().registerEvents(new VillagerClickListener(this), this);
        getServer().getPluginManager().registerEvents(new ChickenClickListener(this), this);
        getServer().getPluginManager().registerEvents(new IronGolemClickListener(this), this);
        getServer().getPluginManager().registerEvents(new MobProtectListener(), this);
        getServer().getPluginManager().registerEvents(new WitherBossListener(), this);

        getServer().getPluginManager().registerEvents(new TeleporterBlockListener(), this);

        ItemStack mace_key = MaceKey.createMaceStack();
        ItemStack teleport_block = TeleporterBlock.createTeleporterItem();

        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        playerEntries = loadPlayerList();

        Connections.loadConnections(connectionsFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = cmd.getName().toLowerCase();

        if (name.equals("spawnguide") && sender instanceof Player) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            } else {
                Player player = (Player) sender;
                Villager villager = (Villager) player.getWorld()
                        .spawnEntity(player.getLocation(), EntityType.VILLAGER);

                villager.setCustomName("§aGuide");
                villager.setAI(false);
                villager.setAdult();
                villager.setGlowing(true);
                villager.setCustomNameVisible(true);
                villager.getPersistentDataContainer()
                        .set(GUIDE_KEY, PersistentDataType.BYTE, (byte) 1);

                player.sendMessage("Guide spawned!");
                return true;
            }
        }

        if (name.equals("spawnchicken") && sender instanceof Player) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            } else {
                Player player = (Player) sender;
                Chicken chicken = (Chicken) player.getWorld()
                        .spawnEntity(player.getLocation(), EntityType.CHICKEN);

                chicken.setCustomName("§aChicken");
                chicken.setAI(false);
                chicken.setBaby();
                chicken.setGlowing(true);
                chicken.setCustomNameVisible(true);
                chicken.getPersistentDataContainer()
                        .set(CHICKEN_KEY, PersistentDataType.BYTE, (byte) 1);
                player.sendMessage("Chicken spawned!");
                return true;
            }
        }

        if (name.equals("spawnirongolem") && sender instanceof Player) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            } else {
                Player player = (Player) sender;
                IronGolem irongolem = (IronGolem) player.getWorld()
                        .spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);

                irongolem.setCustomName("Possible");
                irongolem.setAI(false);
                irongolem.setGlowing(true);
                irongolem.setCustomNameVisible(true);
                irongolem.getPersistentDataContainer()
                        .set(IRON_GOLEM_KEY, PersistentDataType.BYTE, (byte) 1);
                player.sendMessage("Possible spawned!");
                return true;
            }
        }

        if (name.equals("give_mace_key") && sender instanceof Player) {
            Player p = (Player) sender;
            p.getInventory().addItem(MaceKey.createMaceStack());
            return true;
        }

        if (name.equals("give_teleporter") && sender instanceof Player) {
            if (!sender.hasPermission("blissfuse.give")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            Player player = (Player) sender;
            ItemStack teleporter = TeleporterBlock.createTeleporterItem();
            player.getInventory().addItem(teleporter);
            player.sendMessage("§d✦ Given teleporter block! Place it and right-click to teleport!");
            return true;
        }

        if (name.equals("addplayer")) {
            if (!sender.hasPermission("blissfuse.addplayer")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to run this.");
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /addplayer <playername>");
                return true;
            }

            String playerName = args[0];

            boolean exists = playerEntries.stream()
                    .anyMatch(entry -> entry.getName().equalsIgnoreCase(playerName));
            if (exists) {
                sender.sendMessage(ChatColor.RED + playerName + " is already on the list.");
                return true;
            }

            String timestamp = Instant.now().toString();
            playerEntries.add(new PlayerEntry(playerName, timestamp));
            savePlayerList(playerEntries);

            sender.sendMessage(ChatColor.GREEN
                    + "Added " + playerName
                    + " at " + timestamp
                    + ". Total entries: " + playerEntries.size());
            return true;
        }

        return false;
    }


    @Override
    public void onDisable() {
        Connections.saveConnections(connectionsFile);
    }

    public static AUBlissFuse getInstance() {
        return instance;
    }

    private List<PlayerEntry> loadPlayerList() {
        if (!listFile.exists()) {
            List<PlayerEntry> empty = new ArrayList<>();
            savePlayerList(empty);
            return empty;
        }
        try (Reader reader = new FileReader(listFile)) {
            return new Gson().fromJson(reader, new TypeToken<List<PlayerEntry>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void savePlayerList(List<PlayerEntry> entries) {
        try (Writer writer = new FileWriter(listFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(entries, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}