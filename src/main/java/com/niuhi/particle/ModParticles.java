package com.niuhi.particle;

import com.niuhi.ElytraRevampedReReWritten;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public final class ModParticles {
    public static final SimpleParticleType COLORED_CAMPFIRE_SMOKE = FabricParticleTypes.simple();

    private ModParticles() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE,
                Identifier.fromNamespaceAndPath(ElytraRevampedReReWritten.MOD_ID, "colored_campfire_smoke"),
                COLORED_CAMPFIRE_SMOKE);
    }
}
