package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleTeethGlint extends SpriteTexturedParticle {

    private float initScale = 0.25F;

    private ParticleTeethGlint(ClientWorld p_i232444_1_, double p_i232444_2_, double p_i232444_4_, double p_i232444_6_, double p_i232444_8_, double p_i232444_10_, double p_i232444_12_) {
        super(p_i232444_1_, p_i232444_2_, p_i232444_4_, p_i232444_6_, p_i232444_8_, p_i232444_10_, p_i232444_12_);
        float lvt_14_1_ = this.rand.nextFloat() * 0.1F + 0.2F;
        this.particleRed = lvt_14_1_;
        this.particleGreen = lvt_14_1_;
        this.particleBlue = lvt_14_1_;
        this.setSize(0.02F, 0.02F);
        this.particleScale = initScale = 0.1F * (this.rand.nextFloat() * 0.3F + 0.5F);
        this.motionX *= 0.019999999552965164D;
        this.motionY *= 0.019999999552965164D;
        this.motionZ *= 0.019999999552965164D;
        this.maxAge = 3 + this.rand.nextInt(5);
        this.setAlphaF(1F - (this.age / (float)this.maxAge) * 0.5F);
    }

    public int getBrightnessForRender(float p_189214_1_) {
        int lvt_2_1_ = super.getBrightnessForRender(p_189214_1_);
        int lvt_4_1_ = lvt_2_1_ >> 16 & 255;
        return 240 | lvt_4_1_ << 16;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
        this.setBoundingBox(this.getBoundingBox().offset(p_187110_1_, p_187110_3_, p_187110_5_));
        this.resetPositionToBB();
    }

    public void tick() {
        super.tick();
        this.prevParticleAngle = this.particleAngle;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.maxAge-- <= 0) {
            this.setExpired();
        } else {
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.99D;
            this.motionY *= 0.99D;
            this.motionZ *= 0.99D;
        }
        this.particleAngle += 0.25F * Math.sin(this.age * 2);
        this.particleScale = initScale * (1F - (this.age / (float)this.maxAge) * 0.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50522_1_) {
            this.spriteSet = p_i50522_1_;
        }

        public Particle makeParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            ParticleTeethGlint lvt_15_1_ = new ParticleTeethGlint(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
            lvt_15_1_.selectSpriteRandomly(this.spriteSet);
            lvt_15_1_.setColor(1.0F, 1.0F, 1.0F);
            return lvt_15_1_;
        }
    }


}
