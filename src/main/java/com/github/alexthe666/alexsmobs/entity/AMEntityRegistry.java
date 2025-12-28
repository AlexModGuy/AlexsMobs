package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Predicate;

@EventBusSubscriber(modid = AlexsMobs.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AMEntityRegistry {

    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(Registries.ENTITY_TYPE, AlexsMobs.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGrizzlyBear>> GRIZZLY_BEAR = DEF_REG.register("grizzly_bear", () -> registerEntity(EntityType.Builder.of(EntityGrizzlyBear::new, MobCategory.CREATURE).sized(1.6F, 1.8F).setTrackingRange(10), "grizzly_bear"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRoadrunner>> ROADRUNNER = DEF_REG.register("roadrunner", () -> registerEntity(EntityType.Builder.of(EntityRoadrunner::new, MobCategory.CREATURE).sized(0.45F, 0.75F).setTrackingRange(10), "roadrunner"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBoneSerpent>> BONE_SERPENT = DEF_REG.register("bone_serpent", () -> registerEntity(EntityType.Builder.of(EntityBoneSerpent::new, MobCategory.MONSTER).sized(1.2F, 1.15F).fireImmune().setTrackingRange(10), "bone_serpent"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBoneSerpentPart>> BONE_SERPENT_PART = DEF_REG.register("bone_serpent_part", () -> registerEntity(EntityType.Builder.of(EntityBoneSerpentPart::new, MobCategory.MONSTER).sized(1F, 1F).fireImmune().setTrackingRange(10), "bone_serpent_part"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGazelle>> GAZELLE = DEF_REG.register("gazelle", () -> registerEntity(EntityType.Builder.of(EntityGazelle::new, MobCategory.CREATURE).sized(0.85F, 1.25F).setTrackingRange(10), "gazelle"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCrocodile>> CROCODILE = DEF_REG.register("crocodile", () -> registerEntity(EntityType.Builder.of(EntityCrocodile::new, MobCategory.CREATURE).sized(2.15F, 0.75F).setTrackingRange(10), "crocodile"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFly>> FLY = DEF_REG.register("fly", () -> registerEntity(EntityType.Builder.of(EntityFly::new, MobCategory.AMBIENT).sized(0.35F, 0.35F).setTrackingRange(4), "fly"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHummingbird>> HUMMINGBIRD = DEF_REG.register("hummingbird", () -> registerEntity(EntityType.Builder.of(EntityHummingbird::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(5), "hummingbird"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOrca>> ORCA = DEF_REG.register("orca", () -> registerEntity(EntityType.Builder.of(EntityOrca::new, MobCategory.WATER_CREATURE).sized(3.75F, 1.75F).setTrackingRange(10), "orca"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySunbird>> SUNBIRD = DEF_REG.register("sunbird", () -> registerEntity(EntityType.Builder.of(EntitySunbird::new, MobCategory.CREATURE).sized(2.75F, 1.5F).fireImmune().setTrackingRange(12).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "sunbird"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGorilla>> GORILLA = DEF_REG.register("gorilla", () -> registerEntity(EntityType.Builder.of(EntityGorilla::new, MobCategory.CREATURE).sized(1.15F, 1.35F).setTrackingRange(10), "gorilla"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCrimsonMosquito>> CRIMSON_MOSQUITO = DEF_REG.register("crimson_mosquito", () -> registerEntity(EntityType.Builder.of(EntityCrimsonMosquito::new, MobCategory.MONSTER).sized(1.25F, 1.15F).fireImmune().setTrackingRange(8), "crimson_mosquito"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMosquitoSpit>> MOSQUITO_SPIT = DEF_REG.register("mosquito_spit", () -> registerEntity(EntityType.Builder.of(EntityMosquitoSpit::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "mosquito_spit"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRattlesnake>> RATTLESNAKE = DEF_REG.register("rattlesnake", () -> registerEntity(EntityType.Builder.of(EntityRattlesnake::new, MobCategory.CREATURE).sized(0.95F, 0.35F).setTrackingRange(10), "rattlesnake"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEndergrade>> ENDERGRADE = DEF_REG.register("endergrade", () -> registerEntity(EntityType.Builder.of(EntityEndergrade::new, MobCategory.CREATURE).sized(0.95F, 0.85F).setTrackingRange(10), "endergrade"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHammerheadShark>> HAMMERHEAD_SHARK = DEF_REG.register("hammerhead_shark", () -> registerEntity(EntityType.Builder.of(EntityHammerheadShark::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.25F).setTrackingRange(10), "hammerhead_shark"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySharkToothArrow>> SHARK_TOOTH_ARROW = DEF_REG.register("shark_tooth_arrow", () -> registerEntity(EntityType.Builder.of(EntitySharkToothArrow::new, MobCategory.MISC).sized(0.5F, 0.5F), "shark_tooth_arrow"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLobster>> LOBSTER = DEF_REG.register("lobster", () -> registerEntity(EntityType.Builder.of(EntityLobster::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).setTrackingRange(5), "lobster"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityKomodoDragon>> KOMODO_DRAGON = DEF_REG.register("komodo_dragon", () -> registerEntity(EntityType.Builder.of(EntityKomodoDragon::new, MobCategory.CREATURE).sized(1.9F, 0.9F).setTrackingRange(10), "komodo_dragon"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCapuchinMonkey>> CAPUCHIN_MONKEY = DEF_REG.register("capuchin_monkey", () -> registerEntity(EntityType.Builder.of(EntityCapuchinMonkey::new, MobCategory.CREATURE).sized(0.65F, 0.75F).setTrackingRange(10), "capuchin_monkey"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTossedItem>> TOSSED_ITEM = DEF_REG.register("tossed_item", () -> registerEntity(EntityType.Builder.of(EntityTossedItem::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "tossed_item"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCentipedeHead>> CENTIPEDE_HEAD = DEF_REG.register("centipede_head", () -> registerEntity(EntityType.Builder.of(EntityCentipedeHead::new, MobCategory.MONSTER).sized(0.9F, 0.9F).setTrackingRange(8), "centipede_head"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCentipedeBody>> CENTIPEDE_BODY = DEF_REG.register("centipede_body", () -> registerEntity(EntityType.Builder.of(EntityCentipedeBody::new, MobCategory.MISC).sized(0.9F, 0.9F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(8), "centipede_body"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCentipedeTail>> CENTIPEDE_TAIL = DEF_REG.register("centipede_tail", () -> registerEntity(EntityType.Builder.of(EntityCentipedeTail::new, MobCategory.MISC).sized(0.9F, 0.9F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(8), "centipede_tail"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWarpedToad>> WARPED_TOAD = DEF_REG.register("warped_toad", () -> registerEntity(EntityType.Builder.of(EntityWarpedToad::new, MobCategory.CREATURE).sized(0.9F, 1.4F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(10), "warped_toad"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMoose>> MOOSE = DEF_REG.register("moose", () -> registerEntity(EntityType.Builder.of(EntityMoose::new, MobCategory.CREATURE).sized(1.7F, 2.4F).setTrackingRange(10), "moose"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMimicube>> MIMICUBE = DEF_REG.register("mimicube", () -> registerEntity(EntityType.Builder.of(EntityMimicube::new, MobCategory.MONSTER).sized(0.9F, 0.9F).setTrackingRange(8), "mimicube"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRaccoon>> RACCOON = DEF_REG.register("raccoon", () -> registerEntity(EntityType.Builder.of(EntityRaccoon::new, MobCategory.CREATURE).sized(0.8F, 0.9F).setTrackingRange(10), "raccoon"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBlobfish>> BLOBFISH = DEF_REG.register("blobfish", () -> registerEntity(EntityType.Builder.of(EntityBlobfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.45F).setTrackingRange(5), "blobfish"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySeal>> SEAL = DEF_REG.register("seal", () -> registerEntity(EntityType.Builder.of(EntitySeal::new, MobCategory.CREATURE).sized(1.45F, 0.9F).setTrackingRange(10), "seal"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCockroach>> COCKROACH = DEF_REG.register("cockroach", () -> registerEntity(EntityType.Builder.of(EntityCockroach::new, MobCategory.AMBIENT).sized(0.7F, 0.3F).setTrackingRange(5), "cockroach"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCockroachEgg>> COCKROACH_EGG = DEF_REG.register("cockroach_egg", () -> registerEntity(EntityType.Builder.of(EntityCockroachEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "cockroach_egg"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityShoebill>> SHOEBILL = DEF_REG.register("shoebill", () -> registerEntity(EntityType.Builder.of(EntityShoebill::new, MobCategory.CREATURE).sized(0.8F, 1.5F).setUpdateInterval(1).setTrackingRange(10), "shoebill"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityElephant>> ELEPHANT = DEF_REG.register("elephant", () -> registerEntity(EntityType.Builder.of(EntityElephant::new, MobCategory.CREATURE).sized(3.1F, 3.5F).setUpdateInterval(1).setTrackingRange(10), "elephant"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySoulVulture>> SOUL_VULTURE = DEF_REG.register("soul_vulture", () -> registerEntity(EntityType.Builder.of(EntitySoulVulture::new, MobCategory.MONSTER).sized(0.9F, 1.3F).setUpdateInterval(1).fireImmune().setTrackingRange(8), "soul_vulture"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySnowLeopard>> SNOW_LEOPARD = DEF_REG.register("snow_leopard", () -> registerEntity(EntityType.Builder.of(EntitySnowLeopard::new, MobCategory.CREATURE).sized(1.2F, 1.3F).immuneTo(Blocks.POWDER_SNOW).setTrackingRange(10), "snow_leopard"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpectre>> SPECTRE = DEF_REG.register("spectre", () -> registerEntity(EntityType.Builder.of(EntitySpectre::new, MobCategory.CREATURE).sized(3.15F, 0.8F).fireImmune().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "spectre"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCrow>> CROW = DEF_REG.register("crow", () -> registerEntity(EntityType.Builder.of(EntityCrow::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(10), "crow"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAlligatorSnappingTurtle>> ALLIGATOR_SNAPPING_TURTLE = DEF_REG.register("alligator_snapping_turtle", () -> registerEntity(EntityType.Builder.of(EntityAlligatorSnappingTurtle::new, MobCategory.CREATURE).sized(1.25F, 0.65F).setTrackingRange(10), "alligator_snapping_turtle"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMungus>> MUNGUS = DEF_REG.register("mungus", () -> registerEntity(EntityType.Builder.of(EntityMungus::new, MobCategory.CREATURE).sized(0.75F, 1.45F).setTrackingRange(10), "mungus"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMantisShrimp>> MANTIS_SHRIMP = DEF_REG.register("mantis_shrimp", () -> registerEntity(EntityType.Builder.of(EntityMantisShrimp::new, MobCategory.WATER_CREATURE).sized(1.25F, 1.2F).setTrackingRange(10), "mantis_shrimp"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGuster>> GUSTER = DEF_REG.register("guster", () -> registerEntity(EntityType.Builder.of(EntityGuster::new, MobCategory.MONSTER).sized(1.42F, 2.35F).fireImmune().setTrackingRange(8), "guster"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySandShot>> SAND_SHOT = DEF_REG.register("sand_shot", () -> registerEntity(EntityType.Builder.of(EntitySandShot::new, MobCategory.MISC).sized(0.95F, 0.65F).fireImmune(), "sand_shot"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGust>> GUST = DEF_REG.register("gust", () -> registerEntity(EntityType.Builder.of(EntityGust::new, MobCategory.MISC).sized(0.8F, 0.8F).fireImmune(), "gust"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWarpedMosco>> WARPED_MOSCO = DEF_REG.register("warped_mosco", () -> registerEntity(EntityType.Builder.of(EntityWarpedMosco::new, MobCategory.MONSTER).sized(1.99F, 3.25F).fireImmune().setTrackingRange(10), "warped_mosco"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityHemolymph>> HEMOLYMPH = DEF_REG.register("hemolymph", () -> registerEntity(EntityType.Builder.of(EntityHemolymph::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "hemolymph"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityStraddler>> STRADDLER = DEF_REG.register("straddler", () -> registerEntity(EntityType.Builder.of(EntityStraddler::new, MobCategory.MONSTER).sized(1.65F, 3F).fireImmune().setTrackingRange(8), "straddler"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityStradpole>> STRADPOLE = DEF_REG.register("stradpole", () -> registerEntity(EntityType.Builder.of(EntityStradpole::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.5F).fireImmune().setTrackingRange(4), "stradpole"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityStraddleboard>> STRADDLEBOARD = DEF_REG.register("straddleboard", () -> registerEntity(EntityType.Builder.of(EntityStraddleboard::new, MobCategory.MISC).sized(1.5F, 0.35F).fireImmune().setUpdateInterval(1).clientTrackingRange(10).setShouldReceiveVelocityUpdates(true), "straddleboard"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEmu>> EMU = DEF_REG.register("emu", () -> registerEntity(EntityType.Builder.of(EntityEmu::new, MobCategory.CREATURE).sized(1.1F, 1.8F).setTrackingRange(10), "emu"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEmuEgg>> EMU_EGG = DEF_REG.register("emu_egg", () -> registerEntity(EntityType.Builder.of(EntityEmuEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "emu_egg"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityPlatypus>> PLATYPUS = DEF_REG.register("platypus", () -> registerEntity(EntityType.Builder.of(EntityPlatypus::new, MobCategory.CREATURE).sized(0.8F, 0.5F).setTrackingRange(10), "platypus"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDropBear>> DROPBEAR = DEF_REG.register("dropbear", () -> registerEntity(EntityType.Builder.of(EntityDropBear::new, MobCategory.MONSTER).sized(1.65F, 1.5F).fireImmune().setTrackingRange(8), "dropbear"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTasmanianDevil>> TASMANIAN_DEVIL = DEF_REG.register("tasmanian_devil", () -> registerEntity(EntityType.Builder.of(EntityTasmanianDevil::new, MobCategory.CREATURE).sized(0.7F, 0.8F).setTrackingRange(10), "tasmanian_devil"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityKangaroo>> KANGAROO = DEF_REG.register("kangaroo", () -> registerEntity(EntityType.Builder.of(EntityKangaroo::new, MobCategory.CREATURE).sized(1.65F, 1.5F).setTrackingRange(10), "kangaroo"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCachalotWhale>> CACHALOT_WHALE = DEF_REG.register("cachalot_whale", () -> registerEntity(EntityType.Builder.of(EntityCachalotWhale::new, MobCategory.WATER_CREATURE).sized(9F, 4.0F).setTrackingRange(10), "cachalot_whale"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCachalotEcho>> CACHALOT_ECHO = DEF_REG.register("cachalot_echo", () -> registerEntity(EntityType.Builder.of(EntityCachalotEcho::new, MobCategory.MISC).sized(2F, 2F).fireImmune(), "cachalot_echo"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLeafcutterAnt>> LEAFCUTTER_ANT = DEF_REG.register("leafcutter_ant", () -> registerEntity(EntityType.Builder.of(EntityLeafcutterAnt::new, MobCategory.CREATURE).sized(0.8F, 0.5F).setTrackingRange(5), "leafcutter_ant"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEnderiophage>> ENDERIOPHAGE = DEF_REG.register("enderiophage", () -> registerEntity(EntityType.Builder.of(EntityEnderiophage::new, MobCategory.CREATURE).sized(0.85F, 1.95F).setUpdateInterval(1).setTrackingRange(8), "enderiophage"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEnderiophageRocket>> ENDERIOPHAGE_ROCKET = DEF_REG.register("enderiophage_rocket", () -> registerEntity(EntityType.Builder.of(EntityEnderiophageRocket::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "enderiophage_rocket"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBaldEagle>> BALD_EAGLE = DEF_REG.register("bald_eagle", () -> registerEntity(EntityType.Builder.of(EntityBaldEagle::new, MobCategory.CREATURE).sized(0.5F, 0.95F).setUpdateInterval(1).setTrackingRange(14), "bald_eagle"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTiger>> TIGER = DEF_REG.register("tiger", () -> registerEntity(EntityType.Builder.of(EntityTiger::new, MobCategory.CREATURE).sized(1.45F, 1.2F).setTrackingRange(10), "tiger"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTarantulaHawk>> TARANTULA_HAWK = DEF_REG.register("tarantula_hawk", () -> registerEntity(EntityType.Builder.of(EntityTarantulaHawk::new, MobCategory.CREATURE).sized(1.2F, 0.9F).setTrackingRange(10), "tarantula_hawk"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVoidWorm>> VOID_WORM = DEF_REG.register("void_worm", () -> registerEntity(EntityType.Builder.of(EntityVoidWorm::new, MobCategory.MONSTER).sized(3.4F, 3F).fireImmune().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVoidWormPart>> VOID_WORM_PART = DEF_REG.register("void_worm_part", () -> registerEntity(EntityType.Builder.of(EntityVoidWormPart::new, MobCategory.MONSTER).sized(1.2F, 1.35F).fireImmune().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm_part"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVoidWormShot>> VOID_WORM_SHOT = DEF_REG.register("void_worm_shot", () -> registerEntity(EntityType.Builder.of(EntityVoidWormShot::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "void_worm_shot"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVoidPortal>> VOID_PORTAL = DEF_REG.register("void_portal", () -> registerEntity(EntityType.Builder.of(EntityVoidPortal::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "void_portal"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFrilledShark>> FRILLED_SHARK = DEF_REG.register("frilled_shark", () -> registerEntity(EntityType.Builder.of(EntityFrilledShark::new, MobCategory.WATER_CREATURE).sized(1.3F, 0.4F).setTrackingRange(8), "frilled_shark"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMimicOctopus>> MIMIC_OCTOPUS = DEF_REG.register("mimic_octopus", () -> registerEntity(EntityType.Builder.of(EntityMimicOctopus::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F).setTrackingRange(8), "mimic_octopus"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySeagull>> SEAGULL = DEF_REG.register("seagull", () -> registerEntity(EntityType.Builder.of(EntitySeagull::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(10), "seagull"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFroststalker>> FROSTSTALKER = DEF_REG.register("froststalker", () -> registerEntity(EntityType.Builder.of(EntityFroststalker::new, MobCategory.CREATURE).sized(0.95F, 1.15F).immuneTo(Blocks.POWDER_SNOW), "froststalker"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityIceShard>> ICE_SHARD = DEF_REG.register("ice_shard", () -> registerEntity(EntityType.Builder.of(EntityIceShard::new, MobCategory.MISC).sized(0.45F, 0.45F).fireImmune(), "ice_shard"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTusklin>> TUSKLIN = DEF_REG.register("tusklin", () -> registerEntity(EntityType.Builder.of(EntityTusklin::new, MobCategory.CREATURE).sized(2.2F, 1.9F).immuneTo(Blocks.POWDER_SNOW).setTrackingRange(10), "tusklin"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityLaviathan>> LAVIATHAN = DEF_REG.register("laviathan", () -> registerEntity(EntityType.Builder.of(EntityLaviathan::new, MobCategory.CREATURE).sized(3.3F, 2.4F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(10), "laviathan"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCosmaw>> COSMAW = DEF_REG.register("cosmaw", () -> registerEntity(EntityType.Builder.of(EntityCosmaw::new, MobCategory.CREATURE).sized(1.95F, 1.8F).setTrackingRange(10), "cosmaw"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityToucan>> TOUCAN = DEF_REG.register("toucan", () -> registerEntity(EntityType.Builder.of(EntityToucan::new, MobCategory.CREATURE).sized(0.45F, 0.45F).setTrackingRange(10), "toucan"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityManedWolf>> MANED_WOLF = DEF_REG.register("maned_wolf", () -> registerEntity(EntityType.Builder.of(EntityManedWolf::new, MobCategory.CREATURE).sized(0.9F, 1.26F).setTrackingRange(10), "maned_wolf"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAnaconda>> ANACONDA = DEF_REG.register("anaconda", () -> registerEntity(EntityType.Builder.of(EntityAnaconda::new, MobCategory.CREATURE).sized(0.8F, 0.8F).setTrackingRange(10), "anaconda"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAnacondaPart>> ANACONDA_PART = DEF_REG.register("anaconda_part", () -> registerEntity(EntityType.Builder.of(EntityAnacondaPart::new, MobCategory.MISC).sized(0.8F, 0.8F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(10), "anaconda_part"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityVineLasso>> VINE_LASSO = DEF_REG.register("vine_lasso", () -> registerEntity(EntityType.Builder.of(EntityVineLasso::new, MobCategory.MISC).sized(0.85F, 0.2F).fireImmune(), "vine_lasso"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAnteater>> ANTEATER = DEF_REG.register("anteater", () -> registerEntity(EntityType.Builder.of(EntityAnteater::new, MobCategory.CREATURE).sized(1.3F, 1.1F).setTrackingRange(10), "anteater"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRockyRoller>> ROCKY_ROLLER = DEF_REG.register("rocky_roller", () -> registerEntity(EntityType.Builder.of(EntityRockyRoller::new, MobCategory.MONSTER).sized(1.2F, 1.45F).setTrackingRange(8), "rocky_roller"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFlutter>> FLUTTER = DEF_REG.register("flutter", () -> registerEntity(EntityType.Builder.of(EntityFlutter::new, MobCategory.AMBIENT).sized(0.5F, 0.7F).setTrackingRange(6), "flutter"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityPollenBall>> POLLEN_BALL = DEF_REG.register("pollen_ball", () -> registerEntity(EntityType.Builder.of(EntityPollenBall::new, MobCategory.MISC).sized(0.35F, 0.35F).fireImmune(), "pollen_ball"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGeladaMonkey>> GELADA_MONKEY = DEF_REG.register("gelada_monkey", () -> registerEntity(EntityType.Builder.of(EntityGeladaMonkey::new, MobCategory.CREATURE).sized(1.2F, 1.2F).setTrackingRange(10), "gelada_monkey"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityJerboa>> JERBOA = DEF_REG.register("jerboa", () -> registerEntity(EntityType.Builder.of(EntityJerboa::new, MobCategory.AMBIENT).sized(0.5F, 0.5F).setTrackingRange(5), "jerboa"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTerrapin>> TERRAPIN = DEF_REG.register("terrapin", () -> registerEntity(EntityType.Builder.of(EntityTerrapin::new, MobCategory.WATER_AMBIENT).sized(0.75F, 0.45F).setTrackingRange(5), "terrapin"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCombJelly>> COMB_JELLY = DEF_REG.register("comb_jelly", () -> registerEntity(EntityType.Builder.of(EntityCombJelly::new, MobCategory.WATER_AMBIENT).sized(0.65F, 0.8F).setTrackingRange(5), "comb_jelly"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCosmicCod>> COSMIC_COD = DEF_REG.register("cosmic_cod", () -> registerEntity(EntityType.Builder.of(EntityCosmicCod::new, MobCategory.AMBIENT).sized(0.85F, 0.4F).setTrackingRange(5), "cosmic_cod"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBunfungus>> BUNFUNGUS = DEF_REG.register("bunfungus", () -> registerEntity(EntityType.Builder.of(EntityBunfungus::new, MobCategory.CREATURE).sized(1.85F, 2.1F).setTrackingRange(10), "bunfungus"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBison>> BISON = DEF_REG.register("bison", () -> registerEntity(EntityType.Builder.of(EntityBison::new, MobCategory.CREATURE).sized(2.4F, 2.1F).setTrackingRange(10), "bison"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGiantSquid>> GIANT_SQUID = DEF_REG.register("giant_squid", () -> registerEntity(EntityType.Builder.of(EntityGiantSquid::new, MobCategory.WATER_CREATURE).sized(0.9F, 1.2F).setTrackingRange(10), "giant_squid"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySquidGrapple>> SQUID_GRAPPLE = DEF_REG.register("squid_grapple", () -> registerEntity(EntityType.Builder.of(EntitySquidGrapple::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune(), "squid_grapple"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySeaBear>> SEA_BEAR = DEF_REG.register("sea_bear", () -> registerEntity(EntityType.Builder.of(EntitySeaBear::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.99F).setTrackingRange(10), "sea_bear"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDevilsHolePupfish>> DEVILS_HOLE_PUPFISH = DEF_REG.register("devils_hole_pupfish", () -> registerEntity(EntityType.Builder.of(EntityDevilsHolePupfish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F).setTrackingRange(4), "devils_hole_pupfish"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCatfish>> CATFISH = DEF_REG.register("catfish", () -> registerEntity(EntityType.Builder.of(EntityCatfish::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.6F).setTrackingRange(5), "catfish"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFlyingFish>> FLYING_FISH = DEF_REG.register("flying_fish", () -> registerEntity(EntityType.Builder.of(EntityFlyingFish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F).setTrackingRange(5), "flying_fish"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySkelewag>> SKELEWAG = DEF_REG.register("skelewag", () -> registerEntity(EntityType.Builder.of(EntitySkelewag::new, MobCategory.MONSTER).sized(2F, 1.2F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(8), "skelewag"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRainFrog>> RAIN_FROG = DEF_REG.register("rain_frog", () -> registerEntity(EntityType.Builder.of(EntityRainFrog::new, MobCategory.AMBIENT).sized(0.55F, 0.5F).setTrackingRange(5), "rain_frog"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityPotoo>> POTOO = DEF_REG.register("potoo", () -> registerEntity(EntityType.Builder.of(EntityPotoo::new, MobCategory.CREATURE).sized(0.6F, 0.8F).setTrackingRange(10), "potoo"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMudskipper>> MUDSKIPPER = DEF_REG.register("mudskipper", () -> registerEntity(EntityType.Builder.of(EntityMudskipper::new, MobCategory.CREATURE).sized(0.7F, 0.44F).setTrackingRange(10), "mudskipper"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMudBall>> MUD_BALL = DEF_REG.register("mud_ball", () -> registerEntity(EntityType.Builder.of(EntityMudBall::new, MobCategory.MISC).sized(0.35F, 0.35F).fireImmune(), "mud_ball"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRhinoceros>> RHINOCEROS = DEF_REG.register("rhinoceros", () -> registerEntity(EntityType.Builder.of(EntityRhinoceros::new, MobCategory.CREATURE).sized(2.3F, 2.4F).setTrackingRange(10), "rhinoceros"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySugarGlider>> SUGAR_GLIDER = DEF_REG.register("sugar_glider", () -> registerEntity(EntityType.Builder.of(EntitySugarGlider::new, MobCategory.CREATURE).sized(0.8F, 0.45F).setTrackingRange(10), "sugar_glider"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFarseer>> FARSEER = DEF_REG.register("farseer", () -> registerEntity(EntityType.Builder.of(EntityFarseer::new, MobCategory.MONSTER).sized(0.99F, 1.5F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).fireImmune().setTrackingRange(8), "farseer"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySkreecher>> SKREECHER = DEF_REG.register("skreecher", () -> registerEntity(EntityType.Builder.of(EntitySkreecher::new, MobCategory.CREATURE).sized(0.99F, 0.95F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).setTrackingRange(8), "skreecher"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityUnderminer>> UNDERMINER = DEF_REG.register("underminer", () -> registerEntity(EntityType.Builder.of(EntityUnderminer::new, MobCategory.AMBIENT).sized(0.8F, 1.8F).setTrackingRange(8), "underminer"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMurmur>> MURMUR = DEF_REG.register("murmur", () -> registerEntity(EntityType.Builder.of(EntityMurmur::new, MobCategory.MONSTER).sized(0.7F, 1.45F).setTrackingRange(8), "murmur"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityMurmurHead>> MURMUR_HEAD = DEF_REG.register("murmur_head", () -> registerEntity(EntityType.Builder.of(EntityMurmurHead::new, MobCategory.MONSTER).sized(0.55F, 0.55F).setTrackingRange(8), "murmur_head"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTendonSegment>> TENDON_SEGMENT = DEF_REG.register("tendon_segment", () -> registerEntity(EntityType.Builder.of(EntityTendonSegment::new, MobCategory.MISC).sized(0.1F, 0.1F).fireImmune(), "tendon_segment"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySkunk>> SKUNK = DEF_REG.register("skunk", () -> registerEntity(EntityType.Builder.of(EntitySkunk::new, MobCategory.CREATURE).sized(0.85F, 0.65F).setTrackingRange(10), "skunk"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityFart>> FART = DEF_REG.register("fart", () -> registerEntity(EntityType.Builder.of(EntityFart::new, MobCategory.MISC).sized(0.7F, 0.3F).fireImmune(), "fart"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBananaSlug>> BANANA_SLUG = DEF_REG.register("banana_slug", () -> registerEntity(EntityType.Builder.of(EntityBananaSlug::new, MobCategory.CREATURE).sized(0.8F, 0.4F).setTrackingRange(10), "banana_slug"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBlueJay>> BLUE_JAY = DEF_REG.register("blue_jay", () -> registerEntity(EntityType.Builder.of(EntityBlueJay::new, MobCategory.CREATURE).sized(0.5F, 0.6F).setTrackingRange(10), "blue_jay"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCaiman>> CAIMAN = DEF_REG.register("caiman", () -> registerEntity(EntityType.Builder.of(EntityCaiman::new, MobCategory.CREATURE).sized(1.3F, 0.6F).setTrackingRange(10), "caiman"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTriops>> TRIOPS = DEF_REG.register("triops", () -> registerEntity(EntityType.Builder.of(EntityTriops::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.25F).setTrackingRange(5), "triops"));

    private static EntityType registerEntity(EntityType.Builder builder, String entityName) {
        return builder.build(entityName);
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // Custom spawn placement type for leaves - using ON_GROUND as base with custom spawn rules
        SpawnPlacementType spawnsOnLeaves = SpawnPlacementTypes.ON_GROUND;
        event.register(GRIZZLY_BEAR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ROADRUNNER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRoadrunner::canRoadrunnerSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BONE_SERPENT.get(), SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBoneSerpent::canBoneSerpentSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(GAZELLE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CROCODILE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrocodile::canCrocodileSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(FLY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFly::canFlySpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HUMMINGBIRD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityHummingbird::canHummingbirdSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ORCA.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityOrca::canOrcaSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SUNBIRD.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySunbird::canSunbirdSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(GORILLA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityGorilla::canGorillaSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CRIMSON_MOSQUITO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrimsonMosquito::canMosquitoSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(RATTLESNAKE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRattlesnake::canRattlesnakeSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ENDERGRADE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEndergrade::canEndergradeSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HAMMERHEAD_SHARK.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityHammerheadShark::canHammerheadSharkSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(LOBSTER.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLobster::canLobsterSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(KOMODO_DRAGON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKomodoDragon::canKomodoDragonSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CAPUCHIN_MONKEY.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntityCapuchinMonkey::canCapuchinSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CENTIPEDE_HEAD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCentipedeHead::canCentipedeSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(WARPED_TOAD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityWarpedToad::canWarpedToadSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MOOSE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMoose::canMooseSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MIMICUBE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(RACCOON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BLOBFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBlobfish::canBlobfishSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SEAL.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeal::canSealSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(COCKROACH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCockroach::canCockroachSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SHOEBILL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ELEPHANT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SOUL_VULTURE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySoulVulture::canVultureSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SNOW_LEOPARD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySnowLeopard::canSnowLeopardSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CROW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityCrow::canCrowSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ALLIGATOR_SNAPPING_TURTLE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAlligatorSnappingTurtle::canTurtleSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MUNGUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMungus::canMungusSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MANTIS_SHRIMP.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMantisShrimp::canMantisShrimpSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(GUSTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGuster::canGusterSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(WARPED_MOSCO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(STRADDLER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStraddler::canStraddlerSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(STRADPOLE.get(), SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStradpole::canStradpoleSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(EMU.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEmu::canEmuSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(PLATYPUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityPlatypus::canPlatypusSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DROPBEAR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TASMANIAN_DEVIL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(KANGAROO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKangaroo::canKangarooSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CACHALOT_WHALE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCachalotWhale::canCachalotWhaleSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(LEAFCUTTER_ANT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ENDERIOPHAGE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEnderiophage::canEnderiophageSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BALD_EAGLE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityBaldEagle::canEagleSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TIGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTiger::canTigerSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TARANTULA_HAWK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTarantulaHawk::canTarantulaHawkSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VOID_WORM.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityVoidWorm::canVoidWormSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(FRILLED_SHARK.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFrilledShark::canFrilledSharkSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MIMIC_OCTOPUS.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMimicOctopus::canMimicOctopusSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SEAGULL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeagull::canSeagullSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(FROSTSTALKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFroststalker::canFroststalkerSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TUSKLIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTusklin::canTusklinSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(LAVIATHAN.get(), SpawnPlacementTypes.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLaviathan::canLaviathanSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(COSMAW.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCosmaw::canCosmawSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TOUCAN.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntityToucan::canToucanSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MANED_WOLF.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityManedWolf::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ANACONDA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnaconda::canAnacondaSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ANTEATER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnteater::canAnteaterSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ROCKY_ROLLER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRockyRoller::checkRockyRollerSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(FLUTTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFlutter::canFlutterSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(GELADA_MONKEY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGeladaMonkey::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(JERBOA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityJerboa::canJerboaSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TERRAPIN.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTerrapin::canTerrapinSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(COMB_JELLY.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCombJelly::canCombJellySpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BUNFUNGUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBunfungus::canBunfungusSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BISON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBison::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(GIANT_SQUID.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGiantSquid::canGiantSquidSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DEVILS_HOLE_PUPFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityDevilsHolePupfish::canPupfishSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CATFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCatfish::canCatfishSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(FLYING_FISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SKELEWAG.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkelewag::canSkelewagSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(RAIN_FROG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRainFrog::canRainFrogSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(POTOO.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntityPotoo::canPotooSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MUDSKIPPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMudskipper::canMudskipperSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(RHINOCEROS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRhinoceros::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SUGAR_GLIDER.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntitySugarGlider::canSugarGliderSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(FARSEER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFarseer::checkFarseerSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SKREECHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkreecher::checkSkreecherSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(UNDERMINER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityUnderminer::checkUnderminerSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(MURMUR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMurmur::checkMurmurSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SKUNK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkunk::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BANANA_SLUG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBananaSlug::checkBananaSlugSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BLUE_JAY.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntityBlueJay::checkBlueJaySpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CAIMAN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCaiman::canCaimanSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(TRIOPS.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        event.put(GRIZZLY_BEAR.get(), EntityGrizzlyBear.bakeAttributes().build());
        event.put(ROADRUNNER.get(), EntityRoadrunner.bakeAttributes().build());
        event.put(BONE_SERPENT.get(), EntityBoneSerpent.bakeAttributes().build());
        event.put(BONE_SERPENT_PART.get(), EntityBoneSerpentPart.bakeAttributes().build());
        event.put(GAZELLE.get(), EntityGazelle.bakeAttributes().build());
        event.put(CROCODILE.get(), EntityCrocodile.bakeAttributes().build());
        event.put(FLY.get(), EntityFly.bakeAttributes().build());
        event.put(HUMMINGBIRD.get(), EntityHummingbird.bakeAttributes().build());
        event.put(ORCA.get(), EntityOrca.bakeAttributes().build());
        event.put(SUNBIRD.get(), EntitySunbird.bakeAttributes().build());
        event.put(GORILLA.get(), EntityGorilla.bakeAttributes().build());
        event.put(CRIMSON_MOSQUITO.get(), EntityCrimsonMosquito.bakeAttributes().build());
        event.put(RATTLESNAKE.get(), EntityRattlesnake.bakeAttributes().build());
        event.put(ENDERGRADE.get(), EntityEndergrade.bakeAttributes().build());
        event.put(HAMMERHEAD_SHARK.get(), EntityHammerheadShark.bakeAttributes().build());
        event.put(LOBSTER.get(), EntityLobster.bakeAttributes().build());
        event.put(KOMODO_DRAGON.get(), EntityKomodoDragon.bakeAttributes().build());
        event.put(CAPUCHIN_MONKEY.get(), EntityCapuchinMonkey.bakeAttributes().build());
        event.put(CENTIPEDE_HEAD.get(), EntityCentipedeHead.bakeAttributes().build());
        event.put(CENTIPEDE_BODY.get(), EntityCentipedeBody.bakeAttributes().build());
        event.put(CENTIPEDE_TAIL.get(), EntityCentipedeTail.bakeAttributes().build());
        event.put(WARPED_TOAD.get(), EntityWarpedToad.bakeAttributes().build());
        event.put(MOOSE.get(), EntityMoose.bakeAttributes().build());
        event.put(MIMICUBE.get(), EntityMimicube.bakeAttributes().build());
        event.put(RACCOON.get(), EntityRaccoon.bakeAttributes().build());
        event.put(BLOBFISH.get(), EntityBlobfish.bakeAttributes().build());
        event.put(SEAL.get(), EntitySeal.bakeAttributes().build());
        event.put(COCKROACH.get(), EntityCockroach.bakeAttributes().build());
        event.put(SHOEBILL.get(), EntityShoebill.bakeAttributes().build());
        event.put(ELEPHANT.get(), EntityElephant.bakeAttributes().build());
        event.put(SOUL_VULTURE.get(), EntitySoulVulture.bakeAttributes().build());
        event.put(SNOW_LEOPARD.get(), EntitySnowLeopard.bakeAttributes().build());
        event.put(SPECTRE.get(), EntitySpectre.bakeAttributes().build());
        event.put(CROW.get(), EntityCrow.bakeAttributes().build());
        event.put(ALLIGATOR_SNAPPING_TURTLE.get(), EntityAlligatorSnappingTurtle.bakeAttributes().build());
        event.put(MUNGUS.get(), EntityMungus.bakeAttributes().build());
        event.put(MANTIS_SHRIMP.get(), EntityMantisShrimp.bakeAttributes().build());
        event.put(GUSTER.get(), EntityGuster.bakeAttributes().build());
        event.put(WARPED_MOSCO.get(), EntityWarpedMosco.bakeAttributes().build());
        event.put(STRADDLER.get(), EntityStraddler.bakeAttributes().build());
        event.put(STRADPOLE.get(), EntityStradpole.bakeAttributes().build());
        event.put(EMU.get(), EntityEmu.bakeAttributes().build());
        event.put(PLATYPUS.get(), EntityPlatypus.bakeAttributes().build());
        event.put(DROPBEAR.get(), EntityDropBear.bakeAttributes().build());
        event.put(TASMANIAN_DEVIL.get(), EntityTasmanianDevil.bakeAttributes().build());
        event.put(KANGAROO.get(), EntityKangaroo.bakeAttributes().build());
        event.put(CACHALOT_WHALE.get(), EntityCachalotWhale.bakeAttributes().build());
        event.put(LEAFCUTTER_ANT.get(), EntityLeafcutterAnt.bakeAttributes().build());
        event.put(ENDERIOPHAGE.get(), EntityEnderiophage.bakeAttributes().build());
        event.put(BALD_EAGLE.get(), EntityBaldEagle.bakeAttributes().build());
        event.put(TIGER.get(), EntityTiger.bakeAttributes().build());
        event.put(TARANTULA_HAWK.get(), EntityTarantulaHawk.bakeAttributes().build());
        event.put(VOID_WORM.get(), EntityVoidWorm.bakeAttributes().build());
        event.put(VOID_WORM_PART.get(), EntityVoidWormPart.bakeAttributes().build());
        event.put(FRILLED_SHARK.get(), EntityFrilledShark.bakeAttributes().build());
        event.put(MIMIC_OCTOPUS.get(), EntityMimicOctopus.bakeAttributes().build());
        event.put(SEAGULL.get(), EntitySeagull.bakeAttributes().build());
        event.put(FROSTSTALKER.get(), EntityFroststalker.bakeAttributes().build());
        event.put(TUSKLIN.get(), EntityTusklin.bakeAttributes().build());
        event.put(LAVIATHAN.get(), EntityLaviathan.bakeAttributes().build());
        event.put(COSMAW.get(), EntityCosmaw.bakeAttributes().build());
        event.put(TOUCAN.get(), EntityToucan.bakeAttributes().build());
        event.put(MANED_WOLF.get(), EntityManedWolf.bakeAttributes().build());
        event.put(ANACONDA.get(), EntityAnaconda.bakeAttributes().build());
        event.put(ANACONDA_PART.get(), EntityAnacondaPart.bakeAttributes().build());
        event.put(ANTEATER.get(), EntityAnteater.bakeAttributes().build());
        event.put(ROCKY_ROLLER.get(), EntityRockyRoller.bakeAttributes().build());
        event.put(FLUTTER.get(), EntityFlutter.bakeAttributes().build());
        event.put(GELADA_MONKEY.get(), EntityGeladaMonkey.bakeAttributes().build());
        event.put(JERBOA.get(), EntityJerboa.bakeAttributes().build());
        event.put(TERRAPIN.get(), EntityTerrapin.bakeAttributes().build());
        event.put(COMB_JELLY.get(), EntityCombJelly.bakeAttributes().build());
        event.put(COSMIC_COD.get(), EntityCosmicCod.bakeAttributes().build());
        event.put(BUNFUNGUS.get(), EntityBunfungus.bakeAttributes().build());
        event.put(BISON.get(), EntityBison.bakeAttributes().build());
        event.put(GIANT_SQUID.get(), EntityGiantSquid.bakeAttributes().build());
        event.put(SEA_BEAR.get(), EntitySeaBear.bakeAttributes().build());
        event.put(DEVILS_HOLE_PUPFISH.get(), EntityDevilsHolePupfish.bakeAttributes().build());
        event.put(CATFISH.get(), EntityCatfish.bakeAttributes().build());
        event.put(FLYING_FISH.get(), EntityFlyingFish.bakeAttributes().build());
        event.put(SKELEWAG.get(), EntitySkelewag.bakeAttributes().build());
        event.put(RAIN_FROG.get(), EntityRainFrog.bakeAttributes().build());
        event.put(POTOO.get(), EntityPotoo.bakeAttributes().build());
        event.put(MUDSKIPPER.get(), EntityMudskipper.bakeAttributes().build());
        event.put(RHINOCEROS.get(), EntityRhinoceros.bakeAttributes().build());
        event.put(SUGAR_GLIDER.get(), EntitySugarGlider.bakeAttributes().build());
        event.put(FARSEER.get(), EntityFarseer.bakeAttributes().build());
        event.put(SKREECHER.get(), EntitySkreecher.bakeAttributes().build());
        event.put(UNDERMINER.get(), EntityUnderminer.bakeAttributes().build());
        event.put(MURMUR.get(), EntityMurmur.bakeAttributes().build());
        event.put(MURMUR_HEAD.get(), EntityMurmurHead.bakeAttributes().build());
        event.put(SKUNK.get(), EntitySkunk.bakeAttributes().build());
        event.put(BANANA_SLUG.get(), EntityBananaSlug.bakeAttributes().build());
        event.put(BLUE_JAY.get(), EntityBlueJay.bakeAttributes().build());
        event.put(CAIMAN.get(), EntityCaiman.bakeAttributes().build());
        event.put(TRIOPS.get(), EntityTriops.bakeAttributes().build());
    }

    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag);
        }
    }

    public static Predicate<LivingEntity> buildPredicateFromTagTameable(TagKey<EntityType<?>> entityTag, LivingEntity owner){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag) && !owner.isAlliedTo(e);
        }
    }

    public static boolean rollSpawn(int rolls, RandomSource random, MobSpawnType reason){
        if(reason == MobSpawnType.SPAWNER){
            return true;
        }else{
            return rolls <= 0 || random.nextInt(rolls) == 0;
        }
    }

    public static boolean createLeavesSpawnPlacement(LevelReader level, BlockPos pos, EntityType<?> type){
        BlockPos blockpos = pos.above();
        BlockPos blockpos1 = pos.below();
        FluidState fluidstate = level.getFluidState(pos);
        BlockState blockstate = level.getBlockState(pos);
        BlockState blockstate1 = level.getBlockState(blockpos1);
        if (!blockstate1.isValidSpawn(level, blockpos1, type) && !blockstate1.is(BlockTags.LEAVES)) {
            return false;
        } else {
            return NaturalSpawner.isValidEmptySpawnBlock(level, pos, blockstate, fluidstate, type) && NaturalSpawner.isValidEmptySpawnBlock(level, blockpos, level.getBlockState(blockpos), level.getFluidState(blockpos), type);
        }
    }

}
