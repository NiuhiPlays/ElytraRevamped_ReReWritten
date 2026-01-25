package com.niuhi.client.particle;

import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.util.math.random.Random;

public class ColoredCampfireSmokeParticle extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected ColoredCampfireSmokeParticle(ClientWorld world, double x, double y, double z,
                                           double velocityX, double velocityY, double velocityZ,
                                           float red, float green, float blue,
                                           SpriteProvider spriteProvider,
                                           Random random) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getSprite(random));
        this.spriteProvider = spriteProvider;
        this.scale = 0.9f + world.random.nextFloat() * 0.2f;
        this.maxAge = 40 + world.random.nextInt(20);
        this.gravityStrength = 0.0f;
        setColor(red, green, blue);
        updateSprite(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        updateSprite(spriteProvider);
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }
}
