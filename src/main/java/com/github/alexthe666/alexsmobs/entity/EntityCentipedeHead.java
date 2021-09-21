package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class EntityCentipedeHead extends Monster {

    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityCentipedeHead.class, EntityDataSerializers.OPTIONAL_UUID);

    protected EntityCentipedeHead(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.maxUpStep = 3;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.caveCentipedeSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D, 13, false));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 20, true, true, null));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, 20, true, true, null));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityCockroach.class, 45, true, true, null));
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CENTIPEDE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CENTIPEDE_HURT;
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 35.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ARMOR, 6.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.5F).add(Attributes.MOVEMENT_SPEED, 0.22F);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(AMSoundRegistry.CENTIPEDE_WALK, 1F, 1.0F);
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    public int getHeadRotSpeed() {
        return 1;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHILD_UUID, Optional.empty());
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (super.doHurtTarget(entityIn)) {
            if (entityIn instanceof LivingEntity) {
                int i = 3;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 20;
                }
                if (i > 0) {
                    ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 1));
                }
            }
            this.playSound(AMSoundRegistry.CENTIPEDE_ATTACK, this.getSoundVolume(), this.getVoicePitch());
            return true;
        } else {
            return false;
        }
    }


    @Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public void pushEntities() {
        List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        entities.stream().filter(entity -> !(entity instanceof EntityCentipedeBody) && entity.isPushable()).forEach(entity -> entity.push(this));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return  source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }
    }

    public void tick(){
        super.tick();
        isInsidePortal = false;
        setYRot(yBodyRot);
        if (!level.isClientSide) {
            Entity child = getChild();
            if (child == null) {
                LivingEntity partParent = this;
                int segments = 5 + getRandom().nextInt(3);
                for (int i = 0; i < segments; i++) {
                    EntityCentipedeBody part = createBody(partParent, i == segments - 1);
                    part.setParent(partParent);
                    part.setBodyIndex(i);
                    if (partParent == this) {
                        this.setChildId(part.getUUID());
                    }
                    part.setInitialPartPos(this, i + 1);
                    level.addFreshEntity(part);
                    partParent = part;
                }
            }
        }
    }

    public EntityCentipedeBody createBody(LivingEntity parent, boolean tail) {
        return tail ? new EntityCentipedeBody(AMEntityRegistry.CENTIPEDE_TAIL, parent, 0.84F, 180, 0) : new EntityCentipedeBody(AMEntityRegistry.CENTIPEDE_BODY, parent, 0.84F, 180, 0);
    }

    public boolean canBeLeashed(Player player) {
        return true;
    }


    public static <T extends Mob> boolean canCentipedeSpawn(EntityType<EntityCentipedeHead> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, Random random) {
        return reason == MobSpawnType.SPAWNER || !iServerWorld.canSeeSky(pos) && pos.getY() <= AMConfig.caveCentipedeSpawnHeight && checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random);
    }
}
