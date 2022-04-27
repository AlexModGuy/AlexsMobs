package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SwimmerJumpPathNavigator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityFlyingFish extends WaterAnimal implements FlyingAnimal {

    private static final EntityDataAccessor<Boolean> GLIDING = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityFlyingFish.class, EntityDataSerializers.INT);
    public float prevOnLandProgress;
    public float onLandProgress;
    public float prevFlyProgress;
    public float flyProgress;
    private int glideIn = 0;

    protected EntityFlyingFish(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new AquaticMoveController(this, 1.0F, 15F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(3, new GlideGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(4, new AnimalAIRandomSwimming(this, 1F, 12, 5));
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new SwimmerJumpPathNavigator(this, worldIn);
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GLIDING, false);
        this.entityData.define(VARIANT, 0);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public void tick(){
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        this.prevFlyProgress = flyProgress;
        boolean onLand = !this.isInWaterOrBubble() && this.isOnGround();
        if (onLand && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (onLand && onLandProgress > 0F) {
            onLandProgress--;
        }
        if (isGliding() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isGliding() && flyProgress > 0F) {
            flyProgress--;
        }
        if(isGliding() && !this.isInWaterOrBubble() && this.getDeltaMovement().y < 0.0){
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0F, 0.5F, 1.0F));
        }
        if(glideIn > 0){
            glideIn--;
        }
    }

    protected void handleAirSupply(int i) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(i - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAirSupply(1000);
        }
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f = 0.6F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, f, 0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }


    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public boolean isGliding() {
        return this.entityData.get(GLIDING);
    }

    public void setGliding(boolean flying) {
        this.entityData.set(GLIDING, flying);
    }

    private boolean canSeeBlock(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level.clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    private class GlideGoal extends Goal {
        private EntityFlyingFish fish;
        private Level level;
        private BlockPos surface;
        private BlockPos glide;

        public GlideGoal(EntityFlyingFish fish) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.fish = fish;
            this.level = fish.level;
        }

        @Override
        public boolean canUse() {
            if(!fish.isInWaterOrBubble()){
                return false;
            }else if(fish.glideIn == 0 || fish.getRandom().nextInt(50) == 0){
                BlockPos found = findSurfacePos();
                if(found != null){
                    BlockPos glideTo = findGlideToPos(fish.blockPosition(), found);
                    if(glideTo != null){
                        surface = found;
                        glide = glideTo;
                        fish.glideIn = 0;
                        return true;
                    }
                }
            }
            return false;
        }

        private BlockPos findSurfacePos(){
            BlockPos fishPos = fish.blockPosition();
            for(int i = 0; i < 15; i++){
                BlockPos offset = fishPos.offset(fish.random.nextInt(16) - 8, 0, fish.random.nextInt(16) - 8);
                while(level.isWaterAt(offset) && offset.getY() < level.getMaxBuildHeight()){
                    offset = offset.above();
                }
                if(!level.isWaterAt(offset) && level.isWaterAt(offset.below()) && fish.canSeeBlock(offset)){
                    return offset;
                }
            }
            return null;
        }

        private BlockPos findGlideToPos(BlockPos fishPos, BlockPos surface) {
            Vec3 sub = Vec3.atLowerCornerOf(surface.subtract(fishPos)).normalize();
            double scale = random.nextDouble() * 8 + 1;

            while(scale > 1){
                Vec3 scaled = sub.scale(scale);
                BlockPos at = surface.offset(scaled.x, 0, scaled.z);
                if(!level.isWaterAt(at) && level.isWaterAt(at.below()) && fish.canSeeBlock(at)){
                    return at;
                }
                scale -= 1;
            }
            return null;
        }

        @Override
        public boolean canContinueToUse() {
            return surface != null && glide != null && (!fish.isOnGround() || fish.isInWaterOrBubble());
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
            surface = null;
            glide = null;
            fish.glideIn = 100;
            fish.setGliding(false);
        }

        @Override
        public void tick() {
            if(fish.isInWaterOrBubble() && fish.distanceToSqr(Vec3.atCenterOf(surface)) > 3F){
                fish.getNavigation().moveTo(surface.getX() + 0.5F, surface.getY() + 1F, surface.getZ() + 0.5F, 1.2F);
                if(fish.isGliding()){
                    stop();
                }
            }else{
                if(!fish.isGliding() || fish.distanceToSqr(Vec3.atCenterOf(glide)) > 3F){
                    fish.setDeltaMovement(fish.getDeltaMovement().add(0, 0.2F, 0));
                }
                fish.setGliding(true);
                fish.getNavigation().moveTo(glide.getX() + 0.5F, glide.getY() + 0.5F, glide.getZ() + 0.5F, 1.2F);
            }
        }
    }
}
