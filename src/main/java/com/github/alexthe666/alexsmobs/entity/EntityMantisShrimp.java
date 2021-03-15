package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class EntityMantisShrimp extends TameableEntity implements ISemiAquatic, IFollower {

    private static final DataParameter<Float> RIGHT_EYE_PITCH = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> RIGHT_EYE_YAW = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> LEFT_EYE_PITCH = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> LEFT_EYE_YAW = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> PUNCH_TICK = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MOISTNESS = EntityDataManager.createKey(EntityMantisShrimp.class, DataSerializers.VARINT);
    public float prevRightPitch;
    public float prevRightYaw;
    public float prevLeftPitch;
    public float prevLeftYaw;
    public float prevInWaterProgress;
    public float inWaterProgress;
    public float prevPunchProgress;
    public float punchProgress;
    private int leftLookCooldown = 0;
    private int rightLookCooldown = 0;
    private float targetRightPitch;
    private float targetRightYaw;
    private float targetLeftPitch;
    private float targetLeftYaw;
    private boolean isLandNavigator;
    private int fishFeedings;
    private int moistureAttackTime = 0;

    protected EntityMantisShrimp(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        switchNavigator(false);
        this.stepHeight = 1;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MANTIS_SHRIMP_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MANTIS_SHRIMP_HURT;
    }


    public static boolean canMantisShrimpSpawn(EntityType type, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockPos downPos = pos;
        while (downPos.getY() > 1 && !worldIn.getFluidState(downPos).isEmpty()) {
            downPos = downPos.down();
        }
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.MANTIS_SHRIMP_SPAWNS).contains(worldIn.getBlockState(downPos).getBlock());
        return spawnBlock && downPos.getY() < worldIn.getSeaLevel() + 1;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.1D).createMutableAttribute(Attributes.ARMOR, 8D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public boolean canDespawn(double distanceToClosestPlayer) {
        return !this.isTamed();
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mantisShrimpSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MantisShrimpAIBreakBlocks(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new FollowOwner(this, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2F, false));
        this.goalSelector.addGoal(4, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(4, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.0D, Ingredient.fromItems(Items.TROPICAL_FISH, AMItemRegistry.LOBSTER_TAIL, AMItemRegistry.COOKED_LOBSTER_TAIL), false));
        this.goalSelector.addGoal(7, new AnimalAIRandomSwimming(this, 1.0D, 30));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 120, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.MANTIS_SHRIMP_TARGETS))) {
            public boolean shouldExecute() {
                return EntityMantisShrimp.this.getCommand() != 3 && !EntityMantisShrimp.this.isSitting() && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new AnimalSwimMoveControllerSink(this, 1F, 1F);
            this.navigator = new SemiAquaticPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    public void travel(Vector3d travelVector) {
        if (this.isSitting()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            travelVector = Vector3d.ZERO;
            super.travel(travelVector);
            return;
        }
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
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

    protected void registerData() {
        super.registerData();
        this.dataManager.register(RIGHT_EYE_PITCH, 0F);
        this.dataManager.register(RIGHT_EYE_YAW, 0F);
        this.dataManager.register(LEFT_EYE_PITCH, 0F);
        this.dataManager.register(LEFT_EYE_YAW, 0F);
        this.dataManager.register(PUNCH_TICK, 0);
        this.dataManager.register(COMMAND, Integer.valueOf(0));
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(MOISTNESS, Integer.valueOf(60000));
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return isTamed() && (item == AMItemRegistry.LOBSTER_TAIL || item == AMItemRegistry.COOKED_LOBSTER_TAIL);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.punch();
        return true;
    }

    public void punch() {
        this.dataManager.set(PUNCH_TICK, 4);
    }

    public float getEyeYaw(boolean left) {
        return dataManager.get(left ? LEFT_EYE_YAW : RIGHT_EYE_YAW);
    }

    public float getEyePitch(boolean left) {
        return dataManager.get(left ? LEFT_EYE_PITCH : RIGHT_EYE_PITCH);
    }

    public void setEyePitch(boolean left, float pitch) {
        dataManager.set(left ? LEFT_EYE_PITCH : RIGHT_EYE_PITCH, pitch);
    }

    public void setEyeYaw(boolean left, float yaw) {
        dataManager.set(left ? LEFT_EYE_YAW : RIGHT_EYE_YAW, yaw);
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int command) {
        this.dataManager.set(VARIANT, Integer.valueOf(command));
    }

    public int getMoistness() {
        return this.dataManager.get(MOISTNESS);
    }

    public void setMoistness(int p_211137_1_) {
        this.dataManager.set(MOISTNESS, p_211137_1_);
    }

    public void tick() {
        super.tick();
        if (this.isAIDisabled()) {
            this.setAir(this.getMaxAir());
        } else {
            if (this.isInWaterRainOrBubbleColumn() || this.getHeldItemMainhand().getItem() == Items.WATER_BUCKET) {
                this.setMoistness(60000);
            } else {
                this.setMoistness(this.getMoistness() - 1);
                if (this.getMoistness() <= 0 && moistureAttackTime-- <= 0) {
                    this.setCommand(0);
                    this.setSitting(false);
                    this.attackEntityFrom(DamageSource.DRYOUT, rand.nextInt(2) == 0 ? 1.0F : 0F);
                    moistureAttackTime = 20;
                }
            }
        }
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if (!isTamed() && item == Items.TROPICAL_FISH) {
            this.consumeItemFromStack(player, itemstack);
            this.playSound(SoundEvents.ENTITY_STRIDER_EAT, this.getSoundVolume(), this.getSoundPitch());
            fishFeedings++;
            if (fishFeedings > 10 && getRNG().nextInt(6) == 0 || fishFeedings > 30) {
                this.setTamedBy(player);
                this.world.setEntityState(this, (byte) 7);
            } else {
                this.world.setEntityState(this, (byte) 6);
            }
            return ActionResultType.SUCCESS;
        }
        if (isTamed() && item.isIn(ItemTags.FISHES)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.consumeItemFromStack(player, itemstack);
                this.playSound(SoundEvents.ENTITY_STRIDER_EAT, this.getSoundVolume(), this.getSoundPitch());
                this.heal(5);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;

        }
        if (type != ActionResultType.SUCCESS && isTamed() && isOwner(player)) {
            if (player.isSneaking()) {
                if (this.getHeldItemMainhand().isEmpty()) {
                    ItemStack cop = itemstack.copy();
                    cop.setCount(1);
                    this.setHeldItem(Hand.MAIN_HAND, cop);
                    itemstack.shrink(1);
                    return ActionResultType.SUCCESS;
                } else {
                    this.entityDropItem(this.getHeldItemMainhand().copy());
                    this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                    return ActionResultType.SUCCESS;
                }
            } else if (!isBreedingItem(itemstack)) {
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() == 4) {
                    this.setCommand(0);
                }
                if (this.getCommand() == 3) {
                    player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.mantis_shrimp.command_3", this.getName()), true);
                } else {
                    player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                }
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.setSitting(true);
                    return ActionResultType.SUCCESS;
                } else {
                    this.setSitting(false);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return type;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("MantisShrimpSitting", this.isSitting());
        compound.putInt("Command", this.getCommand());
        compound.putInt("Moisture", this.getMoistness());
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("MantisShrimpSitting"));
        this.setCommand(compound.getInt("Command"));
        this.setVariant(compound.getInt("Variant"));
        this.setMoistness(compound.getInt("Moisture"));
    }

    public void livingTick() {
        super.livingTick();
        if (this.isChild() && this.getEyeHeight() > this.getHeight()) {
            this.recalculateSize();
        }
        prevLeftPitch = this.getEyePitch(true);
        prevRightPitch = this.getEyePitch(false);
        prevLeftYaw = this.getEyeYaw(true);
        prevRightYaw = this.getEyeYaw(false);
        prevInWaterProgress = this.inWaterProgress;
        prevPunchProgress = this.punchProgress;
        updateEyes();
        if (this.isSitting() && this.getNavigator().noPath()) {
            this.getNavigator().clearPath();
        }
        if (this.isInWater() && inWaterProgress < 5F) {
            inWaterProgress++;
        }
        if (!this.isInWater() && inWaterProgress > 0F) {
            inWaterProgress--;
        }
        if (this.isInWater() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!this.isInWater() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (this.dataManager.get(PUNCH_TICK) > 0) {
            if (this.dataManager.get(PUNCH_TICK) == 2 && this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < 2.8D) {
                if (this.getAttackTarget() instanceof AbstractFishEntity && !this.isTamed()) {
                    AbstractFishEntity fish = (AbstractFishEntity) this.getAttackTarget();
                    CompoundNBT fishNbt = new CompoundNBT();
                    fish.writeAdditional(fishNbt);
                    fishNbt.putString("DeathLootTable", LootTables.EMPTY.toString());
                    fish.readAdditional(fishNbt);
                }
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                this.getAttackTarget().applyKnockback(1.7F, this.getPosX() - getAttackTarget().getPosX(), this.getPosZ() - getAttackTarget().getPosZ());
                float knockbackResist = (float) MathHelper.clamp((1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
                this.getAttackTarget().setMotion(this.getAttackTarget().getMotion().add(0, knockbackResist * 0.8F, 0));
                if (!this.getAttackTarget().isInWater()) {
                    this.getAttackTarget().setFire(2);
                }
            }
            if(punchProgress == 1){
                this.playSound(AMSoundRegistry.MANTIS_SHRIMP_SNAP, this.getSoundPitch(), this.getSoundVolume());
            }
            if (punchProgress == 2 && world.isRemote && this.isInWater()) {
                for (int i = 0; i < 10 + rand.nextInt(8); i++) {
                    double d2 = this.rand.nextGaussian() * 0.6D;
                    double d0 = this.rand.nextGaussian() * 0.2D;
                    double d1 = this.rand.nextGaussian() * 0.6D;
                    float radius = this.getWidth() * 0.85F;
                    float angle = (0.01745329251F * this.renderYawOffset);
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle)) + rand.nextFloat() * 0.5F - 0.25F;
                    double extraZ = radius * MathHelper.cos(angle) + rand.nextFloat() * 0.5F - 0.25F;
                    IParticleData data = ParticleTypes.BUBBLE;
                    this.world.addParticle(data, this.getPosX() + extraX, this.getPosY() + this.getHeight() * 0.3F + rand.nextFloat() * 0.15F, this.getPosZ() + extraZ, d0, d1, d2);
                }
            }
            if (punchProgress < 2F) {
                punchProgress++;
            }
            this.dataManager.set(PUNCH_TICK, this.dataManager.get(PUNCH_TICK) - 1);
        } else {
            if (punchProgress > 0F) {
                punchProgress -= 0.25F;
            }
        }
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

    private void updateEyes() {
        float leftPitchDist = Math.abs(this.getEyePitch(true) - targetLeftPitch);
        float rightPitchDist = Math.abs(this.getEyePitch(false) - targetRightPitch);
        float leftYawDist = Math.abs(this.getEyeYaw(true) - targetLeftYaw);
        float rightYawDist = Math.abs(this.getEyeYaw(false) - targetRightYaw);
        if (rightLookCooldown == 0 && this.rand.nextInt(20) == 0 && rightPitchDist < 0.5F && rightYawDist < 0.5F) {
            targetRightPitch = MathHelper.clamp(rand.nextFloat() * 60F - 30, -30, 30);
            targetRightYaw = MathHelper.clamp(rand.nextFloat() * 60F - 30, -30, 30);
            rightLookCooldown = 3 + rand.nextInt(15);
        }
        if (leftLookCooldown == 0 && this.rand.nextInt(20) == 0 && leftPitchDist < 0.5F && leftYawDist < 0.5F) {
            targetLeftPitch = MathHelper.clamp(rand.nextFloat() * 60F - 30, -30, 30);
            targetLeftYaw = MathHelper.clamp(rand.nextFloat() * 60F - 30, -30, 30);
            leftLookCooldown = 3 + rand.nextInt(15);
        }
        if (this.getEyePitch(true) < this.targetLeftPitch && leftPitchDist > 0.5F) {
            this.setEyePitch(true, this.getEyePitch(true) + Math.min(leftPitchDist, 4F));
        }
        if (this.getEyePitch(true) > this.targetLeftPitch && leftPitchDist > 0.5F) {
            this.setEyePitch(true, this.getEyePitch(true) - Math.min(leftPitchDist, 4F));
        }
        if (this.getEyePitch(false) < this.targetRightPitch && rightPitchDist > 0.5F) {
            this.setEyePitch(false, this.getEyePitch(false) + Math.min(rightPitchDist, 4F));
        }
        if (this.getEyePitch(false) > this.targetRightPitch && rightPitchDist > 0.5F) {
            this.setEyePitch(false, this.getEyePitch(false) - Math.min(rightPitchDist, 4F));
        }
        if (this.getEyeYaw(true) < this.targetLeftYaw && leftYawDist > 0.5F) {
            this.setEyeYaw(true, this.getEyeYaw(true) + Math.min(leftYawDist, 4F));
        }
        if (this.getEyeYaw(true) > this.targetLeftYaw && leftYawDist > 0.5F) {
            this.setEyeYaw(true, this.getEyeYaw(true) - Math.min(leftYawDist, 4F));
        }
        if (this.getEyeYaw(false) < this.targetRightYaw && rightYawDist > 0.5F) {
            this.setEyeYaw(false, this.getEyeYaw(false) + Math.min(rightYawDist, 4F));
        }
        if (this.getEyeYaw(false) > this.targetRightYaw && rightYawDist > 0.5F) {
            this.setEyeYaw(false, this.getEyeYaw(false) - Math.min(rightYawDist, 4F));
        }
        if (rightLookCooldown > 0) {
            rightLookCooldown--;
        }
        if (leftLookCooldown > 0) {
            leftLookCooldown--;
        }
    }

    public boolean isPushedByWater() {
        return false;
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setVariant(this.getRNG().nextInt(3));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        EntityMantisShrimp shrimp = AMEntityRegistry.MANTIS_SHRIMP.create(serverWorld);
        shrimp.setVariant(getRNG().nextInt(3));
        return shrimp;
    }

    @Override
    public boolean shouldEnterWater() {
        return (this.getHeldItemMainhand().isEmpty() || this.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) && !this.isSitting();
    }

    @Override
    public boolean shouldLeaveWater() {
        return this.getHeldItemMainhand().getItem() == Items.WATER_BUCKET;
    }

    @Override
    public boolean shouldStopMoving() {
        return isSitting();
    }

    @Override
    public int getWaterSearchRange() {
        return 16;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    protected void updateAir(int p_209207_1_) {
    }



    public class FollowOwner extends Goal {
        private final EntityMantisShrimp tameable;
        private final IWorldReader world;
        private final double followSpeed;
        private final float maxDist;
        private final float minDist;
        private final boolean teleportToLeaves;
        private LivingEntity owner;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public FollowOwner(EntityMantisShrimp p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
            this.tameable = p_i225711_1_;
            this.world = p_i225711_1_.world;
            this.followSpeed = p_i225711_2_;
            this.minDist = p_i225711_4_;
            this.maxDist = p_i225711_5_;
            this.teleportToLeaves = p_i225711_6_;
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean shouldExecute() {
            LivingEntity lvt_1_1_ = this.tameable.getOwner();
            if (lvt_1_1_ == null) {
                return false;
            } else if (lvt_1_1_.isSpectator()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else if (this.tameable.getDistanceSq(lvt_1_1_) < (double) (this.minDist * this.minDist)) {
                return false;
            } else if (this.tameable.getAttackTarget() != null && this.tameable.getAttackTarget().isAlive()) {
                return false;
            } else {
                this.owner = lvt_1_1_;
                return true;
            }
        }

        public boolean shouldContinueExecuting() {
            if (this.tameable.getNavigator().noPath()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else if (this.tameable.getAttackTarget() != null && this.tameable.getAttackTarget().isAlive()) {
                return false;
            } else {
                return this.tameable.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist);
            }
        }

        public void startExecuting() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
            this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
        }

        public void resetTask() {
            this.owner = null;
            this.tameable.getNavigator().clearPath();
            this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
        }

        public void tick() {

            this.tameable.getLookController().setLookPositionWithEntity(this.owner, 10.0F, (float) this.tameable.getVerticalFaceSpeed());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.tameable.getLeashed() && !this.tameable.isPassenger()) {
                    if (this.tameable.getDistanceSq(this.owner) >= 144.0D) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.tameable.getNavigator().tryMoveToEntityLiving(this.owner, this.followSpeed);
                    }

                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos lvt_1_1_ = this.owner.getPosition();

            for (int lvt_2_1_ = 0; lvt_2_1_ < 10; ++lvt_2_1_) {
                int lvt_3_1_ = this.getRandomNumber(-3, 3);
                int lvt_4_1_ = this.getRandomNumber(-1, 1);
                int lvt_5_1_ = this.getRandomNumber(-3, 3);
                boolean lvt_6_1_ = this.tryToTeleportToLocation(lvt_1_1_.getX() + lvt_3_1_, lvt_1_1_.getY() + lvt_4_1_, lvt_1_1_.getZ() + lvt_5_1_);
                if (lvt_6_1_) {
                    return;
                }
            }

        }

        private boolean tryToTeleportToLocation(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
            if (Math.abs((double) p_226328_1_ - this.owner.getPosX()) < 2.0D && Math.abs((double) p_226328_3_ - this.owner.getPosZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
                return false;
            } else {
                this.tameable.setLocationAndAngles((double) p_226328_1_ + 0.5D, p_226328_2_, (double) p_226328_3_ + 0.5D, this.tameable.rotationYaw, this.tameable.rotationPitch);
                this.tameable.getNavigator().clearPath();
                return true;
            }
        }

        private boolean isTeleportFriendlyBlock(BlockPos p_226329_1_) {
            PathNodeType lvt_2_1_ = WalkNodeProcessor.func_237231_a_(this.world, p_226329_1_.toMutable());
            if (world.getFluidState(p_226329_1_).isTagged(FluidTags.WATER) || !world.getFluidState(p_226329_1_).isTagged(FluidTags.WATER) && world.getFluidState(p_226329_1_.down()).isTagged(FluidTags.WATER)) {
                return true;
            }
            if (lvt_2_1_ != PathNodeType.WALKABLE || tameable.getMoistness() < 2000) {
                return false;
            } else {
                BlockState lvt_3_1_ = this.world.getBlockState(p_226329_1_.down());
                if (!this.teleportToLeaves && lvt_3_1_.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos lvt_4_1_ = p_226329_1_.subtract(this.tameable.getPosition());
                    return this.world.hasNoCollisions(this.tameable, this.tameable.getBoundingBox().offset(lvt_4_1_));
                }
            }
        }

        private int getRandomNumber(int p_226327_1_, int p_226327_2_) {
            return this.tameable.getRNG().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
        }
    }
}
