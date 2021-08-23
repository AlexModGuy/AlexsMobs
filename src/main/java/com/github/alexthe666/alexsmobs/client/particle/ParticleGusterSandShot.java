package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleGusterSandShot extends SpriteTexturedParticle {

    private ParticleGusterSandShot(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, int variant) {
        super(world, x, y, z);
        int color = ParticleGusterSandSpin.selectColor(variant, this.rand);
        float lvt_18_1_ = (float)(color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float)(color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float)(color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        this.motionX = (float) motionX;
        this.motionY = (float) motionY;
        this.motionZ = (float) motionZ;
        this.particleScale *= 0.6F + this.rand.nextFloat() * 1.4F;
        this.maxAge = 10 + this.rand.nextInt(15);
        this.particleGravity = 0.5F;

    }

    public void tick() {
        super.tick();
        this.motionY -= 0.004D + 0.04D * (double)this.particleGravity;
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
            ParticleGusterSandShot p = new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0);
            p.selectSpriteRandomly(spriteSet);
            return p;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class FactoryRed implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public FactoryRed(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleGusterSandShot p = new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1);
            p.selectSpriteRandomly(spriteSet);
            return p;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class FactorySoul implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public FactorySoul(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleGusterSandShot p = new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 2);
            p.selectSpriteRandomly(spriteSet);
            return p;
        }
    }
}
