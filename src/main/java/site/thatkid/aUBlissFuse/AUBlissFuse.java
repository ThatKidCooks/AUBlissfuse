package site.thatkid.aUBlissFuse;

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
import site.thatkid.aUBlissFuse.listeners.MobProtectListener;
import site.thatkid.aUBlissFuse.listeners.mobs.ChickenClickListener;
import site.thatkid.aUBlissFuse.listeners.mobs.VillagerClickListener;
import site.thatkid.aUBlissFuse.listeners.blocks.TeleporterBlockListener;
import site.thatkid.aUBlissFuse.listeners.mobs.WitherBossListener;

public final class AUBlissFuse extends JavaPlugin {

    private static AUBlissFuse instance;

    public static NamespacedKey GUIDE_KEY;
    public static NamespacedKey BOSS_KEY;
    public static NamespacedKey CHICKEN_KEY;
    public static NamespacedKey IRON_GOLEM_KEY;

    @Override
    public void onEnable() {
        instance = this;
        GUIDE_KEY = new NamespacedKey(this, "is_guide");
        CHICKEN_KEY = new NamespacedKey(this, "is_chicken");
        BOSS_KEY = new NamespacedKey(this, "is_boss");
        IRON_GOLEM_KEY = new NamespacedKey(this, "is_iron_golem");

        getServer().getPluginManager().registerEvents(new VillagerClickListener(this), this);
        getServer().getPluginManager().registerEvents(new ChickenClickListener(this), this);
        getServer().getPluginManager().registerEvents(new MobProtectListener(), this);
        getServer().getPluginManager().registerEvents(new WitherBossListener(), this);

        getServer().getPluginManager().registerEvents(new TeleporterBlockListener(), this);

        ItemStack mace_key = MaceKey.createMaceStack();
        ItemStack teleport_block = TeleporterBlock.createTeleporterItem();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("spawnguide") && sender instanceof Player) {
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

        if (label.equalsIgnoreCase("spawnchicken") && sender instanceof Player) {
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

        if (label.equalsIgnoreCase("spawnirongolem") && sender instanceof Player) {
            if (!sender.hasPermission("customvillager.spawn")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
            else  {
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

        if (label.equalsIgnoreCase("give_mace_key") && sender instanceof Player) {
            Player p = (Player) sender;
            p.getInventory().addItem(MaceKey.createMaceStack());
            return true;
        }

        if (label.equalsIgnoreCase("give_teleporter") && sender instanceof Player) {
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

        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AUBlissFuse getInstance() {
        return instance;
    }
}