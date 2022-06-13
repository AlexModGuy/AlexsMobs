package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.phys.Vec3;

public class EffectOiled extends MobEffect {

    public EffectOiled() {
        super(MobEffectCategory.BENEFICIAL, 0XFFE89C);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
       if(entity.isInWaterRainOrBubble()){
           if(!entity.isShiftKeyDown()){
               entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1D, 0));
           }else{
               entity.fallDistance = 0;
           }
           if (!entity.isOnGround()) {
               Vec3 vector3d = entity.getDeltaMovement();
               entity.setDeltaMovement(vector3d.multiply(1.0D, 0.9D, 1.0D));

           }
       }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.oiled";
    }

}