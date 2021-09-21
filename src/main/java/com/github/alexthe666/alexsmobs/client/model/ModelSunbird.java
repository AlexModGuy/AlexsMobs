package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelSunbird extends AdvancedEntityModel<EntitySunbird> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox wingL1;
    private final AdvancedModelBox wingR2;
    private final AdvancedModelBox wingR1;
    private final AdvancedModelBox wingR3;
    private final AdvancedModelBox legL;
    private final AdvancedModelBox footL;
    private final AdvancedModelBox legR;
    private final AdvancedModelBox footR;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox crest;
    private final AdvancedModelBox beak;

    public ModelSunbird() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this);
        root.setPos(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setPos(0.0F, 0.0F, 0.0F);
        root.addChild(body);
        body.texOffs(0, 32).addBox(-3.0F, -5.0F, -7.0F, 6.0F, 5.0F, 14.0F, 0.0F, false);

        tail = new AdvancedModelBox(this);
        tail.setPos(0.0F, -5.0F, 7.0F);
        body.addChild(tail);
        tail.texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 20.0F, 0.0F, false);

        wingL1 = new AdvancedModelBox(this);
        wingL1.setPos(3.0F, -4.0F, -5.0F);
        body.addChild(wingL1);
        wingL1.texOffs(37, 11).addBox(0.0F, -1.0F, -1.0F, 9.0F, 1.0F, 3.0F, 0.0F, false);
        wingL1.texOffs(27, 32).addBox(0.0F, -0.5F, -2.0F, 13.0F, 0.0F, 10.0F, 0.0F, false);

        wingR2 = new AdvancedModelBox(this);
        wingR2.setPos(13.0F, -0.5F, 0.0F);
        wingL1.addChild(wingR2);
        wingR2.texOffs(0, 21).addBox(0.0F, 0.0F, -2.0F, 16.0F, 0.0F, 10.0F, 0.0F, false);

        wingR1 = new AdvancedModelBox(this);
        wingR1.setPos(-3.0F, -4.0F, -5.0F);
        body.addChild(wingR1);
        wingR1.texOffs(37, 11).addBox(-9.0F, -1.0F, -1.0F, 9.0F, 1.0F, 3.0F, 0.0F, true);
        wingR1.texOffs(27, 32).addBox(-13.0F, -0.5F, -2.0F, 13.0F, 0.0F, 10.0F, 0.0F, true);

        wingR3 = new AdvancedModelBox(this);
        wingR3.setPos(-13.0F, -0.5F, 0.0F);
        wingR1.addChild(wingR3);
        wingR3.texOffs(0, 21).addBox(-16.0F, 0.0F, -2.0F, 16.0F, 0.0F, 10.0F, 0.0F, true);

        legL = new AdvancedModelBox(this);
        legL.setPos(1.5F, 0.0F, 3.0F);
        body.addChild(legL);
        setRotationAngle(legL, 1.2654F, 0.0F, 0.0F);
        legL.texOffs(0, 21).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        footL = new AdvancedModelBox(this);
        footL.setPos(0.0F, 5.0F, 0.5F);
        legL.addChild(footL);
        footL.texOffs(6, 9).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 2.0F, 0.0F, false);

        legR = new AdvancedModelBox(this);
        legR.setPos(-1.5F, 0.0F, 3.0F);
        body.addChild(legR);
        setRotationAngle(legR, 1.2654F, 0.0F, 0.0F);
        legR.texOffs(0, 21).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 5.0F, 1.0F, 0.0F, true);

        footR = new AdvancedModelBox(this);
        footR.setPos(0.0F, 5.0F, 0.5F);
        legR.addChild(footR);
        footR.texOffs(6, 9).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 2.0F, 0.0F, true);

        neck = new AdvancedModelBox(this);
        neck.setPos(0.0F, -2.55F, -6.95F);
        body.addChild(neck);
        neck.texOffs(37, 0).addBox(-1.5F, -1.45F, -7.05F, 3.0F, 3.0F, 7.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setPos(0.0F, 0.05F, -7.05F);
        neck.addChild(head);
        head.texOffs(0, 0).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        crest = new AdvancedModelBox(this);
        crest.setPos(0.0F, -2.0F, -2.5F);
        head.addChild(crest);
        crest.texOffs(0, 9).addBox(0.0F, -3.0F, -2.5F, 0.0F, 3.0F, 5.0F, 0.0F, false);

        beak = new AdvancedModelBox(this);
        beak.setPos(0.0F, 1.0F, -4.0F);
        head.addChild(beak);
        beak.texOffs(0, 32).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, tail, wingL1, wingR2, wingR1, wingR3, legL, footL, legR, footR, neck, head, crest, beak);
    }

    @Override
    public void setupAnim(EntitySunbird entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flySpeed = 0.2F;
        float flyDegree = 0.6F;
        this.flap(wingR1, flySpeed, flyDegree, false, 0F, 0F, ageInTicks, 1);
        this.flap(wingL1, flySpeed, flyDegree, true, 0F, 0F, ageInTicks, 1);
        this.flap(wingR2, flySpeed, flyDegree, false, 1.2F, 0F, ageInTicks, 1);
        this.flap(wingR3, flySpeed, flyDegree, true, 1.2F, 0F, ageInTicks, 1);
        this.walk(tail, flySpeed, flyDegree * 0.2F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(legL, flySpeed, flyDegree * 0.2F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.walk(legR, flySpeed, flyDegree * 0.2F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.swing(legL, flySpeed, flyDegree * 0.1F, false, 3F, 0.2F, limbSwing, limbSwingAmount);
        this.swing(legR, flySpeed, flyDegree * 0.1F, true, 3F, 0.2F, limbSwing, limbSwingAmount);
        this.bob(body, flySpeed, flyDegree * 6F, false, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 2, neck, head);
        float partialTick = Minecraft.getInstance().getFrameTime();
        float birdPitch = entityIn.prevBirdPitch + (entityIn.birdPitch - entityIn.prevBirdPitch) * partialTick;
        this.body.xRot = birdPitch * ((float)Math.PI / 180F);

    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.xRot = x;
        AdvancedModelBox.yRot = y;
        AdvancedModelBox.zRot = z;
    }
}