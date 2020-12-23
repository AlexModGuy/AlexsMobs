package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.Random;

public class RaccoonAIWash extends Goal {
    private final EntityRaccoon raccoon;
    private BlockPos waterPos;
    private BlockPos targetPos;
    private int washTime = 0;
    private int executionChance = 30;
    private Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public RaccoonAIWash(EntityRaccoon creature) {
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.raccoon = creature;
    }

    public boolean shouldExecute() {
        if (raccoon.lookForWaterBeforeEatingTimer > 0) {
            waterPos = generateTarget();
            if (waterPos != null) {
                targetPos = getLandPos(waterPos);
                return targetPos != null;
            }
        }
        return false;
    }

    public void startExecuting() {
        this.raccoon.postponeEating = true;
    }

    public void resetTask() {
        targetPos = null;
        waterPos = null;
        washTime = 0;
        this.raccoon.setWashPos(null);
        this.raccoon.setWashing(false);
        this.raccoon.postponeEating = false;
        this.raccoon.getNavigator().clearPath();
    }

    public void tick() {
        if (targetPos != null && waterPos != null) {
            double dist = this.raccoon.getDistanceSq(Vector3d.copyCentered(waterPos));
            if (dist > 2 && this.raccoon.isWashing()) {
                this.raccoon.setWashing(false);
            }
            if (dist <= 1F) {
                double d0 = waterPos.getX() + 0.5D - this.raccoon.getPosX();
                double d2 = waterPos.getZ() + 0.5D - this.raccoon.getPosZ();
                float yaw = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.raccoon.rotationYaw = yaw;
                this.raccoon.rotationYawHead = yaw;
                this.raccoon.renderYawOffset = yaw;
                this.raccoon.getNavigator().clearPath();
                this.raccoon.setWashing(true);
                this.raccoon.setWashPos(waterPos);
                this.raccoon.lookForWaterBeforeEatingTimer = 0;
                if(washTime % 10 == 0){
                    this.raccoon.playSound(SoundEvents.ENTITY_GENERIC_SWIM, 0.7F, 0.5F + raccoon.getRNG().nextFloat());
                }
                washTime++;
                if(washTime > 100 || raccoon.getHeldItemMainhand().getItem() == Items.SUGAR && washTime > 20){
                    this.resetTask();
                    if(raccoon.getHeldItemMainhand().getItem() != Items.SUGAR){
                        raccoon.onEatItem();
                    }
                    this.raccoon.postWashItem(raccoon.getHeldItemMainhand());
                    if(this.raccoon.getHeldItemMainhand().hasContainerItem()){
                        this.raccoon.entityDropItem(this.raccoon.getHeldItemMainhand().getContainerItem());
                    }
                    this.raccoon.getHeldItemMainhand().shrink(1);
                }
            }else{
                this.raccoon.getNavigator().tryMoveToXYZ(waterPos.getX(), waterPos.getY(), waterPos.getZ(), 1.2D);
            }

        }
    }

    public boolean shouldContinueExecuting() {
        return targetPos != null && !this.raccoon.isInWater() && EntityRaccoon.isFood(this.raccoon.getHeldItemMainhand());
    }

    public BlockPos generateTarget() {
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 32;
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.raccoon.getPosition().add(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
            while (this.raccoon.world.isAirBlock(blockpos1) && blockpos1.getY() > 1) {
                blockpos1 = blockpos1.down();
            }
            if (isConnectedToLand(blockpos1)) {
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }

    public boolean isConnectedToLand(BlockPos pos) {
        if (this.raccoon.world.getFluidState(pos).isTagged(FluidTags.WATER)) {
            for (Direction dir : HORIZONTALS) {
                BlockPos offsetPos = pos.offset(dir);
                if (this.raccoon.world.getFluidState(offsetPos).isEmpty() && this.raccoon.world.getFluidState(offsetPos.up()).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public BlockPos getLandPos(BlockPos pos) {
        if (this.raccoon.world.getFluidState(pos).isTagged(FluidTags.WATER)) {
            for (Direction dir : HORIZONTALS) {
                BlockPos offsetPos = pos.offset(dir);
                if (this.raccoon.world.getFluidState(offsetPos).isEmpty() && this.raccoon.world.getFluidState(offsetPos.up()).isEmpty()) {
                    return offsetPos;
                }
            }
        }
        return null;
    }
}
