package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTiger;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBody;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormTail;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.LightType;

public class RenderVoidWormBody extends LivingRenderer<EntityVoidWormPart, EntityModel<EntityVoidWormPart>> {
    private static final ResourceLocation TEXTURE_BODY = new ResourceLocation("alexsmobs:textures/entity/void_worm_body.png");
    private static final ResourceLocation TEXTURE_BODY_HURT = new ResourceLocation("alexsmobs:textures/entity/void_worm_body_hurt.png");
    private static final ResourceLocation TEXTURE_BODY_GLOW = new ResourceLocation("alexsmobs:textures/entity/void_worm_body_glow.png");
    private static final ResourceLocation TEXTURE_TAIL = new ResourceLocation("alexsmobs:textures/entity/void_worm_tail.png");
    private static final ResourceLocation TEXTURE_TAIL_HURT = new ResourceLocation("alexsmobs:textures/entity/void_worm_tail_hurt.png");
    private static final ResourceLocation TEXTURE_TAIL_GLOW = new ResourceLocation("alexsmobs:textures/entity/void_worm_tail_glow.png");
    private ModelVoidWormBody bodyModel = new ModelVoidWormBody();
    private ModelVoidWormTail tailModel = new ModelVoidWormTail();

    public RenderVoidWormBody(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelVoidWormBody(), 1F);
        this.addLayer(new LayerGlow(this));
    }

    public ResourceLocation getEntityTexture(EntityVoidWormPart entity) {
        if(entity.isHurt()){
            return entity.isTail() ? TEXTURE_TAIL_HURT : TEXTURE_BODY_HURT;
        }else{
            return entity.isTail() ? TEXTURE_TAIL : TEXTURE_BODY;
        }
    }

    protected void applyRotations(EntityVoidWormPart entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        Pose pose = entityLiving.getPose();
        if (pose != Pose.SLEEPING) {
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - entityLiving.getWormYaw(partialTicks)));
        }
        if (entityLiving.deathTime > 0) {
            float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
        }

    }

    protected void preRenderCallback(EntityVoidWormPart entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        this.entityModel = entitylivingbaseIn.isTail() ? tailModel : bodyModel;
        matrixStackIn.scale(entitylivingbaseIn.getWormScale(), entitylivingbaseIn.getWormScale(), entitylivingbaseIn.getWormScale());
    }

    protected boolean canRenderName(EntityVoidWormPart entity) {
        return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
    }


    class LayerGlow extends LayerRenderer<EntityVoidWormPart, EntityModel<EntityVoidWormPart>> {

        public LayerGlow(RenderVoidWormBody render) {
            super(render);
        }

        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityVoidWormPart worm, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if(!worm.isHurt()){
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesAlphaEnabled(worm.isTail() ? TEXTURE_TAIL_GLOW : TEXTURE_BODY_GLOW));
                float alpha = (float)MathHelper.clamp((worm.getHealth() - worm.getHealthThreshold()) / (worm.getMaxHealth() - worm.getHealthThreshold()), 0, 1F);
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(worm, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }
}
