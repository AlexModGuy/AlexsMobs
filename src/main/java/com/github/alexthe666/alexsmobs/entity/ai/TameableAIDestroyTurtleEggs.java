package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class TameableAIDestroyTurtleEggs extends RemoveBlockGoal {

    public TameableAIDestroyTurtleEggs(TamableAnimal creatureIn, double speed, int yMax) {
        super(Blocks.TURTLE_EGG, creatureIn, speed, yMax);
        nextStartTick = 800;
    }

    public boolean canUse() {
        return !((TamableAnimal)mob).isTame() && super.canUse();
    }

    public boolean canContinueToUse() {
        return !((TamableAnimal)mob).isTame() && super.canContinueToUse();
    }

        public void playDestroyProgressSound(LevelAccessor worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + this.mob.getRandom().nextFloat() * 0.2F);
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(800 + mob.getRandom().nextInt(800));
    }

    public void playBreakSound(Level worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.random.nextFloat() * 0.2F);
    }

    public double acceptedDistance() {
        return 1.14D;
    }
}
