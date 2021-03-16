package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityGust;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockGustmaker extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public BlockGustmaker() {
        super(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(4.5F));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
        this.setRegistryName("alexsmobs:gustmaker");
    }

    public static Vector3d getDispensePosition(BlockPos coords, Direction dir) {
        double d0 = coords.getX() + 0.5D + 0.7D * (double) dir.getXOffset();
        double d1 = coords.getY() + 0.15D + 0.7D * (double) dir.getYOffset();
        double d2 = coords.getZ() + 0.5D + 0.7D * (double) dir.getZOffset();
        return new Vector3d(d0, d1, d2);
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        tickGustmaker(state, worldIn, pos, false);
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        tickGustmaker(state, worldIn, pos, true);
    }

    public void tickGustmaker(BlockState state, World worldIn, BlockPos pos, boolean tickOff) {
        boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.down()) || worldIn.isBlockPowered(pos.up());
        boolean flag1 = state.get(TRIGGERED);
        if (flag && !flag1) {
            Vector3d dispensePosition = getDispensePosition(pos, state.get(FACING));
            Vector3d gustDir = Vector3d.copy(state.get(FACING).getDirectionVec()).mul(0.1, 0.1, 0.1);
            EntityGust gust = new EntityGust(worldIn);
            gust.setGustDir((float) gustDir.x, (float) gustDir.y, (float) gustDir.z);
            gust.setPosition(dispensePosition.x, dispensePosition.y, dispensePosition.z);
            if(state.get(FACING).getAxis() == Direction.Axis.Y){
                gust.setVertical(true);
            }
            if (!worldIn.isRemote) {
                worldIn.addEntity(gust);
            }
            worldIn.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), 2);
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 20);
        } else if (flag1) {
            if (tickOff) {
                worldIn.getPendingBlockTicks().scheduleTick(pos, this, 20);
                worldIn.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), 2);
            }
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }
}
