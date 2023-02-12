package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import java.util.Iterator;
import java.util.List;

public class EntityDragonsBreathCannonball extends AbstractHurtingProjectile {
    public EntityDragonsBreathCannonball(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public EntityDragonsBreathCannonball(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.DRAGONS_BREATH_CANNONBALL.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick(){
        super.tick();
        float deltaWiggle = (float)Math.sin(tickCount * 0.3F) * 0.02F;
        if(tickCount > 20){
            deltaWiggle += -0.03F * Math.min(1, (tickCount - 20) / 100F);
        }
        this.setDeltaMovement(this.getDeltaMovement().add(0, deltaWiggle, 0));
        if(tickCount > 100 || this.getDeltaMovement().length() < 0.02F){
            boom(false);
        }
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if(tickCount > 2){
            boom(true);
        }else{
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.1F, 0.1F, 0.1F));
        }
    }

    private void boom(boolean cloud) {
        if (!this.level.isClientSide) {
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float) 1F, false, Level.ExplosionInteraction.MOB);
            if (cloud) {
                List<LivingEntity> mobs = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
                AreaEffectCloud cloudEntity = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());

                cloudEntity.setParticle(ParticleTypes.DRAGON_BREATH);
                cloudEntity.setRadius(1.5F);
                cloudEntity.setDuration(80);
                cloudEntity.setRadiusPerTick(-1.0F / (float) cloudEntity.getDuration());
                cloudEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
                if (!mobs.isEmpty()) {
                    Iterator var5 = mobs.iterator();
                    while (var5.hasNext()) {
                        LivingEntity entity = (LivingEntity) var5.next();
                        double $$5 = this.distanceToSqr(entity);
                        if ($$5 < 16.0) {
                            cloudEntity.setPos(entity.getX(), entity.getY(), entity.getZ());
                            break;
                        }
                    }
                }
                this.level.addFreshEntity(cloudEntity);
            }
            this.discard();
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.DRAGON_BREATH;
    }

    protected boolean shouldBurn() {
        return false;
    }
}
