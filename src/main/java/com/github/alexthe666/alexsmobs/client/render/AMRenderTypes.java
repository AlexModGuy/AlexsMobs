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

    protected static final RenderStateShard.TexturingStateShard RAINBOW_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("rainbow_glint_texturing", () -> {
        setupRainbowRendering(0F);
    }, () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });


    protected static final RenderStateShard.TexturingStateShard FRILLED_SHARK_TEETH_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("frilled_shark_teeth_glint_texturing", () -> {
        setupFrilledSharkGlintTexturing(8.0F);
    }, () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);

    });


    public static final RenderType RAINBOW_GLINT = create("rainbow_glint", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_glint.png"), true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RAINBOW_GLINT_TEXTURING).setDiffuseLightingState(DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setShadeModelState(SMOOTH_SHADE).createCompositeState(false));

    public static final RenderType FRILLED_SHARK_TEETH_GLINT =  create("frilled_shark_glint", DefaultVertexFormat.POSITION_TEX, 7, 256, RenderType.CompositeState.builder().setTextureState(new TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));

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

    public AMRenderTypes(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
    }

    public static RenderType getTransparentMimicube(ResourceLocation texture) {
        RenderType.CompositeState lvt_1_1_ = RenderType.CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setCullState(RenderStateShard.NO_CULL).createCompositeState(true);
        return create("mimicube", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, lvt_1_1_);
    }

    public static RenderType getEyesFlickering(ResourceLocation p_228652_0_, float lightLevel) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("eye_flickering", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType getFullBright(ResourceLocation p_228652_0_) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("full_bright", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }


    public static RenderType getFrilledSharkTeeth(ResourceLocation p_228652_0_) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("sharkteeth", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setDiffuseLightingState(NO_DIFFUSE_LIGHTING).setTransparencyState(NO_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType getEyesNoCull(ResourceLocation p_228652_0_) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
        return create("eyes_no_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setFogState(BLACK_FOG).createCompositeState(false));
    }

    public static RenderType getSpectreBones(ResourceLocation p_228652_0_) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
        return create("spectre_bones", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING).setFogState(BLACK_FOG).setCullState(NO_CULL).createCompositeState(false));
    }


    public static RenderType getGhost(ResourceLocation p_228652_0_) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
        return create("ghost_am", DefaultVertexFormat.NEW_ENTITY, 7, 262144, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setWriteMaskState(COLOR_DEPTH_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setAlphaState(DEFAULT_ALPHA).setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).setTransparencyState(GHOST_TRANSPARANCY).setFogState(FOG).setCullState(RenderStateShard.NO_CULL).createCompositeState(true));
    }

    public static RenderType getSnappingTurtleMoss(ResourceLocation LocationIn, float alphaIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(LocationIn, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(new RenderStateShard.AlphaStateShard(alphaIn)).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("snapping_turtle_moss", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, rendertype$state);
    }


    private static void setupRainbowRendering(float scaleIn) {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.getMillis() * 8L;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 10000L) / 10000.0F;
        RenderSystem.translatef(0, f1, 0.0F);
        RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.scalef(scaleIn, scaleIn, scaleIn);
        RenderSystem.matrixMode(5888);
    }

    private static void setupFrilledSharkGlintTexturing(float scaleIn) {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.getMillis() * 64L;
        float f1 = (float)(i % 110000) / 110000.0F;
        RenderSystem.translatef(f1, f1, 0.0F);
        RenderSystem.rotatef(0, 0.0F, 0.0F, 1.0F);
        RenderSystem.scalef(scaleIn, scaleIn, scaleIn);
        RenderSystem.matrixMode(5888);
    }

    public static RenderType getEyesAlphaEnabled(ResourceLocation locationIn) {
        RenderStateShard.TextureStateShard renderstate$texturestate = new RenderStateShard.TextureStateShard(locationIn, false, false);
        return create("eyes", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(renderstate$texturestate).setTransparencyState(WORM_TRANSPARANCY).setWriteMaskState(COLOR_WRITE).setCullState(RenderStateShard.NO_CULL).setFogState(BLACK_FOG).createCompositeState(false));
    }

    public static RenderType getMungusBeam(ResourceLocation guardianBeamTexture) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(guardianBeamTexture, false, false);
        return create("mungus_beam", DefaultVertexFormat.NEW_ENTITY, 7, 262144, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setWriteMaskState(COLOR_DEPTH_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setAlphaState(DEFAULT_ALPHA).setDiffuseLightingState(RenderStateShard.NO_DIFFUSE_LIGHTING).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).setTransparencyState(GHOST_TRANSPARANCY).setFogState(FOG).setCullState(RenderStateShard.NO_CULL).createCompositeState(true));
    }
}
