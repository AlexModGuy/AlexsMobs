package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class EntityRaccoon extends TameableEntity implements IAnimatedEntity, IFollower, ITargetsDroppedItems, ILootsChests {

    private static final DataParameter<Boolean> STANDING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BEGGING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WASHING = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<BlockPos>> WASH_POS = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CARPET_COLOR = EntityDataManager.createKey(EntityRaccoon.class, DataSerializers.VARINT);
    public float prevStandProgress;
    public float standProgress;
    public float prevBegProgress;
    public float begProgress;
    public float prevWashProgress;
    public float washProgress;
    public float prevSitProgress;
    public float sitProgress;
    public int maxStandTime = 75;
    private int standingTime = 0;
    private int stealCooldown = 0;
    public int lookForWaterBeforeEatingTimer = 0;
    private int animationTick;
    private Animation currentAnimation;
    private int pickupItemCooldown = 0;
    @Nullable
    private UUID eggThrowerUUID = null;
    public boolean forcedSit = false;
    public static final Animation ANIMATION_ATTACK = Animation.create(12);
    private static final EntityPredicate VILLAGER_STEAL_PREDICATE = (new EntityPredicate()).setDistance(20.0D).allowInvulnerable().allowFriendlyFire();
    private static final EntityPredicate IRON_GOLEM_PREDICATE = (new EntityPredicate()).setDistance(20.0D).setIgnoresLineOfSight().allowInvulnerable().allowFriendlyFire();

    protected EntityRaccoon(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }


    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.RACCOON_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.RACCOON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.RACCOON_HURT;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.raccoonSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new RaccoonAIWash(this));
        this.goalSelector.addGoal(3, new TameableAIFollowOwner(this, 1.3D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new SwimGoal(this));
        this.goalSelector.addGoal(5, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(7, new AnimalAILootChests(this, 16));
        this.goalSelector.addGoal(8, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(9, new RaccoonAIBeg(this, 0.65D));
        this.goalSelector.addGoal(10, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(11, new AIStealFromVillagers(this));
        this.goalSelector.addGoal(12, new StrollGoal(200));
        this.goalSelector.addGoal(13, new TameableAIDestroyTurtleEggs(this, 1.0D, 3));
        this.goalSelector.addGoal(14, new AnimalAIWanderRanged(this, 120, 1.0D, 14, 7));
        this.goalSelector.addGoal(15, new LookAtGoal(this, PlayerEntity.class, 15.0F));
        this.goalSelector.addGoal(15, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new OwnerHurtTargetGoal(this));
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


    public boolean attackEntityAsMob(Entity entityIn) {
        if(this.getAnimation() == NO_ANIMATION){
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }

    protected void dropInventory() {
        super.dropInventory();
        if (this.getColor() != null) {
            if (!this.world.isRemote) {
                this.entityDropItem(this.getCarpetItemBeingWorn());
            }
            this.setColor(null);
        }

    }

    @Nullable
    public DyeColor getColor() {
        int lvt_1_1_ = this.dataManager.get(CARPET_COLOR);
        return lvt_1_1_ == -1 ? null : DyeColor.byId(lvt_1_1_);
    }

    public void setColor(@Nullable DyeColor color) {
        this.dataManager.set(CARPET_COLOR, color == null ? -1 : color.getId());
    }

    public Item getCarpetItemBeingWorn() {
        if (this.getColor() != null) {
            return EntityElephant.DYE_COLOR_ITEM_MAP.get(this.getColor());
        }
        return Items.AIR;
    }


    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.BREAD;
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.getEntityInteractionResult(player, hand);
        boolean owner = this.isTamed() && isOwner(player);
        if (owner && ItemTags.CARPETS.contains(item)) {
            DyeColor color = EntityElephant.getCarpetColor(itemstack);
            if (color != this.getColor()) {
                if (this.getColor() != null) {
                    this.entityDropItem(this.getCarpetItemBeingWorn());
                }
                this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                itemstack.shrink(1);
                this.setColor(color);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        } else if (owner && this.getColor() != null && itemstack.getItem() == Items.SHEARS) {
            this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            if (this.getColor() != null) {
                this.entityDropItem(this.getCarpetItemBeingWorn());
            }
            this.setColor(null);
            return ActionResultType.SUCCESS;
        }else if(isTamed() && isFood(itemstack) && !isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()){
            if(this.getHeldItemMainhand().isEmpty()){
                ItemStack copy = itemstack.copy();
                copy.setCount(1);
                this.setHeldItem(Hand.MAIN_HAND, copy);
                this.onEatItem();
                if(itemstack.hasContainerItem()){
                    this.entityDropItem(itemstack.getContainerItem());
                }
                if(!player.isCreative()){
                    itemstack.shrink(1);
                }
                this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            }else{
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
                this.heal(5);
            }
            this.consumeItemFromStack(player, itemstack);
            return ActionResultType.SUCCESS;
        }
        if(owner && !this.getHeldItemMainhand().isEmpty()){
            if(!this.world.isRemote){
                this.entityDropItem(this.getHeldItemMainhand().copy());
            }
            this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            pickupItemCooldown = 60;
            return ActionResultType.SUCCESS;
        }
        if(type != ActionResultType.SUCCESS && isTamed() && isOwner(player) && !isBreedingItem(itemstack)){
            if(!player.isSneaking()){
                this.setCommand(this.getCommand() + 1);
                if(this.getCommand() == 3){
                    this.setCommand(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                boolean sit = this.getCommand() == 2;
                if(sit){
                    this.forcedSit = true;
                    this.setSitting(true);
                    return ActionResultType.SUCCESS;
                }else{
                    this.forcedSit = false;
                    this.setSitting(false);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return type;
    }


    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("RacSitting", this.isSitting());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("RacCommand", this.getCommand());
        compound.putInt("Carpet", this.dataManager.get(CARPET_COLOR));
        compound.putInt("StealCooldown", stealCooldown);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSitting(compound.getBoolean("RacSitting"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.setCommand(compound.getInt("RacCommand"));
        this.dataManager.set(CARPET_COLOR, compound.getInt("Carpet"));
        this.stealCooldown = compound.getInt("StealCooldown");

    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public static boolean isFood(ItemStack stack){
        return stack.isFood() || ItemTags.getCollection().get(AMTagRegistry.RACCOON_FOODSTUFFS).contains(stack.getItem());
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 9D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.setSitting(false);
            if (entity != null && this.isTamed() && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 4.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    public void tick() {
        super.tick();
        this.prevStandProgress = this.standProgress;
        this.prevBegProgress = this.begProgress;
        this.prevWashProgress = this.washProgress;
        this.prevSitProgress = this.sitProgress;
        if (this.isStanding() && standProgress < 5) {
            standProgress += 1;
        }
        if (!this.isStanding() && standProgress > 0) {
            standProgress -= 1;
        }
        if (this.isBegging() && begProgress < 5) {
            begProgress += 1;
        }
        if (!this.isBegging() && begProgress > 0) {
            begProgress -= 1;
        }
        if (this.isWashing() && washProgress < 5) {
            washProgress += 1;
        }
        if (!this.isWashing() && washProgress > 0) {
            washProgress -= 1;
        }
        if (this.isSitting() && sitProgress < 5) {
            sitProgress += 1;
        }
        if (!this.isSitting() && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (isStanding() && ++standingTime > maxStandTime) {
            this.setStanding(false);
            standingTime = 0;
            maxStandTime = 75 + rand.nextInt(50);
        }
        if(!world.isRemote){
            if(lookForWaterBeforeEatingTimer > 0){
                lookForWaterBeforeEatingTimer--;
            }else if(!isWashing() && canTargetItem(this.getHeldItemMainhand())) {
                onEatItem();
                if(this.getHeldItemMainhand().hasContainerItem()){
                    this.entityDropItem(this.getHeldItemMainhand().getContainerItem());
                }
                this.getHeldItemMainhand().shrink(1);
            }
        }
        if(isWashing()){
            if(getWashPos() != null){
                BlockPos washingPos = getWashPos();
                if(this.getDistanceSq(washingPos.getX() + 0.5D, washingPos.getY() + 0.5D, washingPos.getZ() + 0.5D) < 3){
                    for(int j = 0; (float)j < 4; ++j) {
                        double d2 = (this.rand.nextDouble()) ;
                        double d3 = (this.rand.nextDouble()) ;
                        Vector3d vector3d = this.getMotion();

                        this.world.addParticle(ParticleTypes.SPLASH, washingPos.getX() + d2, (double)(washingPos.getY() + 0.8F), washingPos.getZ() + d3, vector3d.x, vector3d.y, vector3d.z);
                    }
                }else{
                    setWashing(false);
                }
            }
        }
        if(!world.isRemote && this.getAttackTarget() != null && this.canEntityBeSeen(this.getAttackTarget()) && this.getDistance(this.getAttackTarget()) < 4 && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 5) {
            float f1 = this.rotationYaw * ((float)Math.PI / 180F);
            this.setMotion(this.getMotion().add((double)(-MathHelper.sin(f1) * -0.06F), 0.0D, (double)(MathHelper.cos(f1) * -0.06F)));
            this.getAttackTarget().applyKnockback(0.35F, getAttackTarget().getPosX() - this.getPosX(), getAttackTarget().getPosZ() - this.getPosZ());
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
        if(stealCooldown > 0){
            stealCooldown--;
        }
        if(pickupItemCooldown > 0){
            pickupItemCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void onEatItem(){
        this.heal(10);
        this.world.setEntityState(this, (byte)92);
        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
    }

    public void postWashItem(ItemStack stack){
        if(stack.getItem() == Items.EGG && eggThrowerUUID != null && !this.isTamed()){
            if(getRNG().nextFloat() < 0.3F){
                this.setTamed(true);
                this.setOwnerId(eggThrowerUUID);
                PlayerEntity player = world.getPlayerByUuid(eggThrowerUUID);
                if (player instanceof ServerPlayerEntity) {
                    CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
                }
                this.world.setEntityState(this, (byte)7);
            }else{
                this.world.setEntityState(this, (byte)6);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if(id == 92){
            for (int i = 0; i < 6 + rand.nextInt(3); i++) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getHeldItem(Hand.MAIN_HAND)), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
            }
        }else{
            super.handleStatusUpdate(id);
        }
    }
        public boolean isStanding() {
        return this.dataManager.get(STANDING).booleanValue();
    }

    public void setStanding(boolean standing) {
        this.dataManager.set(STANDING, Boolean.valueOf(standing));
    }

    public boolean isBegging() {
        return this.dataManager.get(BEGGING).booleanValue();
    }

    public void setBegging(boolean begging) {
        this.dataManager.set(BEGGING, Boolean.valueOf(begging));
    }

    public boolean isWashing() {
        return this.dataManager.get(WASHING).booleanValue();
    }

    public void setWashing(boolean washing) {
        this.dataManager.set(WASHING, Boolean.valueOf(washing));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(STANDING, Boolean.valueOf(false));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(BEGGING, Boolean.valueOf(false));
        this.dataManager.register(WASHING, Boolean.valueOf(false));
        this.dataManager.register(CARPET_COLOR, -1);
        this.dataManager.register(COMMAND, 0);
        this.dataManager.register(WASH_POS, Optional.empty());
    }


    public BlockPos getWashPos() {
        return this.dataManager.get(WASH_POS).orElse(null);
    }

    public void setWashPos(BlockPos washingPos) {
        this.dataManager.set(WASH_POS, Optional.ofNullable(washingPos));
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
        if(animation == ANIMATION_ATTACK){
            maxStandTime = 15;
            this.setStanding(true);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_ATTACK};
    }


    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.RACCOON.create(serverWorld);
    }

    public void travel(Vector3d vec3d) {
        if (this.isSitting() || this.isWashing()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }

    @Override
    public boolean shouldFollow() {
        return getCommand() == 1;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return isFood(stack) && pickupItemCooldown == 0;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        lookForWaterBeforeEatingTimer = 100;
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.world.isRemote) {
            this.entityDropItem(this.getHeldItem(Hand.MAIN_HAND), 0.0F);
        }
        this.setHeldItem(Hand.MAIN_HAND, duplicate);
        if(e.getItem().getItem() == Items.EGG){
            eggThrowerUUID = e.getThrowerId();
        }else{
            eggThrowerUUID = null;
        }
    }

    @Override
    public boolean isLootable(IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (shouldLootItem(inventory.getStackInSlot(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldLootItem(ItemStack stack) {
        return isFood(stack);
    }

    class StrollGoal extends MoveThroughVillageAtNightGoal {
        public StrollGoal(int p_i50726_3_) {
            super(EntityRaccoon.this, p_i50726_3_);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            super.startExecuting();
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return super.shouldExecute() && this.func_220759_g();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && this.func_220759_g();
        }

        private boolean func_220759_g() {
            return !EntityRaccoon.this.isWashing() && !EntityRaccoon.this.isSitting() && EntityRaccoon.this.getAttackTarget() == null;
        }
    }

    public BlockPos getLightPosition() {
        BlockPos pos = new BlockPos(this.getPositionVec());
        if (!world.getBlockState(pos).isSolid()) {
            return pos.up();
        }
        return pos;
    }

    private class AIStealFromVillagers extends Goal {
        EntityRaccoon raccoon;
        AbstractVillagerEntity target;
        int golemCheckTime = 0;
        int cooldown = 0;
        int fleeTime = 0;

        private AIStealFromVillagers(EntityRaccoon raccoon){
            this.raccoon = raccoon;
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            if(cooldown > 0){
                cooldown--;
                return false;
            }else if(raccoon != null && raccoon.stealCooldown == 0 && raccoon.getHeldItemMainhand() != null && raccoon.getHeldItemMainhand().isEmpty()){
                AbstractVillagerEntity villager = getNearbyVillagers();
                if(!isGolemNearby() && villager != null){
                    target = villager;
                }
                cooldown = 150;
                return target != null;
            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return target != null && raccoon != null;
        }

        public void resetTask(){
            target = null;
            cooldown = 200 + rand.nextInt(200);
            golemCheckTime = 0;
            fleeTime = 0;
        }

        public void tick(){
            if(target != null){
                golemCheckTime++;
                if(fleeTime > 0){
                    fleeTime--;
                    if(raccoon.getNavigator().noPath()){
                        Vector3d fleevec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(raccoon, 16, 7, raccoon.getPositionVec());
                        if(fleevec != null){
                            raccoon.getNavigator().tryMoveToXYZ(fleevec.x, fleevec.y, fleevec.z, 1.3F);
                        }
                    }
                    if(fleeTime == 0){
                        resetTask();
                    }
                }else{
                    raccoon.getNavigator().tryMoveToEntityLiving(target, 1.0D);
                    if(raccoon.getDistance(target) < 1.7F){
                        raccoon.setStanding(true);
                        raccoon.maxStandTime = 15;
                        MerchantOffers offers = target.getOffers();
                        if(offers == null || offers.isEmpty() || offers.size() < 1){
                            resetTask();
                        }else{
                            MerchantOffer offer = offers.get(offers.size() <= 1 ? 0 : raccoon.getRNG().nextInt(offers.size() - 1));
                            if(offer != null){
                                ItemStack stealStack = offer.getSellingStack().getItem() == Items.EMERALD ? offer.getBuyingStackFirst() : offer.getSellingStack();
                                if(stealStack.isEmpty()){
                                    resetTask();
                                }else{
                                    offer.increaseUses();
                                    ItemStack copy = stealStack.copy();
                                    copy.setCount(1);
                                    raccoon.setHeldItem(Hand.MAIN_HAND, copy);
                                    fleeTime = 60 + rand.nextInt(60);
                                    raccoon.getNavigator().clearPath();
                                    lookForWaterBeforeEatingTimer = 120 + rand.nextInt(60);
                                    target.attackEntityFrom(DamageSource.causeMobDamage(raccoon), target.getHealth() <= 2 ? 0 : 1);
                                    raccoon.stealCooldown = 24000 + rand.nextInt(48000);
                                }
                            }
                        }
                    }
                    if(golemCheckTime % 30 == 0 && rand.nextBoolean() && isGolemNearby()){
                        resetTask();
                    }
                }
            }
        }

        @Nullable
        private boolean isGolemNearby() {
            List<IronGolemEntity> lvt_1_1_ = raccoon.world.getTargettableEntitiesWithinAABB(IronGolemEntity.class, IRON_GOLEM_PREDICATE, raccoon, raccoon.getBoundingBox().grow(25.0D));
            return !lvt_1_1_.isEmpty();
        }

        @Nullable
        private AbstractVillagerEntity getNearbyVillagers() {
            List<AbstractVillagerEntity> lvt_1_1_ = raccoon.world.getTargettableEntitiesWithinAABB(AbstractVillagerEntity.class, VILLAGER_STEAL_PREDICATE, raccoon, raccoon.getBoundingBox().grow(20.0D));
            double lvt_2_1_ = 10000;
            AbstractVillagerEntity lvt_4_1_ = null;
            Iterator var5 = lvt_1_1_.iterator();

            while(var5.hasNext()) {
                AbstractVillagerEntity lvt_6_1_ = (AbstractVillagerEntity)var5.next();
                if (lvt_6_1_.getHealth() > 2.0F && !lvt_6_1_.getOffers().isEmpty() && raccoon.getDistanceSq(lvt_6_1_) < lvt_2_1_) {
                    lvt_4_1_ = lvt_6_1_;
                    lvt_2_1_ = raccoon.getDistanceSq(lvt_6_1_);
                }
            }

            return lvt_4_1_;
        }

    }
}
