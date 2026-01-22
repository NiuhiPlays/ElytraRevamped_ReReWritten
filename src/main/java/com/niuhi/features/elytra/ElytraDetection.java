package com.niuhi.features.elytra;

import com.niuhi.compat.Accessories;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class ElytraDetection {
    private final boolean accessoriesLoaded;

    public ElytraDetection() {
        this.accessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");
    }

    public boolean isFlying(ServerPlayerEntity player) {
        boolean isGliding = player.isGliding();
        boolean isWearingElytra = isWearingElytra(player);

        return isGliding && isWearingElytra;
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
        System.out.print(isWearingElytra(player));
        return result;
    }
}
