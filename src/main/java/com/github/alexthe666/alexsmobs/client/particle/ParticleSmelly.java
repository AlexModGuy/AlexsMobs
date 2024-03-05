package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleSmelly extends SimpleAnimatedParticle {

    private ParticleSmelly(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
        super(world, x, y, z, sprites, 0.0F);
        this.xd = (float) motionX;
        this.yd = (float) motionY;
        this.zd = (float) motionZ;
        this.quadSize *= 0.7F + this.random.nextFloat() * 0.6F;
        this.lifetime = 15 + this.random.nextInt(15);
        this.gravity = -0.1F;
        this.setSpriteFromAge(sprites);
    }

    public void tick() {
        super.tick();
        this.oRoll = roll;
        this.xd += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.yd += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.zd += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleSmelly p = new ParticleSmelly(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return p;
        }
    }
}
