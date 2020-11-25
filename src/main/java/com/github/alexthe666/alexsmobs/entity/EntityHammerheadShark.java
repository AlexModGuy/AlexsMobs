package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class EntityHammerheadShark extends WaterMobEntity {

    protected EntityHammerheadShark(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new AquaticMoveController(this, 1F);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new SemiAquaticPathNavigator(this, worldIn);
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

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FindWaterGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8F, 10));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new CirclePreyGoal(this, 1F));
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, GuardianEntity.class, 8.0F, 1.0D, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, SquidEntity.class, false));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractGroupFishEntity.class, 130, false, true, null));
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 40D).createMutableAttribute(Attributes.ARMOR, 10.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5F);
    }

    private static class CirclePreyGoal extends Goal {
        EntityHammerheadShark shark;
        float speed;

        public CirclePreyGoal(EntityHammerheadShark shark, float speed) {
            this.shark = shark;
            this.speed = speed;
        }

        @Override
        public boolean shouldExecute() {
            return this.shark.getAttackTarget() != null;
        }

    }
}
