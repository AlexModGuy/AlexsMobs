package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockHummingbirdFeeder extends Block {
    public static final IntegerProperty CONTENTS = IntegerProperty.create("contents", 0, 3);
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape AABB = Block.box(4, 0, 4, 12, 12, 12);
    private static final VoxelShape AABB_HANGING = Block.box(4, 0, 4, 12, 16, 12);

    public BlockHummingbirdFeeder() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.LANTERN).strength(0.5F).randomTicks().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(CONTENTS, 0).setValue(HANGING, false));
    }

    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? AABB_HANGING : AABB;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        for(Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, Boolean.valueOf(direction == Direction.UP));
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
                }
            }
        }

        return null;
    }


    protected static Direction getBlockConnected(BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        int contents = state.getValue(CONTENTS);
        ItemStack waterBottle = AMEffectRegistry.createPotion(Potions.WATER);
        ItemStack itemStack = player.getItemInHand(handIn);
        int setContent = -1;
        if(contents == 0){
            if(itemStack.is(AMTagRegistry.HUMMINGBIRD_FEEDER_SWEETENERS)){
                setContent = 2;
                useItem(player, itemStack, false);
            }else if(itemStack.getItem() == waterBottle.getItem() && ItemStack.isSameItemSameTags(waterBottle, itemStack)){
                setContent = 1;
                useItem(player, itemStack, true);
            }
        }else if(contents == 1){
            if(itemStack.is(AMTagRegistry.HUMMINGBIRD_FEEDER_SWEETENERS)){
                setContent = 3;
                useItem(player, itemStack, false);
            }
        }else if(contents == 2){
            if(itemStack.getItem() == waterBottle.getItem() && ItemStack.isSameItemSameTags(waterBottle, itemStack)){
                setContent = 3;
                useItem(player, itemStack, true);
            }
        }
        if(setContent >= 0){
            worldIn.setBlockAndUpdate(pos, state.setValue(CONTENTS, setContent));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public void useItem(Player playerEntity, ItemStack stack, boolean dropBottle){
        if(!playerEntity.isCreative()){
            if(dropBottle){
                playerEntity.addItem(new ItemStack(Items.GLASS_BOTTLE));
            }
            stack.shrink(1);
        }
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        Direction direction = getBlockConnected(state).getOpposite();
        return Block.canSupportCenter(worldIn, pos.relative(direction), direction.getOpposite());
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return getBlockConnected(stateIn).getOpposite() == facing && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENTS, HANGING, WATERLOGGED);
    }
}
