package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.vector.Vector3d;

public class EffectOiled extends Effect {

    public EffectOiled() {
        super(EffectType.BENEFICIAL, 0XFFE89C);
        this.setRegistryName(AlexsMobs.MODID, "oiled");
    }

    public void performEffect(LivingEntity entity, int amplifier) {
       if(entity.isInWaterRainOrBubbleColumn()){
           if(!entity.isSneaking()){
               entity.setMotion(entity.getMotion().add(0, 0.1D, 0));
           }else{
               entity.fallDistance = 0;
           }
           if (!entity.isOnGround()) {
               Vector3d vector3d = entity.getMotion();
               entity.setMotion(vector3d.mul(1.0D, 0.9D, 1.0D));

           }
       }
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.oiled";
    }

}