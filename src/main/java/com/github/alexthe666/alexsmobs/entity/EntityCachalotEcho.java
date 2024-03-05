package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityCachalotEcho extends Entity {
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(EntityCachalotEcho.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FASTER_ANIM = SynchedEntityData.defineId(EntityCachalotEcho.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GREEN = SynchedEntityData.defineId(EntityCachalotEcho.class, EntityDataSerializers.BOOLEAN);
    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;
    private boolean playerLaunched = false;

    public EntityCachalotEcho(EntityType p_i50162_1_, Level p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntityCachalotEcho(Level worldIn, EntityCachalotWhale p_i47273_2_) {
        this(AMEntityRegistry.CACHALOT_ECHO.get(), worldIn);
        this.setShooter(p_i47273_2_);
    }

    public EntityCachalotEcho(Level worldIn, LivingEntity p_i47273_2_, boolean right, boolean green) {
        this(AMEntityRegistry.CACHALOT_ECHO.get(), worldIn);
        this.setShooter(p_i47273_2_);
        float rot = p_i47273_2_.yHeadRot + (right ? 90 : -90);
        playerLaunched = true;
        this.setGreen(green);
        this.setFasterAnimation(true);
        this.setPos(p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth()) * 0.5D * (double) Mth.sin(rot * Mth.DEG_TO_RAD), p_i47273_2_.getY() + 1D, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth()) * 0.5D * (double) Mth.cos(rot * Mth.DEG_TO_RAD));
    }

    @OnlyIn(Dist.CLIENT)
    public EntityCachalotEcho(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(AMEntityRegistry.CACHALOT_ECHO.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    public EntityCachalotEcho(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.CACHALOT_ECHO.get(), world);
    }

    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while (p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while (p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
    }

    public boolean isReturning() {
        return this.entityData.get(RETURNING);
    }

    public void setReturning(boolean returning) {
        this.entityData.set(RETURNING, returning);
    }

    public boolean isFasterAnimation() {
        return this.entityData.get(FASTER_ANIM);
    }

    public void setFasterAnimation(boolean anim) {
        this.entityData.set(FASTER_ANIM, anim);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        final double yMot = Mth.sqrt((float)(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z));
        this.setXRot((float) (Mth.atan2(this.getDeltaMovement().y, yMot) * Mth.RAD_TO_DEG));
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        super.tick();
        final Vec3 vector3d = this.getDeltaMovement();
        final HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (raytraceresult.getType() != HitResult.Type.MISS) {
            this.onImpact(raytraceresult);
        }
        final Entity shooter = this.getOwner();
        if (this.isReturning() && shooter instanceof final EntityCachalotWhale whale) {
            if(whale.headPart.distanceTo(this) < whale.headPart.getBbWidth()){
                remove(RemovalReason.DISCARDED);
                whale.recieveEcho();
            }
        }
        if (!playerLaunched && !this.level().isClientSide && !this.isInWaterOrBubble()) {
            remove(RemovalReason.DISCARDED);
        }
        if (this.tickCount > 100) {
            remove(RemovalReason.DISCARDED);
        }

        final double d0 = this.getX() + vector3d.x;
        final double d1 = this.getY() + vector3d.y;
        final double d2 = this.getZ() + vector3d.z;

        this.updateRotation();
        if (playerLaunched) {
            this.noPhysics = true;
        }
        this.setDeltaMovement(vector3d.scale(0.99F));
        this.setNoGravity(true);
        this.setPos(d0, d1, d2);
        this.setYRot((float) (Mth.atan2(vector3d.x, vector3d.z) * Mth.RAD_TO_DEG) - 90);
    }

    protected void onEntityHit(EntityHitResult result) {
        final Entity entity = this.getOwner();
        if (isReturning()) {
            EntityCachalotWhale whale = null;
            if (entity instanceof EntityCachalotWhale) {
                whale = (EntityCachalotWhale) entity;
                if (result.getEntity() instanceof EntityCachalotWhale || result.getEntity() instanceof EntityCachalotPart) {
                    whale.recieveEcho();
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        } else if (result.getEntity() != entity && !result.getEntity().is(entity)) {
            this.setReturning(true);
            if (entity instanceof EntityCachalotWhale) {
                final Vec3 vec = ((EntityCachalotWhale) entity).getReturnEchoVector();
                final double d0 = vec.x() - this.getX();
                final double d1 = vec.y() - this.getY();
                final double d2 = vec.z() - this.getZ();
                this.setDeltaMovement(Vec3.ZERO);
                final EntityCachalotEcho echo = new EntityCachalotEcho(this.level(), ((EntityCachalotWhale) entity));
                echo.copyPosition(this);
                this.remove(RemovalReason.DISCARDED);
                echo.setReturning(true);
                echo.shoot(d0, d1, d2, 1, 0);
                if (!this.level().isClientSide) {
                    level().addFreshEntity(echo);
                }
            }
        }
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        if (!this.level().isClientSide && !playerLaunched) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    protected void defineSynchedData() {
        this.entityData.define(RETURNING, false);
        this.entityData.define(FASTER_ANIM, false);
        this.entityData.define(GREEN, false);
    }

    public void setShooter(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.ownerUUID = entityIn.getUUID();
            this.ownerNetworkId = entityIn.getId();
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            return ((ServerLevel) this.level()).getEntity(this.ownerUUID);
        } else {
            return this.ownerNetworkId != 0 ? this.level().getEntity(this.ownerNetworkId) : null;
        }
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }

        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }
        compound.putBoolean("Green", isGreen());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }
        this.setGreen(compound.getBoolean("Green"));
        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for (Entity entity1 : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_234613_0_) -> {
                return !p_234613_0_.isSpectator() && p_234613_0_.isPickable();
            })) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        final Vec3 vector3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * 0.0075D * (double) inaccuracy, this.random.nextGaussian() * 0.0075D * (double) inaccuracy, this.random.nextGaussian() * 0.0075D * (double) inaccuracy).scale(velocity);
        this.setDeltaMovement(vector3d);
        final float f = Mth.sqrt((float) horizontalMag(vector3d));
        this.setYRot((float) (Mth.atan2(vector3d.x, vector3d.z) * Mth.RAD_TO_DEG));
        this.setXRot((float) (Mth.atan2(vector3d.y, f) * Mth.RAD_TO_DEG));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    private double horizontalMag(Vec3 vector3d) {
        return vector3d.x * vector3d.x + vector3d.z * vector3d.z;
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        final float f3 = p_234612_3_ * Mth.DEG_TO_RAD;
        final float f0 = Mth.cos(p_234612_2_ * Mth.DEG_TO_RAD);
        final float f = -Mth.sin(f3) * f0;
        final float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * Mth.DEG_TO_RAD);
        final float f2 = Mth.cos(f3) * f0;
        this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
        Vec3 vector3d = p_234612_1_.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.onGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
        if(playerLaunched){
            return;
        }
        if (raytraceresult$type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) result);
        } else if (raytraceresult$type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            final float f = Mth.sqrt((float)(x * x + z * z));
            this.setXRot((float) (Mth.atan2(y, f) * Mth.RAD_TO_DEG));
            this.setYRot((float) (Mth.atan2(x, z) * Mth.RAD_TO_DEG));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean canHitEntity(Entity p_230298_1_) {
        if(playerLaunched){
            return false;
        }
        if (this.isReturning()) {
            return p_230298_1_ instanceof EntityCachalotPart || p_230298_1_ instanceof EntityCachalotWhale;
        } else if (p_230298_1_ instanceof EntityCachalotPart) {
            return false;
        }
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
            Entity entity = this.getOwner();
            return (entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_));
        } else {
            return false;
        }
    }

    protected void updateRotation() {
        final Vec3 vector3d = this.getDeltaMovement();
        final float f = Mth.sqrt((float)horizontalMag(vector3d));
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * Mth.RAD_TO_DEG)));
        this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * Mth.RAD_TO_DEG)));
    }

    public boolean isGreen() {
        return entityData.get(GREEN);
    }
    public void setGreen(boolean bool) {
        entityData.set(GREEN, bool);
    }
}