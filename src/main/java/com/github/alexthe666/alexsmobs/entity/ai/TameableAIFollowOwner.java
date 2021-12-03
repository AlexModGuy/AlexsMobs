package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.IFollower;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.TamableAnimal;

public class TameableAIFollowOwner extends FollowOwnerGoal {

    private IFollower follower;

    public TameableAIFollowOwner(TamableAnimal tameable, double speed, float minDist, float maxDist, boolean teleportToLeaves) {
        super(tameable, speed, minDist, maxDist, teleportToLeaves);
        this.follower = (IFollower)tameable;
    }



    public boolean canUse(){

        return super.canUse() && follower.shouldFollow();
    }
}
