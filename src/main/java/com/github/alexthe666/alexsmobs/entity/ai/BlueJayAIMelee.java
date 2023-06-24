package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class BlueJayAIMelee extends Goal {
    private EntityBlueJay blueJay;
    float circlingTime = 0;
    float circleDistance = 1;
    float yLevel = 2;
    boolean clockwise = false;
    private int maxCircleTime;

    public BlueJayAIMelee(EntityBlueJay blueJay) {
        this.blueJay = blueJay;
    }

    public boolean canUse(){
        Entity entity = blueJay.getTarget();
        return entity != null && entity.isAlive();
    }

    public void start() {
        clockwise = blueJay.getRandom().nextBoolean();
        yLevel = blueJay.getRandom().nextInt(2);
        circlingTime = 0;
        maxCircleTime = 20 + blueJay.getRandom().nextInt(20);
        circleDistance = 0.5F + blueJay.getRandom().nextFloat() * 2F;
    }

    public void stop() {
        clockwise = blueJay.getRandom().nextBoolean();
        yLevel = blueJay.getRandom().nextInt(2);
        circlingTime = 0;
        maxCircleTime = 20 + blueJay.getRandom().nextInt(20);
        circleDistance = 0.5F + blueJay.getRandom().nextFloat() * 2F;
        if(blueJay.onGround()){
            blueJay.setFlying(false);
        }
    }

    public void tick() {
        if (this.blueJay.isFlying()) {
            circlingTime++;
        }
        LivingEntity target = blueJay.getTarget();
        if(target != null){
            if(blueJay.distanceTo(target) < 3){
                blueJay.peck();
                target.hurt(target.damageSources().generic(), 1);
                stop();
            }
            if(circlingTime > maxCircleTime){
                blueJay.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() / 2F, target.getZ(), 1.6F);

            }else{
                Vec3 circlePos = getVultureCirclePos(target.position());
                if (circlePos == null) {
                    circlePos = target.position();
                }
                blueJay.setFlying(true);
                blueJay.getMoveControl().setWantedPosition(circlePos.x(), circlePos.y() + target.getEyeHeight() + 0.2F, circlePos.z(), 1.6F);

            }
        }
    }

    public Vec3 getVultureCirclePos(Vec3 target) {
        float angle = (Maths.STARTING_ANGLE * 13 * (clockwise ? -circlingTime : circlingTime));
        double extraX = circleDistance * Mth.sin((angle));
        double extraZ = circleDistance * Mth.cos(angle);
        Vec3 pos = new Vec3(target.x() + extraX, target.y() + yLevel, target.z() + extraZ);
        if (blueJay.level().isEmptyBlock(AMBlockPos.fromVec3(pos))) {
            return pos;
        }
        return null;
    }
}
