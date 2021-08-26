package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockCrocodileEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

public class EntityCrocodile extends TameableEntity implements IAnimatedEntity, ISemiAquatic {

    public static final Animation ANIMATION_LUNGE = Animation.create(23);
    public static final Animation ANIMATION_DEATHROLL = Animation.create(40);
    public static final Predicate<Entity> NOT_CREEPER = (entity) -> {
        return entity.isAlive() && !(entity instanceof CreeperEntity);
    };
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DESERT = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_DIGGING = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> STUN_TICKS = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.VARINT);
    public float groundProgress = 0;
    public float prevGroundProgress = 0;
    public float swimProgress = 0;
    public float prevSwimProgress = 0;
    public float baskingProgress = 0;
    public float prevBaskingProgress = 0;
    public float grabProgress = 0;
    public float prevGrabProgress = 0;
    public int baskingType = 0;
    public boolean forcedSit = false;
    private int baskingTimer = 0;
    private int swimTimer = -1000;
    private int ticksSinceInWater = 0;
    private int passengerTimer = 0;
    private boolean isLandNavigator;
    private boolean hasSpedUp = false;
    private int animationTick;
    private Animation currentAnimation;

    protected EntityCrocodile(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        switchNavigator(false);
        this.baskingType = rand.nextInt(1);
    }

    public static boolean canCrocodileSpawn(EntityType type, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.CROCODILE_SPAWNS).contains(worldIn.getBlockState(pos.down()).getBlock());
        return spawnBlock && pos.getY() < worldIn.getSeaLevel() + 4;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 15).createMutableAttribute(Attributes.ARMOR, 8.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 10.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.4F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.crocSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public int getMaxSpawnedInChunk() {
        return 2;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }

    protected void onGrowingAdult() {
        super.onGrowingAdult();
        if (!this.isChild() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.entityDropItem(new ItemStack(AMItemRegistry.CROCODILE_SCUTE, rand.nextInt(1) + 1), 1);
        }
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setDesert(this.isBiomeDesert(worldIn, this.getPosition()));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private boolean isBiomeDesert(IWorld worldIn, BlockPos position) {
        RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, worldIn.getBiome(position).getRegistryName());
        boolean sand = BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.SANDY);
        return sand;
    }

    protected SoundEvent getAmbientSound() {
        return isChild() ? AMSoundRegistry.CROCODILE_BABY : AMSoundRegistry.CROCODILE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CROCODILE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CROCODILE_HURT;
    }


    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("CrocodileSitting", this.isSitting());
        compound.putBoolean("Desert", this.isDesert());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("BaskingStyle", this.baskingType);
        compound.putInt("BaskingTimer", this.baskingTimer);
        compound.putInt("SwimTimer", this.swimTimer);
        compound.putInt("StunTimer", this.getStunTicks());
        compound.putBoolean("HasEgg", this.hasEgg());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("CrocodileSitting"));
        this.setDesert(compound.getBoolean("Desert"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.baskingType = compound.getInt("BaskingStyle");
        this.baskingTimer = compound.getInt("BaskingTimer");
        this.swimTimer = compound.getInt("SwimTimer");
        this.setHasEgg(compound.getBoolean("HasEgg"));
        this.setStunTicks(compound.getInt("StunTimer"));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            PathNavigator prevNav = this.navigator;
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new AquaticMoveController(this, 1F);
            PathNavigator prevNav = this.navigator;
            this.navigator = new SemiAquaticPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(DESERT, Boolean.valueOf(false));
        this.dataManager.register(HAS_EGG, Boolean.valueOf(false));
        this.dataManager.register(IS_DIGGING, Boolean.valueOf(false));
        this.dataManager.register(CLIMBING, (byte) 0);
        this.dataManager.register(STUN_TICKS, 0);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataManager.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.dataManager.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }
        this.dataManager.set(CLIMBING, b0);
    }

    public void tick() {
        super.tick();
        this.prevGroundProgress = groundProgress;
        this.prevSwimProgress = swimProgress;
        this.prevBaskingProgress = baskingProgress;
        this.prevGrabProgress = grabProgress;
        boolean ground = !this.isInWater();
        boolean groundAnimate = !this.isInWater();
        boolean basking = groundAnimate && this.isSitting();
        boolean grabbing = !this.getPassengers().isEmpty();
        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (groundAnimate && this.groundProgress < 10F) {
            this.groundProgress++;
        }
        if (!groundAnimate && this.groundProgress > 0F) {
            this.groundProgress--;
        }
        if (!groundAnimate && this.swimProgress < 10F) {
            this.swimProgress++;
        }
        if (groundAnimate && this.swimProgress > 0F) {
            this.swimProgress--;
        }
        if (basking && this.baskingProgress < 10F) {
            this.baskingProgress++;
        }
        if (!basking && this.baskingProgress > 0F) {
            this.baskingProgress--;
        }
        if (grabbing && this.grabProgress < 10F) {
            this.grabProgress++;
        }
        if (!grabbing && this.grabProgress > 0F) {
            this.grabProgress--;
        }
        if (this.getAttackTarget() != null && !hasSpedUp) {
            hasSpedUp = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28F);
        }
        if (this.getAttackTarget() == null && hasSpedUp) {
            hasSpedUp = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        }
        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
        }
        if (baskingTimer < 0) {
            baskingTimer++;
        }
        if (passengerTimer > 0 && this.getPassengers().isEmpty()) {
            passengerTimer = 0;
        }
        if (!world.isRemote) {
            if (isInWater()) {
                swimTimer++;
                ticksSinceInWater = 0;
            } else {
                ticksSinceInWater++;
                swimTimer--;
            }
        }
        if (!world.isRemote && !this.isInWater() && this.isOnGround()) {
            if (!this.isTamed()) {
                if (!this.isSitting() && baskingTimer == 0 && this.getAttackTarget() == null && this.getNavigator().noPath()) {
                    this.setSitting(true);
                    this.baskingTimer = 1000 + rand.nextInt(750);
                }
                if (this.isSitting() && (baskingTimer <= 0 || this.getAttackTarget() != null || swimTimer < -1000)) {
                    this.setSitting(false);
                    this.baskingTimer = -2000 - rand.nextInt(750);
                }
                if (this.isSitting() && baskingTimer > 0) {
                    baskingTimer--;
                }
            }
        }
        if (!world.isRemote && this.getStunTicks() == 0 && this.isAlive() && this.getAttackTarget() != null && this.getAnimation() == ANIMATION_LUNGE  && (world.getDifficulty() != Difficulty.PEACEFUL || !(this.getAttackTarget() instanceof PlayerEntity)) && this.getAnimationTick() > 5 && this.getAnimationTick() < 9) {
            float f1 = this.rotationYaw * ((float) Math.PI / 180F);
            this.setMotion(this.getMotion().add(-MathHelper.sin(f1) * 0.02F, 0.0D, MathHelper.cos(f1) * 0.02F));
            if (this.getDistance(this.getAttackTarget()) < 3.5F && this.canEntityBeSeen(this.getAttackTarget())) {
                boolean flag = this.getAttackTarget().isActiveItemStackBlocking();
                if (!flag) {
                    if (this.getAttackTarget().getWidth() < this.getWidth() && this.getPassengers().isEmpty() && !this.getAttackTarget().isSneaking()) {
                        this.getAttackTarget().startRiding(this, true);
                    }
                }
                if (flag) {
                    if (this.getAttackTarget() instanceof PlayerEntity) {
                        this.damageShieldFor(((PlayerEntity) this.getAttackTarget()), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                    }
                    if (this.getStunTicks() == 0) {
                        this.setStunTicks(25 + rand.nextInt(20));
                    }
                } else {
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
                this.playSound(AMSoundRegistry.CROCODILE_BITE, this.getSoundVolume(), this.getSoundPitch());

            }
        }
        if (!world.isRemote && this.isAlive() && this.getAttackTarget() != null && this.isInWater() && (world.getDifficulty() != Difficulty.PEACEFUL || !(this.getAttackTarget() instanceof PlayerEntity))) {
            if (this.getAttackTarget().getRidingEntity() != null && this.getAttackTarget().getRidingEntity() == this) {
                if (this.getAnimation() == NO_ANIMATION) {
                    this.setAnimation(ANIMATION_DEATHROLL);
                }
                if (this.getAnimation() == ANIMATION_DEATHROLL && this.getAnimationTick() % 10 == 0 && this.getDistance(this.getAttackTarget()) < 5D) {
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), 2);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_DEATHROLL) {
            this.getNavigator().clearPath();
        }
        if (this.isInLove() && this.getAttackTarget() != null) {
            this.setAttackTarget(null);
        }
        if (this.getStunTicks() > 0) {
            this.setStunTicks(this.getStunTicks() - 1);
            if (world.isRemote) {
                float angle = (0.01745329251F * this.renderYawOffset);
                double headX = 1.5F * getRenderScale() * MathHelper.sin((float) (Math.PI + angle));
                double headZ = 1.5F * getRenderScale() * MathHelper.cos(angle);
                for (int i = 0; i < 5; i++) {
                    float innerAngle = (0.01745329251F * (this.renderYawOffset + ticksExisted * 5) * (i + 1));
                    double extraX = 0.5F * MathHelper.sin((float) (Math.PI + innerAngle));
                    double extraZ = 0.5F * MathHelper.cos(innerAngle);
                    world.addParticle(ParticleTypes.CRIT, true, this.getPosX() + headX + extraX, this.getPosYEye() + 0.5F, this.getPosZ() + headZ + extraZ, 0, 0, 0);
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    protected void damageShieldFor(PlayerEntity holder, float damage) {
        if (holder.getActiveItemStack().isShield(holder)) {
            if (!this.world.isRemote) {
                holder.addStat(Stats.ITEM_USED.get(holder.getActiveItemStack().getItem()));
            }

            if (damage >= 3.0F) {
                int i = 1 + MathHelper.floor(damage);
                Hand hand = holder.getActiveHand();
                holder.getActiveItemStack().damageItem(i, holder, (p_213833_1_) -> {
                    p_213833_1_.sendBreakAnimation(hand);
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(holder, holder.getActiveItemStack(), hand);
                });
                if (holder.getActiveItemStack().isEmpty()) {
                    if (hand == Hand.MAIN_HAND) {
                        holder.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                    } else {
                        holder.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                    }
                    holder.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
                }
            }

        }
    }

    protected boolean isMovementBlocked() {
        return super.isMovementBlocked() || this.getStunTicks() > 0;
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public boolean isOnSameTeam(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TameableEntity) {
                return ((TameableEntity) entityIn).isOwner(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isOnSameTeam(entityIn);
            }
        }

        return super.isOnSameTeam(entityIn);
    }

    public void updatePassenger(Entity passenger) {
        if (!this.getPassengers().isEmpty()) {
            this.renderYawOffset = MathHelper.wrapDegrees(this.rotationYaw - 180F);
        }
        if (this.isPassenger(passenger)) {
            float radius = 2F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + 0.1F, this.getPosZ() + extraZ);
            passengerTimer++;
            if (this.isAlive() && passengerTimer > 0 && passengerTimer % 40 == 0) {
                passenger.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
            }
        }
    }

    public boolean isOnLadder() {
        return isInWater() && this.isBesideClimbableBlock();
    }

    public boolean isPushedByWater() {
        return false;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION && this.getPassengers().isEmpty() && this.getStunTicks() == 0) {
            this.setAnimation(ANIMATION_LUNGE);
        }
        return true;
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return super.getBlockPathWeight(pos, worldIn);

    }

    public boolean shouldLeaveWater() {
        if (!this.getPassengers().isEmpty()) {
            return false;
        }
        if (this.getAttackTarget() != null && !this.getAttackTarget().isInWater()) {
            return true;
        }
        return swimTimer > 600;
    }

    @Override
    public boolean shouldStopMoving() {
        return this.getAnimation() == ANIMATION_DEATHROLL;
    }

    @Override
    public int getWaterSearchRange() {
        return this.getPassengers().isEmpty() ? 15 : 45;
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isDesert() {
        return this.dataManager.get(DESERT).booleanValue();
    }

    public void setDesert(boolean desert) {
        this.dataManager.set(DESERT, Boolean.valueOf(desert));
    }

    public boolean hasEgg() {
        return this.dataManager.get(HAS_EGG);
    }

    private void setHasEgg(boolean hasEgg) {
        this.dataManager.set(HAS_EGG, hasEgg);
    }

    public boolean isDigging() {
        return this.dataManager.get(IS_DIGGING);
    }

    private void setDigging(boolean isDigging) {
        this.dataManager.set(IS_DIGGING, isDigging);
    }

    public int getStunTicks() {
        return this.dataManager.get(STUN_TICKS);
    }

    private void setStunTicks(int stun) {
        this.dataManager.set(STUN_TICKS, stun);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new MateGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new LayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new BreatheAirGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new CrocodileAIMelee(this, 1, true));
        this.goalSelector.addGoal(5, new CrocodileAIRandomSwimming(this, 1.0D, 7));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, PlayerEntity.class, 80, false, true, null) {
            public boolean shouldExecute() {
                return !isChild() && !isTamed() && world.getDifficulty() != Difficulty.PEACEFUL && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.CROCODILE_TARGETS))) {
            public boolean shouldExecute() {
                return !isChild() && !isTamed() && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(6, new EntityAINearestTarget3D(this, MonsterEntity.class, 180, false, true, NOT_CREEPER) {
            public boolean shouldExecute() {
                return !isChild() && isTamed() && super.shouldExecute();
            }
        });
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.setSitting(false);
            if (entity != null && this.isTamed() && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.CROCODILE.create(p_241840_1_);
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (itemstack.getItem() == Items.NAME_TAG) {
            return super.getEntityInteractionResult(player, hand);
        }
        if (isTamed() && item.isFood() && item.getFood() != null && item.getFood().isMeat() && this.getHealth() < this.getMaxHealth()) {
            this.consumeItemFromStack(player, itemstack);
            this.heal(10);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
            return ActionResultType.SUCCESS;
        }
        ActionResultType type = super.getEntityInteractionResult(player, hand);
        if (type != ActionResultType.SUCCESS && isTamed() && isOwner(player) && !isBreedingItem(itemstack)) {
            if (this.isSitting()) {
                this.forcedSit = false;
                this.setSitting(false);
                return ActionResultType.SUCCESS;
            } else {
                this.forcedSit = true;
                this.setSitting(true);
                return ActionResultType.SUCCESS;
            }
        }
        return type;
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (!this.isChild()) {
            super.setAttackTarget(entitylivingbaseIn);
        }
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH;
    }

    @Override
    public boolean shouldEnterWater() {
        if (!this.getPassengers().isEmpty()) {
            return true;
        }
        return this.getAttackTarget() == null && !this.isSitting() && this.baskingTimer <= 0 && !shouldLeaveWater() && swimTimer <= -1000;
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
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_LUNGE, ANIMATION_DEATHROLL};
    }

    static class MateGoal extends BreedGoal {
        private final EntityCrocodile turtle;

        MateGoal(EntityCrocodile turtle, double speedIn) {
            super(turtle, speedIn);
            this.turtle = turtle;
        }

        public boolean shouldExecute() {
            return super.shouldExecute() && !this.turtle.hasEgg();
        }

        protected void spawnBaby() {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.targetMate.getLoveCause() != null) {
                serverplayerentity = this.targetMate.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.addStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, this.animal);
            }

            this.turtle.setHasEgg(true);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            Random random = this.animal.getRNG();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), random.nextInt(7) + 1));
            }

        }
    }

    static class LayEggGoal extends MoveToBlockGoal {
        private final EntityCrocodile turtle;
        private int digTime;

        LayEggGoal(EntityCrocodile turtle, double speedIn) {
            super(turtle, speedIn, 16);
            this.turtle = turtle;
        }

        public void resetTask() {
            digTime = 0;
        }

        public boolean shouldExecute() {
            return this.turtle.hasEgg() && super.shouldExecute();
        }

        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && this.turtle.hasEgg();
        }

        public void tick() {
            super.tick();
            BlockPos blockpos = this.turtle.getPosition();
            turtle.setSitting(false);
            turtle.baskingTimer = -100;
            if (!this.turtle.isInWater() && this.getIsAboveDestination()) {
                World world = this.turtle.world;
                world.playSound(null, blockpos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.rand.nextFloat() * 0.2F);
                world.setBlockState(this.destinationBlock.up(), AMBlockRegistry.CROCODILE_EGG.getDefaultState().with(BlockCrocodileEgg.EGGS, Integer.valueOf(this.turtle.rand.nextInt(1) + 1)), 3);
                this.turtle.setHasEgg(false);
                this.turtle.setDigging(false);
                this.turtle.setInLove(600);
            }

        }

        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return worldIn.isAirBlock(pos.up()) && BlockCrocodileEgg.isProperHabitat(worldIn, pos);
        }
    }
}
