package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.EtherealMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.MonsterAIWalkThroughHallsOfStructure;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class EntityUnderminer extends PathfinderMob {

    protected static final EntityDataAccessor<Optional<BlockPos>> TARGETED_BLOCK_POS = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DWARF = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> MINING_PROGRESS = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> VISUALLY_MINING = SynchedEntityData.defineId(EntityUnderminer.class, EntityDataSerializers.BOOLEAN);

    private int mineCooldown = 100;
    private int resetStackTime = 0;
    private ItemStack lastGivenStack = null;
    public float hidingProgress = 0;
    public float prevHidingProgress = 0;
    private boolean mineAIFlag = false;
    private BlockPos lastPosition = this.blockPosition();

    public EntityUnderminer(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new EtherealMoveController(this, 1F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.FOLLOW_RANGE, 64F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new PathNavigator(this, level);
    }

    public static <T extends Mob> boolean checkUnderminerSpawnRules(EntityType<EntityUnderminer> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.SPAWNER) {
            return true;
        }else{
            int j = 3;
            if(pos.getY() >= iServerWorld.getSeaLevel()){
                return false;
            }else if (AlexsMobs.isHalloween()) {
                j = 7;
            } else if (random.nextBoolean()) {
                return false;
            }

            final int i = iServerWorld.getMaxLocalRawBrightness(pos);
            return i > random.nextInt(j) ? false : checkMobSpawnRules(entityType, iServerWorld, reason, pos, random);
        }
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !requiresCustomPersistence() && !this.hasCustomName();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.hasCustomName() || lastGivenStack != null;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.underminerSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DWARF, true);
        this.entityData.define(HIDING, false);
        this.entityData.define(VISUALLY_MINING, false);
        this.entityData.define(TARGETED_BLOCK_POS, Optional.empty());
        this.entityData.define(MINING_PROGRESS, 0.0F);
        this.entityData.define(VARIANT, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Dwarf", this.isDwarf());
        compound.putBoolean("Hiding", this.isHiding());
        compound.putInt("Variant", this.getVariant());
        compound.putInt("ResetItemTime", resetStackTime);
        compound.putInt("MineCooldown", mineCooldown);
        if(lastGivenStack != null){
            compound.put("MineStack", lastGivenStack.serializeNBT());
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setDwarf(compound.getBoolean("Dwarf"));
        this.setHiding(compound.getBoolean("Hiding"));
        this.setVariant(compound.getInt("Variant"));
        this.resetStackTime = compound.getInt("ResetItemTime");
        this.mineCooldown = compound.getInt("MineCooldown");
        if(compound.contains("MineStack")){
            this.lastGivenStack = ItemStack.of(compound.getCompound("MineStack"));
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.UNDERMINER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.UNDERMINER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.UNDERMINER_HURT.get();
    }


    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    public boolean isDwarf() {
        return this.entityData.get(DWARF) && !isExtraSpooky();
    }

    public void setDwarf(boolean phasing) {
        this.entityData.set(DWARF, phasing);
    }

    public int getVariant() {
        return isExtraSpooky() ? 1 : this.entityData.get(VARIANT);
    }

    public void setVariant(int i) {
        this.entityData.set(VARIANT, i);
    }

    public boolean isHiding() {
        return this.entityData.get(HIDING);
    }

    public void setHiding(boolean phasing) {
        this.entityData.set(HIDING, phasing);
    }

    @Nullable
    public BlockPos getMiningPos() {
        return this.getEntityData().get(TARGETED_BLOCK_POS).orElse(null);
    }

    public void setMiningPos(@Nullable BlockPos beamTarget) {
        this.getEntityData().set(TARGETED_BLOCK_POS, Optional.ofNullable(beamTarget));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, (double)1.2F, true));
        this.goalSelector.addGoal(2, new MineGoal());
        this.goalSelector.addGoal(3, new MonsterAIWalkThroughHallsOfStructure(this, 0.5D, 60, StructureTags.MINESHAFT, 50));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.isMagic() && source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer() || super.isInvulnerableTo(source);
    }

    private float calculateDistanceToFloor() {
        BlockPos floor = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
        while (!level.getBlockState(floor).isFaceSturdy(level, floor, Direction.UP) && floor.getY() > level.getMinBuildHeight()) {
            floor = floor.below();
        }
        return (float) (this.getBoundingBox().minY - (floor.getY() + 1));
    }


    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9D, 0.6D, 0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_218949_, DifficultyInstance p_218950_) {
        super.populateDefaultEquipmentSlots(p_218949_, p_218950_);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(AMItemRegistry.GHOSTLY_PICKAXE.get()));
    }

    protected float getEquipmentDropChance(EquipmentSlot slot) {
        if(slot == EquipmentSlot.MAINHAND){
            return 0.5F;
        }
        return super.getEquipmentDropChance(slot);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag) {
        spawnData = super.finalizeSpawn(level, difficultyInstance, mobSpawnType, spawnData, tag);
        RandomSource randomsource = level.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, difficultyInstance);
        if(random.nextFloat() < 0.3F){
            this.setVariant(random.nextInt(2));
            this.setDwarf(false);
        }else{
            this.setDwarf(true);
        }
        return spawnData;
    }

    public boolean isFullyHidden(){
        return this.isHiding() && hidingProgress >= 10F;
    }


    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.prevHidingProgress = hidingProgress;
        this.noPhysics = false;
        if(this.isHiding() && hidingProgress < 10F){
            this.hidingProgress++;
        }
        if(!this.isHiding() && hidingProgress > 0F){
            hidingProgress--;
        }
        if (!level.isClientSide) {
            final double xzSpeed = this.getDeltaMovement().horizontalDistance();
            final double distToFloor = Mth.clamp(calculateDistanceToFloor(), -1F, 1F);
            if (Math.abs(distToFloor) > 0.01 && xzSpeed < 0.05 && !this.isActuallyInAWall()) {
                if (distToFloor < 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -Math.min(distToFloor * 0.1F, 0F), 0));
                } else if (distToFloor > 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, -Math.max(distToFloor * 0.1F, 0F), 0));
                }
            }
            if(lastPosition != null && lastPosition.distSqr(this.blockPosition()) > 2.5F && Math.abs(distToFloor) < 0.5){
                this.playSound(AMSoundRegistry.UNDERMINER_STEP.get(), 1F, 0.75F + random.nextFloat() * 0.25F);
                lastPosition = this.blockPosition();
                if(random.nextFloat() < 0.015F && !level.canSeeSky(lastPosition)){
                    this.playSound(SoundEvents.AMBIENT_CAVE, 3F, 0.75F + random.nextFloat() * 0.25F);
                }
            }
            Player player = this.level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), AMConfig.underminerDisappearDistance, true);
            if(player != null && lastGivenStack == null && (this.getTarget() == null || !this.getTarget().isAlive())){
                this.setHiding(true);
                this.lookAt(player, 360F, 360F);
            }else{
                this.setHiding(false);
            }
        }
        this.setYBodyRot(this.getYRot());

        if(mineCooldown > 0){
            mineCooldown--;
        }
        if(resetStackTime > 0){
            resetStackTime--;
            if(resetStackTime == 0){
                lastGivenStack = null;
            }
        }
        if(entityData.get(VISUALLY_MINING)){
            this.swing(InteractionHand.MAIN_HAND);
        }
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean canPickUpLoot() {
        return true;
    }

    public boolean wantsToPickUp(ItemStack stack) {
        return stack.is(AMTagRegistry.UNDERMINER_ORES);
    }

    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        if (itemstack.is(AMTagRegistry.UNDERMINER_ORES)) {
            this.onItemPickup(itemEntity);
            this.take(itemEntity, itemstack.getCount());
            itemEntity.discard();
            this.mineAIFlag = this.lastGivenStack == null || !this.lastGivenStack.sameItem(itemEntity.getItem());
            this.lastGivenStack = itemEntity.getItem();
            this.resetStackTime = 2000 + random.nextInt(1200);
            this.mineCooldown = 0;
        }else{
            super.pickUpItem(itemEntity);
        }

    }

    protected void jumpFromGround() {

    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean isExtraSpooky(){
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return AlexsMobs.isAprilFools() || AlexsMobs.isHalloween() || s != null && s.toLowerCase().contains("herobrine");
    }

    private boolean isActuallyInAWall() {
        final float f = this.getDimensions(this.getPose()).width * 0.1F;
        AABB aabb = AABB.ofSize(this.getEyePosition(), f, 1.0E-6D, f);
        return BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
            BlockState blockstate = this.level.getBlockState(p_201942_);
            return !blockstate.isAir() && blockstate.isSuffocating(this.level, p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level, p_201942_).move(p_201942_.getX(), p_201942_.getY(), p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
        });
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getBrightness() {
        return 1.0F;
    }

    public float getMiningProgress() {
        return this.entityData.get(MINING_PROGRESS);
    }

    public void setMiningProgress(float f) {
        this.entityData.set(MINING_PROGRESS, f);
    }

    private List<BlockPos> getNearbyObscuredOres(int range, int maxOres) {
        List<BlockPos> obscuredBlocks = new ArrayList<>();
        BlockPos blockpos = this.blockPosition();
        final int half = range / 2;
        for (int i = 0; i <= half && i >= -half; i = (i <= 0 ? 1 : 0) - i) {
            for (int j = 0; j <= range && j >= -range; j = (j <= 0 ? 1 : 0) - j) {
                for (int k = 0; k <= range && k >= -range; k = (k <= 0 ? 1 : 0) - k) {
                    BlockPos offset = blockpos.offset(j, i, k);
                    BlockState state = this.getLevel().getBlockState(offset);
                    if (isValidMiningBlock(state)) {
                        if (obscuredBlocks.size() < maxOres) {
                            BlockPos obscured = getObscuringBlockOf(offset);
                            if(obscured != null){
                                obscuredBlocks.add(obscured);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return obscuredBlocks;
    }

    private boolean isValidMiningBlock(BlockState state) {
        if(lastGivenStack != null){
            return lastGivenStack.getItem() == state.getBlock().asItem();
        }
        return state.is(Tags.Blocks.ORES);
    }

    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    public boolean isAttackable() {
        return !this.isFullyHidden() && super.isAttackable();
    }

    public boolean skipAttackInteraction(Entity entity) {
        return this.isFullyHidden() || super.skipAttackInteraction(entity);
    }


    private BlockPos getObscuringBlockOf(BlockPos target) {
        Vec3 eyes = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        HitResult hitResult = this.level.clip(new ClipContext(eyes, Vec3.atCenterOf(target), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult instanceof BlockHitResult && !((BlockHitResult) hitResult).getBlockPos().equals(target)) {
            BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
            return pos.distSqr(target) > 4 ? null : pos;
        }
        return null;
    }

    private boolean hasPick(){
        return this.getItemInHand(InteractionHand.MAIN_HAND).is(AMItemRegistry.GHOSTLY_PICKAXE.get());
    }

    private class PathNavigator extends GroundPathNavigation {

        public PathNavigator(EntityUnderminer underminer, Level level) {
            super(underminer, level);
        }

        @Override
        protected boolean canUpdatePath() {
            return !this.mob.isPassenger();
        }

        @Override
        protected Vec3 getTempMobPos() {
            return this.mob.position();
        }
    }


    private class MineGoal extends Goal {

        private BlockPos minePretendPos = null;
        private BlockState minePretendStartState = null;
        private int mineTime = 0;

        public MineGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (EntityUnderminer.this.mineCooldown == 0 && EntityUnderminer.this.hasPick() && !EntityUnderminer.this.isHiding() && !EntityUnderminer.this.isActuallyInAWall() && EntityUnderminer.this.getRandom().nextInt(30) == 0) {
                List<BlockPos> obscuredOres = EntityUnderminer.this.getNearbyObscuredOres(16, 8);
                BlockPos nearest = null;
                double nearestDist = Double.MAX_VALUE;
                if (!obscuredOres.isEmpty()) {
                    for (BlockPos obscuredPos : obscuredOres) {
                        final double dist = EntityUnderminer.this.position().distanceTo(Vec3.atCenterOf(obscuredPos));
                        if (nearestDist > dist) {
                            nearest = obscuredPos;
                            nearestDist = dist;
                        }
                    }
                }
                EntityUnderminer.this.mineAIFlag = false;
                minePretendPos = nearest;
                return minePretendPos != null;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return minePretendPos != null && EntityUnderminer.this.hasPick() && !EntityUnderminer.this.isHiding() && !EntityUnderminer.this.mineAIFlag && minePretendStartState != null && minePretendStartState.equals(level.getBlockState(minePretendPos)) && mineTime < 200;
        }

        public void start() {
            if (minePretendPos != null) {
                minePretendStartState = EntityUnderminer.this.level.getBlockState(minePretendPos);
            }
        }

        public void stop() {
            if(minePretendPos != null && minePretendStartState != null && !minePretendStartState.equals(level.getBlockState(minePretendPos))){
                for(ServerPlayer serverplayerentity : EntityUnderminer.this.level.getEntitiesOfClass(ServerPlayer.class, EntityUnderminer.this.getBoundingBox().inflate(12.0D, 12.0D, 12.0D))) {
                    AMAdvancementTriggerRegistry.UNDERMINE_UNDERMINER.trigger(serverplayerentity);
                }
            }
            minePretendPos = null;
            minePretendStartState = null;
            mineTime = 0;
            EntityUnderminer.this.entityData.set(VISUALLY_MINING, false);
            EntityUnderminer.this.setMiningPos(null);
            EntityUnderminer.this.setMiningProgress(0.0F);
            if(EntityUnderminer.this.resetStackTime > 0){
                EntityUnderminer.this.mineCooldown = 40;
            }else{
                EntityUnderminer.this.mineCooldown = 200 + random.nextInt(200);
            }
        }

        public void tick() {
            if (minePretendPos != null && minePretendStartState != null) {
                mineTime++;
                final double distSqr = EntityUnderminer.this.distanceToSqr(minePretendPos.getX() + 0.5F, minePretendPos.getY() + 0.5F, minePretendPos.getZ() + 0.5F);
                if (distSqr < 6.5F) {
                    EntityUnderminer.this.getNavigation().stop();
                    if(EntityUnderminer.this.getNavigation().isDone()) {
                        EntityUnderminer.this.setMiningPos(minePretendPos);
                        EntityUnderminer.this.setMiningProgress((1F + (float) Math.cos(mineTime * 0.1F + Math.PI)) * 0.5F);
                        final double d1 = minePretendPos.getZ() + 0.5F - EntityUnderminer.this.getZ();
                        final double d3 = minePretendPos.getY() + 0.5F - EntityUnderminer.this.getY();
                        final double d2 = minePretendPos.getX() + 0.5F - EntityUnderminer.this.getX();
                        final float f = Mth.sqrt((float) (d2 * d2 + d1 * d1));
                        EntityUnderminer.this.setYRot(-((float) Mth.atan2(d2, d1)) * Mth.RAD_TO_DEG);
                        EntityUnderminer.this.setXRot((float) (Mth.atan2(d3, f) * (double) Mth.RAD_TO_DEG) + (float) Math.sin(EntityUnderminer.this.tickCount * 0.1F));
                        EntityUnderminer.this.entityData.set(VISUALLY_MINING, true);
                        if (mineTime % 10 == 0) {
                            SoundType soundType = minePretendStartState.getBlock().getSoundType(minePretendStartState, EntityUnderminer.this.level, minePretendPos, EntityUnderminer.this);
                            EntityUnderminer.this.playSound(soundType.getHitSound());
                        }
                    }
                } else {
                    EntityUnderminer.this.entityData.set(VISUALLY_MINING, false);
                    EntityUnderminer.this.setMiningPos(null);
                    EntityUnderminer.this.getNavigation().moveTo(minePretendPos.getX() + 0.5F, minePretendPos.getY() + 0.5F, minePretendPos.getZ() + 0.5F, 1.0D);
                }
            }
        }

    }
}
