package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class EntityRoadrunner extends AnimalEntity {

    public float oFlapSpeed;
    public float oFlap;
    public float wingRotDelta = 1.0F;
    public float wingRotation;
    public float destPos;
    public float prevAttackProgress;
    public float attackProgress;
    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.createKey(EntityRoadrunner.class, DataSerializers.VARINT);

    protected EntityRoadrunner(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 50, 1.0D, 25, 7));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, EntityRattlesnake.class, 55, true, true, null));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, EntityRattlesnake.class)).setCallsForHelp());
    }


    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ATTACK_TICK, 0);

    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.dataManager.set(ATTACK_TICK, 5);
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.CACTUS || super.isInvulnerableTo(source);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.45F).createMutableAttribute(Attributes.FOLLOW_RANGE, 10D);
    }

    public void livingTick() {
        super.livingTick();
        this.oFlap = this.wingRotation;
        this.prevAttackProgress = attackProgress;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float) ((double) this.destPos + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float) ((double) this.wingRotDelta * 0.9D);
        Vector3d vector3d = this.getMotion();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setMotion(vector3d.mul(1.0D, 0.6D, 1.0D));
        }
        this.wingRotation += this.wingRotDelta * 2.0F;

        if(this.dataManager.get(ATTACK_TICK) > 0){
            if(this.dataManager.get(ATTACK_TICK) == 2 && this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < 1.3D){
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), 2);
            }
            this.dataManager.set(ATTACK_TICK, this.dataManager.get(ATTACK_TICK) - 1);
            if(attackProgress < 5F){
                attackProgress++;
            }
        }else{
            if(attackProgress > 0F){
                attackProgress--;
            }
        }
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.ROADRUNNER.create(p_241840_1_);
    }
}
