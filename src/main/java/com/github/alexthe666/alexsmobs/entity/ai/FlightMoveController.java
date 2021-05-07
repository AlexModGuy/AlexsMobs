package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FlightMoveController extends MovementController {
    private final MobEntity parentEntity;
    private float speedGeneral;
    private boolean shouldLookAtTarget;
    private boolean needsYSupport;


    public FlightMoveController(MobEntity bird, float speedGeneral, boolean shouldLookAtTarget, boolean needsYSupport) {
        super(bird);
        this.parentEntity = bird;
        this.shouldLookAtTarget = shouldLookAtTarget;
        this.speedGeneral = speedGeneral;
        this.needsYSupport = needsYSupport;
    }

    public FlightMoveController(MobEntity bird, float speedGeneral, boolean shouldLookAtTarget) {
        this(bird, speedGeneral, shouldLookAtTarget, false);
    }

    public FlightMoveController(MobEntity bird, float speedGeneral) {
        this(bird, speedGeneral, true);
    }

    public void tick() {
        if (this.action == MovementController.Action.MOVE_TO) {
            Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
            double d0 = vector3d.length();
            if (d0 < parentEntity.getBoundingBox().getAverageEdgeLength()) {
                this.action = MovementController.Action.WAIT;
                parentEntity.setMotion(parentEntity.getMotion().scale(0.5D));
            } else {
                parentEntity.setMotion(parentEntity.getMotion().add(vector3d.scale(this.speed * speedGeneral * 0.05D / d0)));
                if(needsYSupport){
                    double d1 = this.posY - parentEntity.getPosY();
                    parentEntity.setMotion(parentEntity.getMotion().add(0.0D, (double)parentEntity.getAIMoveSpeed() * speedGeneral * MathHelper.clamp(d1, -1, 1) * 0.6F, 0.0D));
                }
                if (parentEntity.getAttackTarget() == null || !shouldLookAtTarget) {
                    Vector3d vector3d1 = parentEntity.getMotion();
                    parentEntity.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.renderYawOffset = parentEntity.rotationYaw;
                } else{
                    double d2 = parentEntity.getAttackTarget().getPosX() - parentEntity.getPosX();
                    double d1 = parentEntity.getAttackTarget().getPosZ() - parentEntity.getPosZ();
                    parentEntity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                    parentEntity.renderYawOffset = parentEntity.rotationYaw;
                }
            }

        }
    }

    private boolean func_220673_a(Vector3d p_220673_1_, int p_220673_2_) {
        AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

        for (int i = 1; i < p_220673_2_; ++i) {
            axisalignedbb = axisalignedbb.offset(p_220673_1_);
            if (!this.parentEntity.world.hasNoCollisions(this.parentEntity, axisalignedbb)) {
                return false;
            }
        }

        return true;
    }
}