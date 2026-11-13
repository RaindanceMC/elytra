package de.raindancer.modules.elytra.speed;

import de.raindancer.modules.elytra.ElytraSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SpeedCapTest {

    @Test
    @DisplayName("off by default: 0 means no limit")
    void offByDefault() {
        assertThat(ElytraSettings.DEFAULTS.maxSpeed()).isZero();
        assertThat(SpeedCap.clamp(5, 0, 5, 0)).isEmpty();
    }

    @Test
    @DisplayName("under the limit nothing changes")
    void underTheLimit() {
        // 0.7 blocks a tick = 14 blocks a second
        assertThat(SpeedCap.clamp(0.7, 0, 0, 15)).isEmpty();
        assertThat(SpeedCap.clamp(0.75, 0, 0, 15)).isEmpty();
    }

    @Test
    @DisplayName("over it, the same direction at exactly the limit, per tick")
    void clampsToTheLimit() {
        // a rocket: ~1.65 blocks a tick, forwards and a little up
        double[] slowed = SpeedCap.clamp(1.6, 0.4, 0, 15).orElseThrow();
        double length = Math.sqrt(slowed[0] * slowed[0] + slowed[1] * slowed[1] + slowed[2] * slowed[2]);
        assertThat(length).isCloseTo(0.75, within(1e-9));
        assertThat(slowed[0] / slowed[1]).isCloseTo(4.0, within(1e-9));
        assertThat(slowed[2]).isZero();
    }

    @Test
    @DisplayName("diving counts too: the limit is on the whole speed, not only across the ground")
    void divingCounts() {
        assertThat(SpeedCap.clamp(0, -2, 0, 15)).hasValueSatisfying(v -> assertThat(v[1]).isCloseTo(-0.75, within(1e-9)));
    }

    @Test
    @DisplayName("a jump of many blocks in one move is a teleport, not flying, and is left alone")
    void teleportsAreNotSpeed() {
        assertThat(SpeedCap.clamp(40, 0, 0, 15)).isEmpty();
    }
}
