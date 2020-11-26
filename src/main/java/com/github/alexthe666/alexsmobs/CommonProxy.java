package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.Callable;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
   
    public void init() {
    }

    public void clientInit() {
    }

    public Item.Properties setupISTER(Item.Properties group) {
        return group;
    }

    public PlayerEntity getClientSidePlayer() {
        return null;
    }

    public void openBookGUI(ItemStack itemStackIn) {
}

    public Object getArmorModel(int armorId) {
        return null;
    }
}
