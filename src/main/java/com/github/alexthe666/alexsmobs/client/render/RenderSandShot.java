package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.entity.EntityGust;
import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import com.github.alexthe666.alexsmobs.entity.EntitySandShot;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.LlamaSpitModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class RenderSandShot extends EntityRenderer<EntitySandShot> {
    private static final ResourceLocation SAND_SHOT = new ResourceLocation("alexsmobs:textures/entity/sand_shot.png");
    private final LlamaSpitModel<LlamaSpitEntity> model = new LlamaSpitModel<>();

    public RenderSandShot(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(EntitySandShot entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, (double)0.15F, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch)));
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
        int i = EntityGuster.getColorForVariant(entityIn.getVariant());
        float r = (float) (i >> 16 & 255) / 255.0F;
        float g = (float) (i >> 8 & 255) / 255.0F;
        float b = (float) (i & 255) / 255.0F;
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(SAND_SHOT));
        this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public ResourceLocation getEntityTexture(EntitySandShot entity) {
        return SAND_SHOT;
    }
}
