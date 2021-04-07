package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleDna extends SimpleAnimatedParticle {

    private ParticleDna(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite sprites) {
        super(world, x, y, z, sprites, 0.0F);
        this.motionX = (float) motionX;
        this.motionY = (float) motionY;
        this.motionZ = (float) motionZ;
        this.particleScale *= 1.5F + this.rand.nextFloat() * 0.6F;
        this.maxAge = 15 + this.rand.nextInt(15);
        this.particleGravity = 0.1F;
        int color = 15916745;
        float lvt_18_1_ = (float)(color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float)(color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float)(color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        this.selectSpriteWithAge(sprites);
        this.particleAlpha = 0F;
        particleAngle = (float) (Math.PI * 0.5F * rand.nextFloat());
    }

    public int getBrightnessForRender(float p_189214_1_) {
        int lvt_2_1_ = super.getBrightnessForRender(p_189214_1_);
        int lvt_4_1_ = lvt_2_1_ >> 16 & 255;
        return 240 | lvt_4_1_ << 16;
    }

    public void tick() {
        super.tick();
        this.prevParticleAngle = particleAngle;
        this.motionX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
        this.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
        this.motionZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);

        float subAlpha = 1F;
        if(this.age > 5){
            subAlpha = 1 - (float)(this.age - 5) / (this.getMaxAge() - 5);
        }
        this.particleAlpha = subAlpha;
        this.selectSpriteWithAge(this.spriteWithAge);
        this.particleAngle += (float)Math.random() * ((float)Math.PI * 0.3F * particleAlpha);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleDna p = new ParticleDna(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return p;
        }
    }
}
