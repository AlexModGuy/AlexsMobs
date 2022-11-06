package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;

public class BlockBananaSlugSlime extends HalfTransparentBlock {

    protected static final VoxelShape SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public BlockBananaSlugSlime() {
        super(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_YELLOW).speedFactor(0.4F).jumpFactor(0.5F).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion());
    }

    public VoxelShape getVisualShape(BlockState p_48735_, BlockGetter p_48736_, BlockPos p_48737_, CollisionContext p_48738_) {
        return Shapes.empty();
    }


    public VoxelShape getCollisionShape(BlockState p_54015_, BlockGetter p_54016_, BlockPos p_54017_, CollisionContext p_54018_) {
        return SHAPE;
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.8));
        super.entityInside(state, level, pos, entity);
    }
    @Override
    public boolean isSlimeBlock(BlockState state) {
        return true;
    }

    @Override
    public boolean isStickyBlock(BlockState state) {
        return true;
    }

    public boolean propagatesSkylightDown(BlockState p_48740_, BlockGetter p_48741_, BlockPos p_48742_) {
        return true;
    }
    @Override
    public boolean canStickTo(BlockState state, @NotNull BlockState other) {
        return !other.isStickyBlock() || other.getBlock() == this;
    }

    public void onPlace(BlockState p_56811_, Level p_56812_, BlockPos p_56813_, BlockState p_56814_, boolean p_56815_) {
        if (!p_56814_.is(p_56811_.getBlock())) {
            this.tryAbsorbWater(p_56812_, p_56813_);
        }
    }

    public void neighborChanged(BlockState p_56801_, Level p_56802_, BlockPos p_56803_, Block p_56804_, BlockPos p_56805_, boolean p_56806_) {
        this.tryAbsorbWater(p_56802_, p_56803_);
        super.neighborChanged(p_56801_, p_56802_, p_56803_, p_56804_, p_56805_, p_56806_);
    }


    protected void tryAbsorbWater(Level level, BlockPos pos) {
        if (this.removeWaterBreadthFirstSearch(level, pos)) {
            level.playSound(null, pos, AMSoundRegistry.BANANA_SLUG_SLIME_EXPAND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private boolean removeWaterBreadthFirstSearch(Level level, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;
        BlockState state = level.getBlockState(pos);

        while(!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getA();
            int j = tuple.getB();

            for(Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos1);
                Material material = blockstate.getMaterial();
                if (blockstate.getBlock() instanceof BucketPickup && !((BucketPickup)blockstate.getBlock()).pickupBlock(level, blockpos1, blockstate).isEmpty()) {
                    ++i;
                    level.setBlock(blockpos1, AMBlockRegistry.CRYSTALIZED_BANANA_SLUG_MUCUS.get().defaultBlockState(), 3);
                    if (j < 6) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                } else if (blockstate.getBlock() instanceof LiquidBlock) {
                    level.setBlock(blockpos1, AMBlockRegistry.CRYSTALIZED_BANANA_SLUG_MUCUS.get().defaultBlockState(), 3);
                    ++i;
                    if (j < 6) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                    BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(blockpos1) : null;
                    dropResources(blockstate, level, blockpos1, blockentity);
                    level.setBlock(blockpos1, AMBlockRegistry.CRYSTALIZED_BANANA_SLUG_MUCUS.get().defaultBlockState(), 3);
                    ++i;
                    if (j < 6) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }
}
