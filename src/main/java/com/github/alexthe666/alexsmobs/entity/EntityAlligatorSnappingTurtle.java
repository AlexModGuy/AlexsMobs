package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.Predicate;

public class EntityAlligatorSnappingTurtle extends AnimalEntity implements ISemiAquatic, IShearable, net.minecraftforge.common.IForgeShearable {

    public static final Predicate<LivingEntity> TARGET_PRED = (animal) -> {
        return !(animal instanceof EntityAlligatorSnappingTurtle);
    };
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityAlligatorSnappingTurtle.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> MOSS = EntityDataManager.createKey(EntityAlligatorSnappingTurtle.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> WAITING = EntityDataManager.createKey(EntityAlligatorSnappingTurtle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ATTACK_TARGET_FLAG = EntityDataManager.createKey(EntityAlligatorSnappingTurtle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LUNGE_FLAG = EntityDataManager.createKey(EntityAlligatorSnappingTurtle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> TURTLE_SCALE = EntityDataManager.createKey(EntityAlligatorSnappingTurtle.class, DataSerializers.FLOAT);
    public float openMouthProgress;
    public float prevOpenMouthProgress;
    public float attackProgress;
    public float prevAttackProgress;
    public int chaseTime = 0;
    private int biteTick = 0;
    private int waitTime = 0;
    private int timeUntilWait = 0;
    private int mossTime = 0;

    protected EntityAlligatorSnappingTurtle(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        stepHeight = 1F;
    }

    public float getRenderScale() {
        return this.isChild() ? 0.3F : 1.0F;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.7D).createMutableAttribute(Attributes.ARMOR, 8D).createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, false));
        this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(3, new BottomFeederAIWander(this, 1.0D, 120, 150, 10));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this){
            public boolean shouldContinueExecuting(){
                return chaseTime < 0 ? false : super.shouldContinueExecuting();
            }
        }));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 2, false, true, TARGET_PRED) {
            protected AxisAlignedBB getTargetableArea(double targetDistance) {
                return this.goalOwner.getBoundingBox().grow(0.5D, 2D, 0.5D);
            }
        });
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.COD;
    }

    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        return true;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CLIMBING, (byte) 0);
        this.dataManager.register(MOSS, 0);
        this.dataManager.register(TURTLE_SCALE, 1F);
        this.dataManager.register(WAITING, false);
        this.dataManager.register(ATTACK_TARGET_FLAG, false);
        this.dataManager.register(LUNGE_FLAG, false);
    }

    public void tick() {
        super.tick();
        prevOpenMouthProgress = openMouthProgress;
        prevAttackProgress = attackProgress;
        boolean attack = this.dataManager.get(LUNGE_FLAG);
        boolean open = this.isWaiting() || this.dataManager.get(ATTACK_TARGET_FLAG) && !attack;
        if (attack && attackProgress < 5) {
            attackProgress++;
        }
        if (!attack && attackProgress > 0) {
            attackProgress--;
        }
        if (open && openMouthProgress < 5) {
            openMouthProgress++;
        }
        if (!open && openMouthProgress > 0) {
            openMouthProgress--;
        }
        if (this.attackProgress == 4 && this.getAttackTarget() != null && this.canEntityBeSeen(this.getAttackTarget()) && this.getDistance(this.getAttackTarget()) < 2.3F) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
        if (this.attackProgress > 4) {
            biteTick = 5;
        }
        if (biteTick > 0) {
            biteTick--;
        }
        if(chaseTime < 0){
            chaseTime++;
        }
        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally && this.isInWater());
            if (this.isWaiting()) {
                waitTime++;
                timeUntilWait = 1500;
                if (waitTime > 1500 || this.getAttackTarget() != null) {
                    this.setWaiting(false);
                }
            } else {
                timeUntilWait--;
                waitTime = 0;
            }
            if ((this.getAttackTarget() == null || !this.getAttackTarget().isAlive()) && timeUntilWait <= 0 && this.isInWater()) {
                this.setWaiting(true);
            }
            if (this.getAttackTarget() != null && biteTick == 0) {
                this.setWaiting(false);
                chaseTime++;
                this.dataManager.set(ATTACK_TARGET_FLAG, true);
                if (this.canEntityBeSeen(this.getAttackTarget()) && this.getDistance(this.getAttackTarget()) < 2.3F && openMouthProgress > 4) {
                    this.dataManager.set(LUNGE_FLAG, true);

                }
                if (this.getDistance(this.getAttackTarget()) > (this.getAttackTarget() instanceof PlayerEntity ? 5 : 10) && chaseTime > 40) {
                    chaseTime = -50;
                    this.setAttackTarget(null);
                    this.setRevengeTarget(null);
                    this.setLastAttackedEntity(null);
                    this.attackingPlayer = null;
                }
            } else {
                this.dataManager.set(ATTACK_TARGET_FLAG, false);
                this.dataManager.set(LUNGE_FLAG, false);
            }
            mossTime++;
            if (this.isInWater() && mossTime > 12000) {
                mossTime = 0;
                this.setMoss(Math.min(10, this.getMoss() + 1));
            }
        }
    }

    @Nullable
    public LivingEntity getAttackTarget() {
        return this.chaseTime < 0 ? null : super.getAttackTarget();
    }

    @Nullable
    public LivingEntity getRevengeTarget() {
        return this.chaseTime < 0 ? null : super.getRevengeTarget();
    }

    public void setRevengeTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (this.chaseTime >= 0) {
            super.setRevengeTarget(entitylivingbaseIn);
        }else{
            super.setRevengeTarget(null);
        }
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (this.chaseTime >= 0) {
            super.setAttackTarget(entitylivingbaseIn);
        }else{
            super.setAttackTarget(null);
        }
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setMoss(rand.nextInt(6));
        this.setTurtleScale(0.8F + rand.nextFloat() * 0.2F);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public float getTurtleScale() {
        return this.dataManager.get(TURTLE_SCALE);
    }

    public void setTurtleScale(float scale) {
        this.dataManager.set(TURTLE_SCALE, scale);
    }


    protected PathNavigator createNavigator(World worldIn) {
        SemiAquaticPathNavigator flyingpathnavigator = new SemiAquaticPathNavigator(this, worldIn) {
            public boolean canEntityStandOnPos(BlockPos pos) {
                return this.world.getBlockState(pos).getFluidState().isEmpty();
            }
        };
        return flyingpathnavigator;
    }

    public boolean isWaiting() {
        return this.dataManager.get(WAITING).booleanValue();
    }

    public void setWaiting(boolean sit) {
        this.dataManager.set(WAITING, Boolean.valueOf(sit));
    }

    public int getMoss() {
        return this.dataManager.get(MOSS).intValue();
    }

    public void setMoss(int moss) {
        this.dataManager.set(MOSS, Integer.valueOf(moss));
    }

    protected void updateAir(int air) {

    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Waiting", this.isWaiting());
        compound.putInt("MossLevel", this.getMoss());
        compound.putFloat("TurtleScale", this.getTurtleScale());
        compound.putInt("MossTime", this.mossTime);
        compound.putInt("WaitTime", this.waitTime);
        compound.putInt("WaitTime2", this.timeUntilWait);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setWaiting(compound.getBoolean("Waiting"));
        this.setMoss(compound.getInt("MossLevel"));
        this.setTurtleScale(compound.getFloat("TurtleScale"));
        this.mossTime = compound.getInt("MossTime");
        this.waitTime = compound.getInt("WaitTime");
        this.timeUntilWait = compound.getInt("WaitTime2");
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
        return this.isWaiting();
    }

    @Override
    public int getWaterSearchRange() {
        return 10;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return worldIn.getFluidState(pos.down()).isEmpty() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER) ? 10.0F : super.getBlockPathWeight(pos, worldIn);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataManager.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.dataManager.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.dataManager.set(CLIMBING, b0);
    }


    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            if (this.isJumping) {
                this.setMotion(this.getMotion().scale(1D));
                this.setMotion(this.getMotion().add(0.0D, 0.72D, 0.0D));
            } else {
                this.setMotion(this.getMotion().scale(0.4D));
                this.setMotion(this.getMotion().add(0.0D, -0.08D, 0.0D));
            }

        } else {
            super.travel(travelVector);
        }

    }

    public boolean isShearable() {
        return this.isAlive() && this.getMoss() > 0;
    }

    @Override
    public boolean isShearable(@javax.annotation.Nonnull ItemStack item, World world, BlockPos pos) {
        return isShearable();
    }

    @Override
    public void shear(SoundCategory category) {
        world.playMovingSound(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, category, 1.0F, 1.0F);
        if(rand.nextFloat() < this.getMoss() * 0.1F){
            this.entityDropItem(Items.SCUTE);
        }else{
            this.entityDropItem(Items.SEAGRASS);
        }
        this.setMoss(0);
    }

    @javax.annotation.Nonnull
    @Override
    public java.util.List<ItemStack> onSheared(@javax.annotation.Nullable PlayerEntity player, @javax.annotation.Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
        world.playMovingSound(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1.0F, 1.0F);
        if(rand.nextFloat() < this.getMoss() * 0.1F){
            this.setMoss(0);
            return Collections.singletonList(new ItemStack(Items.SCUTE));
        }else{
            this.setMoss(0);
            return Collections.singletonList(new ItemStack(Items.SEAGRASS));
        }
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.ALLIGATOR_SNAPPING_TURTLE.create(p_241840_1_);
    }
}
