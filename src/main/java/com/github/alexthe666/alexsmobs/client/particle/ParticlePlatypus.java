package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticlePlatypus extends SimpleAnimatedParticle {

    private ParticlePlatypus(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite sprites) {
        super(world, x, y, z, sprites, 0.0F);
        this.motionX = (float) motionX;
        this.motionY = (float) motionY;
        this.motionZ = (float) motionZ;
        this.particleScale *= 0.2F + this.rand.nextFloat() * 0.6F;
        this.maxAge = 3 + this.rand.nextInt(2);
        this.particleGravity = 0;
        this.selectSpriteWithAge(sprites);
    }

    public int getBrightnessForRender(float p_189214_1_) {
        int lvt_2_1_ = super.getBrightnessForRender(p_189214_1_);
        int lvt_4_1_ = lvt_2_1_ >> 16 & 255;
        return 240 | lvt_4_1_ << 16;
    }

    public void tick() {
        super.tick();
        this.motionX *= 0.8D;
        this.motionY *= 0.8D;
        this.motionZ *= 0.8D;
        double lvt_1_1_ = this.posX - this.prevPosX;
        double lvt_5_1_ = this.posZ - this.prevPosZ;
        float lvt_9_1_ = (float) (MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 180F;
        this.particleAngle = lvt_9_1_;
        this.prevParticleAngle = this.particleAngle;
        this.selectSpriteWithAge(this.spriteWithAge);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticlePlatypus p = new ParticlePlatypus(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return p;
        }
    }
}
