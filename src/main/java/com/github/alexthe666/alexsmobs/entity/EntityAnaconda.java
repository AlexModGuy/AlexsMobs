package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.AnacondaPartIndex;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class EntityAnaconda extends Animal implements ISemiAquatic {

    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> STRANGLING = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> YELLOW = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SHEDTIME = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.INT);
    public final float[] ringBuffer = new float[64];
    public int ringBufferIndex = -1;
    private EntityAnacondaPart[] parts;
    private float prevStrangleProgress = 0F;
    private float strangleProgress = 0F;
    private int strangleTimer = 0;
    private int shedCooldown = 0;
    private int feedings = 0;
    private boolean isLandNavigator;
    private int swimTimer = -1000;

    protected EntityAnaconda(EntityType t, Level world) {
        super(t, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(true);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ANACONDA_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ANACONDA_HURT;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!isBaby()) {
            this.playSound(AMSoundRegistry.ANACONDA_SLITHER, 1.0F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    public static boolean canAnacondaSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random randomIn) {
        boolean spawnBlock = BlockTags.getAllTags().getTag(AMTagRegistry.ANACONDA_SPAWNS).contains(worldIn.getBlockState(pos.below()).getBlock());
        return spawnBlock && pos.getY() < worldIn.getSeaLevel() + 4;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.anacondaSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AnimalSwimMoveControllerSink(this, 1.3F, 1F);
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(2, new AIMelee());
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25D, Ingredient.of(Items.CHICKEN, Items.COOKED_CHICKEN), false));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 60, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new SemiAquaticAIRandomSwimming(this, 1.5D, 7));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 25F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, LivingEntity.class, 200, false, false, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getAllTags().getTag(AMTagRegistry.ANACONDA_TARGETS))));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 110, false, true, null) {
            public boolean canUse() {
                return !isBaby() && level.getDifficulty() != Difficulty.PEACEFUL && !EntityAnaconda.this.isInLove() && super.canUse();
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    protected float getStandingEyeHeight(Pose p_33799_, EntityDimensions p_33800_) {
        return this.isBaby() ? 0.15F : 0.3F;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        if (isFood(itemstack)) {
            this.setTarget(null);
        }
        return super.mobInteract(player, hand);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }
        feedings = compound.getInt("Feedings");
        this.setSheddingTime(compound.getInt("ShedTime"));
        this.setYellow(compound.getBoolean("Yellow"));
        shedCooldown = compound.getInt("ShedCooldown");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }
        compound.putInt("Feedings", feedings);
        compound.putInt("ShedTime", getSheddingTime());
        compound.putBoolean("Yellow", isYellow());
        compound.putInt("ShedCooldown", shedCooldown);
    }


    public void pushEntities() {
        final List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        entities.stream().filter(entity -> !(entity instanceof EntityAnacondaPart) && entity.isPushable()).forEach(entity -> entity.push(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHILD_UUID, Optional.empty());
        this.entityData.define(CHILD_ID, -1);
        this.entityData.define(STRANGLING, false);
        this.entityData.define(YELLOW, false);
        this.entityData.define(SHEDTIME, 0);
    }

    @Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public int getSheddingTime() {
        return this.entityData.get(SHEDTIME);
    }

    public void setSheddingTime(int shedtime) {
        this.entityData.set(SHEDTIME, shedtime);
    }

    public boolean isStrangling() {
        return this.entityData.get(STRANGLING);
    }

    public void setStrangling(boolean running) {
        this.entityData.set(STRANGLING, running);
    }

    public boolean isYellow() {
        return this.entityData.get(YELLOW);
    }

    public void setYellow(boolean yellow) {
        this.entityData.set(YELLOW, yellow);
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 3;
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public void tick() {
        super.tick();

        if (this.isInWater()) {
            if (this.isLandNavigator)
                switchNavigator(false);
        } else {
            if (!this.isLandNavigator)
                switchNavigator(true);
        }

        this.prevStrangleProgress = strangleProgress;
        if (this.isStrangling()) {
            if (strangleProgress < 5F)
                strangleProgress++;
        } else {
            if (strangleProgress > 0F)
                strangleProgress--;
        }

        this.yBodyRot = this.getYRot();
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70, this.yBodyRot + 70);

        if (this.isStrangling()) {
            if (!level.isClientSide && this.getTarget() != null && this.getTarget().isAlive()) {
                this.setXRot(0);
                final LivingEntity target = this.getTarget();
                final float radius = this.getTarget().getBbWidth() * -0.5F;
                final float angle = (0.0174532925F * (target.yBodyRot - 45F));
                final double extraX = radius * Mth.sin((float) (Math.PI + angle));
                final double extraZ = radius * Mth.cos(angle);
//                double extraY = -0.5F;
                this.setPosRaw(extraX + target.getX(), target.getY(1.0F), extraZ + target.getZ());
                if (!target.isOnGround()) {
                    target.setDeltaMovement(new Vec3(0, -0.08F, 0));
                } else {
                    target.setDeltaMovement(Vec3.ZERO);
                }
                if (strangleTimer >= 40 && strangleTimer % 20 == 0) {
                    final double health = Mth.clamp(this.getTarget().getMaxHealth(), 4, 50);
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) Math.max(4F, 0.25F * health));
                }
                if (this.getTarget() == null || !this.getTarget().isAlive()) {
                    strangleTimer = 0;
                    this.setStrangling(false);
                }
            }
            fallDistance = 0;
            strangleTimer++;
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }
        if (this.ringBufferIndex < 0) {
            for (int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i] = this.getYRot();
            }
        }
        this.ringBufferIndex++;
        if (this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }
        this.ringBuffer[this.ringBufferIndex] = this.getYRot();

        if (!level.isClientSide) {
            final int segments = 7;
            final Entity child = getChild();
            if (child == null) {
                LivingEntity partParent = this;
                parts = new EntityAnacondaPart[segments];
                AnacondaPartIndex partIndex = AnacondaPartIndex.HEAD;
                Vec3 prevPos = this.position();
                for (int i = 0; i < segments; i++) {
                    final float prevReqRot = calcPartRotation(i) + getYawForPart(i);
                    final float reqRot = calcPartRotation(i + 1) + getYawForPart(i);
                    EntityAnacondaPart part = new EntityAnacondaPart(AMEntityRegistry.ANACONDA_PART.get(), this);
                    part.setParent(partParent);
                    part.copyDataFrom(this);
                    part.setBodyIndex(i);
                    part.setPartType(AnacondaPartIndex.sizeAt(1 + i));
                    if (partParent == this) {
                        this.setChildId(part.getUUID());
                        this.entityData.set(CHILD_ID, part.getId());
                    }
                    if (partParent instanceof EntityAnacondaPart) {
                        ((EntityAnacondaPart) partParent).setChildId(part.getUUID());
                    }
                    part.setPos(part.tickMultipartPosition(this.getId(), partIndex, prevPos, this.getXRot(), prevReqRot, reqRot, false));
                    partParent = part;
                    level.addFreshEntity(part);
                    parts[i] = part;
                    partIndex = part.getPartType();
                    prevPos = part.position();
                }
            }
            if (shouldReplaceParts() && this.getChild() instanceof EntityAnacondaPart) {
                parts = new EntityAnacondaPart[segments];
                parts[0] = (EntityAnacondaPart) this.getChild();
                this.entityData.set(CHILD_ID, parts[0].getId());
                int i = 1;
                while (i < parts.length && parts[i - 1].getChild() instanceof EntityAnacondaPart) {
                    parts[i] = (EntityAnacondaPart) parts[i - 1].getChild();
                    i++;
                }
            }
            AnacondaPartIndex partIndex = AnacondaPartIndex.HEAD;
            Vec3 prev = this.position();
            float xRot = this.getXRot();
//                float yRot = this.getYRot();
//                float headRot = Mth.wrapDegrees(this.getYRot());
            for (int i = 0; i < segments; i++) {
                if (this.parts[i] != null) {
                    final float prevReqRot = calcPartRotation(i) + getYawForPart(i);
                    final float reqRot = calcPartRotation(i + 1) + getYawForPart(i);
                    parts[i].setStrangleProgress(this.strangleProgress);
                    parts[i].copyDataFrom(this);
                    prev = parts[i].tickMultipartPosition(this.getId(), partIndex, prev, xRot, prevReqRot, reqRot, true);
                    partIndex = parts[i].getPartType();
                    xRot = parts[i].getXRot();
                }
            }

            if (isInWater()) swimTimer = Math.max(swimTimer + 1, 0);
            else swimTimer = Math.min(swimTimer - 1, 0);
        }
        if (shedCooldown > 0) {
            shedCooldown--;
        }
        if (this.getSheddingTime() > 0) {
            this.setSheddingTime(this.getSheddingTime() - 1);
            if (this.getSheddingTime() == 0) {
                this.spawnItemAtOffset(new ItemStack(AMItemRegistry.SHED_SNAKE_SKIN.get()), 1 + random.nextFloat(), 0.2F);
                shedCooldown = 1000 + random.nextInt(2000);
            }
        }
    }

    private boolean shouldReplaceParts() {
        if (parts == null || parts[0] == null)
            return true;

        for (int i = 0; i < 7; i++) {
            if (parts[i] == null) {
                return true;
            }
        }

        return false;
    }

    private float getYawForPart(int i) {
        return this.getRingBuffer(4 + i * 2, 1.0F);
    }

    public float getRingBuffer(int bufferOffset, float partialTicks) {
        if (this.isDeadOrDying()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        final int i = this.ringBufferIndex - bufferOffset & 63;
        final int j = this.ringBufferIndex - bufferOffset - 1 & 63;
        final float d0 = this.ringBuffer[i];
        final float d1 = this.ringBuffer[j] - d0;
        return Mth.wrapDegrees(d0 + d1 * partialTicks);
    }

    public float getScale() {
        return this.isBaby() ? 0.75F : 1.0F;
    }

    public boolean isPushable() {
        return !this.isStrangling();
    }

    public boolean shouldMove() {
        return !this.isStrangling();
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.CHICKEN || stack.getItem() == Items.COOKED_CHICKEN;
    }

    public void travel(Vec3 travelVector) {
        if (!this.shouldMove()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    public float getStrangleProgress(float partialTick) {
        return this.prevStrangleProgress + (this.strangleProgress - this.prevStrangleProgress) * partialTick;
    }

    private float calcPartRotation(int i) {
        final float f = 1 - (this.strangleProgress * 0.2F);
        final float strangleIntensity = (float) (Mth.clamp(strangleTimer * 3, 0, 100F) * (1.0F + 0.2F * Math.sin(0.15F * strangleTimer)));
        return (float) (40 * -Math.sin(this.walkDist * 3 - (i))) * f + this.strangleProgress * 0.2F * i * strangleIntensity;
    }

    @Nullable
    public ItemEntity spawnItemAtOffset(ItemStack stack, float f, float f1) {
        if (stack.isEmpty()) {
            return null;
        } else if (this.level.isClientSide) {
            return null;
        } else {
            final Vec3 vec = new Vec3(0, 0, f).yRot(-f * ((float) Math.PI / 180F));
            final ItemEntity itementity = new ItemEntity(this.level, this.getX() + vec.x, this.getY() + (double) f1, this.getZ() + vec.z, stack);
            itementity.setDefaultPickUpDelay();
            if (captureDrops() != null) captureDrops().add(itementity);
            else this.level.addFreshEntity(itementity);
            return itementity;
        }
    }


    @Override
    public boolean shouldEnterWater() {
        return this.getTarget() == null && !shouldLeaveWater() && swimTimer <= -1000;
    }

    public boolean shouldLeaveWater() {
        if (!this.getPassengers().isEmpty())
            return false;

        if (this.getTarget() != null && !this.getTarget().isInWater())
            return true;

        return swimTimer > 600 || this.isShedding();
    }

    @Override
    public boolean shouldStopMoving() {
        return !this.shouldMove();
    }

    @Override
    public int getWaterSearchRange() {
        return 12;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob mob) {
        EntityAnaconda anaconda = AMEntityRegistry.ANACONDA.get().create(serverWorld);
        anaconda.setYellow(this.isYellow());
        return anaconda;
    }

    @Override
    public void killed(ServerLevel world, LivingEntity entity) {
        final CompoundTag emptyNbt = new CompoundTag();
        entity.addAdditionalSaveData(emptyNbt);
        emptyNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
        entity.readAdditionalSaveData(emptyNbt);

        if (this.getChild() instanceof EntityAnacondaPart)
            ((EntityAnacondaPart) this.getChild()).setSwell(5);

        super.killed(world, entity);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public void feed() {
        this.heal(10);
        this.feedings++;
        if (feedings >= 3 && feedings % 3 == 0 && shedCooldown <= 0) {
            this.setSheddingTime(this.getRandom().nextInt(500) + 500);
        }
    }

    public boolean isShedding() {
        return this.getSheddingTime() > 0;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setYellow(random.nextBoolean());
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private class AIMelee extends Goal {
        private final EntityAnaconda snake;
        private int jumpAttemptCooldown = 0;

        public AIMelee() {
            snake = EntityAnaconda.this;
        }

        @Override
        public boolean canUse() {
            return snake.getTarget() != null && snake.getTarget().isAlive();
        }

        public void tick() {
            if (jumpAttemptCooldown > 0)
                jumpAttemptCooldown--;

            final LivingEntity target = snake.getTarget();
            if (target != null && target.isAlive()) {
                if (jumpAttemptCooldown == 0 && snake.distanceTo(target) < 1 + target.getBbWidth() && !snake.isStrangling()) {
                    target.hurt(DamageSource.mobAttack(snake), 4);
                    snake.setStrangling(target.getBbWidth() <= 2.3F);
                    snake.playSound(AMSoundRegistry.ANACONDA_ATTACK, snake.getSoundVolume(), snake.getVoicePitch());
                    jumpAttemptCooldown = 5 + random.nextInt(5);
                }
                if (snake.isStrangling()) {
                    snake.getNavigation().stop();
                } else {
                    try {
                        snake.getNavigation().moveTo(target, 1.3F);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // snake.lookAt(target, 1, 1);
                }
            }
        }

        public void stop() {
            snake.setStrangling(false);
        }
    }
}
