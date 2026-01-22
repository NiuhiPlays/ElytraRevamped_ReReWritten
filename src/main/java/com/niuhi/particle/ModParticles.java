package com.niuhi.particle;

import com.niuhi.ElytraRevampedReReWritten;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModParticles {
    public static final SimpleParticleType COLORED_CAMPFIRE_SMOKE = FabricParticleTypes.simple();

    private ModParticles() {
    }

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE,
                Identifier.of(ElytraRevampedReReWritten.MOD_ID, "colored_campfire_smoke"),
                COLORED_CAMPFIRE_SMOKE);
    }
}
