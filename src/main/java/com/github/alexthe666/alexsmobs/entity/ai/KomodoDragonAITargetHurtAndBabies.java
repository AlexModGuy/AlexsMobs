package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class KomodoDragonAITargetHurtAndBabies extends NearestAttackableTargetGoal {

    public KomodoDragonAITargetHurtAndBabies(Mob goalOwnerIn, Class targetClassIn, boolean checkSight) {
        super(goalOwnerIn, targetClassIn, checkSight);
    }


}
