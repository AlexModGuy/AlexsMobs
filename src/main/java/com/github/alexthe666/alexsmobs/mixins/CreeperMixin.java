package com.github.alexthe666.alexsmobs.mixins;


import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster {


    protected CreeperMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"), remap = false)
    protected void registerGoals(CallbackInfo ci) {
        this.targetSelector.addGoal(3, new AvoidEntityGoal<>(this, EntitySnowLeopard.class, 6.0F, 1.0D, 1.2D));
        this.targetSelector.addGoal(3, new AvoidEntityGoal<>(this, EntityTiger.class, 6.0F, 1.0D, 1.2D));
    }

}
