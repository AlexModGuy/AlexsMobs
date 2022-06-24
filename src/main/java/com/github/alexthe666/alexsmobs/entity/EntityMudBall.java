package com.github.alexthe666.alexsmobs.entity;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class EntityMudBall extends EntityMobProjectile {

    public EntityMudBall(EntityType type, Level level) {
        super(type, level);
    }

    public EntityMudBall(Level worldIn, EntityMudskipper mudskipper) {
        super(AMEntityRegistry.MUD_BALL.get(), worldIn, mudskipper);
        Vec3 vec3 = mudskipper.position().add(calcOffsetVec(new Vec3(0, 0, 0.2F * mudskipper.getScale()), 0F, mudskipper.getYRot()));
        this.setPos(vec3.x, vec3.y, vec3.z);
    }

    public EntityMudBall(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.MUD_BALL.get(), world);
    }

    public void doBehavior() {
        this.setDeltaMovement(this.getDeltaMovement().scale((double)0.9F));
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)-0.06F, 0.0D));
        }

    }

    @Override
    protected boolean removeInWater(){
        return false;
    }

    @Override
    protected float getDamage() {
        return 1 + random.nextInt(3);
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        super.onEntityHit(result);
        if(result.getEntity() instanceof LivingEntity hurt){
            hurt.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60));
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 3) {
            ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD.defaultBlockState());
            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }else{
            super.handleEntityEvent(event);
        }

    }


    @Override
    protected void onImpact(HitResult result) {
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
        }
        super.onImpact(result);
    }
}
