package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.mixin.NoiseBasedChunkGeneratorAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AMWorldData extends SavedData {

    private static final String IDENTIFIER = "alexsmobs_world_data";
    private ServerLevel level;
    private int tickCounter;
    private int beachedCachalotSpawnDelay;
    private int beachedCachalotSpawnChance;
    private UUID beachedCachalotID;
    private ChunkPos pupfishChunk;
    private int pupfishChunkTime = 0;
    private int pupfishSeedAddition = 0;
    private long startPupfishSearchTimestamp = -1;
    private boolean noPupfishChunk;
    private static final Map<Level, AMWorldData> dataMap = new HashMap<>();

    public AMWorldData() {
        super();
    }

    public static AMWorldData get(Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
            AMWorldData fromMap = dataMap.get(overworld);
            if(fromMap == null){
                DimensionDataStorage storage = overworld.getDataStorage();
                AMWorldData data = storage.computeIfAbsent(
                    new SavedData.Factory<>(AMWorldData::new, AMWorldData::load),
                    IDENTIFIER
                );
                if (data != null) {
                    data.level =  overworld;
                    data.setDirty();
                }
                dataMap.put(world, data);
                return data;
            }
            return fromMap;
        }
        return null;
    }

    public static AMWorldData load(CompoundTag nbt, HolderLookup.Provider provider) {
        AMWorldData data = new AMWorldData();
        if (nbt.contains("BeachedCachalotSpawnDelay", 99)) {
            data.beachedCachalotSpawnDelay = nbt.getInt("BeachedCachalotSpawnDelay");
        }
        if (nbt.contains("BeachedCachalotSpawnChance", 99)) {
            data.beachedCachalotSpawnChance = nbt.getInt("BeachedCachalotSpawnChance");
        }
        if (nbt.contains("BeachedCachalotId", 8)) {
            data.beachedCachalotID = UUID.fromString(nbt.getString("BeachedCachalotId"));
        }
        if (nbt.contains("PupfishChunkX") && nbt.contains("PupfishChunkZ")) {
            data.pupfishChunk = new ChunkPos(nbt.getInt("PupfishChunkX"), nbt.getInt("PupfishChunkZ"));
        }
        if (nbt.contains("NoPupfishChunk")) {
            data.noPupfishChunk = nbt.getBoolean("NoPupfishChunk");
        }
        return data;
    }

    public int getBeachedCachalotSpawnDelay() {
        return this.beachedCachalotSpawnDelay;
    }

    public void setBeachedCachalotSpawnDelay(int delay) {
        this.beachedCachalotSpawnDelay = delay;
    }

    public int getBeachedCachalotSpawnChance() {
        return this.beachedCachalotSpawnChance;
    }

    public void setBeachedCachalotSpawnChance(int chance) {
        this.beachedCachalotSpawnChance = chance;
    }

    public void setBeachedCachalotID(UUID id) {
        this.beachedCachalotID = id;
    }

    public void debug() {
    }

    public void tick() {
        ++this.tickCounter;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        compound.putInt("beachedCachalotSpawnDelay", this.beachedCachalotSpawnDelay);
        compound.putInt("beachedCachalotSpawnChance", this.beachedCachalotSpawnChance);
        if (this.beachedCachalotID != null) {
            compound.putString("beachedCachalotId", this.beachedCachalotID.toString());
        }
        if (this.pupfishChunk != null) {
            compound.putInt("PupfishChunkX", this.pupfishChunk.x);
            compound.putInt("PupfishChunkZ", this.pupfishChunk.z);
        }
        if(this.noPupfishChunk){
            compound.putBoolean("NoPupfishChunk", noPupfishChunk);
        }
        return compound;
    }

    @Nullable
    public ChunkPos getPupfishChunk() {
        return pupfishChunk;
    }



    public boolean isInPupfishChunk(BlockPos pos) {
        if(pupfishChunk != null){
            return pos.getX() >= pupfishChunk.getMinBlockX() && pos.getX() <= pupfishChunk.getMaxBlockX() && pos.getZ() >= pupfishChunk.getMinBlockZ() && pos.getZ() <= pupfishChunk.getMaxBlockZ();
        }
        return false;
    }

    public void tickPupfish() {
        if(AMConfig.restrictPupfishSpawns && !noPupfishChunk){
            if(pupfishChunk == null && startPupfishSearchTimestamp == -1){
                startPupfishSearchTimestamp = System.currentTimeMillis();
            }
            if (pupfishChunk == null && pupfishChunkTime % 10 == 0) {
                long seconds = (System.currentTimeMillis() - startPupfishSearchTimestamp) / 1000L;
                if(seconds / 60 > 5) {
                    AlexsMobs.LOGGER.info("Giving up search for pupfish chunk after " + (seconds / 60) + " minutes. no pupfish will spawn in this world :( ");
                    noPupfishChunk = true;
                }else{
                    searchForPupfishChunk();
                }
            }
            pupfishChunkTime++;
        }
    }

    private void searchForPupfishChunk() {
        if (level != null && level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator chunkGenerator) {
            Random random = new Random(level.getSeed() + pupfishSeedAddition);
            int randomXCoord = random.nextInt(AMConfig.pupfishChunkSpawnDistance * 2) - AMConfig.pupfishChunkSpawnDistance;
            int randomZCoord = random.nextInt(AMConfig.pupfishChunkSpawnDistance * 2) - AMConfig.pupfishChunkSpawnDistance;
            ChunkPos checkPos = new ChunkPos(randomXCoord >> 4, randomZCoord >> 4);
            BlockPos center = new BlockPos(checkPos.getMiddleBlockX(), chunkGenerator.getSeaLevel(), checkPos.getMiddleBlockZ());
            int maxWater = getWaterHeight(chunkGenerator, level.getChunkSource().randomState(), center.getX(), center.getZ(), level);
            if(maxWater > 31 && maxWater < 63){
                pupfishChunk = checkPos;
                AlexsMobs.LOGGER.info("Found Pupfish chunk at " + pupfishChunk.getMaxBlockX() + " ~ " + pupfishChunk.getMinBlockZ() + " after " + pupfishSeedAddition + " tries");
            }
        }
        pupfishSeedAddition++;
    }

    public int getWaterHeight(NoiseBasedChunkGenerator generator, RandomState rand, int x, int z, LevelHeightAccessor level) {
        if(generator instanceof NoiseBasedChunkGeneratorAccessor accessor){
            return accessor.invokeIterateNoiseColumn(level, rand, x, z, null, (state) -> !state.isAir()).orElse(level.getMinBuildHeight());
        }
        return level.getMinBuildHeight();
    }
}
