package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class EntitySeagull extends AnimalEntity implements ITargetsDroppedItems {

    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntitySeagull.class, DataSerializers.BOOLEAN);
    public float prevFlyProgress;
    public float flyProgress;
    public float prevFlapAmount;
    public float flapAmount;
    public boolean aiItemFlag = false;
    private boolean isLandNavigator;
    private int timeFlying;
    private BlockPos orbitPos = null;
    private double orbitDist = 5D;
    private boolean orbitClockwise = false;
    private boolean fallFlag = false;

    protected EntitySeagull(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathPriority(PathNodeType.COCOA, -1.0F);
        this.setPathPriority(PathNodeType.FENCE, -1.0F);
        switchNavigator(false);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(4, new AIScatter());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new AIWanderIdle());
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(9, new LookAtGoal(this, CreatureEntity.class, 6.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }


    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigator(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new MoveHelper(this);
            this.navigator = new DirectPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, false);
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if (flying && this.isChild()) {
            flying = false;
        }
        this.dataManager.set(FLYING, flying);
    }

    public void tick() {
        super.tick();
        this.prevFlyProgress = flyProgress;
        this.prevFlapAmount = flapAmount;
        float yMot = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (yMot < 0.0F) {
            flapAmount = Math.min(-yMot * 0.2F, 1F);
        } else {
            if (flapAmount > 0.0F) {
                flapAmount -= Math.min(flapAmount, 0.1F);
            } else {
                flapAmount = 0;
            }
        }
        if (!world.isRemote) {
            if (isFlying()) {
                if (this.onGround && !this.isInWaterOrBubbleColumn() && this.timeFlying > 30) {
                    this.setFlying(false);
                }
                timeFlying++;
                this.setNoGravity(true);
                if (this.isPassenger() || this.isInLove()) {
                    this.setFlying(false);
                }
            } else {
                fallFlag = false;
                timeFlying = 0;
                this.setNoGravity(false);
            }
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
        }
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return false;
    }

    @Override
    public void onGetItem(ItemEntity e) {

    }

    public Vector3d getBlockInViewAway(Vector3d fleePos, float radiusAdd) {
        float radius = 5 + this.getRNG().nextInt(5);
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = getCrowGround(radialPos);
        int distFromGround = (int) this.getPosY() - ground.getY();
        int flightHeight = 8 + this.getRNG().nextInt(4);
        BlockPos newPos = ground.up(distFromGround > 3 ? flightHeight : this.getRNG().nextInt(4) + 8);
        if (!this.isTargetBlocked(Vector3d.copyCentered(newPos)) && this.getDistanceSq(Vector3d.copyCentered(newPos)) > 1) {
            return Vector3d.copyCentered(newPos);
        }
        return null;
    }

    private BlockPos getCrowGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getPosY(), in.getZ());
        while (position.getY() < 256 && !world.getFluidState(position).isEmpty()) {
            position = position.up();
        }
        while (position.getY() > 2 && world.isAirBlock(position)) {
            position = position.down();
        }
        return position;
    }

    public Vector3d getBlockGrounding(Vector3d fleePos) {
        float radius = 10 + this.getRNG().nextInt(15);
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, getPosY(), fleePos.getZ() + extraZ);
        BlockPos ground = this.getCrowGround(radialPos);
        if (ground.getY() == 0) {
            return this.getPositionVec();
        } else {
            ground = this.getPosition();
            while (ground.getY() > 2 && world.isAirBlock(ground)) {
                ground = ground.down();
            }
        }
        if (!this.isTargetBlocked(Vector3d.copyCentered(ground.up()))) {
            return Vector3d.copyCentered(ground);
        }
        return null;
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());

        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    private Vector3d getOrbitVec(Vector3d vector3d, float gatheringCircleDist) {
        float angle = (0.01745329251F * (float) this.orbitDist * (orbitClockwise ? -ticksExisted : ticksExisted));
        double extraX = gatheringCircleDist * MathHelper.sin((angle));
        double extraZ = gatheringCircleDist * MathHelper.cos(angle);
        if (this.orbitPos != null) {
            Vector3d pos = new Vector3d(orbitPos.getX() + extraX, orbitPos.getY() + rand.nextInt(2), orbitPos.getZ() + extraZ);
            if (this.world.isAirBlock(new BlockPos(pos))) {
                return pos;
            }
        }
        return null;
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.getPosition();
        while (position.getY() > 0 && world.isAirBlock(position)) {
            position = position.down();
        }
        return !world.getFluidState(position).isEmpty() || position.getY() <= 0;
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return null;
    }

    private class AIScatter extends Goal {
        protected final EntitySeagull.AIScatter.Sorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vector3d flightTarget = null;
        private int cooldown = 0;
        private ITag tag;

        AIScatter() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            tag = EntityTypeTags.getCollection().get(AMTagRegistry.SCATTERS_CROWS);
            this.theNearestAttackableTargetSorter = new EntitySeagull.AIScatter.Sorter(EntitySeagull.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && e.getType().isContained(tag) || e instanceof PlayerEntity && !((PlayerEntity) e).isCreative();
                }
            };
        }

        @Override
        public boolean shouldExecute() {
            if (EntitySeagull.this.isPassenger() || EntitySeagull.this.aiItemFlag || EntitySeagull.this.isBeingRidden()) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntitySeagull.this.world.getGameTime() % 10;
                if (EntitySeagull.this.getIdleTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntitySeagull.this.getRNG().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntitySeagull.this.world.getEntitiesWithinAABB(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
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
        public boolean shouldContinueExecuting() {
            return targetEntity != null;
        }

        public void resetTask() {
            flightTarget = null;
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (flightTarget != null) {
                EntitySeagull.this.setFlying(true);
                EntitySeagull.this.getMoveHelper().setMoveTo(flightTarget.x, flightTarget.y, flightTarget.z, 1F);
                if (cooldown == 0 && EntitySeagull.this.isTargetBlocked(flightTarget)) {
                    cooldown = 30;
                    flightTarget = null;
                }
            }

            if (targetEntity != null) {
                if (EntitySeagull.this.onGround || flightTarget == null || flightTarget != null && EntitySeagull.this.getDistanceSq(flightTarget) < 3) {
                    Vector3d vec = EntitySeagull.this.getBlockInViewAway(targetEntity.getPositionVec(), 0);
                    if (vec != null && vec.getY() > EntitySeagull.this.getPosY()) {
                        flightTarget = vec;
                    }
                }
                if (EntitySeagull.this.getDistance(targetEntity) > 20.0F) {
                    this.resetTask();
                }
            }
        }

        protected double getTargetDistance() {
            return 4D;
        }

        protected AxisAlignedBB getTargetableArea(double targetDistance) {
            Vector3d renderCenter = new Vector3d(EntitySeagull.this.getPosX(), EntitySeagull.this.getPosY() + 0.5, EntitySeagull.this.getPosZ());
            AxisAlignedBB aabb = new AxisAlignedBB(-2, -2, -2, 2, 2, 2);
            return aabb.offset(renderCenter);
        }


        public class Sorter implements Comparator<Entity> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_) {
                double d0 = this.theEntity.getDistanceSq(p_compare_1_);
                double d1 = this.theEntity.getDistanceSq(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
        }
    }

    private class AIWanderIdle extends Goal {
        protected final EntitySeagull eagle;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;
        private int orbitResetCooldown = 0;
        private int maxOrbitTime = 360;
        private int orbitTime = 0;

        public AIWanderIdle() {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
            this.eagle = EntitySeagull.this;
        }

        @Override
        public boolean shouldExecute() {
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if ((eagle.getAttackTarget() != null && eagle.getAttackTarget().isAlive() && !this.eagle.isBeingRidden()) || this.eagle.isPassenger()) {
                return false;
            } else {
                if (this.eagle.getRNG().nextInt(15) != 0 && !eagle.isFlying()) {
                    return false;
                }
                if (this.eagle.isChild()) {
                    this.flightTarget = false;
                } else if (this.eagle.isInWaterOrBubbleColumn()) {
                    this.flightTarget = true;
                } else if (this.eagle.isOnGround()) {
                    this.flightTarget = rand.nextInt(5) == 0;
                } else {
                    if (orbitResetCooldown == 0 && rand.nextInt(6) == 0) {
                        orbitResetCooldown = 300 + rand.nextInt(300);
                        eagle.orbitPos = eagle.getPosition();
                        eagle.orbitDist = 4 + rand.nextInt(5);
                        eagle.orbitClockwise = rand.nextBoolean();
                        orbitTime = 0;
                        maxOrbitTime = (int) (180 + 360 * rand.nextFloat());
                    }
                    this.flightTarget = eagle.isBeingRidden() || rand.nextInt(7) > 0 && eagle.timeFlying < 400;
                }
                Vector3d lvt_1_1_ = this.getPosition();
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
            if (orbitResetCooldown > 0) {
                orbitResetCooldown--;
            }
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                if (orbitTime < maxOrbitTime && !eagle.isInWaterOrBubbleColumn()) {
                    orbitTime++;
                } else {
                    orbitTime = 0;
                    eagle.orbitPos = null;
                    orbitResetCooldown = -400 - rand.nextInt(400);
                }
            }
            if (eagle.collidedHorizontally && !eagle.onGround) {
                resetTask();
            }
            if (flightTarget) {
                eagle.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                if (eagle.isFlying() && !eagle.onGround) {
                    if (!eagle.isInWaterOrBubbleColumn()) {
                        eagle.setMotion(eagle.getMotion().mul(1.2F, 0.6F, 1.2F));
                    }
                } else {
                    this.eagle.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
                }
            }
            if (!flightTarget && isFlying()) {
                eagle.fallFlag = true;
                if(eagle.onGround){
                    eagle.setFlying(false);
                    orbitTime = 0;
                    eagle.orbitPos = null;
                    orbitResetCooldown = -400 - rand.nextInt(400);
                }
            }
            if (isFlying() && (!world.isAirBlock(eagle.getPositionUnderneath()) || eagle.onGround) && !eagle.isInWaterOrBubbleColumn() && eagle.timeFlying > 30) {
                eagle.setFlying(false);
                orbitTime = 0;
                eagle.orbitPos = null;
                orbitResetCooldown = -400 - rand.nextInt(400);
            }
        }

        @Nullable
        protected Vector3d getPosition() {
            Vector3d vector3d = eagle.getPositionVec();
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                return eagle.getOrbitVec(vector3d, 4 + rand.nextInt(2));
            }
            if (eagle.isBeingRidden() || eagle.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (eagle.timeFlying < 500 || eagle.isBeingRidden() || eagle.isOverWaterOrVoid()) {
                    return eagle.getBlockInViewAway(vector3d, 0);
                } else {
                    return eagle.getBlockGrounding(vector3d);
                }
            } else {
                return RandomPositionGenerator.findRandomTarget(this.eagle, 10, 7);
            }
        }

        public boolean shouldContinueExecuting() {
            if (flightTarget) {
                return eagle.isFlying() && eagle.getDistanceSq(x, y, z) > 2F;
            } else {
                return (!this.eagle.getNavigator().noPath()) && !this.eagle.isBeingRidden();
            }
        }

        public void startExecuting() {
            if (flightTarget) {
                eagle.setFlying(true);
                eagle.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                this.eagle.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
        }

        public void resetTask() {
            this.eagle.getNavigator().clearPath();
            super.resetTask();
        }
    }

    class MoveHelper extends MovementController {
        private final EntitySeagull parentEntity;

        public MoveHelper(EntitySeagull bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                double d5 = vector3d.length();
                if (d5 < 0.3) {
                    this.action = MovementController.Action.WAIT;
                    parentEntity.setMotion(parentEntity.getMotion().scale(0.5D));
                } else {
                    double d0 = this.posX - this.parentEntity.getPosX();
                    double d1 = this.posY - this.parentEntity.getPosY();
                    double d2 = this.posZ - this.parentEntity.getPosZ();
                    double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    float yScale = d1 > 0 || fallFlag ? 1F : 0.7F;
                    parentEntity.setMotion(parentEntity.getMotion().add(vector3d.scale(this.speed * 0.05D / d5)).mul(1F, yScale, 1F));
                    Vector3d vector3d1 = parentEntity.getMotion();
                    parentEntity.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.renderYawOffset = parentEntity.rotationYaw;

                }

            }
        }
    }
}
