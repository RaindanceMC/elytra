package de.raindancer.modules.elytra.prime;

import de.raindancer.modules.elytra.util.PermissionNodes;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
 * Prime-only looks: {@code rainselytra.looks} is nobody's by default and a child of Core's {@code rains.prime}, of
 * staff's {@code rainscosmetics.perks} and of {@code rainscosmetics.free} (creators, admins) — the same way the
 * cosmetics plugin gates its Prime perks. Off, the looks are everybody's.
 *
 * <p>The parents are found by name, or made with the default their owner gives them (Core's {@code PrimePermission}
 * and the cosmetics plugin both take an existing node as it is), so neither needs to be installed first or at all.
 */
public final class PrimeGate {

    public static final String PRIME = "rains.prime";
    public static final String COSMETICS_FREE = "rainscosmetics.free";
    public static final String COSMETICS_PERKS = "rainscosmetics.perks";
    public static final List<String> PARENTS = List.of(PRIME, COSMETICS_FREE, COSMETICS_PERKS);

    private PrimeGate() {
    }

    /** Idempotent; on the global thread. */
    public static void apply(PluginManager manager, boolean primeOnly) {
        Permission looks = manager.getPermission(PermissionNodes.LOOKS);
        if (looks != null) {
            looks.setDefault(primeOnly ? PermissionDefault.FALSE : PermissionDefault.TRUE);
            recalculate(manager, looks);
        }
        for (String name : PARENTS) {
            Permission parent = parent(manager, name);
            if (primeOnly) {
                parent.getChildren().put(PermissionNodes.LOOKS, true);
            } else {
                parent.getChildren().remove(PermissionNodes.LOOKS);
            }
            recalculate(manager, parent);
        }
    }

    private static Permission parent(PluginManager manager, String name) {
        Permission found = manager.getPermission(name);
        if (found != null) {
            return found;
        }
        Permission made = switch (name) {
            case PRIME -> new Permission(PRIME, "Holds the Prime subscription — given and taken by RainsCore",
                    PermissionDefault.FALSE);
            case COSMETICS_FREE -> new Permission(COSMETICS_FREE,
                    "Use every cosmetic that is for sale without buying it", PermissionDefault.OP);
            default -> new Permission(COSMETICS_PERKS, "Prime-only cosmetics without Prime — staff",
                    PermissionDefault.OP);
        };
        try {
            manager.addPermission(made);
            return made;
        } catch (IllegalArgumentException registeredMeanwhile) {
            return manager.getPermission(name);
        }
    }

    /** {@code Permission.recalculatePermissibles}, through the manager given rather than {@code Bukkit}'s. */
    private static void recalculate(PluginManager manager, Permission permission) {
        manager.recalculatePermissionDefaults(permission);
        for (Permissible holder : manager.getPermissionSubscriptions(permission.getName())) {
            holder.recalculatePermissions();
        }
    }
}
