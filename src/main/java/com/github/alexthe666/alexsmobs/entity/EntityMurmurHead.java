package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityMurmurHead extends Monster implements FlyingAnimal {

    private static final EntityDataAccessor<Optional<UUID>> BODY_UUID = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> BODY_ID = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.INT);
    public double prevXHair;
    public double prevYHair;
    public double prevZHair;
    public double xHair;
    public double yHair;
    public double zHair;

    protected EntityMurmurHead(EntityType type, Level level) {
        super(type, level);
    }

    protected EntityMurmurHead(EntityMurmur parent) {
        super(AMEntityRegistry.MURMUR_HEAD.get(), parent.level);
        this.setBodyId(parent.getUUID());
        this.doSpawnPositioning(parent);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BODY_UUID, Optional.empty());
        this.entityData.define(BODY_ID, -1);
    }

    private void doSpawnPositioning(EntityMurmur parent){
        this.setPos(parent.getNeckBottom(1.0F).add(0, 0.5F, 0));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 48.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isNoGravity() {
        return true;
    }


    public Vec3 getNeckTop(float partialTick){
        double d0 = Mth.lerp(partialTick, this.xo, this.getX());
        double d1 = Mth.lerp(partialTick, this.yo, this.getY());
        double d2 = Mth.lerp(partialTick, this.zo, this.getZ());
        double bounce = 0;
        Entity body = this.getBody();
        if(body instanceof EntityMurmur){
            bounce = ((EntityMurmur)body).calculateWalkBounce(partialTick);
        }
        return new Vec3(d0, d1 + bounce, d2);
    }

    public Vec3 getNeckBottom(float partialTick){
        Entity body = this.getBody();
        Vec3 top = this.getNeckTop(partialTick);
        if(body instanceof EntityMurmur){
            EntityMurmur murmur = (EntityMurmur) body;
            Vec3 bodyBase = murmur.getNeckBottom(partialTick);
            double sub = top.subtract(bodyBase).horizontalDistance();
            return sub <= 0.06 ? new Vec3(top.x, bodyBase.y, top.z) : bodyBase;
        }
        return top.add(0, -0.5F, 0);
    }

    public boolean hasNeckBottom(){
        return true;
    }


    @Nullable
    public UUID getBodyId() {
        return this.entityData.get(BODY_UUID).orElse(null);
    }

    public void setBodyId(@Nullable UUID uniqueId) {
        this.entityData.set(BODY_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getBody() {
        if (!level.isClientSide) {
            final UUID id = getBodyId();
            return id == null ? null : ((ServerLevel) level).getEntity(id);
        }else{
            int id = this.entityData.get(BODY_ID);
            return id == -1 ? null : level.getEntity(id);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("BodyUUID")) {
            this.setBodyId(compound.getUUID("BodyUUID"));
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getBodyId() != null) {
            compound.putUUID("BodyUUID", this.getBodyId());
        }
    }

    public void tick(){
        super.tick();
        moveHair();
        if(!level.isClientSide) {
            Entity body = getBody();
            if (body instanceof EntityMurmur) {
                EntityMurmur murmur = (EntityMurmur) body;
                this.entityData.set(BODY_ID, body.getId());
                Vec3 vec3 = murmur.getNeckBottom(1.0F).add(0, 0.5F, 0);
                float f = ((float) Math.sin(this.tickCount * 0.05F + 2F) + 1.5F) * 2F;
                float xFun = (float) Math.sin(this.tickCount * 0.05F);
                float zFun = -(float) Math.cos(this.tickCount * 0.05F);
                Vec3 forwards = vec3.add(murmur.getLookAngle().scale(f)).add(xFun, f, zFun);
                this.setPos(forwards.x, forwards.y, forwards.z);
            }
        }
    }

    private void moveHair() {
        this.prevXHair = this.xHair;
        this.prevYHair = this.yHair;
        this.prevZHair = this.zHair;
        double d0 = this.getX() - this.xHair;
        double d1 = this.getY() - this.yHair;
        double d2 = this.getZ() - this.zHair;
        double d3 = 10.0D;
        if (d0 > 10.0D) {
            this.xHair = this.getX();
            this.prevXHair = this.xHair;
        }

        if (d2 > 10.0D) {
            this.zHair = this.getZ();
            this.prevZHair = this.zHair;
        }

        if (d1 > 10.0D) {
            this.yHair = this.getY();
            this.prevYHair = this.yHair;
        }

        if (d0 < -10.0D) {
            this.xHair = this.getX();
            this.prevXHair = this.xHair;
        }

        if (d2 < -10.0D) {
            this.zHair = this.getZ();
            this.prevZHair = this.zHair;
        }

        if (d1 < -10.0D) {
            this.yHair = this.getY();
            this.prevYHair = this.yHair;
        }

        this.xHair += d0 * 0.25D;
        this.zHair += d2 * 0.25D;
        this.yHair += d1 * 0.25D;
    }

    public boolean isFlying() {
        return true;
    }
}
