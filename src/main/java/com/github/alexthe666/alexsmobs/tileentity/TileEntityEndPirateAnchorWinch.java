package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorChain;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityEndPirateAnchorWinch extends BlockEntity {

    public float clientRoll;
    public int tickCount = 0;
    public int windCounter = 0;
    private float chainLength;
    private float prevChainLength;
    private int windTime = 0;
    private float windProgress;
    private float prevWindProgress;
    private boolean draggingAnchor;
    private boolean anchorEW;
    private boolean prevPowered = false;
    private boolean droppingChain = false;
    private boolean pullingChain = false;

    public TileEntityEndPirateAnchorWinch(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.END_PIRATE_ANCHOR_WINCH.get(), pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateAnchorWinch entity) {
        entity.tick();
    }

    private void tickChainLogic(){
        if (droppingChain) {
            boolean flag = true;
            BlockPos chainPos = this.getBlockPos().below((int) Math.floor(chainLength) + 1);
            if (level.isEmptyBlock(chainPos)) {
                level.setBlockAndUpdate(chainPos, AMBlockRegistry.END_PIRATE_ANCHOR_CHAIN.get().defaultBlockState().setValue(BlockEndPirateAnchorChain.WATERLOGGED, level.getFluidState(chainPos).is(FluidTags.WATER)));
            } else if (stopDroppingChainAt(chainPos) || draggingAnchor && stopDroppingChainAt(chainPos.below(3))) {
                droppingChain = false;
                if (draggingAnchor && tryPlaceAnchor((int) Math.floor(chainLength))) {
                    draggingAnchor = false;
                }
            }
            if (flag) {
                chainLength += 0.1F;
            }
            windTime = 2;
        }
        if (pullingChain) {
            BlockPos chainPos = this.getBlockPos().below((int) Math.floor(chainLength) + 1);
            if (level.getBlockState(chainPos).is(AMBlockRegistry.END_PIRATE_ANCHOR_CHAIN.get())) {
                level.setBlockAndUpdate(chainPos, Blocks.AIR.defaultBlockState());
            }
            if (chainLength > 0) {
                chainLength -= 0.1F;
                windTime = 2;
            }else if (draggingAnchor && tryPlaceAnchor(0)) {
                chainLength = 0;
                draggingAnchor = false;
            }
        }
    }

    private void tick() {
        tickCount++;
        prevChainLength = chainLength;
        prevWindProgress = windProgress;
        tickChainLogic();
        boolean powered = false;
        if (getBlockState().getBlock() instanceof BlockEndPirateAnchorWinch) {
            powered = getBlockState().getValue(BlockEndPirateAnchorWinch.POWERED);
        }
        if (powered != prevPowered) {
            prevPowered = powered;
            if (!draggingAnchor && checkAndBreakAnchor(this.getBlockPos().below((int) Math.floor(chainLength) + 1))) {
                draggingAnchor = true;
            }
            if (powered) {
                droppingChain = false;
                pullingChain = true;
            } else {
                chainLength = 0;
                BlockPos.MutableBlockPos checkChainAt = new BlockPos.MutableBlockPos();
                checkChainAt.set(this.getBlockPos());
                checkChainAt.move(0, -1, 0);
                while (level.getBlockState(checkChainAt).is(AMBlockRegistry.END_PIRATE_ANCHOR_CHAIN.get())) {
                    checkChainAt.move(0, -1, 0);
                    chainLength++;
                }
                droppingChain = true;
                pullingChain = false;
            }
        }
        if (windTime > 0) {
            windCounter++;
            windTime--;
            if (windProgress < 1F) {
                windProgress += 0.25F;
            }
        } else {
            windCounter = 0;
            if (windProgress > 0F) {
                windProgress -= 0.25F;
            }
        }
    }

    private boolean stopDroppingChainAt(BlockPos chainPos) {
        BlockState state = level.getBlockState(chainPos);
        return chainPos.getY() < level.getMinBuildHeight() || !state.isAir() && !state.is(AMBlockRegistry.END_PIRATE_ANCHOR_CHAIN.get()) && !chainPos.equals(this.getBlockPos());
    }


    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public boolean checkAndBreakAnchor(BlockPos down) {
        if (level.getBlockState(down).getBlock() instanceof BlockEndPirateAnchor) {
            anchorEW = level.getBlockState(down).getValue(BlockEndPirateAnchor.EASTORWEST);
            BlockPos actualAnchorPos = down.below(2);
            if (level.getBlockState(actualAnchorPos).getBlock() instanceof BlockEndPirateAnchor) {
                BlockEndPirateAnchor.removeAnchor(level, actualAnchorPos, level.getBlockState(actualAnchorPos).getValue(BlockEndPirateAnchor.EASTORWEST));
                return true;
            }
        }
        return false;
    }

    public boolean tryPlaceAnchor(int offset) {
        BlockPos at = this.getBlockPos().below(3 + offset);
        if (BlockEndPirateAnchor.isClearForPlacement(this.level, at, anchorEW)) {
            BlockState anchorState = AMBlockRegistry.END_PIRATE_ANCHOR.get().defaultBlockState().setValue(BlockEndPirateAnchor.EASTORWEST, anchorEW);
            this.level.setBlock(at, anchorState, 2);
            BlockEndPirateAnchor.placeAnchor(level, at, anchorState);
            return true;
        }
        return false;
    }

    public float getChainLengthForRender() {
        return chainLength - 0.2F;
    }

    public float getChainLength(float partialTick) {
        return prevChainLength + (chainLength - prevChainLength) * partialTick - 0.2F;
    }

    public float getWindProgress(float partialTick) {
        return prevWindProgress + (windProgress - prevWindProgress) * partialTick;
    }

    public boolean isAnchorEW() {
        return anchorEW;
    }

    public boolean isWinching() {
        return windTime > 0;
    }

    public boolean isWindingUp() {
        return prevPowered;
    }

    public boolean hasAnchor() {
        return draggingAnchor;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.prevPowered = compound.getBoolean("PullingUp");
        this.draggingAnchor = compound.getBoolean("DraggingAnchor");
        this.anchorEW = compound.getBoolean("EWAnchor");
        this.prevChainLength = this.chainLength = compound.getFloat("ChainLength");
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("PullingUp", prevPowered);
        compound.putBoolean("DraggingAnchor", draggingAnchor);
        compound.putBoolean("EWAnchor", anchorEW);
        compound.putFloat("ChainLength", chainLength);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            load(packet.getTag());
        }
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

}
