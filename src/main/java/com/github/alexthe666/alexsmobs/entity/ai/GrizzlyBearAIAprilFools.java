package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.EnumSet;

public class GrizzlyBearAIAprilFools extends Goal {

    private EntityGrizzlyBear bear;
    private Player target;
    private int runDelay = 0;
    private double maxDistance = 13;
    private int powerOutTimer = 0;
    private int musicBoxTimer = 0;
    private int maxMusicBoxTime = 0;
    private int leapTimer = 0;

    public GrizzlyBearAIAprilFools(EntityGrizzlyBear bear){
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.bear = bear;
    }

    @Override
    public boolean canUse() {
        if(!bear.isBaby() && AlexsMobs.isAprilFools() && runDelay-- <= 0 && bear.getRandom().nextInt(30) == 0){
            runDelay = 400 + bear.getRandom().nextInt(350);
            Player nearestPlayer = bear.level.getNearestPlayer(bear.getX(), bear.getY(), bear.getZ(), maxDistance, entity -> {
                return bear.hasLineOfSight(entity) &&(!(entity instanceof Player) || !((Player) entity).hasEffect(AMEffectRegistry.POWER_DOWN.get()));
            });
            if(nearestPlayer != null){
                target = nearestPlayer;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && bear.distanceTo(target) < maxDistance * 2;
    }

    public void start(){
        maxMusicBoxTime = 100 + bear.getRandom().nextInt(130);
    }

    @Override
    public void tick() {
        super.tick();
        double dist = bear.distanceTo(target);
        bear.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        if(dist <= 6 && bear.hasLineOfSight(target)){
            bear.getNavigation().stop();
            if(bear.getAprilFoolsFlag() == 5){
                leapTimer++;
                if(leapTimer == 7){
                    AlexsMobs.sendMSGToAll(new MessageSendVisualFlagFromServer(target.getId(), 87));
                }
                if(leapTimer >= 10){
                    bear.setAprilFoolsFlag(0);
                    if(bear.level.getLevelData().isHardcore()){
                        target.hurt(AMDamageTypes.BEAR_FREDDY, target.getMaxHealth() - 1);
                        target.setHealth(1);
                    }else{
                        target.hurt(AMDamageTypes.BEAR_FREDDY, target.getMaxHealth() + 1000F);
                    }
                    stop();
                    return;
                }
            }else if(bear.getAprilFoolsFlag() < 4) {
                if(powerOutTimer == 0){
                    target.addEffect(new MobEffectInstance(AMEffectRegistry.POWER_DOWN.get(), 2 * (maxMusicBoxTime + 100), 0, false, false, true));
                }
                powerOutTimer++;
                if (powerOutTimer >= 60) {
                    bear.setAprilFoolsFlag(4);
                    powerOutTimer = 0;
                }else{
                    bear.setAprilFoolsFlag(3);
                }
            }else{
                if(musicBoxTimer == 0){
                    bear.level.broadcastEntityEvent(bear, (byte) 67);
                }
                musicBoxTimer++;
                if (musicBoxTimer >= maxMusicBoxTime) {
                    if(bear.getAprilFoolsFlag() != 5){
                        bear.level.broadcastEntityEvent(bear, (byte) 68);
                        bear.setAprilFoolsFlag(5);
                        bear.gameEvent(GameEvent.ENTITY_ROAR);
                        bear.playSound(AMSoundRegistry.APRIL_FOOLS_SCREAM.get(), 3, 1);
                        musicBoxTimer = 0;
                    }
                }
            }
            if(bear.getAprilFoolsFlag() < 2){
                bear.setAprilFoolsFlag(2);
            }
        }else{
            bear.getNavigation().moveTo(target, 1.2F);
            if(bear.getAprilFoolsFlag() < 1){
                bear.setAprilFoolsFlag(1);
            }
        }
    }

    @Override
    public void stop(){
        target = null;
        runDelay = 100 + bear.getRandom().nextInt(100);
        bear.setAprilFoolsFlag(0);
        powerOutTimer = 0;
        musicBoxTimer = 0;
        leapTimer = 0;
    }
}
