package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class SeagullAIRevealTreasure extends Goal {

    private EntitySeagull seagull;
    private BlockPos sitPos;

    public SeagullAIRevealTreasure(EntitySeagull entitySeagull) {
        this.seagull = entitySeagull;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        return seagull.getTreasurePos() != null && seagull.treasureSitTime > 0;
    }

    public void startExecuting(){
        seagull.aiItemFlag = true;
        sitPos = seagull.getSeagullGround(seagull.getTreasurePos());
    }

    public void resetTask(){
        sitPos = null;
        seagull.setSitting(false);
        seagull.aiItemFlag = false;
    }

    public void tick(){
        if(sitPos != null){
            if(seagull.getDistanceSq(new Vector3d(sitPos.getX() + 0.5F, seagull.getPosY(), sitPos.getZ() + 0.5F)) > 2.5F){
                seagull.getMoveHelper().setMoveTo(sitPos.getX() + 0.5F, sitPos.getY() + 2, sitPos.getZ() + 0.5F, 1F);
                if(!seagull.isOnGround()){
                    seagull.setFlying(true);
                }
            }else{
                Vector3d vec = Vector3d.copyCenteredWithVerticalOffset(sitPos, 1.0F);
                if(vec.subtract(seagull.getPositionVec()).length() > 0.04F){
                    seagull.setMotion(vec.subtract(seagull.getPositionVec()).scale(0.2F));
                }
                seagull.eatItem();
                seagull.treasureSitTime = Math.min(seagull.treasureSitTime, 100);
                seagull.setFlying(false);
                seagull.setSitting(true);
            }
        }
    }

}
