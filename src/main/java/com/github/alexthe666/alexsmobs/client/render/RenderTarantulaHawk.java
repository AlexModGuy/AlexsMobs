package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTarantulaHawk;
import com.github.alexthe666.alexsmobs.client.model.ModelTarantulaHawkBaby;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

import javax.annotation.Nullable;

public class RenderTarantulaHawk extends MobRenderer<EntityTarantulaHawk, EntityModel<EntityTarantulaHawk>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_angry.png");
    private static final ResourceLocation TEXTURE_NETHER = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_nether.png");
    private static final ResourceLocation TEXTURE_NETHER_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_nether_angry.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_baby.png");
    private static final ModelTarantulaHawk MODEL = new ModelTarantulaHawk();
    private static final ModelTarantulaHawkBaby MODEL_BABY = new ModelTarantulaHawkBaby();
    public RenderTarantulaHawk(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, MODEL, 0.5F);
    }

    protected void scale(EntityTarantulaHawk entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        if(entitylivingbaseIn.isBaby()){
            this.model = MODEL_BABY;
        }else{
            this.model = MODEL;
            matrixStackIn.scale(0.9F, 0.9F, 0.9F);
            float f = entitylivingbaseIn.prevDragProgress + (entitylivingbaseIn.dragProgress - entitylivingbaseIn.prevDragProgress) * partialTickTime;
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f * 180 * 0.2F));
        }
    }

    protected boolean isShaking(EntityTarantulaHawk hawk) {
        return hawk.isScared();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EntityTarantulaHawk hawk, boolean b0, boolean b1, boolean b2) {
        ResourceLocation resourcelocation = this.getTextureLocation(hawk);
        if (b1) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (b0) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return b2 ? RenderType.outline(resourcelocation) : null;
        }
    }

    public ResourceLocation getTextureLocation(EntityTarantulaHawk entity) {
        return entity.isBaby() ? TEXTURE_BABY : entity.isNether() ? entity.isAngry() ? TEXTURE_NETHER_ANGRY : TEXTURE_NETHER : entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
