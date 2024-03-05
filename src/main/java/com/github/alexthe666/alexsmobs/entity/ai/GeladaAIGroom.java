package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGeladaMonkey;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GeladaAIGroom extends Goal {

    private final EntityGeladaMonkey monkey;
    private int groomTime = 0;
    private int groomCooldown = 220;
    private EntityGeladaMonkey beingGroomed;

    public GeladaAIGroom(EntityGeladaMonkey monkey) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.monkey = monkey;
    }

    @Override
    public boolean canUse() {
        if(groomCooldown > 0){
            groomCooldown--;
            return false;
        }else{
            groomCooldown = 200 + monkey.getRandom().nextInt(1000);
            EntityGeladaMonkey nearestMonkey = null;
            for (EntityGeladaMonkey entity : monkey.level().getEntitiesOfClass(EntityGeladaMonkey.class, monkey.getBoundingBox().inflate(15F))) {
                if (entity.getId() != monkey.getId() && monkey.canBeGroomed() && (nearestMonkey == null || monkey.distanceTo(nearestMonkey) > monkey.distanceTo(entity))) {
                    nearestMonkey = entity;
                }
            }
            beingGroomed = nearestMonkey;
            return beingGroomed != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return beingGroomed != null && beingGroomed.isAlive() && !beingGroomed.shouldStopBeingGroomed() && groomTime < 200 && (beingGroomed.groomerID == -1 || beingGroomed.groomerID == monkey.getId());
    }

    public void stop(){
        groomTime = 0;
        monkey.isGrooming = false;
        if(beingGroomed != null){
            beingGroomed.groomerID = -1;
        }
        beingGroomed = null;
    }

    public void tick() {
        double dist = monkey.distanceTo(beingGroomed);
        if(dist < monkey.getBbWidth() + 0.5F){
            monkey.isGrooming = true;
            beingGroomed.groomerID = monkey.getId();
            monkey.setSitting(true);
            groomTime++;
            if(groomTime % 50 == 0){
                monkey.heal(1);
            }
            if(monkey.getAnimation() == IAnimatedEntity.NO_ANIMATION){
                monkey.setAnimation(EntityGeladaMonkey.ANIMATION_GROOM);
            }
            monkey.getNavigation().stop();
            monkey.lookAt(beingGroomed, 360, 360);
        }else{
            monkey.isGrooming = false;
            beingGroomed.groomerID = -1;
            monkey.setSitting(false);
            monkey.getNavigation().moveTo(beingGroomed, 1);
        }
    }
}
