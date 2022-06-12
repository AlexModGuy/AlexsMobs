package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Random;

public class BlockEnderResidue extends AbstractGlassBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public BlockEnderResidue() {
        super(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE).noOcclusion().hasPostProcess((i, j, k) -> true).emissiveRendering((i, j, k) -> true).lightLevel((i) -> 3).strength(0.2F).sound(SoundType.AMETHYST).randomTicks().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }

    public void randomTick(BlockState p_53588_, ServerLevel p_53589_, BlockPos p_53590_, RandomSource p_53591_) {
        this.tick(p_53588_, p_53589_, p_53590_, p_53591_);
    }


    public void tick(BlockState p_53574_, ServerLevel p_53575_, BlockPos p_53576_, RandomSource p_53577_) {
        if ((p_53577_.nextInt(5) == 0)) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            for (Direction direction : Direction.values()) {
                blockpos$mutableblockpos.setWithOffset(p_53576_, direction);
                BlockState blockstate = p_53575_.getBlockState(blockpos$mutableblockpos);
                if (blockstate.is(this) && !this.incrementAge(blockstate, p_53575_, blockpos$mutableblockpos)) {
                    p_53575_.scheduleTick(blockpos$mutableblockpos, this, Mth.nextInt(p_53577_, 20, 40));
                }
            }
            this.incrementAge(p_53574_, p_53575_, p_53576_);
        } else {
            p_53575_.scheduleTick(p_53576_, this, Mth.nextInt(p_53577_, 20, 40));
        }
    }

    private boolean incrementAge(BlockState state, Level level, BlockPos pos) {
        int i = state.getValue(AGE);
        if (i < 3) {
            level.setBlock(pos, state.setValue(AGE, Integer.valueOf(i + 1)), 2);
            return false;
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            return true;
        }
    }

    public void neighborChanged(BlockState p_53579_, Level p_53580_, BlockPos p_53581_, Block p_53582_, BlockPos p_53583_, boolean p_53584_) {
        super.neighborChanged(p_53579_, p_53580_, p_53581_, p_53582_, p_53583_, p_53584_);
    }

    private boolean fewerNeigboursThan(BlockGetter p_53566_, BlockPos p_53567_, int p_53568_) {
        int i = 0;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(p_53567_, direction);
            if (p_53566_.getBlockState(blockpos$mutableblockpos).is(this)) {
                ++i;
                if (i >= p_53568_) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53586_) {
        p_53586_.add(AGE);
    }

    public ItemStack getCloneItemStack(BlockGetter p_53570_, BlockPos p_53571_, BlockState p_53572_) {
        return ItemStack.EMPTY;
    }
}