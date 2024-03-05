package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCosmicCod;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.Predicate;

public class CosmicCodAIFollowLeader extends Goal {
    private static final int INTERVAL_TICKS = 200;
    private final EntityCosmicCod mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public CosmicCodAIFollowLeader(EntityCosmicCod cod) {
        this.mob = cod;
        this.nextStartTick = this.nextStartTick(cod);
    }

    protected int nextStartTick(EntityCosmicCod p_25252_) {
        return reducedTickDelay(100 + p_25252_.getRandom().nextInt(100) % 20);
    }

    public boolean canUse() {
        if (this.mob.isGroupLeader() || this.mob.isCircling()) {
            return false;
        } else if (this.mob.hasGroupLeader()) {
            return true;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            Predicate<EntityCosmicCod> predicate = (p_25258_) -> {
                return p_25258_.canGroupGrow() || !p_25258_.hasGroupLeader();
            };
            List<EntityCosmicCod> list = this.mob.level().getEntitiesOfClass(EntityCosmicCod.class, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), predicate);
            EntityCosmicCod cc = DataFixUtils.orElse(list.stream().filter(EntityCosmicCod::canGroupGrow).findAny(), this.mob);
            cc.createFromStream(list.stream().filter((p_25255_) -> {
                return !p_25255_.hasGroupLeader();
            }));
            return this.mob.hasGroupLeader();
        }
    }

    public boolean canContinueToUse() {
        return this.mob.hasGroupLeader() && this.mob.inRangeOfGroupLeader() && !this.mob.isCircling();
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.mob.leaveGroup();
    }

    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.mob.moveToGroupLeader();
        }
    }
}