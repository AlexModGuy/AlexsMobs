package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMungus;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumSet;

public class MungusAITemptMushroom extends Goal {

    private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
    private final TargetingConditions targetingConditions;
    protected final EntityMungus mob;
    private final double speedModifier;
    private double px;
    private double py;
    private double pz;
    private int calmDown;
    private double pRotX;
    private double pRotY;
    protected Player player;

    public MungusAITemptMushroom(EntityMungus p_25939_, double p_25940_) {
        this.mob = p_25939_;
        this.speedModifier = p_25940_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.targetingConditions = TEMP_TARGETING.copy();
    }

    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            this.player = this.mob.level.getNearestPlayer(this.targetingConditions, this.mob);
            if(this.player != null){
                return shouldFollow(this.player.getMainHandItem()) || shouldFollow(this.player.getOffhandItem());
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        return this.canUse();
    }

    public void start() {
        this.px = this.player.getX();
        this.py = this.player.getY();
        this.pz = this.player.getZ();
    }

    public void stop() {
        this.player = null;
        this.mob.getNavigation().stop();
    }

    public void tick() {
        this.mob.getLookControl().setLookAt(this.player, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.player) < 6.25D) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.player, this.speedModifier);
        }

    }

    protected boolean shouldFollow(ItemStack stack) {
        return mob.shouldFollowMushroom(stack) || stack.getItem() == AMItemRegistry.MUNGAL_SPORES;
    }
}
