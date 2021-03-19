package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

public class KangarooAIMelee extends MeleeAttackGoal {

    private EntityKangaroo kangaroo;
    private BlockPos waterPos;
    private int waterCheckTick = 0;
    private int waterTimeout = 0;
    public KangarooAIMelee(EntityKangaroo kangaroo, double speedIn, boolean useLongMemory) {
        super(kangaroo, speedIn, useLongMemory);
        this.kangaroo = kangaroo;
    }

    public boolean shouldExecute() {
        return super.shouldExecute();
    }

    public void tick() {
        boolean dontSuper = false;
        LivingEntity target = kangaroo.getAttackTarget();
        if (target != null) {
            if (target == kangaroo.getRevengeTarget()) {
                if (target.getDistance(kangaroo) < kangaroo.getWidth() + 1F && target.isInWater()) {
                    target.setMotion(target.getMotion().add(0, -0.09, 0));
                    target.setAir(target.getAir() - 30);
                }
                if (waterPos == null || !kangaroo.world.getFluidState(waterPos).isTagged(FluidTags.WATER)) {
                    kangaroo.setVisualFlag(0);
                    waterCheckTick++;
                    waterPos = generateWaterPos();
                } else {
                    kangaroo.setPathPriority(PathNodeType.WATER, 0);
                    kangaroo.setPathPriority(PathNodeType.WATER_BORDER, 0);
                    double localSpeed = MathHelper.clamp(kangaroo.getDistanceSq(waterPos.getX(), waterPos.getY(), waterPos.getZ()) * 0.5F, 1D, 2.3D);
                    kangaroo.getMoveHelper().setMoveTo(waterPos.getX(), waterPos.getY(), waterPos.getZ(), localSpeed);
                    if (kangaroo.isInWater()){
                        waterTimeout++;
                    }
                    if(waterTimeout < 1400){
                        dontSuper = true;
                        checkAndPerformAttack(target, kangaroo.getDistanceSq(target));
                    }
                    if (kangaroo.isInWater() || kangaroo.getDistanceSq(Vector3d.copyCentered(waterPos)) < 10) {
                        kangaroo.totalMovingProgress = 0;
                    }
                    if(kangaroo.getDistanceSq(Vector3d.copyCentered(waterPos)) > 10){
                        kangaroo.setVisualFlag(0);
                    }
                    if (kangaroo.getDistanceSq(Vector3d.copyCentered(waterPos)) < 3 && kangaroo.isInWater()) {
                        kangaroo.setStanding(true);
                        kangaroo.maxStandTime = 100;
                        kangaroo.getLookController().setLookPositionWithEntity(target, 360, 180);
                        kangaroo.setVisualFlag(1);
                    }
                }
            }else{

            }
            if (!dontSuper) {
                super.tick();
            }
        }
    }

    public boolean shouldContinueExecuting() {
        return waterPos != null && this.kangaroo.getAttackTarget() != null || super.shouldContinueExecuting();
    }

    public void resetTask() {
        super.resetTask();
        waterCheckTick = 0;
        waterTimeout = 0;
        waterPos = null;
        kangaroo.setVisualFlag(0);
        kangaroo.setPathPriority(PathNodeType.WATER, 8);
        kangaroo.setPathPriority(PathNodeType.WATER_BORDER, 8);
    }

    public BlockPos generateWaterPos() {
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 15;
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.kangaroo.getPosition().add(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
            while (this.kangaroo.world.isAirBlock(blockpos1) && blockpos1.getY() > 1) {
                blockpos1 = blockpos1.down();
            }
            if (this.kangaroo.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)) {
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy) + 5D;
        if (distToEnemySqr <= d0) {
            if(kangaroo.isInWater()){
                float f1 = kangaroo.rotationYaw * ((float)Math.PI / 180F);
                kangaroo.setMotion(kangaroo.getMotion().add((double)(-MathHelper.sin(f1) * 0.3F), 0.0D, (double)(MathHelper.cos(f1) * 0.3F)));
                enemy.applyKnockback(1F, enemy.getPosX() - kangaroo.getPosX(), enemy.getPosZ() - kangaroo.getPosZ());

            }
            this.func_234039_g_();
            if(kangaroo.getAnimation() == IAnimatedEntity.NO_ANIMATION){
                if(kangaroo.getRNG().nextBoolean()){
                    kangaroo.setAnimation(EntityKangaroo.ANIMATION_KICK);
                }else{
                    if(!kangaroo.getHeldItemMainhand().isEmpty()){
                        kangaroo.setAnimation(kangaroo.isLeftHanded() ? EntityKangaroo.ANIMATION_PUNCH_L : EntityKangaroo.ANIMATION_PUNCH_R);
                    }else{
                        kangaroo.setAnimation(kangaroo.getRNG().nextBoolean() ? EntityKangaroo.ANIMATION_PUNCH_R : EntityKangaroo.ANIMATION_PUNCH_L);
                    }
                }
            }
        }
    }

}