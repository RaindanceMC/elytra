package de.raindancer.modules.elytra.look;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/** Puts a look on dog armour and reads it back: only the {@code equippable} asset changes, like the elytra's. */
public final class DogLooks {

    private DogLooks() {
    }

    public static boolean isDogArmour(ItemStack item) {
        return item != null && item.getType() == Material.WOLF_ARMOR;
    }

    public static Optional<DogLook> lookOf(ItemStack item) {
        if (!isDogArmour(item)) {
            return Optional.empty();
        }
        Equippable equippable = item.getData(DataComponentTypes.EQUIPPABLE);
        return equippable == null ? Optional.empty() : DogLook.ofAsset(equippable.assetId());
    }

    /** {@code null} takes the look off: the component goes back to the game's own. */
    public static void wear(ItemStack armour, DogLook look) {
        if (look == null) {
            armour.resetData(DataComponentTypes.EQUIPPABLE);
            return;
        }
        Equippable equippable = armour.getData(DataComponentTypes.EQUIPPABLE);
        if (equippable == null) {
            armour.resetData(DataComponentTypes.EQUIPPABLE);
            equippable = armour.getData(DataComponentTypes.EQUIPPABLE);
        }
        armour.setData(DataComponentTypes.EQUIPPABLE, equippable.toBuilder().assetId(look.asset()).build());
    }

    /** Dog armour in the main hand, changed on a copy and put back. @return whether they held some */
    public static boolean applyInHand(Player player, DogLook look) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (!isDogArmour(held)) {
            return false;
        }
        ItemStack copy = held.clone();
        wear(copy, look);
        player.getInventory().setItemInMainHand(copy);
        return true;
    }

    /** The look off what a wolf wears. On the wolf's own thread. @return whether one came off */
    public static boolean takeOff(Wolf wolf) {
        EntityEquipment equipment = wolf.getEquipment();
        ItemStack body = equipment.getItem(EquipmentSlot.BODY);
        if (lookOf(body).isEmpty()) {
            return false;
        }
        ItemStack plain = body.clone();
        wear(plain, null);
        equipment.setItem(EquipmentSlot.BODY, plain);
        return true;
    }
}
