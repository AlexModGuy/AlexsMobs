package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBlobfish;
import com.github.alexthe666.alexsmobs.client.model.ModelBlobfishDepressurized;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

public class RenderBlobfish extends MobRenderer<EntityBlobfish, EntityModel<EntityBlobfish>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/blobfish.png");
    private static final ResourceLocation TEXTURE_DEPRESSURIZED = new ResourceLocation("alexsmobs:textures/entity/blobfish_depressurized.png");
    private final ModelBlobfish model = new ModelBlobfish();
    private final ModelBlobfishDepressurized modelDepressurized = new ModelBlobfishDepressurized();

    public RenderBlobfish(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelBlobfish(), 0.35F);
    }

    protected void preRenderCallback(EntityBlobfish entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        if(entitylivingbaseIn.isDepressurized()){
            entityModel = modelDepressurized;
        }else{
            entityModel = model;
        }
        matrixStackIn.scale(entitylivingbaseIn.getBlobfishScale(), entitylivingbaseIn.getBlobfishScale(), entitylivingbaseIn.getBlobfishScale());
    }


    public ResourceLocation getEntityTexture(EntityBlobfish entity) {
        return entity.isDepressurized() ? TEXTURE_DEPRESSURIZED : TEXTURE;
    }
}
