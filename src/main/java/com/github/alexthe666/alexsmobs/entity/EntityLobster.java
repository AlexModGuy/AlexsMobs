package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityLobster extends WaterMobEntity implements ISemiAquatic {

    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.createKey(EntityLobster.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.createKey(EntityLobster.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityLobster.class, DataSerializers.VARINT);
    public float attackProgress;
    public float prevAttackProgress;
    private int attackCooldown = 0;

    protected EntityLobster(EntityType type, World p_i48565_2_) {
        super(type, p_i48565_2_);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
    }

    public int getMaxSpawnedInChunk() {
        return 7;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 5D).createMutableAttribute(Attributes.ARMOR, 2.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.lobsterSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.LOBSTER_HURT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.LOBSTER_HURT;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public static String getVariantName(int variant) {
        switch (variant) {
            case 1:
                return "blue";
            case 2:
                return "yellow";
            case 3:
                return "redblue";
            case 4:
                return "black";
            case 5:
                return "white";
            default:
                return "red";
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(3, new BottomFeederAIWander(this, 1.0D, 10, 50));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            if(this.isJumping){
                this.setMotion(this.getMotion().scale(1.4D));
                this.setMotion(this.getMotion().add(0.0D, 0.72D, 0.0D));
            }else{
                this.setMotion(this.getMotion().scale(0.4D));
                this.setMotion(this.getMotion().add(0.0D, -0.08D, 0.0D));
            }

        } else {
            super.travel(travelVector);
        }

    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, 0);
        this.dataManager.register(ATTACK_TICK, 0);
        this.dataManager.register(FROM_BUCKET, false);
    }

    protected ItemStack getFishBucket(){
        ItemStack stack = new ItemStack(AMItemRegistry.LOBSTER_BUCKET);
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
        compoundnbt.putInt("BucketVariantTag", this.getVariant());
    }

    public boolean preventDespawn() {
        return super.preventDespawn() || this.isFromBucket();
    }

    protected ActionResultType getEntityInteractionResult(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
        if (itemstack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
            itemstack.shrink(1);
            ItemStack itemstack1 = this.getFishBucket();
            this.setBucketData(itemstack1);
            if (!this.world.isRemote) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)p_230254_1_, itemstack1);
            }

            if (itemstack.isEmpty()) {
                p_230254_1_.setHeldItem(p_230254_2_, itemstack1);
            } else if (!p_230254_1_.inventory.addItemStackToInventory(itemstack1)) {
                p_230254_1_.dropItem(itemstack1, false);
            }

            this.remove();
            return ActionResultType.func_233537_a_(this.world.isRemote);
        } else {
            return super.getEntityInteractionResult(p_230254_1_, p_230254_2_);
        }
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return worldIn.getFluidState(pos.down()).isEmpty() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER) ? 10.0F : super.getBlockPathWeight(pos, worldIn);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.dataManager.set(ATTACK_TICK, 5);
        return super.attackEntityAsMob(entityIn);
    }

    public void tick() {
        super.tick();
        prevAttackProgress = attackProgress;
        if (this.dataManager.get(ATTACK_TICK) > 0) {
            if(attackProgress == 3){
                this.playSound(AMSoundRegistry.LOBSTER_ATTACK, this.getSoundVolume(), this.getSoundPitch());
            }
            if (this.dataManager.get(ATTACK_TICK) == 2 && this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < 1.3D) {
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
        if(attackCooldown > 0){
            attackCooldown--;
        }
        if(this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) <= 1F && attackCooldown == 0){
            this.faceEntity(this.getAttackTarget(), 180F, 20F);
            attackEntityAsMob(this.getAttackTarget());
            attackCooldown = 20;
        }
    }

    protected void updateAir(int air) {

    }

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("FromBucket", this.isFromBucket());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setFromBucket(compound.getBoolean("FromBucket"));
    }


    private boolean isFromBucket() {
        return this.dataManager.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean p_203706_1_) {
        this.dataManager.set(FROM_BUCKET, p_203706_1_);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        float variantChange = this.getRNG().nextFloat();
        if(variantChange <= 0.000001){
            this.setVariant(5);
        }else if(variantChange <= 0.000002){
            this.setVariant(4);
        }else if(variantChange <= 0.05F){
            this.setVariant(3);
        }else if(variantChange <= 0.1F){
            this.setVariant(2);
        }else if(variantChange <= 0.25F){
            this.setVariant(1);
        }else{
            this.setVariant(0);
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected PathNavigator createNavigator(World worldIn) {
        SemiAquaticPathNavigator flyingpathnavigator = new SemiAquaticPathNavigator(this, worldIn) {
            public boolean canEntityStandOnPos(BlockPos pos) {
                return this.world.getBlockState(pos).getFluidState().isEmpty();
            }
        };
        return flyingpathnavigator;
    }

    @Override
    public boolean shouldEnterWater() {
        return true;
    }

    @Override
    public boolean shouldLeaveWater() {
        return false;
    }

    @Override
    public boolean shouldStopMoving() {
        return false;
    }

    @Override
    public int getWaterSearchRange() {
        return 5;
    }

    public static <T extends MobEntity> boolean canLobsterSpawn(EntityType type, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        ITag<Block> tag = BlockTags.getCollection().get(AMTagRegistry.LOBSTER_SPAWNS);
        boolean spawnBlock = tag != null && tag.contains(worldIn.getBlockState(pos.down()).getBlock());
        return tag == null || spawnBlock || worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
    }
}
