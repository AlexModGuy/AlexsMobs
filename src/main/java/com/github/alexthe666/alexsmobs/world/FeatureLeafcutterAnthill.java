package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Iterator;
import java.util.Random;

public class FeatureLeafcutterAnthill extends Feature<NoneFeatureConfiguration> {

    public FeatureLeafcutterAnthill(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (context.level().getRandom().nextFloat() > 0.0175F) {
            return false;
        }
        int x = 8;
        int z = 8;
        BlockPos pos = context.origin();
        int y = context.level().getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX() + x, pos.getZ() + z);
        BlockPos heightPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z);
        if(!context.level().getFluidState(heightPos.below()).isEmpty()){
            return false;
        }
        int outOfGround = 2 + context.level().getRandom().nextInt(2);
        for (int i = 0; i < outOfGround; i++) {
            float size = outOfGround - i;
            int lvt_8_1_ = (int) (Math.floor(size) * context.level().getRandom().nextFloat()) + 2;
            int lvt_10_1_ = (int) (Math.floor(size) * context.level().getRandom().nextFloat()) + 2;
            float radius = (float) (lvt_8_1_ + lvt_10_1_) * 0.333F;
            Iterator var12 = BlockPos.betweenClosed(heightPos.offset(-lvt_8_1_, 0, -lvt_10_1_), heightPos.offset(lvt_8_1_, 3, lvt_10_1_)).iterator();
            while (var12.hasNext()) {
                BlockPos lvt_13_1_ = (BlockPos) var12.next();
                if (lvt_13_1_.distSqr(heightPos) <= (double) (radius * radius)) {
                    BlockState block = Blocks.COARSE_DIRT.defaultBlockState();
                    if (context.level().getRandom().nextFloat() < 0.2F) {
                        block = Blocks.DIRT.defaultBlockState();
                    }
                    context.level().setBlock(lvt_13_1_, block, 4);
                }
            }
        }
        Random chunkSeedRandom = new Random(pos.asLong());
        outOfGround -= chunkSeedRandom.nextInt(1) + 1;
        heightPos = heightPos.offset(-chunkSeedRandom.nextInt(2), 0, -chunkSeedRandom.nextInt(2));
        if (context.level().getBlockState(heightPos.above(outOfGround + 1)).getBlock() != AMBlockRegistry.LEAFCUTTER_ANTHILL.get() && context.level().getBlockState(heightPos.above(outOfGround - 1)).getBlock() != AMBlockRegistry.LEAFCUTTER_ANTHILL.get()) {
            context.level().setBlock(heightPos.above(outOfGround), AMBlockRegistry.LEAFCUTTER_ANTHILL.get().defaultBlockState(), 4);
            BlockEntity tileentity = context.level().getBlockEntity(heightPos.above(outOfGround));
            if (tileentity instanceof TileEntityLeafcutterAnthill) {
                TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill)tileentity;
                int j = 3 + chunkSeedRandom.nextInt(3);
                if(beehivetileentity.hasNoAnts()){
                    for(int k = 0; k < j; ++k) {
                        EntityLeafcutterAnt beeentity = new EntityLeafcutterAnt(AMEntityRegistry.LEAFCUTTER_ANT.get(), context.level().getLevel());
                        beeentity.setQueen(k == 0);
                        beehivetileentity.tryEnterHive(beeentity, false, context.level().getRandom().nextInt(599));
                    }
                }
            }
            if(context.level().getRandom().nextBoolean()){
                context.level().setBlock(heightPos.above(outOfGround).north(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 1).north(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 2).north(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
            }
            if(context.level().getRandom().nextBoolean()){
                context.level().setBlock(heightPos.above(outOfGround).east(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 1).east(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 2).east(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
            }
            if(context.level().getRandom().nextBoolean()){
                context.level().setBlock(heightPos.above(outOfGround).south(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 1).south(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 2).south(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
            }
            if(context.level().getRandom().nextBoolean()){
                context.level().setBlock(heightPos.above(outOfGround).west(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 1).west(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
                context.level().setBlock(heightPos.above(outOfGround - 2).west(), Blocks.COARSE_DIRT.defaultBlockState(), 4);
            }
            for(int airs = 1; airs < 3; airs++){
                context.level().setBlock(heightPos.above(outOfGround + airs), Blocks.AIR.defaultBlockState(), 4);
            }
        }
        int i = outOfGround;
        int down = context.level().getRandom().nextInt(2) + 1;
        while (i > -down) {
            i--;
            context.level().setBlock(heightPos.above(i), AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get().defaultBlockState(), 4);
        }
        float size = chunkSeedRandom.nextInt(1) + 1;
        int lvt_8_1_ = (int) (Math.floor(size) * context.level().getRandom().nextFloat()) + 1;
        int lvt_9_1_ = (int) (Math.floor(size) * context.level().getRandom().nextFloat()) + 1;
        int lvt_10_1_ = (int) (Math.floor(size) * context.level().getRandom().nextFloat()) + 1;
        float radius = (float) (lvt_8_1_ + lvt_9_1_ + lvt_10_1_) * 0.333F + 0.5F;
        heightPos = heightPos.below(down + lvt_9_1_).offset(chunkSeedRandom.nextInt(2), 0, chunkSeedRandom.nextInt(2));
        Iterator var12 = BlockPos.betweenClosed(heightPos.offset(-lvt_8_1_, -lvt_9_1_, -lvt_10_1_), heightPos.offset(lvt_8_1_, lvt_9_1_, lvt_10_1_)).iterator();
        while (var12.hasNext()) {
            BlockPos lvt_13_1_ = (BlockPos) var12.next();
            if (lvt_13_1_.distSqr(heightPos) < (double) (radius * radius)) {
                BlockState block = AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get().defaultBlockState();
                context.level().setBlock(lvt_13_1_, block, 4);
            }
        }
        return true;
    }
}
