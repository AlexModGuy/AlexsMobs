package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CaimanAIMelee extends Goal {
    private EntityCaiman caiman;
    private int grabTime = 0;

    public CaimanAIMelee(EntityCaiman caiman) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.caiman = caiman;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = caiman.getTarget();
        return target != null && target.isAlive();
    }

    public void stop(){
        caiman.setHeldMobId(-1);
        grabTime = 0;
    }

    public void tick() {
        if(grabTime < 0){
            grabTime++;
        }
        LivingEntity target = caiman.getTarget();
        if(target != null){
            double bbWidth = (caiman.getBbWidth() + target.getBbWidth()) / 2D;
            double dist = caiman.distanceTo(target);
            boolean flag = false;
            if(dist < bbWidth + 2F){
               if(grabTime >= 0){
                   if(grabTime % 25 == 0){
                       target.hurt(caiman.isTame() ? DamageSource.DROWN : this.damageSources().mobAttack(caiman), (float) caiman.getAttributeValue(Attributes.ATTACK_DAMAGE));
                   }
                   grabTime++;
                   Vec3 shakePreyPos = caiman.getShakePreyPos();
                   Vec3 minus = new Vec3(shakePreyPos.x - target.getX(), 0, shakePreyPos.z - target.getZ()).normalize();
                   target.setDeltaMovement(target.getDeltaMovement().multiply(0.6F, 0.6F, 0.6F).add(minus.scale(0.35F)));
                   flag = true;
                   if(grabTime > getGrabDuration()){
                       grabTime = -10;
                   }
               }
            }
            caiman.setHeldMobId(flag ? target.getId() : -1);
            if(dist > bbWidth && !flag){
                caiman.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                caiman.getNavigation().moveTo(target, 1.2F);
            }
        }
    }

    private int getGrabDuration() {
        if(caiman.isTame() && caiman.tameAttackFlag){
            return 300;
        }
        return 2;
    }
}