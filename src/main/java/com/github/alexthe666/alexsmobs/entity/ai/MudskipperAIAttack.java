package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMudBall;
import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.EnumSet;

public class MudskipperAIAttack extends Goal {
    private final EntityMudskipper entity;
    private int shootCooldown = 0;
    private boolean strafed = false;

    public MudskipperAIAttack(EntityMudskipper mob) {
        this.entity = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        return this.entity.getTarget() != null && this.entity.getTarget().isAlive();
    }

    public void stop(){
        this.shootCooldown = 0;
        this.strafed = false;
    }

    public void tick() {
        Entity target = this.entity.getTarget();
        boolean keepFollowing = true;
        if(shootCooldown > 0){
            shootCooldown--;
        }
        if(this.entity.getSensing().hasLineOfSight(target)){
            float dist = this.entity.distanceTo(target);
            if(dist < this.entity.getBbWidth() + target.getBbWidth() + 3.0F){
                keepFollowing = false;
                this.entity.lookAt(target, 360, 360);
                this.entity.getMoveControl().strafe(-3.0F, 0);
                strafed = true;
            }
            if(dist < 8 && shootCooldown == 0){
                EntityMudBall mudball = new EntityMudBall(this.entity.level(), this.entity);
                double d0 = target.getX() - mudball.getX();
                double d1 = target.getY(0.3F) - mudball.getY();
                double d2 = target.getZ() - mudball.getZ();
                float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.4F;
                mudball.shoot(d0, d1 + (double) f, d2, 1F, 10.0F);
                if (!this.entity.isSilent()) {
                    this.entity.gameEvent(GameEvent.PROJECTILE_SHOOT);
                    this.entity.level().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), AMSoundRegistry.MUDSKIPPER_SPIT.get(), this.entity.getSoundSource(), 1.0F, 1.0F + (this.entity.getRandom().nextFloat() - this.entity.getRandom().nextFloat()) * 0.2F);
                }
                this.entity.level().addFreshEntity(mudball);
                shootCooldown = 10 + entity.getRandom().nextInt(10);
                this.entity.openMouth(10);
            }
        }
        if(keepFollowing){
            if(strafed){
                this.entity.getMoveControl().strafe(0, 0);
                strafed = false;
            }
            this.entity.getNavigation().moveTo(target, 1.5F);
        }else{
            this.entity.getNavigation().stop();
        }
    }
}

