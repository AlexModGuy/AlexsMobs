package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.ToggleableNearestAttackableTargetGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    private static final UUID SAND_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28E");
    private static final UUID SNEAK_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28F");
    private static final AttributeModifier SAND_SPEED_BONUS = new AttributeModifier(SAND_SPEED_MODIFIER, "roadrunner speed bonus", 0.1F, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SNEAK_SPEED_BONUS = new AttributeModifier(SNEAK_SPEED_MODIFIER, "frontier cap speed bonus", 0.1F, AttributeModifier.Operation.ADDITION);

    protected static BlockRayTraceResult rayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vector3d vector3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
        Vector3d vector3d1 = vector3d.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (AMConfig.giveBookOnStartup) {
            CompoundNBT playerData = event.getPlayer().getPersistentData();
            CompoundNBT data = playerData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
            if (data != null && !data.getBoolean("alexsmobs_has_book")) {
                ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY));
                data.putBoolean("alexsmobs_has_book", true);
                playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
            }
        }
    }

    @SubscribeEvent
    public void onLootLevelEvent(LootingLevelEvent event) {
        if(event.getDamageSource() != null && event.getDamageSource().getTrueSource() != null && event.getDamageSource().getTrueSource() instanceof EntitySnowLeopard){
            event.setLootingLevel(event.getLootingLevel() + 2);
        }
    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        if(event.getItemStack().getItem() == Items.WHEAT && event.getPlayer().getRidingEntity() instanceof EntityElephant){
            if(((EntityElephant)event.getPlayer().getRidingEntity()).triggerCharge(event.getItemStack())){
                event.getPlayer().swingArm(event.getHand());
                if(!event.getPlayer().isCreative()){
                    event.getItemStack().shrink(1);
                }
            }
        }
        if (event.getItemStack().getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled) {
            RayTraceResult raytraceresult = rayTrace(event.getWorld(), event.getPlayer(), RayTraceContext.FluidMode.SOURCE_ONLY);
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockRayTraceResult) raytraceresult).getPos();
                if (event.getWorld().isBlockModifiable(event.getPlayer(), blockpos)) {
                    if (event.getWorld().getFluidState(blockpos).isTagged(FluidTags.LAVA)) {
                        event.getWorld().playSound(event.getPlayer(), event.getPlayer().getPosX(), event.getPlayer().getPosY(), event.getPlayer().getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        event.getPlayer().addStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        event.getPlayer().setFire(6);
                        if (!event.getPlayer().addItemStackToInventory(new ItemStack(AMItemRegistry.LAVA_BOTTLE))) {
                            event.getPlayer().entityDropItem(new ItemStack(AMItemRegistry.LAVA_BOTTLE));
                        }
                        event.getPlayer().swingArm(event.getHand());
                        if (!event.getPlayer().isCreative()) {
                            event.getItemStack().shrink(1);
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public void onEntityJoinWorld(LivingSpawnEvent.SpecialSpawn event) {
        if(event.getEntity() instanceof WanderingTraderEntity && AMConfig.elephantTraderSpawnChance > 0){
            Random rand = new Random();
            Biome biome = event.getWorld().getBiome(event.getEntity().getPosition());
            if(rand.nextFloat() <= AMConfig.elephantTraderSpawnChance && (!AMConfig.limitElephantTraderBiomes || biome.getTemperature() >= 1.0F)){
                WanderingTraderEntity traderEntity = (WanderingTraderEntity) event.getEntity();
                EntityElephant elephant = AMEntityRegistry.ELEPHANT.create(traderEntity.world);
                elephant.copyLocationAndAnglesFrom(traderEntity);
                if(elephant.canSpawnWithTraderHere()){
                    elephant.setTrader(true);
                    elephant.setChested(true);
                    if(!event.getWorld().isRemote()){
                        traderEntity.world.addEntity(elephant);
                        traderEntity.startRiding(elephant, true);
                    }
                    elephant.addElephantLoot(null, rand.nextInt());
                }
            }
        }
        try {
            if (event.getEntity() != null && event.getEntity() instanceof SpiderEntity && AMConfig.spidersAttackFlies) {
                SpiderEntity spider = (SpiderEntity) event.getEntity();
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal(spider, EntityFly.class, 1, true, false, null));
            }
            if (event.getEntity() != null && event.getEntity() instanceof WolfEntity && AMConfig.spidersAttackFlies) {
                WolfEntity wolf = (WolfEntity) event.getEntity();
                wolf.targetSelector.addGoal(6, new NonTamedTargetGoal(wolf, EntityMoose.class, false, null));
            }
            if (event.getEntity() != null && event.getEntity() instanceof PolarBearEntity && AMConfig.polarBearsAttackSeals) {
                PolarBearEntity bear = (PolarBearEntity) event.getEntity();
                bear.targetSelector.addGoal(6, new NearestAttackableTargetGoal(bear, EntitySeal.class, 15, true, true, null));
            }
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
        if (event.getPlayer().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == AMItemRegistry.MOOSE_HEADGEAR && event.getTarget() instanceof LivingEntity) {
            float f1 = 2;
            ((LivingEntity) event.getTarget()).applyKnockback(f1 * 0.5F, MathHelper.sin(event.getPlayer().rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(event.getPlayer().rotationYaw * ((float) Math.PI / 180F)));
        }
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event) {
        if(event.getSource().getTrueSource() instanceof PlayerEntity){
           LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
           if(event.getAmount() > 0 && attacker.isPotionActive(AMEffectRegistry.SOULSTEAL) && attacker.getActivePotionEffect(AMEffectRegistry.SOULSTEAL) != null){
               int level = attacker.getActivePotionEffect(AMEffectRegistry.SOULSTEAL).getAmplifier() + 1;
               Random rand = new Random();
               if(attacker.getHealth() < attacker.getMaxHealth() && rand.nextFloat() < (0.25F + (level * 0.25F))){
                   attacker.heal(Math.min(event.getAmount() / 2F * level, 2 + 2 * level));
               }
           }
        }
    }

        @SubscribeEvent
    public void onStructureGetSpawnLists(StructureSpawnListGatherEvent event) {
        if(AMConfig.mimicubeSpawnInEndCity && AMConfig.mimicubeSpawnWeight > 0){
            if(event.getStructure() == Structure.END_CITY){
                event.addEntitySpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(AMEntityRegistry.MIMICUBE, AMConfig.mimicubeSpawnWeight, 1, 3));
            }
        }
        if(AMConfig.soulVultureSpawnOnFossil && AMConfig.soulVultureSpawnWeight > 0) {
            if (event.getStructure() == Structure.NETHER_FOSSIL) {
                event.addEntitySpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(AMEntityRegistry.SOUL_VULTURE, AMConfig.soulVultureSpawnWeight, 2, 3));
            }
        }
    }

    @SubscribeEvent
    public void onLivingSetTargetEvent(LivingSetAttackTargetEvent event) {
        if(event.getTarget() != null && event.getEntityLiving() instanceof MobEntity){
            if(event.getEntityLiving().getCreatureAttribute() == CreatureAttribute.ARTHROPOD){
                if(event.getTarget().isPotionActive(AMEffectRegistry.BUG_PHEROMONES) && event.getEntityLiving().getRevengeTarget() != event.getTarget()){
                    ((MobEntity)event.getEntityLiving()).setAttackTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            ModifiableAttributeInstance modifiableattributeinstance = event.getEntityLiving().getAttribute(Attributes.MOVEMENT_SPEED);
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS || modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                boolean sand = event.getEntityLiving().world.getBlockState(getDownPos(event.getEntityLiving().getPosition(), event.getEntityLiving().world)).getBlock().isIn(BlockTags.SAND);
                if (sand && !modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                    modifiableattributeinstance.applyPersistentModifier(SAND_SPEED_BONUS);
                }
                if (event.getEntityLiving().ticksExisted % 25 == 0 && (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS || !sand) && modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                    modifiableattributeinstance.removeModifier(SAND_SPEED_BONUS);
                }
            }
            if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == AMItemRegistry.FRONTIER_CAP || modifiableattributeinstance.hasModifier(SNEAK_SPEED_BONUS)) {
                if (event.getEntityLiving().isSneaking() && !modifiableattributeinstance.hasModifier(SNEAK_SPEED_BONUS)) {
                    modifiableattributeinstance.applyPersistentModifier(SNEAK_SPEED_BONUS);
                }
                if ((!event.getEntityLiving().isSneaking() || event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() != AMItemRegistry.FRONTIER_CAP) && modifiableattributeinstance.hasModifier(SNEAK_SPEED_BONUS)) {
                    modifiableattributeinstance.removeModifier(SNEAK_SPEED_BONUS);
                }
            }
        }

        if (event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS) {
            if (event.getEntityLiving().collidedHorizontally && !event.getEntityLiving().isInWater()) {
                event.getEntityLiving().fallDistance = 0.0F;
                Vector3d motion = event.getEntityLiving().getMotion();
                double d0 = MathHelper.clamp(motion.x, -0.15F, 0.15F);
                double d1 = MathHelper.clamp(motion.z, -0.15F, 0.15F);
                double d2 = 0.1D;
                if (d2 < 0.0D && !event.getEntityLiving().getBlockState().isScaffolding(event.getEntityLiving()) && event.getEntityLiving().hasStoppedClimbing()) {
                    d2 = 0.0D;
                }
                motion = new Vector3d(d0, d2, d1);
                event.getEntityLiving().setMotion(motion);
            }


        }
    }

    private BlockPos getDownPos(BlockPos entered, IWorld world) {
        int i = 0;
        while (world.isAirBlock(entered) && i < 3) {
            entered = entered.down();
            i++;
        }
        return entered;
    }
}