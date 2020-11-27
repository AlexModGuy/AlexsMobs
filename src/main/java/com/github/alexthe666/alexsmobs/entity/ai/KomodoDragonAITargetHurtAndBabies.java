package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

public class KomodoDragonAITargetHurtAndBabies extends NearestAttackableTargetGoal {

    public KomodoDragonAITargetHurtAndBabies(MobEntity goalOwnerIn, Class targetClassIn, boolean checkSight) {
        super(goalOwnerIn, targetClassIn, checkSight);
    }


}
