package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class AnimalAIFleeAdult extends Goal {
    private final AnimalEntity childAnimal;
    private AnimalEntity parentAnimal;
    private final double moveSpeed;
    private double fleeDistance;
    private int delayCounter;
    private Path path;

    public AnimalAIFleeAdult(AnimalEntity animal, double speed, double fleeDistance) {
        this.childAnimal = animal;
        this.moveSpeed = speed;
        this.fleeDistance = fleeDistance;
    }

    public boolean shouldExecute() {
        if (this.childAnimal.getGrowingAge() >= 0) {
            return false;
        } else {
            List<AnimalEntity> list = this.childAnimal.world.getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.getBoundingBox().grow(fleeDistance, 4.0D, fleeDistance));
            AnimalEntity animalentity = null;
            double d0 = Double.MAX_VALUE;

            for(AnimalEntity animalentity1 : list) {
                if (animalentity1.getGrowingAge() >= 0) {
                    double d1 = this.childAnimal.getDistanceSq(animalentity1);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        animalentity = animalentity1;
                    }
                }
            }

            if (animalentity == null) {
                return false;
            } else if (d0 > 19.0D) {
                return false;
            } else {
                this.parentAnimal = animalentity;
                Vector3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.childAnimal, (int) fleeDistance, 7, new Vector3d(this.parentAnimal.getPosX(), this.parentAnimal.getPosY(), this.parentAnimal.getPosZ()));
                if (vec3d == null) {
                    return false;
                } else if (this.parentAnimal.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.parentAnimal.getDistanceSq(this.childAnimal)) {
                    return false;
                } else {
                    this.path = childAnimal.getNavigator().getPathToPos(new BlockPos(vec3d.x, vec3d.y, vec3d.z), 0);
                    return this.path != null;
                }
            }
        }
    }

    public boolean shouldContinueExecuting() {
        if (this.childAnimal.getGrowingAge() >= 0) {
            return false;
        } else if (!this.parentAnimal.isAlive()) {
            return false;
        } else {
            return !childAnimal.getNavigator().noPath();
        }
    }


    public void startExecuting() {
        childAnimal.getNavigator().setPath(this.path, moveSpeed);
    }
    public void resetTask() {
        this.parentAnimal = null;
        this.childAnimal.getNavigator().clearPath();
        this.path = null;
    }

    public void tick() {
    }
}
