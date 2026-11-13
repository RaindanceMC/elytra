package de.raindancer.modules.elytra.look;

import de.raindancer.core.ui.cosmetic.CosmeticDoor;
import de.raindancer.core.ui.menu.Icons;
import de.raindancer.core.ui.menu.Menu;
import de.raindancer.modules.elytra.ElytraServices;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/** The dog armour looks as a door on the server's cosmetics page. */
public final class DogLooksDoor implements CosmeticDoor {

    private final ElytraServices services;

    public DogLooksDoor(ElytraServices services) {
        this.services = services;
    }

    @Override
    public String id() {
        return "elytra:dog-looks";
    }

    @Override
    public ItemStack icon(Player viewer) {
        String held = DogLooks.lookOf(viewer.getInventory().getItemInMainHand())
                .map(look -> "<gray>In your hand: <white>" + look.title()).orElse("<gray>In your hand: plain");
        return Icons.of(Material.WOLF_ARMOR, "<light_purple>Dog armour looks",
                "<gray>Netherite, diamond, gold… for your dog.", held, "", "<dark_gray>Hold dog armour.");
    }

    @Override
    public Optional<String> locked(Player viewer) {
        if (services.mayWear(viewer)) {
            return Optional.empty();
        }
        return Optional.of(services.primeOnly().getAsBoolean() ? "A Prime perk" : "Not open to you here");
    }

    @Override
    public void open(Player viewer, Menu parent) {
        new DogLooksMenu(services, viewer, parent).open();
    }
}
