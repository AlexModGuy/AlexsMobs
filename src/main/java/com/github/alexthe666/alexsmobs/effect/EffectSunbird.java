package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EffectSunbird extends MobEffect {
    public boolean curse;

    public EffectSunbird(boolean curse) {
        super(curse ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL, 0XFFEAB9);
        this.curse = curse;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (curse) {
            if (entity.isFallFlying()) {
                if (entity instanceof Player) {
                    ((Player) entity).stopFallFlying();
                }
            }
            boolean forceFall = false;
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (!player.isCreative() || !player.getAbilities().flying) {
                    forceFall = true;
                }
            }
            if ((forceFall || !(entity instanceof Player)) && !entity.onGround()) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.2F, 0));
            }
        } else {
            entity.fallDistance = 0.0F;
            if (entity.isFallFlying()) {
                if (entity.getXRot() < -10) {
                    float pitchMulti = Math.abs(entity.getXRot()) / 90F;
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.02 + pitchMulti * 0.02, 0));
                }
            } else if (!entity.onGround() && !entity.isCrouching()) {
                Vec3 vector3d = entity.getDeltaMovement();
                if (vector3d.y < 0.0D) {
                    entity.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
                }
            }

        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return curse ? "alexsmobs.potion.sunbird_curse" : "alexsmobs.potion.sunbird_blessing";
    }

}