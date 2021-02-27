package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleGusterSandShot extends SpriteTexturedParticle {

    private static final int[] POSSIBLE_COLORS = {0XF3C389, 0XEFB578, 0XF8D49A, 0XFFE6AD, 0XFFFFCD};

    private ParticleGusterSandShot(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        int color = POSSIBLE_COLORS[rand.nextInt(POSSIBLE_COLORS.length - 1)];
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
            ParticleGusterSandShot p = new ParticleGusterSandShot(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            p.selectSpriteRandomly(spriteSet);
            return p;
        }
    }
}
