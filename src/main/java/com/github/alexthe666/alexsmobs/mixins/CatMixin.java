package com.github.alexthe666.alexsmobs.mixins;


import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.alexsmobs.entity.EntitySnowLeopard;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Cat.class)
public abstract class CatMixin extends TamableAnimal {


    protected CatMixin(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"), remap = false)
    protected void registerGoals(CallbackInfo ci) {
        if (AMConfig.catsAndFoxesAttackJerboas) {
            this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, EntityJerboa.class, 45, true, true, null));
        }
    }

}
