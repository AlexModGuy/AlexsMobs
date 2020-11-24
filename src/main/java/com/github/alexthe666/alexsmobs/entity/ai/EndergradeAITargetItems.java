package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

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
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
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
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (this.goalOwner.isPassenger() || goalOwner.isBeingRidden() && goalOwner.getControllingPassenger() != null) {
            return false;
        }
        if(!goalOwner.getHeldItem(Hand.MAIN_HAND).isEmpty()){
            return false;
        }
        if (!this.mustUpdate) {
            long worldTime = this.goalOwner.world.getGameTime() % 10;
            if (this.goalOwner.getIdleTime() >= 100 && worldTime != 0) {
                return false;
            }
            if (this.goalOwner.getRNG().nextInt(this.executionChance) != 0 && worldTime != 0) {
                return false;
            }
        }
        List<ItemEntity> list = this.goalOwner.world.getEntitiesWithinAABB(ItemEntity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
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

    protected double getTargetDistance() {
        return 16D;
    }


    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        Vector3d renderCenter = new Vector3d(this.goalOwner.getPosX() + 0.5, this.goalOwner.getPosY()+ 0.5, this.goalOwner.getPosZ() + 0.5D);
        double renderRadius = 9;
        AxisAlignedBB aabb = new AxisAlignedBB(-renderRadius, -renderRadius, -renderRadius, renderRadius, renderRadius, renderRadius);
        return aabb.offset(renderCenter);
    }

    @Override
    public void startExecuting() {
        this.goalOwner.getMoveHelper().setMoveTo(this.targetEntity.getPosX(), this.targetEntity.getPosY(), this.targetEntity.getPosZ(), 1);
        super.startExecuting();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || this.targetEntity != null && !this.targetEntity.isAlive()) {
            this.resetTask();
        }else{
            this.goalOwner.getMoveHelper().setMoveTo(this.targetEntity.getPosX(), this.targetEntity.getPosY(), this.targetEntity.getPosZ(), 1);
        }
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.goalOwner.getDistanceSq(this.targetEntity) < 2.0D && goalOwner.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            ItemStack duplicate = this.targetEntity.getItem().copy();
            endergrade.bite();
            duplicate.setCount(1);
            if (!goalOwner.getHeldItem(Hand.MAIN_HAND).isEmpty() && !goalOwner.world.isRemote) {
                goalOwner.entityDropItem(goalOwner.getHeldItem(Hand.MAIN_HAND), 0.0F);
            }
            goalOwner.setHeldItem(Hand.MAIN_HAND, duplicate);
            endergrade.onGetItem(targetEntity);
            this.targetEntity.getItem().shrink(1);
            resetTask();
        }
    }

    public void resetTask() {
        targetEntity = null;
        this.endergrade.hasItemTarget = false;
        endergrade.stopWandering = false;
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.goalOwner.getMoveHelper().isUpdating();
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.getDistanceSq(p_compare_1_);
            double d1 = this.theEntity.getDistanceSq(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }

}