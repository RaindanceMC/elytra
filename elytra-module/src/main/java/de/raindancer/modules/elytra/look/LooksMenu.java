package de.raindancer.modules.elytra.look;

import de.raindancer.core.ui.menu.Icons;
import de.raindancer.core.ui.menu.Menu;
import de.raindancer.core.ui.menu.PaginatedMenu;
import de.raindancer.modules.elytra.ElytraServices;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

/** Every chestplate look: a click puts it on the elytra in hand, or the one worn. */
public final class LooksMenu extends PaginatedMenu<ElytraLook> {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private final ElytraServices services;

    public LooksMenu(ElytraServices services, Player viewer, Menu parent) {
        super(viewer, services.brand(), parent);
        this.services = services;
    }

    @Override
    protected Component title() {
        return MINI.deserialize("<dark_gray>Elytra looks");
    }

    @Override
    public String breadcrumb() {
        return "Elytra looks";
    }

    @Override
    protected List<ElytraLook> entries() {
        return List.of(ElytraLook.values());
    }

    private Optional<ElytraLook> current() {
        return ElytraLooks.find(viewer).flatMap(slot -> ElytraLooks.lookOf(slot.get(viewer)));
    }

    @Override
    protected ItemStack icon(ElytraLook look) {
        boolean hasOne = ElytraLooks.find(viewer).isPresent();
        String state = current().filter(look::equals).isPresent() ? "<green>Your elytra wears it."
                : !services.mayWear(viewer) ? (services.primeOnly().getAsBoolean() ? "<light_purple>A Prime perk."
                : "<red>Not open to you here.")
                : hasOne ? "<yellow>Click to put it on your elytra." : "<gray>Hold or wear an elytra to put it on.";
        return Icons.of(look.icon(), "<white>" + look.title() + " chestplate", List.of(
                "<gray>The wings with a " + look.title().toLowerCase(java.util.Locale.ROOT) + " chestplate.",
                state, "", "<dark_gray>Only a look: it protects like an elytra."));
    }

    @Override
    protected void onClick(ElytraLook look, InventoryClickEvent event) {
        services.wear(viewer, look);
        refresh();
    }

    @Override
    protected void decorate() {
        super.decorate();
        toolbar(4, Icons.of(Material.ELYTRA, "<gray>Plain wings", "<dark_gray>Takes the chestplate look off."),
                click -> {
                    services.wear(viewer, null);
                    refresh();
                });
    }

    @Override
    protected List<String> helpLines() {
        return List.of(
                "A look draws a chestplate together with",
                "the wings. It changes nothing else: the",
                "elytra flies and protects as it always did.",
                "Trims go on at a smithing table, as on armour.");
    }
}
