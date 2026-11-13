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
        @Describe("Whether the chestplate looks are a Prime perk — Prime members, staff and whoever has free "
                + "cosmetics may put one on; everybody may still look at them and take one off. Off, they are "
                + "everybody's (rainselytra.looks).")
        @Key("prime-only")
        boolean primeOnly,

        @In("elytra") @Title("Fastest glide (blocks per second)") @Range(min = 0, max = 200)
        @Describe("Nobody glides faster than this — rockets, diving and boosts included; anything faster is slowed to it "
                + "in the same direction. Vanilla tops out around 33 with rockets. 0: no limit.")
        @Key("max-speed")
        int maxSpeed) {

    public static final ElytraSettings DEFAULTS = new ElytraSettings(true, 0);
}
