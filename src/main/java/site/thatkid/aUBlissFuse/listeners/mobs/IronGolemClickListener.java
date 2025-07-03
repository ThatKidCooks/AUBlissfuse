package site.thatkid.aUBlissFuse.listeners.mobs;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import site.thatkid.aUBlissFuse.AUBlissFuse;

public class IronGolemClickListener implements Listener {
    private final AUBlissFuse plugin;

    public IronGolemClickListener(AUBlissFuse plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (!(clicked instanceof IronGolem)) return;

        IronGolem ironGolem = (IronGolem) clicked;
        Byte isGuide = ironGolem.getPersistentDataContainer()
                              .get(plugin.GUIDE_KEY, PersistentDataType.BYTE);

        if (isGuide != null && isGuide == (byte) 1) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_BLOCK && event.getPlayer().getInventory().getItemInMainHand().getAmount() > 5) {
                Player player = (Player) event.getPlayer();
                player.getInventory().setItemInMainHand(null);
                player.sendMessage("§eHello adventurer! I have a ri--");
                player.sendMessage("§6Ohh, you figured it out");
                player.sendMessage("§9But since I only tell you riddles");
                player.sendMessage("§eWhat's hurdling towards earth. Apep!");
            } else {
                Player player = event.getPlayer();
                player.sendMessage("§eHello adventurer! I have a riddle");
                player.sendMessage("§6I’m born of ore in caverns deep,");
                player.sendMessage("§6A sturdy block you stack and keep.");
                player.sendMessage("§6To learn my secret, don’t be shy—");
                player.sendMessage("§eDrop ten of me, then I will reply.");
                event.setCancelled(true);
            }
        }
    }
}
