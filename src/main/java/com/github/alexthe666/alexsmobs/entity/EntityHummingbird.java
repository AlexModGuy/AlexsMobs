package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIWander;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityHummingbird extends AnimalEntity {

    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityHummingbird.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityHummingbird.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CROPS_POLLINATED = EntityDataManager.createKey(EntityHummingbird.class, DataSerializers.VARINT);
    public float flyProgress;
    public float prevFlyProgress;
    public float movingProgress;
    public float prevMovingProgress;
    public int hummingStill = 0;
    public int pollinateCooldown = 0;
    private int loopSoundTick = 0;

    protected EntityHummingbird(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new MoveHelper(this, 90, false);
        this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathPriority(PathNodeType.COCOA, -1.0F);
        this.setPathPriority(PathNodeType.FENCE, -1.0F);
        this.setPathPriority(PathNodeType.LEAVES, 0.0F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.hummingbirdSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.HUMMINGBIRD_IDLE;
    }

    public int getTalkInterval() {
        return 60;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.HUMMINGBIRD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.HUMMINGBIRD_HURT;
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 4.0D).createMutableAttribute(Attributes.FLYING_SPEED, 7F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.45F);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem().isIn(ItemTags.FLOWERS);
    }

    public int getMaxSpawnedInChunk() {
        return 7;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BreedGoal(this, 1));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1));
        this.goalSelector.addGoal(4, new HummingbirdAIPollinate(this));
        this.goalSelector.addGoal(5, new HummingbirdAIWander(this, 16, 6, 30, 1));
        this.goalSelector.addGoal(6, new SwimGoal(this));
    }

    protected PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
            public boolean canEntityStandOnPos(BlockPos pos) {
                return !this.world.getBlockState(pos.down(2)).isAir();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(false);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected boolean makeFlySound() {
        return true;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isChild() ? sizeIn.height * 0.5F : sizeIn.height * 0.5F;
    }


    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("CropsPollinated", this.getCropsPollinated());
        compound.putInt("PollinateCooldown", this.pollinateCooldown);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setCropsPollinated(compound.getInt("CropsPollinated"));
        this.pollinateCooldown = compound.getInt("PollinateCooldown");
    }


    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, Boolean.valueOf(false));
        this.dataManager.register(VARIANT, 0);
        this.dataManager.register(CROPS_POLLINATED, 0);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setVariant(this.getRNG().nextInt(3));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING).booleanValue();
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, Boolean.valueOf(flying));
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public int getCropsPollinated() {
        return this.dataManager.get(CROPS_POLLINATED).intValue();
    }

    public void setCropsPollinated(int crops) {
        this.dataManager.set(CROPS_POLLINATED, Integer.valueOf(crops));
    }

    public void tick() {
        super.tick();
        Vector3d vector3d = this.getMotion();
        boolean flag = this.getMotion().x * this.getMotion().x + this.getMotion().z * this.getMotion().z >= 1.0E-3D;
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setMotion(vector3d.mul(1.0D, 0.4D, 1.0D));
        }
        this.setFlying(true);
        this.setNoGravity(true);
        if (this.isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!this.isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (flag && movingProgress < 5F) {
            movingProgress++;
        }
        if (!flag && movingProgress > 0F) {
            movingProgress--;
        }
        if(this.getMotion().lengthSquared() < 1.0E-7D){
            hummingStill++;
        }else{
            hummingStill = 0;
        }
        if(pollinateCooldown > 0){
            pollinateCooldown--;
        }
        if(loopSoundTick == 0){
            this.playSound(AMSoundRegistry.HUMMINGBIRD_LOOP, this.getSoundVolume() * 0.33F, this.getSoundPitch());
        }
        loopSoundTick++;
        if(loopSoundTick > 27){
            loopSoundTick = 0;
        }
        prevFlyProgress = flyProgress;
        prevMovingProgress = movingProgress;
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.HUMMINGBIRD.create(serverWorld);
    }

    private static class MoveHelper extends MovementController {
        private final int field_226323_i_;
        private final boolean field_226324_j_;

        public MoveHelper(MobEntity p_i225710_1_, int p_i225710_2_, boolean p_i225710_3_) {
            super(p_i225710_1_);
            this.field_226323_i_ = p_i225710_2_;
            this.field_226324_j_ = p_i225710_3_;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                this.action = MovementController.Action.WAIT;
                double d0 = this.posX - this.mob.getPosX();
                double d1 = this.posY - this.mob.getPosY();
                double d2 = this.posZ - this.mob.getPosZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double) 2.5000003E-7F) {
                    this.mob.setMoveVertical(0.0F);
                    this.mob.setMoveForward(0.0F);
                    return;
                }

                float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, f, 90.0F);
                float f1 = (float)(this.speed * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                mob.setMotion(mob.getMotion().add(d0 / d3 * 0.5D * this.speed, d1 / d3 * 0.5D * this.speed, d2 / d3 * 0.5D * this.speed));
                double d4 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
                float f2 = (float) (-(MathHelper.atan2(d1, d4) * (double) (180F / (float) Math.PI)));
                this.mob.rotationPitch = this.limitAngle(this.mob.rotationPitch, f2, (float) this.field_226323_i_);
            } else {
                this.mob.setNoGravity(true);
                this.mob.setMoveVertical(0.0F);
                this.mob.setMoveForward(0.0F);
            }

        }
    }

    public static <T extends MobEntity> boolean canHummingbirdSpawn(EntityType<EntityHummingbird> hummingbird, IWorld worldIn, SpawnReason reason, BlockPos p_223317_3_, Random random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.down());
        return (blockstate.isIn(BlockTags.LEAVES) || blockstate.matchesBlock(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LOGS) || blockstate.matchesBlock(Blocks.AIR)) && worldIn.getLightSubtracted(p_223317_3_, 0) > 8;
    }
}
