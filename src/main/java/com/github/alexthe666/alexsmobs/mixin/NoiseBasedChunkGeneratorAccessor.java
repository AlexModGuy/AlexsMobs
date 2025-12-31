package com.github.alexthe666.alexsmobs.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.LevelHeightAccessor;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.OptionalInt;
import java.util.function.Predicate;

@Mixin(NoiseBasedChunkGenerator.class)
public interface NoiseBasedChunkGeneratorAccessor {
    @Invoker("iterateNoiseColumn")
    OptionalInt invokeIterateNoiseColumn(LevelHeightAccessor level, RandomState random, int x, int z, MutableObject column, Predicate<BlockState> stoppingState);
}
