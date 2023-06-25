package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class EntityRhinoceros extends Animal implements IAnimatedEntity {

    public static final Animation ANIMATION_FLICK_EARS = Animation.create(20);
    public static final Animation ANIMATION_EAT_GRASS = Animation.create(35);
    public static final Animation ANIMATION_FLING = Animation.create(15);
    public static final Animation ANIMATION_SLASH = Animation.create(30);
    private static final EntityDataAccessor<String> APPLIED_POTION = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> POTION_LEVEL = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INFLICTED_COUNT = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> POTION_DURATION = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_0 = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_1 = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.BOOLEAN);
    private static final Object2IntMap<String> potionToColor = new Object2IntOpenHashMap<>();
    private int animationTick;
    private Animation currentAnimation;

    protected EntityRhinoceros(EntityType type, Level level) {
        super(type, level);
        this.maxUpStep = 1.1F;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.25F).add(Attributes.ARMOR, 12.0D).add(Attributes.ARMOR_TOUGHNESS, 4.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9D).add(Attributes.ATTACK_KNOCKBACK, 2.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRUSTED_ID_0, Optional.empty());
        this.entityData.define(DATA_TRUSTED_ID_1, Optional.empty());
        this.entityData.define(APPLIED_POTION, "");
        this.entityData.define(POTION_LEVEL, 0);
        this.entityData.define(INFLICTED_COUNT, 0);
        this.entityData.define(POTION_DURATION, 0);
        this.entityData.define(ANGRY, false);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4D, true));
        this.goalSelector.addGoal(2, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT, Items.POTION), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 90, 1.0D, 18, 7));
        this.goalSelector.addGoal(7, new StrollGoal(200));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new DefendTrustedTargetGoal(LivingEntity.class, false, false, (entity) -> {
            return !this.trusts(entity.getUUID());
        }));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Raider.class, 50, true, true, null){
            public boolean canUse(){
                return super.canUse() && !EntityRhinoceros.this.isBaby();
            }
        });
        this.targetSelector.addGoal(3, (new EntityRhinoceros.AIAttackNearPlayers()));
        this.targetSelector.addGoal(4, (new AnimalAIHurtByTargetNotBaby(this)));
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.rhinocerosSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    @Override
    public void tick() {
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (!this.level().isClientSide) {
            if (this.getAnimation() == NO_ANIMATION && (this.getTarget() == null || !this.getTarget().isAlive())) {
                if (this.getDeltaMovement().lengthSqr() < 0.03D && (getRandom().nextInt(500) == 0 && level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK))) {
                    this.setAnimation(ANIMATION_EAT_GRASS);
                } else if (getRandom().nextInt(200) == 0) {
                    this.setAnimation(ANIMATION_FLICK_EARS);
                }
            }
            if (this.getAnimation() == ANIMATION_EAT_GRASS && this.getAnimationTick() == 30 && level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
                BlockPos down = this.blockPosition().below();
                this.level().levelEvent(2001, down, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                this.level().setBlock(down, Blocks.DIRT.defaultBlockState(), 2);
                this.heal(10);
            }
            if (this.getTarget() != null && this.getTarget().isAlive()) {
                this.setAngry(this.distanceTo(this.getTarget()) < 20);
                double dist = this.distanceTo(this.getTarget());
                if (hasLineOfSight(this.getTarget())) {
                    this.lookAt(this.getTarget(), 30, 30);
                    this.yBodyRot = this.getYRot();
                }
                if (dist < this.getBbWidth() + 3.0F) {
                    if (this.getAnimation() == NO_ANIMATION) {
                        this.setAnimation(random.nextBoolean() ? ANIMATION_SLASH : ANIMATION_FLING);
                    }
                    if(dist < this.getBbWidth() + 1.5F && this.hasLineOfSight(this.getTarget())){
                        if (this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() >= 5 && this.getAnimationTick() <= 8) {
                            float dmg = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                            if (this.getTarget() instanceof Raider) {
                                dmg = 10;
                            }
                            attackWithPotion(this.getTarget(), dmg);
                            launch(this.getTarget(), 0, 1F);
                            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                                if(!(entity instanceof Animal) && !trusts(entity.getUUID()) && entity != this.getTarget()){
                                    attackWithPotion(entity, Math.max(dmg - 5, 1));
                                    launch(entity, 0, 0.5F);
                                }
                            }
                        }
                        if (this.getAnimation() == ANIMATION_SLASH && (this.getAnimationTick() >= 9 && this.getAnimationTick() <= 11 || this.getAnimationTick() >= 19 && this.getAnimationTick() <= 21)) {
                            float dmg = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                            if (this.getTarget() instanceof Raider) {
                                dmg = 10;
                            }
                            attackWithPotion(this.getTarget(), dmg);
                            launch(this.getTarget(), this.getAnimationTick() <= 15 ? -90 : 90, 1F);
                            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                                if(!(entity instanceof Animal) && !trusts(entity.getUUID()) && entity != this.getTarget()){
                                    attackWithPotion(entity, Math.max(dmg - 5, 1));
                                    launch(entity, this.getAnimationTick() <= 15 ? -90 : 90, 0.5F);
                                }
                            }
                        }
                    }
                }
            }else{
                this.setAngry(false);
            }
        }
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!isBaby()) {
            this.playSound(AMSoundRegistry.ELEPHANT_WALK.get(), 0.2F, 1.2F);
        } else {
            super.playStepSound(pos, state);
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.RHINOCEROS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.RHINOCEROS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.RHINOCEROS_HURT.get();
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.DEAD_BUSH || item == Items.GRASS;
    }

    public String getAppliedPotionId() {
        return this.entityData.get(APPLIED_POTION);
    }

    public void setAppliedPotionId(String potionId) {
        this.entityData.set(APPLIED_POTION, potionId);
    }

    public int getPotionColor() {
        String s = this.getAppliedPotionId();
        if (s.isEmpty()) {
            return -1;
        } else {
            if (!potionToColor.containsKey(s)) {
                MobEffect effect = getPotionEffect();
                if (effect != null) {
                    int color = effect.getColor();
                    potionToColor.put(s, color);
                    return color;
                }
                return -1;
            } else {
                return potionToColor.getInt(s);
            }
        }
    }

    public MobEffect getPotionEffect() {
        return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.getAppliedPotionId()));
    }

    public int getPotionDuration() {
        return this.entityData.get(POTION_DURATION);
    }

    public void setPotionDuration(int time) {
        this.entityData.set(POTION_DURATION, time);
    }

    public int getPotionLevel() {
        return this.entityData.get(POTION_LEVEL);
    }

    public void setPotionLevel(int time) {
        this.entityData.set(POTION_LEVEL, time);
    }

    public int getInflictedCount() {
        return this.entityData.get(INFLICTED_COUNT);
    }

    public void setInflictedCount(int count) {
        this.entityData.set(INFLICTED_COUNT, count);
    }

    public void resetPotion() {
        this.setAppliedPotionId("");
        this.setPotionDuration(0);
        this.setPotionLevel(0);
        this.setInflictedCount(0);
    }

    private List<UUID> getTrustedUUIDs() {
        List<UUID> list = Lists.newArrayList();
        list.add((UUID)((Optional)this.entityData.get(DATA_TRUSTED_ID_0)).orElse((UUID)null));
        list.add((UUID)((Optional)this.entityData.get(DATA_TRUSTED_ID_1)).orElse((UUID)null));
        return list;
    }

    private void addTrustedUUID(@javax.annotation.Nullable UUID p_28516_) {
        if (((Optional)this.entityData.get(DATA_TRUSTED_ID_0)).isPresent()) {
            this.entityData.set(DATA_TRUSTED_ID_1, Optional.ofNullable(p_28516_));
        } else {
            this.entityData.set(DATA_TRUSTED_ID_0, Optional.ofNullable(p_28516_));
        }
    }

    private void launch(Entity launch, float angle, float scale) {
        float rot = 180F + angle + this.getYRot();
        float hugeScale = 1.0F + random.nextFloat() * 0.5F * scale;
        float strength = (float) (hugeScale *  (1.0D - ((LivingEntity) launch).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        float x = Mth.sin(rot * Mth.DEG_TO_RAD);
        float z = -Mth.cos(rot * Mth.DEG_TO_RAD);
        launch.hasImpulse = true;
        Vec3 vec3 = this.getDeltaMovement();
        Vec3 vec31 = vec3.add((new Vec3(x, 0.0D, z)).normalize().scale(strength));
        launch.setDeltaMovement(vec31.x, hugeScale * 0.3F, vec31.z);
        launch.setOnGround(false);
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
    }

    private boolean trusts(UUID uuid) {
        return this.getTrustedUUIDs().contains(uuid);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_FLICK_EARS, ANIMATION_EAT_GRASS, ANIMATION_FLING, ANIMATION_SLASH};
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return AMEntityRegistry.RHINOCEROS.get().create(serverLevel);
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, Boolean.valueOf(angry));
    }

    private void attackWithPotion(LivingEntity target, float dmg) {
        MobEffect potion = this.getPotionEffect();

        target.hurt(this.damageSources().mobAttack(this), dmg);
        if(potion != null){
            MobEffectInstance instance = new MobEffectInstance(potion, this.getPotionDuration(), this.getPotionLevel());
            if (!target.hasEffect(potion) && target.addEffect(instance)) {
                this.setInflictedCount(this.getInflictedCount() + 1);
            }
        }
        if(this.getInflictedCount() > 15 && random.nextInt(3) == 0 || this.getInflictedCount() > 20){
            this.resetPotion();
        }
    }

    public boolean doHurtTarget(Entity entity) {
        if(this.getAnimation() == NO_ANIMATION){
            this.setAnimation(random.nextBoolean() ? ANIMATION_SLASH : ANIMATION_FLING);
            return true;
        }
        return false;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (entityIn instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwnerUUID() != null && trusts(tamableAnimal.getOwnerUUID())) {
            return true;
        }
        return super.isAlliedTo(entityIn) || trusts(entityIn.getUUID());
    }
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        List<UUID> list = this.getTrustedUUIDs();
        ListTag listtag = new ListTag();
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            UUID uuid = (UUID)var4.next();
            if (uuid != null) {
                listtag.add(NbtUtils.createUUID(uuid));
            }
        }

        tag.put("Trusted", listtag);
        tag.putBoolean("Sleeping", this.isSleeping());
        tag.putString("PotionName", this.getAppliedPotionId());
        tag.putInt("PotionLevel", this.getPotionLevel());
        tag.putInt("PotionDuration", this.getPotionDuration());
        tag.putInt("InflictedCount", this.getInflictedCount());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag listtag = tag.getList("Trusted", 11);

        for(int i = 0; i < listtag.size(); ++i) {
            this.addTrustedUUID(NbtUtils.loadUUID(listtag.get(i)));
        }

        this.setAppliedPotionId(tag.getString("PotionName"));
        this.setPotionLevel(tag.getInt("PotionLevel"));
        this.setPotionDuration(tag.getInt("PotionDuration"));
        this.setInflictedCount(tag.getInt("InflictedCount"));
    }


    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if(!isBaby() && (itemstack.getItem() == Items.POTION || itemstack.getItem() == Items.SPLASH_POTION || itemstack.getItem() == Items.LINGERING_POTION)){
            Potion contained = PotionUtils.getPotion(itemstack);
            if(applyPotion(contained)){
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.DYE_USE);
                this.usePlayerItem(player, hand, itemstack);
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if(!player.addItem(bottle)){
                    player.drop(bottle, false);
                }
                return InteractionResult.SUCCESS;
            }
        }else if(itemstack.getItem() == Items.WHEAT && !trusts(player.getUUID())){
            addTrustedUUID(player.getUUID());
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.HORSE_EAT);
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    public boolean applyPotion(Potion potion){
        if(potion == null || potion == Potions.WATER){
            resetPotion();
            return true;
        }else{
            if(potion.getEffects().size() >= 1){
                MobEffectInstance first = potion.getEffects().get(0);
                ResourceLocation loc = ForgeRegistries.MOB_EFFECTS.getKey(first.getEffect());
                if(loc != null){
                    this.setAppliedPotionId(loc.toString());
                    this.setPotionLevel(first.getAmplifier());
                    this.setPotionDuration(first.getDuration());
                    this.setInflictedCount(0);
                    return true;
                }
            }
        }
        return false;
    }

    class AIAttackNearPlayers extends NearestAttackableTargetGoal<Player> {
        public AIAttackNearPlayers() {
            super(EntityRhinoceros.this, Player.class, 80, true, true, null);
        }

        public boolean canUse() {
            if (EntityRhinoceros.this.isBaby() || EntityRhinoceros.this.isInLove() || EntityRhinoceros.this.trustsAny()) {
                return false;
            } else {
                return super.canUse();
            }
        }

        protected double getFollowDistance() {
            return 3.0D;
        }
    }

    private boolean trustsAny() {
        return this.entityData.get(DATA_TRUSTED_ID_0).isPresent() || this.entityData.get(DATA_TRUSTED_ID_1).isPresent();
    }

    class DefendTrustedTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
        private LivingEntity trustedLastHurtBy;
        private LivingEntity trustedLastHurt;
        private LivingEntity trusted;
        private int timestamp;

        public DefendTrustedTargetGoal(Class<LivingEntity> entities, boolean b, @javax.annotation.Nullable boolean b2, Predicate<LivingEntity> pred) {
            super(EntityRhinoceros.this, entities, 10, b, b2, pred);
        }

        public boolean canUse() {
            if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0 || this.mob.isBaby()) {
                return false;
            } else {
                Iterator var1 = EntityRhinoceros.this.getTrustedUUIDs().iterator();

                while(var1.hasNext()) {
                    UUID uuid = (UUID)var1.next();
                    if (uuid != null && EntityRhinoceros.this.level() instanceof ServerLevel) {
                        Entity entity = ((ServerLevel)EntityRhinoceros.this.level()).getEntity(uuid);
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)entity;
                            this.trusted = livingentity;
                            this.trustedLastHurtBy = livingentity.getLastHurtByMob();
                            this.trustedLastHurt = livingentity.getLastHurtMob();
                            int i = livingentity.getLastHurtByMobTimestamp();
                            int j = livingentity.getLastHurtMobTimestamp();
                            if(i != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions)){
                                return true;
                            }
                            if(j != this.timestamp && this.canAttack(this.trustedLastHurt, this.targetConditions)){
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        }

        public void start() {
            if(this.trustedLastHurtBy != null){
                this.setTarget(this.trustedLastHurtBy);
                this.target = this.trustedLastHurtBy;
                if (this.trusted != null) {
                    this.timestamp = this.trusted.getLastHurtByMobTimestamp();
                }
            }else{
                this.setTarget(this.trustedLastHurt);
                this.target = this.trustedLastHurt;
                if (this.trusted != null) {
                    this.timestamp = this.trusted.getLastHurtMobTimestamp();
                }
            }
            super.start();
        }
    }


    class StrollGoal extends MoveThroughVillageGoal {
        public StrollGoal(int timr) {
            super(EntityRhinoceros.this, 1.0D, true, timr, () -> false);
        }

        public void start() {
            super.start();
        }

        public boolean canUse() {
            return super.canUse() && this.canRhinoWander();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.canRhinoWander();
        }

        private boolean canRhinoWander() {
            return !EntityRhinoceros.this.getTrustedUUIDs().isEmpty();
        }
    }

}