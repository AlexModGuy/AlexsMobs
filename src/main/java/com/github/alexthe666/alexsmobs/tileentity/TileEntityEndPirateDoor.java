package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockEndPirateDoor;
import com.github.alexthe666.alexsmobs.block.BlockVoidWormBeak;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityEndPirateDoor extends BlockEntity {

    private float openProgress;
    private float prevOpenProgress;
    private float wiggleProgress;
    private float prevWiggleProgress;
    public int wiggleTime;
    public int ticksExisted;

    public TileEntityEndPirateDoor(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.END_PIRATE_DOOR.get(), pos, state);
        if(state.getBlock() instanceof BlockEndPirateDoor && state.getValue(BlockEndPirateDoor.OPEN)){
            openProgress = 1F;
            prevOpenProgress = 1F;
        }
    }


    @OnlyIn(Dist.CLIENT)
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        return new net.minecraft.world.phys.AABB(worldPosition, worldPosition.offset(1, 3, 1));
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityEndPirateDoor entity) {
        entity.tick();
    }

    public void tick() {
        prevOpenProgress = openProgress;
        prevWiggleProgress = wiggleProgress;
        boolean opened = false;
        if(getBlockState().getBlock() instanceof BlockEndPirateDoor){
            opened = getBlockState().getValue(BlockEndPirateDoor.OPEN);
        }
        if(opened && openProgress == 0F || !opened && openProgress == 1F){
            this.level.playSound((Player)null, this.getBlockPos(), AMSoundRegistry.END_PIRATE_DOOR.get(), SoundSource.BLOCKS, 1F, 1F);
        }
        if(opened && openProgress < 1F){
            openProgress += 0.25F;
        }
        if(!opened && openProgress > 0F){
            openProgress -= 0.25F;
        }
        if(openProgress >= 1F && prevOpenProgress < 1F || openProgress <= 0F && prevOpenProgress > 0F){
            wiggleTime = 5;
        }
        if(wiggleTime > 0){
            wiggleTime--;
            if(wiggleProgress < 1F){
                wiggleProgress += 0.25F;
            }
        }else{
            if(wiggleProgress > 0F){
                wiggleProgress -= 0.25F;
            }
        }
        ticksExisted++;
    }

    public float getOpenProgress(float partialTick){
        return prevOpenProgress + (openProgress - prevOpenProgress) * partialTick;
    }

    public float getWiggleProgress(float partialTick){
        return prevWiggleProgress + (wiggleProgress - prevWiggleProgress) * partialTick;
    }
}
