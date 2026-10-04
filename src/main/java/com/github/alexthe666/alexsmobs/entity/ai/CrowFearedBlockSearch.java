package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

/** Answers an empty fear-block search from fully loaded section palettes. */
public final class CrowFearedBlockSearch {
    private static final int RINGS = 20, LAYER_FROM = -8, LAYER_TO = 2;
    private static final int FULL_CHUNK_LEVEL = 33;

    private CrowFearedBlockSearch() {}

    /**
     * True when AIAvoidPumpkins.searchForDestination of this crow would read no alexsmobs:crow_fears block and return
     * false. In that case the scan's chunk lookups have just been replayed.
     */
    public static boolean noneFeared(Entity crow) {
        Level level = crow.level();
        if (!(level instanceof ServerLevel server) || level.isDebug() || !server.getServer().isSameThread()) return false;
        BlockPos origin = crow.blockPosition();
        int x = origin.getX(), y = origin.getY(), z = origin.getZ();
        // layers: the same int arithmetic as the scan (setWithOffset adds dy - 1)
        int lowSection = Integer.MAX_VALUE, highSection = Integer.MIN_VALUE;
        for (int layer = LAYER_FROM; layer <= LAYER_TO; layer++) {
            int yy = y + (layer - 1);
            if (level.isOutsideBuildHeight(yy)) return false;                     // outside the build height: let the original read it
            int s = level.getSectionIndex(yy);
            lowSection = Math.min(lowSection, s);
            highSection = Math.max(highSection, s);
        }
        // the scan's chunk lookups for one layer (every layer repeats them), consecutive repeats dropped
        LongArrayList sequence = new LongArrayList(64);
        LongOpenHashSet columns = new LongOpenHashSet(32);
        long last = Long.MIN_VALUE;
        for (int r = 0; r < RINGS; r++) {
            int dx = 0;
            while (dx <= r) {
                int dz = dx < r && dx > -r ? r : 0;
                while (dz <= r) {
                    long key = ChunkPos.asLong((x + dx) >> 4, (z + dz) >> 4);
                    if (key != last) {
                        sequence.add(key);
                        columns.add(key);
                        last = key;
                    }
                    dz = dz > 0 ? -dz : 1 - dz;
                }
                dx = dx > 0 ? -dx : 1 - dx;
            }
        }
        // every column a full chunk at ticket level <= 33 (so the replay loads nothing), every section free of the tag
        TagKey<Block> fears = AMTagRegistry.CROW_FEARS;
        if (Blocks.AIR.defaultBlockState().is(fears)) return false;
        var it = columns.iterator();
        while (it.hasNext()) {
            ChunkHolder holder = server.getChunkSource().getVisibleChunkIfPresent(it.nextLong());
            if (holder == null || holder.getTicketLevel() > FULL_CHUNK_LEVEL) return false;
            LevelChunk chunk = holder.getFullChunk();
            if (chunk == null) return false;
            LevelChunkSection[] sections = chunk.getSections();
            for (int s = lowSection; s <= highSection; s++) {
                if (s < 0 || s >= sections.length) return false;
                LevelChunkSection section = sections[s];
                if (section == null) return false;
                if (section.hasOnlyAir()) continue;                        // only air: every read returns AIR
                if (section.getStates().maybeHas(state -> state.is(fears))) return false;
            }
        }
        // nothing to find: make the scan's own chunk lookups, in its order, then answer for it
        long previous = Long.MIN_VALUE;
        for (int layer = LAYER_FROM; layer <= LAYER_TO; layer++) {
            for (int i = 0; i < sequence.size(); i++) {
                long key = sequence.getLong(i);
                if (key == previous) continue;
                level.getChunk(ChunkPos.getX(key), ChunkPos.getZ(key));
                previous = key;
            }
        }
        return true;
    }

}
