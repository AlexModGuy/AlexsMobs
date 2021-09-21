package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.google.common.base.Predicate;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class GrizzlyBearAIFleeBees extends Goal {
    private final double farSpeed;
    private final double nearSpeed;
    private final float avoidDistance;
    private final Predicate<Bee> avoidTargetSelector;
    protected EntityGrizzlyBear entity;
    protected Bee closestLivingEntity;
    private Path path;

    public GrizzlyBearAIFleeBees(EntityGrizzlyBear entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        this.avoidTargetSelector = new Predicate<Bee>() {
            public boolean apply(@Nullable Bee p_apply_1_) {
                return p_apply_1_.isAlive() && GrizzlyBearAIFleeBees.this.entity.getSensing().hasLineOfSight(p_apply_1_) && !GrizzlyBearAIFleeBees.this.entity.isAlliedTo(p_apply_1_) && p_apply_1_.getRemainingPersistentAngerTime() > 0;
            }
        };
        this.entity = entityIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (this.entity.isTame()) {
            return false;
        }
        if(this.entity.isSitting() && !entity.forcedSit){
            this.entity.setOrderedToSit(false);
        }
        if(this.entity.isSitting()){
            return false;
        }
        List<Bee> beeEntities = this.entity.level.getEntitiesOfClass(Bee.class, this.entity.getBoundingBox().inflate((double) avoidDistance, 8.0D, (double) avoidDistance), this.avoidTargetSelector);
        if (beeEntities.isEmpty()) {
            return false;
        } else {
            this.closestLivingEntity = beeEntities.get(0);
            Vec3 vec3d = LandRandomPos.getPosAway(this.entity, 16, 7, new Vec3(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ()));
            if (vec3d == null) {
                return false;
            } else if (this.closestLivingEntity.distanceToSqr(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.distanceToSqr(this.entity)) {
                return false;
            } else {
                this.path = entity.getNavigation().createPath(new BlockPos(vec3d.x, vec3d.y, vec3d.z), 0);
                return this.path != null;
            }
        }
    }

    public boolean canContinueToUse() {
        return !entity.getNavigation().isDone();
    }

    public void start() {
        entity.getNavigation().moveTo(this.path, farSpeed);
    }

    public void stop() {
        this.entity.getNavigation().stop();
        this.closestLivingEntity = null;
    }

    public void tick() {
        if(closestLivingEntity != null && closestLivingEntity.getRemainingPersistentAngerTime() <= 0){
            this.stop();
        }
        this.entity.getNavigation().setSpeedModifier(getRunSpeed());
    }

    public double getRunSpeed() {
        return 0.7F;
    }
}

