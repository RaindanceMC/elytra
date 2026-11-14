package de.raindancer.modules.elytra.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.raindancer.modules.elytra.look.ElytraLook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/** What the client is sent: every file listed, every trim pattern and material drawn, every look a real asset. */
class ElytraPackTest {

    private static final Path ROOT = Path.of("src/main/resources" + ElytraPack.ROOT);
    /** The game's 26.3 trim patterns. A new one without a wing texture renders as a missing texture on the wings. */
    private static final List<String> PATTERNS = List.of("bolt", "coast", "dune", "eye", "flow", "host", "raiser",
            "rib", "sentry", "shaper", "silence", "snout", "spire", "tide", "vex", "ward", "wayfinder", "wild");
    private static final List<String> MATERIALS = List.of("amethyst", "copper", "diamond", "emerald", "gold", "iron",
            "lapis", "netherite", "quartz", "redstone", "resin");

    private static JsonObject json(String path) throws IOException {
        return JsonParser.parseString(Files.readString(ROOT.resolve(path))).getAsJsonObject();
    }

    @Test
    @DisplayName("FILES is exactly what is in the pack folder, so nothing is left behind when it is copied out")
    void filesListed() throws IOException {
        Set<String> onDisk = new TreeSet<>();
        try (Stream<Path> walk = Files.walk(ROOT)) {
            walk.filter(Files::isRegularFile).forEach(file -> onDisk.add(ROOT.relativize(file).toString()
                    .replace('\\', '/')));
        }
        assertThat(new TreeSet<>(ElytraPack.FILES)).isEqualTo(onDisk);
        assertThat(ElytraPack.FILES).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("every trim pattern has a wing texture, tinted through the game's trim palette")
    void everyPatternOnTheWings() throws IOException {
        for (String pattern : PATTERNS) {
            String texture = "assets/minecraft/textures/trims/entity/wings/" + pattern + ".png";
            assertThat(ROOT.resolve(texture)).as(texture).isRegularFile();
            assertThat(json(texture + ".mcmeta").getAsJsonObject("palette").get("base_palette").getAsString())
                    .as("without the palette the wings are drawn in the template's grey").isEqualTo("trim_base");
        }
    }

    @Test
    @DisplayName("the item shows its trim, broken or not, for every material, and every model it names exists")
    void iconShowsTheTrim() throws IOException {
        JsonObject model = json("assets/minecraft/items/elytra.json").getAsJsonObject("model");
        assertThat(model.get("property").getAsString()).isEqualTo("minecraft:broken");
        for (String branch : List.of("on_false", "on_true")) {
            JsonObject select = model.getAsJsonObject(branch);
            assertThat(select.get("property").getAsString()).isEqualTo("minecraft:trim_material");
            List<String> covered = new ArrayList<>();
            for (JsonElement each : select.getAsJsonArray("cases")) {
                JsonObject trimCase = each.getAsJsonObject();
                covered.add(trimCase.get("when").getAsString().replace("minecraft:", ""));
                String named = trimCase.getAsJsonObject("model").get("model").getAsString();
                assertThat(ROOT.resolve("assets/rainselytra/models/" + named.replace("rainselytra:", "") + ".json"))
                        .as(named).isRegularFile();
            }
            assertThat(covered).containsExactlyInAnyOrderElementsOf(MATERIALS);
        }
        JsonArray atlasTextures = json("assets/minecraft/atlases/items.json").getAsJsonArray("sources")
                .get(0).getAsJsonObject().getAsJsonArray("textures");
        assertThat(atlasTextures.toString()).contains("minecraft:trims/items/wings_trim");
    }

    @Test
    @DisplayName("every look is an equipment asset drawing the chestplate and the wings")
    void everyLookIsAnAsset() throws IOException {
        for (ElytraLook look : ElytraLook.values()) {
            JsonObject layers = json("assets/" + look.asset().namespace() + "/equipment/" + look.asset().value()
                    + ".json").getAsJsonObject("layers");
            assertThat(layers.has("humanoid")).as(look + " draws a chestplate").isTrue();
            assertThat(layers.getAsJsonArray("wings").get(0).getAsJsonObject().get("texture").getAsString())
                    .as(look + " still draws the wings").isEqualTo("minecraft:elytra");
            assertThat(layers.has("humanoid_leggings")).as("a chestplate, not trousers").isFalse();
        }
    }

    @Test
    @DisplayName("the pack is for 26.3 and says whose textures it carries")
    void packMeta() throws IOException {
        JsonObject pack = json("pack.mcmeta").getAsJsonObject("pack");
        assertThat(pack.get("max_format").getAsInt()).isEqualTo(97);
        assertThat(pack.has("supported_formats")).as("rejected from format 82 on").isFalse();
        assertThat(Files.readString(ROOT.resolve("CREDITS.txt"))).contains("rando3214").contains("BSD");
    }

    @Test
    @DisplayName("every wing trim is shaded, not one flat colour, and only in the game's trim greys so the palette tints it")
    void wingsHaveDepth() throws IOException {
        java.util.Set<Integer> greys = java.util.Set.of(0xE0E0E0, 0xC0C0C0, 0xA0A0A0, 0x808080, 0x606060, 0x404040,
                0x202020, 0x000000);
        for (String pattern : PATTERNS) {
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(
                    ROOT.resolve("assets/minecraft/textures/trims/entity/wings/" + pattern + ".png").toFile());
            java.util.Set<Integer> shades = new java.util.TreeSet<>();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int argb = image.getRGB(x, y);
                    if ((argb >>> 24) != 0) {
                        shades.add(argb & 0xFFFFFF);
                    }
                }
            }
            assertThat(shades).as(pattern + "'s greys").isSubsetOf(greys);
            assertThat(shades.size()).as(pattern + " has depth").isGreaterThanOrEqualTo(3);
        }
    }

    @Test
    @DisplayName("the LGPL wing textures travel with their licence and the GPL it builds on")
    void licencesTravelAlong() {
        assertThat(ROOT.resolve("licenses/elytra-trims-LGPL-3.0.txt")).content().contains("LESSER GENERAL PUBLIC LICENSE");
        assertThat(ROOT.resolve("licenses/GPL-3.0.txt")).content().contains("GNU GENERAL PUBLIC LICENSE");
        assertThat(ROOT.resolve("CREDITS.txt")).content().contains("Elytra Trims").contains("KikuGie");
    }
}
