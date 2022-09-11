package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class EtherealMoveController extends MoveControl {

    private final Mob parentEntity;
    private final float speedGeneral;

    public EtherealMoveController(Mob parentEntity, float speedGeneral) {
        super(parentEntity);
        this.parentEntity = parentEntity;
        this.speedGeneral = speedGeneral;
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
            double d0 = vector3d.length();

            parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * speedGeneral * 0.025D / d0)));

            double yAdd = this.wantedY - parentEntity.getY();
            if(d0 > 0.2F){
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0.0D, (double) parentEntity.getSpeed() * speedGeneral * Mth.clamp(yAdd, -1, 1) * 0.6F, 0.0D));
                Vec3 vector3d1 = parentEntity.getDeltaMovement();
                parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                parentEntity.yBodyRot = parentEntity.getYRot();
            }
        } else if (this.operation == Operation.STRAFE || this.operation == Operation.JUMPING) {
            this.operation = Operation.WAIT;
        }
    }

}
