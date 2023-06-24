package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public class ModelBaldEagle extends AdvancedEntityModel<EntityBaldEagle> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox legL;
    private final AdvancedModelBox footL;
    private final AdvancedModelBox legR;
    private final AdvancedModelBox footR;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox headPivot;
    private final AdvancedModelBox head;
    private final AdvancedModelBox topHead;
    private final AdvancedModelBox beak1;
    private final AdvancedModelBox beak2;
    private final AdvancedModelBox hood_tie;
    private final AdvancedModelBox hood;
    private final AdvancedModelBox wingR;
    private final AdvancedModelBox feathersR;
    private final AdvancedModelBox tipR;
    private final AdvancedModelBox wingL;
    private final AdvancedModelBox feathersL;
    private final AdvancedModelBox tipL;

    public ModelBaldEagle() {
        texWidth = 64;
        texHeight = 32;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -9.3F, -2.0F);
        root.addChild(body);
        setRotationAngle(body, 0.8727F, 0.0F, 0.0F);
        body.setTextureOffset(1, 12).addBox(-2.0F, 0.0F, -1.8F, 4.0F, 8.0F, 5.0F, 0.0F, false);

        legL = new AdvancedModelBox(this, "legL");
        legL.setPos(-1.4F, 5.5F, -1.15F);
        body.addChild(legL);
        setRotationAngle(legL, -0.8727F, 0.1745F, 0.0F);
        legL.setTextureOffset(0, 26).addBox(-0.5F, 0.2F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        footL = new AdvancedModelBox(this, "footL");
        footL.setPos(0.0F, 4.2F, 0.5F);
        legL.addChild(footL);
        setRotationAngle(footL, 0.0F, 0.1745F, -0.1745F);
        footL.setTextureOffset(5, 25).addBox(-1.5F, 0.0F, -1.9F, 3.0F, 2.0F, 4.0F, 0.0F, false);

        legR = new AdvancedModelBox(this, "legR");
        legR.setPos(1.4F, 5.5F, -1.15F);
        body.addChild(legR);
        setRotationAngle(legR, -0.8727F, -0.1745F, 0.0F);
        legR.setTextureOffset(0, 26).addBox(-0.5F, 0.2F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        footR = new AdvancedModelBox(this, "footR");
        footR.setPos(0.0F, 4.2F, 0.5F);
        legR.addChild(footR);
        setRotationAngle(footR, 0.0F, -0.1745F, 0.1745F);
        footR.setTextureOffset(5, 25).addBox(-1.5F, 0.0F, -1.9F, 3.0F, 2.0F, 4.0F, 0.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setPos(0.0F, 8.07F, 1.36F);
        body.addChild(tail);
        setRotationAngle(tail, 0.392F, 0.0F, 0.0F);
        tail.setTextureOffset(24, 1).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 7.0F, 1.0F, 0.0F, false);

        headPivot = new AdvancedModelBox(this, "headPivot");
        headPivot.setPos(0.0F, -0.51F, 0.64F);
        body.addChild(headPivot);
        setRotationAngle(headPivot, -0.576F, 0.0F, 0.0F);

        head = new AdvancedModelBox(this, "head");
        headPivot.addChild(head);
        head.setTextureOffset(1, 3).addBox(-1.5F, -4.4F, -1.0F, 3.0F, 6.0F, 3.0F, 0.0F, false);

        topHead = new AdvancedModelBox(this, "topHead");
        topHead.setPos(0.0F, -4.9F, -0.5F);
        head.addChild(topHead);
        topHead.setTextureOffset(10, 0).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 4.0F, 0.0F, false);

        beak1 = new AdvancedModelBox(this, "beak1");
        beak1.setPos(0.0F, -3.5F, -0.7F);
        head.addChild(beak1);
        setRotationAngle(beak1, -0.1745F, 0.0F, 0.0F);
        beak1.setTextureOffset(21, 9).addBox(-0.5F, -1.0F, -1.94F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        beak2 = new AdvancedModelBox(this, "beak2");
        beak2.setPos(0.0F, -1.0F, -2.54F);
        beak1.addChild(beak2);
        beak2.setTextureOffset(16, 9).addBox(-0.5F, 0.01F, -0.4F, 1.0F, 3.0F, 1.0F, 0.0F, false);

        hood_tie = new AdvancedModelBox(this, "hood_tie");
        hood_tie.setPos(0.0F, -3.65F, 1.85F);
        head.addChild(hood_tie);
        setRotationAngle(hood_tie, -0.2215F, 0.0F, 0.0F);
        hood_tie.setTextureOffset(40, -4).addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F, 0.0F, false);

        hood = new AdvancedModelBox(this, "hood");
        hood.setPos(0.0F, -4.9F, -0.5F);
        head.addChild(hood);
        hood.setTextureOffset(36, 7).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 4.0F, 4.0F, 0.0F, false);
        hood.setScale(1.1F, 1.1F, 1.1F);
        wingR = new AdvancedModelBox(this, "wingR");
        wingR.setPos(-1.9F, 0.0F, 2.0F);
        body.addChild(wingR);
        setRotationAngle(wingR, 0.576F, 0.1571F, 0.1396F);
        wingR.setTextureOffset(20, 14).addBox(-0.5F, -1.0F, -4.5F, 1.0F, 12.0F, 5.0F, 0.0F, true);

        feathersR = new AdvancedModelBox(this, "feathersR");
        feathersR.setPos(-0.5F, 0.8F, 1.6F);
        wingR.addChild(feathersR);
        feathersR.setTextureOffset(52, 10).addBox(0.5F, -1.5F, -3.1F, 0.0F, 16.0F, 2.0F, 0.0F, true);

        tipR = new AdvancedModelBox(this, "tipR");
        tipR.setPos(-0.51F, 11.8F, 0.5F);
        wingR.addChild(tipR);
        setRotationAngle(tipR, 0.0F, 0.0F, 0.0873F);
        tipR.setTextureOffset(36, 10).addBox(0.5F, -6.0F, -6.0F, 0.0F, 13.0F, 8.0F, 0.0F, true);

        wingL = new AdvancedModelBox(this, "wingL");
        wingL.setPos(1.9F, 0.0F, 2.0F);
        body.addChild(wingL);
        setRotationAngle(wingL, 0.576F, -0.1571F, -0.1396F);
        wingL.setTextureOffset(20, 14).addBox(-0.5F, -1.0F, -4.5F, 1.0F, 12.0F, 5.0F, 0.0F, false);

        feathersL = new AdvancedModelBox(this, "feathersL");
        feathersL.setPos(0.5F, 0.8F, 1.6F);
        wingL.addChild(feathersL);
        feathersL.setTextureOffset(52, 10).addBox(-0.5F, -1.5F, -3.1F, 0.0F, 16.0F, 2.0F, 0.0F, false);

        tipL = new AdvancedModelBox(this, "tipL");
        tipL.setPos(0.51F, 11.8F, 0.5F);
        wingL.addChild(tipL);
        setRotationAngle(tipL, 0.0F, 0.0F, -0.0873F);
        tipL.setTextureOffset(36, 10).addBox(-0.5F, -6.0F, -6.0F, 0.0F, 13.0F, 8.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityBaldEagle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        this.resetToDefaultPose();
        float flapSpeed = 0.4F;
        float flapDegree = 0.2F;
        float walkSpeed = 0.5F;
        float walkDegree = 0.5F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float partialTicks = ageInTicks - entity.tickCount;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTicks;
        float perchProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTicks;
        float tackleProgress = Math.min(entity.prevTackleProgress + (entity.tackleProgress - entity.prevTackleProgress) * partialTicks, flyProgress);
        float swoopProgress = Math.min(entity.prevSwoopProgress + (entity.swoopProgress - entity.prevSwoopProgress) * partialTicks, flyProgress);
        float flyFeetProgress = Math.max(0, flyProgress - tackleProgress);
        float biteProgress = entity.prevAttackProgress + (entity.attackProgress - entity.prevAttackProgress) * partialTicks;
        float flapAmount = (entity.prevFlapAmount + (entity.flapAmount - entity.prevFlapAmount) * partialTicks) * flyProgress * 0.2F * (5F - swoopProgress) * 0.2F;
        progressRotationPrev(body, flyProgress, Maths.rad(40), 0, 0, 5F);
        progressRotationPrev(tail, flyProgress, Maths.rad(-20), 0, 0, 5F);
        progressRotationPrev(legL, flyFeetProgress, Maths.rad(30), 0, 0, 5F);
        progressRotationPrev(legR, flyFeetProgress, Maths.rad(30), 0, 0, 5F);
        progressRotationPrev(footL, flyFeetProgress, Maths.rad(30), 0, 0, 5F);
        progressRotationPrev(footR, flyFeetProgress, Maths.rad(30), 0, 0, 5F);
        progressPositionPrev(legL, flyFeetProgress, 0, -1, 0, 5f);
        progressPositionPrev(legR, flyFeetProgress, 0, -1, 0, 5f);
        progressRotationPrev(wingR, flyProgress,  Maths.rad(-120),  Maths.rad(90),  0, 5F);
        progressPositionPrev(wingR, flyProgress, -1, 5, 0F, 5f);
        progressRotationPrev(wingL, flyProgress,  Maths.rad(-120),  Maths.rad(-90),  0, 5F);
        progressPositionPrev(wingL, flyProgress, 1, 5, 0F, 5f);
        progressRotationPrev(head, flyProgress, Maths.rad(-30), 0, 0, 5F);
        progressPositionPrev(head, flyProgress, 0, 0, -1.5F, 5f);
        progressPositionPrev(body, flyProgress, 0, 4, -1.5F, 5f);
        progressRotationPrev(body, perchProgress, Maths.rad(-20), 0, 0, 5F);
        progressRotationPrev(legL, perchProgress, Maths.rad(20), 0, 0, 5F);
        progressRotationPrev(legR, perchProgress, Maths.rad(20), 0, 0, 5F);
        progressRotationPrev(tail, perchProgress, Maths.rad(10), 0, 0, 5F);
        progressPositionPrev(body, perchProgress, 0, -1, 0, 5f);
        progressRotationPrev(body, tackleProgress, Maths.rad(-20), 0, 0, 5F);
        progressPositionPrev(body, tackleProgress, 0, -1, 0, 5f);
        progressRotationPrev(legL, tackleProgress, Maths.rad(-60), 0, 0, 5F);
        progressRotationPrev(legR, tackleProgress, Maths.rad(-60), 0, 0, 5F);
        progressPositionPrev(legL, tackleProgress, 0, -1, 1, 5f);
        progressPositionPrev(legR, tackleProgress, 0, -1, 1, 5f);
        progressRotationPrev(wingL, swoopProgress,  Maths.rad(60),  Maths.rad(50),  0, 5F);
        progressPositionPrev(wingL, swoopProgress, -2, -2, 0, 5f);
        progressRotationPrev(wingR, swoopProgress,  Maths.rad(60),  Maths.rad(-50),  0, 5F);
        progressPositionPrev(wingR, swoopProgress, 2, -2, 0, 5f);
        progressRotationPrev(head, swoopProgress, Maths.rad(-10), 0, 0, 5F);
        progressRotationPrev(head, biteProgress, Maths.rad(70), 0, 0, 2.5F);
        if(flyProgress > 0){
            this.bob(body, flapSpeed * 0.5F, flapDegree * 4, true, ageInTicks, 1);
            this.swing(wingL, flapSpeed, flapDegree * 3, true, 0F, 0F, ageInTicks, flapAmount);
            this.swing(wingR, flapSpeed, flapDegree * 3, false, 0F, 0F, ageInTicks, flapAmount);
            this.bob(body, flapSpeed * 0.5F, flapDegree * 4, true, ageInTicks, flapAmount);
        }else{
            float walk = Math.min(limbSwingAmount, 1F);
            progressRotationPrev(body, walk, Maths.rad(15), 0, 0, 1);
            progressRotationPrev(legL, walk, Maths.rad(-15), 0, 0, 1);
            progressRotationPrev(legR, walk, Maths.rad(-15), 0, 0, 1);
            progressRotationPrev(wingL, walk, Maths.rad(-15), 0, 0, 1);
            progressRotationPrev(wingR, walk, Maths.rad(-15), 0, 0, 1);
            progressPositionPrev(body, walk, 0, 1, 0, 1);
            this.bob(body, walkSpeed, walkDegree * 1.3F, true, limbSwing, limbSwingAmount);
            this.walk(legL, walkSpeed, walkDegree * 1.85F, false, 0F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(legR, walkSpeed, walkDegree * 1.85F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(footL, walkSpeed, walkDegree * 0.4F, true, 2F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(footR, walkSpeed, walkDegree * 0.4F, false, 2F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, walkDegree * 0.3F, false, 1F, -0.2F, limbSwing, limbSwingAmount);
            this.flap(tail, walkSpeed, walkDegree * 0.2F, false, 1F, 0F, limbSwing, limbSwingAmount);
            this.flap(body, walkSpeed, walkDegree * 0.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
        }
        this.walk(head, idleSpeed * 0.7F, idleDegree, false, -1F, 0.05F, ageInTicks, 1);
        this.walk(tail, idleSpeed * 0.7F, idleDegree, false, 1F, 0.05F, ageInTicks, 1);
        if(!entity.isVehicle()){
            head.rotateAngleY += netHeadYaw / 57.295776F;
            head.rotateAngleZ += headPitch / 57.295776F;
        }
        float birdPitch = entity.prevBirdPitch + (entity.birdPitch - entity.prevBirdPitch) * partialTicks;
        this.body.rotateAngleX += birdPitch * flyProgress * 0.2F * Mth.DEG_TO_RAD;

    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        if (this.young) {
            float f = 1.35F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
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

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }


    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, head, headPivot, wingL, wingR, beak1, beak2, hood, tipL, tipR, hood_tie, feathersL, feathersR, legL, legR, footL, footR, topHead);
    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
