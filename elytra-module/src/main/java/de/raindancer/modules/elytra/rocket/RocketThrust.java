package de.raindancer.modules.elytra.rocket;

/**
 * The arithmetic of the elytra speed limit, apart from Bukkit.
 *
 * <p>Every speed an elytra reaches comes from gravity — a dive turns height into speed, and the game's glide physics
 * is linear in it — so scaling gravity while gliding scales the top speed and keeps every glide angle. Rockets push
 * regardless of gravity; theirs is done here instead: the limit, in the direction looked at, while they burn.
 */
public final class RocketThrust {

    /** Vanilla's top speed with elytra: a dive straight down, in blocks a second (the game's glide physics run out). */
    public static final double VANILLA_TOP = 78.4;

    private RocketThrust() {
    }

    /** What to multiply gravity by while gliding so nobody passes {@code maxBps}; 1 for none or no limit. */
    public static double gravityFactor(int maxBps) {
        return maxBps <= 0 ? 1.0 : Math.min(1.0, maxBps / VANILLA_TOP);
    }

    /** How long a rocket of this flight power burns, in ticks: vanilla's lifetime on average. */
    public static int burnTicks(int power) {
        return 10 * (Math.max(0, power) + 1) + 6;
    }

    /** The velocity a burning rocket gives, blocks a tick: {@code maxBps} along the look. */
    public static double[] push(double[] look, int maxBps) {
        double length = Math.sqrt(look[0] * look[0] + look[1] * look[1] + look[2] * look[2]);
        double perTick = maxBps / 20.0;
        if (length == 0) {
            return new double[]{0, 0, 0};
        }
        return new double[]{look[0] / length * perTick, look[1] / length * perTick, look[2] / length * perTick};
    }
}
