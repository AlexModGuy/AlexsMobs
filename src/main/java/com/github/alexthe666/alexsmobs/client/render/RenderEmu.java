package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEmu;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderEmu extends MobRenderer<EntityEmu, ModelEmu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/emu.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexsmobs:textures/entity/emu_baby.png");
    private static final ResourceLocation TEXTURE_BLONDE = new ResourceLocation("alexsmobs:textures/entity/emu_blonde.png");
    private static final ResourceLocation TEXTURE_BLONDE_BABY = new ResourceLocation("alexsmobs:textures/entity/emu_baby_blonde.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("alexsmobs:textures/entity/emu_blue.png");

    public RenderEmu(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelEmu(), 0.45F);
    }

    protected void scale(EntityEmu entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }


    public ResourceLocation getTextureLocation(EntityEmu entity) {
        if(entity.getVariant() == 2){
            return entity.isBaby() ? TEXTURE_BLONDE_BABY : TEXTURE_BLONDE;
        }
        if(entity.getVariant() == 1 && !entity.isBaby()){
            return  TEXTURE_BLUE;
        }
        return entity.isBaby() ? TEXTURE_BABY : TEXTURE;
    }
}
