package com.github.alexthe666.alexsmobs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {
   
    public void init() {
    }

    public void clientInit() {
    }

    public PlayerEntity getClientSidePlayer() {
        return null;
    }
}
