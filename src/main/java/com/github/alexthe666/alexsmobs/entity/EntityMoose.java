package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.MooseAIJostle;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;

public class EntityMoose extends Animal implements IAnimatedEntity {

    public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
    public static final Animation ANIMATION_ATTACK = Animation.create(15);
    private static final int DAY = 24000;
    private static final EntityDataAccessor<Boolean> ANTLERED = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> JOSTLING = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> JOSTLE_ANGLE = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> JOSTLER_UUID = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> SNOWY = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.BOOLEAN);
    public float prevJostleAngle;
    public float prevJostleProgress;
    public float jostleProgress;
    public boolean jostleDirection;
    public int jostleTimer = 0;
    public boolean instantlyTriggerJostleAI = false;
    public int jostleCooldown = 100 + random.nextInt(40);
    public int timeUntilAntlerDrop = 7 * DAY + this.random.nextInt(3) * DAY;
    private int animationTick;
    private Animation currentAnimation;
    private int snowTimer = 0;
    private boolean permSnow = false;

    protected EntityMoose(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public static boolean canMooseSpawn(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random randomIn) {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        return (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.SNOW)) || blockstate.is(Blocks.SNOW_BLOCK) && worldIn.getRawBrightness(pos, 0) > 8;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 55D).add(Attributes.ATTACK_DAMAGE, 7.5D).add(Attributes.MOVEMENT_SPEED, 0.25F).add(Attributes.KNOCKBACK_RESISTANCE, 0.5F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mooseSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MooseAIJostle(this));
        this.goalSelector.addGoal(3, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, Ingredient.of(Items.DANDELION), false));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 120, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)));
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 6) {
            for (int lvt_3_1_ = 0; lvt_3_1_ < 7; ++lvt_3_1_) {
                double lvt_4_1_ = this.random.nextGaussian() * 0.02D;
                double lvt_6_1_ = this.random.nextGaussian() * 0.02D;
                double lvt_8_1_ = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), lvt_4_1_, lvt_6_1_, lvt_8_1_);
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    public boolean isFood(ItemStack stack) {
        if (stack.getItem() == Items.DANDELION && !this.isInLove() && this.getAge() == 0) {
            if (this.getRandom().nextInt(5) == 0) {
                return true;
            } else {
                this.level.broadcastEntityEvent(this, (byte) 6);
                return false;
            }
        }
        return false;
    }

    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (!this.isBaby()) {
            super.setTarget(entitylivingbaseIn);
        }
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANTLERED, Boolean.valueOf(true));
        this.entityData.define(JOSTLING, Boolean.valueOf(false));
        this.entityData.define(SNOWY, Boolean.valueOf(false));
        this.entityData.define(JOSTLE_ANGLE, 0F);
        this.entityData.define(JOSTLER_UUID, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSnowy(compound.getBoolean("Snowy"));
        if (compound.contains("AntlerTime")) {
            this.timeUntilAntlerDrop = compound.getInt("AntlerTime");
        }
        this.setAntlered(compound.getBoolean("Antlered"));
        this.jostleCooldown = compound.getInt("JostlingCooldown");
        this.permSnow = compound.getBoolean("SnowPerm");

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Snowy", this.isSnowy());
        compound.putBoolean("SnowPerm", this.permSnow);
        compound.putInt("AntlerTime", this.timeUntilAntlerDrop);
        compound.putBoolean("Antlered", this.isAntlered());
        compound.putInt("JostlingCooldown", this.jostleCooldown);
    }

    public void tick() {
        super.tick();
        prevJostleProgress = jostleProgress;
        prevJostleAngle = this.getJostleAngle();
        if (this.isJostling() && jostleProgress < 5F) {
            jostleProgress++;
        }
        if (!this.isJostling() && jostleProgress > 0F) {
            jostleProgress--;
        }
        if (jostleCooldown > 0) {
            jostleCooldown--;
        }
        if (!level.isClientSide && this.getAnimation() == NO_ANIMATION && getRandom().nextInt(120) == 0 && (this.getTarget() == null || !this.getTarget().isAlive()) && !this.isJostling() && this.getJostlingPartnerUUID() == null) {
            if (level.getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && getRandom().nextInt(3) == 0) {
                this.setAnimation(ANIMATION_EAT_GRASS);
            }
        }
        if (timeUntilAntlerDrop > 0) {
            timeUntilAntlerDrop--;
        }
        if (timeUntilAntlerDrop == 0) {
            if (this.isAntlered()) {
                this.setAntlered(false);
                this.spawnAtLocation(new ItemStack(AMItemRegistry.MOOSE_ANTLER.get()));
                timeUntilAntlerDrop = 2 * DAY + this.random.nextInt(3) * DAY;
            } else {
                this.setAntlered(true);
                timeUntilAntlerDrop = 7 * DAY + this.random.nextInt(3) * DAY;
            }
        }
        if (this.getTarget() != null && this.getTarget().isAlive()) {
            if (this.isJostling()) {
                this.setJostling(false);
            }
            if (!level.isClientSide && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 8) {
                float dmg = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                if (!isAntlered()) {
                    dmg = 3;
                }
                if (this.getTarget() instanceof Wolf || this.getTarget() instanceof EntityOrca) {
                    dmg = 2;
                }
                getTarget().knockback(1F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
                this.getTarget().hurt(DamageSource.mobAttack(this), dmg);
            }
        }
        if(snowTimer > 0){
            snowTimer--;
        }
        if (snowTimer == 0 && !level.isClientSide) {
            snowTimer = 200 + random.nextInt(400);
            if(this.isSnowy()){
                if(!permSnow){
                    if (!this.level.isClientSide || this.getRemainingFireTicks() > 0 || this.isInWaterOrBubble() || !EntityGrizzlyBear.isSnowingAt(level, this.blockPosition().above())) {
                        this.setSnowy(false);
                    }
                }
            }else{
                if (!this.level.isClientSide && EntityGrizzlyBear.isSnowingAt(level, this.blockPosition())) {
                    this.setSnowy(true);
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            if (entity instanceof EntityOrca || entity instanceof Wolf) {
                amount = (amount + 1.0F) * 3.0F;
            }
            return super.hurt(source, amount);
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.MOOSE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MOOSE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MOOSE_HURT;
    }


    public boolean isAntlered() {
        return this.entityData.get(ANTLERED).booleanValue();
    }

    public void setAntlered(boolean anters) {
        this.entityData.set(ANTLERED, anters);
    }

    public boolean isJostling() {
        return this.entityData.get(JOSTLING).booleanValue();
    }

    public void setJostling(boolean jostle) {
        this.entityData.set(JOSTLING, jostle);
    }

    public float getJostleAngle() {
        return this.entityData.get(JOSTLE_ANGLE);
    }

    public void setJostleAngle(float scale) {
        this.entityData.set(JOSTLE_ANGLE, scale);
    }

    @Nullable
    public UUID getJostlingPartnerUUID() {
        return this.entityData.get(JOSTLER_UUID).orElse(null);
    }

    public void setJostlingPartnerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(JOSTLER_UUID, Optional.ofNullable(uniqueId));
    }

    public boolean isSnowy() {
        return this.entityData.get(SNOWY).booleanValue();
    }

    public void setSnowy(boolean honeyed) {
        this.entityData.set(SNOWY, Boolean.valueOf(honeyed));
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (item == Items.SNOW && !this.isSnowy() && !level.isClientSide) {
            this.usePlayerItem(player, hand, itemstack);
            this.permSnow = true;
            this.setSnowy(true);
            this.playSound(SoundEvents.SNOW_PLACE, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        if (item instanceof ShovelItem && this.isSnowy() && !level.isClientSide) {
            this.permSnow = false;
            if (!player.isCreative()) {
                itemstack.hurt(1, this.getRandom(), player instanceof ServerPlayer ? (ServerPlayer) player : null);
            }
            this.setSnowy(false);
            this.playSound(SoundEvents.SNOW_BREAK, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    @Nullable
    public Entity getJostlingPartner() {
        UUID id = getJostlingPartnerUUID();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public void setJostlingPartner(@Nullable Entity jostlingPartner) {
        if (jostlingPartner == null) {
            this.setJostlingPartnerUUID(null);
        } else {
            this.setJostlingPartnerUUID(jostlingPartner.getUUID());
        }
    }

    public void pushBackJostling(EntityMoose entityMoose, float strength) {
        applyKnockbackFromMoose(strength, entityMoose.getX() - this.getX(), entityMoose.getZ() - this.getZ());
    }

    private void applyKnockbackFromMoose(float strength, double ratioX, double ratioZ) {
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(this, strength, ratioX, ratioZ);
        if (event.isCanceled()) return;
        strength = event.getStrength();
        ratioX = event.getRatioX();
        ratioZ = event.getRatioZ();
        if (!(strength <= 0.0F)) {
            this.hasImpulse = true;
            Vec3 vector3d = this.getDeltaMovement();
            Vec3 vector3d1 = (new Vec3(ratioX, 0.0D, ratioZ)).normalize().scale(strength);
            this.setDeltaMovement(vector3d.x / 2.0D - vector3d1.x, 0.3F, vector3d.z / 2.0D - vector3d1.z);
        }
    }


    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
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
        return new Animation[]{ANIMATION_ATTACK, ANIMATION_EAT_GRASS};
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.MOOSE.get().create(serverWorld);
    }

    public boolean canJostleWith(EntityMoose moose) {
        return !moose.isJostling() && moose.isAntlered() && moose.getAnimation() == NO_ANIMATION && !moose.isBaby() && moose.getJostlingPartnerUUID() == null && moose.jostleCooldown == 0;
    }

    public void playJostleSound() {
        this.playSound(AMSoundRegistry.MOOSE_JOSTLE, this.getVoicePitch(), this.getSoundVolume());
    }

}
