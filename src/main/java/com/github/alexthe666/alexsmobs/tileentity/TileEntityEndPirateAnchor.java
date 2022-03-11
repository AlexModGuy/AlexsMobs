package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateAnchor;
import com.github.alexthe666.alexsmobs.block.BlockEndPirateDoor;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class TileEntityEndPirateAnchor extends BlockEntity {

    private static final List<BlockPos> VALID_OFFSET_BOXES_NS = Lists.newArrayList(
            new BlockPos(0, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(0, 2, 0)
    );

    private static final List<BlockPos> VALID_OFFSET_BOXES_EW = Lists.newArrayList(
            new BlockPos(0, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(0, 1, 0),
            new BlockPos(0, 2, 0)
    );

    public TileEntityEndPirateAnchor(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.END_PIRATE_ANCHOR.get(), pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateAnchor entity) {
        entity.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-1, 0, -1), worldPosition.offset(1, 3, 1));
    }

    public static List<BlockPos> getValidBBPositions(boolean eastOrWest){
        return eastOrWest ? VALID_OFFSET_BOXES_EW : VALID_OFFSET_BOXES_NS;
    }

    public boolean hasAllAnchorBlocks() {
        for(BlockPos pos : TileEntityEndPirateAnchor.getValidBBPositions(this.getBlockState().getValue(BlockEndPirateAnchor.EASTORWEST))){
            if(!(getLevel().getBlockState(this.getBlockPos().offset(pos)).getBlock() instanceof BlockEndPirateAnchor)){
                return false;
            }
        }
        return true;
    }

    private void tick() {
    }
}
