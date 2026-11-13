package de.raindancer.modules.elytra.pack;

import de.raindancer.core.RainsCore;
import de.raindancer.core.content.pack.JarPack;
import de.raindancer.core.platform.log.LogChannel;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * The wing trims, the trimmed elytra icons and the chestplate looks, offered to Core's resource pack. Written by
 * {@code art/build-pack.py}; without the pack trims still go on and show in the tooltip, the wings just look plain.
 */
public final class ElytraPack {

    public static final String ROOT = "/de/raindancer/modules/elytra/pack/";
    public static final List<String> FILES = List.of(
            "CREDITS.txt",
            "assets/minecraft/atlases/items.json",
            "assets/minecraft/items/elytra.json",
            "assets/minecraft/textures/trims/entity/horse_body/bolt.png",
            "assets/minecraft/textures/trims/entity/horse_body/bolt.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/coast.png",
            "assets/minecraft/textures/trims/entity/horse_body/coast.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/dune.png",
            "assets/minecraft/textures/trims/entity/horse_body/dune.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/eye.png",
            "assets/minecraft/textures/trims/entity/horse_body/eye.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/flow.png",
            "assets/minecraft/textures/trims/entity/horse_body/flow.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/host.png",
            "assets/minecraft/textures/trims/entity/horse_body/host.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/raiser.png",
            "assets/minecraft/textures/trims/entity/horse_body/raiser.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/rib.png",
            "assets/minecraft/textures/trims/entity/horse_body/rib.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/sentry.png",
            "assets/minecraft/textures/trims/entity/horse_body/sentry.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/shaper.png",
            "assets/minecraft/textures/trims/entity/horse_body/shaper.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/silence.png",
            "assets/minecraft/textures/trims/entity/horse_body/silence.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/snout.png",
            "assets/minecraft/textures/trims/entity/horse_body/snout.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/spire.png",
            "assets/minecraft/textures/trims/entity/horse_body/spire.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/tide.png",
            "assets/minecraft/textures/trims/entity/horse_body/tide.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/vex.png",
            "assets/minecraft/textures/trims/entity/horse_body/vex.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/ward.png",
            "assets/minecraft/textures/trims/entity/horse_body/ward.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/wayfinder.png",
            "assets/minecraft/textures/trims/entity/horse_body/wayfinder.png.mcmeta",
            "assets/minecraft/textures/trims/entity/horse_body/wild.png",
            "assets/minecraft/textures/trims/entity/horse_body/wild.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/bolt.png",
            "assets/minecraft/textures/trims/entity/wings/bolt.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/coast.png",
            "assets/minecraft/textures/trims/entity/wings/coast.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/dune.png",
            "assets/minecraft/textures/trims/entity/wings/dune.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/eye.png",
            "assets/minecraft/textures/trims/entity/wings/eye.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/flow.png",
            "assets/minecraft/textures/trims/entity/wings/flow.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/host.png",
            "assets/minecraft/textures/trims/entity/wings/host.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/raiser.png",
            "assets/minecraft/textures/trims/entity/wings/raiser.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/rib.png",
            "assets/minecraft/textures/trims/entity/wings/rib.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/sentry.png",
            "assets/minecraft/textures/trims/entity/wings/sentry.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/shaper.png",
            "assets/minecraft/textures/trims/entity/wings/shaper.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/silence.png",
            "assets/minecraft/textures/trims/entity/wings/silence.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/snout.png",
            "assets/minecraft/textures/trims/entity/wings/snout.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/spire.png",
            "assets/minecraft/textures/trims/entity/wings/spire.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/tide.png",
            "assets/minecraft/textures/trims/entity/wings/tide.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/vex.png",
            "assets/minecraft/textures/trims/entity/wings/vex.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/ward.png",
            "assets/minecraft/textures/trims/entity/wings/ward.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/wayfinder.png",
            "assets/minecraft/textures/trims/entity/wings/wayfinder.png.mcmeta",
            "assets/minecraft/textures/trims/entity/wings/wild.png",
            "assets/minecraft/textures/trims/entity/wings/wild.png.mcmeta",
            "assets/minecraft/textures/trims/items/wings_trim.png",
            "assets/rainselytra/equipment/chainmail_chestplate.json",
            "assets/rainselytra/equipment/copper_chestplate.json",
            "assets/rainselytra/equipment/diamond_chestplate.json",
            "assets/rainselytra/equipment/dog_copper.json",
            "assets/rainselytra/equipment/dog_diamond.json",
            "assets/rainselytra/equipment/dog_gilded.json",
            "assets/rainselytra/equipment/dog_gold.json",
            "assets/rainselytra/equipment/dog_iron.json",
            "assets/rainselytra/equipment/dog_leather.json",
            "assets/rainselytra/equipment/dog_netherite.json",
            "assets/rainselytra/equipment/gold_chestplate.json",
            "assets/rainselytra/equipment/iron_chestplate.json",
            "assets/rainselytra/equipment/leather_chestplate.json",
            "assets/rainselytra/equipment/netherite_chestplate.json",
            "assets/rainselytra/models/item/elytra_amethyst_trim.json",
            "assets/rainselytra/models/item/elytra_broken_amethyst_trim.json",
            "assets/rainselytra/models/item/elytra_broken_copper_trim.json",
            "assets/rainselytra/models/item/elytra_broken_diamond_trim.json",
            "assets/rainselytra/models/item/elytra_broken_emerald_trim.json",
            "assets/rainselytra/models/item/elytra_broken_gold_trim.json",
            "assets/rainselytra/models/item/elytra_broken_iron_trim.json",
            "assets/rainselytra/models/item/elytra_broken_lapis_trim.json",
            "assets/rainselytra/models/item/elytra_broken_netherite_trim.json",
            "assets/rainselytra/models/item/elytra_broken_quartz_trim.json",
            "assets/rainselytra/models/item/elytra_broken_redstone_trim.json",
            "assets/rainselytra/models/item/elytra_broken_resin_trim.json",
            "assets/rainselytra/models/item/elytra_copper_trim.json",
            "assets/rainselytra/models/item/elytra_diamond_trim.json",
            "assets/rainselytra/models/item/elytra_emerald_trim.json",
            "assets/rainselytra/models/item/elytra_gold_trim.json",
            "assets/rainselytra/models/item/elytra_iron_trim.json",
            "assets/rainselytra/models/item/elytra_lapis_trim.json",
            "assets/rainselytra/models/item/elytra_netherite_trim.json",
            "assets/rainselytra/models/item/elytra_quartz_trim.json",
            "assets/rainselytra/models/item/elytra_redstone_trim.json",
            "assets/rainselytra/models/item/elytra_resin_trim.json",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_copper.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_diamond.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_gilded.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_gilded_overlay.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_gold.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_iron.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_leather.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_leather_overlay.png",
            "assets/rainselytra/textures/entity/equipment/wolf_body/dog_netherite.png",
            "licenses/GPL-3.0.txt",
            "licenses/LGPL-2.1.txt",
            "licenses/elytra-trims-LGPL-3.0.txt",
            "pack.mcmeta");
    private static final String OWNER = "Elytra";
    private static final String NAME = "trims-and-looks";

    private ElytraPack() {
    }

    public static void contribute(Plugin plugin, RainsCore core, LogChannel log) {
        try {
            JarPack.offer(plugin, core.resourcePacks(), ElytraPack.class, ROOT, FILES, OWNER, NAME,
                    "Armour trims on elytra and the chestplate looks");
        } catch (IOException failure) {
            log.warn("The elytra pack could not be written ({}); trims still go on, but the wings look plain "
                    + "and the chestplate looks show as plain elytra.", failure.getMessage());
        }
    }

    public static void withdraw(RainsCore core) {
        core.resourcePacks().withdraw(OWNER.toLowerCase(Locale.ROOT) + ":" + NAME);
    }
}
