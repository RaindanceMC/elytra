package de.raindancer.modules.elytra;

import de.raindancer.core.data.settings.Describe;
import de.raindancer.core.data.settings.In;
import de.raindancer.core.data.settings.Key;
import de.raindancer.core.data.settings.Range;
import de.raindancer.core.data.settings.Settings;
import de.raindancer.core.data.settings.Title;
import de.raindancer.core.data.settings.Topic;
import org.bukkit.Material;

/** What an owner decides about the elytra: the looks and how fast one may go. Trims are the smithing table's and not gated. */
@Settings(id = "elytra", topics = {
        @Topic(path = "elytra", title = "Elytra", icon = Material.ELYTRA),
})
public record ElytraSettings(

        @In("elytra") @Title("Chestplate looks for Prime only")
        @Describe("Whether the chestplate looks are a Prime perk. Prime members, staff and whoever has free "
                + "cosmetics may put one on; everybody may still look at them and take one off. Off, they are "
                + "everybody's (rainselytra.looks).")
        @Key("prime-only")
        boolean primeOnly,

        @In("elytra") @Title("Fastest flight (blocks per second)") @Range(min = 0, max = 200)
        @Describe("Nobody flies an elytra faster than this, diving and rockets included. Smooth: while gliding gravity is "
                + "scaled so the steepest dive tops out here (every glide angle stays the same, just slower), and a rocket "
                + "pushes at this speed along the look while it burns. Vanilla tops out at about 78 diving, 33 with rockets. "
                + "0: vanilla.")
        @Key("max-speed")
        int maxSpeed) {

    public static final ElytraSettings DEFAULTS = new ElytraSettings(true, 0);
}
