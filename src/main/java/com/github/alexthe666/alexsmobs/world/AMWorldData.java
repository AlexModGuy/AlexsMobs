package com.github.alexthe666.alexsmobs.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.UUID;

public class AMWorldData extends WorldSavedData {

    private static final String IDENTIFIER = "alexsmobs_world_data";
    private World world;
    private int tickCounter;
    private int beachedCachalotSpawnDelay;
    private int beachedCachalotSpawnChance;
    private UUID beachedCachalotID;

    public AMWorldData() {
        super(IDENTIFIER);
    }

    public static AMWorldData get(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld overworld = world.getServer().getWorld(World.OVERWORLD);
            DimensionSavedDataManager storage = overworld.getSavedData();
            AMWorldData data = storage.getOrCreate(AMWorldData::new, IDENTIFIER);
            if(data != null){
                data.world = world;
                data.markDirty();
            }
            return data;
        }
        return null;
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
    public void read(CompoundNBT nbt) {
        if (nbt.contains("BeachedCachalotSpawnDelay", 99)) {
            this.beachedCachalotSpawnDelay = nbt.getInt("BeachedCachalotSpawnDelay");
        }
        if (nbt.contains("BeachedCachalotSpawnChance", 99)) {
            this.beachedCachalotSpawnChance = nbt.getInt("BeachedCachalotSpawnChance");
        }
        if (nbt.contains("BeachedCachalotId", 8)) {
            this.beachedCachalotID = UUID.fromString(nbt.getString("BeachedCachalotId"));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("beachedCachalotSpawnDelay", this.beachedCachalotSpawnDelay);
        compound.putInt("beachedCachalotSpawnChance", this.beachedCachalotSpawnChance);
        if (this.beachedCachalotID != null) {
            compound.putString("beachedCachalotId", this.beachedCachalotID.toString());
        }
        return compound;
    }
}
