package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotPart;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class RenderCachalotWhale extends MobRenderer<EntityCachalotWhale, ModelCachalotWhale> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_sleeping.png");
    private static final ResourceLocation TEXTURE_ALBINO = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_albino.png");
    private static final ResourceLocation TEXTURE_ALBINO_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_albino_sleeping.png");

    public RenderCachalotWhale(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelCachalotWhale(), 4.2F);
    }

    protected void preRenderCallback(EntityCachalotWhale entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }

    public boolean shouldRender(EntityCachalotWhale livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            for(EntityCachalotPart part : livingEntityIn.whaleParts){
                if(camera.isBoundingBoxInFrustum(part.getBoundingBox())){
                    return true;
                }
            }
            return false;
        }
    }


    public ResourceLocation getEntityTexture(EntityCachalotWhale entity) {
        return TEXTURE;
    }
}
