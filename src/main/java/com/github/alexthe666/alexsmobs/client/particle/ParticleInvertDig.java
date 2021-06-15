package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.*;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleInvertDig extends SimpleAnimatedParticle {

    IParticleRenderType PARTICLE_SHEET_LIGHTNINGY = new IParticleRenderType() {
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            textureManager.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        public void finishRender(Tessellator tesselator) {
            tesselator.draw();
        }

        public String toString() {
            return "PARTICLE_SHEET_LIGHTNINGY";
        }
    };

    private Entity creator;

    protected ParticleInvertDig(ClientWorld world, double x, double y, double z, IAnimatedSprite spriteWithAge, double creatorId) {
        super(world, x, y, z, spriteWithAge, 0);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.particleScale *= 2F;
        this.setAlphaF(0.9F);
        this.maxAge = ItemDimensionalCarver.MAX_TIME;
        this.canCollide = false;
        this.creator = world.getEntityByID((int) creatorId);
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        boolean live = false;
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
    public IParticleRenderType getRenderType() {
        return PARTICLE_SHEET_LIGHTNINGY;
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
