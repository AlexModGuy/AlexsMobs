package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;

import java.util.Iterator;
import java.util.List;

public class AnimalAIFollowParentRanged  extends Goal {
    private final AnimalEntity childAnimal;
    private AnimalEntity parentAnimal;
    private final double moveSpeed;
    private int delayCounter;
    private float range = 8F;
    private float minDist = 3F;
    public AnimalAIFollowParentRanged(AnimalEntity p_i1626_1_, double p_i1626_2_, float range, float minDist) {
        this.childAnimal = p_i1626_1_;
        this.moveSpeed = p_i1626_2_;
        this.range = range;
        this.minDist = minDist;
    }

    public boolean shouldExecute() {
        if (this.childAnimal.getGrowingAge() >= 0) {
            return false;
        } else {
            List<AnimalEntity> lvt_1_1_ = this.childAnimal.world.getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.getBoundingBox().grow(range, range * 0.5D, range));
            AnimalEntity lvt_2_1_ = null;
            double lvt_3_1_ = 1.7976931348623157E308D;
            Iterator var5 = lvt_1_1_.iterator();

            while(var5.hasNext()) {
                AnimalEntity lvt_6_1_ = (AnimalEntity)var5.next();
                if (lvt_6_1_.getGrowingAge() >= 0) {
                    double lvt_7_1_ = this.childAnimal.getDistanceSq(lvt_6_1_);
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

    public boolean shouldContinueExecuting() {
        if (this.childAnimal.getGrowingAge() >= 0) {
            return false;
        } else if (!this.parentAnimal.isAlive()) {
            return false;
        } else {
            double lvt_1_1_ = this.childAnimal.getDistanceSq(this.parentAnimal);
            return lvt_1_1_ >= minDist * minDist && lvt_1_1_ <= range * range;
        }
    }

    public void startExecuting() {
        this.delayCounter = 0;
    }

    public void resetTask() {
        this.parentAnimal = null;
    }

    public void tick() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = 10;
            this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.moveSpeed);
        }
    }
}
