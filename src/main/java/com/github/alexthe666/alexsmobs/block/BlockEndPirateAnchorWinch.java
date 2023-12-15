package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateAnchorWinch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockEndPirateAnchorWinch extends BaseEntityBlock implements AMSpecialRenderBlock{

    public static final BooleanProperty EASTORWEST = BooleanProperty.create("eastorwest");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape FULL_AABB_EW = Block.box(3.0D, 3.0D, 0.0D, 13.0D, 13.0D, 16.0D);
    protected static final VoxelShape FULL_AABB_NS = Block.box(0.0D, 3.0D, 3.0D, 16.0D, 13.0D, 13.0D);

    protected BlockEndPirateAnchorWinch() {
        super(Properties.of().mapColor(MapColor.COLOR_BLACK).friction(0.97F).strength(10.0F).lightLevel((i) -> 6).sound(SoundType.STONE).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(EASTORWEST, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EASTORWEST, POWERED);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return state.getValue(EASTORWEST) ? FULL_AABB_EW : FULL_AABB_NS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
//        LevelReader levelreader = context.getLevel();
//        BlockPos blockpos = context.getClickedPos();
//        BlockPos actualPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
//        BlockPos u = blockpos.above();
//        BlockPos d = blockpos.below();
//        BlockState clickState = levelreader.getBlockState(actualPos);
//        BlockState upState = levelreader.getBlockState(u);
//        BlockState downState = levelreader.getBlockState(d);
        boolean axis = context.getClickedFace().getAxis() == Direction.Axis.Y ? context.getHorizontalDirection().getAxis() == Direction.Axis.X : context.getClickedFace().getAxis() != Direction.Axis.X;
        return defaultBlockState().setValue(EASTORWEST, axis);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
        if(level.getBlockEntity(pos) instanceof TileEntityEndPirateAnchorWinch winch){
            winch.recalculateChains();
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_52780_, boolean p_52781_) {
        boolean flag = level.hasNeighborSignal(pos);
        if(level.getBlockEntity(pos) instanceof TileEntityEndPirateAnchorWinch winch){
            if(flag != state.getValue(POWERED)){
                level.setBlock(pos, state.setValue(POWERED, flag), 3);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityEndPirateAnchorWinch(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState state, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, AMTileEntityRegistry.END_PIRATE_ANCHOR_WINCH.get(), TileEntityEndPirateAnchorWinch::commonTick);
    }
}
