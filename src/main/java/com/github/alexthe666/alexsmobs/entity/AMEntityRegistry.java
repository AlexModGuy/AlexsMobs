package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.base.Predicates;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMEntityRegistry {

    public static final EntityType<EntityGrizzlyBear> GRIZZLY_BEAR = registerEntity(EntityType.Builder.create(EntityGrizzlyBear::new, EntityClassification.CREATURE).size(1.8F, 2F), "grizzly_bear");
    public static final EntityType<EntityRoadrunner> ROADRUNNER = registerEntity(EntityType.Builder.create(EntityRoadrunner::new, EntityClassification.CREATURE).size(0.45F, 0.75F), "roadrunner");
    public static final EntityType<EntityBoneSerpent> BONE_SERPENT = registerEntity(EntityType.Builder.create(EntityBoneSerpent::new, EntityClassification.MONSTER).size(1.2F, 1.15F).immuneToFire(), "bone_serpent");
    public static final EntityType<EntityBoneSerpentPart> BONE_SERPENT_PART = registerEntity(EntityType.Builder.create(EntityBoneSerpentPart::new, EntityClassification.MONSTER).size(1F, 1F).immuneToFire(), "bone_serpent_part");
    public static final EntityType<EntityGazelle> GAZELLE = registerEntity(EntityType.Builder.create(EntityGazelle::new, EntityClassification.CREATURE).size(0.85F, 1.25F), "gazelle");
    public static final EntityType<EntityCrocodile> CROCODILE = registerEntity(EntityType.Builder.create(EntityCrocodile::new, EntityClassification.WATER_CREATURE).size(2.15F, 0.75F), "crocodile");
    public static final EntityType<EntityFly> FLY = registerEntity(EntityType.Builder.create(EntityFly::new, EntityClassification.AMBIENT).size(0.35F, 0.35F), "fly");
    public static final EntityType<EntityHummingbird> HUMMINGBIRD = registerEntity(EntityType.Builder.create(EntityHummingbird::new, EntityClassification.CREATURE).size(0.45F, 0.45F), "hummingbird");
    public static final EntityType<EntityOrca> ORCA = registerEntity(EntityType.Builder.create(EntityOrca::new, EntityClassification.WATER_CREATURE).size(3.75F, 1.75F), "orca");
    public static final EntityType<EntitySunbird> SUNBIRD = registerEntity(EntityType.Builder.create(EntitySunbird::new, EntityClassification.CREATURE).size(1.75F, 0.75F).immuneToFire().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "sunbird");
    public static final EntityType<EntityGorilla> GORILLA = registerEntity(EntityType.Builder.create(EntityGorilla::new, EntityClassification.CREATURE).size(1.15F, 1.35F), "gorilla");
    public static final EntityType<EntityCrimsonMosquito> CRIMSON_MOSQUITO = registerEntity(EntityType.Builder.create(EntityCrimsonMosquito::new, EntityClassification.MONSTER).size(1.25F, 1.15F).immuneToFire(), "crimson_mosquito");
    public static final EntityType<EntityMosquitoSpit> MOSQUITO_SPIT = registerEntity(EntityType.Builder.create(EntityMosquitoSpit::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityMosquitoSpit::new).immuneToFire(), "mosquito_spit");
    public static final EntityType<EntityRattlesnake> RATTLESNAKE = registerEntity(EntityType.Builder.create(EntityRattlesnake::new, EntityClassification.CREATURE).size(0.95F, 0.35F), "rattlesnake");
    public static final EntityType<EntityEndergrade> ENDERGRADE = registerEntity(EntityType.Builder.create(EntityEndergrade::new, EntityClassification.CREATURE).size(0.95F, 0.85F), "endergrade");
    public static final EntityType<EntityHammerheadShark> HAMMERHEAD_SHARK = registerEntity(EntityType.Builder.create(EntityHammerheadShark::new, EntityClassification.WATER_CREATURE).size(2.4F, 1.25F), "hammerhead_shark");
    public static final EntityType<EntitySharkToothArrow> SHARK_TOOTH_ARROW = registerEntity(EntityType.Builder.create(EntitySharkToothArrow::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntitySharkToothArrow::new), "shark_tooth_arrow");
    public static final EntityType<EntityLobster> LOBSTER = registerEntity(EntityType.Builder.create(EntityLobster::new, EntityClassification.WATER_AMBIENT).size(0.7F, 0.4F), "lobster");
    public static final EntityType<EntityKomodoDragon> KOMODO_DRAGON = registerEntity(EntityType.Builder.create(EntityKomodoDragon::new, EntityClassification.CREATURE).size(2.15F, 0.75F), "komodo_dragon");
    public static final EntityType<EntityCapuchinMonkey> CAPUCHIN_MONKEY = registerEntity(EntityType.Builder.create(EntityCapuchinMonkey::new, EntityClassification.CREATURE).size(0.65F, 0.75F), "capuchin_monkey");
    public static final EntityType<EntityTossedItem> TOSSED_ITEM = registerEntity(EntityType.Builder.create(EntityTossedItem::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityTossedItem::new).immuneToFire(), "tossed_item");
    public static final EntityType<EntityCentipedeHead> CENTIPEDE_HEAD = registerEntity(EntityType.Builder.create(EntityCentipedeHead::new, EntityClassification.CREATURE).size(0.9F, 0.9F), "centipede_head");
    public static final EntityType<EntityCentipedeBody> CENTIPEDE_BODY = registerEntity(EntityType.Builder.create(EntityCentipedeBody::new, EntityClassification.CREATURE).size(0.9F, 0.9F).immuneToFire(), "centipede_body");
    public static final EntityType<EntityCentipedeTail> CENTIPEDE_TAIL = registerEntity(EntityType.Builder.create(EntityCentipedeTail::new, EntityClassification.CREATURE).size(0.9F, 0.9F).immuneToFire(), "centipede_tail");
    public static final EntityType<EntityWarpedToad> WARPED_TOAD = registerEntity(EntityType.Builder.create(EntityWarpedToad::new, EntityClassification.CREATURE).size(0.9F, 1.4F).immuneToFire().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "warped_toad");
    public static final EntityType<EntityMoose> MOOSE = registerEntity(EntityType.Builder.create(EntityMoose::new, EntityClassification.CREATURE).size(1.7F, 2.4F), "moose");
    public static final EntityType<EntityMimicube> MIMICUBE = registerEntity(EntityType.Builder.create(EntityMimicube::new, EntityClassification.MONSTER).size(0.9F, 0.9F), "mimicube");
    public static final EntityType<EntityRaccoon> RACCOON = registerEntity(EntityType.Builder.create(EntityRaccoon::new, EntityClassification.CREATURE).size(0.8F, 0.9F), "raccoon");
    public static final EntityType<EntityBlobfish> BLOBFISH = registerEntity(EntityType.Builder.create(EntityBlobfish::new, EntityClassification.WATER_AMBIENT).size(0.7F, 0.45F), "blobfish");
    public static final EntityType<EntitySeal> SEAL = registerEntity(EntityType.Builder.create(EntitySeal::new, EntityClassification.CREATURE).size(1.3F, 0.7F), "seal");
    public static final EntityType<EntityCockroach> COCKROACH = registerEntity(EntityType.Builder.create(EntityCockroach::new, EntityClassification.AMBIENT).size(0.7F, 0.3F), "cockroach");
    public static final EntityType<EntityCockroachEgg> COCKROACH_EGG = registerEntity(EntityType.Builder.create(EntityCockroachEgg::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityCockroachEgg::new).immuneToFire(), "cockroach_egg");
    public static final EntityType<EntityShoebill> SHOEBILL = registerEntity(EntityType.Builder.create(EntityShoebill::new, EntityClassification.CREATURE).size(0.8F, 1.5F).setUpdateInterval(1), "shoebill");
    public static final EntityType<EntityElephant> ELEPHANT = registerEntity(EntityType.Builder.create(EntityElephant::new, EntityClassification.CREATURE).size(2.1F, 2.5F).setUpdateInterval(1), "elephant");
    public static final EntityType<EntitySoulVulture> SOUL_VULTURE = registerEntity(EntityType.Builder.create(EntitySoulVulture::new, EntityClassification.MONSTER).size(0.9F, 1.3F).setUpdateInterval(1).immuneToFire(), "soul_vulture");
    public static final EntityType<EntitySnowLeopard> SNOW_LEOPARD = registerEntity(EntityType.Builder.create(EntitySnowLeopard::new, EntityClassification.CREATURE).size(1.2F, 1.3F), "snow_leopard");
    public static final EntityType<EntitySpectre> SPECTRE = registerEntity(EntityType.Builder.create(EntitySpectre::new, EntityClassification.CREATURE).size(3.15F, 0.8F).immuneToFire().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "spectre");
    public static final EntityType<EntityCrow> CROW = registerEntity(EntityType.Builder.create(EntityCrow::new, EntityClassification.CREATURE).size(0.45F, 0.45F), "crow");
    public static final EntityType<EntityAlligatorSnappingTurtle> ALLIGATOR_SNAPPING_TURTLE = registerEntity(EntityType.Builder.create(EntityAlligatorSnappingTurtle::new, EntityClassification.CREATURE).size(1.25F, 0.65F), "alligator_snapping_turtle");
    public static final EntityType<EntityMungus> MUNGUS = registerEntity(EntityType.Builder.create(EntityMungus::new, EntityClassification.CREATURE).size(0.75F, 1.45F), "mungus");
    public static final EntityType<EntityMantisShrimp> MANTIS_SHRIMP = registerEntity(EntityType.Builder.create(EntityMantisShrimp::new, EntityClassification.WATER_CREATURE).size(1.25F, 1.2F), "mantis_shrimp");
    public static final EntityType<EntityGuster> GUSTER = registerEntity(EntityType.Builder.create(EntityGuster::new, EntityClassification.MONSTER).size(1.42F, 2.35F).immuneToFire(), "guster");
    public static final EntityType<EntitySandShot> SAND_SHOT = registerEntity(EntityType.Builder.create(EntitySandShot::new, EntityClassification.MISC).size(0.95F, 0.65F).setCustomClientFactory(EntitySandShot::new).immuneToFire(), "sand_shot");
    public static final EntityType<EntityGust> GUST = registerEntity(EntityType.Builder.create(EntityGust::new, EntityClassification.MISC).size(0.8F, 0.8F).setCustomClientFactory(EntityGust::new).immuneToFire(), "gust");
    public static final EntityType<EntityWarpedMosco> WARPED_MOSCO = registerEntity(EntityType.Builder.create(EntityWarpedMosco::new, EntityClassification.MONSTER).size(1.99F, 3.25F).immuneToFire(), "warped_mosco");
    public static final EntityType<EntityHemolymph> HEMOLYMPH = registerEntity(EntityType.Builder.create(EntityHemolymph::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityHemolymph::new).immuneToFire(), "hemolymph");
    public static final EntityType<EntityStraddler> STRADDLER = registerEntity(EntityType.Builder.create(EntityStraddler::new, EntityClassification.MONSTER).size(1.65F, 3F).immuneToFire(), "straddler");
    public static final EntityType<EntityStradpole> STRADPOLE = registerEntity(EntityType.Builder.create(EntityStradpole::new, EntityClassification.WATER_AMBIENT).size(0.5F, 0.5F).immuneToFire(), "stradpole");
    public static final EntityType<EntityStraddleboard> STRADDLEBOARD = registerEntity(EntityType.Builder.create(EntityStraddleboard::new, EntityClassification.MISC).size(1.5F, 0.35F).setCustomClientFactory(EntityStraddleboard::new).immuneToFire(), "straddleboard");
    public static final EntityType<EntityEmu> EMU = registerEntity(EntityType.Builder.create(EntityEmu::new, EntityClassification.CREATURE).size(1.1F, 1.8F), "emu");
    public static final EntityType<EntityEmuEgg> EMU_EGG = registerEntity(EntityType.Builder.create(EntityEmuEgg::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityEmuEgg::new).immuneToFire(), "emu_egg");
    public static final EntityType<EntityPlatypus> PLATYPUS = registerEntity(EntityType.Builder.create(EntityPlatypus::new, EntityClassification.CREATURE).size(0.8F, 0.5F), "platypus");
    public static final EntityType<EntityDropBear> DROPBEAR = registerEntity(EntityType.Builder.create(EntityDropBear::new, EntityClassification.MONSTER).size(1.65F, 1.5F).immuneToFire(), "dropbear");
    public static final EntityType<EntityTasmanianDevil> TASMANIAN_DEVIL = registerEntity(EntityType.Builder.create(EntityTasmanianDevil::new, EntityClassification.CREATURE).size(0.7F, 0.8F), "tasmanian_devil");
    public static final EntityType<EntityKangaroo> KANGAROO = registerEntity(EntityType.Builder.create(EntityKangaroo::new, EntityClassification.CREATURE).size(1.65F, 1.5F), "kangaroo");
    public static final EntityType<EntityCachalotWhale> CACHALOT_WHALE = registerEntity(EntityType.Builder.create(EntityCachalotWhale::new, EntityClassification.WATER_CREATURE).size(9F, 4.0F), "cachalot_whale");
    public static final EntityType<EntityCachalotEcho> CACHALOT_ECHO = registerEntity(EntityType.Builder.create(EntityCachalotEcho::new, EntityClassification.MISC).size(2F, 2F).setCustomClientFactory(EntityCachalotEcho::new).immuneToFire(), "cachalot_echo");
    public static final EntityType<EntityLeafcutterAnt> LEAFCUTTER_ANT = registerEntity(EntityType.Builder.create(EntityLeafcutterAnt::new, EntityClassification.CREATURE).size(0.8F, 0.5F), "leafcutter_ant");
    public static final EntityType<EntityEnderiophage> ENDERIOPHAGE = registerEntity(EntityType.Builder.create(EntityEnderiophage::new, EntityClassification.CREATURE).size(0.85F, 1.95F).setUpdateInterval(1), "enderiophage");
    public static final EntityType<EntityEnderiophageRocket> ENDERIOPHAGE_ROCKET = registerEntity(EntityType.Builder.create(EntityEnderiophageRocket::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityEnderiophageRocket::new).immuneToFire(), "enderiophage_rocket");
    public static final EntityType<EntityBaldEagle> BALD_EAGLE = registerEntity(EntityType.Builder.create(EntityBaldEagle::new, EntityClassification.CREATURE).size(0.5F, 0.95F).setUpdateInterval(1).setTrackingRange(14), "bald_eagle");
    public static final EntityType<EntityTiger> TIGER = registerEntity(EntityType.Builder.create(EntityTiger::new, EntityClassification.CREATURE).size(1.45F, 1.2F), "tiger");
    public static final EntityType<EntityTarantulaHawk> TARANTULA_HAWK = registerEntity(EntityType.Builder.create(EntityTarantulaHawk::new, EntityClassification.CREATURE).size(1.2F, 0.9F), "tarantula_hawk");
    public static final EntityType<EntityVoidWorm> VOID_WORM = registerEntity(EntityType.Builder.create(EntityVoidWorm::new, EntityClassification.MONSTER).size(3.4F, 3F).immuneToFire().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm");
    public static final EntityType<EntityVoidWormPart> VOID_WORM_PART = registerEntity(EntityType.Builder.create(EntityVoidWormPart::new, EntityClassification.MONSTER).size(1.2F, 1.35F).immuneToFire().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm_part");
    public static final EntityType<EntityVoidWormShot> VOID_WORM_SHOT = registerEntity(EntityType.Builder.create(EntityVoidWormShot::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityVoidWormShot::new).immuneToFire(), "void_worm_shot");
    public static final EntityType<EntityVoidPortal> VOID_PORTAL = registerEntity(EntityType.Builder.create(EntityVoidPortal::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityVoidPortal::new).immuneToFire(), "void_portal");
    public static final EntityType<EntityFrilledShark> FRILLED_SHARK = registerEntity(EntityType.Builder.create(EntityFrilledShark::new, EntityClassification.WATER_CREATURE).size(1.3F, 0.4F), "frilled_shark");
    public static final EntityType<EntityMimicOctopus> MIMIC_OCTOPUS = registerEntity(EntityType.Builder.create(EntityMimicOctopus::new, EntityClassification.WATER_CREATURE).size(0.9F, 0.6F), "mimic_octopus");

    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
        ResourceLocation nameLoc = new ResourceLocation(AlexsMobs.MODID, entityName);
        return (EntityType) builder.build(entityName).setRegistryName(nameLoc);
    }

    static {
        EntitySpawnPlacementRegistry.register(GRIZZLY_BEAR, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        EntitySpawnPlacementRegistry.register(ROADRUNNER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityRoadrunner::canRoadrunnerSpawn);
        EntitySpawnPlacementRegistry.register(BONE_SERPENT, EntitySpawnPlacementRegistry.PlacementType.IN_LAVA, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityBoneSerpent::canBoneSerpentSpawn);
        EntitySpawnPlacementRegistry.register(GAZELLE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        EntitySpawnPlacementRegistry.register(CROCODILE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityCrocodile::canCrocodileSpawn);
        EntitySpawnPlacementRegistry.register(FLY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityFly::canFlySpawn);
        EntitySpawnPlacementRegistry.register(HUMMINGBIRD, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, EntityHummingbird::canHummingbirdSpawn);
        EntitySpawnPlacementRegistry.register(ORCA, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityOrca::canOrcaSpawn);
        EntitySpawnPlacementRegistry.register(SUNBIRD, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntitySunbird::canSunbirdSpawn);
        EntitySpawnPlacementRegistry.register(GORILLA, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, EntityGorilla::canGorillaSpawn);
        EntitySpawnPlacementRegistry.register(CRIMSON_MOSQUITO, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityCrimsonMosquito::canMosquitoSpawn);
        EntitySpawnPlacementRegistry.register(RATTLESNAKE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityRattlesnake::canRattlesnakeSpawn);
        EntitySpawnPlacementRegistry.register(ENDERGRADE, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityEndergrade::canEndergradeSpawn);
        EntitySpawnPlacementRegistry.register(HAMMERHEAD_SHARK, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityHammerheadShark::canHammerheadSharkSpawn);
        EntitySpawnPlacementRegistry.register(LOBSTER, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityLobster::canLobsterSpawn);
        EntitySpawnPlacementRegistry.register(KOMODO_DRAGON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityKomodoDragon::canKomodoDragonSpawn);
        EntitySpawnPlacementRegistry.register(CAPUCHIN_MONKEY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, EntityCapuchinMonkey::canCapuchinSpawn);
        EntitySpawnPlacementRegistry.register(CENTIPEDE_HEAD, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityCentipedeHead::canCentipedeSpawn);
        EntitySpawnPlacementRegistry.register(WARPED_TOAD, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, EntityWarpedToad::canWarpedToadSpawn);
        EntitySpawnPlacementRegistry.register(MOOSE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMoose::canMooseSpawn);
        EntitySpawnPlacementRegistry.register(MIMICUBE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
        EntitySpawnPlacementRegistry.register(RACCOON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        EntitySpawnPlacementRegistry.register(BLOBFISH, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityBlobfish::canBlobfishSpawn);
        EntitySpawnPlacementRegistry.register(SEAL, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntitySeal::canSealSpawn);
        EntitySpawnPlacementRegistry.register(COCKROACH, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityCockroach::canCockroachSpawn);
        EntitySpawnPlacementRegistry.register(SHOEBILL, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        EntitySpawnPlacementRegistry.register(ELEPHANT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        EntitySpawnPlacementRegistry.register(SOUL_VULTURE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntitySoulVulture::canVultureSpawn);
        EntitySpawnPlacementRegistry.register(ALLIGATOR_SNAPPING_TURTLE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAlligatorSnappingTurtle::canTurtleSpawn);
        EntitySpawnPlacementRegistry.register(MUNGUS, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMungus::canMungusSpawn);
        EntitySpawnPlacementRegistry.register(MANTIS_SHRIMP, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMantisShrimp::canMantisShrimpSpawn);
        EntitySpawnPlacementRegistry.register(GUSTER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityGuster::canGusterSpawn);
        EntitySpawnPlacementRegistry.register(WARPED_MOSCO, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::canMonsterSpawn);
        EntitySpawnPlacementRegistry.register(STRADDLER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityStraddler::canStraddlerSpawn);
        EntitySpawnPlacementRegistry.register(STRADPOLE, EntitySpawnPlacementRegistry.PlacementType.IN_LAVA, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityStradpole::canStradpoleSpawn);
        EntitySpawnPlacementRegistry.register(EMU, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityEmu::canEmuSpawn);
        EntitySpawnPlacementRegistry.register(PLATYPUS, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityPlatypus::canPlatypusSpawn);
        EntitySpawnPlacementRegistry.register(DROPBEAR, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::canMonsterSpawn);
        EntitySpawnPlacementRegistry.register(TASMANIAN_DEVIL, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        EntitySpawnPlacementRegistry.register(KANGAROO, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityKangaroo::canKangarooSpawn);
        EntitySpawnPlacementRegistry.register(CACHALOT_WHALE, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityCachalotWhale::canCachalotWhaleSpawn);
        EntitySpawnPlacementRegistry.register(LEAFCUTTER_ANT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
        EntitySpawnPlacementRegistry.register(ENDERIOPHAGE, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityEnderiophage::canEnderiophageSpawn);
        EntitySpawnPlacementRegistry.register(BALD_EAGLE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, EntityBaldEagle::canEagleSpawn);
        EntitySpawnPlacementRegistry.register(TIGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityTiger::canTigerSpawn);
        EntitySpawnPlacementRegistry.register(TARANTULA_HAWK, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityTarantulaHawk::canTarantulaHawkSpawn);
        EntitySpawnPlacementRegistry.register(VOID_WORM, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityVoidWorm::canVoidWormSpawn);
        EntitySpawnPlacementRegistry.register(FRILLED_SHARK, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityFrilledShark::canFrilledSharkSpawn);
        EntitySpawnPlacementRegistry.register(MIMIC_OCTOPUS, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMimicOctopus::canMimicOctopusSpawn);
    }

        @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        try {
            for (Field f : AMEntityRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof EntityType) {
                    event.getRegistry().register((EntityType) obj);
                } else if (obj instanceof EntityType[]) {
                    for (EntityType type : (EntityType[]) obj) {
                        event.getRegistry().register(type);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        initializeAttributes();
    }

    private static void initializeAttributes() {
        GlobalEntityTypeAttributes.put(GRIZZLY_BEAR, EntityGrizzlyBear.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(ROADRUNNER, EntityRoadrunner.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(BONE_SERPENT, EntityBoneSerpent.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(BONE_SERPENT_PART, EntityBoneSerpentPart.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(GAZELLE, EntityGazelle.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CROCODILE, EntityCrocodile.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(FLY, EntityFly.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(HUMMINGBIRD, EntityHummingbird.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(ORCA, EntityOrca.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(SUNBIRD, EntitySunbird.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(GORILLA, EntityGorilla.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CRIMSON_MOSQUITO, EntityCrimsonMosquito.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(RATTLESNAKE, EntityRattlesnake.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(ENDERGRADE, EntityEndergrade.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(HAMMERHEAD_SHARK, EntityHammerheadShark.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(LOBSTER, EntityLobster.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(KOMODO_DRAGON, EntityKomodoDragon.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CAPUCHIN_MONKEY, EntityCapuchinMonkey.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CENTIPEDE_HEAD, EntityCentipedeHead.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CENTIPEDE_BODY, EntityCentipedeBody.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CENTIPEDE_TAIL, EntityCentipedeTail.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(WARPED_TOAD, EntityWarpedToad.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(MOOSE, EntityMoose.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(MIMICUBE, EntityMimicube.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(RACCOON, EntityRaccoon.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(BLOBFISH, EntityBlobfish.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(SEAL, EntitySeal.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(COCKROACH, EntityCockroach.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(SHOEBILL, EntityShoebill.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(ELEPHANT, EntityElephant.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(SOUL_VULTURE, EntitySoulVulture.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(SNOW_LEOPARD, EntitySnowLeopard.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(SPECTRE, EntitySpectre.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CROW, EntityCrow.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(ALLIGATOR_SNAPPING_TURTLE, EntityAlligatorSnappingTurtle.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(MUNGUS, EntityMungus.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(MANTIS_SHRIMP, EntityMantisShrimp.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(GUSTER, EntityGuster.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(WARPED_MOSCO, EntityWarpedMosco.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(STRADDLER, EntityStraddler.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(STRADPOLE, EntityStradpole.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(EMU, EntityEmu.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(PLATYPUS, EntityPlatypus.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(DROPBEAR, EntityDropBear.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(TASMANIAN_DEVIL, EntityTasmanianDevil.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(KANGAROO, EntityKangaroo.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(CACHALOT_WHALE, EntityCachalotWhale.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(LEAFCUTTER_ANT, EntityLeafcutterAnt.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(ENDERIOPHAGE, EntityEnderiophage.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(BALD_EAGLE, EntityBaldEagle.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(TIGER, EntityTiger.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(TARANTULA_HAWK, EntityTarantulaHawk.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(VOID_WORM, EntityVoidWorm.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(VOID_WORM_PART, EntityVoidWormPart.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(FRILLED_SHARK, EntityFrilledShark.bakeAttributes().create());
        GlobalEntityTypeAttributes.put(MIMIC_OCTOPUS, EntityMimicOctopus.bakeAttributes().create());

    }

    public static Predicate<LivingEntity> buildPredicateFromTag(ITag entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().isContained(entityTag);
        }
    }

    public static Predicate<LivingEntity> buildPredicateFromTagTameable(ITag entityTag, LivingEntity owner){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().isContained(entityTag) && !owner.isOnSameTeam(e);
        }
    }

    public static boolean rollSpawn(int rolls, Random random, SpawnReason reason){
        if(reason == SpawnReason.SPAWNER){
            return true;
        }else{
            return rolls <= 0 || random.nextInt(rolls) == 0;
        }
    }

}
