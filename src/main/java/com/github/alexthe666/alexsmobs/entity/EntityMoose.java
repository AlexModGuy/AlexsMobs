package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class EntityMoose extends AnimalEntity implements IAnimatedEntity {

    public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
    public static final Animation ANIMATION_ATTACK = Animation.create(15);
    private static final int DAY = 24000;
    private static final DataParameter<Boolean> ANTLERED = EntityDataManager.createKey(EntityMoose.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> JOSTLING = EntityDataManager.createKey(EntityMoose.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> JOSTLE_ANGLE = EntityDataManager.createKey(EntityMoose.class, DataSerializers.FLOAT);
    private static final DataParameter<Optional<UUID>> JOSTLER_UUID = EntityDataManager.createKey(EntityMoose.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public float prevJostleAngle;
    public float prevJostleProgress;
    public float jostleProgress;
    public boolean jostleDirection;
    public int jostleTimer = 0;
    public boolean instantlyTriggerJostleAI = false;
    public int jostleCooldown = 100 + rand.nextInt(40);
    public int timeUntilAntlerDrop = 7 * DAY + this.rand.nextInt(3) * DAY;
    private int animationTick;
    private Animation currentAnimation;

    protected EntityMoose(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    public static boolean canMooseSpawn(EntityType<? extends MobEntity> typeIn, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockState blockstate = worldIn.getBlockState(pos.down());
        return (blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(Blocks.SNOW)) && worldIn.getLightSubtracted(pos, 0) > 8;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 70D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 9.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mooseSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MooseAIJostle(this));
        this.goalSelector.addGoal(3, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, Ingredient.fromItems(Items.DANDELION), false));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 120, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 15.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)));
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 6) {
            for (int lvt_3_1_ = 0; lvt_3_1_ < 7; ++lvt_3_1_) {
                double lvt_4_1_ = this.rand.nextGaussian() * 0.02D;
                double lvt_6_1_ = this.rand.nextGaussian() * 0.02D;
                double lvt_8_1_ = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.SMOKE, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), lvt_4_1_, lvt_6_1_, lvt_8_1_);
            }
        } else {
            super.handleStatusUpdate(id);
        }

    }

    public boolean isBreedingItem(ItemStack stack) {
        if (stack.getItem() == Items.DANDELION && !this.isInLove() && this.getGrowingAge() == 0) {
            if (this.getRNG().nextInt(5) == 0) {
            return true;
            }else{
                this.world.setEntityState(this, (byte) 6);
                return false;
            }
        }
        return false;
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (!this.isChild()) {
            super.setAttackTarget(entitylivingbaseIn);
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ANTLERED, Boolean.valueOf(true));
        this.dataManager.register(JOSTLING, Boolean.valueOf(false));
        this.dataManager.register(JOSTLE_ANGLE, 0F);
        this.dataManager.register(JOSTLER_UUID, Optional.empty());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("AntlerTime")) {
            this.timeUntilAntlerDrop = compound.getInt("AntlerTime");
        }
        this.setAntlered(compound.getBoolean("Antlered"));
        this.jostleCooldown = compound.getInt("JostlingCooldown");

    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("AntlerTime", this.timeUntilAntlerDrop);
        compound.putBoolean("Antlered", this.isAntlered());
        compound.putInt("JostlingCooldown", this.jostleCooldown);
    }

    public void tick() {
        super.tick();
        prevJostleProgress = jostleProgress;
        prevJostleAngle = this.getJostleAngle();
        if (this.isJostling() && jostleProgress < 5F) {
            jostleProgress++;
        }
        if (!this.isJostling() && jostleProgress > 0F) {
            jostleProgress--;
        }
        if (jostleCooldown > 0) {
            jostleCooldown--;
        }
        if (!world.isRemote && this.getAnimation() == NO_ANIMATION && getRNG().nextInt(120) == 0 && (this.getAttackTarget() == null || !this.getAttackTarget().isAlive()) && !this.isJostling() && this.getJostlingPartnerUUID() == null) {
            if (world.getBlockState(this.getPosition().down()).isIn(Blocks.GRASS_BLOCK) && getRNG().nextInt(3) == 0) {
                this.setAnimation(ANIMATION_EAT_GRASS);
            }
        }
        if (timeUntilAntlerDrop > 0) {
            timeUntilAntlerDrop--;
        }
        if (timeUntilAntlerDrop == 0) {
            if (this.isAntlered()) {
                this.setAntlered(false);
                this.entityDropItem(new ItemStack(AMItemRegistry.MOOSE_ANTLER));
                timeUntilAntlerDrop = 2 * DAY + this.rand.nextInt(3) * DAY;
            } else {
                this.setAntlered(true);
                timeUntilAntlerDrop = 7 * DAY + this.rand.nextInt(3) * DAY;
            }
        }
        if (this.getAttackTarget() != null && this.getAttackTarget().isAlive()) {
            if (this.isJostling()) {
                this.setJostling(false);
            }
            if (!world.isRemote && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 8) {
                float dmg = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                if (!isAntlered()) {
                    dmg = 3;
                }
                if (this.getAttackTarget() instanceof WolfEntity || this.getAttackTarget() instanceof EntityOrca) {
                    dmg = 2;
                }
                getAttackTarget().applyKnockback(1F, getAttackTarget().getPosX() - this.getPosX(), getAttackTarget().getPosZ() - this.getPosZ());
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            if (entity instanceof EntityOrca || entity instanceof WolfEntity) {
                amount = (amount + 1.0F) * 3.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.MOOSE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MOOSE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MOOSE_HURT;
    }


    public boolean isAntlered() {
        return this.dataManager.get(ANTLERED).booleanValue();
    }

    public void setAntlered(boolean anters) {
        this.dataManager.set(ANTLERED, anters);
    }

    public boolean isJostling() {
        return this.dataManager.get(JOSTLING).booleanValue();
    }

    public void setJostling(boolean jostle) {
        this.dataManager.set(JOSTLING, jostle);
    }

    public float getJostleAngle() {
        return this.dataManager.get(JOSTLE_ANGLE);
    }

    public void setJostleAngle(float scale) {
        this.dataManager.set(JOSTLE_ANGLE, scale);
    }

    @Nullable
    public UUID getJostlingPartnerUUID() {
        return this.dataManager.get(JOSTLER_UUID).orElse(null);
    }

    public void setJostlingPartnerUUID(@Nullable UUID uniqueId) {
        this.dataManager.set(JOSTLER_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public Entity getJostlingPartner() {
        UUID id = getJostlingPartnerUUID();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
    }

    public void setJostlingPartner(@Nullable Entity jostlingPartner) {
        if (jostlingPartner == null) {
            this.setJostlingPartnerUUID(null);
        } else {
            this.setJostlingPartnerUUID(jostlingPartner.getUniqueID());
        }
    }

    public void pushBackJostling(EntityMoose entityMoose, float strength) {
        applyKnockbackFromMoose(strength, entityMoose.getPosX() - this.getPosX(), entityMoose.getPosZ() - this.getPosZ());
    }

    private void applyKnockbackFromMoose(float strength, double ratioX, double ratioZ) {
        net.minecraftforge.event.entity.living.LivingKnockBackEvent event = net.minecraftforge.common.ForgeHooks.onLivingKnockBack(this, strength, ratioX, ratioZ);
        if (event.isCanceled()) return;
        strength = event.getStrength();
        ratioX = event.getRatioX();
        ratioZ = event.getRatioZ();
        if (!(strength <= 0.0F)) {
            this.isAirBorne = true;
            Vector3d vector3d = this.getMotion();
            Vector3d vector3d1 = (new Vector3d(ratioX, 0.0D, ratioZ)).normalize().scale(strength);
            this.setMotion(vector3d.x / 2.0D - vector3d1.x, 0.3F, vector3d.z / 2.0D - vector3d1.z);
        }
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
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_ATTACK, ANIMATION_EAT_GRASS};
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.MOOSE.create(serverWorld);
    }

    public boolean canJostleWith(EntityMoose moose) {
        return !moose.isJostling() && moose.isAntlered() && moose.getAnimation() == NO_ANIMATION && !moose.isChild() && moose.getJostlingPartnerUUID() == null && moose.jostleCooldown == 0;
    }

    public void playJostleSound() {
        this.playSound(AMSoundRegistry.MOOSE_JOSTLE, this.getSoundPitch(), this.getSoundVolume());
    }

}
