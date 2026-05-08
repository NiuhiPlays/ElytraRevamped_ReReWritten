package com.niuhi.features.elytra;

import com.niuhi.compat.Accessories;
import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElytraDetection {
    private static final boolean ACCESSORIES_LOADED = FabricLoader.getInstance().isModLoaded("accessories");
    private static final Map<UUID, ElytraState> LAST_STATE = new HashMap<>();

    public boolean isFlying(ServerPlayer player) {
        boolean isGliding = player.isFallFlying();
        boolean isWearingElytra = isWearingElytra(player);

        boolean result = isGliding && isWearingElytra;
        ModConfig config = ModConfig.getInstance();
        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        ElytraState last = LAST_STATE.get(player.getUUID());
        ElytraState current = new ElytraState(isGliding, isWearingElytra, result);
        if (!current.equals(last)) {
            logger.log("ELYTRA", "Check gliding=" + isGliding
                    + " wearing=" + isWearingElytra
                    + " result=" + result
                    + " player=" + player.getName().getString());
            LAST_STATE.put(player.getUUID(), current);
        }
        return result;
    }

    public boolean isWearingElytra(ServerPlayer player) {
        boolean hasElytraEquipped = player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
        boolean result = hasElytraEquipped;

        if (ACCESSORIES_LOADED) {
            boolean hasAccessoryElytra = Accessories.hasElytraEquipped(player);
            result = hasElytraEquipped || hasAccessoryElytra;
        }
        return result;
    }

    private static final class ElytraState {
        private final boolean gliding;
        private final boolean wearing;
        private final boolean flying;

        private ElytraState(boolean gliding, boolean wearing, boolean flying) {
            this.gliding = gliding;
            this.wearing = wearing;
            this.flying = flying;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ElytraState state)) {
                return false;
            }
            return gliding == state.gliding && wearing == state.wearing && flying == state.flying;
        }

        @Override
        public int hashCode() {
            int result = gliding ? 1 : 0;
            result = 31 * result + (wearing ? 1 : 0);
            result = 31 * result + (flying ? 1 : 0);
            return result;
        }
    }
}
