package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemFalconryGlove;
import com.github.alexthe666.alexsmobs.message.MessageSwingArm;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import com.github.alexthe666.alexsmobs.misc.ItemsForEmeraldsTrade;
import com.github.alexthe666.alexsmobs.world.AMWorldRegistry;
import com.github.alexthe666.alexsmobs.world.BeachedCachalotWhaleSpawner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.*;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.antlr.v4.runtime.misc.Triple;

import java.util.*;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    public static final UUID CARRO_UUID = UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290");
    private static final UUID SAND_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28E");
    private static final UUID SNEAK_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28F");
    private static final AttributeModifier SAND_SPEED_BONUS = new AttributeModifier(SAND_SPEED_MODIFIER, "roadrunner speed bonus", 0.1F, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SNEAK_SPEED_BONUS = new AttributeModifier(SNEAK_SPEED_MODIFIER, "frontier cap speed bonus", 0.1F, AttributeModifier.Operation.ADDITION);
    private static final Map<ServerLevel, BeachedCachalotWhaleSpawner> BEACHED_CACHALOT_WHALE_SPAWNER_MAP = new HashMap<ServerLevel, BeachedCachalotWhaleSpawner>();
    public static List<Triple<ServerPlayer, ServerLevel, BlockPos>> teleportPlayers = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent tick) {
        if (!tick.world.isClientSide && tick.world instanceof ServerLevel) {
            ServerLevel serverWorld = (ServerLevel) tick.world;
            if (BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(serverWorld) == null) {
                BEACHED_CACHALOT_WHALE_SPAWNER_MAP.put(serverWorld, new BeachedCachalotWhaleSpawner(serverWorld));
            }
            BeachedCachalotWhaleSpawner spawner = BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(serverWorld);
            spawner.tick();
        }
        if (!tick.world.isClientSide && tick.world instanceof ServerLevel) {
            ServerLevel serverWorld = (ServerLevel) tick.world;
            if (BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(serverWorld) == null) {
                BEACHED_CACHALOT_WHALE_SPAWNER_MAP.put(serverWorld, new BeachedCachalotWhaleSpawner(serverWorld));
            }
            BeachedCachalotWhaleSpawner spawner = BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(serverWorld);
            spawner.tick();
        }
        if (!tick.world.isClientSide && tick.world instanceof ServerLevel) {
            for (Triple trip : teleportPlayers) {
                ServerPlayer player = (ServerPlayer) trip.a;
                ServerLevel endpointWorld = (ServerLevel) trip.b;
                BlockPos endpoint = (BlockPos) trip.c;
                player.teleportTo(endpointWorld, endpoint.getX() + 0.5D, endpoint.getY() + 0.5D, endpoint.getZ() + 0.5D, player.getYRot(), player.getXRot());
            }
            teleportPlayers.clear();
        }
    }

    protected static BlockHitResult rayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
        Vec3 vector3d1 = vector3d.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
    }


    @SubscribeEvent
    public static void onItemUseLast(LivingEntityUseItemEvent.Finish event) {

        if (event.getItem().getItem() == Items.CHORUS_FRUIT && new Random().nextInt(3) == 0 && event.getEntityLiving().hasEffect(AMEffectRegistry.ENDER_FLU)) {
            event.getEntityLiving().removeEffect(AMEffectRegistry.ENDER_FLU);
        }
    }

    @SubscribeEvent
    public static void onEntityResize(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();
            try {
                Map<MobEffect, MobEffectInstance> potions = entity.getActiveEffectsMap();
                if (event.getEntity().level != null && potions != null && !potions.isEmpty() && potions.containsKey(AMEffectRegistry.CLINGING)) {
                    if (EffectClinging.isUpsideDown(entity)) {
                        float minus = event.getOldSize().height - event.getOldEyeHeight();
                        event.setNewEyeHeight(minus);
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (AMConfig.giveBookOnStartup) {
            CompoundTag playerData = event.getPlayer().getPersistentData();
            CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            if (data != null && !data.getBoolean("alexsmobs_has_book")) {
                ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY.get()));
                if (event.getPlayer().getUUID() != null && (event.getPlayer().getUUID().equals(ALEX_UUID) || event.getPlayer().getUUID().equals(CARRO_UUID))) {
                    ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(AMItemRegistry.BEAR_DUST.get()));
                }
                data.putBoolean("alexsmobs_has_book", true);
                playerData.put(Player.PERSISTED_NBT_TAG, data);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        ItemFalconryGlove.onLeftClick(event.getPlayer(), event.getPlayer().getOffhandItem());
        ItemFalconryGlove.onLeftClick(event.getPlayer(), event.getPlayer().getMainHandItem());
        if (event.getWorld().isClientSide) {
            AlexsMobs.sendMSGToServer(new MessageSwingArm());
        }
    }

    @SubscribeEvent
    public static void onStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity().getType() == EntityType.SQUID && !event.getEntity().getLevel().isClientSide) {
            ServerLevel level = (ServerLevel) event.getEntity().getLevel();
            event.setCanceled(true);
            EntityGiantSquid squid = AMEntityRegistry.GIANT_SQUID.get().create(level);
            squid.moveTo(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity().getYRot(), event.getEntity().getXRot());
            squid.finalizeSpawn(level, level.getCurrentDifficultyAt(squid.blockPosition()), MobSpawnType.CONVERSION, null, null);
            if (event.getEntity().hasCustomName()) {
                squid.setCustomName(event.getEntity().getCustomName());
                squid.setCustomNameVisible(event.getEntity().isCustomNameVisible());
            }
            squid.setBlue(true);
            squid.setPersistenceRequired();
            level.addFreshEntityWithPassengers(squid);
            event.getEntity().discard();
        }
    }

    @SubscribeEvent
    public void onProjectileHit(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult && ((EntityHitResult) event.getRayTraceResult()).getEntity() instanceof EntityEmu && !event.getEntity().level.isClientSide) {
            EntityEmu emu = ((EntityEmu) ((EntityHitResult) event.getRayTraceResult()).getEntity());
            if (event.getEntity() instanceof AbstractArrow) {
                //fixes soft crash with vanilla
                ((AbstractArrow) event.getEntity()).setPierceLevel((byte) 0);
            }
            if ((emu.getAnimation() == EntityEmu.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EntityEmu.ANIMATION_DODGE_LEFT) && emu.getAnimationTick() < 7) {
                event.setCanceled(true);
            }
            if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
                boolean left = true;
                Vec3 arrowPos = event.getEntity().position();
                Vec3 rightVector = emu.getLookAngle().yRot(0.5F * (float) Math.PI).add(emu.position());
                Vec3 leftVector = emu.getLookAngle().yRot(-0.5F * (float) Math.PI).add(emu.position());
                if (arrowPos.distanceTo(rightVector) < arrowPos.distanceTo(leftVector)) {
                    left = false;
                } else if (arrowPos.distanceTo(rightVector) > arrowPos.distanceTo(leftVector)) {
                    left = true;
                } else {
                    left = emu.getRandom().nextBoolean();
                }
                Vec3 vector3d2 = event.getEntity().getDeltaMovement().yRot((float) ((left ? -0.5F : 0.5F) * Math.PI)).normalize();
                emu.setAnimation(left ? EntityEmu.ANIMATION_DODGE_LEFT : EntityEmu.ANIMATION_DODGE_RIGHT);
                emu.hasImpulse = true;
                if (!emu.horizontalCollision) {
                    emu.move(MoverType.SELF, new Vec3(vector3d2.x() * 0.25F, 0.1F, vector3d2.z() * 0.25F));
                }
                if (!event.getEntity().level.isClientSide) {
                    ServerPlayer serverPlayerEntity = null;
                    if (event.getEntity() instanceof Arrow) {
                        Entity thrower = ((Arrow) event.getEntity()).getOwner();
                        if (thrower instanceof ServerPlayer) {
                            serverPlayerEntity = (ServerPlayer) thrower;
                        }
                    }
                    if (event.getEntity() instanceof ThrowableProjectile) {
                        Entity thrower = ((ThrowableProjectile) event.getEntity()).getOwner();
                        if (thrower instanceof ServerPlayer) {
                            serverPlayerEntity = (ServerPlayer) thrower;
                        }
                    }
                    if (serverPlayerEntity != null) {
                        AMAdvancementTriggerRegistry.EMU_DODGE.trigger(serverPlayerEntity);
                    }
                }
                emu.setDeltaMovement(emu.getDeltaMovement().add(vector3d2.x() * 0.5F, 0.32F, vector3d2.z() * 0.5F));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDespawnAttempt(LivingSpawnEvent.AllowDespawn event) {
        if (event.getEntityLiving().hasEffect(AMEffectRegistry.DEBILITATING_STING) && event.getEntityLiving().getEffect(AMEffectRegistry.DEBILITATING_STING) != null && event.getEntityLiving().getEffect(AMEffectRegistry.DEBILITATING_STING).getAmplifier() > 0) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onTradeSetup(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FISHERMAN) {
            VillagerTrades.ItemListing ambergrisTrade = new EmeraldsForItemsTrade(AMItemRegistry.AMBERGRIS.get(), 20, 3, 4);
            List l = event.getTrades().get(2);
            l.add(ambergrisTrade);
            event.getTrades().put(2, l);
        }
    }

    @SubscribeEvent
    public void onWanderingTradeSetup(WandererTradesEvent event) {
        if (AMConfig.wanderingTraderOffers) {
            List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
            List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ANIMAL_DICTIONARY.get(), 4, 1, 2, 1));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ACACIA_BLOSSOM.get(), 3, 2, 2, 1));
            if (AMConfig.cockroachSpawnWeight > 0) {
                genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.COCKROACH_OOTHECA.get(), 2, 1, 2, 1));
            }
            if (AMConfig.blobfishSpawnWeight > 0) {
                genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOBFISH_BUCKET.get(), 4, 1, 3, 1));
            }
            if (AMConfig.crocodileSpawnWeight > 0) {
                genericTrades.add(new ItemsForEmeraldsTrade(AMBlockRegistry.CROCODILE_EGG.get().asItem(), 6, 1, 2, 1));
            }
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BEAR_FUR.get(), 1, 1, 2, 1));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.CROCODILE_SCUTE.get(), 5, 1, 2, 1));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.ROADRUNNER_FEATHER.get(), 1, 2, 2, 2));
            genericTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.MOSQUITO_LARVA.get(), 1, 3, 5, 1));
            rareTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.SOMBRERO.get(), 20, 1, 1, 1));
            rareTrades.add(new ItemsForEmeraldsTrade(AMBlockRegistry.BANANA_PEEL.get(), 1, 2, 1, 1));
            rareTrades.add(new ItemsForEmeraldsTrade(AMItemRegistry.BLOOD_SAC.get(), 5, 2, 3, 1));
        }
    }

    @SubscribeEvent
    public void onLootLevelEvent(LootingLevelEvent event) {
        DamageSource src = event.getDamageSource();
        if (src != null) {
            Entity dmgSrc = src.getEntity();
            if (dmgSrc != null && dmgSrc instanceof EntitySnowLeopard) {
                event.setLootingLevel(event.getLootingLevel() + 2);
            }
        }

    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() == Items.WHEAT && event.getPlayer().getVehicle() instanceof EntityElephant) {
            if (((EntityElephant) event.getPlayer().getVehicle()).triggerCharge(event.getItemStack())) {
                event.getPlayer().swing(event.getHand());
                if (!event.getPlayer().isCreative()) {
                    event.getItemStack().shrink(1);
                }
            }
        }
        if (event.getItemStack().getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled) {
            HitResult raytraceresult = rayTrace(event.getWorld(), event.getPlayer(), ClipContext.Fluid.SOURCE_ONLY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
                if (event.getWorld().mayInteract(event.getPlayer(), blockpos)) {
                    if (event.getWorld().getFluidState(blockpos).is(FluidTags.LAVA)) {
                        event.getWorld().playSound(event.getPlayer(), event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        event.getPlayer().awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        event.getPlayer().setSecondsOnFire(6);
                        if (!event.getPlayer().addItem(new ItemStack(AMItemRegistry.LAVA_BOTTLE.get()))) {
                            event.getPlayer().spawnAtLocation(new ItemStack(AMItemRegistry.LAVA_BOTTLE.get()));
                        }
                        event.getPlayer().swing(event.getHand());
                        if (!event.getPlayer().isCreative()) {
                            event.getItemStack().shrink(1);
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof LivingEntity && !event.getPlayer().isShiftKeyDown() && VineLassoUtil.hasLassoData((LivingEntity) event.getTarget())) {
            if (!event.getEntity().level.isClientSide) {
                event.getTarget().spawnAtLocation(new ItemStack(AMItemRegistry.VINE_LASSO.get()));
            }
            VineLassoUtil.lassoTo(null, (LivingEntity) event.getTarget());
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
        if (event.getTarget() instanceof LivingEntity && !(event.getTarget() instanceof Player) && !(event.getTarget() instanceof EntityEndergrade) && ((LivingEntity) event.getTarget()).hasEffect(AMEffectRegistry.ENDER_FLU)) {
            if (event.getItemStack().getItem() == Items.CHORUS_FRUIT) {
                if (!event.getPlayer().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.getTarget().playSound(SoundEvents.GENERIC_EAT, 1.0F, 0.5F + event.getPlayer().getRandom().nextFloat());
                if (event.getPlayer().getRandom().nextFloat() < 0.4F) {
                    ((LivingEntity) event.getTarget()).removeEffect(AMEffectRegistry.ENDER_FLU);
                    Items.CHORUS_FRUIT.finishUsingItem(event.getItemStack().copy(), event.getWorld(), ((LivingEntity) event.getTarget()));
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
        if (event.getTarget() instanceof LivingEntity && RainbowUtil.getRainbowType((LivingEntity) event.getTarget()) > 0 && (event.getItemStack().getItem() == Items.SPONGE)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            RainbowUtil.setRainbowType((LivingEntity) event.getTarget(), 0);
            if (!event.getPlayer().isCreative()) {
                event.getItemStack().shrink(1);
            }
            ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
            if (!event.getPlayer().addItem(wetSponge)) {
                event.getPlayer().drop(wetSponge, true);
            }
        }
        if (event.getTarget() instanceof Rabbit && event.getItemStack().getItem() == AMItemRegistry.MUNGAL_SPORES.get() && AMConfig.bunfungusTransformation) {
            Random random = new Random();
            if (!event.getEntityLiving().level.isClientSide && random.nextFloat() < 0.15F) {
                EntityBunfungus bunfungus = ((Rabbit) event.getTarget()).convertTo(AMEntityRegistry.BUNFUNGUS.get(), true);
                if (bunfungus != null) {
                    event.getPlayer().level.addFreshEntity(bunfungus);
                    bunfungus.setTransformsIn(EntityBunfungus.MAX_TRANSFORM_TIME);
                }
            } else {
                for (int i = 0; i < 2 + random.nextInt(2); i++) {
                    double d0 = random.nextGaussian() * 0.02D;
                    double d1 = 0.05F + random.nextGaussian() * 0.02D;
                    double d2 = random.nextGaussian() * 0.02D;
                    event.getTarget().level.addParticle(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION, event.getTarget().getRandomX(0.7F), event.getTarget().getY(0.6F), event.getTarget().getRandomZ(0.7F), d0, d1, d2);
                }
            }
            if (!event.getPlayer().isCreative()) {
                event.getItemStack().shrink(1);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public void onUseItemAir(PlayerInteractEvent.RightClickEmpty event) {
        ItemStack stack = event.getPlayer().getItemInHand(event.getHand());
        if (stack.isEmpty()) {
            stack = event.getPlayer().getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if (RainbowUtil.getRainbowType(event.getPlayer()) > 0 && (stack.is(Items.SPONGE))) {
            event.getPlayer().swing(InteractionHand.MAIN_HAND);
            RainbowUtil.setRainbowType(event.getPlayer(), 0);
            if (!event.getPlayer().isCreative()) {
                stack.shrink(1);
            }
            ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
            if (!event.getPlayer().addItem(wetSponge)) {
                event.getPlayer().drop(wetSponge, true);
            }
        }
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event) {
        if(AlexsMobs.isAprilFools() && event.getItemStack().is(Items.STICK) && !event.getPlayer().getCooldowns().isOnCooldown(Items.STICK)){
            BlockState state = event.getPlayer().level.getBlockState(event.getPos());
            boolean flag = false;
            if(state.is(Blocks.SAND)){
                flag = true;
                event.getPlayer().getLevel().setBlockAndUpdate(event.getPos(), AMBlockRegistry.SAND_CIRCLE.get().defaultBlockState());
            }
            if(state.is(Blocks.RED_SAND)){
                flag = true;
                event.getPlayer().getLevel().setBlockAndUpdate(event.getPos(), AMBlockRegistry.RED_SAND_CIRCLE.get().defaultBlockState());
            }
            if(flag){
                event.setCanceled(true);
                event.getPlayer().playSound(SoundEvents.SAND_BREAK, 1, 1);
                event.getPlayer().getCooldowns().addCooldown(Items.STICK, 30);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        if (VineLassoUtil.hasLassoData(event.getEntityLiving())) {
            VineLassoUtil.lassoTo(null, event.getEntityLiving());
            event.getDrops().add(new ItemEntity(event.getEntity().level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), new ItemStack(AMItemRegistry.VINE_LASSO.get())));
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getEntity() instanceof WanderingTrader && AMConfig.elephantTraderSpawnChance > 0) {
            Random rand = new Random();
            Biome biome = event.getWorld().getBiome(event.getEntity().blockPosition()).value();
            if (rand.nextFloat() <= AMConfig.elephantTraderSpawnChance && (!AMConfig.limitElephantTraderBiomes || biome.getBaseTemperature() >= 1.0F)) {
                WanderingTrader traderEntity = (WanderingTrader) event.getEntity();
                EntityElephant elephant = AMEntityRegistry.ELEPHANT.get().create(traderEntity.level);
                elephant.copyPosition(traderEntity);
                if (elephant.canSpawnWithTraderHere()) {
                    elephant.setTrader(true);
                    elephant.setChested(true);
                    if (!event.getWorld().isClientSide()) {
                        traderEntity.level.addFreshEntity(elephant);
                        traderEntity.startRiding(elephant, true);
                    }
                    elephant.addElephantLoot(null, rand.nextInt());
                }
            }
        }
        try {
            if (event.getEntity() != null && event.getEntity() instanceof Spider && AMConfig.spidersAttackFlies) {
                Spider spider = (Spider) event.getEntity();
                spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal(spider, EntityFly.class, 1, true, false, null));
            }
            if (event.getEntity() != null && event.getEntity() instanceof Wolf && AMConfig.wolvesAttackMoose) {
                Wolf wolf = (Wolf) event.getEntity();
                wolf.targetSelector.addGoal(6, new NonTameRandomTargetGoal(wolf, EntityMoose.class, false, null));
            }
            if (event.getEntity() != null && event.getEntity() instanceof PolarBear && AMConfig.polarBearsAttackSeals) {
                PolarBear bear = (PolarBear) event.getEntity();
                bear.targetSelector.addGoal(6, new NearestAttackableTargetGoal(bear, EntitySeal.class, 15, true, true, null));
            }
            if (event.getEntity() != null && event.getEntity() instanceof Creeper) {
                Creeper creeper = (Creeper) event.getEntity();
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, EntitySnowLeopard.class, 6.0F, 1.0D, 1.2D));
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, EntityTiger.class, 6.0F, 1.0D, 1.2D));
            }
            if (event.getEntity() != null && (event.getEntity() instanceof Fox || event.getEntity() instanceof Cat || event.getEntity() instanceof Ocelot) && AMConfig.catsAndFoxesAttackJerboas) {
                Mob mb = (Mob) event.getEntity();
                mb.targetSelector.addGoal(6, new NearestAttackableTargetGoal(mb, EntityJerboa.class, 45, true, true, null));
            }
            if (event.getEntity() != null && event.getEntity() instanceof Rabbit && AMConfig.bunfungusTransformation) {
                Rabbit rabbit = (Rabbit) event.getEntity();
                rabbit.goalSelector.addGoal(3, new TemptGoal(rabbit, 1.0D, Ingredient.of(AMItemRegistry.MUNGAL_SPORES.get()), false));
            }
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
        if (event.getPlayer().getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.MOOSE_HEADGEAR.get() && event.getTarget() instanceof LivingEntity) {
            float f1 = 2;
            ((LivingEntity) event.getTarget()).knockback(f1 * 0.5F, Mth.sin(event.getPlayer().getYRot() * ((float) Math.PI / 180F)), -Mth.cos(event.getPlayer().getYRot() * ((float) Math.PI / 180F)));
        }
        if (event.getPlayer().hasEffect(AMEffectRegistry.TIGERS_BLESSING) && event.getTarget() instanceof LivingEntity && !event.getTarget().isAlliedTo(event.getPlayer()) && !(event.getTarget() instanceof EntityTiger)) {
            AABB bb = new AABB(event.getPlayer().getX() - 32, event.getPlayer().getY() - 32, event.getPlayer().getZ() - 32, event.getPlayer().getZ() + 32, event.getPlayer().getY() + 32, event.getPlayer().getZ() + 32);
            List<EntityTiger> tigers = event.getPlayer().level.getEntitiesOfClass(EntityTiger.class, bb, EntitySelector.ENTITY_STILL_ALIVE);
            for (EntityTiger tiger : tigers) {
                if (!tiger.isBaby()) {
                    tiger.setTarget((LivingEntity) event.getTarget());
                }
            }

        }
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            if (event.getAmount() > 0 && attacker.hasEffect(AMEffectRegistry.SOULSTEAL) && attacker.getEffect(AMEffectRegistry.SOULSTEAL) != null) {
                int level = attacker.getEffect(AMEffectRegistry.SOULSTEAL).getAmplifier() + 1;
                Random rand = new Random();
                if (attacker.getHealth() < attacker.getMaxHealth() && rand.nextFloat() < (0.25F + (level * 0.25F))) {
                    attacker.heal(Math.min(event.getAmount() / 2F * level, 2 + 2 * level));
                }
            }
        }
        if (event.getEntityLiving() instanceof Player && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            Player player = (Player) event.getEntityLiving();
            if (attacker instanceof EntityMimicOctopus && ((EntityMimicOctopus) attacker).isOwnedBy(player)) {
                event.setCanceled(true);
                return;
            }
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()) {
                float f1 = 1F;
                if (attacker.distanceTo(player) < attacker.getBbWidth() + player.getBbWidth() + 0.5F) {
                    attacker.hurt(DamageSource.thorns(player), 1F);
                    attacker.knockback(f1 * 0.5F, Mth.sin((attacker.getYRot() + 180) * ((float) Math.PI / 180F)), -Mth.cos((attacker.getYRot() + 180) * ((float) Math.PI / 180F)));
                }
            }
        }
        if (!event.getEntityLiving().getItemBySlot(EquipmentSlot.LEGS).isEmpty() && event.getEntityLiving().getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.EMU_LEGGINGS.get()) {
            if (event.getSource().isProjectile() && event.getEntityLiving().getRandom().nextFloat() < AMConfig.emuPantsDodgeChance) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onStructureGetSpawnLists(StructureSpawnListGatherEvent event) {
        if (AMConfig.mimicubeSpawnInEndCity && AMConfig.mimicubeSpawnWeight > 0) {
            if (event.getStructure() == StructureFeature.END_CITY) {
                event.addEntitySpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.MIMICUBE.get(), AMConfig.mimicubeSpawnWeight, 1, 3));
            }
        }
        if (AMConfig.soulVultureSpawnOnFossil && AMConfig.soulVultureSpawnWeight > 0) {
            if (event.getStructure() == StructureFeature.NETHER_FOSSIL) {
                event.addEntitySpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(AMEntityRegistry.SOUL_VULTURE.get(), AMConfig.soulVultureSpawnWeight, 1, 1));
            }
        }
    }

    @SubscribeEvent
    public void onLivingSetTargetEvent(LivingSetAttackTargetEvent event) {
        if (event.getTarget() != null && event.getEntityLiving() instanceof Mob) {
            if (event.getEntityLiving().getMobType() == MobType.ARTHROPOD) {
                if (event.getTarget().hasEffect(AMEffectRegistry.BUG_PHEROMONES) && event.getEntityLiving().getLastHurtByMob() != event.getTarget()) {
                    ((Mob) event.getEntityLiving()).setTarget(null);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player) {
            if (event.getEntityLiving().getEyeHeight() < event.getEntityLiving().getBbHeight() * 0.5D) {
                event.getEntityLiving().refreshDimensions();
            }
            AttributeInstance modifiableattributeinstance = event.getEntityLiving().getAttribute(Attributes.MOVEMENT_SPEED);
            if (event.getEntityLiving().getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS.get() || modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                boolean sand = event.getEntityLiving().level.getBlockState(getDownPos(event.getEntityLiving().blockPosition(), event.getEntityLiving().level)).is(BlockTags.SAND);
                if (sand && !modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                    modifiableattributeinstance.addPermanentModifier(SAND_SPEED_BONUS);
                }
                if (event.getEntityLiving().tickCount % 25 == 0 && (event.getEntityLiving().getItemBySlot(EquipmentSlot.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS.get() || !sand) && modifiableattributeinstance.hasModifier(SAND_SPEED_BONUS)) {
                    modifiableattributeinstance.removeModifier(SAND_SPEED_BONUS);
                }
            }
            if (event.getEntityLiving().getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.FRONTIER_CAP.get() || modifiableattributeinstance.hasModifier(SNEAK_SPEED_BONUS)) {
                if (event.getEntityLiving().isShiftKeyDown() && !modifiableattributeinstance.hasModifier(SNEAK_SPEED_BONUS)) {
                    modifiableattributeinstance.addPermanentModifier(SNEAK_SPEED_BONUS);
                }
                if ((!event.getEntityLiving().isShiftKeyDown() || event.getEntityLiving().getItemBySlot(EquipmentSlot.HEAD).getItem() != AMItemRegistry.FRONTIER_CAP.get()) && modifiableattributeinstance.hasModifier(SNEAK_SPEED_BONUS)) {
                    modifiableattributeinstance.removeModifier(SNEAK_SPEED_BONUS);
                }
            }
            if (event.getEntityLiving().getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()) {
                if (!event.getEntityLiving().isEyeInFluid(FluidTags.WATER)) {
                    event.getEntityLiving().addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 210, 0, false, false, true));
                }
            }
        }
        ItemStack boots = event.getEntityLiving().getItemBySlot(EquipmentSlot.FEET);
        if (!boots.isEmpty() && boots.hasTag() && boots.getOrCreateTag().contains("BisonFur") && boots.getOrCreateTag().getBoolean("BisonFur")) {
            BlockPos pos = new BlockPos(event.getEntityLiving().getX(), event.getEntity().getY() - 0.5F, event.getEntityLiving().getZ());
            if (event.getEntityLiving().level.getBlockState(pos).is(Blocks.POWDER_SNOW)) {
                event.getEntityLiving().setOnGround(true);
                event.getEntityLiving().setTicksFrozen(0);

            }
            if (event.getEntityLiving().isInPowderSnow) {
                float f = 0;
                event.getEntityLiving().setPos(event.getEntityLiving().getX(), pos.getY() + 1, event.getEntityLiving().getZ());
            }
        }

        if (event.getEntityLiving().getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS.get()) {
            if (event.getEntityLiving().horizontalCollision && !event.getEntityLiving().isInWater()) {
                event.getEntityLiving().fallDistance = 0.0F;
                Vec3 motion = event.getEntityLiving().getDeltaMovement();
                double d0 = Mth.clamp(motion.x, -0.15F, 0.15F);
                double d1 = Mth.clamp(motion.z, -0.15F, 0.15F);
                double d2 = 0.1D;
                if (d2 < 0.0D && !event.getEntityLiving().getFeetBlockState().isScaffolding(event.getEntityLiving()) && event.getEntityLiving().isSuppressingSlidingDownLadder()) {
                    d2 = 0.0D;
                }
                motion = new Vec3(d0, d2, d1);
                event.getEntityLiving().setDeltaMovement(motion);
            }
        }
        if (event.getEntityLiving().getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SOMBRERO.get() && !event.getEntityLiving().level.isClientSide && AlexsMobs.isAprilFools() && event.getEntityLiving().isInWaterOrBubble()) {
            Random random = event.getEntityLiving().getRandom();
            if(random.nextInt(245) == 0 && !EntitySeaBear.isMobSafe(event.getEntityLiving())){
                int dist = 32;
                List<EntitySeaBear> nearbySeabears = event.getEntityLiving().level.getEntitiesOfClass(EntitySeaBear.class, event.getEntityLiving().getBoundingBox().inflate(dist, dist, dist));
                if(nearbySeabears.isEmpty()){
                    EntitySeaBear bear = AMEntityRegistry.SEA_BEAR.get().create(event.getEntityLiving().level);
                    BlockPos at = event.getEntityLiving().blockPosition();
                    BlockPos farOff = null;
                    for(int i = 0; i < 15; i++){
                        int f1 = (int) Math.signum(random.nextFloat() - 0.5F);
                        int f2 = (int) Math.signum(random.nextFloat() - 0.5F);
                        BlockPos pos1 = at.offset(f1 * (10 + random.nextInt(dist - 10)), random.nextInt(1), f2 * (10 + random.nextInt(dist - 10)));
                        BlockState state = event.getEntityLiving().getLevel().getBlockState(pos1);
                        if(event.getEntityLiving().level.isWaterAt(pos1)){
                            farOff = pos1;
                        }
                    }
                    if(farOff != null){
                        bear.setPos(farOff.getX() + 0.5F, farOff.getY() + 0.5F, farOff.getZ() + 0.5F);
                        bear.setYRot(random.nextFloat() * 360F);
                        bear.setTarget(event.getEntityLiving());
                        event.getEntityLiving().level.addFreshEntity(bear);
                    }
                }else{
                    for(EntitySeaBear bear : nearbySeabears){
                        bear.setTarget(event.getEntityLiving());
                    }
                }
            }
        }
        if (VineLassoUtil.hasLassoData(event.getEntityLiving())) {
            VineLassoUtil.tickLasso(event.getEntityLiving());
        }
        if (RockyChestplateUtil.isWearing(event.getEntityLiving())) {
            RockyChestplateUtil.tickRockyRolling(event.getEntityLiving());
        }
    }

    private BlockPos getDownPos(BlockPos entered, LevelAccessor world) {
        int i = 0;
        while (world.isEmptyBlock(entered) && i < 3) {
            entered = entered.below();
            i++;
        }
        return entered;
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVModifierEvent event) {
        if (event.getEntity().hasEffect(AMEffectRegistry.FEAR) || event.getEntity().hasEffect(AMEffectRegistry.POWER_DOWN)) {
            event.setNewfov(1.0F);
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!event.getEntityLiving().getUseItem().isEmpty() && event.getSource() != null && event.getSource().getEntity() != null) {
            if (event.getEntityLiving().getUseItem().getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()) {
                Entity attacker = event.getSource().getEntity();
                if (attacker instanceof LivingEntity) {
                    boolean flag = false;
                    if (attacker.distanceTo(event.getEntityLiving()) <= 4 && !((LivingEntity) attacker).hasEffect(AMEffectRegistry.EXSANGUINATION)) {
                        ((LivingEntity) attacker).addEffect(new MobEffectInstance(AMEffectRegistry.EXSANGUINATION, 60, 2));
                        flag = true;
                    }
                    if (event.getEntityLiving().isInWaterOrBubble()) {
                        event.getEntityLiving().setAirSupply(Math.min(event.getEntityLiving().getMaxAirSupply(), event.getEntityLiving().getAirSupply() + 150));
                        flag = true;
                    }
                    if (flag) {
                        event.getEntityLiving().getUseItem().hurtAndBreak(1, event.getEntityLiving(), (playerIn) -> {
                            playerIn.broadcastBreakEvent(event.getEntityLiving().getUsedItemHand());
                        });
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChestGenerated(LootTableLoadEvent event) {
        if (event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE)) {
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(AMItemRegistry.ANCIENT_DART.get()).setQuality(40).setWeight(1);
            LootPool.Builder builder = new LootPool.Builder().name("am_dart").add(item).when(LootItemRandomChanceCondition.randomChance(1f)).setRolls(UniformGenerator.between(0, 1)).setBonusRolls(UniformGenerator.between(0, 1));
            event.getTable().addPool(builder.build());
        }
        if (event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER)) {
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(AMItemRegistry.ANCIENT_DART.get()).setQuality(20).setWeight(3);
            LootPool.Builder builder = new LootPool.Builder().name("am_dart_dispenser").add(item).when(LootItemRandomChanceCondition.randomChance(1f)).setRolls(UniformGenerator.between(0, 2)).setBonusRolls(UniformGenerator.between(0, 1));
            event.getTable().addPool(builder.build());
        }
        if (event.getName().equals(BuiltInLootTables.PIGLIN_BARTERING) && AMConfig.tusklinShoesBarteringChance > 0) {
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(AMItemRegistry.PIGSHOES.get()).setQuality(5).setWeight(8);
            LootPool.Builder builder = new LootPool.Builder().name("am_pigshoes").add(item).when(LootItemRandomChanceCondition.randomChance((float) AMConfig.tusklinShoesBarteringChance)).setRolls(ConstantValue.exactly(1));
            event.getTable().addPool(builder.build());
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().hasTag() && event.getItemStack().getOrCreateTag().contains("BisonFur") && event.getItemStack().getOrCreateTag().getBoolean("BisonFur")) {
            event.getToolTip().add(new TranslatableComponent("item.alexsmobs.insulated_with_fur").withStyle(ChatFormatting.AQUA));
        }
    }
}
