package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFocalPoint;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class EffectClinging extends Effect {

    public EffectClinging() {
        super(EffectType.BENEFICIAL, 0XBD4B4B);
        this.setRegistryName(AlexsMobs.MODID, "clinging");
    }

    private static BlockPos getPositionUnderneath(Entity e) {
        return new BlockPos(e.getPosX(), e.getBoundingBox().maxY + 1.51F, e.getPosZ());
    }

    public void performEffect(LivingEntity entity, int amplifier) {
        entity.recalculateSize();
        entity.setNoGravity(false);

        if (isUpsideDown(entity)) {
            entity.fallDistance = 0;
            if (!entity.isSneaking()) {
                if (!entity.collidedHorizontally) {
                    entity.setMotion(entity.getMotion().add(0, 0.3F, 0));
                }
                entity.setMotion(entity.getMotion().mul(0.998F, 1F, 0.998F));
            }
        }
    }

    public static boolean isUpsideDown(LivingEntity entity){
        BlockPos pos = getPositionUnderneath(entity);
        BlockState ground = entity.world.getBlockState(pos);
        return (entity.collidedVertically || ground.isSolidSide(entity.world, pos, Direction.DOWN)) && !entity.isOnGround();
    }
    public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AttributeModifierManager attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        entityLivingBaseIn.recalculateSize();
    }

    public boolean hasFocalPoint(LivingEntity entity) {
        for (Entity passenger : entity.getPassengers()) {
            if (passenger instanceof EntityFocalPoint) {
                return true;
            }
        }
        return false;
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public String getName() {
        return "alexsmobs.potion.clinging";
    }

}