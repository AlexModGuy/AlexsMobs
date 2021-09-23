package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FroststalkerAIFollowLeader  extends Goal {
    private static final int INTERVAL_TICKS = 200;
    private final EntityFroststalker mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public FroststalkerAIFollowLeader(EntityFroststalker froststalker) {
        this.mob = froststalker;
        this.nextStartTick = this.nextStartTick(froststalker);
    }

    protected int nextStartTick(EntityFroststalker froststalker) {
        return 100 + froststalker.getRandom().nextInt(200) % 40;
    }

    public boolean canUse() {
        if (this.mob.hasFollowers()) {
            return false;
        } else if (this.mob.isFollower()) {
            return true;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            Predicate<Player> playerPredicate = (player) -> {
                return player.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.FROSTSTALKER_HELMET);
            };
            Predicate<EntityFroststalker> froststalkerPredicate = (p_25258_) -> {
                return p_25258_.canBeFollowed() || !p_25258_.isFollower();
            };
            float range = 60F;
            List<Player> playerList = this.mob.level.getEntitiesOfClass(Player.class, this.mob.getBoundingBox().inflate(range, range, range), playerPredicate);
            Player closestPlayer = null;
            for(Player player : playerList){
                if(closestPlayer == null || player.distanceTo(mob) < closestPlayer.distanceTo(mob)){
                    closestPlayer = player;
                }
            }
            if(closestPlayer == null){
                List<EntityFroststalker> list = this.mob.level.getEntitiesOfClass(EntityFroststalker.class, this.mob.getBoundingBox().inflate(range, range, range), froststalkerPredicate);
                EntityFroststalker entityFroststalker = DataFixUtils.orElse(list.stream().filter(EntityFroststalker::canBeFollowed).findAny(), this.mob);
                entityFroststalker.addFollowers(list.stream().filter((p_25255_) -> {
                    return !p_25255_.isFollower();
                }));
            }else{
                this.mob.startFollowing(closestPlayer);
            }

            return this.mob.isFollower();
        }
    }

    public boolean canContinueToUse() {
        return this.mob.isFollower() && this.mob.inRangeOfLeader();
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.mob.stopFollowing();
    }

    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            this.mob.pathToLeader();

        }
    }
}