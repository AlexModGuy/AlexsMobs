package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class EntityPollenBall extends EntityMobProjectile {

    public EntityPollenBall(EntityType type, Level level) {
        super(type, level);
    }

    public EntityPollenBall(Level worldIn, EntityFlutter flutter) {
        super(AMEntityRegistry.POLLEN_BALL.get(), worldIn, flutter);
        Vec3 vec3 = flutter.position().add(calcOffsetVec(new Vec3(0, 0.4F * flutter.getScale(), 0), flutter.getFlutterPitch(), flutter.getYRot()));
        this.setPos(vec3.x, vec3.y, vec3.z);
    }

    public EntityPollenBall(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.POLLEN_BALL.get(), world);
    }

    public boolean isNoGravity() {
        return true;
    }

    public void doBehavior() {

        Entity entity = this.getShooter();
        if (entity instanceof Mob && ((Mob) entity).getTarget() != null) {
            LivingEntity target = ((Mob) entity).getTarget();
            if (target == null) {
                this.kill();
            }
            final double d0 = target.getX() - this.getX();
            final double d1 = target.getY() + target.getBbHeight() * 0.5F - this.getY();
            final double d2 = target.getZ() - this.getZ();
            final float speed = 0.35F;
            shoot(d0, d1, d2, speed, 0);
            this.setYRot(-((float) Mth.atan2(d0, d2)) * Mth.RAD_TO_DEG);
        }
        if(this.level.isClientSide && random.nextInt(2) == 0){
            final float r1 = (random.nextFloat() - 0.5F) * 0.5F;
            final float r2 = (random.nextFloat() - 0.5F) * 0.5F;
            final float r3 = (random.nextFloat() - 0.5F) * 0.5F;
            this.level.addParticle(ParticleTypes.FALLING_NECTAR, this.getX() + r1, this.getY() + r2, this.getZ() + r3, r1 * 0.1F, r2 * 0.1F, r3 * 0.1F);
        }
    }

    @Override
    protected float getDamage() {
        return 3;
    }
}
