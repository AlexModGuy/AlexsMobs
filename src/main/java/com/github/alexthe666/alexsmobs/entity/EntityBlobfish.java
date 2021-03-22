package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAISwimBottom;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FindWaterGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityBlobfish extends WaterMobEntity implements IFlyingAnimal {

    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.createKey(EntityBlobfish.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> BLOBFISH_SCALE = EntityDataManager.createKey(EntityBlobfish.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> DEPRESSURIZED = EntityDataManager.createKey(EntityBlobfish.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SLIMED = EntityDataManager.createKey(EntityBlobfish.class, DataSerializers.BOOLEAN);
    public float squishFactor;
    public float prevSquishFactor;
    public float squishAmount;
    private boolean wasOnGround;

    protected EntityBlobfish(EntityType type, World world) {
        super(type, world);
        this.moveController = new AquaticMoveController(this, 1.0F);
    }

    public static AttributeModifierMap.MutableAttribute func_234176_m_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 3.0D);
    }


    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.blobfishSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public static boolean func_223363_b(EntityType<? extends AbstractFishEntity> p_223363_0_, IWorld p_223363_1_, SpawnReason p_223363_2_, BlockPos p_223363_3_, Random p_223363_4_) {
        return p_223363_1_.getBlockState(p_223363_3_).isIn(Blocks.WATER) && p_223363_1_.getBlockState(p_223363_3_.up()).isIn(Blocks.WATER);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new SwimmerPathNavigator(this, worldIn);
    }

    protected void updateAir(int p_209207_1_) {
        if (this.isAlive() && !this.isInWaterOrBubbleColumn() && !isSlimed()) {
            this.setAir(p_209207_1_ - 1);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.attackEntityFrom(DamageSource.DROWN, rand.nextInt(2) == 0 ? 1F : 0F);
            }
        } else {
            this.setAir(2000);
        }
    }

    protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
        return p_213348_2_.height * 0.65F;
    }

    public boolean preventDespawn() {
        return super.preventDespawn() || this.isFromBucket() || isSlimed();
    }

    public boolean canDespawn(double p_213397_1_) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    public int getMaxSpawnedInChunk() {
        return 4;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(FROM_BUCKET, false);
        this.dataManager.register(BLOBFISH_SCALE, 1.0F);
        this.dataManager.register(DEPRESSURIZED, false);
        this.dataManager.register(SLIMED, false);
    }

    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn).scale(this.getBlobfishScale());
    }


    private boolean isFromBucket() {
        return this.dataManager.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean p_203706_1_) {
        this.dataManager.set(FROM_BUCKET, p_203706_1_);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("FromBucket", this.isFromBucket());
        compound.putBoolean("Depressurized", this.isDepressurized());
        compound.putBoolean("Slimed", this.isSlimed());
        compound.putFloat("BlobfishScale", this.getBlobfishScale());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setDepressurized(compound.getBoolean("Depressurized"));
        this.setSlimed(compound.getBoolean("Slimed"));
        this.setBlobfishScale(compound.getFloat("BlobfishScale"));
    }

    private boolean hasClearance() {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int l1 = 0; l1 < 10; ++l1) {
            BlockState blockstate = world.getBlockState(blockpos$mutable.setPos(this.getPosX(), this.getPosY() + l1, this.getPosZ()));
            if (!blockstate.getFluidState().isTagged(FluidTags.WATER)) {
                return false;
            }
        }

        return true;
    }


    public float getBlobfishScale() {
        return this.dataManager.get(BLOBFISH_SCALE);
    }

    public void setBlobfishScale(float scale) {
        this.dataManager.set(BLOBFISH_SCALE, scale);
    }

    public boolean isDepressurized() {
        return this.dataManager.get(DEPRESSURIZED);
    }

    public void setDepressurized(boolean depressurized) {
        this.dataManager.set(DEPRESSURIZED, depressurized);
    }

    public boolean isSlimed() {
        return this.dataManager.get(SLIMED);
    }

    public void setSlimed(boolean slimed) {
        this.dataManager.set(SLIMED, slimed);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FindWaterGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(3, new AnimalAISwimBottom(this, 1F, 7));
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

    protected ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack lvt_3_1_ = p_230254_1_.getHeldItem(p_230254_2_);
        if (lvt_3_1_.getItem() == Items.SLIME_BALL && this.isAlive() && !this.isSlimed()) {
            this.setSlimed(true);
            for (int i = 0; i < 6 + rand.nextInt(3); i++) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, lvt_3_1_), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
            }
            lvt_3_1_.shrink(1);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        if (lvt_3_1_.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
            lvt_3_1_.shrink(1);
            ItemStack lvt_4_1_ = this.getFishBucket();
            this.setBucketData(lvt_4_1_);
            if (!this.world.isRemote) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) p_230254_1_, lvt_4_1_);
            }

            if (lvt_3_1_.isEmpty()) {
                p_230254_1_.setHeldItem(p_230254_2_, lvt_4_1_);
            } else if (!p_230254_1_.inventory.addItemStackToInventory(lvt_4_1_)) {
                p_230254_1_.dropItem(lvt_4_1_, false);
            }

            this.remove();
            return ActionResultType.func_233537_a_(this.world.isRemote);
        } else {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_FISH_SWIM;
    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
    }

    protected ItemStack getFishBucket(){
        ItemStack stack = new ItemStack(AMItemRegistry.BLOBFISH_BUCKET);
        if (this.hasCustomName()) {
            stack.setDisplayName(this.getCustomName());
        }
        return stack;
    }

    protected void setBucketData(ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setDisplayName(this.getCustomName());
        }
        CompoundNBT compoundnbt = bucket.getOrCreateTag();
        compoundnbt.putFloat("BucketScale", this.getBlobfishScale());
        compoundnbt.putBoolean("Slimed", this.isSlimed());
    }


    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setBlobfishScale(0.75F + rand.nextFloat() * 0.5F);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void tick() {
        super.tick();
        this.prevSquishFactor = this.squishFactor;
        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;

        float f2 = (float) -((float) this.getMotion().y * 2.2F * (double) (180F / (float) Math.PI));
        this.rotationPitch = f2;
        if (!isInWater()) {
            if (this.onGround && !this.wasOnGround) {
                this.squishAmount = -0.35F;
            } else if (!this.onGround && this.wasOnGround) {
                this.squishAmount = 2F;
            }
        }
        this.wasOnGround = this.onGround;

        this.alterSquishAmount();
        boolean clear = hasClearance();
        if (this.isDepressurized() && clear) {
            this.setDepressurized(false);
        }
        if(!isDepressurized() && !clear){
            this.setDepressurized(true);
        }
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    public static boolean canBlobfishSpawn(EntityType<EntityBlobfish> entityType, IServerWorld iServerWorld, SpawnReason reason, BlockPos pos, Random random) {
        return reason == SpawnReason.SPAWNER || pos.getY() <= AMConfig.blobfishSpawnHeight && iServerWorld.getBlockState(pos).isIn(Blocks.WATER) && iServerWorld.getBlockState(pos.up()).isIn(Blocks.WATER);
    }
}
