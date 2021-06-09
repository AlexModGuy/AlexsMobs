package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class EntityTasmanianDevil extends AnimalEntity implements IAnimatedEntity, ITargetsDroppedItems {

    private int animationTick;
    private Animation currentAnimation;
    public static final Animation ANIMATION_HOWL = Animation.create(40);
    public static final Animation ANIMATION_ATTACK = Animation.create(8);
    private static final DataParameter<Boolean> BASKING = EntityDataManager.createKey(EntityTasmanianDevil.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityTasmanianDevil.class, DataSerializers.BOOLEAN);
    public float prevBaskProgress;
    public float prevSitProgress;
    public float baskProgress;
    public float sitProgress;
    private int sittingTime;
    private int maxSitTime;
    private int scareMobsTime = 0;

    protected EntityTasmanianDevil(EntityType type, World world) {
        super(type, world);
    }

    public boolean shouldMove() {
        return !isSitting() && !isBasking();
    }


    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.TASMANIAN_DEVIL_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TASMANIAN_DEVIL_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TASMANIAN_DEVIL_HURT;
    }
    
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5D, true));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, Ingredient.fromItems(Items.ROTTEN_FLESH), false){
            public void tick(){
                super.tick();
                if(EntityTasmanianDevil.this.getAnimation() == NO_ANIMATION){
                    EntityTasmanianDevil.this.setBasking(false);
                    EntityTasmanianDevil.this.setSitting(false);
                }
            }
        });
        this.goalSelector.addGoal(3, new RandomWalkingGoal(this, 1D, 60));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, EntityTasmanianDevil.class)).setCallsForHelp());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 120, false, false, (p_213487_0_) -> {
            return p_213487_0_ instanceof ChickenEntity || p_213487_0_ instanceof RabbitEntity;
        }));
        this.targetSelector.addGoal(3, new CreatureAITargetItems(this, false, 30));
    }

    public void func_241847_a(ServerWorld world, LivingEntity entity) {
        if(this.getRNG().nextBoolean() && (entity instanceof AnimalEntity || entity.getCreatureAttribute() == CreatureAttribute.UNDEAD)){
            entity.entityDropItem(new ItemStack(Items.BONE));
        }
    }


    public void travel(Vector3d vec3d) {
        if (!this.shouldMove()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }


    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BASKING, Boolean.valueOf(false));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
    }


    public boolean isBasking() {
        return this.dataManager.get(BASKING).booleanValue();
    }

    public void setBasking(boolean basking) {
        this.dataManager.set(BASKING, Boolean.valueOf(basking));
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 14.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2F);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem().isFood() && stack.getItem().getFood() != null && stack.getItem().getFood().isMeat() && stack.getItem() != Items.ROTTEN_FLESH;
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
        if (!world.isRemote && this.getAttackTarget() != null && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 5 && this.canEntityBeSeen(this.getAttackTarget())) {
            float f1 = this.rotationYaw * ((float) Math.PI / 180F);
            this.setMotion(this.getMotion().add(-MathHelper.sin(f1) * 0.02F, 0.0D, MathHelper.cos(f1) * 0.02F));
            getAttackTarget().applyKnockback(1F, getAttackTarget().getPosX() - this.getPosX(), getAttackTarget().getPosZ() - this.getPosZ());
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        }
        if (!world.isRemote && (isSitting() || isBasking()) && ++sittingTime > maxSitTime) {
            this.setSitting(false);
            this.setBasking(false);
            sittingTime = 0;
            maxSitTime = 75 + rand.nextInt(50);
        }
        if (!world.isRemote && this.getMotion().lengthSquared() < 0.03D && this.getAnimation() == NO_ANIMATION && !this.isBasking() && !this.isSitting() && rand.nextInt(100) == 0) {
            sittingTime = 0;
            maxSitTime = 100 + rand.nextInt(550);
            if(this.getRNG().nextBoolean()){
                this.setSitting(true);
                this.setBasking(false);
            }else{
                this.setSitting(false);
                this.setBasking(true);
            }
        }
        if(this.getAnimation() == ANIMATION_HOWL && this.getAnimationTick() == 1){
            this.playSound(AMSoundRegistry.TASMANIAN_DEVIL_ROAR, this.getSoundVolume() * 2F, this.getSoundPitch());
        }
        if(this.getAnimation() == ANIMATION_HOWL && this.getAnimationTick() > 3){
            scareMobsTime = 40;

        }
        if(scareMobsTime > 0) {
            List<MonsterEntity> list = this.world.getEntitiesWithinAABB(MonsterEntity.class, this.getBoundingBox().grow(16, 8, 16));
            for (MonsterEntity e : list) {
                e.setAttackTarget(null);
                e.setRevengeTarget(null);
                if(scareMobsTime % 5 == 0){
                    Vector3d vec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(e, 20, 7, this.getPositionVec());
                    if(vec != null){
                        e.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, 1.5D);
                    }
                }

            }
            scareMobsTime--;
        }
        if(this.getAttackTarget() != null && this.getAttackTarget().isAlive() && (this.getRevengeTarget() == null || !this.getRevengeTarget().isAlive()) ){
            this.setRevengeTarget(this.getAttackTarget());
        }
        if((this.isSitting() || this.isBasking()) && (this.getAttackTarget() != null || this.isInLove())) {
            this.setSitting(false);
            this.setBasking(false);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.getEntityInteractionResult(player, hand);
        if (item == Items.ROTTEN_FLESH && this.getAnimation() != ANIMATION_HOWL) {
            this.playSound(SoundEvents.ENTITY_FOX_EAT, this.getSoundVolume(), this.getSoundPitch());
            this.entityDropItem(item.getContainerItem(itemstack));
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setAnimation(ANIMATION_HOWL);
            return ActionResultType.SUCCESS;
        }
        return type;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
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
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.TASMANIAN_DEVIL.create(serverWorld);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isFood() && stack.getItem().getFood() != null && stack.getItem().getFood().isMeat() || stack.getItem() == Items.BONE;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        if(e.getItem().getItem() == Items.BONE){
            dropBonemeal();
            this.playSound(SoundEvents.ENTITY_SKELETON_STEP, this.getSoundVolume(), this.getSoundPitch());
        }else{
            this.playSound(SoundEvents.ENTITY_FOX_EAT, this.getSoundVolume(), this.getSoundPitch());
            this.heal(5);
        }
    }

    public void dropBonemeal(){
        ItemStack stack = new ItemStack(Items.BONE_MEAL);
        for(int i = 0; i < 3 + rand.nextInt(1); i++){
            this.entityDropItem(stack);
        }
    }
}
