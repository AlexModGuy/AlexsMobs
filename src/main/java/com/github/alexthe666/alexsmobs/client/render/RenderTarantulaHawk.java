package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelTarantulaHawk;
import com.github.alexthe666.alexsmobs.client.model.ModelTarantulaHawkBaby;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

public class RenderTarantulaHawk extends MobRenderer<EntityTarantulaHawk, EntityModel<EntityTarantulaHawk>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_angry.png");
    private static final ResourceLocation TEXTURE_NETHER = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_nether.png");
    private static final ResourceLocation TEXTURE_NETHER_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_nether_angry.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexsmobs:textures/entity/tarantula_hawk_baby.png");
    private static final ModelTarantulaHawk MODEL = new ModelTarantulaHawk();
    private static final ModelTarantulaHawkBaby MODEL_BABY = new ModelTarantulaHawkBaby();
    public RenderTarantulaHawk(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, MODEL, 0.5F);
    }

    protected void preRenderCallback(EntityTarantulaHawk entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        if(entitylivingbaseIn.isChild()){
            this.entityModel = MODEL_BABY;
        }else{
            this.entityModel = MODEL;
            matrixStackIn.scale(0.9F, 0.9F, 0.9F);
            float f = entitylivingbaseIn.prevDragProgress + (entitylivingbaseIn.dragProgress - entitylivingbaseIn.prevDragProgress) * partialTickTime;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f * 180 * 0.2F));
        }
    }

    protected boolean func_230495_a_(EntityTarantulaHawk hawk) {
        return hawk.isScared();
    }

    @Nullable
    @Override
    protected RenderType func_230496_a_(EntityTarantulaHawk hawk, boolean b0, boolean b1, boolean b2) {
        ResourceLocation resourcelocation = this.getEntityTexture(hawk);
        if (b1) {
            return RenderType.getItemEntityTranslucentCull(resourcelocation);
        } else if (b0) {
            return RenderType.getEntityTranslucent(resourcelocation);
        } else {
            return b2 ? RenderType.getOutline(resourcelocation) : null;
        }
    }

    public ResourceLocation getEntityTexture(EntityTarantulaHawk entity) {
        return entity.isChild() ? TEXTURE_BABY : entity.isNether() ? entity.isAngry() ? TEXTURE_NETHER_ANGRY : TEXTURE_NETHER : entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
