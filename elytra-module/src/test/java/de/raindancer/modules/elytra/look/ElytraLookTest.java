package de.raindancer.modules.elytra.look;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ElytraLookTest {

    @Test
    @DisplayName("each look has its own asset in the plugin's namespace, and the asset leads back to the look")
    void assetsRoundTrip() {
        assertThat(Arrays.stream(ElytraLook.values()).map(ElytraLook::asset).distinct()).hasSize(ElytraLook.values().length);
        for (ElytraLook look : ElytraLook.values()) {
            assertThat(look.asset().namespace()).isEqualTo("rainselytra");
            assertThat(ElytraLook.ofAsset(look.asset())).contains(look);
        }
        assertThat(ElytraLook.DIAMOND.asset()).isEqualTo(Key.key("rainselytra", "diamond_chestplate"));
    }

    @Test
    @DisplayName("a plain elytra, or somebody else's asset, wears no look of ours")
    void foreignAssets() {
        assertThat(ElytraLook.ofAsset(ElytraLook.PLAIN)).isEmpty();
        assertThat(ElytraLook.ofAsset(Key.key("rainselytra", "nonsense"))).isEmpty();
        assertThat(ElytraLook.ofAsset(Key.key("minecraft", "diamond"))).isEmpty();
        assertThat(ElytraLook.ofAsset(null)).isEmpty();
    }

    @Test
    @DisplayName("typed names are found case-blind, and 'none' or 'off' means the plain wings")
    void parsing() {
        assertThat(ElytraLook.parse("Netherite")).contains(ElytraLook.NETHERITE);
        assertThat(ElytraLook.parse("chainmail")).contains(ElytraLook.CHAINMAIL);
        assertThat(ElytraLook.parse("bogus")).isEmpty();
        assertThat(ElytraLook.isPlainWord("none")).isTrue();
        assertThat(ElytraLook.isPlainWord("OFF")).isTrue();
        assertThat(ElytraLook.isPlainWord("diamond")).isFalse();
    }
}
