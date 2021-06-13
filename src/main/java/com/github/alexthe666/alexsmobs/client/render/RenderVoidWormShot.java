package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormShot;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderVoidWormShot extends EntityRenderer<EntityVoidWormShot> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm_shot.png");
    private static final ResourceLocation TEXTURE_PORTAL = new ResourceLocation("alexsmobs:textures/entity/void_worm_shot_portal.png");
    private static ModelVoidWormShot MODEL = new ModelVoidWormShot();

    public RenderVoidWormShot(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityVoidWormShot entity) {
        return entity.isPortalType() ? TEXTURE_PORTAL : TEXTURE;
    }

    @Override
    public void render(EntityVoidWormShot entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.rotate(new Quaternion(Vector3f.XP, 180F, true));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw)));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)));
        matrixStackIn.push();
        MODEL.animate(entityIn, entityIn.ticksExisted + partialTicks);
        matrixStackIn.translate(0, -1.5F, 0);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getFullBright(getEntityTexture(entityIn)));
        MODEL.render(matrixStackIn, ivertexbuilder, 210, NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
        matrixStackIn.pop();


    }

}
