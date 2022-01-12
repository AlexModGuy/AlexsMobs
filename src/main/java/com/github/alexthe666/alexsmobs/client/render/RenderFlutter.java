package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelFlutter;
import com.github.alexthe666.alexsmobs.client.model.ModelFlutterPotted;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerBasicGlow;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderFlutter extends MobRenderer<EntityFlutter, EntityModel<EntityFlutter>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/flutter.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/flutter_eyes.png");
    private final ModelFlutter modelFlutter = new ModelFlutter();
    private final ModelFlutterPotted modelPotted = new ModelFlutterPotted();

    public RenderFlutter(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFlutter(), 0.25F);
        this.addLayer(new LayerBasicGlow<>(this, TEXTURE_EYES));
    }

    protected void scale(EntityFlutter entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        if(entitylivingbaseIn.isPotted()){
            model = modelPotted;
        }else{
            model = modelFlutter;
        }
    }


    public ResourceLocation getTextureLocation(EntityFlutter entity) {
        return TEXTURE;
    }
}
