package com.github.alexthe666.alexsmobs.entity.util;

import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.Random;

public enum TerrapinTypes {

    GREEN(new ResourceLocation("alexsmobs:textures/entity/terrapin/terrapin_green.png"), 8.0F),
    BLACK(new ResourceLocation("alexsmobs:textures/entity/terrapin/terrapin_black.png"), 11.0F),
    BROWN(new ResourceLocation("alexsmobs:textures/entity/terrapin/terrapin_brown.png"), 10.0F),
    KOOPA(new ResourceLocation("alexsmobs:textures/entity/terrapin/terrapin_koopa.png"), 0.05F),
    PAINTED(new ResourceLocation("alexsmobs:textures/entity/terrapin/terrapin_painted.png"), 8.0F),
    RED_EARED(new ResourceLocation("alexsmobs:textures/entity/terrapin/terrapin_red_eared.png"), 13.0F),
    OVERLAY(new ResourceLocation("alexsmobs:textures/entity/terrapin/overlay/terrapin_with_overlays.png"), 9.0F);

    private final ResourceLocation texture;
    private final float weight;
    private static final int[] DEFAULT_COLORS = new int[]{0xab4935, 0xc2a629, 0x363533, 0xebe9e1, 0x60a372, 0xc29b72};

    TerrapinTypes(ResourceLocation texture, float weight) {
        this.texture = texture;
        this.weight = weight;
    }

    public static TerrapinTypes getRandomType(Random random) {
        float totalWeight = 0;
        for (TerrapinTypes type : TerrapinTypes.values()) {
            totalWeight += type.weight;
        }
        int randomIndex = -1;
        double randomWeightSample = random.nextDouble() * totalWeight;
        for (int i = 0; i < TerrapinTypes.values().length; ++i) {
            randomWeightSample -= TerrapinTypes.values()[i].weight;
            if (randomWeightSample <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        return TerrapinTypes.values()[randomIndex];
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public static int generateRandomColor(Random random){
        return DEFAULT_COLORS[random.nextInt(DEFAULT_COLORS.length) % DEFAULT_COLORS.length];
    }

    public String getTranslationName(){
        return "entity.alexsmobs.terrapin.variant_" + this.name().toLowerCase(Locale.ROOT);
    }

}
