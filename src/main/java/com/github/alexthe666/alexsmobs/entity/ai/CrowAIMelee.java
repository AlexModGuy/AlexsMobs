package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CrowAIMelee extends Goal {
    private EntityCrow crow;
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
        if(crow.isOnGround()){
            crow.setFlying(false);
        }
    }

    public void tick() {
        if (this.crow.isFlying()) {
            circlingTime++;
        }
        LivingEntity target = crow.getTarget();
        if(circlingTime > maxCircleTime){
            crow.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() / 2F, target.getZ(), 1.3F);
            if(crow.distanceTo(target) < 2){
               crow.peck();
                if(target.getMobType() == MobType.UNDEAD){
                    target.hurt(DamageSource.MAGIC, 4);
                }else{
                    target.hurt(DamageSource.GENERIC, 1);
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

    public Vec3 getVultureCirclePos(Vec3 target) {
        float angle = (0.01745329251F * 8 * (clockwise ? -circlingTime : circlingTime));
        double extraX = circleDistance * Mth.sin((angle));
        double extraZ = circleDistance * Mth.cos(angle);
        Vec3 pos = new Vec3(target.x() + extraX, target.y() + yLevel, target.z() + extraZ);
        if (crow.level.isEmptyBlock(new BlockPos(pos))) {
            return pos;
        }
        return null;
    }
}
