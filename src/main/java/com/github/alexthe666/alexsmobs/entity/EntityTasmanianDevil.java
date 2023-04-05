package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class EntityTasmanianDevil extends Animal implements IAnimatedEntity, ITargetsDroppedItems {

    private int animationTick;
    private Animation currentAnimation;
    public static final Animation ANIMATION_HOWL = Animation.create(40);
    public static final Animation ANIMATION_ATTACK = Animation.create(8);
    private static final EntityDataAccessor<Boolean> BASKING = SynchedEntityData.defineId(EntityTasmanianDevil.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityTasmanianDevil.class, EntityDataSerializers.BOOLEAN);
    public float prevBaskProgress;
    public float prevSitProgress;
    public float baskProgress;
    public float sitProgress;
    private int sittingTime;
    private int maxSitTime;
    private int scareMobsTime = 0;

    protected EntityTasmanianDevil(EntityType type, Level world) {
        super(type, world);
    }

    public boolean shouldMove() {
        return !isSitting() && !isBasking();
    }


    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.TASMANIAN_DEVIL_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TASMANIAN_DEVIL_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TASMANIAN_DEVIL_HURT.get();
    }
    
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5D, true));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, Ingredient.of(Items.ROTTEN_FLESH), false){
            public void tick(){
                super.tick();
                if(EntityTasmanianDevil.this.getAnimation() == NO_ANIMATION){
                    EntityTasmanianDevil.this.setBasking(false);
                    EntityTasmanianDevil.this.setSitting(false);
                }
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1D, 60));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, EntityTasmanianDevil.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, 120, false, false, (p_213487_0_) -> {
            return p_213487_0_ instanceof Chicken || p_213487_0_ instanceof Rabbit;
        }));
        this.targetSelector.addGoal(3, new CreatureAITargetItems(this, false, 30));
    }

    public void killed(ServerLevel world, LivingEntity entity) {
        if(this.getRandom().nextBoolean() && (entity instanceof Animal || entity.getMobType() == MobType.UNDEAD)){
            entity.spawnAtLocation(new ItemStack(Items.BONE));
        }
    }


    public void travel(Vec3 vec3d) {
        if (!this.shouldMove()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }


    public void setSitting(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BASKING, Boolean.valueOf(false));
        this.entityData.define(SITTING, Boolean.valueOf(false));
    }


    public boolean isBasking() {
        return this.entityData.get(BASKING).booleanValue();
    }

    public void setBasking(boolean basking) {
        this.entityData.set(BASKING, Boolean.valueOf(basking));
    }


    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_DAMAGE, 2F);
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties() != null && stack.getItem().getFoodProperties().isMeat() && stack.getItem() != Items.ROTTEN_FLESH;
    }

    public void tick(){
        super.tick();
        this.prevBaskProgress = this.baskProgress;
        this.prevSitProgress = this.sitProgress;
        if (this.isSitting() && sitProgress < 5) {
            sitProgress += 1;
        }
        if (!this.isSitting() && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (this.isBasking() && baskProgress < 5) {
            baskProgress += 1;
        }
        if (!this.isBasking() && baskProgress > 0) {
            baskProgress -= 1;
        }
        if (!level.isClientSide && this.getTarget() != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 5 && this.hasLineOfSight(this.getTarget())) {
            float f1 = this.getYRot() * ((float) Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
            getTarget().knockback(1F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
            this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
        if (!level.isClientSide && (isSitting() || isBasking()) && ++sittingTime > maxSitTime) {
            this.setSitting(false);
            this.setBasking(false);
            sittingTime = 0;
            maxSitTime = 75 + random.nextInt(50);
        }
        if (!level.isClientSide && this.getDeltaMovement().lengthSqr() < 0.03D && this.getAnimation() == NO_ANIMATION && !this.isBasking() && !this.isSitting() && random.nextInt(100) == 0) {
            sittingTime = 0;
            maxSitTime = 100 + random.nextInt(550);
            if(this.getRandom().nextBoolean()){
                this.setSitting(true);
                this.setBasking(false);
            }else{
                this.setSitting(false);
                this.setBasking(true);
            }
        }
        if(this.getAnimation() == ANIMATION_HOWL && this.getAnimationTick() == 1){
            this.gameEvent(GameEvent.ENTITY_ROAR);
            this.playSound(AMSoundRegistry.TASMANIAN_DEVIL_ROAR.get(), this.getSoundVolume() * 2F, this.getVoicePitch());
        }
        if(this.getAnimation() == ANIMATION_HOWL && this.getAnimationTick() > 3){
            scareMobsTime = 40;
        }
        if(scareMobsTime > 0) {
            List<Monster> list = this.level.getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(16, 8, 16));
            for (Monster e : list) {
                e.setTarget(null);
                e.setLastHurtByMob(null);
                if(scareMobsTime % 5 == 0){
                    Vec3 vec = LandRandomPos.getPosAway(e, 20, 7, this.position());
                    if(vec != null){
                        e.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.5D);
                    }
                }

            }
            scareMobsTime--;
        }
        if(this.getTarget() != null && this.getTarget().isAlive() && (this.getLastHurtByMob() == null || !this.getLastHurtByMob().isAlive()) ){
            this.setLastHurtByMob(this.getTarget());
        }
        if((this.isSitting() || this.isBasking()) && (this.getTarget() != null || this.isInLove())) {
            this.setSitting(false);
            this.setBasking(false);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (item == Items.ROTTEN_FLESH && this.getAnimation() != ANIMATION_HOWL) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.FOX_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.spawnAtLocation(item.getCraftingRemainingItem(itemstack));
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setAnimation(ANIMATION_HOWL);
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ATTACK);
        }
        return true;
    }


    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
        if(animation == ANIMATION_HOWL){
            this.setSitting(true);
            this.setBasking(false);
            maxSitTime = Math.max(25, maxSitTime);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_ATTACK, ANIMATION_HOWL};
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.TASMANIAN_DEVIL.get().create(serverWorld);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties() != null && stack.getItem().getFoodProperties().isMeat() || stack.getItem() == Items.BONE;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.gameEvent(GameEvent.EAT);
        if(e.getItem().getItem() == Items.BONE){
            dropBonemeal();
            this.playSound(SoundEvents.SKELETON_STEP, this.getSoundVolume(), this.getVoicePitch());
        }else{
            this.playSound(SoundEvents.FOX_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5);
        }
    }

    public void dropBonemeal(){
        ItemStack stack = new ItemStack(Items.BONE_MEAL);
        for(int i = 0; i < 3 + random.nextInt(1); i++){
            this.spawnAtLocation(stack);
        }
    }
}
