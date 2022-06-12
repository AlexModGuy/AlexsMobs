package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityGust;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public class BlockGustmaker extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public BlockGustmaker() {
        super(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.valueOf(false)));
    }

    public static Vec3 getDispensePosition(BlockPos coords, Direction dir) {
        double d0 = coords.getX() + 0.5D + 0.7D * (double) dir.getStepX();
        double d1 = coords.getY() + 0.15D + 0.7D * (double) dir.getStepY();
        double d2 = coords.getZ() + 0.5D + 0.7D * (double) dir.getStepZ();
        return new Vec3(d0, d1, d2);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        tickGustmaker(state, worldIn, pos, false);
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        tickGustmaker(state, worldIn, pos, true);
    }

    public void tickGustmaker(BlockState state, Level worldIn, BlockPos pos, boolean tickOff) {
        boolean flag = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.below()) || worldIn.hasNeighborSignal(pos.above());
        boolean flag1 = state.getValue(TRIGGERED);
        if (flag && !flag1) {
            Vec3 dispensePosition = getDispensePosition(pos, state.getValue(FACING));
            Vec3 gustDir = Vec3.atLowerCornerOf(state.getValue(FACING).getNormal()).multiply(0.1, 0.1, 0.1);
            EntityGust gust = new EntityGust(worldIn);
            gust.setGustDir((float) gustDir.x, (float) gustDir.y, (float) gustDir.z);
            gust.setPos(dispensePosition.x, dispensePosition.y, dispensePosition.z);
            if(state.getValue(FACING).getAxis() == Direction.Axis.Y){
                gust.setVertical(true);
            }
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(gust);
            }
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.valueOf(true)), 2);
            worldIn.scheduleTick(pos, this, 20);
        } else if (flag1) {
            if (tickOff) {
                worldIn.scheduleTick(pos, this, 20);
                worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.valueOf(false)), 2);
            }
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }
}
