package de.raindancer.modules.elytra.trim;

import de.raindancer.core.platform.log.LogChannel;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/** {@code /minecraft:reload} can add, change or drop trim recipes; the elytra twins follow. */
public final class RecipeReloadListener implements Listener {

    private final ElytraTrimRecipes recipes;
    private final LogChannel log;

    public RecipeReloadListener(ElytraTrimRecipes recipes, LogChannel log) {
        this.recipes = recipes;
        this.log = log;
    }

    @EventHandler
    public void reloaded(ServerResourcesReloadedEvent event) {
        log.info("Datapacks reloaded: {} trim recipes take an elytra.", recipes.register());
    }
}
