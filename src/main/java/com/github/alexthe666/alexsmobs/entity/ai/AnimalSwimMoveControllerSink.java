package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.MathHelper;

public class AnimalSwimMoveControllerSink extends MovementController {
    private final CreatureEntity entity;
    private float speedMulti;
    private float ySpeedMod = 1;
    private float yawLimit = 10.0F;

    public AnimalSwimMoveControllerSink(CreatureEntity entity, float speedMulti, float ySpeedMod) {
        super(entity);
        this.entity = entity;
        this.speedMulti = speedMulti;
        this.ySpeedMod = ySpeedMod;
    }

    public AnimalSwimMoveControllerSink(CreatureEntity entity, float speedMulti, float ySpeedMod, float yawLimit) {
        super(entity);
        this.entity = entity;
        this.speedMulti = speedMulti;
        this.ySpeedMod = ySpeedMod;
        this.yawLimit = yawLimit;
    }

    public void tick() {
        if (entity instanceof ISemiAquatic && ((ISemiAquatic) entity).shouldStopMoving()) {
            this.entity.setAIMoveSpeed(0.0F);
            return;
        }
        if (this.action == Action.MOVE_TO && !this.entity.getNavigator().noPath()) {
            double lvt_1_1_ = this.posX - this.entity.getPosX();
            double lvt_3_1_ = this.posY - this.entity.getPosY();
            double lvt_5_1_ = this.posZ - this.entity.getPosZ();
            double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
            if (lvt_7_1_ < 2.500000277905201E-7D) {
                this.mob.setMoveForward(0.0F);
            } else {
                float lvt_9_1_ = (float) (MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
                this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, lvt_9_1_, yawLimit);
                this.entity.renderYawOffset = this.entity.rotationYaw;
                this.entity.rotationYawHead = this.entity.rotationYaw;
                float lvt_10_1_ = (float) (this.speed * speedMulti * 3 * this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED));
                if (this.entity.isInWater()) {
                    if(lvt_3_1_ > 0 && entity.collidedHorizontally){
                        this.entity.setMotion(this.entity.getMotion().add(0.0D, 0.08F, 0.0D));
                    }else{
                        this.entity.setMotion(this.entity.getMotion().add(0.0D, (double) this.entity.getAIMoveSpeed() * lvt_3_1_ * 0.6D * ySpeedMod, 0.0D));
                    }
                    this.entity.setAIMoveSpeed(lvt_10_1_ * 0.02F);
                    float lvt_11_1_ = -((float) (MathHelper.atan2(lvt_3_1_, MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_)) * 57.2957763671875D));
                    lvt_11_1_ = MathHelper.clamp(MathHelper.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                    this.entity.rotationPitch = this.limitAngle(this.entity.rotationPitch, lvt_11_1_, 5.0F);
                    float lvt_12_1_ = MathHelper.cos(this.entity.rotationPitch * 0.017453292F);
                    float lvt_13_1_ = MathHelper.sin(this.entity.rotationPitch * 0.017453292F);
                    this.entity.moveForward = lvt_12_1_ * lvt_10_1_;
                    this.entity.moveVertical = -lvt_13_1_ * lvt_10_1_;
                } else {
                    this.entity.setAIMoveSpeed(lvt_10_1_ * 0.1F);
                }

            }
        } else {
            if(entity instanceof EntityMimicOctopus && !entity.isOnGround()){
                this.entity.setMotion(entity.getMotion().add(0, -0.02, 0));
            }
            this.entity.setAIMoveSpeed(0.0F);
            this.entity.setMoveStrafing(0.0F);
            this.entity.setMoveVertical(0.0F);
            this.entity.setMoveForward(0.0F);
        }
    }
}
