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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;

public class EntityStraddleboard extends Entity implements PlayerRideableJumping {
    private static final EntityDataAccessor<ItemStack> ITEMSTACK = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> TIME_SINCE_HIT = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DEFAULT_COLOR = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> BOARD_ROT = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> REMOVE_SOON = SynchedEntityData.defineId(EntityStraddleboard.class, EntityDataSerializers.BOOLEAN);
    public float prevBoardRot = 0;
    private boolean rocking;
    private float rockingIntensity;
    private float rockingAngle;
    private float prevRockingAngle;
    private int extinguishTimer = 0;

    private int jumpFor = 0;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private int rideForTicks = 0;

    private float boardForwards = 0.0F;
    private int removeIn;
    private Player returnToPlayer = null;

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

    protected void defineSynchedData() {
        this.entityData.define(TIME_SINCE_HIT, 0);
        this.entityData.define(ITEMSTACK, new ItemStack(AMItemRegistry.STRADDLEBOARD.get()));
        this.entityData.define(DEFAULT_COLOR, true);
        this.entityData.define(COLOR, 0);
        this.entityData.define(BOARD_ROT, 0F);
        this.entityData.define(REMOVE_SOON, false);
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }

    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle result) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, result));
    }

    public double getPassengersRidingOffset() {
        return 0.5D;
    }

    public float getBoardRot(){
        return this.entityData.get(BOARD_ROT);
    }

    public void setBoardRot(float f){
        this.entityData.set(BOARD_ROT, f);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && !this.isRemoved()) {
            this.entityData.set(REMOVE_SOON, true);
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

    public boolean isRemoveLogic() {
        return this.entityData.get(REMOVE_SOON) || this.isRemoved();
    }

    public boolean canBeCollidedWith() {
        return !this.isRemoveLogic();
    }

    public boolean isPushable() {
        return !this.isRemoveLogic();
    }

    public boolean isPickable() {
        return !this.isRemoveLogic();
    }

    public boolean shouldBeSaved() {
        return !this.isRemoveLogic();
    }

    public boolean isAttackable() {
        return !this.isRemoveLogic();
    }

    public boolean isDefaultColor() {
        return this.entityData.get(DEFAULT_COLOR);
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

    public void tick() {
        super.tick();
        float boardRot = this.getBoardRot();
        if(jumpFor > 0){
            jumpFor--;
        }
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (extinguishTimer > 0) {
            extinguishTimer--;
        }
        if (this.entityData.get(REMOVE_SOON)) {
            this.removeIn--;
            this.setBoardRot((float) Math.sin(this.removeIn * 0.3F * Math.PI) * 50F);
            if (this.removeIn <= 0 && !this.level.isClientSide) {
                this.removeIn = 0;
                boolean drop;
                if(this.getEnchant(AMEnchantmentRegistry.STRADDLE_BOARDRETURN.get()) > 0){
                    drop = returnToPlayer != null && !returnToPlayer.addItem(this.getItemBoard());
                }else{
                    drop = true;
                }
                if(drop){
                    spawnAtLocation(this.getItemStack().copy());
                }
                this.discard();
            }
        }
        Entity controller = getControllingPlayer();
        if (this.level.isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY())  / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
                this.setRot(this.getYRot(), this.getXRot());
            } else {
                this.reapplyPosition();
                this.setRot(this.getYRot(), this.getXRot());
            }
        } else {
            this.checkInsideBlocks();
            float slowdown = this.isInWaterOrBubble() || isOnGround() ? 0.05F : 0.98F;
            tickMovement();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(slowdown, slowdown, slowdown));
            float f2 = (float) -((float) this.getDeltaMovement().y * 0.5F * (double) Mth.RAD_TO_DEG);
            this.setXRot(Mth.approachDegrees(this.getXRot(), f2, 5));

            if (controller instanceof Player player) {
                returnToPlayer = player;
                rideForTicks++;
                if (this.tickCount % 50 == 0) {
                    if (getEnchant(AMEnchantmentRegistry.STRADDLE_LAVAWAX.get()) > 0) {
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false));
                    }
                }
                if (player.getRemainingFireTicks() > 0 && extinguishTimer == 0) {
                    player.clearFire();
                }
                this.setYRot(Mth.approachDegrees(this.getYRot(), player.getYRot(), 6));
                Vec3 deltaMovement = this.getDeltaMovement();
                if (deltaMovement.y > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                float slow = player.zza < 0 ? 0 : player.zza * 0.115F;

                float threshold = 3F;
                boolean flag = false;
                float boardRot1 = boardRot;
                if (this.yRotO - this.getYRot() > threshold) {
                    boardRot1 += 10;
                    flag = true;
                }
                if (this.yRotO - this.getYRot() < -threshold) {
                    boardRot1 -= 10;
                    flag = true;
                }
                if (!flag) {
                    if (boardRot1 > 0) {
                        boardRot1 = Math.max(boardRot1 - 5, 0);
                    }
                    if (boardRot1 < 0) {
                        boardRot1 = Math.min(boardRot1 + 5, 0);
                    }
                }

                this.setBoardRot(Mth.approachDegrees(boardRot, Mth.clamp(boardRot1, -25, 25), 5));

                boardForwards = slow;

                if(player.isShiftKeyDown() || !this.isAlive() || this.entityData.get(REMOVE_SOON)){
                    this.ejectPassengers();
                }
                if (player.isInWall()) {
                    this.ejectPassengers();
                    this.hurt(DamageSource.GENERIC, 100);
                }
            }else{
                rideForTicks = 0;
            }
        }
        prevBoardRot = boardRot;
    }

    private void tickMovement() {
        this.hasImpulse = true;
        float moveForwards = Math.min(boardForwards, 1.0F);
        float yRot = this.getYRot();
        Vec3 prev = this.getDeltaMovement();
        float gravity = isOnLava() ? 0.0F : isInLava() ? 0.1F : -1;
        float f1 = -Mth.sin(yRot * ((float) Math.PI / 180F));
        float f2 = Mth.cos(yRot * ((float) Math.PI / 180F));
        Vec3 moveVec = new Vec3(f1, 0, f2).scale(moveForwards);
        Vec3 vec31 = prev.scale(0.975F).add(moveVec);
        float jumpGravity = gravity;
        if(jumpFor > 0){
            float jumpRunsOutIn = jumpFor < 5 ? jumpFor / 5F : 1F;
            jumpGravity += jumpRunsOutIn + jumpRunsOutIn * 1F;
        }
        this.setDeltaMovement(vec31.x, jumpGravity, vec31.z);
    }

    private boolean isOnLava() {
        BlockPos ourPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY() + 0.4F), Mth.floor(this.getZ())); // BlockPos.containing()
        BlockPos underPos = this.getOnPos();
        return this.level.getFluidState(underPos).is(FluidTags.LAVA) && !this.level.getFluidState(ourPos).is(FluidTags.LAVA);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    public double getEyeY() {
        return this.getY() + 0.3F;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        return getControllingPlayer();
    }

    @Nullable
    public boolean isControlledByLocalInstance() {
        return false;
    }

    @Nullable
    public Player getControllingPlayer(){
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                return (Player) passenger;
            }
        }
        return null;
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lSteps > 0) {
            this.lSteps = 0;
            this.absMoveTo(this.lx, this.ly, this.lz, (float) this.lyr, (float) this.lxr);
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (REMOVE_SOON.equals(entityDataAccessor)) {
            this.removeIn = 5;
        }
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

    @OnlyIn(Dist.CLIENT)
    public float getRockingAngle(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevRockingAngle, this.rockingAngle);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
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
        return isOnLava();
    }

    @Override
    public void handleStartJump(int i) {
        this.hasImpulse = true;
        if(canJump()){
            float f = 0.075F + getEnchant(AMEnchantmentRegistry.STRADDLE_JUMP.get()) * 0.05F;
            jumpFor = 5 + (int)(i * f);
        }
    }

    private int getEnchant(Enchantment enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, this.getItemBoard());
    }

    public boolean shouldSerpentFriend() {
        return getEnchant(AMEnchantmentRegistry.STRADDLE_SERPENTFRIEND.get()) > 0;
    }

    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        return new Vec3(this.getX(), this.getY() + 2F, this.getZ());
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
}
