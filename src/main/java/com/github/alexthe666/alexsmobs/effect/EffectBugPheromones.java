package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.vector.Vector3d;

public class EffectBugPheromones extends Effect {

    public EffectBugPheromones() {
        super(EffectType.BENEFICIAL, 0X78464B);
        this.setRegistryName(AlexsMobs.MODID, "bug_pheromones");
    }

    public void performEffect(LivingEntity entity, int amplifier) {
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.bug_pheromones";
    }

}