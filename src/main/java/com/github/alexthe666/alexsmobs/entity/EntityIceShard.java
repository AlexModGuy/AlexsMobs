package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityIceShard extends Entity {
    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;

    public EntityIceShard(EntityType p_i50162_1_, Level p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntityIceShard(Level worldIn, EntityFroststalker stalker) {
        this(AMEntityRegistry.ICE_SHARD.get(), worldIn);
        this.setShooter(stalker);
        this.setPos(stalker.getRandomX(0.5F), stalker.getEyeY() + (double)0.1F, stalker.getRandomZ(0.5F));
    }

    @OnlyIn(Dist.CLIENT)
    public EntityIceShard(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(AMEntityRegistry.ICE_SHARD.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    public EntityIceShard(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.ICE_SHARD.get(), world);
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

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        if(level.isClientSide && random.nextInt(2) == 0){
            float r1 = (random.nextFloat() - 0.5F) * 0.5F;
            float r2 = (random.nextFloat() - 0.5F) * 0.5F;
            float r3 = (random.nextFloat() - 0.5F) * 0.5F;
            this.level.addParticle(ParticleTypes.SNOWFLAKE, this.getX() + r1, this.getY() + r2, this.getZ() + r3, r1 * 0.1F, r2 * 0.1F, r3 * 0.1F);
        }
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            this.onImpact(raytraceresult);
        }

        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;

        this.updateRotation();
        if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove(RemovalReason.DISCARDED);
        } else if (this.isInWaterOrBubble()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.setDeltaMovement(vector3d.scale(0.99F));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.02F, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
    }

    protected void onEntityHit(EntityHitResult p_213868_1_) {
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity && p_213868_1_.getEntity() != getOwner() && !(p_213868_1_.getEntity() instanceof EntityFroststalker)) {
            p_213868_1_.getEntity().hurt(DamageSource.indirectMobAttack(this, (LivingEntity) entity).setProjectile(), 2.0F + random.nextFloat() * 3.0F);
        }
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        // TODO Check :: Unused?
//        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        if (!this.level.isClientSide()) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    protected void defineSynchedData() {
    }

    public void setShooter(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.ownerUUID = entityIn.getUUID();
            this.ownerNetworkId = entityIn.getId();
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            return ((ServerLevel) this.level).getEntity(this.ownerUUID);
        } else {
            return this.ownerNetworkId != 0 ? this.level.getEntity(this.ownerNetworkId) : null;
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

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }

        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for (Entity entity1 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_234613_0_) -> {
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
        float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        this.setYRot( (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) Mth.RAD_TO_DEG));
        this.setXRot((float) (Mth.atan2(vector3d.y, f) * (double) Mth.RAD_TO_DEG));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        final float f0 = p_234612_3_ * Mth.DEG_TO_RAD;
        final float f = -Mth.sin(f0) * Mth.cos(p_234612_2_ * Mth.DEG_TO_RAD);
        final float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * Mth.DEG_TO_RAD);
        final float f2 = Mth.cos(f0) * Mth.cos(p_234612_2_ * Mth.DEG_TO_RAD);
        this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
        Vec3 vector3d = p_234612_1_.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, p_234612_1_.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
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
            float f = Mth.sqrt((float)(x * x + z * z));
            this.setXRot((float) (Mth.atan2(y, f) * (double) Mth.RAD_TO_DEG));
            this.setYRot( (float) (Mth.atan2(x, z) * (double) Mth.RAD_TO_DEG));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean canHitEntity(Entity p_230298_1_) {
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
            Entity entity = this.getOwner();
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_);
        } else {
            return false;
        }
    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * (double) Mth.RAD_TO_DEG)));
        this.setYRot( lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) Mth.RAD_TO_DEG)));
    }
}
