package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.entity.Entity;

public class ModelTarantulaHawkBaby extends AdvancedEntityModel<EntityTarantulaHawk> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;

    public ModelTarantulaHawkBaby() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -3.0F, -7.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-4.0F, -3.0F, 0.0F, 8.0F, 6.0F, 15.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, 0.9F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(0, 22).addBox(-3.5F, -3.0F, -3.0F, 7.0F, 5.0F, 3.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityTarantulaHawk entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 1;
        float walkDegree = 0.75F;
        float stretch = (float) (Math.sin(limbSwing * 0.25F) * limbSwingAmount) + limbSwingAmount;
        body.setScale(1, (1 - stretch * 0.05F), (1 + stretch * 0.5F));
        body.z -= stretch * 4;
        this.walk(head, 0.25F, 0.075F, false, -1, 0F, ageInTicks, 1);
        this.walk(head, walkSpeed, walkDegree * 0.1F, false, -1, 0F, limbSwing, limbSwingAmount);

    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        root.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}
