package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.function.Predicate;

public class EffectDebilitatingSting extends MobEffect {

    private int lastDuration = -1;

    protected EffectDebilitatingSting() {
        super(MobEffectCategory.NEUTRAL, 0XFFF385);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.parse("alexsmobs:debilitating_sting_speed"), -1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        // Effects now applied automatically through attribute modifiers
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.getType().is(net.minecraft.tags.EntityTypeTags.ARTHROPOD)) {
            if (entity.getHealth() > entity.getMaxHealth() * 0.5F) {
                entity.hurt(entity.damageSources().magic(), 1.0F);
            }
        } else {
            boolean suf = isEntityInsideOpaqueBlock(entity);
            if (suf) {
                entity.setDeltaMovement(Vec3.ZERO);
                entity.noPhysics = true;
            }
            entity.setNoGravity(suf);
            entity.setJumping(false);
            if (!entity.isPassenger() && entity instanceof Mob && !(((Mob) entity).getMoveControl().getClass() == MoveControl.class)) {
                entity.setDeltaMovement(new Vec3(0, -1, 0));
            }
            if (lastDuration == 1) {
                entity.hurt(entity.damageSources().magic(), (amplifier + 1) * 30);
                if (amplifier > 0) {
                    BlockPos surface = entity.blockPosition();
                    while (!entity.level().isEmptyBlock(surface) && surface.getY() < 256) {
                        surface = surface.above();
                    }
                    EntityTarantulaHawk baby = AMEntityRegistry.TARANTULA_HAWK.get().create(entity.level());
                    baby.setBaby(true);
                    baby.setPos(entity.getX(), surface.getY() + 0.1F, entity.getZ());
                    if (!entity.level().isClientSide) {
                        baby.finalizeSpawn((ServerLevelAccessor) entity.level(), entity.level().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.BREEDING, null);
                        entity.level().addFreshEntity(baby);
                    }
                }
                entity.setNoGravity(false);
                entity.noPhysics = false;
            }
        }
        return true;
    }

    public boolean isEntityInsideOpaqueBlock(Entity entity) {
        Vec3 vec3 = entity.getEyePosition();
        float f = entity.getDimensions(entity.getPose()).width() * 0.8F;
        AABB axisalignedbb = AABB.ofSize(vec3, (double)f, 1.0E-6D, (double)f);
        return entity.level().getBlockStates(axisalignedbb).filter(Predicate.not(BlockBehaviour.BlockStateBase::isAir)).anyMatch((p_185969_) -> {
            BlockPos blockpos = AMBlockPos.fromVec3(vec3);
            return p_185969_.isSuffocating(entity.level(), blockpos) && Shapes.joinIsNotEmpty(p_185969_.getCollisionShape(entity.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisalignedbb), BooleanOp.AND);
        });
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobs.potion.debilitating_sting";
    }
}
