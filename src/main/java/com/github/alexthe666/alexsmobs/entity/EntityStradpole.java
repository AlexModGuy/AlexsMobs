package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class EntityStradpole extends WaterAnimal implements Bucketable {

    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityStradpole.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESPAWN_SOON = SynchedEntityData.defineId(EntityStradpole.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(EntityStradpole.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(EntityStradpole.class, EntityDataSerializers.OPTIONAL_UUID);
    public float swimPitch = 0;
    public float prevSwimPitch = 0;
    private int despawnTimer = 0;
    private int ricochetCount = 0;
    protected EntityStradpole(EntityType type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.moveControl = new AquaticMoveController(this, 1.4F);
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COD_HURT;
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.STRADPOLE_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        Bucketable.saveDefaultDataToBucketTag(this, bucket);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        Bucketable.loadDefaultDataFromBucketTag(this, compound);
    }

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(itemstack.getItem() == AMItemRegistry.MOSQUITO_LARVA.get()){
            if(!player.isCreative()){
                itemstack.shrink(1);
            }
            if(random.nextFloat() < 0.45F){
                EntityStraddler straddler = AMEntityRegistry.STRADDLER.get().create(level);
                straddler.copyPosition(this);
                if(!level.isClientSide){
                    level.addFreshEntity(straddler);
                }
                this.remove(RemovalReason.DISCARDED);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (itemstack.getItem() == Items.LAVA_BUCKET && this.isAlive()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(this.getPickupSound(), 1.0F, 1.0F);
            ItemStack itemstack1 = this.getBucketItemStack();
            this.saveToBucketTag(itemstack1);
            ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1, false);
            player.setItemInHand(hand, itemstack2);
            Level level = this.level;
            if (!level.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, itemstack1);
            }

            this.discard();
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.mobInteract(player, hand);
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PARENT_UUID, Optional.empty());
        this.entityData.define(DESPAWN_SOON, false);
        this.entityData.define(LAUNCHED, false);
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean p_203706_1_) {
        this.entityData.set(FROM_BUCKET, p_203706_1_);
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Nullable
    public UUID getParentId() {
        return this.entityData.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getParentId() != null) {
            compound.putUUID("ParentUUID", this.getParentId());
        }
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putBoolean("DespawnSoon", this.isDespawnSoon());
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double p_27492_) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.stradpoleSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canStradpoleSpawn(EntityType<EntityStradpole> p_234314_0_, LevelAccessor p_234314_1_, MobSpawnType p_234314_2_, BlockPos p_234314_3_, RandomSource p_234314_4_) {
        if(p_234314_1_.getFluidState(p_234314_3_).is(FluidTags.LAVA)){
            if(!p_234314_1_.getFluidState(p_234314_3_.below()).is(FluidTags.LAVA)){

                return p_234314_1_.isEmptyBlock(p_234314_3_.above());
            }
        }
        return false;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ParentUUID")) {
            this.setParentId(compound.getUUID("ParentUUID"));
        }
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setDespawnSoon(compound.getBoolean("DespawnSoon"));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StradpoleAISwim(this, 1.0D, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        if (!worldIn.getBlockState(pos).getFluidState().isEmpty()) {
            return 15.0F;
        } else {
            return Float.NEGATIVE_INFINITY;
        }
    }

    public boolean isDespawnSoon() {
        return this.entityData.get(DESPAWN_SOON);
    }

    public void setDespawnSoon(boolean despawnSoon) {
        this.entityData.set(DESPAWN_SOON, despawnSoon);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new BoneSerpentPathNavigator(this, worldIn);
    }

    public void tick() {
        float f = 1.0F;
        if (entityData.get(LAUNCHED)) {
            this.yBodyRot = this.getYRot();
            HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
                this.onImpact(raytraceresult);
            }
            f = 0.1F;
        }
        super.tick();
        boolean liquid = this.isInWater() || this.isInLava();
        prevSwimPitch = this.swimPitch;

        float f2 = (float) -((float) this.getDeltaMovement().y * (liquid ? 2.5F : f) * (double) (180F / (float) Math.PI));
        this.swimPitch = f2;
        if (this.onGround && !this.isInWater() && !this.isInLava()) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.setYRot( this.random.nextFloat() * 360.0F);
            this.onGround = false;
            this.hasImpulse = true;
        }
        this.setNoGravity(false);
        if (liquid) {
            this.setNoGravity(true);
        }
        if (isDespawnSoon()) {
            despawnTimer++;
            if (despawnTimer > 100) {
                despawnTimer = 0;
                this.spawnAnim();
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private void onImpact(HitResult raytraceresult) {
        HitResult.Type raytraceresult$type = raytraceresult.getType();
        if (raytraceresult$type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) raytraceresult);
        } else if (raytraceresult$type == HitResult.Type.BLOCK) {
            BlockHitResult traceResult = (BlockHitResult) raytraceresult;
            BlockState blockstate = this.level.getBlockState(traceResult.getBlockPos());
            if (!blockstate.getBlockSupportShape(this.level, traceResult.getBlockPos()).isEmpty()) {
                Direction face = traceResult.getDirection();
                Vec3 prevMotion = this.getDeltaMovement();
                double motionX = prevMotion.x();
                double motionY = prevMotion.y();
                double motionZ = prevMotion.z();
                switch(face){
                    case EAST:
                    case WEST:
                        motionX = -motionX;
                        break;
                    case SOUTH:
                    case NORTH:
                        motionZ = -motionZ;
                        break;
                    default:
                        motionY = -motionY;
                        break;
                }
                this.setDeltaMovement(motionX, motionY, motionZ);
                if (this.tickCount > 200 || ricochetCount > 20) {
                   this.entityData.set(LAUNCHED, false);
                } else {
                    ricochetCount++;
                }
            }
        }
    }

    public Entity getParent() {
        UUID id = getParentId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    private void onEntityHit(EntityHitResult raytraceresult) {
        Entity entity = this.getParent();
        if (entity instanceof LivingEntity && !level.isClientSide && raytraceresult.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity)raytraceresult.getEntity();
            target.hurt(DamageSource.indirectMobAttack(this, (LivingEntity)entity).setProjectile(), 3.0F);
            target.knockback(0.7F, entity.getX() - this.getX(), entity.getZ() - this.getZ());
            this.entityData.set(LAUNCHED, false);
        }
    }

    protected boolean canHitEntity(Entity p_230298_1_) {
        return !p_230298_1_.isSpectator() && !(p_230298_1_ instanceof EntityStraddler)&& !(p_230298_1_ instanceof EntityStradpole);
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean canStandOnFluid(Fluid p_230285_1_) {
        return p_230285_1_.is(FluidTags.LAVA);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && (this.isInWater() || this.isInLava())) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.05D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    protected void handleAirSupply(int p_209207_1_) {

    }

    public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
        Vec3 lvt_9_1_ = (new Vec3(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.random.nextGaussian() * 0.007499999832361937D * (double) p_70186_8_, this.random.nextGaussian() * 0.007499999832361937D * (double) p_70186_8_, this.random.nextGaussian() * 0.007499999832361937D * (double) p_70186_8_).scale(p_70186_7_);
        this.setDeltaMovement(lvt_9_1_);
        float lvt_10_1_ = (float) lvt_9_1_.horizontalDistanceSqr();
        this.setYRot( (float) (Mth.atan2(lvt_9_1_.x, lvt_9_1_.z) * 57.2957763671875D));
        this.setXRot((float) (Mth.atan2(lvt_9_1_.y, lvt_10_1_) * 57.2957763671875D));
        this.xRotO = this.getXRot();
        this.yBodyRot = getYRot();
        this.yHeadRot = getYRot();
        this.yHeadRotO = getYRot();
        this.yRotO = getYRot();
        this.setDespawnSoon(true);
        this.entityData.set(LAUNCHED, true);
    }

    class StradpoleAISwim extends RandomStrollGoal {
        public StradpoleAISwim(EntityStradpole creature, double speed, int chance) {
            super(creature, speed, chance, false);
        }

        public boolean canUse() {
            if (!this.mob.isInLava() && !this.mob.isInWater() || this.mob.isPassenger() || mob.getTarget() != null || !this.mob.isInWater() && !this.mob.isInLava() && this.mob instanceof ISemiAquatic && !((ISemiAquatic) this.mob).shouldEnterWater()) {
                return false;
            } else {
                if (!this.forceTrigger) {
                    if (this.mob.getRandom().nextInt(this.interval) != 0) {
                        return false;
                    }
                }
                Vec3 vector3d = this.getPosition();
                if (vector3d == null) {
                    return false;
                } else {
                    this.wantedX = vector3d.x;
                    this.wantedY = vector3d.y;
                    this.wantedZ = vector3d.z;
                    this.forceTrigger = false;
                    return true;
                }
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            if (this.mob.getRandom().nextFloat() < 0.3F) {
                Vec3 vector3d = findSurfaceTarget(this.mob, 15, 7);
                if (vector3d != null) {
                    return vector3d;
                }
            }
            Vec3 vector3d = LandRandomPos.getPos(this.mob, 7, 3);

            for (int i = 0; vector3d != null && !this.mob.level.getFluidState(new BlockPos(vector3d)).is(FluidTags.LAVA) && !this.mob.level.getBlockState(new BlockPos(vector3d)).isPathfindable(this.mob.level, new BlockPos(vector3d), PathComputationType.WATER) && i++ < 15; vector3d = LandRandomPos.getPos(this.mob, 10, 7)) {
            }

            return vector3d;
        }

        private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
            BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
            return this.mob.level.getFluidState(blockpos).is(FluidTags.LAVA) || this.mob.level.getFluidState(blockpos).is(FluidTags.WATER) && !this.mob.level.getBlockState(blockpos).getMaterial().blocksMotion();
        }

        private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
            return this.mob.level.getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir() && this.mob.level.getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
        }

        private Vec3 findSurfaceTarget(PathfinderMob creature, int i, int i1) {
            BlockPos upPos = creature.blockPosition();
            while (creature.level.getFluidState(upPos).is(FluidTags.WATER) || creature.level.getFluidState(upPos).is(FluidTags.LAVA)) {
                upPos = upPos.above();
            }
            if (isAirAbove(upPos.below(), 0, 0, 0) && canJumpTo(upPos.below(), 0, 0, 0)) {
                return new Vec3(upPos.getX() + 0.5F, upPos.getY() - 1F, upPos.getZ() + 0.5F);
            }
            return null;
        }
    }

}
