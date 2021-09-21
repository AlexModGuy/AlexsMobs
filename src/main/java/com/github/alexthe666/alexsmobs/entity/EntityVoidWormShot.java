package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class EntityVoidWormShot extends Entity {
    private UUID ownerUUID;
    private int ownerNetworkId;
    private boolean leftOwner;
    private static final EntityDataAccessor<Boolean> PORTALLING = SynchedEntityData.defineId(EntityVoidWormShot.class, EntityDataSerializers.BOOLEAN);

    public EntityVoidWormShot(EntityType p_i50162_1_, Level p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntityVoidWormShot(Level worldIn, EntityVoidWorm p_i47273_2_) {
        this(AMEntityRegistry.VOID_WORM_SHOT, worldIn);
        this.setShooter(p_i47273_2_);
        this.setPos(p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.35D * (double) Mth.sin(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)), p_i47273_2_.getY() + (double) 1F, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.35D * (double) Mth.cos(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)));
    }

    public EntityVoidWormShot(Level worldIn, LivingEntity p_i47273_2_, boolean right) {
        this(AMEntityRegistry.VOID_WORM_SHOT, worldIn);
        this.setShooter(p_i47273_2_);
        float rot = p_i47273_2_.yHeadRot + (right ? 60 : -60);
        this.setPos(p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth()) * 0.9F * (double) Mth.sin(rot * ((float) Math.PI / 180F)), p_i47273_2_.getY() + (double) 1F, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth()) * 0.9D * (double) Mth.cos(rot * ((float) Math.PI / 180F)));
    }

    @OnlyIn(Dist.CLIENT)
    public EntityVoidWormShot(Level worldIn, double x, double y, double z, double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
        this(AMEntityRegistry.VOID_WORM_SHOT, worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
    }

    public EntityVoidWormShot(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.VOID_WORM_SHOT, world);
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
        if (this.tickCount > 40) {
            Entity entity = this.getShooter();
            if(isPortalType()){
                this.setDeltaMovement(Vec3.ZERO);
                if (this.tickCount > 60) {
                    this.remove();
                }
            }else{
                if (entity instanceof Mob && ((Mob) entity).getTarget() != null) {
                    LivingEntity target = ((Mob) entity).getTarget();
                    if(target == null){
                        this.kill();
                    }
                    double d0 = target.getX() - this.getX();
                    double d1 = target.getY() + target.getBbHeight() * 0.5F - this.getY();
                    double d2 = target.getZ() - this.getZ();
                    Vec3 vector3d = new Vec3(d0, d1, d2);
                    float speed = 0.05F;
                    shoot(d0, d1, d2, 1, 0);
                    this.yRot = -((float) Mth.atan2(d0, d2)) * (180F / (float) Math.PI);
                }
            }
        }
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        HitResult raytraceresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
        }
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        this.setNoGravity(true);
        this.updateRotation();
        float f = 0.99F;
        float f1 = 0.06F;
        if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.remove();
        } else if (this.isInWaterOrBubble()) {
            this.remove();
        } else {
            this.setDeltaMovement(vector3d.scale(0.99F));
            this.setPos(d0, d1, d2);
        }
    }

    protected void onEntityHit(EntityHitResult p_213868_1_) {
        Entity entity = this.getShooter();
        if (entity instanceof LivingEntity && !(p_213868_1_.getEntity() instanceof EntityVoidWorm || p_213868_1_.getEntity() instanceof EntityVoidWormPart)) {
            boolean b = wormAttack(p_213868_1_.getEntity(), DamageSource.indirectMobAttack(this, (LivingEntity) entity).setProjectile(), (float) (AMConfig.voidWormDamageModifier * 4F));
            if(b && p_213868_1_.getEntity() instanceof Player){
                Player player = ((Player)p_213868_1_.getEntity());
                if(player.getUseItem().isShield(player)){
                    player.disableShield(true);
                }
            }
        }

        this.remove();
    }

    private boolean wormAttack(Entity entity, DamageSource source, float dmg){
        return entity.hurt(source, dmg);
    }


    protected void onHitBlock(BlockHitResult p_230299_1_) {
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        if (!this.level.isClientSide) {
            this.remove();
        }
    }

    protected void defineSynchedData() {
        this.entityData.define(PORTALLING, false);
    }

    public boolean isPortalType(){
        return this.entityData.get(PORTALLING);
    }

    public void setPortalType(boolean portalType){
        this.entityData.set(PORTALLING, portalType);
    }

    public void setShooter(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.ownerUUID = entityIn.getUUID();
            this.ownerNetworkId = entityIn.getId();
        }

    }

    @Nullable
    public Entity getShooter() {
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
        Entity entity = this.getShooter();
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
        float f = Mth.sqrt(getHorizontalDistanceSqr(vector3d));
        this.yRot = (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
        this.xRot = (float) (Mth.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI));
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }

    public void shootFromRotation(Entity p_234612_1_, float p_234612_2_, float p_234612_3_, float p_234612_4_, float p_234612_5_, float p_234612_6_) {
        float f = -Mth.sin(p_234612_3_ * ((float) Math.PI / 180F)) * Mth.cos(p_234612_2_ * ((float) Math.PI / 180F));
        float f1 = -Mth.sin((p_234612_2_ + p_234612_4_) * ((float) Math.PI / 180F));
        float f2 = Mth.cos(p_234612_3_ * ((float) Math.PI / 180F)) * Mth.cos(p_234612_2_ * ((float) Math.PI / 180F));
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
        this.playSound(SoundEvents.GLASS_BREAK, 1F, 0.5F);
        Entity entity = this.getShooter();
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = Mth.sqrt(x * x + z * z);
            this.xRot = (float) (Mth.atan2(y, f) * (double) (180F / (float) Math.PI));
            this.yRot = (float) (Mth.atan2(x, z) * (double) (180F / (float) Math.PI));
            this.xRotO = this.xRot;
            this.yRotO = this.yRot;
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
        }

    }

    protected boolean canHitEntity(Entity p_230298_1_) {
        if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
            Entity entity = this.getShooter();
            return (entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_)) && !(p_230298_1_ instanceof EntityVoidWormShot || p_230298_1_ instanceof EntityVoidWormPart);
        } else {
            return false;
        }
    }

    protected void updateRotation() {
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt(getHorizontalDistanceSqr(vector3d));
        this.xRot = lerpRotation(this.xRotO, (float) (Mth.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI)));
        this.yRot = lerpRotation(this.yRotO, (float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)));
    }
}
