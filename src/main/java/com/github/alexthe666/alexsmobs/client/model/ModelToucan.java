package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityToucan;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

public class ModelToucan extends AdvancedEntityModel<EntityToucan> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox tail;
    public final AdvancedModelBox left_wing;
    public final AdvancedModelBox left_wingtip;
    public final AdvancedModelBox right_wing;
    public final AdvancedModelBox right_wingtip;
    public final AdvancedModelBox left_leg;
    public final AdvancedModelBox right_leg;
    public final AdvancedModelBox head;
    public final AdvancedModelBox beak;

    public ModelToucan() {
        texWidth = 64;
        texHeight = 64;
        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -4.3F, 0.6F);
        root.addChild(body);
        setRotationAngle(body, -0.3491F, 0.0F, 0.0F);
        body.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, -5.0F, 4.0F, 4.0F, 7.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setRotationPoint(0.0F, -2.0F, 2.0F);
        body.addChild(tail);
        setRotationAngle(tail, 0.3927F, 0.0F, 0.0F);
        tail.setTextureOffset(18, 7).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 5.0F, 0.0F, false);

        left_wing = new AdvancedModelBox(this);
        left_wing.setRotationPoint(2.0F, -2.0F, -3.0F);
        body.addChild(left_wing);
        setRotationAngle(left_wing, 0.1309F, 0.0F, 0.0F);
        left_wing.setTextureOffset(11, 16).addBox(0.0F, -1.0F, -1.0F, 1.0F, 4.0F, 6.0F, 0.0F, false);

        left_wingtip = new AdvancedModelBox(this);
        left_wingtip.setRotationPoint(0.3F, 0.1F, 3.0F);
        left_wing.addChild(left_wingtip);
        left_wingtip.setTextureOffset(20, 21).addBox(0.0F, -1.0F, -1.0F, 0.0F, 3.0F, 6.0F, 0.0F, false);

        right_wing = new AdvancedModelBox(this);
        right_wing.setRotationPoint(-2.0F, -2.0F, -3.0F);
        body.addChild(right_wing);
        setRotationAngle(right_wing, 0.1309F, 0.0F, 0.0F);
        right_wing.setTextureOffset(11, 16).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 4.0F, 6.0F, 0.0F, true);

        right_wingtip = new AdvancedModelBox(this);
        right_wingtip.setRotationPoint(-0.3F, 0.1F, 3.0F);
        right_wing.addChild(right_wingtip);
        right_wingtip.setTextureOffset(20, 21).addBox(0.0F, -1.0F, -1.0F, 0.0F, 3.0F, 6.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this);
        left_leg.setRotationPoint(1.5F, 1.0F, 1.0F);
        body.addChild(left_leg);
        setRotationAngle(left_leg, 0.3491F, 0.0F, 0.0F);
        left_leg.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 3.0F, 2.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this);
        right_leg.setRotationPoint(-1.5F, 1.0F, 1.0F);
        body.addChild(right_leg);
        setRotationAngle(right_leg, 0.3491F, 0.0F, 0.0F);
        right_leg.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 3.0F, 2.0F, 0.0F, true);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -2.0F, -4.0F);
        body.addChild(head);
        setRotationAngle(head, 0.48F, 0.0F, 0.0F);
        head.setTextureOffset(0, 24).addBox(-1.5F, -4.0F, -2.0F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        beak = new AdvancedModelBox(this);
        beak.setRotationPoint(0.0F, -4.0F, -2.0F);
        head.addChild(beak);
        beak.setTextureOffset(0, 12).addBox(-1.0F, 0.0F, -6.0F, 2.0F, 3.0F, 6.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, left_wing, left_wingtip, right_wing, right_wingtip, right_leg, left_leg, head, beak);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        if (this.young) {
            float f = 1.24F;
            head.setScale(f, f, f);
            head.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            this.head.setScale(0.9F, 0.9F, 0.9F);
        } else {
            this.head.setScale(0.9F, 0.9F, 0.9F);
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }
    }


    @Override
    public void setupAnim(EntityToucan entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flapSpeed = 1.0F;
        float flapDegree = 0.2F;
        float walkSpeed = 1.2F;
        float walkDegree = 0.78F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
        float runProgress = Math.max(0, (limbSwingAmount * 5F) - flyProgress);
        float biteProgress = entity.prevPeckProgress + (entity.peckProgress - entity.prevPeckProgress) * partialTick;
        progressRotationPrev(head, biteProgress, (float)Math.toRadians(90), 0, 0, 5F);
        progressRotationPrev(body, biteProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(left_leg, biteProgress, (float) Math.toRadians(15), 0, 0, 5F);
        progressRotationPrev(right_leg, biteProgress, (float) Math.toRadians(15), 0, 0, 5F);
        progressPositionPrev(head, biteProgress, 0, 1, -2, 5F);
        progressPositionPrev(left_leg, biteProgress, 0, -0.15F, 0, 5F);
        progressPositionPrev(right_leg, biteProgress, 0, -0.15F, 0, 5F);
        progressRotationPrev(head, runProgress, (float)Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(body, runProgress, (float) Math.toRadians(15), 0, 0, 5F);
        progressRotationPrev(left_leg, runProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(right_leg, runProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(body, flyProgress, (float) Math.toRadians(20), 0, 0, 5F);
        progressRotationPrev(head, flyProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(left_leg, flyProgress, (float) Math.toRadians(55), 0, 0, 5F);
        progressRotationPrev(tail, flyProgress, (float) Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(right_leg, flyProgress, (float) Math.toRadians(55), 0, 0, 5F);
        progressRotationPrev(right_wing, flyProgress,  (float) Math.toRadians(-90),  0,  (float) Math.toRadians(90), 5F);
        progressRotationPrev(left_wing, flyProgress,  (float) Math.toRadians(-90),  0,  (float) Math.toRadians(-90), 5F);
        progressPositionPrev(right_wing, flyProgress, 0, 0, 2F, 5f);
        progressPositionPrev(left_wing, flyProgress, 0, 0, 2F, 5f);
        progressPositionPrev(right_wingtip, flyProgress, 0, 0, 2, 5f);
        progressPositionPrev(left_wingtip, flyProgress, 0, 0, 2, 5f);
        progressRotationPrev(left_wingtip, flyProgress, (float) Math.toRadians(10), 0, 0, 5F);
        progressRotationPrev(right_wingtip, flyProgress, (float) Math.toRadians(10), 0, 0, 5F);
        if(flyProgress > 0) {
            this.flap(right_wing, flapSpeed, flapDegree * 5, true, 0F, 0F, ageInTicks, 1);
            this.flap(left_wing, flapSpeed, flapDegree * 5, false, 0F, 0F, ageInTicks, 1);
            this.swing(right_wingtip, flapSpeed, flapDegree * 0.5F, false, 0F, 0F, ageInTicks, 1);
            this.swing(left_wingtip, flapSpeed, flapDegree * 0.5F, true, 0F, 0F, ageInTicks, 1);
            this.bob(body, flapSpeed * 0.5F, flapDegree * 12, true, ageInTicks, 1);
            this.walk(head, flapSpeed, flapDegree * 0.2F, true, 2F, -0.1F, ageInTicks, 1);
        }else {
            this.walk(head, idleSpeed * 0.7F, idleDegree, false, 1F, 0.05F, ageInTicks, 1);
            this.walk(tail, idleSpeed * 0.7F, idleDegree, false, -1F, 0.05F, ageInTicks, 1);
            this.bob(body, walkSpeed * 1F, walkDegree * 1.3F, true, limbSwing, limbSwingAmount);
            this.walk(right_leg, walkSpeed, walkDegree * 1.85F, false, 0F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(left_leg, walkSpeed, walkDegree * 1.85F, true, 0F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, walkDegree * 0.4F, false, 2F, -0.01F, limbSwing, limbSwingAmount);
            this.swing(tail, walkSpeed, walkDegree * 0.5F, false, 1F, 0F, limbSwing, limbSwingAmount);
        }
        this.faceTarget(netHeadYaw, headPitch, 1, head);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
