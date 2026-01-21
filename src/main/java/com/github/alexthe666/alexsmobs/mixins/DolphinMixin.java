package com.github.alexthe666.alexsmobs.mixins;


import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityFlyingFish;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Dolphin.class)
public abstract class DolphinMixin extends WaterAnimal {


    protected DolphinMixin(EntityType<? extends WaterAnimal> p_30341_, Level p_30342_) {
        super(p_30341_, p_30342_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"), remap = false)
    protected void registerGoals(CallbackInfo ci) {
        if (AMConfig.dolphinsAttackFlyingFish) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityFlyingFish.class, 70, true, true, null));
        }
    }

}
