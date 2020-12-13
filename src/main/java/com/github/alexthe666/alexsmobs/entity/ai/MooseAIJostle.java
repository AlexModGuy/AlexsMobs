package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class MooseAIJostle extends Goal {

    private static final EntityPredicate JOSTLE_PREDICATE = (new EntityPredicate()).setDistance(16D).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired();
    protected EntityMoose targetMoose;
    private EntityMoose moose;
    private World world;
    private float angle;

    public MooseAIJostle(EntityMoose moose) {
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        this.moose = moose;
        this.world = moose.world;
    }

    @Override
    public boolean shouldExecute() {
        if (this.moose.isJostling() || !moose.isAntlered() || this.moose.isChild() || this.moose.getAttackTarget() != null || this.moose.jostleCooldown > 0) {
            return false;
        }
        if(this.moose.instantlyTriggerJostleAI || this.moose.getRNG().nextInt(30) == 0){
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

    public void startExecuting(){
        this.moose.jostleTimer = 0;
        this.angle = 0;
       setJostleDirection(this.moose.getRNG().nextBoolean());
    }

    public void setJostleDirection(boolean dir){
        this.moose.jostleDirection = dir;
        this.targetMoose.jostleDirection = dir;
    }

    public void resetTask() {
        this.moose.setJostling(false);
        this.moose.setJostlingPartner(null);
        this.moose.jostleTimer = 0;
        this.angle = 0;
        this.moose.getNavigator().clearPath();
        if (this.targetMoose != null) {
            this.targetMoose.setJostling(false);
            this.targetMoose.setJostlingPartner(null);
            this.targetMoose.jostleTimer = 0;
            this.targetMoose = null;
        }

    }

    public void tick() {
        if(targetMoose != null){
            this.moose.faceEntity(targetMoose, 360, 180);
            this.moose.setJostling(true);
            double dist = this.moose.getDistance(targetMoose);
            if (dist < 3.5F) {
                this.moose.getNavigator().clearPath();
                this.moose.getMoveHelper().strafe(-0.5F, 0);
            } else if(dist > 4F) {
                this.moose.setJostling(false);
                this.moose.getNavigator().tryMoveToEntityLiving(targetMoose, 1);
            }else{
                this.moose.faceEntity(targetMoose, 360, 180);
                //perfect jostle condition
                if(moose.jostleDirection){
                    if(angle < 30){
                        angle++;
                    }
                    this.moose.getMoveHelper().strafe(0, -0.2F);
                }
                if(!moose.jostleDirection){
                    if(angle > -30){
                        angle--;
                    }
                    this.moose.getMoveHelper().strafe(0, 0.2F);
                }
                if(this.moose.getRNG().nextInt(55) == 0 && this.moose.isOnGround()){
                    moose.pushBackJostling(targetMoose, 0.2F);
                }

                moose.setJostleAngle(angle);
                if(this.moose.jostleTimer % 60 == 0 || this.moose.getRNG().nextInt(80) == 0){
                    this.setJostleDirection(!moose.jostleDirection);
                }
                this.moose.jostleTimer++;
                this.targetMoose.jostleTimer++;
                if(this.moose.jostleTimer > 1000){
                    moose.isAirBorne = true;
                    if(moose.isOnGround()){
                        moose.pushBackJostling(targetMoose, 0.9F);
                    }
                    if(targetMoose.isOnGround()){
                        targetMoose.pushBackJostling(moose, 0.9F);
                    }
                    this.moose.jostleTimer = 0;
                    this.targetMoose.jostleTimer = 0;
                    this.moose.jostleCooldown = 500 + this.moose.getRNG().nextInt(2000);
                    this.targetMoose.jostleTimer = 0;
                    this.targetMoose.jostleCooldown = 500 + this.targetMoose.getRNG().nextInt(2000);
                    this.resetTask();
                }
            }
        }

    }


    @Override
    public boolean shouldContinueExecuting() {
        return !this.moose.isChild() && this.moose.isAntlered() && this.moose.getAttackTarget() == null && targetMoose != null && this.targetMoose.isAntlered() && targetMoose.isAlive() && moose.jostleCooldown == 0 && targetMoose.jostleCooldown == 0;
    }

    @Nullable
    private EntityMoose getNearbyMoose() {
        List<EntityMoose> listOfMeese = this.world.getTargettableEntitiesWithinAABB(EntityMoose.class, JOSTLE_PREDICATE, this.moose, this.moose.getBoundingBox().grow(16.0D));
        double lvt_2_1_ = 1.7976931348623157E308D;
        EntityMoose lvt_4_1_ = null;
        Iterator var5 = listOfMeese.iterator();

        while (var5.hasNext()) {
            EntityMoose lvt_6_1_ = (EntityMoose) var5.next();
            if (this.moose.canJostleWith(lvt_6_1_) && this.moose.getDistanceSq(lvt_6_1_) < lvt_2_1_) {
                lvt_4_1_ = lvt_6_1_;
                lvt_2_1_ = this.moose.getDistanceSq(lvt_6_1_);
            }
        }

        return lvt_4_1_;
    }

}
