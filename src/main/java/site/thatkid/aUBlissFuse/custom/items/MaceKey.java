package site.thatkid.aUBlissFuse.custom.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import site.thatkid.aUBlissFuse.AUBlissFuse;

import java.util.List;

public class MaceKey {

  public static ItemStack createMaceStack() {
    ItemStack wand = new ItemStack(Material.STICK);
    ItemMeta meta = wand.getItemMeta();
    if (meta == null) return wand;

    meta.setDisplayName("§dMace Key");
    meta.setLore(List.of(
      "§7A key that brings you to the truth of the mace."
    ));

    meta.setCustomModelData(1234567);

    NamespacedKey key = new NamespacedKey(AUBlissFuse.getInstance(), "mace_key");
    meta.getPersistentDataContainer()
        .set(key, PersistentDataType.BYTE, (byte)1);

    wand.setItemMeta(meta);
    return wand;
  }
}
