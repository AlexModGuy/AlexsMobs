package com.github.alexthe666.alexsmobs.tileentity;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityVoidWormBeak extends TileEntity implements ITickableTileEntity {

    private float chompProgress;
    private float prevChompProgress;

    public TileEntityVoidWormBeak() {
        super(AMTileEntityRegistry.VOID_WORM_BEAK);
    }

    @Override
    public void tick() {

    }

    public float getChompProgress(float partialTick){
        return prevChompProgress + (chompProgress - prevChompProgress) * partialTick;
    }
}
