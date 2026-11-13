package de.raindancer.modules.elytra.speed;

import java.util.Optional;

/** The arithmetic of the glide limit, apart from Bukkit: one move's distance against blocks per second. */
public final class SpeedCap {

    /** Further than this in one move is a teleport or a world change, not speed. */
    private static final double TELEPORT = 10.0;

    private SpeedCap() {
    }

    /**
     * The velocity to set, in blocks per tick, when a move of {@code dx/dy/dz} in one tick is faster than {@code maxBps};
     * empty when it is not, when there is no limit, or when it was a teleport.
     */
    public static Optional<double[]> clamp(double dx, double dy, double dz, int maxBps) {
        if (maxBps <= 0) {
            return Optional.empty();
        }
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double most = maxBps / 20.0;
        if (length <= most + 1e-9 || length > TELEPORT) {
            return Optional.empty();
        }
        double scale = most / length;
        return Optional.of(new double[]{dx * scale, dy * scale, dz * scale});
    }
}
