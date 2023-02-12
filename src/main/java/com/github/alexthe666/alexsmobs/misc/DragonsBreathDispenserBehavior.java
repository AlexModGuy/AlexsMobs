package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockDragonsBreathCannon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DragonsBreathDispenserBehavior extends OptionalDispenseItemBehavior {

    private final DefaultDispenseItemBehavior prevBehavior;
    private final DefaultDispenseItemBehavior dispenseItemBehavior = new DefaultDispenseItemBehavior();

    public DragonsBreathDispenserBehavior(DefaultDispenseItemBehavior prevBehavior) {
        this.prevBehavior = prevBehavior;
    }

    public ItemStack execute(BlockSource blockSource, ItemStack stack) {
        BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
        Level level = blockSource.getLevel();
        BlockState state = level.getBlockState(blockpos);
        if (state.is(AMBlockRegistry.DRAGONS_BREATH_CANNON.get()) && state.getValue(BlockDragonsBreathCannon.PHASE) == 0) {
            if(state.getValue(BlockDragonsBreathCannon.PHASE) == 0){
                level.setBlockAndUpdate(blockpos, state.setValue(BlockDragonsBreathCannon.PHASE, 1));
                level.scheduleTick(blockpos, state.getBlock(), 60);
                level.playSound((Player) null, blockpos, AMSoundRegistry.DRAGONS_BREATH_CANNON_CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.shrink(1);
                this.dispenseItemBehavior.dispense(blockSource, stack.getCraftingRemainingItem().copy());
                setSuccess(true);
            }else{
                setSuccess(false);
            }
            return stack;
        } else {
            setSuccess(false);
            return stack;
        }
    }
}
