package com.github.alexthe666.alexsmobs.entity.util;

import com.github.alexthe666.alexsmobs.attachment.AMAttachmentRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RockyChestplateUtil {

    private static final int MAX_ROLL_TICKS = 30;

    public static void rollFor(LivingEntity roller, int ticks) {
        roller.setData(AMAttachmentRegistry.ROCKY_ROLL_TICKS, ticks);
        if(ticks == MAX_ROLL_TICKS){
            roller.setData(AMAttachmentRegistry.ROCKY_ROLL_TIMESTAMP, roller.tickCount);
        }
    }

    public static int getRollingTicksLeft(LivingEntity entity) {
        return entity.getData(AMAttachmentRegistry.ROCKY_ROLL_TICKS);
    }


    public static int getRollingTimestamp(LivingEntity entity) {
        return entity.getData(AMAttachmentRegistry.ROCKY_ROLL_TIMESTAMP);
    }


    public static boolean isWearing(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == AMItemRegistry.ROCKY_CHESTPLATE.get();
    }

    public static boolean isRockyRolling(LivingEntity entity) {
        return isWearing(entity) && getRollingTicksLeft(entity) > 0;
    }

    public static void tickRockyRolling(LivingEntity roller) {
        if(roller.isInWaterOrBubble()){
            roller.setDeltaMovement(roller.getDeltaMovement().add(0, -0.015F, 0));
        }
        int rollCounter = getRollingTicksLeft(roller);
        if(rollCounter == 0){
            if(roller.isSprinting()  && !roller.isShiftKeyDown() && (!(roller instanceof Player player) || !player.getAbilities().flying) && canRollAgain(roller) && !roller.isPassenger()){
                rollFor(roller, MAX_ROLL_TICKS);
            }
            if(roller instanceof Player &&  ((Player)roller).getForcedPose() == Pose.SWIMMING){
                ((Player)roller).setForcedPose(null);
            }
        }else{
            if(roller instanceof Player){
                ((Player)roller).setForcedPose(Pose.SWIMMING);
            }
            if(!roller.level().isClientSide()){
                for (Entity entity : roller.level().getEntitiesOfClass(LivingEntity.class, roller.getBoundingBox().inflate(1.0F))) {
                    if (!roller.isAlliedTo(entity) && !entity.isAlliedTo(roller) && entity != roller) {
                        entity.hurt(entity.damageSources().mobAttack(roller), 2.0F + roller.getRandom().nextFloat());
                    }
                }
            }
            if(roller.fallDistance > 3.0F){
                roller.fallDistance -= 0.5F;
            }
            roller.refreshDimensions();
            Vec3 vec3 = roller.onGround() ? roller.getDeltaMovement() : roller.getDeltaMovement().multiply(0.9D, 1D, 0.9D);
            float f = roller.getYRot() * Mth.DEG_TO_RAD;
            float f1 = roller.isInWaterOrBubble() ? 0.05F : 0.15F;
            Vec3 rollDelta = new Vec3(vec3.x + (double) (-Mth.sin(f) * f1), 0.0D, vec3.z + (double) (Mth.cos(f) * f1));
            double rollY = roller.isInWaterOrBubble() || roller.isShiftKeyDown() ? -0.1F : rollCounter >= MAX_ROLL_TICKS ? 0.27D : vec3.y;
            roller.setDeltaMovement(rollDelta.add(0.0D, rollY, 0.0D));
            if(rollCounter > 1 || !roller.isSprinting()){
                rollFor(roller, rollCounter - 1);
            }
            if((roller instanceof Player && ((Player) roller).getAbilities().flying || roller.isShiftKeyDown()) && canRollAgain(roller)){
                rollFor(roller, 0);
            }
        }
    }

    private static boolean canRollAgain(LivingEntity roller) {
        return roller.tickCount - getRollingTimestamp(roller) >= 20 || Math.abs(roller.tickCount - getRollingTimestamp(roller)) > 100;
    }
}
