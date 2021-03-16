package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class EntityEmu extends AnimalEntity implements IAnimatedEntity, IHerdPanic {

    public static final Animation ANIMATION_DODGE_LEFT = Animation.create(10);
    public static final Animation ANIMATION_DODGE_RIGHT = Animation.create(10);
    public static final Animation ANIMATION_PECK_GROUND = Animation.create(25);
    public static final Animation ANIMATION_SCRATCH = Animation.create(20);
    public static final Animation ANIMATION_PUZZLED = Animation.create(30);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityEmu.class, DataSerializers.VARINT);
    private int animationTick;
    private Animation currentAnimation;
    private int revengeCooldown = 0;
    private boolean emuAttackedDirectly = false;
    public int timeUntilNextEgg = this.rand.nextInt(6000) + 6000;

    protected EntityEmu(EntityType type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3F);
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, 0);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, true){
            protected double getAttackReachSqr(LivingEntity attackTarget) {
                return super.getAttackReachSqr(attackTarget) + 2.5D;
            }

            @Override
            public boolean shouldExecute() {
                return super.shouldExecute() && EntityEmu.this.revengeCooldown <= 0;
            }

            @Override
            public boolean shouldContinueExecuting() {
                return super.shouldContinueExecuting() && EntityEmu.this.revengeCooldown <= 0;
            }
        });
        this.goalSelector.addGoal(2, new AnimalAIHerdPanic(this, 1.5D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.fromItems(Items.WHEAT), false));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 110, 1.0D, 10, 7));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 15.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new EntityEmu.HurtByTargetGoal());
        if(AMConfig.emuTargetSkeletons){
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
        }
    }

    public boolean canAttack(LivingEntity target) {
        return !this.isChild() && super.canAttack(target);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean prev = super.attackEntityFrom(source, amount);
        if (prev) {
            double range = 15;
            int fleeTime = 100 + getRNG().nextInt(5);
            this.revengeCooldown = fleeTime;
            List<EntityEmu> list = this.world.getEntitiesWithinAABB(this.getClass(), this.getBoundingBox().grow(range, range / 2, range));
            for (EntityEmu emu : list) {
                emu.revengeCooldown = fleeTime;
                if(emu.isChild() && rand.nextInt(2) == 0){
                    emu.emuAttackedDirectly = this.getRevengeTarget() != null;
                    emu.revengeCooldown = emu.emuAttackedDirectly ? 10 + getRNG().nextInt(30) : fleeTime;
                }
            }
            emuAttackedDirectly = this.getRevengeTarget() != null;
            this.revengeCooldown = emuAttackedDirectly ? 10 + getRNG().nextInt(30) : revengeCooldown;
        }
        return prev;
    }

    public void travel(Vector3d travelVector) {
        this.setAIMoveSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.getAnimation() == ANIMATION_PECK_GROUND || this.getAnimation() == ANIMATION_PUZZLED ? 0.15F : 1F) * (isInLava() ? 0.2F : 1F));
        super.travel(travelVector);
    }

    public void tick() {
        super.tick();
        if (!world.isRemote) {
            if (this.getRevengeTarget() == null && this.getAttackTarget() == null) {
                if (this.getMotion().lengthSquared() < 0.03D && this.getRNG().nextInt(190) == 0 && this.getAnimation() == NO_ANIMATION) {
                    if (getRNG().nextInt(3) == 0) {
                        this.setAnimation(ANIMATION_PUZZLED);
                    } else if (this.onGround) {
                        this.setAnimation(ANIMATION_PECK_GROUND);
                    }
                }
            }
            if (revengeCooldown > 0) {
                revengeCooldown--;
            }
            if (revengeCooldown <= 0 && this.getRevengeTarget() != null && !emuAttackedDirectly) {
                this.setRevengeTarget(null);
                revengeCooldown = 0;
            }
            if (this.getAttackTarget() != null && this.getAnimation() == ANIMATION_SCRATCH && this.getDistance(this.getAttackTarget()) < 4F && (this.getAnimationTick() == 8 || this.getAnimationTick() == 15)) {
                float f1 = this.rotationYaw * ((float) Math.PI / 180F);
                this.setMotion(this.getMotion().add(-MathHelper.sin(f1) * 0.02F, 0.0D, MathHelper.cos(f1) * 0.02F));
                getAttackTarget().applyKnockback(0.4F, getAttackTarget().getPosX() - this.getPosX(), getAttackTarget().getPosZ() - this.getPosZ());
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        }
        if (!this.world.isRemote && this.isAlive() && !this.isChild() && --this.timeUntilNextEgg <= 0) {
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.entityDropItem(AMItemRegistry.EMU_EGG);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
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
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_DODGE_LEFT, ANIMATION_DODGE_RIGHT, ANIMATION_PECK_GROUND, ANIMATION_SCRATCH, ANIMATION_PUZZLED};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        EntityEmu emu = AMEntityRegistry.EMU.create(serverWorld);
        emu.setVariant(this.getVariant());
        return emu;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SCRATCH);
        }
        return true;
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        if (compound.contains("EggLayTime")) {
            this.timeUntilNextEgg = compound.getInt("EggLayTime");
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("EggLayTime", this.timeUntilNextEgg);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if(this.rand.nextInt(200) == 0){
            this.setVariant(2);
        }else if(rand.nextInt(3) == 0){
            this.setVariant(1);
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void onPanic() {

    }

    @Override
    public boolean canPanic() {
        return true;
    }

    class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(EntityEmu.this);
        }

        public void startExecuting() {
            if (EntityEmu.this.isChild() || !emuAttackedDirectly) {
                this.alertOthers();
                this.resetTask();
            } else {
                super.startExecuting();
            }
        }

        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityEmu && !mobIn.isChild() && !emuAttackedDirectly && ((EntityEmu) mobIn).revengeCooldown <= 0) {
                super.setAttackTarget(mobIn, targetIn);
            }

        }
    }
}
