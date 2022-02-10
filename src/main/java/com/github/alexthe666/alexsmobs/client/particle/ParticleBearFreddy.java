package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.MobAppearanceParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBearFreddy extends Particle {
    private final ModelGrizzlyBear model = new ModelGrizzlyBear();

    ParticleBearFreddy(ClientLevel lvl, double x, double y, double z) {
        super(lvl, x, y, z);
        this.setSize(2, 2);
        this.gravity = 0.0F;
        this.lifetime = 15;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        float fogBefore = RenderSystem.getShaderFogEnd();
        RenderSystem.setShaderFogEnd(40);
        float f = ((float) this.age + partialTick) / (float) this.lifetime;
        float initalFlip = Math.min(f, 0.1F) / 0.1F;
        float laterFlip = Mth.clamp(f - 0.1F, 0F, 0.1F) / 0.1F;
        float scale = 1;
        PoseStack posestack = new PoseStack();
        posestack.mulPose(camera.rotation());
        posestack.translate(0.0D, -1, 0);
        posestack.mulPose(Vector3f.XP.rotationDegrees(10F - laterFlip * 35F));
        posestack.scale(-scale, -scale, scale);
        posestack.translate(0.0D, 0.5F, 2 + (1F - initalFlip));
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = multibuffersource$buffersource.getBuffer(AMRenderTypes.getFreddy(RenderGrizzlyBear.TEXTURE_FREDDY));
        posestack.mulPose(Vector3f.XP.rotationDegrees(initalFlip * 20F - 5F));
        float swing = laterFlip * (float) Math.sin((age + partialTick) * 0.3F) * 20;
        posestack.mulPose(Vector3f.ZP.rotationDegrees((1F - initalFlip) * 45F + swing));
        boolean baby = this.model.young;
        this.model.young = false;
        this.model.positionForParticle(partialTick, age + partialTick);
        this.model.renderToBuffer(posestack, vertexconsumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        this.model.young = baby;
        multibuffersource$buffersource.endBatch();
        RenderSystem.setShaderFogEnd(fogBefore);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleBearFreddy(worldIn, x, y, z);
        }
    }
}