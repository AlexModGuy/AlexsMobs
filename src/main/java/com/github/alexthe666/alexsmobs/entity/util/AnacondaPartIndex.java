package com.github.alexthe666.alexsmobs.entity.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public enum AnacondaPartIndex {
    HEAD(0F), NECK(0.5F), BODY(0.5F), TAIL(0.4F);

    private float backOffset;

    AnacondaPartIndex(float partOffset){
        this.backOffset = partOffset;
    }

    public static AnacondaPartIndex fromOrdinal(int i){
        switch (i){
            case 0:
                return HEAD;
            case 1:
                return NECK;
            case 2:
                return BODY;
            case 3:
                return TAIL;
        }

        return BODY;
    }

    public static AnacondaPartIndex sizeAt(int bodyindex){
        switch (bodyindex){
            case 0:
                return HEAD;
            case 1:
                return NECK;
            case 2:
            case 3:
            case 4:
                return BODY;
            case 5:
            case 6:
                return NECK;
            case 7:
                return TAIL;
        }
        return BODY;
    }

    public float getBackOffset() {
        return backOffset;
    }
}
