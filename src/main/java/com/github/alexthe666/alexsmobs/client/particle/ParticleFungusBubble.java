package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleFungusBubble extends SimpleAnimatedParticle {

    private ParticleFungusBubble(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet sprites) {
        super(world, x, y, z, sprites, 0.0F);
        this.xd = (float) motionX;
        this.yd = (float) motionY + 0.04D;
        this.zd = (float) motionZ;
        this.quadSize *= 0.5F + this.random.nextFloat() * 0.5F;
        this.lifetime = 15 + this.random.nextInt(20);
        this.gravity = -0.1F;
        this.setSprite(this.sprites.get(0, this.lifetime));
    }

    public void tick() {
        super.tick();
        int halflife = this.lifetime / 2;
        int spriteMod = halflife / 6;
        boolean flag = false;
        if (this.age < halflife) {
            flag = true;
            this.setSprite(this.sprites.get(0, 6));
        } else if (this.age < halflife + spriteMod) {
            this.setSprite(this.sprites.get(1, 6));
        }else if (this.age < halflife + spriteMod * 2) {
            this.setSprite(this.sprites.get(2, 6));
        }else if (this.age < halflife + spriteMod * 3) {
            this.setSprite(this.sprites.get(3, 6));
        }else if (this.age < halflife + spriteMod * 4) {
            this.setSprite(this.sprites.get(4, 6));
        }else if (this.age < halflife + spriteMod * 5) {
            this.setSprite(this.sprites.get(5, 6));
        }else{
            this.setSprite(this.sprites.get(6, 6));
        }
        if(!flag){
            this.yd = 0;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleFungusBubble p = new ParticleFungusBubble(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return p;
        }
    }
}
