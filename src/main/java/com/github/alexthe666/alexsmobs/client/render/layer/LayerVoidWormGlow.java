package com.github.alexthe666.alexsmobs.client.render.layer;

import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormBody;
import com.github.alexthe666.alexsmobs.client.model.ModelVoidWormTail;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.misc.VoidWormMetadataSection;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;

public abstract class LayerVoidWormGlow<T extends LivingEntity> extends RenderLayer<T, EntityModel<T>>  {

    private final ResourceManager resourceManager;
    private final Object2BooleanMap<ResourceLocation> mcmetaData;
    private EntityModel<T> layerModel;
    private final EntityModel bodyModel = new ModelVoidWormBody(1.001F);
    private final EntityModel tailModel = new ModelVoidWormTail(1.001F);

    public LayerVoidWormGlow(RenderLayerParent<T, EntityModel<T>> renderer, ResourceManager resourceManager, EntityModel<T> layerModel) {
        super(renderer);
        this.resourceManager = resourceManager;
        this.mcmetaData = new Object2BooleanOpenHashMap<>();
        this.layerModel = layerModel;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T worm, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = getGlowTexture(worm);
        boolean special = isSpecialRenderer(texture);

        if (isGlowing(worm) || special) {
            if(special){
                if(worm instanceof EntityVoidWormPart body){
                    this.layerModel = body.isTail() ? tailModel : bodyModel;
                }
                this.layerModel.setupAnim(worm, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                VertexConsumer consumer = AMRenderTypes.createMergedVertexConsumer(bufferIn.getBuffer(AMRenderTypes.VOID_WORM_PORTAL_OVERLAY), bufferIn.getBuffer(RenderType.entityCutoutNoCull(texture)));
                this.layerModel.renderToBuffer(matrixStackIn, consumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }else{
                float f = getAlpha(worm);
                this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.eyes(texture)), 240, LivingEntityRenderer.getOverlayCoords(worm, 1.0F), 1.0F, 1.0F, 1.0F, f);
            }
        }
    }

    public abstract ResourceLocation getGlowTexture(LivingEntity worm);
    public abstract boolean isGlowing(LivingEntity livingEntity);
    public abstract float getAlpha(LivingEntity livingEntity);

    private boolean isSpecialRenderer(ResourceLocation resourceLocation){
        if(mcmetaData.containsKey(resourceLocation)){
            return mcmetaData.getBoolean(resourceLocation);
        }
        if(this.resourceManager.getResource(resourceLocation).isPresent()){
            Resource resource = this.resourceManager.getResource(resourceLocation).get();
            try {
                VoidWormMetadataSection section = resource.metadata().getSection(VoidWormMetadataSection.SERIALIZER).orElse(new VoidWormMetadataSection());
                mcmetaData.put(resourceLocation, section.isEndPortalTexture());
                return section.isEndPortalTexture();
            } catch (Exception e) {
                e.printStackTrace();
                mcmetaData.put(resourceLocation, false);
                return false;
            }
        }
        mcmetaData.put(resourceLocation, false);
        return false;
    }

}
