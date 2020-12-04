package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EntityGorilla extends TameableEntity implements IAnimatedEntity, ITargetsDroppedItems {
    private int animationTick;
    private Animation currentAnimation;
    protected static final EntitySize SILVERBACK_SIZE = EntitySize.fixed(1.15F, 1.85F);
    private static final DataParameter<Boolean> SILVERBACK = EntityDataManager.createKey(EntityGorilla.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> STANDING = EntityDataManager.createKey(EntityGorilla.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityGorilla.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EATING = EntityDataManager.createKey(EntityGorilla.class, DataSerializers.BOOLEAN);
    public static final Animation ANIMATION_BREAKBLOCK_R = Animation.create(20);
    public static final Animation ANIMATION_BREAKBLOCK_L = Animation.create(20);
    public static final Animation ANIMATION_POUNDCHEST = Animation.create(40);
    public static final Animation ANIMATION_ATTACK = Animation.create(20);
    public int maxStandTime = 75;
    private int standingTime = 0;
    public float prevStandProgress;
    public float prevSitProgress;
    public float standProgress;
    public float sitProgress;
    private int eatingTime;
    @Nullable
    private EntityGorilla caravanHead;
    @Nullable
    private EntityGorilla caravanTail;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    @Nullable
    private UUID bananaThrowerID = null;
    public boolean forcedSit = false;
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(AMItemRegistry.BANANA);
    private boolean hasSilverbackAttributes = false;

    protected EntityGorilla(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.LEAVES, 0.0F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.gorillaSpawnRolls, this.getRNG(), spawnReasonIn);
    }
    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return isTamed() && item == AMItemRegistry.BANANA;
    }

    public int getMaxSpawnedInChunk() {
        return 8;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.func_233687_w_(false);
            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 2.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new GorillaAIFollowCaravan(this, 0.8D));
        this.goalSelector.addGoal(4, new TameableAITempt(this, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new GorillaAIRideParent(this, 1.25D));
        this.goalSelector.addGoal(6, new AIWalkIdle(this, 0.8D));
        this.goalSelector.addGoal(5, new GorillaAIForageLeaves(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GORILLA_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GORILLA_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GORILLA_HURT;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if(this.getAnimation() == NO_ANIMATION){
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }

    public void travel(Vector3d vec3d) {
        if (this.isSitting()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }


    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if(this.getNearestSilverback(worldIn, 16D) == null){
            this.setSilverback(true);
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    public EntityGorilla getNearestSilverback(IWorld world, double dist){
        List<EntityGorilla> list = world.getEntitiesWithinAABB(this.getClass(), this.getBoundingBox().grow(dist, dist/2, dist));
        if(list.isEmpty()){
            return null;
        }
        EntityGorilla gorilla = null;
        double d0 = Double.MAX_VALUE;
        for(EntityGorilla gorrila2 : list) {
            if (gorrila2.isSilverback()) {
                double d1 = this.getDistanceSq(gorrila2);
                if (!(d1 > d0)) {
                    d0 = d1;
                    gorilla = gorrila2;
                }
            }
        }
        return gorilla;
    }

    public EntitySize getSize(Pose poseIn) {
        return isSilverback() && !isChild() ? SILVERBACK_SIZE : super.getSize(poseIn);
    }

    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            this.setSitting(false);
            passenger.rotationYaw = this.rotationYaw;
            if(passenger instanceof EntityGorilla){
                EntityGorilla babyGorilla = (EntityGorilla)passenger;
                babyGorilla.setStanding(this.isStanding());
                babyGorilla.setSitting(this.isSitting());
            }
            float sitAdd = -0.03F * this.sitProgress;
            float standAdd = -0.03F * this.standProgress;
            float radius = standAdd + sitAdd;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() + extraZ);
        }
    }

    public boolean canBeSteered() {
        return false;
    }

    public double getMountedYOffset() {
        return (double)this.getHeight() * 0.55F * getGorillaScale() *(isSilverback() ? 0.75F : 1.0F);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SILVERBACK, Boolean.valueOf(false));
        this.dataManager.register(STANDING, Boolean.valueOf(false));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(EATING, Boolean.valueOf(false));
    }

    public boolean isSilverback() {
        return this.dataManager.get(SILVERBACK).booleanValue();
    }

    public void setSilverback(boolean silver) {
        this.dataManager.set(SILVERBACK, silver);
    }

    public boolean isStanding() {
        return this.dataManager.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(STANDING, Boolean.valueOf(standing));
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public boolean isEating() {
        return this.dataManager.get(EATING).booleanValue();
    }

    public void setEating(boolean eating) {
        this.dataManager.set(EATING, Boolean.valueOf(eating));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Silverback", this.isSilverback());
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("GorillaSitting", this.isSitting());
        compound.putBoolean("ForcedToSit", this.forcedSit);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSilverback(compound.getBoolean("Silverback"));
        this.setStanding(compound.getBoolean("Standing"));
        this.setSitting(compound.getBoolean("GorillaSitting"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 40.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ARMOR, 12.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 8.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if(itemstack.getItem() == Items.NAME_TAG){
            return super.func_230254_b_(player, hand);
        }
        if(isTamed() && item == AMItemRegistry.BANANA && this.getHealth() < this.getMaxHealth()){
            this.heal(5);
            this.consumeItemFromStack(player, itemstack);
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

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
        if(animation == ANIMATION_POUNDCHEST){
            this.maxStandTime = 45;
            this.setStanding(true);
        }
        if(animation == ANIMATION_ATTACK){
            this.maxStandTime = 10;
            this.setStanding(true);
        }
    }

    public void tick() {
        super.tick();
        if(!this.getHeldItem(Hand.MAIN_HAND).isEmpty() && this.canTargetItem(this.getHeldItem(Hand.MAIN_HAND))){
            this.setEating(true);
            this.setSitting(true);
            this.setStanding(false);
        }
        if(isEating() && !this.canTargetItem(this.getHeldItem(Hand.MAIN_HAND))){
            this.setEating(false);
            eatingTime = 0;
            if(!forcedSit){
                this.setSitting(true);
            }
        }
        if(isEating()){
            eatingTime++;
            if(!ItemTags.LEAVES.contains(this.getHeldItemMainhand().getItem())){
                for(int i = 0; i < 3; i++){
                    double d2 = this.rand.nextGaussian() * 0.02D;
                    double d0 = this.rand.nextGaussian() * 0.02D;
                    double d1 = this.rand.nextGaussian() * 0.02D;
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getHeldItem(Hand.MAIN_HAND)), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
                }
            }
            if(eatingTime % 5 == 0){
                this.playSound(SoundEvents.ENTITY_PANDA_EAT, this.getSoundVolume(), this.getSoundPitch());
            }
            if(eatingTime > 100){
                ItemStack stack = this.getHeldItem(Hand.MAIN_HAND);
                if(!stack.isEmpty()){
                    this.heal(4);
                    if(stack.getItem() == AMItemRegistry.BANANA && bananaThrowerID != null){
                        if(getRNG().nextFloat() < 0.3F){
                            this.setTamed(true);
                            this.setOwnerId(this.bananaThrowerID);
                            this.world.setEntityState(this, (byte)7);
                        }else{
                            this.world.setEntityState(this, (byte)6);
                        }
                    }
                    if(stack.hasContainerItem()){
                        this.entityDropItem(stack.getContainerItem());
                    }
                    stack.shrink(1);
                }
                eatingTime = 0;
            }
        }
        prevSitProgress = sitProgress;
        prevStandProgress = standProgress;
        if (this.isSitting() && sitProgress < 10) {
            sitProgress += 1;
        }
        if (!this.isSitting() && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (this.isStanding() && standProgress < 10) {
            standProgress += 1;
        }
        if (!this.isStanding() && standProgress > 0) {
            standProgress -= 1;
        }
        if(this.isPassenger() && this.getRidingEntity() instanceof EntityGorilla && !this.isChild()){
            this.dismount();
        }
        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + rand.nextInt(50);
        }
        if (isSitting()  && !forcedSit && ++sittingTime > maxSitTime) {
            this.setSitting(false);
            sittingTime = 0;
            maxSitTime = 75 + rand.nextInt(50);
        }
        if (!forcedSit && this.isSitting() && (this.getAttackTarget() != null || this.isStanding()) && !this.isEating()) {
            this.setSitting(false);
        }
        if (!world.isRemote && this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && rand.nextInt(1500) == 0) {
            maxSitTime = 300 + rand.nextInt(250);
            this.setSitting(true);
        }
        if(this.forcedSit && !this.isBeingRidden() && this.isTamed()){
            this.setSitting(true);
        }
        if(this.isSilverback() && rand.nextInt(600) == 0 && this.getAnimation() == NO_ANIMATION && !this.isSitting() && this.getHeldItemMainhand().isEmpty()){
            this.setAnimation(ANIMATION_POUNDCHEST);
        }
        if(!world.isRemote && this.getAttackTarget() != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 10) {
            float f1 = this.rotationYaw * ((float)Math.PI / 180F);
            this.setMotion(this.getMotion().add((double)(-MathHelper.sin(f1) * 0.02F), 0.0D, (double)(MathHelper.cos(f1) * 0.02F)));
            getAttackTarget().applyKnockback(1F, getAttackTarget().getPosX() - this.getPosX(), getAttackTarget().getPosZ() - this.getPosZ());
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
        if(isSilverback() && !isChild() && !hasSilverbackAttributes){
            hasSilverbackAttributes = true;
            recalculateSize();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20F);
        }
        if(!isSilverback() && !isChild() && hasSilverbackAttributes){
            hasSilverbackAttributes = false;
            recalculateSize();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8F);

        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }


    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    public boolean canTargetItem(ItemStack stack) {
        return ItemTags.getCollection().get(AMTagRegistry.GORILLA_FOODSTUFFS).contains(stack.getItem());
    }

    @Override
    public void onGetItem(ItemEntity targetEntity) {
        ItemStack duplicate = targetEntity.getItem().copy();
        duplicate.setCount(1);
        if (!this.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.world.isRemote) {
            this.entityDropItem(this.getHeldItem(Hand.MAIN_HAND), 0.0F);
        }
        this.setHeldItem(Hand.MAIN_HAND, duplicate);
        if(targetEntity.getItem().getItem() == AMItemRegistry.BANANA && !this.isTamed()){
            bananaThrowerID = targetEntity.getThrowerId();
        }
    }


    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BREAKBLOCK_R, ANIMATION_BREAKBLOCK_L, ANIMATION_POUNDCHEST, ANIMATION_ATTACK};
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.GORILLA.create(p_241840_1_);
    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(EntityGorilla caravanHeadIn) {
        this.caravanHead = caravanHeadIn;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTrail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public EntityGorilla getCaravanHead() {
        return this.caravanHead;
    }


    public float getGorillaScale() {
        return isChild() ? 0.5F : isSilverback() ? 1.3F : 1.0F;
    }

    public static boolean canGorillaSpawn(EntityType<EntityGorilla> gorilla, IWorld worldIn, SpawnReason reason, BlockPos p_223317_3_, Random random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.down());
        return (blockstate.isIn(BlockTags.LEAVES) || blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LOGS) || blockstate.isIn(Blocks.AIR)) && worldIn.getLightSubtracted(p_223317_3_, 0) > 8;
    }

    private class AIWalkIdle extends RandomWalkingGoal {
        public AIWalkIdle(EntityGorilla entityGorilla, double v) {
            super(entityGorilla, v);
        }

        public boolean shouldExecute() {
            this.executionChance = EntityGorilla.this.isSilverback() ? 10 : 120;
            return super.shouldExecute();
        }

        @Nullable
        protected Vector3d getPosition() {
            return RandomPositionGenerator.findRandomTarget(this.creature, EntityGorilla.this.isSilverback() ? 25 : 10, 7);
        }

    }
}
