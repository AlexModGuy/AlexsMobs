package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.message.MessageStartDancing;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityRainFrog extends Animal implements ITargetsDroppedItems,IDancingMob {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STANCE_TIME = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TIME = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANCE_TIME = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> BURROWED = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DISTURBED = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.BOOLEAN);
    public float burrowProgress;
    public float prevBurrowProgress;
    public float danceProgress;
    public float prevDanceProgress;
    public float attackProgress;
    public float prevAttackProgress;
    public float stanceProgress;
    public float prevStanceProgress;
    private int burrowCooldown = 0;
    private int weatherCooldown = 0;
    private boolean isJukeboxing;
    private BlockPos jukeboxPosition;

    protected EntityRainFrog(EntityType<? extends Animal> rainFrog, Level lvl) {
        super(rainFrog, lvl);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0D, Ingredient.of(AMTagRegistry.INSECT_ITEMS), false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, EntityRattlesnake.class, 9, 1.3D, 1.0D));
        this.goalSelector.addGoal(5, new AIBurrow());
        this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 20, 1.0D, 10, 7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
    }

    public static boolean canRainFrogSpawn(EntityType animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        boolean spawnBlock = worldIn.getBlockState(pos.below()).is(BlockTags.SAND);
        return spawnBlock && worldIn.getLevelData() != null && (worldIn.getLevelData().isThundering() || worldIn.getLevelData().isRaining());
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.rainFrogSpawnRolls, this.getRandom(), spawnReasonIn);
    }


    public boolean isBurrowed() {
        return this.entityData.get(BURROWED).booleanValue();
    }

    public void setBurrowed(boolean burrowed) {
        this.entityData.set(BURROWED, Boolean.valueOf(burrowed));
    }

    public boolean isDisturbed() {
        return this.entityData.get(DISTURBED).booleanValue();
    }

    public void setDisturbed(boolean burrowed) {
        this.entityData.set(DISTURBED, Boolean.valueOf(burrowed));
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public int getStanceTime() {
        return this.entityData.get(STANCE_TIME).intValue();
    }

    public void setStanceTime(int stanceTime) {
        this.entityData.set(STANCE_TIME, Integer.valueOf(stanceTime));
    }

    public int getAttackTime() {
        return this.entityData.get(ATTACK_TIME).intValue();
    }

    public void setAttackTime(int attackTime) {
        this.entityData.set(ATTACK_TIME, Integer.valueOf(attackTime));
    }

    public int getDanceTime() {
        return this.entityData.get(DANCE_TIME).intValue();
    }

    public void setDanceTime(int danceTime) {
        this.entityData.set(DANCE_TIME, Integer.valueOf(danceTime));
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.INSECT_ITEMS);
    }

    @javax.annotation.Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        EntityRainFrog frog = AMEntityRegistry.RAIN_FROG.get().create(p_241840_1_);
        frog.setVariant(this.getVariant());
        frog.setDisturbed(true);
        return frog;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(STANCE_TIME, 0);
        this.entityData.define(ATTACK_TIME, 0);
        this.entityData.define(DANCE_TIME, 0);
        this.entityData.define(BURROWED, false);
        this.entityData.define(DISTURBED, false);
    }

    public void tick() {
        super.tick();
        prevBurrowProgress = burrowProgress;
        prevDanceProgress = danceProgress;
        prevAttackProgress = attackProgress;
        prevStanceProgress = stanceProgress;
        if (this.isBurrowed() && burrowProgress < 5F) {
            burrowProgress += 0.5F;
        }
        if (!this.isBurrowed() && burrowProgress > 0F) {
            burrowProgress -= 0.5F;
        }
        if (this.burrowCooldown > 0) {
            this.burrowCooldown--;
        }
        if (this.getStanceTime() > 0) {
            this.setStanceTime(this.getStanceTime() - 1);
            if (this.stanceProgress < 5F) {
                this.stanceProgress++;
            }
        } else {
            if (this.stanceProgress > 0F) {
                this.stanceProgress--;
            }
        }
        if (this.getAttackTime() > 0) {
            this.setAttackTime(this.getAttackTime() - 1);
            if (this.attackProgress < 5F) {
                this.attackProgress += 2.5F;
            }
        } else {
            if (this.attackProgress > 0F) {
                this.attackProgress -= 0.5F;
            }
        }
        boolean dancing = this.getDanceTime() > 0 || this.isJukeboxing;
        if(dancing && this.danceProgress < 5f){
            this.danceProgress++;
        }
        if (!dancing && this.danceProgress > 0F) {
            this.danceProgress--;
        }
        if (this.getDanceTime() > 0) {
            this.setBurrowed(false);
            this.setDanceTime(this.getDanceTime() - 1);
            if(this.getDanceTime() == 1 && weatherCooldown <= 0 && level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)){
                changeWeather();
            }
        }
        if(weatherCooldown > 0){
            weatherCooldown--;
        }
        if (this.jukeboxPosition == null || !this.jukeboxPosition.closerToCenterThan(this.position(), 15) || !this.level.getBlockState(this.jukeboxPosition).is(Blocks.JUKEBOX)) {
            this.isJukeboxing = false;
            this.setDanceTime(0);
            this.jukeboxPosition = null;
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (prev && source.getDirectEntity() instanceof LivingEntity) {
            if (this.getStanceTime() <= 0) {
                this.setStanceTime(30 + random.nextInt(20));
            }
            this.setBurrowed(false);
        }
        return prev;
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !requiresCustomPersistence();
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.isDisturbed();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public boolean isSleeping() {
        return this.isBurrowed();
    }

    public void calculateEntityAnimation(LivingEntity mob, boolean flying) {
        mob.animationSpeedOld = mob.animationSpeed;
        double d0 = mob.getX() - mob.xo;
        double d1 = flying ? mob.getY() - mob.yo : 0.0D;
        double d2 = mob.getZ() - mob.zo;
        float f = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 16.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        mob.animationSpeed += (f - mob.animationSpeed) * 0.4F;
        mob.animationPosition += mob.animationSpeed;
    }

    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setVariant(random.nextInt(3));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setDisturbed(compound.getBoolean("Disturbed"));
        this.setVariant(compound.getInt("Variant"));
        this.weatherCooldown = compound.getInt("WeatherCooldown");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Disturbed", isDisturbed());
        compound.putInt("Variant", getVariant());
        compound.putInt("WeatherCooldown", this.weatherCooldown);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (item instanceof ShovelItem && (this.isBurrowed() || !this.isDisturbed()) && !level.isClientSide) {
            this.ambientSoundTime = 1000;
            if (!player.isCreative()) {
                itemstack.hurt(1, this.getRandom(), player instanceof ServerPlayer ? (ServerPlayer) player : null);
            }
            this.setStanceTime(20 + random.nextInt(30));
            this.setBurrowed(false);
            this.setDisturbed(true);
            this.burrowCooldown += 150 + random.nextInt(120);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SAND_BREAK, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    public void travel(Vec3 travelVector) {
        if (this.isBurrowed() || this.getDanceTime() > 0) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    @Override
    public void onFindTarget(ItemEntity e) {
        this.setBurrowed(false);
        this.burrowCooldown += 50;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.is(AMTagRegistry.INSECT_ITEMS);
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.setAttackTime(10);
        this.heal(2);
    }

    private void changeWeather(){
        int time = 24000 + 1200 * random.nextInt(10);
        int type = 0;
        if(!this.level.isRaining()){
            type = random.nextInt(1) + 1;
        }
        if(this.level instanceof ServerLevel serverLevel){
            if(type == 0){
                serverLevel.setWeatherParameters(time, 0, false, false);
            }else {
                serverLevel.setWeatherParameters(0, time, true, type == 2);
            }
        }
        weatherCooldown = time + 24000;
    }

    public void setRecordPlayingNearby(BlockPos pos, boolean isPartying) {
        AlexsMobs.sendMSGToServer(new MessageStartDancing(this.getId(), isPartying, pos));
        if (isPartying) {
            this.setJukeboxPos(pos);
        } else {
            this.setJukeboxPos(null);
        }
    }

    @Override
    public void setDancing(boolean dancing) {
        this.setDanceTime(dancing && weatherCooldown == 0 ? 240 + random.nextInt(200) : 0);
    }

    @Override
    public void setJukeboxPos(BlockPos pos) {
        this.jukeboxPosition = pos;
    }

    protected SoundEvent getAmbientSound() {
        return getStanceTime() > 0 ? AMSoundRegistry.RAIN_FROG_HURT.get() : AMSoundRegistry.RAIN_FROG_IDLE.get();
    }

    public int getAmbientSoundInterval() {
        return getStanceTime() > 0 ? 10 : 80;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.RAIN_FROG_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.RAIN_FROG_HURT.get();
    }


    private class AIBurrow extends Goal {
        private BlockPos sand = null;
        private int burrowedTime = 0;

        public AIBurrow() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!EntityRainFrog.this.isBurrowed() && EntityRainFrog.this.burrowCooldown == 0 && EntityRainFrog.this.random.nextInt(200) == 0) {
                this.burrowedTime = 0;
                sand = findSand();
                return sand != null;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return burrowedTime < 300;
        }

        public BlockPos findSand() {
            BlockPos blockpos = null;

            for (BlockPos blockpos1 : BlockPos.betweenClosed(Mth.floor(EntityRainFrog.this.getX() - 4.0D), Mth.floor(EntityRainFrog.this.getY() - 1.0D), Mth.floor(EntityRainFrog.this.getZ() - 4.0D), Mth.floor(EntityRainFrog.this.getX() + 4.0D), EntityRainFrog.this.getBlockY(), Mth.floor(EntityRainFrog.this.getZ() + 4.0D))) {
                if (EntityRainFrog.this.level.getBlockState(blockpos1).is(BlockTags.SAND)) {
                    blockpos = blockpos1;
                    break;
                }
            }
            return blockpos;
        }

        public void tick() {
            if (EntityRainFrog.this.isBurrowed()) {
                burrowedTime++;
                if (!EntityRainFrog.this.getBlockStateOn().is(BlockTags.SAND)) {
                    EntityRainFrog.this.setBurrowed(false);
                }
            } else if (sand != null) {
                EntityRainFrog.this.getNavigation().moveTo(sand.getX() + 0.5F, sand.getY() + 1F, sand.getZ() + 0.5F, 1F);
                if (EntityRainFrog.this.getBlockStateOn().is(BlockTags.SAND)) {
                    EntityRainFrog.this.setBurrowed(true);
                    EntityRainFrog.this.getNavigation().stop();
                    sand = null;
                } else {
                    EntityRainFrog.this.setBurrowed(false);
                }
            }
        }

        public void stop() {
            EntityRainFrog.this.setBurrowed(false);
            EntityRainFrog.this.burrowCooldown = 120 + random.nextInt(1200);
            this.sand = null;
        }
    }
}
