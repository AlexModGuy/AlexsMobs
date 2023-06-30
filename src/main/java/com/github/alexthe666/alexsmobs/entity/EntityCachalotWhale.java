package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntityCachalotWhale extends Animal {

    private static final TargetingConditions REWARD_PLAYER_PREDICATE = TargetingConditions.forNonCombat().range(50.0D).ignoreLineOfSight();
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BEACHED = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ALBINO = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESPAWN_BEACH = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GRABBING = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOLDING_SQUID_LEFT = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CAUGHT_ID = SynchedEntityData.defineId(EntityCachalotWhale.class, EntityDataSerializers.INT);
    public final double[][] ringBuffer = new double[64][3];
    public final EntityCachalotPart headPart;
    public final EntityCachalotPart bodyFrontPart;
    public final EntityCachalotPart bodyPart;
    public final EntityCachalotPart tail1Part;
    public final EntityCachalotPart tail2Part;
    public final EntityCachalotPart tail3Part;
    public final EntityCachalotPart[] whaleParts;
    private final boolean hasAlbinoAttribute = false;
    public int ringBufferIndex = -1;
    public float prevChargingProgress;
    public float chargeProgress;
    public float prevSleepProgress;
    public float sleepProgress;
    public float prevBeachedProgress;
    public float beachedProgress;
    public float prevGrabProgress;
    public float grabProgress;
    public int grabTime = 0;
    private boolean receivedEcho = false;
    private boolean waitForEchoFlag = true;
    private int echoTimer = 0;
    private boolean prevEyesInWater = false;
    private int spoutTimer = 0;
    private int chargeCooldown = 0;
    private float whaleSpeedMod = 1F;
    private int rewardTime = 0;
    private Player rewardPlayer;
    private int blockBreakCounter;
    private int despawnDelay = 47999;
    private int echoSoundCooldown = 0;
    private boolean hasRewardedPlayer = false;

    public EntityCachalotWhale(EntityType type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.moveControl = new AnimalSwimMoveControllerSink(this, 1, 1, 6);
        this.lookControl = new SmoothSwimmingLookControl(this, 4);
        this.headPart = new EntityCachalotPart(this, 3.0F, 3.5F);
        this.bodyFrontPart = new EntityCachalotPart(this, 4.0F, 4.0F);
        this.bodyPart = new EntityCachalotPart(this, 5.0F, 4.0F);
        this.tail1Part = new EntityCachalotPart(this, 4.0F, 3.0F);
        this.tail2Part = new EntityCachalotPart(this, 3.0F, 2.0F);
        this.tail3Part = new EntityCachalotPart(this, 3.0F, 0.7F);
        this.whaleParts = new EntityCachalotPart[]{this.headPart, this.bodyFrontPart, this.bodyPart, this.tail1Part, this.tail2Part, this.tail3Part};
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 160.0D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 1.2F).add(Attributes.ATTACK_DAMAGE, 30F);
    }

    public static <T extends Mob> boolean canCachalotWhaleSpawn(EntityType<T> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        BlockPos up = pos;
        while(up.getY() < iServerWorld.getMaxBuildHeight() && iServerWorld.getFluidState(up).is(FluidTags.WATER)){
            up = up.above();
        }
        return iServerWorld.getFluidState(up.below()).is(FluidTags.WATER) && up.getY() < iServerWorld.getSeaLevel() + 15 && iServerWorld.canSeeSky(up);
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isSleeping() && !this.isCharging() && !this.isDespawnBeach() && !isAlbino();
    }

    private boolean canDespawn() {
        return isDespawnBeach();
    }

    private void tryDespawn() {
        if (this.canDespawn()) {
            this.despawnDelay = this.despawnDelay - 1;
            if (this.despawnDelay <= 0) {
                this.dropLeash(true, false);
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.CACHALOT_WHALE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CACHALOT_WHALE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CACHALOT_WHALE_HURT.get();
    }

    public void scaleParts() {
        for (EntityCachalotPart parts : whaleParts) {
            float prev = parts.scale;
            parts.scale = this.isBaby() ? 0.5F : 1F;
            if (prev != parts.scale) {
                parts.refreshDimensions();
            }
        }
    }

    public boolean isPickable() {
        return true;
    }

    public void pushEntities() {
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Albino", this.isAlbino());
        compound.putBoolean("Beached", this.isBeached());
        compound.putBoolean("BeachedDespawnFlag", this.isDespawnBeach());
        compound.putBoolean("GivenReward", this.hasRewardedPlayer);
        compound.putInt("DespawnDelay", this.despawnDelay);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAlbino(compound.getBoolean("Albino"));
        this.setBeached(compound.getBoolean("Beached"));
        this.setDespawnBeach(compound.getBoolean("BeachedDespawnFlag"));
        if (compound.contains("DespawnDelay", 99)) {
            this.despawnDelay = compound.getInt("DespawnDelay");
        }
        this.hasRewardedPlayer = compound.getBoolean("GivenReward");

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGING, false);
        this.entityData.define(SLEEPING, false);
        this.entityData.define(BEACHED, false);
        this.entityData.define(ALBINO, false);
        this.entityData.define(GRABBING, false);
        this.entityData.define(HOLDING_SQUID_LEFT, false);
        this.entityData.define(DESPAWN_BEACH, false);
        this.entityData.define(CAUGHT_ID, -1);
    }

    public boolean hasCaughtSquid() {
        return this.entityData.get(CAUGHT_ID) != -1;
    }

    private void setCaughtSquidId(int i) {
        this.entityData.set(CAUGHT_ID, i);
    }

    @Nullable
    public Entity getCaughtSquid() {
        if (!this.hasCaughtSquid()) {
            return null;
        } else {
            return this.level().getEntity(this.entityData.get(CAUGHT_ID));
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AIBreathe());
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AnimalAIFollowParentRanged(this, 1.1F, 32, 10));
        this.goalSelector.addGoal(4, new AnimalAIRandomSwimming(this, 0.6D, 10, 24, true) {
            public boolean canUse() {
                return !EntityCachalotWhale.this.isSleeping() && !EntityCachalotWhale.this.isBeached() && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(7, new FollowBoatGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this).setAlertOthers()));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 30, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CACHALOT_WHALE_TARGETS)) {
            public boolean canUse() {
                return !EntityCachalotWhale.this.isSleeping() && !EntityCachalotWhale.this.isBeached() && super.canUse();
            }
        });
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }

    public void customServerAiStep() {
        super.customServerAiStep();
        breakBlock();
    }

    public void breakBlock() {
        if (this.blockBreakCounter > 0) {
            --this.blockBreakCounter;
            return;
        }
        boolean flag = false;
        if (!this.level().isClientSide && this.blockBreakCounter == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level(), this)) {
            final TagKey<Block> breakables = this.isCharging() && this.getTarget() != null && AMConfig.cachalotDestruction ? AMTagRegistry.CACHALOT_WHALE_BREAKABLES : AMTagRegistry.ORCA_BREAKABLES;
            for (int a = (int) Math.round(this.getBoundingBox().minX); a <= (int) Math.round(this.getBoundingBox().maxX); a++) {
                for (int b = (int) Math.round(this.getBoundingBox().minY) - 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 1) && (b <= 127); b++) {
                    for (int c = (int) Math.round(this.getBoundingBox().minZ); c <= (int) Math.round(this.getBoundingBox().maxZ); c++) {
                        final BlockPos pos = new BlockPos(a, b, c);
                        final BlockState state = level().getBlockState(pos);
                        final FluidState fluidState = level().getFluidState(pos);
                        if (!state.isAir() && !state.getShape(level(), pos).isEmpty() && state.is(breakables) && fluidState.isEmpty()) {
                            final Block block = state.getBlock();
                            if (block != Blocks.AIR) {
                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1, 0.6F));
                                flag = true;
                                level().destroyBlock(pos, true);
                                if (state.is(BlockTags.ICE)) {
                                    level().setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (flag) {
            blockBreakCounter = this.isCharging() && this.getTarget() != null ? 2 : 20;
        }
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));

        } else {
            super.travel(travelVector);
        }
    }

    private void spawnSpoutParticles() {
        if (this.isAlive()) {
            final float radius = this.headPart.getBbWidth() * 0.5F;
            for (int j = 0; j < 5 + random.nextInt(4); ++j) {
                final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
                final double extraX = (radius * (1F + random.nextFloat() * 0.13F)) * Mth.sin(Mth.PI + angle) + (random.nextFloat() - 0.5F) + this.getDeltaMovement().x * 2F;
                final double extraZ = (radius * (1F + random.nextFloat() * 0.13F)) * Mth.cos(angle) + (random.nextFloat() - 0.5F) + this.getDeltaMovement().z * 2F;
                final double motX = this.random.nextGaussian();
                final double motZ = this.random.nextGaussian();
                this.level().addParticle(AMParticleRegistry.WHALE_SPLASH.get(), this.headPart.getX() + extraX, this.headPart.getY() + this.headPart.getBbHeight(), this.headPart.getZ() + extraZ, motX * 0.1F + this.getDeltaMovement().x, 2F, motZ * 0.1F + this.getDeltaMovement().z);
            }
        }
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, Boolean.valueOf(charging));
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public void setSleeping(boolean charging) {
        this.entityData.set(SLEEPING, Boolean.valueOf(charging));
    }

    public boolean isBeached() {
        return this.entityData.get(BEACHED);
    }

    public void setBeached(boolean charging) {
        this.entityData.set(BEACHED, Boolean.valueOf(charging));
    }

    public boolean isGrabbing() {
        return this.entityData.get(GRABBING);
    }

    public void setGrabbing(boolean charging) {
        this.entityData.set(GRABBING, Boolean.valueOf(charging));
    }

    public boolean isHoldingSquidLeft() {
        return this.entityData.get(HOLDING_SQUID_LEFT);
    }

    public void setHoldingSquidLeft(boolean charging) {
        this.entityData.set(HOLDING_SQUID_LEFT, Boolean.valueOf(charging));
    }

    public boolean isAlbino() {
        return this.entityData.get(ALBINO);
    }

    public void setAlbino(boolean albino) {
        boolean prev = isAlbino();
        if (!prev && albino) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(230.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(45.0D);
            this.setHealth(230.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(160.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30.0D);
        }
        this.entityData.set(ALBINO, Boolean.valueOf(albino));
    }

    public boolean isDespawnBeach() {
        return this.entityData.get(DESPAWN_BEACH);
    }

    public void setDespawnBeach(boolean despawn) {
        this.entityData.set(DESPAWN_BEACH, Boolean.valueOf(despawn));
    }

    protected float getSoundVolume() {
        return this.isSilent() ? 0 : (float) AMConfig.cachalotVolume;
    }

    public void aiStep() {
        super.aiStep();
        scaleParts();

        if (echoSoundCooldown > 0) {
            echoSoundCooldown--;
        }
        if (this.isSleeping()) {
            this.getNavigation().stop();
            this.setXRot(-90);
            this.whaleSpeedMod = 0;
            if (this.isEyeInFluid(FluidTags.WATER) && this.getAirSupply() < 200) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.06, 0));
            } else {
                BlockPos waterPos = this.blockPosition();
                while (level().getFluidState(waterPos).is(FluidTags.WATER) && waterPos.getY() < 255) {
                    waterPos = waterPos.above();
                }
                if (waterPos.getY() - this.getY() < (isBaby() ? 7 : 12)) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.06, 0));
                }
                if (random.nextInt(100) == 0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, random.nextGaussian() * 0.06, 0));
                }
            }
        } else {
            if (this.whaleSpeedMod == 0) {
                this.whaleSpeedMod = 1;
            }
        }
        float rPitch = (float) -((float) this.getDeltaMovement().y * Mth.RAD_TO_DEG);
        if (this.isGrabbing()) {
            this.setXRot(0);
        } else {
            this.setXRot(Mth.clamp(rPitch, -90, 90));
        }
        if (this.onGround() && !this.isInWaterOrBubble()) {
            this.setBeached(true);
            this.setXRot(0);
            this.setSleeping(false);
        }
        if (this.isBeached()) {
            this.whaleSpeedMod = 0;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1F, 0.5));
            if (this.isEyeInFluid(FluidTags.WATER)) {
                Player entity = this.level().getNearestPlayer(REWARD_PLAYER_PREDICATE, this);
                if (this.getLastHurtByMob() != entity) {
                    rewardPlayer = entity;
                }
                this.despawnDelay = 47999;
                this.setBeached(false);
            }
        }
        if (rewardPlayer != null && !hasRewardedPlayer && this.isInWaterOrBubble()) {
            final double d0 = rewardPlayer.getX() - this.getX();
            final double d1 = rewardPlayer.getEyeY() - this.getEyeY();
            final double d2 = rewardPlayer.getZ() - this.getZ();
            final double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            final float targetYaw = (float) (Mth.atan2(d2, d0) * Mth.RAD_TO_DEG) - 90.0F;
            final float targetPitch = (float) (-(Mth.atan2(d1, d3) * Mth.RAD_TO_DEG));
            this.setYRot((this.getYRot() + Mth.clamp(targetYaw - this.getYRot(), -2, 2)));
            this.setXRot((this.getXRot() + Mth.clamp(targetPitch - this.getXRot(), -2, 2)));
            this.yBodyRot = getYRot();
            this.whaleSpeedMod = 0.1F;
            this.getMoveControl().setWantedPosition(rewardPlayer.getX(), rewardPlayer.getY(), rewardPlayer.getZ(), 0.5D);
            if (this.distanceTo(rewardPlayer) < 10F) {
                if (!this.level().isClientSide) {
                    final Vec3 vec = this.getMouthVec();
                    final ItemEntity itementity = new ItemEntity(this.level(), vec.x, vec.y, vec.z, new ItemStack(AMItemRegistry.AMBERGRIS.get(), 2 + random.nextInt(2)));
                    itementity.setDefaultPickUpDelay();
                    level().addFreshEntity(itementity);
                }
                hasRewardedPlayer = true;
                rewardPlayer = null;
            }
        }

        prevChargingProgress = chargeProgress;
        prevSleepProgress = sleepProgress;
        prevBeachedProgress = beachedProgress;
        prevGrabProgress = grabProgress;
        if (this.tickCount % 200 == 0) {
            this.heal(2);
        }

        if (isCharging()) {
            if (this.chargeProgress < 10F)
                this.chargeProgress++;
        } else {
            if (this.chargeProgress > 0F)
                this.chargeProgress--;
        }

        if (isSleeping()) {
            if (this.sleepProgress < 10F)
                this.sleepProgress++;
        } else {
            if (this.sleepProgress > 0F)
                this.sleepProgress--;
        }

        if (isBeached()) {
            if (this.beachedProgress < 10F)
                this.beachedProgress++;
        } else {
            if (this.beachedProgress > 0F)
                this.beachedProgress--;
        }

        if (isGrabbing()) {
            if (this.grabProgress < 10F)
                this.grabProgress++;

            grabTime++;
        } else {
            if (this.grabProgress > 0F)
                this.grabProgress--;

            grabTime = 0;
        }

        this.yHeadRot = this.getYRot();
        this.yBodyRot = this.getYRot();

        if (!this.isNoAi()) {
            if (this.ringBufferIndex < 0) {
                for (int i = 0; i < this.ringBuffer.length; ++i) {
                    this.ringBuffer[i][0] = this.getYRot();
                    this.ringBuffer[i][1] = this.getY();
                }
            }
            this.ringBufferIndex++;
            if (this.ringBufferIndex == this.ringBuffer.length) {
                this.ringBufferIndex = 0;
            }
            this.ringBuffer[this.ringBufferIndex][0] = this.getYRot();
            this.ringBuffer[ringBufferIndex][1] = this.getY();
            Vec3[] avector3d = new Vec3[this.whaleParts.length];

            for (int j = 0; j < this.whaleParts.length; ++j) {
                this.whaleParts[j].collideWithNearbyEntities();
                avector3d[j] = new Vec3(this.whaleParts[j].getX(), this.whaleParts[j].getY(), this.whaleParts[j].getZ());
            }
            final float f15 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * Mth.DEG_TO_RAD;
            final float f16 = Mth.cos(f15);
            final float f17 = this.getYRot() * Mth.DEG_TO_RAD;
            final float pitch = this.getXRot() * Mth.DEG_TO_RAD;
            final float xRotDiv90 = Math.abs(this.getXRot() / 90F);
            final float f3 = Mth.sin(f17) * (1 - xRotDiv90);
            final float f18 = Mth.cos(f17) * (1 - xRotDiv90);

            this.setPartPosition(this.bodyPart, f3 * 0.5F, -pitch * 0.5F, -f18 * 0.5F);
            this.setPartPosition(this.bodyFrontPart, (f3) * -3.5F, -pitch * 3F, (f18) * 3.5F);
            this.setPartPosition(this.headPart, f3 * -7F, -pitch * 5F, -f18 * -7F);
            double[] adouble = this.getMovementOffsets(5, 1.0F);

            for (int k = 0; k < 3; ++k) {
                final EntityCachalotPart enderdragonpartentity;
                if (k == 0) {
                    enderdragonpartentity = this.tail1Part;
                } else if (k == 1) {
                    enderdragonpartentity = this.tail2Part;
                } else {
                    enderdragonpartentity = this.tail3Part;
                }

                final double[] adouble1 = this.getMovementOffsets(15 + k * 5, 1.0F);
                final float f7 = this.getYRot() * Mth.DEG_TO_RAD + (float) Mth.wrapDegrees(adouble1[0] - adouble[0]) * Mth.DEG_TO_RAD;
                final float f19 = 1 - Math.abs(this.getXRot() / 90F);
                final float f20 = Mth.sin(f7) * f19;
                final float f21 = Mth.cos(f7) * f19;
                final float f22 = -3.6F;
                final float f23 = (float) (k + 1) * f22 - 2F;
                this.setPartPosition(enderdragonpartentity, -(f3 * 0.5F + f20 * f23) * f16, pitch * 1.5F * (k + 1), (f18 * 0.5F + f21 * f23) * f16);
            }

            for (int l = 0; l < this.whaleParts.length; ++l) {
                this.whaleParts[l].xo = avector3d[l].x;
                this.whaleParts[l].yo = avector3d[l].y;
                this.whaleParts[l].zo = avector3d[l].z;
                this.whaleParts[l].xOld = avector3d[l].x;
                this.whaleParts[l].yOld = avector3d[l].y;
                this.whaleParts[l].zOld = avector3d[l].z;
            }
        }
        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (target == null || !target.isAlive()) {
                this.setGrabbing(false);
                whaleSpeedMod = this.isSleeping() ? 0 : 1;
                this.setCharging(false);
                this.setCaughtSquidId(-1);
            } else if (!isBeached() && !isSleeping() && rewardPlayer == null) {
                if (isGrabbing() && this.getTarget().isAlive()) {
                    this.setCaughtSquidId(this.getTarget().getId());
                    whaleSpeedMod = 0.1F;
                    final float scale = this.isBaby() ? 0.5F : 1F;
                    final float offsetAngle = -(float) Math.cos(grabTime * 0.3F) * 0.1F * grabProgress;
                    final float renderYaw = (float) this.getMovementOffsets(0, 1.0F)[0];
                    final Vec3 extraVec = new Vec3(0, 0, -3F).xRot(-this.getXRot() * Mth.DEG_TO_RAD).yRot(-renderYaw * Mth.DEG_TO_RAD);
                    final Vec3 backOfHead = this.headPart.position().add(extraVec);
                    final Vec3 swingVec = new Vec3(isHoldingSquidLeft() ? 1.4F : -1.4F, -0.1, 3F).xRot(-this.getXRot() * Mth.DEG_TO_RAD).yRot(-renderYaw * Mth.DEG_TO_RAD).yRot(offsetAngle);
                    final Vec3 mouth = backOfHead.add(swingVec).scale(scale);
                    this.getTarget().setPos(mouth.x, mouth.y, mouth.z);
                    if (isHoldingSquidLeft()) {
                        this.getTarget().setYRot(this.yBodyRot + 90 - (float) Math.toDegrees(offsetAngle));
                    } else {
                        this.getTarget().setYRot(this.yBodyRot - 90 - (float) Math.toDegrees(offsetAngle));
                    }
                    if (this.getTarget() instanceof EntityGiantSquid) {
                        if (((EntityGiantSquid) this.getTarget()).tickCaptured(this)) {
                            this.setGrabbing(false);
                            this.getTarget().setPos(this.getDismountLocationForPassenger(this.getTarget()));
                        }
                    }
                    if (grabTime % 20 == 0 && grabTime > 30) {
                        this.getTarget().hurt(this.damageSources().mobAttack(this), 4 + random.nextInt(4));
                    }
                    if (grabTime > 300) {
                        this.setGrabbing(false);
                        this.getTarget().setPos(this.getDismountLocationForPassenger(this.getTarget()));
                    }
                } else {
                    this.setCaughtSquidId(-1);
                    this.lookAt(target, 360, 360);
                    waitForEchoFlag = this.getLastHurtByMob() == null || !this.getLastHurtByMob().is(target);
                    if (target instanceof Player || !target.isInWaterOrBubble()) {
                        waitForEchoFlag = false;
                    }
                    if (waitForEchoFlag && !receivedEcho) {
                        this.setCharging(false);
                        whaleSpeedMod = 0.25F;
                        if (echoTimer % 10 == 0) {
                            if (echoTimer % 40 == 0) {
                                this.playSound(AMSoundRegistry.CACHALOT_WHALE_CLICK.get(), this.getSoundVolume(), this.getVoicePitch());
                                this.gameEvent(GameEvent.ENTITY_ROAR);
                            }
                            final EntityCachalotEcho echo = new EntityCachalotEcho(this.level(), this);
                            final float radius = this.headPart.getBbWidth() * 0.5F;
                            final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
                            final double extraX = (radius * (1F + random.nextFloat() * 0.13F)) * Mth.sin(Mth.PI + angle) + (random.nextFloat() - 0.5F) + this.getDeltaMovement().x * 2F;
                            final double extraZ = (radius * (1F + random.nextFloat() * 0.13F)) * Mth.cos(angle) + (random.nextFloat() - 0.5F) + this.getDeltaMovement().z * 2F;
                            final double x = this.headPart.getX() + extraX;
                            final double y = this.headPart.getY() + this.headPart.getBbHeight() * 0.5D;
                            final double z = this.headPart.getZ() + extraZ;
                            echo.setPos(x, y, z);
                            final double d0 = target.getX() - x;
                            final double d1 = target.getY(0.1D) - y;
                            final double d2 = target.getZ() - z;
                            echo.shoot(d0, d1, d2, 1F, 0.0F);
                            this.level().addFreshEntity(echo);
                        }
                        echoTimer++;
                    }
                    if (!waitForEchoFlag || receivedEcho) {
                        final double d0 = target.getX() - this.getX();
                        final double d1 = target.getEyeY() - this.getEyeY();
                        final double d2 = target.getZ() - this.getZ();
                        final double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                        final float targetYaw = (float) (Mth.atan2(d2, d0) * Mth.RAD_TO_DEG) - 90.0F;
                        final float targetPitch = (float) (-(Mth.atan2(d1, d3) * Mth.RAD_TO_DEG));
                        this.setXRot((this.getXRot() + Mth.clamp(targetPitch - this.getXRot(), -2, 2)));
                        if (d0 * d0 + d2 * d2 >= 4) {
                            this.setYRot((this.getYRot() + Mth.clamp(targetYaw - this.getYRot(), -2, 2)));
                            this.yBodyRot = getYRot();
                        }
                        if (chargeCooldown <= 0 && Math.abs(Mth.wrapDegrees(targetYaw) - Mth.wrapDegrees(this.getYRot())) < 4) {
                            this.setCharging(true);
                            whaleSpeedMod = 1.2F;
                            final double distSq = d0 * d0 + d2 * d2;
                            if (distSq < 4) {
                                this.setYRot(yRotO);
                                this.yBodyRot = yRotO;
                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1, 0.8));
                            } else {
                                if (this.isInWater() && target.isInWater()) {
                                    final Vec3 vector3d = this.getDeltaMovement();
                                    Vec3 vector3d1 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY(), target.getZ() - this.getZ());
                                    if (vector3d1.lengthSqr() > 1.0E-7D) {
                                        vector3d1 = vector3d1.normalize().scale(0.5D).add(vector3d.scale(0.8D));
                                    }
                                    this.setDeltaMovement(vector3d1.x, vector3d1.y, vector3d1.z);
                                }
                                this.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
                            }
                            if (this.isCharging()) {
                                if (this.distanceTo(target) < this.getBbWidth() && chargeProgress > 4) {
                                    if (target instanceof EntityGiantSquid && !this.isBaby()) {
                                        this.setGrabbing(true);
                                        this.setHoldingSquidLeft(random.nextBoolean());
                                    } else {
                                        target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                                    }
                                    this.setCharging(false);
                                    if (target.getVehicle() instanceof final Boat boat) {
                                        for (int i = 0; i < 3; ++i) {
                                            this.spawnAtLocation(boat.getVariant().getPlanks());
                                        }
                                        for (int j = 0; j < 2; ++j) {
                                            this.spawnAtLocation(Items.STICK);
                                        }
                                        target.removeVehicle();
                                        boat.hurt(this.damageSources().mobAttack(this), 1000);
                                        boat.remove(RemovalReason.DISCARDED);
                                    }
                                    chargeCooldown = target instanceof Player ? 30 : 100;
                                    if (random.nextInt(10) == 0) {
                                        Vec3 vec = this.getMouthVec();
                                        ItemEntity itementity = new ItemEntity(this.level(), vec.x, vec.y, vec.z, new ItemStack(AMItemRegistry.CACHALOT_WHALE_TOOTH.get()));
                                        itementity.setDefaultPickUpDelay();
                                        level().addFreshEntity(itementity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (chargeCooldown > 0) {
                chargeCooldown--;
            }
            if (spoutTimer > 0) {
                level().broadcastEntityEvent(this, (byte) 67);
                spoutTimer--;
                this.setXRot(0);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0, 0, 0));
            }
            if (isSleepTime() && !this.isSleeping() && this.isInWaterOrBubble() && this.getTarget() == null) {
                this.setSleeping(true);
            }
            if (this.isSleeping() && (!isSleepTime() || this.getTarget() != null)) {
                this.setSleeping(false);
            }
            if (target instanceof Player && ((Player) target).isCreative()) {
                this.setTarget(null);
            }
        }

        if (this.isAlive() && isCharging()) {
            for (final Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.headPart.getBoundingBox().inflate(1.0D))) {
                if (!isAlliedTo(entity) && !(entity instanceof EntityCachalotPart) && entity != this) {
                    launch(entity, true);
                }
            }
        }
        if (this.isInWater() && !this.isEyeInFluid(FluidTags.WATER) && this.getAirSupply() > 140) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.06, 0));
        }
        if (!this.level().isClientSide) {
            this.tryDespawn();
        }
        prevEyesInWater = this.isEyeInFluid(FluidTags.WATER);
    }

    private void launch(Entity e, boolean huge) {
        if ((e.onGround() || e.isInWater()) && !(e instanceof EntityCachalotWhale)) {
            final double d0 = e.getX() - this.getX();
            final double d1 = e.getZ() - this.getZ();
            final double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = huge ? 2F : 0.5F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    private boolean isSleepTime() {
        final long time = level().getDayTime();
        return time > 18000 && time < 22812 && this.isInWaterOrBubble();
    }

    public Vec3 getReturnEchoVector() {
        return getVec(0.5D);
    }

    public Vec3 getMouthVec() {
        return getVec(0.25D);
    }

    private Vec3 getVec(final double yShift) {
        final float radius = this.headPart.getBbWidth() * 0.5F;
        final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
        final double extraX = (radius * (1F + random.nextFloat() * 0.13F)) * Mth.sin(Mth.PI + angle) + (random.nextFloat() - 0.5F) + this.getDeltaMovement().x * 2F;
        final double extraZ = (radius * (1F + random.nextFloat() * 0.13F)) * Mth.cos(angle) + (random.nextFloat() - 0.5F) + this.getDeltaMovement().z * 2F;
        final double x = this.headPart.getX() + extraX;
        final double y = this.headPart.getY() + yShift;
        final double z = this.headPart.getZ() + extraZ;

        return new Vec3(x, y, z);
    }


    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        LivingEntity prev = this.getTarget();
        if (prev != entitylivingbaseIn && entitylivingbaseIn != null) {
            receivedEcho = false;
        }
        super.setTarget(entitylivingbaseIn);
    }

    public double[] getMovementOffsets(int p_70974_1_, float partialTicks) {
        if (this.isDeadOrDying()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        final int i = this.ringBufferIndex - p_70974_1_ & 63;
        final int j = this.ringBufferIndex - p_70974_1_ - 1 & 63;
        final double[] adouble = new double[3];
        double d0 = this.ringBuffer[i][0];
        double d1 = this.ringBuffer[j][0] - d0;
        adouble[0] = d0 + d1 * (double) partialTicks;
        d0 = this.ringBuffer[i][1];
        d1 = this.ringBuffer[j][1] - d0;
        adouble[1] = d0 + d1 * (double) partialTicks;
        adouble[2] = Mth.lerp(partialTicks, this.ringBuffer[i][2], this.ringBuffer[j][2]);
        return adouble;
    }

    public void push(Entity entityIn) {
    }

    private void setPartPosition(EntityCachalotPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX * part.scale, this.getY() + offsetY * part.scale, this.getZ() + offsetZ * part.scale);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.whaleParts;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        EntityCachalotWhale whale = AMEntityRegistry.CACHALOT_WHALE.get().create(serverWorld);
        whale.setAlbino(this.isAlbino());
        return whale;
    }

    public boolean attackEntityPartFrom(EntityCachalotPart entityCachalotPart, DamageSource source, float amount) {
        return this.hurt(source, amount);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setAirSupply(this.getMaxAirSupply());
        this.setXRot(0.0F);
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableMob.AgeableMobGroupData(0.75F);
        }
        this.setAlbino(random.nextInt(100) == 0);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        this.updateAir(i);
    }

    public boolean isPushedByFluid() {
        return this.isBeached();
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    protected void updateAir(int p_209207_1_) {
    }

    public int getMaxAirSupply() {
        return 4000;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 67) {
            spawnSpoutParticles();
        } else {
            super.handleEntityEvent(id);
        }
    }

    protected int increaseAirSupply(int currentAir) {
        if (!this.level().isClientSide && prevEyesInWater && spoutTimer <= 0 && !this.isEyeInFluid(FluidTags.WATER) && currentAir < this.getMaxAirSupply() / 2) {
            spoutTimer = 20 + random.nextInt(10);
        }
        return this.getMaxAirSupply();
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 3;
    }

    public void recieveEcho() {
        this.receivedEcho = true;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.cachalotWhaleSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity dismount) {
        Vec3 mouth = this.getMouthVec();
        BlockPos pos = AMBlockPos.fromVec3(mouth);
        while(!level().isEmptyBlock(pos) && !level().isWaterAt(pos) && pos.getY() < level().getMaxBuildHeight()){
            pos = pos.above();
        }
        return new Vec3(mouth.x, pos.getY() + 0.5F, mouth.z);
    }


    class AIBreathe extends Goal {

        public AIBreathe() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return EntityCachalotWhale.this.getAirSupply() < 140;
        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        public boolean isInterruptable() {
            return false;
        }

        public void start() {
            this.navigate();
        }

        private void navigate() {
            final Iterable<BlockPos> lvt_1_1_ = BlockPos.betweenClosed(Mth.floor(EntityCachalotWhale.this.getX() - 1.0D), Mth.floor(EntityCachalotWhale.this.getY()), Mth.floor(EntityCachalotWhale.this.getZ() - 1.0D), Mth.floor(EntityCachalotWhale.this.getX() + 1.0D), Mth.floor(EntityCachalotWhale.this.getY() + 8.0D), Mth.floor(EntityCachalotWhale.this.getZ() + 1.0D));
            BlockPos lvt_2_1_ = null;

            for (final BlockPos lvt_4_1_ : lvt_1_1_) {
                if (this.canBreatheAt(EntityCachalotWhale.this.level(), lvt_4_1_)) {
                    lvt_2_1_ = lvt_4_1_.below((int) (EntityCachalotWhale.this.getBbHeight() * 0.25d));
                    break;
                }
            }

            if (lvt_2_1_ == null) {
                lvt_2_1_ = AMBlockPos.fromCoords(EntityCachalotWhale.this.getX(), EntityCachalotWhale.this.getY() + 4.0D, EntityCachalotWhale.this.getZ());
            }
            if (EntityCachalotWhale.this.isEyeInFluid(FluidTags.WATER)) {
                EntityCachalotWhale.this.setDeltaMovement(EntityCachalotWhale.this.getDeltaMovement().add(0, 0.05F, 0));
            }

            EntityCachalotWhale.this.getNavigation().moveTo(lvt_2_1_.getX(), lvt_2_1_.getY(), lvt_2_1_.getZ(), 0.7D);
        }

        public void tick() {
            this.navigate();
        }

        private boolean canBreatheAt(LevelReader p_205140_1_, BlockPos p_205140_2_) {
            final BlockState lvt_3_1_ = p_205140_1_.getBlockState(p_205140_2_);
            return (p_205140_1_.getFluidState(p_205140_2_).isEmpty() || lvt_3_1_.is(Blocks.BUBBLE_COLUMN)) && lvt_3_1_.isPathfindable(p_205140_1_, p_205140_2_, PathComputationType.LAND);
        }
    }
}
