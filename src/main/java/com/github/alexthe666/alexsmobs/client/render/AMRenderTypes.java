package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class AMRenderTypes extends RenderType {

    public static final ResourceLocation STATIC_TEXTURE = new ResourceLocation("alexsmobs:textures/static.png");

    private static boolean encounteredMultiConsumerError = false;

    protected static final RenderStateShard.TexturingStateShard RAINBOW_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
        setupRainbowTexturing(1.2F, 4L);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });
    protected static final RenderStateShard.TexturingStateShard COMB_JELLY_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
        setupRainbowTexturing(2F, 16L);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });
    protected static final RenderStateShard.TexturingStateShard RAINBOW_TEXTURING_LARGE = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
        setupRainbowTexturing2(5F, 14L);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });
    protected static final RenderStateShard.TexturingStateShard WEEZER_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
        setupRainbowTexturing2(7F, 16L);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });
    protected static final RenderStateShard.TexturingStateShard STATIC_PORTAL_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
        setupStaticTexturing(1.1F, 12L);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });
    protected static final RenderStateShard.TexturingStateShard STATIC_PARTICLE_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
        setupStaticTexturing(0.1F, 12L);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });
    protected static final RenderStateShard.TexturingStateShard STATIC_ENTITY_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
        setupStaticTexturing(3F, 12L);
    }, () -> {
        RenderSystem.resetTextureMatrix();
    });



    public static final RenderType COMBJELLY_RAINBOW_GLINT = create("cj_rainbow_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_rainbow.png"), true, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(NO_TRANSPARENCY).setTexturingState(COMB_JELLY_TEXTURING).createCompositeState(false));
    public static final RenderType RAINBOW_GLINT = create("rainbow_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_rainbow.png"), true, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RAINBOW_TEXTURING).setOverlayState(OVERLAY).createCompositeState(true));
    public static final RenderType TRANS_GLINT = create("trans_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_trans.png"), true, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RAINBOW_TEXTURING).setOverlayState(OVERLAY).createCompositeState(true));
    public static final RenderType NONBI_GLINT = create("nonbi_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_nonbi.png"), true, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RAINBOW_TEXTURING).setOverlayState(OVERLAY).createCompositeState(true));
    public static final RenderType BI_GLINT = create("bi_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_bi.png"), true, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RAINBOW_TEXTURING).setOverlayState(OVERLAY).createCompositeState(true));
    public static final RenderType ACE_GLINT = create("ace_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_ace.png"), true, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RAINBOW_TEXTURING).setOverlayState(OVERLAY).createCompositeState(true));
    public static final RenderType BRAZIL_GLINT = create("brazil_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_brazil.png"), true, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(RAINBOW_TEXTURING_LARGE).setOverlayState(OVERLAY).createCompositeState(true));
    public static final RenderType WEEZER_GLINT = create("weezer_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/rainbow_jelly_overlays/glint_weezer.png"), false, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(WEEZER_TEXTURING).setOverlayState(OVERLAY).createCompositeState(true));
    public static final RenderType STATIC_PORTAL = create("static_portal", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(STATIC_TEXTURE, false, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTexturingState(STATIC_PORTAL_TEXTURING).setOverlayState(OVERLAY).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));
    public static final RenderType STATIC_PARTICLE = create("static_particle", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(STATIC_TEXTURE, false, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTexturingState(STATIC_PARTICLE_TEXTURING).setOverlayState(OVERLAY).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));
    public static final RenderType STATIC_ENTITY = create("static_entity", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(STATIC_TEXTURE, false, false)).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTexturingState(STATIC_ENTITY_TEXTURING).setOverlayState(OVERLAY).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true));
    public static final RenderType VOID_WORM_PORTAL_OVERLAY = create("void_worm_portal_overlay", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_END_PORTAL_SHADER).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(NO_CULL).setTransparencyState(NO_TRANSPARENCY).setTextureState(MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));

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
        RenderType.CompositeState lvt_1_1_ = RenderType.CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY).setOutputState(TRANSLUCENT_TARGET).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST).createCompositeState(true);
        return create("mimicube", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, lvt_1_1_);
    }

    public static RenderType getEyesFlickering(ResourceLocation p_228652_0_, float lightLevel) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("eye_flickering", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType getFullBright(ResourceLocation p_228652_0_) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("full_bright", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType getFreddy(ResourceLocation p_228652_0_) {
        RenderStateShard.TextureStateShard lvt_1_1_ = new RenderStateShard.TextureStateShard(p_228652_0_, false, false);
        return create("freddy", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(RenderStateShard.NO_LIGHTMAP).setCullState(NO_CULL).setOverlayState(OVERLAY).createCompositeState(true));
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
        return create("spectre_bones", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_EYES_SHADER).setTransparencyState(GHOST_TRANSPARANCY).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType getGhost(ResourceLocation p_228652_0_) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(p_228652_0_, false, false);
        return create("ghost_am", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RENDERTYPE_EYES_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE).setDepthTestState(EQUAL_DEPTH_TEST).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).setTransparencyState(GHOST_TRANSPARANCY).setCullState(RenderStateShard.NO_CULL).createCompositeState(true));
    }

    public static RenderType getEyesAlphaEnabled(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false)).setTransparencyState(WORM_TRANSPARANCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setDepthTestState(EQUAL_DEPTH_TEST).createCompositeState(true);
        return create("eye_alpha", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
    }

    public static RenderType getEyesNoFog(ResourceLocation locationIn) {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(locationIn, false, false);
        return create("eyes_nofog", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, true, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_OUTLINE_SHADER).setTextureState(renderstateshard$texturestateshard).setTransparencyState(LIGHTNING_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setOverlayState(OVERLAY).createCompositeState(true));
    }

    public static RenderType getSunbirdShine() {
        return create("sunbird_shine", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("alexsmobs:textures/entity/sunbird_shine.png"), true, true)).setLightmapState(LIGHTMAP).setCullState(RenderStateShard.NO_CULL).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(true));
    }

    public static RenderType getSkulkBoom() {
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new TextureStateShard(new ResourceLocation("alexsmobs:textures/particle/skulk_boom.png"), true, true))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(false);
        return create("skulk_boom", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderState);
    }

    public static RenderType getUnderminer(ResourceLocation texture) {
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLayeringState(NO_LAYERING)
                .createCompositeState(false);
        return create("underminer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderState);
    }


    public static RenderType getGhostPickaxe(ResourceLocation texture) {
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
                .setCullState(NO_CULL)
                .setOutputState(ITEM_ENTITY_TARGET)
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLayeringState(NO_LAYERING)
                .createCompositeState(false);
        return create("ghost_pickaxe", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderState);
    }

    public static RenderType getGhostCrumbling(ResourceLocation texture) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(texture, false, false);
        return create("ghost_crumbling_am", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.CompositeState.builder().setTextureState(lvt_1_1_).setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(RenderStateShard.NO_CULL).createCompositeState(true));
    }

    private static void setupRainbowTexturing(float in, long time) {
        long i = Util.getMillis() * time;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        Matrix4f matrix4f = (new Matrix4f()).translation(0, f1, 0.0F);
        matrix4f.scale(in);
        RenderSystem.setTextureMatrix(matrix4f);
    }

    private static void setupRainbowTexturing2(float in, long time){
        long i = Util.getMillis() * time;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        float f2 = (float)Math.sin(i / 30000F);
        Matrix4f matrix4f = (new Matrix4f()).translation(f1, f2, 0.0F);
        matrix4f.scale(in);
        RenderSystem.setTextureMatrix(matrix4f);
    }

    private static void setupStaticTexturing(float in, long time){
        long i = Util.getMillis() * time;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        float f2 = (float)Math.floor((i % 3000L) / 3000.0F * 4.0F);
        float f3 = (float)Math.sin(i / 30000F) * 0.05F;
        Matrix4f matrix4f = (new Matrix4f()).translation(f1, f2 * 0.25F + f3, 0.0F);
        matrix4f.scale(in * 1.5F, in * 0.25F, in);
        RenderSystem.setTextureMatrix(matrix4f);
    }

    public static RenderType getFarseerBeam() {
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setCullState(CULL)
                .setTextureState(new TextureStateShard(STATIC_TEXTURE, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(false);
        return create("farseer_beam", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderState);
    }

    public static VertexConsumer createMergedVertexConsumer(VertexConsumer consumer1, VertexConsumer consumer2){
        VertexConsumer vertexConsumer = consumer2;
        if(!encounteredMultiConsumerError){
            try{
                vertexConsumer = VertexMultiConsumer.create(consumer1, consumer2);
            }catch (Exception e){
                AlexsMobs.LOGGER.warn("Encountered issue mixing two render types together. Likely an issue with Optifine or other rendering mod. This warning will only display once.");
                encounteredMultiConsumerError = true;
            }
        }
        return vertexConsumer;
    }
}
