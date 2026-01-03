package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.config.ConfigHolder;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.inventory.AMMenuRegistry;
import com.github.alexthe666.alexsmobs.item.AMArmorMaterial;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.*;
import com.github.alexthe666.alexsmobs.misc.*;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.world.AMFeatureRegistry;
import com.github.alexthe666.alexsmobs.world.AMLeafcutterAntBiomeModifier;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnBiomeModifier;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnStructureModifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

@Mod(AlexsMobs.MODID)
public class AlexsMobs {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "alexsmobs";
    public static final String VERSION = "1.22.9";
    
    // Use supplier pattern to avoid loading ClientProxy class on dedicated server
    public static final CommonProxy PROXY = unsafeRunForDist(
        () -> ClientProxy::new,
        () -> CommonProxy::new
    );
    private static boolean isAprilFools = false;
    private static boolean isHalloween = false;
    
    public AlexsMobs(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::setupClient);
        modEventBus.addListener(this::onModConfigEvent);
        modEventBus.addListener(this::setupEntityModelLayers);
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(AMEffectRegistry::registerBrewingRecipes);
        
        // Register all deferred registers
        AMBlockRegistry.DEF_REG.register(modEventBus);
        AMEntityRegistry.DEF_REG.register(modEventBus);
        AMItemRegistry.DEF_REG.register(modEventBus);
        AMArmorMaterial.ARMOR_MATERIALS.register(modEventBus);
        AMTileEntityRegistry.DEF_REG.register(modEventBus);
        AMPointOfInterestRegistry.DEF_REG.register(modEventBus);
        AMFeatureRegistry.DEF_REG.register(modEventBus);
        AMSoundRegistry.DEF_REG.register(modEventBus);
        AMParticleRegistry.DEF_REG.register(modEventBus);
        AMPaintingRegistry.DEF_REG.register(modEventBus);
        AMEffectRegistry.EFFECT_DEF_REG.register(modEventBus);
        AMEffectRegistry.POTION_DEF_REG.register(modEventBus);
        // NeoForge 1.21: Enchantments are now data-driven via datapacks, no code registration needed
        // See data/alexsmobs/enchantment/ for enchantment definitions
        AMMenuRegistry.DEF_REG.register(modEventBus);
        AMRecipeRegistry.DEF_REG.register(modEventBus);
        AMLootRegistry.DEF_REG.register(modEventBus);
        AMBannerRegistry.DEF_REG.register(modEventBus);
        AMCreativeTabRegistry.DEF_REG.register(modEventBus);
        AMAdvancementTriggerRegistry.DEF_REG.register(modEventBus);
        
