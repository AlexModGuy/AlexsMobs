package com.github.alexthe666.alexsmobs.block;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBananaPeel extends BushBlock {

    protected static final VoxelShape SHAPE_COLLISON = Block.box(0, 0, 0, 16.0D, 9.0D, 16.0D);
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    public BlockBananaPeel() {
        super(BlockBehaviour.Properties.of(Material.PLANT).dynamicShape().sound(SoundType.WET_GRASS).noCollission().requiresCorrectToolForDrops().strength(0.2F).friction(0.9999999999F));
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return canSupportRigidBlock(worldIn, pos);
    }

    public BlockBehaviour.OffsetType getOffsetType() {
        return BlockBehaviour.OffsetType.XZ;
    }

    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE_COLLISON;
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return SHAPE_COLLISON;
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

}
