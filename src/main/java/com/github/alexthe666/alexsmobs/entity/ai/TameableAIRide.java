package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TameableAIRide extends Goal {

    private final PathfinderMob tameableEntity;
    private LivingEntity player;
    private final double speed;
    private final boolean strafe;

    public TameableAIRide(PathfinderMob dragon, double speed) {
        this(dragon, speed, true);
    }

    public TameableAIRide(PathfinderMob dragon, double speed, boolean strafe) {
        this.tameableEntity = dragon;
        this.speed = speed;
        this.strafe = strafe;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (tameableEntity.getControllingPassenger() instanceof Player && tameableEntity.isVehicle()) {
            player = (Player) tameableEntity.getControllingPassenger();
            return true;
        } else {
            tameableEntity.setSprinting(false);
            return false;
        }
    }

    @Override
    public void start() {
        tameableEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        tameableEntity.maxUpStep = 1;
        tameableEntity.getNavigation().stop();
        tameableEntity.setTarget(null);
        double x = tameableEntity.getX();
        double y = tameableEntity.getY();
        double z = tameableEntity.getZ();
        if (strafe) {
            tameableEntity.xxa = player.xxa * 0.15F;
        }
        if (shouldMoveForward() && tameableEntity.isVehicle()) {
            tameableEntity.setSprinting(true);
            Vec3 lookVec = player.getLookAngle();
            if (shouldMoveBackwards()) {
                lookVec = lookVec.yRot((float) Math.PI);
            }
            x += lookVec.x * 10;
            z += lookVec.z * 10;
            y += modifyYPosition(lookVec.y);
            tameableEntity.getMoveControl().setWantedPosition(x, y, z, speed);
        } else {
            tameableEntity.setSprinting(false);
        }
    }

    public double modifyYPosition(double lookVecY) {
        return tameableEntity instanceof FlyingAnimal ? lookVecY * 10 : 0;
    }

    public boolean shouldMoveForward() {
        return player.zza != 0;
    }


    public boolean shouldMoveBackwards() {
        return player.zza < 0;
    }
}
