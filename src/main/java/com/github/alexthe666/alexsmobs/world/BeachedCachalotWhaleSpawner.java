package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Random;

public class BeachedCachalotWhaleSpawner {
    private final Random random = new Random();
    private final ServerLevel world;
    private int timer;
    private int delay;
    private int chance;

    public BeachedCachalotWhaleSpawner(ServerLevel p_i50177_1_) {
        this.world = p_i50177_1_;
        this.timer = 1200;
        AMWorldData worldinfo = AMWorldData.get(p_i50177_1_);
        this.delay = worldinfo.getBeachedCachalotSpawnDelay();
        this.chance = worldinfo.getBeachedCachalotSpawnChance();
        if (this.delay == 0 && this.chance == 0) {
            this.delay = AMConfig.beachedCachalotWhaleSpawnDelay;
            worldinfo.setBeachedCachalotSpawnDelay(this.delay);
            this.chance = 25;
            worldinfo.setBeachedCachalotSpawnChance(this.chance);
        }

    }

    public void tick() {
        if (AMConfig.beachedCachalotWhales && --this.timer <= 0 && world.isThundering()) {
            this.timer = 1200;
            AMWorldData worldinfo = AMWorldData.get(world);
            this.delay -= 1200;
            if(delay < 0){
                delay = 0;
            }
            worldinfo.setBeachedCachalotSpawnDelay(this.delay);
            if (this.delay <= 0) {
                this.delay = AMConfig.beachedCachalotWhaleSpawnDelay;
                if (this.world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                    int i = this.chance;
                    this.chance = Mth.clamp(this.chance + AMConfig.beachedCachalotWhaleSpawnChance, 5, 100);
                    worldinfo.setBeachedCachalotSpawnChance(this.chance);
                    if (this.random.nextInt(100) <= i && this.attemptSpawnWhale()) {
                        this.chance = AMConfig.beachedCachalotWhaleSpawnChance;
                    }
                }
            }
        }

    }

    private boolean attemptSpawnWhale() {
        Player playerentity = this.world.getRandomPlayer();
        if (playerentity == null) {
            return true;
        } else if (this.random.nextInt(5) != 0) {
            return false;
        } else {
            BlockPos blockpos = new BlockPos(playerentity.position());
            BlockPos blockpos2 = this.func_221244_a(blockpos, 84);
            if (blockpos2 != null && this.func_226559_a_(blockpos2) && blockpos2.distSqr(blockpos) > 225) {
                BlockPos upPos = new BlockPos(blockpos2.getX(), blockpos2.getY() + 2, blockpos2.getZ());
                EntityCachalotWhale whale = AMEntityRegistry.CACHALOT_WHALE.get().create(world);
                whale.moveTo(upPos.getX() + 0.5D, upPos.getY() + 0.5D, upPos.getZ() + 0.5D, random.nextFloat() * 360 - 180F, 0);
                whale.finalizeSpawn(world, world.getCurrentDifficultyAt(upPos), MobSpawnType.SPAWNER, null, null);
                whale.setBeached(true);
                AMWorldData worldinfo = AMWorldData.get(world);
                worldinfo.setBeachedCachalotID(whale.getUUID());
                whale.restrictTo(upPos, 16);
                whale.setDespawnBeach(true);
                world.addFreshEntity(whale);
                return true;
            }
            return false;
        }
    }

    @Nullable
    private BlockPos func_221244_a(BlockPos p_221244_1_, int p_221244_2_) {
        BlockPos blockpos = null;

        for(int i = 0; i < 10; ++i) {
            int j = p_221244_1_.getX() + this.random.nextInt(p_221244_2_ * 2) - p_221244_2_;
            int k = p_221244_1_.getZ() + this.random.nextInt(p_221244_2_ * 2) - p_221244_2_;
            int l = this.world.getHeight(Types.WORLD_SURFACE, j, k);
            BlockPos blockpos1 = new BlockPos(j, l, k);
            if (AMWorldRegistry.testBiome(BiomeConfig.cachalot_whale_beached_spawns, world.getBiome(blockpos1)) && NaturalSpawner.isSpawnPositionOk(Type.ON_GROUND, this.world, blockpos1, EntityType.WANDERING_TRADER)) {
                blockpos = blockpos1;
                break;
            }
        }

        return blockpos;
    }

    private boolean func_226559_a_(BlockPos p_226559_1_) {
        Iterator var2 = BlockPos.betweenClosed(p_226559_1_, p_226559_1_.offset(1, 2, 1)).iterator();

        BlockPos blockpos;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            blockpos = (BlockPos)var2.next();
        } while(this.world.getBlockState(blockpos).getBlockSupportShape(this.world, blockpos).isEmpty() && world.getFluidState(blockpos).isEmpty());

        return false;
    }
}
