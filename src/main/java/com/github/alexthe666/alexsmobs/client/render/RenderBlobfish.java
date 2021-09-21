package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelBlobfish;
import com.github.alexthe666.alexsmobs.client.model.ModelBlobfishDepressurized;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderBlobfish extends MobRenderer<EntityBlobfish, EntityModel<EntityBlobfish>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/blobfish.png");
    private static final ResourceLocation TEXTURE_DEPRESSURIZED = new ResourceLocation("alexsmobs:textures/entity/blobfish_depressurized.png");
    private final ModelBlobfish modelFish = new ModelBlobfish();
    private final ModelBlobfishDepressurized modelDepressurized = new ModelBlobfishDepressurized();

    public RenderBlobfish(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelBlobfish(), 0.35F);
    }

    protected void scale(EntityBlobfish entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        if(entitylivingbaseIn.isDepressurized()){
            model = modelDepressurized;
        }else{
            model = modelFish;
        }
        matrixStackIn.scale(entitylivingbaseIn.getBlobfishScale(), entitylivingbaseIn.getBlobfishScale(), entitylivingbaseIn.getBlobfishScale());
    }


    public ResourceLocation getTextureLocation(EntityBlobfish entity) {
        return entity.isDepressurized() ? TEXTURE_DEPRESSURIZED : TEXTURE;
    }
}
