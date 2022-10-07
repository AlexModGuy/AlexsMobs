package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelRoadrunner extends AdvancedEntityModel<EntityRoadrunner> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox left_wing;
    private final AdvancedModelBox right_wing;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox beak;
    private final AdvancedModelBox right_spin;
    private final AdvancedModelBox left_spin;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox left_knee;
    private final AdvancedModelBox left_foot;
    private final AdvancedModelBox right_leg;
    private final AdvancedModelBox right_knee;
    private final AdvancedModelBox right_foot;

    public ModelRoadrunner() {
        texWidth = 64;
        texHeight = 64;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -7.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(23, 14).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 7.0F, 0.0F, false);

        left_wing = new AdvancedModelBox(this, "left_wing");
        left_wing.setRotationPoint(2.0F, -1.0F, -3.0F);
        body.addChild(left_wing);
        setRotationAngle(left_wing, -0.0873F, 0.1309F, -0.1745F);
        left_wing.setTextureOffset(0, 14).addBox(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 11.0F, 0.0F, false);

        right_wing = new AdvancedModelBox(this, "right_wing");
        right_wing.setRotationPoint(-2.0F, -1.0F, -3.0F);
        body.addChild(right_wing);
        setRotationAngle(right_wing, -0.0873F, -0.1309F, 0.1745F);
        right_wing.setTextureOffset(0, 14).addBox(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 11.0F, 0.0F, true);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, -1.6F, 4.0F);
        body.addChild(tail);
        setRotationAngle(tail, 0.6545F, 0.0F, 0.0F);
        tail.setTextureOffset(0, 0).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 13.0F, 0.0F, false);

        neck = new AdvancedModelBox(this, "neck");
        neck.setRotationPoint(0.0F, -0.7F, -2.9F);
        body.addChild(neck);
        setRotationAngle(neck, 0.6545F, 0.0F, 0.0F);
        neck.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -1.3F, 3.0F, 8.0F, 3.0F, 0.0F, false);
        neck.setTextureOffset(0, 14).addBox(0.0F, -8.0F, -1.3F, 0.0F, 4.0F, 5.0F, 0.0F, false);

        beak = new AdvancedModelBox(this, "beak");
        beak.setRotationPoint(0.0F, -4.5F, -0.8F);
        neck.addChild(beak);
        setRotationAngle(beak, -0.3491F, 0.0F, 0.0F);
        beak.setTextureOffset(12, 14).addBox(-0.5F, -0.5F, -4.2F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        beak.setTextureOffset(47, 22).addBox(-1.0F, -0.1F, -2.1F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        right_spin = new AdvancedModelBox(this, "right_spin");
        right_spin.setRotationPoint(-1.5F, 4.5F, 1.5F);
        body.addChild(right_spin);
        setRotationAngle(right_spin, 0.5236F, 0.0F, 0.0F);
        right_spin.setTextureOffset(42, 9).addBox(-1.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, 0.0F, true);

        left_spin = new AdvancedModelBox(this, "left_spin");
        left_spin.setRotationPoint(1.5F, 4.5F, 1.5F);
        body.addChild(left_spin);
        setRotationAngle(left_spin, 0.5236F, 0.0F, 0.0F);
        left_spin.setTextureOffset(42, 9).addBox(-1.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, 0.0F, false);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(1.5F, 2.0F, 3.0F);
        body.addChild(left_leg);
        setRotationAngle(left_leg, 0.5672F, 0.0F, 0.0F);
        left_leg.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, false);

        left_knee = new AdvancedModelBox(this, "left_knee");
        left_knee.setRotationPoint(0.0F, 2.0F, 0.0F);
        left_leg.addChild(left_knee);
        setRotationAngle(left_knee, -1.1781F, 0.0F, 0.0F);
        left_knee.setTextureOffset(0, 14).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, false);

        left_foot = new AdvancedModelBox(this, "left_foot");
        left_foot.setRotationPoint(0.0F, 4.0F, 0.0F);
        left_knee.addChild(left_foot);
        setRotationAngle(left_foot, -0.9599F, 0.0F, 0.0F);
        left_foot.setTextureOffset(23, 14).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-1.5F, 2.0F, 3.0F);
        body.addChild(right_leg);
        setRotationAngle(right_leg, 0.5672F, 0.0F, 0.0F);
        right_leg.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, 0.0F, true);

        right_knee = new AdvancedModelBox(this, "right_knee");
        right_knee.setRotationPoint(0.0F, 2.0F, 0.0F);
        right_leg.addChild(right_knee);
        setRotationAngle(right_knee, -1.1781F, 0.0F, 0.0F);
        right_knee.setTextureOffset(0, 14).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 4.0F, 0.0F, 0.0F, true);

        right_foot = new AdvancedModelBox(this, "right_foot");
        right_foot.setRotationPoint(0.0F, 4.0F, 0.0F);
        right_knee.addChild(right_foot);
        setRotationAngle(right_foot, -0.9599F, 0.0F, 0.0F);
        right_foot.setTextureOffset(23, 14).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, true);   this.updateDefaultPose();
    }

    @Override
    public void setupAnim(EntityRoadrunner entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netneckYaw, float neckPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.9F;
        float walkDegree = 0.4F;
        float idleSpeed = 0.1F;
        float idleDegree = 0.4F;
        float runProgress = 5F * limbSwingAmount;
        float partialTick = Minecraft.getInstance().getFrameTime();
        boolean spinnyLegs = limbSwingAmount > 0.5F && entityIn.isMeep();
        float biteProgress = entityIn.prevAttackProgress + (entityIn.attackProgress - entityIn.prevAttackProgress) * partialTick;
        progressRotationPrev(neck, biteProgress, (float)Math.toRadians(55), 0, 0, 5F);
        progressRotationPrev(body, runProgress, (float)Math.toRadians(-5), 0, 0, 5F);
        progressRotationPrev(neck, runProgress, (float)Math.toRadians(25), 0, 0, 5F);
        progressRotationPrev(right_leg, runProgress, (float)Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(left_leg, runProgress, (float)Math.toRadians(-15), 0, 0, 5F);
        progressRotationPrev(tail, runProgress, (float)Math.toRadians(-10), 0, 0, 5F);
        progressRotationPrev(right_wing, runProgress, (float)Math.toRadians(-10),  (float)Math.toRadians(-30),  (float)Math.toRadians(40), 5F);
        progressRotationPrev(left_wing, runProgress, (float)Math.toRadians(-10),  (float)Math.toRadians(30),  (float)Math.toRadians(-40), 5F);
        this.swing(tail, idleSpeed, idleDegree, false, 0F, 0F, ageInTicks, 1);
        this.walk(neck, idleSpeed, idleDegree * 0.2F, false, 0F, -0.1F, ageInTicks, 1);
        this.walk(right_leg, walkSpeed, walkDegree * 1.2F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_leg, walkSpeed, walkDegree * 1.2F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_knee, walkSpeed, walkDegree * 0.5F, false, 0F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_knee, walkSpeed, walkDegree  * 0.5F, true, 0F, 0F, limbSwing, limbSwingAmount);
        this.flap(left_wing, walkSpeed, walkDegree, true, 2F, 0.1F, limbSwing, limbSwingAmount);
        this.flap(right_wing, walkSpeed, walkDegree, false, 2F, 0.1F, limbSwing, limbSwingAmount);
        this.left_foot.rotateAngleX = -(left_leg.rotateAngleX + left_knee.rotateAngleX + body.rotateAngleX) - (float)(Math.PI/ 2F);
        this.right_foot.rotateAngleX = -(right_leg.rotateAngleX + right_knee.rotateAngleX + body.rotateAngleX) - (float)(Math.PI/ 2F);
        this.left_leg.rotationPointY += 1.5F * (float) (Math.sin((double) (limbSwing * walkSpeed) + 2) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));
        this.right_leg.rotationPointY += 1.5F * (float) (Math.sin(-(double) (limbSwing * walkSpeed) - 2) * (double) limbSwingAmount * (double) walkDegree - (double) (limbSwingAmount * walkDegree));
        float partialTicks = Minecraft.getInstance().getFrameTime();
        float f = Mth.lerp(partialTicks, entityIn.oFlap, entityIn.wingRotation);
        float f1 = Mth.lerp(partialTicks, entityIn.oFlapSpeed, entityIn.destPos);
        float wingSwing = (Mth.sin(f) + 1.0F) * f1;
        this.flap(left_wing, 0.95F,  0.9F, true, 0F, 0.2F, wingSwing, wingSwing > 0 ? 1 : 0);
        this.flap(right_wing, 0.95F, 0.9F, false, 0F, 0.2F, wingSwing, wingSwing > 0 ? 1 : 0);
        this.faceTarget(netneckYaw, neckPitch, 1, neck);
        if(spinnyLegs){
            this.right_spin.showModel = true;
            this.left_spin.showModel = true;
            this.right_leg.showModel = false;
            this.left_leg.showModel = false;
            float wobbleXZ = 1F + (1F + (float)Math.sin(ageInTicks * 0.6F - 3F)) * 0.6F;
            float wobbleY = 1F + (1F + (float)Math.sin(ageInTicks * 0.6F - 2F)) * 0.6F;
            this.right_spin.setScale(1, wobbleY, wobbleXZ);
            this.left_spin.setScale(1, wobbleY, wobbleXZ);
            this.right_spin.rotateAngleX += limbSwingAmount * ageInTicks * 2;
            this.left_spin.rotateAngleX += limbSwingAmount * ageInTicks * 2;
            this.bob(body, walkSpeed, walkDegree * 5F, true, limbSwing, limbSwingAmount);
        }else{
            this.right_spin.showModel = false;
            this.left_spin.showModel = false;
            this.right_leg.showModel = true;
            this.left_leg.showModel = true;
            this.walk(tail, walkSpeed, walkDegree, false, 2F, 0F, limbSwing, limbSwingAmount);
            this.walk(neck, walkSpeed, walkDegree * 0.7F, false, 1F, -0.2F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed * 2F, walkDegree * 2F, false, limbSwing, limbSwingAmount);
        }
    }

    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.young) {
            float f = 1.75F;
            neck.setScale(f, f, f);
            neck.setShouldScaleChildren(true);
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.translate(0.0D, 1.5D, 0.125D);
            parts().forEach((p_228292_8_) -> {
                p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            });
            matrixStackIn.popPose();
            neck.setScale(1, 1, 1);
        } else {
            matrixStackIn.pushPose();
            parts().forEach((p_228290_8_) -> {
                p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
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
        return ImmutableList.of(root,body, neck, beak, left_leg, right_leg, left_wing, right_wing, left_leg, tail, right_spin, left_spin, beak, left_knee, right_knee, left_foot, right_foot);
    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
