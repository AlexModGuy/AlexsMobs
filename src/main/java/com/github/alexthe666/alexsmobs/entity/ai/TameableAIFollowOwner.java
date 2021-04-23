package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;

public class TameableAIFollowOwner extends FollowOwnerGoal {

    private IFollower follower;

    public TameableAIFollowOwner(TameableEntity tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        super(tameable, speed, minDist, maxDist, teleportToLeaves);
        this.follower = (IFollower)tameable;
    }



    public boolean shouldExecute(){

        return super.shouldExecute() && follower.shouldFollow();
    }
}
