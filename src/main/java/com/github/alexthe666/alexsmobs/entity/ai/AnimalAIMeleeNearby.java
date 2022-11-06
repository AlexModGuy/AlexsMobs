package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AnimalAIMeleeNearby extends Goal {
    private Mob entity;
    private int range;
    private double speed;
    private BlockPos fightStartPos = null;

    public AnimalAIMeleeNearby(Mob entity, int range, double speed) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.entity = entity;
        this.range = range;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null && entity.getTarget().isAlive() && !entity.isVehicle();
    }

    public void start(){
        fightStartPos = entity.getOnPos();

    }

    public void stop(){
        entity.getNavigation().stop();
        fightStartPos = null;
    }

    public void tick(){
        if(entity.distanceTo(entity.getTarget()) < 3F + entity.getBbWidth() + entity.getTarget().getBbWidth()){
            entity.doHurtTarget(entity.getTarget());
            entity.lookAt(entity.getTarget(), 180F, 180F);
        }else{
            if(fightStartPos != null){
                if(entity.distanceToSqr(Vec3.atCenterOf(fightStartPos)) < range * range){
                    entity.getNavigation().moveTo(entity.getTarget(), speed);

                }else{
                    entity.getNavigation().moveTo(fightStartPos.getX() + 0.5F, fightStartPos.getY() + 0.5F, fightStartPos.getZ() + 0.5F, 0.4F + speed);
                }

            }
        }

    }
}
