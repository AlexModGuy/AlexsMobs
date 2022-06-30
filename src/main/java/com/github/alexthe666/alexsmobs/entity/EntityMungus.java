package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.MungusAIAlertBunfungus;
import com.github.alexthe666.alexsmobs.entity.ai.MungusAITemptMushroom;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMungusBiomeChange;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;

public class EntityMungus extends Animal implements ITargetsDroppedItems, Shearable, net.minecraftforge.common.IForgeShearable {

    protected static final EntityDataAccessor<Optional<BlockPos>> TARGETED_BLOCK_POS = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> ALT_ORDER_MUSHROOMS = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> REVERTING = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MUSHROOM_COUNT = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SACK_SWELL = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> EXPLOSION_DISABLED = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockState>> MUSHROOM_STATE = SynchedEntityData.defineId(EntityMungus.class, EntityDataSerializers.BLOCK_STATE);

    //biome container constants
    private static final int WIDTH_BITS = Mth.ceillog2(16) - 2;
    public static final int MAX_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + DimensionType.BITS_FOR_Y - 2;
    private static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
    private static final HashMap<String, String> MUSHROOM_TO_BIOME = new HashMap<>();
    private static final HashMap<String, String> MUSHROOM_TO_BLOCK = new HashMap<>();
    private static boolean initBiomeData = false;
    public float prevSwellProgress = 0;
    public float swellProgress = 0;
    private int beamCounter = 0;
    private int mosquitoAttackCooldown = 0;
    private boolean hasExploded;
    public int timeUntilNextEgg = this.random.nextInt(24000) + 24000;

    protected EntityMungus(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
        initBiomeData();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 15D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public static boolean canMungusSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return worldIn.getBlockState(pos.below()).canOcclude();
    }

    public static BlockState getMushroomBlockstate(Item item) {
        if (item instanceof BlockItem) {
            ResourceLocation name = ForgeRegistries.ITEMS.getKey(item);
            if (name != null && MUSHROOM_TO_BIOME.containsKey(name.toString())) {
                return ((BlockItem) item).getBlock().defaultBlockState();
            }
        }
        return null;
    }

