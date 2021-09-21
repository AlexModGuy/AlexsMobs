package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class EntitySpectre extends Animal implements FlyingAnimal {

    private static final EntityDataAccessor<Integer> CARDINAL_ORDINAL = SynchedEntityData.defineId(EntitySpectre.class, EntityDataSerializers.INT);
    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    public Vec3 lurePos = null;

    protected EntitySpectre(EntityType type, Level world) {
        super(type, world);
        this.moveControl = new MoveHelperController(this);
    }


    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.spectreSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canSpectreSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random) {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        return true;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SPECTRE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SPECTRE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SPECTRE_HURT;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 1F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CARDINAL_ORDINAL, Integer.valueOf(Direction.NORTH.get3DDataValue()));
    }

    public int getCardinalInt() {
        return this.entityData.get(CARDINAL_ORDINAL).intValue();
    }

    public void setCardinalInt(int command) {
        this.entityData.set(CARDINAL_ORDINAL, Integer.valueOf(command));
    }

    public Direction getCardinalDirection() {
        return Direction.from3DDataValue(getCardinalInt());
    }

    public void setCardinalDirection(Direction dir) {
        setCardinalInt(dir.get3DDataValue());
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TemptHeartGoal(this, 1.0D, Ingredient.of(AMItemRegistry.SOUL_HEART), false));
        this.goalSelector.addGoal(2, new FlyGoal(this));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.isMagic() && source != DamageSource.OUT_OF_WORLD || super.isInvulnerableTo(source);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.xRot = 0.0F;
        this.randomizeDirection();
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);

    }

    public float getBrightness() {
        return 1.0F;
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void tick() {
        super.tick();
        Vec3 vector3d1 = this.getDeltaMovement();
        this.yRot = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
        this.yBodyRot = this.yRot;

        prevBirdPitch = this.birdPitch;
        noPhysics = true;
        float f2 = (float) -((float) this.getDeltaMovement().y * 0.5F * (double) (180F / (float) Math.PI));
        this.birdPitch = f2;
        if (this.getLeashHolder() != null && !(this.getLeashHolder() instanceof LeashFenceKnotEntity)) {
            Entity entity = this.getLeashHolder();
            float f = this.distanceTo(entity);
            if (f > 10) {
                double d0 = (this.getX() - entity.getX()) / (double) f;
                double d1 = (this.getY() - entity.getY()) / (double) f;
                double d2 = (this.getZ() - entity.getZ()) / (double) f;
                entity.setDeltaMovement(entity.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
            }
            entity.fallDistance = 0.0F;
            if (entity.getDeltaMovement().y < 0.0D) {
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0.7F, 1));
            }
            if (entity.isShiftKeyDown()) {
                this.dropLeash(true, true);
            }
        }
    }

    @Nullable
    @Override
    public AgableMob getBreedOffspring(ServerLevel serverWorld, AgableMob ageableEntity) {
        return null;
    }

    protected void tickLeash() {
        if (this.getLeashHolder() != null) {
            if (this.getLeashHolder().isPassenger() || this.getLeashHolder() instanceof LeashFenceKnotEntity) {
                super.tickLeash();
                return;
            }
            float f = this.distanceTo(this.getLeashHolder());
            if (f > 30) {
                double lvt_3_1_ = (this.getLeashHolder().getX() - this.getX()) / (double) f;
                double lvt_5_1_ = (this.getLeashHolder().getY() - this.getY()) / (double) f;
                double lvt_7_1_ = (this.getLeashHolder().getZ() - this.getZ()) / (double) f;
                this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(lvt_3_1_ * lvt_3_1_ * 0.4D, lvt_3_1_), Math.copySign(lvt_5_1_ * lvt_5_1_ * 0.4D, lvt_5_1_), Math.copySign(lvt_7_1_ * lvt_7_1_ * 0.4D, lvt_7_1_)));
            }
        }
        if (this.leashInfoTag != null) {
            this.restoreLeashFromSave();
        }

        if (this.getLeashHolder() != null) {
            if (!this.isAlive() || !this.getLeashHolder().isAlive()) {
                this.dropLeash(true, true);
            }

        }
    }

    private void randomizeDirection() {
        this.setCardinalInt(2 + random.nextInt(3));
    }

    static class MoveHelperController extends MoveControl {
        private final EntitySpectre parentEntity;

        public MoveHelperController(EntitySpectre sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d5 = vector3d.length();
                if (d5 < 0.3) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    double d0 = this.wantedX - this.parentEntity.getX();
                    double d1 = this.wantedY - this.parentEntity.getY();
                    double d2 = this.wantedZ - this.parentEntity.getZ();
                    double d3 = Mth.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d5)));
                    Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    parentEntity.yRot = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.yBodyRot = parentEntity.yRot;

                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.parentEntity.level.noCollision(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    private class FlyGoal extends Goal {
        private final EntitySpectre parentEntity;
        boolean island = false;
        float circlingTime = 0;
        float circleDistance = 14;
        float maxCirclingTime = 80;
        boolean clockwise = false;
        private BlockPos target = null;
        private int islandCheckTime = 20;

        public FlyGoal(EntitySpectre sunbird) {
            this.parentEntity = sunbird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (parentEntity.lurePos != null) {
                return false;
            }
            MoveControl movementcontroller = this.parentEntity.getMoveControl();
            clockwise = random.nextBoolean();
            circleDistance = 5 + random.nextInt(10);
            if (!movementcontroller.hasWanted() || target == null) {
                target = island ? getIslandPos(this.parentEntity.blockPosition()) : getBlockFromDirection();
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return parentEntity.lurePos == null;
        }

        public void stop() {
            island = false;
            islandCheckTime = 0;
            circleDistance = 5 + random.nextInt(10);
            circlingTime = 0;
            clockwise = random.nextBoolean();
            target = null;
        }

        public void tick() {
            if (islandCheckTime-- <= 0) {
                islandCheckTime = 20;
                if (circlingTime == 0) {
                    island = this.parentEntity.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, parentEntity.blockPosition()).getY() > 2;
                    if (island) {
                        parentEntity.randomizeDirection();
                    }
                }
            }
            if (island) {
                circlingTime++;
                if (circlingTime > 100) {
                    island = false;
                    islandCheckTime = 1200;
                }
            } else if (circlingTime > 0) {
                circlingTime--;
            }
            if (target == null) {
                target = island ? getIslandPos(this.parentEntity.blockPosition()) : getBlockFromDirection();
            }
            if (!island) {
                parentEntity.yRot = parentEntity.getCardinalDirection().toYRot();
            }
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 5.5F) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockFromDirection() {
            float radius = 15;
            BlockPos forwards = parentEntity.blockPosition().relative(parentEntity.getCardinalDirection(), (int) Math.ceil(radius));
            int height = 0;
            if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, forwards).getY() < 15) {
                height = 70 + random.nextInt(2);
            } else {
                height = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, forwards).getY() + 10 + random.nextInt(10);
            }
            return new BlockPos(forwards.getX(), height, forwards.getZ());
        }

        public BlockPos getIslandPos(BlockPos orbit) {
            float angle = (0.01745329251F * 3 * (clockwise ? -circlingTime : circlingTime));
            double extraX = circleDistance * Mth.sin((angle));
            double extraZ = circleDistance * Mth.cos(angle);
            int height = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, orbit).getY();
            if (height < 3) {
                island = false;
                return getBlockFromDirection();
            }
            BlockPos pos = new BlockPos(orbit.getX() + extraX, Math.min(height + 10, orbit.getY() + random.nextInt(3) - random.nextInt(1)), orbit.getZ() + extraZ);
            return pos;
        }

    }

    class TemptHeartGoal extends Goal {
        protected final EntitySpectre creature;
        private final TargetingConditions ENTITY_PREDICATE = (new TargetingConditions()).range(64D).allowInvulnerable().allowSameTeam().allowNonAttackable();
        private final double speed;
        private final Ingredient temptItem;
        protected Player closestPlayer;
        private int delayTemptCounter;

        public TemptHeartGoal(EntitySpectre p_i47822_1_, double p_i47822_2_, Ingredient p_i47822_4_, boolean p_i47822_5_) {
            this(p_i47822_1_, p_i47822_2_, p_i47822_5_, p_i47822_4_);
        }

        public TemptHeartGoal(EntitySpectre p_i47823_1_, double p_i47823_2_, boolean p_i47823_4_, Ingredient p_i47823_5_) {
            this.creature = p_i47823_1_;
            this.speed = p_i47823_2_;
            this.temptItem = p_i47823_5_;
        }

        public boolean canUse() {
            if (this.delayTemptCounter > 0) {
                --this.delayTemptCounter;
                return false;
            } else {
                this.closestPlayer = this.creature.level.getNearestPlayer(ENTITY_PREDICATE, this.creature);
                if (this.closestPlayer == null || this.creature.getLeashHolder() == closestPlayer) {
                    return false;
                } else {
                    return this.isTempting(this.closestPlayer.getMainHandItem()) || this.isTempting(this.closestPlayer.getOffhandItem());
                }
            }
        }

        protected boolean isTempting(ItemStack p_188508_1_) {
            return this.temptItem.test(p_188508_1_);
        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        public void start() {
            creature.lurePos = this.closestPlayer.position();
        }

        public void stop() {
            this.closestPlayer = null;
            this.delayTemptCounter = 100;
            creature.lurePos = null;
        }

        public void tick() {
            this.creature.getLookControl().setLookAt(this.closestPlayer, (float) (this.creature.getMaxHeadYRot() + 20), (float) this.creature.getMaxHeadXRot());
            if (this.creature.distanceToSqr(this.closestPlayer) < 6.25D) {
                this.creature.getNavigation().stop();
            } else {
                this.creature.getMoveControl().setWantedPosition(this.closestPlayer.getX(), closestPlayer.getY() + closestPlayer.getEyeHeight(), closestPlayer.getZ(), this.speed);
            }

        }
    }

}
