package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BlockEndPirateAnchor extends BaseEntityBlock implements AMSpecialRenderBlock {

    public static final BooleanProperty EASTORWEST = BooleanProperty.create("eastorwest");
    public static final BooleanProperty CENTER = BooleanProperty.create("center");
    protected static final VoxelShape FULL_AABB_EW = Block.box(0.0D, 0.0D, 4D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape FULL_AABB_NS = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);

    protected BlockEndPirateAnchor() {
        super(Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).strength(35.0F).lightLevel((i) -> 6).sound(SoundType.STONE).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(EASTORWEST, Boolean.valueOf(false)).setValue(CENTER, true));
    }

    public static boolean isClearForPlacement(LevelReader reader, BlockPos center, boolean eastOrWest) {
        List<BlockPos> offsets = TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest);
        for (BlockPos offset : offsets) {
            BlockPos check = center.offset(offset);
            BlockState state = reader.getBlockState(check);
            if ((!reader.isEmptyBlock(check) || !state.getMaterial().isReplaceable()) && !state.is(AMBlockRegistry.END_PIRATE_ANCHOR_CHAIN.get())) {
                return false;
            }
        }
        return true;
    }

    public static void placeAnchor(Level level, BlockPos pos, BlockState state) {
        List<BlockPos> offsets = TileEntityEndPirateAnchor.getValidBBPositions(state.getValue(EASTORWEST));
        for (BlockPos offset : offsets) {
            if (!offset.equals(BlockPos.ZERO)) {
                level.setBlock(pos.offset(offset), state.setValue(CENTER, false), 2);
            }
        }
    }

    public static void removeAnchor(Level level, BlockPos pos, boolean eastOrWest) {
        List<BlockPos> offsets = TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest);
        for (BlockPos offset : offsets) {
            level.setBlock(pos.offset(offset), Blocks.AIR.defaultBlockState(), 67);
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean force) {
        if (state.getValue(CENTER)) {
            removeAnchor(level, blockPos, state.getValue(EASTORWEST));
        }
        super.onRemove(state, level, blockPos, newState, force);
    }


    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader levelreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos actualPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState clickState = levelreader.getBlockState(actualPos);
        boolean axis = context.getHorizontalDirection().getAxis() == Direction.Axis.X;
        if (clickState.getBlock() instanceof BlockEndPirateAnchor) {
            axis = clickState.getValue(EASTORWEST);
        }
        return isClearForPlacement(levelreader, blockpos, axis) ? defaultBlockState().setValue(EASTORWEST, axis) : null;
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_52780_, boolean p_52781_) {
        if (!state.getValue(CENTER)) {
            for (int i = -2; i <= 2; i++) {
                for (int j = -3; j <= 3; j++) {
                    for (int k = -2; k <= 2; k++) {
                        BlockPos offsetPos = pos.offset(i, j, k);
                        if (level.getBlockEntity(offsetPos) instanceof TileEntityEndPirateAnchor anchor) {
                            BlockState offsetState = level.getBlockState(offsetPos);
                            if (!anchor.hasAllAnchorBlocks() && offsetState.is(this)) {
                                level.destroyBlock(offsetPos, true);
                            }
                        }
                    }
                }
            }
        }
        if (!canSurviveAnchor(state, level, pos)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        }
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
        placeAnchor(level, pos, state);
    }

    public boolean canSurviveAnchor(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(CENTER)) {
            return true;
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -3; j <= 0; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos offsetPos = pos.offset(i, j, k);
                        BlockState anchorState = world.getBlockState(offsetPos);
                        if (anchorState.getBlock() instanceof BlockEndPirateAnchor && anchorState.getValue(CENTER) && isPartOfAnchor(anchorState, world, offsetPos, pos, state.getValue(EASTORWEST))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public boolean isPartOfAnchor(BlockState anchor, LevelReader level, BlockPos center, BlockPos pos, boolean eastOrWest) {
        if (anchor.getValue(BlockEndPirateAnchor.EASTORWEST) == eastOrWest) {
            BlockPos offset = pos.subtract(center);
            return TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest).contains(offset);
        }
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58032_) {
        p_58032_.add(EASTORWEST, CENTER);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return state.getValue(EASTORWEST) ? FULL_AABB_NS : FULL_AABB_EW;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(CENTER) ? new TileEntityEndPirateAnchor(pos, state) : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState state, BlockEntityType<T> p_152182_) {
        return state.getValue(CENTER) ? createTickerHelper(p_152182_, AMTileEntityRegistry.END_PIRATE_ANCHOR.get(), TileEntityEndPirateAnchor::commonTick) : null;
    }

    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(CENTER) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(CENTER) ? super.getDrops(state, builder) : Collections.emptyList();
    }
}
