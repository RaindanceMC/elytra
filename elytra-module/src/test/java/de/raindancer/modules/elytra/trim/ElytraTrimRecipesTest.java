package de.raindancer.modules.elytra.trim;

import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Which trim recipes get an elytra twin, and under what name. Building the twin needs the server's registries
 * (a {@code TrimPattern} cannot exist without them); that it really trims an elytra is ElytraScenarioTest's, in e2e.
 */
class ElytraTrimRecipesTest {

    private static final String OURS = "rainselytra";
    private static final NamespacedKey COAST =
            NamespacedKey.minecraft("coast_armor_trim_smithing_template_smithing_trim");

    @Test
    @DisplayName("a trim recipe gets a twin named after it, so a datapack's own patterns never collide with vanilla's")
    void named() {
        assertThat(ElytraTrimRecipes.twinKey(COAST, OURS, true, false)).contains(
                new NamespacedKey(OURS, "elytra/minecraft/coast_armor_trim_smithing_template_smithing_trim"));
        assertThat(ElytraTrimRecipes.twinKey(new NamespacedKey("mypack", "trims/star"), OURS, true, false))
                .contains(new NamespacedKey(OURS, "elytra/mypack/trims/star"));
    }

    @Test
    @DisplayName("our own twins, recipes that take an elytra already and trims without a pattern get none")
    void skipped() {
        assertThat(ElytraTrimRecipes.twinKey(new NamespacedKey(OURS, "elytra/minecraft/x"), OURS, true, false))
                .as("a twin of a twin, on every datapack reload").isEmpty();
        assertThat(ElytraTrimRecipes.twinKey(COAST, OURS, true, true)).as("a datapack already did it").isEmpty();
        assertThat(ElytraTrimRecipes.twinKey(COAST, OURS, false, false)).as("nothing to put on").isEmpty();
    }

    @Test
    @DisplayName("horse armour takes trims too: every horse armour as the base of a twin of its own, named horse/...")
    void horseArmour() {
        assertThat(TrimBase.HORSE.twinKey(COAST, OURS, true, false)).contains(
                new NamespacedKey(OURS, "horse/minecraft/" + COAST.getKey()));
        assertThat(TrimBase.ELYTRA.twinKey(COAST, OURS, true, false)).isEqualTo(
                ElytraTrimRecipes.twinKey(COAST, OURS, true, false));
        assertThat(TrimBase.HORSE.materials()).containsExactlyInAnyOrder(org.bukkit.Material.LEATHER_HORSE_ARMOR,
                org.bukkit.Material.COPPER_HORSE_ARMOR, org.bukkit.Material.IRON_HORSE_ARMOR,
                org.bukkit.Material.GOLDEN_HORSE_ARMOR, org.bukkit.Material.DIAMOND_HORSE_ARMOR,
                org.bukkit.Material.NETHERITE_HORSE_ARMOR);
        // which recipe bases count as taken needs a server's RecipeChoice: e2e ElytraScenarioTest smiths one
    }
}
