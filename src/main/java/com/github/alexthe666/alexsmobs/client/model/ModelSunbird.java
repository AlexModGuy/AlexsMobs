package com.github.alexthe666.alexsmobs.client.model;// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.github.alexthe666.alexsmobs.entity.EntitySunbird;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class ModelSunbird extends AdvancedEntityModel<EntitySunbird> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;
    private final AdvancedModelBox hair;
    private final AdvancedModelBox left_wing;
    private final AdvancedModelBox left_wing1;
    private final AdvancedModelBox left_wing2;
    private final AdvancedModelBox right_wing;
    private final AdvancedModelBox right_wing1;
    private final AdvancedModelBox right_wing2;
    private final AdvancedModelBox tail1;
    private final AdvancedModelBox tail2;
    private final AdvancedModelBox left_leg;
    private final AdvancedModelBox left_foot;
    private final AdvancedModelBox right_leg;
    private final AdvancedModelBox right_foot;

    public ModelSunbird() {
        texWidth = 256;
        texHeight = 256;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);


        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, -13.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(106, 38).addBox(-7.0F, -5.0F, -11.0F, 14.0F, 12.0F, 23.0F, 0.0F, false);

        neck = new AdvancedModelBox(this);
        neck.setRotationPoint(0.0F, -1.0F, -12.0F);
        body.addChild(neck);
        neck.setTextureOffset(0, 38).addBox(-3.0F, -3.0F, -12.0F, 6.0F, 6.0F, 13.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, 1.0F, -13.0F);
        neck.addChild(head);
        head.setTextureOffset(0, 0).addBox(-4.0F, -5.0F, -7.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(12, 17).addBox(-2.0F, -2.0F, -12.0F, 4.0F, 3.0F, 5.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addBox(-1.0F, 1.0F, -12.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        hair = new AdvancedModelBox(this);
        hair.setRotationPoint(0.0F, -5.0F, 0.0F);
        head.addChild(hair);
        hair.setTextureOffset(0, 17).addBox(0.0F, -8.0F, -9.0F, 0.0F, 8.0F, 11.0F, 0.0F, false);

        left_wing = new AdvancedModelBox(this);
        left_wing.setRotationPoint(8.0F, -3.0F, -8.0F);
        body.addChild(left_wing);
        left_wing.setTextureOffset(0, 119).addBox(-1.0F, -3.0F, -5.2F, 15.0F, 5.0F, 6.0F, 0.0F, false);

        left_wing1 = new AdvancedModelBox(this);
        left_wing1.setRotationPoint(0.0F, -1.0F, 0.0F);
        left_wing.addChild(left_wing1);
        left_wing1.setTextureOffset(103, 80).addBox(-1.0F, 0.0F, -8.0F, 33.0F, 0.0F, 37.0F, 0.0F, false);

        left_wing2 = new AdvancedModelBox(this);
        left_wing2.setRotationPoint(32.0F, 0.0F, 0.0F);
        left_wing1.addChild(left_wing2);
        left_wing2.setTextureOffset(0, 0).addBox(0.0F, 0.0F, -8.0F, 50.0F, 0.0F, 37.0F, 0.0F, false);

        right_wing = new AdvancedModelBox(this);
        right_wing.setRotationPoint(-8.0F, -3.0F, -8.0F);
        body.addChild(right_wing);
        right_wing.setTextureOffset(0, 119).addBox(-14.0F, -3.0F, -5.2F, 15.0F, 5.0F, 6.0F, 0.0F, true);

        right_wing1 = new AdvancedModelBox(this);
        right_wing1.setRotationPoint(0.0F, -1.0F, 0.0F);
        right_wing.addChild(right_wing1);
        right_wing1.setTextureOffset(103, 80).addBox(-32.0F, 0.0F, -8.0F, 33.0F, 0.0F, 37.0F, 0.0F, true);

        right_wing2 = new AdvancedModelBox(this);
        right_wing2.setRotationPoint(-32.0F, 0.0F, 0.0F);
        right_wing1.addChild(right_wing2);
        right_wing2.setTextureOffset(0, 0).addBox(-50.0F, 0.0F, -8.0F, 50.0F, 0.0F, 37.0F, 0.0F, true);

        tail1 = new AdvancedModelBox(this);
        tail1.setRotationPoint(0.0F, -5.0F, 12.0F);
        body.addChild(tail1);
        tail1.setTextureOffset(0, 80).addBox(-23.0F, 0.0F, 0.0F, 32.0F, 0.0F, 38.0F, 0.0F, false);

        tail2 = new AdvancedModelBox(this);
        tail2.setRotationPoint(-6.0F, 0.0F, 38.0F);
        tail1.addChild(tail2);
        tail2.setTextureOffset(0, 38).addBox(-16.0F, 0.0F, 0.0F, 32.0F, 0.0F, 41.0F, 0.0F, false);

        left_leg = new AdvancedModelBox(this);
        left_leg.setRotationPoint(3.0F, 8.0F, 8.0F);
        body.addChild(left_leg);
        left_leg.setTextureOffset(0, 58).addBox(-2.0F, -1.0F, -5.0F, 5.0F, 4.0F, 8.0F, 0.0F, false);

        left_foot = new AdvancedModelBox(this);
        left_foot.setRotationPoint(0.5F, 3.0F, -2.0F);
        left_leg.addChild(left_foot);
        setRotationAngle(left_foot, 0.0436F, 0.0F, 0.0F);
        left_foot.setTextureOffset(22, 66).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 3.0F, 5.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this);
        right_leg.setRotationPoint(-3.0F, 8.0F, 8.0F);
        body.addChild(right_leg);
        right_leg.setTextureOffset(0, 58).addBox(-3.0F, -1.0F, -5.0F, 5.0F, 4.0F, 8.0F, 0.0F, true);

        right_foot = new AdvancedModelBox(this);
        right_foot.setRotationPoint(-0.5F, 3.0F, -2.0F);
        right_leg.addChild(right_foot);
        setRotationAngle(right_foot, 0.0436F, 0.0F, 0.0F);
        right_foot.setTextureOffset(22, 66).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 3.0F, 5.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, hair, body, tail1, tail2, left_wing, left_wing1, left_wing2, right_wing, right_wing1, right_wing2, left_leg, right_leg, right_foot, left_foot, neck, head);
    }

    @Override
    public void setupAnim(EntitySunbird entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flySpeed = 0.2F;
        float flyDegree = 0.6F;
        this.flap(right_wing, flySpeed, flyDegree, false, 0F, 0F, ageInTicks, 1);
        this.flap(left_wing, flySpeed, flyDegree, true, 0F, 0F, ageInTicks, 1);
        this.flap(right_wing2, flySpeed, flyDegree, false, -1.2F, 0F, ageInTicks, 1);
        this.flap(left_wing2, flySpeed, flyDegree, true, -1.2F, 0F, ageInTicks, 1);
        this.swing(tail1, flySpeed, flyDegree * 0.1F, false, 1F, 0F, ageInTicks, 1);
        this.walk(tail1, flySpeed, flyDegree * 0.2F, false, 1F, 0F, limbSwing, limbSwingAmount);
        this.walk(left_leg, flySpeed, flyDegree * 0.2F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.walk(right_leg, flySpeed, flyDegree * 0.2F, false, 3F, 0F, limbSwing, limbSwingAmount);
        this.bob(body, flySpeed, flyDegree * 6F, false, limbSwing, limbSwingAmount);
        this.faceTarget(netHeadYaw, headPitch, 1, neck, head);
        float partialTick = Minecraft.getInstance().getFrameTime();
        float birdPitch = entityIn.prevBirdPitch + (entityIn.birdPitch - entityIn.prevBirdPitch) * partialTick;
        this.body.rotateAngleX = birdPitch * ((float)Math.PI / 180F);

    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}