        // Biome modifiers
        AMMobSpawnBiomeModifier.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        AMLeafcutterAntBiomeModifier.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        
        // Structure modifiers
        AMMobSpawnStructureModifier.STRUCTURE_MODIFIER_SERIALIZERS.register(modEventBus);
        
        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC, "alexsmobs.toml");
        
        PROXY.init();
        // ServerEvents is already registered via @EventBusSubscriber annotation
        
        // Check for special dates
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        isAprilFools = calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1;
        isHalloween = calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) >= 29 && calendar.get(Calendar.DATE) <= 31;
    }

    public static boolean isAprilFools() {
        return isAprilFools || AMConfig.superSecretSettings;
    }

    public static boolean isHalloween() {
        return isHalloween || AMConfig.superSecretSettings;
    }

    private void setupEntityModelLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        AMModelLayers.register(event);
    }

    private void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
            AMConfig.bake(config);
        }
        BiomeConfig.init();
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID).versioned(VERSION).optional();
        // Client to Server messages
        registrar.playToServer(MessageSwingArm.TYPE, MessageSwingArm.CODEC, MessageSwingArm::handle);
        registrar.playToServer(MessageUpdateEagleControls.TYPE, MessageUpdateEagleControls.CODEC, MessageUpdateEagleControls::handle);
        // Bidirectional - sent from client (when player attacks multipart) and from server (sendMSGToAll for sync)
        registrar.playBidirectional(MessageHurtMultipart.TYPE, MessageHurtMultipart.CODEC, MessageHurtMultipart::handle);
        registrar.playBidirectional(MessageInteractMultipart.TYPE, MessageInteractMultipart.CODEC, MessageInteractMultipart::handle);
        registrar.playToServer(MessageTransmuteFromMenu.TYPE, MessageTransmuteFromMenu.CODEC, MessageTransmuteFromMenu::handle);
        
        // Server to Client messages
        registrar.playToClient(MessageCrowDismount.TYPE, MessageCrowDismount.CODEC, MessageCrowDismount::handle);
        registrar.playToClient(MessageCrowMountPlayer.TYPE, MessageCrowMountPlayer.CODEC, MessageCrowMountPlayer::handle);
        // Bidirectional - sent from client (falconry glove launch) and from server (sendMSGToAll for sync)
        registrar.playBidirectional(MessageMosquitoDismount.TYPE, MessageMosquitoDismount.CODEC, MessageMosquitoDismount::handle);
        registrar.playToClient(MessageMosquitoMountPlayer.TYPE, MessageMosquitoMountPlayer.CODEC, MessageMosquitoMountPlayer::handle);
        registrar.playToClient(MessageKangarooEat.TYPE, MessageKangarooEat.CODEC, MessageKangarooEat::handle);
        registrar.playToClient(MessageKangarooInventorySync.TYPE, MessageKangarooInventorySync.CODEC, MessageKangarooInventorySync::handle);
        // Client to Server - sent from client when jukebox plays near dancing mobs (e.g., rain frog rain dance)
        registrar.playToServer(MessageStartDancing.TYPE, MessageStartDancing.CODEC, MessageStartDancing::handle);
        // Bidirectional - sent from client (falconry glove launch) and from server (sendMSGToAll for sync)
        registrar.playBidirectional(MessageSyncEntityPos.TYPE, MessageSyncEntityPos.CODEC, MessageSyncEntityPos::handle);
        registrar.playToClient(MessageSendVisualFlagFromServer.TYPE, MessageSendVisualFlagFromServer.CODEC, MessageSendVisualFlagFromServer::handle);
        registrar.playToClient(MessageSetPupfishChunkOnClient.TYPE, MessageSetPupfishChunkOnClient.CODEC, MessageSetPupfishChunkOnClient::handle);
        registrar.playToClient(MessageTarantulaHawkSting.TYPE, MessageTarantulaHawkSting.CODEC, MessageTarantulaHawkSting::handle);
        registrar.playToClient(MessageMungusBiomeChange.TYPE, MessageMungusBiomeChange.CODEC, MessageMungusBiomeChange::handle);
        registrar.playToClient(MessageUpdateCapsid.TYPE, MessageUpdateCapsid.CODEC, MessageUpdateCapsid::handle);
        registrar.playToClient(MessageUpdateTransmutablesToDisplay.TYPE, MessageUpdateTransmutablesToDisplay.CODEC, MessageUpdateTransmutablesToDisplay::handle);
    }

    public static <MSG extends CustomPacketPayload> void sendMSGToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sendMSGToAll(MSG message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    public static <MSG extends CustomPacketPayload> void sendNonLocal(MSG msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(AMItemRegistry::init);
        event.enqueueWork(AMItemRegistry::initDispenser);
        AMAdvancementTriggerRegistry.init();
        AMEffectRegistry.init();
        AMRecipeRegistry.init();
        PROXY.initPathfinding();
    }

    private void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(PROXY::clientInit);
    }
    
    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // Safe dist proxy helper - uses Supplier to avoid loading client classes on dedicated server
    private static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> clientTarget.get().get();
            case DEDICATED_SERVER -> serverTarget.get().get();
        };
    }
}
