package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class TameableAIRide extends Goal {

    private CreatureEntity tameableEntity;
    private LivingEntity player;
    private double speed;

    public TameableAIRide(CreatureEntity dragon, double speed) {
        this.tameableEntity = dragon;
        this.speed = speed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (tameableEntity.getControllingPassenger() instanceof LivingEntity) {
            player = (LivingEntity) tameableEntity.getControllingPassenger();

            return player instanceof PlayerEntity;
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
        if (player.moveForward != 0) {
            Vector3d lookVec = player.getLookVec();
            if (player.moveForward < 0) {
                lookVec = lookVec.rotateYaw((float) Math.PI);
            }
            x += lookVec.x * 10;
            z += lookVec.z * 10;
            if(tameableEntity instanceof IFlyingAnimal){
                y += lookVec.y * 10;
            }
        }
        tameableEntity.moveStrafing = player.moveStrafing * 0.35F;
        tameableEntity.stepHeight = 1;
        tameableEntity.getMoveHelper().setMoveTo(x, y, z, speed);
    }
}
