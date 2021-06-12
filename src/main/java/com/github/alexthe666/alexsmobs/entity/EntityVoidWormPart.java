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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityVoidWormPart extends LivingEntity implements IHurtableMultipart {

    protected static final EntitySize SIZE_BASE = EntitySize.flexible(1.2F, 1.95F);
    private static final DataParameter<Boolean> TAIL = EntityDataManager.createKey(EntityVoidWormPart.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BODYINDEX = EntityDataManager.createKey(EntityVoidWormPart.class, DataSerializers.VARINT);
    private static final DataParameter<Float> WORM_SCALE = EntityDataManager.createKey(EntityVoidWormPart.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WORM_YAW = EntityDataManager.createKey(EntityVoidWormPart.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WORM_ANGLE = EntityDataManager.createKey(EntityVoidWormPart.class, DataSerializers.FLOAT);
    private static final DataParameter<Optional<UUID>> PARENT_UUID = EntityDataManager.createKey(EntityVoidWormPart.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Optional<UUID>> CHILD_UUID = EntityDataManager.createKey(EntityVoidWormPart.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected static final EntitySize TAIL_SIZE = EntitySize.flexible(1.6F, 2F);
    public EntitySize multipartSize;
    protected float radius;
    protected float angleYaw;
    protected float offsetY;
    protected float damageMultiplier = 1;
    private float prevWormYaw = 0;
    public float prevWormAngle;

    public EntityVoidWormPart(EntityType t, World world) {
        super(t, world);
        multipartSize = t.getSize();
    }

    public EntityVoidWormPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(t, parent.world);
        this.setParent(parent);
        this.radius = radius;
        this.angleYaw = (angleYaw + 90.0F) * ((float) Math.PI / 180.0F);
        this.offsetY = offsetY;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    public void applyEntityCollision(Entity entityIn) {

    }

    public EntitySize getSize(Pose poseIn) {
        return this.isTail() ? TAIL_SIZE.scale(getRenderScale()) : super.getSize(poseIn);
    }

    public float getWormScale() {
        return dataManager.get(WORM_SCALE);
    }

    public void setWormScale(float scale) {
        this.dataManager.set(WORM_SCALE, scale);
    }

    public float getRenderScale() {
        return getWormScale() + 0.5F;
    }

    public boolean startRiding(Entity entityIn) {
        if (!(entityIn instanceof AbstractMinecartEntity || entityIn instanceof BoatEntity)) {
            return super.startRiding(entityIn);
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.LAVA || source.isFireDamage() || super.isInvulnerableTo(source);
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
        if (this.getChildId() != null) {
            compound.putUniqueId("ChildUUID", this.getChildId());
        }
        compound.putBoolean("TailPart", isTail());
        compound.putInt("BodyIndex", getBodyIndex());
        compound.putFloat("PartAngle", angleYaw);
        compound.putFloat("WormScale", this.getWormScale());
        compound.putFloat("PartRadius", radius);
        compound.putFloat("PartYOffset", offsetY);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("ParentUUID")) {
            this.setParentId(compound.getUniqueId("ParentUUID"));
        }
        if (compound.hasUniqueId("ChildUUID")) {
            this.setChildId(compound.getUniqueId("ChildUUID"));
        }
        this.setTail(compound.getBoolean("TailPart"));
        this.setBodyIndex(compound.getInt("BodyIndex"));
        this.angleYaw = compound.getFloat("PartAngle");
        this.setWormScale(compound.getFloat("WormScale"));
        this.radius = compound.getFloat("PartRadius");
        this.offsetY = compound.getFloat("PartYOffset");
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PARENT_UUID, Optional.empty());
        this.dataManager.register(CHILD_UUID, Optional.empty());
        this.dataManager.register(TAIL, false);
        this.dataManager.register(BODYINDEX, 0);
        this.dataManager.register(WORM_SCALE, 1F);
        this.dataManager.register(WORM_YAW, 0F);
        this.dataManager.register(WORM_ANGLE, 0F);
    }

    @Nullable
    public UUID getParentId() {
        return this.dataManager.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.dataManager.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getChildId() {
        return this.dataManager.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.dataManager.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public void setInitialPartPos(Entity parent) {
        this.setPosition(parent.prevPosX + this.radius * Math.cos(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw), parent.prevPosY + this.offsetY, parent.prevPosZ + this.radius * Math.sin(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw));
    }

    public float getWormAngle() {
        return this.dataManager.get(WORM_ANGLE);
    }

    public void setWormAngle(float progress) {
        this.dataManager.set(WORM_ANGLE, progress);
    }

    @Override
    public void tick() {
        inPortal = false;
        prevWormAngle = this.getWormAngle();
        prevWormYaw = this.dataManager.get(WORM_YAW);
        this.setMotion(Vector3d.ZERO);
        if (this.ticksExisted > 3) {
            Entity parent = getParent();
            recalculateSize();
            if (parent != null && !world.isRemote) {
                this.setNoGravity(true);
                Vector3d parentVec = parent.getPositionVec().subtract(parent.prevPosX, parent.prevPosY, parent.prevPosZ);
                double restrictRadius = MathHelper.clamp(radius - parentVec.lengthSquared() * 0.25F, radius * 0.5F, radius);
                if(parent instanceof EntityVoidWorm){
                    restrictRadius *= (isTail() ? 0.8F : 0.4F);
                }
                double x = parent.getPosX() + restrictRadius * Math.cos(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw);
                double yStretch = Math.abs(parent.getPosY() - parent.prevPosY) > this.getWidth() ? parent.getPosY() : parent.prevPosY;
                double y = yStretch + this.offsetY * getWormScale();
                double z = parent.getPosZ() + restrictRadius * Math.sin(parent.rotationYaw * (Math.PI / 180.0F) + this.angleYaw);

                double d0 = parent.prevPosX  - this.getPosX();
                double d1 = parent.prevPosY - this.getPosY();
                double d2 = parent.prevPosZ  - this.getPosZ();
                float yaw = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                float pitch = parent.rotationPitch;
                this.setPosition(x, y, z);
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double) (180F / (float) Math.PI)));
                this.rotationPitch = this.limitAngle(this.rotationPitch, f2, 5.0F);
                this.markVelocityChanged();
                this.rotationYaw = yaw;
                this.dataManager.set(WORM_YAW, rotationYaw);
                this.rotationYawHead = this.rotationYaw;
                this.renderYawOffset = pitch;
                if (parent instanceof LivingEntity) {
                    if (!world.isRemote && (((LivingEntity) parent).hurtTime > 0 || ((LivingEntity) parent).deathTime > 0)) {
                        AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getEntityId(), parent.getEntityId(), 0));
                        this.hurtTime = ((LivingEntity) parent).hurtTime;
                        this.deathTime = ((LivingEntity) parent).deathTime;
                    }
                }
                //this.collideWithNearbyEntities();
                if (parent.removed && !world.isRemote) {
                    this.remove();
                }
                if(parent instanceof EntityVoidWorm){
                    this.setWormAngle(((EntityVoidWorm) parent).prevWormAngle);
                }else if(parent instanceof EntityVoidWormPart){
                    this.setWormAngle(((EntityVoidWormPart) parent).prevWormAngle);
                }
            } else if (ticksExisted > 20 && !world.isRemote) {
                remove();
            }
        }
        if(ticksExisted % 400 == 0){
            this.heal(1);
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

    public void onDeath(DamageSource cause) {
        EntityVoidWorm worm = this.getWorm();
        if(worm != null){
            int segments = Math.max(worm.getSegmentCount() / 2 - 1, 1);
            worm.setSegmentCount(segments);
            if(this.getChild() instanceof EntityVoidWormPart){
                EntityVoidWormPart segment = (EntityVoidWormPart)this.getChild();
                EntityVoidWorm worm2 = AMEntityRegistry.VOID_WORM.create(world);
                worm2.copyLocationAndAnglesFrom(this);
                segment.copyLocationAndAnglesFrom(this);
                worm2.setChildId(segment.getUniqueID());
                worm2.setSegmentCount(segments);
                segment.setParent(worm2);
                if(!world.isRemote){
                    world.addEntity(worm2);
                }
                worm2.setSplitter(true);
                worm2.setWormSpeed((float)MathHelper.clamp(worm.getWormSpeed() * 0.8, 0.4F, 1F));
                worm2.resetWormScales();
            }
            worm.resetWormScales();
        }
    }

    public void remove() {
        this.remove(false);
    }

    public EntityVoidWorm getWorm(){
        Entity parent = this.getParent();
        while(parent instanceof EntityVoidWormPart){
            parent = ((EntityVoidWormPart)parent).getParent();
        }
        if(parent instanceof EntityVoidWorm){
            return (EntityVoidWorm)parent;
        }
        return null;
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
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
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityVoidWormPart) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(parent));

        }
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        Entity parent = getParent();

        return parent != null ? parent.processInitialInteract(player, hand) : ActionResultType.PASS;
    }

    public boolean isHurt(){
        return this.getHealth() <= getHealthThreshold();
    }

    public double getHealthThreshold(){
        return 5D;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        return super.attackEntityFrom(source, damage);
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

    public float getWormYaw(float partialTicks) {
        return partialTicks == 0 ? dataManager.get(WORM_YAW) : prevWormYaw + (dataManager.get(WORM_YAW) - prevWormYaw) * partialTicks;
    }
}
