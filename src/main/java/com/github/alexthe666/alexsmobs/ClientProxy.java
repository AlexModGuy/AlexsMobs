package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.client.render.*;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.Callable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public void clientInit() {
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.GRIZZLY_BEAR, manager -> new RenderGrizzlyBear(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.ROADRUNNER, manager -> new RenderRoadrunner(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.BONE_SERPENT, manager -> new RenderBoneSerpent(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.BONE_SERPENT_PART, manager -> new RenderBoneSerpentPart(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.GAZELLE, manager -> new RenderGazelle(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.CROCODILE, manager -> new RenderCrocodile(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.FLY, manager -> new RenderFly(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.HUMMINGBIRD, manager -> new RenderHummingbird(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.ORCA, manager -> new RenderOrca(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.SUNBIRD, manager -> new RenderSunbird(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.GORILLA, manager -> new RenderGorilla(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.CRIMSON_MOSQUITO, manager -> new RenderCrimsonMosquito(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.MOSQUITO_SPIT, manager -> new RenderMosquitoSpit(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.RATTLESNAKE, manager -> new RenderRattlesnake(manager));
        RenderingRegistry.registerEntityRenderingHandler(AMEntityRegistry.ENDERGRADE, manager -> new RenderEndergrade(manager));
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        RenderType lavaType = RenderType.getTranslucent();
        RenderTypeLookup.setRenderLayer(Fluids.LAVA, lavaType);
        RenderTypeLookup.setRenderLayer(Fluids.FLOWING_LAVA, lavaType);
    }


    public Item.Properties setupISTER(Item.Properties group) {
        return group.setISTER(ClientProxy::getTEISR);
    }

    @OnlyIn(Dist.CLIENT)
    public static Callable<ItemStackTileEntityRenderer> getTEISR() {
        return AMItemstackRenderer::new;
    }

    public PlayerEntity getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }
}
