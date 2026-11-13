package de.raindancer.modules.elytra.util;

import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/** Registered in code, since a hosted module has no descriptor. Trims need none: they are the smithing table's. */
public final class PermissionNodes {

    /** Putting a chestplate look on one's elytra. Its default and parents follow the prime-only setting (PrimeGate). */
    public static final String LOOKS = "rainselytra.looks";

    private PermissionNodes() {
    }

    public static List<Permission> declared() {
        return List.of(new Permission(LOOKS, "Put a chestplate look on your elytra with /elytra",
                PermissionDefault.TRUE));
    }

    /** Registers whatever is not registered yet. @return how many were added */
    public static int register(Server server) {
        int added = 0;
        for (Permission permission : declared()) {
            if (server.getPluginManager().getPermission(permission.getName()) != null) {
                continue;
            }
            try {
                server.getPluginManager().addPermission(permission);
                added++;
            } catch (IllegalArgumentException alreadyThere) {
                // Registered by another copy between the check and the add.
            }
        }
        return added;
    }
}
