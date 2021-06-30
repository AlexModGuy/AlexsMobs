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

    public int getBrightnessForRender(float p_189214_1_) {
        return 240;
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
