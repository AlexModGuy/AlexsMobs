package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchorWinch;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityEndPirateAnchorWinch extends BlockEntity {

    public float clientRoll;
    public int windCounter = 0;
    private int prevTargetChainLength;
    private int targetChainLength = 0;
    private float prevMaximumChainLength;
    private float chainLength;
    private float prevChainLength;
    private int windTime = 0;
    private int ticksExisted = 0;
    private float windProgress;
    private float prevWindProgress;
    private boolean draggingAnchor;
    private boolean anchorEW;
    private boolean pullingUp;
    private boolean hasPower;
    private int anchorPlaceCooldown = 0;

    public TileEntityEndPirateAnchorWinch(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.END_PIRATE_ANCHOR_WINCH.get(), pos, state);
        prevTargetChainLength = targetChainLength;
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateAnchorWinch entity) {
        entity.tick();
    }

    private int calcChainLength(boolean goBelowAnchor) {
        BlockPos down = this.getBlockPos().below();
        while (level != null && down.getY() > level.getMinBuildHeight() && !isAnchorTop(level, down) && (isEmptyBlock(down) || isAnchorChain(level, down))) {
            down = down.below();
        }
        int i = 0;
        if (isAnchorTop(level, down) || goBelowAnchor) {

            if (goBelowAnchor){// && level.getBlockState(down.below(2)).getBlock() == AMBlockRegistry.END_PIRATE_ANCHOR.get()) {
                i = this.getBlockPos().getY() - 1 - keepMovingBelowAnchor(down.below(2));
            } else {
                i = this.getBlockPos().getY() - 1 - down.getY();
            }
        }
        if (draggingAnchor) {
            return i - 3;
        }
        return i;
    }

    private int keepMovingBelowAnchor(BlockPos below) {
        while (below.getY() > level.getMinBuildHeight() && isEmptyBlock(below)) {
            below = below.below();
        }
        return below.getY();
    }

    private boolean isEmptyBlock(BlockPos pos) {
        return level.isEmptyBlock(pos) || isAnchorChain(level, pos) || level.getBlockState(pos).getMaterial().isReplaceable();
    }

    private boolean isAnchorChain(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof BlockEndPirateAnchor && level.getBlockState(pos).getValue(BlockEndPirateAnchor.PIECE) == BlockEndPirateAnchor.PieceType.CHAIN;
    }

    private boolean isAnchorTop(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof BlockEndPirateAnchor && level.getBlockState(pos.below(2)).getBlock() instanceof BlockEndPirateAnchor && level.getBlockState(pos.below(2)).getValue(BlockEndPirateAnchor.PIECE) == BlockEndPirateAnchor.PieceType.ANCHOR;
    }

    private void tick() {
        prevChainLength = chainLength;
        prevWindProgress = windProgress;
        prevTargetChainLength = targetChainLength;
        ticksExisted++;
        boolean powered = false;
        if(getBlockState().getBlock() instanceof BlockEndPirateAnchorWinch){
            powered = getBlockState().getValue(BlockEndPirateAnchorWinch.POWERED);
        }
        if(powered && pullingUp){
            sendDownChains();
        }
        if(!powered && !pullingUp){
            pullUpChains();
        }
        if (chainLength < targetChainLength) {
            chainLength = Math.min(chainLength + 0.1F, targetChainLength);
        }
        if (chainLength > targetChainLength) {
            chainLength = Math.max(chainLength - 0.1F, targetChainLength);
        }
        if (Math.abs(targetChainLength - chainLength) > 0.2F) {
            windTime = 5;
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
        if (anchorPlaceCooldown > 0) {
            anchorPlaceCooldown--;
        }
        if (chainLength != targetChainLength && isWindingUp() && !draggingAnchor) {
            BlockPos down = this.getBlockPos();
            if (anchorPlaceCooldown == 0 && (checkAndBreakAnchor(down.below()) || checkAndBreakAnchor(down.below(1 + (int) Math.ceil(chainLength))))) {
                draggingAnchor = true;
            }
        }
        if (chainLength == targetChainLength && draggingAnchor) {
            int offset = isWindingUp() ? 0 : targetChainLength;
            if (anchorPlaceCooldown == 0 && tryPlaceAnchor(offset)) {
                draggingAnchor = false;
            }
        }
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
                BlockEndPirateAnchor.removeAnchor(level, actualAnchorPos, level.getBlockState(actualAnchorPos));
                this.removeChainBlocks();
                return true;
            }
        }
        return false;
    }

    public boolean tryPlaceAnchor(int offset) {
        BlockPos at = this.getBlockPos().below(3 + offset);
        if (BlockEndPirateAnchor.isClearForPlacement(this.level, at, anchorEW)) {
            BlockState anchorState = null;//AMBlockRegistry.END_PIRATE_ANCHOR.get().defaultBlockState().setValue(BlockEndPirateAnchor.EASTORWEST, anchorEW);
            this.level.setBlock(at, anchorState, 2);
            BlockEndPirateAnchor.placeAnchor(level, at, anchorState);
            placeChainBlocks(offset);
            return true;
        }
        return false;
    }


    private void placeChainBlocks(int offset) {
        BlockPos at = this.getBlockPos().below(3 + offset);
        BlockPos chainPos = at.above(3);
        while (chainPos.getY() < this.getBlockPos().getY() - 1 && isEmptyBlock(chainPos)) {
          //  this.level.setBlock(chainPos, AMBlockRegistry.END_PIRATE_ANCHOR.get().defaultBlockState().setValue(BlockEndPirateAnchor.PIECE, BlockEndPirateAnchor.PieceType.CHAIN).setValue(BlockEndPirateAnchor.EASTORWEST, anchorEW), 3);
            chainPos = chainPos.above();
        }
    }

    private void removeChainBlocks() {
        BlockPos chainPos = this.getBlockPos().below(1 + (int) Math.ceil(chainLength));
        while (chainPos.getY() < this.getBlockPos().getY()) {
            if(isAnchorChain(level, chainPos)){
                this.level.setBlock(chainPos, Blocks.AIR.defaultBlockState(), 3);
            }
            chainPos = chainPos.above();
        }
    }

    public void recalculateChains() {
        if (targetChainLength != 0) {
            prevMaximumChainLength = targetChainLength;
        }
        BlockPos at = this.getBlockPos().below(1);
        if (isAnchorTop(level, at) && anchorPlaceCooldown == 0 && checkAndBreakAnchor(at)) {
            draggingAnchor = true;
        }
        targetChainLength = calcChainLength(draggingAnchor);
    }

    public void sendDownChains() {
        recalculateChains();
        pullingUp = false;
    }

    public void pullUpChains() {
        if (targetChainLength != 0) {
            prevMaximumChainLength = targetChainLength;
        }
        targetChainLength = 0;
        pullingUp = true;
    }

    public void onInteract(){

    }

    public float getChainLengthForRender() {
        return Math.max(targetChainLength, prevMaximumChainLength);
    }

    public float getChainLength(float partialTick) {
        return prevChainLength + (chainLength - prevChainLength) * partialTick;
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
        return pullingUp;
    }

    public boolean hasAnchor() {
        return draggingAnchor;
    }


    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.pullingUp = compound.getBoolean("PullingUp");
        this.draggingAnchor = compound.getBoolean("DraggingAnchor");
        this.anchorEW = compound.getBoolean("EWAnchor");
        this.prevChainLength = this.chainLength = compound.getFloat("ChainLength");
        this.targetChainLength = compound.getInt("TargetChainLength");
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("PullingUp", pullingUp);
        compound.putBoolean("DraggingAnchor", draggingAnchor);
        compound.putBoolean("EWAnchor", anchorEW);
        compound.putFloat("ChainLength", chainLength);
        compound.putInt("TargetChainLength", targetChainLength);
    }
}
