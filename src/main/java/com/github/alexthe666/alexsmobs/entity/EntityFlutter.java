package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EntityFlutter extends TamableAnimal implements IFollower, FlyingAnimal {

    private static final EntityDataAccessor<Float> FLUTTER_PITCH = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> POTTED = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TENTACLING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SHAKING_HEAD_TICKS = SynchedEntityData.defineId(EntityFlutter.class, EntityDataSerializers.INT);
    public float prevFlyProgress;
    public float flyProgress;
    public float prevShootProgress;
    public float shootProgress;
    public float prevSitProgress;
    public float sitProgress;
    public float prevFlutterPitch;
    public float tentacleProgress;
    public float prevTentacleProgress;
    public float FlutterRotation;
    private float rotationVelocity;
    private int squishCooldown = 0;
    private float randomMotionSpeed;
    private boolean isLandNavigator;
    private int timeFlying;
    private List<String> flowersEaten = new ArrayList<>();
    private boolean hasPotStats = false;

    protected EntityFlutter(EntityType type, Level level) {
        super(type, level);
        this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.5F;
        switchNavigator(false);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.FLYING_SPEED, 0.8F).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.21F);
    }


    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !requiresCustomPersistence();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.isTame() || this.isPotted();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.flutterSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canFlutterSpawnInLight(EntityType<? extends EntityFlutter> p_223325_0_, ServerLevelAccessor p_223325_1_, MobSpawnType p_223325_2_, BlockPos p_223325_3_, Random p_223325_4_) {
        return checkMobSpawnRules(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
    }

    public static <T extends Mob> boolean canFlutterSpawn(EntityType<EntityFlutter> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, Random random) {
        BlockState blockstate = iServerWorld.getBlockState(pos.below());
        return reason == MobSpawnType.SPAWNER || !iServerWorld.canSeeSky(pos) && blockstate.is(Blocks.MOSS_BLOCK) && pos.getY() <= 64 && canFlutterSpawnInLight(entityType, iServerWorld, reason, pos, random);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FlyAwayFromTarget(this));
        this.goalSelector.addGoal(2, new TameableAITempt(this, 1.1D, Ingredient.of(Items.BONE_MEAL), false) {
            @Override
            public boolean shouldFollowAM(LivingEntity le) {
                return EntityFlutter.this.canEatFlower(le.getMainHandItem()) || EntityFlutter.this.canEatFlower(le.getOffhandItem()) || super.shouldFollowAM(le);
            }
        });
        this.goalSelector.addGoal(3, new FlyingAIFollowOwner(this, 1.3D, 7.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new AIWalkIdle());
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 1F, false, true);
            this.navigation = new DirectPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLUTTER_PITCH, 0F);
        this.entityData.define(FLYING, false);
        this.entityData.define(POTTED, false);
        this.entityData.define(COMMAND, 0);
        this.entityData.define(SITTING, false);
        this.entityData.define(TENTACLING, false);
        this.entityData.define(SHOOTING, false);
        this.entityData.define(SHAKING_HEAD_TICKS, 0);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isPotted() {
        return this.entityData.get(POTTED);
    }

    public void setPotted(boolean potted) {
        this.entityData.set(POTTED, potted);
    }

    public float getFlutterPitch() {
        return Mth.clamp(entityData.get(FLUTTER_PITCH).floatValue(), -90, 90);
    }

    public void setFlutterPitch(float pitch) {
        entityData.set(FLUTTER_PITCH, pitch);
    }

    public void incrementFlutterPitch(float pitch) {
        entityData.set(FLUTTER_PITCH, getFlutterPitch() + pitch);
    }

    public void decrementFlutterPitch(float pitch) {
        entityData.set(FLUTTER_PITCH, getFlutterPitch() - pitch);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.FLUTTER_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.FLUTTER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.FLUTTER_HURT;
    }

    public void tick() {
        super.tick();
        prevShootProgress = shootProgress;
        prevFlyProgress = flyProgress;
        prevFlutterPitch = this.getFlutterPitch();
        prevSitProgress = sitProgress;
        float extraMotionSlow = 1.0F;
        float extraMotionSlowY = 1.0F;
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        prevFlutterPitch = this.getFlutterPitch();
        prevTentacleProgress = this.tentacleProgress;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (isSitting() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress--;
        }
        if (entityData.get(TENTACLING) && tentacleProgress < 5F) {
            tentacleProgress++;
        }

        if(!entityData.get(TENTACLING) && tentacleProgress == 5F){
            if (squishCooldown == 0 && this.isFlying()) {
                squishCooldown = 10;
                this.playSound(AMSoundRegistry.FLUTTER_FLAP, 3F, 1.5F * this.getVoicePitch());
            }
        }
        if (!entityData.get(TENTACLING) && tentacleProgress > 0F) {
            tentacleProgress--;
        }
        this.FlutterRotation += this.rotationVelocity;
        if ((double) this.FlutterRotation > (Math.PI * 2D)) {
            if (this.level.isClientSide) {
                this.FlutterRotation = ((float) Math.PI * 2F);
            } else {
                this.FlutterRotation = (float) ((double) this.FlutterRotation - (Math.PI * 2D));
                if (this.random.nextInt(10) == 0) {
                    this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.5F;
                }
                this.level.broadcastEntityEvent(this, (byte) 19);
            }
        }
        if (this.FlutterRotation < (float) Math.PI) {
            float f = this.FlutterRotation / (float) Math.PI;
            if ((double) f >= 0.95F) {
                this.entityData.set(TENTACLING, true);
                if (squishCooldown == 0 && this.isFlying()) {
                    squishCooldown = 10;
                    this.playSound(AMSoundRegistry.FLUTTER_FLAP, 3F, 1.5F * this.getVoicePitch());
                }
                this.randomMotionSpeed = 0.8F;
            } else {
                this.entityData.set(TENTACLING, false);
                randomMotionSpeed = 0.01F;
            }
        }
        if (!this.level.isClientSide) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.isFlying()) {
                this.setDeltaMovement(this.getDeltaMovement().x * this.randomMotionSpeed * extraMotionSlow, this.getDeltaMovement().y * this.randomMotionSpeed * extraMotionSlowY, this.getDeltaMovement().z * this.randomMotionSpeed * extraMotionSlow);
                timeFlying++;
                if (this.isOnGround() && timeFlying > 20 || this.isSitting()) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
            }
        }
        if (!this.onGround && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.8D, 1.0D));
        }
        if (this.isFlying()) {
            float dist = (float) ((Math.abs(this.getDeltaMovement().x()) + Math.abs(this.getDeltaMovement().z())) * 30);
            this.incrementFlutterPitch(-dist);
            if (this.horizontalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2F, 0));
            }
        }
        if (this.getFlutterPitch() > 0F) {
            float decrease = Math.min(2.5F, this.getFlutterPitch());
            this.decrementFlutterPitch(decrease);
        }
        if (this.getFlutterPitch() < 0F) {
            float decrease = Math.min(2.5F, -this.getFlutterPitch());
            this.incrementFlutterPitch(decrease);
        }
        boolean shooting = entityData.get(SHOOTING);
        if (shooting && shootProgress < 5) {
            shootProgress += 1;
        }
        if (!shooting && shootProgress > 0) {
            shootProgress -= 1;
        }
        if (shooting) {
            this.incrementFlutterPitch(-30);
        }
        if (!level.isClientSide && shooting && shootProgress == 5F) {
            if (this.getTarget() != null) {
                this.spit(this.getTarget());
            }
            this.entityData.set(SHOOTING, false);
        }
        if (this.hasPotStats && !this.isPotted()) {
            this.hasPotStats = false;
            this.getAttribute(Attributes.ARMOR).setBaseValue(0.21D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.21D);
        }
        if (!this.hasPotStats && this.isPotted()) {
            this.hasPotStats = true;
            this.getAttribute(Attributes.ARMOR).setBaseValue(16.0D);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.18D);
        }
        if (this.entityData.get(SHAKING_HEAD_TICKS) > 0) {
            this.entityData.set(SHAKING_HEAD_TICKS, this.entityData.get(SHAKING_HEAD_TICKS) - 1);
        }
        if(squishCooldown > 0){
            squishCooldown--;
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.BONE_MEAL && this.isTame();
    }


    private void spit(LivingEntity target) {
        EntityPollenBall llamaspitentity = new EntityPollenBall(this.level, this);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - llamaspitentity.getY();
        double d2 = target.getZ() - this.getZ();
        float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.2F;
        llamaspitentity.shoot(d0, d1 + (double) f, d2, 0.5F, 13.0F);
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }
        this.level.addFreshEntity(llamaspitentity);
    }

    public boolean isShakingHead() {
        return this.entityData.get(SHAKING_HEAD_TICKS) > 0;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (!isTame() && canEatFlower(itemstack)) {
            this.usePlayerItem(player, hand, itemstack);
            this.flowersEaten.add(item.getRegistryName().toString());
            this.playSound(AMSoundRegistry.FLUTTER_YES, this.getSoundVolume(), this.getVoicePitch());
            if (this.flowersEaten.size() > 3 && getRandom().nextInt(3) == 0 || this.flowersEaten.size() > 6) {
                this.tame(player);
                this.level.broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        } else if (!isTame() && itemstack.is(ItemTags.FLOWERS)) {
            this.playSound(AMSoundRegistry.FLUTTER_NO, this.getSoundVolume(), this.getVoicePitch());
            this.entityData.set(SHAKING_HEAD_TICKS, 20);
        }
        if (isTame() && itemstack.is(ItemTags.FLOWERS) && this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5);
            return InteractionResult.SUCCESS;
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack) && !itemstack.is(ItemTags.FLOWERS)) {
            if (item == Items.FLOWER_POT && !this.isPotted()) {
                this.setPotted(true);
                return InteractionResult.SUCCESS;
            } else if (item == Items.SHEARS && this.isPotted()) {
                this.setPotted(false);
                this.spawnAtLocation(Items.FLOWER_POT);
                return InteractionResult.SUCCESS;
            } else if(this.isPotted() && player.isShiftKeyDown()){
                ItemStack fish = getFishBucket();
                if (!player.addItem(fish)) {
                    player.drop(fish, false);
                }
                this.remove(RemovalReason.DISCARDED);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            } else {
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() == 3) {
                    this.setCommand(0);
                }
                player.displayClientMessage(new TranslatableComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
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
        return type;
    }

    public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
        if (this.distanceTo(owner) > 8) {
            this.setFlying(true);
            this.getNavigation().moveTo(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ(), followSpeed);
        } else {
            if (this.isFlying() && !this.isOverWaterOrVoid()) {
                BlockPos vec = this.getFlutterGround(this.blockPosition());
                if (vec != null) {
                    this.getMoveControl().setWantedPosition(vec.getX(), vec.getY(), vec.getZ(), followSpeed);
                }
                if (this.onGround) {
                    this.setFlying(false);
                }
            } else {
                this.getNavigation().moveTo(owner, followSpeed);
            }
        }
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }


    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isPotted()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Items.FLOWER_POT);
            }
        }
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

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("Potted", this.isPotted());
        compound.putInt("FlowersEaten", flowersEaten.size());
        for (int i = 0; i < flowersEaten.size(); i++) {
            compound.putString("FlowerEaten" + i, flowersEaten.get(i));
        }
        compound.putInt("FlutterCommand", this.getCommand());
        compound.putBoolean("FlutterSitting", this.isSitting());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setPotted(compound.getBoolean("Potted"));
        int flowerCount = compound.getInt("FlowersEaten");
        this.flowersEaten = new ArrayList<>();
        for (int i = 0; i < flowerCount; i++) {
            String s = compound.getString("FlowerEaten" + i);
            if (s != null) {
                flowersEaten.add(s);
            }
        }
        this.setCommand(compound.getInt("FlutterCommand"));
        this.setOrderedToSit(compound.getBoolean("FlutterSitting"));
    }


    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -63 && !level.getBlockState(position).getMaterial().isSolidBlocking()) {
            position = position.below();
        }
        return !level.getFluidState(position).isEmpty() || position.getY() < -63;
    }

    private BlockPos getFlutterGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getY(), in.getZ());
        while (position.getY() > -63 && !level.getBlockState(position).getMaterial().isSolidBlocking()) {
            position = position.below();
        }
        if (position.getY() < -62) {
            return position.above(120 + random.nextInt(5));
        }

        return position;
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 1 + this.getRandom().nextInt(3) + radiusAdd;
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + (this.getRandom().nextFloat() * neg) * 0.2F;
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, 0, fleePos.z() + extraZ);
        BlockPos ground = getFlutterGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 3 + this.getRandom().nextInt(2);
        BlockPos newPos = ground.above(distFromGround > 4 ? flightHeight : distFromGround - 2 + this.getRandom().nextInt(4));
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }


    public Vec3 getBlockGrounding(Vec3 fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getFlutterGround(radialPos);
        if (ground.getY() <= -63) {
            return Vec3.upFromBottomCenterOf(ground, 110 + random.nextInt(20));
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -63 && !level.getBlockState(ground).getMaterial().isSolidBlocking()) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground.below());
        }
        return null;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }


    protected ItemStack getFishBucket() {
        ItemStack stack = new ItemStack(AMItemRegistry.POTTED_FLUTTER.get());
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        stack.getOrCreateTag().put("FlutterData", platTag);
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mobo) {
        EntityFlutter baby = AMEntityRegistry.FLUTTER.get().create(level);
        baby.setPersistenceRequired();
        return baby;
    }

    public boolean hasEatenFlower(ItemStack stack) {
        return flowersEaten != null && flowersEaten.contains(stack.getItem().getRegistryName().toString());
    }

    public boolean canEatFlower(ItemStack stack) {
        return !hasEatenFlower(stack) && stack.is(ItemTags.FLOWERS);
    }

    private void setupShooting() {
        this.entityData.set(SHOOTING, true);
    }

    public void spawnChildFromBreeding(ServerLevel world, Animal partner) {
        super.spawnChildFromBreeding(world, partner);
        for(int i = 0; i < 15 + random.nextInt(10); i++){
            BlockPos nearby = this.blockPosition().offset(random.nextInt(16) - 8, random.nextInt(2), random.nextInt(16) - 8);
            if(world.getBlockState(nearby).getBlock() == Blocks.AZALEA){
                world.setBlockAndUpdate(nearby, Blocks.FLOWERING_AZALEA.defaultBlockState());
                world.levelEvent(1505, nearby, 0);
            }
            if(world.getBlockState(nearby).getBlock() == Blocks.AZALEA_LEAVES){
                world.setBlockAndUpdate(nearby, Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState());
                world.levelEvent(1505, nearby, 0);
            }
        }
    }

    private class AIWalkIdle extends Goal {
        protected final EntityFlutter phage;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWalkIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.phage = EntityFlutter.this;
        }

        @Override
        public boolean canUse() {
            if (this.phage.isVehicle() || phage.isSitting() || phage.shouldFollow() || (phage.getTarget() != null && phage.getTarget().isAlive()) || this.phage.isPassenger()) {
                return false;
            } else {
                if (this.phage.getRandom().nextInt(30) != 0 && !phage.isFlying() && !phage.isInWaterOrBubble()) {
                    return false;
                }
                if (this.phage.isOnGround() && !phage.isInWaterOrBubble()) {
                    this.flightTarget = random.nextInt(4) == 0 && !phage.isBaby();
                } else {
                    this.flightTarget = random.nextInt(5) > 0 && phage.timeFlying < 100 && !phage.isBaby();
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
            if (flightTarget) {
                phage.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.phage.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && phage.onGround) {
                phage.setFlying(false);
            }
            if (isFlying() && phage.onGround && phage.timeFlying > 40) {
                phage.setFlying(false);
            }
        }

        @javax.annotation.Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = phage.position();
            if (phage.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (phage.timeFlying < 180 || phage.isOverWaterOrVoid()) {
                    return phage.getBlockInViewAway(vector3d, 0);
                } else {
                    return phage.getBlockGrounding(vector3d);
                }
            } else {
                return LandRandomPos.getPos(this.phage, 5, 5);
            }
        }

        public boolean canContinueToUse() {
            if (phage.isSitting()) {
                return false;
            }
            if (flightTarget) {
                return phage.isFlying() && phage.distanceToSqr(x, y, z) > 2F && !phage.isBaby();
            } else {
                return (!this.phage.getNavigation().isDone()) && !this.phage.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                phage.setFlying(true);
                phage.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.phage.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.phage.getNavigation().stop();
            super.stop();
        }
    }

    private class FlyAwayFromTarget extends Goal {

        private final EntityFlutter parentEntity;
        private int spitCooldown = 0;
        private BlockPos shootPos = null;

        public FlyAwayFromTarget(EntityFlutter entityFlutter) {
            this.parentEntity = entityFlutter;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return !parentEntity.isSitting() && parentEntity.getTarget() != null && parentEntity.getTarget().isAlive()  && !parentEntity.isBaby();
        }

        public void tick() {
            if (spitCooldown > 0) {
                spitCooldown--;
            }
            if (parentEntity.getTarget() != null) {
                parentEntity.setFlying(true);
                if (shootPos == null || parentEntity.distanceTo(parentEntity.getTarget()) >= 10F || parentEntity.getTarget().distanceToSqr(shootPos.getX() + 0.5F, shootPos.getY(), shootPos.getZ() + 0.5F) < 4) {
                    shootPos = getShootFromPos(parentEntity.getTarget());
                }
                if (shootPos != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(shootPos.getX() + 0.5D, shootPos.getY() + 0.5D, shootPos.getZ() + 0.5D, 1.5D);
                }
                if (parentEntity.distanceTo(parentEntity.getTarget()) < 25F) {
                    this.parentEntity.lookAt(parentEntity.getTarget(), 30.0F, 30.0F);
                    if (spitCooldown == 0) {
                        parentEntity.setupShooting();
                        spitCooldown = 10 + random.nextInt(10);
                    }
                    shootPos = null;
                }
            }

        }

        public BlockPos getShootFromPos(LivingEntity target) {
            float radius = 3 + parentEntity.getRandom().nextInt(5);
            float angle = (0.01745329251F * (target.yHeadRot + 90F + parentEntity.getRandom().nextInt(180)));
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos(target.getX() + extraX, target.getY() + 2, target.getZ() + extraZ);
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(radialPos))) {
                return radialPos;
            }
            return parentEntity.blockPosition().above((int) Math.ceil(target.getBbHeight() + 1));
        }
    }
}
