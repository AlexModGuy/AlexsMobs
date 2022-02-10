package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BunfungusAIMelee extends Goal {

    private final EntityBunfungus chungus;
    private LivingEntity target;
    private boolean hasJumped = false;
    private int jumpCooldown = 0;


    public BunfungusAIMelee(EntityBunfungus chungus) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.chungus = chungus;
    }

    @Override
    public boolean canUse() {
        if(chungus.getTarget() != null && chungus.getTarget().isAlive()){
            hasJumped = false;
            return true;
        }
        return false;
    }

    public void tick(){
        if(jumpCooldown > 0){
            jumpCooldown--;
        }
        double dist = chungus.distanceTo(chungus.getTarget()) - chungus.getTarget().getBbWidth();
        if(dist < 2.0D){
            if(hasJumped){
                if(!chungus.isOnGround()){
                    chungus.getTarget().hurt(DamageSource.mobAttack(chungus), 10);
                }
                hasJumped = false;
            }else{
                if(chungus.getRandom().nextBoolean()){
                    chungus.setAnimation(EntityBunfungus.ANIMATION_SLAM);
                }else{
                    chungus.setAnimation(EntityBunfungus.ANIMATION_BELLY);
                }
            }
        }else if(dist < 5.0D || !chungus.hasLineOfSight(chungus.getTarget()) || jumpCooldown > 0 || chungus.isInWaterOrBubble()){
            chungus.getNavigation().moveTo(chungus.getTarget(), 1.0D);
        }else{
            chungus.getNavigation().stop();
            if(chungus.isOnGround()){
                Vec3 vector3d = this.chungus.getDeltaMovement();
                Vec3 vector3d1 = new Vec3(chungus.getTarget().getX() - this.chungus.getX(), 0.0D, chungus.getTarget().getZ() - this.chungus.getZ());
                if (vector3d1.lengthSqr() > 1.0E-7D) {
                    vector3d1 = vector3d1.normalize().scale(0.9D).add(vector3d.scale(0.8D));
                }
                this.chungus.setDeltaMovement(vector3d1.x, 0.6F, vector3d1.z);
                chungus.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                chungus.yBodyRot = chungus.getYRot();
                hasJumped = true;
                jumpCooldown = 10;
            }
        }
    }
}