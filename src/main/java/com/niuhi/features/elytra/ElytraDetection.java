package com.niuhi.features.elytra;

import com.niuhi.compat.Accessories;
import com.niuhi.ElytraRevampedReReWritten;
import com.niuhi.config.DebugLogger;
import com.niuhi.config.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElytraDetection {
    private final boolean accessoriesLoaded;
    private static final Map<UUID, ElytraState> LAST_STATE = new HashMap<>();

    public ElytraDetection() {
        this.accessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");
    }

    public boolean isFlying(ServerPlayerEntity player) {
        boolean isGliding = player.isGliding();
        boolean isWearingElytra = isWearingElytra(player);

        boolean result = isGliding && isWearingElytra;
        ModConfig config = ModConfig.getInstance();
        DebugLogger logger = new DebugLogger(ElytraRevampedReReWritten.MOD_ID, config);
        ElytraState last = LAST_STATE.get(player.getUuid());
        ElytraState current = new ElytraState(isGliding, isWearingElytra, result);
        if (!current.equals(last)) {
            logger.log("ELYTRA", "Check gliding=" + isGliding
                    + " wearing=" + isWearingElytra
                    + " result=" + result
                    + " player=" + player.getName().getString());
            LAST_STATE.put(player.getUuid(), current);
        }
        return result;
    }

    public boolean isWearingElytra(ServerPlayerEntity player) {
        boolean hasElytraEquipped = player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
        boolean result = hasElytraEquipped;

        if (accessoriesLoaded) {
            try {
                boolean hasAccessoryElytra = Accessories.hasElytraEquipped(player);
                result = hasElytraEquipped || hasAccessoryElytra;
            } catch (Exception ignored){
            }
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
