package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.BlockHummingbirdFeeder;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIWander;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.google.common.base.Predicates;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityHummingbird extends AnimalEntity {

    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityHummingbird.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityHummingbird.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CROPS_POLLINATED = EntityDataManager.createKey(EntityHummingbird.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<BlockPos>> FEEDER_POS = EntityDataManager.createKey(EntityHummingbird.class, DataSerializers.OPTIONAL_BLOCK_POS);
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

    protected EntityHummingbird(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new FlightMoveController(this, 1.5F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathPriority(PathNodeType.COCOA, -1.0F);
        this.setPathPriority(PathNodeType.FENCE, -1.0F);
        this.setPathPriority(PathNodeType.LEAVES, 0.0F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.hummingbirdSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.HUMMINGBIRD_IDLE;
    }

    public int getTalkInterval() {
        return 60;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.HUMMINGBIRD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.HUMMINGBIRD_HURT;
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 4.0D).createMutableAttribute(Attributes.FLYING_SPEED, 7F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.45F);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem().isIn(ItemTags.FLOWERS);
    }

    public int getMaxSpawnedInChunk() {
        return 7;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BreedGoal(this, 1));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1));
        this.goalSelector.addGoal(4, new AIUseFeeder(this));
        this.goalSelector.addGoal(4, new HummingbirdAIPollinate(this));
        this.goalSelector.addGoal(5, new HummingbirdAIWander(this, 16, 6, 15, 1));
        this.goalSelector.addGoal(6, new SwimGoal(this));
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {}

    protected PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
            public boolean canEntityStandOnPos(BlockPos pos) {
                return !this.world.getBlockState(pos.down(2)).isAir();
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(false);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected boolean makeFlySound() {
        return true;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isChild() ? sizeIn.height * 0.5F : sizeIn.height * 0.5F;
    }


    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
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

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setCropsPollinated(compound.getInt("CropsPollinated"));
        this.pollinateCooldown = compound.getInt("PollinateCooldown");
        if (compound.contains("HLPX")) {
            int i = compound.getInt("HLPX");
            int j = compound.getInt("HLPY");
            int k = compound.getInt("HLPZ");
            this.dataManager.set(FEEDER_POS, Optional.of(new BlockPos(i, j, k)));
        } else {
            this.dataManager.set(FEEDER_POS, Optional.empty());
        }
    }

    public BlockPos getFeederPos() {
        return this.dataManager.get(FEEDER_POS).orElse(null);
    }

    public void setFeederPos(BlockPos pos) {
        this.dataManager.set(FEEDER_POS, Optional.ofNullable(pos));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, Boolean.valueOf(false));
        this.dataManager.register(VARIANT, 0);
        this.dataManager.register(CROPS_POLLINATED, 0);
        this.dataManager.register(FEEDER_POS, Optional.empty());
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setVariant(this.getRNG().nextInt(3));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private List<BlockPos> getNearbyFeeders(BlockPos blockpos, ServerWorld world, int range) {
        PointOfInterestManager pointofinterestmanager = world.getPointOfInterestManager();
        Stream<BlockPos> stream = pointofinterestmanager.findAll(AMPointOfInterestRegistry.HUMMINGBIRD_FEEDER.getPredicate(), Predicates.alwaysTrue(), blockpos, range, PointOfInterestManager.Status.ANY);
        return stream.collect(Collectors.toList());
    }


    public boolean isFlying() {
        return this.dataManager.get(FLYING).booleanValue();
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, Boolean.valueOf(flying));
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public int getCropsPollinated() {
        return this.dataManager.get(CROPS_POLLINATED).intValue();
    }

    public void setCropsPollinated(int crops) {
        this.dataManager.set(CROPS_POLLINATED, Integer.valueOf(crops));
    }

    public void tick() {
        super.tick();
        Vector3d vector3d = this.getMotion();
        boolean flag = this.getMotion().x * this.getMotion().x + this.getMotion().z * this.getMotion().z >= 1.0E-3D;
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setMotion(vector3d.mul(1.0D, 0.4D, 1.0D));
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
        if(this.getMotion().lengthSquared() < 1.0E-7D){
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
            this.playSound(AMSoundRegistry.HUMMINGBIRD_LOOP, this.getSoundVolume() * 0.33F, this.getSoundPitch());
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
    public void handleStatusUpdate(byte id) {
        if(id == 68){
            if(this.getFeederPos() != null){
                if(rand.nextFloat() < 0.2F){
                    double d2 = this.rand.nextGaussian() * 0.02D;
                    double d0 = this.rand.nextGaussian() * 0.02D;
                    double d1 = this.rand.nextGaussian() * 0.02D;
                    this.world.addParticle(ParticleTypes.FALLING_NECTAR, this.getFeederPos().getX() + 0.2F + (double) (this.rand.nextFloat() * 0.6F), this.getFeederPos().getY() + 0.1F, this.getFeederPos().getZ() + 0.2F + (double) (this.rand.nextFloat() * 0.6F), d0, d1, d2);
                }
                this.sippy = true;
            }
        }else{
            super.handleStatusUpdate(id);
        }
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.HUMMINGBIRD.create(serverWorld);
    }

    public static <T extends MobEntity> boolean canHummingbirdSpawn(EntityType<EntityHummingbird> hummingbird, IWorld worldIn, SpawnReason reason, BlockPos p_223317_3_, Random random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.down());
        return (blockstate.isIn(BlockTags.LEAVES) || blockstate.matchesBlock(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LOGS) || blockstate.matchesBlock(Blocks.AIR)) && worldIn.getLightSubtracted(p_223317_3_, 0) > 8;
    }

    public boolean canBlockBeSeen(BlockPos pos) {
        double x = pos.getX() + 0.5F;
        double y = pos.getY() + 0.5F;
        double z = pos.getZ() + 0.5F;
        RayTraceResult result = this.world.rayTraceBlocks(new RayTraceContext(new Vector3d(this.getPosX(), this.getPosY() + (double) this.getEyeHeight(), this.getPosZ()), new Vector3d(x, y, z), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        double dist = result.getHitVec().squareDistanceTo(x, y, z);
        return dist <= 1.0D || result.getType() == RayTraceResult.Type.MISS;
    }

    private class AIUseFeeder extends Goal {
        int runCooldown = 0;
        private int idleAtFlowerTime = 0;
        private BlockPos localFeeder;

        public AIUseFeeder(EntityHummingbird entityHummingbird) {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        public void resetTask(){
            localFeeder = null;
            idleAtFlowerTime = 0;
        }

        @Override
        public boolean shouldExecute() {
            if(EntityHummingbird.this.sipCooldown > 0){
                return false;
            }
           if(runCooldown > 0){
               runCooldown--;
           }else{
               BlockPos feedPos = getFeederPos();
               if(feedPos != null && isValidFeeder(world.getBlockState(feedPos))){
                   localFeeder = feedPos;
                   return true;
               }else{
                   List<BlockPos> beacons = getNearbyFeeders(EntityHummingbird.this.getPosition(), (ServerWorld) world, 64);
                   BlockPos closest = null;
                   for (BlockPos pos : beacons) {
                       if (closest == null || EntityHummingbird.this.getDistanceSq(closest.getX(), closest.getY(), closest.getZ()) > EntityHummingbird.this.getDistanceSq(pos.getX(), pos.getY(), pos.getZ())) {
                           if (isValidFeeder(world.getBlockState(pos))) {
                               closest = pos;
                           }
                       }
                   }
                   if (closest != null && isValidFeeder(world.getBlockState(closest))) {
                       localFeeder = closest;
                       return true;
                   }
               }
           }
           runCooldown = 400 + rand.nextInt(600);
           return false;
        }

        public boolean shouldContinueExecuting(){
            return localFeeder != null && isValidFeeder(world.getBlockState(localFeeder)) && EntityHummingbird.this.sipCooldown == 0;
        }

        public void tick(){
            if(localFeeder != null && isValidFeeder(world.getBlockState(localFeeder))){
                if(EntityHummingbird.this.getPosY() > localFeeder.getY() && !EntityHummingbird.this.isOnGround()){
                    EntityHummingbird.this.getMoveHelper().setMoveTo(localFeeder.getX() + 0.5F, localFeeder.getY() + 0.1F, localFeeder.getZ() + 0.5F, 1F);
                }else{
                    EntityHummingbird.this.getMoveHelper().setMoveTo(localFeeder.getX() + rand.nextInt(4) - 2, EntityHummingbird.this.getPosY() + 1F, localFeeder.getZ() + rand.nextInt(4) - 2, 1F);
                }
                Vector3d vec = Vector3d.copyCenteredWithVerticalOffset(localFeeder, 0.1F);
                double dist = MathHelper.sqrt(EntityHummingbird.this.getDistanceSq(vec));
                if(dist < 2.5F && EntityHummingbird.this.getPosY() > localFeeder.getY()){
                    EntityHummingbird.this.lookAt(EntityAnchorArgument.Type.EYES, vec);
                    idleAtFlowerTime++;
                    EntityHummingbird.this.setFeederPos(localFeeder);
                    EntityHummingbird.this.world.setEntityState(EntityHummingbird.this, (byte)68);
                    if(idleAtFlowerTime > 55){
                        if(EntityHummingbird.this.getCropsPollinated() > 2 && rand.nextInt(25) == 0 && isValidFeeder(world.getBlockState(localFeeder))){
                            world.setBlockState(localFeeder, world.getBlockState(localFeeder).with(BlockHummingbirdFeeder.CONTENTS, 0));
                        }
                        EntityHummingbird.this.setCropsPollinated(EntityHummingbird.this.getCropsPollinated() + 1);
                        EntityHummingbird.this.sipCooldown = 120 + rand.nextInt(1200);
                        EntityHummingbird.this.pollinateCooldown = Math.max(0, EntityHummingbird.this.pollinateCooldown / 3);
                        runCooldown = 400 + rand.nextInt(600);
                        resetTask();
                    }
                }
            }
        }

        public boolean isValidFeeder(BlockState state){
            return state.getBlock() instanceof BlockHummingbirdFeeder && state.get(BlockHummingbirdFeeder.CONTENTS) == 3;
        }
    }
}
