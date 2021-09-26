package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWaterLava;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.LaviathanAIRandomSwimming;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class EntityLaviathan extends Animal implements ISemiAquatic {

    private static final EntityDataAccessor<Boolean> OBSIDIAN = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> HEAD_HEIGHT = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEAD_YROT = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.FLOAT);
    public final EntityLaviathanPart headPart;
    public final EntityLaviathanPart neckPart1;
    public final EntityLaviathanPart neckPart2;
    public final EntityLaviathanPart neckPart3;
    public final EntityLaviathanPart neckPart4;
    public final EntityLaviathanPart neckPart5;
    public final EntityLaviathanPart[] theEntireNeck;
    public float prevHeadHeight = 0F;
    private float prevHeadYRot = 0F;
    public float swimProgress = 0F;
    public float prevSwimProgress = 0F;
    private boolean isLandNavigator;
    private int emergeIn = 500;
    private boolean surfaced = false;

    protected EntityLaviathan(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.headPart = new EntityLaviathanPart(this, 1.2F, 0.9F);
        this.neckPart1 = new EntityLaviathanPart(this, 0.9F, 0.9F);
        this.neckPart2 = new EntityLaviathanPart(this, 0.9F, 0.9F);
        this.neckPart3 = new EntityLaviathanPart(this, 0.9F, 0.9F);
        this.neckPart4 = new EntityLaviathanPart(this, 0.9F, 0.9F);
        this.neckPart5 = new EntityLaviathanPart(this, 0.9F, 0.9F);
        this.theEntireNeck = new EntityLaviathanPart[]{this.neckPart1, this.neckPart2, this.neckPart3, this.neckPart4, this.neckPart5, this.headPart};
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 60D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.ARMOR, 10D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.lookControl = new LookControl(this);
            this.moveControl = new MoveControl(this);
            this.navigation = createNavigation(level);
            this.isLandNavigator = true;
        } else {
            this.lookControl = new SmoothSwimmingLookControl(this, 15);
            this.moveControl = new MoveController(this);
            this.navigation = new BoneSerpentPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    protected PathNavigation createNavigation(Level p_21480_) {
        return new GroundPathNavigatorWide(this, p_21480_);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreathAirGoal(this));
        this.goalSelector.addGoal(0, new AnimalAIFindWaterLava(this, 1.0D));
        this.goalSelector.addGoal(1, new LaviathanAIRandomSwimming(this, 1.0D, 18));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    protected float getBlockSpeedFactor() {
        return shouldSwim() ? 1.0F : super.getBlockSpeedFactor();
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        if (worldIn.getBlockState(pos).getFluidState().is(FluidTags.WATER) || worldIn.getBlockState(pos).getFluidState().is(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    public int getMaxFallDistance() {
        return 256;
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OBSIDIAN, false);
        this.entityData.define(HEAD_HEIGHT, 0F);
        this.entityData.define(HEAD_YROT, 0F);
    }

    public void travel(Vec3 travelVector) {
        boolean liquid = this.isInLava() || this.isInWater();
        if (this.isEffectiveAi() && liquid) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    public int getMaxHeadXRot() {
        return 50;
    }

    public int getMaxHeadYRot() {
        return 50;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public MobType getMobType() {
        return MobType.WATER;
    }

    public void tick() {
        super.tick();
        maxUpStep = 1.1F;
        prevSwimProgress = swimProgress;
        prevHeadHeight = this.getHeadHeight();
        prevHeadYRot = this.getHeadYRotPlecio();
        this.yBodyRot = this.getYRot();
        if (shouldSwim() && swimProgress < 5F) {
            swimProgress++;
        }
        if (!shouldSwim() && swimProgress > 0F) {
            swimProgress--;
        }
        if(!level.isClientSide){
            float f = -Mth.wrapDegrees(this.getXRot());
            this.setHeadHeight(Mth.clamp(f, -90, 90) / 30F);
        }
        surfaced = false;

        if (surfaced && this.getMaxFluidHeight() < 0.8F) {
        }
        if (!this.isNoAi()) {
            Vec3[] avector3d = new Vec3[this.theEntireNeck.length];
            for (int j = 0; j < this.theEntireNeck.length; ++j) {
                this.theEntireNeck[j].collideWithNearbyEntities();
                avector3d[j] = new Vec3(this.theEntireNeck[j].getX(), this.theEntireNeck[j].getY(), this.theEntireNeck[j].getZ());
            }
            float yaw = this.getYRot() * ((float) Math.PI / 180F);
            float neckBase = 0.8F;
            float neckContraction = 2.0F * Math.abs(getHeadHeight() / 3) + 0.5F * Math.abs(getHeadYaw(0) / 50F);

            for (int l = 0; l < this.theEntireNeck.length; ++l) {
                float f = l / ((float) this.theEntireNeck.length);
                float f1 = -(2.2F + l - f * neckContraction);
                float f2 = Mth.sin(yaw + (float) Math.toRadians(f * getHeadYaw(0))) * (1 - Math.abs((this.getXRot()) / 90F));
                float f3 = Mth.cos(yaw + (float) Math.toRadians(f * getHeadYaw(0))) * (1 - Math.abs((this.getXRot()) / 90F));
                this.setPartPosition(this.theEntireNeck[l], f2 * f1, neckBase + Math.sin(f * Math.PI * 0.5F) * (getHeadHeight() * 1.1F), -f3 * f1);
                this.theEntireNeck[l].xo = avector3d[l].x;
                this.theEntireNeck[l].yo = avector3d[l].y;
                this.theEntireNeck[l].zo = avector3d[l].z;
                this.theEntireNeck[l].xOld = avector3d[l].x;
                this.theEntireNeck[l].yOld = avector3d[l].y;
                this.theEntireNeck[l].zOld = avector3d[l].z;
            }
        }
        if (shouldSwim() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!shouldSwim() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (emergeIn > 0) {
            emergeIn--;
        }
        if (emergeIn <= 0) {
            if (this.getMaxFluidHeight() >= this.getBbHeight() * 0.25F) {
                float dist = (float) Math.min(0, this.getMaxFluidHeight() - this.getBbHeight() * 0.25F * 0.2F);
                this.setDeltaMovement(this.getDeltaMovement().add(0, dist, 0));
            }else{
                emergeIn--;
                if(emergeIn <= -50){
                    emergeIn = 500;
                }
            }
        }
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public boolean shouldSwim() {
        return isInWater() || isInLava();
    }

    private float getXForPart(float yaw, float degree) {
        return Mth.sin((float) (yaw + Math.toRadians(degree))) * (1 - Math.abs((this.getXRot()) / 90F));
    }

    private float getZForPart(float yaw, float degree) {
        return -Mth.cos((float) (yaw + Math.toRadians(degree))) * (1 - Math.abs((this.getXRot()) / 90F));
    }

    public float getHeadHeight() {
        return Mth.clamp(this.entityData.get(HEAD_HEIGHT), -3, 3);
    }

    public void setHeadHeight(float height) {
        this.entityData.set(HEAD_HEIGHT, Mth.clamp(height, -3, 3));
    }

    public float getHeadYRotPlecio() {
        return Mth.wrapDegrees(this.entityData.get(HEAD_YROT));
    }

    public void setHeadYRotPlecio(float rot) {
        this.entityData.set(HEAD_YROT, rot);
    }

    public boolean isObsidian() {
        return this.entityData.get(OBSIDIAN);
    }

    public void setObsidian(boolean obsidian) {
        this.entityData.set(OBSIDIAN, obsidian);
    }

    public float getHeadYaw(float interp) {
        float f;
        if (interp == 0.0F) {
            f = yHeadRot - this.yBodyRot;
        } else {
            float yBodyRot1 = this.yBodyRotO + (this.yBodyRot - this.yBodyRotO) * interp;
            float yHeadRot1 = this.yHeadRotO + (yHeadRot - this.yHeadRotO) * interp;
            f = yHeadRot1 - yBodyRot1;
        }
        return Mth.clamp(f, -50, 50);
    }

    private void setPartPosition(EntityLaviathanPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX * part.scale, this.getY() + offsetY * part.scale, this.getZ() + offsetZ * part.scale);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.theEntireNeck;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    public boolean attackEntityPartFrom(EntityLaviathanPart part, DamageSource source, float amount) {
        return this.hurt(source, amount);
    }

    @Override
    public boolean shouldEnterWater() {
        return !this.isVehicle();
    }

    @Override
    public boolean shouldLeaveWater() {
        return false;
    }

    @Override
    public boolean shouldStopMoving() {
        return false;
    }

    @Override
    public int getWaterSearchRange() {
        return 15;
    }

    private double getMaxFluidHeight() {
        return Math.max(this.getFluidHeight(FluidTags.LAVA), this.getFluidHeight(FluidTags.WATER));
    }

    static class MoveController extends MoveControl {
        private final EntityLaviathan laviathan;

        public MoveController(EntityLaviathan dolphinIn) {
            super(dolphinIn);
            this.laviathan = dolphinIn;
        }

        public void tick() {
            float speed = (float) (this.speedModifier * 3 * laviathan.getAttributeValue(Attributes.MOVEMENT_SPEED));
            if (this.laviathan.emergeIn <= 0) {
                speed *= 0.8F;
            } else if (this.laviathan.shouldSwim()) {
                this.laviathan.setDeltaMovement(this.laviathan.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
            if (this.operation == Operation.MOVE_TO && !laviathan.getNavigation().isDone()) {
                double lvt_1_1_ = this.wantedX - laviathan.getX();
                double lvt_3_1_ = this.wantedY - laviathan.getY();
                double lvt_5_1_ = this.wantedZ - laviathan.getZ();
                double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
                if (lvt_7_1_ < 2.500000277905201E-7D) {
                    this.mob.setZza(0.0F);
                } else {
                    float lvt_9_1_ = (float) (Mth.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
                    float f = this.rotlerp(laviathan.getYRot(), lvt_9_1_, 5);
                    laviathan.setYRot(f);
                    laviathan.yHeadRot = this.rotlerp(laviathan.yHeadRot, lvt_9_1_, 50);
                    laviathan.yBodyRot = f;
                    if (laviathan.shouldSwim()) {
                        if (lvt_3_1_ > 0 && laviathan.horizontalCollision) {
                            laviathan.setDeltaMovement(laviathan.getDeltaMovement().add(0.0D, 0.08F, 0.0D));
                        } else {
                            laviathan.setDeltaMovement(laviathan.getDeltaMovement().add(0.0D, (double) laviathan.getSpeed() * lvt_3_1_ * 0.6D, 0.0D));
                        }
                        laviathan.setSpeed(speed * 0.02F);
                        float lvt_11_1_ = -((float) (Mth.atan2(lvt_3_1_, Mth.sqrt((float) (lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_))) * 57.2957763671875D));
                        lvt_11_1_ = Mth.clamp(Mth.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                        laviathan.setXRot(this.rotlerp(laviathan.getXRot(), lvt_11_1_, 5.0F));
                        float lvt_12_1_ = Mth.cos(laviathan.getXRot() * 0.017453292F);
                        float lvt_13_1_ = Mth.sin(laviathan.getXRot() * 0.017453292F);
                        laviathan.zza = lvt_12_1_ * speed;
                        laviathan.yya = -lvt_13_1_ * speed;
                    } else {
                        laviathan.setSpeed(speed * 0.1F);
                    }

                }
            }
        }
    }
}
