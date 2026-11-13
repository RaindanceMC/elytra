package de.raindancer.modules.elytra;

import de.raindancer.core.ui.chat.Brand;
import de.raindancer.core.ui.messages.Messages;
import de.raindancer.modules.elytra.look.ElytraLook;
import de.raindancer.modules.elytra.look.DogLook;
import de.raindancer.modules.elytra.look.DogLooks;
import de.raindancer.modules.elytra.look.ElytraLooks;
import de.raindancer.modules.elytra.util.PermissionNodes;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/** What the command, the menu and the cosmetics door share. */
public record ElytraServices(Messages messages, Brand brand, BooleanSupplier primeOnly) {

    /** Whether {@code player} may put a look on. Taking one off is always allowed — an ended Prime included. */
    public boolean mayWear(Player player) {
        return player.hasPermission(PermissionNodes.LOOKS);
    }

    /**
     * Looks only stay with whoever may wear them: anybody else's come off — on what they carry, and on the dogs of
     * theirs around them — and they are told once. On the player's own thread.
     */
    public void keepLooksAllowed(org.bukkit.plugin.Plugin plugin, Player player) {
        if (mayWear(player)) {
            return;
        }
        if (ElytraLooks.takeOffAll(player.getInventory()) > 0) {
            messages.send(player, primeOnly.getAsBoolean() ? "elytra.look-lost-prime" : "elytra.look-lost");
        }
        for (org.bukkit.entity.Entity near : player.getNearbyEntities(DOG_REACH, DOG_REACH, DOG_REACH)) {
            if (near instanceof org.bukkit.entity.Wolf dog && player.getUniqueId().equals(ownerOf(dog))) {
                de.raindancer.core.platform.util.Scheduling.entity(plugin, dog, () -> {
                    if (DogLooks.takeOff(dog)) {
                        messages.send(player, primeOnly.getAsBoolean() ? "elytra.dog-lost-prime" : "elytra.dog-lost");
                    }
                });
            }
        }
    }

    private static final double DOG_REACH = 48;

    private static java.util.UUID ownerOf(org.bukkit.entity.Wolf dog) {
        return dog.getOwnerUniqueId();
    }

    /** Puts {@code look} (null: none) on the dog armour in the player's hand and tells them. */
    public void wearDog(Player player, DogLook look) {
        org.bukkit.inventory.ItemStack held = player.getInventory().getItemInMainHand();
        if (!DogLooks.isDogArmour(held)) {
            messages.send(player, "elytra.dog-hold-one");
            return;
        }
        if (look != null && !mayWear(player)) {
            messages.send(player, primeOnly.getAsBoolean() ? "elytra.dog-prime-only" : "elytra.not-allowed");
            return;
        }
        if (Objects.equals(DogLooks.lookOf(held).orElse(null), look)) {
            messages.send(player, "elytra.dog-already");
            return;
        }
        DogLooks.applyInHand(player, look);
        if (look == null) {
            messages.send(player, "elytra.dog-look-off");
        } else {
            messages.send(player, "elytra.dog-look-on", "look", look.title());
        }
    }

    /** Puts {@code look} (null: none) on the player's elytra and tells them. */
    public void wear(Player player, ElytraLook look) {
        Optional<ElytraLooks.Slot> slot = ElytraLooks.find(player);
        if (slot.isEmpty()) {
            messages.send(player, "elytra.hold-one");
            return;
        }
        if (look != null && !mayWear(player)) {
            messages.send(player, primeOnly.getAsBoolean() ? "elytra.prime-only" : "elytra.not-allowed");
            return;
        }
        if (Objects.equals(ElytraLooks.lookOf(slot.get().get(player)).orElse(null), look)) {
            messages.send(player, "elytra.already");
            return;
        }
        ElytraLooks.apply(player, look);
        if (look == null) {
            messages.send(player, "elytra.look-off");
        } else {
            messages.send(player, "elytra.look-on", "look", look.title());
        }
    }
}
