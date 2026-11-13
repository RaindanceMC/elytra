package de.raindancer.modules.elytra.trim;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

import java.util.Optional;
import java.util.Set;

/** What else takes a trim at the smithing table, besides armour: each gets a twin of every trim recipe. */
public enum TrimBase {

    /** Drawn on the wings from {@code trims/entity/wings/<pattern>}. */
    ELYTRA("elytra", Set.of(Material.ELYTRA)),
    /** Drawn on the horse armour from {@code trims/entity/horse_body/<pattern>}. */
    HORSE("horse", Set.of(Material.LEATHER_HORSE_ARMOR, Material.COPPER_HORSE_ARMOR, Material.IRON_HORSE_ARMOR,
            Material.GOLDEN_HORSE_ARMOR, Material.DIAMOND_HORSE_ARMOR, Material.NETHERITE_HORSE_ARMOR));

    private final String prefix;
    private final Set<Material> materials;

    TrimBase(String prefix, Set<Material> materials) {
        this.prefix = prefix;
        this.materials = materials;
    }

    public Set<Material> materials() {
        return materials;
    }

    public RecipeChoice choice() {
        return new RecipeChoice.MaterialChoice(materials.stream().sorted().toList());
    }

    /** Whether a recipe's base already takes one of these — then a datapack did it and no twin is needed. */
    public boolean takes(RecipeChoice base) {
        return base instanceof RecipeChoice.MaterialChoice choice
                && choice.getChoices().stream().anyMatch(materials::contains);
    }

    /** The twin's name for a trim recipe, or empty when it gets none: our own, base takes these already, no pattern. */
    public Optional<NamespacedKey> twinKey(NamespacedKey original, String namespace, boolean hasPattern,
                                           boolean baseTakesThese) {
        if (!hasPattern || baseTakesThese || original.getNamespace().equals(namespace)) {
            return Optional.empty();
        }
        return Optional.of(new NamespacedKey(namespace, prefix + "/" + original.getNamespace() + "/" + original.getKey()));
    }
}
