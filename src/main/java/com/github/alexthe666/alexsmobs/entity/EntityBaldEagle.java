package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.core.jmx.Server;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EntityBaldEagle extends TameableEntity implements IFollower {

    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityBaldEagle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TACKLING = EntityDataManager.createKey(EntityBaldEagle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_CAP = EntityDataManager.createKey(EntityBaldEagle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.createKey(EntityBaldEagle.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityBaldEagle.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityBaldEagle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LAUNCHED = EntityDataManager.createKey(EntityBaldEagle.class, DataSerializers.BOOLEAN);
    private static final Ingredient TEMPT_ITEMS = Ingredient.fromItems(Items.ROTTEN_FLESH, AMItemRegistry.FISH_OIL);
    public float prevAttackProgress;
    public float attackProgress;
    public float prevFlyProgress;
    public float flyProgress;
    public float prevTackleProgress;
    public float tackleProgress;
    public float prevSwoopProgress;
    public float swoopProgress;
    public float prevFlapAmount;
    public float flapAmount;
    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    public float prevSitProgress;
    public float sitProgress;
    private boolean isLandNavigator;
    private int timeFlying;
    private BlockPos orbitPos = null;
    private double orbitDist = 5D;
    private boolean orbitClockwise = false;
    private int passengerTimer = 0;
    private int launchTime = 0;
    private int lastPlayerControlTime = 0;
    private int returnControlTime = 0;
    private int tackleCapCooldown = 0;
    private boolean controlledFlag = false;
    private int chunkLoadCooldown;

    protected EntityBaldEagle(EntityType<? extends TameableEntity> type, World worldIn) {
        super(type, worldIn);
        switchNavigator(true);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public static boolean canEagleSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        return worldIn.getLightSubtracted(pos, 0) > 8;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this) {
            public boolean shouldExecute() {
                return super.shouldExecute() && (EntityBaldEagle.this.getAir() < 30 || EntityBaldEagle.this.getAttackTarget() == null || !EntityBaldEagle.this.getAttackTarget().isInWaterOrBubbleColumn() && EntityBaldEagle.this.getPosY() > EntityBaldEagle.this.getAttackTarget().getPosY());
            }
        });
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0D, 25.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AITackle());
        this.goalSelector.addGoal(4, new AILandOnGlove());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, TEMPT_ITEMS, false));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, Ingredient.fromTag(ItemTags.FISHES), false));
        this.goalSelector.addGoal(8, new AIWanderIdle());
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, SalmonEntity.class, 5, true, true, null) {
            public boolean shouldExecute() {
                return super.shouldExecute() && !EntityBaldEagle.this.isLaunched() && EntityBaldEagle.this.getCommand() == 0;
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, RabbitEntity.class, 5, true, true, null) {
            public boolean shouldExecute() {
                return super.shouldExecute() && !EntityBaldEagle.this.isLaunched() && EntityBaldEagle.this.getCommand() == 0;
            }
        });
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.BALD_EAGLE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BALD_EAGLE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BALD_EAGLE_HURT;
    }


    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.baldEagleSpawnRolls, this.getRNG(), spawnReasonIn);
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

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new EntityBaldEagle.MoveHelper(this);
            this.navigator = new DirectPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    public boolean writeUnlessPassenger(CompoundNBT compound) {
        String s = this.getEntityString();
        compound.putString("id", s);
        super.writeUnlessPassenger(compound);
        return true;
    }

    public boolean writeUnlessRemoved(CompoundNBT compound) {
        if (!this.isTamed()) {
            return super.writeUnlessRemoved(compound);
        } else {
            String s = this.getEntityString();
            compound.putString("id", s);
            this.writeWithoutTypeId(compound);
            return true;
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("BirdSitting", this.isSitting());
        compound.putBoolean("Launched", this.isLaunched());
        compound.putBoolean("HasCap", this.hasCap());
        compound.putInt("EagleCommand", this.getCommand());
        compound.putInt("LaunchTime", this.launchTime);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("BirdSitting"));
        this.setLaunched(compound.getBoolean("Launched"));
        this.setCap(compound.getBoolean("HasCap"));
        this.setCommand(compound.getInt("EagleCommand"));
        this.launchTime = compound.getInt("LaunchTime");
    }

    public void travel(Vector3d vec3d) {
        if (!this.shouldHoodedReturn() && this.hasCap() && this.isTamed() && !this.isPassenger()) {
            super.travel(Vector3d.ZERO);
            return;
        }
        super.travel(vec3d);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.dataManager.get(ATTACK_TICK) == 0 && this.attackProgress == 0 && entityIn.isAlive() && this.getDistance(entityIn) < entityIn.getWidth() + 5) {
            this.dataManager.set(ATTACK_TICK, 5);
        }
        return true;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, false);
        this.dataManager.register(HAS_CAP, false);
        this.dataManager.register(TACKLING, false);
        this.dataManager.register(LAUNCHED, false);
        this.dataManager.register(ATTACK_TICK, 0);
        this.dataManager.register(COMMAND, Integer.valueOf(0));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isLaunched() {
        return this.dataManager.get(LAUNCHED);
    }

    public void setLaunched(boolean flying) {
        this.dataManager.set(LAUNCHED, flying);
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if(flying && this.isChild()){
            flying = false;
        }
        this.dataManager.set(FLYING, flying);
    }

    public boolean hasCap() {
        return this.dataManager.get(HAS_CAP);
    }

    public void setCap(boolean cap) {
        this.dataManager.set(HAS_CAP, cap);
    }

    public boolean isTackling() {
        return this.dataManager.get(TACKLING);
    }

    public void setTackling(boolean tackling) {
        this.dataManager.set(TACKLING, tackling);
    }

    public void followEntity(TameableEntity tameable, LivingEntity owner, double followSpeed) {
        if (this.getDistance(owner) > 15) {
            this.setFlying(true);
            this.getMoveHelper().setMoveTo(owner.getPosX(), owner.getPosY() + owner.getHeight(), owner.getPosZ(), followSpeed);
        } else {
            if (this.isFlying() && !this.isOverWaterOrVoid()) {
                BlockPos vec = this.getCrowGround(this.getPosition());
                if (vec != null) {
                    this.getMoveHelper().setMoveTo(vec.getX(), vec.getY(), vec.getZ(), followSpeed);
                }
                if (this.onGround) {
                    this.setFlying(false);
                }
            } else {
                this.getNavigator().tryMoveToEntityLiving(owner, followSpeed);
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if (item.isIn(ItemTags.FISHES) && this.getHealth() < this.getMaxHealth()) {
            this.heal(10);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.world.setEntityState(this, (byte) 7);
            return ActionResultType.CONSUME;
        } else if (item == AMItemRegistry.FISH_OIL && !this.isTamed()) {
            if (itemstack.hasContainerItem()) {
                this.entityDropItem(itemstack.getContainerItem());
            }
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            if (rand.nextBoolean()) {
                this.world.setEntityState(this, (byte) 7);
                this.setTamedBy(player);
                this.setCommand(1);
            } else {
                this.world.setEntityState(this, (byte) 6);
            }
            return ActionResultType.CONSUME;
        } else if (isTamed() && !isBreedingItem(itemstack)) {
            if (!this.isChild() && item == AMItemRegistry.FALCONRY_HOOD) {
                if (!this.hasCap()) {
                    this.setCap(true);
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, this.getSoundVolume(), this.getSoundPitch());
                    return ActionResultType.SUCCESS;
                }
            }else if(item == Items.SHEARS && this.hasCap()) {
                this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                if(!world.isRemote){
                    if(player instanceof ServerPlayerEntity){
                        itemstack.attemptDamageItem(1, rand, (ServerPlayerEntity)player);
                    }
                }
                this.entityDropItem(AMItemRegistry.FALCONRY_HOOD);
                this.setCap(false);
                return ActionResultType.SUCCESS;
            }else if (!this.isChild() && getRidingEagles(player) <= 0 && (player.getHeldItem(Hand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE || player.getHeldItem(Hand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE)) {
                rideCooldown = 30;
                this.setLaunched(false);
                this.removePassengers();
                this.startRiding(player, true);
                if (!world.isRemote) {
                    AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(this.getEntityId(), player.getEntityId()));
                }
                return ActionResultType.SUCCESS;
            } else {
                this.setCommand((this.getCommand() + 1) % 3);
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

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1 && !isLaunched();
    }

    public int getRidingEagles(LivingEntity player) {
        int crowCount = 0;
        for (Entity e : player.getPassengers()) {
            if (e instanceof EntityBaldEagle) {
                crowCount++;
            }
        }
        return crowCount;
    }

    public void updateRidden() {
        Entity entity = this.getRidingEntity();
        if (this.isPassenger() && (!entity.isAlive() || !this.isAlive())) {
            this.stopRiding();
        } else if (isTamed() && entity instanceof LivingEntity && isOwner((LivingEntity) entity)) {
            this.setMotion(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                Entity mount = this.getRidingEntity();
                if (mount instanceof PlayerEntity) {
                    float yawAdd = 0;
                    if (((PlayerEntity) mount).getHeldItem(Hand.MAIN_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                        yawAdd = ((PlayerEntity) mount).getPrimaryHand() == HandSide.LEFT ? 135 : -135;
                    } else if (((PlayerEntity) mount).getHeldItem(Hand.OFF_HAND).getItem() == AMItemRegistry.FALCONRY_GLOVE) {
                        yawAdd = ((PlayerEntity) mount).getPrimaryHand() == HandSide.LEFT ? -135 : 135;
                    } else {
                        this.setCommand(2);
                        this.setSitting(true);
                        this.dismount();
                        this.copyLocationAndAnglesFrom(mount);
                    }
                    float birdYaw = yawAdd * 0.5F;
                    this.renderYawOffset = MathHelper.wrapDegrees(((LivingEntity) mount).renderYawOffset + birdYaw);
                    this.rotationYaw = MathHelper.wrapDegrees(((LivingEntity) mount).rotationYaw + birdYaw);
                    this.rotationYawHead = MathHelper.wrapDegrees(((LivingEntity) mount).rotationYawHead + birdYaw);
                    float radius = 0.6F;
                    float angle = (0.01745329251F * (((LivingEntity) mount).renderYawOffset - 180F + yawAdd));
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    this.setPosition(mount.getPosX() + extraX, Math.max(mount.getPosY() + mount.getHeight() * 0.45F, mount.getPosY()), mount.getPosZ() + extraZ);
                }
                if (!mount.isAlive()) {
                    this.dismount();
                }
            }
        } else {
            super.updateRidden();
        }
    }

    public void tick() {
        super.tick();

        this.prevAttackProgress = attackProgress;
        this.prevBirdPitch = birdPitch;
        this.prevTackleProgress = tackleProgress;
        this.prevFlyProgress = flyProgress;
        this.prevFlapAmount = flapAmount;
        this.prevSwoopProgress = swoopProgress;
        this.prevSitProgress = sitProgress;
        float yMot = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
        this.birdPitch = yMot;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (isTackling() && tackleProgress < 5F) {
            tackleProgress++;
        }
        if (!isTackling() && tackleProgress > 0F) {
            tackleProgress--;
        }
        boolean sit = isSitting() || this.isPassenger();
        if (sit && sitProgress < 5F) {
            sitProgress++;
        }
        if (!sit && sitProgress > 0F) {
            sitProgress--;
        }
        if (this.isLaunched()) {
            launchTime++;
        } else {
            launchTime = 0;
        }
        if (lastPlayerControlTime > 0) {
            lastPlayerControlTime--;
        }
        if (lastPlayerControlTime <= 0) {
            controlledFlag = false;
        }
        if (yMot < 0.1F) {
            flapAmount = Math.min(-yMot * 0.2F, 1F);
            if (swoopProgress > 0) {
                swoopProgress--;
            }
        } else {
            if (flapAmount > 0.0F) {
                flapAmount -= Math.min(flapAmount, 0.1F);
            } else {
                flapAmount = 0;
            }
            if (swoopProgress < yMot * 0.2F) {
                swoopProgress = Math.min(yMot * 0.2F, swoopProgress + 1);
            }
        }
        if (this.isTackling()) {
            flapAmount = Math.min(2, flapAmount + 0.2F);
        }
        if (!world.isRemote) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (this.isTackling() && !this.isBeingRidden() && (this.getAttackTarget() == null || !this.getAttackTarget().isAlive()) && tackleCapCooldown == 0) {
                this.setTackling(false);
            }
            if (isFlying()) {
                timeFlying++;
                this.setNoGravity(true);
                if (this.isSitting() || this.isPassenger() || this.isInLove()) {
                    if(!isLaunched()){
                        this.setFlying(false);
                    }
                }
                if (this.getAttackTarget() != null && this.getAttackTarget().getPosY() < this.getPosX() && !this.isBeingRidden()) {
                    this.setMotion(this.getMotion().mul(1.0, 0.9, 1.0));
                }
            } else {
                timeFlying = 0;
                this.setNoGravity(false);
            }
            if (this.isInWaterOrBubbleColumn() && this.isBeingRidden()) {
                this.setMotion(this.getMotion().add(0, 0.1F, 0));
            }
            if(this.isSitting() && !this.isLaunched()){
                this.setMotion(this.getMotion().add(0, -0.1F, 0));
            }
            if (this.getAttackTarget() != null && this.isInWaterOrBubbleColumn()) {
                timeFlying = 0;
                this.setFlying(true);
            }
            if (isFlying() && this.onGround && !this.isInWaterOrBubbleColumn() && this.timeFlying > 30) {
                this.setFlying(false);
            }
        }
        if (this.dataManager.get(ATTACK_TICK) > 0) {
            if (this.dataManager.get(ATTACK_TICK) == 2 && this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < this.getAttackTarget().getWidth() + 2D) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), 2);
            }
            this.dataManager.set(ATTACK_TICK, this.dataManager.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        if (this.isPassenger()) {
            this.setFlying(false);
            this.setTackling(false);
        }
        if (rideCooldown > 0) {
            rideCooldown--;
        }
        if (returnControlTime > 0) {
            returnControlTime--;
        }
        if (tackleCapCooldown > 0) {
            tackleCapCooldown--;
        }
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.BALD_EAGLE.create(p_241840_1_);
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
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
        int flightHeight = 7 + this.getRNG().nextInt(10);
        BlockPos newPos = ground.up(distFromGround > 8 ? flightHeight : this.getRNG().nextInt(7) + 4);
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

    private Vector3d getOrbitVec(Vector3d vector3d, float gatheringCircleDist) {
        float angle = (0.01745329251F * (float) this.orbitDist * (orbitClockwise ? -ticksExisted : ticksExisted));
        double extraX = gatheringCircleDist * MathHelper.sin((angle));
        double extraZ = gatheringCircleDist * MathHelper.cos(angle);
        if (this.orbitPos != null) {
            Vector3d pos = new Vector3d(orbitPos.getX() + extraX, orbitPos.getY() + rand.nextInt(2) - 2, orbitPos.getZ() + extraZ);
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

    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float radius = 0.3F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.rotationYaw = this.renderYawOffset + 90F;
            if (passenger instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) passenger;
                living.renderYawOffset = this.renderYawOffset + 90F;
            }
            double extraY = 0;
            if (passenger instanceof AbstractFishEntity && !passenger.isInWaterOrBubbleColumn()) {
                extraY = 0.1F;
            }
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() - 0.3F + extraY + passenger.getHeight() * 0.3F, this.getPosZ() + extraZ);
            passengerTimer++;
            if (this.isAlive() && passengerTimer > 0 && passengerTimer % 40 == 0) {
                passenger.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
            }
        }
    }

    public boolean canBeRiddenInWater(Entity rider) {
        return true;
    }

    public Vector3d func_230268_c_(LivingEntity livingEntity) {
        return new Vector3d(this.getPosX(), this.getBoundingBox().minY, this.getPosZ());
    }

    public boolean shouldHoodedReturn() {
        if (this.getOwner() != null) {
            if (!this.getOwner().isAlive() || this.getOwner().isSneaking()) {
                return true;
            }
        }
        return !this.isAlive() || launchTime > 12000;
    }

    public void remove(boolean keepData) {
        if (this.lastPlayerControlTime == 0 && !this.isPassenger()) {
            super.remove(keepData);
        }
    }

    public void directFromPlayer(float rotationYaw, float rotationPitch, boolean loadChunk, Entity over) {
        Entity owner = this.getOwner();
        if (owner != null && this.getDistance(owner) > 150) {
            returnControlTime = 100;
        }
        this.setSitting(false);
        if (returnControlTime > 0 && owner != null) {
            double d0 = this.getPosX() - owner.getPosX();
            double d2 = this.getPosZ() - owner.getPosZ();
            float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) + 90.0F;
            this.renderYawOffset = f;
            this.rotationYaw = f;
            this.rotationYawHead = f;
            this.rotationPitch = rotationPitch;
        } else {
            this.renderYawOffset = rotationYaw;
            this.rotationYaw = rotationYaw;
            this.rotationYawHead = rotationYaw;
            this.rotationPitch = rotationPitch;
        }
        this.setLaunched(true);
        if (!world.isRemote) {
            if (rotationPitch < 10 && this.isOnGround()) {
                this.setFlying(true);
            }
            float yawOffset = rotationYaw + 90;
            float rad = 3F;
            float speed = 1.2F;
            if (returnControlTime > 0) {
                this.getMoveHelper().setMoveTo(owner.getPosX(), owner.getPosY() + 10, owner.getPosZ(), speed);
            } else {
                this.getMoveHelper().setMoveTo(this.getPosX() + rad * 1.5F * Math.cos(yawOffset * (Math.PI / 180.0F)), this.getPosY() - rad * Math.sin(rotationPitch * (Math.PI / 180.0F)), this.getPosZ() + rad * Math.sin(yawOffset * (Math.PI / 180.0F)), speed);
            }
            if (loadChunk) {
                loadChunkOnServer(this.getPosition());
            }
            this.setRevengeTarget(null);
            this.setAttackTarget(null);
            if (over == null) {
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(3.0D), EntityPredicates.CAN_AI_TARGET);
                Entity closest = null;
                for (Entity e : list) {
                    if (closest == null || this.getDistance(e) < this.getDistance(closest)) {
                        closest = e;
                    }
                }
                over = closest;
            }
        }
        if (over != null && !this.isOnSameTeam(over) && over != owner) {
            if (tackleCapCooldown == 0 && this.getDistance(over) <= over.getWidth() + 4D) {
                this.setTackling(true);
                if (this.getDistance(over) <= over.getWidth() + 2D) {
                    float speedDamage = (float) Math.ceil(MathHelper.clamp(this.getMotion().length() + 0.2, 0, 1.2D) * 3.333);
                    over.attackEntityFrom(DamageSource.causeMobDamage(this), 5 + speedDamage + rand.nextInt(2));
                    tackleCapCooldown = 22;
                }
            }
        }
        this.lastPlayerControlTime = 10;
        this.controlledFlag = true;
    }

    //killEntity
    public void func_241847_a(ServerWorld world, LivingEntity entity) {
        if (this.isLaunched() && this.hasCap() && this.isTamed() && this.getOwner() != null) {
            if (this.getOwner() instanceof ServerPlayerEntity && this.getDistance(this.getOwner()) >= 100) {
                AMAdvancementTriggerRegistry.BALD_EAGLE_CHALLENGE.trigger((ServerPlayerEntity) this.getOwner());
            }
        }
        super.func_241847_a(world, entity);
    }


    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            if (entity != null && this.isTamed() && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity) && this.isLaunched()) {
                amount = (amount + 1.0F) / 4.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    public void loadChunkOnServer(BlockPos center) {
        if (!this.world.isRemote) {
            ServerWorld serverWorld = (ServerWorld) world;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    ChunkPos pos = new ChunkPos(this.getPosition().add(i * 16, 0, j * 16));
                    serverWorld.forceChunk(pos.x, pos.z, true);

                }
            }
        }
    }

    class MoveHelper extends MovementController {
        private final EntityBaldEagle parentEntity;

        public MoveHelper(EntityBaldEagle bird) {
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
                    parentEntity.setMotion(parentEntity.getMotion().add(vector3d.scale(this.speed * 0.05D / d5)));
                    Vector3d vector3d1 = parentEntity.getMotion();
                    parentEntity.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.renderYawOffset = parentEntity.rotationYaw;

                }

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

    private class AIWanderIdle extends Goal {
        protected final EntityBaldEagle eagle;
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
            this.eagle = EntityBaldEagle.this;
        }

        @Override
        public boolean shouldExecute() {
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if ((eagle.getAttackTarget() != null && eagle.getAttackTarget().isAlive() && !this.eagle.isBeingRidden()) || this.eagle.isPassenger() || this.eagle.isSitting() || eagle.controlledFlag) {
                return false;
            } else {
                if (this.eagle.getRNG().nextInt(15) != 0 && !eagle.isFlying()) {
                    return false;
                }
                if(this.eagle.isChild()){
                    this.flightTarget = false;
                }else if (this.eagle.isInWaterOrBubbleColumn()) {
                    this.flightTarget = true;
                } else if (this.eagle.hasCap()) {
                    this.flightTarget = false;
                } else if (this.eagle.isOnGround()) {
                    this.flightTarget = rand.nextBoolean();
                } else {
                    if (orbitResetCooldown == 0 && rand.nextInt(6) == 0) {
                        orbitResetCooldown = 400;
                        eagle.orbitPos = eagle.getPosition();
                        eagle.orbitDist = 4 + rand.nextInt(5);
                        eagle.orbitClockwise = rand.nextBoolean();
                        orbitTime = 0;
                        maxOrbitTime = (int) (360 + 360 * rand.nextFloat());
                    }
                    this.flightTarget = eagle.isBeingRidden() || rand.nextInt(7) > 0 && eagle.timeFlying < 700;
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
            if (!flightTarget && isFlying() && eagle.onGround) {
                eagle.setFlying(false);
                orbitTime = 0;
                eagle.orbitPos = null;
                orbitResetCooldown = -400 - rand.nextInt(400);
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
            if (eagle.isTamed() && eagle.getCommand() == 1 && eagle.getOwner() != null) {
                vector3d = eagle.getOwner().getPositionVec();
                eagle.orbitPos = eagle.getOwner().getPosition();
            }
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
            if (eagle.isSitting()) {
                return false;
            }
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

    private class AITackle extends Goal {
        protected EntityBaldEagle eagle;
        private int circleTime;
        private int maxCircleTime = 10;


        public AITackle() {
            this.eagle = EntityBaldEagle.this;
        }

        @Override
        public boolean shouldExecute() {
            return eagle.getAttackTarget() != null && !eagle.controlledFlag && !eagle.isBeingRidden();
        }

        public void startExecuting() {
            eagle.orbitPos = null;
        }

        public void resetTask() {
            circleTime = 0;
            maxCircleTime = 60 + rand.nextInt(60);
        }

        public void tick() {
            LivingEntity target = eagle.getAttackTarget();
            boolean smallPrey = target != null && target.getHeight() < 1F && target.getWidth() < 0.7F && !(target instanceof EntityBaldEagle) || target instanceof AbstractFishEntity;
            if (eagle.orbitPos != null && circleTime < maxCircleTime) {
                circleTime++;
                eagle.setTackling(false);
                eagle.setFlying(true);
                if (target != null) {
                    int i = 0;
                    int up = 2 + eagle.getRNG().nextInt(4);
                    eagle.orbitPos = target.getPosition().up((int) (target.getHeight()));
                    while (eagle.world.isAirBlock(eagle.orbitPos) && i < up) {
                        i++;
                        eagle.orbitPos = eagle.orbitPos.up();
                    }
                }
                Vector3d vec = eagle.getOrbitVec(Vector3d.ZERO, 4 + rand.nextInt(2));
                if (vec != null) {
                    eagle.getMoveHelper().setMoveTo(vec.x, vec.y, vec.z, 1.2F);
                }
            } else if (target != null) {
                if (eagle.isFlying() || eagle.isInWaterOrBubbleColumn()) {
                    double d0 = eagle.getPosX() - target.getPosX();
                    double d2 = eagle.getPosZ() - target.getPosZ();
                    double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
                    double yAddition = target.getHeight();
                    if (xzDist > 15) {
                        yAddition = 3D;
                    }
                    eagle.setTackling(true);
                    eagle.getMoveHelper().setMoveTo(target.getPosX(), target.getPosY() + yAddition, target.getPosZ(), eagle.isInWaterOrBubbleColumn() ? 1.3F : 1.0F);
                } else {
                    this.eagle.getNavigator().tryMoveToEntityLiving(target, 1F);
                }
                if (eagle.getDistance(target) < target.getWidth() + 2.5F) {
                    if (eagle.isTackling()) {
                        if (smallPrey) {
                            eagle.setFlying(true);
                            eagle.timeFlying = 0;
                            float radius = 0.3F;
                            float angle = (0.01745329251F * eagle.renderYawOffset);
                            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                            double extraZ = radius * MathHelper.cos(angle);
                            target.rotationYaw = eagle.renderYawOffset + 90F;
                            if (target instanceof LivingEntity) {
                                LivingEntity living = target;
                                living.renderYawOffset = eagle.renderYawOffset + 90F;
                            }
                            target.setPosition(eagle.getPosX() + extraX, eagle.getPosY() - 0.4F + target.getHeight() * 0.45F, eagle.getPosZ() + extraZ);
                            target.startRiding(eagle, true);
                        } else {
                            target.attackEntityFrom(DamageSource.causeMobDamage(eagle), 5);
                            eagle.setFlying(false);
                            eagle.orbitPos = target.getPosition().up(2);
                            circleTime = 0;
                            maxCircleTime = 60 + rand.nextInt(60);
                        }
                    } else {
                        eagle.attackEntityAsMob(target);
                    }
                } else if (eagle.getDistance(target) > 12 || target.isInWaterOrBubbleColumn()) {
                    eagle.setFlying(true);
                }
            }
            if (eagle.isLaunched()) {
                eagle.setFlying(true);
            }
        }
    }

    private class AILandOnGlove extends Goal {
        protected EntityBaldEagle eagle;
        private int seperateTime = 0;

        public AILandOnGlove() {
            this.eagle = EntityBaldEagle.this;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {
            return eagle.isLaunched() && !eagle.controlledFlag && eagle.isTamed() && !eagle.isPassenger() && !eagle.isBeingRidden() && (eagle.getAttackTarget() == null || !eagle.getAttackTarget().isAlive());
        }

        public void tick() {
            if (eagle.getMotion().lengthSquared() < 0.03D) {
                seperateTime++;
            }
            LivingEntity owner = eagle.getOwner();
            if (owner != null) {
                if (seperateTime > 200) {
                    seperateTime = 0;
                    eagle.copyLocationAndAnglesFrom(owner);
                }
                eagle.setFlying(true);
                double d0 = eagle.getPosX() - owner.getPosX();
                double d2 = eagle.getPosZ() - owner.getPosZ();
                double xzDist = Math.sqrt(d0 * d0 + d2 * d2);
                double yAdd = xzDist > 14 ? 5 : 0;
                eagle.getMoveHelper().setMoveTo(owner.getPosX(), owner.getPosY() + yAdd + owner.getEyeHeight(), owner.getPosZ(), 1);

                if (this.eagle.getDistance(owner) < owner.getWidth() + 1.4D) {
                    this.eagle.setLaunched(false);
                    if (this.eagle.getRidingEagles(owner) <= 0) {
                        this.eagle.startRiding(owner);
                        if (!eagle.world.isRemote) {
                            AlexsMobs.sendMSGToAll(new MessageMosquitoMountPlayer(eagle.getEntityId(), owner.getEntityId()));
                        }
                    } else {
                        this.eagle.setCommand(2);
                        this.eagle.setSitting(true);
                    }
                }
            }
        }

        public void resetTask() {
            seperateTime = 0;
        }
    }
}
