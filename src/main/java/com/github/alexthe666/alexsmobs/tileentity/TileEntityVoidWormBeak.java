package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockVoidWormBeak;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileEntityVoidWormBeak extends BlockEntity {

    private float chompProgress;
    private float prevChompProgress;
    public int ticksExisted;


    public TileEntityVoidWormBeak(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.VOID_WORM_BEAK.get(), pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityVoidWormBeak entity) {
        entity.tick();
    }

    public void tick() {
        prevChompProgress = chompProgress;
        boolean powered = false;
        if(getBlockState().getBlock() instanceof BlockVoidWormBeak){
            powered = getBlockState().getValue(BlockVoidWormBeak.POWERED);
        }
        if(powered && chompProgress < 5F){
            chompProgress++;
        }
        if(!powered && chompProgress > 0F){
            chompProgress--;
        }
        if(chompProgress >= 5F && !level.isClientSide && ticksExisted % 5 == 0){
            float i = this.getBlockPos().getX() + 0.5F;
            float j = this.getBlockPos().getY() + 0.5F;
            float k = this.getBlockPos().getZ() + 0.5F;
            float d0 = 0.5F;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0))) {
                entity.hurt(DamageSource.GENERIC, 5);
            }
        }
        ticksExisted++;
    }

    public float getChompProgress(float partialTick){
        return prevChompProgress + (chompProgress - prevChompProgress) * partialTick;
    }
}
