package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class EntityTossedItem extends ThrowableItemProjectile {

    protected static final EntityDataAccessor<Boolean> DART = SynchedEntityData.defineId(EntityTossedItem.class, EntityDataSerializers.BOOLEAN);

    public EntityTossedItem(EntityType p_i50154_1_, Level p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public EntityTossedItem(Level worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.TOSSED_ITEM, throwerIn, worldIn);
    }

    public EntityTossedItem(Level worldIn, double x, double y, double z) {
        super(AMEntityRegistry.TOSSED_ITEM, x, y, z, worldIn);
    }

    public EntityTossedItem(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.TOSSED_ITEM, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DART, false);
    }

    public boolean isDart() {
        return this.entityData.get(DART);
    }

    public void setDart(boolean dart) {
        this.entityData.set(DART, dart);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            double d0 = 0.08D;

            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }


    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = Mth.sqrt((float) (x * x + z * z));
            this.setXRot((float)(Mth.atan2(y, (double)f) * (double)(180F / (float)Math.PI)));
            this.setYRot( (float)(Mth.atan2(x, z) * (double)(180F / (float)Math.PI)));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
        this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vector3d.y, (double)f) * (double)(180F / (float)Math.PI))));
        this.setYRot( lerpRotation(this.yRotO, (float)(Mth.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI))));
    }

    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while(p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while(p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
    }


    protected void onHitEntity(EntityHitResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        if(this.getOwner() instanceof EntityCapuchinMonkey){
            EntityCapuchinMonkey boss = (EntityCapuchinMonkey) this.getOwner();
            if(!boss.isAlliedTo(p_213868_1_.getEntity()) || !boss.isTame() && !(p_213868_1_.getEntity() instanceof EntityCapuchinMonkey)){
                p_213868_1_.getEntity().hurt(DamageSource.thrown(this, boss), isDart() ? 8 : 4);
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Dart", this.isDart());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        this.setDart(compound.getBoolean("Dart"));
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide && (!this.isDart() || result.getType() == HitResult.Type.BLOCK)) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    protected Item getDefaultItem() {
        return isDart() ? AMItemRegistry.ANCIENT_DART : Items.COBBLESTONE;
    }
}
