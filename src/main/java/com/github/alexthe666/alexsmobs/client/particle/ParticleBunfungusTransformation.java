package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBunfungusTransformation  extends TextureSheetParticle {
    private final SpriteSet sprites;

    public ParticleBunfungusTransformation(ClientLevel p_107483_, double p_107484_, double p_107485_, double p_107486_, double p_107487_, double p_107488_, double p_107489_, SpriteSet p_107490_) {
        super(p_107483_, p_107484_, p_107485_, p_107486_, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.sprites = p_107490_;
        float f = 2.5F;
        this.xd *= (double)0.1F;
        this.yd *= (double)0.1F;
        this.zd *= (double)0.1F;
        this.xd += p_107487_;
        this.yd += p_107488_;
        this.zd += p_107489_;
        int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
        this.lifetime = (int)Math.max((float)i * 2.5F, 1.0F);
        this.hasPhysics = false;
        this.setSpriteFromAge(p_107490_);
        this.setAlpha((float) (Math.random() * 0.3F + 0.7F));
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getQuadSize(float p_107504_) {
        return this.quadSize * Mth.clamp(((float)this.age + p_107504_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSpriteFromAge(this.sprites);
            Player player = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0D, false);
            if (player != null) {
                double d0 = player.getY();
                if (this.y > d0) {
                    this.y += (d0 - this.y) * 0.2D;
                    this.yd += (player.getDeltaMovement().y - this.yd) * 0.2D;
                    this.setPos(this.x, this.y, this.z);
                }
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet p_107528_) {
            this.sprites = p_107528_;
        }

        public Particle createParticle(SimpleParticleType p_107539_, ClientLevel p_107540_, double p_107541_, double p_107542_, double p_107543_, double p_107544_, double p_107545_, double p_107546_) {
            ParticleBunfungusTransformation particle = new ParticleBunfungusTransformation(p_107540_, p_107541_, p_107542_, p_107543_, p_107544_, p_107545_, p_107546_, this.sprites);
            particle.setColor(0.427451F, 0.4470588F, 0.5764706F);
            return particle;
        }
    }
}
