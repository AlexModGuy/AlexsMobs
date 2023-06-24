package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class EntitySandShot extends Entity {
    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntitySandShot.class, EntityDataSerializers.INT);

    public EntitySandShot(EntityType p_i50162_1_, Level p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntitySandShot(Level worldIn, EntityGuster p_i47273_2_) {
        this(AMEntityRegistry.SAND_SHOT.get(), worldIn);
        this.setShooter(p_i47273_2_);
        this.setPos(p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.35D * (double) Mth.sin(p_i47273_2_.yBodyRot * Mth.DEG_TO_RAD), p_i47273_2_.getEyeY() + (double) 0.2F, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.35D * (double) Mth.cos(p_i47273_2_.yBodyRot * Mth.DEG_TO_RAD));
    }

    public EntitySandShot(Level worldIn, LivingEntity p_i47273_2_, boolean right) {
        this(AMEntityRegistry.SAND_SHOT.get(), worldIn);
        this.setShooter(p_i47273_2_);
        float rot = p_i47273_2_.yHeadRot + (right ? 60 : -60);
        this.setPos(p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth()) * 0.5D * (double) Mth.sin(rot * Mth.DEG_TO_RAD), p_i47273_2_.getEyeY() - (double) 0.2F, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth()) * 0.5D * (double) Mth.cos(rot * Mth.DEG_TO_RAD));
    }

    @OnlyIn(Dist.CLIENT)
    public EntitySandShot(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(AMEntityRegistry.SAND_SHOT.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    public EntitySandShot(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.SAND_SHOT.get(), world);
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

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }
        ParticleOptions type = this.getVariant() == 2 ? AMParticleRegistry.GUSTER_SAND_SHOT_SOUL.get() : this.getVariant() == 1 ? AMParticleRegistry.GUSTER_SAND_SHOT_RED.get() : AMParticleRegistry.GUSTER_SAND_SHOT.get();
        for (int i = 0; i < 3 + random.nextInt(6); ++i) {
            double d0 = 0.1D + 0.3D * (double) i;
            level().addParticle(type, this.getX() + 0.25F * (random.nextFloat() - 0.5F), this.getY() + 0.25F * (random.nextFloat() - 0.5F), this.getZ() + 0.25F * (random.nextFloat() - 0.5F), this.getDeltaMovement().x * d0, this.getDeltaMovement().y, this.getDeltaMovement().z * d0);
        }
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            this.onImpact(raytraceresult);
        }

        this.updateRotation();
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove(RemovalReason.DISCARDED);
        } else if (this.isInWaterOrBubble()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            final double d0 = this.getX() + vector3d.x;
            final double d1 = this.getY() + vector3d.y;
            final double d2 = this.getZ() + vector3d.z;
            this.setDeltaMovement(vector3d.scale(0.99F));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03F, 0.0D));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03F, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
    }

    protected void onEntityHit(EntityHitResult p_213868_1_) {
        Entity entity = this.getSandShooter();
        if (entity instanceof LivingEntity) {
            p_213868_1_.getEntity().hurt(damageSources().mobProjectile(this, (LivingEntity) entity), 2.5F);
        }
        if (entity instanceof Player && p_213868_1_.getEntity() instanceof LivingEntity) {
            ((LivingEntity)p_213868_1_.getEntity()).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, true, false));
        }
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        BlockState blockstate = this.level().getBlockState(p_230299_1_.getBlockPos());
        if (!this.level().isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    protected void defineSynchedData() {
        this.entityData.define(VARIANT, 0);
    }

    public void setShooter(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.ownerUUID = entityIn.getUUID();
            this.ownerNetworkId = entityIn.getId();
        }

    }

    @Nullable
    public Entity getSandShooter() {
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
        Entity entity = this.getSandShooter();
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
        this.setYRot( (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) Mth.RAD_TO_DEG));
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
            this.setYRot( (float) (Mth.atan2(x, z) * (double) Mth.RAD_TO_DEG));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected boolean canHitEntity(Entity p_230298_1_) {
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
            Entity entity = this.getSandShooter();
            return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_);
        } else {
            return false;
        }
    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * (double) Mth.RAD_TO_DEG)));
        this.setYRot( lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) Mth.RAD_TO_DEG)));
    }
}
