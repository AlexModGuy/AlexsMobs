package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.enchantment.AMEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;

public class EntityStraddleboard extends Entity implements PlayerRideableJumping {
    private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE_TAKEN = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ROCKING_TICKS = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DEFAULT_COLOR = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.BOOLEAN);
    public float boardRot = 0;
    public float prevBoardRot = 0;
    private double lastYd;
    private boolean rocking;
    private boolean downwards;
    private float rockingIntensity;
    private float rockingAngle;
    private float prevRockingAngle;
    private boolean jumpOutOfLava = false;
    private float outOfControlTicks;
    private Boat.Status status;
    private Boat.Status previousStatus;
    private float momentum;
    private double waterLevel;
    private float boatGlide;
    private int extinguishTimer = 0;

    public EntityStraddleboard(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
        this.blocksBuilding = true;
    }

    public EntityStraddleboard(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.STRADDLEBOARD.get(), world);
    }

    public EntityStraddleboard(Level worldIn, double x, double y, double z) {
        this(AMEntityRegistry.STRADDLEBOARD.get(), worldIn);
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public static boolean canVehicleCollide(Entity p_242378_0_, Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !p_242378_0_.isPassengerOfSameVehicle(entity);
    }

    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height;
    }

    protected boolean isMovementNoisy() {
        return false;
    }

    protected void defineSynchedData() {
        this.entityData.define(TIME_SINCE_HIT, 0);
        this.entityData.define(ITEMSTACK, new ItemStack(AMItemRegistry.STRADDLEBOARD.get()));
        this.entityData.define(ROCKING_TICKS, 0);
        this.entityData.define(DEFAULT_COLOR, true);
        this.entityData.define(COLOR, 0);
        this.entityData.define(DAMAGE_TAKEN, 0.0F);
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean isPushable() {
        return true;
    }

    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle result) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, result));
    }

    public double getPassengersRidingOffset() {
        return 0.9D;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && !this.isRemoved()) {
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
            this.markHurt();
            this.setRockingTicks(25);
            boolean flag = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (flag || this.getDamageTaken() > 40.0F) {
                if (!flag) {
                    Player p = null;
                    if (source.getEntity() instanceof Player) {
                        p = (Player) source.getEntity();
                    }
                    if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
                        p = (Player) this.getControllingPassenger();
                    }
                    if(!this.isRemoved()){
                        boolean dropItem = true;
                        if (p != null && this.getEnchant(AMEnchantmentRegistry.STRADDLE_BOARDRETURN) > 0) {
                            if (p.addItem(this.getItemBoard())) {
                                dropItem = false;
                            }
                        }
                        if (dropItem) {
                            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                this.spawnAtLocation(this.getItemBoard());
                            }
                        }
                        this.remove(RemovalReason.DISCARDED);
                    }
                }

                this.remove(RemovalReason.DISCARDED);
            }

            return true;
        } else {
            return true;
        }
    }

    private ItemStack getItemBoard() {
        return this.getItemStack();
    }


    public void push(Entity entityIn) {
        if (entityIn instanceof EntityStraddleboard) {
            if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entityIn);
            }
        } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entityIn);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void animateHurt() {
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    private Boat.Status getBoatStatus() {
        Boat.Status boatentity$status = this.getUnderwaterStatus();
        if (boatentity$status != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return boatentity$status;
        } else if (this.checkInWater()) {
            return Boat.Status.IN_WATER;
        } else {
            float f = this.getBoatGlide();
            if (f > 0.0F) {
                this.boatGlide = f;
                return Boat.Status.ON_LAND;
            } else {
                return Boat.Status.IN_AIR;
            }
        }
    }

    public float getBoatGlide() {
        AABB axisalignedbb = this.getBoundingBox();
        AABB axisalignedbb1 = new AABB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = Mth.floor(axisalignedbb1.minX) - 1;
        int j = Mth.ceil(axisalignedbb1.maxX) + 1;
        int k = Mth.floor(axisalignedbb1.minY) - 1;
        int l = Mth.ceil(axisalignedbb1.maxY) + 1;
        int i1 = Mth.floor(axisalignedbb1.minZ) - 1;
        int j1 = Mth.ceil(axisalignedbb1.maxZ) + 1;
        VoxelShape voxelshape = Shapes.create(axisalignedbb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int l1 = i; l1 < j; ++l1) {
            for (int i2 = i1; i2 < j1; ++i2) {
                int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
                if (j2 != 2) {
                    for (int k2 = k; k2 < l; ++k2) {
                        if (j2 <= 0 || k2 != k && k2 != l - 1) {
                            blockpos$mutable.set(l1, k2, i2);
                            BlockState blockstate = this.level.getBlockState(blockpos$mutable);
                            if (!(blockstate.getBlock() instanceof WaterlilyBlock) && Shapes.joinIsNotEmpty(blockstate.getBlockSupportShape(this.level, blockpos$mutable).move(l1, k2, i2), voxelshape, BooleanOp.AND)) {
                                f += blockstate.getFriction(this.level, blockpos$mutable, this);
                                ++k1;
                            }
                        }
                    }
                }
            }
        }

        return f / (float) k1;
    }

    private boolean checkInWater() {
        AABB axisalignedbb = this.getBoundingBox();
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.minY);
        int l = Mth.ceil(axisalignedbb.minY - 0.001D);
        int i1 = Mth.floor(axisalignedbb.minZ);
        int j1 = Mth.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.set(k1, l1, i2);
                    FluidState fluidstate = this.level.getFluidState(blockpos$mutable);
                    if (fluidstate.is(FluidTags.WATER) || fluidstate.is(FluidTags.LAVA)) {
                        float f = (float) l1 + fluidstate.getHeight(this.level, blockpos$mutable);
                        this.waterLevel = Math.max(f, this.waterLevel);
                        flag |= axisalignedbb.minY < (double) f;
                    }
                }
            }
        }

        return flag;
    }

    private void updateMotion() {
        double d0 = -0.04F;
        double d1 = this.isNoGravity() ? 0.0D : (double) -0.04F;
        double d2 = 0.0D;
        this.momentum = 0.05F;
        if (this.previousStatus == Boat.Status.IN_AIR && this.status != Boat.Status.IN_AIR && this.status != Boat.Status.ON_LAND) {
            this.waterLevel = this.getY(1.0D);
            this.setPos(this.getX(), (double) (this.getWaterLevelAbove() - this.getBbHeight()) + 0.25, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 1D, 1.0D));
            this.lastYd = 0.0D;
            this.status = Boat.Status.IN_WATER;
        } else {
            if (this.status == Boat.Status.IN_WATER) {
                d2 = (this.waterLevel - this.getY()) / (double) this.getBbHeight();
                this.momentum = 0.9F;
            } else if (this.status == Boat.Status.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
                this.momentum = 0.9F;
            } else if (this.status == Boat.Status.UNDER_WATER) {
                d2 = 0.01F;
                this.momentum = 0.45F;
            } else if (this.status == Boat.Status.IN_AIR) {
                this.momentum = 0.9F;
            } else if (this.status == Boat.Status.ON_LAND) {
                this.momentum = this.boatGlide;
                if (this.getControllingPassenger() instanceof Player) {
                    this.boatGlide /= 2.0F;
                }
            }

            Vec3 vector3d = this.getDeltaMovement();
            this.setDeltaMovement(vector3d.x * (double) this.momentum, vector3d.y + d1, vector3d.z * (double) this.momentum);
            if (d2 > 0.0D) {
                Vec3 vector3d1 = this.getDeltaMovement();
                this.setDeltaMovement(vector3d1.x, (vector3d1.y + d2 * 0.06153846016296973D) * 0.75D, vector3d1.z);
            }
        }

    }

    public boolean isDefaultColor() {
        return this.entityData.get(DEFAULT_COLOR).booleanValue();
    }

    public void setDefaultColor(boolean bar) {
        this.entityData.set(DEFAULT_COLOR, Boolean.valueOf(bar));
    }

    public int getColor() {
        if (isDefaultColor()) {
            return 0XADC3D7;
        }
        return this.entityData.get(COLOR);
    }

    public void setColor(int index) {
        this.entityData.set(COLOR, index);
    }


    @Nullable
    private Boat.Status getUnderwaterStatus() {
        AABB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.maxY);
        int l = Mth.ceil(d0);
        int i1 = Mth.floor(axisalignedbb.minZ);
        int j1 = Mth.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.set(k1, l1, i2);
                    FluidState fluidstate = this.level.getFluidState(blockpos$mutable);
                    if ((fluidstate.is(FluidTags.WATER) || fluidstate.is(FluidTags.LAVA) && d0 < (double) ((float) blockpos$mutable.getY() + fluidstate.getHeight(this.level, blockpos$mutable)))) {
                        if (!fluidstate.isSource()) {
                            return Boat.Status.UNDER_FLOWING_WATER;
                        }

                        flag = true;
                    }
                }
            }
        }

        return flag ? Boat.Status.UNDER_WATER : null;
    }


    public void tick() {
        prevBoardRot = this.boardRot;
        super.tick();
        this.previousStatus = this.status;
        this.status = this.getBoatStatus();
        this.floatStrider();
        this.checkInsideBlocks();
        if (this.isInWall()) {
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (this.getDamageTaken() > 0.0F) {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }
        if (isInLava()) {
            this.setNoGravity(true);
            if (this.fluidHeight.getDouble(FluidTags.LAVA) >= this.getBbHeight()) {
                this.setDeltaMovement(0, 0.1, 0);
            }
        } else {
            this.setNoGravity(false);
        }
        float f2 = (float) -((float) this.getDeltaMovement().y * 0.5F * (double) (180F / (float) Math.PI));
        this.setXRot(f2);

        if (extinguishTimer > 0) {
            extinguishTimer--;
        }
        this.updateRocking();
        Entity controller = getControllingPassenger();
        if (controller instanceof Player) {
            Player player = (Player) controller;
            if (this.tickCount % 50 == 0) {
                if (getEnchant(AMEnchantmentRegistry.STRADDLE_LAVAWAX) > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false));
                }
            }
            if (player.getRemainingFireTicks() > 0 && extinguishTimer == 0) {
                player.clearFire();
            }
            this.setYRot(player.yRotO);
            Vec3 vector3d = this.getDeltaMovement();
            if (vector3d.y > -0.5D) {
                this.fallDistance = 1.0F;
            }

            Vec3 vector3d1 = player.getLookAngle();
            float f = player.getXRot() * ((float) Math.PI / 180F);
            double d1 = Math.sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z);
            double d3 = Math.sqrt((float) vector3d.horizontalDistanceSqr());
            double d4 = vector3d1.length();
            float f1 = Mth.cos(f);
            f1 = (float) ((double) f1 * (double) f1 * Math.min(1.0D, d4 / 0.4D));
            double d5 = vector3d.y * -0.1D * (double) f1;
            float slow = player.zza < 0 ? 0 : player.zza * 0.115F;
            float threshold = 0.05F;
            if (this.yRotO - this.getYRot() > threshold) {
                boardRot = boardRot + 2;
                slow *= 0;
            } else if (this.yRotO - this.getYRot() < -threshold) {
                boardRot = boardRot - 2;
                slow *= 0;
            } else if (boardRot > 0) {
                boardRot = (Math.max(boardRot - 10, 0));
            } else if (boardRot < 0) {
                boardRot = (Math.min(boardRot + 10, 0));
            }
            boardRot = (Mth.clamp(boardRot, -25, 25));

            vector3d = vector3d.add(vector3d1.x * slow / d1, 0.0D, vector3d1.z * slow / d1);


            if (d1 > 0.0D) {
                vector3d = vector3d.add((vector3d1.x / d1 * d3 - vector3d.x) * 0.1D, 0.0D, (vector3d1.z / d1 * d3 - vector3d.z) * 0.1D);
            }

            this.setDeltaMovement(vector3d.multiply(0.99F, 1F, 0.99F));

            if (player.isInWall()) {
                player.removeVehicle();
                this.hurt(DamageSource.GENERIC, 100);
            }
        }
        this.updateMotion();
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    public double getEyeY() {
        return this.getY() + 0.3F;
    }


    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!level.isClientSide) {
            EntityStraddleboard copy = AMEntityRegistry.STRADDLEBOARD.get().create(level);
            CompoundTag tag = new CompoundTag();
            this.addAdditionalSaveData(tag);
            copy.readAdditionalSaveData(tag);
            copy.copyPosition(passenger);
            level.addFreshEntity(copy);
        }
        this.remove(RemovalReason.DISCARDED);
    }

    private void floatStrider() {

    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                Player player = (Player) passenger;
                return player;
            }
        }
        return null;
    }

    private void updateRocking() {
        if (this.level.isClientSide) {
            int i = this.getRockingTicks();
            if (i > 0) {
                this.rockingIntensity += 1F;
            } else {
                this.rockingIntensity -= 0.1F;
            }

            this.rockingIntensity = Mth.clamp(this.rockingIntensity, 0.0F, 1.0F);
            this.prevRockingAngle = this.rockingAngle;
            this.rockingAngle = 10.0F * (float) Math.sin(0.5F * (float) this.level.getGameTime()) * this.rockingIntensity;
        } else {
            if (!this.rocking) {
                this.setRockingTicks(0);
            }

            int k = this.getRockingTicks();
            if (k > 0) {
                --k;
                this.setRockingTicks(k);
                int j = 60 - k - 1;
                if (j > 0 && k == 0) {
                    this.setRockingTicks(0);
                    Vec3 vector3d = this.getDeltaMovement();
                    if (this.downwards) {
                        this.setDeltaMovement(vector3d.add(0.0D, -0.7D, 0.0D));
                        this.ejectPassengers();
                    } else {
                        this.setDeltaMovement(vector3d.x, this.hasPassenger((p_150274_) -> {
                            return p_150274_ instanceof Player;
                        }) ? 2.7D : 0.6D, vector3d.z);
                    }
                }

                this.rocking = false;
            }
        }

    }

    public float getWaterLevelAbove() {
        AABB axisalignedbb = this.getBoundingBox();
        int i = Mth.floor(axisalignedbb.minX);
        int j = Mth.ceil(axisalignedbb.maxX);
        int k = Mth.floor(axisalignedbb.maxY);
        int l = Mth.ceil(axisalignedbb.maxY - this.lastYd);
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

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else {
            if (!this.level.isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken() {
        return this.entityData.get(DAMAGE_TAKEN);
    }

    public void setDamageTaken(float damageTaken) {
        this.entityData.set(DAMAGE_TAKEN, damageTaken);
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit() {
        return this.entityData.get(TIME_SINCE_HIT);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int timeSinceHit) {
        this.entityData.set(TIME_SINCE_HIT, timeSinceHit);
    }

    private int getRockingTicks() {
        return this.entityData.get(ROCKING_TICKS);
    }

    private void setRockingTicks(int ticks) {
        this.entityData.set(ROCKING_TICKS, ticks);
    }

    @OnlyIn(Dist.CLIENT)
    public float getRockingAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevRockingAngle, this.rockingAngle);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setDefaultColor(compound.getBoolean("IsDefColor"));
        if (compound.contains("BoardStack")) {
            this.setItemStack(ItemStack.of(compound.getCompound("BoardStack")));
        }
        this.setColor(compound.getInt("Color"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("IsDefColor", this.isDefaultColor());
        compound.putInt("Color", this.getColor());
        if (!this.getItemStack().isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.getItemStack().save(stackTag);
            compound.put("BoardStack", stackTag);
        }

    }

    @Override
    public void onPlayerJump(int i) {

    }

    @Override
    public boolean canJump() {
        return this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getFluidState().is(FluidTags.LAVA);
    }

    @Override
    public void handleStartJump(int i) {
        jumpOutOfLava = true;
        this.hasImpulse = true;
        float scaled = i * 0.01F + 0.1F * getEnchant(AMEnchantmentRegistry.STRADDLE_JUMP);
        this.setDeltaMovement(this.getDeltaMovement().add(0, scaled * 1.5F, 0));
    }

    private int getEnchant(Enchantment enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, this.getItemBoard());
    }

    public boolean shouldSerpentFriend() {
        return getEnchant(AMEnchantmentRegistry.STRADDLE_SERPENTFRIEND) > 0;
    }

    @Override
    public void handleStopJump() {

    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEMSTACK);
    }

    public void setItemStack(ItemStack item) {
        this.entityData.set(ITEMSTACK, item);
    }

    public enum Status {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR
    }
}
