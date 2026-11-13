package de.raindancer.modules.elytra.trim;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTrimRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Trims on elytra and horse armour, the game's own way: for every trim recipe the server has — vanilla's and any a
 * datapack adds — a twin whose base is the elytra, and one whose base is the horse armour. The game's trim recipe never checks what the base is, so the smithing table, its
 * preview, shift-clicking and the trim tooltip all work as for armour. The client draws the trim on the wings from
 * {@code trims/entity/wings/<pattern>}, which the pack brings.
 */
public final class ElytraTrimRecipes {

    private final Server server;
    private final String namespace;
    private final List<NamespacedKey> added = new ArrayList<>();

    public ElytraTrimRecipes(Server server, String namespace) {
        this.server = server;
        this.namespace = namespace;
    }

    /** The elytra's twin name for a trim recipe; see {@link TrimBase#twinKey}. */
    static Optional<NamespacedKey> twinKey(NamespacedKey original, String namespace, boolean hasPattern,
                                           boolean baseTakesElytra) {
        return TrimBase.ELYTRA.twinKey(original, namespace, hasPattern, baseTakesElytra);
    }

    /**
     * The twins for {@code recipes}, one per {@link TrimBase}: template, material, pattern and component copying kept,
     * the elytra or the horse armour as base.
     */
    static List<SmithingTrimRecipe> mirror(Iterable<? extends Recipe> recipes, String namespace) {
        List<SmithingTrimRecipe> twins = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (!(recipe instanceof SmithingTrimRecipe trim)) {
                continue;
            }
            for (TrimBase base : TrimBase.values()) {
                base.twinKey(trim.getKey(), namespace, trim.getTrimPattern() != null, base.takes(trim.getBase()))
                        .ifPresent(key -> twins.add(new SmithingTrimRecipe(key, trim.getTemplate(), base.choice(),
                                trim.getAddition(), trim.getTrimPattern(), trim.willCopyDataComponents())));
            }
        }
        return twins;
    }

    static boolean takesElytra(RecipeChoice base) {
        return TrimBase.ELYTRA.takes(base);
    }

    /** Adds the twins, replacing any from before — after a datapack reload the server's recipes may have changed. */
    public synchronized int register() {
        unregister();
        List<Recipe> current = new ArrayList<>();
        for (Iterator<Recipe> it = server.recipeIterator(); it.hasNext(); ) {
            current.add(it.next());
        }
        List<SmithingTrimRecipe> twins = mirror(current, namespace);
        for (int i = 0; i < twins.size(); i++) {
            SmithingTrimRecipe twin = twins.get(i);
            if (server.addRecipe(twin, i == twins.size() - 1)) {
                added.add(twin.getKey());
            }
        }
        return added.size();
    }

    public synchronized void unregister() {
        for (int i = 0; i < added.size(); i++) {
            server.removeRecipe(added.get(i), i == added.size() - 1);
        }
        added.clear();
    }

    /** Whether every twin is really in the server's recipes — the startup check. */
    public synchronized boolean allPresent() {
        return added.stream().allMatch(key -> server.getRecipe(key) instanceof SmithingTrimRecipe);
    }
}
