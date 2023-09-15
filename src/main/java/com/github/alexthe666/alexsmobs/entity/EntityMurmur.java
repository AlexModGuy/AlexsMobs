package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityMurmur extends Monster implements ISemiAquatic {

    private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> HEAD_ID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.INT);
    private boolean renderFakeHead = true;


    protected EntityMurmur(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 48.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.3F).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(2, new AnimalAIWanderRanged(this, 55, 1.0D, 14, 7));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this)));
    }


    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.MURMUR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.MURMUR_HURT.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    public static <T extends Mob> boolean checkMurmurSpawnRules(EntityType<EntityMurmur> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || !iServerWorld.canSeeSky(pos) && (pos.getY() <= AMConfig.murmurSpawnHeight || iServerWorld.getBiome(pos).is(AMTagRegistry.SPAWNS_MURMURS_IGNORE_HEIGHT)) && checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.murmurSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    public boolean isAlliedTo(Entity entity) {
        return this.getHeadUUID() != null && entity.getUUID().equals(this.getHeadUUID()) || super.isAlliedTo(entity);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 1.2F;
    }

    protected float getWaterSlowDown() {
        return 0.9F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAD_UUID, Optional.empty());
        this.entityData.define(HEAD_ID, -1);
    }

    @Nullable
    public UUID getHeadUUID() {
        return this.entityData.get(HEAD_UUID).orElse(null);
    }

    public void setHeadUUID(@Nullable UUID uniqueId) {
        this.entityData.set(HEAD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getHead() {
        if (!level.isClientSide) {
            UUID id = getHeadUUID();
            return id == null ? null : ((ServerLevel) level).getEntity(id);
        }else{
            int id = this.entityData.get(HEAD_ID);
            return id == -1 ? null : level.getEntity(id);
        }
    }

    public boolean shouldRenderFakeHead() {
        return this.renderFakeHead;
    }

    public void tick() {
        super.tick();
        if (this.renderFakeHead) this.renderFakeHead = false;
        this.yBodyRot = this.getYRot();
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70, this.yBodyRot + 70);
        if (!level.isClientSide) {
            Entity head = getHead();
            if(head == null){
                LivingEntity created = createHead();
                this.setHeadUUID(created.getUUID());
                this.entityData.set(HEAD_ID, created.getId());
            }
        }
    }

    public Vec3 getNeckBottom(float partialTick){
        double d0 = Mth.lerp(partialTick, this.xo, this.getX());
        double d1 = Mth.lerp(partialTick, this.yo, this.getY());
        double d2 = Mth.lerp(partialTick, this.zo, this.getZ());
        double height = this.getBbHeight() - 0.4F + calculateWalkBounce(partialTick);
        Vec3 rotatedOnDeath = new Vec3(0, height, 0);
        if(this.deathTime > 0){
            float f = ((float)this.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }
            rotatedOnDeath = rotatedOnDeath.add(f * 0.1F, f * 0.4F, 0).zRot((float) (f * Math.PI / 2F)).yRot(-this.yBodyRot * Mth.DEG_TO_RAD);
        }
        return new Vec3(d0, d1, d2).add(rotatedOnDeath);
    }

    public double calculateWalkBounce(float partialTick){
        float limbSwingAmount = Mth.lerp(partialTick, this.animationSpeedOld, this.animationSpeed);
        float limbSwing = this.animationPosition - this.animationSpeed * (1.0F - partialTick);
        return Math.abs(Math.sin(limbSwing * 0.9F) * limbSwingAmount * 0.25F);
    }


    @Override
    public boolean shouldEnterWater() {
        return false;
    }

    @Override
    public boolean shouldLeaveWater() {
        return true;
    }

    @Override
    public boolean shouldStopMoving() {
        return false;
    }

    @Override
    public int getWaterSearchRange(){
        return 5;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("HeadUUID")) {
            this.setHeadUUID(compound.getUUID("HeadUUID"));
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getHeadUUID() != null) {
            compound.putUUID("HeadUUID", this.getHeadUUID());
        }
    }

    private LivingEntity createHead() {
        EntityMurmurHead head = new EntityMurmurHead(this);
        level.addFreshEntity(head);
        return head;
    }

    public boolean isAngry(){
        Entity entity = this.getHead();
        if(entity instanceof EntityMurmurHead){
            return ((EntityMurmurHead)entity).isAngry();
        }
        return false;
    }
}
