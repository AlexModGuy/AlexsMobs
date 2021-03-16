package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class AnimalAIFleeLight extends Goal {
    protected final CreatureEntity creature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private final double movementSpeed;
    private final World world;
    private int executeChance = 50;

    public AnimalAIFleeLight(CreatureEntity p_i1623_1_, double p_i1623_2_) {
        this.creature = p_i1623_1_;
        this.movementSpeed = p_i1623_2_;
        this.world = p_i1623_1_.world;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (this.creature.getAttackTarget() != null || this.creature.getRNG().nextInt(executeChance) != 0) {
            return false;
        } else if (this.world.getLight(this.creature.getPosition()) < 10) {
            return false;
        } else {
            return this.isPossibleShelter();
        }
    }

    protected boolean isPossibleShelter() {
        Vector3d lvt_1_1_ = this.findPossibleShelter();
        if (lvt_1_1_ == null) {
            return false;
        } else {
            this.shelterX = lvt_1_1_.x;
            this.shelterY = lvt_1_1_.y;
            this.shelterZ = lvt_1_1_.z;
            return true;
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    public void startExecuting() {
        this.creature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    @Nullable
    protected Vector3d findPossibleShelter() {
        Random lvt_1_1_ = this.creature.getRNG();
        BlockPos lvt_2_1_ = this.creature.getPosition();

        for(int lvt_3_1_ = 0; lvt_3_1_ < 10; ++lvt_3_1_) {
            BlockPos lvt_4_1_ = lvt_2_1_.add(lvt_1_1_.nextInt(20) - 10, lvt_1_1_.nextInt(6) - 3, lvt_1_1_.nextInt(20) - 10);
            if (this.creature.world.getLight(lvt_4_1_) < 10) {
                return Vector3d.copyCenteredHorizontally(lvt_4_1_);
            }
        }

        return null;
    }
}
