package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public class ModelBananaSlug extends AdvancedEntityModel<EntityBananaSlug> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox goo;
    private final AdvancedModelBox leftAntenna;
    private final AdvancedModelBox rightAntenna;
    private final AdvancedModelBox tail;

    public ModelBananaSlug() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -2.0F, -2.0F);
        root.addChild(body);
        body.setTextureOffset(18, 23).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 4.0F, 7.0F, 0.0F, false);

        goo = new AdvancedModelBox(this);
        goo.setRotationPoint(0.0F, 2.0F, 0.0F);
        body.addChild(goo);
        goo.setTextureOffset(0, 0).addBox(-2.5F, -0.001F, 0.0F, 5.0F, 0.0F, 17.0F, 0.0F, false);

        leftAntenna = new AdvancedModelBox(this);
        leftAntenna.setRotationPoint(2.0F, -1.0F, -4.0F);
        body.addChild(leftAntenna);
        setRotationAngle(leftAntenna, 0.0F, 0.0F, -0.0873F);
        leftAntenna.setTextureOffset(0, 0).addBox(0.0F, -1.0F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, false);

        rightAntenna = new AdvancedModelBox(this);
        rightAntenna.setRotationPoint(-2.0F, -1.0F, -4.0F);
        body.addChild(rightAntenna);
        setRotationAngle(rightAntenna, 0.0F, 0.0F, 0.0873F);
        rightAntenna.setTextureOffset(0, 0).addBox(0.0F, -1.0F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, true);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, 0.0F, 3.0F);
        body.addChild(tail);
        tail.setTextureOffset(0, 18).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 3.0F, 8.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityBananaSlug entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.25F;
        float idleDegree = 0.25F;
        float walkSpeed = 1F;
        float walkDegree = 0.2F;
        float partialTick = ageInTicks - entity.tickCount;
        this.swing(leftAntenna, idleSpeed, idleDegree * 0.2F, true, 1, 0.1F, ageInTicks, 1);
        this.swing(rightAntenna, idleSpeed, idleDegree * 0.2F, false, 1, 0.1F, ageInTicks, 1);
        this.walk(leftAntenna, idleSpeed, idleDegree * 0.5F, true, 3, 0.1F, ageInTicks, 1);
        this.walk(rightAntenna, idleSpeed, idleDegree * 0.5F, true, 3, 0.1F, ageInTicks, 1);
        this.swing(tail, walkSpeed, walkDegree, true, 3F, 0F, limbSwing, limbSwingAmount);
        float antennaBack = -0.5F + (float) (Math.sin((double) (ageInTicks * idleSpeed) + 3)) * 0.2F;
        leftAntenna.rotationPointZ -= antennaBack;
        rightAntenna.rotationPointZ -= antennaBack;
        float stretch1 = (float) (Math.sin(limbSwing * -walkSpeed) * limbSwingAmount) + limbSwingAmount;
        float stretch2 = (float) (Math.sin(limbSwing * -walkSpeed + 1F) * limbSwingAmount) + limbSwingAmount;
        body.setScale(1, (1 - stretch1 * 0.025F), (1 + stretch1 * 0.25F));
        tail.setScale(1, (1 - stretch2 * 0.05F), (1 + stretch2 * 0.5F));
        body.setShouldScaleChildren(false);
        body.rotationPointZ -= stretch1 * 2F;
        leftAntenna.rotationPointZ -= stretch1 * 1;
        rightAntenna.rotationPointZ -= stretch1 * 1;
        leftAntenna.rotateAngleY += netHeadYaw * 0.6F * ((float)Math.PI / 180F);
        leftAntenna.rotateAngleX += headPitch * 0.3F * ((float)Math.PI / 180F);
        rightAntenna.rotateAngleY += netHeadYaw * 0.6F * ((float)Math.PI / 180F);
        rightAntenna.rotateAngleX += headPitch * 0.3F * ((float)Math.PI / 180F);
        float yaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
        float slimeYaw = Mth.wrapDegrees(entity.prevTrailYaw + (entity.trailYaw - entity.prevTrailYaw) * partialTick - yaw) * 0.65F;
        goo.rotationPointX =  (float) (Math.sin(limbSwing * -walkSpeed - 1F) * limbSwingAmount);
        goo.rotateAngleY += (float) Math.toRadians(slimeYaw);
        tail.rotateAngleY += (float) Math.toRadians(slimeYaw * 0.8F);
        goo.setScale(1, 0, (1 + limbSwingAmount));
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.8D, 0.125D);
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
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, goo, tail, leftAntenna, rightAntenna);
    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
