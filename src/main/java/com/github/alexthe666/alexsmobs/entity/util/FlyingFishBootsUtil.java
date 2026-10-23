package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.attachment.AMAttachmentRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FlyingFishBootsUtil {

    private static final int MIN_BOOST_TIME = 35;

    public static void setBoostTicks(LivingEntity entity, int ticks) {
        entity.setData(AMAttachmentRegistry.FLYING_FISH_BOOST, ticks);
    }

    public static int getBoostTicks(LivingEntity entity) {
        return entity.getData(AMAttachmentRegistry.FLYING_FISH_BOOST);
    }

    public static boolean isWearing(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.FEET).getItem() == AMItemRegistry.FLYING_FISH_BOOTS.get();
    }

    public static void tickFlyingFishBoots(LivingEntity fishy) {
        int boostTime = getBoostTicks(fishy);
        if(boostTime <= 15 && fishy.isInWaterOrBubble() && !fishy.onGround()){
            // In 1.21+, we check if player is trying to swim up instead of directly accessing input
            boolean isJumping = fishy instanceof Player player && player.isSwimming() && player.getDeltaMovement().y > 0;
            if(fishy.getFluidHeight(FluidTags.WATER) < 0.4F && isJumping &&( !(fishy instanceof Player) || !((Player) fishy).getAbilities().flying)){
                final RandomSource rand = fishy.getRandom();
                boostTime = MIN_BOOST_TIME;
                Vec3 forward = new Vec3(0, 0.0F, 0.5F + rand.nextFloat() * 1.2F).xRot(-fishy.getXRot() * Mth.DEG_TO_RAD).yRot(-fishy.getYHeadRot() * Mth.DEG_TO_RAD);
                Vec3 delta = fishy.getDeltaMovement().add(forward);
                fishy.setDeltaMovement(delta.x, 0.3 + rand.nextFloat() * 0.3F, delta.z);
                fishy.setYRot(fishy.getYHeadRot());
            }
        }
        if(boostTime > 0){
            if(!fishy.isInWaterOrBubble() && !fishy.onGround()){
                if(fishy.getDeltaMovement().y < 0){
                    fishy.setDeltaMovement(fishy.getDeltaMovement().multiply(1F, 0.75F, 1F));
                }
                fishy.setPose(Pose.FALL_FLYING);
            }
            setBoostTicks(fishy, boostTime - 1);
        }
    }
}
