package com.niuhi.features.elytra;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ElytraBounce {
    private static final Map<UUID, Integer> LAST_GLIDE_TICK = new HashMap<>();
    private static final Map<UUID, Integer> LAST_BOUNCE_TICK = new HashMap<>();
    private static final int BOUNCE_COOLDOWN_TICKS = 10;
    private static final int GLIDE_WINDOW_TICKS = 2;

    private ElytraBounce() {
    }

    public static void tryBounce(ServerPlayerEntity player, ModConfig config, int serverTick) {
        UUID uuid = player.getUuid();
        boolean isGliding = player.isGliding();
        if (isGliding) {
            LAST_GLIDE_TICK.put(uuid, serverTick);
            return;
        }

        Integer lastGlide = LAST_GLIDE_TICK.get(uuid);
        if (lastGlide == null || serverTick - lastGlide > GLIDE_WINDOW_TICKS) {
            return;
        }

        if (!config.elytraConfig.enableBounce || !player.isOnGround()) {
            return;
        }

        Integer lastBounce = LAST_BOUNCE_TICK.get(uuid);
        if (lastBounce != null && serverTick - lastBounce < BOUNCE_COOLDOWN_TICKS) {
            return;
        }

        Vec3d velocity = player.getVelocity();
        double bounceY = MathHelper.clamp(Math.abs(velocity.y) * 0.8 + 0.25, 0.35, 1.2);
        player.setVelocity(velocity.x * 0.9, bounceY, velocity.z * 0.9);
        player.velocityModified = true;

        LAST_BOUNCE_TICK.put(uuid, serverTick);
        LAST_GLIDE_TICK.remove(uuid);

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("ELYTRA", "Applied bounce=" + String.format("%.2f", bounceY)
                + " player=" + player.getName().getString());
    }
}
