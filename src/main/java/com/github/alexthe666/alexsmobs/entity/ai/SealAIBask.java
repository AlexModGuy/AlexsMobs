package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class SealAIBask extends Goal {
    private final EntitySeal seal;

    public SealAIBask(EntitySeal seal) {
        this.seal = seal;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.seal.isBasking() && !this.seal.isInWaterOrBubble();
    }

    public boolean canUse() {
        if (this.seal.isInWaterOrBubble()) {
            return false;
        } else {
            return seal.getLastHurtByMob() == null && seal.isBasking();
        }
    }

    public void tick() {
        this.seal.getNavigation().stop();
    }

    public void stop() {
        this.seal.setBasking(false);
    }
}
