package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMEntityRegistry {

    public static final EntityType<EntityGrizzlyBear> GRIZZLY_BEAR = registerEntity(EntityType.Builder.create(EntityGrizzlyBear::new, EntityClassification.CREATURE).size(1.45F, 1.75F), "grizzly_bear");
    public static final EntityType<EntityRoadrunner> ROADRUNNER = registerEntity(EntityType.Builder.create(EntityRoadrunner::new, EntityClassification.CREATURE).size(0.45F, 0.75F), "roadrunner");
    public static final EntityType<EntityBoneSerpent> BONE_SERPENT = registerEntity(EntityType.Builder.create(EntityBoneSerpent::new, EntityClassification.MONSTER).size(1.2F, 1.15F).immuneToFire(), "bone_serpent");
    public static final EntityType<EntityBoneSerpentPart> BONE_SERPENT_PART = registerEntity(EntityType.Builder.create(EntityBoneSerpentPart::new, EntityClassification.MISC).size(1F, 1F).immuneToFire(), "bone_serpent_part");
    public static final EntityType<EntityGazelle> GAZELLE = registerEntity(EntityType.Builder.create(EntityGazelle::new, EntityClassification.CREATURE).size(0.85F, 1.25F), "gazelle");
    public static final EntityType<EntityCrocodile> CROCODILE = registerEntity(EntityType.Builder.create(EntityCrocodile::new, EntityClassification.CREATURE).size(2.15F, 0.75F), "crocodile");
    public static final EntityType<EntityFly> FLY = registerEntity(EntityType.Builder.create(EntityFly::new, EntityClassification.CREATURE).size(0.35F, 0.35F), "fly");
    public static final EntityType<EntityHummingbird> HUMMINGBIRD = registerEntity(EntityType.Builder.create(EntityHummingbird::new, EntityClassification.CREATURE).size(0.45F, 0.45F), "hummingbird");
    public static final EntityType<EntityOrca> ORCA = registerEntity(EntityType.Builder.create(EntityOrca::new, EntityClassification.CREATURE).size(3.75F, 1.75F), "orca");
    public static final EntityType<EntitySunbird> SUNBIRD = registerEntity(EntityType.Builder.create(EntitySunbird::new, EntityClassification.CREATURE).size(1.75F, 0.75F).immuneToFire().setTrackingRange(10).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1), "sunbird");
    public static final EntityType<EntityGorilla> GORILLA = registerEntity(EntityType.Builder.create(EntityGorilla::new, EntityClassification.CREATURE).size(1.15F, 1.35F), "gorilla");
    public static final EntityType<EntityCrimsonMosquito> CRIMSON_MOSQUITO = registerEntity(EntityType.Builder.create(EntityCrimsonMosquito::new, EntityClassification.CREATURE).size(1.25F, 1.15F).immuneToFire(), "crimson_mosquito");
    public static final EntityType<EntityMosquitoSpit> MOSQUITO_SPIT = registerEntity(EntityType.Builder.create(EntityMosquitoSpit::new, EntityClassification.CREATURE).size(0.5F, 0.5F).setCustomClientFactory(EntityMosquitoSpit::new).immuneToFire(), "mosquito_spit");
    public static final EntityType<EntityRattlesnake> RATTLESNAKE = registerEntity(EntityType.Builder.create(EntityRattlesnake::new, EntityClassification.CREATURE).size(0.95F, 0.35F), "rattlesnake");
    public static final EntityType<EntityEndergrade> ENDERGRADE = registerEntity(EntityType.Builder.create(EntityEndergrade::new, EntityClassification.CREATURE).size(0.95F, 0.85F), "endergrade");
    public static final EntityType<EntityHammerheadShark> HAMMERHEAD_SHARK = registerEntity(EntityType.Builder.create(EntityHammerheadShark::new, EntityClassification.CREATURE).size(2.4F, 1.25F), "hammerhead_shark");

    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
        ResourceLocation nameLoc = new ResourceLocation(AlexsMobs.MODID, entityName);
        return (EntityType) builder.build(entityName).setRegistryName(nameLoc);
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


    }

}
