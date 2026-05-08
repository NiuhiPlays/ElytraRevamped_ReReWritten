package com.niuhi.client.particle;

import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;

public class ColoredCampfireSmokeParticle extends SingleQuadParticle {
    private final SpriteSet spriteProvider;

    protected ColoredCampfireSmokeParticle(ClientLevel world, double x, double y, double z,
                                           double velocityX, double velocityY, double velocityZ,
                                           float red, float green, float blue,
                                           SpriteSet spriteProvider,
                                           RandomSource random) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.get(random));
        this.spriteProvider = spriteProvider;
        this.quadSize = 0.9f + world.getRandom().nextFloat() * 0.2f;
        this.lifetime = 40 + world.getRandom().nextInt(20);
        this.gravity = 0.0f;
        setColor(red, green, blue);
        setSpriteFromAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(spriteProvider);
    }

    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}
