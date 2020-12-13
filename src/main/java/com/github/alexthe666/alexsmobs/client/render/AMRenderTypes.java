package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class AMRenderTypes extends RenderType{

    public AMRenderTypes(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }

    public static RenderType getEyesFlickering(ResourceLocation p_228652_0_, float lightLevel) {
        RenderState.TextureState lvt_1_1_ = new RenderState.TextureState(p_228652_0_, false, false);
        return makeType("eye_flickering", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(lvt_1_1_).transparency(TRANSLUCENT_TRANSPARENCY).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(false));
    }

}
