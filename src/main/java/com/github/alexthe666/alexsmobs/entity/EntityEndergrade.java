package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EndergradeAIBreakFlowers;
import com.github.alexthe666.alexsmobs.entity.ai.EndergradeAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIRide;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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

public class EntityEndergrade extends Animal implements FlyingAnimal {

    private static final EntityDataAccessor<Integer> BITE_TICK = SynchedEntityData.defineId(EntityEndergrade.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(EntityEndergrade.class, EntityDataSerializers.BOOLEAN);
    public float tartigradePitch = 0;
    public float prevTartigradePitch = 0;
    public float biteProgress = 0;
    public float prevBiteProgress = 0;
    public boolean stopWandering = false;
    public boolean hasItemTarget = false;

    protected EntityEndergrade(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new EntityEndergrade.MoveHelperController(this);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    public static boolean canEndergradeSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return !worldIn.getBlockState(pos.below()).isAir();
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new DirectPathNavigator(this, worldIn);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Saddled", this.isSaddled());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSaddled(compound.getBoolean("Saddled"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BITE_TICK, 0);
        this.entityData.define(SADDLED, Boolean.valueOf(false));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EndergradeAIBreakFlowers(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.2D) {
            public void start() {
                super.start();
                EntityEndergrade.this.stopWandering = true;
            }

            public void stop() {
                super.stop();
                EntityEndergrade.this.stopWandering = false;
            }
        });
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.CHORUS_FRUIT), false) {
            public void start() {
                super.start();
                EntityEndergrade.this.stopWandering = true;
            }

            public void stop() {
                super.stop();
                EntityEndergrade.this.stopWandering = false;
            }
        });
        this.goalSelector.addGoal(4, new RandomFlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new EndergradeAITargetItems(this, true));
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                Player player = (Player) passenger;
                if (player.getMainHandItem().getItem() == AMItemRegistry.CHORUS_ON_A_STICK.get() || player.getOffhandItem().getItem() == AMItemRegistry.CHORUS_ON_A_STICK.get()) {
                    return player;
                }
            }
        }
        return null;
    }
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ENDERGRADE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ENDERGRADE_HURT.get();
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (item == Items.SADDLE && !this.isSaddled()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setSaddled(true);
            return InteractionResult.SUCCESS;
        }
        if (item == Items.CHORUS_FRUIT && this.hasEffect(AMEffectRegistry.ENDER_FLU.get())) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.heal(8);
            this.removeEffect(AMEffectRegistry.ENDER_FLU.get());
            return InteractionResult.SUCCESS;
        }
        InteractionResult type = super.mobInteract(player, hand);
        if (type != InteractionResult.SUCCESS && !isFood(itemstack)) {
            if (!player.isShiftKeyDown() && this.isSaddled()) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.CHORUS_FRUIT;
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            float radius = -0.25F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        return (double) this.getBbHeight() - 0.1D + (double) (0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f);
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED).booleanValue();
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, Boolean.valueOf(saddled));
    }

    public void tick() {
        super.tick();
        prevTartigradePitch = this.tartigradePitch;
        prevBiteProgress = this.biteProgress;
        float f2 = (float) -((float) this.getDeltaMovement().y * 3 * (double) (180F / (float) Math.PI));
        this.tartigradePitch = f2;
        if (this.getDeltaMovement().lengthSqr() > 0.005F) {
            float angleMotion = (0.01745329251F * this.yBodyRot);
            double extraXMotion = -0.2F * Mth.sin((float) (Math.PI + angleMotion));
            double extraZMotion = -0.2F * Mth.cos(angleMotion);
            this.level.addParticle(ParticleTypes.END_ROD, this.getRandomX(0.5D), this.getY() + 0.3, this.getRandomZ(0.5D), extraXMotion, 0D, extraZMotion);
        }
        int tick = this.entityData.get(BITE_TICK);
        if (tick > 0) {
            this.entityData.set(BITE_TICK, tick - 1);
            this.biteProgress++;
        } else if (biteProgress > 0) {
            biteProgress--;
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    private BlockPos getGroundPosition(BlockPos radialPos) {
        while (radialPos.getY() > 1 && level.isEmptyBlock(radialPos)) {
            radialPos = radialPos.below();
        }
        if (radialPos.getY() <= 1) {
            return new BlockPos(radialPos.getX(), level.getSeaLevel(), radialPos.getZ());
        }
        return radialPos;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem() == Items.CHORUS_FRUIT || stack.getItem() == Items.CHORUS_FLOWER;
    }

    public void onGetItem(ItemEntity targetEntity) {
        this.gameEvent(GameEvent.EAT);
        this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
        this.heal(5);
    }

    public void bite() {
        this.entityData.set(BITE_TICK, 5);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.endergradeSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.ENDERGRADE.get().create(p_241840_1_);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(Items.SADDLE);
            }
        }
    }

    protected Vec3 getRiddenInput(LivingEntity player, Vec3 deltaIn) {
        if (player.zza != 0) {
            this.setSprinting(true);
            Vec3 lookVec = player.getLookAngle();
            if (player.zza < 0) {
                lookVec = lookVec.yRot((float) Math.PI);
            }
            double y = lookVec.y * 0.35F;
            return new Vec3(player.xxa, y, player.zza);
        } else {
            this.setSprinting(false);
        }
        return Vec3.ZERO;
    }
    protected void tickRidden(LivingEntity player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if(player.zza != 0 || player.xxa != 0){
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
            this.maxUpStep = 1;
            this.getNavigation().stop();
            this.setTarget(null);
            this.setSprinting(true);
        }
    }

    protected float getRiddenSpeed(LivingEntity rider) {
        return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isOnGround() ? 0.2F : 0.8F));
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    static class RandomFlyGoal extends Goal {
        private final EntityEndergrade parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntityEndergrade mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl movementcontroller = this.parentEntity.getMoveControl();
            if (parentEntity.stopWandering || parentEntity.hasItemTarget) {
                return false;
            }
            if (!movementcontroller.hasWanted() || target == null) {
                target = getBlockInViewEndergrade();
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && !parentEntity.stopWandering && !parentEntity.hasItemTarget && parentEntity.distanceToSqr(Vec3.atCenterOf(target)) > 2.4D && parentEntity.getMoveControl().hasWanted() && !parentEntity.horizontalCollision;
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewEndergrade();
            }
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 2.5F) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewEndergrade() {
            float radius = 1 + parentEntity.getRandom().nextInt(5);
            float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.yBodyRot;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (parentEntity.getRandom().nextFloat() * neg);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = AMBlockPos.fromCoords(parentEntity.getX() + extraX, parentEntity.getY() + 2, parentEntity.getZ() + extraZ);
            BlockPos ground = parentEntity.getGroundPosition(radialPos);
            BlockPos newPos = ground.above(1 + parentEntity.getRandom().nextInt(6));
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6) {
                return newPos;
            }
            return null;
        }
    }

    static class MoveHelperController extends MoveControl {
        private final EntityEndergrade parentEntity;

        public MoveHelperController(EntityEndergrade sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        public void tick() {
            if (this.operation == Operation.STRAFE) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0, vector3d.scale(this.speedModifier * 0.05D / d0).y(), 0));
                float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float f1 = (float) this.speedModifier * f;
                float f2 = this.strafeForwards;
                float f3 = this.strafeRight;
                float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
                if (f4 < 1.0F) {
                    f4 = 1.0F;
                }

                f4 = f1 / f4;
                f2 = f2 * f4;
                f3 = f3 * f4;
                float f5 = Mth.sin(this.mob.getYRot() * ((float) Math.PI / 180F));
                float f6 = Mth.cos(this.mob.getYRot() * ((float) Math.PI / 180F));
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;

                this.mob.setSpeed(f1);
                this.mob.setZza(this.strafeForwards);
                this.mob.setXxa(this.strafeRight);
                this.operation = MoveControl.Operation.WAIT;
            } else if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                if (d0 < parentEntity.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    double localSpeed = this.speedModifier;
                    if (parentEntity.isVehicle()) {
                        localSpeed *= 1.5D;
                    }
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(localSpeed * 0.005D / d0)));
                    if (parentEntity.getTarget() == null) {
                        Vec3 vector3d1 = parentEntity.getDeltaMovement();
                        parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    } else {
                        double d2 = parentEntity.getTarget().getX() - parentEntity.getX();
                        double d1 = parentEntity.getTarget().getZ() - parentEntity.getZ();
                        parentEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.parentEntity.level.noCollision(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }
}
