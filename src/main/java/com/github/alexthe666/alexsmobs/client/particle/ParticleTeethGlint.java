package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleTeethGlint extends TextureSheetParticle {

    private float initScale = 0.25F;

    private ParticleTeethGlint(ClientLevel p_i232444_1_, double p_i232444_2_, double p_i232444_4_, double p_i232444_6_, double p_i232444_8_, double p_i232444_10_, double p_i232444_12_) {
        super(p_i232444_1_, p_i232444_2_, p_i232444_4_, p_i232444_6_, p_i232444_8_, p_i232444_10_, p_i232444_12_);
        float lvt_14_1_ = this.random.nextFloat() * 0.1F + 0.2F;
        this.rCol = lvt_14_1_;
        this.gCol = lvt_14_1_;
        this.bCol = lvt_14_1_;
        this.setSize(0.02F, 0.02F);
        this.quadSize = initScale = 0.1F * (this.random.nextFloat() * 0.3F + 0.5F);
        this.xd *= 0.019999999552965164D;
        this.yd *= 0.019999999552965164D;
        this.zd *= 0.019999999552965164D;
        this.lifetime = 3 + this.random.nextInt(5);
        this.setAlpha(1F - (this.age / (float)this.lifetime) * 0.5F);
    }

    public int getLightColor(float p_189214_1_) {
        int lvt_2_1_ = super.getLightColor(p_189214_1_);
        int lvt_4_1_ = lvt_2_1_ >> 16 & 255;
        return 240 | lvt_4_1_ << 16;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
        this.setBoundingBox(this.getBoundingBox().move(p_187110_1_, p_187110_3_, p_187110_5_));
        this.setLocationFromBoundingbox();
    }

    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.99D;
            this.yd *= 0.99D;
            this.zd *= 0.99D;
        }
        this.roll += 0.25F * Math.sin(this.age * 2);
        this.quadSize = initScale * (1F - (this.age / (float)this.lifetime) * 0.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet p_i50522_1_) {
            this.spriteSet = p_i50522_1_;
        }

        public Particle createParticle(SimpleParticleType p_199234_1_, ClientLevel p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            ParticleTeethGlint lvt_15_1_ = new ParticleTeethGlint(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
            lvt_15_1_.pickSprite(this.spriteSet);
            lvt_15_1_.setColor(1.0F, 1.0F, 1.0F);
            return lvt_15_1_;
        }
    }


}
