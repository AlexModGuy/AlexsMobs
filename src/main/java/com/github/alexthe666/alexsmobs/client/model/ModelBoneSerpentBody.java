package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelBoneSerpentBody extends AdvancedEntityModel<EntityBoneSerpentPart> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox middle_section;

    public ModelBoneSerpentBody() {
        textureWidth = 128;
        textureHeight = 128;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        middle_section = new AdvancedModelBox(this);
        middle_section.setRotationPoint(0.0F, -7.75F, 0.0F);
        root.addChild(middle_section);
        middle_section.setTextureOffset(2, 50).addBox(-2.0F, -9.25F, -8.0F, 4.0F, 2.0F, 16.0F, 0.0F, false);
        middle_section.setTextureOffset(0, 0).addBox(-9.0F, -7.25F, -8.0F, 18.0F, 15.0F, 16.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setRotationAngles(EntityBoneSerpentPart entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        this.middle_section.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.middle_section.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        float walkSpeed = 0.35F;
        float walkDegree = 3F;
        float idleDegree = 0.7F;
        float idleSpeed = 0.2F;
        double walkOffset = (entityIn.getBodyIndex() + 1 ) * Math.PI * 0.5F;
        this.middle_section.rotationPointY += (float)(Math.sin( (double)(limbSwing * walkSpeed) - walkOffset) * (double)limbSwingAmount * (double)walkDegree - (double)(limbSwingAmount * walkDegree) );
        this.middle_section.rotationPointY += (float)(Math.sin( (double)(ageInTicks * idleSpeed) - walkOffset) * (double)1 * (double)idleDegree - (double)(1 * idleDegree) );
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, middle_section);
    }
}