package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.GeladaAIGroom;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EntityGeladaMonkey extends Animal implements IAnimatedEntity, IHerdPanic {

    public static final Animation ANIMATION_SWIPE_R = Animation.create(13);
    public static final Animation ANIMATION_SWIPE_L = Animation.create(13);
    public static final Animation ANIMATION_GROOM = Animation.create(35);
    public static final Animation ANIMATION_CHEST = Animation.create(35);
    private static final EntityDataAccessor<Boolean> LEADER = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_TARGET = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GRASS_TIME = SynchedEntityData.defineId(EntityGeladaMonkey.class, EntityDataSerializers.INT);
    public float prevSitProgress;
    public float sitProgress;
    public boolean isGrooming = false;
    public int groomerID = -1;
    private int animationTick;
    private Animation currentAnimation;
    private int sittingTime;
    private int maxSitTime;
    private int leaderFightTime;
    private HurtByTargetGoal hurtByTargetGoal = null;
    private NearestAttackableTargetGoal<EntityGeladaMonkey> leaderFightGoal = null;
    private int revengeCooldown = 0;
    private boolean hasSpedUp = false;

    protected EntityGeladaMonkey(EntityType type, Level lvl) {
        super(type, lvl);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.geladaMonkeySpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 18.0D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public int getMaxSpawnClusterSize() {
        return 10;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GELADA_MONKEY_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GELADA_MONKEY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GELADA_MONKEY_HURT;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5D, true) {
            protected double getAttackReachSqr(LivingEntity attackTarget) {
                return super.getAttackReachSqr(attackTarget) + 1.5D;
            }

            @Override
            public boolean canUse() {
                return super.canUse() && EntityGeladaMonkey.this.revengeCooldown <= 0;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && EntityGeladaMonkey.this.revengeCooldown <= 0;
            }
        });
        this.goalSelector.addGoal(2, new AIClearGrass());
        this.goalSelector.addGoal(3, new AnimalAIHerdPanic(this, 1.5D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT, Items.DEAD_BUSH), false));
        this.goalSelector.addGoal(7, new GeladaAIGroom(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1D, 120));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, hurtByTargetGoal = (new HurtByTargetGoal(this, EntityGeladaMonkey.class).setAlertOthers()));
        this.targetSelector.addGoal(2, leaderFightGoal = new NearestAttackableTargetGoal<EntityGeladaMonkey>(this, EntityGeladaMonkey.class, 70, false, false, (monkey) -> {
            return EntityGeladaMonkey.this.isLeader() && EntityGeladaMonkey.this.leaderFightTime == 0 && ((EntityGeladaMonkey) monkey).isLeader() && ((EntityGeladaMonkey) monkey).leaderFightTime == 0;
        }));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Leader", this.isLeader());
        compound.putInt("GrassTime", this.getClearGrassTime());
        compound.putInt("FightTime", this.leaderFightTime);
        compound.putBoolean("MonkeySitting", this.isSitting());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setLeader(compound.getBoolean("Leader"));
        this.setClearGrassTime(compound.getInt("GrassTime"));
        this.setSitting(compound.getBoolean("MonkeySitting"));
        this.leaderFightTime = compound.getInt("FightTime");
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.DEAD_BUSH;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEADER, Boolean.valueOf(false));
        this.entityData.define(SITTING, Boolean.valueOf(false));
        this.entityData.define(HAS_TARGET, Boolean.valueOf(false));
        this.entityData.define(GRASS_TIME, 0);
    }

    public boolean isLeader() {
        return this.entityData.get(LEADER).booleanValue() && !this.isBaby();
    }

    public void setLeader(boolean leader) {
        this.entityData.set(LEADER, leader);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isAggro() {
        return this.entityData.get(HAS_TARGET).booleanValue();
    }

    public void setAggro(boolean sit) {
        this.entityData.set(HAS_TARGET, Boolean.valueOf(sit));
    }

    public int getClearGrassTime() {
        return this.entityData.get(GRASS_TIME);
    }

    public void setClearGrassTime(int i) {
        this.entityData.set(GRASS_TIME, i);
    }

    public void tick() {
        super.tick();
        this.prevSitProgress = this.sitProgress;
        if (this.isSitting() && sitProgress < 5) {
            sitProgress += 1;
        }
        if (!this.isSitting() && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (!level.isClientSide && isSitting() && ++sittingTime > maxSitTime) {
            this.setSitting(false);
            sittingTime = 0;
            maxSitTime = 75 + random.nextInt(50);
        }
        if (!level.isClientSide && this.getDeltaMovement().lengthSqr() < 0.03D && this.getAnimation() == NO_ANIMATION && !this.isSitting() && random.nextInt(500) == 0) {
            sittingTime = 0;
            maxSitTime = 200 + random.nextInt(550);
            this.setSitting(true);
        }
        if (this.isSitting() && (this.getTarget() != null || this.isInLove())) {
            this.setSitting(false);
        }
        if (!level.isClientSide && this.getTarget() != null && (this.getAnimation() == ANIMATION_SWIPE_L || this.getAnimation() == ANIMATION_SWIPE_R) && this.getAnimationTick() == 7 && this.hasLineOfSight(this.getTarget()) && this.distanceTo(this.getTarget()) < this.getBbHeight() + this.getTarget().getBbHeight() + 1) {
            getTarget().knockback(0.4F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
            float dmg = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
            if (this.isLeader() && getTarget() instanceof EntityGeladaMonkey monkey) {
                if (monkey.isLeader()) {
                    monkey.setTarget(this);
                    monkey.leaderFightTime = this.leaderFightTime;
                    dmg = 0;
                }
            }
            this.getTarget().hurt(DamageSource.mobAttack(this), dmg);
        }
        if (!level.isClientSide) {
            if (this.getTarget() != null && this.getTarget().isAlive()) {
                this.setAggro(true);
                if (this.isLeader() && this.getTarget() instanceof EntityGeladaMonkey monkey) {
                    if (monkey.isLeader()) {
                        this.leaderFightTime++;
                    }
                    if(leaderFightTime < 10 && random.nextInt(5) == 0 && this.getAnimation() == NO_ANIMATION){
                        this.setAnimation(ANIMATION_CHEST);
                    }
                    if (Math.max(this.leaderFightTime, monkey.leaderFightTime) >= 250) {
                        this.resetAttackAI();
                        monkey.resetAttackAI();
                    }
                }

            } else {
                this.setAggro(false);
            }
            if (this.leaderFightTime < 0) {
                this.leaderFightTime++;
            }
        }
        if (isAggro() && !hasSpedUp) {
            hasSpedUp = true;
            this.setSprinting(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.31F);
        }
        if (!isAggro() && hasSpedUp) {
            hasSpedUp = false;
            this.setSprinting(false);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        }
        if (getClearGrassTime() > 0) {
            setClearGrassTime(getClearGrassTime() - 1);
        }
        if (getClearGrassTime() < 0) {
            setClearGrassTime(getClearGrassTime() + 1);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void resetAttackAI() {
        this.leaderFightTime = -500 - random.nextInt(2000);
        this.setTarget(null);
        this.setLastHurtByMob(null);
        if (leaderFightGoal != null) {
            leaderFightGoal.stop();
        }
        if (hurtByTargetGoal != null) {
            hurtByTargetGoal.stop();
        }
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            attackAnimation();
        }
        return true;
    }

    public float getGeladaScale() {
        return isBaby() ? 0.5F : isLeader() ? 1.15F : 1.0F;
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
        return new Animation[]{ANIMATION_SWIPE_R, ANIMATION_SWIPE_L, ANIMATION_GROOM, ANIMATION_CHEST};
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev) {
            Entity direct = source.getEntity();
            if (direct instanceof EntityGeladaMonkey) {
                double range = 15;
                int fleeTime = 100 + getRandom().nextInt(5);
                this.revengeCooldown = fleeTime;
                this.revengeCooldown = 10 + getRandom().nextInt(30);
            }
        }
        return prev;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if(item == Items.WHEAT && this.getClearGrassTime() == 0){
            this.usePlayerItem(player, hand, itemstack);
            this.eatGrassWithBuddies(3 + random.nextInt(2));
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel lvl, AgeableMob mob) {
        EntityGeladaMonkey baby = AMEntityRegistry.GELADA_MONKEY.get().create(lvl);
        baby.setLeader(random.nextInt(2) == 0);
        return baby;
    }

    public void eatGrassWithBuddies(int otherMonkies){
        int i = 300 + random.nextInt(300);
        this.setClearGrassTime(i);
        int monky = 0;
        for (EntityGeladaMonkey entity : this.level.getEntitiesOfClass(EntityGeladaMonkey.class, this.getBoundingBox().inflate(15F))) {
            if (monky < otherMonkies && entity.getId() != this.getId() && !entity.shouldStopBeingGroomed()) {
                monky++;
                entity.setClearGrassTime(i);
            }
        }

    }

    @Override
    public void onPanic() {

    }

    @Override
    public boolean canPanic() {
        return this.getLastHurtByMob() instanceof EntityGeladaMonkey && this.random.nextInt(3) == 0;
    }

    public void travel(Vec3 vec3d) {
        if (this.isSitting() || this.getAnimation() == ANIMATION_CHEST) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        if (spawnDataIn instanceof AgeableMob.AgeableMobGroupData) {
            AgeableMob.AgeableMobGroupData pack = (AgeableMob.AgeableMobGroupData) spawnDataIn;
            if (pack.getGroupSize() == 0 || pack.getGroupSize() > 4 && random.nextInt(2) == 0) {
                this.setLeader(true);
            }
        } else {
            this.setLeader(this.getRandom().nextInt(4) == 0);
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBeGroomed() {
        return this.groomerID == -1;
    }

    public boolean shouldStopBeingGroomed() {
        return this.getTarget() != null && this.getTarget().isAlive() || this.isInLove() || this.revengeCooldown > 0;
    }

    private class AIClearGrass extends Goal {

        private BlockPos target;

        public AIClearGrass() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (EntityGeladaMonkey.this.getClearGrassTime() > 0) {
                target = generateTarget();
                return target != null;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && EntityGeladaMonkey.this.level.getBlockState(target).is(AMTagRegistry.GELADA_MONKEY_GRASS);
        }

        public void tick() {
            EntityGeladaMonkey.this.setSitting(false);
            EntityGeladaMonkey.this.getNavigation().moveTo(target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F, 1.4F);
            if (EntityGeladaMonkey.this.distanceToSqr(Vec3.atCenterOf(target)) < 3.4F) {
                if(EntityGeladaMonkey.this.getAnimation() == NO_ANIMATION){
                    EntityGeladaMonkey.this.attackAnimation();
                }else if(EntityGeladaMonkey.this.getAnimationTick() > 7){
                    EntityGeladaMonkey.this.level.destroyBlock(target, true);
                }
            }
        }

        public BlockPos generateTarget() {
            BlockPos blockpos = null;
            Random random = new Random();
            int range = 7;
            for (int i = 0; i < 15; i++) {
                BlockPos blockpos1 = EntityGeladaMonkey.this.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                while (EntityGeladaMonkey.this.level.isEmptyBlock(blockpos1) && blockpos1.getY() > -63) {
                    blockpos1 = blockpos1.below();
                }
                if (EntityGeladaMonkey.this.level.getBlockState(blockpos1).is(AMTagRegistry.GELADA_MONKEY_GRASS)) {
                    blockpos = blockpos1;
                }
            }
            return blockpos;
        }
    }

    private void attackAnimation() {
        this.setAnimation(random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
    }
}
