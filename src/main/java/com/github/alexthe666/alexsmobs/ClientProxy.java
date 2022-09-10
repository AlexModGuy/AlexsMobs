package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.ClientLayerRegistry;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.alexsmobs.client.gui.GUITransmutationTable;
import com.github.alexthe666.alexsmobs.client.particle.*;
import com.github.alexthe666.alexsmobs.client.render.*;
import com.github.alexthe666.alexsmobs.client.render.item.AMItemRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.CustomArmorRenderProperties;
import com.github.alexthe666.alexsmobs.client.render.item.GhostlyPickaxeBakedModel;
import com.github.alexthe666.alexsmobs.client.render.tile.*;
import com.github.alexthe666.alexsmobs.client.sound.SoundBearMusicBox;
import com.github.alexthe666.alexsmobs.client.sound.SoundLaCucaracha;
import com.github.alexthe666.alexsmobs.client.sound.SoundWormBoss;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.inventory.AMMenuRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemBloodSprayer;
import com.github.alexthe666.alexsmobs.item.ItemHemolymphBlaster;
import com.github.alexthe666.alexsmobs.item.ItemTarantulaHawkElytra;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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
    public static int voidPortalCreationTime = 0;
    public CameraType prevPOV = CameraType.FIRST_PERSON;
    public boolean initializedRainbowBuffers = false;
    private int pupfishChunkX = 0;
    private int pupfishChunkZ = 0;
    private ItemStack[] transmuteStacks = new ItemStack[3];

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        AlexsMobs.LOGGER.info("loaded in item colorizer");
        event.register((stack, colorIn) -> colorIn < 1 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack), AMItemRegistry.STRADDLEBOARD.get());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        AlexsMobs.LOGGER.info("loaded in block colorizer");
        event.register((state, tintGetter, pos, tint) -> {
            return tintGetter != null && pos != null ? RainbowUtil.calculateGlassColor(pos) : -1;
        }, AMBlockRegistry.RAINBOW_GLASS.get());
    }

    @OnlyIn(Dist.CLIENT)
    public static Callable<BlockEntityWithoutLevelRenderer> getTEISR() {
        return AMItemstackRenderer::new;
    }

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onBlockColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientLayerRegistry::onAddLayers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::setupParticles);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::onBakingCompleted);
    }

    public void clientInit() {
        initRainbowBuffers();
        ItemRenderer itemRendererIn = Minecraft.getInstance().getItemRenderer();
        EntityRenderers.register(AMEntityRegistry.GRIZZLY_BEAR.get(), RenderGrizzlyBear::new);
        EntityRenderers.register(AMEntityRegistry.ROADRUNNER.get(), RenderRoadrunner::new);
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT.get(), RenderBoneSerpent::new);
        EntityRenderers.register(AMEntityRegistry.BONE_SERPENT_PART.get(), RenderBoneSerpentPart::new);
        EntityRenderers.register(AMEntityRegistry.GAZELLE.get(), RenderGazelle::new);
        EntityRenderers.register(AMEntityRegistry.CROCODILE.get(), RenderCrocodile::new);
        EntityRenderers.register(AMEntityRegistry.FLY.get(), RenderFly::new);
        EntityRenderers.register(AMEntityRegistry.HUMMINGBIRD.get(), RenderHummingbird::new);
        EntityRenderers.register(AMEntityRegistry.ORCA.get(), RenderOrca::new);
        EntityRenderers.register(AMEntityRegistry.SUNBIRD.get(), RenderSunbird::new);
        EntityRenderers.register(AMEntityRegistry.GORILLA.get(), RenderGorilla::new);
        EntityRenderers.register(AMEntityRegistry.CRIMSON_MOSQUITO.get(), RenderCrimsonMosquito::new);
        EntityRenderers.register(AMEntityRegistry.MOSQUITO_SPIT.get(), RenderMosquitoSpit::new);
        EntityRenderers.register(AMEntityRegistry.RATTLESNAKE.get(), RenderRattlesnake::new);
        EntityRenderers.register(AMEntityRegistry.ENDERGRADE.get(), RenderEndergrade::new);
        EntityRenderers.register(AMEntityRegistry.HAMMERHEAD_SHARK.get(), RenderHammerheadShark::new);
        EntityRenderers.register(AMEntityRegistry.SHARK_TOOTH_ARROW.get(), RenderSharkToothArrow::new);
        EntityRenderers.register(AMEntityRegistry.LOBSTER.get(), RenderLobster::new);
        EntityRenderers.register(AMEntityRegistry.KOMODO_DRAGON.get(), RenderKomodoDragon::new);
        EntityRenderers.register(AMEntityRegistry.CAPUCHIN_MONKEY.get(), RenderCapuchinMonkey::new);
        EntityRenderers.register(AMEntityRegistry.TOSSED_ITEM.get(), RenderTossedItem::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_HEAD.get(), RenderCentipedeHead::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_BODY.get(), RenderCentipedeBody::new);
        EntityRenderers.register(AMEntityRegistry.CENTIPEDE_TAIL.get(), RenderCentipedeTail::new);
        EntityRenderers.register(AMEntityRegistry.WARPED_TOAD.get(), RenderWarpedToad::new);
        EntityRenderers.register(AMEntityRegistry.MOOSE.get(), RenderMoose::new);
        EntityRenderers.register(AMEntityRegistry.MIMICUBE.get(), RenderMimicube::new);
        EntityRenderers.register(AMEntityRegistry.RACCOON.get(), RenderRaccoon::new);
        EntityRenderers.register(AMEntityRegistry.BLOBFISH.get(), RenderBlobfish::new);
        EntityRenderers.register(AMEntityRegistry.SEAL.get(), RenderSeal::new);
        EntityRenderers.register(AMEntityRegistry.COCKROACH.get(), RenderCockroach::new);
        EntityRenderers.register(AMEntityRegistry.COCKROACH_EGG.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.SHOEBILL.get(), RenderShoebill::new);
        EntityRenderers.register(AMEntityRegistry.ELEPHANT.get(), RenderElephant::new);
        EntityRenderers.register(AMEntityRegistry.SOUL_VULTURE.get(), RenderSoulVulture::new);
        EntityRenderers.register(AMEntityRegistry.SNOW_LEOPARD.get(), RenderSnowLeopard::new);
        EntityRenderers.register(AMEntityRegistry.SPECTRE.get(), RenderSpectre::new);
        EntityRenderers.register(AMEntityRegistry.CROW.get(), RenderCrow::new);
        EntityRenderers.register(AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.get(), RenderAlligatorSnappingTurtle::new);
        EntityRenderers.register(AMEntityRegistry.MUNGUS.get(), RenderMungus::new);
        EntityRenderers.register(AMEntityRegistry.MANTIS_SHRIMP.get(), RenderMantisShrimp::new);
        EntityRenderers.register(AMEntityRegistry.GUSTER.get(), RenderGuster::new);
        EntityRenderers.register(AMEntityRegistry.SAND_SHOT.get(), RenderSandShot::new);
        EntityRenderers.register(AMEntityRegistry.GUST.get(), RenderGust::new);
        EntityRenderers.register(AMEntityRegistry.WARPED_MOSCO.get(), RenderWarpedMosco::new);
        EntityRenderers.register(AMEntityRegistry.HEMOLYMPH.get(), RenderHemolymph::new);
        EntityRenderers.register(AMEntityRegistry.STRADDLER.get(), RenderStraddler::new);
        EntityRenderers.register(AMEntityRegistry.STRADPOLE.get(), RenderStradpole::new);
        EntityRenderers.register(AMEntityRegistry.STRADDLEBOARD.get(), RenderStraddleboard::new);
        EntityRenderers.register(AMEntityRegistry.EMU.get(), RenderEmu::new);
        EntityRenderers.register(AMEntityRegistry.EMU_EGG.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.PLATYPUS.get(), RenderPlatypus::new);
        EntityRenderers.register(AMEntityRegistry.DROPBEAR.get(), RenderDropBear::new);
        EntityRenderers.register(AMEntityRegistry.TASMANIAN_DEVIL.get(), RenderTasmanianDevil::new);
        EntityRenderers.register(AMEntityRegistry.KANGAROO.get(), RenderKangaroo::new);
        EntityRenderers.register(AMEntityRegistry.CACHALOT_WHALE.get(), RenderCachalotWhale::new);
        EntityRenderers.register(AMEntityRegistry.CACHALOT_ECHO.get(), RenderCachalotEcho::new);
        EntityRenderers.register(AMEntityRegistry.LEAFCUTTER_ANT.get(), RenderLeafcutterAnt::new);
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE.get(), RenderEnderiophage::new);
        EntityRenderers.register(AMEntityRegistry.ENDERIOPHAGE_ROCKET.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        EntityRenderers.register(AMEntityRegistry.BALD_EAGLE.get(), RenderBaldEagle::new);
        EntityRenderers.register(AMEntityRegistry.TIGER.get(), RenderTiger::new);
        EntityRenderers.register(AMEntityRegistry.TARANTULA_HAWK.get(), RenderTarantulaHawk::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM.get(), RenderVoidWormHead::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_PART.get(), RenderVoidWormBody::new);
        EntityRenderers.register(AMEntityRegistry.VOID_WORM_SHOT.get(), RenderVoidWormShot::new);
        EntityRenderers.register(AMEntityRegistry.VOID_PORTAL.get(), RenderVoidPortal::new);
        EntityRenderers.register(AMEntityRegistry.FRILLED_SHARK.get(), RenderFrilledShark::new);
        EntityRenderers.register(AMEntityRegistry.MIMIC_OCTOPUS.get(), RenderMimicOctopus::new);
        EntityRenderers.register(AMEntityRegistry.SEAGULL.get(), RenderSeagull::new);
        EntityRenderers.register(AMEntityRegistry.FROSTSTALKER.get(), RenderFroststalker::new);
        EntityRenderers.register(AMEntityRegistry.ICE_SHARD.get(), RenderIceShard::new);
        EntityRenderers.register(AMEntityRegistry.TUSKLIN.get(), RenderTusklin::new);
        EntityRenderers.register(AMEntityRegistry.LAVIATHAN.get(), RenderLaviathan::new);
        EntityRenderers.register(AMEntityRegistry.COSMAW.get(), RenderCosmaw::new);
        EntityRenderers.register(AMEntityRegistry.TOUCAN.get(), RenderToucan::new);
        EntityRenderers.register(AMEntityRegistry.MANED_WOLF.get(), RenderManedWolf::new);
        EntityRenderers.register(AMEntityRegistry.ANACONDA.get(), RenderAnaconda::new);
        EntityRenderers.register(AMEntityRegistry.ANACONDA_PART.get(), RenderAnacondaPart::new);
        EntityRenderers.register(AMEntityRegistry.VINE_LASSO.get(), RenderVineLasso::new);
        EntityRenderers.register(AMEntityRegistry.ANTEATER.get(), RenderAnteater::new);
        EntityRenderers.register(AMEntityRegistry.ROCKY_ROLLER.get(), RenderRockyRoller::new);
        EntityRenderers.register(AMEntityRegistry.FLUTTER.get(), RenderFlutter::new);
        EntityRenderers.register(AMEntityRegistry.POLLEN_BALL.get(), RenderPollenBall::new);
        EntityRenderers.register(AMEntityRegistry.GELADA_MONKEY.get(), RenderGeladaMonkey::new);
        EntityRenderers.register(AMEntityRegistry.JERBOA.get(), RenderJerboa::new);
        EntityRenderers.register(AMEntityRegistry.TERRAPIN.get(), RenderTerrapin::new);
        EntityRenderers.register(AMEntityRegistry.COMB_JELLY.get(), RenderCombJelly::new);
        EntityRenderers.register(AMEntityRegistry.COSMIC_COD.get(), RenderCosmicCod::new);
        EntityRenderers.register(AMEntityRegistry.BUNFUNGUS.get(), RenderBunfungus::new);
        EntityRenderers.register(AMEntityRegistry.BISON.get(), RenderBison::new);
        EntityRenderers.register(AMEntityRegistry.GIANT_SQUID.get(), RenderGiantSquid::new);
        EntityRenderers.register(AMEntityRegistry.SQUID_GRAPPLE.get(), RenderSquidGrapple::new);
        EntityRenderers.register(AMEntityRegistry.SEA_BEAR.get(), RenderSeaBear::new);
        EntityRenderers.register(AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(), RenderDevilsHolePupfish::new);
        EntityRenderers.register(AMEntityRegistry.CATFISH.get(), RenderCatfish::new);
        EntityRenderers.register(AMEntityRegistry.FLYING_FISH.get(), RenderFlyingFish::new);
        EntityRenderers.register(AMEntityRegistry.SKELEWAG.get(), RenderSkelewag::new);
        EntityRenderers.register(AMEntityRegistry.RAIN_FROG.get(), RenderRainFrog::new);
        EntityRenderers.register(AMEntityRegistry.POTOO.get(), RenderPotoo::new);
        EntityRenderers.register(AMEntityRegistry.MUDSKIPPER.get(), RenderMudskipper::new);
        EntityRenderers.register(AMEntityRegistry.MUD_BALL.get(), RenderMudBall::new);
        EntityRenderers.register(AMEntityRegistry.RHINOCEROS.get(), RenderRhinoceros::new);
        EntityRenderers.register(AMEntityRegistry.SUGAR_GLIDER.get(), RenderSugarGlider::new);
        EntityRenderers.register(AMEntityRegistry.FARSEER.get(), RenderFarseer::new);
        EntityRenderers.register(AMEntityRegistry.SKREECHER.get(), RenderSkreecher::new);
        EntityRenderers.register(AMEntityRegistry.UNDERMINER.get(), RenderUnderminer::new);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        try {
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
            ItemProperties.register(AMItemRegistry.SOMBRERO.get(), new ResourceLocation("silly"), (stack, p_239421_1_, p_239421_2_, j) -> {
                return AlexsMobs.isAprilFools() ? 1.0F : 0.0F;
            });
            ItemProperties.register(AMItemRegistry.PUPFISH_LOCATOR.get(), new ResourceLocation("in_chunk"), (stack, world, entity, j) -> {
                int x = pupfishChunkX * 16;
                int z = pupfishChunkZ * 16;
                if (entity != null && entity.getX() >= x && entity.getX() <= x + 16 && entity.getZ() >= z && entity.getZ() <= z + 16) {
                    return 1.0F;
                }
                return 0.0F;
            });
            ItemProperties.register(AMItemRegistry.SKELEWAG_SWORD.get(), new ResourceLocation("blocking"), (stack, p_239421_1_, p_239421_2_, j) -> {
                return p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == stack ? 1.0F : 0.0F;
            });
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not load item models for weapons");
        }
        BlockEntityRenderers.register(AMTileEntityRegistry.CAPSID.get(), RenderCapsid::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.VOID_WORM_BEAK.get(), RenderVoidWormBeak::new);
        BlockEntityRenderers.register(AMTileEntityRegistry.TRANSMUTATION_TABLE.get(), RenderTransmutationTable::new);
        MenuScreens.register(AMMenuRegistry.TRANSMUTATION_TABLE.get(), GUITransmutationTable::new);
    }

    private void initRainbowBuffers() {
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.COMBJELLY_RAINBOW_GLINT, new BufferBuilder(AMRenderTypes.COMBJELLY_RAINBOW_GLINT.bufferSize()));
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.VOID_WORM_PORTAL_OVERLAY, new BufferBuilder(AMRenderTypes.VOID_WORM_PORTAL_OVERLAY.bufferSize()));
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.STATIC_PORTAL, new BufferBuilder(AMRenderTypes.STATIC_PORTAL.bufferSize()));
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.STATIC_PARTICLE, new BufferBuilder(AMRenderTypes.STATIC_PARTICLE.bufferSize()));
        Minecraft.getInstance().renderBuffers().fixedBuffers.put(AMRenderTypes.STATIC_ENTITY, new BufferBuilder(AMRenderTypes.STATIC_ENTITY.bufferSize()));
        initializedRainbowBuffers = true;
    }

    private static void onBakingCompleted(final ModelEvent.BakingCompleted e) {
        String ghostlyPickaxe = "alexsmobs:ghostly_pickaxe";
        for (ResourceLocation id : e.getModels().keySet()) {
            if (id.toString().contains(ghostlyPickaxe)) {
                e.getModels().put(id, new GhostlyPickaxeBakedModel(e.getModels().get(id)));
                System.out.println(e.getModels().get(id));
            }
        }
    }

    public void openBookGUI(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreen(new GUIAnimalDictionary(itemStackIn));
    }

    public void openBookGUI(ItemStack itemStackIn, String page) {
        Minecraft.getInstance().setScreen(new GUIAnimalDictionary(itemStackIn, page));
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
        if (entity instanceof EntityCockroach && entity.isAlive() && updateKind == 67) {
            SoundLaCucaracha sound;
            if (COCKROACH_SOUND_MAP.get(entity.getId()) == null) {
                sound = new SoundLaCucaracha((EntityCockroach) entity);
                COCKROACH_SOUND_MAP.put(entity.getId(), sound);
            } else {
                sound = COCKROACH_SOUND_MAP.get(entity.getId());
            }
            if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyCockroach()) {
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        }
        if (entity instanceof EntityVoidWorm && entity.isAlive() && updateKind == 67) {
            float f2 = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
            if (f2 <= 0) {
                WORMBOSS_SOUND_MAP.clear();
            } else {
                SoundWormBoss sound;
                if (WORMBOSS_SOUND_MAP.get(entity.getId()) == null) {
                    sound = new SoundWormBoss((EntityVoidWorm) entity);
                    WORMBOSS_SOUND_MAP.put(entity.getId(), sound);
                } else {
                    sound = WORMBOSS_SOUND_MAP.get(entity.getId());
                }
                if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.isNearest()) {
                    Minecraft.getInstance().getSoundManager().play(sound);
                }
            }
        }
        if (entity instanceof EntityGrizzlyBear && entity.isAlive() && updateKind == 67) {
            SoundBearMusicBox sound;
            if (BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId()) == null) {
                sound = new SoundBearMusicBox((EntityGrizzlyBear) entity);
                BEAR_MUSIC_BOX_SOUND_MAP.put(entity.getId(), sound);
            } else {
                sound = BEAR_MUSIC_BOX_SOUND_MAP.get(entity.getId());
            }
            if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.canPlaySound() && sound.isOnlyMusicBox()) {
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        }
    }

    public void updateBiomeVisuals(int x, int z) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty(x - 32, 0, x - 32, z + 32, 255, z + 32);
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        AlexsMobs.LOGGER.debug("Registered particle factories");
        registry.register(AMParticleRegistry.GUSTER_SAND_SPIN.get(), ParticleGusterSandSpin.Factory::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SHOT.get(), ParticleGusterSandShot.Factory::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SPIN_RED.get(), ParticleGusterSandSpin.FactoryRed::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SHOT_RED.get(), ParticleGusterSandShot.FactoryRed::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SPIN_SOUL.get(), ParticleGusterSandSpin.FactorySoul::new);
        registry.register(AMParticleRegistry.GUSTER_SAND_SHOT_SOUL.get(), ParticleGusterSandShot.FactorySoul::new);
        registry.register(AMParticleRegistry.HEMOLYMPH.get(), ParticleHemolymph.Factory::new);
        registry.register(AMParticleRegistry.PLATYPUS_SENSE.get(), ParticlePlatypus.Factory::new);
        registry.register(AMParticleRegistry.WHALE_SPLASH.get(), ParticleWhaleSplash.Factory::new);
        registry.register(AMParticleRegistry.DNA.get(), ParticleDna.Factory::new);
        registry.register(AMParticleRegistry.SHOCKED.get(), ParticleSimpleHeart.Factory::new);
        registry.register(AMParticleRegistry.WORM_PORTAL.get(), ParticleWormPortal.Factory::new);
        registry.register(AMParticleRegistry.INVERT_DIG.get(), ParticleInvertDig.Factory::new);
        registry.register(AMParticleRegistry.TEETH_GLINT.get(), ParticleTeethGlint.Factory::new);
        registry.register(AMParticleRegistry.SMELLY.get(), ParticleSmelly.Factory::new);
        registry.register(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(), ParticleBunfungusTransformation.Factory::new);
        registry.register(AMParticleRegistry.FUNGUS_BUBBLE.get(), ParticleFungusBubble.Factory::new);
        registry.register(AMParticleRegistry.BEAR_FREDDY.get(), new ParticleBearFreddy.Factory());
        registry.register(AMParticleRegistry.SUNBIRD_FEATHER.get(), ParticleSunbirdFeather.Factory::new);
        registry.register(AMParticleRegistry.STATIC_SPARK.get(), new ParticleStaticSpark.Factory());
        registry.register(AMParticleRegistry.SKULK_BOOM.get(), new ParticleSkulkBoom.Factory());
    }


    public void setRenderViewEntity(Entity entity) {
        prevPOV = Minecraft.getInstance().options.getCameraType();
        Minecraft.getInstance().setCameraEntity(entity);
        Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    public void resetRenderViewEntity() {
        Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
    }

    public int getPreviousPOV() {
        return prevPOV.ordinal();
    }

    public boolean isFarFromCamera(double x, double y, double z) {
        Minecraft lvt_1_1_ = Minecraft.getInstance();
        return lvt_1_1_.gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z) >= 256.0D;
    }

    public void resetVoidPortalCreation(Player player) {

    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterEntityRenders(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @Override
    public Object getISTERProperties() {
        return new AMItemRenderProperties();
    }

    @Override
    public Object getArmorRenderProperties() {
        return new CustomArmorRenderProperties();
    }

    public void spawnSpecialParticle(int type) {
        if (type == 0) {
            Minecraft.getInstance().level.addParticle(AMParticleRegistry.BEAR_FREDDY.get(), Minecraft.getInstance().player.getX(), Minecraft.getInstance().player.getY(), Minecraft.getInstance().player.getZ(), 0, 0, 0);
        }
    }

    public void processVisualFlag(Entity entity, int flag) {
        if (entity == Minecraft.getInstance().player && flag == 87) {
            ClientEvents.renderStaticScreenFor = 60;
        }
    }

    public void setPupfishChunkForItem(int chunkX, int chunkZ) {
        this.pupfishChunkX = chunkX;
        this.pupfishChunkZ = chunkZ;
    }

    public void setDisplayTransmuteResult(int slot, ItemStack stack){
        transmuteStacks[Mth.clamp(slot, 0, 2)] = stack;
    }

    public ItemStack getDisplayTransmuteResult(int slot){
        ItemStack stack = transmuteStacks[Mth.clamp(slot, 0, 2)];
        return stack == null ? ItemStack.EMPTY : stack;
    }
}
