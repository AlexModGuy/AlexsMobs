package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.apache.commons.compress.archivers.sevenz.CLI;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

public class EntitySkreecher extends Monster {

    public static final float MAX_DIST_TO_CEILING = 4f;
    private static final EntityDataAccessor<Boolean> CLINGING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DIST_TO_CEILING = SynchedEntityData.defineId(EntitySkreecher.class, EntityDataSerializers.FLOAT);
    public float prevClingProgress;
    public float clingProgress;
    public float prevClapProgress;
    public float clapProgress;
    public float prevDistanceToCeiling;
    private boolean isUpsideDownNavigator;
    protected EntitySkreecher(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        switchNavigator(true);
    }


    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AIUpsideDownWander());
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 30F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true) {
            protected AABB getTargetSearchArea(double targetDistance) {
                AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
                return new AABB(bb.minX, -64, bb.minZ, bb.maxX, 320, bb.maxZ);
            }
        });
    }


    private void switchNavigator(boolean rightsideUp) {
        if (rightsideUp) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isUpsideDownNavigator = false;
        } else {
            this.moveControl = new FlightMoveController(this, 1.1F, false);
            this.navigation = createScreecherNavigation(level);
            this.isUpsideDownNavigator = true;
        }
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 2D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.FOLLOW_RANGE, 64F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DIST_TO_CEILING, 0F);
        this.entityData.define(CLINGING, false);
    }

    public void tick() {
        super.tick();
        prevClapProgress = clapProgress;
        prevClingProgress = clingProgress;
        prevDistanceToCeiling = this.getDistanceToCeiling();
        if (this.isClinging() && clingProgress < 5F) {
            clingProgress++;
        }
        if (!this.isClinging() && clingProgress > 0F && this.getDistanceToCeiling() == 0) {
            clingProgress--;
        }
        if (!level.isClientSide) {
            float technicalDistToCeiling = calculateDistanceToCeiling();
            if(this.isClinging()){
                this.setNoGravity(true);
                if (technicalDistToCeiling > MAX_DIST_TO_CEILING) {
                    this.setClinging(false);
                }
                float goal = Math.min(technicalDistToCeiling, MAX_DIST_TO_CEILING);
                if(this.getDistanceToCeiling() < goal){
                    this.setDistanceToCeiling(Math.min(goal, prevDistanceToCeiling + 0.15F));
                }
                if(this.getDistanceToCeiling() > goal){
                    this.setDistanceToCeiling(Math.max(goal, prevDistanceToCeiling - 0.15F));
                }
            }else{
                this.setNoGravity(false);
                if (technicalDistToCeiling < MAX_DIST_TO_CEILING) {
                    this.setClinging(true);
                }
                this.setDistanceToCeiling(Math.max(0, prevDistanceToCeiling - 0.5F));
            }
        }
        if (this.isClinging() && !this.isUpsideDownNavigator) {
            switchNavigator(false);
        }
        if (!this.isClinging() && this.isUpsideDownNavigator) {
            switchNavigator(true);
        }
    }

    public boolean isClinging() {
        return this.entityData.get(CLINGING).booleanValue();
    }

    public void setClinging(boolean upsideDown) {
        this.entityData.set(CLINGING, Boolean.valueOf(upsideDown));
    }


    protected BlockPos getPositionAbove(float height) {
        return new BlockPos(this.position().x, this.getBoundingBox().maxY + height + 0.5000001D, this.position().z);
    }

    protected PathNavigation createScreecherNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos pos) {
                int airAbove = 0;
                while(level.getBlockState(pos).isAir() && airAbove < MAX_DIST_TO_CEILING + 2){
                    pos = pos.above();
                    airAbove++;
                }
                return airAbove <= MAX_DIST_TO_CEILING;
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    private float calculateDistanceToCeiling(){
        float checkAt = 0;
        while (checkAt <= 5F) {
            if (!isOpaqueBlockAt(this.getX(), this.getBoundingBox().maxY + checkAt, this.getZ())) {
                checkAt += 0.1F;
            } else {
                break;
            }
        }
        return checkAt;
    }

    private boolean isOpaqueBlockAt(double x, double y, double z) {
        if (this.noPhysics) {
            return false;
        } else {
            final double d = 0.3F;
            final Vec3 vec3 = new Vec3(x, y, z);
            final AABB axisAlignedBB = AABB.ofSize(vec3, d, 1.0E-6D, d);
            return this.level.getBlockStates(axisAlignedBB).filter(Predicate.not(BlockBehaviour.BlockStateBase::isAir)).anyMatch((p_185969_) -> {
                BlockPos blockpos = new BlockPos(vec3);
                return p_185969_.isSuffocating(this.level, blockpos) && Shapes.joinIsNotEmpty(p_185969_.getCollisionShape(this.level, blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(axisAlignedBB), BooleanOp.AND);
            });
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getDistanceToCeiling() {
        return this.entityData.get(DIST_TO_CEILING);
    }

    public void setDistanceToCeiling(float dist) {
        this.entityData.set(DIST_TO_CEILING, dist);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isClinging()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }

    }

    class AIUpsideDownWander extends RandomStrollGoal {

        public AIUpsideDownWander() {
            super(EntitySkreecher.this, 1D, 25);
        }

        @Nullable
        protected Vec3 getPosition() {
            if (EntitySkreecher.this.isClinging()) {
                for (int i = 0; i < 15; i++) {
                    Random rand = new Random();
                    BlockPos randPos = EntitySkreecher.this.blockPosition().offset(rand.nextInt(16) - 8, -MAX_DIST_TO_CEILING, rand.nextInt(16) - 8);
                    BlockPos lowestPos = EntityDropBear.getLowestPos(level, randPos).below(rand.nextInt((int)MAX_DIST_TO_CEILING));
                    return Vec3.atCenterOf(lowestPos);

                }
                return null;
            } else {
                return super.getPosition();
            }
        }

        public boolean canUse() {
            return super.canUse();
        }

        public boolean canContinueToUse() {
            if (EntitySkreecher.this.isClinging()) {
                double d0 = EntitySkreecher.this.getX() - wantedX;
                double d2 = EntitySkreecher.this.getZ() - wantedZ;
                double d4 = d0 * d0 + d2 * d2;
                return d4 > 4;
            } else {
                return super.canContinueToUse();
            }
        }

        public void stop() {
            super.stop();
            this.wantedX = 0;
            this.wantedY = 0;
            this.wantedZ = 0;
        }

        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }
    }

}
