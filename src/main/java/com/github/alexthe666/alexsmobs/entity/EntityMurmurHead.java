package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityMurmurHead extends Monster {

    private static final EntityDataAccessor<Optional<UUID>> BODY_UUID = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> BODY_ID = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.INT);

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
        this.setPos(parent.getNeckBottom(1.0F));
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
        return new Vec3(d0, d1, d2);
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

    public void tick(){
        super.tick();
        if(!level.isClientSide){
            Entity body = getBody();
            if(body instanceof EntityMurmur){
                EntityMurmur murmur = (EntityMurmur)body;
                this.entityData.set(BODY_ID, body.getId());
                Vec3 vec3 = murmur.getNeckBottom(1.0F);
                float f = ( (float)Math.sin(this.tickCount * 0.05F) + 1F) * 2F;
                f = 3;
                float xFun = (float)Math.sin(this.tickCount * 0.05F);
                float zFun = -(float)Math.cos(this.tickCount * 0.05F);
                this.setPos(vec3.x + xFun, vec3.y + f, vec3.z - zFun);
                this.setYRot(body.getYRot());
                this.setYHeadRot(body.getYHeadRot());
            }
        }
    }
}
