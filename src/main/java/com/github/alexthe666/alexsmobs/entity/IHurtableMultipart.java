package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.world.entity.LivingEntity;

public interface IHurtableMultipart {

    void onAttackedFromServer(LivingEntity parent, float damage);
}
