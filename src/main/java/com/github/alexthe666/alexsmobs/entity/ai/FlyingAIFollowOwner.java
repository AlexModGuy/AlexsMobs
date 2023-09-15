package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class FlyingAIFollowOwner extends Goal {
    private final TamableAnimal tameable;
    private LivingEntity owner;
    private final LevelReader world;
    private final double followSpeed;
    private final PathNavigation navigator;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private float oldWaterCost;
    private final boolean teleportToLeaves;
    private final IFollower follower;

    public FlyingAIFollowOwner(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        this.tameable = tameable;
        this.world = tameable.level;
        this.followSpeed = speed;
        this.navigator = tameable.getNavigation();
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.teleportToLeaves = teleportToLeaves;
        follower = (IFollower)tameable;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LivingEntity livingentity = this.tameable.getOwner();
        if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.tameable.isOrderedToSit()) {
            return false;
        } else if (this.tameable.distanceToSqr(livingentity) < (double)(this.minDist * this.minDist) || isInCombat()) {
            return false;
        } else {
            this.owner = livingentity;
            return follower.shouldFollow();
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        if (this.tameable.isOrderedToSit() || isInCombat()) {
            return false;
        } else {
            return this.tameable.distanceToSqr(this.owner) > (double)(this.maxDist * this.maxDist);
        }
    }

    private boolean isInCombat() {
        Entity owner = tameable.getOwner();
        if(owner != null){
            return tameable.distanceTo(owner) < 30 && tameable.getTarget() != null && tameable.getTarget().isAlive();
        }
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tameable.getPathfindingMalus(BlockPathTypes.WATER);
        this.tameable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.owner = null;
        this.navigator.stop();
        this.tameable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        this.tameable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tameable.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.tameable.isLeashed() && !this.tameable.isPassenger()) {
                if (this.tameable.distanceToSqr(this.owner) >= 144.0D) {
                    this.tryToTeleportNearEntity();
                }
                follower.followEntity(tameable, owner, followSpeed);
            }
        }
    }

    private void tryToTeleportNearEntity() {
        BlockPos blockpos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomNumber(-3, 3);
            int k = this.getRandomNumber(-1, 1);
            int l = this.getRandomNumber(-3, 3);
            boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean tryToTeleportToLocation(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0D && Math.abs((double)z - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.tameable.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.tameable.getYRot(), this.tameable.getXRot());
            this.navigator.stop();
            return true;
        }
    }

    private boolean isTeleportFriendlyBlock(BlockPos pos) {
        if(this.world.getBlockState(pos).isAir()){
            BlockPos blockpos = pos.subtract(this.tameable.blockPosition());
            return this.world.noCollision(this.tameable, this.tameable.getBoundingBox().move(blockpos));
        }
        BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.world, pos.mutable());
        if (pathnodetype != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.world.getBlockState(pos.below());
            if (!this.teleportToLeaves && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.tameable.blockPosition());
                return this.world.noCollision(this.tameable, this.tameable.getBoundingBox().move(blockpos));
            }
        }
    }

    private int getRandomNumber(int min, int max) {
        return this.tameable.getRandom().nextInt(max - min + 1) + min;
    }
}

