package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFleeLight;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
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
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class EntityCockroach extends AnimalEntity implements IShearable, net.minecraftforge.common.IForgeShearable, ITargetsDroppedItems {

    public static final ResourceLocation MARACA_LOOT = new ResourceLocation("alexsmobs", "entities/cockroach_maracas");
    public static final ResourceLocation MARACA_HEADLESS_LOOT = new ResourceLocation("alexsmobs", "entities/cockroach_maracas_headless");
    protected static final EntitySize STAND_SIZE = EntitySize.fixed(0.7F, 0.9F);
    private static final DataParameter<Boolean> DANCING = EntityDataManager.createKey(EntityCockroach.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HEADLESS = EntityDataManager.createKey(EntityCockroach.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> MARACAS = EntityDataManager.createKey(EntityCockroach.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> NEAREST_MUSICIAN = EntityDataManager.createKey(EntityCockroach.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Boolean> RAINBOW = EntityDataManager.createKey(EntityCockroach.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BREADED = EntityDataManager.createKey(EntityCockroach.class, DataSerializers.BOOLEAN);
    public int randomWingFlapTick = 0;
    public float prevDanceProgress;
    public float danceProgress;
    private boolean prevStand = false;
    private boolean isJukeboxing;
    private BlockPos jukeboxPosition;
    private int laCucarachaTimer = 0;
    public int timeUntilNextEgg = this.rand.nextInt(24000) + 24000;

    public EntityCockroach(EntityType type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 6.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    public static boolean isValidLightLevel(IServerWorld p_223323_0_, BlockPos p_223323_1_, Random p_223323_2_) {
        if (p_223323_0_.getLightFor(LightType.SKY, p_223323_1_) > p_223323_2_.nextInt(32)) {
            return false;
        } else {
            int lvt_3_1_ = p_223323_0_.getWorld().isThundering() ? p_223323_0_.getNeighborAwareLightSubtracted(p_223323_1_, 10) : p_223323_0_.getLight(p_223323_1_);
            return lvt_3_1_ <= p_223323_2_.nextInt(8);
        }
    }

    public static boolean canMonsterSpawnInLight(EntityType<? extends EntityCockroach> p_223325_0_, IServerWorld p_223325_1_, SpawnReason p_223325_2_, BlockPos p_223325_3_, Random p_223325_4_) {
        return isValidLightLevel(p_223325_1_, p_223325_3_, p_223325_4_) && canSpawnOn(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
    }

    public static <T extends MobEntity> boolean canCockroachSpawn(EntityType<EntityCockroach> entityType, IServerWorld iServerWorld, SpawnReason reason, BlockPos pos, Random random) {
        return reason == SpawnReason.SPAWNER || !iServerWorld.canSeeSky(pos) && pos.getY() <= 64 && canMonsterSpawnInLight(entityType, iServerWorld, reason, pos, random);
    }

    public boolean canDespawn(double distanceToClosestPlayer) {
        return !preventDespawn();
    }

    public boolean preventDespawn() {
        return super.preventDespawn() || this.isBreaded() || this.isRainbow() || this.isDancing() || this.hasMaracas() || this.isHeadless();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.COCKROACH_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.COCKROACH_HURT;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, false, Ingredient.fromItems(AMItemRegistry.MARACA, Items.SUGAR)));
        this.goalSelector.addGoal(4, new AvoidEntityGoal(this, EntityCentipedeHead.class, 16, 1.3D, 1.0D));
        this.goalSelector.addGoal(4, new AvoidEntityGoal(this, PlayerEntity.class, 8, 1.3D, 1.0D) {
            public boolean shouldExecute() {
                return !EntityCockroach.this.isBreaded() && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(5, new AnimalAIFleeLight(this, 1.0D) {
            public boolean shouldExecute() {
                return !EntityCockroach.this.isBreaded() && super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(6, new RandomWalkingGoal(this, 1.0D, 80));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean prev = super.attackEntityFrom(source, amount);
        if(prev){
            randomWingFlapTick = 5 + rand.nextInt(15);
            if (this.getHealth() <= 1.0F && amount > 0 && !this.isHeadless() && this.getRNG().nextInt(3) == 0) {
                this.setHeadless(true);
                if (!world.isRemote) {
                    for (int i = 0; i < 3; i++) {
                        ((ServerWorld) this.world).spawnParticle(ParticleTypes.SNEEZE, this.getPosXRandom(0.52F), this.getPosYHeight(1D), this.getPosZRandom(0.52F), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
        return prev;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.SUGAR;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Maracas", this.hasMaracas());
        compound.putBoolean("Rainbow", this.isRainbow());
        compound.putBoolean("Dancing", this.isDancing());
        compound.putBoolean("Breaded", this.isBreaded());
        compound.putInt("EggTime", this.timeUntilNextEgg);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setMaracas(compound.getBoolean("Maracas"));
        this.setRainbow(compound.getBoolean("Rainbow"));
        this.setDancing(compound.getBoolean("Dancing"));
        this.setBreaded(compound.getBoolean("Breaded"));
        if (compound.contains("EggTime")) {
            this.timeUntilNextEgg = compound.getInt("EggTime");
        }
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return this.hasMaracas() ? this.isHeadless() ? MARACA_HEADLESS_LOOT : MARACA_LOOT : super.getLootTable();
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return 0.5F - worldIn.getBrightness(pos);
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    public EntitySize getSize(Pose poseIn) {
        return isDancing() ? STAND_SIZE.scale(this.getRenderScale()) : super.getSize(poseIn);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack lvt_3_1_ = p_230254_1_.getHeldItem(p_230254_2_);
        if ((lvt_3_1_.getItem() == Items.SPONGE || lvt_3_1_.getItem() == Items.WET_SPONGE) && this.isAlive() && this.isRainbow()) {
            this.setRainbow(false);
            for (int i = 0; i < 6 + rand.nextInt(3); i++) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(AMItemRegistry.MIMICREAM)), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
            }
            return ActionResultType.SUCCESS;
        } else if (lvt_3_1_.getItem() == AMItemRegistry.MIMICREAM && this.isAlive() && !this.isRainbow()) {
            this.setRainbow(true);
            for (int i = 0; i < 6 + rand.nextInt(3); i++) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, lvt_3_1_), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
            }
            lvt_3_1_.shrink(1);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        } else if (lvt_3_1_.getItem() == AMItemRegistry.MARACA && this.isAlive() && !this.hasMaracas()) {
            this.setMaracas(true);
            lvt_3_1_.shrink(1);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        } else if (lvt_3_1_.getItem() != AMItemRegistry.MARACA && this.isAlive() && this.hasMaracas()) {
            this.setMaracas(false);
            this.setDancing(false);
            this.entityDropItem(new ItemStack(AMItemRegistry.MARACA));
            return ActionResultType.SUCCESS;
        } else {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(DANCING, Boolean.valueOf(false));
        this.dataManager.register(HEADLESS, Boolean.valueOf(false));
        this.dataManager.register(MARACAS, Boolean.valueOf(false));
        this.dataManager.register(NEAREST_MUSICIAN, Optional.empty());
        this.dataManager.register(RAINBOW, Boolean.valueOf(false));
        this.dataManager.register(BREADED, Boolean.valueOf(false));
    }

    public boolean isDancing() {
        return this.dataManager.get(DANCING).booleanValue();
    }

    public void setDancing(boolean dancing) {
        this.dataManager.set(DANCING, dancing);
    }

    public boolean isHeadless() {
        return this.dataManager.get(HEADLESS).booleanValue();
    }

    public void setHeadless(boolean head) {
        this.dataManager.set(HEADLESS, head);
    }

    public boolean hasMaracas() {
        return this.dataManager.get(MARACAS).booleanValue();
    }

    public void setMaracas(boolean head) {
        this.dataManager.set(MARACAS, head);
    }

    public boolean isBreaded() {
        return this.dataManager.get(BREADED).booleanValue();
    }

    public void setBreaded(boolean breaded) {
        this.dataManager.set(BREADED, breaded);
    }

    @Nullable
    public UUID getNearestMusicianId() {
        return this.dataManager.get(NEAREST_MUSICIAN).orElse(null);
    }

    public boolean isRainbow() {
        return this.dataManager.get(RAINBOW).booleanValue();
    }

    public void setRainbow(boolean head) {
        this.dataManager.set(RAINBOW, head);
    }

    public void tick() {
        super.tick();
        prevDanceProgress = danceProgress;
        boolean dance = this.isJukeboxing || isDancing();
        if (this.jukeboxPosition == null || !this.jukeboxPosition.withinDistance(this.getPositionVec(), 3.46D) || !this.world.getBlockState(this.jukeboxPosition).isIn(Blocks.JUKEBOX)) {
            this.isJukeboxing = false;
            this.jukeboxPosition = null;
        }
        if (this.getEyeHeight() > this.getHeight()) {
            this.recalculateSize();
        }
        if (dance && danceProgress < 5F) {
            danceProgress++;
        }
        if (!dance && danceProgress > 0F) {
            danceProgress--;
        }
        if (!this.onGround || rand.nextInt(200) == 0) {
            randomWingFlapTick = 5 + rand.nextInt(15);
        }
        if (randomWingFlapTick > 0) {
            randomWingFlapTick--;
        }
        if (prevStand != dance) {
            if (hasMaracas()) {
                tellOthersImPlayingLaCucaracha();
            }
            this.recalculateSize();
        }
        if (!hasMaracas()) {
            Entity musician = this.getNearestMusician();
            if (musician != null) {
                if (!musician.isAlive() || this.getDistance(musician) > 10 || musician instanceof EntityCockroach && !((EntityCockroach) musician).hasMaracas()) {
                    this.setNearestMusician(null);
                    this.setDancing(false);
                } else {
                    this.setDancing(true);
                }
            }
        }
        if (hasMaracas()) {
            laCucarachaTimer++;
            if (laCucarachaTimer % 20 == 0 && rand.nextFloat() < 0.3F) {
                tellOthersImPlayingLaCucaracha();
            }
            this.setDancing(true);
            if (!this.isSilent()) {
                this.world.setEntityState(this, (byte) 67);
            }
        } else {
            laCucarachaTimer = 0;
        }
        if (!this.world.isRemote && this.isAlive() && !this.isChild() && --this.timeUntilNextEgg <= 0) {
           ItemEntity dropped = this.entityDropItem(AMItemRegistry.COCKROACH_OOTHECA);
            dropped.setDefaultPickupDelay();
            this.timeUntilNextEgg = this.rand.nextInt(24000) + 24000;

        }
        prevStand = dance;
    }

    private void tellOthersImPlayingLaCucaracha() {
        List<EntityCockroach> list = this.world.getEntitiesWithinAABB(EntityCockroach.class, this.getMusicianDistance(), null);
        for (EntityCockroach roach : list) {
            if (!roach.hasMaracas()) {
                roach.setNearestMusician(this.getUniqueID());
            }
        }
    }

    private AxisAlignedBB getMusicianDistance() {
        return this.getBoundingBox().grow(10, 10, 10);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 67) {
            AlexsMobs.PROXY.onEntityStatus(this, id);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public Entity getNearestMusician() {
        UUID id = getNearestMusicianId();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
    }

    public void setNearestMusician(@Nullable UUID uniqueId) {
        this.dataManager.set(NEAREST_MUSICIAN, Optional.ofNullable(uniqueId));
    }

    @OnlyIn(Dist.CLIENT)
    public void setPartying(BlockPos pos, boolean isPartying) {
        this.jukeboxPosition = pos;
        this.isJukeboxing = isPartying;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        EntityCockroach roach = AMEntityRegistry.COCKROACH.create(serverWorld);
        roach.setBreaded(true);
        return roach;
    }

    public boolean isShearable() {
        return this.isAlive() && !this.isChild() && !isHeadless();
    }

    @Override
    public boolean isShearable(@javax.annotation.Nonnull ItemStack item, World world, BlockPos pos) {
        return isShearable();
    }

    @Override
    public void shear(SoundCategory category) {
        this.attackEntityFrom(DamageSource.GENERIC, 0F);
        world.playMovingSound(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, category, 1.0F, 1.0F);
        this.setHeadless(true);
    }

    @javax.annotation.Nonnull
    @Override
    public java.util.List<ItemStack> onSheared(@javax.annotation.Nullable PlayerEntity player, @javax.annotation.Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
        world.playMovingSound(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1.0F, 1.0F);
        this.attackEntityFrom(DamageSource.GENERIC, 0F);
        if (!world.isRemote) {
            for (int i = 0; i < 3; i++) {
                ((ServerWorld) this.world).spawnParticle(ParticleTypes.SNEEZE, this.getPosXRandom(0.52F), this.getPosYHeight(1D), this.getPosZRandom(0.52F), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        this.setHeadless(true);
        return java.util.Collections.emptyList();
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isFood() || stack.getItem() == Items.SUGAR;
    }

    public void travel(Vector3d vec3d) {
        if (this.isDancing() || danceProgress > 0) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }


    @Override
    public void onGetItem(ItemEntity e) {
        if (e.getItem().getItem() == AMItemRegistry.MARACA) {
            this.setMaracas(true);
        } else {
            if (e.getItem().hasContainerItem()) {
                this.entityDropItem(e.getItem().getContainerItem().copy());
            }
            this.heal(5);
            if (e.getItem().getItem() == Items.BREAD || e.getItem().getItem() == Items.SUGAR) {
                this.setBreaded(true);
            }
        }
    }
}
