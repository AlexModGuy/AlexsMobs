package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityBoneSerpentPart extends LivingEntity implements IHurtableMultipart {

    private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(EntityBoneSerpentPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(EntityBoneSerpentPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(EntityBoneSerpentPart.class, EntityDataSerializers.OPTIONAL_UUID);
    public EntityDimensions multipartSize;
    protected float radius;
    protected float angleYaw;
    protected float offsetY;
    protected float damageMultiplier = 1;

    public EntityBoneSerpentPart(EntityType t, Level world) {
        super(t, world);
        multipartSize = t.getDimensions();
    }

    public EntityBoneSerpentPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(t, parent.level());
        this.setParent(parent);
        this.radius = radius;
        this.angleYaw = (angleYaw + 90.0F) * Mth.DEG_TO_RAD;
        this.offsetY = offsetY;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean startRiding(Entity entityIn) {
        if(!(entityIn instanceof AbstractMinecart || entityIn instanceof Boat)){
            return super.startRiding(entityIn);
        }
        return false;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getParentId() != null) {
            compound.putUUID("ParentUUID", this.getParentId());
        }
        compound.putBoolean("TailPart", isTail());
        compound.putInt("BodyIndex", getBodyIndex());
        compound.putFloat("PartAngle", angleYaw);
        compound.putFloat("PartRadius", radius);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ParentUUID")) {
            this.setParentId(compound.getUUID("ParentUUID"));
        }
        this.setTail(compound.getBoolean("TailPart"));
        this.setBodyIndex(compound.getInt("BodyIndex"));
        this.angleYaw = compound.getFloat("PartAngle");
        this.radius = compound.getFloat("PartRadius");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PARENT_UUID, Optional.empty());
        this.entityData.define(TAIL, false);
        this.entityData.define(BODYINDEX, 0);
    }

    @Nullable
    public UUID getParentId() {
        return this.entityData.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public void setInitialPartPos(Entity parent) {
        this.setPos(parent.xo + this.radius * Math.cos(parent.getYRot() * Mth.DEG_TO_RAD + this.angleYaw), parent.yo + this.offsetY, parent.zo + this.radius * Math.sin(parent.getYRot() * Mth.DEG_TO_RAD + this.angleYaw));
    }

    @Override
    public void tick() {
        isInsidePortal = false;
        if (this.tickCount > 10) {
            Entity parent = getParent();
            refreshDimensions();
            if (parent != null && !this.level().isClientSide) {
                this.setNoGravity(true);
                this.setPos(parent.xo + this.radius * Math.cos(parent.yRotO * Mth.DEG_TO_RAD + this.angleYaw), parent.yo + this.offsetY, parent.zo + this.radius * Math.sin(parent.yRotO * Mth.DEG_TO_RAD + this.angleYaw));
                final double d0 = parent.getX() - this.getX();
                final double d1 = parent.getY() - this.getY();
                final double d2 = parent.getZ() - this.getZ();
                final float f2 = -((float) (Mth.atan2(d1, Mth.sqrt((float)(d0 * d0 + d2 * d2))) * Mth.RAD_TO_DEG));
                this.setXRot(this.limitAngle(this.getXRot(), f2, 5.0F));
                this.markHurt();
                this.setYRot(parent.yRotO);
                this.yHeadRot = this.getYRot();
                this.yBodyRot = this.yRotO;
                if (parent instanceof LivingEntity) {
                    if(!this.level().isClientSide && (((LivingEntity) parent).hurtTime > 0 || ((LivingEntity) parent).deathTime > 0)){
                        AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), 0));
                        this.hurtTime = ((LivingEntity) parent).hurtTime;
                        this.deathTime = ((LivingEntity) parent).deathTime;
                    }
                }
                this.pushEntities();
                if (parent.isRemoved() && !this.level().isClientSide) {
                    this.remove(RemovalReason.DISCARDED);
                }
            } else if (tickCount > 20 && !this.level().isClientSide) {
                remove(RemovalReason.DISCARDED);
            }
        }
        super.tick();
    }

    public Entity getParent() {
        UUID id = getParentId();
        if (id != null && !this.level().isClientSide) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

    public void setParent(Entity entity) {
        this.setParentId(entity.getUUID());
    }

    @Override
    public boolean is(net.minecraft.world.entity.Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void pushEntities() {
        List<net.minecraft.world.entity.Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityBoneSerpentPart) && entity.isPushable()).forEach(entity -> entity.push(parent));
        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        Entity parent = getParent();

        return parent != null ? parent.interact(player, hand) : InteractionResult.PASS;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        final Entity parent = getParent();
        final boolean prev = parent != null && parent.hurt(source, damage * this.damageMultiplier);
        if (prev && !this.level().isClientSide) {
            AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), damage * this.damageMultiplier));
        }
        return prev;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {

    }

    public boolean isTail() {
        return this.entityData.get(TAIL);
    }

    public void setTail(boolean tail) {
        this.entityData.set(TAIL, tail);
    }

    public int getBodyIndex() {
        return this.entityData.get(BODYINDEX);
    }

    public void setBodyIndex(int index) {
        this.entityData.set(BODYINDEX, index);
    }

    public boolean shouldNotExist() {
        Entity parent = getParent();
        return !parent.isAlive();
    }

    @Override
    public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
        if (parent.deathTime > 0) {
            this.deathTime = parent.deathTime;
        }
        if (parent.hurtTime > 0) {
            this.hurtTime = parent.hurtTime;
        }
    }

    public boolean shouldContinuePersisting() {
        return isAddedToWorld() || this.isRemoved();
    }
}
