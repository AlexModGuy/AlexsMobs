package com.github.alexthe666.alexsmobs.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
    @Accessor("pierceLevel")
    void setPierceLevel(byte pierceLevel);

    @Accessor("pierceLevel")
    byte getPierceLevel();
}
