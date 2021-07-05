package net.trollyloki.lokidumbcommands.gun;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class GunRunnable extends BukkitRunnable {

    private final Plugin plugin;
    private final Entity shooter, target;

    private long startTime = 0;
    private Location location = null;
    private double speed = 0;

    /**
     * Constructs a new gun runnable.
     *
     * @param plugin Plugin
     * @param shooter Shooter
     * @param target Target
     */
    public GunRunnable(Plugin plugin, Entity shooter, Entity target) {
        this.plugin = plugin;
        this.shooter = shooter;
        this.target = target;
    }

    /**
     * Fires at the target.
     *
     * @param speed Speed in blocks per tick
     */
    public void fire(double speed) {
        if (location != null)
            throw new IllegalStateException("Already fired");
        this.startTime = System.currentTimeMillis();
        this.location = shooter.getBoundingBox().getCenter().toLocation(shooter.getWorld());
        this.speed = speed;

        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 2f);
        runTaskTimer(plugin, 0, 1);
    }

    /**
     * Do not call externally.
     */
    @Override
    public void run() {

        if (System.currentTimeMillis() - startTime > 60000) {
            cancel();
            plugin.getLogger().warning("Gun runnable timed out");
        }

        if (!target.isValid()) {
            cancel();
            shooter.sendMessage(ChatColor.RED + "Target lost");
        }

        Vector direction = target.getBoundingBox().getCenter().subtract(location.toVector()).normalize();
        RayTraceResult result = location.getWorld().rayTraceEntities(location, direction, speed,0.1);
        if (result != null && target.equals(result.getHitEntity())) {

            cancel();

            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0.5f);
            if (target instanceof LivingEntity)
                ((LivingEntity) target).setHealth(0);
            else
                target.remove();

            String shooterName = shooter instanceof Player ? ((Player) shooter).getDisplayName() : shooter.getName();
            String targetName = target instanceof Player ? ((Player) target).getDisplayName() : target.getName();
            Bukkit.broadcastMessage(shooterName + " shot " + targetName);

        } else {
            location.add(direction.multiply(speed));
            location.getWorld().spawnParticle(Particle.CRIT, location, 1, 0, 0, 0,
                    0, null, true);
        }

    }

}
