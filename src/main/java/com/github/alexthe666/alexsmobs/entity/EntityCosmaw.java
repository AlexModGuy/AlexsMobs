package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.base.Predicates;
import net.minecraft.advancements.CriteriaTriggers;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class EntityCosmaw extends TamableAnimal implements ITargetsDroppedItems, FlyingAnimal, IFollower {

    private static final EntityDataAccessor<Float> COSMAW_PITCH = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.INT);
    public float clutchProgress;
    public float prevClutchProgress;
    public float openProgress;
    public float prevOpenProgress;
    public float prevCosmawPitch;
    public float biteProgress;
    public float prevBiteProgress;
    private float stuckRot = random.nextInt(3) * 90;
    private UUID fishThrowerID;
    private int heldItemTime;
    private BlockPos lastSafeTpPosition;

    protected EntityCosmaw(EntityType<? extends TamableAnimal> type, Level lvl) {
        super(type, lvl);
        this.moveControl = new FlightMoveController(this, 1F, false, true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.cosmawSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canCosmawSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return !worldIn.getBlockState(pos.below()).isAir();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COSMAW_PITCH, 0.0F);
        this.entityData.define(ATTACK_TICK, 0);
        this.entityData.define(COMMAND, Integer.valueOf(0));
        this.entityData.define(SITTING, Boolean.valueOf(false));

    }

    @Override
    protected void outOfWorld() {
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.entityData.get(ATTACK_TICK) == 0 && this.biteProgress == 0) {
            this.entityData.set(ATTACK_TICK, 5);
        }
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AIAttack());
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FlyingAIFollowOwner(this, 1.3D, 8.0F, 4.0F, false));
        this.goalSelector.addGoal(4, new AIPickupOwner());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.2D));
        this.goalSelector.addGoal(6, new AnimalAITemptDistance(this, 1.1D, Ingredient.of(Items.CHORUS_FRUIT, AMItemRegistry.COSMIC_COD.get()), false, 25) {
            public boolean canUse() {
                return super.canUse() && EntityCosmaw.this.getMainHandItem().isEmpty();
            }

            public boolean canContinueToUse() {
                return super.canContinueToUse() && EntityCosmaw.this.getMainHandItem().isEmpty();
            }
        });
        this.goalSelector.addGoal(7, new RandomFlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems<>(this, true));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this) {
            public boolean canUse() {
                LivingEntity livingentity = this.mob.getLastHurtByMob();
                if (livingentity != null && EntityCosmaw.this.isOwnedBy(livingentity)) {
                    return false;
                }
                return super.canUse();
            }
        }));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, EntityCosmicCod.class, 80, true, false, Predicates.alwaysTrue()));

    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.COSMAW_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.COSMAW_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.COSMAW_HURT.get();
    }

    public boolean isFood(ItemStack stack) {
        return this.isTame() && stack.is(AMItemRegistry.COSMIC_COD.get());
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean isLeftHanded() {
        return false;
    }

    public float getClampedCosmawPitch(float partialTick) {
        float f = prevCosmawPitch + (this.getCosmawPitch() - prevCosmawPitch) * partialTick;
        return Mth.clamp(f, -90, 90);
    }

    public float getCosmawPitch() {
        return this.entityData.get(COSMAW_PITCH);
    }

    public void setCosmawPitch(float pitch) {
        this.entityData.set(COSMAW_PITCH, pitch);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, sit);
    }


    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            final float f = this.walkAnimation.position();
            final float f1 = this.walkAnimation.speed();
            final float bob = (float) (Math.sin(f * 0.7F) * (double) f1 * 0.0625F * 1.6F - (f1 * 0.0625F * 1.6F));
            passenger.setPos(this.getX(), this.getY() - bob + 0.3F - this.getPassengersRidingOffset(), this.getZ());
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("CosmawSitting", this.isSitting());
        compound.putInt("Command", this.getCommand());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("CosmawSitting"));
        this.setCommand(compound.getInt("Command"));
    }

    public void tick() {
        super.tick();
        prevOpenProgress = openProgress;
        prevClutchProgress = clutchProgress;
        prevBiteProgress = biteProgress;
        prevCosmawPitch = this.getCosmawPitch();
        if (!level.isClientSide) {
            final float f2 = (float) -((float) this.getDeltaMovement().y * Maths.oneEightyDividedByFloatPi);
            this.setCosmawPitch(this.getCosmawPitch() + 0.6F * (this.getCosmawPitch() + f2) - this.getCosmawPitch());
        }

        if (isMouthOpen()) {
            if (openProgress < 5F)
                openProgress++;
        } else {
            if (openProgress > 0F)
                openProgress--;
        }

        if (isVehicle()) {
            if (clutchProgress < 5F)
                clutchProgress++;
        } else {
            if (clutchProgress > 0F)
                clutchProgress--;
        }

        if (this.entityData.get(ATTACK_TICK) > 0) {
            if (biteProgress < 5F) {
                biteProgress = Math.min(5F, biteProgress + 2F);
            } else {
                if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 3.3D) {
                    if (this.getTarget() instanceof EntityCosmicCod && !this.isTame()) {
                        EntityCosmicCod fish = (EntityCosmicCod) this.getTarget();
                        CompoundTag fishNbt = new CompoundTag();
                        fish.addAdditionalSaveData(fishNbt);
                        fishNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
                        fish.readAdditionalSaveData(fishNbt);
                    }
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }
                this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            }
        } else {
            if (biteProgress > 0F) {
                biteProgress -= 1F;
            }
        }
        if (!this.getMainHandItem().isEmpty()) {
            heldItemTime++;
            if (heldItemTime > 30 && canTargetItem(this.getMainHandItem())) {
                heldItemTime = 0;
                this.heal(4);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.DOLPHIN_EAT, this.getSoundVolume(), this.getVoicePitch());
                if (this.getMainHandItem().getItem() == AMItemRegistry.COSMIC_COD.get() && fishThrowerID != null && !this.isTame()) {
                    if (getRandom().nextFloat() < 0.3F) {
                        this.setTame(true);
                        this.setCommand(1);
                        this.setOwnerUUID(this.fishThrowerID);
                        Player player = level.getPlayerByUUID(fishThrowerID);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer) player, this);
                        }
                        this.level.broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                    }
                }
                if (this.getMainHandItem().hasCraftingRemainingItem()) {
                    this.spawnAtLocation(this.getMainHandItem().getCraftingRemainingItem());
                }
                this.getMainHandItem().shrink(1);
            }
        } else {
            heldItemTime = 0;
        }
        if (!level.isClientSide) {
            if (tickCount % 100 == 0 || lastSafeTpPosition == null) {
                BlockPos pos = getCosmawGround(this.blockPosition());
                if (pos.getY() > 1) {
                    lastSafeTpPosition = pos;
                }
            }

            if (this.isVehicle()) {
                if (lastSafeTpPosition != null) {
                    final double dist = this.distanceToSqr(Vec3.atCenterOf(lastSafeTpPosition));
                    float speed = 0.8F;
                    if(this.getY() < -40){
                        speed = 3F;
                    }
                    if (verticalCollision && dist > 14) {
                        this.setYRot(this.stuckRot);
                        if (random.nextInt(50) == 0) {
                            this.stuckRot = Mth.wrapDegrees(this.stuckRot + 90);

                        }
                        final float angle = (0.0174532925F * stuckRot);
                        final double extraX = -2 * Mth.sin((float) (Math.PI + angle));
                        final double extraZ = -2 * Mth.cos(angle);
                        this.getMoveControl().setWantedPosition(this.getX() + extraX, this.getY() + 2, this.getZ() + extraZ, speed);
                    } else if (lastSafeTpPosition.getY() > this.getY() + 2.3F) {
                        this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 2, this.getZ(), speed);
                    } else {
                        this.getMoveControl().setWantedPosition(lastSafeTpPosition.getX(), lastSafeTpPosition.getY() + 2, lastSafeTpPosition.getZ(), speed);
                    }
                    if (dist < 7 && getCosmawGround(this.blockPosition()).getY() > 1) {
                        this.ejectPassengers();
                    }
                } else {
                    if (this.getY() < 0F) {
                        this.getDeltaMovement().add(0, 0.75F, 0);
                    } else if (this.getY() < 80F) {
                        this.getDeltaMovement().add(0, 0.1F, 0);
                    }
                }

            }
        }

    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        final InteractionResult type = super.mobInteract(player, hand);
        final InteractionResult interactionresult = stack.interactLivingEntity(player, this, hand);
        if (canTargetItem(stack) && this.getMainHandItem().isEmpty()) {
            final ItemStack rippedStack = stack.copy();
            rippedStack.setCount(1);
            stack.shrink(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
            if (rippedStack.getItem() == AMItemRegistry.COSMIC_COD.get()) {
                fishThrowerID = player.getUUID();
            }
            return InteractionResult.SUCCESS;
        } else if ((this.isTame() && isOwnedBy(player)) && !this.isBaby() && interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
                this.setCommand(0);
            }
            player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
            final boolean sit = this.getCommand() == 2;
            if (sit) {
                this.setOrderedToSit(true);
                return InteractionResult.SUCCESS;
            } else {
                this.setOrderedToSit(false);
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }


    public boolean isMouthOpen() {
        return !this.getMainHandItem().isEmpty();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new DirectPathNavigator(this, level, 0.5F);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            final LivingEntity livingentity = this.getOwner();
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

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return AMEntityRegistry.COSMAW.get().create(level);
    }

    private BlockPos getCosmawGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() < 256 && !level.getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > 1 && level.isEmptyBlock(position)) {
            position = position.below();
        }
        return position;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.COSMIC_COD.get() || stack.getItem() == Items.CHORUS_FRUIT;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        Entity itemThrower = e.getOwner();
        if (e.getItem().getItem() == Items.PUMPKIN_SEEDS && !this.isTame() && itemThrower != null) {
            fishThrowerID = itemThrower.getUUID();
        } else {
            fishThrowerID = null;
        }
    }

    public boolean isTargetBlocked(Vec3 target) {
        final Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1 && !this.isVehicle();
    }

    private boolean shouldWander() {
        if (this.isVehicle()) {
            return false;
        }
        if (this.isTame()) {
            final int command = this.getCommand();
            if (command == 2 || this.isSitting()) {
                return false;
            }
            if (command == 1 && this.getOwner() != null && this.distanceTo(this.getOwner()) < 10) {
                return true;
            }
            return command == 0;
        } else {
            return true;
        }
    }

    public void push(Entity entity) {
        if (!this.isTame() || !(entity instanceof LivingEntity) || !isOwnedBy((LivingEntity) entity)) {
            super.push(entity);
        }
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    static class RandomFlyGoal extends Goal {
        private final EntityCosmaw parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntityCosmaw mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.parentEntity.getNavigation().isDone() && this.parentEntity.shouldWander() && this.parentEntity.getTarget() == null && this.parentEntity.getRandom().nextInt(4) == 0) {
                target = getBlockInViewCosmaw();
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                    return true;
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && this.parentEntity.shouldWander() && parentEntity.getTarget() == null;
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 4D || this.parentEntity.horizontalCollision) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewCosmaw() {
            final float radius = 5 + parentEntity.getRandom().nextInt(10);
            final float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            final float renderYawOffset = parentEntity.getYRot();
            final float angle = (0.0174532925F * renderYawOffset) + 3.15F * (parentEntity.getRandom().nextFloat() * neg);
            final double extraX = radius * Mth.sin((float) (Math.PI + angle));
            final double extraZ = radius * Mth.cos(angle);
            final BlockPos radialPos = AMBlockPos.fromCoords(parentEntity.getX() + extraX, parentEntity.getY(), parentEntity.getZ() + extraZ);
            BlockPos ground = parentEntity.getCosmawGround(radialPos);
            if (ground.getY() <= 1) {
                ground = ground.above(70 + parentEntity.random.nextInt(4));
            } else {
                ground = ground.above(2 + parentEntity.random.nextInt(2));
            }
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
                return ground;
            }
            return null;
        }

    }

    private class AIPickupOwner extends Goal {
        private LivingEntity owner;

        @Override
        public boolean canUse() {
            if (EntityCosmaw.this.isTame() && EntityCosmaw.this.getOwner() != null && !EntityCosmaw.this.isSitting() && !EntityCosmaw.this.getOwner().isPassenger()) {
                if (!EntityCosmaw.this.getOwner().isOnGround() && EntityCosmaw.this.getOwner().fallDistance > 4F) {
                    owner = EntityCosmaw.this.getOwner();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void tick() {
            if (owner != null) {
                if(!owner.isFallFlying() || owner.getY() < -30F) {
                    final double dist = EntityCosmaw.this.distanceTo(owner);
                    if (dist < 3F || owner.getY() <= -50F) {
                        owner.fallDistance = 0.0F;
                        owner.startRiding(EntityCosmaw.this);
                    } else if (dist > 100F || owner.getY() <= -20F) {
                        EntityCosmaw.this.teleportTo(owner.getX(), owner.getY() - 1F, owner.getZ());
                    } else {
                        EntityCosmaw.this.getNavigation().moveTo(owner, 1F + Math.min(dist * 0.3F, 3));
                    }
                }
            }
        }
    }

    private class AIAttack extends Goal {

        public AIAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntityCosmaw.this.getTarget() != null && EntityCosmaw.this.getTarget().isAlive();
        }

        public void tick() {
            if (EntityCosmaw.this.distanceTo(EntityCosmaw.this.getTarget()) < 3D * (EntityCosmaw.this.isBaby() ? 0.5F : 1)) {
                EntityCosmaw.this.doHurtTarget(EntityCosmaw.this.getTarget());
            } else {
                EntityCosmaw.this.getNavigation().moveTo(EntityCosmaw.this.getTarget(), 1);
            }
        }
    }
}
