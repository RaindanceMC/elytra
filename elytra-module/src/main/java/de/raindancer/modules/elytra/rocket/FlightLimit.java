package de.raindancer.modules.elytra.rocket;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import de.raindancer.core.platform.util.Scheduling;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntSupplier;

/**
 * Keeps elytra under {@code elytra:max-speed} without ever correcting a player mid-flight: while somebody glides their
 * gravity is scaled down (their own client flies with it, so nothing jerks), and a rocket, which would push past any
 * limit, is the server's: an even push at the limit along the look, for as long as it burns. Off (0), all vanilla.
 */
public final class FlightLimit implements Listener {

    static final NamespacedKey KEY = new NamespacedKey("rainselytra", "glide_gravity");

    private final Plugin plugin;
    private final IntSupplier maxSpeed;
    private final Map<UUID, ScheduledTask> burning = new ConcurrentHashMap<>();

    public FlightLimit(Plugin plugin, IntSupplier maxSpeed) {
        this.plugin = plugin;
        this.maxSpeed = maxSpeed;
    }

    /** Every two seconds: gravity right for everybody — the setting may have changed, a glide ended unnoticed. */
    public ScheduledTask start() {
        return Scheduling.globalTimer(plugin, 40, 40, task -> {
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                Scheduling.entity(plugin, online, () -> gravity(online, online.isGliding()));
            }
        });
    }

    public void stop() {
        burning.values().forEach(ScheduledTask::cancel);
        burning.clear();
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            Scheduling.entity(plugin, online, () -> gravity(online, false));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player player) {
            gravity(player, event.isGliding());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        gravity(event.getPlayer(), false);
        ScheduledTask task = burning.remove(event.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRocket(PlayerElytraBoostEvent event) {
        int most = maxSpeed.getAsInt();
        if (most <= 0) {
            return;
        }
        event.setCancelled(true);   // the game's own rocket would push far past the limit
        Player player = event.getPlayer();
        ItemStack rocket = event.getItemStack();
        int power = rocket.getItemMeta() instanceof FireworkMeta meta ? meta.getPower() : 1;
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack held = player.getInventory().getItem(event.getHand());
            if (held != null && held.isSimilar(rocket)) {
                held.setAmount(held.getAmount() - 1);
            }
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3f, 1f);
        ScheduledTask before = burning.remove(player.getUniqueId());
        if (before != null) {
            before.cancel();
        }
        int[] left = {RocketThrust.burnTicks(power)};
        burning.put(player.getUniqueId(), Scheduling.entityTimer(plugin, player, 1, 1, task -> {
            if (left[0]-- <= 0 || !player.isGliding()) {
                task.cancel();
                burning.remove(player.getUniqueId(), task);
                return;
            }
            Vector look = player.getLocation().getDirection();
            double[] v = RocketThrust.push(new double[]{look.getX(), look.getY(), look.getZ()}, maxSpeed.getAsInt());
            player.setVelocity(new Vector(v[0], v[1], v[2]));
            player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation().subtract(look), 1, 0, 0, 0, 0);
        }, () -> burning.remove(player.getUniqueId())));
    }

    private void gravity(Player player, boolean gliding) {
        AttributeInstance gravity = player.getAttribute(Attribute.GRAVITY);
        if (gravity == null) {
            return;
        }
        double factor = gliding ? RocketThrust.gravityFactor(maxSpeed.getAsInt()) : 1.0;
        double amount = factor - 1.0;
        AttributeModifier current = gravity.getModifier(KEY);
        if (current != null && Math.abs(current.getAmount() - amount) < 1e-9) {
            return;
        }
        if (current != null) {
            gravity.removeModifier(KEY);
        }
        if (Math.abs(amount) > 1e-9) {
            gravity.addTransientModifier(new AttributeModifier(KEY, amount, AttributeModifier.Operation.ADD_SCALAR));
        }
    }
}
