package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntitySeal extends AnimalEntity implements ISemiAquatic, IHerdPanic, ITargetsDroppedItems {

    private static final DataParameter<Float> SWIM_ANGLE = EntityDataManager.createKey(EntitySeal.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> BASKING = EntityDataManager.createKey(EntitySeal.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DIGGING = EntityDataManager.createKey(EntitySeal.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ARCTIC = EntityDataManager.createKey(EntitySeal.class, DataSerializers.BOOLEAN);
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

    protected EntitySeal(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SealAIBask(this));
        this.goalSelector.addGoal(1, new BreatheAirGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new AnimalAIHerdPanic(this, 1.6D));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.addGoal(6, new SealAIDiveForItems(this));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0D, 7));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(9, new AvoidEntityGoal(this, EntityOrca.class, 20F, 1.3D, 1.0D));
        this.goalSelector.addGoal(10, new TemptGoal(this, 1.1D, Ingredient.fromTag(ItemTags.getCollection().get(AMTagRegistry.SEAL_FOODSTUFFS)), false));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new AquaticMoveController(this, 1.5F);
            this.navigator = new SemiAquaticPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean prev = super.attackEntityFrom(source, amount);
        if (prev) {
            double range = 15;
            int fleeTime = 100 + getRNG().nextInt(150);
            this.revengeCooldown = fleeTime;
            List<EntitySeal> list = this.world.getEntitiesWithinAABB(this.getClass(), this.getBoundingBox().grow(range, range / 2, range));
            for (EntitySeal gaz : list) {
                gaz.revengeCooldown = fleeTime;
                gaz.setBasking(false);
            }
            this.setBasking(false);
        }
        return prev;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SWIM_ANGLE, 0F);
        this.dataManager.register(BASKING, false);
        this.dataManager.register(DIGGING, false);
        this.dataManager.register(ARCTIC, false);
    }

    public float getSwimAngle() {
        return this.dataManager.get(SWIM_ANGLE);
    }

    public void setSwimAngle(float progress) {
        this.dataManager.set(SWIM_ANGLE, progress);
    }

    public void tick() {
        super.tick();
        prevBaskProgress = baskProgress;
        prevDigProgress = digProgress;
        prevSwimAngle = this.getSwimAngle();
        boolean dig = isDigging() && isInWaterOrBubbleColumn();
        float f2 = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
        if (isInWater()) {
            this.rotationPitch = f2 * 2.5F;
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
        if (dig && world.getBlockState(this.getPositionUnderneath()).isSolid()) {
            BlockPos posit = this.getPositionUnderneath();
            BlockState understate = world.getBlockState(posit);
            for (int i = 0; i < 4 + rand.nextInt(2); i++) {
                double particleX = posit.getX() + rand.nextFloat();
                double particleY = posit.getY() + 1F;
                double particleZ = posit.getZ() + rand.nextFloat();
                double motX = this.rand.nextGaussian() * 0.02D;
                double motY = 0.1F + rand.nextFloat() * 0.2F;
                double motZ = this.rand.nextGaussian() * 0.02D;
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, understate), particleX, particleY, particleZ, motX, motY, motZ);
            }
        }
        if (!this.world.isRemote) {
            if (isBasking()) {
                if (this.getRevengeTarget() != null || isInLove() || revengeCooldown > 0 || this.isInWaterOrBubbleColumn() || this.getAttackTarget() != null || baskingTimer > 1000 && this.getRNG().nextInt(100) == 0) {
                    this.setBasking(false);
                }
            } else {
                if (this.getAttackTarget() == null && !isInLove() && this.getRevengeTarget() == null && revengeCooldown == 0 && !isBasking() && baskingTimer == 0 && this.getRNG().nextInt(15) == 0) {
                    if (!isInWaterOrBubbleColumn()) {
                        this.setBasking(true);
                    }
                }
            }
            if (revengeCooldown > 0) {
                revengeCooldown--;
            }
            if (revengeCooldown == 0 && this.getRevengeTarget() != null) {
                this.setRevengeTarget(null);
            }
            float threshold = 0.05F;
            if (isInWater() && this.prevRotationYaw - this.rotationYaw > threshold) {
                this.setSwimAngle(this.getSwimAngle() + 2);
            } else if (isInWater() && this.prevRotationYaw - this.rotationYaw < -threshold) {
                this.setSwimAngle(this.getSwimAngle() - 2);
            } else if (this.getSwimAngle() > 0) {
                this.setSwimAngle(Math.max(this.getSwimAngle() - 10, 0));
            } else if (this.getSwimAngle() < 0) {
                this.setSwimAngle(Math.min(this.getSwimAngle() + 10, 0));
            }
            this.setSwimAngle(MathHelper.clamp(this.getSwimAngle(), -70, 70));
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
        return this.dataManager.get(BASKING);
    }

    public void setBasking(boolean basking) {
        this.dataManager.set(BASKING, basking);
    }

    public boolean isDigging() {
        return this.dataManager.get(DIGGING);
    }

    public void setDigging(boolean digging) {
        this.dataManager.set(DIGGING, digging);
    }

    public boolean isArctic() {
        return this.dataManager.get(ARCTIC);
    }

    public void setArctic(boolean arctic) {
        this.dataManager.set(ARCTIC, arctic);
    }

    public int getMaxAir() {
        return 4800;
    }

    protected int determineNextAir(int currentAir) {
        return this.getMaxAir();
    }

    public int getVerticalFaceSpeed() {
        return 1;
    }

    public int getHorizontalFaceSpeed() {
        return 1;
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason
            reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setArctic(this.isBiomeArctic(worldIn, this.getPosition()));
        this.setAir(this.getMaxAir());
        this.rotationPitch = 0.0F;
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Arctic", this.isArctic());
        compound.putBoolean("Basking", this.isBasking());
        compound.putInt("BaskingTimer", this.baskingTimer);
        compound.putInt("SwimTimer", this.swimTimer);
        compound.putInt("FishFeedings", this.fishFeedings);
        if(feederUUID != null){
            compound.putUniqueId("FeederUUID", feederUUID);
        }
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setArctic(compound.getBoolean("Arctic"));
        this.setBasking(compound.getBoolean("Basking"));
        this.baskingTimer = compound.getInt("BaskingTimer");
        this.swimTimer = compound.getInt("SwimTimer");
        this.fishFeedings = compound.getInt("FishFeedings");
        if(compound.hasUniqueId("FeederUUID")){
            this.feederUUID = compound.getUniqueId("FeederUUID");
        }
    }

    private boolean isBiomeArctic(IWorld worldIn, BlockPos position) {
        RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, worldIn.getBiome(position).getRegistryName());
        return BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
            if (this.isDigging()) {
                this.setMotion(this.getMotion().add(0.0D, -0.02D, 0.0D));

            }
        } else {
            super.travel(travelVector);
        }

    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.LOBSTER_TAIL;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.SEAL.create(serverWorld);
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
        if (this.getAttackTarget() != null && !this.getAttackTarget().isInWater()) {
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
        return ItemTags.getCollection().get(AMTagRegistry.SEAL_FOODSTUFFS).contains(stack.getItem());
    }

    @Override
    public void onGetItem(ItemEntity e) {
        if (ItemTags.getCollection().get(AMTagRegistry.SEAL_FOODSTUFFS).contains(e.getItem().getItem())) {
            fishFeedings++;
            this.playSound(SoundEvents.ENTITY_CAT_EAT, this.getSoundVolume(), this.getSoundPitch());
            if (fishFeedings >= 3) {
                feederUUID = e.getThrowerId();
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
