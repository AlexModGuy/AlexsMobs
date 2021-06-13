package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class RenderVoidPortal extends EntityRenderer<EntityVoidPortal> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/portal/portal_idle_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/portal/portal_idle_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobs:textures/entity/portal/portal_idle_2.png");
    private static final ResourceLocation[] TEXTURE_PROGRESS = new ResourceLocation[10];
    public RenderVoidPortal(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        for(int i = 0; i < 10; i++){
            TEXTURE_PROGRESS[i] = new ResourceLocation("alexsmobs:textures/entity/portal/portal_grow_" + i + ".png");
        }
    }

    public void render(EntityVoidPortal entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.rotate(entityIn.getAttachmentFacing().getOpposite().getRotation());
        matrixStackIn.translate(0.5D, 0, 0.5D);
        ResourceLocation tex;
        if(entityIn.getLifespan() < 20){
            tex = getGrowingTexture((int) ((entityIn.getLifespan() * 0.5F) % 10));
        }else if(entityIn.ticksExisted < 20){
            tex = getGrowingTexture((int) ((entityIn.ticksExisted * 0.5F) % 10));
        }else{
            tex = getIdleTexture(entityIn.ticksExisted % 9);
        }
        matrixStackIn.scale(2, 2, 2);
        renderArc(matrixStackIn, bufferIn, tex);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private void renderArc(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, ResourceLocation res) {
        matrixStackIn.push();

        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getGhost(res));
        MatrixStack.Entry lvt_19_1_ = matrixStackIn.getLast();
        Matrix4f lvt_20_1_ = lvt_19_1_.getMatrix();
        Matrix3f lvt_21_1_ = lvt_19_1_.getNormal();
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0, 0, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0, 1, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1, 1, 1, 0, 1, 240);
        this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1, 0, 1, 0, 1, 240);
        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityVoidPortal entity) {
        return TEXTURE_0;
    }


    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, IVertexBuilder p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.pos(p_229039_1_, (float) p_229039_4_, (float) p_229039_5_, (float) p_229039_6_).color(255, 255, 255, 255).tex(p_229039_7_, p_229039_8_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229039_12_).normal(p_229039_2_, (float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_).endVertex();
    }


    public ResourceLocation getIdleTexture(int age) {
        if (age < 3) {
            return TEXTURE_0;
        } else if (age < 6) {
            return TEXTURE_1;
        } else if (age < 10) {
            return TEXTURE_2;
        } else {
            return TEXTURE_0;
        }
    }

    public ResourceLocation getGrowingTexture(int age) {
        return TEXTURE_PROGRESS[MathHelper.clamp(age, 0, 9)];
    }
}
