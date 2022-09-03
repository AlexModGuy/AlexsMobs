package com.github.alexthe666.alexsmobs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;

public class ButtonTransmute extends Button {
    private final Screen parent;

    public ButtonTransmute(Screen parent, int x, int y, OnPress onPress) {
        super(x, y, 109, 19, CommonComponents.EMPTY, onPress);
        this.parent = parent;
    }

    public void renderToolTip(PoseStack poseStack, int x, int y) {
    }

    public void renderButton(PoseStack poseStack, int x, int y, float partialTick) {
        if (this.active && this.isHoveredOrFocused()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUITransmutationTable.TEXTURE);
            this.blit(poseStack, this.x, this.y, 0, 201, 109, 19);
        }
    }
}