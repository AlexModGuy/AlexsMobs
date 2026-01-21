package com.github.alexthe666.alexsmobs.mixins;


import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal {


    protected WolfMixin(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"), remap = false)
    protected void registerGoals(CallbackInfo ci) {
        if (AMConfig.wolvesAttackMoose) {
            this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, EntityMoose.class, false, null));
        }
    }

}
