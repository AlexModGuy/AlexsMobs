package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityTossedItem extends ProjectileItemEntity {

    protected static final DataParameter<Boolean> DART = EntityDataManager.createKey(EntityTossedItem.class, DataSerializers.BOOLEAN);

    public EntityTossedItem(EntityType p_i50154_1_, World p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public EntityTossedItem(World worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.TOSSED_ITEM, throwerIn, worldIn);
    }

    public EntityTossedItem(World worldIn, double x, double y, double z) {
        super(AMEntityRegistry.TOSSED_ITEM, x, y, z, worldIn);
    }

    public EntityTossedItem(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.TOSSED_ITEM, world);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(DART, false);
    }

    public boolean isDart() {
        return this.dataManager.get(DART);
    }

    public void setDart(boolean dart) {
        this.dataManager.set(DART, dart);
    }


    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            double d0 = 0.08D;

            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getPosX(), this.getPosY(), this.getPosZ(), ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }


    @OnlyIn(Dist.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.setMotion(x, y, z);
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (double)(180F / (float)Math.PI));
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
        }

    }

    public void tick() {
        super.tick();
        Vector3d vector3d = this.getMotion();
        float f = MathHelper.sqrt(horizontalMag(vector3d));
        this.rotationPitch = func_234614_e_(this.prevRotationPitch, (float)(MathHelper.atan2(vector3d.y, (double)f) * (double)(180F / (float)Math.PI)));
        this.rotationYaw = func_234614_e_(this.prevRotationYaw, (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI)));
    }

    protected static float func_234614_e_(float p_234614_0_, float p_234614_1_) {
        while(p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while(p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return MathHelper.lerp(0.2F, p_234614_0_, p_234614_1_);
    }


    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        if(this.getShooter() instanceof EntityCapuchinMonkey){
            EntityCapuchinMonkey boss = (EntityCapuchinMonkey) this.getShooter();
            if(!boss.isOnSameTeam(p_213868_1_.getEntity()) || !boss.isTamed() && !(p_213868_1_.getEntity() instanceof EntityCapuchinMonkey)){
                p_213868_1_.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, boss), isDart() ? 8 : 4);
            }
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Dart", this.isDart());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setDart(compound.getBoolean("Dart"));
    }

    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote && (!this.isDart() || result.getType() == RayTraceResult.Type.BLOCK)) {
            this.world.setEntityState(this, (byte)3);
            this.remove();
        }
    }

    protected Item getDefaultItem() {
        return isDart() ? AMItemRegistry.ANCIENT_DART : Items.COBBLESTONE;
    }
}
