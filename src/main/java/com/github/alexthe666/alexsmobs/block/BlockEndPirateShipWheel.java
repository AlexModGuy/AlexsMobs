package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateShipWheel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockEndPirateShipWheel extends BaseEntityBlock implements AMSpecialRenderBlock{

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SOUTH_AABB = Block.box(-2, -2, 0, 18, 18, 3);
    private static final VoxelShape NORTH_AABB = Block.box(-2, -2, 13, 18, 18, 16);
    private static final VoxelShape EAST_AABB = Block.box(0, -2, -2, 3, 18, 18);
    private static final VoxelShape WEST_AABB = Block.box(13, -2, -2, 16, 18, 18);
    private static final VoxelShape UP_AABB = Block.box(-2, 0, -2, 18, 3, 18);
    private static final VoxelShape DOWN_AABB = Block.box(-2, 13, -2, 16, 16, 18);

    public BlockEndPirateShipWheel() {
        super(Properties.of(Material.EGG).noOcclusion().sound(SoundType.ANCIENT_DEBRIS).strength(1F).lightLevel((i) -> 3).noCollission().requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos p_52801_) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state2, level, pos, p_52801_);
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return switch (p_54561_.getValue(FACING)) {
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
            case UP -> UP_AABB;
            default -> DOWN_AABB;
        };
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        boolean remove = false;
        Direction dir = state.getValue(FACING).getOpposite();
        BlockPos offset = pos.relative(dir);
        return remove || world.getBlockState(offset).isFaceSturdy(world, offset, dir.getOpposite());
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(worldIn.getBlockEntity(pos) instanceof TileEntityEndPirateShipWheel wheel){
            boolean clockwise = false;
            Vec3 offset = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            switch (state.getValue(FACING)) {
                case NORTH -> clockwise = offset.x <= 0.5F;
                case SOUTH -> clockwise = offset.x >= 0.5F;
                case EAST -> clockwise = offset.z <= 0.5F;
                case WEST -> clockwise = offset.z >= 0.5F;
            }
            wheel.rotate(clockwise);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityEndPirateShipWheel(pos, state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, AMTileEntityRegistry.END_PIRATE_SHIP_WHEEL.get(), TileEntityEndPirateShipWheel::commonTick);
    }
}
