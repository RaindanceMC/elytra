package de.raindancer.modules.elytra.look;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * A chestplate drawn together with the wings. Each is an equipment asset in the pack (the vanilla chestplate's
 * layers plus the elytra's wings) that the elytra's {@code equippable} points at: only the looks change, never
 * the armour points.
 */
public enum ElytraLook {

    LEATHER("Leather", Material.LEATHER_CHESTPLATE),
    CHAINMAIL("Chainmail", Material.CHAINMAIL_CHESTPLATE),
    COPPER("Copper", Material.COPPER_CHESTPLATE),
    IRON("Iron", Material.IRON_CHESTPLATE),
    GOLD("Gold", Material.GOLDEN_CHESTPLATE),
    DIAMOND("Diamond", Material.DIAMOND_CHESTPLATE),
    NETHERITE("Netherite", Material.NETHERITE_CHESTPLATE);

    public static final String NAMESPACE = "rainselytra";
    /** The game's own elytra asset — what a plain elytra points at. */
    public static final Key PLAIN = Key.key("minecraft", "elytra");

    private final String title;
    private final Material icon;
    private final Key asset;

    ElytraLook(String title, Material icon) {
        this.title = title;
        this.icon = icon;
        this.asset = Key.key(NAMESPACE, id() + "_chestplate");
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

    public static Optional<ElytraLook> ofAsset(Key asset) {
        return asset == null ? Optional.empty()
                : Arrays.stream(values()).filter(look -> look.asset.equals(asset)).findFirst();
    }

    public static Optional<ElytraLook> parse(String typed) {
        return typed == null ? Optional.empty()
                : Arrays.stream(values()).filter(look -> look.id().equalsIgnoreCase(typed.trim())).findFirst();
    }

    /** What a player types for the plain wings again. */
    public static boolean isPlainWord(String typed) {
        return typed != null && switch (typed.trim().toLowerCase(Locale.ROOT)) {
            case "none", "off", "plain", "reset" -> true;
            default -> false;
        };
    }
}
