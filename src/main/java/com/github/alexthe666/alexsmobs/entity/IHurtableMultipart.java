package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IHurtableMultipart {

    void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource);

    default Vec3 calcOffsetVec(float offsetZ, float xRot, float yRot){
        return new Vec3(0, 0, offsetZ).xRot(xRot * Mth.DEG_TO_RAD).yRot(-yRot * Mth.DEG_TO_RAD);
    }

    default float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
        float f = Mth.wrapDegrees(targetAngle - sourceAngle);
        if (f > maximumChange) {
            f = maximumChange;
        }

        if (f < -maximumChange) {
            f = -maximumChange;
        }

        float f1 = sourceAngle + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }
}
