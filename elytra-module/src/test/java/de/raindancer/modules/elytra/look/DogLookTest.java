package de.raindancer.modules.elytra.look;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/** Prime's looks for dog armour: an armour of a metal drawn on the wolf, the armour itself unchanged. */
class DogLookTest {

    @Test
    @DisplayName("seven looks, each its own asset in the plugin's namespace, and the asset leads back to the look")
    void assetsRoundTrip() {
        assertThat(DogLook.values()).extracting(DogLook::id)
                .containsExactly("leather", "copper", "iron", "gold", "diamond", "netherite", "gilded");
        for (DogLook look : DogLook.values()) {
            assertThat(look.asset()).isEqualTo(Key.key("rainselytra", "dog_" + look.id()));
            assertThat(DogLook.ofAsset(look.asset())).contains(look);
        }
        assertThat(Arrays.stream(DogLook.values()).map(DogLook::asset).distinct()).hasSize(DogLook.values().length);
    }

    @Test
    @DisplayName("plain dog armour, an elytra look or anybody else's asset is no dog look; typed names are read loosely")
    void foreignAndTyped() {
        assertThat(DogLook.ofAsset(DogLook.PLAIN)).isEmpty();
        assertThat(DogLook.ofAsset(ElytraLook.NETHERITE.asset())).isEmpty();
        assertThat(DogLook.ofAsset(null)).isEmpty();
        assertThat(DogLook.parse(" Gilded ")).contains(DogLook.GILDED);
        assertThat(DogLook.parse("mithril")).isEmpty();
    }
}
