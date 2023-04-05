package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTriops;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockTriopsEggs extends FrogspawnBlock {
    public BlockTriopsEggs() {
        super(BlockBehaviour.Properties.of(Material.FROGSPAWN).instabreak().noOcclusion().noCollission().sound(SoundType.FROGSPAWN).offsetType(OffsetType.XZ));
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!this.canSurvive(blockState, serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, false);
        } else if (serverLevel.getFluidState(blockPos.below()).is(FluidTags.WATER)) {
            serverLevel.destroyBlock(blockPos, false);
            int i = 2 + randomSource.nextInt(2);
            for (int j = 1; j <= i; ++j) {
                EntityTriops tadpole = AMEntityRegistry.TRIOPS.get().create(serverLevel);
                if (tadpole != null) {
                    double d0 = (double) blockPos.getX();
                    double d1 = (double) blockPos.getZ();
                    int k = randomSource.nextInt(1, 361);
                    tadpole.moveTo(d0, (double) blockPos.getY() - 0.5D, d1, (float) k, 0.0F);
                    tadpole.setPersistenceRequired();
                    tadpole.setBabyAge(-12000);
                    serverLevel.addFreshEntity(tadpole);
                }
            }
        }
    }
}
