package com.github.alexthe666.alexsmobs.client.render;

/**
 * Utility class for color conversion in 1.21+
 * The renderToBuffer method now takes a single packed ARGB int instead of 4
 * separate floats
 */
public class AMColorUtil {
    /**
     * Packs RGBA floats (0.0-1.0) into a single ARGB int
     * 
     * @param r Red component (0.0-1.0)
     * @param g Green component (0.0-1.0)
     * @param b Blue component (0.0-1.0)
     * @param a Alpha component (0.0-1.0)
     * @return Packed ARGB color int
     */
    public static int packColor(float r, float g, float b, float a) {
        int aInt = (int) (a * 255.0F) & 0xFF;
        int rInt = (int) (r * 255.0F) & 0xFF;
        int gInt = (int) (g * 255.0F) & 0xFF;
        int bInt = (int) (b * 255.0F) & 0xFF;
        return (aInt << 24) | (rInt << 16) | (gInt << 8) | bInt;
    }

    /**
     * Convenience constant for white opaque color (1.0F, 1.0F, 1.0F, 1.0F)
     */
    public static final int WHITE = 0xFFFFFFFF;

    /**
     * Convenience method to pack color with full opacity
     */
    public static int packColor(float r, float g, float b) {
        return packColor(r, g, b, 1.0F);
    }
}
