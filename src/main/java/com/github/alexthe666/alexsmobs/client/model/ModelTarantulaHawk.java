package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;

public class ModelTarantulaHawk extends AdvancedEntityModel<EntityTarantulaHawk> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox wing_left;
    private final AdvancedModelBox wing_right;
    private final AdvancedModelBox legback_left;
    private final AdvancedModelBox legback_right;
    private final AdvancedModelBox legmid_left;
    private final AdvancedModelBox legmid_right;
    private final AdvancedModelBox legfront_left;
    private final AdvancedModelBox legfront_right;
    private final AdvancedModelBox head;
    private final AdvancedModelBox fang_left;
    private final AdvancedModelBox fang_right;
    private final AdvancedModelBox antenna_left;
    private final AdvancedModelBox antenna_right;
    private final AdvancedModelBox abdomen;
    private final AdvancedModelBox stinger;

    public ModelTarantulaHawk() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -15.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(33, 54).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 10.0F, 0.0F, false);

        wing_left = new AdvancedModelBox(this, "wing_left");
        wing_left.setPos(1.0F, -3.0F, -3.0F);
        body.addChild(wing_left);
        setRotationAngle(wing_left, 0.0F, 0.0F, -0.1309F);
        wing_left.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -1.0F, 20.0F, 0.0F, 21.0F, 0.0F, false);

        wing_right = new AdvancedModelBox(this, "wing_right");
        wing_right.setPos(-1.0F, -3.0F, -3.0F);
        body.addChild(wing_right);
        setRotationAngle(wing_right, 0.0F, 0.0F, 0.1309F);
        wing_right.setTextureOffset(0, 0).addBox(-20.0F, 0.0F, -1.0F, 20.0F, 0.0F, 21.0F, 0.0F, true);

        legback_left = new AdvancedModelBox(this, "legback_left");
        legback_left.setPos(2.0F, 3.0F, 3.0F);
        body.addChild(legback_left);
        setRotationAngle(legback_left, 0.0F, -0.3054F, 0.0F);
        legback_left.setTextureOffset(0, 41).addBox(0.0F, -3.0F, 0.0F, 21.0F, 15.0F, 0.0F, 0.0F, false);

        legback_right = new AdvancedModelBox(this, "legback_right");
        legback_right.setPos(-2.0F, 3.0F, 3.0F);
        body.addChild(legback_right);
        setRotationAngle(legback_right, 0.0F, 0.3054F, 0.0F);
        legback_right.setTextureOffset(0, 41).addBox(-21.0F, -3.0F, 0.0F, 21.0F, 15.0F, 0.0F, 0.0F, true);

        legmid_left = new AdvancedModelBox(this, "legmid_left");
        legmid_left.setPos(2.0F, 3.0F, 0.0F);
        body.addChild(legmid_left);
        legmid_left.setTextureOffset(43, 38).addBox(0.0F, -3.0F, 0.0F, 19.0F, 15.0F, 0.0F, 0.0F, false);

        legmid_right = new AdvancedModelBox(this, "legmid_right");
        legmid_right.setPos(-2.0F, 3.0F, 0.0F);
        body.addChild(legmid_right);
        legmid_right.setTextureOffset(43, 38).addBox(-19.0F, -3.0F, 0.0F, 19.0F, 15.0F, 0.0F, 0.0F, true);

        legfront_left = new AdvancedModelBox(this, "legfront_left");
        legfront_left.setPos(2.0F, 3.0F, -3.0F);
        body.addChild(legfront_left);
        setRotationAngle(legfront_left, 0.0F, 0.2618F, 0.0F);
        legfront_left.setTextureOffset(41, 22).addBox(0.0F, -3.0F, 0.0F, 19.0F, 15.0F, 0.0F, 0.0F, false);

        legfront_right = new AdvancedModelBox(this, "legfront_right");
        legfront_right.setPos(-2.0F, 3.0F, -3.0F);
        body.addChild(legfront_right);
        setRotationAngle(legfront_right, 0.0F, -0.2618F, 0.0F);
        legfront_right.setTextureOffset(41, 22).addBox(-19.0F, -3.0F, 0.0F, 19.0F, 15.0F, 0.0F, 0.0F, true);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, 0.0F, -5.0F);
        body.addChild(head);
        head.setTextureOffset(0, 57).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 7.0F, 4.0F, 0.0F, false);

        fang_left = new AdvancedModelBox(this, "fang_left");
        fang_left.setPos(1.0F, 4.5F, -3.3F);
        head.addChild(fang_left);
        fang_left.setTextureOffset(0, 22).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, 0.0F, false);

        fang_right = new AdvancedModelBox(this, "fang_right");
        fang_right.setPos(-1.0F, 4.5F, -3.3F);
        head.addChild(fang_right);
        fang_right.setTextureOffset(0, 22).addBox(-2.0F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F, 0.0F, true);

        antenna_left = new AdvancedModelBox(this, "antenna_left");
        antenna_left.setPos(1.0F, -2.0F, -4.0F);
        head.addChild(antenna_left);
        setRotationAngle(antenna_left, 0.0F, -0.3927F, -0.3491F);
        antenna_left.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -8.0F, 0.0F, 11.0F, 8.0F, 0.0F, false);

        antenna_right = new AdvancedModelBox(this, "antenna_right");
        antenna_right.setPos(-1.0F, -2.0F, -4.0F);
        head.addChild(antenna_right);
        setRotationAngle(antenna_right, 0.0F, 0.3927F, 0.3491F);
        antenna_right.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -8.0F, 0.0F, 11.0F, 8.0F, 0.0F, true);

        abdomen = new AdvancedModelBox(this, "abdomen");
        abdomen.setPos(0.0F, -2.0F, 5.0F);
        body.addChild(abdomen);
        abdomen.setTextureOffset(0, 22).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 6.0F, 12.0F, 0.0F, false);

        stinger = new AdvancedModelBox(this, "stinger");
        stinger.setPos(0.0F, 3.0F, 12.0F);
        abdomen.addChild(stinger);
        stinger.setTextureOffset(9, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 5.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, abdomen, head, antenna_left, antenna_right, legback_left, legback_right, legfront_left, legfront_right, legmid_left, legmid_right, wing_left, wing_right, stinger, fang_left, fang_right);
    }

    @Override
    public void setupAnim(EntityTarantulaHawk entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.25F;
        float idleDegree = 0.25F;
        float walkSpeed = entity.isDragging() ? 2F : 0.8F;
        float walkDegree = 0.4F;
        float flySpeed = 0.25F;
        float flyDegree = 0.6F;
        float digSpeed = 0.85F;
        float digDegree = 0.6F;
        float partialTick = ageInTicks - entity.tickCount;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
        float dragProgress = entity.prevDragProgress + (entity.dragProgress - entity.prevDragProgress) * partialTick;
        float sitProgress = entity.prevSitProgress + (entity.sitProgress - entity.prevSitProgress) * partialTick;
        float digProgress = entity.prevDigProgress + (entity.digProgress - entity.prevDigProgress) * partialTick;
        float stingProgress = entity.prevAttackProgress + (entity.attackProgress - entity.prevAttackProgress) * partialTick;
        float walkProgress = 5F - flyProgress;
        float stingFlyProgress = stingProgress * flyProgress * 0.2F;
        float stingGroundProgress = stingProgress * walkProgress * 0.2F;
        float flyAngle = entity.prevFlyAngle + (entity.getFlyAngle() - entity.prevFlyAngle) * partialTick;
        this.flap(antenna_left, idleSpeed, idleDegree * 1, false, 1, 0, ageInTicks, 1);
        this.flap(antenna_right, idleSpeed, idleDegree * 1, true, 1, 0, ageInTicks, 1);
        this.walk(antenna_right, idleSpeed, idleDegree * 2F, true, -1, 0, ageInTicks, 1);
        this.walk(antenna_left, idleSpeed, idleDegree * 2F, false, -1, 0, ageInTicks, 1);
        this.flap(fang_right, idleSpeed, idleDegree * -0.5F, false, -1, 0, ageInTicks, 1);
        this.flap(fang_left, idleSpeed, idleDegree * 0.5F, false, -1, 0, ageInTicks, 1);
        this.walk(abdomen, idleSpeed, idleDegree * 0.4F, true, 0, 0.1F, ageInTicks, 1);
        progressPositionPrev(body, flyProgress, 0, -3, -2, 5F);
        progressPositionPrev(legfront_right, flyProgress, 0, -1, 2, 5F);
        progressPositionPrev(legfront_left, flyProgress, 0, -1, 2, 5F);
        progressRotationPrev(legfront_left, flyProgress, (float) Math.toRadians(35), (float) Math.toRadians(-20), (float) Math.toRadians(30), 5F);
        progressRotationPrev(legfront_right, flyProgress, (float) Math.toRadians(35), (float) Math.toRadians(20), (float) Math.toRadians(-30), 5F);
        progressRotationPrev(legmid_left, flyProgress, (float) Math.toRadians(35), (float) Math.toRadians(-35), (float) Math.toRadians(20), 5F);
        progressRotationPrev(legmid_right, flyProgress, (float) Math.toRadians(35), (float) Math.toRadians(35), (float) Math.toRadians(-20), 5F);
        progressRotationPrev(legback_left, flyProgress, (float) Math.toRadians(35), (float) Math.toRadians(-35), (float) Math.toRadians(20), 5F);
        progressRotationPrev(legback_right, flyProgress, (float) Math.toRadians(35), (float) Math.toRadians(35), (float) Math.toRadians(-20), 5F);
        progressRotationPrev(wing_left, flyProgress, 0, (float) Math.toRadians(35), 0, 5F);
        progressRotationPrev(wing_right, flyProgress, 0, (float) Math.toRadians(-35), 0, 5F);
        progressRotationPrev(head, flyProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(wing_left, walkProgress, (float) Math.toRadians(20), (float) Math.toRadians(-20), (float) Math.toRadians(20), 5F);
        progressRotationPrev(wing_right, walkProgress, (float) Math.toRadians(20), (float) Math.toRadians(20), (float) Math.toRadians(-20), 5F);
        progressRotationPrev(wing_right, walkProgress * limbSwingAmount, (float) Math.toRadians(20), (float) Math.toRadians(15), 0, 5F);
        progressRotationPrev(wing_left, walkProgress * limbSwingAmount, (float) Math.toRadians(20), (float) Math.toRadians(-15), 0, 5F);
        progressRotationPrev(head, dragProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(fang_right, dragProgress, 0, 0, (float) Math.toRadians(20), 5F);
        progressRotationPrev(fang_left, dragProgress, 0, 0, (float) Math.toRadians(-20), 5F);
        progressPositionPrev(head, dragProgress, 0, 3, -1, 5F);
        progressPositionPrev(fang_right, dragProgress, 0, 1, 0, 5F);
        progressPositionPrev(fang_left, dragProgress, 0, 1, 0, 5F);
        progressPositionPrev(body, sitProgress, 0, 7, 0, 5F);
        progressRotationPrev(legfront_right, sitProgress, 0, (float) Math.toRadians(-25), (float) Math.toRadians(27), 5F);
        progressRotationPrev(legfront_left, sitProgress, 0, (float) Math.toRadians(25), (float) Math.toRadians(-27), 5F);
        progressRotationPrev(legmid_right, sitProgress, 0, 0, (float) Math.toRadians(21), 5F);
        progressRotationPrev(legmid_left, sitProgress, 0, 0, (float) Math.toRadians(-21), 5F);
        progressRotationPrev(legback_right, sitProgress, 0, (float) Math.toRadians(25), (float) Math.toRadians(27), 5F);
        progressRotationPrev(legback_left, sitProgress, 0, (float) Math.toRadians(-25), (float) Math.toRadians(-27), 5F);
        progressRotationPrev(head, sitProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(abdomen, stingGroundProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(stinger, stingGroundProgress, (float) Math.toRadians(-30), 0, 0, 5F);
        progressRotationPrev(body, stingGroundProgress, (float) Math.toRadians(-40), 0, 0, 5F);
        progressPositionPrev(body, stingGroundProgress, 0, -2, 0, 5F);
        progressPositionPrev(abdomen, stingGroundProgress, 0, 0, 2, 5F);
        progressPositionPrev(stinger, stingGroundProgress, 0, 1, 0, 5F);
        progressRotationPrev(legfront_right, stingGroundProgress,  (float) Math.toRadians(40), 0, (float) Math.toRadians(-40), 5F);
        progressRotationPrev(legfront_left, stingGroundProgress,  (float) Math.toRadians(40), 0, (float) Math.toRadians(40), 5F);
        progressRotationPrev(legmid_right, stingGroundProgress,  (float) Math.toRadians(40), 0, (float) Math.toRadians(-10), 5F);
        progressRotationPrev(legmid_left, stingGroundProgress,  (float) Math.toRadians(40), 0, (float) Math.toRadians(10), 5F);
        progressRotationPrev(legback_left, stingGroundProgress,  (float) Math.toRadians(40), 0, (float) Math.toRadians(-10), 5F);
        progressRotationPrev(legback_right, stingGroundProgress,  (float) Math.toRadians(40), 0, (float) Math.toRadians(10), 5F);
        progressRotationPrev(body, stingFlyProgress, (float) Math.toRadians(-70), 0, 0, 5F);
        progressRotationPrev(abdomen, stingFlyProgress, (float) Math.toRadians(-50), 0, 0, 5F);
        progressRotationPrev(stinger, stingFlyProgress, (float) Math.toRadians(-30), 0, 0, 5F);
        progressPositionPrev(body, stingFlyProgress, 0, -5, 0, 5F);
        progressPositionPrev(abdomen, stingFlyProgress, 0, 0, 2, 5F);
        progressPositionPrev(stinger, 5F - stingProgress, 0, 0, -3, 5F);
        this.stinger.setScale(1F, 1F, 1F + stingProgress * 0.15F);
        progressRotationPrev(body, digProgress, (float) Math.toRadians(40), 0, 0, 5F);
        progressRotationPrev(head, digProgress, (float) Math.toRadians(-20), 0, 0, 5F);
        progressRotationPrev(legfront_right, digProgress,  (float) Math.toRadians(-50), 0, (float) Math.toRadians(20), 5F);
        progressRotationPrev(legfront_left, digProgress,  (float) Math.toRadians(-50), 0, (float) Math.toRadians(-20), 5F);
        progressRotationPrev(legmid_right, digProgress,  (float) Math.toRadians(-10), 0, (float) Math.toRadians(-10), 5F);
        progressRotationPrev(legmid_left, digProgress,  (float) Math.toRadians(-10), 0, (float) Math.toRadians(10), 5F);
        progressRotationPrev(legback_left, digProgress,  (float) Math.toRadians(-30), 0, (float) Math.toRadians(30), 5F);
        progressRotationPrev(legback_right, digProgress,  (float) Math.toRadians(-30), 0, (float) Math.toRadians(-30), 5F);
        this.swing(legfront_left, digSpeed, digDegree * 1, false, 1, -0.5F, ageInTicks, digProgress * 0.2F);
        this.swing(legfront_right, digSpeed, digDegree * 1, false, 1, 0.5F, ageInTicks, digProgress * 0.2F);
        this.swing(head, digSpeed, digDegree * 1, false, 0, 0F, ageInTicks, digProgress * 0.2F);

        if (flyProgress > 0) {
            this.bob(body, flySpeed, flyDegree * 5, false, ageInTicks, 1);
            this.flap(legfront_left, flySpeed, flyDegree * 0.5F, true, 1, 0.1F, ageInTicks, 1);
            this.flap(legfront_right, flySpeed, flyDegree * 0.5F, false, 1, 0.1F, ageInTicks, 1);
            this.flap(legmid_left, flySpeed, flyDegree * 0.5F, true, 2, 0.1F, ageInTicks, 1);
            this.flap(legmid_right, flySpeed, flyDegree * 0.5F, false, 2, 0.1F, ageInTicks, 1);
            this.flap(legback_left, flySpeed, flyDegree * 0.5F, true, 2, 0.1F, ageInTicks, 1);
            this.flap(legback_right, flySpeed, flyDegree * 0.5F, false, 2, 0.1F, ageInTicks, 1);
            this.walk(abdomen, flySpeed, flyDegree * 0.35F, false, 0, -0.1F, ageInTicks, 1);
            this.walk(head, flySpeed, flyDegree * 0.15F, true, 0, -0.1F, ageInTicks, 1);
            this.flap(wing_left, flySpeed * 7F, flyDegree, true, 0, 0.1F, ageInTicks, 1);
            this.flap(wing_right, flySpeed * 7F, flyDegree, false, 0, 0.1F, ageInTicks, 1);
        } else {
            this.swing(legback_right, walkSpeed, walkDegree * 1.2F, false, 0, 0.2F, limbSwing, limbSwingAmount);
            this.flap(legback_right, walkSpeed, walkDegree * 0.8F, false, -1.5F, 0.4F, limbSwing, limbSwingAmount);
            this.swing(legfront_right, walkSpeed, walkDegree, false, 0, -0.3F, limbSwing, limbSwingAmount);
            this.flap(legfront_right, walkSpeed, walkDegree * 0.8F, false, -1.5F, 0.4F, limbSwing, limbSwingAmount);
            this.swing(legmid_left, walkSpeed, walkDegree, false, 0, 0F, limbSwing, limbSwingAmount);
            this.flap(legmid_left, walkSpeed, walkDegree * 0.8F, false, -1.5F, -0.4F, limbSwing, limbSwingAmount);
            this.bob(body, walkSpeed * 2F, walkDegree * -3F, false, limbSwing, limbSwingAmount);
            float offsetleft = 2F;
            this.swing(legback_left, walkSpeed, -walkDegree * 1.2F, false, offsetleft, -0.2F, limbSwing, limbSwingAmount);
            this.flap(legback_left, walkSpeed, walkDegree * 0.8F, false, offsetleft - 1.5F, -0.4F, limbSwing, limbSwingAmount);
            this.swing(legfront_left, walkSpeed, -walkDegree, false, offsetleft, 0.3F, limbSwing, limbSwingAmount);
            this.flap(legfront_left, walkSpeed, walkDegree * 0.8F, false, offsetleft + 1.5F, -0.4F, limbSwing, limbSwingAmount);
            this.swing(legmid_right, walkSpeed, -walkDegree, false, offsetleft, 0, limbSwing, limbSwingAmount);
            this.flap(legmid_right, walkSpeed, walkDegree * 0.8F, false, offsetleft - 1.5F, 0.4F, limbSwing, limbSwingAmount);
            this.swing(abdomen, walkSpeed, walkDegree * 0.4F, false, 3, 0, limbSwing, limbSwingAmount);
            this.walk(head, walkSpeed, walkDegree * 0.4F, false, 3, 0, limbSwing, limbSwingAmount);
        }
        float f = (float) Math.toRadians(flyAngle);
        this.body.rotateAngleZ += f;
        if(dragProgress == 0){
            this.faceTarget(netHeadYaw, headPitch, 1.2F, head);
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}