package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntitySunbird extends Animal implements FlyingAnimal {

    public static final Predicate<? super Entity> SCORCH_PRED = new com.google.common.base.Predicate<Entity>() {
        @Override
        public boolean apply(@Nullable Entity e) {
            return e.isAlive() && e.getType().is(AMTagRegistry.SUNBIRD_SCORCH_TARGETS);
        }
    };
    private static final EntityDataAccessor<Boolean> SCORCHING = SynchedEntityData.defineId(EntitySunbird.class, EntityDataSerializers.BOOLEAN);
    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    private int beaconSearchCooldown = 50;
    private BlockPos beaconPos = null;
    private boolean orbitClockwise = false;
    private float prevScorchProgress;
    private float scorchProgress;
    private int fullScorchTime;


    protected EntitySunbird(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new MoveHelperController(this);
        orbitClockwise = new Random().nextBoolean();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCORCHING, false);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 1F);
    }

    public static boolean canSunbirdSpawn(EntityType<? extends Mob> typeIn, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return true;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.sunbirdSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SUNBIRD_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SUNBIRD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SUNBIRD_HURT.get();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 32F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public float getBrightness() {
        return 1.0F;
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev) {
            if (source.getEntity() != null) {
                if (source.getEntity() instanceof LivingEntity) {
                    LivingEntity hurter = (LivingEntity) source.getEntity();
                    if (hurter.hasEffect(AMEffectRegistry.SUNBIRD_BLESSING.get())) {
                        hurter.removeEffect(AMEffectRegistry.SUNBIRD_BLESSING.get());
                    }
                    hurter.addEffect(new MobEffectInstance(AMEffectRegistry.SUNBIRD_CURSE.get(), 600, 0));
                }
            }
            return prev;
        }
        return prev;
    }

    public void travel(Vec3 travelVector) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
        } else {
            BlockPos ground = AMBlockPos.get(this.getX(), this.getY() - 1.0D, this.getZ());
            float f = 0.91F;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }

            //float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround) {
                f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
            }
            this.calculateEntityAnimation(this, true);

            this.moveRelative(0.2F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(f));
        }

        this.calculateEntityAnimation(this, false);
    }

    public void tick() {
        super.tick();
        prevBirdPitch = this.birdPitch;
        prevScorchProgress = this.scorchProgress;
        float f2 = (float) -((float) this.getDeltaMovement().y * (double) Mth.RAD_TO_DEG);
        this.birdPitch = f2;
        if (this.level.isClientSide) {
            final float radius = 0.35F + random.nextFloat() * 3.5F;
            final float angle = (Maths.STARTING_ANGLE * ((random.nextBoolean() ? -85F : 85F) + this.yBodyRot));
            final float angleMotion = (Maths.STARTING_ANGLE * this.yBodyRot);
            final double extraX = radius * Mth.sin(Mth.PI + angle);
            final double extraZ = radius * Mth.cos(angle);
            final double extraXMotion = -0.2F * Mth.sin((float) (Math.PI + angleMotion));
            final double extraZMotion = -0.2F * Mth.cos(angleMotion);
            final double yRandom = 0.2F + random.nextFloat() * 0.3F;
            this.level.addParticle(AMParticleRegistry.SUNBIRD_FEATHER.get(), this.getX() + extraX, this.getY() + yRandom, this.getZ() + extraZ, extraXMotion, 0D, extraZMotion);
        } else {
            if (this.tickCount % 100 == 0) {
                if(!this.isScorching() && !getScorchingMobs().isEmpty()){
                    this.setScorching(true);
                }
                List<Player> playerList = this.level.getEntitiesOfClass(Player.class, this.getScorchArea(), Predicates.alwaysTrue());
                for (Player e : playerList) {
                    if (!e.hasEffect(AMEffectRegistry.SUNBIRD_BLESSING.get()) && !e.hasEffect(AMEffectRegistry.SUNBIRD_CURSE.get())) {
                        e.addEffect(new MobEffectInstance(AMEffectRegistry.SUNBIRD_BLESSING.get(), 600, 0));
                    }
                }
            }
            if (beaconSearchCooldown > 0) {
                beaconSearchCooldown--;
            }
            if (beaconSearchCooldown <= 0) {
                beaconSearchCooldown = 100 + random.nextInt(200);
                if (level instanceof ServerLevel) {
                    List<BlockPos> beacons = this.getNearbyBeacons(this.blockPosition(), (ServerLevel) level, 64);
                    BlockPos closest = null;
                    for (BlockPos pos : beacons) {
                        if (closest == null || this.distanceToSqr(closest.getX(), closest.getY(), closest.getZ()) > this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())) {
                            if (isValidBeacon(pos)) {
                                closest = pos;
                            }
                        }
                    }
                    if (closest != null && isValidBeacon(closest)) {
                        beaconPos = closest;
                    }
                }
                if (beaconPos != null) {

                    if (!isValidBeacon(beaconPos) && tickCount > 40) {
                        this.beaconPos = null;
                    }
                }
            }
        }

        final boolean scorching = this.isScorching();
        if (scorching) {
            if (scorchProgress < 20F)
                scorchProgress++;
        } else {
            if (scorchProgress > 0F)
                scorchProgress--;
        }

        if (scorching && scorchProgress == 20F && !this.level.isClientSide) {
            if(fullScorchTime > 30){
                this.setScorching(false);
            }else if(fullScorchTime % 5 == 0){
                for (Entity e : getScorchingMobs()) {
                    e.setSecondsOnFire(4);
                    if (e instanceof Phantom) {
                        ((Phantom) e).addEffect(new MobEffectInstance(AMEffectRegistry.SUNBIRD_CURSE.get(), 200, 0));
                    }
                }
            }
            fullScorchTime++;
        }else{
            fullScorchTime = 0;
        }
    }

    private List<LivingEntity> getScorchingMobs(){
        return this.level.getEntitiesOfClass(LivingEntity.class, this.getScorchArea(), SCORCH_PRED);
    }

    public boolean isScorching() {
        return this.entityData.get(SCORCHING);
    }

    public void setScorching(boolean scorching) {
        this.entityData.set(SCORCHING, scorching);
    }


    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BeaconPosX")) {
            int i = compound.getInt("BeaconPosX");
            int j = compound.getInt("BeaconPosY");
            int k = compound.getInt("BeaconPosZ");
            this.beaconPos = new BlockPos(i, j, k);
        } else {
            this.beaconPos = null;
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        BlockPos blockpos = this.beaconPos;
        if (blockpos != null) {
            compound.putInt("BeaconPosX", blockpos.getX());
            compound.putInt("BeaconPosY", blockpos.getY());
            compound.putInt("BeaconPosZ", blockpos.getZ());
        }

    }


    private AABB getScorchArea() {
        return this.getBoundingBox().inflate(15, 32, 15);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return null;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private List<BlockPos> getNearbyBeacons(BlockPos blockpos, ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        Stream<BlockPos> stream = pointofinterestmanager.findAll((poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.BEACON.getKey())), Predicates.alwaysTrue(), blockpos, range, PoiManager.Occupancy.ANY);
        return stream.collect(Collectors.toList());
    }

    private boolean isValidBeacon(BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        return te instanceof BeaconBlockEntity && !((BeaconBlockEntity) te).getBeamSections().isEmpty();
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    public float getScorchProgress(float partialTick){
        return (prevScorchProgress + (scorchProgress - prevScorchProgress) * partialTick) / 20F;
    }

    static class MoveHelperController extends MoveControl {
        private final EntitySunbird parentEntity;

        public MoveHelperController(EntitySunbird sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                final double d0 = vector3d.length();
                if (d0 < parentEntity.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));
                    if (parentEntity.getTarget() == null) {
                        Vec3 vector3d1 = parentEntity.getDeltaMovement();
                        parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * Mth.RAD_TO_DEG);
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    } else {
                        final double d2 = parentEntity.getTarget().getX() - parentEntity.getX();
                        final double d1 = parentEntity.getTarget().getZ() - parentEntity.getZ();
                        parentEntity.setYRot(-((float) Mth.atan2(d2, d1)) * Mth.RAD_TO_DEG);
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

    static class RandomFlyGoal extends Goal {
        private final EntitySunbird parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntitySunbird sunbird) {
            this.parentEntity = sunbird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl movementcontroller = this.parentEntity.getMoveControl();
            if (!movementcontroller.hasWanted() || target == null) {
                if (parentEntity.beaconPos != null) {
                    target = getBlockInViewBeacon(parentEntity.beaconPos, 5 + parentEntity.random.nextInt(1));
                } else {
                    target = getBlockInViewSunbird();
                }
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, parentEntity.beaconPos != null ? 0.8D : 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && parentEntity.distanceToSqr(Vec3.atCenterOf(target)) > 2.4D && parentEntity.getMoveControl().hasWanted() && !parentEntity.horizontalCollision;
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target == null) {
                if (parentEntity.beaconPos != null) {
                    target = getBlockInViewBeacon(parentEntity.beaconPos, 5 + parentEntity.random.nextInt(1));
                } else {
                    target = getBlockInViewSunbird();
                }
            }
            if(parentEntity.beaconPos != null && parentEntity.random.nextInt(100) == 0){
                parentEntity.orbitClockwise = parentEntity.random.nextBoolean();
            }
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, parentEntity.beaconPos != null ? 0.8D : 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 2.5F) {
                    target = null;
                }
            }
        }

        private BlockPos getBlockInViewBeacon(BlockPos orbitPos, float gatheringCircleDist) {
            final float angle = (Maths.STARTING_ANGLE * (float) 9 * (parentEntity.orbitClockwise ? -parentEntity.tickCount : parentEntity.tickCount));
            final double extraX = gatheringCircleDist * Mth.sin((angle));
            final double extraZ = gatheringCircleDist * Mth.cos(angle);
            if (orbitPos != null) {
                BlockPos pos = AMBlockPos.get(orbitPos.getX() + extraX, orbitPos.getY() + parentEntity.random.nextInt(2) + 2, orbitPos.getZ() + extraZ);
                if (parentEntity.level.isEmptyBlock(new BlockPos(pos))) {
                    return pos;
                }
            }
            return null;
        }

        public BlockPos getBlockInViewSunbird() {
            final float radius = 0.75F * (0.7F * 6) * -3 - parentEntity.getRandom().nextInt(24);
            final float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            final float renderYawOffset = parentEntity.yBodyRot;
            final float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (parentEntity.getRandom().nextFloat() * neg);
            final double extraX = radius * Mth.sin(Mth.PI + angle);
            final double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = AMBlockPos.get(parentEntity.getX() + extraX, 0, parentEntity.getZ() + extraZ);
            BlockPos ground = parentEntity.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, radialPos);
            final int distFromGround = (int) parentEntity.getY() - ground.getY();
            final int flightHeight = Math.max(ground.getY(), 230 + parentEntity.getRandom().nextInt(40)) - ground.getY();
            BlockPos newPos = radialPos.above(distFromGround > 16 ? flightHeight : (int) parentEntity.getY() + parentEntity.getRandom().nextInt(16) + 1);
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6) {
                return newPos;
            }
            return null;
        }

    }
}
