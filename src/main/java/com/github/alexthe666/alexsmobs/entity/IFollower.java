package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public interface IFollower {
    boolean shouldFollow();

   default void followEntity(TameableEntity tameable, LivingEntity owner, double followSpeed){
       tameable.navigator.tryMoveToEntityLiving(owner, followSpeed);
   }
}
