package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityAnteater extends Animal implements NeutralMob, IAnimatedEntity, ITargetsDroppedItems {

    public static final Animation ANIMATION_SLASH_R = Animation.create(20);
    public static final Animation ANIMATION_TOUNGE_IDLE = Animation.create(10);
    public static final Animation ANIMATION_SLASH_L = Animation.create(20);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEANING_DOWN = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANT_ON_TONGUE = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANGER_TIME = SynchedEntityData.defineId(EntityAnteater.class, EntityDataSerializers.INT);
    public float prevStandProgress;
    public float standProgress;
    public float prevTongueProgress;
    public float tongueProgress;
    public float prevLeaningProgress;
    public float leaningProgress;
    public int eatAntCooldown = 0;
    public int ticksAntOnTongue = 0;
    private int animationTick;
    private Animation currentAnimation;
    private int maxStandTime = 75;
    private int standingTime = 0;
    private int antsEatenRecently = 0;
    private int heldItemTime;
    private UUID lastHurtBy;
    private static final UniformInt ANGRY_TIMER = TimeUtil.rangeOfSeconds(30, 60);

    protected EntityAnteater(EntityType type, Level world) {
        super(type, world);
        this.maxUpStep = 1;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 6D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public static boolean canAnteaterSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getRawBrightness(pos, 0) > 8;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.anteaterSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AIMelee());
        this.goalSelector.addGoal(3, new AnteaterAIRaidNest(this));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1D));
        this.goalSelector.addGoal(5, new AnimalAIRideParent(this, 1.25D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.2D, Ingredient.of(AMTagRegistry.INSECT_ITEMS), false));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 110, 1.0D, 10, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, false, 25, 16));
        this.targetSelector.addGoal(2, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(3, new AITargetAnts());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.getDirectEntity() != null && source.getDirectEntity() instanceof EntityLeafcutterAnt;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ANTEATER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ANTEATER_HURT.get();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Standing", this.isStanding());
        compound.putInt("AntCooldown", this.eatAntCooldown);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setStanding(compound.getBoolean("Standing"));
        this.eatAntCooldown = compound.getInt("AntCooldown");
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.LEAFCUTTER_ANT_PUPA.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STANDING, Boolean.FALSE);
        this.entityData.define(ANT_ON_TONGUE, Boolean.FALSE);
        this.entityData.define(LEANING_DOWN, Boolean.FALSE);
        this.entityData.define(ANGER_TIME, 0);
    }

    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(ANGER_TIME, time);
    }

    public UUID getPersistentAngerTarget() {
        return this.lastHurtBy;
    }

    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.lastHurtBy = target;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGRY_TIMER.sample(this.random));
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, standing);
    }

    public boolean hasAntOnTongue() {
        return this.entityData.get(ANT_ON_TONGUE);
    }

    public void setAntOnTongue(boolean standing) {
        this.entityData.set(ANT_ON_TONGUE, standing);
    }

    public boolean canCollideWith(Entity entity) {
        return !(entity instanceof EntityLeafcutterAnt) && super.canCollideWith(entity);
    }

    public void push(Entity entity) {
        if (!(entity instanceof EntityLeafcutterAnt)) {
            super.push(entity);
        }
    }

    public boolean isLeaning() {
        return this.entityData.get(LEANING_DOWN);
    }

    public void setLeaning(boolean leaning) {
        this.entityData.set(LEANING_DOWN, leaning);
    }

    protected boolean isImmobile() {
        return super.isImmobile();
    }

    protected void customServerAiStep() {
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), false);
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        final InteractionResult type = super.mobInteract(player, hand);
        final boolean isInsect = itemstack.is(AMTagRegistry.INSECT_ITEMS);
        if (isInsect) {
            final Item item = itemstack.getItem();
            final ItemStack rippedStack = itemstack.copy();
            rippedStack.setCount(1);
            this.stopBeingAngry();
            this.heal(4);
            this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
            if (item == AMItemRegistry.LEAFCUTTER_ANT_PUPA.get()) {
                return type;
            }
            this.usePlayerItem(player, hand, itemstack);
            return InteractionResult.SUCCESS;
        }
        return type;
    }


    public void tick() {
        super.tick();
        prevStandProgress = standProgress;
        prevTongueProgress = tongueProgress;
        prevLeaningProgress = leaningProgress;

        if (isStanding()) {
            if (standProgress < 5F)
                standProgress++;
        } else {
            if (standProgress > 0F)
                standProgress--;
        }

        final boolean isTongueOut = this.getAnimation() == ANIMATION_TOUNGE_IDLE;
        if (isTongueOut) {
            if (tongueProgress < 5F)
                tongueProgress++;
        } else {
            if (tongueProgress > 0F)
                tongueProgress--;
        }

        if (isLeaning()) {
            if (leaningProgress < 5F)
                leaningProgress++;
        } else {
            if (leaningProgress > 0F)
                leaningProgress--;
        }

        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + random.nextInt(50);
        }

        if (this.isPassenger() && this.getVehicle() instanceof final EntityAnteater mount) {
            if (this.isBaby()) {
                this.setYRot(mount.yBodyRot);
                this.yHeadRot = mount.yBodyRot;
                this.yBodyRot = mount.yBodyRot;
            } else {
                this.removeVehicle();
            }
        }

        if (eatAntCooldown > 0) {
            eatAntCooldown--;
        }
        if (antsEatenRecently >= 3 && eatAntCooldown <= 0) {
            this.resetAntCooldown();
        }
        if (ticksAntOnTongue > 10 && this.hasAntOnTongue()) {
            this.heal(6);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.setAntOnTongue(false);
        }
        if (this.hasAntOnTongue()) {
            ticksAntOnTongue++;
        } else {
            ticksAntOnTongue = 0;
        }
        if (!this.level().isClientSide && getTongueStickOut() > 0.6F && !this.hasAntOnTongue() && antsEatenRecently < 3) {
            EntityLeafcutterAnt closestAnt = null;
            for (EntityLeafcutterAnt entity : this.level().getEntitiesOfClass(EntityLeafcutterAnt.class, this.getBoundingBox().inflate(2.6F))) {
                if (closestAnt == null || entity.distanceTo(this) < closestAnt.distanceTo(this) && this.hasLineOfSight(entity)) {
                    closestAnt = entity;
                }
            }
            if (closestAnt != null) {
                closestAnt.remove(RemovalReason.KILLED);
                ticksAntOnTongue = 0;
                this.setAntOnTongue(true);
                antsEatenRecently++;
            }
        }
        if (!this.getMainHandItem().isEmpty()) {
            heldItemTime++;
            if (heldItemTime > 10 && getTongueStickOut() < 0.3F && canTargetItem(this.getMainHandItem())) {
                heldItemTime = 0;
                this.heal(4);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.gameEvent(GameEvent.EAT);
                if (this.getMainHandItem().hasCraftingRemainingItem()) {
                    this.spawnAtLocation(this.getMainHandItem().getCraftingRemainingItem());
                }
                this.stopBeingAngry();
                this.getMainHandItem().shrink(1);
            }
        } else {
            heldItemTime = 0;
        }

        if (!this.level().isClientSide) {
            if (getRandom().nextInt(300) == 0)
                this.setAnimation(ANIMATION_TOUNGE_IDLE);

            final LivingEntity attackTarget = this.getTarget();
            if (attackTarget != null) {
                if (distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 2) {
                    if (this.getAnimationTick() == 7) {
                        if (this.getAnimation() == ANIMATION_SLASH_L) {
                            doHurtTarget(attackTarget);
                            final float rot = getYRot() + 90;
                            attackTarget.knockback(0.5F, Mth.sin(rot * Maths.piDividedBy180), -Mth.cos(rot * Maths.piDividedBy180));
                        } else if (this.getAnimation() == ANIMATION_SLASH_R) {
                            doHurtTarget(attackTarget);
                            final float rot = getYRot() - 90;
                            attackTarget.knockback(0.5F, Mth.sin(rot * Maths.piDividedBy180), -Mth.cos(rot * Maths.piDividedBy180));
                        }
                    }
                }
            }
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void resetAntCooldown() {
        this.eatAntCooldown = 600 + random.nextInt(1000);
        this.antsEatenRecently = 0;
    }

    public void standFor(int time) {
        this.setStanding(true);
        this.maxStandTime = time;
    }

    public float getTongueStickOut() {
        if (this.tongueProgress > 0F) {
            final double tongueM = Math.min(Math.sin(this.tickCount * 0.15F), 0);
            return (float) -tongueM * (this.tongueProgress * 0.2F);
        }
        return 0.0F;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return AMEntityRegistry.ANTEATER.get().create(level());
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
    public boolean canTargetItem(ItemStack stack) {
        return !this.hasAntOnTongue() && stack.is(AMTagRegistry.INSECT_ITEMS);
    }

    @Override
    public void onGetItem(ItemEntity e) {
        final ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        this.setAnimation(ANIMATION_TOUNGE_IDLE);
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SLASH_L, ANIMATION_SLASH_R, ANIMATION_TOUNGE_IDLE};
    }

    private boolean shouldTargetAnts() {
        return !this.isAngry();
    }

    public boolean isPeter() {
        final String name = ChatFormatting.stripFormatting(this.getName().getString());
        if (name == null)
            return false;

        final String lowercaseName = name.toLowerCase(Locale.ROOT);
        return lowercaseName.contains("peter") || lowercaseName.contains("petr") || lowercaseName.contains("zot");
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn == null)
            spawnDataIn = new AgeableMob.AgeableMobGroupData(0.5F);

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private class AITargetAnts extends NearestAttackableTargetGoal {
        private static final Predicate<EntityLeafcutterAnt> QUEEN_ANT = (entity) -> !entity.isQueen();

        public AITargetAnts() {
            super(EntityAnteater.this, EntityLeafcutterAnt.class, 30, true, false, QUEEN_ANT);
        }

        @Override
        public boolean canUse() {
            return  EntityAnteater.this.shouldTargetAnts() && !EntityAnteater.this.isBaby() && !EntityAnteater.this.hasAntOnTongue() && !EntityAnteater.this.isStanding() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return EntityAnteater.this.shouldTargetAnts() && !EntityAnteater.this.hasAntOnTongue() && !EntityAnteater.this.isStanding() && super.canContinueToUse();
        }
    }

    private class AIMelee extends Goal {
        public AIMelee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityAnteater.this.getTarget() != null && EntityAnteater.this.getTarget().isAlive() && !EntityAnteater.this.isBaby();
        }

        public void tick() {
            final LivingEntity enemy = EntityAnteater.this.getTarget();
            final double attackReachSqr = this.getAttackReachSqr(enemy);
            final double distToEnemySqr = EntityAnteater.this.distanceTo(enemy);
            EntityAnteater.this.lookAt(enemy, 100, 5);
            if (enemy instanceof EntityLeafcutterAnt) {
                if (distToEnemySqr <= attackReachSqr + 1.5F) {
                    EntityAnteater.this.setAnimation(ANIMATION_TOUNGE_IDLE);
                } else {
                    EntityAnteater.this.lookAt(enemy, 5, 5);
                }
                EntityAnteater.this.getNavigation().moveTo(enemy, 1.0D);
            } else {
                if (distToEnemySqr <= attackReachSqr) {
                    EntityAnteater.this.getNavigation().moveTo(enemy, 1.0D);
                    EntityAnteater.this.setAnimation(EntityAnteater.this.getRandom().nextBoolean() ? ANIMATION_SLASH_L : ANIMATION_SLASH_R);
                }
                final double x = enemy.getX() - EntityAnteater.this.getX();
                final double z = enemy.getZ() - EntityAnteater.this.getZ();
//                double d3 = (double)Mth.sqrt((float) (x * x + z * z));
                final float f = (float) (Mth.atan2(z, x) * Maths.oneEightyDividedByFloatPi) - 90.0F;
                EntityAnteater.this.setYRot(f);
                EntityAnteater.this.yBodyRot = f;
                EntityAnteater.this.setStanding(true);
            }
        }

        public void stop() {
            EntityAnteater.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 2.0F + attackTarget.getBbWidth();
        }
    }

}
