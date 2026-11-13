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
import java.util.Locale;
import java.util.Optional;

/** Every dog armour look: a click puts it on the dog armour in hand. */
public final class DogLooksMenu extends PaginatedMenu<DogLook> {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private final ElytraServices services;

    public DogLooksMenu(ElytraServices services, Player viewer, Menu parent) {
        super(viewer, services.brand(), parent);
        this.services = services;
    }

    @Override
    protected Component title() {
        return MINI.deserialize("<dark_gray>Dog armour looks");
    }

    @Override
    public String breadcrumb() {
        return "Dog armour looks";
    }

    @Override
    protected List<DogLook> entries() {
        return List.of(DogLook.values());
    }

    private Optional<DogLook> current() {
        return DogLooks.lookOf(viewer.getInventory().getItemInMainHand());
    }

    @Override
    protected ItemStack icon(DogLook look) {
        boolean holding = DogLooks.isDogArmour(viewer.getInventory().getItemInMainHand());
        String state = current().filter(look::equals).isPresent() ? "<green>The armour in your hand wears it."
                : !services.mayWear(viewer) ? (services.primeOnly().getAsBoolean() ? "<light_purple>A Prime perk."
                : "<red>Not open to you here.")
                : holding ? "<yellow>Click to put it on the armour in your hand." : "<gray>Hold dog armour to put it on.";
        return Icons.of(look.icon(), "<white>" + look.title() + " dog armour", List.of(
                "<gray>Your dog in " + look.title().toLowerCase(Locale.ROOT) + " armour.",
                state, "", "<dark_gray>Only a look: it protects like dog armour."));
    }

    @Override
    protected void onClick(DogLook look, InventoryClickEvent event) {
        services.wearDog(viewer, look);
        refresh();
    }

    @Override
    protected void decorate() {
        super.decorate();
        toolbar(4, Icons.of(Material.WOLF_ARMOR, "<gray>Plain dog armour", "<dark_gray>Takes the look off."),
                click -> {
                    services.wearDog(viewer, null);
                    refresh();
                });
    }

    @Override
    protected List<String> helpLines() {
        return List.of(
                "A look draws another armour on your dog.",
                "It changes nothing else: the dog armour",
                "protects and wears out as it always did.",
                "Put it on, then give the armour to your dog.");
    }
}
