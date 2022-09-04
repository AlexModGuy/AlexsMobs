package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;

public class GUITransmutationTable extends AbstractContainerScreen<MenuTransmutationTable> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/gui/transmutation_table.png");
    private int tickCount = 0;
    private ButtonTransmute transmuteBtn1;
    private ButtonTransmute transmuteBtn2;
    private ButtonTransmute transmuteBtn3;

    public GUITransmutationTable(MenuTransmutationTable menu, Inventory inventory, Component name) {
        super(menu, inventory, name);
        this.imageHeight = 201;
    }

    @Override
    protected void init() {
        super.init();
        int i = this.leftPos;
        int j = this.topPos;
        this.addRenderableWidget(transmuteBtn1 = new ButtonTransmute(this, i + 30, j + 16,  (button) -> {
            this.menu.clickMenuButton(minecraft.player, 0);
        }));
        this.addRenderableWidget(transmuteBtn2 = new ButtonTransmute(this, i + 30, j + 35,  (button) -> {
            this.menu.clickMenuButton(minecraft.player, 1);
        }));
        this.addRenderableWidget(transmuteBtn3 = new ButtonTransmute(this, i + 30, j + 54,  (button) -> {
            this.menu.clickMenuButton(minecraft.player, 2);
        }));
        transmuteBtn1.visible = false;
        transmuteBtn2.visible = false;
        transmuteBtn3.visible = false;
    }

    public void render(PoseStack stack, int x, int y, float partialTick) {
        this.renderBackground(stack);
        this.renderBg(stack, partialTick, x, y);
        super.render(stack, x, y, partialTick);
        this.renderItemsTransmute(stack, x, y);
        this.renderTooltip(stack, x, y);
    }

    protected void renderBg(PoseStack poseStack, float f, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void containerTick() {
        tickCount++;
        boolean thingIn = !this.menu.getSlot(0).getItem().isEmpty();
        transmuteBtn1.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(0).isEmpty() && thingIn;
        transmuteBtn2.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(1).isEmpty() && thingIn;
        transmuteBtn3.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(2).isEmpty() && thingIn;
    }

    protected void renderLabels(PoseStack poseStack, int x, int y) {
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 0X4EFF21);
    }

    protected void renderItemsTransmute(PoseStack poseStack, int x, int y){
        int i = this.leftPos;
        int j = this.topPos;
        if(!this.menu.getSlot(0).getItem().isEmpty()){
            this.itemRenderer.renderAndDecorateItem(AlexsMobs.PROXY.getDisplayTransmuteResult(0), i + 31, j + 17);
            this.itemRenderer.renderAndDecorateItem(AlexsMobs.PROXY.getDisplayTransmuteResult(1), i + 31, j + 36);
            this.itemRenderer.renderAndDecorateItem(AlexsMobs.PROXY.getDisplayTransmuteResult(2), i + 31, j + 55);
        }
    }

}
