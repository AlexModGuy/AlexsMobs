package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FroststalkerAIMelee extends Goal {

    private final EntityFroststalker froststalker;
    private boolean willJump = false;
    private boolean hasJumped = false;
    private boolean clockwise = false;
    private int pursuitTime = 0;
    private int maxPursuitTime = 0;
    private BlockPos pursuitPos = null;
    private int startingOrbit = 0;

    public FroststalkerAIMelee(EntityFroststalker froststalker) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.froststalker = froststalker;
    }

    @Override
    public boolean canUse() {
        if(froststalker.getTarget() != null && froststalker.getTarget().isAlive()){
            if(froststalker.isValidLeader(froststalker.getTarget())){
                return froststalker.getLastHurtByMob() != null && froststalker.getLastHurtByMob().equals(froststalker.getTarget());
            }else{
                return !froststalker.isFleeingFire();
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        LivingEntity target = froststalker.getTarget();
        return target != null && !froststalker.isValidLeader(target);
    }

    
    public void start() {
        willJump = froststalker.getRandom().nextInt(2) == 0;
        hasJumped = false;
        clockwise = froststalker.getRandom().nextBoolean();
        pursuitPos = null;
        pursuitTime = 0;
        maxPursuitTime = 40 + froststalker.getRandom().nextInt(40);
        startingOrbit = froststalker.getRandom().nextInt(360);
        this.froststalker.frostJump();
    }

    public void tick() {
        froststalker.setBipedal(true);
        froststalker.standFor(20);
        LivingEntity target = froststalker.getTarget();
        boolean flag = false;
        if ((hasJumped || froststalker.isTackling()) && froststalker.onGround()) {
            hasJumped = false;
            willJump = false;
            froststalker.setTackling(false);
        }
        if (target != null && target.isAlive()) {
            if (pursuitTime < maxPursuitTime) {
                pursuitTime++;
                pursuitPos = getBlockNearTarget(target);

                float extraSpeed = 0.2F * Math.max(5F - froststalker.distanceTo(target), 0F);
                if (pursuitPos != null) {
                    froststalker.getNavigation().moveTo(pursuitPos.getX(), pursuitPos.getY(), pursuitPos.getZ(), 1.0F + extraSpeed);
                }else{
                    froststalker.getNavigation().moveTo(target, 1.0F);
                }
            } else if (willJump && pursuitTime == maxPursuitTime) {
                froststalker.lookAt(target, 180F, 10F);
                if (froststalker.distanceTo(target) > 10F) {
                    froststalker.getNavigation().moveTo(target, 1.0F);
                } else if (froststalker.onGround() && froststalker.hasLineOfSight(target)) {
                    this.froststalker.setTackling(true);
                    hasJumped = true;
                    Vec3 vector3d = this.froststalker.getDeltaMovement();
                    Vec3 vector3d1 = new Vec3(target.getX() - this.froststalker.getX(), 0.0D, target.getZ() - this.froststalker.getZ());
                    if (vector3d1.lengthSqr() > 1.0E-7D) {
                        vector3d1 = vector3d1.normalize().scale(0.9D).add(vector3d.scale(0.8D));
                    }
                    this.froststalker.setDeltaMovement(vector3d1.x, 0.6F, vector3d1.z);
                } else {
                    flag = true;
                }
            } else {
                if (!froststalker.isTackling()) {
                    froststalker.getNavigation().moveTo(target, 1.0F);
                }
            }
            if (froststalker.isTackling() && froststalker.distanceTo(target) <= froststalker.getBbWidth() + target.getBbWidth() + 1.1F && froststalker.hasLineOfSight(target)) {
                target.hurt(froststalker.damageSources().mobAttack(froststalker), (float) froststalker.getAttributeValue(Attributes.ATTACK_DAMAGE));
                start();
            }
            if (!flag) {
                if (froststalker.distanceTo(target) <= froststalker.getBbWidth() + target.getBbWidth() + 1.1F && froststalker.hasLineOfSight(target)) {
                    if (pursuitTime == maxPursuitTime) {
                        if (!froststalker.isTackling()) {
                            froststalker.doHurtTarget(target);
                        }
                        start();
                    }
                }
            }
        }
        if (target != null && !froststalker.onGround()) {
            froststalker.lookAt(target, 180F, 10F);
            froststalker.yBodyRot = froststalker.getYRot();
        }
    }

    public BlockPos getBlockNearTarget(LivingEntity target) {
        float radius = froststalker.getRandom().nextInt(5) + 3 + target.getBbWidth();
        float neg = froststalker.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = froststalker.yBodyRot;
        int orbit = (int) (startingOrbit + (pursuitTime / (float) maxPursuitTime) * 360);
        float angle = (Maths.STARTING_ANGLE * (clockwise ? -orbit : orbit));
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos circlePos = AMBlockPos.fromCoords(target.getX() + extraX, target.getEyeY(), target.getZ() + extraZ);
        while (!froststalker.level().getBlockState(circlePos).isAir() && circlePos.getY() < froststalker.level().getMaxBuildHeight()) {
            circlePos = circlePos.above();
        }
        while (!froststalker.level().getBlockState(circlePos.below()).entityCanStandOn(froststalker.level(), circlePos.below(), froststalker) && circlePos.getY() > 1) {
            circlePos = circlePos.below();
        }
        if (froststalker.getWalkTargetValue(circlePos) > -1) {
            return circlePos;
        }
        return null;
    }

    public void stop() {
        froststalker.setTackling(false);
    }
}