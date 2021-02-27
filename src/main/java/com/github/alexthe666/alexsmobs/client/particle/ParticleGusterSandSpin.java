package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleGusterSandSpin extends SpriteTexturedParticle {

    private float targetX;
    private float targetY;
    private float targetZ;
    private float distX;
    private float distZ;
    private static final int[] POSSIBLE_COLORS = {0XF3C389, 0XEFB578, 0XF8D49A, 0XFFE6AD, 0XFFFFCD};

    private ParticleGusterSandSpin(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        int color = POSSIBLE_COLORS[rand.nextInt(POSSIBLE_COLORS.length - 1)];
        float lvt_18_1_ = (float)(color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float)(color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float)(color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        targetX = (float) motionX;
        targetY = (float) motionY;
        targetZ = (float) motionZ;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.particleScale *= 0.4F + this.rand.nextFloat() * 1.4F;
        this.maxAge = 15 + this.rand.nextInt(15);
        distX = (float) (x - targetX);
        distZ = (float) (z - targetZ);
    }

    public void tick() {
        super.tick();
        float radius = 2;
        float angle = age * 2;
        double extraX = this.targetX + radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = this.targetZ + radius * MathHelper.cos(angle);
        double d2 = extraX - this.posX;
        double d3 = this.targetY - this.posY;
        double d4 = extraZ - this.posZ;
        float speed = 0.02F;
        this.motionX += d2 * speed;
        this.motionY += d3 * speed;
        this.motionZ += d4 * speed;

    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
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
            ParticleGusterSandSpin p = new ParticleGusterSandSpin(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            p.selectSpriteRandomly(spriteSet);
            return p;
        }
    }
}
