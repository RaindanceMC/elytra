package de.raindancer.modules.elytra;

import de.raindancer.core.ui.chat.Brand;
import de.raindancer.core.ui.messages.Messages;
import de.raindancer.modules.elytra.look.ElytraLook;
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
