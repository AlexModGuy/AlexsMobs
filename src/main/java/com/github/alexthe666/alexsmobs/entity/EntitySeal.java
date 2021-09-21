package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.*;

import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;

public class EntitySeal extends Animal implements ISemiAquatic, IHerdPanic, ITargetsDroppedItems {

    private static final EntityDataAccessor<Float> SWIM_ANGLE = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> BASKING = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ARCTIC = SynchedEntityData.defineId(EntitySeal.class, EntityDataSerializers.BOOLEAN);
    public float prevSwimAngle;
    public float prevBaskProgress;
    public float baskProgress;
    public float prevDigProgress;
    public float digProgress;
    public int revengeCooldown = 0;
    public UUID feederUUID = null;
    private int baskingTimer = 0;
    private int swimTimer = -1000;
    private int ticksSinceInWater = 0;
    private boolean isLandNavigator;
    public int fishFeedings = 0;

    protected EntitySeal(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SEAL_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SEAL_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SEAL_HURT;
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.18F);
    }

    public static boolean canSealSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random) {
        Optional<ResourceKey<Biome>> optional = worldIn.getBiomeName(pos);
        if (!Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN))) {
            boolean spawnBlock = BlockTags.getAllTags().getTag(AMTagRegistry.SEAL_SPAWNS).contains(worldIn.getBlockState(pos.below()).getBlock());
            return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
        } else {
            return worldIn.getRawBrightness(pos, 0) > 8 && worldIn.getBlockState(pos.below()).is(Blocks.ICE);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SealAIBask(this));
        this.goalSelector.addGoal(1, new BreathAirGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new AnimalAIHerdPanic(this, 1.6D));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.addGoal(6, new SealAIDiveForItems(this));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0D, 7));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new AvoidEntityGoal(this, EntityOrca.class, 20F, 1.3D, 1.0D));
        this.goalSelector.addGoal(10, new TemptGoal(this, 1.1D, Ingredient.of(ItemTags.getAllTags().getTag(AMTagRegistry.SEAL_FOODSTUFFS)), false));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AquaticMoveController(this, 1.5F);
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev) {
            double range = 15;
            int fleeTime = 100 + getRandom().nextInt(150);
            this.revengeCooldown = fleeTime;
            List<EntitySeal> list = this.level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range / 2, range));
            for (EntitySeal gaz : list) {
                gaz.revengeCooldown = fleeTime;
                gaz.setBasking(false);
            }
            this.setBasking(false);
        }
        return prev;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWIM_ANGLE, 0F);
        this.entityData.define(BASKING, false);
        this.entityData.define(DIGGING, false);
        this.entityData.define(ARCTIC, false);
    }

    public float getSwimAngle() {
        return this.entityData.get(SWIM_ANGLE);
    }

    public void setSwimAngle(float progress) {
        this.entityData.set(SWIM_ANGLE, progress);
    }

    public void tick() {
        super.tick();
        prevBaskProgress = baskProgress;
        prevDigProgress = digProgress;
        prevSwimAngle = this.getSwimAngle();
        boolean dig = isDigging() && isInWaterOrBubble();
        float f2 = (float) -((float) this.getDeltaMovement().y * (double) (180F / (float) Math.PI));
        if (isInWater()) {
            this.xRot = f2 * 2.5F;
        }

        if (isInWater() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!isInWater() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (isBasking() && baskProgress < 5F) {
            baskProgress++;
        }
        if (!isBasking() && baskProgress > 0F) {
            baskProgress--;
        }
        if (dig && digProgress < 5F) {
            digProgress++;
        }
        if (!dig && digProgress > 0F) {
            digProgress--;
        }
        if (dig && level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).canOcclude()) {
            BlockPos posit = this.getBlockPosBelowThatAffectsMyMovement();
            BlockState understate = level.getBlockState(posit);
            for (int i = 0; i < 4 + random.nextInt(2); i++) {
                double particleX = posit.getX() + random.nextFloat();
                double particleY = posit.getY() + 1F;
                double particleZ = posit.getZ() + random.nextFloat();
                double motX = this.random.nextGaussian() * 0.02D;
                double motY = 0.1F + random.nextFloat() * 0.2F;
                double motZ = this.random.nextGaussian() * 0.02D;
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, understate), particleX, particleY, particleZ, motX, motY, motZ);
            }
        }
        if (!this.level.isClientSide) {
            if (isBasking()) {
                if (this.getLastHurtByMob() != null || isInLove() || revengeCooldown > 0 || this.isInWaterOrBubble() || this.getTarget() != null || baskingTimer > 1000 && this.getRandom().nextInt(100) == 0) {
                    this.setBasking(false);
                }
            } else {
                if (this.getTarget() == null && !isInLove() && this.getLastHurtByMob() == null && revengeCooldown == 0 && !isBasking() && baskingTimer == 0 && this.getRandom().nextInt(15) == 0) {
                    if (!isInWaterOrBubble()) {
                        this.setBasking(true);
                    }
                }
            }
            if (revengeCooldown > 0) {
                revengeCooldown--;
            }
            if (revengeCooldown == 0 && this.getLastHurtByMob() != null) {
                this.setLastHurtByMob(null);
            }
            float threshold = 0.05F;
            if (isInWater() && this.yRotO - this.yRot > threshold) {
                this.setSwimAngle(this.getSwimAngle() + 2);
            } else if (isInWater() && this.yRotO - this.yRot < -threshold) {
                this.setSwimAngle(this.getSwimAngle() - 2);
            } else if (this.getSwimAngle() > 0) {
                this.setSwimAngle(Math.max(this.getSwimAngle() - 10, 0));
            } else if (this.getSwimAngle() < 0) {
                this.setSwimAngle(Math.min(this.getSwimAngle() + 10, 0));
            }
            this.setSwimAngle(Mth.clamp(this.getSwimAngle(), -70, 70));
            if (isBasking()) {
                baskingTimer++;
            } else {
                baskingTimer = 0;
            }
            if (isInWater()) {
                swimTimer++;
                ticksSinceInWater = 0;
            } else {
                ticksSinceInWater++;
                swimTimer--;
            }
        }
    }

    public boolean isBasking() {
        return this.entityData.get(BASKING);
    }

    public void setBasking(boolean basking) {
        this.entityData.set(BASKING, basking);
    }

    public boolean isDigging() {
        return this.entityData.get(DIGGING);
    }

    public void setDigging(boolean digging) {
        this.entityData.set(DIGGING, digging);
    }

    public boolean isArctic() {
        return this.entityData.get(ARCTIC);
    }

    public void setArctic(boolean arctic) {
        this.entityData.set(ARCTIC, arctic);
    }

    public int getMaxAirSupply() {
        return 4800;
    }

    protected int increaseAirSupply(int currentAir) {
        return this.getMaxAirSupply();
    }

    public int getMaxHeadXRot() {
        return 1;
    }

    public int getMaxHeadYRot() {
        return 1;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType
            reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setArctic(this.isBiomeArctic(worldIn, this.blockPosition()));
        this.setAirSupply(this.getMaxAirSupply());
        this.xRot = 0.0F;
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Arctic", this.isArctic());
        compound.putBoolean("Basking", this.isBasking());
        compound.putInt("BaskingTimer", this.baskingTimer);
        compound.putInt("SwimTimer", this.swimTimer);
        compound.putInt("FishFeedings", this.fishFeedings);
        if(feederUUID != null){
            compound.putUUID("FeederUUID", feederUUID);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setArctic(compound.getBoolean("Arctic"));
        this.setBasking(compound.getBoolean("Basking"));
        this.baskingTimer = compound.getInt("BaskingTimer");
        this.swimTimer = compound.getInt("SwimTimer");
        this.fishFeedings = compound.getInt("FishFeedings");
        if(compound.hasUUID("FeederUUID")){
            this.feederUUID = compound.getUUID("FeederUUID");
        }
    }

    private boolean isBiomeArctic(LevelAccessor worldIn, BlockPos position) {
        ResourceKey<Biome> biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, worldIn.getBiome(position).getRegistryName());
        return BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
            if (this.isDigging()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.02D, 0.0D));

            }
        } else {
            super.travel(travelVector);
        }

    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.LOBSTER_TAIL;
    }

    @Nullable
    @Override
    public AgableMob getBreedOffspring(ServerLevel serverWorld, AgableMob ageableEntity) {
        EntitySeal seal = AMEntityRegistry.SEAL.create(serverWorld);
        seal.setArctic(this.isBiomeArctic(serverWorld, this.blockPosition()));
        return seal;
    }

    @Override
    public boolean shouldEnterWater() {
        return !shouldLeaveWater() && swimTimer <= -1000;
    }

    @Override
    public boolean shouldLeaveWater() {
        if (!this.getPassengers().isEmpty()) {
            return false;
        }
        if (this.getTarget() != null && !this.getTarget().isInWater()) {
            return true;
        }
        return swimTimer > 600;
    }

    @Override
    public boolean shouldStopMoving() {
        return isBasking();
    }

    @Override
    public int getWaterSearchRange() {
        return 32;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return ItemTags.getAllTags().getTag(AMTagRegistry.SEAL_FOODSTUFFS).contains(stack.getItem());
    }

    @Override
    public void onGetItem(ItemEntity e) {
        if (ItemTags.getAllTags().getTag(AMTagRegistry.SEAL_FOODSTUFFS).contains(e.getItem().getItem())) {
            fishFeedings++;
            this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (fishFeedings >= 3) {
                feederUUID = e.getThrower();
                fishFeedings = 0;
            }
        } else {
            feederUUID = null;
        }
        this.heal(10);
    }

    @Override
    public void onPanic() {

    }

    @Override
    public boolean canPanic() {
        return !isBasking();
    }
}
