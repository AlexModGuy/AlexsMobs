package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class OctopusColorRegistry {

    public static final BlockState FALLBACK_BLOCK = Blocks.SAND.getDefaultState();
    public static Map<String, Integer> TEXTURES_TO_COLOR = new HashMap<>();

    public static int getBlockColor(BlockState stack) {
        String blockName = stack.toString();
        if (TEXTURES_TO_COLOR.get(blockName) != null) {
            return TEXTURES_TO_COLOR.get(blockName).intValue();
        } else { BufferedImage texture = null;
            int color = 0XFFFFFF;
            try {
                Color texColour = getAverageColour(getTextureAtlas(stack));
                color = texColour.getRGB();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            TEXTURES_TO_COLOR.put(blockName, color);
            return color;
        }
    }

    private static Color getAverageColour(TextureAtlasSprite image) {
        float red = 0;
        float green = 0;
        float blue = 0;
        float count = 0;
        int uMax = image.getWidth();
        int vMax = image.getHeight();
        for (float i = 0; i < uMax; i++)
            for (float j = 0; j < vMax; j++) {
                int alpha = image.getPixelRGBA(0, (int) i, (int) j) >> 24 & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                red += image.getPixelRGBA(0, (int) i, (int) j) >> 0 & 0xFF;
                green += image.getPixelRGBA(0, (int) i, (int) j) >> 8 & 0xFF;
                blue += image.getPixelRGBA(0, (int) i, (int) j) >> 16 & 0xFF;
                count++;
            }
        //Average color
        return new Color((int) (red / count), (int) (green / count), (int) (blue / count));
    }

    private static TextureAtlasSprite getTextureAtlas(BlockState state) {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(state).getParticleTexture();
    }
}
