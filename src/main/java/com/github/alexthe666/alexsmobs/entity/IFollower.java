package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public interface IFollower {
    boolean shouldFollow();

   default void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed){
       tameable.navigation.moveTo(owner, followSpeed);
   }
}
