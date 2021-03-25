package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleWhaleSplash  extends RainParticle {

    private ParticleWhaleSplash(ClientWorld p_i232433_1_, double p_i232433_2_, double p_i232433_4_, double p_i232433_6_, double p_i232433_8_, double p_i232433_10_, double p_i232433_12_) {
        super(p_i232433_1_, p_i232433_2_, p_i232433_4_, p_i232433_6_);
        this.particleGravity = 0.04F;
        this.motionY = 1D;
        this.maxAge = (int)(16.0D / (Math.random() * 0.4D + 0.1D));
        this.particleScale = 0.2F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
    }

    public void tick() {
        super.tick();
        if(this.motionY < 0D){
            if(Math.abs(this.motionX) < 0.23){
                this.motionX *= 1.2D;
            }
            if(Math.abs(this.motionZ) < 0.23){
                this.motionZ *= 1.2D;
            }
        }
    }

        @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50679_1_) {
            this.spriteSet = p_i50679_1_;
        }

        public Particle makeParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            ParticleWhaleSplash lvt_15_1_ = new ParticleWhaleSplash(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
            lvt_15_1_.selectSpriteRandomly(this.spriteSet);
            return lvt_15_1_;
        }
    }

}
