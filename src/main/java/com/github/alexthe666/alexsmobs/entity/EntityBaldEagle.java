package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class EntityBaldEagle extends TamableAnimal implements IFollower, IFalconry {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_CAP = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(EntityBaldEagle.class, EntityDataSerializers.BOOLEAN);
    private static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.ROTTEN_FLESH, AMItemRegistry.FISH_OIL.get());
    public float prevAttackProgress;
    public float attackProgress;
    public float prevFlyProgress;
    public float flyProgress;
    public float prevTackleProgress;
    public float tackleProgress;
    public float prevSwoopProgress;
    public float swoopProgress;
    public float prevFlapAmount;
    public float flapAmount;
    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    public float prevSitProgress;
    public float sitProgress;
    private boolean isLandNavigator;
    private int timeFlying;
    private BlockPos orbitPos = null;
    private double orbitDist = 5D;
    private boolean orbitClockwise = false;
    private int passengerTimer = 0;
    private int launchTime = 0;
    private int lastPlayerControlTime = 0;
    private int returnControlTime = 0;
    private int tackleCapCooldown = 0;
    private boolean controlledFlag = false;
    private int chunkLoadCooldown;
    private int stillTicksCounter = 0;

    protected EntityBaldEagle(EntityType<? extends TamableAnimal> type, Level worldIn) {
        super(type, worldIn);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public static boolean canEagleSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getRawBrightness(pos, 0) > 8;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this) {
            public boolean canUse() {
                return super.canUse() && (EntityBaldEagle.this.getAirSupply() < 30 || EntityBaldEagle.this.getTarget() == null || !EntityBaldEagle.this.getTarget().isInWaterOrBubble() && EntityBaldEagle.this.getY() > EntityBaldEagle.this.getTarget().getY());
            }
        });
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0D, 25.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AITackle());
        this.goalSelector.addGoal(4, new AILandOnGlove());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, Ingredient.of(AMTagRegistry.BALD_EAGLE_TAMEABLES), false));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, Ingredient.of(ItemTags.FISHES), false));
        this.goalSelector.addGoal(8, new AIWanderIdle());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return EntityBaldEagle.this.returnControlTime == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return EntityBaldEagle.this.returnControlTime == 0 && super.canUse();
            }
        });
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.BALD_EAGLE_TARGETS)) {
            public boolean canUse() {
                return super.canUse() && !EntityBaldEagle.this.isLaunched() && EntityBaldEagle.this.getCommand() == 0;
            }
        });
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.BALD_EAGLE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BALD_EAGLE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BALD_EAGLE_HURT.get();
    }


    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.baldEagleSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }

        return super.isAlliedTo(entityIn);
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new MoveHelper(this);
            this.navigation = new DirectPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    public boolean save(CompoundTag compound) {
        String s = this.getEncodeId();
        compound.putString("id", s);
        super.save(compound);
        return true;
    }

    public boolean saveAsPassenger(CompoundTag compound) {
        if (!this.isTame()) {
            return super.saveAsPassenger(compound);
        } else {
            String s = this.getEncodeId();
            compound.putString("id", s);
            this.saveWithoutId(compound);
            return true;
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("BirdSitting", this.isSitting());
        compound.putBoolean("Launched", this.isLaunched());
        compound.putBoolean("HasCap", this.hasCap());
        compound.putInt("EagleCommand", this.getCommand());
        compound.putInt("LaunchTime", this.launchTime);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("BirdSitting"));
        this.setLaunched(compound.getBoolean("Launched"));
        this.setCap(compound.getBoolean("HasCap"));
        this.setCommand(compound.getInt("EagleCommand"));
        this.launchTime = compound.getInt("LaunchTime");
    }

    public void travel(Vec3 vec3d) {
        if (!this.shouldHoodedReturn() && this.hasCap() && this.isTame() && !this.isPassenger() || this.isSitting()) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(vec3d);
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.attackProgress == 0 && this.entityData.get(ATTACK_TICK) == 0 && entityIn.isAlive()) {
            final double dist = this.isSitting() ? entityIn.getBbWidth() + 1 : entityIn.getBbWidth() + 5;
            if (this.distanceTo(entityIn) < dist) {
                this.entityData.set(ATTACK_TICK, 5);
            }
        }
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(HAS_CAP, false);
        this.entityData.define(TACKLING, false);
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(ATTACK_TICK, 0);
        this.entityData.define(COMMAND, 0);
        this.entityData.define(SITTING, false);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, sit);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public boolean isLaunched() {
        return this.entityData.get(LAUNCHED);
    }

    public void setLaunched(boolean flying) {
        this.entityData.set(LAUNCHED, flying);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if (flying && this.isBaby()) {
            flying = false;
        }
        this.entityData.set(FLYING, flying);
    }

    public boolean hasCap() {
        return this.entityData.get(HAS_CAP);
    }

    public void setCap(boolean cap) {
        this.entityData.set(HAS_CAP, cap);
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLING);
    }

    public void setTackling(boolean tackling) {
        this.entityData.set(TACKLING, tackling);
    }

    public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
        if (this.distanceTo(owner) > 15) {
            this.setFlying(true);
            this.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
        } else {
            if (this.isFlying() && !this.isOverWaterOrVoid()) {
                BlockPos vec = this.getCrowGround(this.blockPosition());
                if (vec != null) {
                    this.getMoveControl().setWantedPosition(vec.getX(), vec.getY(), vec.getZ(), followSpeed);
                }
                if (this.onGround()) {
                    this.setFlying(false);
                }
            } else {
                this.getNavigation().moveTo(owner, followSpeed);
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (itemstack.is(ItemTags.FISHES) && this.getHealth() < this.getMaxHealth()) {
            this.heal(10);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.level().broadcastEntityEvent(this, (byte) 7);
            return InteractionResult.CONSUME;
        } else if (itemstack.is(AMTagRegistry.BALD_EAGLE_TAMEABLES)) {
            if (itemstack.hasCraftingRemainingItem() && !player.getAbilities().instabuild) {
                this.spawnAtLocation(itemstack.getCraftingRemainingItem());
            }
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            if (random.nextBoolean()) {
                this.level().broadcastEntityEvent(this, (byte) 7);
                this.tame(player);
                this.setCommand(1);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.CONSUME;
        } else if (isTame() && !isFood(itemstack)) {
            if (!this.isBaby() && item == AMItemRegistry.FALCONRY_HOOD.get()) {
                if (!this.hasCap()) {
                    this.setCap(true);
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }
                    this.gameEvent(GameEvent.ENTITY_INTERACT);
                    this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, this.getSoundVolume(), this.getVoicePitch());
                    return InteractionResult.SUCCESS;
                }
            } else if (item == Items.SHEARS && this.hasCap()) {
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if (!this.level().isClientSide) {
                    if (player instanceof ServerPlayer) {
                        itemstack.hurt(1, random, (ServerPlayer) player);
                    }
                }
                this.spawnAtLocation(AMItemRegistry.FALCONRY_HOOD.get());
                this.setCap(false);
                return InteractionResult.SUCCESS;
            } else if (!this.isBaby() && getRidingFalcons(player) <= 0 && (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get() || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get())) {
                boardingCooldown = 30;
                this.setLaunched(false);
                this.ejectPassengers();
                this.startRiding(player, true);
                if (!this.level().isClientSide) {
                    AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.getId(), player.getId()));
                }
                return InteractionResult.SUCCESS;
            } else {
                InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
                if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS) {
                    this.setCommand((this.getCommand() + 1) % 3);

                    if (this.getCommand() == 3) {
                        this.setCommand(0);
                    }
                    player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                    boolean sit = this.getCommand() == 2;
                    if (sit) {
                        this.setOrderedToSit(true);
                        return InteractionResult.SUCCESS;
                    } else {
                        this.setOrderedToSit(false);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return type;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1 && !isLaunched();
    }



    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && (!entity.isAlive() || !this.isAlive())) {
            this.stopRiding();
        } else if (isTame() && entity instanceof LivingEntity && isOwnedBy((LivingEntity) entity)) {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                Entity mount = this.getVehicle();
                if (mount instanceof Player) {
                    float yawAdd = 0;
                    if (((Player) mount).getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                        yawAdd = ((Player) mount).getMainArm() == HumanoidArm.LEFT ? 135 : -135;
                    } else if (((Player) mount).getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                        yawAdd = ((Player) mount).getMainArm() == HumanoidArm.LEFT ? -135 : 135;
                    } else {
                        this.setCommand(2);
                        this.setOrderedToSit(true);
                        this.removeVehicle();
                        this.copyPosition(mount);
                    }
                    float birdYaw = yawAdd * 0.5F;
                    this.yBodyRot = Mth.wrapDegrees(((LivingEntity) mount).yBodyRot + birdYaw);
                    this.setYRot(Mth.wrapDegrees(mount.getYRot() + birdYaw));
                    this.yHeadRot = Mth.wrapDegrees(((LivingEntity) mount).yHeadRot + birdYaw);
                    float radius = 0.6F;
                    float angle = (Maths.STARTING_ANGLE * (((LivingEntity) mount).yBodyRot - 180F + yawAdd));
                    double extraX = radius * Mth.sin(Mth.PI + angle);
                    double extraZ = radius * Mth.cos(angle);
                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() * 0.45F, mount.getY()), mount.getZ() + extraZ);
                }
                if (!mount.isAlive()) {
                    this.removeVehicle();
                }
            }
        } else {
            super.rideTick();
        }
    }

    public void tick() {
        super.tick();

        this.prevAttackProgress = attackProgress;
        this.prevBirdPitch = birdPitch;
        this.prevTackleProgress = tackleProgress;
        this.prevFlyProgress = flyProgress;
        this.prevFlapAmount = flapAmount;
        this.prevSwoopProgress = swoopProgress;
        this.prevSitProgress = sitProgress;
        float yMot = (float) -((float) this.getDeltaMovement().y * Mth.RAD_TO_DEG);
        this.birdPitch = yMot;

        if (isFlying()) {
            if (flyProgress < 5F)
                flyProgress++;
        } else {
            if (flyProgress > 0F)
                flyProgress--;
        }

        if (isTackling()) {
            if (tackleProgress < 5F)
                tackleProgress++;
        } else {
            if (tackleProgress > 0F)
                tackleProgress--;
        }

        final boolean sit = isSitting() || this.isPassenger();
        if (sit) {
            if (sitProgress < 5F)
                sitProgress++;
        } else {
            if (sitProgress > 0F)
                sitProgress--;
        }

        if (this.isLaunched()) {
            launchTime++;
        } else {
            launchTime = 0;
        }

        if (lastPlayerControlTime > 0) {
            lastPlayerControlTime--;
        }
        if (lastPlayerControlTime <= 0) {
            controlledFlag = false;
        }

        if (yMot < 0.1F) {
            flapAmount = Math.min(-yMot * 0.2F, 1F);
            if (swoopProgress > 0) {
                swoopProgress--;
            }
        } else {
            if (flapAmount > 0.0F) {
                flapAmount -= Math.min(flapAmount, 0.1F);
            } else {
                flapAmount = 0;
            }
            if (swoopProgress < yMot * 0.2F) {
                swoopProgress = Math.min(yMot * 0.2F, swoopProgress + 1);
            }
        }

        if (this.isTackling()) {
            flapAmount = Math.min(2, flapAmount + 0.2F);
        }

        if (!this.level().isClientSide) {
            if (isFlying()) {
                if (this.isLandNavigator)
                    switchNavigator(false);
            } else {
                if (!this.isLandNavigator)
                    switchNavigator(true);
            }

            if (tackleCapCooldown == 0 && this.isTackling() && !this.isVehicle() && (this.getTarget() == null || !this.getTarget().isAlive())) {
                this.setTackling(false);
            }

            if (isFlying()) {
                timeFlying++;
                this.setNoGravity(true);
                if (this.isSitting() || this.isPassenger() || this.isInLove()) {
                    if (!isLaunched()) {
                        this.setFlying(false);
                    }
                }
                if (this.getTarget() != null && this.getTarget().getY() < this.getX() && !this.isVehicle()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.9, 1.0));
                }
            } else {
                timeFlying = 0;
                this.setNoGravity(false);
            }

            if (this.isInWaterOrBubble() && this.isVehicle()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1F, 0));
            }

            if (this.isSitting() && !this.isLaunched()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1F, 0));
            }

            if (this.getTarget() != null && this.isInWaterOrBubble()) {
                timeFlying = 0;
                this.setFlying(true);
            }

            if (this.onGround() && this.timeFlying > 30 && isFlying() && !this.isInWaterOrBubble()) {
                this.setFlying(false);
            }
        }

        final int attackTick = this.entityData.get(ATTACK_TICK);
        if (attackTick > 0) {
            if (attackTick == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < this.getTarget().getBbWidth() + 2D) {
                this.getTarget().hurt(this.damageSources().mobAttack(this), 2);
            }
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }

        if (this.isPassenger()) {
            this.setFlying(false);
            this.setTackling(false);
        }

        if (boardingCooldown > 0) {
            boardingCooldown--;
        }
        if (returnControlTime > 0) {
            returnControlTime--;
        }
        if (tackleCapCooldown > 0) {
            tackleCapCooldown--;
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.BALD_EAGLE.get().create(p_241840_1_);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        final float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24) - radiusAdd;
        final float neg = this.getRandom().nextBoolean() ? 1 : -1;
        final float renderYawOffset = this.yBodyRot;
        final float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        final double extraX = radius * Mth.sin(Mth.PI + angle);
        final double extraZ = radius * Mth.cos(angle);
        final BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), 0, (int) (fleePos.z() + extraZ));
        final BlockPos ground = getCrowGround(radialPos);
        final int distFromGround = (int) this.getY() - ground.getY();
        final int flightHeight = 7 + this.getRandom().nextInt(10);
        final BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(7) + 4);
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    private BlockPos getCrowGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() < 320 && !level().getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > -64 && !level().getBlockState(position).isSolid()) {
            position = position.below();
        }
        return position;
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        final float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24);
        final float neg = this.getRandom().nextBoolean() ? 1 : -1;
        final float renderYawOffset = this.yBodyRot;
        final float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        final double extraX = radius * Mth.sin(Mth.PI + angle);
        final double extraZ = radius * Mth.cos(angle);
        final BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getCrowGround(radialPos);
        if (ground.getY() == -64) {
            return this.position();
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -64 && !level().getBlockState(ground).isSolid()) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground);
        }
        return null;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        return this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private Vec3 getOrbitVec(Vec3 vector3d, float gatheringCircleDist) {
        final float angle = (Maths.STARTING_ANGLE * (float) this.orbitDist * (orbitClockwise ? -tickCount : tickCount));
        final double extraX = gatheringCircleDist * Mth.sin((angle));
        final double extraZ = gatheringCircleDist * Mth.cos(angle);
        if (this.orbitPos != null) {
            final Vec3 pos = new Vec3(orbitPos.getX() + extraX, orbitPos.getY() + random.nextInt(2) - 2, orbitPos.getZ() + extraZ);
            if (this.level().isEmptyBlock(AMBlockPos.fromVec3(pos))) {
                return pos;
            }
        }
        return null;
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -64 && level().isEmptyBlock(position)) {
            position = position.below();
        }
        return !level().getFluidState(position).isEmpty() || position.getY() <= -64;
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            final float radius = 0.3F;
            final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
            final double extraX = radius * Mth.sin(Mth.PI + angle);
            final double extraZ = radius * Mth.cos(angle);
            passenger.setYRot(this.yBodyRot + 90F);
            if (passenger instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) passenger;
                living.yBodyRot = this.yBodyRot + 90F;
            }
            float extraY = 0F;
            if (passenger instanceof AbstractFish && !passenger.isInWaterOrBubble()) {
                extraY = 0.1F;
            }
            moveFunc.accept(passenger, this.getX() + extraX, this.getY() - 0.3F + extraY + passenger.getBbHeight() * 0.3F, this.getZ() + extraZ);
            passengerTimer++;
            if (this.isAlive() && passengerTimer > 0 && passengerTimer % 40 == 0) {
                passenger.hurt(this.damageSources().mobAttack(this), 1);
            }
        }
    }

    public boolean canBeRiddenInWater(Entity rider) {
        return true;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    public boolean shouldHoodedReturn() {
        if (this.getOwner() != null) {
            if (!this.getOwner().isAlive() || this.getOwner().isShiftKeyDown()) {
                return true;
            }
        }
        return !this.isAlive() || this.isInsidePortal || launchTime > 12000 || this.portalTime > 0 || this.isRemoved();
    }

    public void remove(RemovalReason reason) {
        if (this.lastPlayerControlTime == 0 && !this.isPassenger()) {
            super.remove(reason);
        }
    }

    public void directFromPlayer(float rotationYaw, float rotationPitch, boolean loadChunk, Entity over) {
        final Entity owner = this.getOwner();
        if (owner != null && this.distanceTo(owner) > 150) {
            returnControlTime = 100;
        }
        if (Math.abs(xo - this.getX()) > 0.1F || Math.abs(yo - this.getY()) > 0.1F || Math.abs(zo - this.getZ()) > 0.1F) {
            stillTicksCounter = 0;
        } else {
            stillTicksCounter++;
        }
        int stillTPthreshold = AMConfig.falconryTeleportsBack ? 200 : 6000;
        this.setOrderedToSit(false);
        this.setLaunched(true);
        if (owner != null && (returnControlTime > 0 && AMConfig.falconryTeleportsBack || stillTicksCounter > stillTPthreshold && this.distanceTo(owner) > 30)) {
            this.copyPosition(owner);
            returnControlTime = 0;
            stillTicksCounter = 0;
            launchTime = Math.max(launchTime, 12000);
        }
        if (!this.level().isClientSide) {
            if (returnControlTime > 0 && owner != null) {
                this.getLookControl().setLookAt(owner, 30, 30);
            } else {
                this.yBodyRot = rotationYaw;
                this.setYRot(rotationYaw);
                this.yHeadRot = rotationYaw;
                this.setXRot(rotationPitch);
            }
            if (rotationPitch < 10 && this.onGround()) {
                this.setFlying(true);
            }
            final float yawOffset = rotationYaw + 90;
            final float rad = 3F;
            final float speed = 1.2F;
            if (returnControlTime > 0) {
                this.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + 10, owner.getZ(), speed);
            } else {
                this.getMoveControl().setWantedPosition(this.getX() + rad * 1.5F * Math.cos(yawOffset * Mth.DEG_TO_RAD), this.getY() - rad * Math.sin(rotationPitch * Mth.DEG_TO_RAD), this.getZ() + rad * Math.sin(yawOffset * Mth.DEG_TO_RAD), speed);
            }
            if (loadChunk) {
                loadChunkOnServer(this.blockPosition());
            }
            this.setLastHurtByMob(null);
            this.setTarget(null);
            if (over == null) {
                final List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(3.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
                Entity closest = null;
                for (final Entity e : list) {
                    if (closest == null || this.distanceTo(e) < this.distanceTo(closest)) {
                        closest = e;
                    }
                }
                over = closest;
            }
        }
        if (over != null && over != owner && !this.isAlliedTo(over) && canFalconryAttack(over)) {
            if (tackleCapCooldown == 0 && this.distanceTo(over) <= over.getBbWidth() + 4D) {
                this.setTackling(true);
                if (this.distanceTo(over) <= over.getBbWidth() + 2D) {
                    final float speedDamage = (float) Math.ceil(Mth.clamp(this.getDeltaMovement().length() + 0.2, 0, 1.2D) * 3.333);
                    over.hurt(this.damageSources().mobAttack(this), 5 + speedDamage + random.nextInt(2));
                    tackleCapCooldown = 22;
                }
            }
        }
        this.lastPlayerControlTime = 10;
        this.controlledFlag = true;
    }


    public float getHandOffset(){
        return 0.8F;
    }

    private boolean canFalconryAttack(Entity over) {
        return !(over instanceof ItemEntity) && (!(over instanceof LivingEntity) || !this.isOwnedBy((LivingEntity) over));
    }

    //killEntity
    public void awardKillScore(LivingEntity entity, int score, DamageSource src) {
        if (this.isLaunched() && this.hasCap() && this.isTame() && this.getOwner() != null) {
            if (this.getOwner() instanceof ServerPlayer && this.distanceTo(this.getOwner()) >= 100) {
                AMAdvancementTriggerRegistry.BALD_EAGLE_CHALLENGE.trigger((ServerPlayer) this.getOwner());
            }
        }
        super.awardKillScore(entity, score, src);
    }


    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            final Entity entity = source.getEntity();
            if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow) && this.isLaunched()) {
                amount = (amount + 1.0F) / 4.0F;
            }
            return super.hurt(source, amount);
        }
    }

    public void loadChunkOnServer(BlockPos center) {
        if (!this.level().isClientSide) {
            ServerLevel serverWorld = (ServerLevel) level();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    ChunkPos pos = new ChunkPos(this.blockPosition().offset(i * 16, 0, j * 16));
                    serverWorld.setChunkForced(pos.x, pos.z, true);

                }
            }
        }
    }

    @Override
    public void onLaunch(Player player, Entity pointedEntity) {
        this.setLaunched(true);
        this.setOrderedToSit(false);
        this.setCommand(0);
        if (this.hasCap()) {
            this.setFlying(true);
            this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.1F);
            if (this.level().isClientSide) {
                AlexsMobs.sendMSGToServer(new MessageMosquitoDismount(this.getId(), player.getId()));
            }
            AlexsMobs.PROXY.setRenderViewEntity(this);
        } else {
            this.getNavigation().stop();
            this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.1F);
            if (pointedEntity != null && pointedEntity.isAlive() && !this.isAlliedTo(pointedEntity)) {
                this.setFlying(true);
                if (pointedEntity instanceof final LivingEntity pointedLivingEntity) {
                    this.setTarget(pointedLivingEntity);
                }
            } else {
                this.setFlying(false);
                this.setCommand(2);
                this.setOrderedToSit(true);
            }
        }
    }

    static class MoveHelper extends MoveControl {
        private final EntityBaldEagle parentEntity;

        public MoveHelper(EntityBaldEagle bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                final Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                final double d5 = vector3d.length();
                if (d5 < 0.3) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d5)));
                    final Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (float) Mth.RAD_TO_DEG);
                    parentEntity.yBodyRot = parentEntity.getYRot();
                }
            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    private class AIWanderIdle extends Goal {
        protected final EntityBaldEagle eagle;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;
        private int orbitResetCooldown = 0;
        private int maxOrbitTime = 360;
        private int orbitTime = 0;

        public AIWanderIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.eagle = EntityBaldEagle.this;
        }

        @Override
        public boolean canUse() {
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if ((eagle.getTarget() != null && eagle.getTarget().isAlive() && !this.eagle.isVehicle()) || this.eagle.isPassenger() || this.eagle.isSitting() || eagle.controlledFlag) {
                return false;
            } else {
                if (this.eagle.getRandom().nextInt(15) != 0 && !eagle.isFlying()) {
                    return false;
                }
                if (this.eagle.isBaby()) {
                    this.flightTarget = false;
                } else if (this.eagle.isInWaterOrBubble()) {
                    this.flightTarget = true;
                } else if (this.eagle.hasCap()) {
                    this.flightTarget = false;
                } else if (this.eagle.onGround()) {
                    this.flightTarget = random.nextBoolean();
                } else {
                    if (orbitResetCooldown == 0 && random.nextInt(6) == 0) {
                        orbitResetCooldown = 400;
                        eagle.orbitPos = eagle.blockPosition();
                        eagle.orbitDist = 4 + random.nextInt(5);
                        eagle.orbitClockwise = random.nextBoolean();
                        orbitTime = 0;
                        maxOrbitTime = (int) (360 + 360 * random.nextFloat());
                    }
                    this.flightTarget = eagle.isVehicle() || random.nextInt(7) > 0 && eagle.timeFlying < 700;
                }
                Vec3 lvt_1_1_ = this.getPosition();
                if (lvt_1_1_ == null) {
                    return false;
                } else {
                    this.x = lvt_1_1_.x;
                    this.y = lvt_1_1_.y;
                    this.z = lvt_1_1_.z;
                    return true;
                }
            }
        }

        public void tick() {
            if (orbitResetCooldown > 0) {
                orbitResetCooldown--;
            }
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                if (orbitTime < maxOrbitTime && !eagle.isInWaterOrBubble()) {
                    orbitTime++;
                } else {
                    orbitTime = 0;
                    eagle.orbitPos = null;
                    orbitResetCooldown = -400 - random.nextInt(400);
                }
            }
            if (eagle.horizontalCollision && !eagle.onGround()) {
                stop();
            }
            if (flightTarget) {
                eagle.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                if (!eagle.onGround() && eagle.isFlying()) {
                    if (!eagle.isInWaterOrBubble()) {
                        eagle.setDeltaMovement(eagle.getDeltaMovement().multiply(1.2F, 0.6F, 1.2F));
                    }
                } else {
                    this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1F);
                }
            }
            if (!flightTarget && eagle.onGround() && isFlying()) {
                eagle.setFlying(false);
                orbitTime = 0;
                eagle.orbitPos = null;
                orbitResetCooldown = -400 - random.nextInt(400);
            }
            if (eagle.timeFlying > 30 && isFlying() && (!level().isEmptyBlock(eagle.getBlockPosBelowThatAffectsMyMovement()) || eagle.onGround()) && !eagle.isInWaterOrBubble()) {
                eagle.setFlying(false);
                orbitTime = 0;
                eagle.orbitPos = null;
                orbitResetCooldown = -400 - random.nextInt(400);
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = eagle.position();
            if (eagle.isTame() && eagle.getCommand() == 1 && eagle.getOwner() != null) {
                vector3d = eagle.getOwner().position();
                eagle.orbitPos = eagle.getOwner().blockPosition();
            }
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                return eagle.getOrbitVec(vector3d, 4 + random.nextInt(2));
            }
            if (eagle.isVehicle() || eagle.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (eagle.timeFlying < 500 || eagle.isVehicle() || eagle.isOverWaterOrVoid()) {
                    return eagle.getBlockInViewAway(vector3d, 0);
                } else {
                    return eagle.getBlockGrounding(vector3d);
                }
            } else {
                return LandRandomPos.getPos(this.eagle, 10, 7);
            }
        }

        public boolean canContinueToUse() {
            if (eagle.isSitting()) {
                return false;
            }
            if (flightTarget) {
                return eagle.isFlying() && eagle.distanceToSqr(x, y, z) > 2F;
            } else {
                return (!this.eagle.getNavigation().isDone()) && !this.eagle.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                eagle.setFlying(true);
                eagle.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.eagle.getNavigation().stop();
            super.stop();
        }
    }

    private class AITackle extends Goal {
        protected EntityBaldEagle eagle;
        private int circleTime;
        private int maxCircleTime = 10;


        public AITackle() {
            this.eagle = EntityBaldEagle.this;
        }

        @Override
        public boolean canUse() {
            return eagle.getTarget() != null && !eagle.controlledFlag && !eagle.isVehicle();
        }

        public void start() {
            eagle.orbitPos = null;
        }

        public void stop() {
            circleTime = 0;
            maxCircleTime = 60 + random.nextInt(60);
        }

        public void tick() {
            final LivingEntity target = eagle.getTarget();
            boolean smallPrey = target != null && target.getBbHeight() < 1F && target.getBbWidth() < 0.7F && !(target instanceof EntityBaldEagle) || target instanceof AbstractFish;
            if (eagle.orbitPos != null && circleTime < maxCircleTime) {
                circleTime++;
                eagle.setTackling(false);
                eagle.setFlying(true);
                if (target != null) {
                    int i = 0;
                    final int up = 2 + eagle.getRandom().nextInt(4);
                    eagle.orbitPos = target.blockPosition().above((int) (target.getBbHeight()));
                    while (eagle.level().isEmptyBlock(eagle.orbitPos) && i < up) {
                        i++;
                        eagle.orbitPos = eagle.orbitPos.above();
                    }
                }
                final Vec3 vec = eagle.getOrbitVec(Vec3.ZERO, 4 + random.nextInt(2));
                if (vec != null) {
                    eagle.getMoveControl().setWantedPosition(vec.x, vec.y, vec.z, 1.2F);
                }
            } else if (target != null) {
                if (eagle.isFlying() || eagle.isInWaterOrBubble()) {
                    final double d0 = eagle.getX() - target.getX();
                    final double d2 = eagle.getZ() - target.getZ();
                    final double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
                    double yAddition = target.getBbHeight();
                    if (xzDist > 15D) {
                        yAddition = 3D;
                    }
                    eagle.setTackling(true);
                    eagle.getMoveControl().setWantedPosition(target.getX(), target.getY() + yAddition, target.getZ(), eagle.isInWaterOrBubble() ? 1.3F : 1.0F);
                } else {
                    this.eagle.getNavigation().moveTo(target, 1F);
                }
                if (eagle.distanceTo(target) < target.getBbWidth() + 2.5F) {
                    if (eagle.isTackling()) {
                        if (smallPrey) {
                            eagle.setFlying(true);
                            eagle.timeFlying = 0;
                            final float radius = 0.3F;
                            final float angle = (Maths.STARTING_ANGLE * eagle.yBodyRot);
                            final double extraX = radius * Mth.sin(Mth.PI + angle);
                            final double extraZ = radius * Mth.cos(angle);
                            target.setYRot(eagle.yBodyRot + 90F);
                            if (target instanceof LivingEntity) {
                                LivingEntity living = target;
                                living.yBodyRot = eagle.yBodyRot + 90F;
                            }
                            target.setPos(eagle.getX() + extraX, eagle.getY() - 0.4F + target.getBbHeight() * 0.45F, eagle.getZ() + extraZ);
                            target.startRiding(eagle, true);
                        } else {
                            target.hurt(eagle.damageSources().mobAttack(eagle), 5);
                            eagle.setFlying(false);
                            eagle.orbitPos = target.blockPosition().above(2);
                            circleTime = 0;
                            maxCircleTime = 60 + random.nextInt(60);
                        }
                    } else {
                        eagle.doHurtTarget(target);
                    }
                } else if (eagle.distanceTo(target) > 12 || target.isInWaterOrBubble()) {
                    eagle.setFlying(true);
                }
            }
            if (eagle.isLaunched()) {
                eagle.setFlying(true);
            }
        }
    }

    private class AILandOnGlove extends Goal {
        protected EntityBaldEagle eagle;
        private int seperateTime = 0;

        public AILandOnGlove() {
            this.eagle = EntityBaldEagle.this;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return eagle.isLaunched() && !eagle.controlledFlag && eagle.isTame() && !eagle.isPassenger() && !eagle.isVehicle() && (eagle.getTarget() == null || !eagle.getTarget().isAlive());
        }

        public void tick() {
            if (eagle.getDeltaMovement().lengthSqr() < 0.03D) {
                seperateTime++;
            }
            final LivingEntity owner = eagle.getOwner();
            if (owner != null) {
                if (seperateTime > 200) {
                    seperateTime = 0;
                    eagle.copyPosition(owner);
                }
                eagle.setFlying(true);
                final double d0 = eagle.getX() - owner.getX();
                final double d2 = eagle.getZ() - owner.getZ();
                final double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
                final double yAdd = xzDist > 14 ? 5 : 0;
                eagle.getMoveControl().setWantedPosition(owner.getX(), owner.getY() + yAdd + owner.getEyeHeight(), owner.getZ(), 1);

                if (this.eagle.distanceTo(owner) < owner.getBbWidth() + 1.4D) {
                    this.eagle.setLaunched(false);
                    if (this.eagle.getRidingFalcons(owner) <= 0) {
                        this.eagle.startRiding(owner);
                        if (!eagle.level().isClientSide) {
                            AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(eagle.getId(), owner.getId()));
                        }
                    } else {
                        this.eagle.setCommand(2);
                        this.eagle.setOrderedToSit(true);
                    }
                }
            }
        }

        public void stop() {
            seperateTime = 0;
        }
    }
}
