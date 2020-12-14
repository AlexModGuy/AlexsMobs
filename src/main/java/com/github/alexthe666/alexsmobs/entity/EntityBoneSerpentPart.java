package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityBoneSerpentPart extends LivingEntity implements IHurtableMultipart {

    private static final DataParameter<Boolean> TAIL = EntityDataManager.createKey(EntityBoneSerpentPart.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BODYINDEX = EntityDataManager.createKey(EntityBoneSerpentPart.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<UUID>> PARENT_UUID = EntityDataManager.createKey(EntityBoneSerpentPart.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public EntitySize multipartSize;
    protected float radius;
    protected float angleYaw;
    protected float offsetY;
    protected float damageMultiplier = 1;


    public EntityBoneSerpentPart(EntityType t, World world) {
        super(t, world);
        multipartSize = t.getSize();
    }

    public EntityBoneSerpentPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(t, parent.world);
        this.setParent(parent);
        this.radius = radius;
        this.angleYaw = (angleYaw + 90.0F) * ((float) Math.PI / 180.0F);
        this.offsetY = offsetY;
    }

    public boolean startRiding(Entity entityIn) {
        if(!(entityIn instanceof AbstractMinecartEntity || entityIn instanceof BoatEntity)){
            return super.startRiding(entityIn);
        }
        return false;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    @Override
    public net.minecraft.entity.Entity getEntity() {
        return this;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getParentId() != null) {
            compound.putUniqueId("ParentUUID", this.getParentId());
        }
        compound.putBoolean("TailPart", isTail());
        compound.putInt("BodyIndex", getBodyIndex());
        compound.putFloat("PartAngle", angleYaw);
        compound.putFloat("PartRadius", radius);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("ParentUUID")) {
            this.setParentId(compound.getUniqueId("ParentUUID"));
        }
        this.setTail(compound.getBoolean("TailPart"));
        this.setBodyIndex(compound.getInt("BodyIndex"));
        this.angleYaw = compound.getFloat("PartAngle");
        this.radius = compound.getFloat("PartRadius");
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PARENT_UUID, Optional.empty());
        this.dataManager.register(TAIL, false);
        this.dataManager.register(BODYINDEX, 0);
    }

    @Nullable
    public UUID getParentId() {
        return this.dataManager.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.dataManager.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public void setInitialPartPos(Entity parent) {
        this.setPosition(parent.prevPosX + this.radius * Math.cos(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw), parent.prevPosY + this.offsetY, parent.prevPosZ + this.radius * Math.sin(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw));
    }

    @Override
    public void tick() {
        if (this.ticksExisted > 10) {
            Entity parent = getParent();
            recalculateSize();
            if (parent != null && !world.isRemote) {
                this.setNoGravity(true);
                this.setPosition(parent.prevPosX + this.radius * Math.cos(parent.prevRotationYaw * (Math.PI / 180.0F) + this.angleYaw), parent.prevPosY + this.offsetY, parent.prevPosZ + this.radius * Math.sin(parent.prevRotationYaw * (Math.PI / 180.0F) + this.angleYaw));
                double d0 = parent.getPosX() - this.getPosX();
                double d1 = parent.getPosY() - this.getPosY();
                double d2 = parent.getPosZ() - this.getPosZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double) (180F / (float) Math.PI)));
                this.rotationPitch = this.limitAngle(this.rotationPitch, f2, 5.0F);
                this.markVelocityChanged();
                this.rotationYaw = parent.prevRotationYaw;
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
                if (parent.removed && !world.isRemote) {
                    this.remove();
                }
            } else if (ticksExisted > 20 && !world.isRemote) {
                remove();
            }
        }
        super.tick();
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

    public void remove() {
        this.remove(false);
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
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public HandSide getPrimaryHand() {
        return null;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void collideWithNearbyEntities() {
        List<net.minecraft.entity.Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityBoneSerpentPart) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(parent));

        }
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        Entity parent = getParent();

        return parent != null ? parent.processInitialInteract(player, hand) : ActionResultType.PASS;
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
    public Iterable<ItemStack> getArmorInventoryList() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {

    }

    public boolean isTail() {
        return this.dataManager.get(TAIL).booleanValue();
    }

    public void setTail(boolean tail) {
        this.dataManager.set(TAIL, Boolean.valueOf(tail));
    }

    public int getBodyIndex() {
        return this.dataManager.get(BODYINDEX);
    }

    public void setBodyIndex(int index) {
        this.dataManager.set(BODYINDEX, index);
    }

    public boolean shouldNotExist() {
        Entity parent = getParent();
        return !parent.isAlive();
    }

    @Override
    public void onAttackedFromServer(LivingEntity parent, float damage) {
        if (parent.deathTime > 0) {
            this.deathTime = parent.deathTime;
        }
        if (parent.hurtTime > 0) {
            this.hurtTime = parent.hurtTime;
        }
    }

    public boolean shouldContinuePersisting() {
        return isAddedToWorld() || this.removed;
    }
}
