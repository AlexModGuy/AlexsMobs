package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
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

    public void render(PoseStack matrixStack, int x, int y, float partialTicks) {
        if(this.currentPageJSON == getRootPage() && currentPageCounter == 0){
            int k = (this.width - this.xSize) / 2;
            int l = (this.height - this.ySize + 128) / 2;
            RenderSystem.applyModelViewMatrix();
            PoseStack stack = RenderSystem.getModelViewStack();
            stack.pushPose();
            stack.translate((double)k, (double)l, 0.0D);
            stack.scale(2.75F, 2.75F, 2.75F);
            this.itemRenderer.renderGuiItem(new ItemStack(AMItemRegistry.TAB_ICON.get()), 25, 14);
            this.itemRenderer.blitOffset = 0.0F;
            stack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
        super.render(matrixStack, x, y, partialTicks);
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
