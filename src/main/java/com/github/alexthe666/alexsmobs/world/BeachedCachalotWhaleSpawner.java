package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestManager.Status;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

public class BeachedCachalotWhaleSpawner {
    private final Random random = new Random();
    private final ServerWorld world;
    private int timer;
    private int delay;
    private int chance;

    public BeachedCachalotWhaleSpawner(ServerWorld p_i50177_1_) {
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
        if (AMConfig.beachedCachalotWhales && --this.timer <= 0) {
            this.timer = 1200;
            AMWorldData worldinfo = AMWorldData.get(world);
            this.delay -= 1200;
            if(delay < 0){
                delay = 0;
            }
            worldinfo.setBeachedCachalotSpawnDelay(this.delay);
            if (this.delay <= 0 && world.isThundering()) {
                this.delay = AMConfig.beachedCachalotWhaleSpawnDelay;
                if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                    int i = this.chance;
                    this.chance = MathHelper.clamp(this.chance + AMConfig.beachedCachalotWhaleSpawnChance, 5, 100);
                    worldinfo.setBeachedCachalotSpawnChance(this.chance);
                    if (this.random.nextInt(100) <= i && this.func_221245_b()) {
                        this.chance = AMConfig.beachedCachalotWhaleSpawnChance;
                    }
                }
            }
        }

    }

    private boolean func_221245_b() {
        PlayerEntity playerentity = this.world.getRandomPlayer();
        if (playerentity == null) {
            return true;
        } else if (this.random.nextInt(5) != 0) {
            return false;
        } else {
            BlockPos blockpos = new BlockPos(playerentity.getPositionVec());
            BlockPos blockpos2 = this.func_221244_a(blockpos, 84);
            if (blockpos2 != null && this.func_226559_a_(blockpos2) && blockpos2.distanceSq(blockpos) > 225) {
                BlockPos upPos = new BlockPos(blockpos2.getX(), blockpos2.getY() + 2, blockpos2.getZ());
                EntityCachalotWhale whale = AMEntityRegistry.CACHALOT_WHALE.create(world);
                whale.setLocationAndAngles(upPos.getX() + 0.5D, upPos.getY() + 0.5D, upPos.getZ() + 0.5D, random.nextFloat() * 360 - 180F, 0);
                whale.onInitialSpawn(world, world.getDifficultyForLocation(upPos), SpawnReason.SPAWNER, null, null);
                whale.setBeached(true);
                AMWorldData worldinfo = AMWorldData.get(world);
                worldinfo.setBeachedCachalotID(whale.getUniqueID());
                whale.setHomePosAndDistance(upPos, 16);
                whale.setDespawnBeach(true);
                world.addEntity(whale);
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
            int l = this.world.getHeight(Type.WORLD_SURFACE, j, k);
            BlockPos blockpos1 = new BlockPos(j, l, k);
            Biome biome = world.getBiome(blockpos1);
            if (BiomeConfig.test(BiomeConfig.cachalot_whale_beached_spawns, biome) && WorldEntitySpawner.canCreatureTypeSpawnAtLocation(PlacementType.ON_GROUND, this.world, blockpos1, EntityType.WANDERING_TRADER)) {
                blockpos = blockpos1;
                break;
            }
        }

        return blockpos;
    }

    private boolean func_226559_a_(BlockPos p_226559_1_) {
        Iterator var2 = BlockPos.getAllInBoxMutable(p_226559_1_, p_226559_1_.add(1, 2, 1)).iterator();

        BlockPos blockpos;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            blockpos = (BlockPos)var2.next();
        } while(this.world.getBlockState(blockpos).getCollisionShape(this.world, blockpos).isEmpty());

        return false;
    }
}
