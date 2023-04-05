package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EntityFroststalker extends Animal implements IAnimatedEntity, ISemiAquatic {

    public static final ResourceLocation SPIKED_LOOT = new ResourceLocation("alexsmobs", "entities/froststalker_spikes");
    public static final Animation ANIMATION_BITE = Animation.create(13);
    public static final Animation ANIMATION_SPEAK = Animation.create(11);
    public static final Animation ANIMATION_SLASH_L = Animation.create(12);
    public static final Animation ANIMATION_SLASH_R = Animation.create(12);
    public static final Animation ANIMATION_SHOVE = Animation.create(12);
    private static final EntityDataAccessor<Boolean> SPIKES = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPIKE_SHAKING = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BIPEDAL = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TURN_ANGLE = SynchedEntityData.defineId(EntityFroststalker.class, EntityDataSerializers.FLOAT);
    public static final Predicate<Player> VALID_LEADER_PLAYERS = (player) -> {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.FROSTSTALKER_HELMET.get());
    };
    public float bipedProgress;
    public float prevBipedProgress;
    public float tackleProgress;
    public float prevTackleProgress;
    public float spikeShakeProgress;
    public float prevSpikeShakeProgress;
    public float prevTurnAngle;
    private int animationTick;
    private Animation currentAnimation;
    private int standingTime = 400 - random.nextInt(700);
    private int currentSpeedMode = -1;
    private LivingEntity leader;
    private int packSize = 1;
    private int shakeTime = 0;
    private boolean hasSpikedArmor = false;
    private int fleeFireFlag;
    private int resetLeaderCooldown = 100;

    protected EntityFroststalker(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.FROSTSTALKER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.FROSTSTALKER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.FROSTSTALKER_HURT.get();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.froststalkerSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canFroststalkerSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getRawBrightness(pos, 0) > 8 && (worldIn.getBlockState(pos.below()).is(Blocks.ICE) || worldIn.getBlockState(pos.below()).getMaterial().isSolid() || worldIn.getBlockState(pos.below()).is(Blocks.SNOW_BLOCK));
    }

    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return this.hasSpikes() ? SPIKED_LOOT : super.getDefaultLootTable();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 24D).add(Attributes.ARMOR, 2.0D).add(Attributes.ATTACK_DAMAGE, 4.5D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean hurt(DamageSource source, float amount) {
        if(source.isFire()){
            amount *= 2F;
        }
        boolean prev = super.hurt(source, amount);
        if (prev && this.hasSpikes() && !this.isSpikeShaking() && source.getEntity() != null && source.getEntity().distanceTo(this) < 10) {
            this.setSpikeShaking(true);
            shakeTime = 20 + random.nextInt(60);
            standFor(shakeTime + 10);
        }
        return prev;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this) {
            @Override
            public void tick() {
                if (EntityFroststalker.this.getRandom().nextFloat() < 0.8F) {
                    if (EntityFroststalker.this.hasSpikes()) {
                        EntityFroststalker.this.jumpUnderwater();
                    } else {
                        EntityFroststalker.this.getJumpControl().jump();
                    }
                }
            }
        });
        this.goalSelector.addGoal(1, new AIAvoidFire());
        this.goalSelector.addGoal(2, new FroststalkerAIMelee(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new FroststalkerAIFollowLeader(this));
        this.goalSelector.addGoal(6, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(7, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(8, new AnimalAIWanderRanged(this, 90, 1.0D, 7, 7));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, EntityFroststalker.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 40, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.FROSTSTALKER_TARGETS)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 80, false, true, (livingEntity -> {
            return !livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.FROSTSTALKER_HELMET.get());
        })));
    }

    private void jumpUnderwater() {
        BlockPos pos = this.getOnPos();
        if(this.level.isWaterAt(pos) && !this.level.isWaterAt(pos.above())){
            this.setPos(this.getX(), this.getY() + 1, this.getZ());
            this.level.setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
            this.level.scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(this.getRandom(), 60, 120));
        }
        double d0 = 0.2F;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d0, vec3.z);
    }

    @Override
    public void setInLove(@Nullable Player player) {
        if(player != null && isValidLeader(player)){
            super.setInLove(player);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TURN_ANGLE, 0F);
        this.entityData.define(SPIKES, Boolean.valueOf(true));
        this.entityData.define(BIPEDAL, Boolean.valueOf(false));
        this.entityData.define(SPIKE_SHAKING, Boolean.valueOf(false));
        this.entityData.define(TACKLING, Boolean.valueOf(false));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Spiked", this.hasSpikes());
        compound.putBoolean("Bipedal", this.isBipedal());
        compound.putBoolean("SpikeShaking", this.isSpikeShaking());
        compound.putInt("StandingTime", standingTime);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSpiked(compound.getBoolean("Spiked"));
        this.setBipedal(compound.getBoolean("Bipedal"));
        this.setSpikeShaking(compound.getBoolean("SpikeShaking"));
        this.standingTime = compound.getInt("StandingTime");
    }

    public BlockPos getRestrictCenter() {
        return this.leader == null ? super.getRestrictCenter() : this.leader.getOnPos();
    }

    public boolean hasRestriction() {
        return this.isFollower();
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.PORKCHOP) || stack.is(Items.COOKED_PORKCHOP);
    }

    public void tick() {
        super.tick();
        this.prevBipedProgress = bipedProgress;
        this.prevTackleProgress = tackleProgress;
        this.prevSpikeShakeProgress = spikeShakeProgress;
        this.prevTurnAngle = getTurnAngle();
        if (this.isBipedal() && bipedProgress < 5.0F) {
            bipedProgress++;
        }
        if (!this.isBipedal() && bipedProgress > 0.0F) {
            bipedProgress--;
        }
        if (this.isTackling() && tackleProgress < 5.0F) {
            tackleProgress++;
        }
        if (!this.isTackling() && tackleProgress > 0.0F) {
            tackleProgress--;
        }
        if (this.isSpikeShaking() && spikeShakeProgress < 5.0F) {
            spikeShakeProgress++;
        }
        if (!this.isSpikeShaking() && spikeShakeProgress > 0.0F) {
            spikeShakeProgress--;
        }
        if (this.isSpikeShaking() && currentSpeedMode != 2) {
            currentSpeedMode = 2;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1F);
        }
        if (!this.isSpikeShaking() && isBipedal() && currentSpeedMode != 0) {
            currentSpeedMode = 0;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
        }
        if (!this.isSpikeShaking() && !isBipedal() && currentSpeedMode != 1) {
            currentSpeedMode = 1;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        }
        if (this.hasSpikes() && !hasSpikedArmor) {
            hasSpikedArmor = true;
            this.getAttribute(Attributes.ARMOR).setBaseValue(12F);
        }
        if (!this.hasSpikes() && hasSpikedArmor) {
            hasSpikedArmor = false;
            this.getAttribute(Attributes.ARMOR).setBaseValue(0F);
        }

        if (!level.isClientSide) {
            if (this.tickCount % 200 == 0) {
                if (isInWaterRainOrBubble() && !this.hasSpikes()) {
                    this.setSpiked(true);
                }
                if (this.isHotBiome() && !isInWaterRainOrBubble()) {
                    this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400));
                    if (random.nextInt(2) == 0 && !this.isInWaterRainOrBubble()) {
                        this.setSpiked(false);
                    }
                }
            }
            float threshold = 1F;
            boolean flag = false;
            if (isBipedal() && this.yRotO - this.getYRot() > threshold) {
                this.setTurnAngle(this.getTurnAngle() + 5);
                flag = true;
            }
            if (isBipedal() && this.yRotO - this.getYRot() < -threshold) {
                this.setTurnAngle(this.getTurnAngle() - 5);
                flag = true;
            }
            if (!flag) {
                if (this.getTurnAngle() > 0) {
                    this.setTurnAngle(Math.max(this.getTurnAngle() - 10, 0));
                }
                if (this.getTurnAngle() < 0) {
                    this.setTurnAngle(Math.min(this.getTurnAngle() + 10, 0));
                }
            }
            this.setTurnAngle(Mth.clamp(this.getTurnAngle(), -60, 60));
            if (standingTime > 0) {
                standingTime--;
            }
            if (standingTime < 0) {
                standingTime++;
            }
            if (this.isBipedal() && standingTime <= 0) {
                standingTime = -200 - random.nextInt(400);
                this.setBipedal(false);
            }
            if (!this.isBipedal() && standingTime == 0 && this.getDeltaMovement().lengthSqr() >= 0.03D) {
                standingTime = 200 + random.nextInt(600);
                this.setBipedal(true);
            }
            if (shakeTime > 0) {
                if (this.shakeTime % 5 == 0) {
                    int spikeCount = 2 + random.nextInt(4);
                    for (int i = 0; i < spikeCount; i++) {
                        float f = ((i + 1) / (float) spikeCount) * 360F;
                        EntityIceShard shard = new EntityIceShard(level, this);
                        shard.shootFromRotation(this, this.getXRot() - random.nextInt(40), f, 0.0F, 0.15F + random.nextFloat() * 0.2F, 1.0F);
                        level.addFreshEntity(shard);
                    }
                }
                shakeTime--;
            }
            if (this.isSpikeShaking() && shakeTime == 0) {
                this.setSpikeShaking(false);
                if (random.nextInt(2) == 0) {
                    this.setSpiked(false);
                }
            }
            if (this.getTarget() != null && isValidLeader(this.getTarget())) {
                this.setTarget(null);
            }
            //Makes entire pack attack target
            if (this.getTarget() != null && !isValidLeader(this.getTarget()) && this.getTarget().isAlive() && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().isAlive())) {
                this.setLastHurtByMob(this.getTarget());
            }
            LivingEntity playerTarget = null;
            if (leader instanceof Player) {
                playerTarget = leader.getLastHurtMob();
                if (playerTarget == null || !playerTarget.isAlive() || playerTarget instanceof EntityFroststalker) {
                    playerTarget = leader.getLastHurtByMob();
                }
            }
            if (playerTarget != null && playerTarget.isAlive() && !(playerTarget instanceof EntityFroststalker)) {
                this.setTarget(playerTarget);
            }
            boolean attackAnim = this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5 ||
                    this.getAnimation() == ANIMATION_SHOVE && this.getAnimationTick() == 8 ||
                    this.getAnimation() == ANIMATION_SLASH_L && this.getAnimationTick() == 7 ||
                    this.getAnimation() == ANIMATION_SLASH_R && this.getAnimationTick() == 7;
            if (this.getTarget() != null && attackAnim) {
                getTarget().knockback(0.2F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
                this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
        }
        if (fleeFireFlag > 0) {
            fleeFireFlag--;
        }
        if(!level.isClientSide){
            if(resetLeaderCooldown > 0){
                resetLeaderCooldown--;
            }else{
                resetLeaderCooldown = 200 + this.getRandom().nextInt(200);
                this.lookForPlayerLeader();
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void lookForPlayerLeader() {
       if(!(this.leader instanceof Player)){
           float range = 10;
           List<Player> playerList = this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(range, range, range), EntityFroststalker.VALID_LEADER_PLAYERS);
           Player closestPlayer = null;
           for(Player player : playerList){
               if(closestPlayer == null || player.distanceTo(this) < closestPlayer.distanceTo(this)){
                   closestPlayer = player;
               }
           }
           if(closestPlayer != null){
               this.stopFollowing();
               this.startFollowing(closestPlayer);
           }
       }
    }

    public boolean isFleeingFire(){
        return fleeFireFlag > 0;
    }

    public boolean isHotBiome() {
        if (this.isNoAi()) {
            return false;
        }
        if (this.level.dimension() == Level.NETHER) {
            return true;
        } else {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY());
            int k = Mth.floor(this.getZ());
            return this.level.getBiome(new BlockPos(i, 0, k)).value().shouldSnowGolemBurn(new BlockPos(i, j, k));
        }
    }

    public void standFor(int time) {
        this.setBipedal(true);
        standingTime = time;
    }

    @Override
    protected float getJumpPower() {
        return 0.52F * this.getBlockJumpFactor();
    }

    @Override
    protected void jumpFromGround() {
        double d0 = (double) this.getJumpPower() + this.getJumpBoostPower();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d0, vec3.z);
        float f = this.getYRot() * ((float) Math.PI / 180F);
        this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f) * 0.2F, 0, Mth.cos(f) * 0.2F));
        this.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
    }

    public void frostJump() {
        jumpFromGround();
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BITE, ANIMATION_SPEAK, ANIMATION_SLASH_L, ANIMATION_SLASH_R, ANIMATION_SHOVE};
    }

    public float getTurnAngle() {
        return this.entityData.get(TURN_ANGLE);
    }

    public void setTurnAngle(float progress) {
        this.entityData.set(TURN_ANGLE, progress);
    }

    public boolean hasSpikes() {
        return this.entityData.get(SPIKES).booleanValue();
    }

    public void setSpiked(boolean bar) {
        this.entityData.set(SPIKES, Boolean.valueOf(bar));
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLING).booleanValue();
    }

    public void setTackling(boolean bar) {
        this.entityData.set(TACKLING, Boolean.valueOf(bar));
    }

    public boolean isBipedal() {
        return this.entityData.get(BIPEDAL).booleanValue();
    }

    public void setBipedal(boolean bar) {
        this.entityData.set(BIPEDAL, Boolean.valueOf(bar));
    }

    public boolean isSpikeShaking() {
        return this.entityData.get(SPIKE_SHAKING).booleanValue();
    }

    public void setSpikeShaking(boolean bar) {
        this.entityData.set(SPIKE_SHAKING, Boolean.valueOf(bar));
    }

    public boolean isFollower() {
        return this.leader != null && isValidLeader(leader);
    }

    public boolean isValidLeader(LivingEntity leader) {
        if (leader instanceof Player) {
            if (this.getLastHurtByMob() != null && this.getLastHurtByMob().equals(leader)) {
                return false;
            }
            return leader.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.FROSTSTALKER_HELMET.get());
        } else {
            return leader.isAlive() && leader instanceof EntityFroststalker;
        }
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            int anim = this.random.nextInt(4);
            if (anim == 0) {
                this.setAnimation(ANIMATION_SHOVE);
            } else if (anim == 1) {
                this.setAnimation(ANIMATION_BITE);
            } else if (anim == 2) {
                this.setAnimation(ANIMATION_SLASH_L);
            } else if (anim == 3) {
                this.setAnimation(ANIMATION_SLASH_R);
            }
        }
        return true;
    }


    public LivingEntity startFollowing(LivingEntity leader) {
        this.leader = leader;
        if (leader instanceof EntityFroststalker) {
            ((EntityFroststalker) leader).addFollower();
        }
        return leader;
    }

    public void stopFollowing() {
        if (this.leader instanceof EntityFroststalker) {
            ((EntityFroststalker) this.leader).removeFollower();
        }
        this.leader = null;
    }

    private void addFollower() {
        ++this.packSize;
    }

    private void removeFollower() {
        --this.packSize;
    }

    public boolean canBeFollowed() {
        return this.hasFollowers() && this.packSize < getMaxPackSize() && isValidLeader(this);
    }

    public boolean hasFollowers() {
        return this.packSize > 1;
    }

    public int getMaxSpawnClusterSize() {
        return 6;
    }

    public int getMaxPackSize() {
        return this.getMaxSpawnClusterSize();
    }

    public void addFollowers(Stream<EntityFroststalker> p_27534_) {
        p_27534_.limit(getMaxPackSize() - this.packSize).filter((p_27538_) -> {
            return p_27538_ != this;
        }).forEach((p_27536_) -> {
            p_27536_.startFollowing(this);
        });
    }

    public boolean inRangeOfLeader() {
        return this.distanceTo(this.leader) <= 60.0D;
    }

    public void pathToLeader() {
        if (this.isFollower()) {
            double speed = 1.0D;
            if (leader instanceof Player) {
                speed = 1.3D;
                if (this.distanceTo(leader) > 24) {
                    speed = 1.4F;
                    this.standFor(20);
                }
            }
            if (this.distanceTo(leader) > 6 && this.getNavigation().isDone()) {
                this.getNavigation().moveTo(this.leader, speed);
            }
        }
    }

    @Override
    protected void onChangedBlock(BlockPos pos) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, this);
        if (i > 0 || this.hasSpikes()) {
            FrostWalkerEnchantment.onEntityMoved(this, this.level, pos, i == 0 ? -1 : i);
        }
        if (this.shouldRemoveSoulSpeed(this.getBlockStateOn())) {
            this.removeSoulSpeed();
        }
        this.tryAddSoulSpeed();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_27528_, DifficultyInstance p_27529_, MobSpawnType p_27530_, @Nullable SpawnGroupData p_27531_, @Nullable CompoundTag p_27532_) {
        //do not call super here
        this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
        if (p_27531_ == null) {
            p_27531_ = new SchoolSpawnGroupData(this);
        } else {
            this.startFollowing(((SchoolSpawnGroupData) p_27531_).leader);
        }

        return p_27531_;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return AMEntityRegistry.FROSTSTALKER.get().create(p_146743_);
    }

    @Override
    public boolean shouldEnterWater() {
        return !this.hasSpikes() && (this.getTarget() == null || !this.getTarget().isAlive());
    }

    @Override
    public boolean shouldLeaveWater() {
        return this.hasSpikes() || (this.getTarget() != null && this.getTarget().isAlive());
    }

    @Override
    public boolean shouldStopMoving() {
        return false;
    }

    @Override
    public int getWaterSearchRange() {
        return 10;
    }

    public static class SchoolSpawnGroupData implements SpawnGroupData {
        public final EntityFroststalker leader;

        public SchoolSpawnGroupData(EntityFroststalker p_27553_) {
            this.leader = p_27553_;
        }
    }


    private class AIAvoidFire extends Goal {
        private final int searchLength;
        private final int verticalSearchRange;
        protected BlockPos destinationBlock;
        protected int runDelay = 20;
        private Vec3 fleeTarget;

        private AIAvoidFire() {
            searchLength = 20;
            verticalSearchRange = 1;
        }

        public boolean canContinueToUse() {
            return destinationBlock != null && isFire(EntityFroststalker.this.level, destinationBlock.mutable()) && isCloseToFire(16);
        }

        public boolean isCloseToFire(double dist) {
            return destinationBlock == null || EntityFroststalker.this.distanceToSqr(Vec3.atCenterOf(destinationBlock)) < dist * dist;
        }

        @Override
        public boolean canUse() {
            if (this.runDelay > 0) {
                --this.runDelay;
                return false;
            } else {
                this.runDelay = 30 + EntityFroststalker.this.random.nextInt(100);
                return this.searchForDestination();
            }
        }

        public void start() {
            EntityFroststalker.this.fleeFireFlag = 200;
            Vec3 vec = LandRandomPos.getPosAway(EntityFroststalker.this, 15, 5, Vec3.atCenterOf(destinationBlock));
            if (vec != null) {
                EntityFroststalker.this.standFor(100 + random.nextInt(100));
                fleeTarget = vec;
                EntityFroststalker.this.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.2F);
            }
        }

        public void tick() {
            if (this.isCloseToFire(16)) {
                EntityFroststalker.this.fleeFireFlag = 200;
                if (fleeTarget == null || EntityFroststalker.this.distanceToSqr(fleeTarget) < 2F) {
                    Vec3 vec = LandRandomPos.getPosAway(EntityFroststalker.this, 15, 5, Vec3.atCenterOf(destinationBlock));
                    if (vec != null) {
                        fleeTarget = vec;
                    }
                }
                if (fleeTarget != null) {
                    EntityFroststalker.this.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1F);
                }
            }
        }

        public void stop() {
            fleeTarget = null;
        }

        protected boolean searchForDestination() {
            int lvt_1_1_ = this.searchLength;
            int lvt_2_1_ = this.verticalSearchRange;
            BlockPos lvt_3_1_ = EntityFroststalker.this.blockPosition();
            BlockPos.MutableBlockPos lvt_4_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
                for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; ++lvt_6_1_) {
                    for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                        for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0; lvt_8_1_ <= lvt_6_1_; lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_) {
                            lvt_4_1_.setWithOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                            if (this.isFire(EntityFroststalker.this.level, lvt_4_1_)) {
                                this.destinationBlock = lvt_4_1_;
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        private boolean isFire(Level world, BlockPos.MutableBlockPos lvt_4_1_) {
            return world.getBlockState(lvt_4_1_).is(AMTagRegistry.FROSTSTALKER_FEARS);
        }

    }
}
