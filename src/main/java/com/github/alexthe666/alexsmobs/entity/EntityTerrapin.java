package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockTerrapinEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTerrapinEgg;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class EntityTerrapin extends Animal implements ISemiAquatic, Bucketable {

    private static final EntityDataAccessor<Integer> TURTLE_TYPE = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHELL_TYPE = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TURTLE_COLOR = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHELL_COLOR = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SKIN_COLOR = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RETREATED = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPINNING = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityTerrapin.class, EntityDataSerializers.BOOLEAN);
    public float clientSpin = 0;
    public int spinCounter = 0;
    public float prevSwimProgress;
    public float swimProgress;
    public float prevRetreatProgress;
    public float retreatProgress;
    public float prevSpinProgress;
    public float spinProgress;
    private int maxRollTime = 50;
    private boolean isLandNavigator;
    private int swimTimer = -1000;
    private int hideInShellTimer = 0;
    private Vec3 spinDelta;
    private float spinYRot;
    private int changeSpinAngleCooldown = 0;
    private LivingEntity lastLauncher = null;
    private TileEntityTerrapinEgg.ParentData partnerData;

    protected EntityTerrapin(EntityType animal, Level level) {
        super(animal, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ARMOR, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.1F);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TERRAPIN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TERRAPIN_HURT.get();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.terrapinSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canTerrapinSpawn(EntityType<EntityTerrapin> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || iServerWorld.getBlockState(pos).getFluidState().is(Fluids.WATER);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreathAirGoal(this));
        this.goalSelector.addGoal(1, new MateGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new LayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, Ingredient.of(Items.SEAGRASS), false));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new SemiAquaticAIRandomSwimming(this, 1.0D, 30));
        this.goalSelector.addGoal(6, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D, 60));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public void tick() {
        super.tick();
        prevSwimProgress = swimProgress;
        prevRetreatProgress = retreatProgress;
        prevSpinProgress = spinProgress;
        if (this.isInWaterOrBubble() && swimProgress < 5F) {
            swimProgress++;
        }
        if (!this.isInWaterOrBubble() && swimProgress > 0F) {
            swimProgress--;
        }
        if (this.isSpinning() && spinProgress < 5F) {
            spinProgress++;
        }
        if (!this.isSpinning() && spinProgress > 0F) {
            spinProgress--;
        }
        if (this.hasRetreated() && retreatProgress < 5F) {
            retreatProgress++;
        }
        if (!this.hasRetreated() && retreatProgress > 0F) {
            retreatProgress--;
        }
        if (this.isSpinning()) {
            this.handleSpin();
            if (this.isAlive() && spinCounter > 5 && !this.isBaby()) {
                for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3F))) {
                    if (!isAlliedTo(entity) && !(entity instanceof EntityTerrapin)) {
                        entity.hurt(DamageSource.mobAttack(lastLauncher == null ? this : lastLauncher), 4.0F + random.nextFloat() * 4.0F);
                    }
                }
            }
            if (!this.isAlive()) {
                this.setSpinning(false);
            }
            if (this.horizontalCollision) {
                if(changeSpinAngleCooldown == 0){
                    changeSpinAngleCooldown = 10;
                    float f = collideDirectionAndSound().getAxis() == Direction.Axis.X ? this.spinYRot - 180 : 180 - this.spinYRot;
                    f += random.nextInt(40) - 20;
                    this.setYRot(f);
                    this.copySpinDelta(f, Vec3.ZERO);
                }else{
                    maxRollTime -= 30;
                }

            }
            if (changeSpinAngleCooldown > 0) {
                changeSpinAngleCooldown--;
            }
        }
        if (!level.isClientSide) {
            if (this.isInWaterOrBubble() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!this.isInWaterOrBubble() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (isInWater()) {
                swimTimer = Math.max(0, swimTimer + 1);
            } else {
                swimTimer = Math.min(0, swimTimer - 1);
                List<Player> list = this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(0, 0.15F, 0));
                for (Player player : list) {
                    if ((player.jumping || !player.isOnGround()) && player.getY() > this.getEyeY()) {
                        if (!hasRetreated()) {
                            this.hideInShellTimer += 40 + random.nextInt(40);
                        } else if (!isSpinning()) {
                            lastLauncher = player;
                            int spin = 100 + random.nextInt(100);
                            this.hideInShellTimer = spin;
                            this.setYRot(player.getYHeadRot());
                            spinFor(spin);
                        }
                    }
                }
            }

            if (swimProgress > 0) {
                this.maxUpStep = 1;
            } else {
                this.maxUpStep = 0.6F;
            }
            if (hideInShellTimer > 0) {
                hideInShellTimer--;
            }
            this.setRetreated(hideInShellTimer > 0 && !this.isSpinning());
        }
    }

    private Direction collideDirectionAndSound(){
        HitResult raytraceresult = ProjectileUtil.getHitResult(this, entity -> false);
        if(raytraceresult instanceof BlockHitResult){
            BlockState state = level.getBlockState(((BlockHitResult) raytraceresult).getBlockPos());
            if(state != null && !this.isSilent()){
            }
            return ((BlockHitResult) raytraceresult).getDirection();
        }
        return Direction.DOWN;
    }

    private boolean isMoving() {
        return this.getDeltaMovement().lengthSqr() > 0.02D;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AnimalSwimMoveControllerSink(this, 2.5F, 1.15F);
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TURTLE_TYPE, 0);
        this.entityData.define(SHELL_TYPE, 0);
        this.entityData.define(SKIN_TYPE, 0);
        this.entityData.define(SHELL_COLOR, 0);
        this.entityData.define(SKIN_COLOR, 0);
        this.entityData.define(TURTLE_COLOR, 0);
        this.entityData.define(RETREATED, false);
        this.entityData.define(SPINNING, false);
        this.entityData.define(HAS_EGG, false);
        this.entityData.define(FROM_BUCKET, false);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TurtleType", this.getTurtleTypeOrdinal());
        compound.putInt("ShellType", this.getShellType());
        compound.putInt("SkinType", this.getSkinType());
        compound.putInt("TurtleColor", this.getTurtleColor());
        compound.putInt("ShellColor", this.getShellColor());
        compound.putInt("SkinColor", this.getSkinColor());
        compound.putBoolean("HasEgg", this.hasEgg());
        compound.putBoolean("Bucketed", this.fromBucket());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTurtleTypeOrdinal(compound.getInt("TurtleType"));
        this.setShellType(compound.getInt("ShellType"));
        this.setSkinType(compound.getInt("SkinType"));
        this.setTurtleColor(compound.getInt("TurtleColor"));
        this.setShellColor(compound.getInt("ShellColor"));
        this.setSkinColor(compound.getInt("SkinColor"));
        this.setHasEgg(compound.getBoolean("HasEgg"));
        this.setFromBucket(compound.getBoolean("Bucketed"));
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if(!this.isSpinning()){
            super.playStepSound(pos, state);
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Blocks.SEAGRASS.asItem();
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

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket() || this.hasCustomName();
    }

    public boolean removeWhenFarAway(double d) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    private int getTurtleTypeOrdinal() {
        return Mth.clamp(this.entityData.get(TURTLE_TYPE).intValue(), 0, TerrapinTypes.values().length - 1);
    }

    private void setTurtleTypeOrdinal(int i) {
        this.entityData.set(TURTLE_TYPE, Integer.valueOf(i));
    }

    public int getShellType() {
        return this.entityData.get(SHELL_TYPE).intValue();
    }

    public void setShellType(int i) {
        this.entityData.set(SHELL_TYPE, Integer.valueOf(i));
    }

    public int getSkinType() {
        return this.entityData.get(SKIN_TYPE).intValue();
    }

    public void setSkinType(int i) {
        this.entityData.set(SKIN_TYPE, Integer.valueOf(i));
    }

    public int getShellColor() {
        return this.entityData.get(SHELL_COLOR).intValue();
    }

    public void setShellColor(int i) {
        this.entityData.set(SHELL_COLOR, Integer.valueOf(i));
    }

    public int getSkinColor() {
        return this.entityData.get(SKIN_COLOR).intValue();
    }

    public void setSkinColor(int i) {
        this.entityData.set(SKIN_COLOR, Integer.valueOf(i));
    }

    public int getTurtleColor() {
        return this.entityData.get(TURTLE_COLOR).intValue();
    }

    public void setTurtleColor(int i) {
        this.entityData.set(TURTLE_COLOR, Integer.valueOf(i));
    }

    public TerrapinTypes getTurtleType() {
        return TerrapinTypes.values()[getTurtleTypeOrdinal()];
    }

    public void setTurtleType(TerrapinTypes type) {
        this.setTurtleTypeOrdinal(type.ordinal());
    }

    public boolean isSpinning() {
        return this.entityData.get(SPINNING).booleanValue();
    }

    public void setSpinning(boolean b) {
        this.entityData.set(SPINNING, Boolean.valueOf(b));
    }

    public boolean hasRetreated() {
        return this.entityData.get(RETREATED).booleanValue();
    }

    public void setRetreated(boolean b) {
        this.entityData.set(RETREATED, Boolean.valueOf(b));
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    private void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    public int getMaxAirSupply() {
        return 4800;
    }

    protected int increaseAirSupply(int currentAir) {
        return this.getMaxAirSupply();
    }

    public void push(Entity entity) {
        if (this.isInWaterOrBubble() || entity instanceof EntityTerrapin) {
            super.push(entity);
        } else {
            entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement()));
        }
    }

    public boolean canBeCollidedWith() {
        return this.isInWaterOrBubble() ? super.canBeCollidedWith() : this.isAlive();
    }

    private void spinFor(int time) {
        this.maxRollTime = time;
        this.setSpinning(true);
    }

    private void copySpinDelta(float spinRot, Vec3 motionIn) {
        float f = spinRot * ((float) Math.PI / 180F);
        float f1 = this.isBaby() ? 0.3F : 0.5F;
        this.spinYRot = spinRot;
        this.spinDelta = new Vec3(motionIn.x + (double) (-Mth.sin(f) * f1), 0.0D, motionIn.z + (double) (Mth.cos(f) * f1));
        this.setDeltaMovement(this.spinDelta.add(0.0D, 0.0D, 0.0D));

    }

    private void handleSpin() {
        this.setRetreated(true);
        ++this.spinCounter;
        if (!this.level.isClientSide) {
            if (this.spinCounter > maxRollTime) {
                this.setSpinning(false);
                this.hideInShellTimer = 10 + random.nextInt(30);
                this.spinCounter = 0;
            } else {
                Vec3 vec3 = this.getDeltaMovement();
                if (this.spinCounter == 1) {
                    copySpinDelta(this.getYRot(), vec3);
                } else {
                    this.setYRot(spinYRot);
                    this.setYHeadRot(spinYRot);
                    this.setYBodyRot(spinYRot);
                    this.setDeltaMovement(this.spinDelta.x, vec3.y, this.spinDelta.z);
                }
            }
        }
    }


    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setAirSupply(this.getMaxAirSupply());
        this.setTurtleType(TerrapinTypes.getRandomType(random));
        this.setShellType(random.nextInt(7));
        this.setSkinType(random.nextInt(4));
        this.setTurtleColor(TerrapinTypes.generateRandomColor(random));
        this.setShellColor(TerrapinTypes.generateRandomColor(random));
        this.setSkinColor(TerrapinTypes.generateRandomColor(random));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return AMEntityRegistry.TERRAPIN.get().create(p_146743_);
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isSpinning() || this.hasRetreated();
    }

    @Override
    public boolean shouldEnterWater() {
        return this.getTarget() == null && !shouldLeaveWater() && swimTimer <= -1000;
    }

    @Override
    public boolean shouldLeaveWater() {
        return swimTimer > 600 || this.hasEgg();
    }

    @Override
    public int getWaterSearchRange() {
        return 10;
    }


    public boolean isPushedByFluid() {
        return false;
    }

    public void travel(Vec3 travelVector) {
        if (this.shouldStopMoving()) {
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

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.TERRAPIN_BUCKET.get());
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
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.put("TerrapinData", platTag);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("TerrapinData")) {
            this.readAdditionalSaveData(compound.getCompound("TerrapinData"));
        }
    }

    @Override
    @Nonnull
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.SEAGRASS){
            this.setPersistenceRequired();
        }
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    public void calculateEntityAnimation(LivingEntity mob, boolean flying) {
        mob.animationSpeedOld = mob.animationSpeed;
        double d0 = mob.getX() - mob.xo;
        double d1 = flying ? mob.getY() - mob.yo : 0.0D;
        double d2 = mob.getZ() - mob.zo;
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * (isSpinning() ? 4.0F : 32.0F);
        if (f > 1.0F) {
            f = 1.0F;
        }

        mob.animationSpeed += (f - mob.animationSpeed) * 0.4F;
        mob.animationPosition += mob.animationSpeed;
    }


    public boolean isKoopa() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && s.toLowerCase().contains("koopa");
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    static class MateGoal extends BreedGoal {
        private final EntityTerrapin turtle;

        MateGoal(EntityTerrapin turtle, double speedIn) {
            super(turtle, speedIn);
            this.turtle = turtle;
        }

        public boolean canUse() {
            return super.canUse() && !this.turtle.hasEgg();
        }

        protected void breed() {
            ServerPlayer serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.partner.getLoveCause() != null) {
                serverplayerentity = this.partner.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, this.animal);
            }
            if(partner instanceof EntityTerrapin terrapin){
                this.turtle.partnerData = new TileEntityTerrapinEgg.ParentData(terrapin.getTurtleType(), terrapin.getShellType(), terrapin.getSkinType(), terrapin.getTurtleColor(), terrapin.getShellColor(), terrapin.getSkinColor());
            }
            this.turtle.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            RandomSource random = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }

        }
    }
    
    static class LayEggGoal extends MoveToBlockGoal {
        private final EntityTerrapin turtle;
        private int digTime;

        LayEggGoal(EntityTerrapin turtle, double speedIn) {
            super(turtle, speedIn, 16);
            this.turtle = turtle;
        }

        public void stop() {
            digTime = 0;
        }

        public boolean canUse() {
            return this.turtle.hasEgg() && super.canUse();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.turtle.hasEgg();
        }

        public double acceptedDistance() {
            return turtle.getBbWidth() + 0.5D;
        }

        public void tick() {
            super.tick();
            BlockPos blockpos = this.turtle.blockPosition();
            turtle.swimTimer = 1000;
            if (!this.turtle.isInWater() && this.isReachedTarget()) {
                Level world = this.turtle.level;
                turtle.gameEvent(GameEvent.BLOCK_PLACE);
                world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                world.setBlock(this.blockPos.above(), AMBlockRegistry.TERRAPIN_EGG.get().defaultBlockState().setValue(BlockTerrapinEgg.EGGS, Integer.valueOf(this.turtle.random.nextInt(1) + 3)), 3);
                if(world.getBlockEntity(this.blockPos.above()) instanceof TileEntityTerrapinEgg eggTe){
                    eggTe.parent1 = new TileEntityTerrapinEgg.ParentData(turtle.getTurtleType(), turtle.getShellType(), turtle.getSkinType(), turtle.getTurtleColor(), turtle.getShellColor(), turtle.getSkinColor());
                    eggTe.parent2 = turtle.partnerData == null ? eggTe.parent1 : turtle.partnerData;
                }
                this.turtle.setHasEgg(false);
                this.turtle.setInLoveTime(600);
            }

        }

        protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
            return worldIn.isEmptyBlock(pos.above()) && BlockTerrapinEgg.isProperHabitat(worldIn, pos);
        }
    }
}
