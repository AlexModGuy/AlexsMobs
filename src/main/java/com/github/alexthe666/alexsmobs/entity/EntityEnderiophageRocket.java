package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class EntityEnderiophageRocket extends FireworkRocketEntity {

    private static final EntityDataAccessor<ItemStack> DATA_FIREWORKS_ITEM = SynchedEntityData.defineId(EntityEnderiophageRocket.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TARGET = SynchedEntityData.defineId(EntityEnderiophageRocket.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<Boolean> DATA_SHOT_AT_ANGLE = SynchedEntityData.defineId(EntityEnderiophageRocket.class, EntityDataSerializers.BOOLEAN);
    private int life = 0;
    private int lifetime = 0;
    @Nullable
    private LivingEntity attachedToEntity;

    public EntityEnderiophageRocket(EntityType p_i50164_1_, Level p_i50164_2_) {
        super(p_i50164_1_, p_i50164_2_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FIREWORKS_ITEM, ItemStack.EMPTY);
        builder.define(DATA_ATTACHED_TARGET, OptionalInt.empty());
        builder.define(DATA_SHOT_AT_ANGLE, false);
    }

    public EntityEnderiophageRocket(Level worldIn, double x, double y, double z, ItemStack givenItem) {
        super(AMEntityRegistry.ENDERIOPHAGE_ROCKET.get(), worldIn);
        this.setPos(x, y, z);
        if (!givenItem.isEmpty()) {
            this.entityData.set(DATA_FIREWORKS_ITEM, givenItem.copy());
        }

        this.setDeltaMovement(this.random.nextGaussian() * 0.001D, 0.05D, this.random.nextGaussian() * 0.001D);
        this.lifetime = 10 + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public EntityEnderiophageRocket(Level p_i231581_1_, @Nullable Entity p_i231581_2_, double p_i231581_3_, double p_i231581_5_, double p_i231581_7_, ItemStack p_i231581_9_) {
        this(p_i231581_1_, p_i231581_3_, p_i231581_5_, p_i231581_7_, p_i231581_9_);
        this.setOwner(p_i231581_2_);
    }

    public EntityEnderiophageRocket(Level worldIn, ItemStack stack, LivingEntity attachedTo) {
        this(worldIn, attachedTo, attachedTo.getX(), attachedTo.getY(), attachedTo.getZ(), stack);
        this.entityData.set(DATA_ATTACHED_TARGET, OptionalInt.of(attachedTo.getId()));
        this.attachedToEntity = attachedTo;
    }

    @Nullable
    private LivingEntity getAttachedToEntity() {
        OptionalInt optionalint = this.entityData.get(DATA_ATTACHED_TARGET);
        if (optionalint.isPresent()) {
            Entity entity = this.level().getEntity(optionalint.getAsInt());
            if (entity instanceof LivingEntity) {
                return (LivingEntity) entity;
            }
        }
        return null;
    }

    public void tick() {
        // Don't call super.tick() - we implement our own logic
        if (!this.level().isClientSide) {
            this.setSharedFlag(6, this.isCurrentlyGlowing());
        }
        this.baseTick();
        
        ++this.life;
        
        // Handle attached to entity (elytra boosting)
        if (this.attachedToEntity == null) {
            this.attachedToEntity = this.getAttachedToEntity();
        }
        
        if (this.attachedToEntity != null) {
            // We're boosting a player with elytra
            if (this.attachedToEntity.isFallFlying()) {
                Vec3 lookVec = this.attachedToEntity.getLookAngle();
                Vec3 currentMotion = this.attachedToEntity.getDeltaMovement();
                this.attachedToEntity.setDeltaMovement(
                    currentMotion.add(
                        lookVec.x * 0.1D + (lookVec.x * 1.5D - currentMotion.x) * 0.5D,
                        lookVec.y * 0.1D + (lookVec.y * 1.5D - currentMotion.y) * 0.5D,
                        lookVec.z * 0.1D + (lookVec.z * 1.5D - currentMotion.z) * 0.5D
                    )
                );
                this.setPos(this.attachedToEntity.getX(), this.attachedToEntity.getY(), this.attachedToEntity.getZ());
                
                // Particles when boosting
                if (this.level().isClientSide) {
                    this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY() - 0.3D, this.getZ(), 
                        this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);
                }
            }
            
            // Check if we should explode
            if (this.life > this.lifetime) {
                this.explode();
            }
        } else {
            // Not attached - this is a ground-launched rocket
            // Apply upward movement
            if (!this.onGround()) {
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(motion.x * 1.15D, motion.y + 0.04D, motion.z * 1.15D);
            }
            
            // Move the rocket
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitresult)) {
                this.hitTargetOrDeflectSelf(hitresult);
            }
            
            this.move(MoverType.SELF, this.getDeltaMovement());
            
            // Particles
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY() - 0.3D, this.getZ(), 
                    this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);
            }
            
            // Explode after lifetime or on collision
            if (this.life > this.lifetime || this.horizontalCollision || this.verticalCollision) {
                this.explode();
            }
        }
    }
    
    private void explode() {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)17);
            this.discard();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 17) {
            this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, 0.005D, this.random.nextGaussian() * 0.05D);
            for(int i = 0; i < this.random.nextInt(15) + 30; ++i) {
                this.level().addParticle(AMParticleRegistry.DNA.get(), this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D, this.random.nextGaussian() * 0.25D);
            }
            for(int i = 0; i < this.random.nextInt(15) + 15; ++i) {
                this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D, this.random.nextGaussian() * 0.15D);
            }
            SoundEvent soundEvent = AlexsMobs.PROXY.isFarFromCamera(this.getX(), this.getY(), this.getZ()) ? SoundEvents.FIREWORK_ROCKET_BLAST : SoundEvents.FIREWORK_ROCKET_BLAST_FAR;
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), soundEvent, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);


        }else{
            super.handleEntityEvent(id);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return new ItemStack(AMItemRegistry.ENDERIOPHAGE_ROCKET.get());
    }

}