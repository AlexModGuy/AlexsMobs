package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.model.*;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.particle.*;
import com.github.alexthe666.alexsmobs.client.render.*;
import com.github.alexthe666.alexsmobs.client.render.item.AMItemRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderCapsid;
import com.github.alexthe666.alexsmobs.client.render.tile.RenderVoidWormBeak;
import com.github.alexthe666.alexsmobs.client.sound.SoundLaCucaracha;
import com.github.alexthe666.alexsmobs.client.sound.SoundWormBoss;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemBloodSprayer;
import com.github.alexthe666.alexsmobs.item.ItemHemolymphBlaster;
import com.github.alexthe666.alexsmobs.item.ItemTarantulaHawkElytra;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.citadel.client.CitadelItemRenderProperties;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static final Map<Integer, SoundLaCucaracha> COCKROACH_SOUND_MAP = new HashMap<>();
    public static final Map<Integer, SoundWormBoss> WORMBOSS_SOUND_MAP = new HashMap<>();
    public static List<UUID> currentUnrenderedEntities = new ArrayList<UUID>();
    public CameraType prevPOV = CameraType.FIRST_PERSON;
    public static int voidPortalCreationTime = 0;

    public void init(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onItemColors);
    }

    public void clientInit() {
        ItemRenderer itemRendererIn = Minecraft.getInstance().getItemRenderer();
        EntityRenderers.register(AMEntityRegistry.GRIZZLY_BEAR, RenderGrizzlyBear::new);
        EntityRenderers.register(AMEntityRegistry.ROADRUNNER, RenderRoadrunner::new);
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT, RenderBoneSerpent::new);
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT_PART, RenderBoneSerpentPart::new);
        EntityRenderers.register(AMEntityRegistry.GAZELLE, RenderGazelle::new);
        EntityRenderers.register(AMEntityRegistry.CROCODILE, RenderCrocodile::new);
        EntityRenderers.register(AMEntityRegistry.FLY, RenderFly::new);
        EntityRenderers.register(AMEntityRegistry.HUMMINGBIRD, RenderHummingbird::new);
        EntityRenderers.register(AMEntityRegistry.ORCA, RenderOrca::new);
        EntityRenderers.register(AMEntityRegistry.SUNBIRD, RenderSunbird::new);
        EntityRenderers.register(AMEntityRegistry.GORILLA, RenderGorilla::new);
        EntityRenderers.register(AMEntityRegistry.CRIMSON_MOSQUITO, RenderCrimsonMosquito::new);
        EntityRenderers.register(AMEntityRegistry.MOSQUITO_SPIT, RenderMosquitoSpit::new);
        EntityRenderers.register(AMEntityRegistry.RATTLESNAKE, RenderRattlesnake::new);
        EntityRenderers.register(AMEntityRegistry.ENDERGRADE, RenderEndergrade::new);
        EntityRenderers.register(AMEntityRegistry.HAMMERHEAD_SHARK, RenderHammerheadShark::new);
        EntityRenderers.register(AMEntityRegistry.SHARK_TOOTH_ARROW, RenderSharkToothArrow::new);
        EntityRenderers.register(AMEntityRegistry.LOBSTER, RenderLobster::new);
        EntityRenderers.register(AMEntityRegistry.KOMODO_DRAGON, RenderKomodoDragon::new);
        EntityRenderers.register(AMEntityRegistry.CAPUCHIN_MONKEY, RenderCapuchinMonkey::new);
        EntityRenderers.register(AMEntityRegistry.TOSSED_ITEM, RenderTossedItem::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_HEAD, RenderCentipedeHead::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_BODY, RenderCentipedeBody::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_TAIL, RenderCentipedeTail::new);
        EntityRenderers.register(AMEntityRegistry.WARPED_TOAD, RenderWarpedToad::new);
        EntityRenderers.register(AMEntityRegistry.MOOSE, RenderMoose::new);
        EntityRenderers.register(AMEntityRegistry.MIMICUBE, RenderMimicube::new);
        EntityRenderers.register(AMEntityRegistry.RACCOON, RenderRaccoon::new);
        EntityRenderers.register(AMEntityRegistry.BLOBFISH, RenderBlobfish::new);
        EntityRenderers.register(AMEntityRegistry.SEAL, RenderSeal::new);
        EntityRenderers.register(AMEntityRegistry.COCKROACH, RenderCockroach::new);
        EntityRenderers.register(AMEntityRegistry.COCKROACH_EGG, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.SHOEBILL, RenderShoebill::new);
        EntityRenderers.register(AMEntityRegistry.ELEPHANT, RenderElephant::new);
        EntityRenderers.register(AMEntityRegistry.SOUL_VULTURE, RenderSoulVulture::new);
        EntityRenderers.register(AMEntityRegistry.SNOW_LEOPARD, RenderSnowLeopard::new);
        EntityRenderers.register(AMEntityRegistry.SPECTRE, RenderSpectre::new);
        EntityRenderers.register(AMEntityRegistry.CROW, RenderCrow::new);
        EntityRenderers.register(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE, RenderAlligatorSnappingTurtle::new);
        EntityRenderers.register(AMEntityRegistry.MUNGUS, RenderMungus::new);
        EntityRenderers.register(AMEntityRegistry.MANTIS_SHRIMP, RenderMantisShrimp::new);
        EntityRenderers.register(AMEntityRegistry.GUSTER, RenderGuster::new);
        EntityRenderers.register(AMEntityRegistry.SAND_SHOT, RenderSandShot::new);
        EntityRenderers.register(AMEntityRegistry.GUST, RenderGust::new);
        EntityRenderers.register(AMEntityRegistry.WARPED_MOSCO, RenderWarpedMosco::new);
        EntityRenderers.register(AMEntityRegistry.HEMOLYMPH, RenderHemolymph::new);
        EntityRenderers.register(AMEntityRegistry.STRADDLER, RenderStraddler::new);
        EntityRenderers.register(AMEntityRegistry.STRADPOLE, RenderStradpole::new);
        EntityRenderers.register(AMEntityRegistry.STRADDLEBOARD, RenderStraddleboard::new);
        EntityRenderers.register(AMEntityRegistry.EMU, RenderEmu::new);
        EntityRenderers.register(AMEntityRegistry.EMU_EGG, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.PLATYPUS, RenderPlatypus::new);
        EntityRenderers.register(AMEntityRegistry.DROPBEAR, RenderDropBear::new);
        EntityRenderers.register(AMEntityRegistry.TASMANIAN_DEVIL, RenderTasmanianDevil::new);
        EntityRenderers.register(AMEntityRegistry.KANGAROO, RenderKangaroo::new);
        EntityRenderers.register(AMEntityRegistry.CACHALOT_WHALE, RenderCachalotWhale::new);
        EntityRenderers.register(AMEntityRegistry.CACHALOT_ECHO, RenderCachalotEcho::new);
        EntityRenderers.register(AMEntityRegistry.LEAFCUTTER_ANT, RenderLeafcutterAnt::new);
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE, RenderEnderiophage::new);
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE_ROCKET, (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.BALD_EAGLE, RenderBaldEagle::new);
        EntityRenderers.register(AMEntityRegistry.TIGER, RenderTiger::new);
        EntityRenderers.register(AMEntityRegistry.TARANTULA_HAWK, RenderTarantulaHawk::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM, RenderVoidWormHead::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_PART, RenderVoidWormBody::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_SHOT, RenderVoidWormShot::new);
        EntityRenderers.register(AMEntityRegistry.VOID_PORTAL, RenderVoidPortal::new);
        EntityRenderers.register(AMEntityRegistry.FRILLED_SHARK, RenderFrilledShark::new);
        EntityRenderers.register(AMEntityRegistry.MIMIC_OCTOPUS, RenderMimicOctopus::new);
        EntityRenderers.register(AMEntityRegistry.SEAGULL, RenderSeagull::new);
        EntityRenderers.register(AMEntityRegistry.FROSTSTALKER, RenderFroststalker::new);
        EntityRenderers.register(AMEntityRegistry.ICE_SHARD, RenderIceShard::new);
        EntityRenderers.register(AMEntityRegistry.TUSKLIN, RenderTusklin::new);
        EntityRenderers.register(AMEntityRegistry.LAVIATHAN, RenderLaviathan::new);
        EntityRenderers.register(AMEntityRegistry.COSMAW, RenderCosmaw::new);
        EntityRenderers.register(AMEntityRegistry.TOUCAN, RenderToucan::new);
        EntityRenderers.register(AMEntityRegistry.MANED_WOLF, RenderManedWolf::new);
        EntityRenderers.register(AMEntityRegistry.ANACONDA, RenderAnaconda::new);
        EntityRenderers.register(AMEntityRegistry.ANACONDA_PART, RenderAnacondaPart::new);
        EntityRenderers.register(AMEntityRegistry.VINE_LASSO, RenderVineLasso::new);
        EntityRenderers.register(AMEntityRegistry.ANTEATER, RenderAnteater::new);
        EntityRenderers.register(AMEntityRegistry.ROCKY_ROLLER, RenderRockyRoller::new);
        EntityRenderers.register(AMEntityRegistry.FLUTTER, RenderFlutter::new);
        EntityRenderers.register(AMEntityRegistry.POLLEN_BALL, RenderPollenBall::new);
        EntityRenderers.register(AMEntityRegistry.GELADA_MONKEY, RenderGeladaMonkey::new);
        EntityRenderers.register(AMEntityRegistry.JERBOA, RenderJerboa::new);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        try{
            ItemProperties.register(AMItemRegistry.BLOOD_SPRAYER, new ResourceLocation("empty"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return !ItemBloodSprayer.isUsable(stack) || p_239428_2_ instanceof Player && ((Player) p_239428_2_).getCooldowns().isOnCooldown(AMItemRegistry.BLOOD_SPRAYER) ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.HEMOLYMPH_BLASTER, new ResourceLocation("empty"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return !ItemHemolymphBlaster.isUsable(stack) || p_239428_2_ instanceof Player && ((Player) p_239428_2_).getCooldowns().isOnCooldown(AMItemRegistry.HEMOLYMPH_BLASTER) ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.TARANTULA_HAWK_ELYTRA, new ResourceLocation("broken"), (stack, p_239428_1_, p_239428_2_, j) -> {
                return ItemTarantulaHawkElytra.isUsable(stack) ? 0.0F : 1.0F;
            });
            ItemProperties.register(AMItemRegistry.SHIELD_OF_THE_DEEP, new ResourceLocation("blocking"), (stack, p_239421_1_, p_239421_2_, j) -> {
                return p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == stack ? 1.0F : 0.0F;
            });
        }catch (Exception e){
            AlexsMobs.LOGGER.warn("Could not load item models for weapons");
        }
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.BANANA_PEEL, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.CAPSID, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.VOID_WORM_BEAK, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(AMBlockRegistry.HUMMINGBIRD_FEEDER, RenderType.cutout());
        BlockEntityRenderers.register(AMTileEntityRegistry.CAPSID, RenderCapsid::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.VOID_WORM_BEAK, RenderVoidWormBeak::new);

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemColors(ColorHandlerEvent.Item event) {
        AlexsMobs.LOGGER.info("loaded in item colorizer");
        event.getItemColors().register((stack, colorIn) -> colorIn < 1 ? -1 : ((DyeableLeatherItem)stack.getItem()).getColor(stack), AMItemRegistry.STRADDLEBOARD);
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
}
