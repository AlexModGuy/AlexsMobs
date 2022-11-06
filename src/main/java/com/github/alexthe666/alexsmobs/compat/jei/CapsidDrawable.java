package com.github.alexthe666.alexsmobs.compat.jei;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
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
    public void draw(PoseStack poseStack, int xOffset, int yOffset) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = xOffset;
        int j = yOffset;
        GuiComponent.blit(poseStack, i, j, 0, 0, 125, 59, 256, 256);
    }
}
