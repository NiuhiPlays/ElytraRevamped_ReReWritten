package com.niuhi.features.drag;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import com.niuhi.network.ModNetworking;
import com.niuhi.network.VisualEventType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ControllableDrag {
    private static final Map<UUID, Integer> LAST_DRAG_TICK = new HashMap<>();

    private ControllableDrag() {
    }

    public static void tryApply(ServerPlayerEntity player, ModConfig config, int serverTick) {
        if (!config.dragConfig.enableControllableDrag) {
            return;
        }
        if (!player.isSneaking()) {
            return;
        }

        double dragAmount = Math.max(0.0, Math.min(1.0, config.dragConfig.dragAmount));
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x * dragAmount, velocity.y * dragAmount, velocity.z * dragAmount);
        player.velocityModified = true;

        Integer lastTick = LAST_DRAG_TICK.get(player.getUuid());
        if (lastTick == null || serverTick - lastTick >= 5) {
            ModNetworking.sendVisualEvent(player, VisualEventType.DRAG, player.getPos());
            LAST_DRAG_TICK.put(player.getUuid(), serverTick);
        }

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("DRAG", "Applied controllable drag=" + dragAmount
                + " player=" + player.getName().getString());
    }
}
