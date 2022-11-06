package com.github.alexthe666.alexsmobs.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityEndPirateFlag  extends BlockEntity {

    public int ticksExisted;

    public TileEntityEndPirateFlag(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.END_PIRATE_FLAG.get(), pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateFlag entity) {
        entity.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-2, -2, -2), worldPosition.offset(2, 2, 2));
    }

    public void tick() {
        ticksExisted++;
    }
}

