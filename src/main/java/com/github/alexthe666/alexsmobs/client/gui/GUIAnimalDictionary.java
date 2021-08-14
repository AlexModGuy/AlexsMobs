package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class GUIAnimalDictionary extends GuiBasicBook {


    public GUIAnimalDictionary(ItemStack bookStack) {
        super(bookStack, new TranslationTextComponent("animal_dictionary.title"));
    }

    public GUIAnimalDictionary(ItemStack bookStack, String page) {
        super(bookStack, new TranslationTextComponent("animal_dictionary.title"));
        this.currentPageJSON = new ResourceLocation(this.getTextFileDirectory() + page + ".json");
    }

    protected int getBindingColor() {
        return 0X606B26;
    }

    public ResourceLocation getRootPage() {
        return new ResourceLocation("alexsmobs:book/animal_dictionary/root.json");
    }

    public String getTextFileDirectory() {
        return "alexsmobs:book/animal_dictionary/";
    }
}
