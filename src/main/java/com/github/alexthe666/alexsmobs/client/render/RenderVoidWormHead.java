package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWorm;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerVoidWormGlow;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class RenderVoidWormHead extends MobRenderer<EntityVoidWorm, ModelVoidWorm> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm/void_worm_head.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/void_worm/void_worm_head_glow.png");

    public RenderVoidWormHead(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelVoidWorm(0.0f), 1F);
        this.addLayer(new LayerVoidWormGlow(this, renderManagerIn.getResourceManager(), new ModelVoidWorm(1.001F)){
            public ResourceLocation getGlowTexture(LivingEntity worm){
                return TEXTURE_GLOW;
            }
            public boolean isGlowing(LivingEntity worm){
                return true;
            }
            public float getAlpha(LivingEntity livingEntity){
                return 1.0F;
            }
        });
    }

    @Nullable
    protected RenderType getRenderType(EntityVoidWorm jelly, boolean normal, boolean invis, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(jelly);
        if (invis) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    public boolean shouldRender(EntityVoidWorm worm, Frustum camera, double camX, double camY, double camZ) {
        return worm.getPortalTicks() <= 0 && super.shouldRender(worm, camera, camX, camY, camZ);
    }

    public ResourceLocation getTextureLocation(EntityVoidWorm entity) {
        return TEXTURE;
    }
}
