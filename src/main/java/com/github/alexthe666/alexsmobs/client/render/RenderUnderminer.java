package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.model.ModelUnderminerDwarf;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerUnderminerItem;
import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RenderUnderminer extends MobRenderer<EntityUnderminer, EntityModel<EntityUnderminer>> {
    private static final ResourceLocation TEXTURE_DWARF = ResourceLocation
            .parse("alexsmobs:textures/entity/underminer_dwarf.png");
    private static final ResourceLocation TEXTURE_0 = ResourceLocation
            .parse("alexsmobs:textures/entity/underminer_0.png");
    private static final ResourceLocation TEXTURE_1 = ResourceLocation
            .parse("alexsmobs:textures/entity/underminer_1.png");
    public static final List<ResourceLocation> BREAKING_LOCATIONS = IntStream.range(0, 10)
            .mapToObj((destroyStage) -> ResourceLocation
                    .parse("alexsmobs:textures/block/ghostly_pickaxe/destroy_stage_" + destroyStage + ".png"))
            .collect(Collectors.toList());
    private static final ModelUnderminerDwarf DWARF_MODEL = new ModelUnderminerDwarf();
    private static HumanoidModel<EntityUnderminer> NORMAL_MODEL = null;
    private static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream()
            .map(AMRenderTypes::getGhostCrumbling).collect(Collectors.toList());
    public static boolean renderWithPickaxe = false;

    public RenderUnderminer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, DWARF_MODEL, 0.4F);
        NORMAL_MODEL = new HumanoidModel<>(
                Minecraft.getInstance().getEntityModels().bakeLayer(AMModelLayers.UNDERMINER));
        this.addLayer(new LayerUnderminerItem(this));
    }

    @Override
    protected void scale(EntityUnderminer entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.925F, 0.925F, 0.925F);
    }

    public boolean shouldRender(EntityUnderminer livingEntityIn, Frustum camera, double camX, double camY,
            double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.getMiningPos() != null) {
                BlockPos pos = livingEntityIn.getMiningPos();
                if (pos != null) {
                    Vec3 vector3d = Vec3.atLowerCornerOf(pos);
                    Vec3 vector3dCorner = Vec3.atLowerCornerOf(pos).add(1, 1, 1);
                    return camera.isVisible(new AABB(vector3d.x, vector3d.y, vector3d.z, vector3dCorner.x,
                        vector3dCorner.y, vector3dCorner.z));
                }
            }
            return false;
        }
    }

    @Override
    protected float getFlipDegrees(EntityUnderminer entityUnderminer) {
        return 0.0F;
    }

    public void setupDwarfModel(EntityUnderminer entityIn) {
        if (entityIn.isDwarf()) {
            this.model = DWARF_MODEL;
        } else {
            this.model = NORMAL_MODEL;
        }
    }

    @Override
    public void render(EntityUnderminer entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
            MultiBufferSource bufferIn, int packedLightIn) {
        this.setupDwarfModel(entityIn);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        BlockPos miningPos = entityIn.getMiningPos();
        if (miningPos != null) {
            matrixStackIn.pushPose();
            double d0 = Mth.lerp(partialTicks, entityIn.xo, entityIn.getX());
            double d1 = Mth.lerp(partialTicks, entityIn.yo, entityIn.getY());
            double d2 = Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ());

            matrixStackIn.translate((double) miningPos.getX() - d0, (double) miningPos.getY() - d1,
                    (double) miningPos.getZ() - d2);
            int progress = (int) Math
                    .round((DESTROY_TYPES.size() - 1) * (float) Mth.clamp(entityIn.getMiningProgress(), 0F, 1.0F));
            PoseStack.Pose posestack$pose = matrixStackIn.last();
            VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(
                    bufferIn.getBuffer(DESTROY_TYPES.get(progress)), posestack$pose,
                    1.0F);

            Minecraft.getInstance().getBlockRenderer().renderBreakingTexture(entityIn.level().getBlockState(miningPos),
                miningPos, entityIn.level(), matrixStackIn, vertexconsumer1);
            matrixStackIn.popPose();
        }
    }

    public void renderUnderminerModel(PoseStack matrixStackIn, MultiBufferSource source, RenderType defRenderType,
            float partialTicks, int packedLightIn, int overlayColors, float alphaIn, EntityUnderminer entityIn) {
        boolean hurt = Math.max(entityIn.hurtTime, entityIn.deathTime) > 0;
        this.model.renderToBuffer(matrixStackIn, source.getBuffer(defRenderType), packedLightIn,
                LivingEntityRenderer.getOverlayCoords(entityIn, 0.0F),
                AMColorUtil.packColor(hurt ? 0.4F : 1.0F, hurt ? 0.8F : 1.0F, hurt ? 0.7F : 1.0F, alphaIn));
    }

    @Nullable
    protected RenderType getRenderType(EntityUnderminer farseer, boolean normal, boolean invis, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(farseer);
        return outline ? RenderType.outline(resourcelocation) : AMRenderTypes.getUnderminer(resourcelocation);
    }

    public ResourceLocation getTextureLocation(EntityUnderminer entity) {
        return entity.isDwarf() ? TEXTURE_DWARF : entity.getVariant() == 0 ? TEXTURE_0 : TEXTURE_1;
    }

}
