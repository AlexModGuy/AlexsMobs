package com.github.alexthe666.alexsmobs.compat.jei;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class CapsidDrawable implements IDrawable {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs", "textures/gui/capsid_jei_representation.png");
    @Override
    public int getWidth() {
        return 125;
    }

    @Override
    public int getHeight() {
        return 59;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        int i = xOffset;
        int j = yOffset;
        guiGraphics.blit(TEXTURE, i, j, 0, 0, 125, 59, 256, 256);
    }
}
