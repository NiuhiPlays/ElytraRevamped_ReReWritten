package com.niuhi.features.elytra;

import com.niuhi.compat.Accessories;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PlayerInput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ElytraBounce {
    private static final boolean ACCESSORIES_LOADED = FabricLoader.getInstance().isModLoaded("accessories");
    private static final Map<UUID, Integer> TICKS_ON_GROUND = new HashMap<>();
    private static final int MAX_GROUND_TICKS = 5;

    private ElytraBounce() {
    }

    public static boolean shouldKeepGliding(LivingEntity entity, boolean originalValue) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return originalValue;
        }

        UUID uuid = player.getUuid();

        // Check conditions: on ground, was flying, holding jump, wearing elytra
        if (!entity.isOnGround()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return originalValue;
        }

        // Check if currently gliding (before this call tries to disable it)
        if (!player.isGliding()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return originalValue;
        }

        // Check if jump is held
        PlayerInput input = player.getPlayerInput();
        if (input == null || !input.jump()) {
            return originalValue;
        }

        // Check if wearing elytra
        if (!isWearingElytra(player)) {
            return originalValue;
        }

        // Increment ground ticks
        Integer groundTicks = TICKS_ON_GROUND.getOrDefault(uuid, 0);
        groundTicks++;
        TICKS_ON_GROUND.put(uuid, groundTicks);

        // Keep gliding for up to MAX_GROUND_TICKS
        if (groundTicks <= MAX_GROUND_TICKS) {
            return true; // Force gliding to stay active
        }

        return originalValue;
    }

    public static void updateBounceState(LivingEntity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        UUID uuid = player.getUuid();

        // Check conditions
        if (!entity.isOnGround()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return;
        }

        // Check if jump is held
        PlayerInput input = player.getPlayerInput();
        if (input == null || !input.jump()) {
            TICKS_ON_GROUND.put(uuid, 0);
            return;
        }

        // Check if wearing elytra
        if (!isWearingElytra(player)) {
            TICKS_ON_GROUND.put(uuid, 0);
            return;
        }

        // Increment ground ticks
        Integer groundTicks = TICKS_ON_GROUND.getOrDefault(uuid, 0);
        groundTicks++;
        TICKS_ON_GROUND.put(uuid, groundTicks);

        // Re-enable gliding flag if still within window
        if (groundTicks <= MAX_GROUND_TICKS && !player.isGliding()) {
            try {
                java.lang.reflect.Method setFlag = net.minecraft.entity.Entity.class.getDeclaredMethod("setFlag", int.class, boolean.class);
                setFlag.setAccessible(true);
                setFlag.invoke(player, 7, true);
            } catch (Exception e) {
                // Fallback: this shouldn't happen
            }
        } else if (groundTicks > MAX_GROUND_TICKS) {
            TICKS_ON_GROUND.put(uuid, 0);
        }
    }

    private static boolean isWearingElytra(ServerPlayerEntity player) {
        if (player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)) {
            return true;
        }

        if (ACCESSORIES_LOADED) {
            return Accessories.hasElytraEquipped(player);
        }

        return false;
    }

    public static void clearState(UUID playerUUID) {
        TICKS_ON_GROUND.remove(playerUUID);
    }
}