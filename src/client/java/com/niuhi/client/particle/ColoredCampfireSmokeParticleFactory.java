package com.niuhi.client.particle;

import com.niuhi.particle.ModParticles;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ColoredCampfireSmokeParticleFactory implements ParticleProvider<SimpleParticleType> {
    private static int nextColor = 0xFFFFFF;
    private final SpriteSet spriteProvider;

    public ColoredCampfireSmokeParticleFactory(SpriteSet spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    public static void setNextColor(int rgb) {
        nextColor = rgb;
    }

    @Override
    public ColoredCampfireSmokeParticle createParticle(SimpleParticleType parameters, ClientLevel world,
                              double x, double y, double z,
                              double velocityX, double velocityY, double velocityZ,
                              RandomSource random) {
        if (parameters != ModParticles.COLORED_CAMPFIRE_SMOKE) {
            return null;
        }
        int rgb = nextColor;
        float red = ((rgb >> 16) & 0xFF) / 255.0f;
        float green = ((rgb >> 8) & 0xFF) / 255.0f;
        float blue = (rgb & 0xFF) / 255.0f;
        return new ColoredCampfireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, red, green, blue, spriteProvider, random);
    }
}
