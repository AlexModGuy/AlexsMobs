package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleDna extends SimpleAnimatedParticle {

    private ParticleDna(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
        super(world, x, y, z, sprites, 0.0F);
        this.xd = (float) motionX;
        this.yd = (float) motionY;
        this.zd = (float) motionZ;
        this.quadSize *= 1.5F + this.random.nextFloat() * 0.6F;
        this.lifetime = 15 + this.random.nextInt(15);
        this.gravity = 0.1F;
        int color = 15916745;
        float lvt_18_1_ = (float)(color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float)(color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float)(color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        this.setSpriteFromAge(sprites);
        this.alpha = 0F;
        roll = (float) (Math.PI * 0.5F * random.nextFloat());
    }

    public int getLightColor(float p_189214_1_) {
        int lvt_2_1_ = super.getLightColor(p_189214_1_);
        int lvt_4_1_ = lvt_2_1_ >> 16 & 255;
        return 240 | lvt_4_1_ << 16;
    }

    public void tick() {
        super.tick();
        this.oRoll = roll;
        this.xd += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.yd += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.zd += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);

        float subAlpha = 1F;
        if(this.age > 5){
            subAlpha = 1 - (float)(this.age - 5) / (this.getLifetime() - 5);
        }
        this.alpha = subAlpha;
        this.setSpriteFromAge(this.sprites);
        this.roll += (float)Math.random() * ((float)Math.PI * 0.3F * alpha);
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
            ParticleDna p = new ParticleDna(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return p;
        }
    }
}
