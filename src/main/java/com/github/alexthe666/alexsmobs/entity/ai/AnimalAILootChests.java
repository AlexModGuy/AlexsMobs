package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimalAILootChests extends MoveToBlockGoal {

    private final AnimalEntity entity;
    private final ILootsChests chestLooter;
    private boolean hasOpenedChest = false;

    public AnimalAILootChests(AnimalEntity entity, int range) {
        super(entity, 1.0F, range);
        this.entity = entity;
        this.chestLooter = (ILootsChests) entity;
    }

    public boolean isChestRaidable(IWorldReader world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof ContainerBlock) {
            Block block = world.getBlockState(pos).getBlock();
            boolean listed = false;
            TileEntity entity = world.getTileEntity(pos);
            if (entity instanceof IInventory) {
                IInventory inventory = (IInventory) entity;
                try {
                    if (!inventory.isEmpty() && chestLooter.isLootable(inventory)) {
                        return true;
                    }
                } catch (Exception e) {
                    AlexsMobs.LOGGER.warn("Alex's Mobs stopped a " + entity.getClass().getSimpleName() + " from causing a crash during access");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity instanceof TameableEntity && ((TameableEntity) entity).isTamed()) {
            return false;
        }
        if (!AMConfig.raccoonsStealFromChests) {
            return false;
        }
        if (!this.entity.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            return false;
        }
        if (this.runDelay <= 0) {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.entity.world, this.entity)) {
                return false;
            }
        }
        return super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && this.entity.getHeldItem(Hand.MAIN_HAND).isEmpty();
    }

    public boolean canSeeChest() {
        RayTraceResult raytraceresult = entity.world.rayTraceBlocks(new RayTraceContext(entity.getEyePosition(1.0F), new Vector3d(destinationBlock.getX() + 0.5, destinationBlock.getY() + 0.5, destinationBlock.getZ() + 0.5), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
        if (raytraceresult instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) raytraceresult;
            BlockPos pos = blockRayTraceResult.getPos();
            return pos.equals(destinationBlock) || entity.world.isAirBlock(pos) || this.entity.world.getTileEntity(pos) == this.entity.world.getTileEntity(destinationBlock);
        }
        return true;
    }

    public ItemStack getFoodFromInventory(IInventory inventory, Random random) {
        List<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (chestLooter.shouldLootItem(stack)) {
                items.add(stack);
            }
        }
        if (items.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            return items.get(random.nextInt(items.size() - 1));
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (this.destinationBlock != null) {
            TileEntity te = this.entity.world.getTileEntity(this.destinationBlock);
            if (te instanceof IInventory) {
                IInventory feeder = (IInventory) te;
                double distance = this.entity.getDistanceSq(this.destinationBlock.getX() + 0.5F, this.destinationBlock.getY() + 0.5F, this.destinationBlock.getZ() + 0.5F);
                if (canSeeChest()) {
                    if (this.getIsAboveDestination() && distance <= 3) {
                        toggleChest(feeder, false);
                        ItemStack stack = getFoodFromInventory(feeder, this.entity.world.rand);
                        if (stack == ItemStack.EMPTY) {
                            this.resetTask();
                        } else {
                            ItemStack duplicate = stack.copy();
                            duplicate.setCount(1);
                            if (!this.entity.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.entity.world.isRemote) {
                                this.entity.entityDropItem(this.entity.getHeldItem(Hand.MAIN_HAND), 0.0F);
                            }
                            this.entity.setHeldItem(Hand.MAIN_HAND, duplicate);
                            if (entity instanceof EntityRaccoon) {
                                ((EntityRaccoon) entity).lookForWaterBeforeEatingTimer = 10;
                            }
                            stack.shrink(1);
                            this.resetTask();

                        }
                    } else {
                        if (distance < 5 && !hasOpenedChest) {
                            hasOpenedChest = true;
                            toggleChest(feeder, true);
                        }
                    }
                }

            }

        }
    }


    public void resetTask() {
        super.resetTask();
        if (this.destinationBlock != null) {
            TileEntity te = this.entity.world.getTileEntity(this.destinationBlock);
            if (te instanceof IInventory) {
                toggleChest((IInventory) te, false);
            }
        }
        this.destinationBlock = null;
        this.hasOpenedChest = false;
    }


    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        return pos != null && isChestRaidable(worldIn, pos);
    }

    public void toggleChest(IInventory te, boolean open) {
        if (te instanceof ChestTileEntity) {
            ChestTileEntity chest = (ChestTileEntity) te;
            if (open) {
                this.entity.world.addBlockEvent(this.destinationBlock, chest.getBlockState().getBlock(), 1, 1);
            } else {
                this.entity.world.addBlockEvent(this.destinationBlock, chest.getBlockState().getBlock(), 1, 0);
            }
            chest.numPlayersUsing = open ? 1 : 0;
            this.entity.world.notifyNeighborsOfStateChange(destinationBlock, chest.getBlockState().getBlock());
            this.entity.world.notifyNeighborsOfStateChange(destinationBlock.down(), chest.getBlockState().getBlock());
        }
    }
}
