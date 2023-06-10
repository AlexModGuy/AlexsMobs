package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class GorillaAIChargeLooker extends Goal {
    private final EntityGorilla gorilla;
    private final double range = 20D;
    private Player starer;
    private double speed;
    private int runDelay = 0;

    public GorillaAIChargeLooker(EntityGorilla gorilla, double speed) {
        this.setFlags(EnumSet.of(Flag.LOOK, Goal.Flag.MOVE));
        this.gorilla = gorilla;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        if (this.gorilla.isSilverback() && !this.gorilla.isTame() && runDelay-- == 0) {
            runDelay = 100 + gorilla.getRandom().nextInt(200);
            List<Player> playerList = this.gorilla.level().getEntitiesOfClass(Player.class, this.gorilla.getBoundingBox().inflate(range, range, range), EntitySelector.NO_SPECTATORS);
            Player closestPlayer = null;
            for (Player player : playerList) {
                if (isLookingAtMe(player)) {
                    if (closestPlayer == null || player.distanceTo(gorilla) < closestPlayer.distanceTo(gorilla)) {
                        closestPlayer = player;
                    }
                }
            }
            starer = closestPlayer;
            return starer != null;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return starer != null && this.gorilla.isAlive();
    }

    @Override
    public void stop() {
        this.starer = null;
        this.gorilla.setSprinting(false);
        runDelay = 300 + gorilla.getRandom().nextInt(200);
    }

    public void tick(){
        this.gorilla.setOrderedToSit(false);
        this.gorilla.poundChestCooldown = 50;
        if(this.gorilla.distanceTo(starer) > 1 + starer.getBbWidth() + this.gorilla.getBbWidth()){
            this.gorilla.getNavigation().moveTo(starer, speed);
            this.gorilla.setSprinting(!this.gorilla.isSitting() && !this.gorilla.isStanding());
        }else{
            this.gorilla.getNavigation().stop();
            this.gorilla.lookAt(starer, 180, 30);
            this.gorilla.setSprinting(false);
            if(this.gorilla.getAnimation() == IAnimatedEntity.NO_ANIMATION){
                this.gorilla.setStanding(true);
                this.gorilla.maxStandTime = 45;
                this.gorilla.setAnimation(EntityGorilla.ANIMATION_POUNDCHEST);
            }
            if(this.gorilla.getAnimation() == EntityGorilla.ANIMATION_POUNDCHEST && this.gorilla.getAnimationTick() >= 10){
                this.stop();
            }
        }
    }

    private boolean isLookingAtMe(Player player) {
        Vec3 vec3 = player.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(gorilla.getX() - player.getX(), gorilla.getEyeY() - player.getEyeY(), gorilla.getZ() - player.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1.0D - 0.025D / d0 && player.hasLineOfSight(gorilla);
    }
}
