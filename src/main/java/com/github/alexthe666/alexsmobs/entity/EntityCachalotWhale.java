package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFollowParentRanged;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityCachalotWhale extends AnimalEntity {

    private static final EntityPredicate REWARD_PLAYER_PREDICATE = (new EntityPredicate()).setDistance(50.0D).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks();
    private static final DataParameter<Boolean> CHARGING = EntityDataManager.createKey(EntityCachalotWhale.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(EntityCachalotWhale.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BEACHED = EntityDataManager.createKey(EntityCachalotWhale.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALBINO = EntityDataManager.createKey(EntityCachalotWhale.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DESPAWN_BEACH = EntityDataManager.createKey(EntityCachalotWhale.class, DataSerializers.BOOLEAN);
    public final double[][] ringBuffer = new double[64][3];
    public final EntityCachalotPart headPart;
    public final EntityCachalotPart bodyFrontPart;
    public final EntityCachalotPart bodyPart;
    public final EntityCachalotPart tail1Part;
    public final EntityCachalotPart tail2Part;
    public final EntityCachalotPart tail3Part;
    public final EntityCachalotPart[] whaleParts;
    public int ringBufferIndex = -1;
    public float prevChargingProgress;
    public float chargeProgress;
    public float prevSleepProgress;
    public float sleepProgress;
    public float prevBeachedProgress;
    public float beachedProgress;
    private boolean receivedEcho = false;
    private boolean waitForEchoFlag = true;
    private int echoTimer = 0;
    private boolean prevEyesInWater = false;
    private int spoutTimer = 0;
    private int chargeCooldown = 0;
    private float whaleSpeedMod = 1F;
    private int rewardTime = 0;
    private PlayerEntity rewardPlayer;
    private int blockBreakCounter;
    private int despawnDelay = 47999;
    private int ambergrisDrops = 0;
    private boolean hasAlbinoAttribute = false;
    private int echoSoundCooldown = 0;

    public EntityCachalotWhale(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.moveController = new MoveHelperController(this);
        this.lookController = new DolphinLookController(this, 4);
        this.headPart = new EntityCachalotPart(this, 3.0F, 3.5F);
        this.bodyFrontPart = new EntityCachalotPart(this, 4.0F, 4.0F);
        this.bodyPart = new EntityCachalotPart(this, 5.0F, 4.0F);
        this.tail1Part = new EntityCachalotPart(this, 4.0F, 3.0F);
        this.tail2Part = new EntityCachalotPart(this, 3.0F, 2.0F);
        this.tail3Part = new EntityCachalotPart(this, 3.0F, 0.7F);
        this.whaleParts = new EntityCachalotPart[]{this.headPart, this.bodyFrontPart, this.bodyPart, this.tail1Part, this.tail2Part, this.tail3Part};
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 160.0D).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 1.2F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 30F);
    }

    public static <T extends MobEntity> boolean canCachalotWhaleSpawn(EntityType<T> entityType, IServerWorld iServerWorld, SpawnReason reason, BlockPos pos, Random random) {
        return iServerWorld.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    public boolean canDespawn(double distanceToClosestPlayer) {
        return !this.isSleeping() && !this.isCharging() && !this.isDespawnBeach() && !isAlbino();
    }

    private boolean canDespawn() {
        return isDespawnBeach();
    }

    private void tryDespawn() {
        if (this.canDespawn()) {
            this.despawnDelay = this.despawnDelay - 1;
            if (this.despawnDelay <= 0) {
                this.clearLeashed(true, false);
                this.remove();
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.CACHALOT_WHALE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CACHALOT_WHALE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CACHALOT_WHALE_HURT;
    }

    public void scaleParts() {
        for (EntityCachalotPart parts : whaleParts) {
            float prev = parts.scale;
            parts.scale = this.isChild() ? 0.5F : 1F;
            if (prev != parts.scale) {
                parts.recalculateSize();
            }
        }
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public void collideWithNearbyEntities() {
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        return super.func_230254_b_(p_230254_1_, p_230254_2_);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Albino", this.isAlbino());
        compound.putBoolean("Beached", this.isBeached());
        compound.putBoolean("BeachedDespawnFlag", this.isDespawnBeach());
        compound.putInt("DespawnDelay", this.despawnDelay);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setAlbino(compound.getBoolean("Albino"));
        this.setBeached(compound.getBoolean("Beached"));
        this.setDespawnBeach(compound.getBoolean("BeachedDespawnFlag"));
        if (compound.contains("DespawnDelay", 99)) {
            this.despawnDelay = compound.getInt("DespawnDelay");
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CHARGING, Boolean.valueOf(false));
        this.dataManager.register(SLEEPING, Boolean.valueOf(false));
        this.dataManager.register(BEACHED, Boolean.valueOf(false));
        this.dataManager.register(ALBINO, Boolean.valueOf(false));
        this.dataManager.register(DESPAWN_BEACH, Boolean.valueOf(false));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AIBreathe());
        this.goalSelector.addGoal(1, new FindWaterGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AnimalAIFollowParentRanged(this, 1.1F, 32, 10));
        this.goalSelector.addGoal(4, new AnimalAIRandomSwimming(this, 0.6D, 35, 24, true) {
            public boolean shouldExecute() {
                return !EntityCachalotWhale.this.isSleeping() && !EntityCachalotWhale.this.isBeached() && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 20.0F));
        this.goalSelector.addGoal(7, new FollowBoatGoal(this));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this).setCallsForHelp()));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 30, false, true, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.CACHALOT_WHALE_TARGETS))) {
            public boolean shouldExecute() {
                return !EntityCachalotWhale.this.isSleeping() && !EntityCachalotWhale.this.isBeached() && super.shouldExecute();
            }
        });
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new SwimmerPathNavigator(this, worldIn);
    }

    public void updateAITasks() {
        super.updateAITasks();
        breakBlock();
    }

    public void breakBlock() {
        if (this.blockBreakCounter > 0) {
            --this.blockBreakCounter;
            return;
        }
        boolean flag = false;
        ResourceLocation breakables = this.isCharging() && this.getAttackTarget() != null ? AMTagRegistry.CACHALOT_WHALE_BREAKABLES : AMTagRegistry.ORCA_BREAKABLES;
        if (!world.isRemote && this.blockBreakCounter == 0 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this)) {
            for (int a = (int) Math.round(this.getBoundingBox().minX); a <= (int) Math.round(this.getBoundingBox().maxX); a++) {
                for (int b = (int) Math.round(this.getBoundingBox().minY) - 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 1) && (b <= 127); b++) {
                    for (int c = (int) Math.round(this.getBoundingBox().minZ); c <= (int) Math.round(this.getBoundingBox().maxZ); c++) {
                        BlockPos pos = new BlockPos(a, b, c);
                        BlockState state = world.getBlockState(pos);
                        FluidState fluidState = world.getFluidState(pos);
                        Block block = state.getBlock();
                        if (!state.isAir() && !state.getShape(world, pos).isEmpty() && BlockTags.getCollection().get(breakables).contains(state.getBlock()) && fluidState.isEmpty()) {
                            if (block != Blocks.AIR) {
                                this.setMotion(this.getMotion().mul(0.6F, 1, 0.6F));
                                flag = true;
                                world.destroyBlock(pos, true);
                                if (state.getBlock().isIn(BlockTags.ICE)) {
                                    world.setBlockState(pos, Blocks.WATER.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (flag) {
            blockBreakCounter = this.isCharging() && this.getAttackTarget() != null ? 2 : 20;
        }
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));

        } else {
            super.travel(travelVector);
        }
    }

    private void spawnSpoutParticles() {
        if (this.isAlive()) {
            for (int j = 0; j < 5 + rand.nextInt(4); ++j) {
                float radius = this.headPart.getWidth() * 0.5F;
                float angle = (0.01745329251F * this.renderYawOffset);
                double extraX = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.sin((float) (Math.PI + angle)) + (rand.nextFloat() - 0.5F) + this.getMotion().x * 2F;
                double extraZ = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.cos(angle) + (rand.nextFloat() - 0.5F) + this.getMotion().z * 2F;
                double motX = this.rand.nextGaussian();
                double motZ = this.rand.nextGaussian();
                this.world.addParticle(AMParticleRegistry.WHALE_SPLASH, this.headPart.getPosX() + extraX, this.headPart.getPosY() + this.headPart.getHeight(), this.headPart.getPosZ() + extraZ, motX * 0.1F + this.getMotion().x, 2F, motZ * 0.1F + this.getMotion().z);
            }
        }
    }

    public boolean isCharging() {
        return this.dataManager.get(CHARGING).booleanValue();
    }

    public void setCharging(boolean charging) {
        this.dataManager.set(CHARGING, Boolean.valueOf(charging));
    }

    public boolean isSleeping() {
        return this.dataManager.get(SLEEPING).booleanValue();
    }

    public void setSleeping(boolean charging) {
        this.dataManager.set(SLEEPING, Boolean.valueOf(charging));
    }

    public boolean isBeached() {
        return this.dataManager.get(BEACHED).booleanValue();
    }

    public void setBeached(boolean charging) {
        this.dataManager.set(BEACHED, Boolean.valueOf(charging));
    }

    public boolean isAlbino() {
        return this.dataManager.get(ALBINO).booleanValue();
    }

    public void setAlbino(boolean albino) {
        boolean prev = isAlbino();
        if (!prev && albino) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(230.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(45.0D);
            this.setHealth(160.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(160.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30.0D);
        }
        this.dataManager.set(ALBINO, Boolean.valueOf(albino));
    }

    public boolean isDespawnBeach() {
        return this.dataManager.get(DESPAWN_BEACH).booleanValue();
    }

    public void setDespawnBeach(boolean despawn) {
        this.dataManager.set(DESPAWN_BEACH, Boolean.valueOf(despawn));
    }

    protected float getSoundVolume() {
        return this.isSilent() ? 0 : (float) AMConfig.cachalotVolume;
    }

    public void livingTick() {
        super.livingTick();
        scaleParts();
        if (echoSoundCooldown > 0) {
            echoSoundCooldown--;
        }
        if (this.isSleeping()) {
            this.getNavigator().clearPath();
            this.rotationPitch = -90;
            this.whaleSpeedMod = 0;
            if (this.areEyesInFluid(FluidTags.WATER) && this.getAir() < 200) {
                this.setMotion(this.getMotion().add(0, 0.06, 0));
            } else {
                BlockPos waterPos = this.getPosition();
                while (world.getFluidState(waterPos).isTagged(FluidTags.WATER) && waterPos.getY() < 255) {
                    waterPos = waterPos.up();
                }
                if (waterPos.getY() - this.getPosY() < (isChild() ? 7 : 12)) {
                    this.setMotion(this.getMotion().add(0, -0.06, 0));
                }
                if (rand.nextInt(100) == 0) {
                    this.setMotion(this.getMotion().add(0, rand.nextGaussian() * 0.06, 0));
                }
            }
        } else {
            if (this.whaleSpeedMod == 0) {
                this.whaleSpeedMod = 1;
            }
        }
        float rPitch = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
        this.rotationPitch = MathHelper.clamp(rPitch, -90, 90);
        if (this.isOnGround() && !this.isInWaterOrBubbleColumn()) {
            this.setBeached(true);
            this.rotationPitch = 0;
            this.setSleeping(false);
        }
        if (this.isBeached()) {
            this.whaleSpeedMod = 0;
            this.setMotion(this.getMotion().mul(0.5, 0.5, 0.5));
            if (this.areEyesInFluid(FluidTags.WATER)) {
                PlayerEntity entity = this.world.getClosestPlayer(REWARD_PLAYER_PREDICATE, this);
                if (this.getRevengeTarget() != entity) {
                    rewardTime = 15;
                    rewardPlayer = entity;
                }
                this.despawnDelay = 47999;
                this.setBeached(false);
            }
        }
        if (!this.isBeached() && rewardTime > 0) {
            float dif = 0;
            if (rewardPlayer != null) {
                double d0 = rewardPlayer.getPosX() - this.getPosX();
                double d1 = rewardPlayer.getPosYEye() - this.getPosYEye();
                double d2 = rewardPlayer.getPosZ() - this.getPosZ();
                double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
                float targetYaw = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                float targetPitch = (float) (-(MathHelper.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
                this.rotationYaw = (this.rotationYaw + MathHelper.clamp(targetYaw - this.rotationYaw, -2, 2));
                this.rotationPitch = (this.rotationPitch + MathHelper.clamp(targetPitch - this.rotationPitch, -2, 2));
                this.renderYawOffset = rotationYaw;
                dif = Math.abs(MathHelper.wrapDegrees(targetYaw) - MathHelper.wrapDegrees(this.rotationYaw));
            }
            if (dif < 5) {
                if (rewardTime % 5 == 0 && ambergrisDrops < 2 + rand.nextInt(1) && this.isDespawnBeach()) {
                    ambergrisDrops++;
                    if (!world.isRemote) {
                        Vector3d vec = this.getMouthVec();
                        ItemEntity itementity = new ItemEntity(this.world, vec.x, vec.y, vec.z, new ItemStack(AMItemRegistry.AMBERGRIS));
                        itementity.setDefaultPickupDelay();
                        world.addEntity(itementity);
                    }
                }
                this.rewardTime--;
            }
            if (rewardTime <= 2) {
                this.setDespawnBeach(false);
                this.setCharging(false);
                this.whaleSpeedMod = 1F;
            } else {
                this.setCharging(true);
                this.whaleSpeedMod = 0.2F;
            }
        }

        prevChargingProgress = chargeProgress;
        prevSleepProgress = sleepProgress;
        prevBeachedProgress = beachedProgress;
        if (isCharging() && this.chargeProgress < 10F) {
            this.chargeProgress++;
        }
        if (!isCharging() && this.chargeProgress > 0F) {
            this.chargeProgress--;
        }
        if (isSleeping() && this.sleepProgress < 10F) {
            this.sleepProgress++;
        }
        if (!isSleeping() && this.sleepProgress > 0F) {
            this.sleepProgress--;
        }
        if (isBeached() && this.beachedProgress < 10F) {
            this.beachedProgress++;
        }
        if (!isBeached() && this.beachedProgress > 0F) {
            this.beachedProgress--;
        }
        this.rotationYawHead = this.rotationYaw;
        this.renderYawOffset = this.rotationYaw;

        if (!this.isAIDisabled()) {
            if (this.ringBufferIndex < 0) {
                for (int i = 0; i < this.ringBuffer.length; ++i) {
                    this.ringBuffer[i][0] = this.rotationYaw;
                    this.ringBuffer[i][1] = this.getPosY();
                }
            }
            this.ringBufferIndex++;
            if (this.ringBufferIndex == this.ringBuffer.length) {
                this.ringBufferIndex = 0;
            }
            this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
            this.ringBuffer[ringBufferIndex][1] = this.getPosY();
            Vector3d[] avector3d = new Vector3d[this.whaleParts.length];

            for (int j = 0; j < this.whaleParts.length; ++j) {
                this.whaleParts[j].collideWithNearbyEntities();
                avector3d[j] = new Vector3d(this.whaleParts[j].getPosX(), this.whaleParts[j].getPosY(), this.whaleParts[j].getPosZ());
            }
            float f4 = MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F) - 0 * 0.01F);
            float f19 = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F) - 0 * 0.01F);
            float f15 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * ((float) Math.PI / 180F);
            float f16 = MathHelper.cos(f15);
            float f2 = MathHelper.sin(f15);
            float f17 = this.rotationYaw * ((float) Math.PI / 180F);
            float pitch = this.rotationPitch * ((float) Math.PI / 180F);
            float f3 = MathHelper.sin(f17) * (1 - Math.abs(this.rotationPitch / 90F));
            float f18 = MathHelper.cos(f17) * (1 - Math.abs(this.rotationPitch / 90F));

            this.setPartPosition(this.bodyPart, f3 * 0.5F, -pitch * 0.5F, -f18 * 0.5F);
            this.setPartPosition(this.bodyFrontPart, (f3) * -3.5F, -pitch * 3F, (f18) * 3.5F);
            this.setPartPosition(this.headPart, f3 * -7F, -pitch * 5F, -f18 * -7F);
            double[] adouble = this.getMovementOffsets(5, 1.0F);

            for (int k = 0; k < 3; ++k) {
                EntityCachalotPart enderdragonpartentity = null;
                if (k == 0) {
                    enderdragonpartentity = this.tail1Part;
                }
                if (k == 1) {
                    enderdragonpartentity = this.tail2Part;
                }
                if (k == 2) {
                    enderdragonpartentity = this.tail3Part;
                }

                double[] adouble1 = this.getMovementOffsets(15 + k * 5, 1.0F);
                float f7 = this.rotationYaw * ((float) Math.PI / 180F) + (float) MathHelper.wrapDegrees(adouble1[0] - adouble[0]) * ((float) Math.PI / 180F);
                float f20 = MathHelper.sin(f7) * (1 - Math.abs(this.rotationPitch / 90F));
                float f21 = MathHelper.cos(f7) * (1 - Math.abs(this.rotationPitch / 90F));
                float f22 = k == 2 ? -3.6F : -3.6F;
                float f23 = (float) (k + 1) * f22 - 2F;
                this.setPartPosition(enderdragonpartentity, -(f3 * 0.5F + f20 * f23) * f16, pitch * 1.5F * (k + 1), (f18 * 0.5F + f21 * f23) * f16);
            }

            for (int l = 0; l < this.whaleParts.length; ++l) {
                this.whaleParts[l].prevPosX = avector3d[l].x;
                this.whaleParts[l].prevPosY = avector3d[l].y;
                this.whaleParts[l].prevPosZ = avector3d[l].z;
                this.whaleParts[l].lastTickPosX = avector3d[l].x;
                this.whaleParts[l].lastTickPosY = avector3d[l].y;
                this.whaleParts[l].lastTickPosZ = avector3d[l].z;
            }
        }
        if (!world.isRemote) {
            LivingEntity target = this.getAttackTarget();
            if (target == null || !target.isAlive()) {
                whaleSpeedMod = this.isSleeping() ? 0 : 1;
                this.setCharging(false);
            } else if (!isBeached() && !isSleeping()) {
                this.faceEntity(target, 360, 360);
                waitForEchoFlag = this.getRevengeTarget() == null || !this.getRevengeTarget().isEntityEqual(target);
                if (target instanceof PlayerEntity || !target.isInWaterOrBubbleColumn()) {
                    waitForEchoFlag = false;
                }
                if (waitForEchoFlag && !receivedEcho) {
                    this.setCharging(false);
                    whaleSpeedMod = 0.25F;
                    if (echoTimer % 10 == 0) {
                        if (echoTimer % 40 == 0) {
                            this.playSound(AMSoundRegistry.CACHALOT_WHALE_CLICK, this.getSoundVolume(), this.getSoundPitch());
                        }
                        EntityCachalotEcho echo = new EntityCachalotEcho(this.world, this);
                        float radius = this.headPart.getWidth() * 0.5F;
                        float angle = (0.01745329251F * this.renderYawOffset);
                        double extraX = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.sin((float) (Math.PI + angle)) + (rand.nextFloat() - 0.5F) + this.getMotion().x * 2F;
                        double extraZ = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.cos(angle) + (rand.nextFloat() - 0.5F) + this.getMotion().z * 2F;
                        double x = this.headPart.getPosX() + extraX;
                        double y = this.headPart.getPosY() + this.headPart.getHeight() * 0.5D;
                        double z = this.headPart.getPosZ() + extraZ;
                        double d0 = target.getPosX() - x;
                        double d1 = target.getPosYHeight(0.1D) - y;
                        double d2 = target.getPosZ() - z;
                        echo.setPosition(x, y, z);
                        echo.shoot(d0, d1, d2, 1F, 0.0F);
                        this.world.addEntity(echo);
                    }
                    echoTimer++;
                }
                if (!waitForEchoFlag || receivedEcho) {
                    double d0 = target.getPosX() - this.getPosX();
                    double d1 = target.getPosYEye() - this.getPosYEye();
                    double d2 = target.getPosZ() - this.getPosZ();
                    double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
                    float targetYaw = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    float targetPitch = (float) (-(MathHelper.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
                    this.rotationPitch = (this.rotationPitch + MathHelper.clamp(targetPitch - this.rotationPitch, -2, 2));
                    if (d0 * d0 + d2 * d2 >= 4) {
                        this.rotationYaw = (this.rotationYaw + MathHelper.clamp(targetYaw - this.rotationYaw, -2, 2));
                        this.renderYawOffset = rotationYaw;
                    }
                    float dif = Math.abs(MathHelper.wrapDegrees(targetYaw) - MathHelper.wrapDegrees(this.rotationYaw));
                    if (chargeCooldown <= 0 && dif < 4) {
                        this.setCharging(true);
                        whaleSpeedMod = 1.5F;
                        if (d0 * d0 + d2 * d2 < 4) {
                            this.rotationYaw = prevRotationYaw;
                            this.renderYawOffset = prevRotationYaw;
                            this.setMotion(this.getMotion().mul(0.8, 1, 0.8));
                        } else {
                            this.getMoveHelper().setMoveTo(target.getPosX(), target.getPosY(), target.getPosZ(), 1.0D);
                        }
                        if (this.isCharging()) {
                            if (this.getDistance(target) < this.getWidth() && chargeProgress > 4) {
                                target.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                                this.setCharging(false);
                                if (target.getRidingEntity() instanceof BoatEntity) {
                                    BoatEntity boat = (BoatEntity) target.getRidingEntity();
                                    for (int i = 0; i < 3; ++i) {
                                        this.entityDropItem(boat.getBoatType().asPlank());
                                    }
                                    for (int j = 0; j < 2; ++j) {
                                        this.entityDropItem(Items.STICK);
                                    }
                                    target.dismount();
                                    boat.attackEntityFrom(DamageSource.causeMobDamage(this), 1000);
                                    boat.remove();
                                }
                                chargeCooldown = target instanceof PlayerEntity ? 30 : 100;
                                if (rand.nextInt(10) == 0) {
                                    if (!world.isRemote) {
                                        Vector3d vec = this.getMouthVec();
                                        ItemEntity itementity = new ItemEntity(this.world, vec.x, vec.y, vec.z, new ItemStack(AMItemRegistry.CACHALOT_WHALE_TOOTH));
                                        itementity.setDefaultPickupDelay();
                                        world.addEntity(itementity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (chargeCooldown > 0) {
                chargeCooldown--;
            }
            if (spoutTimer > 0) {
                world.setEntityState(this, (byte) 67);
                spoutTimer--;
                this.rotationPitch = 0;
                this.setMotion(this.getMotion().mul(0, 0, 0));
            }
            if (isSleepTime() && !this.isSleeping() && this.isInWaterOrBubbleColumn() && this.getAttackTarget() == null) {
                this.setSleeping(true);
            }
            if (this.isSleeping() && (!isSleepTime() || this.getAttackTarget() != null)) {
                this.setSleeping(false);
            }
        }

        if (this.isAlive() && isCharging()) {
            for (Entity entity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.headPart.getBoundingBox().grow(1.0D), null)) {
                if (!isOnSameTeam(entity) && !(entity instanceof EntityCachalotPart) && entity != this) {
                    launch(entity, true);
                }
            }
        }
        if (this.isInWater() && !this.areEyesInFluid(FluidTags.WATER) && this.getAir() > 140) {
            this.setMotion(this.getMotion().add(0, -0.06, 0));
        }
        if (!this.world.isRemote) {
            this.tryDespawn();
        }
        prevEyesInWater = this.areEyesInFluid(FluidTags.WATER);
    }

    private void launch(Entity e, boolean huge) {
        if (e.isOnGround() || e.isInWater()) {
            double d0 = e.getPosX() - this.getPosX();
            double d1 = e.getPosZ() - this.getPosZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            float f = huge ? 2F : 0.5F;
            e.addVelocity(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    private boolean isSleepTime() {
        long time = world.getDayTime();
        return time > 18000 && time < 22812 && this.isInWaterOrBubbleColumn();
    }

    public Vector3d getReturnEchoVector() {
        float radius = this.headPart.getWidth() * 0.5F;
        float angle = (0.01745329251F * this.renderYawOffset);
        double extraX = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.sin((float) (Math.PI + angle)) + (rand.nextFloat() - 0.5F) + this.getMotion().x * 2F;
        double extraZ = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.cos(angle) + (rand.nextFloat() - 0.5F) + this.getMotion().z * 2F;
        double x = this.headPart.getPosX() + extraX;
        double y = this.headPart.getPosY() + this.headPart.getHeight() * 0.5D;
        double z = this.headPart.getPosZ() + extraZ;
        return new Vector3d(x, y, z);
    }

    public Vector3d getMouthVec() {
        float radius = this.headPart.getWidth() * 0.5F;
        float angle = (0.01745329251F * this.renderYawOffset);
        double extraX = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.sin((float) (Math.PI + angle)) + (rand.nextFloat() - 0.5F) + this.getMotion().x * 2F;
        double extraZ = (radius * (1F + rand.nextFloat() * 0.13F)) * MathHelper.cos(angle) + (rand.nextFloat() - 0.5F) + this.getMotion().z * 2F;
        double x = this.headPart.getPosX() + extraX;
        double y = this.headPart.getPosY() + 0.25D;
        double z = this.headPart.getPosZ() + extraZ;
        return new Vector3d(x, y, z);
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        LivingEntity prev = this.getAttackTarget();
        if (prev != entitylivingbaseIn && entitylivingbaseIn != null) {
            receivedEcho = false;
        }
        super.setAttackTarget(entitylivingbaseIn);
    }

    public double[] getMovementOffsets(int p_70974_1_, float partialTicks) {
        if (this.getShouldBeDead()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        int i = this.ringBufferIndex - p_70974_1_ & 63;
        int j = this.ringBufferIndex - p_70974_1_ - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[i][0];
        double d1 = this.ringBuffer[j][0] - d0;
        adouble[0] = d0 + d1 * (double) partialTicks;
        d0 = this.ringBuffer[i][1];
        d1 = this.ringBuffer[j][1] - d0;
        adouble[1] = d0 + d1 * (double) partialTicks;
        adouble[2] = MathHelper.lerp(partialTicks, this.ringBuffer[i][2], this.ringBuffer[j][2]);
        return adouble;
    }

    public void applyEntityCollision(Entity entityIn) {

    }

    private void setPartPosition(EntityCachalotPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPosition(this.getPosX() + offsetX * part.scale, this.getPosY() + offsetY * part.scale, this.getPosZ() + offsetZ * part.scale);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.whaleParts;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        EntityCachalotWhale whale = AMEntityRegistry.CACHALOT_WHALE.create(serverWorld);
        whale.setAlbino(this.isAlbino());
        return whale;
    }

    public boolean attackEntityPartFrom(EntityCachalotPart entityCachalotPart, DamageSource source, float amount) {
        return this.attackEntityFrom(source, amount);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setAir(this.getMaxAir());
        this.rotationPitch = 0.0F;
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableEntity.AgeableData(0.75F);
        }
        this.setAlbino(rand.nextInt(100) == 0);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    public void baseTick() {
        int i = this.getAir();
        super.baseTick();
        this.updateAir(i);
    }

    public boolean isPushedByWater() {
        return false;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    protected void updateAir(int p_209207_1_) {
    }

    public int getMaxAir() {
        return 4000;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 67) {
            spawnSpoutParticles();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    protected int determineNextAir(int currentAir) {
        if (!this.areEyesInFluid(FluidTags.WATER) && prevEyesInWater && !world.isRemote && spoutTimer <= 0 && currentAir < this.getMaxAir() / 2) {
            spoutTimer = 20 + rand.nextInt(10);
        }
        return this.getMaxAir();
    }

    public int getVerticalFaceSpeed() {
        return 1;
    }

    public int getHorizontalFaceSpeed() {
        return 1;
    }

    public void recieveEcho() {
        this.receivedEcho = true;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.cachalotWhaleSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    static class MoveHelperController extends MovementController {
        private final EntityCachalotWhale dolphin;

        public MoveHelperController(EntityCachalotWhale dolphinIn) {
            super(dolphinIn);
            this.dolphin = dolphinIn;
        }

        public void tick() {
            if (this.action == Action.MOVE_TO) {
                double lvt_1_1_ = this.posX - dolphin.getPosX();
                double lvt_3_1_ = this.posY - dolphin.getPosY();
                double lvt_5_1_ = this.posZ - dolphin.getPosZ();
                double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;

                float lvt_9_1_ = (float) (MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
                dolphin.rotationYaw = this.limitAngle(dolphin.rotationYaw, lvt_9_1_, 10.0F);
                float lvt_10_1_ = (float) (this.speed * dolphin.whaleSpeedMod * 2 * dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
                if (dolphin.isInWater()) {
                    dolphin.setAIMoveSpeed(lvt_10_1_ * 0.02F);
                    float lvt_11_1_ = -((float) (MathHelper.atan2(lvt_3_1_, MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_)) * 57.2957763671875D));
                    dolphin.setMotion(dolphin.getMotion().add(0.0D, (double) dolphin.getAIMoveSpeed() * lvt_3_1_ * 0.6D, 0.0D));
                    lvt_11_1_ = MathHelper.clamp(MathHelper.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                    dolphin.rotationPitch = this.limitAngle(dolphin.rotationPitch, lvt_11_1_, 5.0F);
                    float lvt_12_1_ = MathHelper.cos(dolphin.rotationPitch * 0.017453292F);
                    float lvt_13_1_ = MathHelper.sin(dolphin.rotationPitch * 0.017453292F);
                    dolphin.moveForward = lvt_12_1_ * lvt_10_1_;
                    dolphin.moveVertical = -lvt_13_1_ * lvt_10_1_;
                } else {
                    dolphin.setMotion(dolphin.getMotion().add(0.0D, -0.6D, 0.0D));
                    dolphin.setAIMoveSpeed(lvt_10_1_ * 0.1F);
                }

            }
        }
    }

    class AIBreathe extends Goal {

        public AIBreathe() {
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean shouldExecute() {
            return EntityCachalotWhale.this.getAir() < 140;
        }

        public boolean shouldContinueExecuting() {
            return this.shouldExecute();
        }

        public boolean isPreemptible() {
            return false;
        }

        public void startExecuting() {
            this.navigate();
        }

        private void navigate() {
            Iterable<BlockPos> lvt_1_1_ = BlockPos.getAllInBoxMutable(MathHelper.floor(EntityCachalotWhale.this.getPosX() - 1.0D), MathHelper.floor(EntityCachalotWhale.this.getPosY()), MathHelper.floor(EntityCachalotWhale.this.getPosZ() - 1.0D), MathHelper.floor(EntityCachalotWhale.this.getPosX() + 1.0D), MathHelper.floor(EntityCachalotWhale.this.getPosY() + 8.0D), MathHelper.floor(EntityCachalotWhale.this.getPosZ() + 1.0D));
            BlockPos lvt_2_1_ = null;
            Iterator var3 = lvt_1_1_.iterator();

            while (var3.hasNext()) {
                BlockPos lvt_4_1_ = (BlockPos) var3.next();
                if (this.canBreatheAt(EntityCachalotWhale.this.world, lvt_4_1_)) {
                    lvt_2_1_ = lvt_4_1_.down((int) (EntityCachalotWhale.this.getHeight() * 0.25d));
                    break;
                }
            }

            if (lvt_2_1_ == null) {
                lvt_2_1_ = new BlockPos(EntityCachalotWhale.this.getPosX(), EntityCachalotWhale.this.getPosY() + 4.0D, EntityCachalotWhale.this.getPosZ());
            }
            if (EntityCachalotWhale.this.areEyesInFluid(FluidTags.WATER)) {
                EntityCachalotWhale.this.setMotion(EntityCachalotWhale.this.getMotion().add(0, 0.05F, 0));
            }

            EntityCachalotWhale.this.getNavigator().tryMoveToXYZ(lvt_2_1_.getX(), lvt_2_1_.getY(), lvt_2_1_.getZ(), 0.7D);
        }

        public void tick() {
            this.navigate();
        }

        private boolean canBreatheAt(IWorldReader p_205140_1_, BlockPos p_205140_2_) {
            BlockState lvt_3_1_ = p_205140_1_.getBlockState(p_205140_2_);
            return (p_205140_1_.getFluidState(p_205140_2_).isEmpty() || lvt_3_1_.isIn(Blocks.BUBBLE_COLUMN)) && lvt_3_1_.allowsMovement(p_205140_1_, p_205140_2_, PathType.LAND);
        }
    }
}
