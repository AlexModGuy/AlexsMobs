package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.client.render.RenderLaviathan;
import com.github.alexthe666.alexsmobs.client.render.RenderMurmurBody;
import com.github.alexthe666.alexsmobs.client.render.RenderUnderminer;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class GUIAnimalDictionary extends GuiBasicBook {

    private static final ResourceLocation ROOT = new ResourceLocation("alexsmobs:book/animal_dictionary/root.json");

    public GUIAnimalDictionary(ItemStack bookStack) {
        super(bookStack, Component.translatable("animal_dictionary.title"));
    }

    public GUIAnimalDictionary(ItemStack bookStack, String page) {
        super(bookStack, Component.translatable("animal_dictionary.title"));
        this.currentPageJSON = new ResourceLocation(this.getTextFileDirectory() + page + ".json");
    }

    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {

        RenderLaviathan.renderWithoutShaking = true;
        RenderMurmurBody.renderWithHead = true;
        RenderUnderminer.renderWithPickaxe = true;
        super.render(guiGraphics, x, y, partialTicks);
        RenderLaviathan.renderWithoutShaking = false;
        RenderMurmurBody.renderWithHead = false;
        RenderUnderminer.renderWithPickaxe = false;
    }

    protected int getBindingColor() {
        return 0X606B26;
    }

    public ResourceLocation getRootPage() {
        return ROOT;
    }

    public String getTextFileDirectory() {
        return "alexsmobs:book/animal_dictionary/";
    }
}
