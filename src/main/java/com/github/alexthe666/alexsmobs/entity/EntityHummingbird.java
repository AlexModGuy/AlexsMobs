package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.BlockHummingbirdFeeder;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIWander;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.base.Predicates;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityHummingbird extends Animal {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityHummingbird.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityHummingbird.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CROPS_POLLINATED = SynchedEntityData.defineId(EntityHummingbird.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> FEEDER_POS = SynchedEntityData.defineId(EntityHummingbird.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public float flyProgress;
    public float prevFlyProgress;
    public float movingProgress;
    public float prevMovingProgress;
    public int hummingStill = 0;
    public int pollinateCooldown = 0;
    public int sipCooldown = 0;
    private int loopSoundTick = 0;
    private boolean sippy;
    public float sipProgress;
    public float prevSipProgress;

    protected EntityHummingbird(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlightMoveController(this, 1.5F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.hummingbirdSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.HUMMINGBIRD_IDLE.get();
    }

    public int getAmbientSoundInterval() {
        return 60;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.HUMMINGBIRD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.HUMMINGBIRD_HURT.get();
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.FLYING_SPEED, 7F).add(Attributes.ATTACK_DAMAGE, 0.0D).add(Attributes.MOVEMENT_SPEED, 0.45F);
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FLOWERS);
    }

    public int getMaxSpawnClusterSize() {
        return 7;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BreedGoal(this, 1));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1, Ingredient.of(ItemTags.FLOWERS), false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1));
        this.goalSelector.addGoal(4, new AIUseFeeder(this));
        this.goalSelector.addGoal(4, new HummingbirdAIPollinate(this));
        this.goalSelector.addGoal(5, new HummingbirdAIWander(this, 16, 6, 15, 1));
        this.goalSelector.addGoal(6, new FloatGoal(this));
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {}

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below(2)).isAir();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected boolean makeFlySound() {
        return true;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.5F : sizeIn.height * 0.5F;
    }


    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("CropsPollinated", this.getCropsPollinated());
        compound.putInt("PollinateCooldown", this.pollinateCooldown);
        BlockPos blockpos = this.getFeederPos();
        if (blockpos != null) {
            compound.putInt("HLPX", blockpos.getX());
            compound.putInt("HLPY", blockpos.getY());
            compound.putInt("HLPZ", blockpos.getZ());
        }

    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setCropsPollinated(compound.getInt("CropsPollinated"));
        this.pollinateCooldown = compound.getInt("PollinateCooldown");
        if (compound.contains("HLPX")) {
            int i = compound.getInt("HLPX");
            int j = compound.getInt("HLPY");
            int k = compound.getInt("HLPZ");
            this.entityData.set(FEEDER_POS, Optional.of(new BlockPos(i, j, k)));
        } else {
            this.entityData.set(FEEDER_POS, Optional.empty());
        }
    }

    public BlockPos getFeederPos() {
        return this.entityData.get(FEEDER_POS).orElse(null);
    }

    public void setFeederPos(BlockPos pos) {
        this.entityData.set(FEEDER_POS, Optional.ofNullable(pos));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(VARIANT, 0);
        this.entityData.define(CROPS_POLLINATED, 0);
        this.entityData.define(FEEDER_POS, Optional.empty());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setVariant(this.getRandom().nextInt(3));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private List<BlockPos> getNearbyFeeders(BlockPos blockpos, ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        Stream<BlockPos> stream = pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.HUMMINGBIRD_FEEDER.getKey()), Predicates.alwaysTrue(), blockpos, range, PoiManager.Occupancy.ANY);
        return stream.collect(Collectors.toList());
    }


    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, Boolean.valueOf(flying));
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public int getCropsPollinated() {
        return this.entityData.get(CROPS_POLLINATED);
    }

    public void setCropsPollinated(int crops) {
        this.entityData.set(CROPS_POLLINATED, Integer.valueOf(crops));
    }

    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        boolean flag = this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z >= 1.0E-3D;
        if (!this.onGround() && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.4D, 1.0D));
        }
        this.setFlying(true);
        this.setNoGravity(true);
        if (this.isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!this.isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if(sippy && sipProgress < 5F){
            sipProgress++;
        }
        if(!sippy && sipProgress > 0F){
            sipProgress--;
        }
        if(sippy && sipProgress == 5F){
            sippy = false;
        }
        if (flag && movingProgress < 5F) {
            movingProgress++;
        }
        if (!flag && movingProgress > 0F) {
            movingProgress--;
        }
        if(this.getDeltaMovement().lengthSqr() < 1.0E-7D){
            hummingStill++;
        }else{
            hummingStill = 0;
        }
        if(pollinateCooldown > 0){
            pollinateCooldown--;
        }
        if(sipCooldown > 0){
            sipCooldown--;
        }
        if(loopSoundTick == 0){
            this.playSound(AMSoundRegistry.HUMMINGBIRD_LOOP.get(), this.getSoundVolume() * 0.33F, this.getVoicePitch());
        }
        loopSoundTick++;
        if(loopSoundTick > 27){
            loopSoundTick = 0;
        }
        prevFlyProgress = flyProgress;
        prevMovingProgress = movingProgress;
        prevSipProgress = sipProgress;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if(id == 68){
            if(this.getFeederPos() != null){
                if(random.nextFloat() < 0.2F){
                    double d2 = this.random.nextGaussian() * 0.02D;
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.FALLING_NECTAR, this.getFeederPos().getX() + 0.2F + (double) (this.random.nextFloat() * 0.6F), this.getFeederPos().getY() + 0.1F, this.getFeederPos().getZ() + 0.2F + (double) (this.random.nextFloat() * 0.6F), d0, d1, d2);
                }
                this.sippy = true;
            }
        }else{
            super.handleEntityEvent(id);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.HUMMINGBIRD.get().create(serverWorld);
    }

    public static <T extends Mob> boolean canHummingbirdSpawn(EntityType<EntityHummingbird> hummingbird, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
        return (blockstate.is(BlockTags.LEAVES) || blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(BlockTags.LOGS) || blockstate.is(Blocks.AIR)) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
    }

    public boolean canBlockBeSeen(BlockPos pos) {
        double x = pos.getX() + 0.5F;
        double y = pos.getY() + 0.5F;
        double z = pos.getZ() + 0.5F;
        HitResult result = this.level().clip(new ClipContext(new Vec3(this.getX(), this.getY() + (double) this.getEyeHeight(), this.getZ()), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        double dist = result.getLocation().distanceToSqr(x, y, z);
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }

    private class AIUseFeeder extends Goal {
        int runCooldown = 0;
        private int idleAtFlowerTime = 0;
        private BlockPos localFeeder;

        public AIUseFeeder(EntityHummingbird entityHummingbird) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        public void stop(){
            localFeeder = null;
            idleAtFlowerTime = 0;
        }

        @Override
        public boolean canUse() {
            if(EntityHummingbird.this.sipCooldown > 0){
                return false;
            }
           if(runCooldown > 0){
               runCooldown--;
           }else{
               BlockPos feedPos = getFeederPos();
               if(feedPos != null && isValidFeeder(level().getBlockState(feedPos))){
                   localFeeder = feedPos;
                   return true;
               }else{
                   List<BlockPos> beacons = getNearbyFeeders(EntityHummingbird.this.blockPosition(), (ServerLevel) level(), 64);
                   BlockPos closest = null;
                   for (BlockPos pos : beacons) {
                       if (closest == null || EntityHummingbird.this.distanceToSqr(closest.getX(), closest.getY(), closest.getZ()) > EntityHummingbird.this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())) {
                           if (isValidFeeder(level().getBlockState(pos))) {
                               closest = pos;
                           }
                       }
                   }
                   if (closest != null && isValidFeeder(level().getBlockState(closest))) {
                       localFeeder = closest;
                       return true;
                   }
               }
           }
           runCooldown = 400 + random.nextInt(600);
           return false;
        }

        public boolean canContinueToUse(){
            return localFeeder != null && isValidFeeder(level().getBlockState(localFeeder)) && EntityHummingbird.this.sipCooldown == 0;
        }

        public void tick(){
            if(localFeeder != null && isValidFeeder(level().getBlockState(localFeeder))){
                if(EntityHummingbird.this.getY() > localFeeder.getY() && !EntityHummingbird.this.onGround()){
                    EntityHummingbird.this.getMoveControl().setWantedPosition(localFeeder.getX() + 0.5F, localFeeder.getY() + 0.1F, localFeeder.getZ() + 0.5F, 1F);
                }else{
                    EntityHummingbird.this.getMoveControl().setWantedPosition(localFeeder.getX() + random.nextInt(4) - 2, EntityHummingbird.this.getY() + 1F, localFeeder.getZ() + random.nextInt(4) - 2, 1F);
                }
                Vec3 vec = Vec3.upFromBottomCenterOf(localFeeder, 0.1F);
                double dist = Mth.sqrt((float)EntityHummingbird.this.distanceToSqr(vec));
                if(dist < 2.5F && EntityHummingbird.this.getY() > localFeeder.getY()){
                    EntityHummingbird.this.lookAt(EntityAnchorArgument.Anchor.EYES, vec);
                    idleAtFlowerTime++;
                    EntityHummingbird.this.setFeederPos(localFeeder);
                    EntityHummingbird.this.level().broadcastEntityEvent(EntityHummingbird.this, (byte)68);
                    if(idleAtFlowerTime > 55){
                        if(EntityHummingbird.this.getCropsPollinated() > 2 && random.nextInt(25) == 0 && isValidFeeder(level().getBlockState(localFeeder))){
                            level().setBlockAndUpdate(localFeeder, level().getBlockState(localFeeder).setValue(BlockHummingbirdFeeder.CONTENTS, 0));
                        }
                        EntityHummingbird.this.setCropsPollinated(EntityHummingbird.this.getCropsPollinated() + 1);
                        EntityHummingbird.this.sipCooldown = 120 + random.nextInt(1200);
                        EntityHummingbird.this.pollinateCooldown = Math.max(0, EntityHummingbird.this.pollinateCooldown / 3);
                        runCooldown = 400 + random.nextInt(600);
                        stop();
                    }
                }
            }
        }

        public boolean isValidFeeder(BlockState state){
            return state.getBlock() instanceof BlockHummingbirdFeeder && state.getValue(BlockHummingbirdFeeder.CONTENTS) == 3;
        }
    }
}
