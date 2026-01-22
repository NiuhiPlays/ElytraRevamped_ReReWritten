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
        sendVisualEvent(player, type, position, null);
    }

    public static void sendVisualEvent(ServerPlayerEntity player, VisualEventType type, Vec3d position, Integer color) {
        if (!ServerPlayNetworking.canSend(player, VisualEventPayload.ID)) {
            return;
        }
        int packedColor = color != null ? color : 0;
        VisualEventPayload payload = new VisualEventPayload(
                type.getId(),
                position.x,
                position.y,
                position.z,
                color != null,
                packedColor
        );
        ServerPlayNetworking.send(player, payload);

        ModConfig config = ModConfig.getInstance();
        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        logger.log("VISUALS", "Sent visual event=" + type.name()
                + " player=" + player.getName().getString()
                + " pos=" + position.x + "," + position.y + "," + position.z
                + (color != null ? " color=" + color : ""));
    }
}
