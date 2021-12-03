package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;

import java.util.Iterator;
import java.util.List;

public class AnimalAIFollowParentRanged  extends Goal {
    private final Animal childAnimal;
    private Animal parentAnimal;
    private final double moveSpeed;
    private int delayCounter;
    private float range = 8F;
    private float minDist = 3F;
    public AnimalAIFollowParentRanged(Animal p_i1626_1_, double p_i1626_2_, float range, float minDist) {
        this.childAnimal = p_i1626_1_;
        this.moveSpeed = p_i1626_2_;
        this.range = range;
        this.minDist = minDist;
    }

    public boolean canUse() {
        if (this.childAnimal.getAge() >= 0) {
            return false;
        } else {
            List<? extends Animal> lvt_1_1_ = this.childAnimal.level.getEntitiesOfClass(this.childAnimal.getClass(), this.childAnimal.getBoundingBox().inflate(range, range * 0.5D, range));
            Animal lvt_2_1_ = null;
            double lvt_3_1_ = 1.7976931348623157E308D;
            Iterator var5 = lvt_1_1_.iterator();

            while(var5.hasNext()) {
                Animal lvt_6_1_ = (Animal)var5.next();
                if (lvt_6_1_.getAge() >= 0) {
                    double lvt_7_1_ = this.childAnimal.distanceToSqr(lvt_6_1_);
                    if (lvt_7_1_ <= lvt_3_1_) {
                        lvt_3_1_ = lvt_7_1_;
                        lvt_2_1_ = lvt_6_1_;
                    }
                }
            }

            if (lvt_2_1_ == null) {
                return false;
            } else if (lvt_3_1_ < minDist * minDist) {
                return false;
            } else {
                this.parentAnimal = lvt_2_1_;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        if (this.childAnimal.getAge() >= 0) {
            return false;
        } else if (!this.parentAnimal.isAlive()) {
            return false;
        } else {
            double lvt_1_1_ = this.childAnimal.distanceToSqr(this.parentAnimal);
            return lvt_1_1_ >= minDist * minDist && lvt_1_1_ <= range * range;
        }
    }

    public void start() {
        this.delayCounter = 0;
    }

    public void stop() {
        this.parentAnimal = null;
    }

    public void tick() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = 10;
            this.childAnimal.getNavigation().moveTo(this.parentAnimal, this.moveSpeed);
        }
    }
}
