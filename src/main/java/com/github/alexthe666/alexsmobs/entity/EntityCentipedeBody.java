package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityCentipedeBody extends MobEntity implements IHurtableMultipart {

    private static final DataParameter<Integer> BODYINDEX = EntityDataManager.createKey(EntityCentipedeBody.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<UUID>> PARENT_UUID = EntityDataManager.createKey(EntityCentipedeBody.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public EntitySize multipartSize;
    protected float radius;
    protected float angleYaw;
    protected float offsetY;
    protected float damageMultiplier = 1;
    private float parentYaw = 0;
    protected EntityCentipedeBody(EntityType type, World worldIn) {
        super(type, worldIn);
        multipartSize = type.getSize();
    }

    public boolean preventDespawn() {
        return super.preventDespawn() || this.getParent() != null;
    }


    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return  source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    @Override
    public void tick() {
        super.tick();
        inPortal = false;
        Entity parent = getParent();
        recalculateSize();
        if (parent != null && !world.isRemote) {
            float f = this.getDistance(parent);
            this.setNoGravity(true);
            this.faceEntity(parent, 1, 1);
            this.parentYaw = this.limitAngle(this.parentYaw, parent.prevRotationYaw, 5.0F);
            double yD1 = (parent.getPosY() - this.getPosY()) / (double) f;
            double ySet = parent.prevPosY;
            if (!world.getBlockState(new BlockPos(this.getPosX(), ySet - 0.1, this.getPosZ())).isSolid()) {
                ySet = parent.prevPosY - 0.2F;
            }
            if (this.isEntityInsideOpaqueBlock() || world.getBlockState(new BlockPos(this.getPosX(), ySet, this.getPosZ())).isSolid()) {
                ySet = parent.prevPosY + 0.2F;
            }
            double yaw = parentYaw;
            double x = parent.prevPosX + this.radius * Math.cos(yaw * (Math.PI / 180.0F) + this.angleYaw);
            double z = parent.prevPosZ + this.radius * Math.sin(yaw * (Math.PI / 180.0F) + this.angleYaw);
            this.setPosition(x, ySet, z);
            double d0 = parent.getPosX() - this.getPosX();
            double d1 = parent.getPosY() - this.getPosY();
            double d2 = parent.getPosZ() - this.getPosZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double) (180F / (float) Math.PI)));
            this.rotationPitch = this.limitAngle(this.rotationPitch, f2, 5.0F);
            this.markVelocityChanged();
            this.rotationYaw = parentYaw;
            this.rotationYawHead = this.rotationYaw;
            this.renderYawOffset = this.prevRotationYaw;
            if (parent instanceof LivingEntity) {
                if(!world.isRemote && (((LivingEntity) parent).hurtTime > 0 || ((LivingEntity) parent).deathTime > 0)){
                    AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getEntityId(), parent.getEntityId(), 0));
                    this.hurtTime = ((LivingEntity) parent).hurtTime;
                    this.deathTime = ((LivingEntity) parent).deathTime;
                }
            }
            this.collideWithNearbyEntities();
            if ((parent.removed) && !world.isRemote) {
                this.remove();
            }
        }
        if (parent == null && !world.isRemote) {
            this.remove();
        }
    }

    protected float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
        float f = MathHelper.wrapDegrees(targetAngle - sourceAngle);
        if (f > maximumChange) {
            f = maximumChange;
        }

        if (f < -maximumChange) {
            f = -maximumChange;
        }

        float f1 = sourceAngle + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }

    public void setInitialPartPos(LivingEntity parent, int index) {
        double radAdd = this.radius * index;
        this.rotationYaw = parent.rotationYaw;
        this.renderYawOffset = parent.renderYawOffset;
        this.parentYaw = parent.rotationYaw;
        this.setPosition(parent.prevPosX +  radAdd * Math.cos(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw), parent.prevPosY + this.offsetY, parent.prevPosZ + radAdd * Math.sin(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw));
    }

    public EntityCentipedeBody(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(t, parent.world);
        this.setParent(parent);
        this.radius = radius;
        this.angleYaw = (angleYaw + 90.0F) * ((float) Math.PI / 180.0F);
        this.offsetY = offsetY;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getParentId() != null) {
            compound.putUniqueId("ParentUUID", this.getParentId());
        }
        compound.putInt("BodyIndex", getBodyIndex());
        compound.putFloat("PartAngle", angleYaw);
        compound.putFloat("PartRadius", radius);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("ParentUUID")) {
            this.setParentId(compound.getUniqueId("ParentUUID"));
        }
        this.setBodyIndex(compound.getInt("BodyIndex"));
        this.angleYaw = compound.getFloat("PartAngle");
        this.radius = compound.getFloat("PartRadius");
    }
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PARENT_UUID, Optional.empty());
        this.dataManager.register(BODYINDEX, 0);
    }

    public Entity getParent() {
        UUID id = getParentId();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
    }

    public void setParent(Entity entity) {
        this.setParentId(entity.getUniqueID());
    }

    @Override
    public boolean isEntityEqual(net.minecraft.entity.Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        Entity parent = getParent();
        boolean prev = parent != null && parent.attackEntityFrom(source, damage * this.damageMultiplier);
        if (prev && !world.isRemote) {
            AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getEntityId(), parent.getEntityId(), damage * this.damageMultiplier));
        }
        return prev;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public void collideWithNearbyEntities() {
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityCentipedeBody) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(parent));

        }
    }

    public boolean startRiding(Entity entityIn) {
        if(!(entityIn instanceof AbstractMinecartEntity || entityIn instanceof BoatEntity)){
            return super.startRiding(entityIn);
        }
        return false;
    }

    public int getBodyIndex() {
        return this.dataManager.get(BODYINDEX);
    }

    public void setBodyIndex(int index) {
        this.dataManager.set(BODYINDEX, index);
    }

    @Nullable
    public UUID getParentId() {
        return this.dataManager.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.dataManager.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ARMOR, 12.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 8.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Override
    public void onAttackedFromServer(LivingEntity parent, float damage) {
        if(parent.deathTime > 0){
            this.deathTime = parent.deathTime;
        }
        if(parent.hurtTime > 0){
            this.hurtTime = parent.hurtTime;
        }
    }
}