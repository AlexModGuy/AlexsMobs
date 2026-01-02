package com.github.alexthe666.alexsmobs.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
    @Invoker("setPierceLevel")
    void invokeSetPierceLevel(byte pierceLevel);

    @Invoker("getPierceLevel")
    byte invokeGetPierceLevel();
}
