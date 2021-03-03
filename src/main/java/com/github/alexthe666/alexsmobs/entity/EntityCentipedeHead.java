package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class EntityCentipedeHead extends MonsterEntity {

    private static final DataParameter<Optional<UUID>> CHILD_UUID = EntityDataManager.createKey(EntityCentipedeHead.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    protected EntityCentipedeHead(EntityType type, World worldIn) {
        super(type, worldIn);
        this.stepHeight = 3;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.caveCentipedeSpawnRolls, this.getRNG(), spawnReasonIn) && super.canSpawn(worldIn, spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new RandomWalkingGoal(this, 1.0D, 13, false));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 20, true, true, null));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, 20, true, true, null));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityCockroach.class, 45, true, true, null));
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CENTIPEDE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CENTIPEDE_HURT;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 60.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ARMOR, 12.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 8.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(AMSoundRegistry.CENTIPEDE_WALK, 1F, 1.0F);
    }

    public int getVerticalFaceSpeed() {
        return 1;
    }

    public int getHorizontalFaceSpeed() {
        return 1;
    }

    public int getFaceRotSpeed() {
        return 1;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CHILD_UUID, Optional.empty());
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (super.attackEntityAsMob(entityIn)) {
            if (entityIn instanceof LivingEntity) {
                int i = 3;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 20;
                }
                if (i > 0) {
                    ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 1));
                }
            }
            this.playSound(AMSoundRegistry.CENTIPEDE_ATTACK, this.getSoundVolume(), this.getSoundPitch());
            return true;
        } else {
            return false;
        }
    }


    @Nullable
    public UUID getChildId() {
        return this.dataManager.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.dataManager.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
    }

    public void collideWithNearbyEntities() {
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        entities.stream().filter(entity -> !(entity instanceof EntityCentipedeBody) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(this));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getChildId() != null) {
            compound.putUniqueId("ChildUUID", this.getChildId());
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return  source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("ChildUUID")) {
            this.setChildId(compound.getUniqueId("ChildUUID"));
        }
    }

    public void tick(){
        super.tick();
        inPortal = false;
        rotationYaw = renderYawOffset;
        if (!world.isRemote) {
            Entity child = getChild();
            if (child == null) {
                LivingEntity partParent = this;
                int segments = 5 + getRNG().nextInt(3);
                for (int i = 0; i < segments; i++) {
                    EntityCentipedeBody part = createBody(partParent, i == segments - 1);
                    part.setParent(partParent);
                    part.setBodyIndex(i);
                    if (partParent == this) {
                        this.setChildId(part.getUniqueID());
                    }
                    part.setInitialPartPos(this, i + 1);
                    world.addEntity(part);
                    partParent = part;
                }
            }
        }
    }

    public EntityCentipedeBody createBody(LivingEntity parent, boolean tail) {
        return tail ? new EntityCentipedeBody(AMEntityRegistry.CENTIPEDE_TAIL, parent, 0.84F, 180, 0) : new EntityCentipedeBody(AMEntityRegistry.CENTIPEDE_BODY, parent, 0.84F, 180, 0);
    }

    public boolean canBeLeashedTo(PlayerEntity player) {
        return true;
    }


    public static <T extends MobEntity> boolean canCentipedeSpawn(EntityType<EntityCentipedeHead> entityType, IServerWorld iServerWorld, SpawnReason reason, BlockPos pos, Random random) {
        return reason == SpawnReason.SPAWNER || !iServerWorld.canSeeSky(pos) && pos.getY() <= AMConfig.caveCentipedeSpawnHeight && canMonsterSpawnInLight(entityType, iServerWorld, reason, pos, random);
    }
}
