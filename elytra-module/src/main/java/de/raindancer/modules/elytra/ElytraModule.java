package de.raindancer.modules.elytra;

import de.raindancer.modules.api.FlexModule;
import de.raindancer.modules.api.ModuleCommand;
import de.raindancer.modules.api.ModuleContext;
import de.raindancer.modules.api.ModuleInfo;
import de.raindancer.modules.elytra.look.ElytraLook;
import de.raindancer.modules.elytra.look.ElytraLooksDoor;
import de.raindancer.modules.elytra.pack.ElytraPack;
import de.raindancer.modules.elytra.trim.ElytraTrimRecipes;
import de.raindancer.modules.elytra.trim.RecipeReloadListener;
import de.raindancer.modules.elytra.util.PermissionNodes;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;

import java.util.List;

/**
 * Armour trims on elytra — the smithing table takes them, the wings show them — and chestplate looks: a diamond or
 * netherite chestplate drawn with the wings, only as a look.
 */
public final class ElytraModule implements FlexModule {

    private static final ModuleInfo INFO = ModuleInfo.of("elytra", "Elytra", "0.1.0")
            .describedAs("Armour trims on elytra, and chestplate looks for them with /elytra")
            .by("Raindancer118");

    private static volatile ElytraServices services;

    @Override
    public ModuleInfo info() {
        return INFO;
    }

    @Override
    public void enable(ModuleContext context) {
        Server server = context.plugin().getServer();
        context.core().messages().defineFrom(ElytraModule.class.getResourceAsStream("messages.yml"),
                context.chat().brand()::chatPrefix);
        context.core().messages().seriousFrom(ElytraModule.class.getResourceAsStream("messages-serious.yml"));
        PermissionNodes.register(server);

        ElytraTrimRecipes recipes = new ElytraTrimRecipes(server,
                new NamespacedKey(context.plugin(), "elytra").getNamespace());
        int trims = recipes.register();
        context.closeWith(recipes::unregister);
        context.listener(new RecipeReloadListener(recipes, context.log()));
        if (trims == 0 || !recipes.allPresent()) {
            context.log().warn("No trim recipe for elytra is in the server's recipes ({} added) — the smithing "
                    + "table will not take an elytra.", trims);
        }

        ElytraPack.contribute(context.plugin(), context.core(), context.log());
        context.closeWith(() -> ElytraPack.withdraw(context.core()));

        ElytraServices live = new ElytraServices(context.core().messages(), context.chat().brand());
        services = live;
        // A door on the cosmetics page (Core 2.34.0); /elytra opens the same looks either way.
        try {
            de.raindancer.core.ui.cosmetic.CosmeticDoor door = new ElytraLooksDoor(live);
            de.raindancer.core.ui.cosmetic.CosmeticDoors.provide(context.plugin(), door);
            context.closeWith(() -> de.raindancer.core.ui.cosmetic.CosmeticDoors.retract(door));
        } catch (LinkageError olderCore) {
            context.log().info("RainsCore is older than 2.34.0: the elytra looks are only in /elytra.");
        }
        context.log().info("Elytra is up: {} trim recipes take an elytra, {} chestplate looks.", trims,
                ElytraLook.values().length);
    }

    @Override
    public List<ModuleCommand> commands() {
        return List.of(ModuleCommand.of("elytra", "Picks a chestplate look for your elytra",
                        new ElytraCommand(ElytraModule::require))
                .aliased("elytralook", "wings")
                .taking("(nothing) — pick a look", "look <name> — put one on", "look none — plain wings again")
                .needing(PermissionNodes.LOOKS));
    }

    @Override
    public void disable() {
        services = null;
    }

    private static ElytraServices require() {
        ElytraServices live = services;
        if (live == null) {
            throw new IllegalStateException("the elytra module is not running");
        }
        return live;
    }
}
