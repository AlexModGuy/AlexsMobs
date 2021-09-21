package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class MantisShrimpAIBreakBlocks extends Goal {

    private EntityMantisShrimp mantisShrimp;
    private int idleAtFlowerTime = 0;
    private int timeoutCounter = 0;
    private int searchCooldown = 0;
    private boolean isAboveDestinationBear;
    private BlockPos destinationBlock;
    private final BlockSorter targetSorter;


    public MantisShrimpAIBreakBlocks(EntityMantisShrimp mantisShrimp) {
        super();
        this.mantisShrimp = mantisShrimp;
        this.targetSorter = new BlockSorter(mantisShrimp);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
    }

    public void start() {
        super.start();
    }

    public boolean canUse() {

        if (!mantisShrimp.isBaby() && (mantisShrimp.getTarget() == null || !mantisShrimp.getTarget().isAlive()) && mantisShrimp.getCommand() == 3 && !mantisShrimp.getMainHandItem().isEmpty()) {
            if(searchCooldown <= 0){
                resetTarget();
                searchCooldown = 100 + mantisShrimp.getRandom().nextInt(200);
                return destinationBlock != null;
            }else{
                searchCooldown--;
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        return destinationBlock != null && timeoutCounter < 1200 && (mantisShrimp.getTarget() == null || !mantisShrimp.getTarget().isAlive()) && mantisShrimp.getCommand() == 3 && !mantisShrimp.getMainHandItem().isEmpty();
    }

    public void stop() {
        searchCooldown = 50;
        timeoutCounter = 0;
        destinationBlock = null;
    }

    public double getTargetDistanceSq() {
        return 2.3D;
    }

    public void tick() {
        BlockPos blockpos = destinationBlock;
        float yDist = (float) Math.abs(blockpos.getY() - mantisShrimp.getY() - mantisShrimp.getBbHeight()/2);
        this.mantisShrimp.getNavigation().moveTo((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY() + 0.5D, (double) ((float) blockpos.getZ()) + 0.5D, 1);
        if (!isWithinXZDist(blockpos, mantisShrimp.position(), this.getTargetDistanceSq()) || yDist > 2F) {
            this.isAboveDestinationBear = false;
            ++this.timeoutCounter;
        } else {
            this.isAboveDestinationBear = true;
            --this.timeoutCounter;
        }
        if(timeoutCounter > 2400){
            stop();
        }
        if (this.getIsAboveDestination()) {
            mantisShrimp.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            if (this.idleAtFlowerTime >= 2) {
                idleAtFlowerTime = 0;
                this.breakBlock();
                this.stop();
            } else {
                mantisShrimp.punch();
                ++this.idleAtFlowerTime;
            }
        }
    }

    private void resetTarget() {
        List<BlockPos> allBlocks = new ArrayList<>();
        int radius = 16;
        for (BlockPos pos : BlockPos.betweenClosedStream(this.mantisShrimp.blockPosition().offset(-radius, -radius, -radius), this.mantisShrimp.blockPosition().offset(radius, radius, radius)).map(BlockPos::immutable).collect(Collectors.toList())) {
            if (!mantisShrimp.level.isEmptyBlock(pos) && shouldMoveTo(mantisShrimp.level, pos)) {
                if(!mantisShrimp.isInWater() || isBlockTouchingWater(pos)){
                    allBlocks.add(pos);
                }
            }
        }
        if (!allBlocks.isEmpty()) {
            allBlocks.sort(this.targetSorter);
            for(BlockPos pos : allBlocks){
                if(canSeeBlock(pos)){
                    this.destinationBlock = pos;
                    return;
                }
            }
        }
        destinationBlock = null;
    }

    private boolean isBlockTouchingWater(BlockPos pos) {
        for(Direction dir : Direction.values()){
            if(mantisShrimp.level.getFluidState(pos.relative(dir)).is(FluidTags.WATER)){
                return true;
            }
        }
        return false;
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
        return blockpos.distSqr(positionVec.x(), blockpos.getY(), positionVec.z(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void breakBlock() {
        if (shouldMoveTo(mantisShrimp.level, destinationBlock)) {
            BlockState state = mantisShrimp.level.getBlockState(destinationBlock);
            if(!mantisShrimp.level.isEmptyBlock(destinationBlock) && net.minecraftforge.common.ForgeHooks.canEntityDestroy(mantisShrimp.level, destinationBlock, mantisShrimp) && state.getDestroySpeed(mantisShrimp.level, destinationBlock) >= 0){
                mantisShrimp.level.destroyBlock(destinationBlock, true);
            }
        }
    }

    private boolean canSeeBlock(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(mantisShrimp.getX(), mantisShrimp.getEyeY(), mantisShrimp.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = mantisShrimp.level.clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mantisShrimp));
        return result.getBlockPos().equals(destinationBlock);
    }


    protected boolean shouldMoveTo(LevelReader worldIn, BlockPos pos) {
        Item blockItem = worldIn.getBlockState(pos).getBlock().asItem();
        return mantisShrimp.getMainHandItem().getItem() == blockItem;
    }

    public class BlockSorter implements Comparator<BlockPos> {
        private final Entity entity;

        public BlockSorter(Entity entity) {
            this.entity = entity;
        }

        @Override
        public int compare(BlockPos pos1, BlockPos pos2) {
            double distance1 = this.getDistance(pos1);
            double distance2 = this.getDistance(pos2);
            return Double.compare(distance1, distance2);
        }

        private double getDistance(BlockPos pos) {
            double deltaX = this.entity.getX() - (pos.getX() + 0.5);
            double deltaY = this.entity.getY() + this.entity.getEyeHeight() - (pos.getY() + 0.5);
            double deltaZ = this.entity.getZ() - (pos.getZ() + 0.5);
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        }
    }
}
