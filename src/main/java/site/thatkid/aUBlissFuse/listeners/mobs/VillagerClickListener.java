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

public class VillagerClickListener implements Listener {
    private final AUBlissFuse plugin;

    public VillagerClickListener(AUBlissFuse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (!(clicked instanceof Villager)) return;

        Villager villager = (Villager) clicked;
        Byte isGuide = villager.getPersistentDataContainer()
                              .get(plugin.GUIDE_KEY, PersistentDataType.BYTE);

        if (isGuide != null && isGuide == (byte) 1) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD) {
                Player player = (Player) event.getPlayer();
                player.getInventory().setItemInMainHand(null);
                player.sendMessage("§eHello adventurer! I'm hun-");
                player.sendMessage("§6Ohh, you got me one.");
                player.sendMessage("§9I guess I have to tell you where the mace is...");
                player.sendMessage("§e Ask the chicken at 100, 100 and leave me to eat this in piece.");
            } else {
                Player player = event.getPlayer();
                player.sendMessage("§eHello adventurer! I'm hungry!");
                player.sendMessage("§6Maybe I won't kill you");
                player.sendMessage("§6If you bring me the head of another.");
                player.sendMessage("§6Now go. Bring me back my desire and I will tell you the secret");
                player.sendMessage("§eOf the §9Mace!!!");
                event.setCancelled(true);
            }
        }
    }
}
