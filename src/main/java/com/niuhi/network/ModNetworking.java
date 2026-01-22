package com.niuhi.network;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class ModNetworking {
    private static boolean payloadsRegistered = false;
    private ModNetworking() {
    }

    public static void registerPayloads() {
        if (payloadsRegistered) {
            return;
        }
        PayloadTypeRegistry.playS2C().register(VisualEventPayload.ID, VisualEventPayload.CODEC);
        payloadsRegistered = true;
    }

    public static void sendVisualEvent(ServerPlayerEntity player, VisualEventType type, Vec3d position) {
        sendVisualEvent(player, type, position, (int[]) null);
    }

    public static void sendVisualEvent(ServerPlayerEntity player, VisualEventType type, Vec3d position, Integer color) {
        int[] colors = color != null ? new int[] { color } : null;
        sendVisualEvent(player, type, position, colors);
    }

    public static void sendVisualEvent(ServerPlayerEntity player, VisualEventType type, Vec3d position, int[] colors) {
        if (!canSendVisuals(player)) {
            return;
        }
        int[] payloadColors = colors != null ? colors : new int[0];
        VisualEventPayload payload = new VisualEventPayload(
                type.getId(),
                position.x,
                position.y,
                position.z,
                payloadColors.length > 0,
                payloadColors
        );
        ServerPlayNetworking.send(player, payload);

        ModConfig config = ModConfig.getInstance();
        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("VISUALS", "Sent visual event=" + type.name()
                + " player=" + player.getName().getString()
                + " pos=" + position.x + "," + position.y + "," + position.z
                + (colors != null && colors.length > 0 ? " colors=" + colors.length : ""));
    }

    public static boolean canSendVisuals(ServerPlayerEntity player) {
        return ServerPlayNetworking.canSend(player, VisualEventPayload.ID);
    }
}
