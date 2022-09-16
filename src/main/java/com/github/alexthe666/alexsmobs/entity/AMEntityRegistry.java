package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMEntityRegistry {

    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AlexsMobs.MODID);
    public static final RegistryObject<EntityType<EntityGrizzlyBear>> GRIZZLY_BEAR = DEF_REG.register("grizzly_bear", () -> registerEntity(EntityType.Builder.of(EntityGrizzlyBear::new, MobCategory.CREATURE).sized(1.6F, 1.8F), "grizzly_bear"));
    public static final RegistryObject<EntityType<EntityRoadrunner>> ROADRUNNER = DEF_REG.register("roadrunner", () -> registerEntity(EntityType.Builder.of(EntityRoadrunner::new, MobCategory.CREATURE).sized(0.45F, 0.75F), "roadrunner"));
    public static final RegistryObject<EntityType<EntityBoneSerpent>> BONE_SERPENT = DEF_REG.register("bone_serpent", () -> registerEntity(EntityType.Builder.of(EntityBoneSerpent::new, MobCategory.MONSTER).sized(1.2F, 1.15F).fireImmune(), "bone_serpent"));
    public static final RegistryObject<EntityType<EntityBoneSerpentPart>> BONE_SERPENT_PART = DEF_REG.register("bone_serpent_part", () -> registerEntity(EntityType.Builder.of(EntityBoneSerpentPart::new, MobCategory.MONSTER).sized(1F, 1F).fireImmune(), "bone_serpent_part"));
    public static final RegistryObject<EntityType<EntityGazelle>> GAZELLE = DEF_REG.register("gazelle", () -> registerEntity(EntityType.Builder.of(EntityGazelle::new, MobCategory.CREATURE).sized(0.85F, 1.25F), "gazelle"));
    public static final RegistryObject<EntityType<EntityCrocodile>> CROCODILE = DEF_REG.register("crocodile", () -> registerEntity(EntityType.Builder.of(EntityCrocodile::new, MobCategory.WATER_CREATURE).sized(2.15F, 0.75F), "crocodile"));
    public static final RegistryObject<EntityType<EntityFly>> FLY = DEF_REG.register("fly", () -> registerEntity(EntityType.Builder.of(EntityFly::new, MobCategory.AMBIENT).sized(0.35F, 0.35F), "fly"));
    public static final RegistryObject<EntityType<EntityHummingbird>> HUMMINGBIRD = DEF_REG.register("hummingbird", () -> registerEntity(EntityType.Builder.of(EntityHummingbird::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "hummingbird"));
    public static final RegistryObject<EntityType<EntityOrca>> ORCA = DEF_REG.register("orca", () -> registerEntity(EntityType.Builder.of(EntityOrca::new, MobCategory.WATER_CREATURE).sized(3.75F, 1.75F), "orca"));
    public static final RegistryObject<EntityType<EntitySunbird>> SUNBIRD = DEF_REG.register("sunbird", () -> registerEntity(EntityType.Builder.of(EntitySunbird::new, MobCategory.CREATURE).sized(2.75F, 1.5F).fireImmune().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "sunbird"));
    public static final RegistryObject<EntityType<EntityGorilla>> GORILLA = DEF_REG.register("gorilla", () -> registerEntity(EntityType.Builder.of(EntityGorilla::new, MobCategory.CREATURE).sized(1.15F, 1.35F), "gorilla"));
    public static final RegistryObject<EntityType<EntityCrimsonMosquito>> CRIMSON_MOSQUITO = DEF_REG.register("crimson_mosquito", () -> registerEntity(EntityType.Builder.of(EntityCrimsonMosquito::new, MobCategory.MONSTER).sized(1.25F, 1.15F).fireImmune(), "crimson_mosquito"));
    public static final RegistryObject<EntityType<EntityMosquitoSpit>> MOSQUITO_SPIT = DEF_REG.register("mosquito_spit", () -> registerEntity(EntityType.Builder.of(EntityMosquitoSpit::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityMosquitoSpit::new).fireImmune(), "mosquito_spit"));
    public static final RegistryObject<EntityType<EntityRattlesnake>> RATTLESNAKE = DEF_REG.register("rattlesnake", () -> registerEntity(EntityType.Builder.of(EntityRattlesnake::new, MobCategory.CREATURE).sized(0.95F, 0.35F), "rattlesnake"));
    public static final RegistryObject<EntityType<EntityEndergrade>> ENDERGRADE = DEF_REG.register("endergrade", () -> registerEntity(EntityType.Builder.of(EntityEndergrade::new, MobCategory.CREATURE).sized(0.95F, 0.85F), "endergrade"));
    public static final RegistryObject<EntityType<EntityHammerheadShark>> HAMMERHEAD_SHARK = DEF_REG.register("hammerhead_shark", () -> registerEntity(EntityType.Builder.of(EntityHammerheadShark::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.25F), "hammerhead_shark"));
    public static final RegistryObject<EntityType<EntitySharkToothArrow>> SHARK_TOOTH_ARROW = DEF_REG.register("shark_tooth_arrow", () -> registerEntity(EntityType.Builder.of(EntitySharkToothArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntitySharkToothArrow::new), "shark_tooth_arrow"));
    public static final RegistryObject<EntityType<EntityLobster>> LOBSTER = DEF_REG.register("lobster", () -> registerEntity(EntityType.Builder.of(EntityLobster::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F), "lobster"));
    public static final RegistryObject<EntityType<EntityKomodoDragon>> KOMODO_DRAGON = DEF_REG.register("komodo_dragon", () -> registerEntity(EntityType.Builder.of(EntityKomodoDragon::new, MobCategory.CREATURE).sized(1.9F, 0.9F), "komodo_dragon"));
    public static final RegistryObject<EntityType<EntityCapuchinMonkey>> CAPUCHIN_MONKEY = DEF_REG.register("capuchin_monkey", () -> registerEntity(EntityType.Builder.of(EntityCapuchinMonkey::new, MobCategory.CREATURE).sized(0.65F, 0.75F), "capuchin_monkey"));
    public static final RegistryObject<EntityType<EntityTossedItem>> TOSSED_ITEM = DEF_REG.register("tossed_item", () -> registerEntity(EntityType.Builder.of(EntityTossedItem::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityTossedItem::new).fireImmune(), "tossed_item"));
    public static final RegistryObject<EntityType<EntityCentipedeHead>> CENTIPEDE_HEAD = DEF_REG.register("centipede_head", () -> registerEntity(EntityType.Builder.of(EntityCentipedeHead::new, MobCategory.CREATURE).sized(0.9F, 0.9F), "centipede_head"));
    public static final RegistryObject<EntityType<EntityCentipedeBody>> CENTIPEDE_BODY = DEF_REG.register("centipede_body", () -> registerEntity(EntityType.Builder.of(EntityCentipedeBody::new, MobCategory.CREATURE).sized(0.9F, 0.9F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "centipede_body"));
    public static final RegistryObject<EntityType<EntityCentipedeTail>> CENTIPEDE_TAIL = DEF_REG.register("centipede_tail", () -> registerEntity(EntityType.Builder.of(EntityCentipedeTail::new, MobCategory.CREATURE).sized(0.9F, 0.9F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "centipede_tail"));
    public static final RegistryObject<EntityType<EntityWarpedToad>> WARPED_TOAD = DEF_REG.register("warped_toad", () -> registerEntity(EntityType.Builder.of(EntityWarpedToad::new, MobCategory.CREATURE).sized(0.9F, 1.4F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "warped_toad"));
    public static final RegistryObject<EntityType<EntityMoose>> MOOSE = DEF_REG.register("moose", () -> registerEntity(EntityType.Builder.of(EntityMoose::new, MobCategory.CREATURE).sized(1.7F, 2.4F), "moose"));
    public static final RegistryObject<EntityType<EntityMimicube>> MIMICUBE = DEF_REG.register("mimicube", () -> registerEntity(EntityType.Builder.of(EntityMimicube::new, MobCategory.MONSTER).sized(0.9F, 0.9F), "mimicube"));
    public static final RegistryObject<EntityType<EntityRaccoon>> RACCOON = DEF_REG.register("raccoon", () -> registerEntity(EntityType.Builder.of(EntityRaccoon::new, MobCategory.CREATURE).sized(0.8F, 0.9F), "raccoon"));
    public static final RegistryObject<EntityType<EntityBlobfish>> BLOBFISH = DEF_REG.register("blobfish", () -> registerEntity(EntityType.Builder.of(EntityBlobfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.45F), "blobfish"));
    public static final RegistryObject<EntityType<EntitySeal>> SEAL = DEF_REG.register("seal", () -> registerEntity(EntityType.Builder.of(EntitySeal::new, MobCategory.CREATURE).sized(1.8F, 0.9F), "seal"));
    public static final RegistryObject<EntityType<EntityCockroach>> COCKROACH = DEF_REG.register("cockroach", () -> registerEntity(EntityType.Builder.of(EntityCockroach::new, MobCategory.AMBIENT).sized(0.7F, 0.3F), "cockroach"));
    public static final RegistryObject<EntityType<EntityCockroachEgg>> COCKROACH_EGG = DEF_REG.register("cockroach_egg", () -> registerEntity(EntityType.Builder.of(EntityCockroachEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityCockroachEgg::new).fireImmune(), "cockroach_egg"));
    public static final RegistryObject<EntityType<EntityShoebill>> SHOEBILL = DEF_REG.register("shoebill", () -> registerEntity(EntityType.Builder.of(EntityShoebill::new, MobCategory.CREATURE).sized(0.8F, 1.5F).setUpdateInterval(1), "shoebill"));
    public static final RegistryObject<EntityType<EntityElephant>> ELEPHANT = DEF_REG.register("elephant", () -> registerEntity(EntityType.Builder.of(EntityElephant::new, MobCategory.CREATURE).sized(3.1F, 3.5F).setUpdateInterval(1), "elephant"));
    public static final RegistryObject<EntityType<EntitySoulVulture>> SOUL_VULTURE = DEF_REG.register("soul_vulture", () -> registerEntity(EntityType.Builder.of(EntitySoulVulture::new, MobCategory.MONSTER).sized(0.9F, 1.3F).setUpdateInterval(1).fireImmune(), "soul_vulture"));
    public static final RegistryObject<EntityType<EntitySnowLeopard>> SNOW_LEOPARD = DEF_REG.register("snow_leopard", () -> registerEntity(EntityType.Builder.of(EntitySnowLeopard::new, MobCategory.CREATURE).sized(1.2F, 1.3F).immuneTo(Blocks.POWDER_SNOW), "snow_leopard"));
    public static final RegistryObject<EntityType<EntitySpectre>> SPECTRE = DEF_REG.register("spectre", () -> registerEntity(EntityType.Builder.of(EntitySpectre::new, MobCategory.CREATURE).sized(3.15F, 0.8F).fireImmune().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "spectre"));
    public static final RegistryObject<EntityType<EntityCrow>> CROW = DEF_REG.register("crow", () -> registerEntity(EntityType.Builder.of(EntityCrow::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "crow"));
    public static final RegistryObject<EntityType<EntityAlligatorSnappingTurtle>> ALLIGATOR_SNAPPING_TURTLE = DEF_REG.register("alligator_snapping_turtle", () -> registerEntity(EntityType.Builder.of(EntityAlligatorSnappingTurtle::new, MobCategory.CREATURE).sized(1.25F, 0.65F), "alligator_snapping_turtle"));
    public static final RegistryObject<EntityType<EntityMungus>> MUNGUS = DEF_REG.register("mungus", () -> registerEntity(EntityType.Builder.of(EntityMungus::new, MobCategory.CREATURE).sized(0.75F, 1.45F), "mungus"));
    public static final RegistryObject<EntityType<EntityMantisShrimp>> MANTIS_SHRIMP = DEF_REG.register("mantis_shrimp", () -> registerEntity(EntityType.Builder.of(EntityMantisShrimp::new, MobCategory.WATER_CREATURE).sized(1.25F, 1.2F), "mantis_shrimp"));
    public static final RegistryObject<EntityType<EntityGuster>> GUSTER = DEF_REG.register("guster", () -> registerEntity(EntityType.Builder.of(EntityGuster::new, MobCategory.MONSTER).sized(1.42F, 2.35F).fireImmune(), "guster"));
    public static final RegistryObject<EntityType<EntitySandShot>> SAND_SHOT = DEF_REG.register("sand_shot", () -> registerEntity(EntityType.Builder.of(EntitySandShot::new, MobCategory.MISC).sized(0.95F, 0.65F).setCustomClientFactory(EntitySandShot::new).fireImmune(), "sand_shot"));
    public static final RegistryObject<EntityType<EntityGust>> GUST = DEF_REG.register("gust", () -> registerEntity(EntityType.Builder.of(EntityGust::new, MobCategory.MISC).sized(0.8F, 0.8F).setCustomClientFactory(EntityGust::new).fireImmune(), "gust"));
    public static final RegistryObject<EntityType<EntityWarpedMosco>> WARPED_MOSCO = DEF_REG.register("warped_mosco", () -> registerEntity(EntityType.Builder.of(EntityWarpedMosco::new, MobCategory.MONSTER).sized(1.99F, 3.25F).fireImmune(), "warped_mosco"));
    public static final RegistryObject<EntityType<EntityHemolymph>> HEMOLYMPH = DEF_REG.register("hemolymph", () -> registerEntity(EntityType.Builder.of(EntityHemolymph::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityHemolymph::new).fireImmune(), "hemolymph"));
    public static final RegistryObject<EntityType<EntityStraddler>> STRADDLER = DEF_REG.register("straddler", () -> registerEntity(EntityType.Builder.of(EntityStraddler::new, MobCategory.MONSTER).sized(1.65F, 3F).fireImmune(), "straddler"));
    public static final RegistryObject<EntityType<EntityStradpole>> STRADPOLE = DEF_REG.register("stradpole", () -> registerEntity(EntityType.Builder.of(EntityStradpole::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.5F).fireImmune(), "stradpole"));
    public static final RegistryObject<EntityType<EntityStraddleboard>> STRADDLEBOARD = DEF_REG.register("straddleboard", () -> registerEntity(EntityType.Builder.of(EntityStraddleboard::new, MobCategory.MISC).sized(1.5F, 0.35F).setCustomClientFactory(EntityStraddleboard::new).fireImmune(), "straddleboard"));
    public static final RegistryObject<EntityType<EntityEmu>> EMU = DEF_REG.register("emu", () -> registerEntity(EntityType.Builder.of(EntityEmu::new, MobCategory.CREATURE).sized(1.1F, 1.8F), "emu"));
    public static final RegistryObject<EntityType<EntityEmuEgg>> EMU_EGG = DEF_REG.register("emu_egg", () -> registerEntity(EntityType.Builder.of(EntityEmuEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityEmuEgg::new).fireImmune(), "emu_egg"));
    public static final RegistryObject<EntityType<EntityPlatypus>> PLATYPUS = DEF_REG.register("platypus", () -> registerEntity(EntityType.Builder.of(EntityPlatypus::new, MobCategory.CREATURE).sized(0.8F, 0.5F), "platypus"));
    public static final RegistryObject<EntityType<EntityDropBear>> DROPBEAR = DEF_REG.register("dropbear", () -> registerEntity(EntityType.Builder.of(EntityDropBear::new, MobCategory.MONSTER).sized(1.65F, 1.5F).fireImmune(), "dropbear"));
    public static final RegistryObject<EntityType<EntityTasmanianDevil>> TASMANIAN_DEVIL = DEF_REG.register("tasmanian_devil", () -> registerEntity(EntityType.Builder.of(EntityTasmanianDevil::new, MobCategory.CREATURE).sized(0.7F, 0.8F), "tasmanian_devil"));
    public static final RegistryObject<EntityType<EntityKangaroo>> KANGAROO = DEF_REG.register("kangaroo", () -> registerEntity(EntityType.Builder.of(EntityKangaroo::new, MobCategory.CREATURE).sized(1.65F, 1.5F), "kangaroo"));
    public static final RegistryObject<EntityType<EntityCachalotWhale>> CACHALOT_WHALE = DEF_REG.register("cachalot_whale", () -> registerEntity(EntityType.Builder.of(EntityCachalotWhale::new, MobCategory.WATER_CREATURE).sized(9F, 4.0F), "cachalot_whale"));
    public static final RegistryObject<EntityType<EntityCachalotEcho>> CACHALOT_ECHO = DEF_REG.register("cachalot_echo", () -> registerEntity(EntityType.Builder.of(EntityCachalotEcho::new, MobCategory.MISC).sized(2F, 2F).setCustomClientFactory(EntityCachalotEcho::new).fireImmune(), "cachalot_echo"));
    public static final RegistryObject<EntityType<EntityLeafcutterAnt>> LEAFCUTTER_ANT = DEF_REG.register("leafcutter_ant", () -> registerEntity(EntityType.Builder.of(EntityLeafcutterAnt::new, MobCategory.CREATURE).sized(0.8F, 0.5F), "leafcutter_ant"));
    public static final RegistryObject<EntityType<EntityEnderiophage>> ENDERIOPHAGE = DEF_REG.register("enderiophage", () -> registerEntity(EntityType.Builder.of(EntityEnderiophage::new, MobCategory.CREATURE).sized(0.85F, 1.95F).setUpdateInterval(1), "enderiophage"));
    public static final RegistryObject<EntityType<EntityEnderiophageRocket>> ENDERIOPHAGE_ROCKET = DEF_REG.register("enderiophage_rocket", () -> registerEntity(EntityType.Builder.of(EntityEnderiophageRocket::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityEnderiophageRocket::new).fireImmune(), "enderiophage_rocket"));
    public static final RegistryObject<EntityType<EntityBaldEagle>> BALD_EAGLE = DEF_REG.register("bald_eagle", () -> registerEntity(EntityType.Builder.of(EntityBaldEagle::new, MobCategory.CREATURE).sized(0.5F, 0.95F).setUpdateInterval(1).setTrackingRange(14), "bald_eagle"));
    public static final RegistryObject<EntityType<EntityTiger>> TIGER = DEF_REG.register("tiger", () -> registerEntity(EntityType.Builder.of(EntityTiger::new, MobCategory.CREATURE).sized(1.45F, 1.2F), "tiger"));
    public static final RegistryObject<EntityType<EntityTarantulaHawk>> TARANTULA_HAWK = DEF_REG.register("tarantula_hawk", () -> registerEntity(EntityType.Builder.of(EntityTarantulaHawk::new, MobCategory.CREATURE).sized(1.2F, 0.9F), "tarantula_hawk"));
    public static final RegistryObject<EntityType<EntityVoidWorm>> VOID_WORM = DEF_REG.register("void_worm", () -> registerEntity(EntityType.Builder.of(EntityVoidWorm::new, MobCategory.MONSTER).sized(3.4F, 3F).fireImmune().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm"));
    public static final RegistryObject<EntityType<EntityVoidWormPart>> VOID_WORM_PART = DEF_REG.register("void_worm_part", () -> registerEntity(EntityType.Builder.of(EntityVoidWormPart::new, MobCategory.MONSTER).sized(1.2F, 1.35F).fireImmune().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm_part"));
    public static final RegistryObject<EntityType<EntityVoidWormShot>> VOID_WORM_SHOT = DEF_REG.register("void_worm_shot", () -> registerEntity(EntityType.Builder.of(EntityVoidWormShot::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityVoidWormShot::new).fireImmune(), "void_worm_shot"));
    public static final RegistryObject<EntityType<EntityVoidPortal>> VOID_PORTAL = DEF_REG.register("void_portal", () -> registerEntity(EntityType.Builder.of(EntityVoidPortal::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityVoidPortal::new).fireImmune(), "void_portal"));
    public static final RegistryObject<EntityType<EntityFrilledShark>> FRILLED_SHARK = DEF_REG.register("frilled_shark", () -> registerEntity(EntityType.Builder.of(EntityFrilledShark::new, MobCategory.WATER_CREATURE).sized(1.3F, 0.4F), "frilled_shark"));
    public static final RegistryObject<EntityType<EntityMimicOctopus>> MIMIC_OCTOPUS = DEF_REG.register("mimic_octopus", () -> registerEntity(EntityType.Builder.of(EntityMimicOctopus::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F), "mimic_octopus"));
    public static final RegistryObject<EntityType<EntitySeagull>> SEAGULL = DEF_REG.register("seagull", () -> registerEntity(EntityType.Builder.of(EntitySeagull::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "seagull"));
    public static final RegistryObject<EntityType<EntityFroststalker>> FROSTSTALKER = DEF_REG.register("froststalker", () -> registerEntity(EntityType.Builder.of(EntityFroststalker::new, MobCategory.CREATURE).sized(0.95F, 1.15F).immuneTo(Blocks.POWDER_SNOW), "froststalker"));
    public static final RegistryObject<EntityType<EntityIceShard>> ICE_SHARD = DEF_REG.register("ice_shard", () -> registerEntity(EntityType.Builder.of(EntityIceShard::new, MobCategory.MISC).sized(0.45F, 0.45F).setCustomClientFactory(EntityIceShard::new).fireImmune(), "ice_shard"));
    public static final RegistryObject<EntityType<EntityTusklin>> TUSKLIN = DEF_REG.register("tusklin", () -> registerEntity(EntityType.Builder.of(EntityTusklin::new, MobCategory.CREATURE).sized(2.2F, 1.9F).immuneTo(Blocks.POWDER_SNOW), "tusklin"));
    public static final RegistryObject<EntityType<EntityLaviathan>> LAVIATHAN = DEF_REG.register("laviathan", () -> registerEntity(EntityType.Builder.of(EntityLaviathan::new, MobCategory.CREATURE).sized(3.3F, 2.4F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "laviathan"));
    public static final RegistryObject<EntityType<EntityCosmaw>> COSMAW = DEF_REG.register("cosmaw", () -> registerEntity(EntityType.Builder.of(EntityCosmaw::new, MobCategory.CREATURE).sized(1.95F, 1.8F), "cosmaw"));
    public static final RegistryObject<EntityType<EntityToucan>> TOUCAN = DEF_REG.register("toucan", () -> registerEntity(EntityType.Builder.of(EntityToucan::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "toucan"));
    public static final RegistryObject<EntityType<EntityManedWolf>> MANED_WOLF = DEF_REG.register("maned_wolf", () -> registerEntity(EntityType.Builder.of(EntityManedWolf::new, MobCategory.CREATURE).sized(0.9F, 1.26F), "maned_wolf"));
    public static final RegistryObject<EntityType<EntityAnaconda>> ANACONDA = DEF_REG.register("anaconda", () -> registerEntity(EntityType.Builder.of(EntityAnaconda::new, MobCategory.CREATURE).sized(0.8F, 0.8F), "anaconda"));
    public static final RegistryObject<EntityType<EntityAnacondaPart>> ANACONDA_PART = DEF_REG.register("anaconda_part", () -> registerEntity(EntityType.Builder.of(EntityAnacondaPart::new, MobCategory.CREATURE).sized(0.8F, 0.8F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "anaconda_part"));
    public static final RegistryObject<EntityType<EntityVineLasso>> VINE_LASSO = DEF_REG.register("vine_lasso", () -> registerEntity(EntityType.Builder.of(EntityVineLasso::new, MobCategory.MISC).sized(0.85F, 0.2F).setCustomClientFactory(EntityVineLasso::new).fireImmune(), "vine_lasso"));
    public static final RegistryObject<EntityType<EntityAnteater>> ANTEATER = DEF_REG.register("anteater", () -> registerEntity(EntityType.Builder.of(EntityAnteater::new, MobCategory.CREATURE).sized(1.3F, 1.1F), "anteater"));
    public static final RegistryObject<EntityType<EntityRockyRoller>> ROCKY_ROLLER = DEF_REG.register("rocky_roller", () -> registerEntity(EntityType.Builder.of(EntityRockyRoller::new, MobCategory.MONSTER).sized(1.2F, 1.45F), "rocky_roller"));
    public static final RegistryObject<EntityType<EntityFlutter>> FLUTTER = DEF_REG.register("flutter", () -> registerEntity(EntityType.Builder.of(EntityFlutter::new, MobCategory.AMBIENT).sized(0.5F, 0.7F), "flutter"));
    public static final RegistryObject<EntityType<EntityPollenBall>> POLLEN_BALL = DEF_REG.register("pollen_ball", () -> registerEntity(EntityType.Builder.of(EntityPollenBall::new, MobCategory.MISC).sized(0.35F, 0.35F).setCustomClientFactory(EntityPollenBall::new).fireImmune(), "pollen_ball"));
    public static final RegistryObject<EntityType<EntityGeladaMonkey>> GELADA_MONKEY = DEF_REG.register("gelada_monkey", () -> registerEntity(EntityType.Builder.of(EntityGeladaMonkey::new, MobCategory.CREATURE).sized(1.2F, 1.2F), "gelada_monkey"));
    public static final RegistryObject<EntityType<EntityJerboa>> JERBOA = DEF_REG.register("jerboa", () -> registerEntity(EntityType.Builder.of(EntityJerboa::new, MobCategory.AMBIENT).sized(0.5F, 0.5F), "jerboa"));
    public static final RegistryObject<EntityType<EntityTerrapin>> TERRAPIN = DEF_REG.register("terrapin", () -> registerEntity(EntityType.Builder.of(EntityTerrapin::new, MobCategory.WATER_AMBIENT).sized(0.75F, 0.45F), "terrapin"));
    public static final RegistryObject<EntityType<EntityCombJelly>> COMB_JELLY = DEF_REG.register("comb_jelly", () -> registerEntity(EntityType.Builder.of(EntityCombJelly::new, MobCategory.WATER_AMBIENT).sized(0.65F, 0.8F), "comb_jelly"));
    public static final RegistryObject<EntityType<EntityCosmicCod>> COSMIC_COD = DEF_REG.register("cosmic_cod", () -> registerEntity(EntityType.Builder.of(EntityCosmicCod::new, MobCategory.AMBIENT).sized(0.85F, 0.4F), "cosmic_cod"));
    public static final RegistryObject<EntityType<EntityBunfungus>> BUNFUNGUS = DEF_REG.register("bunfungus", () -> registerEntity(EntityType.Builder.of(EntityBunfungus::new, MobCategory.CREATURE).sized(1.85F, 2.1F), "bunfungus"));
    public static final RegistryObject<EntityType<EntityBison>> BISON = DEF_REG.register("bison", () -> registerEntity(EntityType.Builder.of(EntityBison::new, MobCategory.CREATURE).sized(2.4F, 2.1F), "bison"));
    public static final RegistryObject<EntityType<EntityGiantSquid>> GIANT_SQUID = DEF_REG.register("giant_squid", () -> registerEntity(EntityType.Builder.of(EntityGiantSquid::new, MobCategory.WATER_CREATURE).sized(0.9F, 1.2F), "giant_squid"));
    public static final RegistryObject<EntityType<EntitySquidGrapple>> SQUID_GRAPPLE = DEF_REG.register("squid_grapple", () -> registerEntity(EntityType.Builder.of(EntitySquidGrapple::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntitySquidGrapple::new).fireImmune(), "squid_grapple"));
    public static final RegistryObject<EntityType<EntitySeaBear>> SEA_BEAR = DEF_REG.register("sea_bear", () -> registerEntity(EntityType.Builder.of(EntitySeaBear::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.99F), "sea_bear"));
    public static final RegistryObject<EntityType<EntityDevilsHolePupfish>> DEVILS_HOLE_PUPFISH = DEF_REG.register("devils_hole_pupfish", () -> registerEntity(EntityType.Builder.of(EntityDevilsHolePupfish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F), "devils_hole_pupfish"));
    public static final RegistryObject<EntityType<EntityCatfish>> CATFISH = DEF_REG.register("catfish", () -> registerEntity(EntityType.Builder.of(EntityCatfish::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.6F), "catfish"));
    public static final RegistryObject<EntityType<EntityFlyingFish>> FLYING_FISH = DEF_REG.register("flying_fish", () -> registerEntity(EntityType.Builder.of(EntityFlyingFish::new, MobCategory.WATER_AMBIENT).sized(0.6F, 0.4F), "flying_fish"));
    public static final RegistryObject<EntityType<EntitySkelewag>> SKELEWAG = DEF_REG.register("skelewag", () -> registerEntity(EntityType.Builder.of(EntitySkelewag::new, MobCategory.MONSTER).sized(2F, 1.2F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "skelewag"));
    public static final RegistryObject<EntityType<EntityRainFrog>> RAIN_FROG = DEF_REG.register("rain_frog", () -> registerEntity(EntityType.Builder.of(EntityRainFrog::new, MobCategory.AMBIENT).sized(0.55F, 0.5F), "rain_frog"));
    public static final RegistryObject<EntityType<EntityPotoo>> POTOO = DEF_REG.register("potoo", () -> registerEntity(EntityType.Builder.of(EntityPotoo::new, MobCategory.CREATURE).sized(0.6F, 0.8F), "potoo"));
    public static final RegistryObject<EntityType<EntityMudskipper>> MUDSKIPPER = DEF_REG.register("mudskipper", () -> registerEntity(EntityType.Builder.of(EntityMudskipper::new, MobCategory.CREATURE).sized(0.7F, 0.44F), "mudskipper"));
    public static final RegistryObject<EntityType<EntityMudBall>> MUD_BALL = DEF_REG.register("mud_ball", () -> registerEntity(EntityType.Builder.of(EntityMudBall::new, MobCategory.MISC).sized(0.35F, 0.35F).setCustomClientFactory(EntityMudBall::new).fireImmune(), "mud_ball"));
    public static final RegistryObject<EntityType<EntityRhinoceros>> RHINOCEROS = DEF_REG.register("rhinoceros", () -> registerEntity(EntityType.Builder.of(EntityRhinoceros::new, MobCategory.CREATURE).sized(2.3F, 2.4F), "rhinoceros"));
    public static final RegistryObject<EntityType<EntitySugarGlider>> SUGAR_GLIDER = DEF_REG.register("sugar_glider", () -> registerEntity(EntityType.Builder.of(EntitySugarGlider::new, MobCategory.CREATURE).sized(0.8F, 0.45F), "sugar_glider"));
    public static final RegistryObject<EntityType<EntityFarseer>> FARSEER = DEF_REG.register("farseer", () -> registerEntity(EntityType.Builder.of(EntityFarseer::new, MobCategory.MONSTER).sized(0.99F, 1.5F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).fireImmune(), "farseer"));
    public static final RegistryObject<EntityType<EntitySkreecher>> SKREECHER = DEF_REG.register("skreecher", () -> registerEntity(EntityType.Builder.of(EntitySkreecher::new, MobCategory.MONSTER).sized(0.99F, 0.95F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "skreecher"));
    public static final RegistryObject<EntityType<EntityUnderminer>> UNDERMINER = DEF_REG.register("underminer", () -> registerEntity(EntityType.Builder.of(EntityUnderminer::new, MobCategory.AMBIENT).sized(0.8F, 1.8F), "underminer"));
    public static final RegistryObject<EntityType<EntityMurmur>> MURMUR = DEF_REG.register("murmur", () -> registerEntity(EntityType.Builder.of(EntityMurmur::new, MobCategory.MONSTER).sized(0.7F, 1.45F), "murmur"));
    public static final RegistryObject<EntityType<EntityMurmurHead>> MURMUR_HEAD = DEF_REG.register("murmur_head", () -> registerEntity(EntityType.Builder.of(EntityMurmurHead::new, MobCategory.MONSTER).sized(0.55F, 0.55F), "murmur_head"));
    public static final RegistryObject<EntityType<EntityTendonSegment>> TENDON_SEGMENT = DEF_REG.register("tendon_segment", () -> registerEntity(EntityType.Builder.of(EntityTendonSegment::new, MobCategory.MISC).sized(0.1F, 0.1F).setCustomClientFactory(EntityTendonSegment::new).fireImmune(), "tendon_segment"));

    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
        return (EntityType) builder.build(entityName);
    }

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        SpawnPlacements.Type spawnsOnLeaves = SpawnPlacements.Type.create("am_leaves", AMEntityRegistry::createLeavesSpawnPlacement);
        SpawnPlacements.register(GRIZZLY_BEAR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ROADRUNNER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRoadrunner::canRoadrunnerSpawn);
        SpawnPlacements.register(BONE_SERPENT.get(), SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBoneSerpent::canBoneSerpentSpawn);
        SpawnPlacements.register(GAZELLE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(CROCODILE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrocodile::canCrocodileSpawn);
        SpawnPlacements.register(FLY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFly::canFlySpawn);
        SpawnPlacements.register(HUMMINGBIRD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityHummingbird::canHummingbirdSpawn);
        SpawnPlacements.register(ORCA.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityOrca::canOrcaSpawn);
        SpawnPlacements.register(SUNBIRD.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySunbird::canSunbirdSpawn);
        SpawnPlacements.register(GORILLA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityGorilla::canGorillaSpawn);
        SpawnPlacements.register(CRIMSON_MOSQUITO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrimsonMosquito::canMosquitoSpawn);
        SpawnPlacements.register(RATTLESNAKE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRattlesnake::canRattlesnakeSpawn);
        SpawnPlacements.register(ENDERGRADE.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEndergrade::canEndergradeSpawn);
        SpawnPlacements.register(HAMMERHEAD_SHARK.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityHammerheadShark::canHammerheadSharkSpawn);
        SpawnPlacements.register(LOBSTER.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLobster::canLobsterSpawn);
        SpawnPlacements.register(KOMODO_DRAGON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKomodoDragon::canKomodoDragonSpawn);
        SpawnPlacements.register(CAPUCHIN_MONKEY.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntityCapuchinMonkey::canCapuchinSpawn);
        SpawnPlacements.register(CENTIPEDE_HEAD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCentipedeHead::canCentipedeSpawn);
        SpawnPlacements.register(WARPED_TOAD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityWarpedToad::canWarpedToadSpawn);
        SpawnPlacements.register(MOOSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMoose::canMooseSpawn);
        SpawnPlacements.register(MIMICUBE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(RACCOON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(BLOBFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBlobfish::canBlobfishSpawn);
        SpawnPlacements.register(SEAL.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeal::canSealSpawn);
        SpawnPlacements.register(COCKROACH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCockroach::canCockroachSpawn);
        SpawnPlacements.register(SHOEBILL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ELEPHANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(SOUL_VULTURE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySoulVulture::canVultureSpawn);
        SpawnPlacements.register(SNOW_LEOPARD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySnowLeopard::canSnowLeopardSpawn);
        SpawnPlacements.register(ALLIGATOR_SNAPPING_TURTLE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAlligatorSnappingTurtle::canTurtleSpawn);
        SpawnPlacements.register(MUNGUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMungus::canMungusSpawn);
        SpawnPlacements.register(MANTIS_SHRIMP.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMantisShrimp::canMantisShrimpSpawn);
        SpawnPlacements.register(GUSTER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGuster::canGusterSpawn);
        SpawnPlacements.register(WARPED_MOSCO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(STRADDLER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStraddler::canStraddlerSpawn);
        SpawnPlacements.register(STRADPOLE.get(), SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStradpole::canStradpoleSpawn);
        SpawnPlacements.register(EMU.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEmu::canEmuSpawn);
        SpawnPlacements.register(PLATYPUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityPlatypus::canPlatypusSpawn);
        SpawnPlacements.register(DROPBEAR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(TASMANIAN_DEVIL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(KANGAROO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKangaroo::canKangarooSpawn);
        SpawnPlacements.register(CACHALOT_WHALE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCachalotWhale::canCachalotWhaleSpawn);
        SpawnPlacements.register(LEAFCUTTER_ANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ENDERIOPHAGE.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEnderiophage::canEnderiophageSpawn);
        SpawnPlacements.register(BALD_EAGLE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityBaldEagle::canEagleSpawn);
        SpawnPlacements.register(TIGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTiger::canTigerSpawn);
        SpawnPlacements.register(TARANTULA_HAWK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTarantulaHawk::canTarantulaHawkSpawn);
        SpawnPlacements.register(VOID_WORM.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityVoidWorm::canVoidWormSpawn);
        SpawnPlacements.register(FRILLED_SHARK.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFrilledShark::canFrilledSharkSpawn);
        SpawnPlacements.register(MIMIC_OCTOPUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMimicOctopus::canMimicOctopusSpawn);
        SpawnPlacements.register(SEAGULL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeagull::canSeagullSpawn);
        SpawnPlacements.register(FROSTSTALKER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFroststalker::canFroststalkerSpawn);
        SpawnPlacements.register(TUSKLIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTusklin::canTusklinSpawn);
        SpawnPlacements.register(LAVIATHAN.get(), SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLaviathan::canLaviathanSpawn);
        SpawnPlacements.register(COSMAW.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCosmaw::canCosmawSpawn);
        SpawnPlacements.register(TOUCAN.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntityToucan::canToucanSpawn);
        SpawnPlacements.register(MANED_WOLF.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityManedWolf::checkAnimalSpawnRules);
        SpawnPlacements.register(ANACONDA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnaconda::canAnacondaSpawn);
        SpawnPlacements.register(ANTEATER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnteater::canAnteaterSpawn);
        SpawnPlacements.register(ROCKY_ROLLER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRockyRoller::checkRockyRollerSpawnRules);
        SpawnPlacements.register(FLUTTER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFlutter::canFlutterSpawn);
        SpawnPlacements.register(GELADA_MONKEY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGeladaMonkey::checkAnimalSpawnRules);
        SpawnPlacements.register(JERBOA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityJerboa::canJerboaSpawn);
        SpawnPlacements.register(TERRAPIN.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTerrapin::canTerrapinSpawn);
        SpawnPlacements.register(COMB_JELLY.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCombJelly::canCombJellySpawn);
        SpawnPlacements.register(BUNFUNGUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBunfungus::canBunfungusSpawn);
        SpawnPlacements.register(BISON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBison::checkAnimalSpawnRules);
        SpawnPlacements.register(GIANT_SQUID.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGiantSquid::canGiantSquidSpawn);
        SpawnPlacements.register(DEVILS_HOLE_PUPFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityDevilsHolePupfish::canPupfishSpawn);
        SpawnPlacements.register(CATFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCatfish::canCatfishSpawn);
        SpawnPlacements.register(FLYING_FISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacements.register(SKELEWAG.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkelewag::canSkelewagSpawn);
        SpawnPlacements.register(RAIN_FROG.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRainFrog::canRainFrogSpawn);
        SpawnPlacements.register(POTOO.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntityPotoo::canPotooSpawn);
        SpawnPlacements.register(MUDSKIPPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMudskipper::canMudskipperSpawn);
        SpawnPlacements.register(RHINOCEROS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRhinoceros::checkAnimalSpawnRules);
        SpawnPlacements.register(SUGAR_GLIDER.get(), spawnsOnLeaves, Heightmap.Types.MOTION_BLOCKING, EntitySugarGlider::canSugarGliderSpawn);
        SpawnPlacements.register(FARSEER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFarseer::checkFarseerSpawnRules);
        SpawnPlacements.register(SKREECHER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySkreecher::checkSkreecherSpawnRules);
        SpawnPlacements.register(UNDERMINER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityUnderminer::checkUnderminerSpawnRules);
        SpawnPlacements.register(MURMUR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMurmur::checkMurmurSpawnRules);
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
        if (!blockstate1.isValidSpawn(level, blockpos1, SpawnPlacements.Type.ON_GROUND, type) && !blockstate1.is(BlockTags.LEAVES)) {
            return false;
        } else {
            return NaturalSpawner.isValidEmptySpawnBlock(level, pos, blockstate, fluidstate, type) && NaturalSpawner.isValidEmptySpawnBlock(level, blockpos, level.getBlockState(blockpos), level.getFluidState(blockpos), type);
        }
    }

}
