package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BoneSerpentAIMeleeJump extends JumpGoal {
    private final EntityBoneSerpent dolphin;
    private int attackCooldown = 0;
    private boolean inWater;

    public BoneSerpentAIMeleeJump(EntityBoneSerpent dolphin) {
        this.dolphin = dolphin;
    }

    public boolean canUse() {
        if (this.dolphin.getTarget() == null || this.dolphin.isOnGround() || !dolphin.isInLava() && !dolphin.isInWater() || dolphin.jumpCooldown> 0) {
            return false;
        } else {
            BlockPos blockpos = this.dolphin.blockPosition();
            return true;
        }
    }
    public boolean canContinueToUse() {
        double d0 = this.dolphin.getDeltaMovement().y;
        return dolphin.getTarget() != null && dolphin.jumpCooldown > 0 && (!(d0 * d0 < (double) 0.03F) || this.dolphin.xRot == 0.0F || !(Math.abs(this.dolphin.xRot) < 10.0F) || !this.dolphin.isInWater()) && !this.dolphin.isOnGround();
    }

    public boolean isInterruptable() {
        return false;
    }

    public void start() {
        LivingEntity target = this.dolphin.getTarget();
        if(target != null){
            double distanceXZ = dolphin.distanceToSqr(target.getX(), dolphin.getY(), target.getZ());
            if(distanceXZ < 150){
                dolphin.lookAt(target, 260, 30);
                double smoothX = Mth.clamp(Math.abs(target.getX() - dolphin.getX()), 0, 1);
                double smoothY = Mth.clamp(Math.abs(target.getY() - dolphin.getY()), 0, 1);
                double smoothZ = Mth.clamp(Math.abs(target.getZ() - dolphin.getZ()), 0, 1);
                double d0 = (target.getX() - this.dolphin.getX()) * 0.3 * smoothX;
                double d1 = Math.signum(target.getY() - this.dolphin.getY());
                double d2 = (target.getZ() - this.dolphin.getZ()) * 0.3 * smoothZ;
                float up = 1F + dolphin.getRandom().nextFloat() * 0.8F;
                this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add((double) d0 * 0.3D, up, (double) d2 * 0.3D));
                this.dolphin.getNavigation().stop();
                this.dolphin.jumpCooldown = dolphin.getRandom().nextInt(32) + 64;
            }else{
                dolphin.getNavigation().moveTo(target, 1.0F);
            }

        }
    }

    public void stop() {
        this.dolphin.xRot = 0.0F;
        this.attackCooldown = 0;
    }

    public void tick() {
        boolean flag = this.inWater;
        if (!flag) {
            FluidState fluidstate = this.dolphin.level.getFluidState(this.dolphin.blockPosition());
            this.inWater = fluidstate.is(FluidTags.LAVA) || fluidstate.is(FluidTags.WATER);
        }
        if(attackCooldown > 0){
            attackCooldown--;
        }
        if (this.inWater && !flag) {
            this.dolphin.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
        }
        LivingEntity target = this.dolphin.getTarget();
        if(target != null){
            if(this.dolphin.distanceTo(target) < 3F && attackCooldown <= 0){
                this.dolphin.doHurtTarget(target);
                attackCooldown = 20;
            }
        }

        Vec3 vector3d = this.dolphin.getDeltaMovement();
        if (vector3d.y * vector3d.y < (double) 0.1F && this.dolphin.xRot != 0.0F) {
            this.dolphin.xRot = Mth.rotlerp(this.dolphin.xRot, 0.0F, 0.2F);
        } else {
            double d0 = Math.sqrt(Entity.getHorizontalDistanceSqr(vector3d));
            double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double) (180F / (float) Math.PI);
            this.dolphin.xRot = (float) d1;
        }

    }
}
