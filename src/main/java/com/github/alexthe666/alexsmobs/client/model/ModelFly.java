package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public class ModelFly extends AdvancedEntityModel<EntityFly> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox legs;
    private final AdvancedModelBox left_wing;
    private final AdvancedModelBox right_wing;
    private final AdvancedModelBox mouth;

    public ModelFly() {
        texWidth = 32;
        texHeight = 32;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, -3.0F, 0.0F);
        root.addChild(body);
        body.texOffs(0, 0).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);

        legs = new AdvancedModelBox(this);
        legs.setPos(0.0F, 2.0F, -2.0F);
        body.addChild(legs);
        legs.texOffs(0, 11).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 5.0F, 0.0F, false);

        left_wing = new AdvancedModelBox(this);
        left_wing.setPos(1.0F, -2.0F, -1.0F);
        body.addChild(left_wing);
        left_wing.texOffs(12, 11).addBox(0.0F, 0.0F, -1.0F, 4.0F, 0.0F, 3.0F, 0.0F, false);

        right_wing = new AdvancedModelBox(this);
        right_wing.setPos(-1.0F, -2.0F, -1.0F);
        body.addChild(right_wing);
        right_wing.texOffs(12, 11).addBox(-4.0F, 0.0F, -1.0F, 4.0F, 0.0F, 3.0F, 0.0F, true);

        mouth = new AdvancedModelBox(this);
        mouth.setPos(0.0F, 0.0F, -3.0F);
        body.addChild(mouth);
        mouth.texOffs(15, 16).addBox(0.0F, 0.0F, -1.0F, 0.0F, 4.0F, 2.0F, 0.0F, false);        this.updateDefaultPose();
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.95D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }

    @Override
    public void setupAnim(EntityFly entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flySpeed = 1.4F;
        float flyDegree = 0.8F;
        float idleSpeed = 1.4F;
        float idleDegree = 0.8F;
        this.walk(mouth, idleSpeed * 0.2F, idleDegree * 0.1F, false, -1, 0.2F, ageInTicks, 1);
        this.flap(mouth, idleSpeed * 0.2F, idleDegree * 0.05F, false, -2, 0F, ageInTicks, 1);
        boolean flag = entityIn.isOnGround() && entityIn.getDeltaMovement().lengthSqr() < 1.0E-7D;
        if(flag){
            this.left_wing.zRot = (float) Math.toRadians(-35);
            this.right_wing.zRot = (float) Math.toRadians(35);
            this.swing(legs, flySpeed * 0.6F, flyDegree * 0.2F, false, 1, 0F, limbSwing, limbSwingAmount);
        }else{
            this.flap(left_wing, flySpeed * 1.3F, flyDegree, true, 0, 0.2F, ageInTicks, 1);
            this.flap(right_wing, flySpeed * 1.3F, flyDegree, false, 0, 0.2F, ageInTicks, 1);
            this.walk(legs, flySpeed * 0.2F, flyDegree * 0.2F, false, 1, 0.2F, ageInTicks, 1);
        }
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, left_wing, right_wing, legs, mouth);
    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.xRot = x;
        advancedModelBox.yRot = y;
        advancedModelBox.zRot = z;
    }
}