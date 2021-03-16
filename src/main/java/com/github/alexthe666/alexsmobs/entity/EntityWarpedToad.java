package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class EntityWarpedToad extends TameableEntity implements ITargetsDroppedItems, IFollower, ISemiAquatic {

    private static final DataParameter<Float> TONGUE_LENGTH = EntityDataManager.createKey(EntityWarpedToad.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> TONGUE_OUT = EntityDataManager.createKey(EntityWarpedToad.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityWarpedToad.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityWarpedToad.class, DataSerializers.VARINT);
    public float blinkProgress;
    public float prevBlinkProgress;
    public float attackProgress;
    public float prevAttackProgress;
    public float sitProgress;
    public float prevSitProgress;
    public float swimProgress;
    public float prevSwimProgress;
    private boolean isLandNavigator;
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;
    private int swimTimer = -100;

    protected EntityWarpedToad(EntityType entityType, World world) {
        super(entityType, world);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.LAVA, 0.0F);
        switchNavigator(false);
    }

    public static boolean canWarpedToadSpawn(EntityType<? extends MobEntity> typeIn, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockPos blockpos = pos.down();
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.WARPED_TOAD_SPAWNS).contains(worldIn.getBlockState(blockpos).getBlock());
        return reason == SpawnReason.SPAWNER || spawnBlock;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.25F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.WARPED_TOAD_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.WARPED_TOAD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.WARPED_TOAD_HURT;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.warpedToadSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public int getMaxSpawnedInChunk() {
        return 5;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.func_233687_w_(false);
            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("MonkeySitting", this.isSitting());
        compound.putInt("Command", this.getCommand());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("MonkeySitting"));
        this.setCommand(compound.getInt("Command"));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new EntityWarpedToad.TongueAttack(this));
        this.goalSelector.addGoal(2, new FollowOwner(this, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.fromItems(AMItemRegistry.MAGGOT, AMItemRegistry.MOSQUITO_LARVA), false));
        this.goalSelector.addGoal(5, new WarpedToadAIRandomSwimming(this, 1.0D, 7));
        this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 60, 1.0D, 5, 4));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(11, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.WARPED_TOAD_TARGETS))));
        this.targetSelector.addGoal(5, new HurtByTargetGoal(this));
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && (this.isInWater() || this.isInLava())) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    protected float getJumpUpwardsMotion() {
        return 0.5F;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }


    protected void jump() {
        super.jump();
        double d0 = this.moveController.getSpeed();
        if (d0 > 0.0D) {
            double d1 = horizontalMag(this.getMotion());
            if (d1 < 0.01D) {
            }
        }

        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte) 1);
        }

    }

    public void setMovementSpeed(double newSpeed) {
        this.getNavigator().setSpeed(newSpeed);
        this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ(), newSpeed);
    }

    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        if (jumping) {
            //    this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    public void updateAITasks() {
        super.updateAITasks();

        if (this.currentMoveTypeDuration > 0) {
            --this.currentMoveTypeDuration;
        }

        if (this.onGround) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            if (this.currentMoveTypeDuration == 0) {
                LivingEntity livingentity = this.getAttackTarget();
                if (livingentity != null && this.getDistanceSq(livingentity) < 16.0D) {
                    this.calculateRotationYaw(livingentity.getPosX(), livingentity.getPosZ());
                    this.moveController.setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), this.moveController.getSpeed());
                    this.startJumping();
                    this.wasOnGround = true;
                }
            }
            if (this.jumpController instanceof EntityWarpedToad.JumpHelperController) {
                EntityWarpedToad.JumpHelperController rabbitController = (EntityWarpedToad.JumpHelperController) this.jumpController;
                if (!rabbitController.getIsJumping()) {
                    if (this.moveController.isUpdating() && this.currentMoveTypeDuration == 0) {
                        Path path = this.navigator.getPath();
                        Vector3d vector3d = new Vector3d(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ());
                        if (path != null && !path.isFinished()) {
                            vector3d = path.getPosition(this);
                        }

                        this.calculateRotationYaw(vector3d.x, vector3d.z);
                        this.startJumping();
                    }
                } else if (!rabbitController.canJump()) {
                    this.enableJumpControl();
                }
            }
        }

        this.wasOnGround = this.onGround;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.MOSQUITO_LARVA && isTamed();
    }


    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if (!isTamed() && item == AMItemRegistry.MOSQUITO_LARVA) {
            this.consumeItemFromStack(player, itemstack);
            this.playSound(SoundEvents.ENTITY_STRIDER_EAT, this.getSoundVolume(), this.getSoundPitch());
            if (getRNG().nextInt(3) == 0) {
                this.setTamedBy(player);
                this.world.setEntityState(this, (byte) 7);
            } else {
                this.world.setEntityState(this, (byte) 6);
            }
            return ActionResultType.SUCCESS;
        }
        if (isTamed() && (item == AMItemRegistry.MAGGOT)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.consumeItemFromStack(player, itemstack);
                this.playSound(SoundEvents.ENTITY_STRIDER_EAT, this.getSoundVolume(), this.getSoundPitch());
                this.heal(5);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;

        }
        if (type != ActionResultType.SUCCESS && isTamed() && isOwner(player) && !isBreedingItem(itemstack)) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
                this.setCommand(0);
            }
            player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
                this.setSitting(true);
                return ActionResultType.SUCCESS;
            } else {
                this.setSitting(false);
                return ActionResultType.SUCCESS;
            }
        }
        return type;
    }


    public boolean isOnSameTeam(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TameableEntity) {
                return ((TameableEntity) entityIn).isOwner(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isOnSameTeam(entityIn);
            }
        }

        return super.isOnSameTeam(entityIn);
    }

    public boolean shouldSpawnRunningEffects() {
        return false;
    }

    private void calculateRotationYaw(double x, double z) {
        this.rotationYaw = (float) (MathHelper.atan2(z - this.getPosZ(), x - this.getPosX()) * (double) (180F / (float) Math.PI)) - 90.0F;
    }

    private void enableJumpControl() {
        if (jumpController instanceof EntityWarpedToad.JumpHelperController) {
            ((EntityWarpedToad.JumpHelperController) this.jumpController).setCanJump(true);
        }
    }

    private void disableJumpControl() {
        if (jumpController instanceof EntityWarpedToad.JumpHelperController) {
            ((EntityWarpedToad.JumpHelperController) this.jumpController).setCanJump(false);
        }
    }

    private void updateMoveTypeDuration() {
        if (this.moveController.getSpeed() < 2.2D) {
            this.currentMoveTypeDuration = 10;
        } else {
            this.currentMoveTypeDuration = 1;
        }

    }

    private void checkLandingDelay() {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    public void livingTick() {
        super.livingTick();
        if(this.isChild() && this.getEyeHeight() > this.getHeight()){
            this.recalculateSize();
        }
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
        if (!world.isRemote) {
            if (isInWater() || isInLava()) {
                if (swimTimer < 0) {
                    swimTimer = 0;
                }
                swimTimer++;
            } else {
                if (swimTimer > 0) {
                    swimTimer = 0;
                }
                swimTimer--;
            }
        }
    }


    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.jumpController = new EntityWarpedToad.JumpHelperController(this);
            this.moveController = new EntityWarpedToad.MoveHelperController(this);
            this.navigator = createNavigator(world);
            this.isLandNavigator = true;
        } else {
            this.jumpController = new JumpController(this);
            this.moveController = new AquaticMoveController(this, 1.2F);
            this.navigator = new BoneSerpentPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TONGUE_LENGTH, 1F);
        this.dataManager.register(TONGUE_OUT, false);
        this.dataManager.register(COMMAND, Integer.valueOf(0));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public void tick() {
        super.tick();
        prevBlinkProgress = blinkProgress;
        prevAttackProgress = attackProgress;
        prevSitProgress = sitProgress;
        prevSwimProgress = swimProgress;
        this.stepHeight = 1;

        boolean isTechnicalBlinking = this.ticksExisted % 50 > 42;
        if (isTechnicalBlinking && blinkProgress < 5F) {
            blinkProgress++;
        }
        if (!isTechnicalBlinking && blinkProgress > 0F) {
            blinkProgress--;
        }
        if (isTongueOut() && attackProgress < 5F) {
            attackProgress++;
        }
        LivingEntity entityIn = this.getAttackTarget();
        if (entityIn != null && attackProgress > 0) {
            if (isTongueOut()) {
                double d0 = entityIn.getPosX() - this.getPosX();
                double d2 = entityIn.getPosZ() - this.getPosZ();
                double d1 = entityIn.getPosYEye() - this.getPosYEye();
                double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
                float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                float f1 = (float) (-(MathHelper.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
                this.rotationPitch = f1;
                this.rotationYaw = f;
                this.renderYawOffset = this.rotationYaw;
                this.rotationYawHead = this.rotationYaw;
            } else {
                if (entityIn instanceof EntityCrimsonMosquito) {
                    ((EntityCrimsonMosquito) entityIn).setShrink(true);
                }
                this.rotationPitch = 0;
                float radius = attackProgress * 0.2F * 1.2F * (getTongueLength() - getTongueLength() * 0.4F);
                float angle = (0.01745329251F * this.renderYawOffset);
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraZ = radius * MathHelper.cos(angle);
                double yHelp = entityIn.getHeight();
                entityIn.setPosition(this.getPosX() + extraX, this.getPosY() + this.getEyeHeight() - yHelp, this.getPosZ() + extraZ);
                if (attackProgress == 0.5F) {
                    float damage = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                    if (entityIn instanceof EntityCrimsonMosquito) {
                        damage = Float.MAX_VALUE;
                    }
                    entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
                }
            }

            if (attackProgress == 5 && (entityIn.getHeight() < 0.89D || entityIn instanceof EntityCrimsonMosquito) && !entityIn.isPassenger(this)) {
            }
        }
        if (!world.isRemote && isTongueOut() && attackProgress == 5F) {
            setTongueOut(false);
            attackProgress = 4F;
        }
        if (!isTongueOut() && attackProgress > 0F) {
            attackProgress -= 0.5F;
        }
        if (isSitting() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress--;
        }
        if (shouldSwim() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!shouldSwim() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (shouldSwim() && swimProgress < 5F) {
            swimProgress++;
        }
        if (!shouldSwim() && swimProgress > 0F) {
            swimProgress--;
        }
    }

    public boolean shouldSwim() {
        return isInWater() || isInLava();
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.MAGGOT;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.heal(5);
    }

    public boolean isBlinking() {
        return blinkProgress > 1 || blinkProgress < -1 || attackProgress > 1;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.WARPED_TOAD.create(serverWorld);
    }

    public float getTongueLength() {
        return dataManager.get(TONGUE_LENGTH);
    }

    public void setTongueLength(float length) {
        dataManager.set(TONGUE_LENGTH, length);
    }

    public float getJumpCompletion(float partialTicks) {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + partialTicks) / (float) this.jumpDuration;
    }

    public boolean isPushedByWater() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 1) {
            this.handleRunningEffect();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleStatusUpdate(id);
        }

    }

    public boolean hasJumper() {
        return jumpController instanceof EntityWarpedToad.JumpHelperController;
    }

    @Override
    public boolean shouldEnterWater() {
        return swimTimer < -200 && !isSitting() && this.getCommand() != 1;
    }

    @Override
    public boolean shouldLeaveWater() {
        return swimTimer > 600 && !isSitting() && this.getCommand() != 1;
    }

    @Override
    public boolean shouldStopMoving() {
        return isSitting();
    }

    private boolean isTongueOut() {
        return this.dataManager.get(TONGUE_OUT);
    }

    private void setTongueOut(boolean out) {
        this.dataManager.set(TONGUE_OUT, out);
    }

    @Override
    public int getWaterSearchRange() {
        return 8;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    static class MoveHelperController extends MovementController {
        private final EntityWarpedToad warpedToad;
        private double nextJumpSpeed;

        public MoveHelperController(EntityWarpedToad warpedToad) {
            super(warpedToad);
            this.warpedToad = warpedToad;
        }

        public void tick() {
            if (this.warpedToad.hasJumper() && this.warpedToad.onGround && !this.warpedToad.isJumping && !((EntityWarpedToad.JumpHelperController) this.warpedToad.jumpController).getIsJumping()) {
                this.warpedToad.setMovementSpeed(0.0D);
            } else if (this.isUpdating()) {
                this.warpedToad.setMovementSpeed(this.nextJumpSpeed);
            }

            super.tick();
        }

        /**
         * Sets the speed and location to move to
         */
        public void setMoveTo(double x, double y, double z, double speedIn) {
            if (this.warpedToad.isInWater()) {
                speedIn = 1.5D;
            }

            super.setMoveTo(x, y, z, speedIn);
            if (speedIn > 0.0D) {
                this.nextJumpSpeed = speedIn;
            }

        }
    }

    public class JumpHelperController extends JumpController {
        private final EntityWarpedToad toad;
        private boolean canJump;

        public JumpHelperController(EntityWarpedToad toad) {
            super(toad);
            this.toad = toad;
        }

        public boolean getIsJumping() {
            return this.isJumping;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean canJumpIn) {
            this.canJump = canJumpIn;
        }

        public void tick() {
            if (this.isJumping) {
                this.toad.startJumping();
                this.isJumping = false;
            }

        }
    }

    public class TongueAttack extends Goal {
        private final EntityWarpedToad parentEntity;
        private int spitCooldown = 0;
        private BlockPos shootPos = null;

        public TongueAttack(EntityWarpedToad toad) {
            this.parentEntity = toad;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
        }

        public boolean shouldExecute() {

            return parentEntity.getAttackTarget() != null && parentEntity.getPassengers().isEmpty();
        }

        public boolean shouldContinueExecuting() {
            return parentEntity.getAttackTarget() != null && parentEntity.getPassengers().isEmpty();
        }

        public void resetTask() {
            spitCooldown = 20;
            parentEntity.getNavigator().clearPath();
        }

        public void tick() {
            if (spitCooldown > 0) {
                spitCooldown--;
            }
            Entity entityIn = parentEntity.getAttackTarget();
            if (entityIn != null) {
                double dist = parentEntity.getDistance(entityIn);
                if (dist < 8 && this.parentEntity.canEntityBeSeen(entityIn)) {
                    if (!parentEntity.isTongueOut() && parentEntity.attackProgress == 0 && spitCooldown == 0) {
                        this.parentEntity.setTongueLength((float) Math.max(1F, dist + 2F));
                        spitCooldown = 10;
                        this.parentEntity.setTongueOut(true);
                    }
                }
                this.parentEntity.getNavigator().tryMoveToEntityLiving(entityIn, 1.4F);


            }
        }
    }

    public class FollowOwner extends Goal {
        private final EntityWarpedToad tameable;
        private final IWorldReader world;
        private final double followSpeed;
        private final float maxDist;
        private final float minDist;
        private final boolean teleportToLeaves;
        private LivingEntity owner;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public FollowOwner(EntityWarpedToad p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
            this.tameable = p_i225711_1_;
            this.world = p_i225711_1_.world;
            this.followSpeed = p_i225711_2_;
            this.minDist = p_i225711_4_;
            this.maxDist = p_i225711_5_;
            this.teleportToLeaves = p_i225711_6_;
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(p_i225711_1_.getNavigator() instanceof GroundPathNavigator) && !(p_i225711_1_.getNavigator() instanceof FlyingPathNavigator)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean shouldExecute() {
            LivingEntity lvt_1_1_ = this.tameable.getOwner();
            if (lvt_1_1_ == null) {
                return false;
            } else if (lvt_1_1_.isSpectator()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else if (this.tameable.getDistanceSq(lvt_1_1_) < (double) (this.minDist * this.minDist)) {
                return false;
            } else {
                this.owner = lvt_1_1_;
                return true;
            }
        }

        public boolean shouldContinueExecuting() {
            if (this.tameable.getNavigator().noPath()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else {
                return this.tameable.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist);
            }
        }

        public void startExecuting() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
            this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
        }

        public void resetTask() {
            this.owner = null;
            this.tameable.getNavigator().clearPath();
            this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.tameable.getLookController().setLookPositionWithEntity(this.owner, 10.0F, (float) this.tameable.getVerticalFaceSpeed());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.tameable.getLeashed() && !this.tameable.isPassenger()) {
                    if (this.tameable.getDistanceSq(this.owner) >= 144.0D) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.tameable.getNavigator().tryMoveToEntityLiving(this.owner, this.followSpeed);
                    }

                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos lvt_1_1_ = this.owner.getPosition();

            for (int lvt_2_1_ = 0; lvt_2_1_ < 10; ++lvt_2_1_) {
                int lvt_3_1_ = this.getRandomNumber(-3, 3);
                int lvt_4_1_ = this.getRandomNumber(-1, 1);
                int lvt_5_1_ = this.getRandomNumber(-3, 3);
                boolean lvt_6_1_ = this.tryToTeleportToLocation(lvt_1_1_.getX() + lvt_3_1_, lvt_1_1_.getY() + lvt_4_1_, lvt_1_1_.getZ() + lvt_5_1_);
                if (lvt_6_1_) {
                    return;
                }
            }

        }

        private boolean tryToTeleportToLocation(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
            if (Math.abs((double) p_226328_1_ - this.owner.getPosX()) < 2.0D && Math.abs((double) p_226328_3_ - this.owner.getPosZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
                return false;
            } else {
                this.tameable.setLocationAndAngles((double) p_226328_1_ + 0.5D, p_226328_2_, (double) p_226328_3_ + 0.5D, this.tameable.rotationYaw, this.tameable.rotationPitch);
                this.tameable.getNavigator().clearPath();
                return true;
            }
        }

        private boolean isTeleportFriendlyBlock(BlockPos p_226329_1_) {
            PathNodeType lvt_2_1_ = WalkNodeProcessor.func_237231_a_(this.world, p_226329_1_.toMutable());
            if (lvt_2_1_ != PathNodeType.WALKABLE) {
                return false;
            } else {
                BlockState lvt_3_1_ = this.world.getBlockState(p_226329_1_.down());
                if (!this.teleportToLeaves && lvt_3_1_.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos lvt_4_1_ = p_226329_1_.subtract(this.tameable.getPosition());
                    return this.world.hasNoCollisions(this.tameable, this.tameable.getBoundingBox().offset(lvt_4_1_));
                }
            }
        }

        private int getRandomNumber(int p_226327_1_, int p_226327_2_) {
            return this.tameable.getRNG().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
        }
    }

}
