package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotPart;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class RenderCachalotWhale extends MobRenderer<EntityCachalotWhale, ModelCachalotWhale> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_sleeping.png");
    private static final ResourceLocation TEXTURE_ALBINO = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_albino.png");
    private static final ResourceLocation TEXTURE_ALBINO_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_albino_sleeping.png");

    public RenderCachalotWhale(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new ModelCachalotWhale(), 4.2F);
    }

    protected void scale(EntityCachalotWhale entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    public boolean shouldRender(EntityCachalotWhale livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            for(EntityCachalotPart part : livingEntityIn.whaleParts){
                if(camera.isVisible(part.getBoundingBox())){
                    return true;
                }
            }
            return false;
        }
    }


    public ResourceLocation getTextureLocation(EntityCachalotWhale entity) {
        if(entity.isAlbino()){
            return entity.isSleeping() || entity.isBeached() ? TEXTURE_ALBINO_SLEEPING : TEXTURE_ALBINO;
        }else {
            return entity.isSleeping() || entity.isBeached()  ? TEXTURE_SLEEPING : TEXTURE;
        }
    }
}
