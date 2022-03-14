package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class KomodoDragonAIJostle  extends Goal {

    private static final TargetingConditions JOSTLE_PREDICATE = TargetingConditions.forNonCombat().range(16D).ignoreLineOfSight();
    protected EntityKomodoDragon targetKomodoDragon;
    private EntityKomodoDragon komodo;
    private Level world;
    private float angle;

    public KomodoDragonAIJostle(EntityKomodoDragon moose) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        this.komodo = moose;
        this.world = moose.level;
    }

    @Override
    public boolean canUse() {
        if (this.komodo.isJostling() || this.komodo.isOrderedToSit() || this.komodo.isVehicle() || this.komodo.shouldFollow() || komodo.isPassenger() || this.komodo.isBaby() || this.komodo.getTarget() != null || this.komodo.jostleCooldown > 0) {
            return false;
        }
        if(this.komodo.instantlyTriggerJostleAI || this.komodo.getRandom().nextInt(30) == 0){
            this.komodo.instantlyTriggerJostleAI = false;
            if (this.komodo.getJostlingPartner() instanceof EntityKomodoDragon) {
                targetKomodoDragon = (EntityKomodoDragon) komodo.getJostlingPartner();
                return targetKomodoDragon.jostleCooldown == 0;
            } else {
                EntityKomodoDragon possiblePartner = this.getNearbyKomodoDragon();
                if (possiblePartner != null) {
                    this.komodo.setJostlingPartner(possiblePartner);
                    possiblePartner.setJostlingPartner(komodo);
                    targetKomodoDragon = possiblePartner;
                    targetKomodoDragon.instantlyTriggerJostleAI = true;
                    return true;
                }
            }
        }
        return false;
    }

    public void start(){
        this.komodo.jostleTimer = 0;
        this.angle = 0;
        setJostleDirection(this.komodo.getRandom().nextBoolean());
    }

    public void setJostleDirection(boolean dir){
        this.komodo.jostleDirection = dir;
        this.targetKomodoDragon.jostleDirection = dir;
    }

    public void stop() {
        this.komodo.setJostling(false);
        this.komodo.setJostlingPartner(null);
        this.komodo.jostleTimer = 0;
        this.angle = 0;
        this.komodo.getNavigation().stop();
        if (this.targetKomodoDragon != null) {
            this.targetKomodoDragon.setJostling(false);
            this.targetKomodoDragon.setJostlingPartner(null);
            this.targetKomodoDragon.jostleTimer = 0;
            this.targetKomodoDragon = null;
        }

    }

    public void tick() {
        if(targetKomodoDragon != null){
            this.komodo.lookAt(targetKomodoDragon, 360, 180);
            this.komodo.setJostling(true);
            double dist = this.komodo.distanceTo(targetKomodoDragon);
            if (dist < 2.0F) {
                this.komodo.getNavigation().stop();
                this.komodo.getMoveControl().strafe(-0.5F, 0);
            } else if(dist > 2.7F) {
                this.komodo.setJostling(false);
                this.komodo.getNavigation().moveTo(targetKomodoDragon, 1);
            }else{
                this.komodo.lookAt(targetKomodoDragon, 360, 180);
                float f = komodo.getRandom().nextFloat() - 0.5F;
                //perfect jostle condition
                if(komodo.jostleDirection){
                    if(angle < 10){
                        angle += 1;
                    }else{
                        komodo.jostleDirection = false;
                    }
                    this.komodo.getMoveControl().strafe(f * 1, -0.4F);
                }
                if(!komodo.jostleDirection){
                    if(angle > -10){
                        angle -= 1;
                    }else{
                        komodo.jostleDirection = true;
                    }
                    this.komodo.getMoveControl().strafe(f * 1, 0.4F);
                }
                if(this.komodo.getRandom().nextInt(15) == 0 && this.komodo.isOnGround()){
                    komodo.pushBackJostling(targetKomodoDragon, 0.1F);
                }
                komodo.nextJostleAngleFromServer = angle;
                this.komodo.jostleTimer++;
                this.targetKomodoDragon.jostleTimer++;
                if(this.komodo.jostleTimer > 1000){
                    komodo.hasImpulse = true;
                    if(komodo.isOnGround()){
                        komodo.pushBackJostling(targetKomodoDragon, 0.4F);
                    }
                    if(targetKomodoDragon.isOnGround()){
                        targetKomodoDragon.pushBackJostling(komodo, 0.4F);
                    }
                    this.komodo.jostleTimer = 0;
                    this.targetKomodoDragon.jostleTimer = 0;
                    this.komodo.jostleCooldown = 700 + this.komodo.getRandom().nextInt(2000);
                    this.targetKomodoDragon.jostleTimer = 0;
                    this.targetKomodoDragon.jostleCooldown = 700 + this.targetKomodoDragon.getRandom().nextInt(2000);
                    this.stop();
                }
            }
        }

    }


    @Override
    public boolean canContinueToUse() {
        return !this.komodo.isBaby() && !this.komodo.isVehicle() && !this.komodo.isOrderedToSit() && this.komodo.getTarget() == null && targetKomodoDragon != null && targetKomodoDragon.isAlive() && komodo.jostleCooldown == 0 && targetKomodoDragon.jostleCooldown == 0;
    }

    @Nullable
    private EntityKomodoDragon getNearbyKomodoDragon() {
        List<EntityKomodoDragon> komodoDragons = this.world.getNearbyEntities(EntityKomodoDragon.class, JOSTLE_PREDICATE, this.komodo, this.komodo.getBoundingBox().inflate(16.0D));
        double lvt_2_1_ = 1.7976931348623157E308D;
        EntityKomodoDragon lvt_4_1_ = null;
        Iterator var5 = komodoDragons.iterator();

        while (var5.hasNext()) {
            EntityKomodoDragon lvt_6_1_ = (EntityKomodoDragon) var5.next();
            if (this.komodo.canJostleWith(lvt_6_1_) && this.komodo.distanceToSqr(lvt_6_1_) < lvt_2_1_) {
                lvt_4_1_ = lvt_6_1_;
                lvt_2_1_ = this.komodo.distanceToSqr(lvt_6_1_);
            }
        }

        return lvt_4_1_;
    }

}
