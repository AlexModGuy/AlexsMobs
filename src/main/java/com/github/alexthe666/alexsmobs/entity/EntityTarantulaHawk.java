package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.message.MessageTarantulaHawkSting;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EntityTarantulaHawk extends TameableEntity implements IFollower {

    public static final int STING_DURATION = 12000;
    protected static final EntitySize FLIGHT_SIZE = EntitySize.fixed(0.9F, 1.5F);
    private static final DataParameter<Float> FLY_ANGLE = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DRAGGING = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DIGGING = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SCARED = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(EntityTarantulaHawk.class, DataSerializers.BOOLEAN);
    public float prevFlyAngle;
    public float prevSitProgress;
    public float sitProgress;
    public float prevDragProgress;
    public float dragProgress;
    public float prevFlyProgress;
    public float flyProgress;
    public float prevAttackProgress;
    public float attackProgress;
    public float prevDigProgress;
    public float digProgress;
    private boolean isLandNavigator;
    private boolean flightSize = false;
    private int timeFlying = 0;
    private boolean bredBuryFlag = false;
    private int spiderFeedings = 0;
    private int dragTime = 0;

    protected EntityTarantulaHawk(EntityType type, World worldIn) {
        super(type, worldIn);
        switchNavigator(false);
    }

    public static boolean canTarantulaHawkSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.SAND.contains(worldIn.getBlockState(pos.down()).getBlock());
        return (spawnBlock) && worldIn.getLightSubtracted(pos, 0) > 8 || AMConfig.fireproofTarantulaHawk;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 18.0D).createMutableAttribute(Attributes.ARMOR, 4.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 5);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.tarantulaHawkSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AIFleeRoadrunners());
        this.goalSelector.addGoal(4, new AIMelee());
        this.goalSelector.addGoal(5, new AIBury());
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, Ingredient.fromItems(Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE), false));
        this.goalSelector.addGoal(8, new AIWalkIdle());
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, SpiderEntity.class, 15, true, true, null) {
            public boolean shouldExecute() {
                return super.shouldExecute() && !EntityTarantulaHawk.this.isChild() && !EntityTarantulaHawk.this.isSitting();
            }
        });
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TARANTULA_HAWK_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TARANTULA_HAWK_HURT;
    }

    public boolean isImmuneToFire() {
        return AMConfig.fireproofTarantulaHawk;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigator(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new MoveController();
            this.navigator = new DirectPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLY_ANGLE, 0F);
        this.dataManager.register(FLYING, false);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(DRAGGING, false);
        this.dataManager.register(DIGGING, false);
        this.dataManager.register(SCARED, false);
        this.dataManager.register(ANGRY, false);
        this.dataManager.register(ATTACK_TICK, 0);
        this.dataManager.register(COMMAND, 0);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() instanceof LivingEntity && ((LivingEntity) source.getTrueSource()).getCreatureAttribute() == CreatureAttribute.ARTHROPOD && ((LivingEntity) source.getTrueSource()).isPotionActive(AMEffectRegistry.DEBILITATING_STING)) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("HawkSitting", this.isSitting());
        compound.putBoolean("Digging", this.isDigging());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("Command", this.getCommand());
        compound.putInt("SpiderFeedings", this.spiderFeedings);
        compound.putBoolean("BreedFlag", this.bredBuryFlag);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("HawkSitting"));
        this.setDigging(compound.getBoolean("Digging"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setCommand(compound.getInt("Command"));
        this.spiderFeedings = compound.getInt("SpiderFeedings");
        this.bredBuryFlag = compound.getBoolean("BreedFlag");
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

    public float getFlyAngle() {
        return this.dataManager.get(FLY_ANGLE);
    }

    public void setFlyAngle(float progress) {
        this.dataManager.set(FLY_ANGLE, progress);
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if (flying && isChild()) {
            return;
        }
        this.dataManager.set(FLYING, flying);
    }

    public boolean isScared() {
        return this.dataManager.get(SCARED).booleanValue();
    }

    public void setScared(boolean sit) {
        this.dataManager.set(SCARED, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isDragging() {
        return this.dataManager.get(DRAGGING).booleanValue();
    }

    public void setDragging(boolean sit) {
        this.dataManager.set(DRAGGING, Boolean.valueOf(sit));
    }

    public boolean isDigging() {
        return this.dataManager.get(DIGGING).booleanValue();
    }

    public void setDigging(boolean sit) {
        this.dataManager.set(DIGGING, Boolean.valueOf(sit));
    }

    public EntitySize getSize(Pose poseIn) {
        return isFlying() && !isChild() ? FLIGHT_SIZE : super.getSize(poseIn);
    }

    public void tick() {
        prevFlyAngle = this.getFlyAngle();
        super.tick();
        prevAttackProgress = attackProgress;
        prevFlyProgress = flyProgress;
        prevSitProgress = sitProgress;
        prevDragProgress = dragProgress;
        prevDigProgress = digProgress;
        if (this.isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!this.isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (this.isSitting() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!this.isSitting() && sitProgress > 0F) {
            sitProgress--;
        }
        if (this.isDragging() && dragProgress < 5F) {
            dragProgress++;
        }
        if (!this.isDragging() && dragProgress > 0F) {
            dragProgress--;
        }
        if (this.isDigging() && digProgress < 5F) {
            digProgress++;
        }
        if (!this.isDigging() && digProgress > 0F) {
            digProgress--;
        }
        if (flightSize && !isFlying()) {
            this.recalculateSize();
            flightSize = false;
        }
        if (!flightSize && isFlying()) {
            this.recalculateSize();
            flightSize = true;
        }
        float threshold = 0.015F;
        if (isFlying() && this.prevRotationYaw - this.rotationYaw > threshold) {
            this.setFlyAngle(this.getFlyAngle() + 5);
        } else if (isFlying() && this.prevRotationYaw - this.rotationYaw < -threshold) {
            this.setFlyAngle(this.getFlyAngle() - 5);
        } else if (this.getFlyAngle() > 0) {
            this.setFlyAngle(Math.max(this.getFlyAngle() - 4, 0));
        } else if (this.getFlyAngle() < 0) {
            this.setFlyAngle(Math.min(this.getFlyAngle() + 4, 0));
        }
        this.setFlyAngle(MathHelper.clamp(this.getFlyAngle(), -30, 30));
        if (!world.isRemote) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (isFlying()) {
                if(timeFlying % 25 == 0){
                    this.playSound(AMSoundRegistry.TARANTULA_HAWK_WING, this.getSoundVolume(), this.getSoundPitch());
                }
                timeFlying++;
                this.setNoGravity(true);
                if (this.isSitting() || this.isPassenger() || this.isInLove()) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
                this.setNoGravity(false);
            }
            if (this.getAttackTarget() != null && this.getAttackTarget() instanceof PlayerEntity && !this.isTamed()) {
                this.dataManager.set(ANGRY, true);
            } else {
                this.dataManager.set(ANGRY, false);
            }
        }
        if (this.dataManager.get(ATTACK_TICK) > 0) {
            this.dataManager.set(ATTACK_TICK, this.dataManager.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        if (isDigging() && world.getBlockState(this.getPositionUnderneath()).isSolid()) {
            BlockPos posit = this.getPositionUnderneath();
            BlockState understate = world.getBlockState(posit);
            for (int i = 0; i < 4 + rand.nextInt(2); i++) {
                double particleX = posit.getX() + rand.nextFloat();
                double particleY = posit.getY() + 1F;
                double particleZ = posit.getZ() + rand.nextFloat();
                double motX = this.rand.nextGaussian() * 0.02D;
                double motY = 0.1F + rand.nextFloat() * 0.2F;
                double motZ = this.rand.nextGaussian() * 0.02D;
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, understate), particleX, particleY, particleZ, motX, motY, motZ);
            }
        }
        if(this.ticksExisted > 0 && ticksExisted % 300 == 0 && this.getHealth() < this.getMaxHealth()){
            this.heal(1);
        }
        if(!world.isRemote && this.isDragging() && this.getPassengers().isEmpty() && !this.isDigging()){
            dragTime++;
            if(dragTime > 5000){
                dragTime = 0;
                for(Entity e : this.getPassengers()){
                    e.attackEntityFrom(DamageSource.causeMobDamage(this), 10);
                }
                this.removePassengers();
                this.setDragging(false);
            }
        }
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.getEntityInteractionResult(player, hand);
        if (!isTamed() && item == Items.SPIDER_EYE) {
            this.consumeItemFromStack(player, itemstack);
            this.playSound(SoundEvents.ENTITY_STRIDER_EAT, this.getSoundVolume(), this.getSoundPitch());
            spiderFeedings++;
            if (spiderFeedings >= 15 && getRNG().nextInt(6) == 0 || spiderFeedings > 25) {
                this.setTamedBy(player);
                this.world.setEntityState(this, (byte) 7);
            } else {
                this.world.setEntityState(this, (byte) 6);
            }
            return ActionResultType.SUCCESS;
        }
        if (isTamed() && item.isIn(ItemTags.FLOWERS)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.consumeItemFromStack(player, itemstack);
                this.playSound(SoundEvents.ENTITY_STRIDER_EAT, this.getSoundVolume(), this.getSoundPitch());
                this.heal(5);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;

        }
        if (type != ActionResultType.SUCCESS && isTamed() && isOwner(player)) {
            if (player.isSneaking()) {
                if (this.getHeldItemMainhand().isEmpty()) {
                    ItemStack cop = itemstack.copy();
                    cop.setCount(1);
                    this.setHeldItem(Hand.MAIN_HAND, cop);
                    itemstack.shrink(1);
                    return ActionResultType.SUCCESS;
                } else {
                    this.entityDropItem(this.getHeldItemMainhand().copy());
                    this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                    return ActionResultType.SUCCESS;
                }
            } else if (!isBreedingItem(itemstack)) {
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
        }
        return type;
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return isTamed() && item == Items.FERMENTED_SPIDER_EYE;
    }


    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.CACTUS || super.isInvulnerableTo(source);
    }

    public void func_234177_a_(ServerWorld world, AnimalEntity animalEntity) {
        bredBuryFlag = true;
        ServerPlayerEntity serverplayerentity = this.getLoveCause();
        if (serverplayerentity == null && animalEntity.getLoveCause() != null) {
            serverplayerentity = animalEntity.getLoveCause();
        }

        if (serverplayerentity != null) {
            serverplayerentity.addStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this, animalEntity, this);
        }

        this.setGrowingAge(6000);
        animalEntity.setGrowingAge(6000);
        this.resetInLove();
        animalEntity.resetInLove();
        world.setEntityState(this, (byte) 18);
        if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            world.addEntity(new ExperienceOrbEntity(world, this.getPosX(), this.getPosY(), this.getPosZ(), this.getRNG().nextInt(7) + 1));
        }

    }

    public void followEntity(TameableEntity tameable, LivingEntity owner, double followSpeed) {
        if (this.getDistance(owner) > 5) {
            this.setFlying(true);
            this.getMoveHelper().setMoveTo(owner.getPosX(), owner.getPosY() + owner.getHeight(), owner.getPosZ(), followSpeed);
        } else {
            if (this.onGround) {
                this.setFlying(false);
            }
            if (this.isFlying() && !this.isOverWater()) {
                BlockPos vec = this.getCrowGround(this.getPosition());
                if (vec != null) {
                    this.getMoveHelper().setMoveTo(vec.getX(), vec.getY(), vec.getZ(), followSpeed);
                }
            } else {
                this.getNavigator().tryMoveToEntityLiving(owner, followSpeed);
            }
        }
    }

    public boolean shouldBury() {
        return bredBuryFlag || !this.isTamed();
    }

    public void updatePassenger(Entity passenger) {
        this.rotationPitch = 0;
        float radius = 1.0F + passenger.getWidth() * 0.5F;
        float angle = (0.01745329251F * (this.renderYawOffset - 180));
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        double extraY = 0;
        passenger.setPosition(this.getPosX() + extraX, this.getPosY() + extraY, this.getPosZ() + extraZ);
    }

    private boolean isOverWater() {
        BlockPos position = this.getPosition();
        while (position.getY() > 0 && world.isAirBlock(position)) {
            position = position.down();
        }
        return !world.getFluidState(position).isEmpty() || position.getY() <= 0;
    }

    public Vector3d getBlockInViewAway(Vector3d fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24) - radiusAdd;
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = getCrowGround(radialPos);
        int distFromGround = (int) this.getPosY() - ground.getY();
        int flightHeight = 4 + this.getRNG().nextInt(10);
        BlockPos newPos = ground.up(distFromGround > 8 ? flightHeight : this.getRNG().nextInt(6) + 1);
        if (!this.isTargetBlocked(Vector3d.copyCentered(newPos)) && this.getDistanceSq(Vector3d.copyCentered(newPos)) > 1) {
            return Vector3d.copyCentered(newPos);
        }
        return null;
    }

    private BlockPos getCrowGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getPosY(), in.getZ());
        while (position.getY() > 2 && world.isAirBlock(position)) {
            position = position.down();
        }
        return position;
    }

    public Vector3d getBlockGrounding(Vector3d fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24);
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

    private Vector3d getOrbitVec(Vector3d vector3d, float gatheringCircleDist, boolean orbitClockwise) {
        float angle = (0.01745329251F * (float) 2 * (orbitClockwise ? -ticksExisted : ticksExisted));
        double extraX = gatheringCircleDist * MathHelper.sin((angle));
        double extraZ = gatheringCircleDist * MathHelper.cos(angle);
        if (vector3d != null) {
            Vector3d pos = new Vector3d(vector3d.getX() + extraX, vector3d.getY() + rand.nextInt(2) + 4, vector3d.getZ() + extraZ);
            if (this.world.isAirBlock(new BlockPos(pos))) {
                return pos;
            }
        }
        return null;
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    @Override
    public boolean shouldFollow() {
        return getCommand() == 1 && !this.isDragging() && !this.isDigging() && (this.getAttackTarget() == null || !this.getAttackTarget().isAlive());
    }

    public boolean isAngry() {
        return dataManager.get(ANGRY);
    }

    class MoveController extends MovementController {
        private final MobEntity parentEntity;


        public MoveController() {
            super(EntityTarantulaHawk.this);
            this.parentEntity = EntityTarantulaHawk.this;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getAverageEdgeLength();
                if (d0 < width) {
                    this.action = MovementController.Action.WAIT;
                    parentEntity.setMotion(parentEntity.getMotion().scale(0.5D));
                } else {
                    float angle = (0.01745329251F * (parentEntity.renderYawOffset + 90));
                    float radius = (float) Math.sin(parentEntity.ticksExisted * 0.2F) * 2;
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    Vector3d vector3d1 = vector3d.scale(this.speed * 0.05D / d0);
                    Vector3d strafPlus = new Vector3d(extraX, 0, extraZ).scale(0.003D * Math.min(d0, 100));
                    parentEntity.setMotion(parentEntity.getMotion().add(strafPlus));
                    parentEntity.setMotion(parentEntity.getMotion().add(vector3d1));
                    parentEntity.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    if (!EntityTarantulaHawk.this.isDragging()) {
                        parentEntity.renderYawOffset = parentEntity.rotationYaw;
                    }
                }

            }
        }
    }

    private class AIMelee extends Goal {
        private EntityTarantulaHawk hawk;
        private int orbitCooldown = 0;
        private boolean clockwise = false;
        private Vector3d orbitVec = null;

        public AIMelee() {
            hawk = EntityTarantulaHawk.this;
        }

        @Override
        public boolean shouldExecute() {
            return hawk.getAttackTarget() != null && !hawk.isScared() && hawk.getAttackTarget().isAlive() && !hawk.isDragging() && !hawk.isDigging() && !hawk.getAttackTarget().noClip && !hawk.getAttackTarget().isPassenger();
        }

        @Override
        public void startExecuting() {
            hawk.setDragging(false);
            clockwise = rand.nextBoolean();
        }

        @Override
        public void tick() {
            LivingEntity target = hawk.getAttackTarget();
            boolean paralized = target != null && target.getCreatureAttribute() == CreatureAttribute.ARTHROPOD && !target.noClip && target.isPotionActive(AMEffectRegistry.DEBILITATING_STING);

            if (orbitCooldown > 0) {
                orbitCooldown--;
                hawk.setFlying(true);
                if (target != null) {
                    if (orbitVec == null || hawk.getDistanceSq(orbitVec) < 4F || !hawk.getMoveHelper().isUpdating()) {
                        orbitVec = hawk.getOrbitVec(target.getPositionVec().add(0, target.getHeight(), 0), 10 + rand.nextInt(2), false);
                        if (orbitVec != null) {
                            hawk.getMoveHelper().setMoveTo(orbitVec.x, orbitVec.y, orbitVec.z, 1F);
                        }
                    }
                }
            } else if (paralized && hawk.shouldBury()) {
                if (hawk.isOnGround()) {
                    hawk.setFlying(false);
                    hawk.getNavigator().tryMoveToEntityLiving(target, 1);
                } else {
                    Vector3d vector3d = hawk.getBlockGrounding(hawk.getPositionVec());
                    if (vector3d != null && hawk.isFlying()) {
                        hawk.getMoveHelper().setMoveTo(vector3d.x, vector3d.y, vector3d.z, 1F);
                    }
                }
                if (hawk.getDistance(target) < target.getWidth() + 1.5F && !target.isPassenger()) {
                    hawk.setDragging(true);
                    hawk.setFlying(false);
                    target.startRiding(hawk, true);
                }
            } else {
                if (target != null) {
                    double dist = hawk.getDistance(target);
                    if (dist < 10 && !hawk.isFlying()) {
                        if (hawk.isOnGround()) {
                            hawk.setFlying(false);
                        }
                        hawk.getNavigator().tryMoveToEntityLiving(target, 1);
                    } else {
                        hawk.setFlying(true);
                        hawk.getMoveHelper().setMoveTo(target.getPosX(), target.getPosYEye(), target.getPosZ(), 1F);
                    }
                    if (dist < target.getWidth() + 2.5F) {
                        if (hawk.dataManager.get(ATTACK_TICK) == 0 && hawk.attackProgress == 0) {
                            hawk.dataManager.set(ATTACK_TICK, 7);
                        }
                        if (hawk.attackProgress == 5F) {
                            hawk.attackEntityAsMob(target);
                            target.addPotionEffect(new EffectInstance(AMEffectRegistry.DEBILITATING_STING, target.getCreatureAttribute() == CreatureAttribute.ARTHROPOD ? EntityTarantulaHawk.STING_DURATION : 600, hawk.bredBuryFlag ? 1 : 0));
                            if (!hawk.world.isRemote && target.getCreatureAttribute() == CreatureAttribute.ARTHROPOD) {
                                AlexsMobs.sendMSGToAll(new MessageTarantulaHawkSting(hawk.getEntityId(), target.getEntityId()));
                            }
                            orbitCooldown = target.getCreatureAttribute() == CreatureAttribute.ARTHROPOD ? 200 + rand.nextInt(200) : 10 + rand.nextInt(20);
                        }
                    }
                }
            }


        }

        @Override
        public void resetTask() {
            orbitCooldown = 0;
            hawk.bredBuryFlag = false;
            clockwise = rand.nextBoolean();
            orbitVec = null;
            if(hawk.getPassengers().isEmpty()){
                hawk.setAttackTarget(null);
            }
        }
    }

    private class AIWalkIdle extends Goal {
        protected final EntityTarantulaHawk hawk;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWalkIdle() {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
            this.hawk = EntityTarantulaHawk.this;
        }

        @Override
        public boolean shouldExecute() {
            if (this.hawk.isBeingRidden() || hawk.isScared() || hawk.isDragging() || EntityTarantulaHawk.this.getCommand() == 1 || (hawk.getAttackTarget() != null && hawk.getAttackTarget().isAlive()) || this.hawk.isPassenger() || this.hawk.isSitting()) {
                return false;
            } else {
                if (this.hawk.getRNG().nextInt(30) != 0 && !hawk.isFlying()) {
                    return false;
                }
                if (this.hawk.isOnGround()) {
                    this.flightTarget = rand.nextBoolean();
                } else {
                    this.flightTarget = rand.nextInt(5) > 0 && hawk.timeFlying < 200;
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
            if (flightTarget) {
                hawk.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                this.hawk.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && hawk.onGround) {
                hawk.setFlying(false);
            }
            if (isFlying() && hawk.onGround && hawk.timeFlying > 10) {
                hawk.setFlying(false);
            }
        }

        @Nullable
        protected Vector3d getPosition() {
            Vector3d vector3d = hawk.getPositionVec();
            if (hawk.isOverWater()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (hawk.timeFlying < 50 || hawk.isOverWater()) {
                    return hawk.getBlockInViewAway(vector3d, 0);
                } else {
                    return hawk.getBlockGrounding(vector3d);
                }
            } else {

                return RandomPositionGenerator.findRandomTarget(this.hawk, 10, 7);
            }
        }

        public boolean shouldContinueExecuting() {
            if (hawk.isSitting() || EntityTarantulaHawk.this.getCommand() == 1) {
                return false;
            }
            if (flightTarget) {
                return hawk.isFlying() && hawk.getDistanceSq(x, y, z) > 2F;
            } else {
                return (!this.hawk.getNavigator().noPath()) && !this.hawk.isBeingRidden();
            }
        }

        public void startExecuting() {
            if (flightTarget) {
                hawk.setFlying(true);
                hawk.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                this.hawk.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
        }

        public void resetTask() {
            this.hawk.getNavigator().clearPath();
            super.resetTask();
        }
    }

    private class AIBury extends Goal {
        private EntityTarantulaHawk hawk;
        private BlockPos buryPos = null;
        private int digTime = 0;
        private double stageX;
        private double stageY;
        private double stageZ;

        private AIBury() {
            hawk = EntityTarantulaHawk.this;
        }

        @Override
        public boolean shouldExecute() {
            if (hawk.isDragging() && hawk.getAttackTarget() != null) {
                BlockPos pos = genSandPos(hawk.getPosition());
                if (pos != null) {
                    buryPos = pos;
                    return true;
                }
            }
            return false;
        }

        public boolean shouldContinueExecuting() {
            return hawk.isDragging() && digTime < 200 && hawk.getAttackTarget() != null && buryPos != null && BlockTags.SAND.contains(world.getBlockState(buryPos).getBlock());
        }

        public void startExecuting() {
            digTime = 0;
            stageX = hawk.getPosX();
            stageY = hawk.getPosY();
            stageZ = hawk.getPosZ();
        }

        public void resetTask() {
            digTime = 0;
            hawk.setDigging(false);
            hawk.setDragging(false);
            hawk.setAttackTarget(null);
            hawk.setRevengeTarget(null);
        }

        public void tick() {
            hawk.setFlying(false);
            hawk.setDragging(true);
            LivingEntity target = hawk.getAttackTarget();
            if (hawk.getDistanceSq(Vector3d.copyCentered(buryPos)) < 9) {
                if (!hawk.isDigging()) {
                    hawk.setDigging(true);
                    stageX = target.getPosX();
                    stageY = target.getPosY();
                    stageZ = target.getPosZ();
                }
            }
            if (hawk.isDigging()) {
                target.noClip = true;
                digTime++;
                hawk.removePassengers();
                target.setPosition(stageX, stageY - Math.min(3, digTime * 0.05F), stageZ);
                hawk.getNavigator().tryMoveToXYZ(stageX, stageY, stageZ, 0.85F);
            } else {
                hawk.getNavigator().tryMoveToXYZ(buryPos.getX(), buryPos.getY(), buryPos.getZ(), 0.5F);
            }
        }


        private BlockPos genSandPos(BlockPos parent) {
            IWorld world = hawk.world;
            Random random = new Random();
            int range = 24;
            for (int i = 0; i < 15; i++) {
                BlockPos sandAir = parent.add(random.nextInt(range) - range / 2, -5, random.nextInt(range) - range / 2);
                while (!world.isAirBlock(sandAir) && sandAir.getY() < 255) {
                    sandAir = sandAir.up();
                }
                BlockState state = world.getBlockState(sandAir.down());
                if (BlockTags.SAND.contains(state.getBlock())) {
                    return sandAir.down();
                }
            }
            return null;
        }
    }

    private class AIFleeRoadrunners extends Goal {
        private int searchCooldown = 0;
        private LivingEntity fear = null;
        private Vector3d fearVec = null;

        @Override
        public boolean shouldExecute() {
            if (searchCooldown <= 0) {
                searchCooldown = 100 + EntityTarantulaHawk.this.rand.nextInt(100);
                List<EntityRoadrunner> list = EntityTarantulaHawk.this.world.getEntitiesWithinAABB(EntityRoadrunner.class, EntityTarantulaHawk.this.getBoundingBox().grow(15, 32, 15));
                for (EntityRoadrunner roadrunner : list) {
                    if (fear == null || EntityTarantulaHawk.this.getDistance(fear) > EntityTarantulaHawk.this.getDistance(roadrunner)) {
                        fear = roadrunner;
                    }
                }
            } else {
                searchCooldown--;
            }
            return EntityTarantulaHawk.this.isAlive() && fear != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return fear != null && fear.isAlive() && EntityTarantulaHawk.this.getDistance(fear) < 32F;
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            EntityTarantulaHawk.this.setScared(true);
        }

        public void tick() {
            if (fear != null) {
                if (fearVec == null || EntityTarantulaHawk.this.getDistanceSq(fearVec) < 4) {
                    fearVec = EntityTarantulaHawk.this.getBlockInViewAway(fearVec == null ? fear.getPositionVec() : fearVec, 12);
                }
                if (fearVec != null) {
                    EntityTarantulaHawk.this.setFlying(true);
                    EntityTarantulaHawk.this.getMoveHelper().setMoveTo(fearVec.x, fearVec.y, fearVec.z, 1.1F);
                }
            }
        }

        public void resetTask() {
            EntityTarantulaHawk.this.setScared(false);
            fear = null;
            fearVec = null;
        }
    }
}
