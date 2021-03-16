package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class EntityFly extends AnimalEntity implements IFlyingAnimal {

    private int conversionTime = 0;

    protected EntityFly(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new FlyingMovementController(this, 20, true);
    }

    public static boolean canFlySpawn(EntityType<EntityFly> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        return worldIn.getLightSubtracted(pos, 0) > 8;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.flySpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public boolean isInNether() {
        return this.world.getDimensionKey() == World.THE_NETHER && !this.isAIDisabled();
    }


    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.FLY_IDLE;
    }

    public int getTalkInterval() {
        return 30;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.FLY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.FLY_HURT;
    }

    public int getMaxSpawnedInChunk() {
        return 2;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 2.0D).createMutableAttribute(Attributes.FLYING_SPEED, 0.8F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25D, Ingredient.fromItems(Items.ROTTEN_FLESH), false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, SpiderEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new AnnoyZombieGoal());
        this.goalSelector.addGoal(5, new WanderGoal());
        this.goalSelector.addGoal(6, new SwimGoal(this));
    }

    protected PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
            public boolean canEntityStandOnPos(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(false);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isChild() ? sizeIn.height * 0.5F : sizeIn.height * 0.5F;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        fallDistance = 0;
    }

    public void tick(){
        super.tick();
        if(this.isChild() && this.getEyeHeight() > this.getHeight()){
            this.recalculateSize();
        }
        if(isInNether()){
            conversionTime++;
            if(conversionTime > 300){
                EntityCrimsonMosquito mosquito = AMEntityRegistry.CRIMSON_MOSQUITO.create(world);
                mosquito.copyLocationAndAnglesFrom(this);
                if(!world.isRemote){
                    mosquito.onInitialSpawn((IServerWorld)world, world.getDifficultyForLocation(this.getPosition()), SpawnReason.CONVERSION, null, null);
                }
                world.addEntity(mosquito);
                mosquito.onSpawnFromFly();
                this.remove();
            }
        }
    }

    protected boolean makeFlySound() {
        return true;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    protected void handleFluidJump(ITag<Fluid> fluidTag) {
        this.setMotion(this.getMotion().add(0.0D, 0.01D, 0.0D));
    }

    @OnlyIn(Dist.CLIENT)
    public Vector3d func_241205_ce_() {
        return new Vector3d(0.0D, 0.5F * this.getEyeHeight(), this.getWidth() * 0.2F);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.FLY.create(p_241840_1_);
    }

    class WanderGoal extends Goal {
        WanderGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return EntityFly.this.navigator.noPath() && EntityFly.this.rand.nextInt(3) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EntityFly.this.navigator.hasPath();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            Vector3d vector3d = this.getRandomLocation();
            if (vector3d != null) {
                EntityFly.this.navigator.setPath(EntityFly.this.navigator.getPathToPos(new BlockPos(vector3d), 1), 1.0D);
            }

        }

        @Nullable
        private Vector3d getRandomLocation() {
            Vector3d vector3d = EntityFly.this.getLook(0.0F);
            int i = 8;
            Vector3d vector3d2 = RandomPositionGenerator.findAirTarget(EntityFly.this, 3, 3, vector3d, ((float) Math.PI / 2F), 1, 1);
            return vector3d2 != null ? vector3d2 : RandomPositionGenerator.findGroundTarget(EntityFly.this, 3, 3, -3, vector3d, (float) Math.PI / 2F);
        }
    }

    private class AnnoyZombieGoal extends Goal {
        protected final Sorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private int cooldown = 0;
        private ITag tag;

        AnnoyZombieGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            tag = EntityTypeTags.getCollection().get(AMTagRegistry.FLY_TARGETS);
            this.theNearestAttackableTargetSorter = new Sorter(EntityFly.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && e.getType().isContained(tag) && (!(e instanceof LivingEntity) || ((LivingEntity) e).getHealth() >= 2D);
                }
            };
        }

        @Override
        public boolean shouldExecute() {
            if (EntityFly.this.isPassenger() || EntityFly.this.isBeingRidden()) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityFly.this.world.getGameTime() % 10;
                if (EntityFly.this.getIdleTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityFly.this.getRNG().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntityFly.this.world.getEntitiesWithinAABB(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                return true;
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return targetEntity != null;
        }

        public void resetTask() {
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if(cooldown > 0){
                cooldown--;
            }
            if(targetEntity != null){
                if (EntityFly.this.getNavigator().noPath()) {
                    int i = EntityFly.this.getRNG().nextInt(3) - 1;
                    int k = EntityFly.this.getRNG().nextInt(3) - 1;
                    int l = (int) ((EntityFly.this.getRNG().nextInt(3) - 1) * Math.ceil(targetEntity.getHeight()));
                    EntityFly.this.getNavigator().tryMoveToXYZ(this.targetEntity.getPosX() + i, this.targetEntity.getPosY() + l, this.targetEntity.getPosZ() + k, 1);
                }
                if(EntityFly.this.getDistanceSq(targetEntity) < 3.0F){
                    if(targetEntity instanceof LivingEntity && ((LivingEntity) targetEntity).getHealth() > 2D){
                        if(cooldown == 0){
                            targetEntity.attackEntityFrom(DamageSource.GENERIC, 1);
                            cooldown = 100;
                        }
                    }else{
                        this.resetTask();
                    }

                }
            }
        }

        protected double getTargetDistance() {
            return 16D;
        }

        protected AxisAlignedBB getTargetableArea(double targetDistance) {
            Vector3d renderCenter = new Vector3d(EntityFly.this.getPosX() + 0.5, EntityFly.this.getPosY() + 0.5, EntityFly.this.getPosZ() + 0.5D);
            double renderRadius = 5;
            AxisAlignedBB aabb = new AxisAlignedBB(-renderRadius, -renderRadius, -renderRadius, renderRadius, renderRadius, renderRadius);
            return aabb.offset(renderCenter);
        }


        public class Sorter implements Comparator<Entity> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_) {
                double d0 = this.theEntity.getDistanceSq(p_compare_1_);
                double d1 = this.theEntity.getDistanceSq(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
        }
    }
}
