package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityVoidWormPart extends LivingEntity implements IHurtableMultipart {

    protected static final EntityDimensions SIZE_BASE = EntityDimensions.scalable(1.2F, 1.95F);
    protected static final EntityDimensions TAIL_SIZE = EntityDimensions.scalable(1.6F, 2F);
    private static final EntityDataAccessor<Boolean> TAIL = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> WORM_SCALE = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WORM_YAW = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WORM_ANGLE = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> PORTAL_TICKS = SynchedEntityData.defineId(EntityVoidWormPart.class, EntityDataSerializers.INT);
    public EntityDimensions multipartSize;
    public float prevWormAngle;
    protected float radius;
    protected float angleYaw;
    protected float offsetY;
    protected float damageMultiplier = 1;
    private float prevWormYaw = 0;
    private Vec3 teleportPos = null;
    private Vec3 enterPos = null;
    private boolean doesParentControlPos = false;

    public EntityVoidWormPart(EntityType t, Level world) {
        super(t, world);
        multipartSize = t.getDimensions();
    }

    public EntityVoidWormPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
        super(t, parent.level);
        this.setParent(parent);
        this.radius = radius;
        this.angleYaw = (angleYaw + 90.0F) * ((float) Math.PI / 180.0F);
        this.offsetY = offsetY;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    public void push(Entity entityIn) {

    }

    public void kill() {
        this.remove(RemovalReason.DISCARDED);
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isTail() ? TAIL_SIZE.scale(getScale()) : super.getDimensions(poseIn);
    }

    public float getWormScale() {
        return entityData.get(WORM_SCALE);
    }

    public void setWormScale(float scale) {
        this.entityData.set(WORM_SCALE, scale);
    }

    public float getScale() {
        return getWormScale() + 0.5F;
    }

    public boolean startRiding(Entity entityIn) {
        if (!(entityIn instanceof AbstractMinecart || entityIn instanceof Boat)) {
            return super.startRiding(entityIn);
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.OUT_OF_WORLD || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.LAVA || source.isFire() || super.isInvulnerableTo(source);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getParentId() != null) {
            compound.putUUID("ParentUUID", this.getParentId());
        }
        if (this.getChildId() != null) {
            compound.putUUID("ChildUUID", this.getChildId());
        }
        compound.putBoolean("TailPart", isTail());
        compound.putInt("BodyIndex", getBodyIndex());
        compound.putInt("PortalTicks", getPortalTicks());
        compound.putFloat("PartAngle", angleYaw);
        compound.putFloat("WormScale", this.getWormScale());
        compound.putFloat("PartRadius", radius);
        compound.putFloat("PartYOffset", offsetY);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("ParentUUID")) {
            this.setParentId(compound.getUUID("ParentUUID"));
        }
        if (compound.hasUUID("ChildUUID")) {
            this.setChildId(compound.getUUID("ChildUUID"));
        }
        this.setTail(compound.getBoolean("TailPart"));
        this.setBodyIndex(compound.getInt("BodyIndex"));
        this.setPortalTicks(compound.getInt("PortalTicks"));
        this.angleYaw = compound.getFloat("PartAngle");
        this.setWormScale(compound.getFloat("WormScale"));
        this.radius = compound.getFloat("PartRadius");
        this.offsetY = compound.getFloat("PartYOffset");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PARENT_UUID, Optional.empty());
        this.entityData.define(CHILD_UUID, Optional.empty());
        this.entityData.define(TAIL, false);
        this.entityData.define(BODYINDEX, 0);
        this.entityData.define(WORM_SCALE, 1F);
        this.entityData.define(WORM_YAW, 0F);
        this.entityData.define(WORM_ANGLE, 0F);
        this.entityData.define(PORTAL_TICKS, 0);
    }

    @Nullable
    public UUID getParentId() {
        return this.entityData.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public void setInitialPartPos(Entity parent) {
        this.setPos(parent.xo + this.radius * Math.cos(parent.getYRot() * (Math.PI / 180.0F) + this.angleYaw), parent.yo + this.offsetY, parent.zo + this.radius * Math.sin(parent.getYRot() * (Math.PI / 180.0F) + this.angleYaw));
    }

    public float getWormAngle() {
        return this.entityData.get(WORM_ANGLE);
    }

    public void setWormAngle(float progress) {
        this.entityData.set(WORM_ANGLE, progress);
    }

    public int getPortalTicks() {
        return this.entityData.get(PORTAL_TICKS).intValue();
    }

    public void setPortalTicks(int ticks) {
        this.entityData.set(PORTAL_TICKS, Integer.valueOf(ticks));
    }

    @Override
    public void tick() {
        isInsidePortal = false;
        prevWormAngle = this.getWormAngle();
        prevWormYaw = this.entityData.get(WORM_YAW);
        this.setDeltaMovement(Vec3.ZERO);
        radius = 1.0F + (this.getWormScale() * (this.isTail() ? 0.65F : 0.3F)) + (this.getBodyIndex() == 0 ? 0.8F : 0);
        if (this.tickCount > 3) {
            Entity parent = getParent();
            refreshDimensions();
            if (parent != null && !level.isClientSide) {
                this.setNoGravity(true);
                Vec3 parentVec = parent.position().subtract(parent.xo, parent.yo, parent.zo);
                double restrictRadius = Mth.clamp(radius - parentVec.lengthSqr() * 0.25F, radius * 0.5F, radius);
                if (parent instanceof EntityVoidWorm) {
                    restrictRadius *= (isTail() ? 0.8F : 0.4F);
                }
                double x = parent.getX() + restrictRadius * Math.cos(parent.getYRot() * (Math.PI / 180.0F) + this.angleYaw);
                double yStretch = Math.abs(parent.getY() - parent.yo) > this.getBbWidth() ? parent.getY() : parent.yo;
                double y = yStretch + this.offsetY * getWormScale();
                double z = parent.getZ() + restrictRadius * Math.sin(parent.getYRot() * (Math.PI / 180.0F) + this.angleYaw);

                double d0 = parent.xo - this.getX();
                double d1 = parent.yo - this.getY();
                double d2 = parent.zo - this.getZ();
                float yaw = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                float pitch = parent.getXRot();
                if (this.getPortalTicks() <= 1 && !doesParentControlPos) {
                    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                    float f2 = -((float) (Mth.atan2(d1, Mth.sqrt((float) (d0 * d0 + d2 * d2))) * (double) (180F / (float) Math.PI)));
                    this.setPos(x, y, z);
                    this.setXRot(this.limitAngle(this.getXRot(), f2, 5.0F));
                    this.setYRot(yaw);
                    this.entityData.set(WORM_YAW, getYRot());
                }
                this.markHurt();
                this.yHeadRot = this.getYRot();
                this.yBodyRot = pitch;
                if (parent instanceof LivingEntity) {
                    if (!level.isClientSide && (((LivingEntity) parent).hurtTime > 0 || ((LivingEntity) parent).deathTime > 0)) {
                        AlexsMobs.sendMSGToAll(new MessageHurtMultipart(this.getId(), parent.getId(), 0));
                        this.hurtTime = ((LivingEntity) parent).hurtTime;
                        this.deathTime = ((LivingEntity) parent).deathTime;
                    }
                }
                this.pushEntities();
                if (parent.isRemoved() && !level.isClientSide) {
                    this.remove(RemovalReason.DISCARDED);
                }
                if (parent instanceof EntityVoidWorm) {
                    this.setWormAngle(((EntityVoidWorm) parent).prevWormAngle);
                } else if (parent instanceof EntityVoidWormPart) {
                    this.setWormAngle(((EntityVoidWormPart) parent).prevWormAngle);
                }
            } else if (tickCount > 20 && !level.isClientSide) {
                remove(RemovalReason.DISCARDED);
            }
        }
        if (tickCount % 400 == 0) {
            this.heal(1);
        }
        super.tick();
        if (doesParentControlPos && enterPos != null) {
            this.teleportTo(enterPos.x, enterPos.y, enterPos.z);
        }
        if (this.getPortalTicks() > 0) {
            this.setPortalTicks(this.getPortalTicks() - 1);
            if (this.getPortalTicks() <= 5 && teleportPos != null) {
                Vec3 vec = teleportPos;
                this.teleportTo(vec.x, vec.y, vec.z);
                xOld = vec.x;
                yOld = vec.y;
                zOld = vec.z;
                if (this.getPortalTicks() == 5 && this.getChild() instanceof EntityVoidWormPart) {
                    ((EntityVoidWormPart) this.getChild()).teleportTo(enterPos, teleportPos);
                }
                teleportPos = null;
            } else if (this.getPortalTicks() > 5 && enterPos != null) {
                this.teleportTo(enterPos.x, enterPos.y, enterPos.z);
            }
            if (this.getPortalTicks() == 0) {
                doesParentControlPos = false;
            }
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.DISCARDED); //Forge keep data until we revive player
            for (int i = 0; i < 30; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(AMParticleRegistry.WORM_PORTAL, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), d0, d1, d2);
            }
        }

    }

    public void die(DamageSource cause) {
        EntityVoidWorm worm = this.getWorm();
        if (worm != null) {
            int segments = Math.max(worm.getSegmentCount() / 2 - 1, 1);
            worm.setSegmentCount(segments);
            if (this.getChild() instanceof EntityVoidWormPart) {
                EntityVoidWormPart segment = (EntityVoidWormPart) this.getChild();
                EntityVoidWorm worm2 = AMEntityRegistry.VOID_WORM.create(level);
                worm2.copyPosition(this);
                segment.copyPosition(this);
                worm2.setChildId(segment.getUUID());
                worm2.setSegmentCount(segments);
                segment.setParent(worm2);
                if (!level.isClientSide) {
                    level.addFreshEntity(worm2);
                }
                worm2.setSplitter(true);
                worm2.setMaxHealth(worm.getMaxHealth() / 2F, true);
                worm2.setSplitFromUuid(worm.getUUID());
                worm2.setWormSpeed((float) Mth.clamp(worm.getWormSpeed() * 0.8, 0.4F, 1F));
                worm2.resetWormScales();
                if (!level.isClientSide) {
                    if (cause != null && cause.getEntity() instanceof ServerPlayer) {
                        AMAdvancementTriggerRegistry.VOID_WORM_SPLIT.trigger((ServerPlayer) cause.getEntity());
                    }
                }
            }
            worm.resetWormScales();
        }
    }

    public boolean isAlliedTo(Entity entityIn) {
        EntityVoidWorm worm = this.getWorm();
        return super.isAlliedTo(entityIn) || worm != null && worm.isAlliedTo(entityIn);
    }

    public EntityVoidWorm getWorm() {
        Entity parent = this.getParent();
        while (parent instanceof EntityVoidWormPart) {
            parent = ((EntityVoidWormPart) parent).getParent();
        }
        if (parent instanceof EntityVoidWorm) {
            return (EntityVoidWorm) parent;
        }
        return null;
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return null;
    }

    public Entity getParent() {
        UUID id = getParentId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
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
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void pushEntities() {
        List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> !entity.is(parent) && !(entity instanceof EntityVoidWormPart) && entity.isPushable()).forEach(entity -> entity.push(parent));

        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        Entity parent = getParent();

        return parent != null ? parent.interact(player, hand) : InteractionResult.PASS;
    }

    public boolean isHurt() {
        return this.getHealth() <= getHealthThreshold();
    }

    public double getHealthThreshold() {
        return 5D;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (super.hurt(source, damage)) {
            EntityVoidWorm worm = this.getWorm();
            if (worm != null) {
                worm.playHurtSoundWorm(source);
            }
            return true;
        }
        return false;
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
        return this.entityData.get(TAIL).booleanValue();
    }

    public void setTail(boolean tail) {
        this.entityData.set(TAIL, Boolean.valueOf(tail));
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
    public void onAttackedFromServer(LivingEntity parent, float damage) {
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

    public float getWormYaw(float partialTicks) {
        return partialTicks == 0 ? entityData.get(WORM_YAW) : prevWormYaw + (entityData.get(WORM_YAW) - prevWormYaw) * partialTicks;
    }

    public void teleportTo(Vec3 enterPos, Vec3 to) {
        this.setPortalTicks(10);
        teleportPos = to;
        this.enterPos = enterPos;
        EntityVoidWorm worm = this.getWorm();
        if (worm != null) {
            if (this.getChild() == null) {
                worm.fullyThrough = true;
            }
        }
    }

}
