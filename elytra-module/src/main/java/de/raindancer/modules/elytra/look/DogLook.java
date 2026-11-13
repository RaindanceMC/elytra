package de.raindancer.modules.elytra.look;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * An armour drawn on a wolf wearing dog armour (Prime). Each is an equipment asset in the pack with only a wolf body
 * layer, which the dog armour's {@code equippable} points at: it protects exactly as before.
 */
public enum DogLook {

    LEATHER("Leather", Material.LEATHER),
    COPPER("Copper", Material.COPPER_INGOT),
    IRON("Iron", Material.IRON_INGOT),
    GOLD("Gold", Material.GOLD_INGOT),
    DIAMOND("Diamond", Material.DIAMOND),
    NETHERITE("Netherite", Material.NETHERITE_INGOT),
    GILDED("Gilded netherite", Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);

    /** The game's own dog armour asset — what plain dog armour points at. */
    public static final Key PLAIN = Key.key("minecraft", "armadillo_scute");

    private final String title;
    private final Material icon;
    private final Key asset;

    DogLook(String title, Material icon) {
        this.title = title;
        this.icon = icon;
        this.asset = Key.key(ElytraLook.NAMESPACE, "dog_" + id());
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String title() {
        return title;
    }

    public Material icon() {
        return icon;
    }

    public Key asset() {
        return asset;
    }

    public static Optional<DogLook> ofAsset(Key asset) {
        return asset == null ? Optional.empty()
                : Arrays.stream(values()).filter(look -> look.asset.equals(asset)).findFirst();
    }

    public static Optional<DogLook> parse(String typed) {
        return typed == null ? Optional.empty()
                : Arrays.stream(values()).filter(look -> look.id().equalsIgnoreCase(typed.trim())).findFirst();
    }
}
