package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EntityBlueJay extends Animal implements ITargetsDroppedItems{

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> CREST_TARGET = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Optional<UUID>> LAST_FEEDER_UUID = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Optional<UUID>> RACCOON_UUID = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final EntityDataAccessor<Integer> FEED_TIME = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SING_TIME = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BLUE_VISUAL_FLAG = SynchedEntityData.defineId(EntityBlueJay.class, EntityDataSerializers.BOOLEAN);

    private static final java.util.function.Predicate<Entity> HIGHLIGHTS_WITH_SONG = (entity) -> {
        return entity instanceof Enemy;
    };

    public float prevFlyProgress;
    public float flyProgress;
    public float prevFlapAmount;
    public float flapAmount;
    public float attackProgress;
    public float prevAttackProgress;
    public float prevCrestAmount;
    public float crestAmount;
    private boolean isLandNavigator;
    private int timeFlying;
    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    public boolean aiItemFlag = false;
    private int prevSingTime = 0;
    private int blueTime = 0;
    private int raiseCrestOverrideTicks;

    protected EntityBlueJay(EntityType<? extends Animal> animal, Level level) {
        super(animal, level);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
        switchNavigator(false);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new BlueJayAIMelee(this));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1D));
        this.goalSelector.addGoal(4, new FlyingAITempt(this, 1.0D, Ingredient.of(AMTagRegistry.BLUE_JAY_FOODSTUFFS), false));
        this.goalSelector.addGoal(5, new AIFollowFeederOrRaccoon());
        this.goalSelector.addGoal(6, new AIFlyIdle());
        this.goalSelector.addGoal(7, new AIScatter());
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new AITargetItems(this, false, false, 40, 16));
        this.targetSelector.addGoal(4, (new HurtByTargetGoal(this, Player.class)).setAlertOthers());
    }


    public static boolean checkBlueJaySpawnRules(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return isBrightEnoughToSpawn(worldIn, pos);
    }

    public boolean checkSpawnObstruction(LevelReader reader) {
        if (reader.isUnobstructed(this) && !reader.containsAnyLiquid(this.getBoundingBox())) {
            BlockPos blockpos = this.blockPosition();
            BlockState blockstate2 = reader.getBlockState(blockpos.below());
            return blockstate2.is(BlockTags.LEAVES) || blockstate2.is(BlockTags.LOGS) || blockstate2.is(Blocks.GRASS_BLOCK);
        }
        return false;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.blueJaySpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.INSECT_ITEMS);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(ATTACK_TICK, 0);
        this.entityData.define(FEED_TIME, 0);
        this.entityData.define(SING_TIME, 0);
        this.entityData.define(CREST_TARGET, 0F);
        this.entityData.define(BLUE_VISUAL_FLAG, false);
        this.entityData.define(RACCOON_UUID, Optional.empty());
        this.entityData.define(LAST_FEEDER_UUID, Optional.empty());
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 1, false);
            this.navigation = new DirectPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    public void tick(){
        super.tick();
        this.prevCrestAmount = crestAmount;
        this.prevAttackProgress = attackProgress;
        this.prevFlapAmount = flapAmount;
        this.prevFlyProgress = flyProgress;
        this.prevBirdPitch = birdPitch;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        float yMov = (float) this.getDeltaMovement().y;
        this.birdPitch = yMov * 2 * -(float) (180F / (float) Math.PI);
        if(yMov >= 0){
            if(flapAmount < 1F){
                flapAmount += 0.25F;
            }
        }else if(yMov < -0.07F){
            if(flapAmount > 0){
                flapAmount -= 0.25F;
            }
        }

        if(raiseCrestOverrideTicks > 0){
            raiseCrestOverrideTicks--;
            this.crestAmount = 0.75F;
        }else{
            this.crestAmount = Mth.approach(this.crestAmount, this.getTargetCrest(), 0.3F);
        }
        if(!level.isClientSide){
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (isFlying()) {
                timeFlying++;
                this.setNoGravity(true);
                if (this.isPassenger() || this.isInLove()) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
                this.setNoGravity(false);
            }
            if(this.getTarget() != null){
                this.setCrestTarget(1F);
            }else if(this.getRaccoonUUID() != null){
                this.setCrestTarget(0.5F);
            }else{
                this.setCrestTarget(0.0F);
            }
        }
        if(this.getFeedTime() > 0){
            this.setFeedTime(this.getFeedTime() - 1);
            if(this.getFeedTime() == 0){
                this.setLastFeeder(null);
            }
        }
        if(this.getVehicle() instanceof EntityRaccoon riddenRaccoon){
            this.yBodyRot = riddenRaccoon.yBodyRot;
        }
        Entity owner = this.getRaccoon();
        if(owner instanceof EntityRaccoon raccoon){
            LivingEntity jayTarget = this.getTarget();
            LivingEntity raccoonTarget = raccoon.getTarget();
            if(jayTarget != null && jayTarget.isAlive()){
                if(this.isPassenger()){
                    this.stopRiding();
                }
            }else if(raccoonTarget != null && raccoonTarget.isAlive()) {
                if (this.canAttack(raccoonTarget)) {
                    this.setTarget(raccoonTarget);
                }
            }
        }
        if(this.getSingTime() > 0){
            this.setSingTime(this.getSingTime() - 1);
            if(this.prevSingTime % 15 == 0){
               this.playSound(AMSoundRegistry.BLUE_JAY_SONG.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            if(level.isClientSide){
                if(this.getSingTime() % 5 == 0 && this.level.isClientSide){
                    Vec3 modelFront = new Vec3(0, 0.2F, 0.3F).scale(this.getScale()).xRot(-this.getXRot() * ((float)Math.PI / 180F)).yRot(-this.getYRot() * ((float)Math.PI / 180F));
                    Vec3 particleFrom = this.position().add(modelFront);
                    this.level.addParticle(AMParticleRegistry.BIRD_SONG.get(), particleFrom.x, particleFrom.y, particleFrom.z, modelFront.x, modelFront.y, modelFront.z);
                }
            }
        }
        if(prevSingTime < getSingTime() && !level.isClientSide){
            blueTime = 1200;
            this.entityData.set(BLUE_VISUAL_FLAG, true);
            highlightMonsters();
        }
        if(blueTime > 0){
            blueTime--;
            if(blueTime == 0){
                this.entityData.set(BLUE_VISUAL_FLAG, false);
                this.level.broadcastEntityEvent(this, (byte) 68);
            }else{
                this.level.broadcastEntityEvent(this, (byte) 67);
            }
        }
        prevSingTime = getSingTime();
    }

    @Override
    public void playAmbientSound() {
        super.playAmbientSound();
        raiseCrestOverrideTicks = 15;
    }

    private boolean highlightMonsters() {
        AABB allyBox = this.getBoundingBox().inflate(64);
        allyBox = allyBox.setMinY(-64);
        allyBox = allyBox.setMaxY(320);
        boolean any = false;
        for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, allyBox, HIGHLIGHTS_WITH_SONG)) {
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, blueTime, 0, true, false));
        }
        return any;
    }

    public boolean isMakingMonstersBlue(){
        return this.entityData.get(BLUE_VISUAL_FLAG);
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if(this.getSingTime() > 0 && !level.isClientSide){
            this.entityData.set(BLUE_VISUAL_FLAG, false);
            this.level.broadcastEntityEvent(this, (byte) 68);
        }
        super.remove(removalReason);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL)  || super.isInvulnerableTo(source);
    }

    public void travel(Vec3 vec3d) {
        if(this.isInWater() && this.getDeltaMovement().y > 0F){
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
        }
        super.travel(vec3d);
    }

    public BlockPos getBlueJayGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() < 320 && !level.getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > -64 && !level.getBlockState(position).getMaterial().isSolidBlocking() && level.getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return position;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (entityIn instanceof EntityRaccoon ) {
            return this.getRaccoonUUID() != null && this.getRaccoonUUID().equals(entityIn.getUUID());
        }
        return super.isAlliedTo(entityIn);
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        float radius = 10 + this.getRandom().nextInt(15);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), (int) getY(), (int) (fleePos.z() + extraZ));
        BlockPos ground = this.getBlueJayGround(radialPos);
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

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 5 + radiusAdd + this.getRandom().nextInt(5);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), 0, (int) (fleePos.z() + extraZ));
        BlockPos ground = getBlueJayGround(radialPos);
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

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.BLUE_JAY_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BLUE_JAY_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BLUE_JAY_HURT.get();
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
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

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.blueTime = compound.getInt("BlueTime");
        if (compound.hasUUID("FeederUUID")) {
            this.setLastFeederUUID(compound.getUUID("FeederUUID"));
        }
        if (compound.hasUUID("RaccoonUUID")) {
            this.setRaccoonUUID(compound.getUUID("RaccoonUUID"));
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("BlueTime", this.blueTime);
        if (this.getLastFeederUUID() != null) {
            compound.putUUID("FeederUUID", this.getLastFeederUUID());
        }
        if (this.getRaccoonUUID() != null) {
            compound.putUUID("RaccoonUUID", this.getRaccoonUUID());
        }
    }

    public int getFeedTime() {
        return this.entityData.get(FEED_TIME);
    }

    public void setFeedTime(int feedTime) {
        this.entityData.set(FEED_TIME, feedTime);
    }

    public int getSingTime() {
        return this.entityData.get(SING_TIME);
    }

    public void setSingTime(int singTime) {
        this.entityData.set(SING_TIME, singTime);
    }

    public float getTargetCrest() {
        return this.entityData.get(CREST_TARGET);
    }

    public void setCrestTarget(float crestTarget) {
        this.entityData.set(CREST_TARGET, crestTarget);
    }

    @javax.annotation.Nullable
    public UUID getLastFeederUUID() {
        return this.entityData.get(LAST_FEEDER_UUID).orElse(null);
    }

    public void setLastFeederUUID(@javax.annotation.Nullable UUID uniqueId) {
        this.entityData.set(LAST_FEEDER_UUID, Optional.ofNullable(uniqueId));
    }

    @javax.annotation.Nullable
    public Entity getLastFeeder() {
        UUID id = getLastFeederUUID();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public void setLastFeeder(@javax.annotation.Nullable Entity feeder) {
        if (feeder == null) {
            this.setLastFeederUUID(null);
        } else {
            this.setLastFeederUUID(feeder.getUUID());
        }
    }

    @javax.annotation.Nullable
    public UUID getRaccoonUUID() {
        return this.entityData.get(RACCOON_UUID).orElse(null);
    }

    public void setRaccoonUUID(@javax.annotation.Nullable UUID uniqueId) {
        this.entityData.set(RACCOON_UUID, Optional.ofNullable(uniqueId));
    }

    @javax.annotation.Nullable
    public Entity getRaccoon() {
        UUID id = getRaccoonUUID();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public void setRaccoon(@javax.annotation.Nullable Entity feeder) {
        if (feeder == null) {
            this.setRaccoonUUID(null);
        } else {
            this.setRaccoonUUID(feeder.getUUID());
        }
    }


    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -65 && level.isEmptyBlock(position)) {
            position = position.below();
        }
        return !level.getFluidState(position).isEmpty() || level.getBlockState(position).is(Blocks.VINE) || position.getY() <= -65;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return AMEntityRegistry.BLUE_JAY.get().create(level);
    }


    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isEdible() || stack.is(AMTagRegistry.BLUE_JAY_FOODSTUFFS);
    }

    public double getMaxDistToItem() {
        return 1.0D;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        this.heal(3);
        Entity itemThrower = e.getOwner();
        if(itemThrower != null && e.getItem().is(Items.GLOW_BERRIES)){
            this.setLastFeederUUID(itemThrower.getUUID());
            this.setFeedTime(1200);
            this.stopRiding();
        }
        if(e.getOwner() != null && e.getItem().is(Tags.Items.SEEDS)){
            this.setSingTime(40);
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (!type.consumesAction()) {
            if(itemstack.is(Items.GLOW_BERRIES) && this.getFeedTime() <= 0){
                this.heal(3);
                this.usePlayerItem(player, hand, itemstack);
                this.setRaccoonUUID(null);
                this.stopRiding();
                this.setLastFeeder(player);
                this.setFeedTime(1200);
                return InteractionResult.SUCCESS;
            }else if(itemstack.is(Tags.Items.SEEDS) && this.getSingTime() <= 0){
                this.heal(3);
                this.setSingTime(40);
                this.usePlayerItem(player, hand, itemstack);
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }

    public void peck() {
        this.entityData.set(ATTACK_TICK, 7);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 67 || id == 68) {
            AlexsMobs.PROXY.onEntityStatus(this, id);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private class AIFlyIdle extends Goal {
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget;

        public AIFlyIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (EntityBlueJay.this.isVehicle() ||  (EntityBlueJay.this.getTarget() != null && EntityBlueJay.this.getTarget().isAlive()) || EntityBlueJay.this.isPassenger() || EntityBlueJay.this.aiItemFlag || EntityBlueJay.this.getSingTime() > 0) {
                return false;
            } else {
                if (EntityBlueJay.this.getRandom().nextInt(45) != 0 && !EntityBlueJay.this.isFlying()) {
                    return false;
                }
                if (EntityBlueJay.this.isOnGround()) {
                    this.flightTarget = random.nextBoolean();
                } else {
                    this.flightTarget = random.nextInt(5) > 0 && EntityBlueJay.this.timeFlying < 200;
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
                EntityBlueJay.this.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1F);
            } else {
                EntityBlueJay.this.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && EntityBlueJay.this.onGround) {
                EntityBlueJay.this.setFlying(false);
            }
            if (isFlying() && EntityBlueJay.this.onGround && EntityBlueJay.this.timeFlying > 10) {
                EntityBlueJay.this.setFlying(false);
            }
        }

        @javax.annotation.Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = EntityBlueJay.this.position();

            if(EntityBlueJay.this.isOverWaterOrVoid()){
                flightTarget = true;
            }
            if (flightTarget) {
                if (EntityBlueJay.this.timeFlying < 200 || EntityBlueJay.this.isOverWaterOrVoid()) {
                    return EntityBlueJay.this.getBlockInViewAway(vector3d, 0);
                } else {
                    return EntityBlueJay.this.getBlockGrounding(vector3d);
                }
            } else {
                return LandRandomPos.getPos(EntityBlueJay.this, 10, 7);
            }
        }

        public boolean canContinueToUse() {
            if (flightTarget) {
                return EntityBlueJay.this.isFlying() && EntityBlueJay.this.distanceToSqr(x, y, z) > 5F;
            } else {
                return (!EntityBlueJay.this.getNavigation().isDone()) && !EntityBlueJay.this.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                EntityBlueJay.this.setFlying(true);
                EntityBlueJay.this.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                EntityBlueJay.this.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            EntityBlueJay.this.getNavigation().stop();
            x = 0;
            y = 0;
            z = 0;
            super.stop();
        }

    }

    private class AIScatter extends Goal {
        protected final AIScatter.Sorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vec3 flightTarget = null;
        private int cooldown = 0;

        AIScatter() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new AIScatter.Sorter(EntityBlueJay.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@javax.annotation.Nullable Entity e) {
                    return e.isAlive() && e.getType().is(AMTagRegistry.SCATTERS_CROWS) || e instanceof Player && !((Player) e).isCreative();
                }
            };
        }

        @Override
        public boolean canUse() {
            Entity entity = EntityBlueJay.this.getTarget();
            if (EntityBlueJay.this.isPassenger()  || EntityBlueJay.this.isVehicle() || entity != null && entity.isAlive() || EntityBlueJay.this.isTrusting()) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityBlueJay.this.level.getGameTime() % 10;
                if (EntityBlueJay.this.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityBlueJay.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntityBlueJay.this.level.getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return targetEntity != null;
        }

        public void stop() {
            flightTarget = null;
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (flightTarget != null) {
                EntityBlueJay.this.setFlying(true);
                EntityBlueJay.this.getMoveControl().setWantedPosition(flightTarget.x, flightTarget.y, flightTarget.z, 1F);
                if(cooldown == 0 && EntityBlueJay.this.isTargetBlocked(flightTarget)){
                    cooldown = 30;
                    flightTarget = null;
                }
            }

            if (targetEntity != null) {
                if (EntityBlueJay.this.onGround || flightTarget == null || flightTarget != null && EntityBlueJay.this.distanceToSqr(flightTarget) < 3) {
                    Vec3 vec = EntityBlueJay.this.getBlockInViewAway(targetEntity.position(), 0);
                    if (vec != null && vec.y() > EntityBlueJay.this.getY()) {
                        flightTarget = vec;
                    }
                }
                if (EntityBlueJay.this.distanceTo(targetEntity) > 20.0F) {
                    this.stop();
                }
            }
        }

        protected double getTargetDistance() {
            return 4D;
        }

        protected AABB getTargetableArea(double targetDistance) {
            Vec3 renderCenter = new Vec3(EntityBlueJay.this.getX(), EntityBlueJay.this.getY() + 0.5, EntityBlueJay.this.getZ());
            AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
            return aabb.move(renderCenter);
        }


        public class Sorter implements Comparator<Entity> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_) {
                double d0 = this.theEntity.distanceToSqr(p_compare_1_);
                double d1 = this.theEntity.distanceToSqr(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
        }
    }

    private boolean isTrusting() {
        return this.getFeedTime() > 0 || this.getSingTime() > 0 || this.getRaccoonUUID() != null || aiItemFlag;
    }

    private class AITargetItems extends CreatureAITargetItems {

        public AITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
            super(creature, checkSight, onlyNearby, tickThreshold, radius);
            this.executionChance = 1;
        }

        public void stop() {
            super.stop();
            ((EntityBlueJay) mob).aiItemFlag = false;
        }

        public boolean canUse() {
            return super.canUse() && (mob.getTarget() == null || !mob.getTarget().isAlive());
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && (mob.getTarget() == null || !mob.getTarget().isAlive());
        }

        @Override
        protected void moveTo() {
            EntityBlueJay jay = (EntityBlueJay) mob;
            if (this.targetEntity != null) {
                jay.aiItemFlag = true;
                if (this.mob.distanceTo(targetEntity) < 2) {
                    jay.getMoveControl().setWantedPosition(this.targetEntity.getX(), targetEntity.getY(), this.targetEntity.getZ(), 1);
                    jay.peck();
                }
                if (this.mob.distanceTo(this.targetEntity) > 8 || jay.isFlying()) {
                    jay.setFlying(true);
                    float f = (float) (jay.getX() - targetEntity.getX());
                    float f1 = 1.8F;
                    float f2 = (float) (jay.getZ() - targetEntity.getZ());
                    float xzDist = Mth.sqrt(f * f + f2 * f2);

                    if(!jay.hasLineOfSight(targetEntity)){
                        jay.getMoveControl().setWantedPosition(this.targetEntity.getX(), 1 + jay.getY(), this.targetEntity.getZ(), 1);
                    }else{
                        if (xzDist < 5) {
                            f1 = 0;
                        }
                        jay.getMoveControl().setWantedPosition(this.targetEntity.getX(), f1 + this.targetEntity.getY(), this.targetEntity.getZ(), 1);
                    }
                } else {
                    this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1);
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
            moveTo();
        }
    }

    private class AIFollowFeederOrRaccoon extends Goal {
        private Entity following;

        AIFollowFeederOrRaccoon() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if(EntityBlueJay.this.isPassenger() || EntityBlueJay.this.getTarget() != null && EntityBlueJay.this.getTarget().isAlive()){
                return false;
            }
            if(EntityBlueJay.this.getRaccoonUUID() != null){
                Entity raccoon = EntityBlueJay.this.getRaccoon();
                if(raccoon != null){
                    following = raccoon;
                    return true;
                }
            }
            if(EntityBlueJay.this.getFeedTime() > 0){
                Entity feeder = EntityBlueJay.this.getLastFeeder();
                if(feeder != null){
                    following = feeder;
                    return true;
                }
            }
            return false;
        }

        public boolean canContinueToUse(){
            LivingEntity target = EntityBlueJay.this.getTarget();
            return following != null && following.isAlive() && (target == null || !target.isAlive()) && (following instanceof EntityRaccoon || EntityBlueJay.this.getFeedTime() > 0) && !EntityBlueJay.this.isPassenger();
        }

        public void tick() {
            double dist = EntityBlueJay.this.distanceTo(this.following);
            if (dist > 6 || EntityBlueJay.this.isFlying()) {
                EntityBlueJay.this.setFlying(true);
                EntityBlueJay.this.getMoveControl().setWantedPosition(this.following.getX(), this.following.getY(), this.following.getZ(), 1);
            }else{
                EntityBlueJay.this.getNavigation().moveTo(this.following.getX(), this.following.getY(), this.following.getZ(), 1);
            }
            if(EntityBlueJay.this.isFlying() && EntityBlueJay.this.isOnGround() && dist < 3){
                EntityBlueJay.this.setFlying(false);
            }
            if (this.following instanceof EntityRaccoon raccoon) {
                if(dist > 40){
                    EntityBlueJay.this.teleportTo(this.following.getX(), this.following.getY(), this.following.getZ());
                }
                if(dist < 2.5F){
                    EntityBlueJay.this.getMoveControl().setWantedPosition(this.following.getX(), this.following.getY(), this.following.getZ(), 1);
                }
                if(dist < 1F && raccoon.getPassengers().isEmpty()){
                    EntityBlueJay.this.startRiding(raccoon, false);
                }
            }
        }
    }
}
