package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EntityBananaSlug extends Animal {

    private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntityBananaSlug.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityBananaSlug.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityBananaSlug.class, EntityDataSerializers.INT);
    private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public float trailYaw;
    public float prevTrailYaw;
    public float trailVisability;
    public float prevTrailVisability;

    public float attachChangeProgress = 0F;
    public float prevAttachChangeProgress = 0F;
    public Direction prevAttachDir = Direction.DOWN;
    public int timeUntilSlime = this.random.nextInt(12000) + 24000;
    protected EntityBananaSlug(EntityType<? extends Animal> animal, Level level) {
        super(animal, level);
        prevTrailYaw = this.yBodyRot;
        trailYaw = this.yBodyRot;

    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation(this, worldIn);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BANANA_SLUG_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BANANA_SLUG_HURT.get();
    }


    public static boolean checkBananaSlugSpawnRules(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return !worldIn.getBlockState(pos.below()).isAir();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.bananaSlugSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(ATTACHED_FACE, Direction.DOWN);
        this.entityData.define(VARIANT, 0);
    }

    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void onInsideBlock(BlockState state) {

    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setVariant(random.nextInt(4));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }


    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean onClimbable() {
        return this.isBesideClimbableBlock() ;
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0 && this.getAttachmentFacing() != Direction.DOWN;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.BROWN_MUSHROOM);
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(CLIMBING, b0);
    }

    public Direction getAttachmentFacing() {
        return this.entityData.get(ATTACHED_FACE);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0D, Ingredient.of(Items.BROWN_MUSHROOM), false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new AnimalAIWanderRanged(this, 40, 1.0D, 10, 7));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 5.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.1F);
    }

    public void tick(){
        super.tick();
        this.prevTrailYaw = trailYaw;
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getMaxHeadYRot());
        this.trailYaw = Mth.approachDegrees(this.trailYaw, yBodyRot, 2);
        this.prevTrailVisability = trailVisability;
        prevAttachChangeProgress = attachChangeProgress;
        boolean showTrail = isTrailVisible() && this.getDeltaMovement().length() > 0.03F;
        if (this.prevAttachDir != this.getAttachmentFacing()) {
            if (attachChangeProgress < 5.0F) {
                attachChangeProgress += 1F;
                this.trailYaw = yBodyRot;
            } else if (attachChangeProgress >= 5.0F) {
                this.prevAttachDir = this.getAttachmentFacing();
            }
        } else {
            this.attachChangeProgress = 5.0F;
        }

        if(trailVisability < 1.0F && showTrail){
            trailVisability = Math.min(1.0F, trailVisability + 0.1F);
        }
        if(trailVisability > 0.0F && !showTrail){
            float dec = this.getDeltaMovement().length() > 0.03F ? 1.0F : 0.1F;
            trailVisability = Math.max(0.0F, trailVisability - dec);
        }

        Vec3 vector3d = this.getDeltaMovement();
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
            this.setBesideClimbableBlock(this.horizontalCollision || this.verticalCollision && !this.onGround());
            if (this.onGround() || this.isInWaterOrBubble() || this.isInLava()) {
                this.entityData.set(ATTACHED_FACE, Direction.DOWN);
            } else  if (this.verticalCollision) {
                this.entityData.set(ATTACHED_FACE, Direction.UP);
            }else {
                boolean flag = false;
                Direction closestDirection = Direction.DOWN;
                double closestDistance = 100;
                for (Direction dir : POSSIBLE_DIRECTIONS) {
                    BlockPos antPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
                    BlockPos offsetPos = antPos.relative(dir);
                    Vec3 offset = Vec3.atCenterOf(offsetPos);
                    if (closestDistance > this.position().distanceTo(offset) && level().loadedAndEntityCanStandOnFace(offsetPos, this, dir.getOpposite())) {
                        closestDistance = this.position().distanceTo(offset);
                        closestDirection = dir;
                    }
                }
                this.entityData.set(ATTACHED_FACE, closestDirection);
            }
        }
        boolean flag = false;
        if (this.getAttachmentFacing() != Direction.DOWN) {
            if(this.getAttachmentFacing() == Direction.UP){
                this.setDeltaMovement(this.getDeltaMovement().add(0, 1, 0));
            }else{
                if (!this.horizontalCollision && this.getAttachmentFacing() != Direction.UP) {
                    Vec3 vec = Vec3.atLowerCornerOf(this.getAttachmentFacing().getNormal());
                    this.setDeltaMovement(this.getDeltaMovement().add(vec.normalize().multiply(0.1F, 0.1F, 0.1F)));
                }
                if (!this.onGround() && vector3d.y < 0.0D) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
                    flag = true;
                }
            }
        }
        if(this.getAttachmentFacing() == Direction.UP) {
            this.setNoGravity(true);
            this.setDeltaMovement(vector3d.multiply(0.7D, 1D, 0.7D));
        }else{
            this.setNoGravity(false);
        }
        if (!flag) {
            if (this.onClimbable()) {
                this.setDeltaMovement(vector3d.multiply(1.0D, 0.4D, 1.0D));
            }
        }
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && --this.timeUntilSlime <= 0) {
            this.spawnAtLocation(AMItemRegistry.BANANA_SLUG_SLIME.get());
            this.timeUntilSlime = this.random.nextInt(12000) + 24000;
        }
    }

    protected float getJumpPower() {
        return super.getJumpPower();
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 4;
    }


    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    private boolean isTrailVisible() {
        if(this.isInWaterOrBubble()){
            return false;
        }
        if(this.onGround()){
            Vec3 modelBack = new Vec3(0, -0.1F, isBaby() ? -0.35F : -0.7F).yRot(-this.trailYaw * Mth.DEG_TO_RAD);
            Vec3 slugBack = this.position().add(modelBack);
            BlockPos backPos = AMBlockPos.fromVec3(slugBack);
            BlockState state = level().getBlockState(backPos);
            VoxelShape shape = state.getCollisionShape(level(), backPos);
            if(shape.isEmpty()){
                return false;
            }else{
                Optional<Vec3> closest = shape.closestPointTo(modelBack.add(0, 1, 0));
                return closest.isPresent() && Math.min((float)closest.get().y, 1.0F) >= 0.8F;
            }
        }else if(this.getAttachmentFacing().getAxis() != Direction.Axis.Y){
            BlockPos pos = this.blockPosition().relative(this.getAttachmentFacing()).above(this.getDeltaMovement().y <= -0.001F ? 1 : -1);
            BlockState state = level().getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level(), pos);
            return !shape.isEmpty();
        }
        return this.getAttachmentFacing() != Direction.DOWN;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (ATTACHED_FACE.equals(entityDataAccessor)) {
            this.prevAttachChangeProgress = 0.0F;
            this.attachChangeProgress = 0.0F;
        }
    }


    public void calculateEntityAnimation( boolean flying) {
        float f1 = (float)Mth.length(this.getX() - this.xo, 0.5F * (this.getY() - this.yo), this.getZ() - this.zo);
        float f2 = Math.min(f1 * 16.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        EntityBananaSlug slug = AMEntityRegistry.BANANA_SLUG.get().create(level());
        slug.setVariant(this.getVariant());
        return slug;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("SlimeTime", this.timeUntilSlime);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SlimeTime")) {
            this.timeUntilSlime = compound.getInt("SlimeTime");
        }
        this.setVariant(compound.getInt("Variant"));
    }


    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int i) {
        this.entityData.set(VARIANT, i);
    }

}
