package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityGust extends Entity {
    protected static final DataParameter<Boolean> VERTICAL = EntityDataManager.createKey(EntityGust.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Float> X_DIR = EntityDataManager.createKey(EntityGust.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> Y_DIR = EntityDataManager.createKey(EntityGust.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> Z_DIR = EntityDataManager.createKey(EntityGust.class, DataSerializers.FLOAT);
    private Entity pushedEntity = null;

    public EntityGust(EntityType p_i50162_1_, World p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntityGust(World worldIn) {
        this(AMEntityRegistry.GUST, worldIn);
    }

    public EntityGust(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.GUST, world);
    }

    public void applyEntityCollision(Entity entityIn) {

    }

    protected static float func_234614_e_(float p_234614_0_, float p_234614_1_) {
        while (p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while (p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return MathHelper.lerp(0.2F, p_234614_0_, p_234614_1_);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        if(this.ticksExisted > 300){
            this.remove();
        }
        for (int i = 0; i < 1 + rand.nextInt(1); ++i) {
            world.addParticle(AMParticleRegistry.GUSTER_SAND_SPIN, this.getPosX() + 0.5F * (rand.nextFloat() - 0.5F), this.getPosY() + 0.5F * (rand.nextFloat() - 0.5F), this.getPosZ() + 0.5F * (rand.nextFloat() - 0.5F), this.getPosX(), this.getPosY() + 0.5F, this.getPosZ());
        }
        Vector3d vector3d = new Vector3d(this.dataManager.get(X_DIR), this.dataManager.get(Y_DIR), this.dataManager.get(Z_DIR));
        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && ticksExisted > 4) {
            this.onImpact(raytraceresult);
        }
        List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, this.getBoundingBox().grow(0.1));

        if(pushedEntity != null && this.getDistance(pushedEntity) > 2){
            pushedEntity = null;
        }
        double d0 = this.getPosX() + vector3d.x;
        double d1 = this.getPosY() + vector3d.y;
        double d2 = this.getPosZ() + vector3d.z;
        if(this.getPosY() > this.world.getHeight()){
            this.remove();
        }
        this.func_234617_x_();
        float f = 0.99F;
        float f1 = 0.06F;
         if (this.isInWaterOrBubbleColumn()) {
            this.remove();
        } else {
            this.setMotion(vector3d);
            this.setMotion(this.getMotion().add(0.0D, -0.06F, 0.0D));
            this.setPosition(d0, d1, d2);
            if(pushedEntity != null){
                pushedEntity.setMotion(this.getMotion().add(0, 0.063, 0));
            }
            for(Entity e : list){
                e.setMotion(this.getMotion().add(0, 0.068, 0));
                if(e.getMotion().y < 0){
                    e.setMotion(e.getMotion().mul(1, 0, 1));
                }
                e.fallDistance = 0;
            }
        }
    }

    public void setGustDir(float x, float y, float z){
        this.dataManager.set(X_DIR, x);
        this.dataManager.set(Y_DIR, y);
        this.dataManager.set(Z_DIR, z);
    }

    public float getGustDir(int xyz){
       return this.dataManager.get(xyz == 2 ? Z_DIR : xyz == 1 ? Y_DIR : X_DIR);
    }

    protected void onEntityHit(EntityRayTraceResult result) {
        Entity entity = result.getEntity();
        if(entity instanceof EntityGust){
            EntityGust other = (EntityGust)entity;
            double avgX = (other.getPosX() + this.getPosX()) / 2F;
            double avgY = (other.getPosY() + this.getPosY()) / 2F;
            double avgZ = (other.getPosZ() + this.getPosZ()) / 2F;
            other.setPosition(avgX, avgY, avgZ);
            other.setGustDir(other.getGustDir(0) + this.getGustDir(0), other.getGustDir(1) + this.getGustDir(1), other.getGustDir(2) + this.getGustDir(2));
            if(this.isAlive() && other.isAlive()){
                this.remove();
            }
        }else if(entity != null){
            pushedEntity = entity;
        }
    }


    protected boolean func_230298_a_(Entity p_230298_1_) {
        return !p_230298_1_.isSpectator();
    }

    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        if( p_230299_1_.getPos() != null){
            BlockPos pos = p_230299_1_.getPos();
            if(world.getBlockState(pos).getMaterial().isSolid()){
                if (!this.world.isRemote) {
                    this.remove();

                }
            }
        }

    }

    protected void registerData() {
        this.dataManager.register(VERTICAL, false);
        this.dataManager.register(X_DIR, 0f);
        this.dataManager.register(Y_DIR, 0F);
        this.dataManager.register(Z_DIR, 0F);
    }

    protected void writeAdditional(CompoundNBT compound) {
        compound.putBoolean("VerticalTornado", getVertical());
        compound.putFloat("GustDirX", this.dataManager.get(X_DIR));
        compound.putFloat("GustDirY", this.dataManager.get(Y_DIR));
        compound.putFloat("GustDirZ", this.dataManager.get(Z_DIR));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(CompoundNBT compound) {
        this.dataManager.set(X_DIR, compound.getFloat("GustDirX"));
        this.dataManager.set(Y_DIR, compound.getFloat("GustDirX"));
        this.dataManager.set(Z_DIR, compound.getFloat("GustDirX"));
        this.setVertical((compound.getBoolean("VerticalTornado")));
    }

    public void setVertical(boolean vertical){
        this.dataManager.set(VERTICAL, vertical);
    }

    public boolean getVertical(){
        return this.dataManager.get(VERTICAL);
    }

    protected void onImpact(RayTraceResult result) {
        RayTraceResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            this.onEntityHit((EntityRayTraceResult) result);
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
            this.func_230299_a_((BlockRayTraceResult) result);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.setMotion(x, y, z);
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float) (MathHelper.atan2(y, f) * (double) (180F / (float) Math.PI));
            this.rotationYaw = (float) (MathHelper.atan2(x, z) * (double) (180F / (float) Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
        }

    }

    protected void func_234617_x_() {
        Vector3d vector3d = this.getMotion();
        float f = MathHelper.sqrt(horizontalMag(vector3d));
        this.rotationPitch = func_234614_e_(this.prevRotationPitch, (float) (MathHelper.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI)));
        this.rotationYaw = func_234614_e_(this.prevRotationYaw, (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)));
    }
}
