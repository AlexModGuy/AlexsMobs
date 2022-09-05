package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class EntityDropBear extends Monster implements IAnimatedEntity {

    public static final Animation ANIMATION_BITE = Animation.create(9);
    public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
    public static final Animation ANIMATION_SWIPE_L = Animation.create(15);
    public static final Animation ANIMATION_JUMPUP = Animation.create(20);
    private static final EntityDataAccessor<Boolean> UPSIDE_DOWN = SynchedEntityData.defineId(EntityDropBear.class, EntityDataSerializers.BOOLEAN);
    public float prevUpsideDownProgress;
    public float upsideDownProgress;
    public boolean fallRotation = random.nextBoolean();
    private int animationTick;
    private boolean jumpingUp = false;
    private Animation currentAnimation;
    private int upwardsFallingTicks = 0;
    private boolean isUpsideDownNavigator;
    private boolean prevOnGround = false;

    protected EntityDropBear(EntityType type, Level world) {
        super(type, world);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 22.0D).add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.7F).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public static BlockPos getLowestPos(LevelAccessor world, BlockPos pos) {
        while (!world.getBlockState(pos).isFaceSturdy(world, pos, Direction.DOWN) && pos.getY() < 320) {
            pos = pos.above();
        }
        return pos;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.dropbearSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.DROPBEAR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.DROPBEAR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.DROPBEAR_HURT.get();
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(random.nextBoolean() ? ANIMATION_BITE : random.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
        }
        return true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AIDropMelee());
        this.goalSelector.addGoal(2, new AIUpsideDownWander());
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 30F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntityDropBear.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true) {
            protected AABB getTargetSearchArea(double targetDistance) {
                AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
                return new AABB(bb.minX, 0, bb.minZ, bb.maxX, 256, bb.maxZ);
            }
        });
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, AbstractVillager.class, true) {
            protected AABB getTargetSearchArea(double targetDistance) {
                AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
                return new AABB(bb.minX, 0, bb.minZ, bb.maxX, 256, bb.maxZ);
            }
        });
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.FALL || source == DamageSource.IN_WALL;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        super.checkFallDamage(y, onGroundIn, state, pos);
    }

    protected void playBlockFallSound() {
        this.onLand();
        super.playBlockFallSound();
    }

    private void switchNavigator(boolean rightsideUp) {
        if (rightsideUp) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isUpsideDownNavigator = false;
        } else {
            this.moveControl = new FlightMoveController(this, 1.1F, false);
            this.navigation = new DirectPathNavigator(this, level);
            this.isUpsideDownNavigator = true;
        }
    }

    public void tick() {
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);
        prevUpsideDownProgress = upsideDownProgress;
        if (this.isUpsideDown() && upsideDownProgress < 5F) {
            upsideDownProgress++;
        }
        if (!this.isUpsideDown() && upsideDownProgress > 0F) {
            upsideDownProgress--;
        }
        if (!level.isClientSide) {
            BlockPos abovePos = this.getPositionAbove();
            BlockState aboveState = level.getBlockState(abovePos);
            BlockState belowState = level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
            BlockPos worldHeight = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition());
            boolean validAboveState = aboveState.isFaceSturdy(level, abovePos, Direction.DOWN);
            boolean validBelowState = belowState.isFaceSturdy(level, this.getBlockPosBelowThatAffectsMyMovement(), Direction.UP);
            LivingEntity attackTarget = this.getTarget();
            if (attackTarget != null && distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 1 && this.hasLineOfSight(attackTarget)) {
                if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 6) {
                    attackTarget.knockback(0.5F, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_L) && this.getAnimationTick() == 9) {
                    float rot = getYRot() + 90;
                    attackTarget.knockback(0.5F, Mth.sin(rot * ((float) Math.PI / 180F)), -Mth.cos(rot * ((float) Math.PI / 180F)));
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_R) && this.getAnimationTick() == 9) {
                    float rot = getYRot() - 90;
                    attackTarget.knockback(0.5F, Mth.sin(rot * ((float) Math.PI / 180F)), -Mth.cos(rot * ((float) Math.PI / 180F)));
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
            }
            if ((attackTarget == null || attackTarget != null && !attackTarget.isAlive()) && random.nextInt(300) == 0 && this.onGround && !this.isUpsideDown() && this.getY() + 2 < worldHeight.getY()) {
                if (this.getAnimation() == NO_ANIMATION) {
                    this.setAnimation(ANIMATION_JUMPUP);
                }
            }
            if (jumpingUp && this.getY() > worldHeight.getY()) {
                jumpingUp = false;
            }
            if ((this.onGround && this.getAnimation() == ANIMATION_JUMPUP && this.getAnimationTick() > 10 || jumpingUp && this.getAnimation() == NO_ANIMATION)) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 2F, 0));
                jumpingUp = true;
            }
            if (this.isUpsideDown()) {
                jumpingUp = false;
                this.setNoGravity(!this.onGround);
                float f = 0.91F;
                this.setDeltaMovement(this.getDeltaMovement().multiply(f, 1F, f));
                if (!this.verticalCollision) {
                    if (this.onGround || validBelowState || upwardsFallingTicks > 5) {
                        this.setUpsideDown(false);
                        upwardsFallingTicks = 0;
                    } else {
                        if (!validAboveState) {
                            upwardsFallingTicks++;
                        }
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2F, 0));
                    }
                } else {
                    upwardsFallingTicks = 0;
                }
                if (this.horizontalCollision) {
                    upwardsFallingTicks = 0;
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -0.3F, 0));
                }
                if (this.isInWall() && level.isEmptyBlock(this.getBlockPosBelowThatAffectsMyMovement())) {
                    this.setPos(this.getX(), this.getY() - 1, this.getZ());
                }
            } else {
                this.setNoGravity(false);
                if (validAboveState) {
                    this.setUpsideDown(true);
                }
            }
            if (this.isUpsideDown() && !this.isUpsideDownNavigator) {
                switchNavigator(false);
            }
            if (!this.isUpsideDown() && this.isUpsideDownNavigator) {
                switchNavigator(true);
            }
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(UPSIDE_DOWN, false);
    }

    public boolean isUpsideDown() {
        return this.entityData.get(UPSIDE_DOWN).booleanValue();
    }

    public void setUpsideDown(boolean upsideDown) {
        this.entityData.set(UPSIDE_DOWN, Boolean.valueOf(upsideDown));
    }

    protected BlockPos getPositionAbove() {
        return new BlockPos(this.position().x, this.getBoundingBox().maxY + 0.5000001D, this.position().z);
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BITE, ANIMATION_SWIPE_L, ANIMATION_SWIPE_R, ANIMATION_JUMPUP};
    }

    private boolean hasLineOfSightBlock(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level.clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos upperPos = this.getPositionAbove().above();
        BlockPos highest = getLowestPos(world, upperPos);
        this.setPos(highest.getX() + 0.5F, highest.getY(), highest.getZ() + 0.5F);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void onLand() {
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, (byte) 39);
            for (Entity entity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.5D))) {
                if (!isAlliedTo(entity) && !(entity instanceof EntityDropBear) && entity != this) {
                    entity.hurt(DamageSource.mobAttack(this), 2.0F + random.nextFloat() * 5F);
                    launch(entity, true);
                }
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.isOnGround()) {
            double d0 = e.getX() - this.getX();
            double d1 = e.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = 0.5F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 39) {
            spawnGroundEffects();
        } else {
            super.handleEntityEvent(id);

        }
    }

    public void spawnGroundEffects() {
        float radius = 2.3F;
        if (level.isClientSide) {
            for (int i1 = 0; i1 < 20 + random.nextInt(12); i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.yBodyRot) + i1;
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = getGroundPosition(new BlockPos(Mth.floor(this.getX() + extraX), this.getY(), Mth.floor(this.getZ() + extraZ)));
                BlockState BlockState = this.level.getBlockState(ground);
                if (BlockState.getMaterial() != Material.AIR) {
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockState), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        }
    }

    private BlockPos getGroundPosition(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getY(), in.getZ());
        while (position.getY() > 2 && level.isEmptyBlock(position) && level.getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return position;
    }

    class AIUpsideDownWander extends RandomStrollGoal {

        public AIUpsideDownWander() {
            super(EntityDropBear.this, 1D, 50);
        }

        @Nullable
        protected Vec3 getPosition() {
            if (EntityDropBear.this.isUpsideDown()) {
                for (int i = 0; i < 15; i++) {
                    Random rand = new Random();
                    BlockPos randPos = EntityDropBear.this.blockPosition().offset(rand.nextInt(16) - 8, -2, rand.nextInt(16) - 8);
                    BlockPos lowestPos = EntityDropBear.getLowestPos(level, randPos);
                    if (level.getBlockState(lowestPos).isFaceSturdy(level, lowestPos, Direction.DOWN)) {
                        return Vec3.atCenterOf(lowestPos);
                    }
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
            if (EntityDropBear.this.isUpsideDown()) {
                double d0 = EntityDropBear.this.getX() - wantedX;
                double d2 = EntityDropBear.this.getZ() - wantedZ;
                double d4 = d0 * d0 + d2 * d2;
                return d4 > 4;
            } else {
                return super.canContinueToUse();
            }
        }

        public void stop() {
            super.stop();
            this.wantedX = 0;
            this.wantedY = 0;
            this.wantedZ = 0;
        }

        public void start() {
            if (EntityDropBear.this.isUpsideDown()) {
                this.mob.getMoveControl().setWantedPosition(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier * 0.7F);
            } else {
                this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
            }
        }

    }

    private class AIDropMelee extends Goal {
        private boolean prevOnGround = false;

        public AIDropMelee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityDropBear.this.getTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = EntityDropBear.this.getTarget();
            if (target != null) {
                double dist = EntityDropBear.this.distanceTo(target);
                if (EntityDropBear.this.isUpsideDown()) {
                    double d0 = EntityDropBear.this.getX() - target.getX();
                    double d2 = EntityDropBear.this.getZ() - target.getZ();
                    double xzDistSqr = d0 * d0 + d2 * d2;
                    BlockPos ceilingPos = new BlockPos(target.getX(), EntityDropBear.this.getY() - 3 - random.nextInt(3), target.getZ());
                    BlockPos lowestPos = EntityDropBear.getLowestPos(level, ceilingPos);
                    EntityDropBear.this.getMoveControl().setWantedPosition(lowestPos.getX() + 0.5F, ceilingPos.getY(), lowestPos.getZ() + 0.5F, 1.1D);
                    if (xzDistSqr < 2.5F) {
                        EntityDropBear.this.setUpsideDown(false);
                    }
                } else {
                    if (EntityDropBear.this.onGround) {
                        EntityDropBear.this.getNavigation().moveTo(target, 1.2D);
                    }
                }
                if (dist < 3D) {
                    EntityDropBear.this.doHurtTarget(target);
                }
            }
        }

    }

}
