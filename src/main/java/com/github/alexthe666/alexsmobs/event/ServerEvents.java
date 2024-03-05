package com.github.alexthe666.alexsmobs.event;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.effect.EffectClinging;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.github.alexthe666.alexsmobs.entity.util.RainbowUtil;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.entity.util.VineLassoUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ILeftClick;
import com.github.alexthe666.alexsmobs.item.ItemGhostlyPickaxe;
import com.github.alexthe666.alexsmobs.message.MessageSwingArm;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.misc.EmeraldsForItemsTrade;
import com.github.alexthe666.alexsmobs.misc.ItemsForEmeraldsTrade;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.github.alexthe666.alexsmobs.world.BeachedCachalotWhaleSpawner;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
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
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import org.antlr.v4.runtime.misc.Triple;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = AlexsMobs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    public static final UUID ALEX_UUID = UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c");
    public static final UUID CARRO_UUID = UUID.fromString("98905d4a-1cbc-41a4-9ded-2300404e2290");
    private static final UUID SAND_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28E");
    private static final UUID SNEAK_SPEED_MODIFIER = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF28F");
    private static final AttributeModifier SAND_SPEED_BONUS = new AttributeModifier(SAND_SPEED_MODIFIER, "roadrunner speed bonus", 0.1F, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SNEAK_SPEED_BONUS = new AttributeModifier(SNEAK_SPEED_MODIFIER, "frontier cap speed bonus", 0.1F, AttributeModifier.Operation.ADDITION);
    private static final Map<ServerLevel, BeachedCachalotWhaleSpawner> BEACHED_CACHALOT_WHALE_SPAWNER_MAP = new HashMap<>();
    public static final ObjectList<Triple<ServerPlayer, ServerLevel, BlockPos>> teleportPlayers = new ObjectArrayList<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent tick) {
        if (!tick.level.isClientSide && tick.level instanceof ServerLevel serverWorld) {
            BEACHED_CACHALOT_WHALE_SPAWNER_MAP.computeIfAbsent(serverWorld,
                k -> new BeachedCachalotWhaleSpawner(serverWorld));
            BeachedCachalotWhaleSpawner spawner = BEACHED_CACHALOT_WHALE_SPAWNER_MAP.get(serverWorld);
            spawner.tick();

            if (!teleportPlayers.isEmpty()) {
                for (final var triple : teleportPlayers) {
                    ServerPlayer player = triple.a;
                    ServerLevel endpointWorld = triple.b;
                    BlockPos endpoint = triple.c;
                    final int heightFromMap = endpointWorld.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, endpoint.getX(), endpoint.getZ());
                    endpoint = new BlockPos(endpoint.getX(), Math.max(heightFromMap, endpoint.getY()), endpoint.getZ());
                    player.teleportTo(endpointWorld, endpoint.getX() + 0.5D, endpoint.getY() + 0.5D, endpoint.getZ() + 0.5D, player.getYRot(), player.getXRot());
                    ChunkPos chunkpos = new ChunkPos(endpoint);
                    endpointWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, player.getId());
                    player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));

                }
                teleportPlayers.clear();
            }
        }
        AMWorldData data = AMWorldData.get(tick.level);
        if (data != null) {
            data.tickPupfish();
        }
    }

    protected static BlockHitResult rayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        final float x = player.getXRot();
        final float y = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        final float f0 = -y * Mth.DEG_TO_RAD - Mth.PI;
        final float f1 = -x * Mth.DEG_TO_RAD;
        final float f2 = Mth.cos(f0);
        final float f3 = Mth.sin(f0);
        final float f4 = -Mth.cos(f1);
        final float f5 = Mth.sin(f1);
        final float f6 = f3 * f4;
        final float f7 = f2 * f4;
        final double d0 = player.getAttribute(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()).getValue();
        Vec3 vector3d1 = vector3d.add(f6 * d0, f5 * d0, f7 * d0);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
    }


    private static final Random RAND = new Random();

    @SubscribeEvent
    public static void onItemUseLast(LivingEntityUseItemEvent.Finish event) {
        if (event.getItem().getItem() == Items.CHORUS_FRUIT && RAND.nextInt(3) == 0
            && event.getEntity().hasEffect(AMEffectRegistry.ENDER_FLU.get())) {
            event.getEntity().removeEffect(AMEffectRegistry.ENDER_FLU.get());
        }
    }

    @SubscribeEvent
    public static void onEntityResize(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player entity) {
            final var potions = entity.getActiveEffectsMap();
            if (event.getEntity().level() != null && potions != null && !potions.isEmpty()
                && potions.containsKey(AMEffectRegistry.CLINGING)) {
                if (EffectClinging.isUpsideDown(entity)) {
                    float minus = event.getOldSize().height - event.getOldEyeHeight();
                    event.setNewEyeHeight(minus);
                }
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (AMConfig.giveBookOnStartup) {
            CompoundTag playerData = event.getEntity().getPersistentData();
            CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            if (data != null && !data.getBoolean("alexsmobs_has_book")) {
                ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(AMItemRegistry.ANIMAL_DICTIONARY.get()));
                final boolean isAlex = Objects.equals(event.getEntity().getUUID(), ALEX_UUID);
                if (isAlex || Objects.equals(event.getEntity().getUUID(), CARRO_UUID)) {
                    ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(AMItemRegistry.BEAR_DUST.get()));
                }
                if (isAlex) {
                    ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(AMItemRegistry.NOVELTY_HAT.get()));
                }
                data.putBoolean("alexsmobs_has_book", true);
                playerData.put(Player.PERSISTED_NBT_TAG, data);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        boolean flag = false;
        ItemStack leftItem = event.getEntity().getOffhandItem();
        ItemStack rightItem = event.getEntity().getMainHandItem();
        if(leftItem.getItem() instanceof final ILeftClick iLeftClick){
            iLeftClick.onLeftClick(leftItem, event.getEntity());
            flag = true;
        }
        if(rightItem.getItem() instanceof final ILeftClick iLeftClick){
            iLeftClick.onLeftClick(rightItem, event.getEntity());
            flag = true;
        }
        if (flag && event.getLevel().isClientSide) {
            AlexsMobs.sendMSGToServer(MessageSwingArm.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void onStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity().getType() == EntityType.SQUID && !event.getEntity().level().isClientSide) {
            ServerLevel level = (ServerLevel) event.getEntity().level();
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
        if (event.getRayTraceResult() instanceof EntityHitResult hitResult
            && hitResult.getEntity() instanceof EntityEmu emu && !event.getEntity().level().isClientSide) {
            if (event.getEntity() instanceof AbstractArrow arrow) {
                //fixes soft crash with vanilla
                arrow.setPierceLevel((byte) 0);
            }
            if ((emu.getAnimation() == EntityEmu.ANIMATION_DODGE_RIGHT || emu.getAnimation() == EntityEmu.ANIMATION_DODGE_LEFT) && emu.getAnimationTick() < 7) {
                event.setCanceled(true);
            }
            if (emu.getAnimation() != EntityEmu.ANIMATION_DODGE_RIGHT && emu.getAnimation() != EntityEmu.ANIMATION_DODGE_LEFT) {
                boolean left = true;
                Vec3 arrowPos = event.getEntity().position();
                Vec3 rightVector = emu.getLookAngle().yRot(0.5F * Mth.PI).add(emu.position());
                Vec3 leftVector = emu.getLookAngle().yRot(-0.5F * Mth.PI).add(emu.position());
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
                if (!event.getEntity().level().isClientSide) {
                    if (event.getEntity() instanceof Projectile projectile) {
                        if (projectile.getOwner() instanceof ServerPlayer serverPlayer) {
                            AMAdvancementTriggerRegistry.EMU_DODGE.trigger(serverPlayer);
                        }
                    }
                }
                emu.setDeltaMovement(emu.getDeltaMovement().add(vector3d2.x() * 0.5F, 0.32F, vector3d2.z() * 0.5F));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDespawnAttempt(MobSpawnEvent.AllowDespawn event) {
        if (event.getEntity().hasEffect(AMEffectRegistry.DEBILITATING_STING.get()) && event.getEntity().getEffect(AMEffectRegistry.DEBILITATING_STING.get()) != null && event.getEntity().getEffect(AMEffectRegistry.DEBILITATING_STING.get()).getAmplifier() > 0) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onTradeSetup(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FISHERMAN) {
            VillagerTrades.ItemListing ambergrisTrade = new EmeraldsForItemsTrade(AMItemRegistry.AMBERGRIS.get(), 20, 3, 4);
            final var list = event.getTrades().get(2);
            list.add(ambergrisTrade);
            event.getTrades().put(2, list);
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
            if (src.getEntity() instanceof EntitySnowLeopard) {
                event.setLootingLevel(event.getLootingLevel() + 2);
            }
        }

    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        final var player = event.getEntity();
        if (event.getItemStack().getItem() == Items.WHEAT && player.getVehicle() instanceof EntityElephant elephant) {
            if (elephant.triggerCharge(event.getItemStack())) {
                player.swing(event.getHand());
                if (!player.isCreative()) {
                    event.getItemStack().shrink(1);
                }
            }
        }
        if (event.getItemStack().getItem() == Items.GLASS_BOTTLE && AMConfig.lavaBottleEnabled) {
            HitResult raytraceresult = rayTrace(event.getLevel(), player, ClipContext.Fluid.SOURCE_ONLY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
                if (event.getLevel().mayInteract(player, blockpos)) {
                    if (event.getLevel().getFluidState(blockpos).is(FluidTags.LAVA)) {
                        player.gameEvent(GameEvent.ITEM_INTERACT_START);
                        event.getLevel().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        player.setSecondsOnFire(6);
                        if (!player.addItem(new ItemStack(AMItemRegistry.LAVA_BOTTLE.get()))) {
                            player.spawnAtLocation(new ItemStack(AMItemRegistry.LAVA_BOTTLE.get()));
                        }
                        player.swing(event.getHand());
                        if (!player.isCreative()) {
                            event.getItemStack().shrink(1);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof LivingEntity living) {
            if (!event.getEntity().isShiftKeyDown() && VineLassoUtil.hasLassoData(living)) {
                if (!event.getEntity().level().isClientSide) {
                    event.getTarget().spawnAtLocation(new ItemStack(AMItemRegistry.VINE_LASSO.get()));
                }
                VineLassoUtil.lassoTo(null, living);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
            if (!(event.getTarget() instanceof Player) && !(event.getTarget() instanceof EntityEndergrade)
                    && living.hasEffect(AMEffectRegistry.ENDER_FLU.get())) {
                if (event.getItemStack().getItem() == Items.CHORUS_FRUIT) {
                    if (!event.getEntity().isCreative()) {
                        event.getItemStack().shrink(1);
                    }
                    event.getTarget().gameEvent(GameEvent.EAT);
                    event.getTarget().playSound(SoundEvents.GENERIC_EAT, 1.0F, 0.5F + event.getEntity().getRandom().nextFloat());
                    if (event.getEntity().getRandom().nextFloat() < 0.4F) {
                        living.removeEffect(AMEffectRegistry.ENDER_FLU.get());
                        Items.CHORUS_FRUIT.finishUsingItem(event.getItemStack().copy(), event.getLevel(), ((LivingEntity) event.getTarget()));
                    }
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
            if (RainbowUtil.getRainbowType(living) > 0 && (event.getItemStack().getItem() == Items.SPONGE)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                RainbowUtil.setRainbowType(living, 0);
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
                if (!event.getEntity().addItem(wetSponge)) {
                    event.getEntity().drop(wetSponge, true);
                }
            }
            if (living instanceof Rabbit rabbit && event.getItemStack().getItem() == AMItemRegistry.MUNGAL_SPORES.get()
                    && AMConfig.bunfungusTransformation) {
                final var random = ThreadLocalRandom.current();
                if (!event.getEntity().level().isClientSide && random.nextFloat() < 0.15F) {
                    final EntityBunfungus bunfungus = rabbit.convertTo(AMEntityRegistry.BUNFUNGUS.get(), true);
                    if (bunfungus != null) {
                        event.getEntity().level().addFreshEntity(bunfungus);
                        bunfungus.setTransformsIn(EntityBunfungus.MAX_TRANSFORM_TIME);
                    }
                } else {
                    for (int i = 0; i < 2 + random.nextInt(2); i++) {
                        final double d0 = random.nextGaussian() * 0.02D;
                        final double d1 = 0.05F + random.nextGaussian() * 0.02D;
                        final double d2 = random.nextGaussian() * 0.02D;
                        event.getTarget().level().addParticle(AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get(), event.getTarget().getRandomX(0.7F), event.getTarget().getY(0.6F), event.getTarget().getRandomZ(0.7F), d0, d1, d2);
                    }
                }
                if (!event.getEntity().isCreative()) {
                    event.getItemStack().shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public void onUseItemAir(PlayerInteractEvent.RightClickEmpty event) {
        ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (stack.isEmpty()) {
            stack = event.getEntity().getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if (RainbowUtil.getRainbowType(event.getEntity()) > 0 && (stack.is(Items.SPONGE))) {
            event.getEntity().swing(InteractionHand.MAIN_HAND);
            RainbowUtil.setRainbowType(event.getEntity(), 0);
            if (!event.getEntity().isCreative()) {
                stack.shrink(1);
            }
            ItemStack wetSponge = new ItemStack(Items.WET_SPONGE);
            if (!event.getEntity().addItem(wetSponge)) {
                event.getEntity().drop(wetSponge, true);
            }
        }
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event) {
        if (AlexsMobs.isAprilFools() && event.getItemStack().is(Items.STICK)
            && !event.getEntity().getCooldowns().isOnCooldown(Items.STICK)) {
            BlockState state = event.getEntity().level().getBlockState(event.getPos());
            boolean flag = false;
            if (state.is(Blocks.SAND)) {
                flag = true;
                event.getEntity().level().setBlockAndUpdate(event.getPos(), AMBlockRegistry.SAND_CIRCLE.get().defaultBlockState());
            } else if (state.is(Blocks.RED_SAND)) {
                flag = true;
                event.getEntity().level().setBlockAndUpdate(event.getPos(), AMBlockRegistry.RED_SAND_CIRCLE.get().defaultBlockState());
            }
            if (flag) {
                event.setCanceled(true);
                event.getEntity().gameEvent(GameEvent.BLOCK_PLACE);
                event.getEntity().playSound(SoundEvents.SAND_BREAK, 1, 1);
                event.getEntity().getCooldowns().addCooldown(Items.STICK, 30);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        if (VineLassoUtil.hasLassoData(event.getEntity())) {
            VineLassoUtil.lassoTo(null, event.getEntity());
            event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), new ItemStack(AMItemRegistry.VINE_LASSO.get())));
        }
    }

    @SubscribeEvent
    public void onEntityFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        final var entity = event.getEntity();
        if (entity instanceof WanderingTrader trader && AMConfig.elephantTraderSpawnChance > 0) {
            Biome biome = event.getLevel().getBiome(entity.blockPosition()).value();
            if (RAND.nextFloat() <= AMConfig.elephantTraderSpawnChance && (!AMConfig.limitElephantTraderBiomes || biome.getBaseTemperature() >= 1.0F)) {
                ChunkPos chunkPos = new ChunkPos(trader.blockPosition());
                if(event.getLevel().getChunkSource().getChunkNow(chunkPos.x, chunkPos.z) != null) {
                    EntityElephant elephant = AMEntityRegistry.ELEPHANT.get().create(trader.level());
                    elephant.copyPosition(trader);
                    if (elephant.canSpawnWithTraderHere()) {
                        elephant.setTrader(true);
                        elephant.setChested(true);
                        if (!event.getLevel().isClientSide()) {
                            trader.level().addFreshEntity(elephant);
                            trader.startRiding(elephant, true);
                        }
                        elephant.addElephantLoot(null, RAND.nextInt());
                    }
                }
            }
        }
        try {
            if (AMConfig.spidersAttackFlies && entity instanceof final Spider spider) {
                spider.targetSelector.addGoal(4,
                    new NearestAttackableTargetGoal<>(spider, EntityFly.class, 1, true, false, null));
            }
            else if (AMConfig.wolvesAttackMoose && entity instanceof final Wolf wolf) {
                wolf.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(wolf, EntityMoose.class, false, null));
            }
            else if (AMConfig.polarBearsAttackSeals && entity instanceof final PolarBear bear) {
                bear.targetSelector.addGoal(6,
                    new NearestAttackableTargetGoal<>(bear, EntitySeal.class, 15, true, true, null));
            }
            else if (entity instanceof final Creeper creeper) {
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, EntitySnowLeopard.class, 6.0F, 1.0D, 1.2D));
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, EntityTiger.class, 6.0F, 1.0D, 1.2D));
            }
            else if (AMConfig.catsAndFoxesAttackJerboas
                    && (entity instanceof Fox || entity instanceof Cat || entity instanceof Ocelot)) {
                Mob mb = (Mob) entity;
                mb.targetSelector.addGoal(6,
                    new NearestAttackableTargetGoal<>(mb, EntityJerboa.class, 45, true, true, null));
            }
            else if (AMConfig.bunfungusTransformation && entity instanceof final Rabbit rabbit) {
                rabbit.goalSelector.addGoal(3, new TemptGoal(rabbit, 1.0D, Ingredient.of(AMItemRegistry.MUNGAL_SPORES.get()), false));
            }
            else if (AMConfig.dolphinsAttackFlyingFish && entity instanceof final Dolphin dolphin) {
                dolphin.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(dolphin, EntityFlyingFish.class, 70, true, true, null));
            }
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
        if (event.getTarget() instanceof LivingEntity living) {
            if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.MOOSE_HEADGEAR.get()) {
                living.knockback(1F, Mth.sin(event.getEntity().getYRot() * Mth.DEG_TO_RAD),
                        -Mth.cos(event.getEntity().getYRot() * Mth.DEG_TO_RAD));
            }
            if (event.getEntity().hasEffect(AMEffectRegistry.TIGERS_BLESSING.get())
                    && !event.getTarget().isAlliedTo(event.getEntity()) && !(event.getTarget() instanceof EntityTiger)) {
                AABB bb = new AABB(event.getEntity().getX() - 32, event.getEntity().getY() - 32, event.getEntity().getZ() - 32, event.getEntity().getZ() + 32, event.getEntity().getY() + 32, event.getEntity().getZ() + 32);
                final var tigers = event.getEntity().level().getEntitiesOfClass(EntityTiger.class, bb,
                        EntitySelector.ENTITY_STILL_ALIVE);
                for (EntityTiger tiger : tigers) {
                    if (!tiger.isBaby()) {
                        tiger.setTarget(living);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof final LivingEntity attacker) {
            if (event.getAmount() > 0 && attacker.hasEffect(AMEffectRegistry.SOULSTEAL.get()) && attacker.getEffect(AMEffectRegistry.SOULSTEAL.get()) != null) {
                final int level = attacker.getEffect(AMEffectRegistry.SOULSTEAL.get()).getAmplifier() + 1;
                if (attacker.getHealth() < attacker.getMaxHealth()
                    && ThreadLocalRandom.current().nextFloat() < (0.25F + (level * 0.25F))) {
                    attacker.heal(Math.min(event.getAmount() / 2F * level, 2 + 2 * level));
                }
            }

            if (event.getEntity() instanceof final Player player) {
                if (attacker instanceof final EntityMimicOctopus octupus && octupus.isOwnedBy(player)) {
                    event.setCanceled(true);
                    return;
                }
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()) {
                    if (attacker.distanceTo(player) < attacker.getBbWidth() + player.getBbWidth() + 0.5F) {
                        attacker.hurt(attacker.damageSources().thorns(player), 1F);
                        attacker.knockback(0.5F, Mth.sin((attacker.getYRot() + 180) * Mth.DEG_TO_RAD),
                            -Mth.cos((attacker.getYRot() + 180) * Mth.DEG_TO_RAD));
                    }
                }
            }
        }
        if (!event.getEntity().getItemBySlot(EquipmentSlot.LEGS).isEmpty() && event.getEntity().getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.EMU_LEGGINGS.get()) {
            if (event.getSource().is(DamageTypeTags.IS_PROJECTILE) && event.getEntity().getRandom().nextFloat() < AMConfig.emuPantsDodgeChance) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingSetTargetEvent(LivingChangeTargetEvent event) {
        if (event.getNewTarget() != null && event.getEntity() instanceof Mob mob) {
            if (mob.getMobType() == MobType.ARTHROPOD) {
                if (event.getNewTarget().hasEffect(AMEffectRegistry.BUG_PHEROMONES.get()) && event.getEntity().getLastHurtByMob() != event.getNewTarget()) {
                    event.setCanceled(true);
                    return;
                }
            }
            if (mob.getMobType() == MobType.UNDEAD && !mob.getType().is(AMTagRegistry.IGNORES_KIMONO)) {
                if (event.getNewTarget().getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO.get()) && event.getEntity().getLastHurtByMob() != event.getNewTarget()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingTickEvent event) {
        final var entity = event.getEntity();
        if (entity instanceof Player player) {
            if (player.getEyeHeight() < player.getBbHeight() * 0.5D) {
                player.refreshDimensions();
            }
            if(entity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)){
                final var attributes = entity.getAttribute(Attributes.MOVEMENT_SPEED);
                if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.ROADDRUNNER_BOOTS.get()
                        || attributes.hasModifier(SAND_SPEED_BONUS)) {
                    final boolean sand = player.level().getBlockState(getDownPos(player.blockPosition(), player.level()))
                            .is(BlockTags.SAND);
                    if (sand && !attributes.hasModifier(SAND_SPEED_BONUS)) {
                        attributes.addPermanentModifier(SAND_SPEED_BONUS);
                    }
                    if (player.tickCount % 25 == 0
                            && (player.getItemBySlot(EquipmentSlot.FEET).getItem() != AMItemRegistry.ROADDRUNNER_BOOTS.get()
                            || !sand)
                            && attributes.hasModifier(SAND_SPEED_BONUS)) {
                        attributes.removeModifier(SAND_SPEED_BONUS);
                    }
                }
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.FRONTIER_CAP.get()
                        || attributes.hasModifier(SNEAK_SPEED_BONUS)) {
                    final var shift = player.isShiftKeyDown();
                    if (shift && !attributes.hasModifier(SNEAK_SPEED_BONUS)) {
                        attributes.addPermanentModifier(SNEAK_SPEED_BONUS);
                    }
                    if ((!shift || player.getItemBySlot(EquipmentSlot.HEAD).getItem() != AMItemRegistry.FRONTIER_CAP.get())
                            && attributes.hasModifier(SNEAK_SPEED_BONUS)) {
                        attributes.removeModifier(SNEAK_SPEED_BONUS);
                    }
                }
            }
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SPIKED_TURTLE_SHELL.get()) {
                if (!player.isEyeInFluid(FluidTags.WATER)) {
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 310, 0, false, false, true));
                }
            }
        }
        final ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.isEmpty() && boots.hasTag() && boots.getOrCreateTag().contains("BisonFur") && boots.getOrCreateTag().getBoolean("BisonFur")) {
            BlockPos posBelow = new BlockPos((int) event.getEntity().getX(), (int) (entity.getBoundingBox().minY - 0.1F), (int) entity.getZ());
            if (entity.level().getBlockState(posBelow).is(Blocks.POWDER_SNOW)) {
                entity.setOnGround(true);
                entity.setTicksFrozen(0);
                entity.setPos(entity.getX(), Math.max(entity.getY(), posBelow.getY() + 1F), entity.getZ());
            }
            if (entity.isInPowderSnow) {
                entity.setOnGround(true);
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1F, 0));
            }
        }
        if (entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == AMItemRegistry.CENTIPEDE_LEGGINGS.get()) {
            if (entity.horizontalCollision && !entity.isInWater()) {
                entity.fallDistance = 0.0F;
                Vec3 motion = entity.getDeltaMovement();
                double d2 = 0.1D;
                if (entity.isShiftKeyDown() || !entity.getFeetBlockState().isScaffolding(entity) && entity.isSuppressingSlidingDownLadder()) {
                    d2 = 0.0D;
                }
                motion = new Vec3(Mth.clamp(motion.x, -0.15F, 0.15F), d2, Mth.clamp(motion.z, -0.15F, 0.15F));
                entity.setDeltaMovement(motion);
            }
        }
        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == AMItemRegistry.SOMBRERO.get() && !entity.level().isClientSide && AlexsMobs.isAprilFools() && entity.isInWaterOrBubble()) {
            RandomSource random = entity.getRandom();
            if (random.nextInt(245) == 0 && !EntitySeaBear.isMobSafe(entity)) {
                final int dist = 32;
                final var nearbySeabears = entity.level().getEntitiesOfClass(EntitySeaBear.class,
                    entity.getBoundingBox().inflate(dist, dist, dist));
                if (nearbySeabears.isEmpty()) {
                    final EntitySeaBear bear = AMEntityRegistry.SEA_BEAR.get().create(entity.level());
                    final BlockPos at = entity.blockPosition();
                    BlockPos farOff = null;
                    for (int i = 0; i < 15; i++) {
                        final int f1 = (int) Math.signum(random.nextInt() - 0.5F);
                        final int f2 = (int) Math.signum(random.nextInt() - 0.5F);
                        final BlockPos pos1 = at.offset(f1 * (10 + random.nextInt(dist - 10)), random.nextInt(1),
                            f2 * (10 + random.nextInt(dist - 10)));
                        if (entity.level().isWaterAt(pos1)) {
                            farOff = pos1;
                        }
                    }
                    if (farOff != null) {
                        bear.setPos(farOff.getX() + 0.5F, farOff.getY() + 0.5F, farOff.getZ() + 0.5F);
                        bear.setYRot(random.nextFloat() * 360F);
                        bear.setTarget(entity);
                        entity.level().addFreshEntity(bear);
                    }
                } else {
                    for (EntitySeaBear bear : nearbySeabears) {
                        bear.setTarget(entity);
                    }
                }
            }
        }
        if (VineLassoUtil.hasLassoData(entity)) {
            VineLassoUtil.tickLasso(entity);
        }
        if (RockyChestplateUtil.isWearing(entity)) {
            RockyChestplateUtil.tickRockyRolling(entity);
        }
        if (FlyingFishBootsUtil.isWearing(entity)) {
            FlyingFishBootsUtil.tickFlyingFishBoots(entity);
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
    public void onFOVUpdate(ComputeFovModifierEvent event) {
        if (event.getPlayer().hasEffect(AMEffectRegistry.FEAR.get()) || event.getPlayer().hasEffect(AMEffectRegistry.POWER_DOWN.get())) {
            event.setNewFovModifier(1.0F);
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!event.getEntity().getUseItem().isEmpty() && event.getSource() != null && event.getSource().getEntity() != null) {
            if (event.getEntity().getUseItem().getItem() == AMItemRegistry.SHIELD_OF_THE_DEEP.get()) {
                if (event.getSource().getEntity() instanceof LivingEntity living) {
                    boolean flag = false;
                    if (living.distanceTo(event.getEntity()) <= 4
                        && !living.hasEffect(AMEffectRegistry.EXSANGUINATION.get())) {
                        living.addEffect(new MobEffectInstance(AMEffectRegistry.EXSANGUINATION.get(), 60, 2));
                        flag = true;
                    }
                    if (event.getEntity().isInWaterOrBubble()) {
                        event.getEntity().setAirSupply(Math.min(event.getEntity().getMaxAirSupply(), event.getEntity().getAirSupply() + 150));
                        flag = true;
                    }
                    if (flag) {
                        event.getEntity().getUseItem().hurtAndBreak(1, event.getEntity(),
                            player -> player.broadcastBreakEvent(event.getEntity().getUsedItemHand()));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        CompoundTag tag = event.getItemStack().getTag();
        if (tag != null && tag.contains("BisonFur") && tag.getBoolean("BisonFur")) {
            event.getToolTip().add(Component.translatable("item.alexsmobs.insulated_with_fur").withStyle(ChatFormatting.AQUA));
        }
    }

    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event){
        AlexsMobs.LOGGER.info("Adding datapack listener capsid_recipes");
        event.addListener(AlexsMobs.PROXY.getCapsidRecipeManager());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHarvestCheck(PlayerEvent.HarvestCheck event){
        if(event.getEntity() != null && event.getEntity().isHolding(AMItemRegistry.GHOSTLY_PICKAXE.get()) && ItemGhostlyPickaxe.shouldStoreInGhost(event.getEntity(), event.getEntity().getMainHandItem())){
            //stops drops from being spawned
            event.setCanHarvest(false);
        }
    }

}
