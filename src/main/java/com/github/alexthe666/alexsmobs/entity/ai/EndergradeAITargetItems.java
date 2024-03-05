package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.google.common.base.Predicate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class EndergradeAITargetItems<T extends ItemEntity> extends TargetGoal {
    protected final EndergradeAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected int executionChance;
    protected boolean mustUpdate;
    protected ItemEntity targetEntity;
    private EntityEndergrade endergrade;

    public EndergradeAITargetItems(EntityEndergrade creature, boolean checkSight) {
        this(creature, checkSight, false);
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public EndergradeAITargetItems(EntityEndergrade creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 10, checkSight, onlyNearby, null);
    }

    public EndergradeAITargetItems(EntityEndergrade creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.executionChance = chance;
        this.endergrade = creature;
        this.theNearestAttackableTargetSorter = new EndergradeAITargetItems.Sorter(creature);
        this.targetEntitySelector = new Predicate<ItemEntity>() {
            @Override
            public boolean apply(@Nullable ItemEntity item) {
                ItemStack stack = item.getItem();
                return !stack.isEmpty()  && endergrade.canTargetItem(stack);
            }
        };
        this.setFlags(EnumSet.of(Flag.MOVE));
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
            long worldTime = this.mob.level().getGameTime() % 10;
            if (this.mob.getNoActionTime() >= 100 && worldTime != 0) {
                return false;
            }
            if (this.mob.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                return false;
            }
        }
        List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);
        if (list.isEmpty()) {
            return false;
        } else {
            Collections.sort(list, this.theNearestAttackableTargetSorter);
            this.targetEntity = list.get(0);
            this.endergrade.stopWandering = true;
            this.endergrade.hasItemTarget = true;
            this.mustUpdate = false;
            return true;
        }
    }

    protected double getFollowDistance() {
        return 16D;
    }


    protected AABB getTargetableArea(double targetDistance) {
        Vec3 renderCenter = new Vec3(this.mob.getX() + 0.5, this.mob.getY()+ 0.5, this.mob.getZ() + 0.5D);
        double renderRadius = 9;
        AABB aabb = new AABB(-renderRadius, -renderRadius, -renderRadius, renderRadius, renderRadius, renderRadius);
        return aabb.move(renderCenter);
    }

    @Override
    public void start() {
        this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1);
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || this.targetEntity != null && !this.targetEntity.isAlive()) {
            this.stop();
        }else{
            this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1);
        }
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.distanceToSqr(this.targetEntity) < 2.0D && mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            ItemStack duplicate = this.targetEntity.getItem().copy();
            endergrade.bite();
            duplicate.setCount(1);
            if (!mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !mob.level().isClientSide) {
                mob.spawnAtLocation(mob.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
            }
            mob.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
            endergrade.onGetItem(targetEntity);
            this.targetEntity.getItem().shrink(1);
            stop();
        }
    }

    public void stop() {
        targetEntity = null;
        this.endergrade.hasItemTarget = false;
        endergrade.stopWandering = false;
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getMoveControl().hasWanted();
    }

    public record Sorter(Entity theEntity) implements Comparator<Entity> {
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            final double d0 = this.theEntity.distanceToSqr(p_compare_1_);
            final double d1 = this.theEntity.distanceToSqr(p_compare_2_);
            return Double.compare(d0, d1);
        }
    }

}