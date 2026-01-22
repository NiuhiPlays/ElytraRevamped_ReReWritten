package com.niuhi.client.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.SpriteProvider;

public class ColoredCampfireSmokeParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    protected ColoredCampfireSmokeParticle(ClientWorld world, double x, double y, double z,
                                           double velocityX, double velocityY, double velocityZ,
                                           float red, float green, float blue,
                                           SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.scale = 0.9f + world.random.nextFloat() * 0.2f;
        this.maxAge = 40 + world.random.nextInt(20);
        this.gravityStrength = 0.0f;
        setColor(red, green, blue);
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
