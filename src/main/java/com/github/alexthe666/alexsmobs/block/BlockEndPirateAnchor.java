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
    public static final EnumProperty<BlockEndPirateAnchor.PieceType> PIECE = EnumProperty.create("piece", BlockEndPirateAnchor.PieceType.class);
    protected static final VoxelShape FULL_AABB_EW = Block.box(0.0D, 0.0D, 4D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape FULL_AABB_NS = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape CHAIN_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    protected BlockEndPirateAnchor() {
        super(Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).friction(0.97F).strength(10.0F).lightLevel((i) -> 6).sound(SoundType.STONE).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(EASTORWEST, Boolean.valueOf(false)).setValue(PIECE, PieceType.ANCHOR));
    }

    public static boolean isClearForPlacement(LevelReader reader, BlockPos center, boolean eastOrWest) {
        List<BlockPos> offsets = TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest);
        for (BlockPos offset : offsets) {
            BlockPos check = center.offset(offset);
            if (!reader.isEmptyBlock(check) || !reader.getBlockState(check).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return true;
    }

    public static void placeAnchor(Level level, BlockPos pos, BlockState state) {
        List<BlockPos> offsets = TileEntityEndPirateAnchor.getValidBBPositions(state.getValue(EASTORWEST));
        for (BlockPos offset : offsets) {
            if (!offset.equals(BlockPos.ZERO)) {
                level.setBlock(pos.offset(offset), state.setValue(PIECE, PieceType.ANCHOR_SIDE), 2);
            }
        }
    }

    public static void removeAnchor(Level level, BlockPos pos, BlockState state) {
        List<BlockPos> offsets = TileEntityEndPirateAnchor.getValidBBPositions(state.getValue(EASTORWEST));
        for (BlockPos offset : offsets) {
            level.setBlock(pos.offset(offset), Blocks.AIR.defaultBlockState(), 67);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader levelreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos actualPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockPos u = blockpos.above();
        BlockPos d = blockpos.below();
        BlockState clickState = levelreader.getBlockState(actualPos);
        boolean axis = context.getHorizontalDirection().getAxis() == Direction.Axis.X;
        if (clickState.getBlock() instanceof BlockEndPirateAnchor) {
            axis = clickState.getValue(EASTORWEST);
        }
        return isClearForPlacement(levelreader, blockpos, axis) ? defaultBlockState().setValue(EASTORWEST, axis) : null;
    }

    public boolean isLadder(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
        return state.getValue(PIECE) == PieceType.CHAIN;
    }

    public boolean isScaffolding(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return state.getValue(PIECE) == PieceType.CHAIN;
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && state.getValue(PIECE) == PieceType.CHAIN) {
            LivingEntity livingEntity = (LivingEntity)entity;
            if (livingEntity.horizontalCollision && !livingEntity.isInWater()) {
                livingEntity.fallDistance = 0.0F;
                Vec3 motion = livingEntity.getDeltaMovement();
                double d0 = Mth.clamp(motion.x, -0.15F, 0.15F);
                double d1 = Mth.clamp(motion.z, -0.15F, 0.15F);
                double d2 = 0.3D;
                if (d2 < 0.0D && livingEntity.isSuppressingSlidingDownLadder()) {
                    d2 = 0.0D;
                }
                motion = new Vec3(d0, d2, d1);
                livingEntity.setDeltaMovement(motion);
            }
        }
    }


    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_52780_, boolean p_52781_) {
        if (state.getValue(PIECE) == PieceType.ANCHOR_SIDE) {
            for (int i = -2; i <= 2; i++) {
                for (int j = -3; j <= 3; j++) {
                    for (int k = -2; k <= 2; k++) {
                        BlockPos offsetPos = pos.offset(i, j, k);
                        if (level.getBlockEntity(offsetPos) instanceof TileEntityEndPirateAnchor anchor) {
                            if (!anchor.hasAllAnchorBlocks()) {
                                removeAnchor(level, offsetPos, level.getBlockState(offsetPos));
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
        if (state.getValue(PIECE) == PieceType.ANCHOR) {
            return true;
        } else if (state.getValue(PIECE) == PieceType.ANCHOR_SIDE) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -3; j <= 0; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos offsetPos = pos.offset(i, j, k);
                        BlockState anchorState = world.getBlockState(offsetPos);
                        if (anchorState.getBlock() instanceof BlockEndPirateAnchor && anchorState.getValue(PIECE) == PieceType.ANCHOR && isPartOfAnchor(anchorState, world, offsetPos, pos, state.getValue(EASTORWEST))) {
                            return true;
                        }
                    }
                }
            }
        } else if (state.getValue(PIECE) == PieceType.CHAIN) {
            BlockPos below = pos.below();
            BlockState chainBelow = world.getBlockState(below);
            BlockState chainAbove = world.getBlockState(below);
            return chainBelow.getBlock() instanceof BlockEndPirateAnchor && (chainAbove.getBlock() instanceof BlockEndPirateAnchor || chainAbove.getBlock() instanceof BlockEndPirateAnchorWinch);
        }
        return false;
    }

    public boolean isPartOfAnchor(BlockState anchor, LevelReader level, BlockPos center, BlockPos pos, boolean eastOrWest) {
        if (anchor.getValue(BlockEndPirateAnchor.EASTORWEST) == eastOrWest) {
            BlockPos offset = pos.subtract(center);
            return TileEntityEndPirateAnchor.getValidBBPositions(eastOrWest).contains(offset);
        }
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58032_) {
        p_58032_.add(EASTORWEST, PIECE);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (state.getValue(PIECE) == PieceType.CHAIN) {
            return CHAIN_AABB;
        }
        return state.getValue(EASTORWEST) ? FULL_AABB_NS : FULL_AABB_EW;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PIECE) == PieceType.ANCHOR ? new TileEntityEndPirateAnchor(pos, state) : null;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState state, BlockEntityType<T> p_152182_) {
        return state.getValue(PIECE) == PieceType.ANCHOR ? createTickerHelper(p_152182_, AMTileEntityRegistry.END_PIRATE_ANCHOR.get(), TileEntityEndPirateAnchor::commonTick) : null;
    }

    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PIECE) == PieceType.ANCHOR_SIDE ? RenderShape.INVISIBLE : RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(PIECE) == PieceType.ANCHOR ? super.getDrops(state, builder) : Collections.emptyList();
    }

    public enum PieceType implements StringRepresentable {
        ANCHOR,
        ANCHOR_SIDE,
        CHAIN;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
