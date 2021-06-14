package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.tileentity.TileEntityVoidWormBeak;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockVoidWormBeak extends ContainerBlock {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public BlockVoidWormBeak() {
        super(Properties.create(Material.DRAGON_EGG).notSolid().sound(SoundType.ANCIENT_DEBRIS).harvestTool(ToolType.PICKAXE).hardnessAndResistance(3F).doesNotBlockMovement());
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
        this.setRegistryName("alexsmobs:void_worm_beak");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityVoidWormBeak();
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
        builder.add(FACING);
    }
}
