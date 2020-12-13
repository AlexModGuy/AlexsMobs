package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.MathHelper;

public class AquaticMoveController extends MovementController {
    private final CreatureEntity entity;
    private float speedMulti;
    public AquaticMoveController(CreatureEntity entity, float speedMulti) {
        super(entity);
        this.entity = entity;
        this.speedMulti = speedMulti;
    }

    public void tick() {
        if (this.entity.isInWater() || entity instanceof EntityWarpedToad && entity.isInLava()) {
            this.entity.setMotion(this.entity.getMotion().add(0.0D, 0.005D, 0.0D));
        }
        if(entity instanceof ISemiAquatic && ((ISemiAquatic) entity).shouldStopMoving()){
            this.entity.setAIMoveSpeed(0.0F);
            return;
        }
        if (this.action == Action.MOVE_TO && !this.entity.getNavigator().noPath()) {
            double d0 = this.posX - this.entity.getPosX();
            double d1 = this.posY - this.entity.getPosY();
            double d2 = this.posZ - this.entity.getPosZ();
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 /= d3;
            float f = (float)(MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F;
            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 3.0F);
            this.entity.renderYawOffset = this.entity.rotationYaw;
            float f1 = (float)(this.speed * this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedMulti);
            this.entity.setAIMoveSpeed(f1 * 0.4F);
            this.entity.setMotion(this.entity.getMotion().add(0.0D, (double)this.entity.getAIMoveSpeed() * d1 * 0.6D, 0.0D));
        } else {
            this.entity.setAIMoveSpeed(0.0F);
        }
    }
}
