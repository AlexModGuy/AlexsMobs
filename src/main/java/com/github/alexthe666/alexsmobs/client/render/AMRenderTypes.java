package com.github.alexthe666.alexsmobs.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;

import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;

public class AMRenderTypes extends RenderType {

    public static final RenderType RAINBOW_GLINT = create("rainbow_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, true, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_glint.png"), true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));

    protected static final RenderStateShard.TransparencyStateShard WORM_TRANSPARANCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderStateShard.TransparencyStateShard MIMICUBE_TRANSPARANCY = new RenderStateShard.TransparencyStateShard("mimicube_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderStateShard.TransparencyStateShard GHOST_TRANSPARANCY = new RenderStateShard.TransparencyStateShard("translucent_ghost_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public AMRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static RenderType getTransparentMimicube(ResourceLocation texture) {
        RenderType.CompositeState lvt_1_1_ = RenderType.CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setCullState(RenderStateShard.NO_CULL).createCompositeState(true);
        return create("mimicube", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, lvt_1_1_);
    }

    public static RenderType getEyesFlickering(ResourceLocation p_228652_0_, float lightLevel) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("eye_flickering", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType getFullBright(ResourceLocation p_228652_0_) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("full_bright", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }


    public static RenderType getFrilledSharkTeeth(ResourceLocation p_228652_0_) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("sharkteeth", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType getEyesNoCull(ResourceLocation p_228652_0_) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
        return create("eyes_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).createCompositeState(false));
    }

    public static RenderType getSpectreBones(ResourceLocation p_228652_0_) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
        return create("spectre_bones", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).createCompositeState(false));
    }


    public static RenderType getGhost(ResourceLocation p_228652_0_) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
        return create("ghost_am", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).setTransparencyState(GHOST_TRANSPARANCY).setCullState(RenderStateShard.NO_CULL).createCompositeState(true));
    }

    public static RenderType getEyesAlphaEnabled(ResourceLocation locationIn) {
        RenderStateShard.TextureStateShard renderstate$texturestate = new RenderStateShard.TextureStateShard(locationIn, false, false);
        return create("eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(renderstate$texturestate).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(WORM_TRANSPARANCY).setWriteMaskState(COLOR_WRITE).setCullState(RenderStateShard.NO_CULL).createCompositeState(false));
    }

    public static RenderType getMungusBeam(ResourceLocation guardianBeamTexture) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(guardianBeamTexture, false, false);
        return create("mungus_beam", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).setTransparencyState(GHOST_TRANSPARANCY).setCullState(RenderStateShard.NO_CULL).createCompositeState(true));
    }
}
