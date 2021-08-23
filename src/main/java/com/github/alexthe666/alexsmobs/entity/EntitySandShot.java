package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntitySandShot extends Entity {
    private UUID field_234609_b_;
    private int field_234610_c_;
    private boolean field_234611_d_;
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntitySandShot.class, DataSerializers.VARINT);

    public EntitySandShot(EntityType p_i50162_1_, World p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntitySandShot(World worldIn, EntityGuster p_i47273_2_) {
        this(AMEntityRegistry.SAND_SHOT, worldIn);
        this.setShooter(p_i47273_2_);
        this.setPosition(p_i47273_2_.getPosX() - (double) (p_i47273_2_.getWidth() + 1.0F) * 0.35D * (double) MathHelper.sin(p_i47273_2_.renderYawOffset * ((float) Math.PI / 180F)), p_i47273_2_.getPosYEye() + (double) 0.2F, p_i47273_2_.getPosZ() + (double) (p_i47273_2_.getWidth() + 1.0F) * 0.35D * (double) MathHelper.cos(p_i47273_2_.renderYawOffset * ((float) Math.PI / 180F)));
    }

    public EntitySandShot(World worldIn, LivingEntity p_i47273_2_, boolean right) {
        this(AMEntityRegistry.SAND_SHOT, worldIn);
        this.setShooter(p_i47273_2_);
        float rot = p_i47273_2_.rotationYawHead + (right ? 60 : -60);
        this.setPosition(p_i47273_2_.getPosX() - (double) (p_i47273_2_.getWidth()) * 0.5D * (double) MathHelper.sin(rot * ((float) Math.PI / 180F)), p_i47273_2_.getPosYEye() - (double) 0.2F, p_i47273_2_.getPosZ() + (double) (p_i47273_2_.getWidth()) * 0.5D * (double) MathHelper.cos(rot * ((float) Math.PI / 180F)));
    }

    @OnlyIn(Dist.CLIENT)
    public EntitySandShot(World worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(AMEntityRegistry.SAND_SHOT, worldIn);
        this.setPosition(x, y, z);
        this.setMotion(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    public EntitySandShot(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.SAND_SHOT, world);
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

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        if (!this.field_234611_d_) {
            this.field_234611_d_ = this.func_234615_h_();
        }
        IParticleData type = this.getVariant() == 2 ? AMParticleRegistry.GUSTER_SAND_SHOT_SOUL : this.getVariant() == 1 ? AMParticleRegistry.GUSTER_SAND_SHOT_RED : AMParticleRegistry.GUSTER_SAND_SHOT;
        for (int i = 0; i < 3 + rand.nextInt(6); ++i) {
            double d0 = 0.1D + 0.3D * (double) i;
            world.addParticle(type, this.getPosX() + 0.25F * (rand.nextFloat() - 0.5F), this.getPosY() + 0.25F * (rand.nextFloat() - 0.5F), this.getPosZ() + 0.25F * (rand.nextFloat() - 0.5F), this.getMotion().x * d0, this.getMotion().y, this.getMotion().z * d0);
        }
        super.tick();
        Vector3d vector3d = this.getMotion();
        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
        }

        double d0 = this.getPosX() + vector3d.x;
        double d1 = this.getPosY() + vector3d.y;
        double d2 = this.getPosZ() + vector3d.z;

        this.func_234617_x_();
        float f = 0.99F;
        float f1 = 0.06F;
        if (this.world.func_234853_a_(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
            this.remove();
        } else if (this.isInWaterOrBubbleColumn()) {
            this.remove();
        } else {
            this.setMotion(vector3d.scale(0.99F));
            this.setMotion(this.getMotion().add(0.0D, -0.03F, 0.0D));
            if (!this.hasNoGravity()) {
                this.setMotion(this.getMotion().add(0.0D, -0.03F, 0.0D));
            }

            this.setPosition(d0, d1, d2);
        }
    }

    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        Entity entity = this.getSandShooter();
        if (entity instanceof LivingEntity) {
            p_213868_1_.getEntity().attackEntityFrom(DamageSource.causeIndirectDamage(this, (LivingEntity) entity).setProjectile(), 2.5F);
        }
        if (entity instanceof PlayerEntity && p_213868_1_.getEntity() instanceof LivingEntity) {
            ((LivingEntity)p_213868_1_.getEntity()).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100, 0, true, false));
        }
    }

    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockState blockstate = this.world.getBlockState(p_230299_1_.getPos());
        if (!this.world.isRemote) {
            this.remove();
        }
    }

    protected void registerData() {
        this.dataManager.register(VARIANT, 0);
    }

    public void setShooter(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.field_234609_b_ = entityIn.getUniqueID();
            this.field_234610_c_ = entityIn.getEntityId();
        }

    }

    @Nullable
    public Entity getSandShooter() {
        if (this.field_234609_b_ != null && this.world instanceof ServerWorld) {
            return ((ServerWorld) this.world).getEntityByUuid(this.field_234609_b_);
        } else {
            return this.field_234610_c_ != 0 ? this.world.getEntityByID(this.field_234610_c_) : null;
        }
    }

    protected void writeAdditional(CompoundNBT compound) {
        if (this.field_234609_b_ != null) {
            compound.putUniqueId("Owner", this.field_234609_b_);
        }

        if (this.field_234611_d_) {
            compound.putBoolean("LeftOwner", true);
        }

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("Owner")) {
            this.field_234609_b_ = compound.getUniqueId("Owner");
        }

        this.field_234611_d_ = compound.getBoolean("LeftOwner");
    }

    private boolean func_234615_h_() {
        Entity entity = this.getSandShooter();
        if (entity != null) {
            for (Entity entity1 : this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (p_234613_0_) -> {
                return !p_234613_0_.isSpectator() && p_234613_0_.canBeCollidedWith();
            })) {
                if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vector3d vector3d = (new Vector3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy).scale(velocity);
        this.setMotion(vector3d);
        float f = MathHelper.sqrt(horizontalMag(vector3d));
        this.rotationYaw = (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    public void func_234612_a_(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        float f = -MathHelper.sin(p_234612_3_ * ((float) Math.PI / 180F)) * MathHelper.cos(p_234612_2_ * ((float) Math.PI / 180F));
        float f1 = -MathHelper.sin((p_234612_2_ + p_234612_4_) * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(p_234612_3_ * ((float) Math.PI / 180F)) * MathHelper.cos(p_234612_2_ * ((float) Math.PI / 180F));
        this.shoot(f, f1, f2, p_234612_5_, p_234612_6_);
        Vector3d vector3d = p_234612_1_.getMotion();
        this.setMotion(this.getMotion().add(vector3d.x, p_234612_1_.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
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

    protected boolean func_230298_a_(Entity p_230298_1_) {
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.canBeCollidedWith()) {
            Entity entity = this.getSandShooter();
            return entity == null || this.field_234611_d_ || !entity.isRidingSameEntity(p_230298_1_);
        } else {
            return false;
        }
    }

    protected void func_234617_x_() {
        Vector3d vector3d = this.getMotion();
        float f = MathHelper.sqrt(horizontalMag(vector3d));
        this.rotationPitch = func_234614_e_(this.prevRotationPitch, (float) (MathHelper.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI)));
        this.rotationYaw = func_234614_e_(this.prevRotationYaw, (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)));
    }
}
