package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import java.util.EnumSet;

public class RaccoonAIBeg extends Goal {
    private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(32D).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();
    protected final EntityRaccoon raccoon;
    private final double speed;
    protected PlayerEntity closestPlayer;
    private int delayTemptCounter;
    private boolean isRunning;

    public RaccoonAIBeg(EntityRaccoon raccoon, double speed) {
        this.raccoon = raccoon;
        this.speed = speed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean shouldExecute() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            if(!this.raccoon.getHeldItemMainhand().isEmpty()){
                return false;
            }
            this.closestPlayer = this.raccoon.world.getClosestPlayer(ENTITY_PREDICATE, this.raccoon);
            if (this.closestPlayer == null) {
                return false;
            } else {
                boolean food =  this.closestPlayer.getHeldItemMainhand().isFood() || this.closestPlayer.getHeldItemOffhand().isFood();
                return food;
            }
        }
    }


    public boolean shouldContinueExecuting() {
        return this.raccoon.getHeldItemMainhand().isEmpty() && this.shouldExecute();
    }

    public void startExecuting() {
        this.isRunning = true;
    }

    public void resetTask() {
        this.closestPlayer = null;
        this.raccoon.getNavigator().clearPath();
        this.delayTemptCounter = 100;
        this.raccoon.setBegging(false);
        this.isRunning = false;
    }

    public void tick() {
        this.raccoon.getLookController().setLookPositionWithEntity(this.closestPlayer, (float)(this.raccoon.getHorizontalFaceSpeed() + 20), (float)this.raccoon.getVerticalFaceSpeed());
        if (this.raccoon.getDistanceSq(this.closestPlayer) < 12D) {
            this.raccoon.getNavigator().clearPath();
            this.raccoon.setBegging(true);
        } else {
            this.raccoon.getNavigator().tryMoveToEntityLiving(this.closestPlayer, this.speed);
        }

    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
