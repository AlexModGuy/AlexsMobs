package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class EntitySkreecher extends Monster {

    public static final float MAX_DIST_TO_CEILING = 4f;
    private static final EntityDataAccessor<Boolean> CLINGING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> JUMPING_UP = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLAPPING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DIST_TO_CEILING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.FLOAT);
    protected static final EntityDimensions GROUND_SIZE = EntityDimensions.scalable(0.99F, 1.35F);
    public float prevClingProgress;
    public float clingProgress;
    public float prevClapProgress;
    public float clapProgress;
    public float prevDistanceToCeiling;
    private int clapTick = 0;
    private int clingCooldown = 0;
    private boolean isUpsideDownNavigator;
    private boolean hasAttemptedWardenSpawning;
    private boolean hasGroundSize = false;

    protected EntitySkreecher(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        switchNavigator(false);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Warden.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(2, new FollowTargetGoal());
        this.goalSelector.addGoal(3, new WanderUpsideDownGoal());
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 30F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true) {
            protected AABB getTargetSearchArea(double targetDistance) {
                AABB bb = this.mob.getBoundingBox().inflate(16, 1F, 16);
                return new AABB(bb.minX, -64, bb.minZ, bb.maxX, 320, bb.maxZ);
            }
        });
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.skreecherSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean checkSkreecherSpawnRules(EntityType<? extends Monster> animal, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        boolean isOnSculk = worldIn.getBlockState(pos.below()).is(Blocks.SCULK);
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(worldIn, pos, random) && isOnSculk;
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    private void switchNavigator(boolean clinging) {
        if (clinging) {
            this.moveControl = new MoveController();
            this.navigation = createScreecherNavigation(level());
            this.isUpsideDownNavigator = true;
        } else {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isUpsideDownNavigator = false;
        }
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 2D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.FOLLOW_RANGE, 64F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DIST_TO_CEILING, 0F);
        this.entityData.define(CLINGING, false);
        this.entityData.define(JUMPING_UP, false);
        this.entityData.define(CLAPPING, false);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SKREECHER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SKREECHER_HURT.get();
    }

    public boolean hurt(DamageSource source, float value){
        this.setClinging(false);
        this.setClapping(false);
        clingCooldown = 200 + random.nextInt(200);
        return super.hurt(source, value);
    }

    public void tick() {
        super.tick();
        prevClapProgress = clapProgress;
        prevClingProgress = clingProgress;
        prevDistanceToCeiling = this.getDistanceToCeiling();
        boolean clingVisually = this.isClinging() || this.isJumpingUp() || this.jumping;
        if (clingVisually && clingProgress < 5F) {
            clingProgress++;
        }
        if (!clingVisually && clingProgress > 0F && this.getDistanceToCeiling() == 0) {
            clingProgress--;
        }
        if(isClapping() && clapProgress < 5F){
            clapProgress++;
        }
        if(!isClapping() && clapProgress > 0F){
            clapProgress--;
        }
        if (!this.level().isClientSide) {
            float technicalDistToCeiling = calculateDistanceToCeiling();
            float gap = Math.max(technicalDistToCeiling - this.getDistanceToCeiling(), 0F);
            if(this.isClinging()){
                this.setNoGravity(true);
                if (technicalDistToCeiling > MAX_DIST_TO_CEILING || !isAlive() || clingCooldown > 0 || this.isInFluidType()) {
                    this.setClinging(false);
                }
                float goal = Math.min(technicalDistToCeiling, MAX_DIST_TO_CEILING);
                if(this.getDistanceToCeiling() < goal){
                    this.setDistanceToCeiling(Math.min(goal, prevDistanceToCeiling + 0.15F));
                }
                if(this.getDistanceToCeiling() > goal){
                    this.setDistanceToCeiling(Math.max(goal, prevDistanceToCeiling - 0.15F));
                }
                if(this.getDistanceToCeiling() < 1F){
                    gap = -0.03F;
                }
                this.setDeltaMovement(this.getDeltaMovement().add(0, gap * 0.5F, 0));
            }else{
                this.setNoGravity(false);
                if (technicalDistToCeiling < MAX_DIST_TO_CEILING && clingCooldown <= 0) {
                    this.setClinging(true);
                }
                this.setDistanceToCeiling(Math.max(0, prevDistanceToCeiling - 0.5F));
                if(this.onGround() && clingCooldown <= 0 && !this.isJumpingUp() && this.isAlive() && random.nextFloat() < 0.0085F && technicalDistToCeiling > MAX_DIST_TO_CEILING && !this.level().canSeeSky(this.blockPosition())){
                    this.setJumpingUp(true);
                }
            }
        }
        if(this.isJumpingUp()){
            if(this.isAlive() && !this.level().canSeeSky(this.blockPosition()) && (!this.verticalCollision || this.onGround())){
                this.setDistanceToCeiling(1.5F);
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2F, 0));
                for(int i = 0; i < 3; i++){
                    this.level().addParticle(ParticleTypes.SCULK_CHARGE_POP, this.getRandomX(0.5F), this.getY() - 0.2F, this.getRandomZ(0.5F), 0, -0.2F, 0);
                }
            }else{
                this.setJumpingUp(false);
            }
        }
        if(clingCooldown > 0){
            clingCooldown--;
        }
        if(!this.isAlive() || clingCooldown > 0 && this.isClinging()){
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.25F, 0));
        }
        if (this.isClinging() && !this.isUpsideDownNavigator) {
            switchNavigator(true);
        }
        if (!this.isClinging() && this.isUpsideDownNavigator) {
            switchNavigator(false);
        }
        if(this.isClapping() && this.isAlive() && clingCooldown <= 0){
            float dir = this.isClinging() ? -0.5F : 0.1F;
            if(clapTick % 8 == 0){
                this.playSound(AMSoundRegistry.SKREECHER_CLAP.get(), this.getSoundVolume() * 3F, this.getVoicePitch());
                this.gameEvent(GameEvent.ENTITY_ROAR);
                angerAllNearbyWardens();
                this.level().addParticle(AMParticleRegistry.SKULK_BOOM.get(), this.getX(), this.getEyeY(), this.getZ(), 0, dir, 0);
            }else if(clapTick % 15 == 0){
                this.playSound(AMSoundRegistry.SKREECHER_CALL.get(), this.getSoundVolume() * 4F, this.getVoicePitch());
            }
            if(clapTick >= 100){
                if(!hasAttemptedWardenSpawning && AMConfig.skreechersSummonWarden){
                    hasAttemptedWardenSpawning = true;
                    BlockPos spawnAt = this.blockPosition().below();
                    while(spawnAt.getY() > -64 && !level().getBlockState(spawnAt).isFaceSturdy(level(), spawnAt, Direction.UP)){
                        spawnAt = spawnAt.below();
                    }
                    Holder<Biome> holder = level().getBiome(spawnAt);
                    if(!this.level().isClientSide && getNearbyWardens().isEmpty() && holder.is(AMTagRegistry.SKREECHERS_CAN_SPAWN_WARDENS)){
                        Warden warden = EntityType.WARDEN.create(this.level());

                        warden.moveTo(this.getX(), spawnAt.getY() + 1, this.getZ(), this.getYRot(), 0.0F);
                        warden.finalizeSpawn((ServerLevel)level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
                        warden.setAttackTarget(this);
                        warden.increaseAngerAt(this, 79, false);
                        this.level().addFreshEntity(warden);

                    }
                }
            }
            clapTick++;
            if(!this.level().isClientSide){
                if(this.getTarget() != null && this.getTarget().isAlive() && this.hasLineOfSight(this.getTarget()) && !this.getTarget().hasEffect(MobEffects.INVISIBILITY) && !this.hasEffect(MobEffects.BLINDNESS)) {
                    double horizDist = this.getTarget().position().subtract(this.position()).horizontalDistance();
                    if (horizDist > 20) {
                        this.setClapping(false);
                    }
                }else{
                    this.setClapping(false);
                }
            }
        }
        if(!this.isClinging() && !hasGroundSize){
            refreshDimensions();
            hasGroundSize = true;
        }
        if(this.isClinging() && hasGroundSize){
            refreshDimensions();
            hasGroundSize = false;
        }
    }

    public boolean dampensVibrations() {
        return true;
    }

    public void angerAllNearbyWardens(){
        for (Warden warden : getNearbyWardens()) {
            if(warden.hasLineOfSight(this)){
                warden.increaseAngerAt(this, 100, false);
            }
        }
    }

    private List<Warden> getNearbyWardens(){
        AABB angerBox = new AABB(this.getX() - 35, this.getY() + (isClinging() ? 5F : 25F), this.getZ() - 35F, this.getX() + 35F, -64, this.getZ() + 35F);
        return this.level().getEntitiesOfClass(Warden.class, angerBox);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Clinging", this.isClinging());
        compound.putDouble("CeilDist", this.getDistanceToCeiling());
        compound.putBoolean("SummonedWarden", this.hasAttemptedWardenSpawning);
        compound.putInt("ClingCooldown", this.clingCooldown);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setClinging(compound.getBoolean("Clinging"));
        this.setDistanceToCeiling((float)compound.getDouble("CeilDist"));
        this.hasAttemptedWardenSpawning = compound.getBoolean("SummonedWarden");
        this.clingCooldown = compound.getInt("ClingCooldown");
    }


    public EntityDimensions getDimensions(Pose poseIn) {
        return isClinging() ? super.getDimensions(poseIn) : GROUND_SIZE.scale(this.getScale());
    }

    public boolean isClinging() {
        return this.entityData.get(CLINGING).booleanValue();
    }

    public void setClinging(boolean upsideDown) {
        this.entityData.set(CLINGING, Boolean.valueOf(upsideDown));
    }

    public boolean isClapping() {
        return this.entityData.get(CLAPPING).booleanValue();
    }

    public void setClapping(boolean clapping) {
        this.entityData.set(CLAPPING, Boolean.valueOf(clapping));
        if(!clapping){
            clapTick = 0;
        }
    }

    public boolean isJumpingUp() {
        return this.entityData.get(JUMPING_UP).booleanValue();
    }

    public void setJumpingUp(boolean jumping) {
        this.entityData.set(JUMPING_UP, Boolean.valueOf(jumping));
    }

    protected BlockPos getPositionAbove(float height) {
        return AMBlockPos.fromCoords(this.position().x, this.getBoundingBox().maxY + height + 0.5000001D, this.position().z);
    }

    protected PathNavigation createScreecherNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos pos) {
                int airAbove = 0;
                while(level().getBlockState(pos).isAir() && airAbove < MAX_DIST_TO_CEILING + 2){
                    pos = pos.above();
                    airAbove++;
                }
                return airAbove < Math.min(MAX_DIST_TO_CEILING, random.nextInt((int)MAX_DIST_TO_CEILING));
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        return flyingpathnavigation;
    }

    private float calculateDistanceToCeiling(){
        BlockPos ceiling = this.getCeilingOf(this.blockPosition());
        return (float) (ceiling.getY() - this.getBoundingBox().maxY);
    }

    private boolean isOpaqueBlockAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            final double d = 0.3F;
            final Vec3 vec3 = new Vec3(x, y, z);
            final AABB axisAlignedBB = AABB.ofSize(vec3, d, 1.0E-6D, d);
            return this.level().getBlockStates(axisAlignedBB).filter(Predicate.not(BlockBehaviour.BlockStateBase::isAir)).anyMatch((p_185969_) -> {
                BlockPos blockpos = AMBlockPos.fromVec3(vec3);
                return p_185969_.isSuffocating(this.level(), blockpos) && Shapes.joinIsNotEmpty(p_185969_.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisAlignedBB), BooleanOp.AND);
            });
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getDistanceToCeiling() {
        return this.entityData.get(DIST_TO_CEILING);
    }

    public void setDistanceToCeiling(float dist) {
        this.entityData.set(DIST_TO_CEILING, dist);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isClinging() && !this.isInFluidType()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.75D));
        } else {
            super.travel(travelVector);
        }

    }

    public BlockPos getCeilingOf(BlockPos usPos){
        while (!level().getBlockState(usPos).isFaceSturdy(level(), usPos, Direction.DOWN) && usPos.getY() < level().getMaxBuildHeight()){
            usPos = usPos.above();
        }
        return usPos;
    }

    class WanderUpsideDownGoal extends RandomStrollGoal {

        private int stillTicks = 0;

        public WanderUpsideDownGoal() {
            super(EntitySkreecher.this, 1D, 25);
        }

        @Nullable
        protected Vec3 getPosition() {
            if (EntitySkreecher.this.isClinging()) {
                int distance = 16;
                for (int i = 0; i < 15; i++) {
                    Random rand = new Random();
                    BlockPos randPos = EntitySkreecher.this.blockPosition().offset(rand.nextInt(distance * 2) - distance, (int) -MAX_DIST_TO_CEILING, rand.nextInt(distance * 2) - distance);
                    BlockPos lowestPos = EntitySkreecher.this.getCeilingOf(randPos).below(rand.nextInt((int)MAX_DIST_TO_CEILING));
                    return Vec3.atCenterOf(lowestPos);

                }
                return null;
            } else {
                return super.getPosition();
            }
        }

        public boolean canUse() {
            return super.canUse();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        public void stop() {
            super.stop();
            this.wantedX = 0;
            this.wantedY = 0;
            this.wantedZ = 0;
        }

        public void start() {
            this.stillTicks = 0;
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity;

        public MoveController() {
            super(EntitySkreecher.this);
            this.parentEntity = EntitySkreecher.this;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.035D / d0);
                float verticalSpeed = 0.15F;
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1.multiply(1F, verticalSpeed, 1F)));
                if(parentEntity.getTarget() != null){
                    double d1 = parentEntity.getTarget().getZ() - parentEntity.getZ();
                    double d3 = parentEntity.getTarget().getY() - parentEntity.getY();
                    double d2 = parentEntity.getTarget().getX() - parentEntity.getX();
                    float f = Mth.sqrt((float)(d2 * d2 + d1 * d1));
                    parentEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                    parentEntity.setXRot((float) (Mth.atan2(d3, f) * (double) (180F / (float) Math.PI)));
                    parentEntity.yBodyRot = parentEntity.getYRot();
                }else if (d0 >= width) {
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                }
            }
        }
    }

    private class FollowTargetGoal extends Goal {

        public FollowTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntitySkreecher.this.getTarget() != null && EntitySkreecher.this.getTarget().isAlive() && EntitySkreecher.this.clingCooldown <= 0;
        }

        public void start(){
            EntitySkreecher.this.playSound(AMSoundRegistry.SKREECHER_DETECT.get(), EntitySkreecher.this.getSoundVolume() * 6F, EntitySkreecher.this.getVoicePitch());
        }

        public void tick(){
            LivingEntity target = EntitySkreecher.this.getTarget();
            if(target != null){
                if(EntitySkreecher.this.isClinging()){
                    BlockPos ceilAbove = EntitySkreecher.this.getCeilingOf(target.blockPosition().above());
                    EntitySkreecher.this.getNavigation().moveTo(target.getX(), ceilAbove.getY() - random.nextFloat() * MAX_DIST_TO_CEILING, target.getZ(), 1.2F);
                }else{
                    EntitySkreecher.this.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1F);
                }
                Vec3 vec = target.position().subtract(EntitySkreecher.this.position());
                EntitySkreecher.this.getLookControl().setLookAt(target, 360.0F, 180.0F);
                if(vec.horizontalDistance() < 2.5F && EntitySkreecher.this.clingCooldown == 0){
                    EntitySkreecher.this.setClapping(true);
                }
            }
        }
    }
}
