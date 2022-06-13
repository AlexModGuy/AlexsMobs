package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.MovementControllerCustomCollisions;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EntityRockyRoller extends Monster implements ICustomCollisions {

    private static final EntityDataAccessor<Boolean> ROLLING = SynchedEntityData.defineId(EntityRockyRoller.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityRockyRoller.class, EntityDataSerializers.BOOLEAN);
    public float rollProgress;
    public float prevRollProgress;
    public int rollCounter = 0;
    public float clientRoll = 0;
    private int maxRollTime = 50;
    private Vec3 rollDelta;
    private float rollYRot;
    private int rollCooldown = 0;
    private int earthquakeCooldown = 0;

    protected EntityRockyRoller(EntityType<? extends Monster> monster, Level level) {
        super(monster, level);
        this.xpReward = 8;
        this.moveControl = new MovementControllerCustomCollisions(this);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.rockyRollerSpawnRolls, this.getRandom(), spawnReasonIn);
    }


    public static boolean checkRockyRollerSpawnRules(EntityType<? extends Monster> animal, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(worldIn, pos, random) && (worldIn.getBlockState(pos.below()).is(Blocks.POINTED_DRIPSTONE) || worldIn.getBlockState(pos.below()).getMaterial().isSolid() || worldIn.getBlockState(pos.below()).is(Blocks.DRIPSTONE_BLOCK));
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ARMOR, 20.0D).add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.7F).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new Navigator(this, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AIMelee());
        this.goalSelector.addGoal(2, new AIRollIdle(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANGRY, false);
        this.entityData.define(ROLLING, false);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.ROCKY_ROLLER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ROCKY_ROLLER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ROCKY_ROLLER_HURT.get();
    }

    public void tick() {
        super.tick();
        prevRollProgress = rollProgress;
        if (isRolling() && rollProgress < 5F) {
            rollProgress++;
        }
        if (!isRolling() && rollProgress > 0F) {
            rollProgress--;
        }
        if (!level.isClientSide) {
            this.setAngry(this.getTarget() != null && this.getTarget().isAlive() && this.distanceToSqr(this.getTarget()) < 20 * 20);
        }
        if (this.isRolling() && rollCooldown <= 0) {
            this.handleRoll();
            if (this.isAngry() && this.isAlive()) {
                for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3F))) {
                    if (!isAlliedTo(entity) && entity != this) {
                        entity.hurt(DamageSource.mobAttack(this), (isTarget(entity) ? 5.0F : 2.0F) + random.nextFloat() * 2.0F);
                        launch(entity, isTarget(entity));
                        if (isTarget(entity)) {
                            maxRollTime = rollCounter + 10;
                        }
                    }
                }
            }
            if (this.rollCounter > 2 && !this.isMoving() || !this.isAlive()) {
                this.setRolling(false);
            }
            maxUpStep = 1;
        } else {
            maxUpStep = 0.66F;
            this.rollCounter = 0;
        }
        if (rollCooldown > 0) {
            rollCooldown--;
        }
        if (earthquakeCooldown > 0) {
            earthquakeCooldown--;
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().lengthSqr() > 0.02D;
    }

    private void earthquake() {
        boolean flag = false;
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6, 8, 6));
        for (LivingEntity e : list) {
            if (!(e instanceof EntityRockyRoller) && e.isAlive()) {
                e.addEffect(new MobEffectInstance(AMEffectRegistry.EARTHQUAKE.get(), 20, 0, false, false, true));
                flag = true;
            }
        }
        if (!this.level.canSeeSky(this.blockPosition()) && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos ceil = this.blockPosition().offset(0, 2, 0);
            while ((!level.getBlockState(ceil).getMaterial().isSolid() || level.getBlockState(ceil).getBlock() == Blocks.POINTED_DRIPSTONE) && ceil.getY() < level.getMaxBuildHeight()) {
                ceil = ceil.above();
            }
            int i = 2 + random.nextInt(2);
            int j = 2 + random.nextInt(2);
            int k = 2 + random.nextInt(2);
            float f = (float) (i + j + k) * 0.333F + 0.5F;

            for (BlockPos blockpos1 : BlockPos.betweenClosed(ceil.offset(-i, -j, -k), ceil.offset(i, j, k))) {
                if (blockpos1.distSqr(ceil) <= (double) (f * f) && level.getBlockState(blockpos1).getBlock() instanceof Fallable) {
                    if (isHangingDripstone(blockpos1)) {
                        while (isHangingDripstone(blockpos1.above()) && blockpos1.getY() < level.getMaxBuildHeight()) {
                            blockpos1 = blockpos1.above();
                        }
                        if (isHangingDripstone(blockpos1)) {
                            Vec3 vec3 = Vec3.atBottomCenterOf(blockpos1);
                            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(level, new BlockPos(vec3), level.getBlockState(blockpos1));
                            this.level.destroyBlock(blockpos1, false);
                            this.level.addFreshEntity(fallingblockentity);
                        }
                    } else {
                        this.level.scheduleTick(blockpos1, level.getBlockState(blockpos1).getBlock(), 2);
                    }
                    flag = true;
                }
            }
        }
        if(flag){
            this.playSound(AMSoundRegistry.ROCKY_ROLLER_EARTHQUAKE.get(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private boolean isHangingDripstone(BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof PointedDripstoneBlock && level.getBlockState(pos).getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN;
    }

    private boolean isTarget(Entity entity) {
        return this.getTarget() != null && this.getTarget().is(entity);
    }

    public boolean isRolling() {
        return this.entityData.get(ROLLING).booleanValue();
    }

    public void setRolling(boolean rolling) {
        this.entityData.set(ROLLING, Boolean.valueOf(rolling));
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY).booleanValue();
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, Boolean.valueOf(angry));
    }

    private void handleRoll() {
        ++this.rollCounter;
        if (!this.level.isClientSide) {
            if (this.horizontalCollision && earthquakeCooldown == 0 & this.isAngry()) {
                earthquakeCooldown = maxRollTime;
                this.earthquake();
            }
            if (this.rollCounter > maxRollTime) {
                this.setRolling(false);
                this.rollCooldown = 10 + random.nextInt(10);
                this.rollCounter = 0;
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                Vec3 vec3 = this.getDeltaMovement();
                if (this.rollCounter == 1) {
                    float f = this.getYRot() * ((float) Math.PI / 180F);
                    float f1 = this.isBaby() ? 0.2F : 0.35F;
                    this.rollYRot = this.getYRot();
                    this.rollDelta = new Vec3(vec3.x + (double) (-Mth.sin(f) * f1), 0.0D, vec3.z + (double) (Mth.cos(f) * f1));
                    this.setDeltaMovement(this.rollDelta.add(0.0D, 0.27D, 0.0D));
                } else {
                    this.setYRot(rollYRot);
                    this.setYHeadRot(rollYRot);
                    this.setYBodyRot(rollYRot);
                    this.setDeltaMovement(this.rollDelta.x, vec3.y, this.rollDelta.z);
                }
            }
        }
    }

    private void rollFor(int time) {
        if (this.rollCooldown == 0) {
            this.maxRollTime = time;
            earthquakeCooldown = 0;
            this.setRolling(true);
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.isOnGround()) {
            double d0 = e.getX() - this.getX();
            double d1 = e.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = huge ? 1.0F : 0.35F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALLING_STALACTITE || super.isInvulnerableTo(source);
    }

    public int getMaxFallDistance() {
        return super.getMaxFallDistance() * 2;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public void push(Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement()));
    }

    @Override
    public boolean canPassThrough(BlockPos blockPos, BlockState blockstate, VoxelShape voxelShape) {
        return blockstate.getBlock() instanceof PointedDripstoneBlock;
    }

    public boolean isColliding(BlockPos pos, BlockState blockstate) {
        return !(blockstate.getBlock() instanceof PointedDripstoneBlock) && super.isColliding(pos, blockstate);
    }

    public Vec3 collide(Vec3 vec3) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    public boolean hurt(DamageSource dmg, float amount) {
        if (!this.isMoving() && !dmg.isMagic() && dmg.getDirectEntity() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) dmg.getDirectEntity();
            if (!dmg.isExplosion()) {
                livingentity.hurt(DamageSource.thorns(this), 2.0F);
            }
        }
        return super.hurt(dmg, amount);
    }

    static class RockyRollerNodeEvaluator extends WalkNodeEvaluator {
        protected BlockPathTypes evaluateBlockPathType(BlockGetter level, boolean b1, boolean b2, BlockPos pos, BlockPathTypes typeIn) {
            return level.getBlockState(pos).getBlock() instanceof PointedDripstoneBlock ? BlockPathTypes.OPEN : super.evaluateBlockPathType(level, b1, b2, pos, typeIn);
        }
    }

    class AIRollIdle extends Goal {
        EntityRockyRoller rockyRoller;

        public AIRollIdle(EntityRockyRoller p_29328_) {
            this.rockyRoller = p_29328_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        public boolean canUse() {
            if (this.rockyRoller.onGround) {
                if (rockyRoller.isRolling() || rockyRoller.rollCooldown > 0 || rockyRoller.getTarget() != null && rockyRoller.getTarget().isAlive()) {
                    return false;
                } else {
                    float f = rockyRoller.getYRot() * ((float) Math.PI / 180F);
                    int i = 0;
                    int j = 0;
                    float f1 = -Mth.sin(f);
                    float f2 = Mth.cos(f);
                    if ((double) Math.abs(f1) > 0.5D) {
                        i = (int) ((float) i + f1 / Math.abs(f1));
                    }

                    if ((double) Math.abs(f2) > 0.5D) {
                        j = (int) ((float) j + f2 / Math.abs(f2));
                    }

                    return rockyRoller.level.getBlockState(rockyRoller.blockPosition().offset(i, -1, j)).isAir();
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void start() {
            this.rockyRoller.rollFor(30 + random.nextInt(30));
        }

        public boolean isInterruptable() {
            return false;
        }
    }

    private class AIMelee extends Goal {

        private BlockPos rollFromPos = null;
        private int rollTimeout = 0;

        public AIMelee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityRockyRoller.this.getTarget() != null && EntityRockyRoller.this.getTarget().isAlive() && !EntityRockyRoller.this.isRolling();
        }

        public void tick() {
            LivingEntity enemy = EntityRockyRoller.this.getTarget();
            double d0 = this.validRollDistance(enemy);
            double distToEnemySqr = EntityRockyRoller.this.distanceTo(enemy);
            if (rollFromPos == null || enemy.distanceToSqr(rollFromPos.getX() + 0.5F, rollFromPos.getY() + 0.5F, rollFromPos.getZ() + 0.5) > 60 || !canEntitySeePosition(enemy, rollFromPos)) {
                rollFromPos = getRollAtPosition(enemy);
            }
            EntityRockyRoller.this.lookAt(enemy, 100, 5);

            if (rollTimeout < 40 && rollFromPos != null && (distToEnemySqr <= d0 && EntityRockyRoller.this.distanceToSqr(rollFromPos.getX() + 0.5F, rollFromPos.getY() + 0.5F, rollFromPos.getZ() + 0.5) > 2.25F)) {
                EntityRockyRoller.this.getNavigation().moveTo(rollFromPos.getX() + 0.5F, rollFromPos.getY() + 0.5F, rollFromPos.getZ() + 0.5F, 1.6D);
                rollTimeout++;
            } else {
                double d1 = enemy.getX() - EntityRockyRoller.this.getX();
                double d2 = enemy.getZ() - EntityRockyRoller.this.getZ();
                float f = (float) (Mth.atan2(d2, d1) * (double) (180F / (float) Math.PI)) - 90.0F;
                EntityRockyRoller.this.setYRot(f);
                EntityRockyRoller.this.yBodyRot = f;
                EntityRockyRoller.this.rollFor(30 + random.nextInt(40));
            }
        }

        public void stop() {
            super.stop();
            rollTimeout = 0;
        }

        protected double validRollDistance(LivingEntity attackTarget) {
            return 3.0F + attackTarget.getBbWidth();
        }

        private boolean canEntitySeePosition(LivingEntity entity, BlockPos destinationBlock) {
            Vec3 Vector3d = new Vec3(entity.getX(), entity.getY() + 0.5F, entity.getZ());
            Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
            BlockHitResult result = entity.level.clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            return result != null && (result.getBlockPos().equals(destinationBlock) || entity.level.getBlockState(result.getBlockPos()).getBlock() == Blocks.POINTED_DRIPSTONE);
        }


        public BlockPos getRollAtPosition(Entity target) {
            float radius = EntityRockyRoller.this.getRandom().nextInt(2) + 6 + target.getBbWidth();
            int orbit = EntityRockyRoller.this.getRandom().nextInt(360);
            float angle = (0.01745329251F * orbit);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos circlePos = new BlockPos(target.getX() + extraX, target.getEyeY(), target.getZ() + extraZ);
            while (!EntityRockyRoller.this.level.getBlockState(circlePos).isAir() && circlePos.getY() < EntityRockyRoller.this.level.getMaxBuildHeight()) {
                circlePos = circlePos.above();
            }
            while (!EntityRockyRoller.this.level.getBlockState(circlePos.below()).entityCanStandOn(EntityRockyRoller.this.level, circlePos.below(), EntityRockyRoller.this) && circlePos.getY() > 1) {
                circlePos = circlePos.below();
            }
            if (EntityRockyRoller.this.getWalkTargetValue(circlePos) > -1) {
                return circlePos;
            }
            return null;
        }
    }

    class Navigator extends GroundPathNavigatorWide {

        public Navigator(Mob mob, Level world) {
            super(mob, world, 0.75F);
        }

        protected PathFinder createPathFinder(int i) {
            this.nodeEvaluator = new RockyRollerNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, i);
        }
    }
}
