package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlightMoveController extends MoveControl {
    private final Mob parentEntity;
    private final float speedGeneral;
    private final boolean shouldLookAtTarget;
    private final boolean needsYSupport;


    public FlightMoveController(Mob bird, float speedGeneral, boolean shouldLookAtTarget, boolean needsYSupport) {
        super(bird);
        this.parentEntity = bird;
        this.shouldLookAtTarget = shouldLookAtTarget;
        this.speedGeneral = speedGeneral;
        this.needsYSupport = needsYSupport;
    }

    public FlightMoveController(Mob bird, float speedGeneral, boolean shouldLookAtTarget) {
        this(bird, speedGeneral, shouldLookAtTarget, false);
    }

    public FlightMoveController(Mob bird, float speedGeneral) {
        this(bird, speedGeneral, true);
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
            double d0 = vector3d.length();
            if (d0 < parentEntity.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
            } else {
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * speedGeneral * 0.05D / d0)));
                if (needsYSupport) {
                    double d1 = this.wantedY - parentEntity.getY();
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0.0D, (double) parentEntity.getSpeed() * speedGeneral * Mth.clamp(d1, -1, 1) * 0.6F, 0.0D));
                }
                if (parentEntity.getTarget() == null || !shouldLookAtTarget) {
                    Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * Mth.RAD_TO_DEG);
                    parentEntity.yBodyRot = parentEntity.getYRot();
                } else {
                    double d2 = parentEntity.getTarget().getX() - parentEntity.getX();
                    double d1 = parentEntity.getTarget().getZ() - parentEntity.getZ();
                    parentEntity.setYRot(-((float) Mth.atan2(d2, d1)) * Mth.RAD_TO_DEG);
                    parentEntity.yBodyRot = parentEntity.getYRot();
                }
            }

        } else if (this.operation == Operation.STRAFE) {
            this.operation = Operation.WAIT;
        }
    }
    private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
        AABB axisalignedbb = this.parentEntity.getBoundingBox();

        for (int i = 1; i < p_220673_2_; ++i) {
            axisalignedbb = axisalignedbb.move(p_220673_1_);
            if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
                return false;
            }
        }

        return true;
    }
}