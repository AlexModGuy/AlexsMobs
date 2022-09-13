package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityMurmur extends Monster {

    private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> HEAD_ID = SynchedEntityData.defineId(EntityMurmur.class, EntityDataSerializers.INT);

    protected EntityMurmur(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 48.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.3F).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 60, 1.0D, 14, 7));

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

    public void tick() {
        super.tick();
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
        return new Vec3(d0, d1 + this.getBbHeight() - 0.4F + calculateWalkBounce(partialTick), d2);
    }

    public double calculateWalkBounce(float partialTick){
        float limbSwingAmount = Mth.lerp(partialTick, this.animationSpeedOld, this.animationSpeed);
        float limbSwing = this.animationPosition - this.animationSpeed * (1.0F - partialTick);
        return Math.abs(Math.sin(limbSwing * 0.9F) * limbSwingAmount * 0.25F);
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
}
