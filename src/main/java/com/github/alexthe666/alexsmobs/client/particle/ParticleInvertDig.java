package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleInvertDig extends SimpleAnimatedParticle {

    private Entity creator;

    protected ParticleInvertDig(ClientWorld world, double x, double y, double z, IAnimatedSprite spriteWithAge, double creatorId) {
        super(world, x, y, z, spriteWithAge, 0);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.particleScale = 0.1F;
        this.particleAlpha = 1F;
        this.maxAge = ItemDimensionalCarver.MAX_TIME;
        this.canCollide = false;
        this.creator = world.getEntityByID((int) creatorId);
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        boolean live = false;
        this.particleScale = 0.1F + Math.min((age / (float)maxAge), 0.5F) * 0.5F;
        if (this.age++ >= maxAge || creator == null) {
            this.setExpired();
        } else {
            if (creator instanceof PlayerEntity) {
                ItemStack item = ((PlayerEntity) creator).getActiveItemStack();
                if (item.getItem() == AMItemRegistry.DIMENSIONAL_CARVER) {
                    this.age = MathHelper.clamp(maxAge - ((PlayerEntity) creator).getItemInUseCount(), 0, maxAge);
                    live = true;
                }
            }
        }
        if(!live){
            this.setExpired();
        }
        this.selectSpriteWithAge(this.spriteWithAge);
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
        RenderSystem.enableBlend();
        Vector3d vector3d = renderInfo.getProjectedView();
        float f = (float)(MathHelper.lerp((double)partialTicks, this.prevPosX, this.posX) - vector3d.getX());
        float f1 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosY, this.posY) - vector3d.getY());
        float f2 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosZ, this.posZ) - vector3d.getZ());
        Quaternion quaternion;
        if (this.particleAngle == 0.0F) {
            quaternion = renderInfo.getRotation();
        } else {
            quaternion = new Quaternion(renderInfo.getRotation());
            float f3 = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
            quaternion.multiply(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getScale(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getMinU();
        float f8 = this.getMaxU();
        float f5 = this.getMinV();
        float f6 = this.getMaxV();
        int j = this.getBrightnessForRender(partialTicks);
        Tessellator tessellator = Tessellator.getInstance();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        BufferBuilder buffer2 = tessellator.getBuffer();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        buffer2.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer2.pos((double)avector3f[0].getX(), (double)avector3f[0].getY(), (double)avector3f[0].getZ()).tex(f8, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer2.pos((double)avector3f[1].getX(), (double)avector3f[1].getY(), (double)avector3f[1].getZ()).tex(f8, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer2.pos((double)avector3f[2].getX(), (double)avector3f[2].getY(), (double)avector3f[2].getZ()).tex(f7, f5).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        buffer2.pos((double)avector3f[3].getX(), (double)avector3f[3].getY(), (double)avector3f[3].getZ()).tex(f7, f6).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j).endVertex();
        Tessellator.getInstance().draw();
        RenderSystem.disableBlend();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleInvertDig heartparticle = new ParticleInvertDig(worldIn, x, y, z, this.spriteSet, xSpeed);
            heartparticle.selectSpriteWithAge(this.spriteSet);
            return heartparticle;
        }
    }
}
