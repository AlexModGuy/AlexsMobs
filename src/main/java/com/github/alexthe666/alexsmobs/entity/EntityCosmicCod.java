package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CosmicCodAIFollowLeader;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class EntityCosmicCod extends Mob implements Bucketable {

    private static final EntityDataAccessor<Float> FISH_PITCH = SynchedEntityData.defineId(EntityCosmicCod.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(EntityCosmicCod.class, EntityDataSerializers.BOOLEAN);
    public float prevFishPitch;
    private int baitballCooldown = 100 + random.nextInt(100);
    private int circleTime = 0;
    private int maxCircleTime = 300;
    private BlockPos circlePos;
    private int teleportIn;
    private EntityCosmicCod groupLeader;
    private int groupSize = 1;

    protected EntityCosmicCod(EntityType<? extends Mob> mob, Level level) {
        super(mob, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.moveControl = new FlightMoveController(this, 1F, false, true);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.cosmicCodSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.COSMIC_COD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.COSMIC_COD_HURT.get();
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new AISwimIdle(this));
        this.goalSelector.addGoal(1, new CosmicCodAIFollowLeader(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FISH_PITCH, 0F);
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean p_203706_1_) {
        this.entityData.set(FROM_BUCKET, p_203706_1_);
    }

    @Override
    @Nonnull
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    @Nonnull
    public ItemStack getBucketItemStack() {
        ItemStack stack = new ItemStack(AMItemRegistry.COSMIC_COD_BUCKET.get());
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        return stack;
    }

    @Override
    public void saveToBucketTag(@Nonnull ItemStack bucket) {
        if (this.hasCustomName()) {
            bucket.setHoverName(this.getCustomName());
        }
        CompoundTag platTag = new CompoundTag();
        this.addAdditionalSaveData(platTag);
        CompoundTag compound = bucket.getOrCreateTag();
        compound.put("CosmicCodData", platTag);
    }

    @Override
    public void loadFromBucketTag(@Nonnull CompoundTag compound) {
        if (compound.contains("CosmicCodData")) {
            this.readAdditionalSaveData(compound.getCompound("CosmicCodData"));
        }
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName() || this.fromBucket();
    }

    public boolean removeWhenFarAway(double p_213397_1_) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FromBucket", this.fromBucket());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFromBucket(compound.getBoolean("FromBucket"));
    }

    public boolean isSensitiveToWater() {
        return true;
    }

    private void doInitialPosing(LevelAccessor world, EntityCosmicCod.GroupData data) {
        BlockPos down = this.blockPosition();
        while(world.isEmptyBlock(down) && down.getY() > -62){
            down = down.below();
        }
        if(down.getY() <= -60){
            if(data != null && data.groupLeader != null){
                this.setPos(down.getX() + 0.5F, data.groupLeader.getY() - 1 + random.nextInt(1), down.getZ() + 0.5F);
            }else{
                this.setPos(down.getX() + 0.5F, down.getY() + 90 + random.nextInt(60), down.getZ() + 0.5F);
            }
        }else{
            this.setPos(down.getX() + 0.5F, down.getY() + 1, down.getZ() + 0.5F);
        }
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    public void tick() {
        super.tick();
        this.prevFishPitch = this.getFishPitch();
        if (!this.level().isClientSide) {
            final double ydist = (this.yo - this.getY());//down 0.4 up -0.38
            final float fishDist = (float) ((Math.abs(this.getDeltaMovement().x) + Math.abs(this.getDeltaMovement().z)) * 6F) / getPitchSensitivity();
            this.incrementFishPitch((float) (ydist) * 10 * getPitchSensitivity());
            this.setFishPitch(Mth.clamp(this.getFishPitch(), -60, 40));
            if (this.getFishPitch() > 2F) {
                this.decrementFishPitch(fishDist * Math.abs(this.getFishPitch()) / 90);
            }
            if (this.getFishPitch() < -2F) {
                this.incrementFishPitch(fishDist * Math.abs(this.getFishPitch()) / 90);
            }
            if (this.getFishPitch() > 2F) {
                this.decrementFishPitch(1);
            } else if (this.getFishPitch() < -2F) {
                this.incrementFishPitch(1);
            }
            if (baitballCooldown > 0) {
                baitballCooldown--;
            }
        }
        if(teleportIn > 0){
            teleportIn--;
            if(teleportIn == 0 && !this.level().isClientSide){
                final double range = 8;
                final AABB bb = new AABB(this.getX() - range, this.getY() - range, this.getZ() - range, this.getX() + range, this.getY() + range, this.getZ() + range);
                final List<EntityCosmicCod> list = this.level().getEntitiesOfClass(EntityCosmicCod.class, bb);
                final Vec3 vec3 = this.teleport();
                if (vec3 != null) {
                    baitballCooldown = 5;
                    for (final EntityCosmicCod cod : list) {
                        if (cod != this) {
                            cod.baitballCooldown = 5;
                            cod.teleport(vec3.x, vec3.y, vec3.z);
                        }
                    }
                }
            }
        }
    }

    public void handleEntityEvent(byte msg) {
        if (msg == 46) {
            this.gameEvent(GameEvent.TELEPORT);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
        super.handleEntityEvent(msg);
    }

    public void resetBaitballCooldown(){
        baitballCooldown = 120 + random.nextInt(100);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if(prev){
            teleportIn = 5;

        }
        return prev;
    }

    private float getPitchSensitivity() {
        return 3F;
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByWater() {
        return false;
    }

    public float getFishPitch() {
        return entityData.get(FISH_PITCH).floatValue();
    }

    public void setFishPitch(float pitch) {
        entityData.set(FISH_PITCH, pitch);
    }

    public void incrementFishPitch(float pitch) {
        entityData.set(FISH_PITCH, getFishPitch() + pitch);
    }

    public void decrementFishPitch(float pitch) {
        entityData.set(FISH_PITCH, getFishPitch() - pitch);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        final double x = pos.getX() + 0.5F;
        final double y = pos.getY() + 0.5F;
        final double z = pos.getZ() + 0.5F;
        final HitResult result = this.level().clip(new ClipContext(this.getEyePosition(), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        final double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

    protected Vec3 teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            final double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            final double d1 = this.getY() + (double) (this.random.nextInt(64) - 32);
            final double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            if (this.teleport(d0, d1, d2)) {
                this.circlePos = null;
                return new Vec3(d0, d1, d2);
            }
        }
        return null;
    }

    private boolean teleport(double x, double y, double z) {
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);
        final BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        final boolean flag = blockstate.isAir();
        if (flag && !blockstate.getFluidState().is(FluidTags.WATER)) {
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, x, y, z);
            if (event.isCanceled()) return false;
            level().broadcastEntityEvent(this, (byte) 46);
            this.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            return true;
        } else {
            return false;
        }
    }

    public void leaveGroup() {
        if(this.groupLeader != null){
            this.groupLeader.decreaseGroupSize();
        }
        this.groupLeader = null;
    }

    protected boolean hasNoLeader() {
        return !this.hasGroupLeader();
    }

    public boolean hasGroupLeader() {
        return this.groupLeader != null && this.groupLeader.isAlive();
    }

    private void increaseGroupSize() {
        ++this.groupSize;
    }

    private void decreaseGroupSize() {
        --this.groupSize;
    }

    public boolean canGroupGrow() {
        return this.isGroupLeader() && this.groupSize < this.getMaxGroupSize();
    }

    private int getMaxGroupSize() {
        return 15;
    }

    public int getMaxSpawnClusterSize() {
        return 7;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    public boolean isGroupLeader() {
        return this.groupSize > 1;
    }

    public boolean inRangeOfGroupLeader() {
        return this.distanceToSqr(this.groupLeader) <= 121.0D;
    }

    public void moveToGroupLeader() {
        if (this.hasGroupLeader()) {
            this.getMoveControl().setWantedPosition(this.groupLeader.getX(), this.groupLeader.getY(), this.groupLeader.getZ(), 1.0D);
        }

    }

    public EntityCosmicCod createAndSetLeader(EntityCosmicCod leader) {
        this.groupLeader = leader;
        leader.increaseGroupSize();
        return leader;
    }


    public void createFromStream(Stream<EntityCosmicCod> stream) {
        stream.limit(this.getMaxGroupSize() - this.groupSize).filter((fishe) -> {
            return fishe != this;
        }).forEach((fishe) -> {
            fishe.createAndSetLeader(this);
        });
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new EntityCosmicCod.GroupData(this);
        } else {
            this.createAndSetLeader(((EntityCosmicCod.GroupData) spawnDataIn).groupLeader);
        }
        if (reason == MobSpawnType.NATURAL && spawnDataIn instanceof EntityCosmicCod.GroupData) {
            doInitialPosing(worldIn, (GroupData) spawnDataIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean isCircling() {
        return circlePos != null && circleTime < maxCircleTime;
    }

    @Override
    @Nonnull
    protected InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.BUCKET && this.isAlive()) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(this.getPickupSound(), 1.0F, 1.0F);
            final ItemStack itemstack1 = this.getBucketItemStack();
            this.saveToBucketTag(itemstack1);
            final ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1, false);
            player.setItemInHand(hand, itemstack2);
            final Level level = this.level();
            if (!this.level().isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, itemstack1);
            }

            this.discard();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    public static class GroupData extends AgeableMob.AgeableMobGroupData {
        public final EntityCosmicCod groupLeader;

        public GroupData(EntityCosmicCod groupLeaderIn) {
            super(0.05F);
            this.groupLeader = groupLeaderIn;
        }
    }

    private static class AISwimIdle extends Goal {

        private final EntityCosmicCod cod;
        float circleDistance = 5;
        boolean clockwise = false;

        public AISwimIdle(EntityCosmicCod cod) {
            this.cod = cod;
        }

        @Override
        public boolean canUse() {
            return this.cod.isGroupLeader() || cod.hasNoLeader() || cod.hasGroupLeader() && cod.groupLeader.circlePos != null;
        }

        public void tick() {
            if(cod.circleTime > cod.maxCircleTime){
                cod.circleTime = 0;
                cod.circlePos = null;
            }
            if(cod.circlePos != null && cod.circleTime <= cod.maxCircleTime){
                cod.circleTime++;
                Vec3 movePos = getSharkCirclePos(cod.circlePos);
                cod.getMoveControl().setWantedPosition(movePos.x(), movePos.y(), movePos.z(), 1.0F);
            }else if (this.cod.isGroupLeader()) {
                if (cod.baitballCooldown == 0) {
                    cod.resetBaitballCooldown();
                    if (cod.circlePos == null || cod.circleTime >= cod.maxCircleTime) {
                        cod.circleTime = 0;
                        cod.maxCircleTime = 360 + this.cod.random.nextInt(80);
                        circleDistance = 1 + this.cod.random.nextFloat();
                        clockwise = this.cod.random.nextBoolean();
                        cod.circlePos = cod.blockPosition().above();
                    }
                }
            } else if (cod.random.nextInt(40) == 0 || cod.hasNoLeader()) {
                final Vec3 movepos = cod.position().add(cod.random.nextInt(4) - 2, cod.getY() < 0 ? 1 : cod.random.nextInt(4) - 2, cod.random.nextInt(4) - 2);
                cod.getMoveControl().setWantedPosition(movepos.x, movepos.y, movepos.z, 1.0F);
            } else if (cod.hasGroupLeader() && cod.groupLeader.circlePos != null) {
                if (cod.circlePos == null) {
                    cod.circlePos = cod.groupLeader.circlePos;
                    cod.circleTime = cod.groupLeader.circleTime;
                    cod.maxCircleTime = cod.groupLeader.maxCircleTime;
                    circleDistance = 1 + this.cod.random.nextFloat();
                    clockwise = this.cod.random.nextBoolean();
                }
            }
        }

        public Vec3 getSharkCirclePos(BlockPos target) {
            final float prog = 1F - (cod.circleTime / (float) cod.maxCircleTime);
            final float angle = (Maths.STARTING_ANGLE * 10 * (clockwise ? -cod.circleTime : cod.circleTime));
            final float circleDistanceTimesProg = circleDistance * prog;
            final double extraX = (circleDistanceTimesProg + 0.75F) * Mth.sin((angle));
            final double extraZ =  (circleDistanceTimesProg + 0.75F) * prog * Mth.cos(angle);
            return new Vec3(target.getX() + 0.5F + extraX, Math.max(target.getY() + cod.random.nextInt(4) - 2, -62), target.getZ() + 0.5F + extraZ);
        }
    }
}
