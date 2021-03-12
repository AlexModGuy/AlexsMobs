package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelEmu;
import com.github.alexthe666.alexsmobs.client.model.ModelLobster;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderEmu extends MobRenderer<EntityEmu, ModelEmu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/emu.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexsmobs:textures/entity/emu_baby.png");
    private static final ResourceLocation TEXTURE_BLONDE = new ResourceLocation("alexsmobs:textures/entity/emu_blonde.png");
    private static final ResourceLocation TEXTURE_BLONDE_BABY = new ResourceLocation("alexsmobs:textures/entity/emu_baby_blonde.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("alexsmobs:textures/entity/emu_blue.png");

    public RenderEmu(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelEmu(), 0.45F);
    }

    protected void preRenderCallback(EntityEmu entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);
    }


    public ResourceLocation getEntityTexture(EntityEmu entity) {
        if(entity.getVariant() == 2){
            return entity.isChild() ? TEXTURE_BLONDE_BABY : TEXTURE_BLONDE;
        }
        if(entity.getVariant() == 1 && !entity.isChild()){
            return  TEXTURE_BLUE;
        }
        return entity.isChild() ? TEXTURE_BABY : TEXTURE;
    }
}
