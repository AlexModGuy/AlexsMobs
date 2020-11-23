package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class TameableAIRide extends Goal {

    private TameableEntity tameableEntity;
    private LivingEntity player;
    private double speed;

    public TameableAIRide(TameableEntity dragon, double speed) {
        this.tameableEntity = dragon;
        this.speed = speed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (tameableEntity.getControllingPassenger() instanceof LivingEntity) {
            player = (LivingEntity) tameableEntity.getControllingPassenger();

            return player != null;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        tameableEntity.getNavigator().clearPath();
    }

    @Override
    public void tick() {
        tameableEntity.getNavigator().clearPath();
        tameableEntity.setAttackTarget(null);
        double x = tameableEntity.getPosX();
        double y = tameableEntity.getPosY();
        double z = tameableEntity.getPosZ();
        if (player.moveStrafing != 0 || player.moveForward != 0) {
            Vector3d lookVec = player.getLookVec();
            if (player.moveForward < 0) {
                lookVec = lookVec.rotateYaw((float) Math.PI);
            } else if (player.moveStrafing > 0) {
                lookVec = lookVec.rotateYaw((float) Math.PI * 0.85f);
            } else if (player.moveStrafing < 0) {
                lookVec = lookVec.rotateYaw((float) Math.PI * -0.85f);
            }

            x += lookVec.x * 10;
            z += lookVec.z * 10;
        }
        tameableEntity.getMoveHelper().setMoveTo(x, y, z, speed);
    }
}
