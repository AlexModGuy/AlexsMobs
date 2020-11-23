package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class TameableAIHurtByTarget  extends TargetGoal {
    private static final EntityPredicate field_220795_a = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();
    private boolean entityCallsForHelp;
    /** Store the previous revengeTimer value */
    private int revengeTimerOld;
    private final Class<?>[] excludedReinforcementTypes;
    private Class<?>[] reinforcementTypes;

    public TameableAIHurtByTarget(CreatureEntity creatureIn, Class<?>... excludeReinforcementTypes) {
        super(creatureIn, true);
        this.excludedReinforcementTypes = excludeReinforcementTypes;
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        int i = this.goalOwner.getRevengeTimer();
        LivingEntity livingentity = this.goalOwner.getRevengeTarget();
        if (i != this.revengeTimerOld && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER && this.goalOwner.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
                return false;
            } else {
                for(Class<?> oclass : this.excludedReinforcementTypes) {
                    if (oclass.isAssignableFrom(livingentity.getClass())) {
                        return false;
                    }
                }

                return this.isSuitableTarget(livingentity, field_220795_a);
            }
        } else {
            return false;
        }
    }

    public TameableAIHurtByTarget setCallsForHelp(Class<?>... reinforcementTypes) {
        this.entityCallsForHelp = true;
        this.reinforcementTypes = reinforcementTypes;
        return this;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.goalOwner.getRevengeTarget());
        this.target = this.goalOwner.getAttackTarget();
        this.revengeTimerOld = this.goalOwner.getRevengeTimer();
        this.unseenMemoryTicks = 300;
        if (this.entityCallsForHelp) {
            this.alertOthers();
        }

        super.startExecuting();
    }

    protected void alertOthers() {
        double d0 = this.getTargetDistance();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.fromVector(this.goalOwner.getPositionVec()).grow(d0, 10.0D, d0);
        List<MobEntity> list = this.goalOwner.world.getLoadedEntitiesWithinAABB(this.goalOwner.getClass(), axisalignedbb);
        Iterator iterator = list.iterator();

        while(true) {
            MobEntity mobentity;
            while(true) {
                if (!iterator.hasNext()) {
                    return;
                }

                mobentity = (MobEntity)iterator.next();
                if (this.goalOwner != mobentity && mobentity.getAttackTarget() == null && (!(this.goalOwner instanceof TameableEntity) || ((TameableEntity)this.goalOwner).getOwner() == ((TameableEntity)mobentity).getOwner()) && !mobentity.isOnSameTeam(this.goalOwner.getRevengeTarget())) {
                    if (this.reinforcementTypes == null) {
                        break;
                    }

                    boolean flag = false;

                    for(Class<?> oclass : this.reinforcementTypes) {
                        if (mobentity.getClass() == oclass) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        break;
                    }
                }
            }

            this.setAttackTarget(mobentity, this.goalOwner.getRevengeTarget());
        }
    }

    protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
        mobIn.setAttackTarget(targetIn);
    }
}
