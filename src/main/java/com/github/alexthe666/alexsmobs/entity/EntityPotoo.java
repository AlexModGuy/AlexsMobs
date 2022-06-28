package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class EntityPotoo extends Animal implements IFalconry {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PERCHING = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Direction> PERCH_DIRECTION = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Integer> MOUTH_TICK = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TEMP_BRIGHTNESS = SynchedEntityData.defineId(EntityPotoo.class, EntityDataSerializers.INT);
    public final float[] ringBuffer = new float[64];
    public float prevFlyProgress;
    public float flyProgress;
    public float mouthProgress;
    public float prevMouthProgress;
    public float prevPerchProgress;
    public float perchProgress;
    public int ringBufferIndex = -1;
    private int lastScreamTimestamp;
    private int perchCooldown = 100;
    private boolean isLandNavigator;
    private int timeFlying;

    protected EntityPotoo(EntityType type, Level level) {
        super(type, level);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0D, Ingredient.of(AMTagRegistry.INSECT_ITEMS), false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(4, new AIPerch());
        this.goalSelector.addGoal(5, new AIMelee());
        this.goalSelector.addGoal(6, new AIFlyIdle());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, EntityFly.class, 100, true, true, null));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 0.6F, false, true);
            this.navigation = new FlyingPathNavigation(this, level) {
                public boolean isStableDestination(BlockPos pos) {
                    return !this.level.getBlockState(pos.below(2)).isAir();
                }
            };
            navigation.setCanFloat(false);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(PERCHING, false);
        this.entityData.define(PERCH_POS, Optional.empty());
        this.entityData.define(PERCH_DIRECTION, Direction.NORTH);
        this.entityData.define(SLEEPING, Boolean.valueOf(false));
        this.entityData.define(MOUTH_TICK, 0);
        this.entityData.define(TEMP_BRIGHTNESS, 0);
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING).booleanValue();
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, Boolean.valueOf(sleeping));
    }

    public static boolean canPotooSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return isBrightEnoughToSpawn(worldIn, pos);
    }

    public boolean checkSpawnObstruction(LevelReader reader) {
        if (reader.isUnobstructed(this) && !reader.containsAnyLiquid(this.getBoundingBox())) {
            BlockPos blockpos = this.blockPosition();
            BlockState blockstate2 = reader.getBlockState(blockpos.below());
            return blockstate2.is(BlockTags.LEAVES) || blockstate2.is(BlockTags.LOGS);
        }
        return false;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.potooSpawnRolls, this.getRandom(), spawnReasonIn);
    }


    public void tick() {
        super.tick();
        this.prevPerchProgress = perchProgress;
        this.prevMouthProgress = mouthProgress;
        this.prevFlyProgress = flyProgress;
        if (this.isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!this.isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (this.isPerching() && perchProgress < 5F) {
            perchProgress++;
        }
        if (!this.isPerching() && perchProgress > 0F) {
            perchProgress--;
        }
        if (this.ringBufferIndex < 0) {
            //initial population of buffer
            for (int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i] = 15;
            }
        }
        this.ringBufferIndex++;
        if (this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }
        this.ringBuffer[this.ringBufferIndex] = this.entityData.get(TEMP_BRIGHTNESS);
        if (perchCooldown > 0) {
            perchCooldown--;
        }
        if (!level.isClientSide) {
            this.entityData.set(TEMP_BRIGHTNESS, level.getMaxLocalRawBrightness(this.blockPosition()));
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.isFlying()) {
                if (this.isFlying() && !this.onGround) {
                    if (!this.isInWaterOrBubble()) {
                        this.setDeltaMovement(this.getDeltaMovement().multiply(1F, 0.6F, 1F));
                    }
                }
                if (this.isOnGround() && timeFlying > 20) {
                    this.setFlying(false);
                }
                this.timeFlying++;
            } else {
                this.timeFlying = 0;
            }
            if (this.isPerching() && !this.isVehicle()) {
                this.setSleeping(this.level.isDay() && (this.getTarget() == null || !this.getTarget().isAlive()));
            } else if (isSleeping()) {
                this.setSleeping(false);
            }
            if(isPerching() && this.getPerchPos() != null) {
                if ((!level.getBlockState(this.getPerchPos()).is(AMTagRegistry.POTOO_PERCHES) || this.distanceToSqr(Vec3.atCenterOf(this.getPerchPos())) > 2.25F)) {
                    this.setPerching(false);
                } else {
                    slideTowardsPerch();
                }
            }
        }
        if (this.entityData.get(MOUTH_TICK) > 0) {
            this.entityData.set(MOUTH_TICK, this.entityData.get(MOUTH_TICK) - 1);
            if (mouthProgress < 5F) {
                mouthProgress++;
            }
        } else {
            if (mouthProgress > 0F) {
                mouthProgress--;
            }
        }
        if (!isSleeping() && (this.getTarget() == null || !this.getTarget().isAlive())) {
            int j = tickCount - lastScreamTimestamp;
            if (getEyeScale(10, 1.0F) == 0F) {
                if (j > 40) {
                    this.openMouth(30);
                    this.playSound(AMSoundRegistry.POTOO_CALL.get());
                    this.gameEvent(GameEvent.ENTITY_ROAR);
                }
            } else if (getEyeScale(10, 1.0F) < 7) {
                if (j > 300 && j % 300 == 0 && random.nextInt(4) == 0) {
                    this.openMouth(30);
                    this.playSound(AMSoundRegistry.POTOO_CALL.get());
                    this.gameEvent(GameEvent.ENTITY_ROAR);
                }
            }
        }
    }

    public float getHandOffset(){
        return 1.0F;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.POTOO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.POTOO_HURT.get();
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev && source.getDirectEntity() instanceof LivingEntity) {
            this.setPerching(false);
        }
        return prev;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && (!entity.isAlive() || !this.isAlive())) {
            this.stopRiding();
        } else if (entity instanceof LivingEntity) {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            this.setFlying(false);
            this.setSleeping(false);
            this.setPerching(false);
            if (this.isPassenger()) {
                Entity mount = this.getVehicle();
                if (mount instanceof Player) {
                    float yawAdd = 0;
                    if (((Player) mount).getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                        yawAdd = ((Player) mount).getMainArm() == HumanoidArm.LEFT ? 135 : -135;
                    } else if (((Player) mount).getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get()) {
                        yawAdd = ((Player) mount).getMainArm() == HumanoidArm.LEFT ? -135 : 135;
                    } else {
                        this.removeVehicle();
                        this.copyPosition(mount);
                    }
                    float birdYaw = yawAdd * 0.5F;
                    this.yBodyRot = Mth.wrapDegrees(((LivingEntity) mount).yBodyRot + birdYaw);
                    this.setYRot(Mth.wrapDegrees(mount.getYRot() + birdYaw));
                    this.yHeadRot = Mth.wrapDegrees(((LivingEntity) mount).yHeadRot + birdYaw);
                    float radius = 0.6F;
                    float angle = (0.01745329251F * (((LivingEntity) mount).yBodyRot - 180F + yawAdd));
                    double extraX = radius * Mth.sin((float) (Math.PI + angle));
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

    public void openMouth(int duration) {
        this.entityData.set(MOUTH_TICK, duration);
        lastScreamTimestamp = tickCount;
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if (flying && isBaby()) {
            return;
        }
        this.entityData.set(FLYING, flying);
    }

    public BlockPos getPerchPos() {
        return this.entityData.get(PERCH_POS).orElse(null);
    }

    public void setPerchPos(BlockPos pos) {
        this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
    }

    public Direction getPerchDirection() {
        return this.entityData.get(PERCH_DIRECTION);
    }

    public void setPerchDirection(Direction direction) {
        this.entityData.set(PERCH_DIRECTION, direction);
    }

    public boolean isPerching() {
        return this.entityData.get(PERCHING).booleanValue();
    }

    public void setPerching(boolean perching) {
        this.entityData.set(PERCHING, perching);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("Perching", this.isPerching());
        compound.putInt("PerchDir", this.getPerchDirection().ordinal());
        if (this.getPerchPos() != null) {
            compound.putInt("PerchX", this.getPerchPos().getX());
            compound.putInt("PerchY", this.getPerchPos().getY());
            compound.putInt("PerchZ", this.getPerchPos().getZ());
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setPerching(compound.getBoolean("Perching"));
        this.setPerchDirection(Direction.from3DDataValue(compound.getInt("PerchDir")));
        if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
    }

    public boolean isValidPerchFromSide(BlockPos pos, Direction direction) {
        BlockPos offset = pos.relative(direction);
        BlockState state = level.getBlockState(pos);
        return state.is(AMTagRegistry.POTOO_PERCHES) && (!level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above()) || level.isEmptyBlock(pos.above())) && (!level.getBlockState(offset).isCollisionShapeFullBlock(level, offset) && !level.getBlockState(offset).is(AMTagRegistry.POTOO_PERCHES) || level.isEmptyBlock(offset));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return AMEntityRegistry.POTOO.get().create(serverLevel);
    }

    public float getEyeScale(int bufferOffset, float partialTicks) {
        int i = (this.ringBufferIndex - bufferOffset) & 63;
        int j = (this.ringBufferIndex - bufferOffset - 1) & 63;
        float prevBuffer = this.ringBuffer[j];
        float buffer = this.ringBuffer[i];
        return prevBuffer + (buffer - prevBuffer) * partialTicks;
    }

    private void slideTowardsPerch() {
        Vec3 block = Vec3.upFromBottomCenterOf(this.getPerchPos(), 1.0F);
        Vec3 look = block.subtract(this.position()).normalize();
        Vec3 onBlock = block.add(this.getPerchDirection().getStepX() * 0.35F, 0F, this.getPerchDirection().getStepZ() * 0.35F);
        Vec3 diff = onBlock.subtract(this.position());
        float f = (float)diff.length();
        float f1 = f > 1F ? 0.25F : f * 0.1F;
        Vec3 sub = diff.normalize().scale(f1);
        float f2 = -(float) (Mth.atan2(look.x, look.z) * (double) (180F / (float) Math.PI));
        EntityPotoo.this.setYRot(f2);
        EntityPotoo.this.yHeadRot = f2;
        EntityPotoo.this.yBodyRot = f2;

        this.setDeltaMovement(this.getDeltaMovement().add(sub));

    }

    public BlockPos getToucanGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getY(), in.getZ());
        while (position.getY() < 320 && !level.getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > -64 && !level.getBlockState(position).getMaterial().isSolidBlocking() && level.getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return position;
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        float radius = 10 + this.getRandom().nextInt(15);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getToucanGround(radialPos);
        if (ground.getY() < -64) {
            return null;
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -64 && !level.getBlockState(ground).getMaterial().isSolidBlocking()) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground.below());
        }
        return null;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (!this.isBaby() && getRidingFalcons(player) <= 0 && (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get() || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE.get())) {
            boardingCooldown = 30;
            this.ejectPassengers();
            this.startRiding(player, true);
            if (!level.isClientSide) {
                AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.getId(), player.getId()));
            }
            return InteractionResult.SUCCESS;
        } else {
            return type;
        }
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 5 + radiusAdd + this.getRandom().nextInt(5);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.x() + extraX, 0, fleePos.z() + extraZ);
        BlockPos ground = getToucanGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 5 + this.getRandom().nextInt(5);
        int j = this.getRandom().nextInt(5) + 5;

        BlockPos newPos = ground.above(distFromGround > 5 ? flightHeight : j);
        if (level.getBlockState(ground).is(BlockTags.LEAVES)) {
            newPos = ground.above(1 + this.getRandom().nextInt(3));
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -65 && level.isEmptyBlock(position)) {
            position = position.below();
        }
        return !level.getFluidState(position).isEmpty() || level.getBlockState(position).is(Blocks.VINE) || position.getY() <= -65;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.INSECT_ITEMS);
    }

    @Override
    public void onLaunch(Player player, Entity pointedEntity) {

    }

    private class AIFlyIdle extends Goal {
        protected double x;
        protected double y;
        protected double z;

        public AIFlyIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (EntityPotoo.this.isVehicle() || EntityPotoo.this.isPerching() || (EntityPotoo.this.getTarget() != null && EntityPotoo.this.getTarget().isAlive()) || EntityPotoo.this.isPassenger()) {
                return false;
            } else {
                if (EntityPotoo.this.getRandom().nextInt(45) != 0 && !EntityPotoo.this.isFlying()) {
                    return false;
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
            EntityPotoo.this.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1F);
            if (isFlying() && EntityPotoo.this.onGround && EntityPotoo.this.timeFlying > 10) {
                EntityPotoo.this.setFlying(false);
            }
        }

        @javax.annotation.Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = EntityPotoo.this.position();
            if (EntityPotoo.this.timeFlying < 200 || EntityPotoo.this.isOverWaterOrVoid()) {
                return EntityPotoo.this.getBlockInViewAway(vector3d, 0);
            } else {
                return EntityPotoo.this.getBlockGrounding(vector3d);
            }
        }

        public boolean canContinueToUse() {
            return EntityPotoo.this.isFlying() && EntityPotoo.this.distanceToSqr(x, y, z) > 5F;
        }

        public void start() {
            EntityPotoo.this.setFlying(true);
            EntityPotoo.this.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1F);
        }

        public void stop() {
            EntityPotoo.this.getNavigation().stop();
            x = 0;
            y = 0;
            z = 0;
            super.stop();
        }

    }

    private class AIPerch extends Goal {
        private BlockPos perch = null;
        private Direction perchDirection = null;
        private int perchingTime = 0;

        public AIPerch() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (EntityPotoo.this.getTarget() != null && EntityPotoo.this.getTarget().isAlive()) {
                return false;
            }
            if (!EntityPotoo.this.isPerching() && EntityPotoo.this.perchCooldown == 0 && EntityPotoo.this.random.nextInt(25) == 0) {
                this.perchingTime = 0;
                if (EntityPotoo.this.getPerchPos() != null && EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getPerchPos(), EntityPotoo.this.getPerchDirection())) {
                    perch = EntityPotoo.this.getPerchPos();
                    perchDirection = EntityPotoo.this.getPerchDirection();
                } else {
                    findPerch();
                }
                return perch != null && perchDirection != null;
            }
            return false;
        }

        private void findPerch() {
            RandomSource random = EntityPotoo.this.getRandom();
            Direction[] horiz = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
            if (isValidPerchFromSide(EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement(), EntityPotoo.this.getDirection())) {
                perch = EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement();
                perchDirection = EntityPotoo.this.getDirection();
                return;
            }
            for(Direction dir : horiz){
                if (isValidPerchFromSide(EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement(), dir)) {
                    perch = EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement();
                    perchDirection = dir;
                    return;
                }
            }
            int range = 14;
            for (int i = 0; i < 15; i++) {
                BlockPos blockpos1 = EntityPotoo.this.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                while (EntityPotoo.this.level.isEmptyBlock(blockpos1) && blockpos1.getY() > -64) {
                    blockpos1 = blockpos1.below();
                }
                Direction dir = Direction.from2DDataValue(random.nextInt(3));
                if (isValidPerchFromSide(blockpos1, dir)) {
                    perch = blockpos1;
                    perchDirection = dir;
                    break;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return (perchingTime < 300 || EntityPotoo.this.level.isDay()) && (EntityPotoo.this.getTarget() == null || !EntityPotoo.this.getTarget().isAlive()) && !EntityPotoo.this.isPassenger();
        }

        public void tick() {
            if (EntityPotoo.this.isPerching()) {
                perchingTime++;
                EntityPotoo.this.getNavigation().stop();
                Vec3 block = Vec3.upFromBottomCenterOf(EntityPotoo.this.getPerchPos(), 1.0F);
                Vec3 onBlock = block.add(EntityPotoo.this.getPerchDirection().getStepX() * 0.35F, 0F, EntityPotoo.this.getPerchDirection().getStepZ() * 0.35F);
                double dist = EntityPotoo.this.distanceToSqr(onBlock);
                Vec3 dirVec = block.subtract(EntityPotoo.this.position());
                if (perchingTime > 10 && (dist > 2.3F || !EntityPotoo.this.isValidPerchFromSide(EntityPotoo.this.getPerchPos(), EntityPotoo.this.getPerchDirection()))) {
                    EntityPotoo.this.setPerching(false);
                } else if (dist > 1F) {
                    EntityPotoo.this.slideTowardsPerch();
                    if (EntityPotoo.this.getPerchPos().getY() + 1.2F > EntityPotoo.this.getBoundingBox().minY) {
                        EntityPotoo.this.setDeltaMovement(EntityPotoo.this.getDeltaMovement().add(0, 0.2F, 0));
                    }
                    float f = -(float) (Mth.atan2(dirVec.x, dirVec.z) * (double) (180F / (float) Math.PI));
                    EntityPotoo.this.setYRot(f);
                    EntityPotoo.this.yHeadRot = f;
                    EntityPotoo.this.yBodyRot = f;
                }
            } else if (perch != null) {
                if (EntityPotoo.this.distanceToSqr(Vec3.atCenterOf(perch)) > 100) {
                    EntityPotoo.this.setFlying(true);
                }
                double distX = perch.getX() + 0.5F - EntityPotoo.this.getX();
                double distZ = perch.getZ() + 0.5F - EntityPotoo.this.getZ();
                if (distX * distX + distZ * distZ < 1F || !EntityPotoo.this.isFlying()) {
                    EntityPotoo.this.getNavigation().moveTo(perch.getX() + 0.5F, perch.getY() + 1.5F, perch.getZ() + 0.5F, 1F);
                    if(EntityPotoo.this.getNavigation().isDone()){
                        EntityPotoo.this.getMoveControl().setWantedPosition(perch.getX() + 0.5F, perch.getY() + 1.5F, perch.getZ() + 0.5F, 1F);

                    }
                } else {
                    EntityPotoo.this.getNavigation().moveTo(perch.getX() + 0.5F, perch.getY() + 2.5F, perch.getZ() + 0.5F, 1F);
                }
                if (EntityPotoo.this.getBlockPosBelowThatAffectsMyMovement().equals(perch)) {
                    EntityPotoo.this.setDeltaMovement(Vec3.ZERO);
                    EntityPotoo.this.setPerching(true);
                    EntityPotoo.this.setFlying(false);
                    EntityPotoo.this.setPerchPos(perch);
                    EntityPotoo.this.setPerchDirection(perchDirection);
                    EntityPotoo.this.getNavigation().stop();
                    perch = null;
                } else {
                    EntityPotoo.this.setPerching(false);
                }
            }
        }

        public void stop() {
            EntityPotoo.this.setPerching(false);
            EntityPotoo.this.perchCooldown = 120 + random.nextInt(1200);
            this.perch = null;
            this.perchDirection = null;
        }
    }

    private class AIMelee extends Goal {

        private int biteCooldown = 0;

        public AIMelee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !EntityPotoo.this.isSleeping() && !EntityPotoo.this.isPassenger() && EntityPotoo.this.getTarget() != null && EntityPotoo.this.getTarget().isAlive();
        }

        public void tick() {
            if (biteCooldown > 0) {
                biteCooldown--;
            }
            LivingEntity entity = EntityPotoo.this.getTarget();
            if (entity != null) {
                EntityPotoo.this.setFlying(true);
                EntityPotoo.this.setPerching(false);
                EntityPotoo.this.getMoveControl().setWantedPosition(entity.getX(), entity.getY(0.5F), entity.getZ(), 1.5F);
                if (EntityPotoo.this.distanceTo(entity) < 1.4F) {
                    if (biteCooldown == 0) {
                        EntityPotoo.this.openMouth(7);
                        biteCooldown = 10;
                    }
                    if (EntityPotoo.this.mouthProgress >= 4.5F) {
                        entity.hurt(DamageSource.mobAttack(EntityPotoo.this), 2);
                        if (entity.getBbWidth() <= 0.5F) {
                            entity.remove(RemovalReason.KILLED);
                        }
                    }
                }
            }

        }
    }

}
