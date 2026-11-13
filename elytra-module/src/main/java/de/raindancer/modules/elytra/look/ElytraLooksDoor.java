package de.raindancer.modules.elytra.look;

import de.raindancer.core.ui.cosmetic.CosmeticDoor;
import de.raindancer.core.ui.menu.Icons;
import de.raindancer.core.ui.menu.Menu;
import de.raindancer.modules.elytra.ElytraServices;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/** The looks as a door on the server's cosmetics page, beside everything else worn. */
public final class ElytraLooksDoor implements CosmeticDoor {

    private final ElytraServices services;

    public ElytraLooksDoor(ElytraServices services) {
        this.services = services;
    }

    @Override
    public String id() {
        return "elytra:looks";
    }

    @Override
    public ItemStack icon(Player viewer) {
        String wearing = ElytraLooks.find(viewer).flatMap(slot -> ElytraLooks.lookOf(slot.get(viewer)))
                .map(look -> "<gray>Yours: <white>" + look.title() + " chestplate").orElse("<gray>Yours: plain wings");
        return Icons.of(Material.ELYTRA, "<light_purple>Elytra looks",
                "<gray>A chestplate drawn with your wings.", wearing, "", "<dark_gray>Hold or wear an elytra.");
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
        new LooksMenu(services, viewer, parent).open();
    }
}
