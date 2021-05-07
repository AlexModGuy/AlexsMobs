package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.StraddlerAIShoot;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;
import java.util.Set;

public class EntityStraddler extends MonsterEntity implements IAnimatedEntity {

    public static final Animation ANIMATION_LAUNCH = Animation.create(30);
    private static final DataParameter<Integer> STRADPOLE_COUNT = EntityDataManager.createKey(EntityStraddler.class, DataSerializers.VARINT);
    private int animationTick;
    private Animation currentAnimation;

    protected EntityStraddler(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.LAVA, 0.0F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.STRADDLER_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.STRADDLER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.STRADDLER_HURT;
    }

    public static boolean canStraddlerSpawn(EntityType animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.BASE_STONE_NETHER.contains(worldIn.getBlockState(pos.down()).getBlock());
        return spawnBlock;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 28.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.8D).createMutableAttribute(Attributes.ARMOR, 5.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(STRADPOLE_COUNT, 0);
    }

    public int getStradpoleCount() {
        return this.dataManager.get(STRADPOLE_COUNT);
    }

    public void setStradpoleCount(int index) {
        this.dataManager.set(STRADPOLE_COUNT, index);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.straddlerSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StraddlerAIShoot(this, 0.5F, 30, 16));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0D, 60));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(9, new LookAtGoal(this, StriderEntity.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
    }

    protected void updateFallState(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
        this.doBlockCollisions();
        if (this.isInLava()) {
            this.fallDistance = 0.0F;
        } else {
            super.updateFallState(p_184231_1_, p_184231_3_, p_184231_4_, p_184231_5_);
        }
    }

    public void travel(Vector3d travelVector) {
        this.setAIMoveSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.getAnimation() == ANIMATION_LAUNCH ? 0.5F : 1F) * (isInLava() ? 0.2F : 1F));
        if (this.isServerWorld() && (this.isInWater() || this.isInLava())) {
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

    private void func_234318_eL_() {
        if (this.isInLava()) {
            ISelectionContext lvt_1_1_ = ISelectionContext.forEntity(this);
            if (lvt_1_1_.func_216378_a(FlowingFluidBlock.LAVA_COLLISION_SHAPE, this.getPosition().down(), true) && !this.world.getFluidState(this.getPosition().up()).isTagged(FluidTags.LAVA)) {
                this.onGround = true;
            } else {
                this.setMotion(this.getMotion().scale(0.5D).add(0.0D, rand.nextFloat() * 0.5, 0.0D));
            }
        }

    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    protected float determineNextStepDistance() {
        return this.distanceWalkedOnStepModified + 0.6F;
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        if (worldIn.getBlockState(pos).getFluidState().isTagged(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    public Vector3d func_230268_c_(LivingEntity livingEntity) {
        Vector3d[] avector3d = new Vector3d[]{func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw), func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw - 22.5F), func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw + 22.5F), func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw - 45.0F), func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw + 45.0F)};
        Set<BlockPos> set = Sets.newLinkedHashSet();
        double d0 = this.getBoundingBox().maxY;
        double d1 = this.getBoundingBox().minY - 0.5D;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Vector3d vector3d : avector3d) {
            blockpos$mutable.setPos(this.getPosX() + vector3d.x, d0, this.getPosZ() + vector3d.z);

            for (double d2 = d0; d2 > d1; --d2) {
                set.add(blockpos$mutable.toImmutable());
                blockpos$mutable.move(Direction.DOWN);
            }
        }

        for (BlockPos blockpos : set) {
            if (!this.world.getFluidState(blockpos).isTagged(FluidTags.LAVA)) {
                double d3 = this.world.func_242403_h(blockpos);
                if (TransportationHelper.func_234630_a_(d3)) {
                    Vector3d vector3d1 = Vector3d.copyCenteredWithVerticalOffset(blockpos, d3);

                    for (Pose pose : livingEntity.getAvailablePoses()) {
                        AxisAlignedBB axisalignedbb = livingEntity.getPoseAABB(pose);
                        if (TransportationHelper.func_234631_a_(this.world, livingEntity, axisalignedbb.offset(vector3d1))) {
                            livingEntity.setPose(pose);
                            return vector3d1;
                        }
                    }
                }
            }
        }

        return new Vector3d(this.getPosX(), this.getBoundingBox().maxY, this.getPosZ());
    }

    public boolean isBurning() {
        return false;
    }

    public boolean func_230285_a_(Fluid p_230285_1_) {
        return p_230285_1_.isIn(FluidTags.LAVA);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("StradpoleCount", getStradpoleCount());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setStradpoleCount(compound.getInt("StradpoleCount"));
    }

    public void tick() {
        super.tick();
        this.func_234318_eL_();
        this.doBlockCollisions();
        if (this.getAnimation() == ANIMATION_LAUNCH && this.isAlive()){
            if(this.getAnimationTick() == 2){
                this.playSound(SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE, 2F, 1F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            }
        }
        if (this.getAnimation() == ANIMATION_LAUNCH && this.isAlive() && this.getAnimationTick() == 20 && this.getAttackTarget() != null) {
            EntityStradpole pole = AMEntityRegistry.STRADPOLE.create(world);
            pole.setParentId(this.getUniqueID());
            pole.setPosition(this.getPosX(), this.getPosYEye(), this.getPosZ());
            double d0 = this.getAttackTarget().getPosYEye() - (double)1.1F;
            double d1 = this.getAttackTarget().getPosX() - this.getPosX();
            double d2 = d0 - pole.getPosY();
            double d3 = this.getAttackTarget().getPosZ() - this.getPosZ();
            float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.4F;
            float f3 = MathHelper.sqrt(d1 * d1 + d2 * d2 + d3 * d3) * 0.2F;
            this.playSound(SoundEvents.ITEM_CROSSBOW_LOADING_END, 2F, 1F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            pole.shoot(d1, d2 + (double)f3, d3, 2F, 0F);
            pole.rotationYaw = this.rotationYaw % 360.0F;
            pole.rotationPitch = MathHelper.clamp(this.rotationYaw, -90.0F, 90.0F) % 360.0F;
            if(!world.isRemote){
                this.world.addEntity(pole);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
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
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_LAUNCH};
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new LavaPathNavigator(this, worldIn);
    }

    public boolean shouldShoot() {
        return true;
    }

    static class LavaPathNavigator extends GroundPathNavigator {
        LavaPathNavigator(EntityStraddler p_i231565_1_, World p_i231565_2_) {
            super(p_i231565_1_, p_i231565_2_);
        }

        protected PathFinder getPathFinder(int p_179679_1_) {
            this.nodeProcessor = new WalkNodeProcessor();
            return new PathFinder(this.nodeProcessor, p_179679_1_);
        }

        protected boolean func_230287_a_(PathNodeType p_230287_1_) {
            return p_230287_1_ == PathNodeType.LAVA || p_230287_1_ == PathNodeType.DAMAGE_FIRE || p_230287_1_ == PathNodeType.DANGER_FIRE || super.func_230287_a_(p_230287_1_);
        }

        public boolean canEntityStandOnPos(BlockPos pos) {
            return this.world.getBlockState(pos).isIn(Blocks.LAVA) || super.canEntityStandOnPos(pos);
        }
    }
}
