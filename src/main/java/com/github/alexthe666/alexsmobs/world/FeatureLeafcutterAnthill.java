package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Iterator;
import java.util.Random;

public class FeatureLeafcutterAnthill extends Feature<NoFeatureConfig> {

    public FeatureLeafcutterAnthill(Codec<NoFeatureConfig> p_i231953_1_) {
        super(p_i231953_1_);
    }


    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos pos, NoFeatureConfig p_230362_6_) {
        if (rand.nextFloat() > 0.005F) {
            return false;
        }
        int z = 8;
        int x = 8;
        int y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX() + x, pos.getZ() + z);
        BlockPos heightPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z);
        if(!worldIn.getFluidState(heightPos.down()).isEmpty()){
            return false;
        }
        int outOfGround = 2 + rand.nextInt(2);
        for (int i = 0; i < outOfGround; i++) {
            float size = outOfGround - i;
            int lvt_8_1_ = (int) (Math.floor(size) * rand.nextFloat()) + 2;
            int lvt_10_1_ = (int) (Math.floor(size) * rand.nextFloat()) + 2;
            float radius = (float) (lvt_8_1_ + lvt_10_1_) * 0.333F;
            Iterator var12 = BlockPos.getAllInBoxMutable(heightPos.add(-lvt_8_1_, 0, -lvt_10_1_), heightPos.add(lvt_8_1_, 3, lvt_10_1_)).iterator();
            while (var12.hasNext()) {
                BlockPos lvt_13_1_ = (BlockPos) var12.next();
                if (lvt_13_1_.distanceSq(heightPos) <= (double) (radius * radius)) {
                    BlockState block = Blocks.COARSE_DIRT.getDefaultState();
                    if (rand.nextFloat() < 0.2F) {
                        block = Blocks.DIRT.getDefaultState();
                    }
                    worldIn.setBlockState(lvt_13_1_, block, 4);
                }
            }
        }
        Random chunkSeedRandom = new Random(pos.toLong());
        outOfGround -= chunkSeedRandom.nextInt(1) + 1;
        heightPos = heightPos.add(-chunkSeedRandom.nextInt(2), 0, -chunkSeedRandom.nextInt(2));
        if (worldIn.getBlockState(heightPos.up(outOfGround + 1)).getBlock() != AMBlockRegistry.LEAFCUTTER_ANTHILL && worldIn.getBlockState(heightPos.up(outOfGround - 1)).getBlock() != AMBlockRegistry.LEAFCUTTER_ANTHILL) {
            worldIn.setBlockState(heightPos.up(outOfGround), AMBlockRegistry.LEAFCUTTER_ANTHILL.getDefaultState(), 4);
            TileEntity tileentity = worldIn.getTileEntity(heightPos.up(outOfGround));
            if (tileentity instanceof TileEntityLeafcutterAnthill) {
                TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill)tileentity;
                int j = 3 + chunkSeedRandom.nextInt(3);
                if(beehivetileentity.hasNoAnts()){
                    for(int k = 0; k < j; ++k) {
                        EntityLeafcutterAnt beeentity = new EntityLeafcutterAnt(AMEntityRegistry.LEAFCUTTER_ANT, worldIn.getWorld());
                        beeentity.setQueen(k == 0);
                        beehivetileentity.tryEnterHive(beeentity, false, rand.nextInt(599));
                    }
                }

            }

            if(rand.nextBoolean()){
                worldIn.setBlockState(heightPos.up(outOfGround).north(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 1).north(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 2).north(), Blocks.COARSE_DIRT.getDefaultState(), 4);
            }
            if(rand.nextBoolean()){
                worldIn.setBlockState(heightPos.up(outOfGround).east(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 1).east(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 2).east(), Blocks.COARSE_DIRT.getDefaultState(), 4);
            }
            if(rand.nextBoolean()){
                worldIn.setBlockState(heightPos.up(outOfGround).south(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 1).south(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 2).south(), Blocks.COARSE_DIRT.getDefaultState(), 4);
            }
            if(rand.nextBoolean()){
                worldIn.setBlockState(heightPos.up(outOfGround).west(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 1).west(), Blocks.COARSE_DIRT.getDefaultState(), 4);
                worldIn.setBlockState(heightPos.up(outOfGround - 2).west(), Blocks.COARSE_DIRT.getDefaultState(), 4);
            }
        }
        int i = outOfGround;
        int down = rand.nextInt(2) + 1;
        while (i > -down) {
            i--;
            worldIn.setBlockState(heightPos.up(i), AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.getDefaultState(), 4);
        }
        float size = chunkSeedRandom.nextInt(1) + 1;
        int lvt_8_1_ = (int) (Math.floor(size) * rand.nextFloat()) + 1;
        int lvt_9_1_ = (int) (Math.floor(size) * rand.nextFloat()) + 1;
        int lvt_10_1_ = (int) (Math.floor(size) * rand.nextFloat()) + 1;
        float radius = (float) (lvt_8_1_ + lvt_9_1_ + lvt_10_1_) * 0.333F + 0.5F;
        heightPos = heightPos.down(down + lvt_9_1_).add(chunkSeedRandom.nextInt(2), 0, chunkSeedRandom.nextInt(2));
        Iterator var12 = BlockPos.getAllInBoxMutable(heightPos.add(-lvt_8_1_, -lvt_9_1_, -lvt_10_1_), heightPos.add(lvt_8_1_, lvt_9_1_, lvt_10_1_)).iterator();
        while (var12.hasNext()) {
            BlockPos lvt_13_1_ = (BlockPos) var12.next();
            if (lvt_13_1_.distanceSq(heightPos) < (double) (radius * radius)) {
                BlockState block = AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.getDefaultState();
                worldIn.setBlockState(lvt_13_1_, block, 4);
            }
        }
        return true;
    }
}
