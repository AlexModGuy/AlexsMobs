package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;

import java.util.List;

public class AnimalAIRideParent extends Goal {
    private final Animal childAnimal;
    private Animal parentAnimal;
    private final double moveSpeed;
    private int delayCounter;

    public AnimalAIRideParent(Animal animal, double speed) {
        this.childAnimal = animal;
        this.moveSpeed = speed;
    }

    public boolean canUse() {
        if (this.childAnimal.getAge() >= 0 || this.childAnimal.isPassenger()) {
            return false;
        } else {
            List<Animal> list = this.childAnimal.level.getEntitiesOfClass(this.childAnimal.getClass(), this.childAnimal.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
            Animal animalentity = null;
            double d0 = Double.MAX_VALUE;

            for(Animal animalentity1 : list) {
                if (animalentity1.getAge() >= 0 && animalentity1.getPassengers().isEmpty()) {
                    double d1 = this.childAnimal.distanceToSqr(animalentity1);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        animalentity = animalentity1;
                    }
                }
            }

            if (animalentity == null) {
                return false;
            } else if (d0 < 2.0D) {
                return false;
            } else {
                this.parentAnimal = animalentity;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        if (this.childAnimal.getAge() >= 0) {
            return false;
        } else if (parentAnimal == null || !this.parentAnimal.isAlive() || !this.parentAnimal.getPassengers().isEmpty()) {
            return false;
        } else {
            double d0 = this.childAnimal.distanceToSqr(this.parentAnimal);
            return !(d0 < 2.0D) && !(d0 > 256.0D) && !this.childAnimal.isPassengerOfSameVehicle(this.parentAnimal);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.delayCounter = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.parentAnimal = null;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = 10;
            this.childAnimal.getNavigation().moveTo(this.parentAnimal, this.moveSpeed);
        }
        if(this.childAnimal.distanceTo(this.parentAnimal) < 2.0D){
            this.childAnimal.startRiding(this.parentAnimal, false);
            this.stop();
        }
    }
}
