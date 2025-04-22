package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.EnumSet;
import java.util.List;

import com.github.alexthe666.alexsmobs.entity.EntitySeagull;

public class SeagullAITargetSeeds extends Goal {
    private final EntitySeagull seagull;
    private ItemEntity targetSeed;

    public SeagullAITargetSeeds(EntitySeagull seagull) {
        this.seagull = seagull;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        List<ItemEntity> items = seagull.level().getEntitiesOfClass(
            ItemEntity.class,
            seagull.getBoundingBox().inflate(10.0),
            item -> item.isAlive() && isSeed(item.getItem())
        );

        if (!items.isEmpty()) {
            targetSeed = items.get(0);
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (targetSeed != null) {
            seagull.getNavigation().moveTo(targetSeed, 1.0);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return targetSeed != null && targetSeed.isAlive();
    }

    @Override
    public void tick() {
        if (targetSeed != null) {
            seagull.getNavigation().moveTo(targetSeed, 1.0);

            if (seagull.distanceToSqr(targetSeed) < 2.0) {
                targetSeed.discard(); // "Eat" the seed
                targetSeed = null;
            }
        }
    }

    private boolean isSeed(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.WHEAT_SEEDS || item == Items.MELON_SEEDS ||
               item == Items.PUMPKIN_SEEDS || item == Items.BEETROOT_SEEDS;
    }
}

