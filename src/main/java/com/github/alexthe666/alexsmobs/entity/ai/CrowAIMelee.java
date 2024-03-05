package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class CrowAIMelee extends Goal {
    private final EntityCrow crow;
    float circlingTime = 0;
    float circleDistance = 1;
    float yLevel = 2;
    boolean clockwise = false;
    private int maxCircleTime;

    public CrowAIMelee(EntityCrow crow) {
        this.crow = crow;
    }

    public boolean canUse(){
        return crow.getTarget() != null && !crow.isSitting() && crow.getCommand() != 3;
    }

    public void start() {
        clockwise = crow.getRandom().nextBoolean();
        yLevel = crow.getRandom().nextInt(2);
        circlingTime = 0;
        maxCircleTime = 20 + crow.getRandom().nextInt(100);
        circleDistance = 1F + crow.getRandom().nextFloat() * 3F;
    }

    public void stop() {
        clockwise = crow.getRandom().nextBoolean();
        yLevel = crow.getRandom().nextInt(2);
        circlingTime = 0;
        maxCircleTime = 20 + crow.getRandom().nextInt(100);
        circleDistance = 1F + crow.getRandom().nextFloat() * 3F;
        if(crow.onGround()){
            crow.setFlying(false);
        }
    }

    public void tick() {
        LivingEntity target = crow.getTarget();
        if(target != null){
            if(circlingTime > maxCircleTime){
                crow.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() / 2F, target.getZ(), 1.3F);
                if(crow.distanceTo(target) < 2){
                    crow.peck();
                    if(target.getMobType() == MobType.UNDEAD){
                        target.hurt(target.damageSources().generic(), 4);
                    }else{
                        target.hurt(target.damageSources().generic(), 1);
                    }

                    stop();
                }
            }else{
                Vec3 circlePos = getVultureCirclePos(target.position());
                if (circlePos == null) {
                    circlePos = target.position();
                }
                crow.setFlying(true);
                crow.getMoveControl().setWantedPosition(circlePos.x(), circlePos.y() + target.getEyeHeight() + 0.2F, circlePos.z(), 1F);
            }
        }
        if (this.crow.isFlying()) {
            circlingTime++;
        }
    }

    public Vec3 getVultureCirclePos(Vec3 target) {
        float angle = (Maths.EIGHT_STARTING_ANGLE * (clockwise ? -circlingTime : circlingTime));
        double extraX = circleDistance * Mth.sin((angle));
        double extraZ = circleDistance * Mth.cos(angle);
        Vec3 pos = new Vec3(target.x() + extraX, target.y() + yLevel, target.z() + extraZ);
        if (crow.level().isEmptyBlock(AMBlockPos.fromVec3(pos))) {
            return pos;
        }
        return null;
    }
}
