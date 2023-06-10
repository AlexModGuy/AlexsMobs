package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

import java.util.List;

public class EntityGust extends Entity {
    protected static final EntityDataAccessor<Boolean> VERTICAL = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> X_DIR = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> Y_DIR = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> Z_DIR = SynchedEntityData.defineId(EntityGust.class, EntityDataSerializers.FLOAT);
    private Entity pushedEntity = null;

    public EntityGust(EntityType p_i50162_1_, Level p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntityGust(Level worldIn) {
        this(AMEntityRegistry.GUST.get(), worldIn);
    }

    public EntityGust(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.GUST.get(), world);
    }

    public void push(Entity entityIn) {

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
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        if(this.tickCount > 300){
            this.remove(RemovalReason.DISCARDED);
        }
        for (int i = 0; i < 1 + random.nextInt(1); ++i) {
            level.addParticle(AMParticleRegistry.GUSTER_SAND_SPIN.get(), this.getX() + 0.5F * (random.nextFloat() - 0.5F), this.getY() + 0.5F * (random.nextFloat() - 0.5F), this.getZ() + 0.5F * (random.nextFloat() - 0.5F), this.getX(), this.getY() + 0.5F, this.getZ());
        }
        Vec3 vector3d = new Vec3(this.entityData.get(X_DIR), this.entityData.get(Y_DIR), this.entityData.get(Z_DIR));
        HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && tickCount > 4) {
            this.onImpact(raytraceresult);
        }
        List<Entity> list = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.1));

        if(pushedEntity != null && this.distanceTo(pushedEntity) > 2){
            pushedEntity = null;
        }
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        if(this.getY() > this.level().getMaxBuildHeight()){
            this.remove(RemovalReason.DISCARDED);
        }
        this.updateRotation();
        float f = 0.99F;
        float f1 = 0.06F;
         if (this.isInWaterOrBubble()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            this.setDeltaMovement(vector3d);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.06F, 0.0D));
            this.setPos(d0, d1, d2);
            if(pushedEntity != null){
                pushedEntity.setDeltaMovement(this.getDeltaMovement().add(0, 0.063, 0));
            }
            for(Entity e : list){
                e.setDeltaMovement(this.getDeltaMovement().add(0, 0.068, 0));
                if(e.getDeltaMovement().y < 0){
                    e.setDeltaMovement(e.getDeltaMovement().multiply(1, 0, 1));
                }
                e.fallDistance = 0;
            }
        }
    }

    public void setGustDir(float x, float y, float z){
        this.entityData.set(X_DIR, x);
        this.entityData.set(Y_DIR, y);
        this.entityData.set(Z_DIR, z);
    }

    public float getGustDir(int xyz){
       return this.entityData.get(xyz == 2 ? Z_DIR : xyz == 1 ? Y_DIR : X_DIR);
    }

    protected void onEntityHit(EntityHitResult result) {
        Entity entity = result.getEntity();
        if(entity instanceof EntityGust){
            EntityGust other = (EntityGust)entity;
            double avgX = (other.getX() + this.getX()) / 2F;
            double avgY = (other.getY() + this.getY()) / 2F;
            double avgZ = (other.getZ() + this.getZ()) / 2F;
            other.setPos(avgX, avgY, avgZ);
            other.setGustDir(other.getGustDir(0) + this.getGustDir(0), other.getGustDir(1) + this.getGustDir(1), other.getGustDir(2) + this.getGustDir(2));
            if(this.isAlive() && other.isAlive()){
                this.remove(RemovalReason.DISCARDED);
            }
        }else if(entity != null){
            pushedEntity = entity;
        }
    }


    protected boolean canHitEntity(Entity p_230298_1_) {
        return !p_230298_1_.isSpectator();
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        if( p_230299_1_.getBlockPos() != null){
            BlockPos pos = p_230299_1_.getBlockPos();
            if(level().getBlockState(pos).getMaterial().isSolid()){
                if (!this.level().isClientSide) {
                    this.remove(RemovalReason.DISCARDED);

                }
            }
        }

    }

    protected void defineSynchedData() {
        this.entityData.define(VERTICAL, false);
        this.entityData.define(X_DIR, 0f);
        this.entityData.define(Y_DIR, 0F);
        this.entityData.define(Z_DIR, 0F);
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("VerticalTornado", getVertical());
        compound.putFloat("GustDirX", this.entityData.get(X_DIR));
        compound.putFloat("GustDirY", this.entityData.get(Y_DIR));
        compound.putFloat("GustDirZ", this.entityData.get(Z_DIR));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(X_DIR, compound.getFloat("GustDirX"));
        this.entityData.set(Y_DIR, compound.getFloat("GustDirX"));
        this.entityData.set(Z_DIR, compound.getFloat("GustDirX"));
        this.setVertical((compound.getBoolean("VerticalTornado")));
    }

    public void setVertical(boolean vertical){
        this.entityData.set(VERTICAL, vertical);
    }

    public boolean getVertical(){
        return this.entityData.get(VERTICAL);
    }

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
            this.setXRot((float) (Mth.atan2(y, f) * (double) (180F / (float) Math.PI)));
            this.setYRot( (float) (Mth.atan2(x, z) * (double) (180F / (float) Math.PI)));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI))));
        this.setYRot( lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI))));
    }
}
