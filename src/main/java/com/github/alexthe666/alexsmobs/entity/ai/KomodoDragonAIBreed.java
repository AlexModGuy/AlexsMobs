package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class KomodoDragonAIBreed extends BreedGoal {
    boolean withPartner;
    private EntityKomodoDragon komodo;
    int selfBreedTime = 0;

    public KomodoDragonAIBreed(EntityKomodoDragon entityKomodoDragon, double v) {
        super(entityKomodoDragon, v);
        this.komodo = entityKomodoDragon;
    }

    public boolean shouldExecute(){
        boolean prev = super.shouldExecute();
        withPartner = prev;
        return withPartner || animal.isInLove();
    }

    public boolean shouldContinueExecuting() {
        return withPartner ? super.shouldContinueExecuting() : selfBreedTime < 60;
    }

    public void resetTask() {
        super.resetTask();
        selfBreedTime = 0;
    }

    public void tick() {
        if(withPartner){
            super.tick();
        }else{
            this.animal.getNavigator().clearPath();
            ++this.selfBreedTime;
            if (this.selfBreedTime >= 60) {
                this.spawnParthogenicBaby();
            }
        }
    }

    protected void spawnBaby() {
        for(int i = 0; i < 2 + this.animal.getRNG().nextInt(2); i++){
            this.animal.func_234177_a_((ServerWorld)this.world, this.targetMate);
        }
        komodo.slaughterCooldown = 200;
    }

    private void spawnParthogenicBaby() {
        for(int i = 0; i < 2 + this.animal.getRNG().nextInt(2); i++){
            this.animal.func_234177_a_((ServerWorld)this.world, this.animal);
        }
        komodo.slaughterCooldown = 200;
    }
}
