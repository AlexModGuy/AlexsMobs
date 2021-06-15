package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class EntityFrilledShark extends WaterMobEntity {

    protected EntityFrilledShark(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new AquaticMoveController(this, 1F);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new SemiAquaticPathNavigator(this, worldIn);
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    public void tick(){
        super.tick();
        this.setAir(100);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20D).createMutableAttribute(Attributes.ARMOR, 0.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5F);
    }

}
