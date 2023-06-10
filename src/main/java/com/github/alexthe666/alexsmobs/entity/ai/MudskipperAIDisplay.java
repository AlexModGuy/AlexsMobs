package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class MudskipperAIDisplay extends Goal {

    private static final TargetingConditions JOSTLE_PREDICATE = TargetingConditions.forNonCombat().range(16D).ignoreLineOfSight();
    protected EntityMudskipper partner;
    private EntityMudskipper mudskipper;
    private Level world;
    private float angle;
    private Vec3 center = null;

    public MudskipperAIDisplay(EntityMudskipper mudskipper) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        this.mudskipper = mudskipper;
        this.world = mudskipper.level;
    }

    @Override
    public boolean canUse() {
        if (this.mudskipper.isDisplaying() || this.mudskipper.shouldFollow() || this.mudskipper.isOrderedToSit() || this.mudskipper.isInLove()  || this.mudskipper.isVehicle() || mudskipper.isPassenger() || this.mudskipper.isBaby() || this.mudskipper.getTarget() != null || this.mudskipper.onGround() || this.mudskipper.displayCooldown > 0) {
            return false;
        }
        if(this.mudskipper.instantlyTriggerDisplayAI || this.mudskipper.getRandom().nextInt(30) == 0){
            this.mudskipper.instantlyTriggerDisplayAI = false;
            if (this.mudskipper.getDisplayingPartner() instanceof EntityMudskipper) {
                partner = (EntityMudskipper) mudskipper.getDisplayingPartner();
                return partner.displayCooldown == 0;
            } else {
                EntityMudskipper possiblePartner = this.getNearbyMudskipper();
                if (possiblePartner != null) {
                    this.mudskipper.setDisplayingPartner(possiblePartner);
                    possiblePartner.setDisplayingPartner(mudskipper);
                    partner = possiblePartner;
                    partner.instantlyTriggerDisplayAI = true;
                    return true;
                }
            }
        }
        return false;
    }

    public void start(){
        this.mudskipper.displayTimer = 0;
        this.angle = 0;
        setDisplayDirection(this.mudskipper.getRandom().nextBoolean());
    }

    public void setDisplayDirection(boolean dir){
        this.mudskipper.displayDirection = dir;
        this.partner.displayDirection = !dir;
    }

    public void stop() {
        this.center = null;
        this.mudskipper.setDisplaying(false);
        this.mudskipper.setDisplayingPartner(null);
        this.mudskipper.displayTimer = 0;
        this.angle = 0;
        this.mudskipper.getNavigation().stop();
        if (this.partner != null) {
            this.partner.setDisplaying(false);
            this.partner.setDisplayingPartner(null);
            this.partner.displayTimer = 0;
            this.partner = null;
        }

    }

    public void tick() {
        if(partner != null){
            if(center == null || mudskipper.getRandom().nextInt(100) == 0){
                center = new Vec3((mudskipper.getX() + partner.getX()) / 2F, (mudskipper.getY() + partner.getY()) / 2F, (mudskipper.getZ() + partner.getZ()) / 2F);
            }
            this.mudskipper.setDisplaying(true);
            float x = (float)(mudskipper.getX() - partner.getX());
            float y = Math.abs((float)(mudskipper.getY() - partner.getY()));
            float z = (float)(mudskipper.getZ() - partner.getZ());
            double distXZ = Math.sqrt((x * x + z * z));

            if (distXZ > 3F) {
                mudskipper.getNavigation().moveTo(partner, 1F);
            }else{
                float speed = mudskipper.getRandom().nextFloat() * 0.5F + 0.8F;
                if(mudskipper.displayDirection){
                    if(angle < 180){
                        angle += 10;
                    }else{
                        mudskipper.displayDirection = false;
                    }
                }
                if(!mudskipper.displayDirection){
                    if(angle > -180){
                        angle -= 10;
                    }else{
                        mudskipper.displayDirection = true;
                    }
                }
                if(distXZ < 0.8F){
                    if(this.mudskipper.onGround() && this.partner.onGround()){
                        this.mudskipper.lookAt(this.partner, 360, 360);
                        this.setDisplayDirection(!mudskipper.displayDirection);
                        this.mudskipper.openMouth(10 + this.mudskipper.getRandom().nextInt(20));
                        this.partner.setDeltaMovement(partner.getDeltaMovement().add(0.2F * mudskipper.getRandom().nextFloat(), 0.35F, 0.2F * mudskipper.getRandom().nextFloat()));
                    }
                }
                Vec3 circle = getCirclingPosOf(center, 1.5F + mudskipper.getRandom().nextFloat());
                Vec3 dirVec = circle.subtract(mudskipper.position());
                float headAngle = -(float) (Mth.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
                this.mudskipper.getNavigation().moveTo(circle.x, circle.y, circle.z, speed);
                mudskipper.setYRot(headAngle);
                mudskipper.yHeadRot = headAngle;
                mudskipper.yBodyRot = headAngle;
                mudskipper.nextDisplayAngleFromServer = angle;
                this.mudskipper.displayTimer++;
                this.partner.displayTimer++;
                if(this.mudskipper.displayTimer > 400 || y > 2.0F){
                    this.mudskipper.getNavigation().stop();
                    this.partner.getNavigation().stop();
                    mudskipper.hasImpulse = true;
                    this.mudskipper.displayTimer = 0;
                    this.partner.displayTimer = 0;
                    this.mudskipper.displayCooldown = 200 + this.mudskipper.getRandom().nextInt(200);
                    this.partner.displayTimer = 0;
                    this.partner.displayCooldown = 200 + this.partner.getRandom().nextInt(200);
                    this.stop();
                }
            }
        }
    }

    public Vec3 getCirclingPosOf(Vec3 center, double circleDistance) {
        float cir = (0.01745329251F * angle);
        double extraX = circleDistance * Mth.sin((cir));
        double extraZ = circleDistance * Mth.cos(cir);
        return center.add(extraX, 0, extraZ);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mudskipper.isBaby() && !this.mudskipper.shouldFollow() && !this.mudskipper.isOrderedToSit() && !this.mudskipper.isInLove() && !this.mudskipper.isVehicle() && this.mudskipper.getTarget() == null && partner != null && partner.isAlive() && mudskipper.displayCooldown == 0 && partner.displayCooldown == 0;
    }

    @Nullable
    private EntityMudskipper getNearbyMudskipper() {
        List<EntityMudskipper> skippers = this.world.getNearbyEntities(EntityMudskipper.class, JOSTLE_PREDICATE, this.mudskipper, this.mudskipper.getBoundingBox().inflate(16.0D));
        double lvt_2_1_ = 1.7976931348623157E308D;
        EntityMudskipper lvt_4_1_ = null;
        Iterator var5 = skippers.iterator();

        while (var5.hasNext()) {
            EntityMudskipper lvt_6_1_ = (EntityMudskipper) var5.next();
            if (this.mudskipper.canDisplayWith(lvt_6_1_) && this.mudskipper.distanceToSqr(lvt_6_1_) < lvt_2_1_) {
                lvt_4_1_ = lvt_6_1_;
                lvt_2_1_ = this.mudskipper.distanceToSqr(lvt_6_1_);
            }
        }

        return lvt_4_1_;
    }
}
