package com.github.alexthe666.alexsmobs.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleGusterSandSpin extends TextureSheetParticle {

    private float targetX;
    private float targetY;
    private float targetZ;
    private float distX;
    private float distZ;
    private static final int[] POSSIBLE_COLORS = {0XF3C389, 0XEFB578, 0XF8D49A, 0XFFE6AD, 0XFFFFCD};
    private static final int[] POSSIBLE_COLORS_RED = {0XD57136, 0XC66127, 0XBC5A21, 0XB2541B, 0XA74B11};
    private static final int[] POSSIBLE_COLORS_SOUL = {0X534238, 0X4E3D33, 0X3D2E25, 0X49372C, 0X2B201B};

    private ParticleGusterSandSpin(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, int variant) {
        super(world, x, y, z);
        int color = selectColor(variant, this.random);
        float lvt_18_1_ = (float)(color >> 16 & 255) / 255.0F;
        float lvt_19_1_ = (float)(color >> 8 & 255) / 255.0F;
        float lvt_20_1_ = (float)(color & 255) / 255.0F;
        setColor(lvt_18_1_, lvt_19_1_, lvt_20_1_);
        targetX = (float) motionX;
        targetY = (float) motionY;
        targetZ = (float) motionZ;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize *= 0.4F + this.random.nextFloat() * 1.4F;
        this.lifetime = 15 + this.random.nextInt(15);
        distX = (float) (x - targetX);
        distZ = (float) (z - targetZ);
    }

    public static int selectColor(int variant, RandomSource rand){
        if(variant == 2){
            return POSSIBLE_COLORS_SOUL[rand.nextInt(POSSIBLE_COLORS_SOUL.length - 1)];
        }else if(variant == 1){
            return POSSIBLE_COLORS_RED[rand.nextInt(POSSIBLE_COLORS_RED.length - 1)];
        }else{
            return POSSIBLE_COLORS[rand.nextInt(POSSIBLE_COLORS.length - 1)];
        }
    }

    public void tick() {
        super.tick();
        float radius = 2;
        float angle = age * 2;
        double extraX = this.targetX + radius * Mth.sin(Mth.PI + angle);
        double extraZ = this.targetZ + radius * Mth.cos(angle);
        double d2 = extraX - this.x;
        double d3 = this.targetY - this.y;
        double d4 = extraZ - this.z;
        float speed = 0.02F;
        this.xd += d2 * speed;
        this.yd += d3 * speed;
        this.zd += d4 * speed;
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
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
            ParticleGusterSandSpin p = new ParticleGusterSandSpin(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0);
            p.pickSprite(spriteSet);
            return p;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class FactoryRed implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FactoryRed(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleGusterSandSpin p = new ParticleGusterSandSpin(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1);
            p.pickSprite(spriteSet);
            return p;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class FactorySoul implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public FactorySoul(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleGusterSandSpin p = new ParticleGusterSandSpin(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 2);
            p.pickSprite(spriteSet);
            return p;
        }
    }
}
