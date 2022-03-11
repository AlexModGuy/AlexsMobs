package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.ClientLayerRegistry;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.particle.*;
import com.github.alexthe666.alexsmobs.client.render.*;
import com.github.alexthe666.alexsmobs.client.render.item.AMItemRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.tile.*;
import com.github.alexthe666.alexsmobs.client.sound.SoundBearMusicBox;
import com.github.alexthe666.alexsmobs.client.sound.SoundLaCucaracha;
import com.github.alexthe666.alexsmobs.client.sound.SoundWormBoss;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemBloodSprayer;
import com.github.alexthe666.alexsmobs.item.ItemHemolymphBlaster;
import com.github.alexthe666.alexsmobs.item.ItemTarantulaHawkElytra;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.CameraType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;
import java.util.concurrent.Callable;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static final Map<Integer, SoundBearMusicBox> BEAR_MUSIC_BOX_SOUND_MAP = new HashMap<>();
    public static final Map<Integer, SoundLaCucaracha> COCKROACH_SOUND_MAP = new HashMap<>();
    public static final Map<Integer, SoundWormBoss> WORMBOSS_SOUND_MAP = new HashMap<>();
    public static List<UUID> currentUnrenderedEntities = new ArrayList<UUID>();
    public CameraType prevPOV = CameraType.FIRST_PERSON;
    public static int voidPortalCreationTime = 0;
    public boolean initializedRainbowBuffers = false;

    public void init(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onBlockColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientLayerRegistry::onAddLayers);
    }

    public void clientInit() {
        initRainbowBuffers();
        ItemRenderer itemRendererIn = Minecraft.getInstance().getItemRenderer();
        EntityRenderers.register(AMEntityRegistry.GRIZZLY_BEAR.get(),  RenderGrizzlyBear::new);
        EntityRenderers.register(AMEntityRegistry.ROADRUNNER.get(),  RenderRoadrunner::new);
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT.get(),  RenderBoneSerpent::new);
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT_PART.get(),  RenderBoneSerpentPart::new);
        EntityRenderers.register(AMEntityRegistry.GAZELLE.get(),  RenderGazelle::new);
        EntityRenderers.register(AMEntityRegistry.CROCODILE.get(),  RenderCrocodile::new);
        EntityRenderers.register(AMEntityRegistry.FLY.get(),  RenderFly::new);
        EntityRenderers.register(AMEntityRegistry.HUMMINGBIRD.get(),  RenderHummingbird::new);
        EntityRenderers.register(AMEntityRegistry.ORCA.get(),  RenderOrca::new);
        EntityRenderers.register(AMEntityRegistry.SUNBIRD.get(),  RenderSunbird::new);
        EntityRenderers.register(AMEntityRegistry.GORILLA.get(),  RenderGorilla::new);
        EntityRenderers.register(AMEntityRegistry.CRIMSON_MOSQUITO.get(),  RenderCrimsonMosquito::new);
        EntityRenderers.register(AMEntityRegistry.MOSQUITO_SPIT.get(),  RenderMosquitoSpit::new);
        EntityRenderers.register(AMEntityRegistry.RATTLESNAKE.get(),  RenderRattlesnake::new);
        EntityRenderers.register(AMEntityRegistry.ENDERGRADE.get(),  RenderEndergrade::new);
        EntityRenderers.register(AMEntityRegistry.HAMMERHEAD_SHARK.get(),  RenderHammerheadShark::new);
        EntityRenderers.register(AMEntityRegistry.SHARK_TOOTH_ARROW.get(),  RenderSharkToothArrow::new);
        EntityRenderers.register(AMEntityRegistry.LOBSTER.get(),  RenderLobster::new);
        EntityRenderers.register(AMEntityRegistry.KOMODO_DRAGON.get(),  RenderKomodoDragon::new);
        EntityRenderers.register(AMEntityRegistry.CAPUCHIN_MONKEY.get(),  RenderCapuchinMonkey::new);
        EntityRenderers.register(AMEntityRegistry.TOSSED_ITEM.get(),  RenderTossedItem::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_HEAD.get(),  RenderCentipedeHead::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_BODY.get(),  RenderCentipedeBody::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_TAIL.get(),  RenderCentipedeTail::new);
        EntityRenderers.register(AMEntityRegistry.WARPED_TOAD.get(),  RenderWarpedToad::new);
        EntityRenderers.register(AMEntityRegistry.MOOSE.get(),  RenderMoose::new);
        EntityRenderers.register(AMEntityRegistry.MIMICUBE.get(),  RenderMimicube::new);
        EntityRenderers.register(AMEntityRegistry.RACCOON.get(),  RenderRaccoon::new);
        EntityRenderers.register(AMEntityRegistry.BLOBFISH.get(),  RenderBlobfish::new);
        EntityRenderers.register(AMEntityRegistry.SEAL.get(),  RenderSeal::new);
        EntityRenderers.register(AMEntityRegistry.COCKROACH.get(),  RenderCockroach::new);
        EntityRenderers.register(AMEntityRegistry.COCKROACH_EGG.get(),  (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.SHOEBILL.get(),  RenderShoebill::new);
        EntityRenderers.register(AMEntityRegistry.ELEPHANT.get(),  RenderElephant::new);
        EntityRenderers.register(AMEntityRegistry.SOUL_VULTURE.get(),  RenderSoulVulture::new);
        EntityRenderers.register(AMEntityRegistry.SNOW_LEOPARD.get(),  RenderSnowLeopard::new);
        EntityRenderers.register(AMEntityRegistry.SPECTRE.get(),  RenderSpectre::new);
        EntityRenderers.register(AMEntityRegistry.CROW.get(),  RenderCrow::new);
        EntityRenderers.register(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(),  RenderAlligatorSnappingTurtle::new);
        EntityRenderers.register(AMEntityRegistry.MUNGUS.get(),  RenderMungus::new);
        EntityRenderers.register(AMEntityRegistry.MANTIS_SHRIMP.get(),  RenderMantisShrimp::new);
        EntityRenderers.register(AMEntityRegistry.GUSTER.get(),  RenderGuster::new);
        EntityRenderers.register(AMEntityRegistry.SAND_SHOT.get(),  RenderSandShot::new);
        EntityRenderers.register(AMEntityRegistry.GUST.get(),  RenderGust::new);
        EntityRenderers.register(AMEntityRegistry.WARPED_MOSCO.get(),  RenderWarpedMosco::new);
        EntityRenderers.register(AMEntityRegistry.HEMOLYMPH.get(),  RenderHemolymph::new);
        EntityRenderers.register(AMEntityRegistry.STRADDLER.get(),  RenderStraddler::new);
        EntityRenderers.register(AMEntityRegistry.STRADPOLE.get(),  RenderStradpole::new);
        EntityRenderers.register(AMEntityRegistry.STRADDLEBOARD.get(),  RenderStraddleboard::new);
        EntityRenderers.register(AMEntityRegistry.EMU.get(),  RenderEmu::new);
        EntityRenderers.register(AMEntityRegistry.EMU_EGG.get(),  (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.PLATYPUS.get(),  RenderPlatypus::new);
        EntityRenderers.register(AMEntityRegistry.DROPBEAR.get(),  RenderDropBear::new);
        EntityRenderers.register(AMEntityRegistry.TASMANIAN_DEVIL.get(),  RenderTasmanianDevil::new);
        EntityRenderers.register(AMEntityRegistry.KANGAROO.get(),  RenderKangaroo::new);
        EntityRenderers.register(AMEntityRegistry.CACHALOT_WHALE.get(),  RenderCachalotWhale::new);
        EntityRenderers.register(AMEntityRegistry.CACHALOT_ECHO.get(),  RenderCachalotEcho::new);
        EntityRenderers.register(AMEntityRegistry.LEAFCUTTER_ANT.get(),  RenderLeafcutterAnt::new);
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE.get(),  RenderEnderiophage::new);
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE_ROCKET.get(),  (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.BALD_EAGLE.get(),  RenderBaldEagle::new);
        EntityRenderers.register(AMEntityRegistry.TIGER.get(),  RenderTiger::new);
        EntityRenderers.register(AMEntityRegistry.TARANTULA_HAWK.get(),  RenderTarantulaHawk::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM.get(),  RenderVoidWormHead::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_PART.get(),  RenderVoidWormBody::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_SHOT.get(),  RenderVoidWormShot::new);
        EntityRenderers.register(AMEntityRegistry.VOID_PORTAL.get(),  RenderVoidPortal::new);
        EntityRenderers.register(AMEntityRegistry.FRILLED_SHARK.get(),  RenderFrilledShark::new);
        EntityRenderers.register(AMEntityRegistry.MIMIC_OCTOPUS.get(),  RenderMimicOctopus::new);
        EntityRenderers.register(AMEntityRegistry.SEAGULL.get(),  RenderSeagull::new);
        EntityRenderers.register(AMEntityRegistry.FROSTSTALKER.get(),  RenderFroststalker::new);
        EntityRenderers.register(AMEntityRegistry.ICE_SHARD.get(),  RenderIceShard::new);
        EntityRenderers.register(AMEntityRegistry.TUSKLIN.get(),  RenderTusklin::new);
        EntityRenderers.register(AMEntityRegistry.LAVIATHAN.get(),  RenderLaviathan::new);
        EntityRenderers.register(AMEntityRegistry.COSMAW.get(),  RenderCosmaw::new);
        EntityRenderers.register(AMEntityRegistry.TOUCAN.get(),  RenderToucan::new);
        EntityRenderers.register(AMEntityRegistry.MANED_WOLF.get(),  RenderManedWolf::new);
        EntityRenderers.register(AMEntityRegistry.ANACONDA.get(),  RenderAnaconda::new);
        EntityRenderers.register(AMEntityRegistry.ANACONDA_PART.get(),  RenderAnacondaPart::new);
        EntityRenderers.register(AMEntityRegistry.VINE_LASSO.get(),  RenderVineLasso::new);
        EntityRenderers.register(AMEntityRegistry.ANTEATER.get(),  RenderAnteater::new);
        EntityRenderers.register(AMEntityRegistry.ROCKY_ROLLER.get(),  RenderRockyRoller::new);
        EntityRenderers.register(AMEntityRegistry.FLUTTER.get(),  RenderFlutter::new);
        EntityRenderers.register(AMEntityRegistry.POLLEN_BALL.get(),  RenderPollenBall::new);
        EntityRenderers.register(AMEntityRegistry.GELADA_MONKEY.get(),  RenderGeladaMonkey::new);
        EntityRenderers.register(AMEntityRegistry.JERBOA.get(),  RenderJerboa::new);
        EntityRenderers.register(AMEntityRegistry.TERRAPIN.get(),  RenderTerrapin::new);
        EntityRenderers.register(AMEntityRegistry.COMB_JELLY.get(),  RenderCombJelly::new);
        EntityRenderers.register(AMEntityRegistry.COSMIC_COD.get(),  RenderCosmicCod::new);
        EntityRenderers.register(AMEntityRegistry.BUNFUNGUS.get(),  RenderBunfungus::new);
        EntityRenderers.register(AMEntityRegistry.BISON.get(),  RenderBison::new);
        EntityRenderers.register(AMEntityRegistry.GIANT_SQUID.get(),  RenderGiantSquid::new);
        EntityRenderers.register(AMEntityRegistry.SQUID_GRAPPLE.get(),  RenderSquidGrapple::new);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        try{
            ItemProperties.register(AMItemRegistry.BLOOD_SPRAYER.get(), new ResourceLocation("empty"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return !ItemBloodSprayer.isUsable(stack) || p_239428_2_ instanceof Player && ((Player) p_239428_2_).getCooldowns().isOnCooldown(AMItemRegistry.BLOOD_SPRAYER.get()) ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.HEMOLYMPH_BLASTER.get(), new ResourceLocation("empty"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return !ItemHemolymphBlaster.isUsable(stack) || p_239428_2_ instanceof Player && ((Player) p_239428_2_).getCooldowns().isOnCooldown(AMItemRegistry.HEMOLYMPH_BLASTER.get()) ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.TARANTULA_HAWK_ELYTRA.get(), new ResourceLocation("broken"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return ItemTarantulaHawkElytra.isUsable(stack) ? 0.0F : 1.0F;
            });
            ItemProperties.register(AMItemRegistry.SHIELD_OF_THE_DEEP.get(), new ResourceLocation("blocking"), (stack, p_239421_1_, p_239421_2_, j) -> {
                return p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == stack ? 1.0F : 0.0F;
            });
        }catch (Exception e){
            AlexsMobs.LOGGER.warn("Could not load item models for weapons");
        }
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.BANANA_PEEL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.CAPSID.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.VOID_WORM_BEAK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.HUMMINGBIRD_FEEDER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.RAINBOW_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.BISON_FUR_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.BISON_CARPET.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.END_PIRATE_DOOR.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.END_PIRATE_TRAPDOOR.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.PHANTOM_SAIL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.SPECTRE_SAIL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.ENDER_RESIDUE.get(), RenderType.translucent());
        BlockEntityRenderers.register(AMTileEntityRegistry.CAPSID.get(), RenderCapsid::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.VOID_WORM_BEAK.get(), RenderVoidWormBeak::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.END_PIRATE_DOOR.get(), RenderEndPirateDoor::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.END_PIRATE_ANCHOR.get(), RenderEndPirateAnchor::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.END_PIRATE_ANCHOR_WINCH.get(), RenderEndPirateAnchorWinch::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.END_PIRATE_SHIP_WHEEL.get(), RenderEndPirateShipWheel::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.END_PIRATE_FLAG.get(), RenderEndPirateFlag::new);
    }

    private void initRainbowBuffers(){
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.COMBJELLY_RAINBOW_GLINT, new BufferBuilder(AMRenderTypes.COMBJELLY_RAINBOW_GLINT.bufferSize()));
        initializedRainbowBuffers = true;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemColors(ColorHandlerEvent.Item event) {
        AlexsMobs.LOGGER.info("loaded in item colorizer");
        event.getItemColors().register((stack, colorIn) -> colorIn < 1 ? -1 : ((DyeableLeatherItem)stack.getItem()).getColor(stack), AMItemRegistry.STRADDLEBOARD.get());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onBlockColors(ColorHandlerEvent.Block event) {
        AlexsMobs.LOGGER.info("loaded in block colorizer");
        event.getBlockColors().register((state, tintGetter, pos, tint) -> {
            return tintGetter != null && pos != null ? RainbowUtil.calculateGlassColor(pos) : -1;
        }, AMBlockRegistry.RAINBOW_GLASS.get());
    }

    public void openBookGUI(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreen(new GUIAnimalDictionary(itemStackIn));
    }

    public void openBookGUI(ItemStack itemStackIn, String page) {
        Minecraft.getInstance().setScreen(new GUIAnimalDictionary(itemStackIn, page));
    }

    @OnlyIn(Dist.CLIENT)
    public static Callable<BlockEntityWithoutLevelRenderer> getTEISR() {
        return AMItemstackRenderer::new;
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    @OnlyIn(Dist.CLIENT)
    public Object getArmorModel(int armorId, LivingEntity entity) {
        switch (armorId) {
            /*
            case 0:
                return ROADRUNNER_BOOTS_MODEL;
            case 1:
                return MOOSE_HEADGEAR_MODEL;
            case 2:
                return FRONTIER_CAP_MODEL.withAnimations(entity);
            case 3:
                return SOMBRERO_MODEL;
            case 4:
                return SPIKED_TURTLE_SHELL_MODEL;
            case 5:
                return FEDORA_MODEL;
            case 6:
                return ELYTRA_MODEL.withAnimations(entity);

             */
            default:
                return null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onEntityStatus(Entity entity, byte updateKind) {
        if(entity instanceof EntityCockroach && entity.isAlive() && updateKind == 67){
            SoundLaCucaracha sound;
            if(COCKROACH_SOUND_MAP.get(entity.getId()) == null){
                sound = new SoundLaCucaracha((EntityCockroach)entity);
                COCKROACH_SOUND_MAP.put(entity.getId(), sound);
            }else{
                sound = COCKROACH_SOUND_MAP.get(entity.getId());
            }
            if(!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyCockroach()){
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        }
        if(entity instanceof EntityVoidWorm && entity.isAlive() && updateKind == 67){
            float f2 = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
            if(f2 <= 0){
                WORMBOSS_SOUND_MAP.clear();
            }else{
                SoundWormBoss sound;
                if(WORMBOSS_SOUND_MAP.get(entity.getId()) == null){
                    sound = new SoundWormBoss((EntityVoidWorm)entity);
                    WORMBOSS_SOUND_MAP.put(entity.getId(), sound);
                }else{
                    sound = WORMBOSS_SOUND_MAP.get(entity.getId());
                }
                if(!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.isNearest()){
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            }
        }
        if(entity instanceof EntityGrizzlyBear && entity.isAlive() && updateKind == 67){
            SoundBearMusicBox sound;
            if(BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId()) == null){
                sound = new SoundBearMusicBox((EntityGrizzlyBear)entity);
                BEAR_MUSIC_BOX_SOUND_MAP.put(entity.getId(), sound);
            }else{
                sound = BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId());
            }
            if(!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyMusicBox()){
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        }
    }

    public void updateBiomeVisuals(int x, int z) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty(x- 32, 0, x - 32, z + 32, 255, z + 32);
    }

    public void setupParticles() {
        AlexsMobs.LOGGER.debug("Registered particle factories");
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.GUSTER_SAND_SPIN, ParticleGusterSandSpin.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.GUSTER_SAND_SHOT, ParticleGusterSandShot.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.GUSTER_SAND_SPIN_RED, ParticleGusterSandSpin.FactoryRed::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.GUSTER_SAND_SHOT_RED, ParticleGusterSandShot.FactoryRed::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.GUSTER_SAND_SPIN_SOUL, ParticleGusterSandSpin.FactorySoul::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.GUSTER_SAND_SHOT_SOUL, ParticleGusterSandShot.FactorySoul::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.HEMOLYMPH, ParticleHemolymph.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.PLATYPUS_SENSE, ParticlePlatypus.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.WHALE_SPLASH, ParticleWhaleSplash.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.DNA, ParticleDna.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.SHOCKED, ParticleSimpleHeart.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.WORM_PORTAL, ParticleWormPortal.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.INVERT_DIG, ParticleInvertDig.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.TEETH_GLINT, ParticleTeethGlint.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.SMELLY, ParticleSmelly.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION, ParticleBunfungusTransformation.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.FUNGUS_BUBBLE, ParticleFungusBubble.Factory::new);
        Minecraft.getInstance().particleEngine.register(AMParticleRegistry.BEAR_FREDDY, new ParticleBearFreddy.Factory());
    }


    public void setRenderViewEntity(Entity entity){
        prevPOV = Minecraft.getInstance().options.getCameraType();
        Minecraft.getInstance().setCameraEntity(entity);
        Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    public void resetRenderViewEntity(){
        Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
    }

    public int getPreviousPOV(){
        return prevPOV.ordinal();
    }

    public boolean isFarFromCamera(double x, double y, double z) {
        Minecraft lvt_1_1_ = Minecraft.getInstance();
        return lvt_1_1_.gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z) >= 256.0D;
    }

    public void resetVoidPortalCreation(Player player){

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterEntityRenders(EntityRenderersEvent.RegisterLayerDefinitions event){
    }

    @Override
    public Object getISTERProperties() {
        return new AMItemRenderProperties();
    }

    @Override
    public Object getArmorRenderProperties() {
        return new CustomArmorRenderProperties();
    }

    public void spawnSpecialParticle(int type){
        if(type == 0){
            Minecraft.getInstance().level.addParticle(AMParticleRegistry.BEAR_FREDDY, Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ(), 0, 0, 0);
        }
    }

    public void processVisualFlag(Entity entity, int flag) {
        if(entity == Minecraft.getInstance().player && flag == 87){
            ClientEvents.renderStaticScreenFor = 60;
        }
    }
}
