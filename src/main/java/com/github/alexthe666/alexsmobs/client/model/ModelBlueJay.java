package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;

public class ModelBlueJay extends AdvancedEntityModel<EntityBlueJay> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftLeg;
    private final AdvancedModelBox rightLeg;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox leftWing;
    private final AdvancedModelBox rightWing;
    private final AdvancedModelBox head;
    private final AdvancedModelBox crest;

    public ModelBlueJay() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -3.2F, 0.0F);
        root.addChild(body);
        setRotateAngle(body, -0.1309F, 0.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 4.0F, 7.0F, 0.0F, false);

        leftLeg = new AdvancedModelBox(this, "leftLeg");
        leftLeg.setRotationPoint(1.5F, 0.0F, 1.0F);
        body.addChild(leftLeg);
        setRotateAngle(leftLeg, 0.1309F, 0.0F, 0.0F);
        leftLeg.setTextureOffset(26, 10).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 3.0F, 2.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this, "rightLeg");
        rightLeg.setRotationPoint(-1.5F, 0.0F, 1.0F);
        body.addChild(rightLeg);
        setRotateAngle(rightLeg, 0.1309F, 0.0F, 0.0F);
        rightLeg.setTextureOffset(26, 10).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 3.0F, 2.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -3.0F, 3.0F);
        body.addChild(tail);
        tail.setTextureOffset(0, 22).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 7.0F, 0.0F, false);

        leftWing = new AdvancedModelBox(this, "leftWing");
        leftWing.setRotationPoint(2.0F, -3.0F, -2.0F);
        body.addChild(leftWing);
        leftWing.setTextureOffset(15, 14).addBox(0.0F, -1.0F, -1.0F, 1.0F, 3.0F, 8.0F, 0.0F, false);

        rightWing = new AdvancedModelBox(this, "rightWing");
        rightWing.setRotationPoint(-2.0F, -3.0F, -2.0F);
        body.addChild(rightWing);
        rightWing.setTextureOffset(15, 14).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 3.0F, 8.0F, 0.0F, true);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -3.0F, -4.0F);
        body.addChild(head);
        setRotateAngle(head, 0.2182F, 0.0F, 0.0F);
        head.setTextureOffset(23, 0).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 4.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(26, 16).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 2.0F, 3.0F, 0.0F, false);

        crest = new AdvancedModelBox(this, "crest");
        crest.setRotationPoint(0.0F, -2.0F, -1.0F);
        head.addChild(crest);
        setRotateAngle(crest, 0.3491F, 0.0F, 0.0F);
        crest.setTextureOffset(0, 12).addBox(-2.5F, -1.0F, 0.0F, 5.0F, 3.0F, 6.0F, -0.01F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public void setupAnim(EntityBlueJay entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flapSpeed = 0.6F;
        float flapDegree = 0.2F;
        float walkSpeed = 0.95F;
        float walkDegree = 0.6F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float partialTick = ageInTicks - entity.tickCount;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
        float flapAmount = flyProgress * 0.2F * (entity.prevFlapAmount + (entity.flapAmount - entity.prevFlapAmount) * partialTick);
        float crestAmount = entity.prevCrestAmount + (entity.crestAmount - entity.prevCrestAmount) * partialTick;
        float biteProgress = entity.prevAttackProgress + (entity.attackProgress - entity.prevAttackProgress) * partialTick;
        float birdPitch = entity.prevBirdPitch + (entity.birdPitch - entity.prevBirdPitch) * partialTick;
        progressRotationPrev(rightWing, flyProgress,  Maths.rad(-20),  0,  Maths.rad(20), 5F);
        progressRotationPrev(leftWing, flyProgress,  Maths.rad(-20),  0,  Maths.rad(-20), 5F);
        progressRotationPrev(body, flyProgress,  Maths.rad(10),  0,  0, 5F);
        progressRotationPrev(leftLeg, flyProgress,  Maths.rad(40),  0,  0, 5F);
        progressRotationPrev(rightLeg, flyProgress,  Maths.rad(40),  0,  0, 5F);
        progressPositionPrev(head, flyProgress, 0, 1F, -1F, 5f);
        progressPositionPrev(body, flyProgress, 0, 1F, 0F, 5f);
        progressPositionPrev(rightWing, flyProgress, 0, 1F, 1F, 5f);
        progressPositionPrev(leftWing, flyProgress, 0, 1F, 1F, 5f);
        progressPositionPrev(rightLeg, flyProgress, 0, -2F, 0F, 5f);
        progressPositionPrev(leftLeg, flyProgress, 0, -2F, 0F, 5f);
        progressRotationPrev(rightWing, flapAmount,  Maths.rad(-70),  0,  Maths.rad(70), 1F);
        progressRotationPrev(leftWing, flapAmount,  Maths.rad(-70),  0,  Maths.rad(-70), 1F);
        progressRotationPrev(crest, crestAmount,  Maths.rad(20),  0, 0, 1F);
        progressRotationPrev(head, biteProgress, Maths.rad(60), 0, 0, 5F);
        leftWing.setScale(1F + flyProgress * 0.1F, 1F + flyProgress * 0.1F, 1F + flyProgress * 0.1F);
        rightWing.setScale(1F + flyProgress * 0.1F, 1F + flyProgress * 0.1F, 1F + flyProgress * 0.1F);
        this.flap(leftWing, flapSpeed, flapDegree * 5, true, -1F, 0F, ageInTicks, flapAmount);
        this.flap(rightWing, flapSpeed, flapDegree * 5, false, -1F, 0F, ageInTicks, flapAmount);
        this.swing(leftWing, flapSpeed, flapDegree * 2, false, 0F, 0F, ageInTicks, flapAmount);
        this.swing(rightWing, flapSpeed, flapDegree * 2, false, 0F, 0F, ageInTicks, flapAmount);
        this.walk(leftWing, flapSpeed, flapDegree * 2, false, 1F, 0F, ageInTicks, flapAmount);
        this.walk(rightWing, flapSpeed, flapDegree * 2, false, 1F, 0F, ageInTicks, flapAmount);
        this.walk(tail, flapSpeed, flapDegree * 0.3F, false, -3F, -0.1F, ageInTicks, flapAmount);
        this.bob(body, flapSpeed, flapDegree * 10, false, ageInTicks, flapAmount);
        this.bob(head, flapSpeed, flapDegree * -6, false, ageInTicks, flapAmount);
        if(flyProgress <= 0.0F){
            this.bob(body, walkSpeed * 1F, walkDegree * 1.3F, true, limbSwing, limbSwingAmount);
            this.walk(rightLeg, walkSpeed, walkDegree * 1.85F, false, 0F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(leftLeg, walkSpeed, walkDegree * 1.85F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, walkDegree * 0.2F, false, 2F, -0.01F, limbSwing, limbSwingAmount);
            this.walk(tail, walkSpeed, walkDegree * 0.5F, false, 1F, 0F, limbSwing, limbSwingAmount);
        }
        this.walk(tail, idleSpeed, idleDegree, false, 1F, 0F, ageInTicks, 1);
        this.walk(crest, idleSpeed, idleDegree, false, 2F, 0F, ageInTicks, 1);
        this.bob(head, idleSpeed, idleDegree * 1.5F, true, ageInTicks, 1);
        this.faceTarget(netHeadYaw, headPitch, 1.3F, head);
        this.body.rotateAngleX += birdPitch * flyProgress * 0.2F * Mth.DEG_TO_RAD;
        if(entity.getFeedTime() > 0){
            this.flap(head, 0.4F, 0.4F, false, 1F, 0F, ageInTicks, 1);
        }
        if(entity.getSingTime() > 0){
            this.flap(head, 0.4F, 0.4F, false, 1F, 0F, ageInTicks, 1);
            this.walk(crest, 0.4F, 0.3F, false, 1F, 0.1F, ageInTicks, 1);
            this.swing(head, 0.4F, 0.4F, false, 2F, 0F, ageInTicks, 1);
            head.rotationPointZ +=  (float) (Math.sin(ageInTicks * -0.4 - 1F));
        }
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
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, crest, head, tail, leftLeg, rightLeg, leftWing, rightWing);
    }


}
