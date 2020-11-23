package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class EntityAINearestTarget3D<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public EntityAINearestTarget3D(MobEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight) {
        super(goalOwnerIn, targetClassIn, checkSight);
    }

    public EntityAINearestTarget3D(MobEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight, boolean nearbyOnlyIn) {
        super(goalOwnerIn, targetClassIn, checkSight, nearbyOnlyIn);
    }

    public EntityAINearestTarget3D(MobEntity goalOwnerIn, Class<T> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(goalOwnerIn, targetClassIn, targetChanceIn, checkSight, nearbyOnlyIn, targetPredicate);
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.goalOwner.getBoundingBox().grow(targetDistance, targetDistance, targetDistance);
    }

}
