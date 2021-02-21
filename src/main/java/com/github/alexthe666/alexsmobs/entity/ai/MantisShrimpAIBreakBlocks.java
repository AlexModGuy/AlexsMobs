package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

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
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
    }

    public void startExecuting() {
        super.startExecuting();
    }

    public boolean shouldExecute() {

        if (!mantisShrimp.isChild() && (mantisShrimp.getAttackTarget() == null || !mantisShrimp.getAttackTarget().isAlive()) && mantisShrimp.getCommand() == 3 && !mantisShrimp.getHeldItemMainhand().isEmpty()) {
            if(searchCooldown <= 0){
                resetTarget();
                searchCooldown = 100 + mantisShrimp.getRNG().nextInt(200);
                return destinationBlock != null;
            }else{
                searchCooldown--;
            }
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        return destinationBlock != null && (mantisShrimp.getAttackTarget() == null || !mantisShrimp.getAttackTarget().isAlive()) && mantisShrimp.getCommand() == 3 && !mantisShrimp.getHeldItemMainhand().isEmpty();
    }

    public void resetTask() {
        searchCooldown = 50;
        timeoutCounter = 0;
        destinationBlock = null;
    }

    public double getTargetDistanceSq() {
        return 2.3D;
    }

    public void tick() {
        BlockPos blockpos = destinationBlock;
        float yDist = (float) Math.abs(blockpos.getY() - mantisShrimp.getPosY() - mantisShrimp.getHeight()/2);
        this.mantisShrimp.getNavigator().tryMoveToXYZ((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY() + 0.5D, (double) ((float) blockpos.getZ()) + 0.5D, 1);
        if (!isWithinXZDist(blockpos, mantisShrimp.getPositionVec(), this.getTargetDistanceSq()) && yDist < 2.4F) {
            this.isAboveDestinationBear = false;
            ++this.timeoutCounter;
        } else {
            this.isAboveDestinationBear = true;
            --this.timeoutCounter;
        }
        if(timeoutCounter > 2400){
            resetTask();
        }
        if (this.getIsAboveDestination()) {
            mantisShrimp.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(destinationBlock.getX() + 0.5D, destinationBlock.getY(), destinationBlock.getZ() + 0.5));
            if (this.idleAtFlowerTime >= 2) {
                idleAtFlowerTime = 0;
                this.breakBlock();
                this.resetTask();
            } else {
                mantisShrimp.punch();
                ++this.idleAtFlowerTime;
            }
        }
    }

    private void resetTarget() {
        List<BlockPos> allBlocks = new ArrayList<>();
        int radius = 16;
        for (BlockPos pos : BlockPos.getAllInBox(this.mantisShrimp.getPosition().add(-radius, -radius, -radius), this.mantisShrimp.getPosition().add(radius, radius, radius)).map(BlockPos::toImmutable).collect(Collectors.toList())) {
            if (!mantisShrimp.world.isAirBlock(pos) && shouldMoveTo(mantisShrimp.world, pos)) {
                if(!mantisShrimp.isInWater() || isBlockTouchingWater(pos)){
                    allBlocks.add(pos);
                }
            }
        }
        if (!allBlocks.isEmpty()) {
            allBlocks.sort(this.targetSorter);
            this.destinationBlock = allBlocks.get(0);
        }
    }

    private boolean isBlockTouchingWater(BlockPos pos) {
        for(Direction dir : Direction.values()){
            if(mantisShrimp.world.getFluidState(pos.offset(dir)).isTagged(FluidTags.WATER)){
                return true;
            }
        }
        return false;
    }

    private boolean isWithinXZDist(BlockPos blockpos, Vector3d positionVec, double distance) {
        return blockpos.distanceSq(positionVec.getX(), blockpos.getY(), positionVec.getZ(), true) < distance * distance;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestinationBear;
    }

    private void breakBlock() {
        if (shouldMoveTo(mantisShrimp.world, destinationBlock)) {
            mantisShrimp.world.destroyBlock(destinationBlock, true);
        }
    }

    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        Item blockItem = worldIn.getBlockState(pos).getBlock().asItem();
        return mantisShrimp.getHeldItemMainhand().getItem() == blockItem;
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
            boolean canSee1 = canSeeBlock(pos1);
            boolean canSee2 = canSeeBlock(pos2);
            if(canSee1 && !canSee2){
                return 1;
            }
            if(!canSee1 && canSee2){
                return 2;
            }
            return Double.compare(distance1, distance2);
        }

        private boolean canSeeBlock(BlockPos destinationBlock) {
            Vector3d Vector3d = new Vector3d(entity.getPosX(), entity.getPosYEye(), entity.getPosZ());
            Vector3d blockVec = net.minecraft.util.math.vector.Vector3d.copyCentered(destinationBlock);
            BlockRayTraceResult result = entity.world.rayTraceBlocks(new RayTraceContext(Vector3d, blockVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
            return result.getPos().equals(destinationBlock);
        }


        private double getDistance(BlockPos pos) {
            double deltaX = this.entity.getPosX() - (pos.getX() + 0.5);
            double deltaY = this.entity.getPosY() + this.entity.getEyeHeight() - (pos.getY() + 0.5);
            double deltaZ = this.entity.getPosZ() - (pos.getZ() + 0.5);
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        }
    }
}
