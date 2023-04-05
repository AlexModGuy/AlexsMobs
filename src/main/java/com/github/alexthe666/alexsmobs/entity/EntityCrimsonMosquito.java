package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class EntityCrimsonMosquito extends Monster {

    public static final ResourceLocation FULL_LOOT = new ResourceLocation("alexsmobs", "entities/crimson_mosquito_full");
    public static final ResourceLocation FROM_FLY_LOOT = new ResourceLocation("alexsmobs", "entities/crimson_mosquito_fly");
    public static final ResourceLocation FROM_FLY_FULL_LOOT = new ResourceLocation("alexsmobs", "entities/crimson_mosquito_fly_full");
    protected static final EntityDimensions FLIGHT_SIZE = EntityDimensions.fixed(1.2F, 1.8F);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BLOOD_LEVEL = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHRINKING = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_FLY = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> MOSQUITO_SCALE = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SICK = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LURING_LAVIATHAN = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FLEEING_ENTITY = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.INT);
    private static final Predicate<LivingEntity> REPELLENT = (mob) -> {
        return mob.hasEffect(AMEffectRegistry.MOSQUITO_REPELLENT.get()) || mob instanceof EntityTriops;
    };
    private static final Predicate<LivingEntity> NO_REPELLENT = (mob) -> {
        return !mob.hasEffect(AMEffectRegistry.MOSQUITO_REPELLENT.get());
    };
    public float prevFlyProgress;
    public float flyProgress;
    public float prevShootProgress;
    public float shootProgress;
    public int shootingTicks;
    public int randomWingFlapTick = 0;
    private int flightTicks = 0;
    private int sickTicks = 0;
    private boolean prevFlying = false;
    private int spitCooldown = 0;
    private int loopSoundTick = 0;
    private int drinkTime = 0;
    public float prevMosquitoScale = 1F;
    private int repellentCheckTime = 0;
    private Vec3 fleePos = null;
    protected EntityCrimsonMosquito(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new EntityCrimsonMosquito.MoveHelperController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
    }

    public boolean hasLuringLaviathan() {
        return this.entityData.get(LURING_LAVIATHAN) != -1;
    }

    public void onSpawnFromFly() {
        prevMosquitoScale = 0.2F;
        this.setShrink(false);
        this.setMosquitoScale(0.2F);
        this.setFromFly(true);
        for (int j = 0; j < 4; ++j) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextDouble() / 2.0D, this.getY(0.5D), this.getZ() + this.random.nextDouble() / 2.0D, this.random.nextDouble() * 0.5F + 0.5F, 0, 0.0D);
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MOSQUITO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MOSQUITO_DIE.get();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.crimsonMosquitoSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        if (this.getBloodLevel() > 0) {
            return this.isFromFly() ? FROM_FLY_FULL_LOOT : FULL_LOOT;
        }
        return this.isFromFly() ? FROM_FLY_LOOT : super.getDefaultLootTable();
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyTowardsTarget(this));
        this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyAwayFromTarget(this));
        this.goalSelector.addGoal(3, new EntityCrimsonMosquito.RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntityCrimsonMosquito.class, EntityWarpedMosco.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 20, true, false, NO_REPELLENT));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, EntityTriops.class, 16, 1.3D, 1.0D));
    }

    public static boolean canMosquitoSpawn(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        BlockPos blockpos = pos.below();
        boolean spawnBlock = worldIn.getBlockState(blockpos).canOcclude();
        return reason == MobSpawnType.SPAWNER || spawnBlock && worldIn.getBlockState(blockpos).isValidSpawn(worldIn, blockpos, typeIn) && isDarkEnoughToSpawn(worldIn, pos, randomIn) && checkMobSpawnRules(AMEntityRegistry.CRIMSON_MOSQUITO.get(), worldIn, reason, pos, randomIn);
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FlightTicks", this.flightTicks);
        compound.putInt("SickTicks", this.sickTicks);
        compound.putFloat("MosquitoScale", this.getMosquitoScale());
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("Shrinking", this.isShrinking());
        compound.putBoolean("IsFromFly", this.isFromFly());
        compound.putBoolean("Sick", this.isSick());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.flightTicks = compound.getInt("FlightTicks");
        this.sickTicks = compound.getInt("SickTicks");
        this.setMosquitoScale(compound.getFloat("MosquitoScale"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setShrink(compound.getBoolean("Shrinking"));
        this.setFromFly(compound.getBoolean("IsFromFly"));
        this.setSick(compound.getBoolean("Sick"));
    }

    private void spit(LivingEntity target) {
        if (this.isSick()) {
            return;
        }
        final EntityMosquitoSpit llamaspitentity = new EntityMosquitoSpit(this.level, this);
        final double d0 = target.getX() - this.getX();
        final double d1 = target.getY(0.3333333333333333D) - llamaspitentity.getY();
        final double d2 = target.getZ() - this.getZ();
        final float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.2F;
        llamaspitentity.shoot(d0, d1 + (double) f, d2, 1.5F, 10.0F);
        if (!this.isSilent()) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT);
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }
        if (this.getBloodLevel() > 0) {
            this.setBloodLevel(this.getBloodLevel() - 1);
        }
        this.level.addFreshEntity(llamaspitentity);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.LAVA) || source.is(DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(source);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && this.getRootVehicle() == source.getEntity().getRootVehicle()) {
            return super.hurt(source, amount * 0.333F);
        }
        if (flightTicks < 0) {
            flightTicks = 0;
        }
        return super.hurt(source, amount);
    }

    public void rideTick() {
        final Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                final Entity mount = this.getVehicle();
                if (mount instanceof final LivingEntity livingEntity) {
                    this.yBodyRot = livingEntity.yBodyRot;
                    this.setYRot(livingEntity.getYRot());
                    this.yHeadRot = livingEntity.yHeadRot;
                    this.yRotO = livingEntity.yHeadRot;
                    final float radius = 1F;
                    final float angle = (0.0174532925F * livingEntity.yBodyRot);
                    final double extraX = radius * Mth.sin((float) (Math.PI + angle));
                    final double extraZ = radius * Mth.cos(angle);
                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getEyeHeight() * 0.25F, mount.getY()), mount.getZ() + extraZ);
                    if (!mount.isAlive() || mount instanceof Player && ((Player) mount).isCreative()) {
                        this.removeVehicle();
                    }
                    if (!level.isClientSide) {
                        if (drinkTime % 20 == 0 && this.isAlive()) {
                            final boolean mungus = AMConfig.warpedMoscoTransformation && mount instanceof EntityMungus && ((EntityMungus) mount).isWarpedMoscoReady();
                            if (mount.hurt(this.damageSources().mobAttack(this), mungus ? 7F : 2.0F)) {
                                if (mungus) {
                                    ((EntityMungus) mount).disableExplosion();
                                }
                                final boolean sick = this.isNonMungusWarpedTrigger(mount);
                                if (sick || mungus) {
                                    if (!this.isSick()) {
                                        for (ServerPlayer serverplayerentity : this.level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(40.0D, 25.0D, 40.0D))) {
                                            AMAdvancementTriggerRegistry.MOSQUITO_SICK.trigger(serverplayerentity);
                                        }
                                    }
                                    this.setSick(true);
                                    this.setFlying(false);
                                    flightTicks = -150 - random.nextInt(200);
                                }
                                this.gameEvent(GameEvent.EAT);
                                this.playSound(SoundEvents.HONEY_DRINK, this.getSoundVolume(), this.getVoicePitch());
                                this.setBloodLevel(this.getBloodLevel() + 1);
                                if (this.getBloodLevel() > 3) {
                                    this.removeVehicle();
                                    AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.getId(), mount.getId()));
                                    this.setFlying(false);
                                    this.flightTicks = -15;
                                }
                            }
                        }

                        if (drinkTime > 81) {
                            drinkTime = -20 - random.nextInt(20);
                            this.removeVehicle();
                            AlexsMobs.sendMSGToAll(new MessageMosquitoDismount(this.getId(), mount.getId()));
                            this.setFlying(false);
                            this.flightTicks = -15;
                        }
                    }
                }

            }
        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(SHOOTING, false);
        this.entityData.define(SICK, false);
        this.entityData.define(BLOOD_LEVEL, 0);
        this.entityData.define(SHRINKING, false);
        this.entityData.define(FROM_FLY, false);
        this.entityData.define(MOSQUITO_SCALE, 1F);
        this.entityData.define(LURING_LAVIATHAN, -1);
        this.entityData.define(FLEEING_ENTITY, -1);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING).booleanValue();
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public void setupShooting() {
        this.entityData.set(SHOOTING, true);
        this.shootingTicks = 5;
    }

    public int getLuringLaviathan() {
        return this.entityData.get(LURING_LAVIATHAN).intValue();
    }

    public void setLuringLaviathan(int lure) {
        this.entityData.set(LURING_LAVIATHAN, lure);
    }

    public int getFleeingEntityId() {
        return this.entityData.get(FLEEING_ENTITY).intValue();
    }

    public void setFleeingEntityId(int lure) {
        this.entityData.set(FLEEING_ENTITY, lure);
    }

    public int getBloodLevel() {
        return Math.min(this.entityData.get(BLOOD_LEVEL).intValue(), 4);
    }

    public void setBloodLevel(int bloodLevel) {
        this.entityData.set(BLOOD_LEVEL, bloodLevel);
    }

    public boolean isShrinking() {
        return this.entityData.get(SHRINKING).booleanValue();
    }

    public boolean isFromFly() {
        return this.entityData.get(FROM_FLY).booleanValue();
    }

    public void setShrink(boolean shrink) {
        this.entityData.set(SHRINKING, shrink);
    }

    public void setFromFly(boolean fromFly) {
        this.entityData.set(FROM_FLY, fromFly);
    }

    public float getMosquitoScale() {
        return this.entityData.get(MOSQUITO_SCALE);
    }

    public void setMosquitoScale(float scale) {
        this.entityData.set(MOSQUITO_SCALE, scale);
    }


    public boolean isSick() {
        return this.entityData.get(SICK).booleanValue();
    }

    public void setSick(boolean shrink) {
        this.entityData.set(SICK, shrink);
    }

    public void tick() {
        super.tick();
        final boolean shooting = entityData.get(SHOOTING);
        if (prevFlying != this.isFlying()) {
            this.refreshDimensions();
        }

        if (shooting) {
            if (shootProgress < 5F)
                shootProgress++;
        } else {
            if (shootProgress > 0F)
                shootProgress--;
        }

        if (this.isFlying()) {
            if (flyProgress < 5F)
                flyProgress++;
        } else {
            if (flyProgress > 0F)
                flyProgress--;
        }

        if (!level.isClientSide) {
            if (this.isPassenger())
                this.setFlying(false);

            if (isFlying()) {
                this.setNoGravity(true);
            } else {
                this.setNoGravity(false);
            }
            LivingEntity target = this.getTarget();
            if (this.getFleeingEntityId() == -1) {
                if (target == null && tickCount - repellentCheckTime > 50) {
                    repellentCheckTime = tickCount;
                    LivingEntity closestRepel = null;
                    for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(30), REPELLENT)) {
                        if(closestRepel == null || entity.distanceTo(this) < closestRepel.distanceTo(this)){
                            closestRepel = entity;
                        }
                    }
                    if(closestRepel != null){
                        this.setFleeingEntityId(closestRepel.getId());
                    }
                }
                if (target != null && REPELLENT.test(target) && this.distanceTo(target) < 20) {
                    this.setFleeingEntityId(target.getId());
                }
            } else {
                Entity fleeing = level.getEntity(this.getFleeingEntityId());
                if (fleeing instanceof LivingEntity living && REPELLENT.test(living) && this.distanceTo(living) < 20) {
                    this.setTarget(null);
                    this.setLastHurtByMob(null);
                    if(this.isPassenger()){
                        this.stopRiding();
                    }
                    if(fleePos == null || fleePos.distanceTo(this.position()) < 3 || random.nextInt(40) == 0){
                        Vec3 vec = LandRandomPos.getPosAway(this, 8, 4, fleeing.position());
                        if(vec != null){
                            fleePos = vec;
                        }
                    }else{
                        this.setFlying(true);
                        this.moveControl.setWantedPosition(fleePos.x, fleePos.y + 1, fleePos.z, 1.2F);
                    }
                } else {
                    this.setFleeingEntityId(-1);
                }
            }
            if (hasLuringLaviathan()) {
                this.setTarget(null);
                this.setLastHurtByMob(null);
                final Entity entity = this.level.getEntity(this.getLuringLaviathan());
                if (entity instanceof EntityLaviathan && ((EntityLaviathan) entity).isChilling()) {
                    Vec3 vec = ((EntityLaviathan) entity).getLureMosquitoPos();
                    this.setFlying(true);
                    this.lookAt(entity, 10, 10);
                    this.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 0.7F);
                } else {
                    this.setLuringLaviathan(-1);
                }
            }
        }
        if (this.flyProgress == 0 && random.nextInt(200) == 0) {
            randomWingFlapTick = 5 + random.nextInt(15);
        }
        if (randomWingFlapTick > 0) {
            randomWingFlapTick--;
        }
        if (!level.isClientSide && isOnGround() && !this.isFlying() && (flightTicks >= 0 && random.nextInt(5) == 0 || this.getTarget() != null)) {
            this.setFlying(true);
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.onGround = false;
            this.hasImpulse = true;
        }
        if (flightTicks < 0) {
            flightTicks++;
        }
        if (!level.isClientSide && isFlying()) {
            flightTicks++;
            if (flightTicks > 200 && (this.getTarget() == null || !this.getTarget().isAlive())) {
                BlockPos above = this.getGroundPosition(this.blockPosition().above());
                if (level.getFluidState(above).isEmpty() && !level.getBlockState(above).isAir()) {
                    this.getDeltaMovement().add(0, -0.2D, 0);
                    if (this.isOnGround()) {
                        this.setFlying(false);
                        flightTicks = -150 - random.nextInt(200);
                    }
                }
            }
        }
        prevMosquitoScale = this.getMosquitoScale();
        if (isShrinking()) {
            if (this.getMosquitoScale() > 0.4F) {
                this.setMosquitoScale(this.getMosquitoScale() - 0.1F);
            }
        } else {
            if (this.getMosquitoScale() < 1F && !this.isSick()) {
                this.setMosquitoScale(this.getMosquitoScale() + 0.05F);
            }
        }
        if (!level.isClientSide && shootingTicks > 0) {
            shootingTicks--;
            if (shootingTicks == 0) {
                if (this.getTarget() != null && this.getBloodLevel() > 0) {
                    this.spit(this.getTarget());
                }
                this.entityData.set(SHOOTING, false);
            }
        }
        if (isFlying()) {
            if (loopSoundTick == 0) {
                this.gameEvent(GameEvent.ENTITY_ROAR);
                this.playSound(AMSoundRegistry.MOSQUITO_LOOP.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            loopSoundTick++;
            if (loopSoundTick > 100) {
                loopSoundTick = 0;
            }
        }

        if (isPassenger()) {
            if (drinkTime < 0)
                drinkTime = 0;

            drinkTime++;
        } else {
            drinkTime = 0;
        }
        prevFlyProgress = flyProgress;
        prevShootProgress = shootProgress;
        prevFlying = this.isFlying();
        if (this.isSick()) {
            sickTicks++;
            if (this.getTarget() != null && !this.isPassenger()) {
                this.setTarget(null);
            }
            if (sickTicks > 100) {
                this.setShrink(false);
                this.setMosquitoScale(this.getMosquitoScale() + 0.015F);
                if (sickTicks > 160) {
                    EntityWarpedMosco mosco = AMEntityRegistry.WARPED_MOSCO.get().create(level);
                    mosco.copyPosition(this);
                    if (!level.isClientSide) {
                        mosco.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                    }

                    if (!level.isClientSide) {
                        this.level.broadcastEntityEvent(this, (byte) 79);
                        level.addFreshEntity(mosco);
                    }
                    this.remove(RemovalReason.DISCARDED);

                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 79) {
            for (int i = 0; i < 27; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10.0D;
                this.level.addParticle(ParticleTypes.EXPLOSION, this.getRandomX(1.6D), this.getY() + random.nextFloat() * 3.4F, this.getRandomZ(1.6D), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return isFlying() ? FLIGHT_SIZE : super.getDimensions(poseIn);
    }

    public void travel(Vec3 vec3d) {
        if (this.isOnGround() && !this.isFlying()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (item == AMItemRegistry.WARPED_MIXTURE.get() && !this.isSick()) {
            this.spawnAtLocation(item.getCraftingRemainingItem(itemstack));
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setSick(true);
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private BlockPos getGroundPosition(BlockPos radialPos) {
        while (radialPos.getY() > 1 && level.isEmptyBlock(radialPos)) {
            radialPos = radialPos.below();
        }
        return radialPos;
    }

    static class RandomFlyGoal extends Goal {
        private final EntityCrimsonMosquito parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntityCrimsonMosquito mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl movementcontroller = this.parentEntity.getMoveControl();
            if (!parentEntity.isFlying() || parentEntity.getTarget() != null || parentEntity.hasLuringLaviathan() || parentEntity.getFleeingEntityId() != -1) {
                return false;
            }
            if (!movementcontroller.hasWanted() || target == null) {
                target = getBlockInViewMosquito();
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && parentEntity.isFlying() && parentEntity.distanceToSqr(Vec3.atCenterOf(target)) > 2.4D && parentEntity.getMoveControl().hasWanted() && !parentEntity.horizontalCollision;
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewMosquito();
            }
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 2.5F) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewMosquito() {
            final float radius = 1 + parentEntity.getRandom().nextInt(5);
            final float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            final float renderYawOffset = parentEntity.yBodyRot;
            final float angle = (0.0174532925F * renderYawOffset) + 3.15F + (parentEntity.getRandom().nextFloat() * neg);
            final double extraX = radius * Mth.sin((float) (Math.PI + angle));
            final double extraZ = radius * Mth.cos(angle);
            final BlockPos radialPos = AMBlockPos.fromCoords(parentEntity.getX() + extraX, parentEntity.getY() + 2, parentEntity.getZ() + extraZ);
            final BlockPos ground = parentEntity.getGroundPosition(radialPos);
            final int up = parentEntity.isSick() ? 2 : 6;
            final BlockPos newPos = ground.above(1 + parentEntity.getRandom().nextInt(up));
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6) {
                return newPos;
            }
            return null;
        }

    }

    static class MoveHelperController extends MoveControl {
        private final EntityCrimsonMosquito parentEntity;

        public MoveHelperController(EntityCrimsonMosquito sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        public void tick() {
            if (speedModifier >= 1 && parentEntity.isSick()) {
                speedModifier = 0.35D;
            }
            if (parentEntity.isFlying()) {
                if (this.operation == Operation.STRAFE) {
                    final Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                    final double d0 = vector3d.length();
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0, vector3d.scale(this.speedModifier * 0.05D / d0).y(), 0));
                    final float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                    final float f1 = (float) this.speedModifier * f;

                    this.strafeForwards = 1.0F;
                    this.strafeRight = 0.0F;

                    this.mob.setSpeed(f1);
                    this.mob.setZza(this.strafeForwards);
                    this.mob.setXxa(this.strafeRight);
                    this.operation = MoveControl.Operation.WAIT;
                } else if (this.operation == MoveControl.Operation.MOVE_TO) {
                    final Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                    final double d0 = vector3d.length();
                    if (d0 < parentEntity.getBoundingBox().getSize()) {
                        this.operation = MoveControl.Operation.WAIT;
                        parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                    } else {
                        parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));
                        if (parentEntity.getTarget() == null) {
                            final Vec3 vector3d1 = parentEntity.getDeltaMovement();
                            parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                            parentEntity.yBodyRot = parentEntity.getYRot();
                        } else {
                            final double d2 = parentEntity.getTarget().getX() - parentEntity.getX();
                            final double d1 = parentEntity.getTarget().getZ() - parentEntity.getZ();
                            parentEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                            parentEntity.yBodyRot = parentEntity.getYRot();
                        }
                    }

                }
            } else {
                operation = Operation.WAIT;
                this.mob.setSpeed(0);
                this.mob.setZza(0);
                this.mob.setXxa(0);

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.parentEntity.level.noCollision(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    public class FlyTowardsTarget extends Goal {
        private final EntityCrimsonMosquito parentEntity;

        public FlyTowardsTarget(EntityCrimsonMosquito mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (!parentEntity.isFlying() || parentEntity.getBloodLevel() > 0 || parentEntity.drinkTime < 0 || parentEntity.getFleeingEntityId() != -1) {
                return false;
            }
            return !parentEntity.isPassenger() && parentEntity.getTarget() != null && !isBittenByMosquito(parentEntity.getTarget());
        }

        public boolean canContinueToUse() {
            return parentEntity.drinkTime >= 0 && parentEntity.getFleeingEntityId() == -1 && parentEntity.getTarget() != null && !isBittenByMosquito(parentEntity.getTarget()) && !parentEntity.horizontalCollision && parentEntity.getBloodLevel() == 0 && parentEntity.isFlying() && parentEntity.getMoveControl().hasWanted();
        }

        public boolean isBittenByMosquito(Entity entity) {
            for (Entity e : entity.getPassengers()) {
                if (e instanceof EntityCrimsonMosquito) {
                    return true;
                }
            }
            return false;
        }

        public void stop() {
        }

        public void tick() {
            if (parentEntity.getTarget() != null) {
                this.parentEntity.getMoveControl().setWantedPosition(parentEntity.getTarget().getX(), parentEntity.getTarget().getY(), parentEntity.getTarget().getZ(), 1.0D);
                if (parentEntity.getBoundingBox().inflate(0.3F, 0.3F, 0.3F).intersects(parentEntity.getTarget().getBoundingBox()) && !isBittenByMosquito(parentEntity.getTarget()) && parentEntity.drinkTime == 0) {
                    parentEntity.startRiding(parentEntity.getTarget(), true);
                    if (!parentEntity.level.isClientSide) {
                        AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(parentEntity.getId(), parentEntity.getTarget().getId()));
                    }
                }
            }
        }
    }

    public class FlyAwayFromTarget extends Goal {
        private final EntityCrimsonMosquito parentEntity;
        private int spitCooldown = 0;
        private BlockPos shootPos = null;

        public FlyAwayFromTarget(EntityCrimsonMosquito mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (!parentEntity.isFlying() || parentEntity.getBloodLevel() <= 0 && parentEntity.drinkTime >= 0 || parentEntity.getFleeingEntityId() != -1) {
                return false;
            }
            if (!parentEntity.isPassenger() && parentEntity.getTarget() != null) {
                shootPos = getBlockInTargetsViewMosquito(parentEntity.getTarget());
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return parentEntity.getTarget() != null && (parentEntity.getBloodLevel() > 0 || parentEntity.drinkTime < 0) && parentEntity.isFlying() && !parentEntity.horizontalCollision;
        }

        public void stop() {
            spitCooldown = 20;
        }

        public void tick() {
            if (spitCooldown > 0) {
                spitCooldown--;
            }
            if (parentEntity.getTarget() != null) {
                if (shootPos == null) {
                    shootPos = getBlockInTargetsViewMosquito(parentEntity.getTarget());
                } else {
                    this.parentEntity.getMoveControl().setWantedPosition(shootPos.getX() + 0.5D, shootPos.getY() + 0.5D, shootPos.getZ() + 0.5D, 1.0D);
                    this.parentEntity.lookAt(parentEntity.getTarget(), 30.0F, 30.0F);
                    if (parentEntity.distanceToSqr(Vec3.atCenterOf(shootPos)) < 2.5F) {
                        if (spitCooldown == 0 && parentEntity.getBloodLevel() > 0) {
                            parentEntity.setupShooting();
                            spitCooldown = 20;
                        }
                        shootPos = null;
                    }
                }
            }

        }

        public BlockPos getBlockInTargetsViewMosquito(LivingEntity target) {
            final float radius = 4 + parentEntity.getRandom().nextInt(5);
            final float angle = (0.0174532925F * (target.yHeadRot + 90F + parentEntity.getRandom().nextInt(180)));
            final double extraX = radius * Mth.sin((float) (Math.PI + angle));
            final double extraZ = radius * Mth.cos(angle);
            final BlockPos ground = AMBlockPos.fromCoords(target.getX() + extraX, target.getY() + 1, target.getZ() + extraZ);
            if (parentEntity.distanceToSqr(Vec3.atCenterOf(ground)) > 30) {
                if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(ground)) && parentEntity.distanceToSqr(Vec3.atCenterOf(ground)) > 6) {
                    return ground;
                }
            }
            return parentEntity.blockPosition();
        }
    }

    public boolean isNonMungusWarpedTrigger(Entity entity) {
        final ResourceLocation mobtype = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return mobtype != null && !AMConfig.warpedMoscoMobTriggers.isEmpty() && AMConfig.warpedMoscoMobTriggers.contains(mobtype.toString());
    }

}
