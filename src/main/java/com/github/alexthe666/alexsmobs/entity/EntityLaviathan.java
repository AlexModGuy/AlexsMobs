package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityLaviathan extends Animal implements ISemiAquatic, IHerdPanic {

    private static final EntityDataAccessor<Boolean> OBSIDIAN = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> HEAD_HEIGHT = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEAD_YROT = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CHILL_TIME = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_BODY_GEAR = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_HEAD_GEAR = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<EntityCrimsonMosquito> HEALTHY_MOSQUITOES = (mob) -> {
        return mob.isAlive() && mob.getHealth() > 0 && !mob.isSick();
    };
    public static final ResourceLocation OBSIDIAN_LOOT = new ResourceLocation("alexsmobs", "entities/laviathan_obsidian");
    public final EntityLaviathanPart headPart;
    public final EntityLaviathanPart neckPart1;
    public final EntityLaviathanPart neckPart2;
    public final EntityLaviathanPart neckPart3;
    public final EntityLaviathanPart neckPart4;
    public final EntityLaviathanPart neckPart5;
    public final EntityLaviathanPart seat1;
    public final EntityLaviathanPart seat2;
    public final EntityLaviathanPart seat3;
    public final EntityLaviathanPart seat4;
    public final EntityLaviathanPart[] theEntireNeck;
    public final EntityLaviathanPart[] allParts;
    public final EntityLaviathanPart[] seatParts;
    private final UUID[] riderPositionMap = new UUID[4];
    public float prevHeadHeight = 0F;
    public float swimProgress = 0F;
    public float prevSwimProgress = 0F;
    public float biteProgress;
    public float prevBiteProgress;
    public int revengeCooldown = 0;
    private boolean isLandNavigator;
    private int conversionTime = 0;
    private int dismountCooldown = 0;
    private int headPeakCooldown = 0;
    private boolean hasObsidianArmor;

    protected EntityLaviathan(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        maxUpStep = 1.3F;
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
        this.seat1 = new EntityLaviathanPart(this, 0.9F, 0.4F);
        this.seat2 = new EntityLaviathanPart(this, 0.9F, 0.4F);
        this.seat3 = new EntityLaviathanPart(this, 0.9F, 0.4F);
        this.seat4 = new EntityLaviathanPart(this, 0.9F, 0.4F);
        this.theEntireNeck = new EntityLaviathanPart[]{this.neckPart1, this.neckPart2, this.neckPart3, this.neckPart4, this.neckPart5, this.headPart};
        this.allParts = new EntityLaviathanPart[]{this.neckPart1, this.neckPart2, this.neckPart3, this.neckPart4, this.neckPart5, this.headPart, this.seat1, this.seat2, this.seat3, this.seat4};
        this.seatParts = new EntityLaviathanPart[]{this.seat1, this.seat2, this.seat3, this.seat4};
        switchNavigator(true);
    }


    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return this.isObsidian() ? OBSIDIAN_LOOT : super.getDefaultLootTable();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 60D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.ARMOR, 10D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    protected boolean canAddPassenger(Entity p_38390_) {
        return this.getPassengers().size() < 4 && !this.isEyeInFluid(FluidTags.LAVA) && !this.isEyeInFluid(FluidTags.WATER);
    }

    public void push(Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement()));
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMItemRegistry.MOSQUITO_LARVA);
    }

    public boolean isPushable() {
        return false;
    }

    public float getPickRadius() {
        return 0.0F;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (item == Items.MAGMA_CREAM && this.getHealth() < this.getMaxHealth()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.heal(10);
            return InteractionResult.SUCCESS;
        }
        if (item == AMItemRegistry.STRADDLE_HELMET && !this.hasHeadGear() && !this.isBaby()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setHeadGear(true);
            return InteractionResult.SUCCESS;
        }
        if (item == AMItemRegistry.STRADDLE_SADDLE && !this.hasBodyGear() && !this.isBaby()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setBodyGear(true);
            return InteractionResult.SUCCESS;
        }
        InteractionResult type = super.mobInteract(player, hand);
        if (type != InteractionResult.SUCCESS && !isFood(itemstack) && this.hasBodyGear()) {
            if (!this.isBaby()) {
                if (!player.isShiftKeyDown()) {
                    if (!level.isClientSide) {
                        player.startRiding(this);
                    }
                } else {
                    this.ejectPassengers();
                }
                return InteractionResult.SUCCESS;
            }

        }
        return type;
    }

    public int getClosestOpenSeat(Vec3 entityPos) {
        int closest = -1;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < seatParts.length; i++) {
            double dist = entityPos.distanceTo(seatParts[i].position());
            if (closest == -1 || closestDistance > dist) {
                if (riderPositionMap[i] == null) {
                    closest = i;
                    closestDistance = dist;
                }
            }
        }
        return closest;
    }

    @Nullable
    public Entity getControllingPassenger() {
        int playerPosition = -1;
        Player player = null;
        if (this.hasHeadGear() && this.hasBodyGear()) {
            for (Entity passenger : this.getPassengers()) {
                if (passenger instanceof Player) {
                    Player player2 = (Player) passenger;
                    int player2Position = getRiderPosition(passenger);
                    if (player == null || playerPosition > player2Position) {
                        player = player2;
                        playerPosition = player2Position;
                    }
                }
            }
        }
        return player;
    }

    public int getSeatRaytrace(Entity player) {
        HitResult result = player.pick(player.distanceTo(this), 0.0F, false);
        if (result != null) {
            Vec3 vec = result.getLocation();
            return getClosestOpenSeat(vec);
        }
        return -1;
    }

    @Override
    public void removePassenger(Entity entity) {
        super.removePassenger(entity);
        dismountCooldown = 40 + random.nextInt(40);
        if (entity != null && entity.getUUID() != null) {
            for (int i = 0; i < riderPositionMap.length; i++) {
                if (riderPositionMap[i] != null && riderPositionMap[i].equals(entity.getUUID())) {
                    riderPositionMap[i] = null;
                }
            }
        }
    }

    public int getRiderPosition(Entity passenger) {
        int posit = -1;
        for (int i = 0; i < this.riderPositionMap.length; i++) {
            if (this.riderPositionMap[i] != null && passenger != null && passenger.getUUID().equals(riderPositionMap[i])) {
                posit = i;
            }
        }
        return posit;
    }

    protected void addPassenger(Entity entity) {
        int rayTrace = getSeatRaytrace(entity);
        if (rayTrace >= 0 && rayTrace < 4) {
            if (riderPositionMap[rayTrace] != null) {
                if (!this.level.isClientSide && level instanceof ServerLevel) {
                    Entity kickOff = ((ServerLevel) this.level).getEntity(riderPositionMap[rayTrace]);
                    riderPositionMap[rayTrace] = null;
                    if (kickOff != null) {
                        kickOff.stopRiding();
                    }
                }
            }
            riderPositionMap[rayTrace] = entity.getUUID();
            super.addPassenger(entity);
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Obsidian", this.isObsidian());
        compound.putBoolean("HeadGear", this.hasHeadGear());
        compound.putBoolean("BodyGear", this.hasBodyGear());
        compound.putInt("ChillTime", this.getChillTime());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setObsidian(compound.getBoolean("Obsidian"));
        this.setHeadGear(compound.getBoolean("HeadGear"));
        this.setBodyGear(compound.getBoolean("BodyGear"));
        this.setChillTime(compound.getInt("ChillTime"));
    }

    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            int posit = getRiderPosition(passenger);
            if (posit < 0 || posit > 3) {
                passenger.stopRiding();
            } else {
                EntityLaviathanPart seat = seatParts[posit];
                passenger.setPos(seat.getX(), this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), seat.getZ());
            }
        }
    }

    public double getPassengersRidingOffset() {
        float f = this.animationPosition;
        float f1 = this.animationSpeed;
        float f2 = 0;
        return (double) this.getBbHeight() - 0.4F;
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.hasBodyGear()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(AMItemRegistry.STRADDLE_SADDLE);
            }
        }
        if (this.hasHeadGear()) {
            if (!this.level.isClientSide) {
                this.spawnAtLocation(AMItemRegistry.STRADDLE_HELMET);
            }
        }
        this.setBodyGear(false);
        this.setHeadGear(false);
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
        this.goalSelector.addGoal(0, new AnimalAIHerdPanic(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !EntityLaviathan.this.hasHeadGear();
            }
        });
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new TameableAIRide(this, 1D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.MAGMA_CREAM, AMItemRegistry.MOSQUITO_LARVA), false));
        this.goalSelector.addGoal(4, new AnimalAIFindWaterLava(this, 1.0D));
        this.goalSelector.addGoal(5, new LaviathanAIRandomSwimming(this, 1.0D, 22) {
            @Override
            public boolean canUse() {
                return super.canUse() && !EntityLaviathan.this.hasHeadGear();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return true;
    }

    protected float getBlockSpeedFactor() {
        return shouldSwim() || this.onSoulSpeedBlock() ? 1.0F : super.getBlockSpeedFactor();
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        if (worldIn.getBlockState(pos).getFluidState().is(FluidTags.WATER) || worldIn.getBlockState(pos).getFluidState().is(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? -1.0F : 0.0F;
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

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev && source.getEntity() != null) {
            double range = 15;
            int fleeTime = 100 + getRandom().nextInt(150);
            this.revengeCooldown = fleeTime;
            this.setChillTime(0);
        }
        return prev;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OBSIDIAN, false);
        this.entityData.define(HAS_BODY_GEAR, false);
        this.entityData.define(HAS_HEAD_GEAR, false);
        this.entityData.define(HEAD_HEIGHT, 0F);
        this.entityData.define(HEAD_YROT, 0F);
        this.entityData.define(CHILL_TIME, 0);
        this.entityData.define(ATTACK_TICK, 0);
    }

    public void travel(Vec3 travelVector) {
        boolean liquid = this.shouldSwim();
        if (this.isEffectiveAi() && liquid) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if(!this.isChilling()){
             //   this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    public void setDeltaMovement(Vec3 p_20257_) {
        super.setDeltaMovement(p_20257_);
    }

    public int getMaxHeadXRot() {
        return 50;
    }

    public int getMaxHeadYRot() {
        return 50;
    }

    public int getHeadRotSpeed() {
        return 4;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new LaviathanBodyRotationControl(this);
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

        prevSwimProgress = swimProgress;
        prevBiteProgress = biteProgress;
        prevHeadHeight = this.getHeadHeight();
        this.yBodyRot = this.getYRot();
        if (shouldSwim() && swimProgress < 5F) {
            swimProgress++;
        }
        if (!shouldSwim() && swimProgress > 0F) {
            swimProgress--;
        }
        if (this.isObsidian() && !hasObsidianArmor) {
            hasObsidianArmor = true;
            this.getAttribute(Attributes.ARMOR).setBaseValue(30F);
        }
        if (!this.isObsidian() && hasObsidianArmor) {
            hasObsidianArmor = false;
            this.getAttribute(Attributes.ARMOR).setBaseValue(10F);
        }

        if (!level.isClientSide) {
            if (!this.isObsidian() && this.isInWaterOrBubble()) {
                if (conversionTime < 300) {
                    conversionTime++;
                } else {
                    this.setObsidian(true);
                }

            }
            if (shouldSwim()) {
                fallDistance = 0.0F;
            }
        }
        float neckBase = 0.8F;
        if (!this.isNoAi()) {
            Vec3[] avector3d = new Vec3[this.allParts.length];
            for (int j = 0; j < this.allParts.length; ++j) {
                this.allParts[j].collideWithNearbyEntities();
                avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
            }
            float yaw = this.getYRot() * ((float) Math.PI / 180F);
            float neckContraction = 2.0F * Math.abs(getHeadHeight() / 3) + 0.5F * Math.abs(getHeadYaw(0) / 50F);

            for (int l = 0; l < this.theEntireNeck.length; ++l) {
                float f = l / ((float) this.theEntireNeck.length);
                float f1 = -(2.2F + l - f * neckContraction);
                float f2 = Mth.sin(yaw + (float) Math.toRadians(f * getHeadYaw(0))) * (1 - Math.abs((this.getXRot()) / 90F));
                float f3 = Mth.cos(yaw + (float) Math.toRadians(f * getHeadYaw(0))) * (1 - Math.abs((this.getXRot()) / 90F));
                this.setPartPosition(this.theEntireNeck[l], f2 * f1, neckBase + Math.sin(f * Math.PI * 0.5F) * (getHeadHeight() * 1.1F), -f3 * f1);
            }
            this.setPartPosition(this.seat1, getXForPart(yaw, 145) * 0.75F, 2F, getZForPart(yaw, 145) * 0.75F);
            this.setPartPosition(this.seat2, getXForPart(yaw, -145) * 0.75F, 2F, getZForPart(yaw, -145) * 0.75F);
            this.setPartPosition(this.seat3, getXForPart(yaw, 35) * 0.95F, 2F, getZForPart(yaw, 35) * 0.95F);
            this.setPartPosition(this.seat4, getXForPart(yaw, -35) * 0.95F, 2F, getZForPart(yaw, -35) * 0.95F);

            if (level.isClientSide && this.isChilling()) {
                if (!this.isBaby()) {
                    this.level.addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, 158) * 1.75F, this.getY(1), this.getZ() + getZForPart(yaw, 158) * 1.75F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, -166) * 1.48F, this.getY(1), this.getZ() + getZForPart(yaw, -166) * 1.48F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, 14) * 1.78F, this.getY(0.9), this.getZ() + getZForPart(yaw, 14) * 1.78F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, -14) * 1.6F, this.getY(1.1), this.getZ() + getZForPart(yaw, -14) * 1.6F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                }
                this.level.addParticle(ParticleTypes.SMOKE, this.headPart.getRandomX(0.6D), this.headPart.getY(0.9), this.headPart.getRandomZ(0.6D), 0.0D, this.random.nextDouble() / 5.0D, 0.0D);

            }

            for (int l = 0; l < this.allParts.length; ++l) {
                this.allParts[l].xo = avector3d[l].x;
                this.allParts[l].yo = avector3d[l].y;
                this.allParts[l].zo = avector3d[l].z;
                this.allParts[l].xOld = avector3d[l].x;
                this.allParts[l].yOld = avector3d[l].y;
                this.allParts[l].zOld = avector3d[l].z;
            }
        }
        if ((this.isInLava() || this.isInWaterOrBubble()) && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!(this.isInLava() || this.isInWaterOrBubble()) && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (!level.isClientSide) {
            if (this.getChillTime() > 0) {
                System.out.println(this.getChillTime());
                this.setChillTime(this.getChillTime() - 1);
            } else if (this.shouldSwim()) {
                if (random.nextInt(this.isVehicle() ? 200 : 2000) == 0 && revengeCooldown == 0) {
                    this.setChillTime(100 + random.nextInt(500));
                }
            }
            if (revengeCooldown > 0) {
                revengeCooldown--;
            }
            if(headPeakCooldown > 0){
                headPeakCooldown--;
            }
            if (revengeCooldown == 0 && this.getLastHurtByMob() != null) {
                this.setLastHurtByMob(null);
            }
        }
        if (!level.isClientSide) {
            if (this.getChillTime() > 0 || this.hasHeadGear() || this.isVehicle() || dismountCooldown > 0) {
                floatLaviathan();
            }

            if (!this.isChilling() && headPeakCooldown == 0) {
                float low = getLowHeadHeight();
                this.setHeadHeight(this.getHeadHeight() +  (0.5F + ((getLowHeadHeight() + getHighHeadHeight(low)) / 2F) - this.getHeadHeight()) * 0.2F);
            } else {
                if (getMaxFluidHeight() <= this.getBbHeight() * 0.5F && getMaxFluidHeight() >= this.getBbHeight() * 0.25F) {
                    float mot = (float) this.getDeltaMovement().lengthSqr();
                    this.setHeadHeight(Mth.clamp(this.getHeadHeight() + 0.1F - 0.2F * mot, 0, 2));
                    headPeakCooldown = 5;
                }
            }
        }
        if (this.isChilling()) {
            boolean keepChillin = false;
            boolean startBiting = false;
            for (EntityCrimsonMosquito entity : this.level.getEntitiesOfClass(EntityCrimsonMosquito.class, this.getBoundingBox().inflate(30.0D), HEALTHY_MOSQUITOES)) {
                entity.setLuringLaviathan(this.getId());
                keepChillin = true;
            }
            if (keepChillin) {
                this.setChillTime(Math.max(20, this.getChillTime()));
            }
            for (EntityCrimsonMosquito entity : this.level.getEntitiesOfClass(EntityCrimsonMosquito.class, this.headPart.getBoundingBox().inflate(1.0D), HEALTHY_MOSQUITOES)) {
                startBiting = true;
                if (this.biteProgress == 5.0F) {
                    entity.hurt(DamageSource.mobAttack(this), 1000);
                    entity.setShrink(true);
                    this.setChillTime(0);
                }
            }
            if (startBiting) {
                if (this.entityData.get(ATTACK_TICK) <= 0 && this.biteProgress == 0) {
                    this.entityData.set(ATTACK_TICK, 7);
                }
            }
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
        }
        if (this.entityData.get(ATTACK_TICK) > 0 && this.biteProgress < 5.0F) {
            this.biteProgress++;
        }
        if (this.entityData.get(ATTACK_TICK) <= 0 && this.biteProgress > 0.0F) {
            this.biteProgress--;
        }
        if (dismountCooldown > 0) {
            dismountCooldown--;
        }
        if (this.hasBodyGear()) {
            List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F), EntitySelector.pushableBy(this));
            if (!list.isEmpty()) {
                boolean flag2 = !this.level.isClientSide;
                for (int j = 0; j < list.size(); ++j) {
                    Entity entity = list.get(j);
                    if (!entity.hasPassenger(this)) {
                        if (flag2 && !(entity instanceof Player) && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && !(entity instanceof EntityLaviathan) && !(entity instanceof Monster) && entity instanceof Mob && this.canAddPassenger(entity) && !(entity instanceof WaterAnimal)) {
                            entity.startRiding(this);
                        } else {
                            this.push(entity);
                        }
                    }
                }
            }
        }
    }

    public float getLowHeadHeight() {
        float checkAt = 0F;
        while (checkAt > -3F && !isHeadInWall((float) this.getY() + checkAt)) {
            checkAt -= 0.2F;
        }
        return checkAt;
    }

    public float getHighHeadHeight(float low) {
        float checkAt = 3F;
        while (checkAt > 0) {
            if(isHeadInWall((float) this.getY() + checkAt)) {
                break;
            }
            checkAt -= 0.2F;
        }

        return checkAt;
    }

    public boolean canStandOnFluid(Fluid p_230285_1_) {
        return false;
    }


    public boolean isHeadInWall(float offset) {
        if (this.noPhysics) {
            return false;
        } else {
            float f = 0.8F;
            Vec3 vec1 = new Vec3(headPart.getX(), offset, headPart.getZ());
            AABB aabb = AABB.ofSize(vec1, f, 1.0E-6D, f);
            return this.level.getBlockCollisions(this, aabb, (p_20129_, p_20130_) -> {
                return p_20129_.isSuffocating(this.level, p_20130_);
            }).findAny().isPresent();
        }
    }

    private void floatLaviathan() {
        if (this.shouldSwim()) {
            if (getMaxFluidHeight() >= this.getBbHeight()) {
                this.setDeltaMovement(this.getDeltaMovement().x, 0.12F, this.getDeltaMovement().z);
            } else if (getMaxFluidHeight() >= this.getBbHeight() * 0.5F) {
                this.setDeltaMovement(this.getDeltaMovement().x, 0.08F, this.getDeltaMovement().z);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().x, 0.0F, this.getDeltaMovement().z);
            }

        }

    }

    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        float expand = this.getBbWidth() + 1.0F;
        Vec3[] avector3d = new Vec3[]{getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot()), getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() - 22.5F), getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() + 22.5F), getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() - 45.0F), getCollisionHorizontalEscapeVector(expand, livingEntity.getBbWidth(), livingEntity.getYRot() + 45.0F)};
        Set<BlockPos> set = Sets.newLinkedHashSet();
        double d0 = this.getBoundingBox().maxY;
        double d1 = this.getBoundingBox().minY - 0.5D;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (Vec3 vector3d : avector3d) {
            blockpos$mutable.set(this.getX() + vector3d.x, d0, this.getZ() + vector3d.z);

            for (double d2 = d0; d2 > d1; --d2) {
                set.add(blockpos$mutable.immutable());
                blockpos$mutable.move(Direction.DOWN);
            }
        }

        for (BlockPos blockpos : set) {
            if (!this.level.getFluidState(blockpos).is(FluidTags.LAVA)) {
                double d3 = this.level.getBlockFloorHeight(blockpos);
                if (DismountHelper.isBlockFloorValid(d3)) {
                    Vec3 vector3d1 = Vec3.upFromBottomCenterOf(blockpos, d3);

                    for (Pose pose : livingEntity.getDismountPoses()) {
                        AABB axisalignedbb = livingEntity.getLocalBoundsForPose(pose);
                        if (DismountHelper.canDismountTo(this.level, livingEntity, axisalignedbb.move(vector3d1))) {
                            livingEntity.setPose(pose);
                            return vector3d1;
                        }
                    }
                }
            }
        }

        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    public float getWaterLevelAbove() {
        AABB axisalignedbb = this.getBoundingBox();
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.maxY);
        int l = Mth.ceil(axisalignedbb.maxY);
        int i1 = Mth.floor(axisalignedbb.minZ);
        int j1 = Mth.ceil(axisalignedbb.maxZ);
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        label39:
        for (int k1 = k; k1 < l; ++k1) {
            float f = 0.0F;

            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.set(l1, k1, i2);
                    FluidState fluidstate = this.level.getFluidState(blockpos$mutable);
                    if (fluidstate.is(FluidTags.WATER) || fluidstate.is(FluidTags.LAVA)) {
                        f = Math.max(f, fluidstate.getHeight(this.level, blockpos$mutable));
                    }

                    if (f >= 1.0F) {
                        continue label39;
                    }
                }
            }

            if (f < 1.0F) {
                return (float) blockpos$mutable.getY() + f;
            }
        }

        return (float) (l + 1);
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public boolean shouldSwim() {
        return getMaxFluidHeight() >= 0.1F || this.isInLava() || this.isInWaterOrBubble();
    }

    private float getXForPart(float yaw, float degree) {
        return Mth.sin((float) (yaw + Math.toRadians(degree)));
    }

    private float getZForPart(float yaw, float degree) {
        return -Mth.cos((float) (yaw + Math.toRadians(degree)));
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

    public boolean hasHeadGear() {
        return this.entityData.get(HAS_HEAD_GEAR);
    }

    public void setHeadGear(boolean headGear) {
        this.entityData.set(HAS_HEAD_GEAR, headGear);
    }

    public boolean hasBodyGear() {
        return this.entityData.get(HAS_BODY_GEAR);
    }

    public void setBodyGear(boolean bodyGear) {
        this.entityData.set(HAS_BODY_GEAR, bodyGear);
    }

    public int getChillTime() {
        return this.entityData.get(CHILL_TIME);
    }

    public void setChillTime(int chillTime) {
        this.entityData.set(CHILL_TIME, chillTime);
    }

    public float getHeadYaw(float interp) {
        float f;
        if (interp == 0.0F) {
            f = getYHeadRot() - this.yBodyRot;
        } else {
            float yBodyRot1 = this.yBodyRotO + (this.yBodyRot - this.yBodyRotO) * interp;
            float yHeadRot1 = this.yHeadRotO + (getYHeadRot() - this.yHeadRotO) * interp;
            f = yHeadRot1 - yBodyRot1;
        }
        return Mth.clamp(Mth.wrapDegrees(f), -50, 50);
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
        return this.allParts;
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
        return this.isVehicle();
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isVehicle();
    }

    @Override
    public int getWaterSearchRange() {
        return 15;
    }

    private double getMaxFluidHeight() {
        return Math.max(this.getFluidHeight(FluidTags.LAVA), this.getFluidHeight(FluidTags.WATER));
    }

    public boolean isChilling() {
        return this.getChillTime() > 0 && this.getMaxFluidHeight() <= this.getBbHeight() * 0.5F;
    }

    public void scaleParts() {
        for (EntityLaviathanPart parts : allParts) {
            float prev = parts.scale;
            parts.scale = this.isBaby() ? 0.5F : 1F;
            if (prev != parts.scale) {
                parts.refreshDimensions();
            }
        }
    }

    public void aiStep() {
        super.aiStep();
        scaleParts();
    }

    public Vec3 getLureMosquitoPos() {
        return new Vec3(this.headPart.getX(), this.headPart.getY(0.4F), this.headPart.getZ());
    }

    @Override
    public void onPanic() {

    }

    @Override
    public boolean canPanic() {
        return true;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.LAVIATHAN.create(serverWorld);
    }

    static class MoveController extends MoveControl {
        private final EntityLaviathan laviathan;

        public MoveController(EntityLaviathan dolphinIn) {
            super(dolphinIn);
            this.laviathan = dolphinIn;
        }

        public void tick() {
            float speed = (float) (this.speedModifier * 3 * laviathan.getAttributeValue(Attributes.MOVEMENT_SPEED));
            if (!laviathan.isVehicle()) {
                if (this.laviathan.isChilling()) {
                    speed *= 0.5F;
                } else if (this.laviathan.shouldSwim()) {
                    this.laviathan.setDeltaMovement(this.laviathan.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
                }
            }
            if (this.operation == Operation.MOVE_TO && (!this.laviathan.getNavigation().isDone() || laviathan.getControllingPassenger() != null)) {
                double lvt_1_1_ = this.wantedX - laviathan.getX();
                double lvt_3_1_ = this.wantedY - laviathan.getY();
                double lvt_5_1_ = this.wantedZ - laviathan.getZ();

                double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
                if (lvt_7_1_ < 2.5F) {
                    this.laviathan.setZza(0.0F);
                } else {
                    float lvt_9_1_ = (float) (Mth.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
                    this.laviathan.setYRot(this.rotlerp(this.laviathan.getYRot(), lvt_9_1_, 5F));
                    this.laviathan.setYHeadRot(this.rotlerp(this.laviathan.getYHeadRot(), lvt_9_1_, 90.0F));
                    BlockPos blockpos = this.mob.blockPosition();
                    BlockState blockstate = this.mob.level.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level, blockpos);
                    if (lvt_3_1_ >= 0 && laviathan.horizontalCollision) {
                        laviathan.setDeltaMovement(laviathan.getDeltaMovement().add(0.0D, 0.5D, 0.0D));
                    } else {
                        laviathan.setDeltaMovement(laviathan.getDeltaMovement().add(0.0D, (double) laviathan.getSpeed() * lvt_3_1_ * 0.6D, 0.0D));
                    }
                    if (laviathan.shouldSwim()) {
                        laviathan.setSpeed(speed * 0.03F);
                        float lvt_11_1_ = -((float) (Mth.atan2(lvt_3_1_, Mth.sqrt((float) (lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_))) * 57.2957763671875D));
                        lvt_11_1_ = Mth.clamp(Mth.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                        laviathan.setXRot(this.rotlerp(laviathan.getXRot(), lvt_11_1_, 25.0F));
                        float lvt_12_1_ = Mth.cos(laviathan.getXRot() * 0.017453292F);
                        float lvt_13_1_ = Mth.sin(laviathan.getXRot() * 0.017453292F);
                        laviathan.zza = lvt_12_1_ * speed;
                        laviathan.yya = -lvt_13_1_ * speed;
                    } else {
                        laviathan.setSpeed(speed * 0.1F);
                    }

                }
            }else if(!laviathan.level.getBlockState(this.laviathan.blockPosition().above()).getFluidState().isEmpty() && laviathan.getChillTime() <= 0){
                this.laviathan.setDeltaMovement(this.laviathan.getDeltaMovement().add(0.0D, -0.05D, 0.0D));
            }
        }
    }

    private class LaviathanBodyRotationControl extends BodyRotationControl {
        private final EntityLaviathan laviathan;

        public LaviathanBodyRotationControl(EntityLaviathan laviathan) {
            super(laviathan);
            this.laviathan = laviathan;
        }

        public void clientTick() {
        }
    }
}
