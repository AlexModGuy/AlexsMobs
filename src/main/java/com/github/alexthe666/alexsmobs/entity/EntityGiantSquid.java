package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class EntityGiantSquid extends WaterAnimal {

    private static final EntityDataAccessor<Float> SQUID_PITCH = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DEPRESSURIZATION = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> OVERRIDE_BODYROT = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GRABBING = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CAPTURED = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BLUE = SynchedEntityData.defineId(EntityGiantSquid.class, EntityDataSerializers.BOOLEAN);
    public final EntityGiantSquidPart mantlePart1;
    public final EntityGiantSquidPart mantlePart2;
    public final EntityGiantSquidPart mantlePart3;
    public final EntityGiantSquidPart tentaclesPart1;
    public final EntityGiantSquidPart tentaclesPart2;
    public final EntityGiantSquidPart tentaclesPart3;
    public final EntityGiantSquidPart tentaclesPart4;
    public final EntityGiantSquidPart tentaclesPart5;
    public final EntityGiantSquidPart tentaclesPart6;
    public final EntityGiantSquidPart mantleCollisionPart;
    public final EntityGiantSquidPart[] allParts;
    public final float[][] ringBuffer = new float[64][2];
    public int ringBufferIndex = -1;
    public float prevSquidPitch;
    public float prevDepressurization;
    public float grabProgress;
    public float prevGrabProgress;
    public float dryProgress;
    public float prevDryProgress;
    public float capturedProgress;
    public float prevCapturedProgress;
    public int humTick = 0;
    private int holdTime;
    private int resetCapturedStateIn;

    protected EntityGiantSquid(EntityType type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.mantlePart1 = new EntityGiantSquidPart(this, 0.9F, 0.9F);
        this.mantlePart2 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
        this.mantlePart3 = new EntityGiantSquidPart(this, 0.45F, 0.45F);
        this.tentaclesPart1 = new EntityGiantSquidPart(this, 0.9F, 0.9F);
        this.tentaclesPart2 = new EntityGiantSquidPart(this, 1F, 1F);
        this.tentaclesPart3 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
        this.tentaclesPart4 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
        this.tentaclesPart5 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
        this.tentaclesPart6 = new EntityGiantSquidPart(this, 1.2F, 1.2F);
        this.mantleCollisionPart = new EntityGiantSquidPart(this, 2.9F, 2.9F, true);
        this.allParts = new EntityGiantSquidPart[]{this.mantlePart1, this.mantlePart2, this.mantlePart3, this.mantleCollisionPart, this.tentaclesPart1, this.tentaclesPart2, this.tentaclesPart3, this.tentaclesPart4, this.tentaclesPart5, this.tentaclesPart6};
        this.lookControl = new SmoothSwimmingLookControl(this, 4);
        this.moveControl = new AquaticMoveController(this, 1.2F, 5);
    }


    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.giantSquidSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canGiantSquidSpawn(EntityType<EntityGiantSquid> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, Random random) {
        return reason == MobSpawnType.SPAWNER || iServerWorld.getBlockState(pos).getMaterial() == Material.WATER && iServerWorld.getBlockState(pos.above()).getMaterial() == Material.WATER;
    }


    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            doInitialPosing(worldIn);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.blockPosition();
        while(!world.getFluidState(down).isEmpty() && down.getY() > 1){
            down = down.below();
        }
        this.setPos(down.getX() + 0.5F, down.getY() + 3 + random.nextInt(3), down.getZ() + 0.5F);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GIANT_SQUID_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GIANT_SQUID_HURT;
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 38.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SQUID_PITCH, 0F);
        this.entityData.define(OVERRIDE_BODYROT, false);
        this.entityData.define(DEPRESSURIZATION, 0F);
        this.entityData.define(GRABBING, false);
        this.entityData.define(CAPTURED, false);
        this.entityData.define(BLUE, false);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        return super.mobInteract(player, hand);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation(this, worldIn);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new AIAvoidWhales());
        this.goalSelector.addGoal(2, new AIMelee());
        this.goalSelector.addGoal(3, new AIDeepwaterSwimming());
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, EntityCachalotWhale.class)));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Guardian.class, 20, true, true, null) {
            public boolean canUse() {
                return super.canUse();
            }
        });
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 70, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getAllTags().getTag(AMTagRegistry.GIANT_SQUID_TARGETS))) {
            public boolean canUse() {
                return  !EntityGiantSquid.this.isInWaterOrBubble() && !EntityGiantSquid.this.isCaptured() && super.canUse();
            }
        });
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isNoAi()) {
            if (this.ringBufferIndex < 0) {
                //initial population of buffer
                for (int i = 0; i < this.ringBuffer.length; ++i) {
                    this.ringBuffer[i][0] = 180 + this.getYRot();
                    this.ringBuffer[i][1] = this.getSquidPitch();
                }
            }
            this.ringBufferIndex++;
            if (this.ringBufferIndex == this.ringBuffer.length) {
                this.ringBufferIndex = 0;
            }
            this.ringBuffer[this.ringBufferIndex][0] = this.yBodyRot;
            this.ringBuffer[this.ringBufferIndex][1] = this.getSquidPitch();
        }
    }

    public void tick() {
        super.tick();
        if(this.tickCount % 100 == 0){
            this.heal(2);
        }
        float f = Mth.wrapDegrees(180 + this.getYRot());
        this.yBodyRot = rotlerp(this.yBodyRot, f, 180);
        prevSquidPitch = getSquidPitch();
        prevDepressurization = getDepressurization();
        prevDryProgress = dryProgress;
        prevGrabProgress = grabProgress;
        prevCapturedProgress = capturedProgress;
        if (!this.isInWater() && dryProgress < 5F) {
            dryProgress++;
        }
        if (this.isInWater() && dryProgress > 0F) {
            dryProgress--;
        }
        if (this.isGrabbing() && grabProgress < 5F) {
            grabProgress += 0.25F;
        }
        if (!this.isGrabbing() && grabProgress > 0F) {
            grabProgress -= 0.25F;
        }
        if (this.isCaptured() && capturedProgress < 5F) {
            capturedProgress += 0.5F;
        }
        if (!this.isCaptured() && capturedProgress > 0F) {
            capturedProgress -= 0.5F;
        }
        if (this.isGrabbing()) {
            if (!level.isClientSide && this.getTarget() != null && this.getTarget().isAlive()) {
                this.setXRot(0);
                float invert = 1F - grabProgress * 0.2F;
                float radius = 1.0F + this.getTarget().getBbWidth() * 0.5F;
                Vec3 extraVec = new Vec3(0, 0, 2F + invert * 7F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                Vec3 minus = new Vec3(this.getX() + extraVec.x - this.getTarget().getX(), this.getY() + extraVec.y - this.getTarget().getY(), this.getZ() + extraVec.z - this.getTarget().getZ());
                this.getTarget().setDeltaMovement(minus);
                if (holdTime % 20 == 0 && holdTime > 30) {
                    this.getTarget().hurt(DamageSource.mobAttack(this), 3 + random.nextInt(5));
                }
            }
            holdTime++;
            if (holdTime > 1000) {
                holdTime = 0;
                this.setGrabbing(false);
            }
        } else {
            holdTime = 0;
        }
        if (!this.isNoAi()) {
            Vec3[] avector3d = new Vec3[this.allParts.length];
            for (int j = 0; j < this.allParts.length; ++j) {
                this.allParts[j].collideWithNearbyEntities();
                avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
            }
            float yaw = this.getYRot() * ((float) Math.PI / 180F);
            float pitch = this.getXRot() * ((float) Math.PI / 180F) * 0.8F;
            this.mantleCollisionPart.setPos(this.getX(), this.getY() - ((this.mantleCollisionPart.getBbHeight() - this.getEyeHeight()) * 0.5F) * (1F - dryProgress * 0.2F), this.getZ());
            this.setPartPositionFromBuffer(this.mantlePart1, pitch, 0.9F, 0);
            this.setPartPositionFromBuffer(this.mantlePart2, pitch, 1.6F, 0);
            this.setPartPositionFromBuffer(this.mantlePart3, pitch, 2.45F, 0);
            this.setPartPositionFromBuffer(this.tentaclesPart1, pitch, -0.8F, 0);
            this.setPartPositionFromBuffer(this.tentaclesPart2, pitch, -1.5F, 0);
            this.setPartPositionFromBuffer(this.tentaclesPart3, pitch, -2.3F, 5);
            this.setPartPositionFromBuffer(this.tentaclesPart4, pitch, -3.4F, 10);
            this.setPartPositionFromBuffer(this.tentaclesPart5, pitch, -5.4F, 15);
            this.setPartPositionFromBuffer(this.tentaclesPart6, pitch, -7.4F, 20);
            if (this.isInWaterOrBubble()) {
                if (this.mantleCollisionPart.scale != 1F) {
                    this.mantleCollisionPart.scale = 1F;
                    this.mantleCollisionPart.refreshDimensions();
                }
            } else {
                if (this.mantleCollisionPart.scale != 0.25F) {
                    this.mantleCollisionPart.scale = 0.25F;
                    this.mantleCollisionPart.refreshDimensions();
                }
            }
            for (int l = 0; l < this.allParts.length; ++l) {
                this.allParts[l].xo = avector3d[l].x;
                this.allParts[l].yo = avector3d[l].y;
                this.allParts[l].zo = avector3d[l].z;
                this.allParts[l].xOld = avector3d[l].x;
                this.allParts[l].yOld = avector3d[l].y;
                this.allParts[l].zOld = avector3d[l].z;
            }
            this.setNoGravity(this.isInWater());
        }
        if (!level.isClientSide) {
            if (this.getSquidPitch() > 0F) {
                float decrease = Math.min(2F, this.getSquidPitch());
                this.decrementSquidPitch(decrease);
            }
            if (this.getSquidPitch() < 0F) {
                float decrease = Math.min(2F, -this.getSquidPitch());
                this.incrementSquidPitch(decrease);
            }
            if (this.isInWaterOrBubble()) {
                float dist = (float) this.getDeltaMovement().y() * 45;
                if (entityData.get(OVERRIDE_BODYROT)) {
                    this.decrementSquidPitch(dist);
                } else {
                    this.incrementSquidPitch(dist);
                }
            }
            if (!this.isOnGround() && this.getFluidHeight(FluidTags.WATER) < this.getBbHeight()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.1F, 0));
            }
            float pressure = getDepressureLevel();
            if (this.getDepressurization() < pressure) {
                this.setDepressurization(this.getDepressurization() + 0.1F);
            }
            if (this.getDepressurization() > pressure) {
                this.setDepressurization(this.getDepressurization() - 0.1F);
            }
        }
        if (this.isHumming()) {
            if (humTick % 20 == 0) {
                this.playSound(AMSoundRegistry.GIANT_SQUID_GAMES, this.getSoundVolume(), 1);
                humTick = 0;
            }
            humTick++;
        }
        if(!level.isClientSide){
            if(resetCapturedStateIn > 0){
                resetCapturedStateIn--;
            }else{
                this.setCaptured(false);
            }
        }
    }

    private boolean isHumming() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && s.toLowerCase().contains("squid games!!");
    }

    public float getRingBuffer(int bufferOffset, float partialTicks, boolean pitch) {
        int i = (this.ringBufferIndex - bufferOffset) & 63;
        int j = (this.ringBufferIndex - bufferOffset - 1) & 63;
        int k = pitch ? 1 : 0;
        float prevBuffer = this.ringBuffer[j][k];
        float buffer = this.ringBuffer[i][k];
        float end = prevBuffer + (buffer - prevBuffer) * partialTicks;
        return rotlerp(prevBuffer, end, 10);
    }

    private void setPartPosition(EntityGiantSquidPart part, double offsetX, double offsetY, double offsetZ, float offsetScale) {
        part.setPos(this.getX() + offsetX * offsetScale * part.scale, this.getY() + offsetY * offsetScale * part.scale, this.getZ() + offsetZ * offsetScale * part.scale);
    }

    private void setPartPositionFromBuffer(EntityGiantSquidPart part, float pitch, float offsetScale, int ringBufferOffset) {
        float f2 = Mth.sin(getRingBuffer(ringBufferOffset, 1.0F, false) * ((float) Math.PI / 180F)) * (1 - Math.abs((this.getXRot()) / 90F));
        float f3 = Mth.cos(getRingBuffer(ringBufferOffset, 1.0F, false) * ((float) Math.PI / 180F)) * (1 - Math.abs((this.getXRot()) / 90F));
        setPartPosition(part, f2, pitch, -f3, offsetScale);
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 3;
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            if (entityData.get(OVERRIDE_BODYROT)) {
                travelVector = new Vec3(travelVector.x, travelVector.y, -travelVector.z);
            }
            this.moveRelative(this.getSpeed(), travelVector);
            double d = this.getTarget() == null ? 0.6D : 0.9D;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, d, 0.9D));
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            super.travel(travelVector);
        }
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

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setBlue(compound.getBoolean("Blue"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Blue", isBlue());
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public float getDepressurization() {
        return Mth.clamp(entityData.get(DEPRESSURIZATION).floatValue(), 0, 1F);
    }

    public void setDepressurization(float depressurization) {
        entityData.set(DEPRESSURIZATION, depressurization);
    }

    public float getSquidPitch() {
        return Mth.clamp(entityData.get(SQUID_PITCH).floatValue(), -90, 90);
    }

    public void setSquidPitch(float pitch) {
        entityData.set(SQUID_PITCH, pitch);
    }

    public void incrementSquidPitch(float pitch) {
        entityData.set(SQUID_PITCH, getSquidPitch() + pitch);
    }

    public void decrementSquidPitch(float pitch) {
        entityData.set(SQUID_PITCH, getSquidPitch() - pitch);
    }

    public boolean isGrabbing() {
        return this.entityData.get(GRABBING).booleanValue();
    }

    public void setGrabbing(boolean running) {
        this.entityData.set(GRABBING, Boolean.valueOf(running));
    }

    public boolean isCaptured() {
        return this.entityData.get(CAPTURED).booleanValue();
    }

    public void setCaptured(boolean running) {
        this.entityData.set(CAPTURED, Boolean.valueOf(running));
    }

    public boolean isBlue() {
        return this.entityData.get(BLUE).booleanValue();
    }

    public void setBlue(boolean t) {
        this.entityData.set(BLUE, Boolean.valueOf(t));
    }

    public void push(Entity entity) {
        if (!this.isCaptured()) {
            super.push(entity);
        }
    }

    @Override
    public void calculateEntityAnimation(LivingEntity entity, boolean flying) {
        entity.animationSpeedOld = entity.animationSpeed;
        double d0 = entity.getX() - entity.xo;
        double d1 = entity.getY() - entity.yo;
        double d2 = entity.getZ() - entity.zo;
        float f = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2)) * 8.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        entity.animationSpeed += (f - entity.animationSpeed) * 0.4F;
        entity.animationPosition += entity.animationSpeed;
    }

    public Vec3 collide(Vec3 movement) {
        if (touchingUnloadedChunk() || !this.isInWaterOrBubble()) {
            return super.collide(movement);
        } else {
            AABB aabb = this.mantleCollisionPart.getBoundingBox();
            List<VoxelShape> list = this.level.getEntityCollisions(this, aabb.expandTowards(movement));
            Vec3 vec3 = movement.lengthSqr() == 0.0D ? movement : collideBoundingBox(this, movement, aabb, this.level, list);
            boolean flag = movement.x != vec3.x;
            boolean flag1 = movement.y != vec3.y;
            boolean flag2 = movement.z != vec3.z;
            boolean flag3 = this.onGround || flag1 && movement.y < 0.0D;
            if (this.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
                Vec3 vec31 = collideBoundingBox(this, new Vec3(movement.x, this.maxUpStep, movement.z), aabb, this.level, list);
                Vec3 vec32 = collideBoundingBox(this, new Vec3(0.0D, this.maxUpStep, 0.0D), aabb.expandTowards(movement.x, 0.0D, movement.z), this.level, list);
                if (vec32.y < (double) this.maxUpStep) {
                    Vec3 vec33 = collideBoundingBox(this, new Vec3(movement.x, 0.0D, movement.z), aabb.move(vec32), this.level, list).add(vec32);
                    if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                        vec31 = vec33;
                    }
                }

                if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                    return vec31.add(collideBoundingBox(this, new Vec3(0.0D, -vec31.y + movement.y, 0.0D), aabb.move(vec31), this.level, list));
                }
            }

            return vec3;
        }
    }

    public float getXRot() {
        return getSquidPitch();
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.allParts;
    }

    public boolean attackEntityPartFrom(EntityGiantSquidPart part, DamageSource source, float amount) {
        return this.hurt(source, amount);
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || super.isInvulnerableTo(source);
    }

    public void directPitch(double d0, double d1, double d2, double d3) {
        boolean shift = this.entityData.get(OVERRIDE_BODYROT);
        float add = shift ? 90.0F : -90.0F;
        float f = (float) (Mth.atan2(d2, d0) * 57.2957763671875D) + add;
        this.setYRot(this.rotlerp(this.getYRot(), f, shift ? 10 : 5));
    }

    public float getViewXRot(float partialTick) {
        return prevSquidPitch + (getSquidPitch() - prevSquidPitch) * partialTick;
    }

    public float getViewYRot(float partialTick) {
        return partialTick == 1.0F ? this.yBodyRot : Mth.lerp(partialTick, this.yBodyRotO, this.yBodyRot);
    }

    protected float rotlerp(float in, float target, float maxShift) {
        float f = Mth.wrapDegrees(target - in);
        if (f > maxShift) {
            f = maxShift;
        }

        if (f < -maxShift) {
            f = -maxShift;
        }

        float f1 = in + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }

    private float getDepressureLevel() {
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        int waterLevelAbove = 0;
        while (waterLevelAbove < 10) {
            BlockState blockstate = level.getBlockState(blockpos$mutable.set(this.getX(), this.getY() + waterLevelAbove, this.getZ()));
            if (!blockstate.getFluidState().is(FluidTags.WATER) && !blockstate.getMaterial().isSolid()) {
                break;
            } else {
                waterLevelAbove++;
            }
        }
        return 1F - (waterLevelAbove / 10F);
    }


    private boolean canFitAt(BlockPos pos) {
        return true;
    }

    public boolean tickCaptured(EntityCachalotWhale whale) {
        resetCapturedStateIn = 25;
        if (random.nextInt(13) == 0) {
            spawnInk();
            whale.hurt(DamageSource.mobAttack(this), 4 + random.nextInt(4));
            if (random.nextFloat() <= 0.3F) {
                this.setCaptured(false);
                if(random.nextFloat() < 0.4F){
                    this.spawnAtLocation(AMItemRegistry.LOST_TENTACLE);
                }
                return true;
            }
        }
        this.setCaptured(true);
        this.setSquidPitch(0);
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);

    }

    public boolean hurt(DamageSource src, float f) {
        if (super.hurt(src, f) && this.getLastHurtByMob() != null && !this.isCaptured() && random.nextBoolean()) {
            this.spawnInk();
            return true;
        } else {
            return false;
        }
    }

    private void spawnInk() {
        this.playSound(SoundEvents.SQUID_SQUIRT, this.getSoundVolume(), 0.5F * this.getVoicePitch());
        if (!level.isClientSide) {
            Vec3 inkDirection = new Vec3(0, 0, 1.2F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
            Vec3 vec3 = this.position().add(inkDirection);
            for (int i = 0; i < 30; ++i) {
                Vec3 vec32 = inkDirection.add(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).scale(0.8D + (double) (this.random.nextFloat() * 2.0F));
                ((ServerLevel) this.level).sendParticles(ParticleTypes.SQUID_INK, vec3.x, vec3.y + 0.5D, vec3.z, 0, vec32.x, vec32.y, vec32.z, 0.1F);
            }
        }
    }

    private class AIAvoidWhales extends Goal {

        private EntityCachalotWhale whale;
        private Vec3 moveTo;
        private int runDelay;

        public AIAvoidWhales() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (EntityGiantSquid.this.isInWaterOrBubble() && !EntityGiantSquid.this.horizontalCollision && !EntityGiantSquid.this.isCaptured() && runDelay-- <= 0) {
                EntityCachalotWhale closest = null;
                float dist = 50;
                for (EntityCachalotWhale dude : EntityGiantSquid.this.level.getEntitiesOfClass(EntityCachalotWhale.class, EntityGiantSquid.this.getBoundingBox().inflate(dist))) {
                    if (closest == null || dude.distanceTo(EntityGiantSquid.this) < closest.distanceTo(EntityGiantSquid.this)) {
                        closest = dude;
                    }
                }
                if (closest != null) {
                    whale = closest;
                    return true;
                }
                runDelay = 50 + random.nextInt(50);
            }

            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return whale != null && whale.isAlive() && !EntityGiantSquid.this.horizontalCollision && EntityGiantSquid.this.distanceTo(whale) < 60;
        }

        public void tick() {
            if (whale != null && whale.isAlive()) {
                double dist = EntityGiantSquid.this.distanceTo(whale);
                Vec3 vec = EntityGiantSquid.this.position().subtract(whale.position()).normalize();
                Vec3 vec2 = EntityGiantSquid.this.position().add(vec.scale(12 + random.nextInt(5)));
                EntityGiantSquid.this.getNavigation().moveTo(vec2.x, vec2.y, vec2.z, dist < 20 ? 1.9F : 1.3F);
            }
        }

        public void stop() {
            whale = null;
            moveTo = null;
        }
    }

    private class AIDeepwaterSwimming extends Goal {

        private BlockPos moveTo;

        public AIDeepwaterSwimming() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (EntityGiantSquid.this.isVehicle() || EntityGiantSquid.this.getTarget() != null && !EntityGiantSquid.this.isGrabbing() || !EntityGiantSquid.this.isInWater() && !EntityGiantSquid.this.isInLava()) {
                return false;
            } else {
                if (EntityGiantSquid.this.getNavigation().isDone() || EntityGiantSquid.this.getRandom().nextInt(30) == 0) {
                    BlockPos found = findTargetPos();
                    if (found != null) {
                        moveTo = found;
                        return true;
                    }
                }
                return false;
            }
        }

        private BlockPos findTargetPos() {
            Random r = EntityGiantSquid.this.getRandom();
            for (int i = 0; i < 15; i++) {
                BlockPos pos = EntityGiantSquid.this.blockPosition().offset(r.nextInt(16) - 8, r.nextInt(32) - 16, r.nextInt(16) - 8);
                if (EntityGiantSquid.this.level.isWaterAt(pos) && EntityGiantSquid.this.canFitAt(pos)) {
                    return getDeeperTarget(pos);
                }
            }
            return null;
        }

        private BlockPos getDeeperTarget(BlockPos waterAtPos){
            BlockPos surface = new BlockPos(waterAtPos);
            BlockPos seafloor = new BlockPos(waterAtPos);
            while (EntityGiantSquid.this.level.isWaterAt(surface) && surface.getY() < 320){
                surface = surface.above();
            }
            while (EntityGiantSquid.this.level.isWaterAt(seafloor) && seafloor.getY() > -64){
                seafloor = seafloor.below();
            }
            int distance = surface.getY() - seafloor.getY();
            if(distance < 10){
                return waterAtPos;
            }else{
                int i = (int) (distance * 0.4);
                return seafloor.above(1 + EntityGiantSquid.this.getRandom().nextInt(i));
            }
        }

        public void start() {
            EntityGiantSquid.this.getNavigation().moveTo(moveTo.getX() + 0.5F, moveTo.getY() + 0.5F, moveTo.getZ() + 0.5F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }

    private class AIMelee extends Goal {

        @Override
        public boolean canUse() {
            return EntityGiantSquid.this.isInWaterOrBubble() && EntityGiantSquid.this.getTarget() != null && EntityGiantSquid.this.getTarget().isAlive();
        }

        public void tick() {
            EntityGiantSquid squid = EntityGiantSquid.this;
            LivingEntity target = EntityGiantSquid.this.getTarget();
            double dist = squid.distanceTo(target);
            if (squid.hasLineOfSight(target) && dist < 7.0F) {
                squid.setGrabbing(true);
            } else {
                Vec3 moveBodyTo = target.position();
                squid.getNavigation().moveTo(moveBodyTo.x, moveBodyTo.y, moveBodyTo.z, 1.0F);
            }
            if (dist < 14.0F) {
                squid.entityData.set(OVERRIDE_BODYROT, true);
            } else {
                squid.entityData.set(OVERRIDE_BODYROT, false);
            }
        }

        @Override
        public void stop() {
            EntityGiantSquid.this.entityData.set(OVERRIDE_BODYROT, false);
            EntityGiantSquid.this.setGrabbing(false);
        }
    }
}
