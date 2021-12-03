package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class EntityHammerheadShark extends WaterAnimal {

    private static final Predicate<LivingEntity> INJURED_PREDICATE = (mob) -> {
        return mob.getHealth() <= mob.getMaxHealth() / 2D;
    };

    protected EntityHammerheadShark(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AquaticMoveController(this, 1F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.hammerheadSharkSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new SemiAquaticPathNavigator(this, worldIn);
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COD_HURT;
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new CirclePreyGoal(this, 1F));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.6F, 7));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, Guardian.class, 8.0F, 1.0D, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 50, false, true, INJURED_PREDICATE));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Squid.class, 50, false, true, null));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityMimicOctopus.class, 80, false, true, null));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractSchoolingFish.class, 70, false, true, null));
    }


    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.BLOCK;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30D).add(Attributes.ARMOR, 0.0D).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.5F);
    }

    public static <T extends Mob> boolean canHammerheadSharkSpawn(EntityType<EntityHammerheadShark> p_223364_0_, LevelAccessor p_223364_1_, MobSpawnType reason, BlockPos p_223364_3_, Random p_223364_4_) {
        if (p_223364_3_.getY() > 45 && p_223364_3_.getY() < p_223364_1_.getSeaLevel()) {
            Optional<ResourceKey<Biome>> optional = p_223364_1_.getBiomeName(p_223364_3_);
            return (!Objects.equals(optional, Optional.of(Biomes.OCEAN)) || !Objects.equals(optional, Optional.of(Biomes.DEEP_OCEAN))) && p_223364_1_.getFluidState(p_223364_3_).is(FluidTags.WATER);
        } else {
            return false;
        }
    }

    private static class CirclePreyGoal extends Goal {
        EntityHammerheadShark shark;
        float speed;
        float circlingTime = 0;
        float circleDistance = 5;
        float maxCirclingTime = 80;
        boolean clockwise = false;

        public CirclePreyGoal(EntityHammerheadShark shark, float speed) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.shark = shark;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            return this.shark.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.shark.getTarget() != null;
        }

        public void start(){
            circlingTime = 0;
            maxCirclingTime = 360 + this.shark.random.nextInt(80);
            circleDistance = 5 + this.shark.random.nextFloat() * 5;
            clockwise = this.shark.random.nextBoolean();
        }

        public void stop(){
            circlingTime = 0;
            maxCirclingTime = 360 + this.shark.random.nextInt(80);
            circleDistance = 5 + this.shark.random.nextFloat() * 5;
            clockwise = this.shark.random.nextBoolean();
        }

        public void tick(){
            LivingEntity prey = this.shark.getTarget();
            if(prey != null){
                double dist = this.shark.distanceTo(prey);
                if(circlingTime >= maxCirclingTime){
                    shark.lookAt(prey, 30.0F, 30.0F);
                    shark.getNavigation().moveTo(prey, 1.5D);
                    if(dist < 2D){
                        shark.doHurtTarget(prey);
                        if(shark.random.nextFloat() < 0.3F){
                            shark.spawnAtLocation(new ItemStack(AMItemRegistry.SHARK_TOOTH));
                        }
                        stop();
                    }
                }else{
                    if(dist <= 25){
                        circlingTime++;
                        BlockPos circlePos = getSharkCirclePos(prey);
                        if(circlePos != null){
                            shark.getNavigation().moveTo(circlePos.getX() + 0.5D, circlePos.getY() + 0.5D, circlePos.getZ() + 0.5D, 0.6D);
                        }
                    }else{
                        shark.lookAt(prey, 30.0F, 30.0F);
                        shark.getNavigation().moveTo(prey, 0.8D);
                    }
                }
            }
        }

        public BlockPos getSharkCirclePos(LivingEntity target) {
            float angle = (0.01745329251F * (clockwise ? -circlingTime : circlingTime));
            double extraX = circleDistance * Mth.sin((angle));
            double extraZ = circleDistance * Mth.cos(angle);
            BlockPos ground = new BlockPos(target.getX() + 0.5F + extraX, shark.getY(), target.getZ() + 0.5F + extraZ);
            if(shark.level.getFluidState(ground).is(FluidTags.WATER)){
                return ground;

            }
            return null;
    }
    }
}
