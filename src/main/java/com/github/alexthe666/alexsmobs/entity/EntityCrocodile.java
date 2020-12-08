package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class EntityCrocodile extends TameableEntity implements IAnimatedEntity, ISemiAquatic {

    public static final Animation ANIMATION_LUNGE = Animation.create(23);
    public static final Animation ANIMATION_DEATHROLL = Animation.create(40);
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DESERT = EntityDataManager.createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
    public float groundProgress = 0;
    public float prevGroundProgress = 0;
    public float swimProgress = 0;
    public float prevSwimProgress = 0;
    public float baskingProgress = 0;
    public float prevBaskingProgress = 0;
    public float grabProgress = 0;
    public float prevGrabProgress = 0;
    public int baskingType = 0;
    public boolean forcedSit = false;
    private int baskingTimer = 0;
    private int swimTimer = -1000;
    private int ticksSinceInWater = 0;
    private int passengerTimer = 0;
    private boolean isLandNavigator;
    private boolean hasSpedUp = false;
    private int animationTick;
    private Animation currentAnimation;
    public int timeUntilNextEgg = this.rand.nextInt(24000) + 24000;

    public static final Predicate<Entity> NOT_CREEPER = (entity) -> {
        return entity.isAlive() && !(entity instanceof CreeperEntity);
    };

    protected EntityCrocodile(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        switchNavigator(false);
        this.baskingType = rand.nextInt(1);
    }

    public static boolean canCrocodileSpawn(EntityType type, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.CROCODILE_SPAWNS).contains(worldIn.getBlockState(pos.down()).getBlock());
        return spawnBlock && pos.getY() < worldIn.getSeaLevel() + 4;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0D).createMutableAttribute(Attributes.ARMOR, 10.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 10.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.4F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.crocSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public int getMaxSpawnedInChunk() {
        return 2;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }

    protected void onGrowingAdult() {
        super.onGrowingAdult();
        if (!this.isChild() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.entityDropItem(new ItemStack(AMItemRegistry.CROCODILE_SCUTE, rand.nextInt(1) + 1), 1);
        }
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setDesert(this.isBiomeDesert(worldIn, this.getPosition()));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private boolean isBiomeDesert(IWorld worldIn, BlockPos position) {
        RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, worldIn.getBiome(position).getRegistryName());
        boolean sand = BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.SANDY);
        return sand;
    }

    protected SoundEvent getAmbientSound() {
        return isChild() ? AMSoundRegistry.CROCODILE_BABY : AMSoundRegistry.CROCODILE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CROCODILE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CROCODILE_HURT;
    }


    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("CrocodileSitting", this.isSitting());
        compound.putBoolean("Desert", this.isDesert());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("BaskingStyle", this.baskingType);
        compound.putInt("BaskingTimer", this.baskingTimer);
        compound.putInt("SwimTimer", this.swimTimer);
        compound.putInt("EggTime", this.timeUntilNextEgg);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("CrocodileSitting"));
        this.setDesert(compound.getBoolean("Desert"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.baskingType = compound.getInt("BaskingStyle");
        this.baskingTimer = compound.getInt("BaskingTimer");
        this.swimTimer = compound.getInt("SwimTimer");
        if (compound.contains("FeatherTime")) {
            this.timeUntilNextEgg = compound.getInt("EggTime");
        }
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            PathNavigator prevNav = this.navigator;
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new AquaticMoveController(this, 1F);
            PathNavigator prevNav = this.navigator;
            this.navigator = new SemiAquaticPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(DESERT, Boolean.valueOf(false));
        this.dataManager.register(CLIMBING, (byte) 0);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataManager.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.dataManager.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }
        this.dataManager.set(CLIMBING, b0);
    }

    public void tick() {
        super.tick();
        this.prevGroundProgress = groundProgress;
        this.prevSwimProgress = swimProgress;
        this.prevBaskingProgress = baskingProgress;
        this.prevGrabProgress = grabProgress;
        boolean ground = !this.isInWater();
        boolean groundAnimate = !this.isInWater();
        boolean basking = groundAnimate && this.isSitting();
        boolean grabbing = !this.getPassengers().isEmpty();
        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (groundAnimate && this.groundProgress < 10F) {
            this.groundProgress++;
        }
        if (!groundAnimate && this.groundProgress > 0F) {
            this.groundProgress--;
        }
        if (!groundAnimate && this.swimProgress < 10F) {
            this.swimProgress++;
        }
        if (groundAnimate && this.swimProgress > 0F) {
            this.swimProgress--;
        }
        if (basking && this.baskingProgress < 10F) {
            this.baskingProgress++;
        }
        if (!basking && this.baskingProgress > 0F) {
            this.baskingProgress--;
        }
        if (grabbing && this.grabProgress < 10F) {
            this.grabProgress++;
        }
        if (!grabbing && this.grabProgress > 0F) {
            this.grabProgress--;
        }
        if (this.getAttackTarget() != null && !hasSpedUp) {
            hasSpedUp = true;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35F);
        }
        if (this.getAttackTarget() == null && hasSpedUp) {
            hasSpedUp = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        }
        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
        }
        if (baskingTimer < 0) {
            baskingTimer++;
        }
        if (passengerTimer > 0 && this.getPassengers().isEmpty()) {
            passengerTimer = 0;
        }
        if (!world.isRemote) {
            if (isInWater()) {
                swimTimer++;
                ticksSinceInWater = 0;
            } else {
                ticksSinceInWater++;
                swimTimer--;
            }
        }
        if (!world.isRemote && !this.isInWater() && this.isOnGround()) {
            if (!this.isTamed()) {
                if (!this.isSitting() && baskingTimer == 0 && this.getAttackTarget() == null && this.getNavigator().noPath()) {
                    this.setSitting(true);
                    this.baskingTimer = 1000 + rand.nextInt(750);
                }
                if (this.isSitting() && (baskingTimer <= 0 || this.getAttackTarget() != null || swimTimer < -1000)) {
                    this.setSitting(false);
                    this.baskingTimer = -2000 - rand.nextInt(750);
                }
                if (this.isSitting() && baskingTimer > 0) {
                    baskingTimer--;
                }
            }
        }
        if (!world.isRemote && this.getAttackTarget() != null && this.getAnimation() == ANIMATION_LUNGE && this.getAnimationTick() > 5 && this.getAnimationTick() < 9) {
            float f1 = this.rotationYaw * ((float) Math.PI / 180F);
            this.setMotion(this.getMotion().add(-MathHelper.sin(f1) * 0.02F, 0.0D, MathHelper.cos(f1) * 0.02F));
            if (this.getDistance(this.getAttackTarget()) < 3.5F && this.canEntityBeSeen(this.getAttackTarget())) {
                if (this.getAttackTarget().getWidth() < this.getWidth() && this.getPassengers().isEmpty()) {
                    this.getAttackTarget().startRiding(this, true);
                }
                this.playSound(AMSoundRegistry.CROCODILE_BITE, this.getSoundVolume(), this.getSoundPitch());
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        }
        if (!world.isRemote && this.getAttackTarget() != null && this.isInWater()) {
            if (this.getAttackTarget().getRidingEntity() != null && this.getAttackTarget().getRidingEntity() == this) {
                if (this.getAnimation() == NO_ANIMATION) {
                    this.setAnimation(ANIMATION_DEATHROLL);
                }
                if (this.getAnimation() == ANIMATION_DEATHROLL && this.getAnimationTick() % 10 == 0) {
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), 2);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_DEATHROLL) {
            this.getNavigator().clearPath();
        }
        if (!this.world.isRemote && this.isAlive() && !this.isChild() && --this.timeUntilNextEgg <= 0) {
            if(!this.isInWater()){
                this.entityDropItem(AMItemRegistry.CROCODILE_EGG);
                this.timeUntilNextEgg = this.rand.nextInt(24000) + 24000;
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
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

    public void updatePassenger(Entity passenger) {
        if (!this.getPassengers().isEmpty()) {
            this.renderYawOffset = MathHelper.wrapDegrees(this.rotationYaw - 180F);
        }
        if (this.isPassenger(passenger)) {
            float radius = 2F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + 0.1F, this.getPosZ() + extraZ);
            passengerTimer++;
            if (passengerTimer > 0 && passengerTimer % 40 == 0) {
                passenger.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
            }
        }
    }

    public boolean isOnLadder() {
        return isInWater() && this.isBesideClimbableBlock();
    }

    public boolean isPushedByWater() {
        return false;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION && this.getPassengers().isEmpty()) {
            this.setAnimation(ANIMATION_LUNGE);
        }
        return true;
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
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

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return super.getBlockPathWeight(pos, worldIn);

    }

    public boolean shouldLeaveWater() {
        if (!this.getPassengers().isEmpty()) {
            return false;
        }
        if (this.getAttackTarget() != null && !this.getAttackTarget().isInWater()) {
            return true;
        }
        return swimTimer > 600;
    }

    @Override
    public boolean shouldStopMoving() {
        return this.getAnimation() == ANIMATION_DEATHROLL;
    }

    @Override
    public int getWaterSearchRange() {
        return this.getPassengers().isEmpty() ? 15 : 45;
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isDesert() {
        return this.dataManager.get(DESERT).booleanValue();
    }

    public void setDesert(boolean desert) {
        this.dataManager.set(DESERT, Boolean.valueOf(desert));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new BreatheAirGoal(this));
        this.goalSelector.addGoal(1, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(2, new CrocodileAIMelee(this, 1, true));
        this.goalSelector.addGoal(3, new CrocodileAIRandomSwimming(this, 1.0D, 7));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, PlayerEntity.class, true){
            public boolean shouldExecute(){
                return !isChild() && !isTamed() && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.CROCODILE_TARGETS))){
            public boolean shouldExecute(){
                return !isChild() && !isTamed() && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(6, new EntityAINearestTarget3D(this, MonsterEntity.class, 180, false, true, NOT_CREEPER){
            public boolean shouldExecute(){
                return !isChild() && isTamed() && super.shouldExecute();
            }
        });
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

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.CROCODILE.create(p_241840_1_);
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if(itemstack.getItem() == Items.NAME_TAG){
            return super.func_230254_b_(player, hand);
        }
        if(isTamed() && item.isFood() && item.getFood() != null && item.getFood().isMeat() && this.getHealth() < this.getMaxHealth()){
            this.consumeItemFromStack(player, itemstack);
            this.heal(10);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
            return ActionResultType.SUCCESS;
        }
        ActionResultType type = super.func_230254_b_(player, hand);
        if(type != ActionResultType.SUCCESS && isTamed() && isOwner(player) && !isBreedingItem(itemstack) ){
            if(this.isSitting()){
                this.forcedSit = false;
                this.setSitting(false);
                return ActionResultType.SUCCESS;
            }else{
                this.forcedSit = true;
                this.setSitting(true);
                return ActionResultType.SUCCESS;
            }
        }
        return type;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldEnterWater() {
        if (!this.getPassengers().isEmpty()) {
            return true;
        }
        return this.getAttackTarget() == null && !this.isSitting() && this.baskingTimer <= 0 && !shouldLeaveWater() && swimTimer <= -1000;
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
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_LUNGE, ANIMATION_DEATHROLL};
    }
}
