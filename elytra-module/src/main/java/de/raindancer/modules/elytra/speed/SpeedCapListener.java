package de.raindancer.modules.elytra.speed;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.function.IntSupplier;

/**
 * Slows a gliding player to the limit. A player's movement is the client's, so the server measures the move it is told
 * about and sends a velocity in the same direction at the limit; the client takes it like any knockback.
 */
public final class SpeedCapListener implements Listener {

    private final IntSupplier maxSpeed;

    public SpeedCapListener(IntSupplier maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        int most = maxSpeed.getAsInt();
        if (most <= 0 || !event.getPlayer().isGliding()) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getWorld() != to.getWorld()) {
            return;
        }
        SpeedCap.clamp(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ(), most)
                .ifPresent(v -> event.getPlayer().setVelocity(new Vector(v[0], v[1], v[2])));
    }
}
