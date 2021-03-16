package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SealAIBask extends Goal {
    private final EntitySeal seal;

    public SealAIBask(EntitySeal seal) {
        this.seal = seal;
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean shouldContinueExecuting() {
        return this.seal.isBasking() && !this.seal.isInWaterOrBubbleColumn();
    }

    public boolean shouldExecute() {
        if (this.seal.isInWaterOrBubbleColumn()) {
            return false;
        } else {
            return seal.getRevengeTarget() == null && seal.isBasking();
        }
    }

    public void tick() {
        this.seal.getNavigator().clearPath();
    }

    public void resetTask() {
        this.seal.setBasking(false);
    }
}
