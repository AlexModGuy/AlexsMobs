package com.github.alexthe666.alexsmobs.client.render.item;

import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;

public class AMItemRenderProperties implements IItemRenderProperties {

    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return new AMItemstackRenderer();
    }
}
