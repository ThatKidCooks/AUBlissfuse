package site.thatkid.aUBlissFuse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import site.thatkid.aUBlissFuse.custom.items.MaceKey;
import site.thatkid.aUBlissFuse.custom.blocks.TeleporterBlock;
import site.thatkid.aUBlissFuse.custom.jsonsaver.PlayerEntry;
import site.thatkid.aUBlissFuse.custom.mobs.WitherBoss;
import site.thatkid.aUBlissFuse.listeners.MobProtectListener;
import site.thatkid.aUBlissFuse.listeners.mobs.*;
import site.thatkid.aUBlissFuse.listeners.blocks.TeleporterBlockListener;
import site.thatkid.aUBlissFuse.listeners.mobs.connections.Connections;
import site.thatkid.aUBlissFuse.listeners.pvp.PvPProtectListener;
import site.thatkid.aUBlissFuse.listeners.pvp.PvPToggleManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AUBlissFuse extends JavaPlugin {

    private static AUBlissFuse instance;

    public static NamespacedKey GUIDE_KEY;
    public static NamespacedKey BOSS_KEY;
    public static NamespacedKey CHICKEN_KEY;
    public static NamespacedKey IRON_GOLEM_KEY;
    public static NamespacedKey INFUSE_SERVER_TP;

    private final File listFile = new File(getDataFolder(), "players.json");
    private final File connectionsFile = new File(getDataFolder(), "connections.json");
    private final File pvpFile = new File(getDataFolder(), "pvpToggle.json");

    public static List<PlayerEntry> playerEntries;


    @Override
    public void onEnable() {
        instance = this;
        GUIDE_KEY = new NamespacedKey(this, "is_guide");
        CHICKEN_KEY = new NamespacedKey(this, "is_chicken");
        BOSS_KEY = new NamespacedKey(this, "is_boss");
        IRON_GOLEM_KEY = new NamespacedKey(this, "is_iron_golem");
        INFUSE_SERVER_TP = new NamespacedKey(this, "is_infuse_tp");

        getServer().getPluginManager().registerEvents(new VillagerClickListener(this), this);
        getServer().getPluginManager().registerEvents(new ChickenClickListener(this), this);
        getServer().getPluginManager().registerEvents(new IronGolemClickListener(this), this);
        getServer().getPluginManager().registerEvents(new InfuseTpClickListener(this), this);
        getServer().getPluginManager().registerEvents(new MobProtectListener(), this);
        getServer().getPluginManager().registerEvents(new PvPProtectListener(), this);
        getServer().getPluginManager().registerEvents(new WitherBossListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleporterBlockListener(), this);

        ItemStack maceKey = MaceKey.createMaceStack();
        ItemStack teleportBlock = TeleporterBlock.createTeleporterItem();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        playerEntries = loadPlayerList();
        Connections.loadConnections(connectionsFile);

        PvPToggleManager.init(getDataFolder());

        getLogger().info("AUBlissFuse enabled successfully!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = cmd.getName().toLowerCase();

        if (name.equals("spawninfuseteleporter")) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }

            Player player = (Player) sender;
            Villager villager = (Villager) player.getWorld()
                    .spawnEntity(player.getLocation(), EntityType.VILLAGER);
            villager.setCustomName("§aInfuse");
            villager.setCustomNameVisible(true);
            villager.setAI(false);
            villager.setAdult();
            villager.getPersistentDataContainer()
                    .set(INFUSE_SERVER_TP, PersistentDataType.BYTE, (byte) 1);
            villager.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            villager.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 255));
            player.sendMessage(ChatColor.GOLD + "Infuse tp has been spawned!");
            return true;
        }

        if (name.equals("spawnguide") && sender instanceof Player) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
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
            villager.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            villager.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 255));
            player.sendMessage("Guide spawned!");
            return true;
        }

        if (name.equals("spawnchicken") && sender instanceof Player) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            Player player = (Player) sender;
            Chicken chicken = (Chicken) player.getWorld()
                    .spawnEntity(player.getLocation(), EntityType.CHICKEN);
            chicken.setCustomName("§aChicken God");
            chicken.setAI(false);
            chicken.setBaby();
            chicken.setGlowing(true);
            chicken.setCustomNameVisible(true);
            chicken.getPersistentDataContainer()
                    .set(CHICKEN_KEY, PersistentDataType.BYTE, (byte) 1);
            chicken.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            chicken.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 255));
            player.sendMessage("Chicken spawned!");
            return true;
        }

        if (name.equals("spawnirongolem") && sender instanceof Player) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            Player player = (Player) sender;
            IronGolem ironGolem = (IronGolem) player.getWorld()
                    .spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);
            ironGolem.setCustomName("Possible");
            ironGolem.setAI(false);
            ironGolem.setGlowing(true);
            ironGolem.setCustomNameVisible(true);
            ironGolem.getPersistentDataContainer()
                    .set(IRON_GOLEM_KEY, PersistentDataType.BYTE, (byte) 1);
            ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 255));
            player.sendMessage("Possible spawned!");
            return true;
        }

        if (name.equals("spawn_boss") && sender instanceof Player) {
            Player player = (Player) sender;

            if  (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }

            WitherBoss.createBossAtPLayer(player);
        }

        if (name.equals("give_mace_key") && sender instanceof Player) {
            Player player = (Player) sender;
            player.getInventory().addItem(MaceKey.createMaceStack());
            return true;
        }

        if (name.equals("give_teleporter") && sender instanceof Player) {
            if (!sender.hasPermission("blissfuse.give")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            Player player = (Player) sender;
            player.getInventory().addItem(TeleporterBlock.createTeleporterItem());
            player.sendMessage("§d✦ Given teleporter block! Place it and right-click to teleport!");
            return true;
        }

        if (name.equals("addplayer") && sender instanceof Player) {
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

        if (name.equals("finalplayers") && sender instanceof Player) {
            Player player = (Player) sender;

            List<PlayerEntry> all = loadPlayerList();
            if (all.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "No players have been added yet.");
                return true;
            }

            DateTimeFormatter fmt = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss z")
                    .withZone(ZoneId.of("Australia/Sydney"));

            player.sendMessage(ChatColor.AQUA + "=== Final Players List ===");
            for (PlayerEntry entry : all) {
                Instant instant = Instant.parse(entry.getAddedAt());
                String localTime = fmt.format(instant);
                String line = ChatColor.GREEN
                        + entry.getName()
                        + ChatColor.GRAY + " added at "
                        + ChatColor.WHITE + localTime;
                player.sendMessage(line);
            }
            player.sendMessage(ChatColor.AQUA + "Total: " + all.size());
            return true;
        }

        if (name.equals("pvp") && sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /pvp <on|off|status|toggle>");
                return true;
            }

            if (args[0].equals("on")) {
                PvPToggleManager.setToggled(true);
                sender.sendMessage(ChatColor.AQUA + "PvP is On");
                return true;
            } else if (args[0].equals("off")) {
                PvPToggleManager.setToggled(false);
                sender.sendMessage(ChatColor.AQUA + "PvP is Off");
                return true;
            }  else if (args[0].equals("toggle")) {
                if (PvPToggleManager.isToggled()) {
                    PvPToggleManager.setToggled(false);
                } else {
                    PvPToggleManager.setToggled(true);
                }
                return true;
            } else if (args[0].equals("status")) {
                if (PvPToggleManager.isToggled()) {
                    sender.sendMessage(ChatColor.AQUA + "PvP is On");
                } else {
                    sender.sendMessage(ChatColor.AQUA + "PvP is Off");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /pvp <on|off|status|toggle>");
            }  return true;
        }

        if (label.equalsIgnoreCase("tempban") || !(sender instanceof Player)) {

            Player admin = (Player) sender;

            if (args.length != 3) {
                admin.sendMessage(ChatColor.YELLOW + "Usage: /tempban <playername> <length> <reason>");
                return true;
            }

            String targetName = args[0];
            String lengthArg = args[1].toLowerCase();
            String reasonArg = args[2].toLowerCase();

            Pattern pattern = Pattern.compile("^(\\d+)([mhdw])$");
            Matcher matcher = pattern.matcher(lengthArg);
            if (!matcher.matches()) {
                admin.sendMessage(ChatColor.RED + "Invalid length. Use 1m, 1h, 1d or 1w.");
                return true;
            }

            long amount = Long.parseLong(matcher.group(1));
            char unit = matcher.group(2).charAt(0);

            long millis;
            switch (unit) {
                case 'm':
                    millis = TimeUnit.MINUTES.toMillis(amount);
                    break;
                case 'h':
                    millis = TimeUnit.HOURS.toMillis(amount);
                    break;
                case 'd':
                    millis = TimeUnit.DAYS.toMillis(amount);
                    break;
                case 'w':
                    millis = TimeUnit.DAYS.toMillis(amount * 7);
                    break;
                default:
                    admin.sendMessage(ChatColor.RED + "Unknown time unit.");
                    return true;
            }

            Date expires = new Date(System.currentTimeMillis() + millis);

            String reason = "Temp-banned by " + admin.getName() + " for " + reasonArg + ". You have been banned for " + lengthArg;
            BanList banList = Bukkit.getBanList(BanList.Type.NAME);
            banList.addBan(targetName, reason, expires, admin.getName());

            Player target = Bukkit.getPlayerExact(targetName);
            if (target != null) {
                target.kickPlayer(ChatColor.RED + "You’ve been temporarily banned until "
                        + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expires));
            }

            admin.sendMessage(ChatColor.GREEN + targetName + " banned for " + lengthArg);
            return true;
        }

        return false;
    }

    @Override
    public void onDisable() {
        Connections.saveConnections(connectionsFile);

        PvPToggleManager.save();
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
            return new Gson().fromJson(reader,
                    new TypeToken<List<PlayerEntry>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void savePlayerList(List<PlayerEntry> entries) {
        try (Writer writer = new FileWriter(listFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(entries, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
