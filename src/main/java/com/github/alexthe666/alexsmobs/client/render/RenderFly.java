package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFly;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;

public class RenderFly extends MobRenderer<EntityFly, ModelFly> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/fly.png");

    public RenderFly(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelFly(), 0.2F);
    }

    protected void preRenderCallback(EntityFly entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
    }

    protected boolean func_230495_a_(EntityFly fly) {
        return fly.isInNether();
    }

    protected void applyRotations(EntityFly entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.func_230495_a_(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.ticksExisted * 7F) * Math.PI * (double)0.9F);
            float vibrate = 0.05F;
            matrixStackIn.translate((entityLiving.getRNG().nextFloat() - 0.5F)* vibrate, (entityLiving.getRNG().nextFloat() - 0.5F) * vibrate, (entityLiving.getRNG().nextFloat() - 0.5F)* vibrate);
        }
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    public ResourceLocation getEntityTexture(EntityFly entity) {
        return TEXTURE;
    }
}
