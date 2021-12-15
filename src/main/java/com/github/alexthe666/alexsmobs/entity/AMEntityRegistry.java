package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.google.common.base.Predicates;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
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

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMEntityRegistry {

    public static final EntityType<EntityGrizzlyBear> GRIZZLY_BEAR = registerEntity(EntityType.Builder.of(EntityGrizzlyBear::new, MobCategory.CREATURE).sized(1.6F, 1.8F), "grizzly_bear");
    public static final EntityType<EntityRoadrunner> ROADRUNNER = registerEntity(EntityType.Builder.of(EntityRoadrunner::new, MobCategory.CREATURE).sized(0.45F, 0.75F), "roadrunner");
    public static final EntityType<EntityBoneSerpent> BONE_SERPENT = registerEntity(EntityType.Builder.of(EntityBoneSerpent::new, MobCategory.MONSTER).sized(1.2F, 1.15F).fireImmune(), "bone_serpent");
    public static final EntityType<EntityBoneSerpentPart> BONE_SERPENT_PART = registerEntity(EntityType.Builder.of(EntityBoneSerpentPart::new, MobCategory.MONSTER).sized(1F, 1F).fireImmune(), "bone_serpent_part");
    public static final EntityType<EntityGazelle> GAZELLE = registerEntity(EntityType.Builder.of(EntityGazelle::new, MobCategory.CREATURE).sized(0.85F, 1.25F), "gazelle");
    public static final EntityType<EntityCrocodile> CROCODILE = registerEntity(EntityType.Builder.of(EntityCrocodile::new, MobCategory.WATER_CREATURE).sized(2.15F, 0.75F), "crocodile");
    public static final EntityType<EntityFly> FLY = registerEntity(EntityType.Builder.of(EntityFly::new, MobCategory.AMBIENT).sized(0.35F, 0.35F), "fly");
    public static final EntityType<EntityHummingbird> HUMMINGBIRD = registerEntity(EntityType.Builder.of(EntityHummingbird::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "hummingbird");
    public static final EntityType<EntityOrca> ORCA = registerEntity(EntityType.Builder.of(EntityOrca::new, MobCategory.WATER_CREATURE).sized(3.75F, 1.75F), "orca");
    public static final EntityType<EntitySunbird> SUNBIRD = registerEntity(EntityType.Builder.of(EntitySunbird::new, MobCategory.CREATURE).sized(1.75F, 0.75F).fireImmune().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "sunbird");
    public static final EntityType<EntityGorilla> GORILLA = registerEntity(EntityType.Builder.of(EntityGorilla::new, MobCategory.CREATURE).sized(1.15F, 1.35F), "gorilla");
    public static final EntityType<EntityCrimsonMosquito> CRIMSON_MOSQUITO = registerEntity(EntityType.Builder.of(EntityCrimsonMosquito::new, MobCategory.MONSTER).sized(1.25F, 1.15F).fireImmune(), "crimson_mosquito");
    public static final EntityType<EntityMosquitoSpit> MOSQUITO_SPIT = registerEntity(EntityType.Builder.of(EntityMosquitoSpit::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityMosquitoSpit::new).fireImmune(), "mosquito_spit");
    public static final EntityType<EntityRattlesnake> RATTLESNAKE = registerEntity(EntityType.Builder.of(EntityRattlesnake::new, MobCategory.CREATURE).sized(0.95F, 0.35F), "rattlesnake");
    public static final EntityType<EntityEndergrade> ENDERGRADE = registerEntity(EntityType.Builder.of(EntityEndergrade::new, MobCategory.CREATURE).sized(0.95F, 0.85F), "endergrade");
    public static final EntityType<EntityHammerheadShark> HAMMERHEAD_SHARK = registerEntity(EntityType.Builder.of(EntityHammerheadShark::new, MobCategory.WATER_CREATURE).sized(2.4F, 1.25F), "hammerhead_shark");
    public static final EntityType<EntitySharkToothArrow> SHARK_TOOTH_ARROW = registerEntity(EntityType.Builder.of(EntitySharkToothArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntitySharkToothArrow::new), "shark_tooth_arrow");
    public static final EntityType<EntityLobster> LOBSTER = registerEntity(EntityType.Builder.of(EntityLobster::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F), "lobster");
    public static final EntityType<EntityKomodoDragon> KOMODO_DRAGON = registerEntity(EntityType.Builder.of(EntityKomodoDragon::new, MobCategory.CREATURE).sized(2.15F, 0.75F), "komodo_dragon");
    public static final EntityType<EntityCapuchinMonkey> CAPUCHIN_MONKEY = registerEntity(EntityType.Builder.of(EntityCapuchinMonkey::new, MobCategory.CREATURE).sized(0.65F, 0.75F), "capuchin_monkey");
    public static final EntityType<EntityTossedItem> TOSSED_ITEM = registerEntity(EntityType.Builder.of(EntityTossedItem::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityTossedItem::new).fireImmune(), "tossed_item");
    public static final EntityType<EntityCentipedeHead> CENTIPEDE_HEAD = registerEntity(EntityType.Builder.of(EntityCentipedeHead::new, MobCategory.CREATURE).sized(0.9F, 0.9F), "centipede_head");
    public static final EntityType<EntityCentipedeBody> CENTIPEDE_BODY = registerEntity(EntityType.Builder.of(EntityCentipedeBody::new, MobCategory.CREATURE).sized(0.9F, 0.9F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "centipede_body");
    public static final EntityType<EntityCentipedeTail> CENTIPEDE_TAIL = registerEntity(EntityType.Builder.of(EntityCentipedeTail::new, MobCategory.CREATURE).sized(0.9F, 0.9F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "centipede_tail");
    public static final EntityType<EntityWarpedToad> WARPED_TOAD = registerEntity(EntityType.Builder.of(EntityWarpedToad::new, MobCategory.CREATURE).sized(0.9F, 1.4F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "warped_toad");
    public static final EntityType<EntityMoose> MOOSE = registerEntity(EntityType.Builder.of(EntityMoose::new, MobCategory.CREATURE).sized(1.7F, 2.4F), "moose");
    public static final EntityType<EntityMimicube> MIMICUBE = registerEntity(EntityType.Builder.of(EntityMimicube::new, MobCategory.MONSTER).sized(0.9F, 0.9F), "mimicube");
    public static final EntityType<EntityRaccoon> RACCOON = registerEntity(EntityType.Builder.of(EntityRaccoon::new, MobCategory.CREATURE).sized(0.8F, 0.9F), "raccoon");
    public static final EntityType<EntityBlobfish> BLOBFISH = registerEntity(EntityType.Builder.of(EntityBlobfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.45F), "blobfish");
    public static final EntityType<EntitySeal> SEAL = registerEntity(EntityType.Builder.of(EntitySeal::new, MobCategory.CREATURE).sized(1.3F, 0.7F), "seal");
    public static final EntityType<EntityCockroach> COCKROACH = registerEntity(EntityType.Builder.of(EntityCockroach::new, MobCategory.AMBIENT).sized(0.7F, 0.3F), "cockroach");
    public static final EntityType<EntityCockroachEgg> COCKROACH_EGG = registerEntity(EntityType.Builder.of(EntityCockroachEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityCockroachEgg::new).fireImmune(), "cockroach_egg");
    public static final EntityType<EntityShoebill> SHOEBILL = registerEntity(EntityType.Builder.of(EntityShoebill::new, MobCategory.CREATURE).sized(0.8F, 1.5F).setUpdateInterval(1), "shoebill");
    public static final EntityType<EntityElephant> ELEPHANT = registerEntity(EntityType.Builder.of(EntityElephant::new, MobCategory.CREATURE).sized(3.1F, 3.5F).setUpdateInterval(1), "elephant");
    public static final EntityType<EntitySoulVulture> SOUL_VULTURE = registerEntity(EntityType.Builder.of(EntitySoulVulture::new, MobCategory.MONSTER).sized(0.9F, 1.3F).setUpdateInterval(1).fireImmune(), "soul_vulture");
    public static final EntityType<EntitySnowLeopard> SNOW_LEOPARD = registerEntity(EntityType.Builder.of(EntitySnowLeopard::new, MobCategory.CREATURE).sized(1.2F, 1.3F).immuneTo(Blocks.POWDER_SNOW), "snow_leopard");
    public static final EntityType<EntitySpectre> SPECTRE = registerEntity(EntityType.Builder.of(EntitySpectre::new, MobCategory.CREATURE).sized(3.15F, 0.8F).fireImmune().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "spectre");
    public static final EntityType<EntityCrow> CROW = registerEntity(EntityType.Builder.of(EntityCrow::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "crow");
    public static final EntityType<EntityAlligatorSnappingTurtle> ALLIGATOR_SNAPPING_TURTLE = registerEntity(EntityType.Builder.of(EntityAlligatorSnappingTurtle::new, MobCategory.CREATURE).sized(1.25F, 0.65F), "alligator_snapping_turtle");
    public static final EntityType<EntityMungus> MUNGUS = registerEntity(EntityType.Builder.of(EntityMungus::new, MobCategory.CREATURE).sized(0.75F, 1.45F), "mungus");
    public static final EntityType<EntityMantisShrimp> MANTIS_SHRIMP = registerEntity(EntityType.Builder.of(EntityMantisShrimp::new, MobCategory.WATER_CREATURE).sized(1.25F, 1.2F), "mantis_shrimp");
    public static final EntityType<EntityGuster> GUSTER = registerEntity(EntityType.Builder.of(EntityGuster::new, MobCategory.MONSTER).sized(1.42F, 2.35F).fireImmune(), "guster");
    public static final EntityType<EntitySandShot> SAND_SHOT = registerEntity(EntityType.Builder.of(EntitySandShot::new, MobCategory.MISC).sized(0.95F, 0.65F).setCustomClientFactory(EntitySandShot::new).fireImmune(), "sand_shot");
    public static final EntityType<EntityGust> GUST = registerEntity(EntityType.Builder.of(EntityGust::new, MobCategory.MISC).sized(0.8F, 0.8F).setCustomClientFactory(EntityGust::new).fireImmune(), "gust");
    public static final EntityType<EntityWarpedMosco> WARPED_MOSCO = registerEntity(EntityType.Builder.of(EntityWarpedMosco::new, MobCategory.MONSTER).sized(1.99F, 3.25F).fireImmune(), "warped_mosco");
    public static final EntityType<EntityHemolymph> HEMOLYMPH = registerEntity(EntityType.Builder.of(EntityHemolymph::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityHemolymph::new).fireImmune(), "hemolymph");
    public static final EntityType<EntityStraddler> STRADDLER = registerEntity(EntityType.Builder.of(EntityStraddler::new, MobCategory.MONSTER).sized(1.65F, 3F).fireImmune(), "straddler");
    public static final EntityType<EntityStradpole> STRADPOLE = registerEntity(EntityType.Builder.of(EntityStradpole::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.5F).fireImmune(), "stradpole");
    public static final EntityType<EntityStraddleboard> STRADDLEBOARD = registerEntity(EntityType.Builder.of(EntityStraddleboard::new, MobCategory.MISC).sized(1.5F, 0.35F).setCustomClientFactory(EntityStraddleboard::new).fireImmune(), "straddleboard");
    public static final EntityType<EntityEmu> EMU = registerEntity(EntityType.Builder.of(EntityEmu::new, MobCategory.CREATURE).sized(1.1F, 1.8F), "emu");
    public static final EntityType<EntityEmuEgg> EMU_EGG = registerEntity(EntityType.Builder.of(EntityEmuEgg::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityEmuEgg::new).fireImmune(), "emu_egg");
    public static final EntityType<EntityPlatypus> PLATYPUS = registerEntity(EntityType.Builder.of(EntityPlatypus::new, MobCategory.CREATURE).sized(0.8F, 0.5F), "platypus");
    public static final EntityType<EntityDropBear> DROPBEAR = registerEntity(EntityType.Builder.of(EntityDropBear::new, MobCategory.MONSTER).sized(1.65F, 1.5F).fireImmune(), "dropbear");
    public static final EntityType<EntityTasmanianDevil> TASMANIAN_DEVIL = registerEntity(EntityType.Builder.of(EntityTasmanianDevil::new, MobCategory.CREATURE).sized(0.7F, 0.8F), "tasmanian_devil");
    public static final EntityType<EntityKangaroo> KANGAROO = registerEntity(EntityType.Builder.of(EntityKangaroo::new, MobCategory.CREATURE).sized(1.65F, 1.5F), "kangaroo");
    public static final EntityType<EntityCachalotWhale> CACHALOT_WHALE = registerEntity(EntityType.Builder.of(EntityCachalotWhale::new, MobCategory.WATER_CREATURE).sized(9F, 4.0F), "cachalot_whale");
    public static final EntityType<EntityCachalotEcho> CACHALOT_ECHO = registerEntity(EntityType.Builder.of(EntityCachalotEcho::new, MobCategory.MISC).sized(2F, 2F).setCustomClientFactory(EntityCachalotEcho::new).fireImmune(), "cachalot_echo");
    public static final EntityType<EntityLeafcutterAnt> LEAFCUTTER_ANT = registerEntity(EntityType.Builder.of(EntityLeafcutterAnt::new, MobCategory.CREATURE).sized(0.8F, 0.5F), "leafcutter_ant");
    public static final EntityType<EntityEnderiophage> ENDERIOPHAGE = registerEntity(EntityType.Builder.of(EntityEnderiophage::new, MobCategory.CREATURE).sized(0.85F, 1.95F).setUpdateInterval(1), "enderiophage");
    public static final EntityType<EntityEnderiophageRocket> ENDERIOPHAGE_ROCKET = registerEntity(EntityType.Builder.of(EntityEnderiophageRocket::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityEnderiophageRocket::new).fireImmune(), "enderiophage_rocket");
    public static final EntityType<EntityBaldEagle> BALD_EAGLE = registerEntity(EntityType.Builder.of(EntityBaldEagle::new, MobCategory.CREATURE).sized(0.5F, 0.95F).setUpdateInterval(1).setTrackingRange(14), "bald_eagle");
    public static final EntityType<EntityTiger> TIGER = registerEntity(EntityType.Builder.of(EntityTiger::new, MobCategory.CREATURE).sized(1.45F, 1.2F), "tiger");
    public static final EntityType<EntityTarantulaHawk> TARANTULA_HAWK = registerEntity(EntityType.Builder.of(EntityTarantulaHawk::new, MobCategory.CREATURE).sized(1.2F, 0.9F), "tarantula_hawk");
    public static final EntityType<EntityVoidWorm> VOID_WORM = registerEntity(EntityType.Builder.of(EntityVoidWorm::new, MobCategory.MONSTER).sized(3.4F, 3F).fireImmune().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm");
    public static final EntityType<EntityVoidWormPart> VOID_WORM_PART = registerEntity(EntityType.Builder.of(EntityVoidWormPart::new, MobCategory.MONSTER).sized(1.2F, 1.35F).fireImmune().setTrackingRange(20).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "void_worm_part");
    public static final EntityType<EntityVoidWormShot> VOID_WORM_SHOT = registerEntity(EntityType.Builder.of(EntityVoidWormShot::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityVoidWormShot::new).fireImmune(), "void_worm_shot");
    public static final EntityType<EntityVoidPortal> VOID_PORTAL = registerEntity(EntityType.Builder.of(EntityVoidPortal::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(EntityVoidPortal::new).fireImmune(), "void_portal");
    public static final EntityType<EntityFrilledShark> FRILLED_SHARK = registerEntity(EntityType.Builder.of(EntityFrilledShark::new, MobCategory.WATER_CREATURE).sized(1.3F, 0.4F), "frilled_shark");
    public static final EntityType<EntityMimicOctopus> MIMIC_OCTOPUS = registerEntity(EntityType.Builder.of(EntityMimicOctopus::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F), "mimic_octopus");
    public static final EntityType<EntitySeagull> SEAGULL = registerEntity(EntityType.Builder.of(EntitySeagull::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "seagull");
    public static final EntityType<EntityFroststalker> FROSTSTALKER = registerEntity(EntityType.Builder.of(EntityFroststalker::new, MobCategory.CREATURE).sized(0.95F, 1.15F).immuneTo(Blocks.POWDER_SNOW), "froststalker");
    public static final EntityType<EntityIceShard> ICE_SHARD = registerEntity(EntityType.Builder.of(EntityIceShard::new, MobCategory.MISC).sized(0.45F, 0.45F).setCustomClientFactory(EntityIceShard::new).fireImmune(), "ice_shard");
    public static final EntityType<EntityTusklin> TUSKLIN = registerEntity(EntityType.Builder.of(EntityTusklin::new, MobCategory.CREATURE).sized(2.2F, 1.9F).immuneTo(Blocks.POWDER_SNOW), "tusklin");
    public static final EntityType<EntityLaviathan> LAVIATHAN = registerEntity(EntityType.Builder.of(EntityLaviathan::new, MobCategory.CREATURE).sized(3.3F, 2.4F).fireImmune().setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "laviathan");
    public static final EntityType<EntityCosmaw> COSMAW = registerEntity(EntityType.Builder.of(EntityCosmaw::new, MobCategory.CREATURE).sized(1.95F, 1.8F), "cosmaw");
    public static final EntityType<EntityToucan> TOUCAN = registerEntity(EntityType.Builder.of(EntityToucan::new, MobCategory.CREATURE).sized(0.45F, 0.45F), "toucan");
    public static final EntityType<EntityManedWolf> MANED_WOLF = registerEntity(EntityType.Builder.of(EntityManedWolf::new, MobCategory.CREATURE).sized(0.9F, 1.26F), "maned_wolf");
    public static final EntityType<EntityAnaconda> ANACONDA = registerEntity(EntityType.Builder.of(EntityAnaconda::new, MobCategory.CREATURE).sized(0.8F, 0.8F), "anaconda");
    public static final EntityType<EntityAnacondaPart> ANACONDA_PART = registerEntity(EntityType.Builder.of(EntityAnacondaPart::new, MobCategory.CREATURE).sized(0.8F, 0.8F).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "anaconda_part");
    public static final EntityType<EntityVineLasso> VINE_LASSO = registerEntity(EntityType.Builder.of(EntityVineLasso::new, MobCategory.MISC).sized(0.85F, 0.2F).setCustomClientFactory(EntityVineLasso::new).fireImmune(), "vine_lasso");
    public static final EntityType<EntityAnteater> ANTEATER = registerEntity(EntityType.Builder.of(EntityAnteater::new, MobCategory.CREATURE).sized(1.3F, 1.1F), "anteater");
    public static final EntityType<EntityRockyRoller> ROCKY_ROLLER = registerEntity(EntityType.Builder.of(EntityRockyRoller::new, MobCategory.MONSTER).sized(1.2F, 1.45F), "rocky_roller");
    public static final EntityType<EntityFlutter> FLUTTER = registerEntity(EntityType.Builder.of(EntityFlutter::new, MobCategory.CREATURE).sized(0.5F, 0.7F), "flutter");
    public static final EntityType<EntityPollenBall> POLLEN_BALL = registerEntity(EntityType.Builder.of(EntityPollenBall::new, MobCategory.MISC).sized(0.35F, 0.35F).setCustomClientFactory(EntityPollenBall::new).fireImmune(), "pollen_ball");
//public static final EntityType<EntityJerboa> JERBOA = registerEntity(EntityType.Builder.of(EntityJerboa::new, MobCategory.CREATURE).sized(0.5F, 0.5F), "jerboa");

    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
        ResourceLocation nameLoc = new ResourceLocation(AlexsMobs.MODID, entityName);
        return (EntityType) builder.build(entityName).setRegistryName(nameLoc);
    }

    static {
        SpawnPlacements.register(GRIZZLY_BEAR, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ROADRUNNER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRoadrunner::canRoadrunnerSpawn);
        SpawnPlacements.register(BONE_SERPENT, SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBoneSerpent::canBoneSerpentSpawn);
        SpawnPlacements.register(GAZELLE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(CROCODILE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrocodile::canCrocodileSpawn);
        SpawnPlacements.register(FLY, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFly::canFlySpawn);
        SpawnPlacements.register(HUMMINGBIRD, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityHummingbird::canHummingbirdSpawn);
        SpawnPlacements.register(ORCA, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityOrca::canOrcaSpawn);
        SpawnPlacements.register(SUNBIRD, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySunbird::canSunbirdSpawn);
        SpawnPlacements.register(GORILLA, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityGorilla::canGorillaSpawn);
        SpawnPlacements.register(CRIMSON_MOSQUITO, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCrimsonMosquito::canMosquitoSpawn);
        SpawnPlacements.register(RATTLESNAKE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityRattlesnake::canRattlesnakeSpawn);
        SpawnPlacements.register(ENDERGRADE, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEndergrade::canEndergradeSpawn);
        SpawnPlacements.register(HAMMERHEAD_SHARK, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityHammerheadShark::canHammerheadSharkSpawn);
        SpawnPlacements.register(LOBSTER, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLobster::canLobsterSpawn);
        SpawnPlacements.register(KOMODO_DRAGON, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKomodoDragon::canKomodoDragonSpawn);
        SpawnPlacements.register(CAPUCHIN_MONKEY, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityCapuchinMonkey::canCapuchinSpawn);
        SpawnPlacements.register(CENTIPEDE_HEAD, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCentipedeHead::canCentipedeSpawn);
        SpawnPlacements.register(WARPED_TOAD, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityWarpedToad::canWarpedToadSpawn);
        SpawnPlacements.register(MOOSE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMoose::canMooseSpawn);
        SpawnPlacements.register(MIMICUBE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(RACCOON, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(BLOBFISH, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityBlobfish::canBlobfishSpawn);
        SpawnPlacements.register(SEAL, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeal::canSealSpawn);
        SpawnPlacements.register(COCKROACH, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCockroach::canCockroachSpawn);
        SpawnPlacements.register(SHOEBILL, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ELEPHANT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(SOUL_VULTURE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySoulVulture::canVultureSpawn);
        SpawnPlacements.register(SNOW_LEOPARD, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySnowLeopard::canSnowLeopardSpawn);
        SpawnPlacements.register(ALLIGATOR_SNAPPING_TURTLE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAlligatorSnappingTurtle::canTurtleSpawn);
        SpawnPlacements.register(MUNGUS, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMungus::canMungusSpawn);
        SpawnPlacements.register(MANTIS_SHRIMP, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMantisShrimp::canMantisShrimpSpawn);
        SpawnPlacements.register(GUSTER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityGuster::canGusterSpawn);
        SpawnPlacements.register(WARPED_MOSCO, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(STRADDLER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStraddler::canStraddlerSpawn);
        SpawnPlacements.register(STRADPOLE, SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityStradpole::canStradpoleSpawn);
        SpawnPlacements.register(EMU, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEmu::canEmuSpawn);
        SpawnPlacements.register(PLATYPUS, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityPlatypus::canPlatypusSpawn);
        SpawnPlacements.register(DROPBEAR, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(TASMANIAN_DEVIL, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(KANGAROO, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityKangaroo::canKangarooSpawn);
        SpawnPlacements.register(CACHALOT_WHALE, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCachalotWhale::canCachalotWhaleSpawn);
        SpawnPlacements.register(LEAFCUTTER_ANT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ENDERIOPHAGE, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityEnderiophage::canEnderiophageSpawn);
        SpawnPlacements.register(BALD_EAGLE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityBaldEagle::canEagleSpawn);
        SpawnPlacements.register(TIGER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTiger::canTigerSpawn);
        SpawnPlacements.register(TARANTULA_HAWK, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTarantulaHawk::canTarantulaHawkSpawn);
        SpawnPlacements.register(VOID_WORM, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityVoidWorm::canVoidWormSpawn);
        SpawnPlacements.register(FRILLED_SHARK, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFrilledShark::canFrilledSharkSpawn);
        SpawnPlacements.register(MIMIC_OCTOPUS, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityMimicOctopus::canMimicOctopusSpawn);
        SpawnPlacements.register(SEAGULL, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySeagull::canSeagullSpawn);
        SpawnPlacements.register(FROSTSTALKER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityFroststalker::canFroststalkerSpawn);
        SpawnPlacements.register(TUSKLIN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityTusklin::canTusklinSpawn);
        SpawnPlacements.register(LAVIATHAN, SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLaviathan::canLaviathanSpawn);
        SpawnPlacements.register(COSMAW, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityCosmaw::canCosmawSpawn);
        SpawnPlacements.register(TOUCAN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, EntityToucan::canToucanSpawn);
        SpawnPlacements.register(MANED_WOLF, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityManedWolf::checkAnimalSpawnRules);
        SpawnPlacements.register(ANACONDA, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnaconda::canAnacondaSpawn);
        SpawnPlacements.register(ANTEATER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityAnteater::canAnteaterSpawn);

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
    }

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        event.put(GRIZZLY_BEAR, EntityGrizzlyBear.bakeAttributes().build());
        event.put(ROADRUNNER, EntityRoadrunner.bakeAttributes().build());
        event.put(BONE_SERPENT, EntityBoneSerpent.bakeAttributes().build());
        event.put(BONE_SERPENT_PART, EntityBoneSerpentPart.bakeAttributes().build());
        event.put(GAZELLE, EntityGazelle.bakeAttributes().build());
        event.put(CROCODILE, EntityCrocodile.bakeAttributes().build());
        event.put(FLY, EntityFly.bakeAttributes().build());
        event.put(HUMMINGBIRD, EntityHummingbird.bakeAttributes().build());
        event.put(ORCA, EntityOrca.bakeAttributes().build());
        event.put(SUNBIRD, EntitySunbird.bakeAttributes().build());
        event.put(GORILLA, EntityGorilla.bakeAttributes().build());
        event.put(CRIMSON_MOSQUITO, EntityCrimsonMosquito.bakeAttributes().build());
        event.put(RATTLESNAKE, EntityRattlesnake.bakeAttributes().build());
        event.put(ENDERGRADE, EntityEndergrade.bakeAttributes().build());
        event.put(HAMMERHEAD_SHARK, EntityHammerheadShark.bakeAttributes().build());
        event.put(LOBSTER, EntityLobster.bakeAttributes().build());
        event.put(KOMODO_DRAGON, EntityKomodoDragon.bakeAttributes().build());
        event.put(CAPUCHIN_MONKEY, EntityCapuchinMonkey.bakeAttributes().build());
        event.put(CENTIPEDE_HEAD, EntityCentipedeHead.bakeAttributes().build());
        event.put(CENTIPEDE_BODY, EntityCentipedeBody.bakeAttributes().build());
        event.put(CENTIPEDE_TAIL, EntityCentipedeTail.bakeAttributes().build());
        event.put(WARPED_TOAD, EntityWarpedToad.bakeAttributes().build());
        event.put(MOOSE, EntityMoose.bakeAttributes().build());
        event.put(MIMICUBE, EntityMimicube.bakeAttributes().build());
        event.put(RACCOON, EntityRaccoon.bakeAttributes().build());
        event.put(BLOBFISH, EntityBlobfish.bakeAttributes().build());
        event.put(SEAL, EntitySeal.bakeAttributes().build());
        event.put(COCKROACH, EntityCockroach.bakeAttributes().build());
        event.put(SHOEBILL, EntityShoebill.bakeAttributes().build());
        event.put(ELEPHANT, EntityElephant.bakeAttributes().build());
        event.put(SOUL_VULTURE, EntitySoulVulture.bakeAttributes().build());
        event.put(SNOW_LEOPARD, EntitySnowLeopard.bakeAttributes().build());
        event.put(SPECTRE, EntitySpectre.bakeAttributes().build());
        event.put(CROW, EntityCrow.bakeAttributes().build());
        event.put(ALLIGATOR_SNAPPING_TURTLE, EntityAlligatorSnappingTurtle.bakeAttributes().build());
        event.put(MUNGUS, EntityMungus.bakeAttributes().build());
        event.put(MANTIS_SHRIMP, EntityMantisShrimp.bakeAttributes().build());
        event.put(GUSTER, EntityGuster.bakeAttributes().build());
        event.put(WARPED_MOSCO, EntityWarpedMosco.bakeAttributes().build());
        event.put(STRADDLER, EntityStraddler.bakeAttributes().build());
        event.put(STRADPOLE, EntityStradpole.bakeAttributes().build());
        event.put(EMU, EntityEmu.bakeAttributes().build());
        event.put(PLATYPUS, EntityPlatypus.bakeAttributes().build());
        event.put(DROPBEAR, EntityDropBear.bakeAttributes().build());
        event.put(TASMANIAN_DEVIL, EntityTasmanianDevil.bakeAttributes().build());
        event.put(KANGAROO, EntityKangaroo.bakeAttributes().build());
        event.put(CACHALOT_WHALE, EntityCachalotWhale.bakeAttributes().build());
        event.put(LEAFCUTTER_ANT, EntityLeafcutterAnt.bakeAttributes().build());
        event.put(ENDERIOPHAGE, EntityEnderiophage.bakeAttributes().build());
        event.put(BALD_EAGLE, EntityBaldEagle.bakeAttributes().build());
        event.put(TIGER, EntityTiger.bakeAttributes().build());
        event.put(TARANTULA_HAWK, EntityTarantulaHawk.bakeAttributes().build());
        event.put(VOID_WORM, EntityVoidWorm.bakeAttributes().build());
        event.put(VOID_WORM_PART, EntityVoidWormPart.bakeAttributes().build());
        event.put(FRILLED_SHARK, EntityFrilledShark.bakeAttributes().build());
        event.put(MIMIC_OCTOPUS, EntityMimicOctopus.bakeAttributes().build());
        event.put(SEAGULL, EntitySeagull.bakeAttributes().build());
        event.put(FROSTSTALKER, EntityFroststalker.bakeAttributes().build());
        event.put(TUSKLIN, EntityTusklin.bakeAttributes().build());
        event.put(LAVIATHAN, EntityLaviathan.bakeAttributes().build());
        event.put(COSMAW, EntityCosmaw.bakeAttributes().build());
        event.put(TOUCAN, EntityToucan.bakeAttributes().build());
        event.put(MANED_WOLF, EntityManedWolf.bakeAttributes().build());
        event.put(ANACONDA, EntityAnaconda.bakeAttributes().build());
        event.put(ANACONDA_PART, EntityAnacondaPart.bakeAttributes().build());
        event.put(ANTEATER, EntityAnteater.bakeAttributes().build());
        event.put(ROCKY_ROLLER, EntityRockyRoller.bakeAttributes().build());
        event.put(FLUTTER, EntityFlutter.bakeAttributes().build());
        //event.put(JERBOA, EntityJerboa.bakeAttributes().build());
    }

    public static Predicate<LivingEntity> buildPredicateFromTag(Tag entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag);
        }
    }

    public static Predicate<LivingEntity> buildPredicateFromTagTameable(Tag entityTag, LivingEntity owner){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag) && !owner.isAlliedTo(e);
        }
    }

    public static boolean rollSpawn(int rolls, Random random, MobSpawnType reason){
        if(reason == MobSpawnType.SPAWNER){
            return true;
        }else{
            return rolls <= 0 || random.nextInt(rolls) == 0;
        }
    }

}