    private static void initBiomeData() {
        if (!initBiomeData || MUSHROOM_TO_BIOME.isEmpty()) {
            initBiomeData = true;
            for (String str : AMConfig.mungusBiomeMatches) {
                String[] split = str.split("\\|");
                if (split.length >= 2) {
                    MUSHROOM_TO_BIOME.put(split[0], split[1]);
                    MUSHROOM_TO_BLOCK.put(split[0], split[2]);
                }
            }
        }

    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.MUNGUS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MUNGUS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MUNGUS_HURT.get();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mungusSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new MungusAITemptMushroom(this, 1.0F));
        this.goalSelector.addGoal(5, new AITargetMushrooms());
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 60, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 10));
        this.targetSelector.addGoal(2, new MungusAIAlertBunfungus(this, EntityBunfungus.class));
    }

    public void tick(){
        super.tick();
        if (!this.level.isClientSide && this.isAlive() && !this.isBaby() && --this.timeUntilNextEgg <= 0) {
            ItemEntity dropped = this.spawnAtLocation(AMItemRegistry.MUNGAL_SPORES.get());
            dropped.setDefaultPickUpDelay();
            this.timeUntilNextEgg = this.random.nextInt(24000) + 24000;

        }
    }

    public void baseTick() {
        super.baseTick();
        this.prevSwellProgress = swellProgress;
        if (this.isReverting() && AMConfig.mungusBiomeTransformationType == 2) {
            swellProgress += 0.5F;
            if (swellProgress >= 10) {
                try {
                    explode();
                }catch (Exception e){
                    e.printStackTrace();
                }
                swellProgress = 0;
                this.entityData.set(REVERTING, false);
            }
        } else if (isAlive() && swellProgress > 0F) {
            swellProgress -= 1F;
        }
        if (entityData.get(EXPLOSION_DISABLED)) {
            if (mosquitoAttackCooldown < 0) {
                mosquitoAttackCooldown++;
            }
            if (mosquitoAttackCooldown > 200) {
                mosquitoAttackCooldown = 0;
                entityData.set(EXPLOSION_DISABLED, false);
            }
        }
    }

    protected void tickDeath() {
        super.tickDeath();
        if (this.getMushroomCount() >= 5 && AMConfig.mungusBiomeTransformationType > 0 && !this.isBaby() && !this.entityData.get(EXPLOSION_DISABLED)) {
            this.swellProgress++;
            if (this.deathTime == 19 && !hasExploded) {
                hasExploded = true;
                try {
                    explode();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void explode() {
        for (int i = 0; i < 5; i++) {
            float r1 = 6F * (random.nextFloat() - 0.5F);
            float r2 = 2F * (random.nextFloat() - 0.5F);
            float r3 = 6F * (random.nextFloat() - 0.5F);
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + r1, this.getY() + 0.5F + r2, this.getZ() + r3, r1 * 4, r2 * 4, r3 * 4);
        }
        if(!level.isClientSide){
            ServerLevel serverLevel = (ServerLevel) level;
            final int radius = 3;
            final int j = radius + level.random.nextInt(1);
            final int k = (radius + level.random.nextInt(1));
            final int l = radius + level.random.nextInt(1);
            final float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;
            final double ffDouble = ff;
            BlockPos center = this.blockPosition();
            BlockState transformState = Blocks.MYCELIUM.defaultBlockState();
            Registry<Biome> registry = serverLevel.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            Holder<Biome> biome = registry.getHolder(Biomes.MUSHROOM_FIELDS).get();
            TagKey<Block> transformMatches = AMTagRegistry.MUNGUS_REPLACE_MUSHROOM;
            if (this.getMushroomState() != null) {
                String mushroomKey = ForgeRegistries.BLOCKS.getKey(this.getMushroomState().getBlock()).toString();
                if (MUSHROOM_TO_BLOCK.containsKey(mushroomKey)) {
                    Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MUSHROOM_TO_BLOCK.get(mushroomKey)));
                    if (block != null) {
                        transformState = block.defaultBlockState();
                        if (block == Blocks.WARPED_NYLIUM) {
                            transformMatches = AMTagRegistry.MUNGUS_REPLACE_NETHER;
                        }
                        if (block == Blocks.CRIMSON_NYLIUM) {
                            transformMatches = AMTagRegistry.MUNGUS_REPLACE_NETHER;
                        }
                    }
                }
                Holder<Biome> gottenFrom = getBiomeKeyFromShroom();
                if (gottenFrom != null) {
                    biome = gottenFrom;
                }
            }
            BlockState finalTransformState = transformState;
            TagKey<Block> finalTransformReplace = transformMatches;

            if (AMConfig.mungusBiomeTransformationType == 2 && !level.isClientSide) {
                transformBiome(center, biome);
            }
            this.gameEvent(GameEvent.EXPLODE);
            this.playSound(SoundEvents.GENERIC_EXPLODE, this.getSoundVolume(), this.getVoicePitch());
            if (!isReverting()) {
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(blockpos -> {
                    if (blockpos.distSqr(center) <= ffDouble) {
                        if (level.random.nextFloat() > (float) blockpos.distSqr(center) / ff) {
                            if (level.getBlockState(blockpos).is(finalTransformReplace) && !level.getBlockState(blockpos.above()).canOcclude()) {
                                level.setBlockAndUpdate(blockpos, finalTransformState);
                            }
                            if (level.random.nextInt(4) == 0 && level.getBlockState(blockpos).getMaterial().isSolid() && level.getFluidState(blockpos.above()).isEmpty() && !level.getBlockState(blockpos.above()).canOcclude()) {
                                level.setBlockAndUpdate(blockpos.above(), this.getMushroomState());
                            }
                        }
                    }
                });
            }
        }
    }

    public void disableExplosion() {
        this.entityData.set(EXPLOSION_DISABLED, true);
    }

    private Holder<Biome> getBiomeKeyFromShroom() {
        Registry<Biome> registry = this.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        BlockState state = this.getMushroomState();
        if (state == null) {
            return null;
        }
        ResourceLocation blockRegName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockRegName != null && MUSHROOM_TO_BIOME.containsKey(blockRegName.toString())) {
            String str = MUSHROOM_TO_BIOME.get(blockRegName.toString());
            Biome biome = registry.getOptional(new ResourceLocation(str)).orElse(null);
            ResourceKey<Biome> resourceKey = registry.getResourceKey(biome).orElse(null);
            return registry.getHolder(resourceKey).orElse(null);
        }
        return null;
    }

    private PalettedContainerRO<Holder<Biome>> getChunkBiomes(LevelChunk chunk) {
        int i = QuartPos.fromBlock(chunk.getMinBuildHeight());
        int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
        int l = Mth.clamp(QuartPos.fromBlock((int) this.getY()), i, k);
        int j = chunk.getSectionIndex(QuartPos.toBlock(l));
        LevelChunkSection section = chunk.getSection(j);
        return section == null ? null : section.getBiomes();
    }

    private void setChunkBiomes(LevelChunk chunk, PalettedContainer<Holder<Biome>> container) {
        int i = QuartPos.fromBlock(chunk.getMinBuildHeight());
        int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
        int l = Mth.clamp(QuartPos.fromBlock((int) this.getY()), i, k);
        int j = chunk.getSectionIndex(QuartPos.toBlock(l));
        LevelChunkSection section = chunk.getSection(j);
        if(section != null){
            section.biomes = container;
        }
    }


    private void transformBiome(BlockPos pos, Holder<Biome> biome) {
        LevelChunk chunk = level.getChunkAt(pos);
        PalettedContainer<Holder<Biome>> container = getChunkBiomes(chunk).recreate();
        if (this.entityData.get(REVERTING)) {
            int lvt_4_1_ = chunk.getPos().getMinBlockX() >> 2;
            int yChunk = (int)this.getY() >> 2;
            int lvt_5_1_ = chunk.getPos().getMinBlockZ() >> 2;
            ChunkGenerator chunkgenerator = ((ServerLevel) level).getChunkSource().getGenerator();
            for(int k = 0; k < 4; ++k) {
                for(int l = 0; l < 4; ++l) {
                    for(int i1 = 0; i1 < 4; ++i1) {
                        container.getAndSetUnchecked(k, l, i1, ((ServerLevel) level).getUncachedNoiseBiome(lvt_4_1_ + k, yChunk + l, lvt_5_1_ + i1));
                    }
                }
            }
            setChunkBiomes(chunk, container);
            if (!level.isClientSide) {
                //AlexsMobs.sendMSGToAll(new MessageMungusBiomeChange(this.getId(), pos.getX(), pos.getZ(), ForgeRegistries.BIOMES.getKey(biome.value()).toString()));
            }
        } else {
            if (biome == null) {
                return;
            }
            if (container != null && !level.isClientSide) {
                for (int biomeX = 0; biomeX < 4; ++biomeX) {
                    for (int biomeY = 0; biomeY < 4; ++biomeY) {
                        for (int biomeZ = 0; biomeZ < 4; ++biomeZ) {
                            container.getAndSetUnchecked(biomeX, biomeY, biomeZ, biome);
                        }
                    }
                }
                int id = this.getId();
                setChunkBiomes(chunk, container);
                AlexsMobs.sendMSGToAll(new MessageMungusBiomeChange(this.getId(), pos.getX(), pos.getZ(), ForgeRegistries.BIOMES.getKey(biome.value()).toString()));
            }
        }

    }

    public boolean shouldFollowMushroom(ItemStack stack) {
        BlockState state = getMushroomBlockstate(stack.getItem());
        if (state != null && !state.isAir()) {
            if (this.getMushroomCount() == 0) {
                return true;
            } else {
                return this.getMushroomState().getBlock() == state.getBlock();
            }
        }
        return false;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (itemstack.getItem() == Items.POISONOUS_POTATO && !this.isBaby()) {
            this.entityData.set(REVERTING, true);
            usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }
        if (shouldFollowMushroom(itemstack) && this.getMushroomCount() < 5) {
            this.entityData.set(REVERTING, false);
            BlockState state = getMushroomBlockstate(itemstack.getItem());
            this.gameEvent(GameEvent.BLOCK_PLACE);
            this.playSound(SoundEvents.FUNGUS_PLACE, this.getSoundVolume(), this.getVoicePitch());
            if (this.getMushroomState() != null && state != null && state.getBlock() != this.getMushroomState().getBlock()) {
                this.setMushroomCount(0);
            }
            this.setMushroomState(state);
            this.usePlayerItem(player, hand, itemstack);
            this.setMushroomCount(this.getMushroomCount() + 1);
            return InteractionResult.SUCCESS;
        } else {
            return type;
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev) {
            this.setBeamTarget(null);
            beamCounter = Math.min(beamCounter, -1200);
        }
        return prev;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MUSHROOM_STATE, Optional.empty());
        this.getEntityData().define(TARGETED_BLOCK_POS, Optional.empty());
        this.entityData.define(ALT_ORDER_MUSHROOMS, Boolean.valueOf(false));
        this.entityData.define(REVERTING, Boolean.valueOf(false));
        this.entityData.define(EXPLOSION_DISABLED, Boolean.valueOf(false));
        this.entityData.define(MUSHROOM_COUNT, 0);
        this.entityData.define(SACK_SWELL, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        BlockState blockstate = this.getMushroomState();
        if (blockstate != null) {
            compound.put("MushroomState", NbtUtils.writeBlockState(blockstate));
        }
        compound.putInt("MushroomCount", this.getMushroomCount());
        compound.putInt("Sack", this.getSackSwell());
        compound.putInt("BeamCounter", this.beamCounter);
        compound.putBoolean("AltMush", this.entityData.get(ALT_ORDER_MUSHROOMS));
        if (this.getBeamTarget() != null) {
            compound.put("BeamTarget", NbtUtils.writeBlockPos(this.getBeamTarget()));
        }
        compound.putInt("EggTime", this.timeUntilNextEgg);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        BlockState blockstate = null;
        if (compound.contains("MushroomState", 10)) {
            blockstate = NbtUtils.readBlockState(compound.getCompound("MushroomState"));
            if (blockstate.isAir()) {
                blockstate = null;
            }
        }
        if (compound.contains("BeamTarget", 10)) {
            this.setBeamTarget(NbtUtils.readBlockPos(compound.getCompound("BeamTarget")));
        }
        this.setMushroomState(blockstate);
        this.setMushroomCount(compound.getInt("MushroomCount"));
        this.setSackSwell(compound.getInt("Sack"));
        this.beamCounter = compound.getInt("BeamCounter");
        this.entityData.set(ALT_ORDER_MUSHROOMS, compound.getBoolean("AltMush"));
        if (compound.contains("EggTime")) {
            this.timeUntilNextEgg = compound.getInt("EggTime");
        }
    }

    public void aiStep() {
        super.aiStep();
        if (this.getBeamTarget() != null) {
            BlockPos t = this.getBeamTarget();
            if (isMushroomTarget(t) && this.hasLineOfSightMushroom(t)) {
                this.getLookControl().setLookAt(t.getX() + 0.5F, t.getY() + 0.15F, t.getZ() + 0.5F, 90.0F, 90.0F);
                this.getLookControl().tick();
                double d5 = 1.0F;
                double eyeHeight = this.getY() + 1.0F;
                if (beamCounter % 20 == 0) {
                    this.playSound(AMSoundRegistry.MUNGUS_LASER_LOOP.get(), this.getVoicePitch(), this.getSoundVolume());
                }
                beamCounter++;

                double d0 = t.getX() + 0.5F - this.getX();
                double d1 = t.getY() + 0.5F - eyeHeight;
                double d2 = t.getZ() + 0.5F - this.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d0 = d0 / d3;
                d1 = d1 / d3;
                d2 = d2 / d3;
                double d4 = this.random.nextDouble();
                while (d4 < d3 - 0.5F) {
                    d4 += 1.0D - d5 + this.random.nextDouble();
                    if (random.nextFloat() < 0.1F) {
                        float r1 = 0.3F * (random.nextFloat() - 0.5F);
                        float r2 = 0.3F * (random.nextFloat() - 0.5F);
                        float r3 = 0.3F * (random.nextFloat() - 0.5F);

                        this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() + d0 * d4 + r1, eyeHeight + d1 * d4 + r2, this.getZ() + d2 * d4 + r3, r1 * 4, r2 * 4, r3 * 4);
                    }
                }
                if (beamCounter > 200) {
                    BlockState state = level.getBlockState(t);
                    if (state.getBlock() instanceof BonemealableBlock) {
                        BonemealableBlock igrowable = (BonemealableBlock) state.getBlock();
                        boolean flag = false;
                        if (igrowable.isValidBonemealTarget(this.level, t, state, this.level.isClientSide)) {
                            for (int i = 0; i < 5; i++) {
                                float r1 = 3F * (random.nextFloat() - 0.5F);
                                float r2 = 2F * (random.nextFloat() - 0.5F);
                                float r3 = 3F * (random.nextFloat() - 0.5F);
                                this.level.addParticle(ParticleTypes.EXPLOSION, t.getX() + 0.5F + r1, t.getY() + 0.5F + r2, t.getZ() + 0.5F + r3, r1 * 4, r2 * 4, r3 * 4);
                            }
                            if (!this.level.isClientSide) {
                                this.level.levelEvent(2005, t, 0);
                                igrowable.performBonemeal((ServerLevel) this.level, this.level.random, t, state);
                                flag = level.getBlockState(t).getBlock() != state.getBlock();
                            }
                        }
                        if (!flag) {
                            int grown = 0;
                            int maxGrow = 2 + random.nextInt(3);
                            for (int i = 0; i < 15; i++) {
                                BlockPos pos = t.offset(random.nextInt(10) - 5, random.nextInt(4) - 2, random.nextInt(10) - 5);
                                if (grown < maxGrow) {
                                    if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).canOcclude()) {
                                        level.setBlockAndUpdate(pos, state);
                                        grown++;
                                    }
                                }
                            }
                        }
                        this.playSound(AMSoundRegistry.MUNGUS_LASER_END.get(), this.getVoicePitch(), this.getSoundVolume());
                        if (flag) {
                            this.playSound(AMSoundRegistry.MUNGUS_LASER_GROW.get(), this.getVoicePitch(), this.getSoundVolume());
                        }
                        this.setBeamTarget(null);
                        beamCounter = -1200;
                        if (this.getMushroomCount() > 0) {
                            this.setMushroomCount(this.getMushroomCount() - 1);
                        }
                    }
                }
            } else {
                this.setBeamTarget(null);
            }
        }
        if (beamCounter < 0) {
            beamCounter++;
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.MUNGAL_SPORES.get();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.entityData.set(ALT_ORDER_MUSHROOMS, random.nextBoolean());
        this.setMushroomCount(random.nextInt(2));
        setMushroomState(random.nextBoolean() ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState());
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public int getMushroomCount() {
        return this.entityData.get(MUSHROOM_COUNT).intValue();
    }

    public void setMushroomCount(int command) {
        this.entityData.set(MUSHROOM_COUNT, Integer.valueOf(command));
    }

    public int getSackSwell() {
        return this.entityData.get(SACK_SWELL).intValue();
    }

    public void setSackSwell(int command) {
        this.entityData.set(SACK_SWELL, Integer.valueOf(command));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return this.getEntityData().get(TARGETED_BLOCK_POS).orElse(null);
    }

    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        this.getEntityData().set(TARGETED_BLOCK_POS, Optional.ofNullable(beamTarget));
    }

    public boolean isAltOrderMushroom() {
        return this.entityData.get(ALT_ORDER_MUSHROOMS).booleanValue();
    }

    @Nullable
    public BlockState getMushroomState() {
        return this.entityData.get(MUSHROOM_STATE).orElse(null);
    }

    public void setMushroomState(@Nullable BlockState state) {
        this.entityData.set(MUSHROOM_STATE, Optional.ofNullable(state));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.MUNGUS.get().create(p_241840_1_);
    }

    public boolean isMushroomTarget(BlockPos pos) {
        if (this.getMushroomState() != null) {
            return level.getBlockState(pos).getBlock() == this.getMushroomState().getBlock();
        }
        return false;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return shouldFollowMushroom(stack) && this.getMushroomCount() < 5;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        if (shouldFollowMushroom(e.getItem())) {
            BlockState state = getMushroomBlockstate(e.getItem().getItem());
            if (this.getMushroomState() != null && state != null && state.getBlock() != this.getMushroomState().getBlock()) {
                this.setMushroomCount(0);
            }
            this.gameEvent(GameEvent.BLOCK_PLACE);
            this.playSound(SoundEvents.FUNGUS_PLACE, this.getSoundVolume(), this.getVoicePitch());
            this.setMushroomState(state);
            this.setMushroomCount(this.getMushroomCount() + 1);
        }
    }

    private boolean hasLineOfSightMushroom(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level.clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }


    public boolean readyForShearing() {
        return this.isAlive() && this.getMushroomState() != null && this.getMushroomCount() > 0;
    }

    @Override
    public boolean isShearable(@javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos) {
        return readyForShearing();
    }

    @Override
    public void shear(SoundSource category) {
        this.gameEvent(GameEvent.ENTITY_INTERACT);
        level.playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1.0F, 1.0F);
        if (!level.isClientSide() && this.getMushroomState() != null && this.getMushroomCount() > 0) {
            this.setMushroomCount(this.getMushroomCount() - 1);
            if (this.getMushroomCount() <= 0) {
                this.setMushroomState(null);
                this.setBeamTarget(null);
                beamCounter = Math.min(-1200, beamCounter);
            }

        }
    }

    @javax.annotation.Nonnull
    @Override
    public java.util.List<ItemStack> onSheared(@javax.annotation.Nullable Player player, @javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
        world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
        if (!world.isClientSide() && this.getMushroomState() != null && this.getMushroomCount() > 0) {
            this.setMushroomCount(this.getMushroomCount() - 1);
            if (this.getMushroomCount() <= 0) {
                this.setMushroomState(null);
                this.setBeamTarget(null);
                beamCounter = Math.min(-1200, beamCounter);
            }

        }
        return java.util.Collections.emptyList();
    }

    public boolean isReverting() {
        return entityData.get(REVERTING);
    }

    public boolean isWarpedMoscoReady() {
        return this.getMushroomState() == Blocks.WARPED_FUNGUS.defaultBlockState() && this.getMushroomCount() >= 5;
    }


    class AITargetMushrooms extends Goal {
        private final int searchLength;
        protected BlockPos destinationBlock;
        protected int runDelay = 70;

        private AITargetMushrooms() {
            searchLength = 20;
        }

        public boolean canContinueToUse() {
            return destinationBlock != null && EntityMungus.this.isMushroomTarget(destinationBlock.mutable()) && isCloseToShroom(32);
        }

        public boolean isCloseToShroom(double dist) {
            return destinationBlock == null || EntityMungus.this.distanceToSqr(Vec3.atCenterOf(destinationBlock)) < dist * dist;
        }

        @Override
        public boolean canUse() {
            if (EntityMungus.this.getBeamTarget() != null || EntityMungus.this.beamCounter < 0 || EntityMungus.this.getMushroomCount() <= 0) {
                return false;
            }
            if (this.runDelay > 0) {
                --this.runDelay;
                return false;
            } else {
                this.runDelay = 70 + EntityMungus.this.random.nextInt(150);
                return this.searchForDestination();
            }
        }

        public void start() {
        }

        public void tick() {
            if (this.destinationBlock == null || !EntityMungus.this.isMushroomTarget(this.destinationBlock) || EntityMungus.this.beamCounter < 0) {
                stop();
            } else {
                if (!EntityMungus.this.hasLineOfSightMushroom(this.destinationBlock)) {
                    EntityMungus.this.getNavigation().moveTo(this.destinationBlock.getX(), this.destinationBlock.getY(), this.destinationBlock.getZ(), 1D);
                } else {
                    EntityMungus.this.setBeamTarget(this.destinationBlock);
                    if (!EntityMungus.this.isInLove()) {
                        EntityMungus.this.getNavigation().stop();
                    }
                }
            }
        }

        public void stop() {
            EntityMungus.this.setBeamTarget(null);
        }

        protected boolean searchForDestination() {
            int lvt_1_1_ = this.searchLength;
            BlockPos lvt_3_1_ = EntityMungus.this.blockPosition();
            BlockPos.MutableBlockPos lvt_4_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_5_1_ = -5; lvt_5_1_ <= 5; lvt_5_1_++) {
                for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; ++lvt_6_1_) {
                    for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                        for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0; lvt_8_1_ <= lvt_6_1_; lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_) {
                            lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                            if (this.isMushroom(EntityMungus.this.level, lvt_4_1_)) {
                                this.destinationBlock = lvt_4_1_;
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        private boolean isMushroom(Level world, BlockPos.MutableBlockPos lvt_4_1_) {
            return EntityMungus.this.isMushroomTarget(lvt_4_1_);
        }

    }
}