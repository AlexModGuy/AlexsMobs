package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityCapsid;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityEndPirateDoor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BlockEndPirateDoor extends BaseEntityBlock {

    public static final DirectionProperty HORIZONTAL_FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final IntegerProperty SEGMENT = IntegerProperty.create("segment", 0, 2);
    protected static final float AABB_DOOR_THICKNESS = 3.0F;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);

    public BlockEndPirateDoor() {
        super(Properties.of(Material.GLASS).noOcclusion().sound(SoundType.GLASS).lightLevel((state) -> 3).requiresCorrectToolForDrops().strength(1.5F));
        this.registerDefaultState(this.stateDefinition.any().setValue(SEGMENT, 0).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.RIGHT).setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    public VoxelShape getShape(BlockState p_52807_, BlockGetter p_52808_, BlockPos p_52809_, CollisionContext p_52810_) {
        Direction direction = p_52807_.getValue(HORIZONTAL_FACING);
        boolean flag = !p_52807_.getValue(OPEN);
        boolean flag1 = p_52807_.getValue(HINGE) == DoorHingeSide.RIGHT;
        switch(direction) {
            case EAST:
            default:
                return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
            case SOUTH:
                return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
            case WEST:
                return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
            case NORTH:
                return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos p_52801_) {
        if(state.getValue(SEGMENT) == 0){
            return !state.canSurvive(level, pos) || !level.getBlockState(pos.above()).is(this) || !level.getBlockState(pos.above(2)).is(this) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state2, level, pos, p_52801_);

        }
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state2, level, pos, p_52801_);
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return (BlockState)p_185499_1_.setValue(HORIZONTAL_FACING, p_185499_2_.rotate((Direction)p_185499_1_.getValue(HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mir) {
        return state.rotate(mir.getRotation((Direction)state.getValue(HORIZONTAL_FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEGMENT, HORIZONTAL_FACING, OPEN, HINGE, POWERED);
    }

    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(SEGMENT) == 0 ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(SEGMENT) == 0 ? new TileEntityEndPirateDoor(pos, state) : null;
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_52780_, boolean p_52781_) {

        boolean flag = level.hasNeighborSignal(pos);
        if(state.getValue(SEGMENT) == 0){
            flag = flag || level.hasNeighborSignal(pos.above()) || level.hasNeighborSignal(pos.above(2));
        }
        if(state.getValue(SEGMENT) == 1){
            flag = flag || level.hasNeighborSignal(pos.below()) || level.hasNeighborSignal(pos.above());
        }
        if(state.getValue(SEGMENT) == 2){
            flag = flag || level.hasNeighborSignal(pos.below()) || level.hasNeighborSignal(pos.below(2));
        }
        if (!this.defaultBlockState().is(block) && flag != state.getValue(POWERED)) {
            if (flag != state.getValue(OPEN)) {
               // level.gameEvent(flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
            Direction swap = state.getValue(HINGE) == DoorHingeSide.LEFT ? state.getValue(HORIZONTAL_FACING).getClockWise() : state.getValue(HORIZONTAL_FACING).getCounterClockWise();
            BlockPos relative = pos.relative(swap);
            BlockState neighbor = level.getBlockState(relative);
            if(neighbor.getBlock() == this && state.getValue(HINGE) != neighbor.getValue(HINGE)){
                openDoorAt(level, relative, flag, flag);
            }
            openDoorAt(level, pos, flag, flag);

        }

    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        boolean open = state.getValue(OPEN);
        boolean powered = state.getValue(POWERED);
        Direction swap = state.getValue(HINGE) == DoorHingeSide.LEFT ? state.getValue(HORIZONTAL_FACING).getClockWise() : state.getValue(HORIZONTAL_FACING).getCounterClockWise();
        BlockPos relative = pos.relative(swap);
        BlockState neighbor = worldIn.getBlockState(relative);
        if(neighbor.getBlock() == this && state.getValue(HINGE) != neighbor.getValue(HINGE)){
            openDoorAt(worldIn, relative, !open, powered);
        }
        openDoorAt(worldIn, pos, !open, powered);
        return InteractionResult.sidedSuccess(worldIn.isClientSide);
    }

    public static void openDoorAt(Level worldIn, BlockPos pos, boolean open, boolean powered) {
        TileEntityEndPirateDoor te = getDoorTE(worldIn, pos);
        if(te != null){
            BlockPos bottom = te.getBlockPos();
            for(int i = 0; i <= 2; i++){
                BlockPos up = bottom.above(i);
                if(worldIn.getBlockState(up).getBlock() instanceof BlockEndPirateDoor){
                    worldIn.setBlock(up, worldIn.getBlockState(up).setValue(OPEN, open).setValue(POWERED, powered), 10);
                }
            }
        }
    }

    public static TileEntityEndPirateDoor getDoorTE(Level worldIn, BlockPos pos) {
        for (int i = 0; i <= 2; i++){
            if(worldIn.getBlockEntity(pos.below(i)) instanceof TileEntityEndPirateDoor e){
                return e;
            }
        }
        return null;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_52739_) {
        BlockPos blockpos = p_52739_.getClickedPos();
        Level level = p_52739_.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(p_52739_) && level.getBlockState(blockpos.above(2)).canBeReplaced(p_52739_)) {
            boolean flag = level.hasNeighborSignal(blockpos) || level.hasNeighborSignal(blockpos.above());
            return this.defaultBlockState().setValue(HORIZONTAL_FACING, p_52739_.getHorizontalDirection()).setValue(HINGE, this.getHinge(p_52739_)).setValue(OPEN, Boolean.valueOf(flag)).setValue(SEGMENT, 0);
        } else {
            return null;
        }
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        p_52749_.setBlock(p_52750_.above(), p_52751_.setValue(SEGMENT, 1), 3);
        p_52749_.setBlock(p_52750_.above(2), p_52751_.setValue(SEGMENT, 2), 3);
    }

    private DoorHingeSide getHinge(BlockPlaceContext p_52805_) {
        BlockGetter blockgetter = p_52805_.getLevel();
        BlockPos blockpos = p_52805_.getClickedPos();
        Direction direction = p_52805_.getHorizontalDirection();
        BlockPos blockpos1 = blockpos.above();
        Direction direction1 = direction.getCounterClockWise();
        BlockPos blockpos2 = blockpos.relative(direction1);
        BlockState blockstate = blockgetter.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.relative(direction1);
        BlockState blockstate1 = blockgetter.getBlockState(blockpos3);
        Direction direction2 = direction.getClockWise();
        BlockPos blockpos4 = blockpos.relative(direction2);
        BlockState blockstate2 = blockgetter.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.relative(direction2);
        BlockState blockstate3 = blockgetter.getBlockState(blockpos5);
        int i = (blockstate.isCollisionShapeFullBlock(blockgetter, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeFullBlock(blockgetter, blockpos3) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(blockgetter, blockpos4) ? 1 : 0) + (blockstate3.isCollisionShapeFullBlock(blockgetter, blockpos5) ? 1 : 0);
        boolean flag = blockstate.is(this) && blockstate.getValue(SEGMENT) == 0;
        boolean flag1 = blockstate2.is(this) && blockstate2.getValue(SEGMENT) == 0;
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getStepX();
                int k = direction.getStepZ();
                Vec3 vec3 = p_52805_.getClickLocation();
                double d0 = vec3.x - (double)blockpos.getX();
                double d1 = vec3.z - (double)blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }


    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        switch (state.getValue(SEGMENT)){
            case 0:
                return world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP);
            case 1:
                return world.getBlockState(pos.below()).is(this) && world.getBlockState(pos.above()).is(this);
            case 2:
                return world.getBlockState(pos.below()).is(this) && world.getBlockState(pos.below(2)).is(this);
        }
        return false;
    }


    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState state, BlockEntityType<T> p_152182_) {
        return state.getValue(SEGMENT) == 0 ? createTickerHelper(p_152182_, AMTileEntityRegistry.END_PIRATE_DOOR.get(), TileEntityEndPirateDoor::commonTick) : null;
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(SEGMENT) == 0? super.getDrops(state, builder) : Collections.emptyList();
    }
}
