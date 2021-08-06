package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntitySeagull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SeagullAIStealFromPlayers extends Goal {

    private EntitySeagull seagull;
    private Vector3d fleeVec = null;
    private PlayerEntity target;
    private int fleeTime = 0;

    public SeagullAIStealFromPlayers(EntitySeagull entitySeagull) {
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.TARGET));
        this.seagull = entitySeagull;
    }

    @Override
    public boolean shouldExecute() {
        long worldTime = this.seagull.world.getGameTime() % 10;
        if (this.seagull.getIdleTime() >= 100 && worldTime != 0 || seagull.isSitting()) {
            return false;
        }
        if (this.seagull.getRNG().nextInt(12) != 0 && worldTime != 0 || seagull.stealCooldown > 0) {
            return false;
        }
        if(this.seagull.getHeldItemMainhand().isEmpty()){
            PlayerEntity valid = getClosestValidPlayer();
            if(valid != null){
                target = valid;
                return true;
            }
        }
        return false;
    }

    public void startExecuting(){
        this.seagull.aiItemFlag = true;
    }

    public void resetTask(){
        this.seagull.aiItemFlag = false;
        target = null;
        fleeVec = null;
        fleeTime = 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return target != null && !target.isCreative() && (seagull.getHeldItemMainhand().isEmpty() || fleeTime > 0);
    }

    public void tick(){
        seagull.setFlying(true);
        seagull.getMoveHelper().setMoveTo(target.getPosX(), target.getPosYEye(), target.getPosZ(), 1.2F);
        if(seagull.getDistance(target) < 2F && seagull.getHeldItemMainhand().isEmpty()){
            if(hasFoods(target)){
                ItemStack foodStack = getFoodItemFrom(target);
                if(!foodStack.isEmpty()){
                    ItemStack copy = foodStack.copy();
                    foodStack.shrink(1);
                    copy.setCount(1);
                    seagull.peck();
                    seagull.setHeldItem(Hand.MAIN_HAND, copy);
                    fleeTime = 60;
                    seagull.stealCooldown = 1500 + seagull.getRNG().nextInt(1500);
                }else{
                    resetTask();
                }
            }else{
                resetTask();
            }
        }
        if(fleeTime > 0){
            if(fleeVec == null){
                fleeVec = seagull.getBlockInViewAway(target.getPositionVec(), 4);
            }
            if(fleeVec != null){
                seagull.setFlying(true);
                seagull.getMoveHelper().setMoveTo(fleeVec.x, fleeVec.y, fleeVec.z, 1.2F);
                if(seagull.getDistanceSq(fleeVec) < 5){
                    fleeVec = seagull.getBlockInViewAway(fleeVec, 4);
                }
            }
            fleeTime--;
        }
    }

    private PlayerEntity getClosestValidPlayer(){
        List<PlayerEntity> list = seagull.world.getEntitiesWithinAABB(PlayerEntity.class, seagull.getBoundingBox().grow(10, 25, 10), EntityPredicates.CAN_AI_TARGET);
        PlayerEntity closest = null;
        if(!list.isEmpty()){
            for(PlayerEntity player : list){
                if((closest == null || closest.getDistance(seagull) > player.getDistance(seagull)) && hasFoods(player)){
                    closest = player;
                }
            }
        }
        return closest;
    }

    private boolean hasFoods(PlayerEntity player){
        for(int i = 0; i < 9; i++){
            ItemStack stackIn = player.inventory.mainInventory.get(i);
            if(stackIn.isFood()){
                return true;
            }
        }
        return false;
    }

    private ItemStack getFoodItemFrom(PlayerEntity player){
        List<ItemStack> foods = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            ItemStack stackIn = player.inventory.mainInventory.get(i);
            if(stackIn.isFood()){
                foods.add(stackIn);
            }
        }
        if(!foods.isEmpty()){
            return foods.get(foods.size() <= 1 ? 0 : seagull.getRNG().nextInt(foods.size() - 1));
        }
        return ItemStack.EMPTY;
    }
}
