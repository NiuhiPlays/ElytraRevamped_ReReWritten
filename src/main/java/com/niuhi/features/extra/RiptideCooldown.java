package com.niuhi.features.extra;

import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RiptideCooldown {
    private static final Map<UUID, Integer> PENDING_COOLDOWN = new HashMap<>();

    private RiptideCooldown() {
    }

    public static void clearOnLanding(ServerPlayerEntity player, ModConfig config) {
        if (!config.elytraConfig.riptideNerf) {
            return;
        }
        if (player.isGliding() || !player.isOnGround()) {
            return;
        }
        Identifier group = player.getItemCooldownManager().getGroup(new ItemStack(Items.TRIDENT));
        player.getItemCooldownManager().remove(group);
    }

    public static void applyPending(ServerPlayerEntity player, ModConfig config, int serverTick) {
        if (!config.elytraConfig.riptideNerf) {
            return;
        }
        Integer cooldown = PENDING_COOLDOWN.remove(player.getUuid());
        if (cooldown == null || cooldown <= 0) {
            return;
        }
        player.getItemCooldownManager().set(new ItemStack(Items.TRIDENT), cooldown);
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }
            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }

            ModConfig config = ModConfig.getInstance();
            if (!config.elytraConfig.riptideNerf) {
                return ActionResult.PASS;
            }

            var stack = player.getStackInHand(hand);
            if (!stack.isOf(Items.TRIDENT)) {
                return ActionResult.PASS;
            }
            if (serverPlayer.getItemCooldownManager().isCoolingDown(stack)) {
                return ActionResult.FAIL;
            }
            var enchantmentRegistry = serverPlayer.getServerWorld()
                    .getRegistryManager()
                    .getOrThrow(RegistryKeys.ENCHANTMENT);
            Enchantment riptide = enchantmentRegistry.get(Enchantments.RIPTIDE);
            if (riptide == null) {
                return ActionResult.PASS;
            }
            RegistryEntry<Enchantment> riptideEntry = enchantmentRegistry.getEntry(riptide);
            if (EnchantmentHelper.getLevel(riptideEntry, stack) <= 0) {
                return ActionResult.PASS;
            }

            if (!serverPlayer.isGliding()) {
                return ActionResult.PASS;
            }

            int cooldown = Math.max(0, config.elytraConfig.riptideCooldown);
            if (cooldown > 0) {
                PENDING_COOLDOWN.put(serverPlayer.getUuid(), cooldown);
                DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
                logger.log("ELYTRA", "Applied riptide cooldown=" + cooldown
                        + " player=" + serverPlayer.getName().getString());
            }

            return ActionResult.PASS;
        });
    }
}
