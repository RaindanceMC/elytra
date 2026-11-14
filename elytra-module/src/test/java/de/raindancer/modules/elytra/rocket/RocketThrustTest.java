package de.raindancer.modules.elytra.rocket;

import de.raindancer.modules.elytra.ElytraSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RocketThrustTest {

    @Test
    @DisplayName("shipped off: 0 leaves elytra and rockets vanilla")
    void offByDefault() {
        assertThat(ElytraSettings.DEFAULTS.maxSpeed()).isZero();
        assertThat(RocketThrust.gravityFactor(0)).isEqualTo(1.0);
    }

    @Test
    @DisplayName("gliding: gravity scaled so the steepest dive tops out at the limit (vanilla's is 78.4 b/s)")
    void gravity() {
        assertThat(RocketThrust.gravityFactor(20)).isCloseTo(20 / 78.4, within(1e-9));
        assertThat(RocketThrust.gravityFactor(200)).as("never more than vanilla").isEqualTo(1.0);
    }

    @Test
    @DisplayName("a rocket burns as long as a vanilla one of its flight power, on average")
    void burnsLikeVanilla() {
        assertThat(RocketThrust.burnTicks(1)).isEqualTo(26);
        assertThat(RocketThrust.burnTicks(2)).isEqualTo(36);
        assertThat(RocketThrust.burnTicks(3)).isEqualTo(46);
        assertThat(RocketThrust.burnTicks(0)).isEqualTo(16);
    }

    @Test
    @DisplayName("its push is the limit, per tick, in the direction looked at")
    void push() {
        double[] v = RocketThrust.push(new double[]{0, -3, 4}, 20);
        assertThat(v[0]).isZero();
        assertThat(v[1]).isCloseTo(-0.6, within(1e-9));
        assertThat(v[2]).isCloseTo(0.8, within(1e-9));
        assertThat(Math.sqrt(v[1] * v[1] + v[2] * v[2])).isCloseTo(1.0, within(1e-9));
    }
}
