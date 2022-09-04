package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class ButtonTransmute extends Button {
    private final Screen parent;

    public ButtonTransmute(Screen parent, int x, int y, OnPress onPress) {
        super(x, y, 117, 19, CommonComponents.EMPTY, onPress);
        this.parent = parent;
    }

    public void renderToolTip(PoseStack poseStack, int x, int y) {
    }

    public void renderButton(PoseStack poseStack, int x, int y, float partialTick) {
        int color = 8453920;
        int cost = AMConfig.transmutingExperienceCost;
        if(!canBeTransmuted(cost)){
            color = 16736352;
        }else if (this.active && this.isHoveredOrFocused()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUITransmutationTable.TEXTURE);
            this.blit(poseStack, this.x, this.y, 0, 201, 117, 19);
            color = 0XC7FFD0;
        }
        poseStack.pushPose();
        drawString(poseStack, Minecraft.getInstance().font, Component.translatable("alexsmobs.container.transmutation_table.cost").append(" " + cost), this.x + 21, this.y + (this.height - 8) / 2, color);
        poseStack.popPose();
    }

    public boolean canBeTransmuted(int cost){
        return Minecraft.getInstance().player.experienceLevel >= cost || Minecraft.getInstance().player.getAbilities().instabuild;
    }

    public void playDownSound(SoundManager sounds) {
        if(canBeTransmuted(AMConfig.transmutingExperienceCost)){
            super.playDownSound(sounds);
        }
    }

    public void onPress() {
        if(canBeTransmuted(AMConfig.transmutingExperienceCost)){
            super.onPress();
        }
    }
}