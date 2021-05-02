package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class EffectDebilitatingSting extends Effect {

    private int lastDuration = -1;

    protected EffectDebilitatingSting() {
        super(EffectType.NEUTRAL, 0XFFF385);
        this.addAttributesModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -1.0F, AttributeModifier.Operation.MULTIPLY_BASE);
        this.setRegistryName(AlexsMobs.MODID, "debilitating_sting");
    }

    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        if (entityLivingBaseIn.getCreatureAttribute() == CreatureAttribute.ARTHROPOD) {
            super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        }
    }

    public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        if (entityLivingBaseIn.getCreatureAttribute() == CreatureAttribute.ARTHROPOD) {
            super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        }
    }

    public void performEffect(LivingEntity entity, int amplifier) {
        if (entity.getCreatureAttribute() != CreatureAttribute.ARTHROPOD) {
            if (entity.getHealth() > entity.getMaxHealth() * 0.5F) {
                entity.attackEntityFrom(DamageSource.MAGIC, 2.0F);
            }
        } else {
            boolean suf = isEntityInsideOpaqueBlock(entity);
            if (suf) {
                entity.setMotion(Vector3d.ZERO);
                entity.noClip = true;
            }
            entity.setNoGravity(suf);
            entity.setJumping(false);
            if (!entity.isPassenger() && entity instanceof MobEntity && !(((MobEntity) entity).getMoveHelper().getClass() == MovementController.class)) {
                entity.setMotion(new Vector3d(0, -1, 0));
            }
            if (lastDuration == 1) {
                entity.attackEntityFrom(DamageSource.MAGIC, 1000 + entity.getMaxHealth());
                if (amplifier > 0) {
                    BlockPos surface = entity.getPosition();
                    while (!entity.world.isAirBlock(surface) && surface.getY() < 256) {
                        surface = surface.up();
                    }
                    EntityTarantulaHawk baby = AMEntityRegistry.TARANTULA_HAWK.create(entity.world);
                    baby.setChild(true);
                    baby.setPosition(entity.getPosX(), surface.getY() + 0.1F, entity.getPosZ());
                    if (!entity.world.isRemote) {
                        entity.world.addEntity(baby);
                    }
                }
                entity.setNoGravity(false);
                entity.noClip = false;
            }
        }
    }

    public boolean isEntityInsideOpaqueBlock(Entity entity) {
        float f = 0.1F;
        float f1 = entity.getSize(entity.getPose()).width * 0.8F;
        AxisAlignedBB axisalignedbb = AxisAlignedBB.withSizeAtOrigin(f1, 0.1F, f1).offset(entity.getPosX(), entity.getPosYEye(), entity.getPosZ());
        return entity.world.func_241457_a_(entity, axisalignedbb, (p_241338_1_, p_241338_2_) -> {
            return p_241338_1_.isSuffocating(entity.world, p_241338_2_);
        }).findAny().isPresent();
    }

    public boolean isReady(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.debilitating_sting";
    }
}
