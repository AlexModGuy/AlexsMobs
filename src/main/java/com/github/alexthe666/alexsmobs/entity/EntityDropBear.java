package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class EntityDropBear extends MonsterEntity implements IAnimatedEntity {

    public static final Animation ANIMATION_BITE = Animation.create(9);
    public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
    public static final Animation ANIMATION_SWIPE_L = Animation.create(15);
    private static final DataParameter<Boolean> UPSIDE_DOWN = EntityDataManager.createKey(EntityDropBear.class, DataSerializers.BOOLEAN);
    public float prevUpsideDownProgress;
    public float upsideDownProgress;
    public boolean fallRotation = rand.nextBoolean();
    private int animationTick;
    private Animation currentAnimation;
    private int upwardsFallingTicks = 0;
    private boolean isUpsideDownNavigator;
    private boolean prevOnGround = false;

    protected EntityDropBear(EntityType type, World world) {
        super(type, world);
        switchNavigator(true);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.7F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    public static BlockPos getLowestPos(IWorld world, BlockPos pos) {
        while (!world.getBlockState(pos).isSolidSide(world, pos, Direction.DOWN) && pos.getY() < 255) {
            pos = pos.up();
        }
        return pos;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(rand.nextBoolean() ? ANIMATION_BITE : rand.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
        }
        return true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new AIDropMelee());
        this.goalSelector.addGoal(2, new AIUpsideDownWander());
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntityDropBear.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillagerEntity.class, true));
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.FALL || source == DamageSource.IN_WALL;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        super.updateFallState(y, onGroundIn, state, pos);
    }

    protected void playFallSound() {
        this.onLand();
        super.playFallSound();
    }

    private void switchNavigator(boolean rightsideUp) {
        if (rightsideUp) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isUpsideDownNavigator = false;
        } else {
            this.moveController = new FlightMoveController(this, 1F, false);
            this.navigator = new DirectPathNavigator(this, world);
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
        BlockPos abovePos = this.getPositionAbove();
        BlockState aboveState = world.getBlockState(abovePos);
        BlockState belowState = world.getBlockState(this.getPositionUnderneath());
        boolean validAboveState = aboveState.isSolidSide(world, abovePos, Direction.DOWN);
        boolean validBelowState = belowState.isSolidSide(world, this.getPositionUnderneath(), Direction.UP);
        if (!world.isRemote) {
            LivingEntity attackTarget = this.getAttackTarget();
            if (attackTarget != null && getDistance(attackTarget) < attackTarget.getWidth() + this.getWidth() + 1 && this.canEntityBeSeen(attackTarget)) {
                if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 6) {
                    attackTarget.applyKnockback(0.5F, (double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F))));
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_L) && this.getAnimationTick() == 9) {
                    float rot = rotationYaw + 90;
                    attackTarget.applyKnockback(0.5F, MathHelper.sin(rot * ((float) Math.PI / 180F)), -MathHelper.cos(rot * ((float) Math.PI / 180F)));
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_R) && this.getAnimationTick() == 9) {
                    float rot = rotationYaw - 90;
                    attackTarget.applyKnockback(0.5F, MathHelper.sin(rot * ((float) Math.PI / 180F)), -MathHelper.cos(rot * ((float) Math.PI / 180F)));
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                }

            }
            if (this.isUpsideDown()) {
                this.setNoGravity(!this.onGround);
                float f = 0.91F;
                this.setMotion(this.getMotion().mul(f, 1F, f));
                if (!this.collidedVertically) {
                    if (this.onGround || validBelowState || upwardsFallingTicks > 5) {
                        this.setUpsideDown(false);
                        upwardsFallingTicks = 0;
                    } else {
                        upwardsFallingTicks++;
                        this.setMotion(this.getMotion().add(0, 0.2F, 0));
                    }
                } else {
                    upwardsFallingTicks = 0;
                }
                if (this.collidedHorizontally) {
                    upwardsFallingTicks = 0;
                    this.setMotion(this.getMotion().add(0, -0.3F, 0));
                }
                if (this.isEntityInsideOpaqueBlock()) {
                    this.setPosition(this.getPosX(), this.getPosY() - 1, this.getPosZ());
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

    protected void registerData() {
        super.registerData();
        this.dataManager.register(UPSIDE_DOWN, false);
    }

    public boolean isUpsideDown() {
        return this.dataManager.get(UPSIDE_DOWN).booleanValue();
    }

    public void setUpsideDown(boolean upsideDown) {
        this.dataManager.set(UPSIDE_DOWN, Boolean.valueOf(upsideDown));
    }

    protected BlockPos getPositionAbove() {
        return new BlockPos(this.getPositionVec().x, this.getBoundingBox().maxY + 0.5000001D, this.getPositionVec().z);
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
        return new Animation[]{ANIMATION_BITE, ANIMATION_SWIPE_L, ANIMATION_SWIPE_R};
    }

    private boolean canSeeBlock(BlockPos destinationBlock) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        Vector3d blockVec = net.minecraft.util.math.vector.Vector3d.copyCentered(destinationBlock);
        BlockRayTraceResult result = this.world.rayTraceBlocks(new RayTraceContext(Vector3d, blockVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        return result.getPos().equals(destinationBlock);
    }

    private void doInitialPosing(IWorld world) {
        BlockPos upperPos = this.getPositionAbove().up();
        BlockPos highest = getLowestPos(world, upperPos);
        this.setPosition(highest.getX() + 0.5F, highest.getY(), highest.getZ() + 0.5F);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (reason == SpawnReason.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void onLand() {
        if (!world.isRemote) {
            world.setEntityState(this, (byte) 39);
            for (Entity entity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(2.5D), null)) {
                if (!isOnSameTeam(entity) && !(entity instanceof EntityDropBear) && entity != this) {
                    entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2.0F + rand.nextFloat() * 5F);
                    launch(entity, true);
                }
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.isOnGround()) {
            double d0 = e.getPosX() - this.getPosX();
            double d1 = e.getPosZ() - this.getPosZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = 0.5F;
            e.addVelocity(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 39) {
            spawnGroundEffects();
        } else {
            super.handleStatusUpdate(id);

        }
    }

    public void spawnGroundEffects() {
        float radius = 2.3F;
        if (world.isRemote) {
            for (int i1 = 0; i1 < 20 + rand.nextInt(12); i1++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.renderYawOffset) + i1;
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * MathHelper.cos(angle);
                BlockPos ground = getGroundPosition(new BlockPos(MathHelper.floor(this.getPosX() + extraX), this.getPosY(), MathHelper.floor(this.getPosZ() + extraZ)));
                BlockState BlockState = this.world.getBlockState(ground);
                if (BlockState.getMaterial() != Material.AIR) {
                    world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState), true, this.getPosX() + extraX, ground.getY() + extraY, this.getPosZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        }
    }

    private BlockPos getGroundPosition(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getPosY(), in.getZ());
        while (position.getY() > 2 && world.isAirBlock(position) && world.getFluidState(position).isEmpty()) {
            position = position.down();
        }
        return position;
    }

    class AIUpsideDownWander extends RandomWalkingGoal {

        public AIUpsideDownWander() {
            super(EntityDropBear.this, 1D, 50);
        }

        @Nullable
        protected Vector3d getPosition() {
            if (EntityDropBear.this.isUpsideDown()) {
                for (int i = 0; i < 15; i++) {
                    Random rand = new Random();
                    BlockPos randPos = EntityDropBear.this.getPosition().add(rand.nextInt(16) - 8, -2, rand.nextInt(16) - 8);
                    BlockPos lowestPos = EntityDropBear.getLowestPos(world, randPos);
                    if (world.getBlockState(lowestPos).isSolidSide(world, lowestPos, Direction.DOWN)) {
                        return Vector3d.copyCentered(lowestPos);
                    }
                }
                return null;
            } else {
                return super.getPosition();
            }
        }

        public boolean shouldExecute() {
            return super.shouldExecute();
        }

        public boolean shouldContinueExecuting() {
            if (EntityDropBear.this.isUpsideDown()) {
                double d0 = EntityDropBear.this.getPosX() - x;
                double d2 = EntityDropBear.this.getPosZ() - z;
                double d4 = d0 * d0 + d2 * d2;
                return d4 > 4;
            } else {
                return super.shouldContinueExecuting();
            }
        }

        public void resetTask() {
            super.resetTask();
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }

        public void startExecuting() {
            if (EntityDropBear.this.isUpsideDown()) {
                this.creature.getMoveHelper().setMoveTo(this.x, this.y, this.z, this.speed * 0.7F);
            } else {
                this.creature.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);
            }
        }

    }

    private class AIDropMelee extends Goal {
        private boolean prevOnGround = false;

        public AIDropMelee() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            return EntityDropBear.this.getAttackTarget() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = EntityDropBear.this.getAttackTarget();
            if (target != null) {
                double dist = EntityDropBear.this.getDistance(target);
                if (EntityDropBear.this.isUpsideDown()) {
                    double d0 = EntityDropBear.this.getPosX() - target.getPosX();
                    double d2 = EntityDropBear.this.getPosZ() - target.getPosZ();
                    double xzDistSqr = d0 * d0 + d2 * d2;
                    BlockPos ceilingPos = new BlockPos(target.getPosX(), EntityDropBear.this.getPosY() - 3 - rand.nextInt(3), target.getPosZ());
                    BlockPos lowestPos = EntityDropBear.getLowestPos(world, ceilingPos);
                    EntityDropBear.this.getMoveHelper().setMoveTo(lowestPos.getX() + 0.5F, ceilingPos.getY(), lowestPos.getZ() + 0.5F, 1.1D);
                    if (xzDistSqr < 2.5F) {
                        EntityDropBear.this.setUpsideDown(false);
                    }
                } else {
                    if (EntityDropBear.this.onGround) {
                        EntityDropBear.this.getNavigator().tryMoveToEntityLiving(target, 1.2D);
                    }
                }
                if (dist < 3D) {
                    EntityDropBear.this.attackEntityAsMob(target);
                }
            }
        }

    }

}
