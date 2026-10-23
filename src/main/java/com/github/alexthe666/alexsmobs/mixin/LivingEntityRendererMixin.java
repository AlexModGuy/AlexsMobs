package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.client.render.AMColorUtil;
import com.github.alexthe666.alexsmobs.client.render.RenderFarseer;
import com.github.alexthe666.alexsmobs.client.render.RenderTiger;
import com.github.alexthe666.alexsmobs.client.render.RenderUnderminer;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), index = 2)
    private int modifyColorForTigerStealth(int color, @Local(argsOnly = true) T entity, @Local(argsOnly = true, ordinal = 1) float partialTicks) {
        if (entity instanceof EntityTiger tiger && (Object) this instanceof RenderTiger && tiger.isStealth()) {
            float stealthLevel = tiger.prevStealthProgress
                + (tiger.stealthProgress - tiger.prevStealthProgress) * partialTicks;

            this.shadowRadius = 0.6F * (1 - stealthLevel * 0.1F);
            return AMColorUtil.packColor(1.0F, 1.0F, 1.0F, color != -1 ? 0.15F : Mth.clamp(1 - stealthLevel * 0.1F, 0, 1));
        }

        return color;
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType storeRenderType(RenderType original, @Share("renderType") LocalRef<RenderType> renderTypeRef) {
        renderTypeRef.set(original);
        return original;
    }

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private void useAlternativeRenderPathForUnderminer(EntityModel instance, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, @Local(argsOnly = true) T entity, @Local(argsOnly = true, ordinal = 1) float partialTicks, @Share("renderType") LocalRef<RenderType> renderTypeRef, @Local(argsOnly = true) MultiBufferSource buffers) {
        if (entity instanceof EntityUnderminer underminer && (Object) this instanceof RenderUnderminer renderUnderminer) {
            if (!underminer.isFullyHidden()) {
                float hide = (underminer.prevHidingProgress
                    + (underminer.hidingProgress - underminer.prevHidingProgress) * partialTicks) * 0.1F;
                float alpha = (1F - hide) * 0.6F;
                this.shadowRadius = 0.9F * alpha;
                renderUnderminer.renderUnderminerModel(poseStack, buffers, renderTypeRef.get(), partialTicks, light, overlay, alpha, underminer);
            } else {
                this.shadowRadius = 0;
            }
        } else {
            original.call(instance, poseStack, vertexConsumer, light, overlay, color);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"))
    private void rotateFarseerToCamera(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (entity instanceof EntityFarseer farseer) {
            float faceCameraAmount = farseer.getFacingCameraAmount(partialTicks);
            Quaternionf camera = this.entityRenderDispatcher.cameraOrientation();

            if (faceCameraAmount != 0) {
                poseStack.mulPose(camera);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            }
        }
    }

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
    private void setupFarseerModelAnims(EntityModel<T> instance, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original) {
        original.call(instance, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity instanceof EntityFarseer farseer && (Object) this instanceof RenderFarseer renderFarseer) {
            renderFarseer.setupFarseerAnims(farseer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private void useAlternativeRenderPathForFarseer(EntityModel instance, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, @Local(argsOnly = true) T entity, @Local(argsOnly = true, ordinal = 1) float partialTicks, @Local(argsOnly = true) MultiBufferSource buffers, @Share("renderType") LocalRef<RenderType> renderType) {
        if (entity instanceof EntityFarseer farseer && (Object) this instanceof RenderFarseer renderFarseer && renderType.get() != null) {
            float portalLevel = farseer.getFarseerOpacity(partialTicks);
            this.shadowRadius = 0.9F * portalLevel;

            renderFarseer.renderFarseerModel(poseStack, buffers, renderType.get(), partialTicks, light, overlay, color != -1 ? 0.15F : Mth.clamp(portalLevel, 0, 1), farseer);
        } else {
            original.call(instance, poseStack, vertexConsumer, light, overlay, color);
        }
    }
}
