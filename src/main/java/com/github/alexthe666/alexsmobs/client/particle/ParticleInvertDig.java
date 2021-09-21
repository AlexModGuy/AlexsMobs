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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleInvertDig extends SimpleAnimatedParticle {

    private Entity creator;

    protected ParticleInvertDig(ClientLevel world, double x, double y, double z, SpriteSet spriteWithAge, double creatorId) {
        super(world, x, y, z, spriteWithAge, 0);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize = 0.1F;
        this.alpha = 1F;
        this.lifetime = ItemDimensionalCarver.MAX_TIME;
        this.hasPhysics = false;
        this.creator = world.getEntity((int) creatorId);
    }

    public int getLightColor(float p_189214_1_) {
        return 240;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        boolean live = false;
        this.quadSize = 0.1F + Math.min((age / (float)lifetime), 0.5F) * 0.5F;
        if (this.age++ >= lifetime || creator == null) {
            this.remove();
        } else {
            if (creator instanceof Player) {
                ItemStack item = ((Player) creator).getUseItem();
                if (item.getItem() == AMItemRegistry.DIMENSIONAL_CARVER) {
                    this.age = Mth.clamp(lifetime - ((Player) creator).getUseItemRemainingTicks(), 0, lifetime);
                    live = true;
                }
            }
        }
        if(!live){
            this.remove();
        }
        this.setSpriteFromAge(this.sprites);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ParticleInvertDig heartparticle = new ParticleInvertDig(worldIn, x, y, z, this.spriteSet, xSpeed);
            heartparticle.setSpriteFromAge(this.spriteSet);
            return heartparticle;
        }
    }
}
