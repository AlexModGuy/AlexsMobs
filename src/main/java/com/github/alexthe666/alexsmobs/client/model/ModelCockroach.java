package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ModelCockroach extends AdvancedEntityModel<EntityCockroach> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox abdomen;
    public final AdvancedModelBox left_leg_front;
    public final AdvancedModelBox right_leg_front;
    public final AdvancedModelBox left_leg_back;
    public final AdvancedModelBox right_leg_back;
    public final AdvancedModelBox left_leg_mid;
    public final AdvancedModelBox right_leg_mid;
    public final AdvancedModelBox left_wing;
    public final AdvancedModelBox right_wing;
    public final AdvancedModelBox neck;
    public final AdvancedModelBox head;
    public final AdvancedModelBox left_antenna;
    public final AdvancedModelBox right_antenna;

    public ModelCockroach() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        abdomen = new AdvancedModelBox(this, "abdomen");
        abdomen.setPos(0.0F, -1.6F, -1.0F);
        root.addChild(abdomen);
        abdomen.setTextureOffset(0, 12).addBox(-2.0F, -0.9F, -2.0F, 4.0F, 2.0F, 9.0F, 0.0F, false);

        left_leg_front = new AdvancedModelBox(this, "left_leg_front");
        left_leg_front.setPos(1.5F, 0.6F, -2.0F);
        abdomen.addChild(left_leg_front);
        setRotationAngle(left_leg_front, 0.0F, 0.0F, 0.1309F);
        left_leg_front.setTextureOffset(0, 24).addBox(0.0F, 0.0F, 0.0F, 7.0F, 0.0F, 3.0F, 0.0F, false);

        right_leg_front = new AdvancedModelBox(this, "right_leg_front");
        right_leg_front.setPos(-1.5F, 0.6F, -2.0F);
        abdomen.addChild(right_leg_front);
        setRotationAngle(right_leg_front, 0.0F, 0.0F, -0.1309F);
        right_leg_front.setTextureOffset(0, 24).addBox(-7.0F, 0.0F, 0.0F, 7.0F, 0.0F, 3.0F, 0.0F, true);

        left_leg_back = new AdvancedModelBox(this, "left_leg_back");
        left_leg_back.setPos(1.5F, 0.6F, 3.0F);
        abdomen.addChild(left_leg_back);
        setRotationAngle(left_leg_back, -0.0436F, -0.5236F, 0.1745F);
        left_leg_back.setTextureOffset(18, 12).addBox(0.0F, 0.0F, 0.0F, 7.0F, 0.0F, 5.0F, 0.0F, false);

        right_leg_back = new AdvancedModelBox(this, "right_leg_back");
        right_leg_back.setPos(-1.5F, 0.6F, 3.0F);
        abdomen.addChild(right_leg_back);
        setRotationAngle(right_leg_back, -0.0436F, 0.5236F, -0.1745F);
        right_leg_back.setTextureOffset(18, 12).addBox(-7.0F, 0.0F, 0.0F, 7.0F, 0.0F, 5.0F, 0.0F, true);

        left_leg_mid = new AdvancedModelBox(this, "left_leg_mid");
        left_leg_mid.setPos(1.5F, 0.6F, 0.0F);
        abdomen.addChild(left_leg_mid);
        setRotationAngle(left_leg_mid, -0.0436F, -0.2182F, 0.1309F);
        left_leg_mid.setTextureOffset(23, 20).addBox(0.0F, 0.0F, 0.0F, 7.0F, 0.0F, 4.0F, 0.0F, false);

        right_leg_mid = new AdvancedModelBox(this, "right_leg_mid");
        right_leg_mid.setPos(-1.5F, 0.6F, 0.0F);
        abdomen.addChild(right_leg_mid);
        setRotationAngle(right_leg_mid, -0.0436F, 0.2182F, -0.1309F);
        right_leg_mid.setTextureOffset(23, 20).addBox(-7.0F, 0.0F, 0.0F, 7.0F, 0.0F, 4.0F, 0.0F, true);

        left_wing = new AdvancedModelBox(this, "left_wing");
        left_wing.setPos(0.0F, -1.4F, -2.0F);
        abdomen.addChild(left_wing);
        left_wing.setTextureOffset(0, 0).addBox(0.0F, 0.0F, 0.0F, 3.0F, 1.0F, 10.0F, 0.0F, false);

        right_wing = new AdvancedModelBox(this, "right_wing");
        right_wing.setPos(0.0F, -1.4F, -2.0F);
        abdomen.addChild(right_wing);
        right_wing.setTextureOffset(0, 0).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 10.0F, 0.0F, true);

        neck = new AdvancedModelBox(this, "neck");
        neck.setPos(0.0F, 0.0F, -2.0F);
        abdomen.addChild(neck);
        neck.setTextureOffset(21, 25).addBox(-2.5F, -1.6F, -2.0F, 5.0F, 3.0F, 2.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, -0.1F, -2.0F);
        neck.addChild(head);
        head.setTextureOffset(0, 28).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 2.0F, 2.0F, 0.0F, false);

        left_antenna = new AdvancedModelBox(this, "left_antenna");
        left_antenna.setPos(0.1F, -1.0F, -2.0F);
        head.addChild(left_antenna);
        setRotationAngle(left_antenna, -0.2182F, -0.2618F, 0.1309F);
        left_antenna.setTextureOffset(17, 0).addBox(0.0F, 0.0F, -8.0F, 5.0F, 0.0F, 8.0F, 0.0F, false);

        right_antenna = new AdvancedModelBox(this, "right_antenna");
        right_antenna.setPos(-0.1F, -1.0F, -2.0F);
        head.addChild(right_antenna);
        setRotationAngle(right_antenna, -0.2182F, 0.2618F, -0.1309F);
        right_antenna.setTextureOffset(17, 0).addBox(-5.0F, 0.0F, -8.0F, 5.0F, 0.0F, 8.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, abdomen, neck, head, left_antenna, right_antenna, left_leg_front, right_leg_front, left_leg_mid, right_leg_mid, left_leg_back, right_leg_back, left_wing, right_wing);
    }

    @Override
    public void setupAnim(EntityCockroach entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.25F;
        float idleDegree = 0.25F;
        float flySpeed = 0.5F;
        float flyDegree = 0.5F;
        float walkSpeed = 1.25F;
        float walkDegree = 0.5F;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float danceProgress = entity.prevDanceProgress + (entity.danceProgress - entity.prevDanceProgress) * partialTick;
        progressRotationPrev(abdomen, danceProgress, Maths.rad(-70), 0, 0, 5F);
        progressRotationPrev(left_leg_front, danceProgress, 0, Maths.rad(-10), 0, 5F);
        progressRotationPrev(right_leg_front, danceProgress, 0, Maths.rad(10), 0, 5F);
        progressRotationPrev(left_leg_mid, danceProgress, 0, Maths.rad(-10), 0, 5F);
        progressRotationPrev(right_leg_mid, danceProgress, 0, Maths.rad(10), 0, 5F);
        progressPositionPrev(abdomen, danceProgress, 0, -15, 2, 5F);
        if (danceProgress > 0) {
            this.walk(left_antenna, 0.5F, 0.5F, false, -1, -0.05F, ageInTicks, 1);
            this.walk(right_antenna, 0.5F, 0.5F, false, -1, -0.05F, ageInTicks, 1);
            if (entity.hasMaracas()) {
                this.swing(abdomen, 0.5F, 0.15F, false, 0, 0F, ageInTicks, 1);
                this.flap(abdomen, 0.5F, 0.15F, false, 1, 0F, ageInTicks, 1);
                this.bob(abdomen, 0.25F, 10F, true, ageInTicks, 1);
                this.swing(right_leg_front, 0.5F, 0.5F, false, 0, -0.05F, ageInTicks, 1);
                this.swing(left_leg_front, 0.5F, 0.5F, false, 0, -0.05F, ageInTicks, 1);
                this.swing(right_leg_mid, 0.5F, 0.5F, false, 2, -0.05F, ageInTicks, 1);
                this.swing(left_leg_mid, 0.5F, 0.5F, false, 2, -0.05F, ageInTicks, 1);
            } else {
                float spinDegree = Mth.wrapDegrees(ageInTicks * 15F);
                abdomen.rotateAngleY = (float) (Math.toRadians(spinDegree) * danceProgress * 0.2F);
                this.bob(abdomen, 0.25F, 10F, true, ageInTicks, 1);
            }
        }
        this.swing(left_antenna, idleSpeed, idleDegree, true, 1, -0.1F, ageInTicks, 1);
        this.swing(right_antenna, idleSpeed, idleDegree, false, 1, -0.1F, ageInTicks, 1);
        this.walk(left_antenna, idleSpeed, idleDegree * 0.25F, false, -1, -0.05F, ageInTicks, 1);
        this.walk(right_antenna, idleSpeed, idleDegree * 0.25F, false, -1, -0.05F, ageInTicks, 1);
        this.walk(head, idleSpeed * 0.5F, idleDegree * 0.5F, false, 0, 0.1F, ageInTicks, 1);
        if (entity.randomWingFlapTick > 0) {
            this.swing(left_wing, flySpeed * 3.3F, flyDegree * 0.6F, true, 0, -0.2F, ageInTicks, 1);
            this.swing(right_wing, flySpeed * 3.3F, flyDegree * 0.6F, false, 0, -0.2F, ageInTicks, 1);
        }
        this.swing(right_leg_front, walkSpeed, walkDegree, false, -1, 0F, limbSwing, limbSwingAmount);
        this.swing(right_leg_back, walkSpeed, walkDegree, false, 1, 0F, limbSwing, limbSwingAmount);
        this.swing(left_leg_mid, walkSpeed, walkDegree, false, 0, 0F, limbSwing, limbSwingAmount);
        this.bob(abdomen, walkSpeed, walkDegree * 2.5F, true, limbSwing, limbSwingAmount);
        this.swing(left_leg_front, walkSpeed, walkDegree, true, 1, 0F, limbSwing, limbSwingAmount);
        this.swing(left_leg_back, walkSpeed, walkDegree, true, -1, 0F, limbSwing, limbSwingAmount);
        this.swing(right_leg_mid, walkSpeed, walkDegree, true, 0, 0F, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 1, head);
        if(entity.isHeadless()){
            head.showModel = false;
            left_antenna.showModel = false;
            right_antenna.showModel = false;
        }else{
            head.showModel = true;
            left_antenna.showModel = true;
            right_antenna.showModel = true;
        }
        if(entity.isBaby()){
            left_wing.showModel = false;
            right_wing.showModel = false;
        }else{
            left_wing.showModel = true;
            right_wing.showModel = true;
        }
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            this.head.setScale(1.5F, 1.5F, 1.5F);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);
            matrixStackIn.translate(0.0D, 0.815D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        } else {
            this.head.setScale(1F, 1F, 1F);
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
        }

    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}