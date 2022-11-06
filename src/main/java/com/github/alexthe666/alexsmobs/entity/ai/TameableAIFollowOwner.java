package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;

public class TameableAIFollowOwner extends FollowOwnerGoal {

    private IFollower follower;
    private TamableAnimal tameable;

    public TameableAIFollowOwner(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        super(tameable, speed, minDist, maxDist, teleportToLeaves);
        this.follower = (IFollower)tameable;
        this.tameable = tameable;
    }

    public boolean canUse(){
        return super.canUse() && follower.shouldFollow() && !isInCombat();
    }

    public boolean canContinueToUse(){
        return super.canContinueToUse() && follower.shouldFollow() && !isInCombat();
    }

    private boolean isInCombat() {
        Entity owner = tameable.getOwner();
        if(owner != null){
            return tameable.distanceTo(owner) < 30 && tameable.getTarget() != null && tameable.getTarget().isAlive();
        }
        return false;
    }
}
