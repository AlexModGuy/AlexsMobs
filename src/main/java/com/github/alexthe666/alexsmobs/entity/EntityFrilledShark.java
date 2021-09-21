package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

public class EntityFrilledShark extends WaterAnimal implements IAnimatedEntity {

    public static final Animation ANIMATION_ATTACK = Animation.create(17);
    private static final EntityDataAccessor<Boolean> DEPRESSURIZED = SynchedEntityData.defineId(EntityFrilledShark.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityFrilledShark.class, EntityDataSerializers.BOOLEAN);
    public float prevOnLandProgress;
    public float onLandProgress;
    private int animationTick;
    private Animation currentAnimation;

    protected EntityFrilledShark(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AquaticMoveController(this, 1F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEPRESSURIZED, false);
        this.entityData.define(FROM_BUCKET, false);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(2, new AIMelee());
        this.goalSelector.addGoal(3, new AnimalAISwimBottom(this, 0.8F, 7));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8F, 3));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FollowBoatGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Squid.class, 40, false, true, null));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityMimicOctopus.class, 70, false, true, null));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractSchoolingFish.class, 100, false, true, null));
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, Drowned.class, 4, false, true, null));
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.frilledSharkSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canFrilledSharkSpawn(EntityType<EntityFrilledShark> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, Random random) {
        return reason == MobSpawnType.SPAWNER || iServerWorld.getBlockState(pos).getMaterial() == Material.WATER && iServerWorld.getBlockState(pos.above()).getMaterial() == Material.WATER;
    }

    private boolean isFromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean p_203706_1_) {
        this.entityData.set(FROM_BUCKET, p_203706_1_);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.isFromBucket());
        compound.putBoolean("Depressurized", this.isDepressurized());
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.isFromBucket();
    }

    public boolean removeWhenFarAway(double p_213397_1_) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setDepressurized(compound.getBoolean("Depressurized"));
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.blockPosition();
        while(!world.getFluidState(down).isEmpty() && down.getY() > 1){
            down = down.below();
        }
        this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public boolean isDepressurized() {
        return this.entityData.get(DEPRESSURIZED);
    }

    public void setDepressurized(boolean depressurized) {
        this.entityData.set(DEPRESSURIZED, depressurized);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COD_HURT;
    }

    protected ItemStack getFishBucket() {
        ItemStack stack = new ItemStack(AMItemRegistry.FRILLED_SHARK_BUCKET);
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        stack.getOrCreateTag().put("FrilledSharkData", platTag);
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    public InteractionResult mobInteract(Player p_230254_1_, InteractionHand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
        if (itemstack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0F, 1.0F);
            itemstack.shrink(1);
            ItemStack itemstack1 = this.getFishBucket();
            if (!this.level.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) p_230254_1_, itemstack1);
            }

            if (itemstack.isEmpty()) {
                p_230254_1_.setItemInHand(p_230254_2_, itemstack1);
            } else if (!p_230254_1_.inventory.add(itemstack1)) {
                p_230254_1_.drop(itemstack1, false);
            }

            this.remove();
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(p_230254_1_, p_230254_2_);
        }
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, 0.6D, 0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    @Override
    public void calculateEntityAnimation(LivingEntity p_233629_1_, boolean p_233629_2_) {
        p_233629_1_.animationSpeedOld = p_233629_1_.animationSpeed;
        double d0 = p_233629_1_.getX() - p_233629_1_.xo;
        double d1 = p_233629_1_.getY() - p_233629_1_.yo;
        double d2 = p_233629_1_.getZ() - p_233629_1_.zo;
        float f = Mth.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 8.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        p_233629_1_.animationSpeed += (f - p_233629_1_.animationSpeed) * 0.4F;
        p_233629_1_.animationPosition += p_233629_1_.animationSpeed;
    }

    public void tick() {
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        if (!this.isInWater() && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (this.isInWater() && onLandProgress > 0F) {
            onLandProgress--;
        }
        if (this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.8D, 1.0D));
        }
        boolean clear = hasClearance();
        if (this.isDepressurized() && clear) {
            this.setDepressurized(false);
        }
        if (!isDepressurized() && !clear) {
            this.setDepressurized(true);
        }
        if (!level.isClientSide && this.getTarget() != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 12) {
            float f1 = this.yRot * ((float) Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.06F, 0.0D, Mth.cos(f1) * 0.06F));
            if (this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue())){
                this.getTarget().addEffect(new MobEffectInstance(AMEffectRegistry.EXSANGUINATION, 60, 2));
                if(random.nextInt(15) == 0 && this.getTarget() instanceof Squid){
                    this.spawnAtLocation(AMItemRegistry.SERRATED_SHARK_TOOTH);
                }
            }

        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Drowned) {
            amount *= 0.5F;
        }
        return super.hurt(source, amount);
    }

    private boolean hasClearance() {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        for (int l1 = 0; l1 < 10; ++l1) {
            BlockState blockstate = level.getBlockState(blockpos$mutable.set(this.getX(), this.getY() + l1, this.getZ()));
            if (!blockstate.getFluidState().is(FluidTags.WATER)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    public boolean isKaiju() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && (s.toLowerCase().contains("kamata kun") || s.toLowerCase().contains("kamata-kun"));
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_ATTACK};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 68) {
            double d2 = this.random.nextGaussian() * 0.1D;
            double d0 = this.random.nextGaussian() * 0.1D;
            double d1 = this.random.nextGaussian() * 0.1D;
            float radius = this.getBbWidth() * 0.8F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            double x = this.getX() + extraX + d0;
            double y = this.getY() + this.getBbHeight() * 0.15F + d1;
            double z = this.getZ() + extraZ + d2;
            level.addParticle(AMParticleRegistry.TEETH_GLINT, x, y, z, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private class AIMelee extends Goal {

        public AIMelee() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntityFrilledShark.this.getTarget() != null && EntityFrilledShark.this.getTarget().isAlive();
        }

        public void tick() {
            LivingEntity target = EntityFrilledShark.this.getTarget();
            double speed = 1.0F;
            boolean move = true;
            if (EntityFrilledShark.this.distanceTo(target) < 10) {
                if (EntityFrilledShark.this.distanceTo(target) < 1.9D) {
                    EntityFrilledShark.this.doHurtTarget(target);
                    speed = 0.8F;
                } else {
                    speed = 0.6F;
                    EntityFrilledShark.this.lookAt(target, 70, 70);
                    if (target instanceof Squid) {
                        Vec3 mouth = EntityFrilledShark.this.position();
                        float squidSpeed = 0.07F;
                        ((Squid) target).setMovementVector((float) (mouth.x - target.getX()) * squidSpeed, (float) (mouth.y - target.getEyeY()) * squidSpeed, (float) (mouth.z - target.getZ()) * squidSpeed);
                        EntityFrilledShark.this.level.broadcastEntityEvent(EntityFrilledShark.this, (byte) 68);
                    }
                }
            }
            if (target instanceof Drowned || target instanceof Player) {
                speed = 1.0F;
            }
            EntityFrilledShark.this.getNavigation().moveTo(target, speed);
        }
    }
}
