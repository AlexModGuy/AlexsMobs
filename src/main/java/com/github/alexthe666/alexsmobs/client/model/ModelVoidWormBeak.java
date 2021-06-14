package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelVoidWormBeak extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox left;
    private final AdvancedModelBox right;

    public ModelVoidWormBeak() {
        textureWidth = 64;
        textureHeight = 64;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        left = new AdvancedModelBox(this);
        left.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(left);
        left.setTextureOffset(0, 0).addBox(-0.1F, -12.9F, -3.5F, 7.0F, 13.0F, 7.0F, -0.1F, false);
        right = new AdvancedModelBox(this);
        right.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(right);
        right.setTextureOffset(0, 21).addBox(-7.0F, -13.0F, -3.5F, 7.0F, 13.0F, 7.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, left, right);
    }

    @Override
    public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
    }

    public void renderBeak(TileEntityVoidWormBeak beak, float partialTick){
        this.resetToDefaultPose();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}