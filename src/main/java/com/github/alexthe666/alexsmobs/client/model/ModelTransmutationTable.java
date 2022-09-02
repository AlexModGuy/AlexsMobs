package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelTransmutationTable extends AdvancedEntityModel<Entity> {
    private final AdvancedModelBox base;
    private final AdvancedModelBox star;
    private final AdvancedModelBox portal;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox leftElbow;
    private final AdvancedModelBox leftHand;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox rightElbow;
    private final AdvancedModelBox rightHand;

    public ModelTransmutationTable(float scale) {
        texWidth = 64;
        texHeight = 64;

        base = new AdvancedModelBox(this);
        base.setRotationPoint(0.0F, 24.0F, 0.0F);
        base.setTextureOffset(0, 0).addBox(-7.0F, -5.0F, -7.0F, 14.0F, 5.0F, 14.0F, scale, false);

        star = new AdvancedModelBox(this);
        star.setRotationPoint(0.0F, -12.5F, 0.0F);
        base.addChild(star);
        star.setTextureOffset(0, 20).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, scale, false);

        portal = new AdvancedModelBox(this);
        portal.setRotationPoint(0.0F, -12.5F, 0.0F);
        base.addChild(portal);
        portal.setTextureOffset(21, 20).addBox(-4.1F, -4.5F, 0.0F, 9.0F, 9.0F, 0.0F, scale, false);

        leftArm = new AdvancedModelBox(this);
        leftArm.setRotationPoint(6.6F, -3.3F, 0.0F);
        base.addChild(leftArm);
        setRotationAngle(leftArm, 0.0F, 0.0F, 0.3054F);
        leftArm.setTextureOffset(13, 39).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 7.0F, 2.0F, scale, false);

        leftElbow = new AdvancedModelBox(this);
        leftElbow.setRotationPoint(-1.0F, -6.0F, 0.0F);
        leftArm.addChild(leftElbow);
        setRotationAngle(leftElbow, 0.0F, 0.0F, 0.4363F);
        leftElbow.setTextureOffset(0, 36).addBox(-3.0F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, scale -0.1F, false);

        leftHand = new AdvancedModelBox(this);
        leftHand.setRotationPoint(-3.8F, -0.8F, 0.0F);
        leftElbow.addChild(leftHand);
        setRotationAngle(leftHand, 0.0F, 0.0F, -0.7418F);
        leftHand.setTextureOffset(31, 39).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale, false);
        leftHand.setTextureOffset(32, 30).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, scale, false);

        rightArm = new AdvancedModelBox(this);
        rightArm.setRotationPoint(-6.6F, -3.3F, 0.0F);
        base.addChild(rightArm);
        setRotationAngle(rightArm, 0.0F, 0.0F, -0.3054F);
        rightArm.setTextureOffset(13, 39).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 7.0F, 2.0F, scale, true);

        rightElbow = new AdvancedModelBox(this);
        rightElbow.setRotationPoint(1.0F, -6.0F, 0.0F);
        rightArm.addChild(rightElbow);
        setRotationAngle(rightElbow, 0.0F, 0.0F, -0.4363F);
        rightElbow.setTextureOffset(0, 36).addBox(-2.0F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, scale -0.1F, true);

        rightHand = new AdvancedModelBox(this);
        rightHand.setRotationPoint(3.8F, -0.8F, 0.0F);
        rightElbow.addChild(rightHand);
        setRotationAngle(rightHand, 0.0F, 0.0F, 0.7418F);
        rightHand.setTextureOffset(31, 39).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale, true);
        rightHand.setTextureOffset(32, 30).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, scale, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(base);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(base, star, portal, leftArm, rightArm, leftElbow, rightElbow, leftHand, rightHand);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    public void animate(TileEntityTransmutationTable beak, float partialTick) {
        this.resetToDefaultPose();
        float ageInTicks = beak.ticksExisted + partialTick;
        this.flap(leftArm, 0.5F, 0.2F, false, 0F, 0.1F, ageInTicks, 1F);
        this.flap(rightArm, 0.5F, -0.2F, false, 0F, -0.1F, ageInTicks, 1F);
        this.flap(leftElbow, 0.5F, 0.1F, true, 1F, 0F, ageInTicks, 1F);
        this.flap(rightElbow, 0.5F, -0.1F, true, 1F, 0F, ageInTicks, 1F);
        this.flap(leftHand, 0.5F, 0.1F, true, -1F, 0F, ageInTicks, 1F);
        this.flap(rightHand, 0.5F, -0.1F, true, -1F, 0F, ageInTicks, 1F);
        this.flap(portal, 0.5F, -0.1F, true, -3F, 0F, ageInTicks, 1F);
        this.bob(star, 0.25F, 1F, false, ageInTicks, 1F);
        this.bob(portal, 0.25F, 1F, false, ageInTicks, 1F);
        this.portal.setScale(0.35F * (float)Math.sin(ageInTicks * 0.5F + 1F) + 1.35F, 0.1F * (float)Math.cos(ageInTicks * 0.5F + 1F) + 1.1F, 1F);
        this.star.rotateAngleY = ageInTicks * 0.1F;
    }


    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}