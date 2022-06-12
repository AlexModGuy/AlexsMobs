package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class EntityGuster extends Monster {

    private static final EntityDataAccessor<Integer> LIFT_ENTITY = SynchedEntityData.defineId(EntityGuster.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityGuster.class, EntityDataSerializers.INT);
    private LivingEntity liftedEntity;
    private int liftingTime = 0;
    private int maxLiftTime = 40;
    private int shootingTicks;
    public static final ResourceLocation RED_LOOT = new ResourceLocation("alexsmobs", "entities/guster_red");
    public static final ResourceLocation SOUL_LOOT = new ResourceLocation("alexsmobs", "entities/guster_soul");

    protected EntityGuster(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public int getAmbientSoundInterval() {
        return 80;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GUSTER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GUSTER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GUSTER_HURT.get();
    }


    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return this.getVariant() == 2 ? SOUL_LOOT : this.getVariant() == 1 ? RED_LOOT : super.getDefaultLootTable();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    public static boolean canGusterSpawn(EntityType animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        boolean spawnBlock = worldIn.getBlockState(pos.below()).is(BlockTags.SAND);
        return spawnBlock && (!AMConfig.limitGusterSpawnsToWeather || worldIn.getLevelData() != null && (worldIn.getLevelData().isThundering() || worldIn.getLevelData().isRaining()) || isBiomeNether(worldIn, pos));
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.gusterSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeGoal());
        this.goalSelector.addGoal(1, new AnimalAIWanderRanged(this, 60, 1.0D, 10, 7));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }


    public void doPush(Entity entityIn) {
        if (this.getLiftedEntity() == null && liftingTime >= 0 && !(entityIn instanceof EntityGuster)) {
            this.setLiftedEntity(entityIn.getId());
            maxLiftTime = 30 + random.nextInt(30);
        }
    }

    public boolean hasLiftedEntity() {
        return this.entityData.get(LIFT_ENTITY) != 0;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LIFT_ENTITY, 0);
        this.entityData.define(VARIANT, 0);
    }


    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (source.isProjectile()) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.hurt(source, amount);
        }
    }


    private void spit(LivingEntity target) {
        EntitySandShot sghot = new EntitySandShot(this.level, this);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - sghot.getY();
        double d2 = target.getZ() - this.getZ();
        float f = Mth.sqrt((float)(d0 * d0 + d2 * d2)) * 0.35F;
        sghot.shoot(d0, d1 + (double) f, d2, 1F, 10.0F);
        sghot.setVariant(this.getVariant());
        if (!this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SAND_BREAK, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }
        this.level.addFreshEntity(sghot);
    }

    public double getEyeY() {
        return this.getY() + 1.0F;
    }


    @Nullable
    public Entity getLiftedEntity() {
        if (!this.hasLiftedEntity()) {
            return null;
        } else {
            return this.level.getEntity(this.entityData.get(LIFT_ENTITY));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType
            reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if(this.isBiomeNether(worldIn, this.blockPosition())){
            this.setVariant(2);
        }else if(this.isBiomeRed(worldIn, this.blockPosition())){
            this.setVariant(1);
        }else{
            this.setVariant(0);
        }
        this.setAirSupply(this.getMaxAirSupply());
        this.setXRot(0.0F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void setLiftedEntity(int p_175463_1_) {
        this.entityData.set(LIFT_ENTITY, p_175463_1_);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public void aiStep() {
        super.aiStep();
        Entity lifted = this.getLiftedEntity();
        if (lifted == null && !level.isClientSide && tickCount % 15 == 0) {
            List<ItemEntity> list = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.8F));
            ItemEntity closestItem = null;
            for (int i = 0; i < list.size(); ++i) {
                ItemEntity entity = list.get(i);
                if (entity.isOnGround() && (closestItem == null || this.distanceTo(closestItem) > this.distanceTo(entity))) {
                    closestItem = entity;
                }
            }
            if (closestItem != null) {
                this.setLiftedEntity(closestItem.getId());
                maxLiftTime = 30 + random.nextInt(30);
            }
        }
        if (this.isInWaterOrBubble()) {
            this.hurt(DamageSource.DROWN, 0.5F);
        }
        float f = (float) this.getY();
        if (this.isAlive()) {
            ParticleOptions type = this.getVariant() == 2 ? AMParticleRegistry.GUSTER_SAND_SPIN_SOUL.get() : this.getVariant() == 1 ? AMParticleRegistry.GUSTER_SAND_SPIN_RED.get() : AMParticleRegistry.GUSTER_SAND_SPIN.get();
            for (int j = 0; j < 4; ++j) {
                float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.95F;
                float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.95F;
                this.level.addParticle(type, this.getX() + (double) f1, f, this.getZ() + (double) f2, this.getX(), this.getY() + random.nextFloat() * this.getBbHeight() + 0.2F, this.getZ());
            }
        }
        if (lifted != null && liftingTime >= 0) {
            liftingTime++;
            float resist = 1F;
            if (lifted instanceof LivingEntity) {
                resist = (float) Mth.clamp((1.0D - ((LivingEntity) lifted).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
            }
            float radius = 1F + (liftingTime * 0.05F);
            if (lifted instanceof ItemEntity) {
                radius = 0.2F + (liftingTime * 0.025F);
            }
            float angle = liftingTime * -0.25F;
            double extraX = this.getX() + radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = this.getZ() + radius * Mth.cos(angle);
            double d0 = (extraX - lifted.getX()) * resist;
            double d1 = (extraZ - lifted.getZ()) * resist;
            lifted.setDeltaMovement(d0, 0.1 * resist, d1);
            lifted.hasImpulse = true;
            if (liftingTime > maxLiftTime) {
                this.setLiftedEntity(0);
                liftingTime = -20;
                maxLiftTime = 30 + random.nextInt(30);
            }
        } else if (liftingTime < 0) {
            liftingTime++;
        } else if (this.getTarget() != null && this.distanceTo(this.getTarget()) < this.getBbWidth() + 1F && !(this.getTarget() instanceof EntityGuster)) {
            this.setLiftedEntity(this.getTarget().getId());
            maxLiftTime = 30 + random.nextInt(30);
        }
        if (!level.isClientSide && shootingTicks >= 0) {
            if (shootingTicks <= 0) {
                if (this.getTarget() != null && (lifted == null || lifted.getId() != this.getTarget().getId()) && this.isAlive()) {
                    this.spit(this.getTarget());
                }
                shootingTicks = 40 + random.nextInt(40);
            } else {
                shootingTicks--;
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    public boolean isGooglyEyes() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && s.toLowerCase().contains("tweester");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
    }

    private static boolean isBiomeRed(LevelAccessor worldIn, BlockPos position) {
        return BiomeDictionary.hasType(worldIn.getBiome(position).unwrapKey().get(), BiomeDictionary.Type.MESA);
    }

    private static boolean isBiomeNether(LevelAccessor worldIn, BlockPos position) {
        return BiomeDictionary.hasType(worldIn.getBiome(position).unwrapKey().get(), BiomeDictionary.Type.NETHER);
    }

    public static int getColorForVariant(int variant){
        if(variant == 2){
            return 0X4E3D33;
        }else if(variant == 1){
            return 0XC66127;
        }else{
            return 0XF3C389;
        }
    }

    private class MeleeGoal extends Goal {

        public MeleeGoal() {
        }

        public boolean canUse() {
            return EntityGuster.this.getTarget() != null;
        }

        public void tick() {
            Entity thrownEntity = EntityGuster.this.getLiftedEntity();

            if (EntityGuster.this.getTarget() != null) {
                if (thrownEntity != null && thrownEntity.getId() == EntityGuster.this.getTarget().getId()) {
                    EntityGuster.this.getNavigation().stop();
                } else {
                    EntityGuster.this.getNavigation().moveTo(EntityGuster.this.getTarget(), 1.25F);
                }
            }
        }
    }
}
