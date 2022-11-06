package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ParticleBirdSong extends TextureSheetParticle {

    private ParticleBirdSong(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(world, x, y, z);
        this.friction = 0.7F;
        this.gravity = 0.0F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = 0.15F + this.random.nextFloat() * 0.2F;
        this.lifetime = 20 + this.random.nextInt(20);
        this.pickSprite(sprites);
        rCol = 0.294F;
        gCol = 0.584F;
        bCol = 1.0F;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd = Math.sin(age * 0.3F) * 0.3F;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.yd -= (double)this.gravity;
        }
        float subAlpha = 1F;
        if(this.age > 5){
            subAlpha = 1 - (float)(this.age - 5) / (this.getLifetime() - 5);
        }
        this.xd *= 0.99D;
        this.yd *= 0.99D;
        this.zd *= 0.99D;
        this.alpha = subAlpha;
        this.roll = (float) (Math.toRadians(Math.sin(age * 0.01F) * 5));

    }

    public int getLightColor(float p_189214_1_) {
        int lvt_2_1_ = super.getLightColor(p_189214_1_);
        int lvt_4_1_ = lvt_2_1_ >> 16 & 255;
        return 240 | lvt_4_1_ << 16;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleBirdSong(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }
}
