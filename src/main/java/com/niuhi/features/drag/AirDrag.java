package com.niuhi.features.drag;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AirDrag {
    private static final Map<UUID, Integer> LAST_DRAG_TICK = new HashMap<>();
    private static final int EMIT_INTERVAL_TICKS = 12;

    private AirDrag() {
    }

    public static void tryApply(ServerPlayerEntity player, ModConfig config, int serverTick) {
        if (!config.dragConfig.enableAirDrag) {
            return;
        }

        double dragAmount = MathHelper.clamp(config.dragConfig.airDragAmount, 0.0, 1.0);
        double minY = player.getEntityWorld().getBottomY();
        double maxY = minY + player.getEntityWorld().getDimension().height();
        double heightFactor = (player.getY() - minY) / Math.max(1.0, (maxY - minY));
        heightFactor = MathHelper.clamp(heightFactor, 0.0, 1.0);

        double scaledFactor = Math.pow(heightFactor, 2.0);
        double appliedDrag = 1.0 - scaledFactor * (1.0 - dragAmount);
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x * appliedDrag, velocity.y * appliedDrag, velocity.z * appliedDrag);
        player.velocityModified = true;

        Integer lastTick = LAST_DRAG_TICK.get(player.getUuid());
        if (lastTick == null || serverTick - lastTick >= EMIT_INTERVAL_TICKS) {
            LAST_DRAG_TICK.put(player.getUuid(), serverTick);
        }

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("DRAG", "Applied air drag=" + String.format("%.3f", appliedDrag)
                + " heightFactor=" + String.format("%.2f", heightFactor)
                + " player=" + player.getName().getString());
    }
}
