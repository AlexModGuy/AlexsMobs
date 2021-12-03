package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;

public class FlyingAITargetDroppedItems extends CreatureAITargetItems {

    public FlyingAITargetDroppedItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
        super(creature, checkSight, onlyNearby, tickThreshold, radius);
        this.executionChance = 1;
    }

    public void stop() {
        super.stop();
        hunter.setItemFlag(false);
    }

    public boolean canUse() {
        return super.canUse() && (mob.getTarget() == null || !mob.getTarget().isAlive());
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && (mob.getTarget() == null || !mob.getTarget().isAlive());
    }

    @Override
    protected void moveTo() {
        if (this.targetEntity != null) {
            hunter.setItemFlag(true);
            if (this.mob.distanceTo(targetEntity) < 2) {
                mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), targetEntity.getY(), this.targetEntity.getZ(), 1.5F);
                hunter.peck();
            }
            if (this.mob.distanceTo(this.targetEntity) > 8 || hunter.isFlying()) {
                hunter.setFlying(true);
                float f = (float) (mob.getX() - targetEntity.getX());
                float f1 = 1.8F;
                float f2 = (float) (mob.getZ() - targetEntity.getZ());
                float xzDist = Mth.sqrt(f * f + f2 * f2);

                if (!mob.hasLineOfSight(targetEntity)) {
                    mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), 1 + mob.getY(), this.targetEntity.getZ(), 1.5F);
                } else {
                    if (xzDist < 5) {
                        f1 = 0;
                    }
                    mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), f1 + this.targetEntity.getY(), this.targetEntity.getZ(), 1.5F);
                }
            } else {
                this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.5F);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        moveTo();
    }
}
