package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class GrizzlyBearAIFleeBees extends Goal {
    private final double farSpeed;
    private final double nearSpeed;
    private final float avoidDistance;
    private final Predicate<BeeEntity> avoidTargetSelector;
    protected EntityGrizzlyBear entity;
    protected BeeEntity closestLivingEntity;
    private Path path;

    public GrizzlyBearAIFleeBees(EntityGrizzlyBear entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        this.avoidTargetSelector = new Predicate<BeeEntity>() {
            public boolean apply(@Nullable BeeEntity p_apply_1_) {
                return p_apply_1_.isAlive() && GrizzlyBearAIFleeBees.this.entity.getEntitySenses().canSee(p_apply_1_) && !GrizzlyBearAIFleeBees.this.entity.isOnSameTeam(p_apply_1_) && p_apply_1_.getAngerTime() > 0;
            }
        };
        this.entity = entityIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (this.entity.isTamed()) {
            return false;
        }
        if(this.entity.isSitting() && !entity.forcedSit){
            this.entity.setSitting(false);
        }
        if(this.entity.isSitting()){
            return false;
        }
        List<BeeEntity> beeEntities = this.entity.world.getEntitiesWithinAABB(BeeEntity.class, this.entity.getBoundingBox().grow((double) avoidDistance, 8.0D, (double) avoidDistance), this.avoidTargetSelector);
        if (beeEntities.isEmpty()) {
            return false;
        } else {
            this.closestLivingEntity = beeEntities.get(0);
            Vector3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vector3d(this.closestLivingEntity.getPosX(), this.closestLivingEntity.getPosY(), this.closestLivingEntity.getPosZ()));
            if (vec3d == null) {
                return false;
            } else if (this.closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.getDistanceSq(this.entity)) {
                return false;
            } else {
                this.path = entity.getNavigator().getPathToPos(new BlockPos(vec3d.x, vec3d.y, vec3d.z), 0);
                return this.path != null;
            }
        }
    }

    public boolean shouldContinueExecuting() {
        return !entity.getNavigator().noPath();
    }

    public void startExecuting() {
        entity.getNavigator().setPath(this.path, farSpeed);
    }

    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.closestLivingEntity = null;
    }

    public void tick() {
        if(closestLivingEntity != null && closestLivingEntity.getAngerTime() <= 0){
            this.resetTask();
        }
        this.entity.getNavigator().setSpeed(getRunSpeed());
    }

    public double getRunSpeed() {
        return 0.7F;
    }
}

