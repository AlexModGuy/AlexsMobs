package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleSkulkBoom extends Particle {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/particle/skulk_boom.png");
    private float size;
    private float prevSize;
    private float prevAlpha;
    private float alphaDecrease;


    private ParticleSkulkBoom(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.setSize(1, 0.1F);
        this.alpha = 1F;
        this.gravity = 0.0F;
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.lifetime = 20 + this.random.nextInt(20);
        this.alphaDecrease = 1F / (float)Math.max(this.lifetime, 1F);
        this.size = 0.3F;
    }

    public void tick(){
        super.tick();
        this.prevSize = size;
        this.prevAlpha = alpha;
        this.size += 0.3F;
        this.xd *= 0.1D;
        this.yd *= 0.8D;
        this.zd *= 0.1D;
        if(this.alpha > 0.0F){
            this.alpha = Math.max(this.alpha - alphaDecrease, 0.0F);
        }
        this.setSize(1 + size, 0.1F);
    }
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)partialTick, this.zo, this.z) - vec3.z());
        Quaternion quaternion = Vector3f.XP.rotationDegrees(90F);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer portalStatic = multibuffersource$buffersource.getBuffer(AMRenderTypes.getSkulkBoom());
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = prevSize + partialTick * (size - prevSize);
        float alphaLerp = prevAlpha + partialTick * (alpha - prevAlpha);
        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }
        float f7 = 0;
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        int j = 240;
        portalStatic.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).color(this.rCol, this.gCol, this.bCol, alphaLerp).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).color(this.rCol, this.gCol, this.bCol, alphaLerp).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).color(this.rCol, this.gCol, this.bCol, alphaLerp).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).color(this.rCol, this.gCol, this.bCol, alphaLerp).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

        multibuffersource$buffersource.endBatch();
    }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleSkulkBoom(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
