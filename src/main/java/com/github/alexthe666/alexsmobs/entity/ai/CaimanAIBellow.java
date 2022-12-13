package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.EnumSet;

public class CaimanAIBellow extends Goal {

    private EntityCaiman caiman;
    private int bellowTime = 0;

    public CaimanAIBellow(EntityCaiman caiman) {
        this.caiman = caiman;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return caiman.getTarget() == null && caiman.bellowCooldown <= 0 && caiman.isInWaterOrBubble() && !caiman.shouldFollow();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && bellowTime < 60;
    }

    public void stop() {
        bellowTime = 0;
        caiman.bellowCooldown = 1000 + caiman.getRandom().nextInt(1000);
        caiman.setBellowing(false);
    }

    public void tick(){
        if(caiman.isInWaterOrBubble()){
            double d1 = caiman.getFluidTypeHeight(ForgeMod.WATER_TYPE.get());
            caiman.getNavigation().stop();
            if(d1 > 0.3F){
                double d2 = Math.pow(d1 - 0.3F, 2);
                caiman.setDeltaMovement(new Vec3(caiman.getDeltaMovement().x, Math.min(d2 * 0.08F, 0.04F), caiman.getDeltaMovement().z));
            }else{
                caiman.setDeltaMovement(new Vec3(caiman.getDeltaMovement().x, -0.02F, caiman.getDeltaMovement().z));
            }
            if(d1 > 0.19F && d1 < 0.5F){
                bellowTime++;
                caiman.playSound(AMSoundRegistry.CAIMAN_SPLASH.get(), 1, caiman.getVoicePitch());
                caiman.setBellowing(true);
            }
        }
    }
}
