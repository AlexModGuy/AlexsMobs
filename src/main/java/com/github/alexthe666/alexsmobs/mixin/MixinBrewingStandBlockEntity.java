package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class MixinBrewingStandBlockEntity {

    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    public void am_canPlaceItem(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (index >= 0 && index <= 2) { // Bottle slots
            if (stack.is(AMItemRegistry.LAVA_BOTTLE.get()) || 
                stack.is(AMItemRegistry.KOMODO_SPIT_BOTTLE.get()) || 
                stack.is(AMItemRegistry.POISON_BOTTLE.get())) {
                cir.setReturnValue(true);
            }
        }
    }
}
