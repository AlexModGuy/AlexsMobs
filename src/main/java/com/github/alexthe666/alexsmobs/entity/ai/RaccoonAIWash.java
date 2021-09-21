package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class RaccoonAIWash extends Goal {
    private final EntityRaccoon raccoon;
    private BlockPos waterPos;
    private BlockPos targetPos;
    private int washTime = 0;
    private int executionChance = 30;
    private Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public RaccoonAIWash(EntityRaccoon creature) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.raccoon = creature;
    }

    public boolean canUse() {
        if(raccoon.getMainHandItem().isEmpty()){
            return false;
        }
        if (raccoon.lookForWaterBeforeEatingTimer > 0) {
            waterPos = generateTarget();
            if (waterPos != null) {
                targetPos = getLandPos(waterPos);
                return targetPos != null;
            }
        }
        return false;
    }

    public void start() {
        this.raccoon.lookForWaterBeforeEatingTimer = 1800;
    }

    public void stop() {
        targetPos = null;
        waterPos = null;
        washTime = 0;
        this.raccoon.setWashPos(null);
        this.raccoon.setWashing(false);
        this.raccoon.lookForWaterBeforeEatingTimer = 100;
        this.raccoon.getNavigation().stop();
    }

    public void tick() {
        if (targetPos != null && waterPos != null) {
            double dist = this.raccoon.distanceToSqr(Vec3.atCenterOf(waterPos));
            if (dist > 2 && this.raccoon.isWashing()) {
                this.raccoon.setWashing(false);
            }
            if (dist <= 1F) {
                double d0 = waterPos.getX() + 0.5D - this.raccoon.getX();
                double d2 = waterPos.getZ() + 0.5D - this.raccoon.getZ();
                float yaw = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.raccoon.setYRot(yaw);
                this.raccoon.yHeadRot = yaw;
                this.raccoon.yBodyRot = yaw;
                this.raccoon.getNavigation().stop();
                this.raccoon.setWashing(true);
                this.raccoon.setWashPos(waterPos);
                this.raccoon.lookForWaterBeforeEatingTimer = 0;
                if(washTime % 10 == 0){
                    this.raccoon.playSound(SoundEvents.GENERIC_SWIM, 0.7F, 0.5F + raccoon.getRandom().nextFloat());
                }
                washTime++;
                if(washTime > 100 || raccoon.getMainHandItem().getItem() == Items.SUGAR && washTime > 20){
                    this.stop();
                    if(raccoon.getMainHandItem().getItem() != Items.SUGAR){
                        raccoon.onEatItem();
                    }
                    this.raccoon.postWashItem(raccoon.getMainHandItem());
                    if(this.raccoon.getMainHandItem().hasContainerItem()){
                        this.raccoon.spawnAtLocation(this.raccoon.getMainHandItem().getContainerItem());
                    }
                    this.raccoon.getMainHandItem().shrink(1);
                }
            }else{
                this.raccoon.getNavigation().moveTo(waterPos.getX(), waterPos.getY(), waterPos.getZ(), 1.2D);
            }

        }
    }

    public boolean canContinueToUse() {
        if(raccoon.getMainHandItem().isEmpty()){
            return false;
        }
        return targetPos != null && !this.raccoon.isInWater() && EntityRaccoon.isRaccoonFood(this.raccoon.getMainHandItem());
    }

    public BlockPos generateTarget() {
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 32;
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.raccoon.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
            while (this.raccoon.level.isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
                blockpos1 = blockpos1.below();
            }
            if (isConnectedToLand(blockpos1)) {
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }

    public boolean isConnectedToLand(BlockPos pos) {
        if (this.raccoon.level.getFluidState(pos).is(FluidTags.WATER)) {
            for (Direction dir : HORIZONTALS) {
                BlockPos offsetPos = pos.relative(dir);
                if (this.raccoon.level.getFluidState(offsetPos).isEmpty() && this.raccoon.level.getFluidState(offsetPos.above()).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public BlockPos getLandPos(BlockPos pos) {
        if (this.raccoon.level.getFluidState(pos).is(FluidTags.WATER)) {
            for (Direction dir : HORIZONTALS) {
                BlockPos offsetPos = pos.relative(dir);
                if (this.raccoon.level.getFluidState(offsetPos).isEmpty() && this.raccoon.level.getFluidState(offsetPos.above()).isEmpty()) {
                    return offsetPos;
                }
            }
        }
        return null;
    }
}
