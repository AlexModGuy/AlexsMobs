package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHerdPanic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.List;

public class EntityGazelle extends Animal implements IAnimatedEntity, IHerdPanic {

    private int animationTick;
    private Animation currentAnimation;
    public static final Animation ANIMATION_FLICK_EARS = Animation.create(20);
    public static final Animation ANIMATION_FLICK_TAIL = Animation.create(14);
    public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
    private boolean hasSpedUp = false;
    private int revengeCooldown = 0;
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(EntityGazelle.class, EntityDataSerializers.BOOLEAN);

    protected EntityGazelle(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AnimalAIHerdPanic(this, 1.1D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 100, 1.0D, 25, 7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GAZELLE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GAZELLE_HURT.get();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.gazelleSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    public int getMaxSpawnClusterSize() {
        return 8;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if(prev){
            double range = 15;
            int fleeTime = 100 + getRandom().nextInt(150);
            this.revengeCooldown = fleeTime;
            List<? extends EntityGazelle> list = this.level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(range, range/2, range));
            for(EntityGazelle gaz : list){
                gaz.revengeCooldown = fleeTime;

            }
        }
        return prev;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, Boolean.valueOf(false));
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING).booleanValue();
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, Boolean.valueOf(running));
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.WHEAT || stack.getItem() == AMItemRegistry.ACACIA_BLOSSOM.get();
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public void tick() {
        super.tick();
        if(!level.isClientSide && this.getAnimation() == NO_ANIMATION && getRandom().nextInt(70) == 0 && (this.getLastHurtByMob() == null || this.distanceTo(this.getLastHurtByMob()) > 30)){
            if(level.getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && getRandom().nextInt(3) == 0){
                this.setAnimation(ANIMATION_EAT_GRASS);
            }else{
                this.setAnimation(getRandom().nextBoolean()  ? ANIMATION_FLICK_EARS : ANIMATION_FLICK_TAIL);
            }
        }
        if(!this.level.isClientSide){
            if(revengeCooldown >= 0){
                revengeCooldown--;
            }
            if(revengeCooldown == 0 && this.getLastHurtByMob() != null){
                this.setLastHurtByMob(null);
            }
            this.setRunning(revengeCooldown > 0);
            if(isRunning() && !hasSpedUp){
                hasSpedUp = true;
                this.setSprinting(true);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.475F);
            }
            if(!isRunning() && hasSpedUp){
                hasSpedUp = false;
                this.setSprinting(false);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("GazelleRunning", this.isRunning());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setRunning(compound.getBoolean("GazelleRunning"));
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_FLICK_EARS, ANIMATION_FLICK_TAIL, ANIMATION_EAT_GRASS};
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        return AMEntityRegistry.GAZELLE.get().create(p_241840_1_);
    }

    @Override
    public void onPanic() {
    }

    @Override
    public boolean canPanic() {
        return true;
    }
}
