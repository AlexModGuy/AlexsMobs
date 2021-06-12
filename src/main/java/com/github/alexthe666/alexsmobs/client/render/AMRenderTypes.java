package com.github.alexthe666.alexsmobs.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class AMRenderTypes extends RenderType {

    protected static final RenderState.TexturingState RAINBOW_GLINT_TEXTURING = new RenderState.TexturingState("rainbow_glint_texturing", () -> {
        setupRainbowRendering(0F);
    }, () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });

    public static final RenderType RAINBOW_GLINT = makeType("rainbow_glint", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, RenderType.State.getBuilder().texture(new RenderState.TextureState(new ResourceLocation("alexsmobs:textures/entity/rainbow_glint.png"), true, false)).writeMask(COLOR_WRITE).cull(CULL_DISABLED).depthTest(DEPTH_EQUAL).transparency(GLINT_TRANSPARENCY).texturing(RAINBOW_GLINT_TEXTURING).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).build(false));


    protected static final RenderState.TransparencyState MIMICUBE_TRANSPARANCY = new RenderState.TransparencyState("mimicube_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderState.TransparencyState GHOST_TRANSPARANCY = new RenderState.TransparencyState("translucent_ghost_transparency", () -> {
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
        RenderType.State lvt_1_1_ = RenderType.State.getBuilder().texture(new TextureState(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).target(TRANSLUCENT_TARGET).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).writeMask(RenderState.COLOR_DEPTH_WRITE).cull(RenderState.CULL_DISABLED).build(true);
        return makeType("mimicube", DefaultVertexFormats.ENTITY, 7, 256, false, true, lvt_1_1_);
    }

    public static RenderType getEyesFlickering(ResourceLocation p_228652_0_, float lightLevel) {
        RenderState.TextureState lvt_1_1_ = new RenderState.TextureState(p_228652_0_, false, false);
        return makeType("eye_flickering", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(lvt_1_1_).transparency(TRANSLUCENT_TRANSPARENCY).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(false));
    }


    public static RenderType getEyesNoCull(ResourceLocation p_228652_0_) {
        TextureState lvt_1_1_ = new TextureState(p_228652_0_, false, false);
        return makeType("eyes_no_cull", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(lvt_1_1_).transparency(ADDITIVE_TRANSPARENCY).writeMask(COLOR_WRITE).cull(CULL_DISABLED).fog(BLACK_FOG).build(false));
    }

    public static RenderType getSpectreBones(ResourceLocation p_228652_0_) {
        TextureState lvt_1_1_ = new TextureState(p_228652_0_, false, false);
        return makeType("spectre_bones", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(lvt_1_1_).transparency(ADDITIVE_TRANSPARENCY).writeMask(COLOR_WRITE).diffuseLighting(RenderState.DIFFUSE_LIGHTING_DISABLED).fog(BLACK_FOG).cull(CULL_DISABLED).build(false));
    }


    public static RenderType getGhost(ResourceLocation p_228652_0_) {
        TextureState lvt_1_1_ = new TextureState(p_228652_0_, false, false);
        return makeType("ghost_am", DefaultVertexFormats.ENTITY, 7, 262144, false, true, RenderType.State.getBuilder().texture(lvt_1_1_).writeMask(COLOR_DEPTH_WRITE).depthTest(DEPTH_LEQUAL).alpha(DEFAULT_ALPHA).diffuseLighting(RenderState.DIFFUSE_LIGHTING_DISABLED).lightmap(LIGHTMAP_DISABLED).overlay(OVERLAY_ENABLED).transparency(GHOST_TRANSPARANCY).fog(FOG).cull(RenderState.CULL_DISABLED).build(true));
    }

    public static RenderType getSnappingTurtleMoss(ResourceLocation LocationIn, float alphaIn) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(LocationIn, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(new RenderState.AlphaState(alphaIn)).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).build(true);
        return makeType("snapping_turtle_moss", DefaultVertexFormats.ENTITY, 7, 256, true, true, rendertype$state);
    }


    private static void setupRainbowRendering(float scaleIn) {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.milliTime() * 8L;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 10000L) / 10000.0F;
        RenderSystem.translatef(0, f1, 0.0F);
        RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.scalef(scaleIn, scaleIn, scaleIn);
        RenderSystem.matrixMode(5888);
    }

    private static void setupSpectreGlintTexturing(float scaleIn) {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.milliTime() * 8L;
        float f1 = (float)(i % 110000) / 110000.0F;
        RenderSystem.translatef(f1, f1, 0.0F);
        RenderSystem.rotatef(0, 0.0F, 0.0F, 1.0F);
        RenderSystem.scalef(scaleIn, scaleIn, scaleIn);
        RenderSystem.matrixMode(5888);
    }

    public static RenderType getEyesAlphaEnabled(ResourceLocation locationIn) {
        RenderState.TextureState renderstate$texturestate = new RenderState.TextureState(locationIn, false, false);
        return makeType("eyes", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(renderstate$texturestate).transparency(LIGHTNING_TRANSPARENCY).writeMask(COLOR_WRITE).fog(BLACK_FOG).build(false));
    }

    public static RenderType getMungusBeam(ResourceLocation guardianBeamTexture) {
        TextureState lvt_1_1_ = new TextureState(guardianBeamTexture, false, false);
        return makeType("mungus_beam", DefaultVertexFormats.ENTITY, 7, 262144, false, true, RenderType.State.getBuilder().texture(lvt_1_1_).writeMask(COLOR_DEPTH_WRITE).depthTest(DEPTH_LEQUAL).alpha(DEFAULT_ALPHA).diffuseLighting(RenderState.DIFFUSE_LIGHTING_DISABLED).lightmap(LIGHTMAP_DISABLED).overlay(OVERLAY_ENABLED).transparency(GHOST_TRANSPARANCY).fog(FOG).cull(RenderState.CULL_DISABLED).build(true));
    }
}
