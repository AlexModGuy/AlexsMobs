package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFarseer;
import com.github.alexthe666.alexsmobs.client.model.ModelUnderminerDwarf;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerUnderminerItem;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.EntityToucan;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderNameTagEvent;

import javax.annotation.Nullable;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class RenderUnderminer extends MobRenderer<EntityUnderminer, EntityModel<EntityUnderminer>> {
    private static final ResourceLocation TEXTURE_DWARF = new ResourceLocation("alexsmobs:textures/entity/underminer_dwarf.png");
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/underminer_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/underminer_1.png");

    private static final ModelUnderminerDwarf DWARF_MODEL = new ModelUnderminerDwarf();
    private static HumanoidModel<EntityUnderminer> NORMAL_MODEL = null;

    public RenderUnderminer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, DWARF_MODEL, 0.4F);
        NORMAL_MODEL = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.UNDERMINER));
        this.addLayer(new LayerUnderminerItem(this));
    }

    protected void scale(EntityUnderminer entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.925F, 0.925F, 0.925F);
    }

    protected float getFlipDegrees(EntityUnderminer entityUnderminer) {
        return 0.0F;
    }

    public void render(EntityUnderminer entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<EntityUnderminer, EntityModel<EntityUnderminer>>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn)))
            return;
        matrixStackIn.pushPose();
        this.model.attackTime = this.getAttackAnim(entityIn, partialTicks);

        boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = entityIn.isBaby();
        float f = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float f1 = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entityIn.getVehicle();
            f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        if (entityIn.getPose() == Pose.SLEEPING) {
            Direction direction = entityIn.getBedOrientation();
            if (direction != null) {
                float f4 = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStackIn.translate((float) (-direction.getStepX()) * f4, 0.0D, (float) (-direction.getStepZ()) * f4);
            }
        }

        float f7 = this.getBob(entityIn, partialTicks);
        this.setupRotations(entityIn, matrixStackIn, f7, f, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0.0D, -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = Mth.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
            f5 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
            if (entityIn.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }
        if(entityIn.isDwarf()){
            this.model = DWARF_MODEL;
        }else{
            this.model = NORMAL_MODEL;
        }
        this.model.prepareMobModel(entityIn, f5, f8, partialTicks);
        this.model.setupAnim(entityIn, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(entityIn);
        boolean flag1 = !flag && !entityIn.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entityIn);
        RenderType rendertype = this.getRenderType(entityIn, flag, flag1, flag2);
        if (rendertype != null) {
            float alpha = 0.6F;
            this.shadowRadius = 0.9F * alpha;
            int i = getOverlayCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));
            this.renderUnderminerModel(matrixStackIn, bufferIn, rendertype, partialTicks, packedLightIn, i, flag1 ? 0.15F : Mth.clamp(alpha, 0, 1), entityIn);
        }
        if (!entityIn.isSpectator()) {
            for (RenderLayer layerrenderer : this.layers) {
                layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
            }
        }

        matrixStackIn.popPose();
        RenderNameTagEvent renderNameplateEvent = new RenderNameTagEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
        if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(entityIn))) {
            this.renderNameTag(entityIn, renderNameplateEvent.getContent(), matrixStackIn, bufferIn, packedLightIn);
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<EntityUnderminer, EntityModel<EntityUnderminer>>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
    }

    private void renderUnderminerModel(PoseStack matrixStackIn, MultiBufferSource source, RenderType defRenderType, float partialTicks, int packedLightIn, int overlayColors, float alphaIn, EntityUnderminer entityIn) {
        boolean hurt = Math.max(entityIn.hurtTime, entityIn.deathTime) > 0;
        this.model.renderToBuffer(matrixStackIn, source.getBuffer(defRenderType), packedLightIn, LivingEntityRenderer.getOverlayCoords(entityIn, 0.0F), hurt ? 0.4F : 1.0F, hurt ? 0.8F : 1.0F, hurt ? 0.7F : 1.0F, alphaIn);
    }


    @Nullable
    protected RenderType getRenderType(EntityUnderminer farseer, boolean normal, boolean invis, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(farseer);
        return outline ? RenderType.outline(resourcelocation) : AMRenderTypes.getUnderminer(resourcelocation);
    }

    public ResourceLocation getTextureLocation(EntityUnderminer entity) {
        return entity.isDwarf() ? TEXTURE_DWARF : entity.getVariant() == 0 ? TEXTURE_0 : TEXTURE_1;
    }

}
