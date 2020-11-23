package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class AnimalAIWanderRanged extends RandomWalkingGoal {
    protected final float probability;
    protected final int xzRange;
    protected final int yRange;

    public AnimalAIWanderRanged(CreatureEntity creature, int chance, double speedIn, int xzRange, int yRange) {
        this(creature, chance, speedIn, 0.001F, xzRange, yRange);
    }

    public AnimalAIWanderRanged(CreatureEntity creature, int chance, double speedIn, float probabilityIn, int xzRange, int yRange) {
        super(creature, speedIn, chance);
        this.probability = probabilityIn;
        this.xzRange = xzRange;
        this.yRange = yRange;
    }

    @Nullable
    protected Vector3d getPosition() {
        if (this.creature.isInWaterOrBubbleColumn()) {
            Vector3d vector3d = RandomPositionGenerator.getLandPos(this.creature, xzRange, yRange);
            return vector3d == null ? super.getPosition() : vector3d;
        } else {
            return this.creature.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.creature, xzRange, yRange) : super.getPosition();
        }
    }
}
