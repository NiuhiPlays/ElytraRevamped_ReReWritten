package com.niuhi.features.elytra;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.PlayerInput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ElytraBounce {
    private static final Map<UUID, Integer> LAST_HOP_TICK = new HashMap<>();
    private static final int HOP_COOLDOWN_TICKS = 4;
    private static final double HOP_BOOST = 0.35;
    private static final double MAX_HOP_Y = 1.0;

    private ElytraBounce() {
    }

    public static void tryBounce(ServerPlayerEntity player, ModConfig config, int serverTick) {
        if (!config.elytraConfig.enableBounce) {
            return;
        }

        PlayerInput input = player.getPlayerInput();
        if (input == null || !input.jump()) {
            return;
        }
        if (!player.isOnGround()) {
            return;
        }

        UUID uuid = player.getUuid();
        Integer lastHop = LAST_HOP_TICK.get(uuid);
        if (lastHop != null && serverTick - lastHop < HOP_COOLDOWN_TICKS) {
            return;
        }

        Vec3d velocity = player.getVelocity();
        double targetY = MathHelper.clamp(Math.max(velocity.y, 0.0) + HOP_BOOST, 0.0, MAX_HOP_Y);
        player.setVelocity(velocity.x, targetY, velocity.z);
        player.knockedBack = true;

        LAST_HOP_TICK.put(uuid, serverTick);

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("ELYTRA", "Applied bunny hop=" + String.format("%.2f", targetY)
                + " player=" + player.getName().getString());
    }
}
