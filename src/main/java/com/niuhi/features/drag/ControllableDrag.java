package com.niuhi.features.drag;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import com.niuhi.visuals.ServerVisuals;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ControllableDrag {
    private static final Map<UUID, Integer> LAST_DRAG_TICK = new HashMap<>();

    private ControllableDrag() {
    }

    public static void tryApply(ServerPlayer player, ModConfig config, int serverTick) {
        if (!config.dragConfig.enableControllableDrag) {
            return;
        }
        if (!player.isShiftKeyDown()) {
            return;
        }

        double dragAmount = Math.max(0.0, Math.min(1.0, config.dragConfig.dragAmount));
        Vec3 velocity = player.getDeltaMovement();
        player.setDeltaMovement(velocity.x * dragAmount, velocity.y * dragAmount, velocity.z * dragAmount);
        player.hurtMarked = true;

        Integer lastTick = LAST_DRAG_TICK.get(player.getUUID());
        if (lastTick == null || serverTick - lastTick >= 5) {
            ServerVisuals.broadcastDrag(player);
            LAST_DRAG_TICK.put(player.getUUID(), serverTick);
        }

        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("DRAG", "Applied controllable drag=" + dragAmount
                + " player=" + player.getName().getString());
    }
}
