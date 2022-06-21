package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface IFalconry {
    void onLaunch(Player player, Entity pointedEntity);

    default int getRidingFalcons(LivingEntity player) {
        int crowCount = 0;
        for (Entity e : player.getPassengers()) {
            if (e instanceof IFalconry) {
                crowCount++;
            }
        }
        return crowCount;
    }

    float getHandOffset();

}
