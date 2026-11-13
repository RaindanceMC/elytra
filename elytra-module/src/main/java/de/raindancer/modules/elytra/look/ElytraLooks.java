package de.raindancer.modules.elytra.look;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;

/** Puts a look on an elytra and reads it back: only the {@code equippable} asset changes, so it flies and protects as before. */
@SuppressWarnings("UnstableApiUsage")
public final class ElytraLooks {

    private ElytraLooks() {
    }

    public static boolean isElytra(ItemStack item) {
        return item != null && item.getType() == Material.ELYTRA;
    }

    public static Optional<ElytraLook> lookOf(ItemStack item) {
        if (!isElytra(item)) {
            return Optional.empty();
        }
        Equippable equippable = item.getData(DataComponentTypes.EQUIPPABLE);
        return equippable == null ? Optional.empty() : ElytraLook.ofAsset(equippable.assetId());
    }

    /** {@code null} takes the look off: the component goes back to the game's own, so it stacks with plain elytra again. */
    public static void wear(ItemStack elytra, ElytraLook look) {
        if (look == null) {
            elytra.resetData(DataComponentTypes.EQUIPPABLE);
            return;
        }
        Equippable equippable = elytra.getData(DataComponentTypes.EQUIPPABLE);
        if (equippable == null) {
            elytra.resetData(DataComponentTypes.EQUIPPABLE);
            equippable = elytra.getData(DataComponentTypes.EQUIPPABLE);
        }
        elytra.setData(DataComponentTypes.EQUIPPABLE, equippable.toBuilder().assetId(look.asset()).build());
    }

    /** The elytra in the main hand, else the one worn. */
    public static Optional<Slot> find(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (isElytra(inventory.getItemInMainHand())) {
            return Optional.of(Slot.HAND);
        }
        return isElytra(inventory.getChestplate()) ? Optional.of(Slot.WORN) : Optional.empty();
    }

    /** Where the elytra is. Changed on a copy and put back, so it never depends on whether Paper hands out a mirror. */
    public enum Slot {
        HAND, WORN;

        public ItemStack get(Player player) {
            return this == HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getChestplate();
        }

        public void put(Player player, ItemStack item) {
            if (this == HAND) {
                player.getInventory().setItemInMainHand(item);
            } else {
                player.getInventory().setChestplate(item);
            }
        }
    }

    /** Changes the look of whichever elytra {@code player} has. @return where it was, empty when they have none */
    public static Optional<Slot> apply(Player player, ElytraLook look) {
        Optional<Slot> slot = find(player);
        slot.ifPresent(where -> {
            ItemStack copy = where.get(player).clone();
            wear(copy, look);
            where.put(player, copy);
        });
        return slot;
    }
}
