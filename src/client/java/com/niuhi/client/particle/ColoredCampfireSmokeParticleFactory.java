package com.niuhi.client.particle;

import com.niuhi.particle.ModParticles;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class ColoredCampfireSmokeParticleFactory implements ParticleFactory<SimpleParticleType> {
    private static int nextColor = 0xFFFFFF;
    private final SpriteProvider spriteProvider;

    public ColoredCampfireSmokeParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    public static void setNextColor(int rgb) {
        nextColor = rgb;
    }

    @Override
    public Particle createParticle(SimpleParticleType parameters, ClientWorld world,
                                   double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ) {
        if (parameters != ModParticles.COLORED_CAMPFIRE_SMOKE) {
            return null;
        }
        int rgb = nextColor;
        float red = ((rgb >> 16) & 0xFF) / 255.0f;
        float green = ((rgb >> 8) & 0xFF) / 255.0f;
        float blue = (rgb & 0xFF) / 255.0f;
        return new ColoredCampfireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, red, green, blue, spriteProvider);
    }
}
