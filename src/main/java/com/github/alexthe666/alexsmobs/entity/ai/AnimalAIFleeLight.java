package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class AnimalAIFleeLight extends Goal {
    protected final PathfinderMob creature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private final double movementSpeed;
    private final Level world;
    private int executeChance = 50;
    private int lightLevel = 10;

    public AnimalAIFleeLight(PathfinderMob p_i1623_1_, double p_i1623_2_) {
        this.creature = p_i1623_1_;
        this.movementSpeed = p_i1623_2_;
        this.world = p_i1623_1_.level();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public AnimalAIFleeLight(PathfinderMob p_i1623_1_, double p_i1623_2_, int chance, int level) {
        this.creature = p_i1623_1_;
        this.movementSpeed = p_i1623_2_;
        this.world = p_i1623_1_.level();
        this.executeChance = chance;
        this.lightLevel = level;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (this.creature.getTarget() != null || this.creature.getRandom().nextInt(executeChance) != 0) {
            return false;
        } else if (this.world.getMaxLocalRawBrightness(this.creature.blockPosition()) < lightLevel) {
            return false;
        } else {
            return this.isPossibleShelter();
        }
    }

    protected boolean isPossibleShelter() {
        Vec3 lvt_1_1_ = this.findPossibleShelter();
        if (lvt_1_1_ == null) {
            return false;
        } else {
            this.shelterX = lvt_1_1_.x;
            this.shelterY = lvt_1_1_.y;
            this.shelterZ = lvt_1_1_.z;
            return true;
        }
    }

    public boolean canContinueToUse() {
        return !this.creature.getNavigation().isDone();
    }

    public void start() {
        this.creature.getNavigation().moveTo(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    @Nullable
    protected Vec3 findPossibleShelter() {
        RandomSource lvt_1_1_ = this.creature.getRandom();
        BlockPos lvt_2_1_ = this.creature.blockPosition();

        for(int lvt_3_1_ = 0; lvt_3_1_ < 10; ++lvt_3_1_) {
            BlockPos lvt_4_1_ = lvt_2_1_.offset(lvt_1_1_.nextInt(20) - 10, lvt_1_1_.nextInt(6) - 3, lvt_1_1_.nextInt(20) - 10);
            if (this.creature.level().getMaxLocalRawBrightness(lvt_4_1_) < lightLevel) {
                return Vec3.atBottomCenterOf(lvt_4_1_);
            }
        }

        return null;
    }
}
