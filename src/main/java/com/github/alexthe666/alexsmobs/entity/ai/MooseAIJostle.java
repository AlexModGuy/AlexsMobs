package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class MooseAIJostle extends Goal {

    private static final TargetingConditions JOSTLE_PREDICATE = TargetingConditions.forNonCombat().range(16D).ignoreLineOfSight();
    protected EntityMoose targetMoose;
    private EntityMoose moose;
    private Level world;
    private float angle;

    public MooseAIJostle(EntityMoose moose) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        this.moose = moose;
        this.world = moose.level();
    }

    @Override
    public boolean canUse() {
        if (this.moose.isJostling() || !moose.isAntlered() || this.moose.isBaby() || this.moose.getTarget() != null || this.moose.jostleCooldown > 0) {
            return false;
        }
        if(this.moose.instantlyTriggerJostleAI || this.moose.getRandom().nextInt(30) == 0){
            this.moose.instantlyTriggerJostleAI = false;
            if (this.moose.getJostlingPartner() instanceof EntityMoose) {
                targetMoose = (EntityMoose) moose.getJostlingPartner();
                return targetMoose.jostleCooldown == 0;
            } else {
                EntityMoose possiblePartner = this.getNearbyMoose();
                if (possiblePartner != null) {
                    this.moose.setJostlingPartner(possiblePartner);
                    possiblePartner.setJostlingPartner(moose);
                    targetMoose = possiblePartner;
                    targetMoose.instantlyTriggerJostleAI = true;
                    return true;
                }
            }
        }
        return false;
    }

    public void start(){
        this.moose.jostleTimer = 0;
        this.angle = 0;
       setJostleDirection(this.moose.getRandom().nextBoolean());
    }

    public void setJostleDirection(boolean dir){
        this.moose.jostleDirection = dir;
        this.targetMoose.jostleDirection = dir;
    }

    public void stop() {
        this.moose.setJostling(false);
        this.moose.setJostlingPartner(null);
        this.moose.jostleTimer = 0;
        this.angle = 0;
        this.moose.getNavigation().stop();
        if (this.targetMoose != null) {
            this.targetMoose.setJostling(false);
            this.targetMoose.setJostlingPartner(null);
            this.targetMoose.jostleTimer = 0;
            this.targetMoose = null;
        }

    }

    public void tick() {
        if(targetMoose != null){
            this.moose.lookAt(targetMoose, 360, 180);
            this.moose.setJostling(true);
            float f = (float)(moose.getX() - targetMoose.getX());
            float f1 = Math.abs((float)(moose.getY() - targetMoose.getY()));
            float f2 = (float)(moose.getZ() - targetMoose.getZ());
            double distXZ = Math.sqrt((f * f + f2 * f2));
            if (distXZ < 4F) {
                this.moose.getNavigation().stop();
                this.moose.getMoveControl().strafe(-0.5F, 0);
            } else if(distXZ > 4.5F) {
                this.moose.setJostling(false);
                this.moose.getNavigation().moveTo(targetMoose, 1);
            }else{
                this.moose.lookAt(targetMoose, 360, 180);
                //perfect jostle condition
                if(moose.jostleDirection){
                    if(angle < 30){
                        angle++;
                    }
                    this.moose.getMoveControl().strafe(0, -0.2F);
                }
                if(!moose.jostleDirection){
                    if(angle > -30){
                        angle--;
                    }
                    this.moose.getMoveControl().strafe(0, 0.2F);
                }
                if(this.moose.getRandom().nextInt(55) == 0 && this.moose.onGround()){
                    moose.pushBackJostling(targetMoose, 0.2F);
                }
                if(this.moose.getRandom().nextInt(25) == 0 && this.moose.onGround()) {
                    moose.playJostleSound();
                }
                moose.setJostleAngle(angle);
                if(this.moose.jostleTimer % 60 == 0 || this.moose.getRandom().nextInt(80) == 0){
                    this.setJostleDirection(!moose.jostleDirection);
                }
                this.moose.jostleTimer++;
                this.targetMoose.jostleTimer++;
                if(this.moose.jostleTimer > 1000 || f1 > 2.0F){
                    moose.hasImpulse = true;
                    if(moose.onGround()){
                        moose.pushBackJostling(targetMoose, 0.9F);
                    }
                    if(targetMoose.onGround()){
                        targetMoose.pushBackJostling(moose, 0.9F);
                    }
                    this.moose.jostleTimer = 0;
                    this.targetMoose.jostleTimer = 0;
                    this.moose.jostleCooldown = 500 + this.moose.getRandom().nextInt(2000);
                    this.targetMoose.jostleTimer = 0;
                    this.targetMoose.jostleCooldown = 500 + this.targetMoose.getRandom().nextInt(2000);
                    this.stop();
                }
            }
        }

    }


    @Override
    public boolean canContinueToUse() {
        return !this.moose.isBaby() && this.moose.isAntlered() && this.moose.getTarget() == null && targetMoose != null && this.targetMoose.isAntlered() && targetMoose.isAlive() && moose.jostleCooldown == 0 && targetMoose.jostleCooldown == 0;
    }

    @Nullable
    private EntityMoose getNearbyMoose() {
        List<EntityMoose> listOfMeese = this.world.getNearbyEntities(EntityMoose.class, JOSTLE_PREDICATE, this.moose, this.moose.getBoundingBox().inflate(16.0D));
        double lvt_2_1_ = 1.7976931348623157E308D;
        EntityMoose lvt_4_1_ = null;
        Iterator var5 = listOfMeese.iterator();

        while (var5.hasNext()) {
            EntityMoose lvt_6_1_ = (EntityMoose) var5.next();
            if (this.moose.canJostleWith(lvt_6_1_) && this.moose.distanceToSqr(lvt_6_1_) < lvt_2_1_) {
                lvt_4_1_ = lvt_6_1_;
                lvt_2_1_ = this.moose.distanceToSqr(lvt_6_1_);
            }
        }

        return lvt_4_1_;
    }

}
