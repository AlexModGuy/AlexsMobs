package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ModelMudskipper extends AdvancedEntityModel<EntityMudskipper> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox head;
    private final AdvancedModelBox eyes;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox dorsalFin;
    private final AdvancedModelBox tailFin;
    private final AdvancedModelBox leftFin;
    private final AdvancedModelBox rightFin;

    public ModelMudskipper() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -2.0F, -5.0F);
        root.addChild(head);
        head.setTextureOffset(0, 15).addBox(-3.5F, -3.0F, -4.0F, 7.0F, 5.0F, 6.0F, 0.0F, false);

        eyes = new AdvancedModelBox(this, "eyes");
        eyes.setRotationPoint(0.0F, -3.0F, -1.5F);
        head.addChild(eyes);
        eyes.setTextureOffset(19, 0).addBox(-2.5F, -2.0F, -1.5F, 5.0F, 2.0F, 3.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, 0.0F, 3.0F);
        head.addChild(tail);
        tail.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 10.0F, 0.0F, false);
        tail.setTextureOffset(23, 9).addBox(0.0F, -4.0F, 4.0F, 0.0F, 2.0F, 6.0F, 0.0F, false);

        dorsalFin = new AdvancedModelBox(this, "dorsalFin");
        dorsalFin.setRotationPoint(0.0F, -2.0F, -2.0F);
        tail.addChild(dorsalFin);
        dorsalFin.setTextureOffset(0, 27).addBox(0.0F, -5.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, false);

        tailFin = new AdvancedModelBox(this, "tailFin");
        tailFin.setRotationPoint(0.0F, 0.0F, 9.0F);
        tail.addChild(tailFin);
        tailFin.setTextureOffset(20, 20).addBox(0.0F, -3.0F, -1.0F, 0.0F, 6.0F, 7.0F, 0.0F, false);

        leftFin = new AdvancedModelBox(this, "leftFin");
        leftFin.setRotationPoint(2.0F, 2.0F, -1.0F);
        tail.addChild(leftFin);
        leftFin.setTextureOffset(28, 18).addBox(0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, 0.0F, false);

        rightFin = new AdvancedModelBox(this, "rightFin");
        rightFin.setRotationPoint(-2.0F, 2.0F, -1.0F);
        tail.addChild(rightFin);
        rightFin.setTextureOffset(28, 18).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, 0.0F, true);
        this.updateDefaultPose();
    }


    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, head, eyes, tail, dorsalFin, tailFin, leftFin, rightFin);
    }

    @Override
    public void setupAnim(EntityMudskipper entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float blinkAmount = Math.max(0, ((float)Math.sin(ageInTicks * 0.1F) - 0.5F) * 2F);
        float partialTick = ageInTicks - entity.tickCount;
        float displayProgress = entity.prevDisplayProgress + (entity.displayProgress - entity.prevDisplayProgress) * partialTick;
        float swimProgress = entity.prevSwimProgress + (entity.swimProgress - entity.prevSwimProgress) * partialTick;
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
        float walkSpeed = 1f;
        float walkDegree = 0.7f;
        float swimSpeed = 0.4f;
        float swimDegree = 0.5f;
        float displaySpeed = 0.3f;
        float displayDegree = 0.4f;
        //so the model does not sink in mud
        progressPositionPrev(head, entity.prevMudProgress + (entity.mudProgress - entity.prevMudProgress) * partialTick, 0, -2F, 0F, 1f);
        progressPositionPrev(eyes, blinkAmount, 0F, 1.5F, 0F, 1f);
        progressPositionPrev(dorsalFin, 5f - displayProgress, 0F, 2F, 0F, 5f);
        progressRotationPrev(head, displayProgress, (float) Math.toRadians(-10F), 0F, 0F, 5f);
        progressRotationPrev(tail, displayProgress, (float) Math.toRadians(20F), 0F, 0F, 5f);
        progressRotationPrev(dorsalFin, displayProgress, (float) Math.toRadians(-10F), 0F, 0F, 5f);
        progressRotationPrev(rightFin, displayProgress, (float) Math.toRadians(-10F), 0F, 0F, 5f);
        progressRotationPrev(leftFin, displayProgress, (float) Math.toRadians(-10F), 0F, 0F, 5f);
        progressRotationPrev(tailFin, displayProgress, (float) Math.toRadians(10F), 0F, 0F, 5f);
        progressPositionPrev(head, displayProgress, 0F, -0.5F, 0F, 5f);
        progressPositionPrev(rightFin, displayProgress, 0F, -0.5F, 0F, 5f);
        progressPositionPrev(leftFin, displayProgress, 0F, -0.5F, 0F, 5f);
        progressRotationPrev(head, sitProgress, 0F, (float) Math.toRadians(-10F), 0F, 5f);
        progressRotationPrev(tail, sitProgress, 0F, (float) Math.toRadians(30F), 0F, 5f);
        progressRotationPrev(tailFin, sitProgress, 0F, (float) Math.toRadians(10F), 0F, 5f);
        progressRotationPrev(leftFin, sitProgress, 0F, (float) Math.toRadians(-20F), 0F, 5f);
        progressRotationPrev(rightFin, sitProgress, 0F, (float) Math.toRadians(-20F), 0F, 5f);
        progressPositionPrev(rightFin, sitProgress, 0.5F, 0F, 0F, 5f);
        progressPositionPrev(leftFin, sitProgress, 0F, 0F, 1F, 5f);
        float walkSwingAmount = limbSwingAmount * (1F - 0.2F * swimProgress);
        float swimSwingAmount = limbSwingAmount * 0.2F * swimProgress;
        this.swing(head, swimSpeed, 0.4F * swimDegree, true, 1F, 0F, limbSwing, swimSwingAmount);
        this.swing(tail, swimSpeed, swimDegree, false, 0, 0F, limbSwing, swimSwingAmount);
        this.swing(tailFin, swimSpeed, swimDegree, false, -1F, 0F, limbSwing, swimSwingAmount);
        this.flap(rightFin, swimSpeed, swimDegree, false, 2f, 0.1F, limbSwing, swimSwingAmount);
        this.flap(leftFin, swimSpeed, swimDegree, true, 2f, 0.1F, limbSwing, swimSwingAmount);
        this.bob(head, swimSpeed, swimDegree, false, limbSwing, swimSwingAmount);
        this.swing(head, displaySpeed, displayDegree * 0.3F, false, 0F, 0F, ageInTicks, displayProgress * 0.2F);
        this.swing(tail, displaySpeed, displayDegree, true, 0, 0F, ageInTicks, displayProgress * 0.2F);
        this.swing(tailFin, displaySpeed, displayDegree, true, 0, 0F, ageInTicks, displayProgress * 0.2F);
        this.flap(dorsalFin, displaySpeed, displayDegree, true, 0, 0F, ageInTicks, displayProgress * 0.2F);
        float f = walkSpeed;
        float f1 = walkDegree * 0.15F;
        float headUp = 1.6F * Math.min(0, (float) (Math.sin(limbSwing * f) * (double) walkSwingAmount * (double) f1 * 9D - (walkSwingAmount * f1 * 9D)));
        this.head.rotationPointY += headUp;
        this.head.rotationPointZ += (float) (Math.sin(limbSwing * f - 1.5F) * (double) walkSwingAmount * (double) f1 * 9D - (walkSwingAmount * f1 * 9D));
        this.rightFin.rotationPointY += headUp;
        this.leftFin.rotationPointY += headUp;
        this.walk(tail, walkSpeed, walkDegree * 0.5F, true, 1F, 0.04F, limbSwing, walkSwingAmount);
        this.walk(tailFin, walkSpeed, walkDegree * 0.65F, false, 2F, -0.04F, limbSwing, walkSwingAmount);
        this.walk(head, walkSpeed, walkDegree * 0.5F, false, 0F, 0.04F, limbSwing, walkSwingAmount);
        this.flap(rightFin, walkSpeed, walkDegree, true, 3F, -0.3F, limbSwing, walkSwingAmount);
        this.flap(leftFin, walkSpeed, walkDegree, false, 3F, -0.3F, limbSwing, walkSwingAmount);
        this.swing(rightFin, walkSpeed, walkDegree, false, 2F, -0.3F, limbSwing, walkSwingAmount);
        this.swing(leftFin, walkSpeed, walkDegree, true, 2F, -0.3F, limbSwing, walkSwingAmount);

    }


    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        if (this.young) {
            float f = 1.45F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.4D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            this.head.setScale(1F, 1F, 1F);
        } else {
            this.head.setScale(1F, 1F, 1F);
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }
}