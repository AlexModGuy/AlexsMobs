package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.ITargetsDroppedItems;
import com.google.common.base.Predicate;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CreatureAITargetItems<T extends ItemEntity> extends TargetGoal {
    protected final CreatureAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected int executionChance;
    protected boolean mustUpdate;
    protected ItemEntity targetEntity;
    private ITargetsDroppedItems hunter;
    private int tickThreshold;
    private float radius = 9F;
    private int walkCooldown = 0;

    public CreatureAITargetItems(PathfinderMob creature, boolean checkSight) {
        this(creature, checkSight, false);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, int tickThreshold) {
        this(creature, checkSight, false, tickThreshold, 9);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }


    public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 10, checkSight, onlyNearby, null, 0);
    }

    public CreatureAITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
        this(creature, 10, checkSight, onlyNearby, null, tickThreshold);
        this.radius = radius;
    }


    public CreatureAITargetItems(PathfinderMob creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector, int ticksExisted) {
        super(creature, checkSight, onlyNearby);
        this.executionChance = chance;
        this.tickThreshold = ticksExisted;
        this.hunter = (ITargetsDroppedItems) creature;
        this.theNearestAttackableTargetSorter = new CreatureAITargetItems.Sorter(creature);
        this.targetEntitySelector = new Predicate<ItemEntity>() {
            @Override
            public boolean apply(@Nullable ItemEntity item) {
                ItemStack stack = item.getItem();
                return !stack.isEmpty()  && hunter.canTargetItem(stack) && item.tickCount > tickThreshold;
            }
        };
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isPassenger() || mob.isVehicle() && mob.getControllingPassenger() != null) {
            return false;
        }
        if(!mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()){
            return false;
        }
        if (!this.mustUpdate) {
            long worldTime = this.mob.level.getGameTime() % 10;
            if (this.mob.getNoActionTime() >= 100 && worldTime != 0) {
                return false;
            }
            if (this.mob.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                return false;
            }
        }
        List<ItemEntity> list = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);
        if (list.isEmpty()) {
            return false;
        } else {
            Collections.sort(list, this.theNearestAttackableTargetSorter);
            this.targetEntity = list.get(0);
            this.mustUpdate = false;
            this.hunter.onFindTarget(targetEntity);
            return true;
        }
    }

    protected double getFollowDistance() {
        return 16D;
    }


    protected AABB getTargetableArea(double targetDistance) {
        Vec3 renderCenter = new Vec3(this.mob.getX() + 0.5, this.mob.getY()+ 0.5, this.mob.getZ() + 0.5D);
        AABB aabb = new AABB(-radius, -radius, -radius, radius, radius, radius);
        return aabb.move(renderCenter);
    }

    @Override
    public void start() {
        moveTo();
        super.start();
    }

    protected void moveTo(){
        if(walkCooldown > 0){
            walkCooldown--;
        }else{
            this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1);
            walkCooldown = 30 + this.mob.getRandom().nextInt(40);
        }
    }

    public void stop() {
        super.stop();
        this.mob.getNavigation().stop();
        this.targetEntity = null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || this.targetEntity != null && !this.targetEntity.isAlive()) {
            this.stop();
            this.mob.getNavigation().stop();
        }else{
            moveTo();
        }
        if(targetEntity != null && this.mob.canSee(targetEntity) && this.mob.getBbWidth() > 2D && this.mob.isOnGround()){
            this.mob.getMoveControl().setWantedPosition(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), 1);
        }
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.distanceToSqr(this.targetEntity) < this.hunter.getMaxDistToItem() && mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            hunter.onGetItem(targetEntity);
            this.targetEntity.getItem().shrink(1);
            stop();
        }
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    @Override
    public boolean canContinueToUse() {
        boolean path = this.mob.getBbWidth() > 2D ||  !this.mob.getNavigation().isDone();
        return path && targetEntity != null && targetEntity.isAlive();
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }

}