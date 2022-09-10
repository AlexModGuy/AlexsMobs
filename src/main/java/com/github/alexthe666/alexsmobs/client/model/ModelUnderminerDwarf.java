package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityUnderminer;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class ModelUnderminerDwarf extends AdvancedEntityModel<EntityUnderminer> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox helmet;
    private final AdvancedModelBox beard;
    private final AdvancedModelBox leftArm;
    private final AdvancedModelBox rightArm;
    private final AdvancedModelBox leftLeg;
    private final AdvancedModelBox rightLeg;
    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
    public boolean crouching;
    public float swimAmount;

    public ModelUnderminerDwarf() {
        texWidth = 128;
        texHeight = 128;

        body = new AdvancedModelBox(this);
        body.setRotationPoint(0.0F, 12.0F, 0.0F);
        body.setTextureOffset(0, 36).addBox(-5.0F, -10.0F, -3.0F, 10.0F, 11.0F, 6.0F, 0.0F, false);

        head = new AdvancedModelBox(this);
        head.setRotationPoint(0.0F, -10.02F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(30, 24).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 8.0F, 9.0F, 0.0F, false);
        head.setTextureOffset(0, 15).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 8.0F, 9.0F, 0.1F, false);

        helmet = new AdvancedModelBox(this);
        helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(helmet);
        helmet.setTextureOffset(0, 0).addBox(-6.0F, -10.0F, -5.5F, 12.0F, 4.0F, 10.0F, 0.1F, false);

        beard = new AdvancedModelBox(this);
        beard.setRotationPoint(0.0F, 0.1F, -4.1F);
        head.addChild(beard);
        beard.setTextureOffset(0, 54).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 9.0F, 2.0F, 0.0F, false);

        leftArm = new AdvancedModelBox(this);
        leftArm.setRotationPoint(7.0F, -9.0F, 0.0F);
        body.addChild(leftArm);
        leftArm.setTextureOffset(45, 0).addBox(-2.0F, -1.0F, -2.5F, 4.0F, 13.0F, 5.0F, 0.0F, false);

        rightArm = new AdvancedModelBox(this);
        rightArm.setRotationPoint(-7.0F, -9.0F, 0.0F);
        body.addChild(rightArm);
        rightArm.setTextureOffset(45, 0).addBox(-2.0F, -1.0F, -2.5F, 4.0F, 13.0F, 5.0F, 0.0F, true);

        leftLeg = new AdvancedModelBox(this);
        leftLeg.setRotationPoint(2.0F, 2.0F, 0.0F);
        body.addChild(leftLeg);
        leftLeg.setTextureOffset(33, 42).addBox(-2.0F, -1.0F, -3.0F, 5.0F, 11.0F, 6.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this);
        rightLeg.setRotationPoint(-2.0F, 2.0F, 0.0F);
        body.addChild(rightLeg);
        rightLeg.setTextureOffset(33, 42).addBox(-3.0F, -1.0F, -3.0F, 5.0F, 11.0F, 6.0F, 0.0F, true);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(body);
    }

    @Override
    public void setupAnim(EntityUnderminer entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        setupHumanoidAnims(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(body, head, beard, helmet, rightLeg, leftLeg, rightArm, leftArm);
    }

    /*
        From here on out copied from HumanoidModel
     */
    public void setupHumanoidAnims(EntityUnderminer entityIn, float p_102867_, float p_102868_, float p_102869_, float p_102870_, float p_102871_) {
        boolean flag = entityIn.getFallFlyingTicks() > 4;
        boolean flag1 = entityIn.isVisuallySwimming();
        this.head.rotateAngleY = p_102870_ * ((float)Math.PI / 180F);
        if (flag) {
            this.head.rotateAngleX = (-(float)Math.PI / 4F);
        } else if (this.swimAmount > 0.0F) {
            if (flag1) {
                this.head.rotateAngleX = this.rotlerpRad(this.swimAmount, this.head.rotateAngleX, (-(float)Math.PI / 4F));
            } else {
                this.head.rotateAngleX = this.rotlerpRad(this.swimAmount, this.head.rotateAngleX, p_102871_ * ((float)Math.PI / 180F));
            }
        } else {
            this.head.rotateAngleX = p_102871_ * ((float)Math.PI / 180F);
        }
        float f = 1.0F;
        if (flag) {
            f = (float)entityIn.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        this.rightArm.rotateAngleX = Mth.cos(p_102867_ * 0.6662F + (float)Math.PI) * 2.0F * p_102868_ * 0.5F / f;
        this.leftArm.rotateAngleX = Mth.cos(p_102867_ * 0.6662F) * 2.0F * p_102868_ * 0.5F / f;
        this.rightArm.rotateAngleZ = 0.0F;
        this.leftArm.rotateAngleZ = 0.0F;
        this.rightLeg.rotateAngleX = Mth.cos(p_102867_ * 0.6662F) * 1.4F * p_102868_ / f;
        this.leftLeg.rotateAngleX = Mth.cos(p_102867_ * 0.6662F + (float)Math.PI) * 1.4F * p_102868_ / f;
        this.rightLeg.rotateAngleY = 0.0F;
        this.leftLeg.rotateAngleY = 0.0F;
        this.rightLeg.rotateAngleZ = 0.0F;
        this.leftLeg.rotateAngleZ = 0.0F;
        if (this.riding) {
            this.rightArm.rotateAngleX += (-(float)Math.PI / 5F);
            this.leftArm.rotateAngleX += (-(float)Math.PI / 5F);
            this.rightLeg.rotateAngleX = -1.4137167F;
            this.rightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.rightLeg.rotateAngleZ = 0.07853982F;
            this.leftLeg.rotateAngleX = -1.4137167F;
            this.leftLeg.rotateAngleY = (-(float)Math.PI / 10F);
            this.leftLeg.rotateAngleZ = -0.07853982F;
        }

        this.rightArm.rotateAngleY = 0.0F;
        this.leftArm.rotateAngleY = 0.0F;
        boolean flag2 = entityIn.getMainArm() == HumanoidArm.RIGHT;
        if (entityIn.isUsingItem()) {
            boolean flag3 = entityIn.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (flag3 == flag2) {
                this.poseRightArm(entityIn);
            } else {
                this.poseLeftArm(entityIn);
            }
        } else {
            boolean flag4 = flag2 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
            if (flag2 != flag4) {
                this.poseLeftArm(entityIn);
                this.poseRightArm(entityIn);
            } else {
                this.poseRightArm(entityIn);
                this.poseLeftArm(entityIn);
            }
        }

        this.setupAttackAnimation(entityIn, p_102869_);
        if (this.crouching) {
            this.body.rotateAngleX = 0.5F;
            this.rightArm.rotateAngleX += 0.4F;
            this.leftArm.rotateAngleX += 0.4F;
        }

        if (this.rightArmPose != HumanoidModel.ArmPose.SPYGLASS) {
            this.rightArm.rotateAngleZ += 1.0F * (Mth.cos(p_102869_ * 0.09F) * 0.05F + 0.05F);
            this.rightArm.rotateAngleX += 1.0F * Mth.sin(p_102869_ * 0.067F) * 0.05F;
        }

        if (this.leftArmPose != HumanoidModel.ArmPose.SPYGLASS) {
            this.leftArm.rotateAngleZ += -1.0F * (Mth.cos(p_102869_ * 0.09F) * 0.05F + 0.05F);
            this.leftArm.rotateAngleX += -1.0F * Mth.sin(p_102869_ * 0.067F) * 0.05F;
        }

        if (this.swimAmount > 0.0F) {
            float f5 = p_102867_ % 26.0F;
            HumanoidArm humanoidarm = this.getAttackArm(entityIn);
            float f1 = humanoidarm == HumanoidArm.RIGHT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
            float f2 = humanoidarm == HumanoidArm.LEFT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
            if (!entityIn.isUsingItem()) {
                if (f5 < 14.0F) {
                    this.leftArm.rotateAngleX = this.rotlerpRad(f2, this.leftArm.rotateAngleX, 0.0F);
                    this.rightArm.rotateAngleX = Mth.lerp(f1, this.rightArm.rotateAngleX, 0.0F);
                    this.leftArm.rotateAngleY = this.rotlerpRad(f2, this.leftArm.rotateAngleY, (float)Math.PI);
                    this.rightArm.rotateAngleY = Mth.lerp(f1, this.rightArm.rotateAngleY, (float)Math.PI);
                    this.leftArm.rotateAngleZ = this.rotlerpRad(f2, this.leftArm.rotateAngleZ, (float)Math.PI + 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
                    this.rightArm.rotateAngleZ = Mth.lerp(f1, this.rightArm.rotateAngleZ, (float)Math.PI - 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
                } else if (f5 >= 14.0F && f5 < 22.0F) {
                    float f6 = (f5 - 14.0F) / 8.0F;
                    this.leftArm.rotateAngleX = this.rotlerpRad(f2, this.leftArm.rotateAngleX, ((float)Math.PI / 2F) * f6);
                    this.rightArm.rotateAngleX = Mth.lerp(f1, this.rightArm.rotateAngleX, ((float)Math.PI / 2F) * f6);
                    this.leftArm.rotateAngleY = this.rotlerpRad(f2, this.leftArm.rotateAngleY, (float)Math.PI);
                    this.rightArm.rotateAngleY = Mth.lerp(f1, this.rightArm.rotateAngleY, (float)Math.PI);
                    this.leftArm.rotateAngleZ = this.rotlerpRad(f2, this.leftArm.rotateAngleZ, 5.012389F - 1.8707964F * f6);
                    this.rightArm.rotateAngleZ = Mth.lerp(f1, this.rightArm.rotateAngleZ, 1.2707963F + 1.8707964F * f6);
                } else if (f5 >= 22.0F && f5 < 26.0F) {
                    float f3 = (f5 - 22.0F) / 4.0F;
                    this.leftArm.rotateAngleX = this.rotlerpRad(f2, this.leftArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
                    this.rightArm.rotateAngleX = Mth.lerp(f1, this.rightArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
                    this.leftArm.rotateAngleY = this.rotlerpRad(f2, this.leftArm.rotateAngleY, (float)Math.PI);
                    this.rightArm.rotateAngleY = Mth.lerp(f1, this.rightArm.rotateAngleY, (float)Math.PI);
                    this.leftArm.rotateAngleZ = this.rotlerpRad(f2, this.leftArm.rotateAngleZ, (float)Math.PI);
                    this.rightArm.rotateAngleZ = Mth.lerp(f1, this.rightArm.rotateAngleZ, (float)Math.PI);
                }
            }

            float f7 = 0.3F;
            float f4 = 0.33333334F;
            this.leftLeg.rotateAngleX = Mth.lerp(this.swimAmount, this.leftLeg.rotateAngleX, 0.3F * Mth.cos(p_102867_ * 0.33333334F + (float)Math.PI));
            this.rightLeg.rotateAngleX = Mth.lerp(this.swimAmount, this.rightLeg.rotateAngleX, 0.3F * Mth.cos(p_102867_ * 0.33333334F));
        }
    }

    private void poseRightArm(EntityUnderminer p_102876_) {
        switch (this.rightArmPose) {
            case EMPTY:
                this.rightArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.rightArm.rotateAngleX = this.rightArm.rotateAngleX * 0.5F - 0.9424779F;
                this.rightArm.rotateAngleY = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                this.rightArm.rotateAngleX = this.rightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.rightArm.rotateAngleY = 0.0F;
                break;
            case THROW_SPEAR:
                this.rightArm.rotateAngleX = this.rightArm.rotateAngleX * 0.5F - (float)Math.PI;
                this.rightArm.rotateAngleY = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.rotateAngleY = -0.1F + this.head.rotateAngleY;
                this.leftArm.rotateAngleY = 0.1F + this.head.rotateAngleY + 0.4F;
                this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX;
                this.leftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX;
                break;
            case CROSSBOW_CHARGE:
                break;
            case CROSSBOW_HOLD:
                break;
            case SPYGLASS:
                this.rightArm.rotateAngleX = Mth.clamp(this.head.rotateAngleX - 1.9198622F - (p_102876_.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.rightArm.rotateAngleY = this.head.rotateAngleY - 0.2617994F;
                break;
            case TOOT_HORN:
                this.rightArm.rotateAngleX = Mth.clamp(this.head.rotateAngleX, -1.2F, 1.2F) - 1.4835298F;
                this.rightArm.rotateAngleY = this.head.rotateAngleY - ((float)Math.PI / 6F);
        }

    }

    private void poseLeftArm(EntityUnderminer p_102879_) {
        switch (this.leftArmPose) {
            case EMPTY:
                this.leftArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.leftArm.rotateAngleX = this.leftArm.rotateAngleX * 0.5F - 0.9424779F;
                this.leftArm.rotateAngleY = ((float)Math.PI / 6F);
                break;
            case ITEM:
                this.leftArm.rotateAngleX = this.leftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
                this.leftArm.rotateAngleY = 0.0F;
                break;
            case THROW_SPEAR:
                this.leftArm.rotateAngleX = this.leftArm.rotateAngleX * 0.5F - (float)Math.PI;
                this.leftArm.rotateAngleY = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.rotateAngleY = -0.1F + this.head.rotateAngleY - 0.4F;
                this.leftArm.rotateAngleY = 0.1F + this.head.rotateAngleY;
                this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX;
                this.leftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX;
                break;
            case CROSSBOW_CHARGE:
                break;
            case CROSSBOW_HOLD:
                break;
            case SPYGLASS:
                this.leftArm.rotateAngleX = Mth.clamp(this.head.rotateAngleX - 1.9198622F - (p_102879_.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.leftArm.rotateAngleY = this.head.rotateAngleY + 0.2617994F;
                break;
            case TOOT_HORN:
                this.leftArm.rotateAngleX = Mth.clamp(this.head.rotateAngleX, -1.2F, 1.2F) - 1.4835298F;
                this.leftArm.rotateAngleY = this.head.rotateAngleY + ((float)Math.PI / 6F);
        }

    }

    protected void setupAttackAnimation(EntityUnderminer p_102858_, float p_102859_) {
        if (!(this.attackTime <= 0.0F)) {
            HumanoidArm humanoidarm = this.getAttackArm(p_102858_);
            AdvancedModelBox modelpart = this.getArm(humanoidarm);
            float f = this.attackTime;
            this.body.rotateAngleY = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
            if (humanoidarm == HumanoidArm.LEFT) {
                this.body.rotateAngleY *= -1.0F;
            }

            this.rightArm.rotationPointZ = Mth.sin(this.body.rotateAngleY) * 5.0F;
            this.rightArm.rotationPointX = -Mth.cos(this.body.rotateAngleY) * 5.0F;
            this.leftArm.rotationPointZ = -Mth.sin(this.body.rotateAngleY) * 5.0F;
            this.leftArm.rotationPointX = Mth.cos(this.body.rotateAngleY) * 5.0F;
            this.rightArm.rotateAngleY += this.body.rotateAngleY;
            this.leftArm.rotateAngleY += this.body.rotateAngleY;
            this.leftArm.rotateAngleX += this.body.rotateAngleY;
            f = 1.0F - this.attackTime;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f1 = Mth.sin(f * (float)Math.PI);
            float f2 = Mth.sin(this.attackTime * (float)Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
            modelpart.rotateAngleX -= f1 * 1.2F + f2;
            modelpart.rotateAngleY += this.body.rotateAngleY * 2.0F;
            modelpart.rotateAngleZ += Mth.sin(this.attackTime * (float)Math.PI) * -0.4F;
        }
    }

    protected float rotlerpRad(float p_102836_, float p_102837_, float p_102838_) {
        float f = (p_102838_ - p_102837_) % ((float)Math.PI * 2F);
        if (f < -(float)Math.PI) {
            f += ((float)Math.PI * 2F);
        }

        if (f >= (float)Math.PI) {
            f -= ((float)Math.PI * 2F);
        }

        return p_102837_ + p_102836_ * f;
    }

    private float quadraticArmUpdate(float p_102834_) {
        return -65.0F * p_102834_ + p_102834_ * p_102834_;
    }


    public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {
        this.getArm(p_102854_).translateAndRotate(p_102855_);
    }

    protected AdvancedModelBox getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public AdvancedModelBox getHead() {
        return this.head;
    }

    private HumanoidArm getAttackArm(EntityUnderminer p_102857_) {
        HumanoidArm humanoidarm = p_102857_.getMainArm();
        return p_102857_.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
    }
}