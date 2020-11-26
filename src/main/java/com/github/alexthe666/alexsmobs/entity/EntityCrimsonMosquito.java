package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.message.MessageMountPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class EntityCrimsonMosquito extends MonsterEntity {

    public static final ResourceLocation FULL_LOOT = new ResourceLocation("alexsmobs", "entities/crimson_mosquito_full");
    protected static final EntitySize FLIGHT_SIZE = EntitySize.fixed(1.2F, 1.8F);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityCrimsonMosquito.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOOTING = EntityDataManager.createKey(EntityCrimsonMosquito.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BLOOD_LEVEL = EntityDataManager.createKey(EntityCrimsonMosquito.class, DataSerializers.VARINT);
    private static final Predicate<AnimalEntity> WARM_BLOODED = (mob) -> {
        return !(mob instanceof StriderEntity);
    };
    public float prevFlyProgress;
    public float flyProgress;
    public float prevShootProgress;
    public float shootProgress;
    public int shootingTicks;
    public int randomWingFlapTick = 0;
    private int flightTicks = 0;
    private boolean prevFlying = false;
    private int spitCooldown = 0;

    protected EntityCrimsonMosquito(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new EntityCrimsonMosquito.MoveHelperController(this);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.LAVA, 0.0F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ARMOR, 2.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return this.getBloodLevel() > 0 ? FULL_LOOT : super.getLootTable();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyTowardsTarget(this));
        this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyAwayFromTarget(this));
        this.goalSelector.addGoal(3, new EntityCrimsonMosquito.RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 32F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntityCrimsonMosquito.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, AnimalEntity.class, 40, true, false, WARM_BLOODED));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillagerEntity.class, true));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("FlightTicks", this.flightTicks);
        compound.putBoolean("Flying", this.isFlying());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.flightTicks = compound.getInt("FlightTicks");
        this.setFlying(compound.getBoolean("Flying"));
    }

    private void spit(LivingEntity target) {
        EntityMosquitoSpit llamaspitentity = new EntityMosquitoSpit(this.world, this);
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333D) - llamaspitentity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        llamaspitentity.shoot(d0, d1 + (double) f, d2, 1.5F, 10.0F);
        if (!this.isSilent()) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
        }
        if (this.getBloodLevel() > 0) {
            this.setBloodLevel(this.getBloodLevel() - 1);
        }
        this.world.addEntity(llamaspitentity);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.LAVA || source.isFireDamage() || super.isInvulnerableTo(source);
    }

    public void updateRidden() {
        Entity entity = this.getRidingEntity();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else {
            this.setMotion(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                Entity mount = this.getRidingEntity();
                if (mount instanceof LivingEntity) {
                    this.renderYawOffset = ((LivingEntity) mount).renderYawOffset;
                    this.rotationYaw = ((LivingEntity) mount).rotationYaw;
                    this.rotationYawHead = ((LivingEntity) mount).rotationYawHead;
                    this.prevRotationYaw = ((LivingEntity) mount).rotationYawHead;
                    float radius = 1F;
                    float angle = (0.01745329251F * ((LivingEntity) mount).renderYawOffset);
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    this.setPosition(mount.getPosX() + extraX, Math.max(mount.getPosY() + mount.getEyeHeight() * 0.25F, mount.getPosY()), mount.getPosZ() + extraZ);
                    if (!mount.isAlive() || mount instanceof PlayerEntity && ((PlayerEntity) mount).isCreative()) {
                        this.dismount();
                    }
                    if (mount.ticksExisted % 20 == 0 && !world.isRemote) {
                        mount.attackEntityFrom(DamageSource.causeMobDamage(this), 2.0F);
                        this.setBloodLevel(this.getBloodLevel() + 1);
                        if (this.getBloodLevel() > 3) {
                            this.dismount();
                            this.setFlying(false);
                            this.flightTicks = -15;
                        }
                    }
                }

            }
        }

    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, Boolean.valueOf(false));
        this.dataManager.register(SHOOTING, Boolean.valueOf(false));
        this.dataManager.register(BLOOD_LEVEL, 0);
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING).booleanValue();
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, flying);
    }

    public void setupShooting() {
        this.dataManager.set(SHOOTING, true);
        this.shootingTicks = 5;
    }

    public int getBloodLevel() {
        return Math.min(this.dataManager.get(BLOOD_LEVEL).intValue(), 4);
    }

    public void setBloodLevel(int bloodLevel) {
        this.dataManager.set(BLOOD_LEVEL, bloodLevel);
    }

    public void tick() {
        super.tick();
        boolean shooting = dataManager.get(SHOOTING);
        if (prevFlying != this.isFlying()) {
            this.recalculateSize();
        }
        if (shooting && shootProgress < 5) {
            shootProgress += 1;
        }
        if (!shooting && shootProgress > 0) {
            shootProgress -= 1;
        }
        if (this.isFlying() && flyProgress < 5) {
            flyProgress += 1;
        }
        if (!this.isFlying() && flyProgress > 0) {
            flyProgress -= 1;
        }
        if (!world.isRemote && this.isPassenger()) {
            this.setFlying(false);
        }
        if (!world.isRemote) {
            if (isFlying()) {
                this.setNoGravity(true);
            } else {
                this.setNoGravity(false);
            }
        }
        if (this.flyProgress == 0 && rand.nextInt(200) == 0) {
            randomWingFlapTick = 5 + rand.nextInt(15);
        }
        if (randomWingFlapTick > 0) {
            randomWingFlapTick--;
        }
        if (!world.isRemote && isOnGround() && !this.isFlying() && (flightTicks >= 0 && rand.nextInt(5) == 0 || this.getAttackTarget() != null)) {
            this.setFlying(true);
            this.setMotion(this.getMotion().add((this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.onGround = false;
            this.isAirBorne = true;
        }
        if (flightTicks < 0) {
            flightTicks++;
        }
        if (isFlying() & !world.isRemote) {
            flightTicks++;
            if (flightTicks > 200 && (this.getAttackTarget() == null || !this.getAttackTarget().isAlive())) {
                BlockPos above = this.getGroundPosition(this.getPosition().up());
                if (world.getFluidState(above).isEmpty() && !world.getBlockState(above).isAir()) {
                    this.getMotion().add(0, -0.2D, 0);
                    if (this.isOnGround()) {
                        this.setFlying(false);
                        flightTicks = -150 - rand.nextInt(200);
                    }
                }
            }
        }
        if (!world.isRemote && shootingTicks > 0) {
            shootingTicks--;
            if (shootingTicks == 0) {
                if (this.getAttackTarget() != null) {
                    this.spit(this.getAttackTarget());
                }
                this.dataManager.set(SHOOTING, false);
            }
        }
        prevFlyProgress = flyProgress;
        prevShootProgress = shootProgress;
        prevFlying = this.isFlying();
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public EntitySize getSize(Pose poseIn) {
        return isFlying() ? FLIGHT_SIZE : super.getSize(poseIn);
    }

    public void travel(Vector3d vec3d) {
        if (this.isOnGround() && !this.isFlying()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    private BlockPos getGroundPosition(BlockPos radialPos) {
        while (radialPos.getY() > 1 && world.isAirBlock(radialPos)) {
            radialPos = radialPos.down();
        }
        return radialPos;
    }

    static class RandomFlyGoal extends Goal {
        private final EntityCrimsonMosquito parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntityCrimsonMosquito mosquito) {
            this.parentEntity = mosquito;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {
            MovementController movementcontroller = this.parentEntity.getMoveHelper();
            if (!parentEntity.isFlying() || parentEntity.getAttackTarget() != null) {
                return false;
            }
            if (!movementcontroller.isUpdating() || target == null) {
                target = getBlockInViewMosquito();
                if (target != null) {
                    this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean shouldContinueExecuting() {
            return target != null && parentEntity.isFlying() && parentEntity.getDistanceSq(Vector3d.copyCentered(target)) > 2.4D && parentEntity.getMoveHelper().isUpdating() && !parentEntity.collidedHorizontally;
        }

        public void resetTask() {
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewMosquito();
            }
            if (target != null) {
                this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.getDistanceSq(Vector3d.copyCentered(target)) < 2.5F) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewMosquito() {
            float radius = 1 + parentEntity.getRNG().nextInt(5);
            float neg = parentEntity.getRNG().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.renderYawOffset;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (parentEntity.getRNG().nextFloat() * neg);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(parentEntity.getPosX() + extraX, parentEntity.getPosY() + 2, parentEntity.getPosZ() + extraZ);
            BlockPos ground = parentEntity.getGroundPosition(radialPos);
            BlockPos newPos = ground.up(1 + parentEntity.getRNG().nextInt(6));
            if (!parentEntity.isTargetBlocked(Vector3d.copyCentered(newPos)) && parentEntity.getDistanceSq(Vector3d.copyCentered(newPos)) > 6) {
                return newPos;
            }
            return null;
        }

    }

    static class MoveHelperController extends MovementController {
        private final EntityCrimsonMosquito parentEntity;

        public MoveHelperController(EntityCrimsonMosquito sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        public void tick() {
            if (parentEntity.isFlying()) {
                if (this.action == Action.STRAFE) {
                    Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                    double d0 = vector3d.length();
                    parentEntity.setMotion(parentEntity.getMotion().add(0, vector3d.scale(this.speed * 0.05D / d0).getY(), 0));
                    float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                    float f1 = (float) this.speed * f;
                    float f2 = this.moveForward;
                    float f3 = this.moveStrafe;
                    float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
                    if (f4 < 1.0F) {
                        f4 = 1.0F;
                    }

                    f4 = f1 / f4;
                    f2 = f2 * f4;
                    f3 = f3 * f4;
                    float f5 = MathHelper.sin(this.mob.rotationYaw * ((float) Math.PI / 180F));
                    float f6 = MathHelper.cos(this.mob.rotationYaw * ((float) Math.PI / 180F));
                    float f7 = f2 * f6 - f3 * f5;
                    float f8 = f3 * f6 + f2 * f5;
                    this.moveForward = 1.0F;
                    this.moveStrafe = 0.0F;

                    this.mob.setAIMoveSpeed(f1);
                    this.mob.setMoveForward(this.moveForward);
                    this.mob.setMoveStrafing(this.moveStrafe);
                    this.action = MovementController.Action.WAIT;
                } else if (this.action == MovementController.Action.MOVE_TO) {
                    Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                    double d0 = vector3d.length();
                    if (d0 < parentEntity.getBoundingBox().getAverageEdgeLength()) {
                        this.action = MovementController.Action.WAIT;
                        parentEntity.setMotion(parentEntity.getMotion().scale(0.5D));
                    } else {
                        parentEntity.setMotion(parentEntity.getMotion().add(vector3d.scale(this.speed * 0.05D / d0)));
                        if (parentEntity.getAttackTarget() == null) {
                            Vector3d vector3d1 = parentEntity.getMotion();
                            parentEntity.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                            parentEntity.renderYawOffset = parentEntity.rotationYaw;
                        } else {
                            double d2 = parentEntity.getAttackTarget().getPosX() - parentEntity.getPosX();
                            double d1 = parentEntity.getAttackTarget().getPosZ() - parentEntity.getPosZ();
                            parentEntity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                            parentEntity.renderYawOffset = parentEntity.rotationYaw;
                        }
                    }

                }
            } else {
                action = Action.WAIT;
                this.mob.setAIMoveSpeed(0);
                this.mob.setMoveForward(0);
                this.mob.setMoveStrafing(0);

            }
        }

        private boolean func_220673_a(Vector3d p_220673_1_, int p_220673_2_) {
            AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.offset(p_220673_1_);
                if (!this.parentEntity.world.hasNoCollisions(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    public class FlyTowardsTarget extends Goal {
        private final EntityCrimsonMosquito parentEntity;

        public FlyTowardsTarget(EntityCrimsonMosquito mosquito) {
            this.parentEntity = mosquito;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {
            if (!parentEntity.isFlying() || parentEntity.getBloodLevel() > 0) {
                return false;
            }
            return !parentEntity.isPassenger() && parentEntity.getAttackTarget() != null && !isBittenByMosquito(parentEntity.getAttackTarget());
        }

        public boolean shouldContinueExecuting() {
            return parentEntity.getAttackTarget() != null && !isBittenByMosquito(parentEntity.getAttackTarget()) && !parentEntity.collidedHorizontally && parentEntity.getBloodLevel() == 0 && parentEntity.isFlying() && parentEntity.getMoveHelper().isUpdating();
        }

        public boolean isBittenByMosquito(Entity entity) {
            for (Entity e : entity.getPassengers()) {
                if (e instanceof EntityCrimsonMosquito) {
                    return true;
                }
            }
            return false;
        }

        public void resetTask() {
        }

        public void tick() {
            if (parentEntity.getAttackTarget() != null) {
                this.parentEntity.getMoveHelper().setMoveTo(parentEntity.getAttackTarget().getPosX(), parentEntity.getAttackTarget().getPosY(), parentEntity.getAttackTarget().getPosZ(), 1.0D);
                if (parentEntity.getBoundingBox().grow(0.3F, 0.3F, 0.3F).intersects(parentEntity.getAttackTarget().getBoundingBox()) && !isBittenByMosquito(parentEntity.getAttackTarget())) {
                    parentEntity.startRiding(parentEntity.getAttackTarget(), true);
                    if (!parentEntity.world.isRemote) {
                        AlexsMobs.sendMSGToAll(new MessageMountPlayer(parentEntity.getEntityId(), parentEntity.getAttackTarget().getEntityId()));
                    }
                }
            }
        }
    }

    public class FlyAwayFromTarget extends Goal {
        private final EntityCrimsonMosquito parentEntity;
        private int spitCooldown = 0;
        private BlockPos shootPos = null;

        public FlyAwayFromTarget(EntityCrimsonMosquito mosquito) {
            this.parentEntity = mosquito;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {
            if (!parentEntity.isFlying() || parentEntity.getBloodLevel() <= 0) {
                return false;
            }
            if (!parentEntity.isPassenger() && parentEntity.getAttackTarget() != null) {
                shootPos = getBlockInTargetsViewMosquito(parentEntity.getAttackTarget());
                return true;
            }
            return false;
        }

        public boolean shouldContinueExecuting() {
            return parentEntity.getAttackTarget() != null && parentEntity.getBloodLevel() > 0 && parentEntity.isFlying() && !parentEntity.collidedHorizontally;
        }

        public void resetTask() {
            spitCooldown = 20;
        }

        public void tick() {
            if (spitCooldown > 0) {
                spitCooldown--;
            }
            if (parentEntity.getAttackTarget() != null) {
                if (shootPos == null) {
                    shootPos = getBlockInTargetsViewMosquito(parentEntity.getAttackTarget());
                } else {
                    this.parentEntity.getMoveHelper().setMoveTo(shootPos.getX() + 0.5D, shootPos.getY() + 0.5D, shootPos.getZ() + 0.5D, 1.0D);
                    this.parentEntity.faceEntity(parentEntity.getAttackTarget(), 30.0F, 30.0F);
                    if (parentEntity.getDistanceSq(Vector3d.copyCentered(shootPos)) < 2.5F) {
                        if (spitCooldown == 0) {
                            parentEntity.setupShooting();
                            spitCooldown = 20;
                        }
                        shootPos = null;
                    }
                }
            }

        }

        public BlockPos getBlockInTargetsViewMosquito(LivingEntity target) {
            float radius = 4 + parentEntity.getRNG().nextInt(5);
            float neg = parentEntity.getRNG().nextBoolean() ? 1 : -1;
            float angle = (0.01745329251F * (target.rotationYawHead + 90F + parentEntity.getRNG().nextInt(180)));
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(target.getPosX() + extraX, target.getPosY() + 1, target.getPosZ() + extraZ);
            BlockPos ground = radialPos;
            if (parentEntity.getDistanceSq(Vector3d.copyCentered(ground)) > 30) {
                if (!parentEntity.isTargetBlocked(Vector3d.copyCentered(ground)) && parentEntity.getDistanceSq(Vector3d.copyCentered(ground)) > 6) {
                    return ground;
                }
            }
            return parentEntity.getPosition();
        }
    }
}
