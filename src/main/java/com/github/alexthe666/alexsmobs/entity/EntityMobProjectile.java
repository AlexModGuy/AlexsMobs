package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class EntityMobProjectile extends Entity {
    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;

    public EntityMobProjectile(EntityType type, Level level) {
        super(type, level);
    }

    public EntityMobProjectile(EntityType type, Level worldIn, Mob shooter) {
        this(type, worldIn);
        this.setShooter(shooter);
    }

    protected Vec3 calcOffsetVec(Vec3 offset, float xRot, float yRot){
        return offset.xRot(xRot * Mth.DEG_TO_RAD).yRot(-yRot * Mth.DEG_TO_RAD);
    }

    protected static float lerpRotation(float f, float f1) {
        while (f1 - f < -180.0F) {
            f -= 360.0F;
        }

        while (f1 - f >= 180.0F) {
            f += 360.0F;
        }

        return Mth.lerp(0.2F, f, f1);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {

    }

    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        this.doBehavior();
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            this.onImpact(raytraceresult);
        }
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.updateRotation();
        if (this.isInWall() && (!isInWater() || removeInWater())) {
            this.remove(RemovalReason.DISCARDED);
        } else if (this.isInWaterOrBubble() && this.removeInWater()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.setDeltaMovement(vector3d.scale(0.99F));
            this.setPos(d0, d1, d2);
        }
    }

    protected boolean removeInWater(){
        return true;
    }

    public abstract void doBehavior();

    protected void onEntityHit(EntityHitResult result) {
        Entity entity = this.getShooter();
        if (entity instanceof LivingEntity) {
            boolean b = result.getEntity().hurt(damageSources().mobProjectile(this, (LivingEntity) entity), this.getDamage());

        }
        this.remove(RemovalReason.DISCARDED);
    }

    protected abstract float getDamage();

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        BlockState blockstate = this.level().getBlockState(p_230299_1_.getBlockPos());
        if (!this.level().isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Nullable
    public Entity getShooter() {
        if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            return ((ServerLevel) this.level()).getEntity(this.ownerUUID);
        } else {
            return this.ownerNetworkId != 0 ? this.level().getEntity(this.ownerNetworkId) : null;
        }
    }

    public void setShooter(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.ownerUUID = entityIn.getUUID();
            this.ownerNetworkId = entityIn.getId();
        }

    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }

        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }

    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }

        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getShooter();
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
        Vec3 vector3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy).scale(velocity);
        this.setDeltaMovement(vector3d);
        float f = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
        this.setYRot((float) (Mth.atan2(vector3d.x, vector3d.z) * (double) Mth.RAD_TO_DEG));
        this.setXRot((float) (Mth.atan2(vector3d.y, f) * (double) Mth.RAD_TO_DEG));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        float f = -Mth.sin(p_234612_3_ * Mth.DEG_TO_RAD) * Mth.cos(p_234612_2_ * Mth.DEG_TO_RAD);
        float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(p_234612_3_ * Mth.DEG_TO_RAD) * Mth.cos(p_234612_2_ * Mth.DEG_TO_RAD);
        this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
        Vec3 vector3d = p_234612_1_.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.onGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
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
            float f = Mth.sqrt((float) (x * x + z * z));
            this.setXRot((float) (Mth.atan2(y, f) * (double) Mth.RAD_TO_DEG));
            this.setYRot((float) (Mth.atan2(x, z) * (double) Mth.RAD_TO_DEG));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean canHitEntity(Entity inQuestion) {
        if (!inQuestion.isSpectator() && inQuestion.isAlive() && inQuestion.isPickable()) {
            Entity entity = this.getShooter();
            return (entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(inQuestion)) && (entity == null || inQuestion == null || !isSameTeam(entity, inQuestion));
        } else {
            return false;
        }
    }

    public boolean isSameTeam(Entity shooter, Entity entity) {
        if(shooter instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()){
            if(entity instanceof TamableAnimal alsoTameable && alsoTameable.isTame()){
                if(alsoTameable.getOwnerUUID() != null && tamableAnimal.getOwnerUUID() != null && tamableAnimal.getOwnerUUID().equals(alsoTameable.getOwnerUUID())){
                    return true;
                }
            }
            return tamableAnimal.getOwnerUUID() != null && tamableAnimal.getOwnerUUID().equals(entity.getUUID()) || shooter.isAlliedTo(entity);
        }
        return shooter.isAlliedTo(entity);
    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float) vector3d.horizontalDistance());
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * (double) Mth.RAD_TO_DEG)));
        this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) Mth.RAD_TO_DEG)));
    }
}

