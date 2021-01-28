package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.message.MessageCrowMountPlayer;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

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

    public boolean shouldExecute(){
        return crow.getAttackTarget() != null && !crow.isSitting() && crow.getCommand() != 3;
    }

    public void startExecuting() {
        clockwise = crow.getRNG().nextBoolean();
        yLevel = crow.getRNG().nextInt(2);
        circlingTime = 0;
        maxCircleTime = 20 + crow.getRNG().nextInt(100);
        circleDistance = 1F + crow.getRNG().nextFloat() * 3F;
    }

    public void resetTask() {
        clockwise = crow.getRNG().nextBoolean();
        yLevel = crow.getRNG().nextInt(2);
        circlingTime = 0;
        maxCircleTime = 20 + crow.getRNG().nextInt(100);
        circleDistance = 1F + crow.getRNG().nextFloat() * 3F;
        if(crow.isOnGround()){
            crow.setFlying(false);
        }
    }

    public void tick() {
        if (this.crow.isFlying()) {
            circlingTime++;
        }
        LivingEntity target = crow.getAttackTarget();
        if(circlingTime > maxCircleTime){
            crow.getMoveHelper().setMoveTo(target.getPosX(), target.getPosY() + target.getEyeHeight() / 2F, target.getPosZ(), 1.3F);
            if(crow.getDistance(target) < 2){
               crow.peck();
                if(target.getCreatureAttribute() == CreatureAttribute.UNDEAD){
                    target.attackEntityFrom(DamageSource.MAGIC, 4);
                }else{
                    target.attackEntityFrom(DamageSource.GENERIC, 1);
                }

                resetTask();
            }
        }else{
            Vector3d circlePos = getVultureCirclePos(target.getPositionVec());
            if (circlePos == null) {
                circlePos = target.getPositionVec();
            }
            crow.setFlying(true);
            crow.getMoveHelper().setMoveTo(circlePos.getX(), circlePos.getY() + target.getEyeHeight() + 0.2F, circlePos.getZ(), 1F);

        }
    }

    public Vector3d getVultureCirclePos(Vector3d target) {
        float angle = (0.01745329251F * 8 * (clockwise ? -circlingTime : circlingTime));
        double extraX = circleDistance * MathHelper.sin((angle));
        double extraZ = circleDistance * MathHelper.cos(angle);
        Vector3d pos = new Vector3d(target.getX() + extraX, target.getY() + yLevel, target.getZ() + extraZ);
        if (crow.world.isAirBlock(new BlockPos(pos))) {
            return pos;
        }
        return null;
    }
}
