package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.TameableEntity;
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
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityGrizzlyBear extends TameableEntity implements IAngerable, IAnimatedEntity, ITargetsDroppedItems {

    public static final Animation ANIMATION_MAUL = Animation.create(20);
    public static final Animation ANIMATION_SNIFF = Animation.create(12);
    public static final Animation ANIMATION_SWIPE_R = Animation.create(15);
    public static final Animation ANIMATION_SWIPE_L = Animation.create(20);
    private static final DataParameter<Boolean> STANDING = EntityDataManager.createKey(EntityGrizzlyBear.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityGrizzlyBear.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HONEYED = EntityDataManager.createKey(EntityGrizzlyBear.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EATING = EntityDataManager.createKey(EntityGrizzlyBear.class, DataSerializers.BOOLEAN);
    private static final RangedInteger angerLogic = TickRangeConverter.convertRange(20, 39);
    public float prevStandProgress;
    public float prevSitProgress;
    public float standProgress;
    public float sitProgress;
    public int maxStandTime = 75;
    public boolean forcedSit = false;
    private int animationTick;
    private Animation currentAnimation;
    private int standingTime = 0;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    private int eatingTime = 0;
    private int angerTime;
    private UUID angerTarget;
    private int warningSoundTicks;
    private int honeyedTime;
    @Nullable
    private UUID salmonThrowerID = null;
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(Items.SALMON, Items.HONEYCOMB, Items.HONEY_BOTTLE);
    public int timeUntilNextFur = this.rand.nextInt(24000) + 24000;

    protected EntityGrizzlyBear(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 40.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.6F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.grizzlyBearSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.func_233687_w_(false);
            if (entity != null && this.isTamed() && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GRIZZLY_BEAR_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GRIZZLY_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GRIZZLY_BEAR_DIE;
    }

    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float sitAdd = -0.065F * this.sitProgress;
            float standAdd = -0.07F * this.standProgress;
            float radius = standAdd + sitAdd;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() + extraZ);
        }
    }

    public double getMountedYOffset() {
        float f = Math.min(0.25F, this.limbSwingAmount);
        float f1 = this.limbSwing;
        float sitAdd = 0.01F * this.sitProgress;
        float standAdd = 0.07F * this.standProgress;
        return (double)this.getHeight() - 0.4D + (double)(0.12F * MathHelper.cos(f1 * 0.7F) * 0.7F * f) + sitAdd + standAdd;
    }


    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public void func_230258_H__() {
        this.setAngerTime(angerLogic.getRandomWithinRange(this.rand));
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setAngerTime(int time) {
        this.angerTime = time;
    }

    public UUID getAngerTarget() {
        return this.angerTarget;
    }

    public void setAngerTarget(@Nullable UUID target) {
        this.angerTarget = target;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.damageType != null && source.damageType.equals("sting") || super.isInvulnerableTo(source);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new TameableAIRide(this, 1D));
        this.goalSelector.addGoal(2, new EntityGrizzlyBear.MeleeAttackGoal());
        this.goalSelector.addGoal(2, new EntityGrizzlyBear.PanicGoal());
        this.goalSelector.addGoal(4, new TameableAITempt(this, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(4, new GrizzlyBearAIBeehive(this));
        this.goalSelector.addGoal(5, new GrizzlyBearAIFleeBees(this, 14, 1D, 1D));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.75D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new EntityGrizzlyBear.HurtByTargetGoal());
        this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(5, new EntityGrizzlyBear.AttackPlayerGoal());
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, FoxEntity.class, 10, true, true, null));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, WolfEntity.class, 10, true, true, null));
        this.targetSelector.addGoal(7, new ResetAngerGoal<>(this, false));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Honeyed", this.isHoneyed());
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("BearSitting", this.isSitting());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("FurTime", this.timeUntilNextFur);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setHoneyed(compound.getBoolean("Honeyed"));
        this.setStanding(compound.getBoolean("Standing"));
        this.setSitting(compound.getBoolean("BearSitting"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.timeUntilNextFur = compound.getInt("FurTime");
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return isTamed() && item == Items.SALMON;
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) passenger;
                return player;
            }
        }
        return null;
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if(type != ActionResultType.SUCCESS && isTamed() && isOwner(player) && !isBreedingItem(itemstack)){
            if(!player.isSneaking()){
                player.startRiding(this);
                return ActionResultType.SUCCESS;
            }else{
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
        }
        return type;
    }

    public void travel(Vector3d vec3d) {
        if (!this.shouldMove()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }

    public void tick() {
        super.tick();
        if (this.isChild() && this.getEyeHeight() > this.getHeight()) {
            this.recalculateSize();
        }
        this.prevStandProgress = this.standProgress;
        this.prevSitProgress = this.sitProgress;
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
            for(int i = 0; i < 3; i++){
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getHeldItem(Hand.MAIN_HAND)), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
            }
            if(eatingTime % 5 == 0){
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
            }
            if(eatingTime > 100){
                ItemStack stack = this.getHeldItem(Hand.MAIN_HAND);
                if(!stack.isEmpty()){
                    if(ItemTags.getCollection().get(AMTagRegistry.GRIZZLY_HONEY).contains(stack.getItem())){
                        this.setHoneyed(true);
                        this.heal(10);
                        this.honeyedTime = 700;
                    }else{
                        this.heal(4);
                    }
                    if(stack.getItem() == Items.SALMON && !this.isTamed() && this.salmonThrowerID != null){
                       if(getRNG().nextFloat() < 0.3F){
                           this.setTamed(true);
                           this.setOwnerId(this.salmonThrowerID);
                           PlayerEntity player = world.getPlayerByUuid(salmonThrowerID);
                           if (player instanceof ServerPlayerEntity) {
                               CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
                           }
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
        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + rand.nextInt(50);
        }
        if (isSitting() && !forcedSit && ++sittingTime > maxSitTime) {
            this.setSitting(false);
            sittingTime = 0;
            maxSitTime = 75 + rand.nextInt(50);
        }
        if (!world.isRemote && this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && rand.nextInt(1500) == 0) {
            maxSitTime = 300 + rand.nextInt(250);
            this.setSitting(true);
        }
        /*
        if(this.getAnimation() == NO_ANIMATION && !this.isStanding() && !this.isSitting() && rand.nextInt(1500) == 0){
            maxStandTime = 75 + rand.nextInt(50);
            this.setStanding(true);
        }
         */
        if (!forcedSit && this.isSitting() && (this.getAttackTarget() != null || this.isStanding()) && !this.isEating()) {
            this.setSitting(false);
        }
        if (this.getAnimation() == NO_ANIMATION && rand.nextInt(isStanding() ? 350 : 2500) == 0) {
            this.setAnimation(ANIMATION_SNIFF);
        }
        if (this.isSitting()) {
            this.getNavigator().clearPath();
        }
        LivingEntity attackTarget = this.getAttackTarget();
        if(this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity){
            PlayerEntity rider = (PlayerEntity)this.getControllingPassenger();
            if(rider.getLastAttackedEntity() != null && this.getDistance(rider.getLastAttackedEntity()) < this.getWidth() + 3F && !this.isOnSameTeam(rider.getLastAttackedEntity())){
                UUID preyUUID = rider.getLastAttackedEntity().getUniqueID();
                if (!this.getUniqueID().equals(preyUUID)) {
                    attackTarget = rider.getLastAttackedEntity();
                    if (getAnimation() == NO_ANIMATION || getAnimation() == ANIMATION_SNIFF) {
                        EntityGrizzlyBear.this.setAnimation(rand.nextBoolean() ? ANIMATION_MAUL : rand.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
                    }
                }
            }
        }
        if (attackTarget != null) {
            if(!world.isRemote){
                this.setSprinting(true);
            }
            if (getDistance(attackTarget) < attackTarget.getWidth() + this.getWidth() + 2) {
                if (this.getAnimation() == ANIMATION_MAUL && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() > 3) {
                    attackEntityAsMob(attackTarget);
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_L) && this.getAnimationTick() == 7) {
                    attackEntityAsMob(attackTarget);
                    float rot = rotationYaw + 90;
                    attackTarget.applyKnockback(0.5F, MathHelper.sin(rot * ((float) Math.PI / 180F)), -MathHelper.cos(rot * ((float) Math.PI / 180F)));
                }
                if ((this.getAnimation() == ANIMATION_SWIPE_R) && this.getAnimationTick() == 7) {
                    attackEntityAsMob(attackTarget);
                    float rot = rotationYaw - 90;
                    attackTarget.applyKnockback(0.5F, MathHelper.sin(rot * ((float) Math.PI / 180F)), -MathHelper.cos(rot * ((float) Math.PI / 180F)));
                }

            }
        }else{
            if(!world.isRemote){
                this.setSprinting(false);
            }
        }
        if(!world.isRemote && isHoneyed() && --honeyedTime <= 0){
            this.setHoneyed(false);
            honeyedTime = 0;
        }
        if(this.forcedSit && !this.isBeingRidden() && this.isTamed()){
            this.setSitting(true);
        }
        if(this.isBeingRidden() && this.isSitting()){
            this.setSitting(false);
        }
        if (!this.world.isRemote && this.isAlive() && isTamed() && !this.isChild() && --this.timeUntilNextFur <= 0) {
            this.entityDropItem(AMItemRegistry.BEAR_FUR);
            this.timeUntilNextFur = this.rand.nextInt(24000) + 24000;
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

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(STANDING, Boolean.valueOf(false));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(HONEYED, Boolean.valueOf(false));
        this.dataManager.register(EATING, Boolean.valueOf(false));
    }

    public boolean isEating() {
        return this.dataManager.get(EATING).booleanValue();
    }

    public void setEating(boolean eating) {
        this.dataManager.set(EATING, Boolean.valueOf(eating));
    }

    public boolean isHoneyed() {
        return this.dataManager.get(HONEYED).booleanValue();
    }

    public void setHoneyed(boolean honeyed) {
        this.dataManager.set(HONEYED, Boolean.valueOf(honeyed));
    }

    public boolean isStanding() {
        return this.dataManager.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(STANDING, Boolean.valueOf(standing));
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.GRIZZLY_BEAR.create(world);
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
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
        if (animation == ANIMATION_MAUL) {
            maxStandTime = 21;
            this.setStanding(true);
        }
        if (animation == ANIMATION_SWIPE_R || animation == ANIMATION_SWIPE_L) {
            maxStandTime = 2 + rand.nextInt(5);
            this.setStanding(true);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_MAUL, ANIMATION_SNIFF, ANIMATION_SWIPE_R, ANIMATION_SWIPE_L};
    }

    public boolean shouldMove() {
        return !isSitting();
    }

    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableEntity.AgeableData(1.0F);
        }

        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void playWarningSound() {
    }

    public boolean canTargetItem(ItemStack stack) {
        return ItemTags.getCollection().get(AMTagRegistry.GRIZZLY_FOODSTUFFS).contains(stack.getItem());
    }

    public void onGetItem(ItemEntity targetEntity) {
        ItemStack duplicate = targetEntity.getItem().copy();
        duplicate.setCount(1);
        if (!this.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.world.isRemote) {
            this.entityDropItem(this.getHeldItem(Hand.MAIN_HAND), 0.0F);
        }
        this.setHeldItem(Hand.MAIN_HAND, duplicate);
        if(targetEntity.getItem().getItem() == Items.SALMON && this.isHoneyed()){
            salmonThrowerID = targetEntity.getThrowerId();
        }else{
            salmonThrowerID = null;
        }
    }

    public boolean isEatingHeldItem() {
        return false;
    }

    class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(EntityGrizzlyBear.this);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            super.startExecuting();
            if (EntityGrizzlyBear.this.isChild()) {
                this.alertOthers();
                this.resetTask();
            }

        }

        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityGrizzlyBear && !mobIn.isChild()) {
                super.setAttackTarget(mobIn, targetIn);
            }

        }
    }

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
        public MeleeAttackGoal() {
            super(EntityGrizzlyBear.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0) {
                if (getAnimation() == NO_ANIMATION || getAnimation() == ANIMATION_SNIFF) {
                    EntityGrizzlyBear.this.setAnimation(rand.nextBoolean() ? ANIMATION_MAUL : rand.nextBoolean() ? ANIMATION_SWIPE_L : ANIMATION_SWIPE_R);
                }
            } else if (distToEnemySqr <= d0 * 2.0D) {
                if (this.func_234040_h_()) {
                    this.func_234039_g_();
                }
                if (this.func_234041_j_() <= 10) {
                    EntityGrizzlyBear.this.playWarningSound();
                }
            } else {
                this.func_234039_g_();
            }

        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EntityGrizzlyBear.this.setStanding(false);
            super.resetTask();
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 3.0F + attackTarget.getWidth();
        }
    }

    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(EntityGrizzlyBear.this, PlayerEntity.class, 3, true, true, null);
        }

        public boolean shouldExecute() {
            if (EntityGrizzlyBear.this.isChild() || EntityGrizzlyBear.this.isHoneyed()) {
                return false;
            } else {
                return super.shouldExecute();
            }
        }

        protected double getTargetDistance() {
            return 3.0D;
        }
    }

    class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
        public PanicGoal() {
            super(EntityGrizzlyBear.this, 2.0D);
        }

        public boolean shouldExecute() {
            return (EntityGrizzlyBear.this.isChild() || EntityGrizzlyBear.this.isBurning()) && super.shouldExecute();
        }
    }
}
