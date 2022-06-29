package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class EntityMudskipper extends TamableAnimal implements IFollower, ISemiAquatic, Bucketable {

    public float prevSitProgress;
    public float sitProgress;
    public float prevSwimProgress;
    public float swimProgress;
    public float prevDisplayProgress;
    public float displayProgress;
    public float prevMudProgress;
    public float mudProgress;
    public float nextDisplayAngleFromServer;
    public float prevDisplayAngle;
    public boolean displayDirection;
    public int displayTimer = 0;
    public boolean instantlyTriggerDisplayAI = false;
    public int displayCooldown = 100 + random.nextInt(100);
    private static final EntityDataAccessor<Boolean> DISPLAYING = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DISPLAY_ANGLE = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> DISPLAYER_UUID = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> MOUTH_TICKS = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityMudskipper.class, EntityDataSerializers.INT);
    private boolean isLandNavigator;
    private int swimTimer = -1000;

    public EntityMudskipper(EntityType type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(true);
    }

    public void travel(Vec3 travelVector) {
        if (this.isOrderedToSit()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    public static <T extends Mob> boolean canMudskipperSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
        return blockstate.is(Blocks.MUD) || blockstate.is(Blocks.MUDDY_MANGROVE_ROOTS);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mudskipperSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        BlockPos pos = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
        return !worldIn.getBlockState(pos).isSuffocating(worldIn, pos);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new TameableAIFollowOwnerWater(this, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(2, new MudskipperAIAttack(this));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(AMItemRegistry.LOBSTER_TAIL.get(), AMItemRegistry.COOKED_LOBSTER_TAIL.get()), false));
        this.goalSelector.addGoal(5, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(7, new MudskipperAIDisplay(this));
        this.goalSelector.addGoal(8, new SemiAquaticAIRandomSwimming(this, 1.0D, 80));
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1.0D, 120));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return EntityMudskipper.this.isTame() && super.canUse();
            }
        });
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AnimalSwimMoveControllerSink(this, 1.3F, 1);
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DISPLAYING, Boolean.valueOf(false));
        this.entityData.define(FROM_BUCKET, Boolean.valueOf(false));
        this.entityData.define(DISPLAY_ANGLE, 0F);
        this.entityData.define(DISPLAYER_UUID, Optional.empty());
        this.entityData.define(MOUTH_TICKS, 0);
        this.entityData.define(COMMAND, 0);
        this.entityData.define(SITTING, false);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 12.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putInt("DisplayCooldown", this.displayCooldown);
        compound.putInt("MudskipperCommand", this.getCommand());
        compound.putBoolean("MudskipperSitting", this.isOrderedToSit());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.displayCooldown = compound.getInt("DisplayCooldown");
        this.setCommand(compound.getInt("MudskipperCommand"));
        this.setOrderedToSit(compound.getBoolean("MudskipperSitting"));
    }

    public void tick(){
        super.tick();
        prevSwimProgress = swimProgress;
        prevSitProgress = sitProgress;
        prevDisplayProgress = displayProgress;
        prevMudProgress = mudProgress;
        if(displayProgress < 5F && this.isDisplaying()){
            displayProgress++;
        }
        if(displayProgress > 0F && !this.isDisplaying()){
            displayProgress--;
        }
        if(sitProgress < 5F && this.isOrderedToSit()){
            sitProgress++;
        }
        if(sitProgress > 0F && !this.isOrderedToSit()){
            sitProgress--;
        }
        //so the model does not sink in mud
        boolean mud = onMud();
        if(mudProgress < 1F && mud){
            mudProgress += 0.5f;
        }
        if(mudProgress > 0 && !mud){
            mudProgress -= 0.5f;
        }
        boolean swim = !this.isOnGround() && this.isInWaterOrBubble();
        if(swimProgress < 5F && swim){
            swimProgress++;
        }
        if(swimProgress > 0 && !swim){
            swimProgress--;
        }
        if (!level.isClientSide) {
            if (isInWaterOrBubble()) {
                swimTimer++;
            } else {
                swimTimer--;
            }
        }
        if (displayCooldown > 0) {
            displayCooldown--;
        }
        if(!level.isClientSide){
            if(this.getDisplayAngle() < nextDisplayAngleFromServer){
                this.setDisplayAngle(this.getDisplayAngle() + 1);

            }
            if(this.getDisplayAngle() > nextDisplayAngleFromServer) {
                this.setDisplayAngle(this.getDisplayAngle() - 1);
            }
        }
        if(this.isMouthOpen()){
            this.openMouth(this.getMouthTicks() - 1);
        }
        if (this.isInWater() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!this.isInWater() && !this.isLandNavigator) {
            switchNavigator(true);
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev && source.getDirectEntity() instanceof LivingEntity) {
            this.openMouth(10);
        }
        return prev;
    }

    public boolean isDisplaying() {
        return this.entityData.get(DISPLAYING).booleanValue();
    }

    public void setDisplaying(boolean display) {
        this.entityData.set(DISPLAYING, display);
    }

    public float getDisplayAngle() {
        return this.entityData.get(DISPLAY_ANGLE);
    }

    public void setDisplayAngle(float scale) {
        this.entityData.set(DISPLAY_ANGLE, scale);
    }

    public int getMouthTicks() {
        return this.entityData.get(MOUTH_TICKS);
    }

    public void openMouth(int time) {
        this.entityData.set(MOUTH_TICKS, time);
    }

    @javax.annotation.Nullable
    public UUID getDisplayingPartnerUUID() {
        return this.entityData.get(DISPLAYER_UUID).orElse(null);
    }

    public void setDisplayingPartnerUUID(@javax.annotation.Nullable UUID uniqueId) {
        this.entityData.set(DISPLAYER_UUID, Optional.ofNullable(uniqueId));
    }

    @javax.annotation.Nullable
    public Entity getDisplayingPartner() {
        UUID id = getDisplayingPartnerUUID();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public void setDisplayingPartner(@javax.annotation.Nullable Entity jostlingPartner) {
        if (jostlingPartner == null) {
            this.setDisplayingPartnerUUID(null);
        } else {
            this.setDisplayingPartnerUUID(jostlingPartner.getUUID());
        }
    }

    public boolean canDisplayWith(EntityMudskipper mudskipper) {
        return !mudskipper.isBaby() && !mudskipper.isOrderedToSit() && !mudskipper.shouldFollow() && mudskipper.isOnGround() && mudskipper.getDisplayingPartnerUUID() == null && mudskipper.displayCooldown == 0;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return AMEntityRegistry.MUDSKIPPER.get().create(serverLevel);
    }

    public boolean isMouthOpen() {
        return this.getMouthTicks() > 0;
    }

    public boolean onMud() {
        BlockState below = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
        return below.is(Blocks.MUD);
    }

    public void calculateEntityAnimation(LivingEntity mob, boolean flying) {
        mob.animationSpeedOld = mob.animationSpeed;
        double d0 = mob.getX() - mob.xo;
        double d1 = flying ? mob.getY() - mob.yo : 0.0D;
        double d2 = mob.getZ() - mob.zo;
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 8.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        mob.animationSpeed += (f - mob.animationSpeed) * 0.4F;
        mob.animationPosition += mob.animationSpeed;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(AMSoundRegistry.MUDSKIPPER_WALK.get(), 1F, 1.0F);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MUDSKIPPER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MUDSKIPPER_HURT.get();
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isOrderedToSit() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    @Override
    public boolean shouldEnterWater() {
        return (this.getLastHurtByMob() != null || swimTimer <= -1000) && !this.isDisplaying();
    }

    @Override
    public boolean shouldLeaveWater() {
        return swimTimer > 200 || this.isDisplaying();
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isOrderedToSit();
    }

    @Override
    public int getWaterSearchRange() {
        return 10;
    }


    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean bucket) {
        this.entityData.set(FROM_BUCKET, bucket);
    }

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.MUDSKIPPER_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.put("MudskipperData", platTag);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("MudskipperData")) {
            this.readAdditionalSaveData(compound.getCompound("MudskipperData"));
        }
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.INSECT_ITEMS) || stack.getItem() == AMItemRegistry.LOBSTER_TAIL.get() || stack.getItem() == AMItemRegistry.COOKED_LOBSTER_TAIL.get();
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (!isTame() && (item == AMItemRegistry.LOBSTER_TAIL.get() || item == AMItemRegistry.COOKED_LOBSTER_TAIL.get())) {
            this.usePlayerItem(player, hand, itemstack);
            this.openMouth(10);
            this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (getRandom().nextInt(2) == 0) {
                this.tame(player);
                this.level.broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }
        if (isTame() && itemstack.is(AMTagRegistry.INSECT_ITEMS)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.usePlayerItem(player, hand, itemstack);
                this.openMouth(10);
                this.playSound(SoundEvents.STRIDER_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.heal(5);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (item != Items.WATER_BUCKET && interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack)) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
                this.setCommand(0);
            }
            player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
                this.setOrderedToSit(true);
                return InteractionResult.SUCCESS;
            } else {
                this.setOrderedToSit(false);
                return InteractionResult.SUCCESS;
            }
        }
        return Bucketable.bucketMobPickup(player, hand, this).orElse(type);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }

        return super.isAlliedTo(entityIn);
    }
}
