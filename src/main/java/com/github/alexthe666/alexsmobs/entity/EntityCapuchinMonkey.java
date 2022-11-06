package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;

public class EntityCapuchinMonkey extends TamableAnimal implements IAnimatedEntity, IFollower, ITargetsDroppedItems {

    public static final Animation ANIMATION_THROW = Animation.create(12);
    public static final Animation ANIMATION_HEADTILT = Animation.create(15);
    public static final Animation ANIMATION_SCRATCH = Animation.create(20);

    protected static final EntityDataAccessor<Boolean> DART = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DART_TARGET = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
    public float prevSitProgress;
    public float sitProgress;
    public boolean forcedSit = false;
    public boolean attackDecision = false;//true for ranged, false for melee
    private int animationTick;
    private Animation currentAnimation;
    private int sittingTime = 0;
    private int maxSitTime = 75;
    private boolean hasSlowed = false;
    private int rideCooldown = 0;
    private Ingredient temptItems = null;

    protected EntityCapuchinMonkey(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.4F);
    }

    public static <T extends Mob> boolean canCapuchinSpawn(EntityType<EntityCapuchinMonkey> gorilla, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
        return (blockstate.is(BlockTags.LEAVES) || blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(BlockTags.LOGS) || blockstate.is(Blocks.MANGROVE_ROOTS) || blockstate.is(Blocks.MUDDY_MANGROVE_ROOTS) || blockstate.is(Blocks.AIR)) && worldIn.getRawBrightness(p_223317_3_, 0) > 8;
    }

    public int getMaxSpawnClusterSize() {
        return 8;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.capuchinMonkeySpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public Ingredient getAllFoods(){
        if(temptItems == null){
            temptItems = Ingredient.fromValues(Stream.of(new Ingredient.TagValue(AMTagRegistry.INSECT_ITEMS), new Ingredient.ItemValue(new ItemStack(Items.EGG))));
        }
        return temptItems;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                amount = (amount + 1.0F) / 4.0F;
            }
            return super.hurt(source, amount);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new CapuchinAIMelee(this, 1, true));
        this.goalSelector.addGoal(3, new CapuchinAIRangedAttack(this, 1, 20, 15));
        this.goalSelector.addGoal(6, new TameableAIFollowOwner(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.merge(ImmutableList.of(Ingredient.of(AMTagRegistry.BANANAS))), true) {
            public void tick() {
                super.tick();
                if (this.mob.distanceToSqr(this.player) < 6.25D && this.mob.getRandom().nextInt(14) == 0) {
                    ((EntityCapuchinMonkey) this.mob).setAnimation(ANIMATION_HEADTILT);
                }
            }
        });
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0D, 60));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, (new HurtByTargetGoal(this, EntityCapuchinMonkey.class, EntityTossedItem.class)).setAlertOthers());
        this.targetSelector.addGoal(5, new CapuchinAITargetBalloons(this, true));
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.CAPUCHIN_MONKEY_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CAPUCHIN_MONKEY_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CAPUCHIN_MONKEY_HURT.get();
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }

        return super.isAlliedTo(entityIn);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("MonkeySitting", this.isSitting());
        compound.putBoolean("HasDart", this.hasDart());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("Command", this.getCommand());
        compound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("MonkeySitting"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.setCommand(compound.getInt("Command"));
        this.setDart(compound.getBoolean("HasDart"));
        this.setVariant(compound.getInt("Variant"));
    }

    public void tick() {
        super.tick();
        this.prevSitProgress = this.sitProgress;
        if (this.isSitting()) {
            if (sitProgress < 10F)
                sitProgress++;
        } else {
            if (sitProgress > 0F)
                sitProgress--;
        }

        if (!forcedSit && isSitting() && ++sittingTime > maxSitTime) {
            this.setOrderedToSit(false);
            sittingTime = 0;
            maxSitTime = 75 + random.nextInt(50);
        }
        if (!level.isClientSide && this.getAnimation() == NO_ANIMATION && !this.isSitting() && this.getCommand() != 1 && random.nextInt(1500) == 0) {
            maxSitTime = 300 + random.nextInt(250);
            this.setOrderedToSit(true);
        }
        this.maxUpStep = 2;
        if (!forcedSit && this.isSitting() && (this.getDartTarget() != null || this.getCommand() == 1)) {
            this.setOrderedToSit(false);
        }

        if (!level.isClientSide) {
            if (this.getTarget() != null && this.getAnimation() == ANIMATION_SCRATCH && this.getAnimationTick() == 10) {
                float f1 = this.getYRot() * Maths.piDividedBy180;
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.3F, 0.0D, Mth.cos(f1) * 0.3F));
                getTarget().knockback(1F, getTarget().getX() - this.getX(), getTarget().getZ() - this.getZ());
                this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                this.setAttackDecision(this.getTarget());
            }
            if (this.getDartTarget() != null && this.getDartTarget().isAlive() && this.getAnimation() == ANIMATION_THROW && this.getAnimationTick() == 5) {
                final Vec3 vector3d = this.getDartTarget().getDeltaMovement();
                final double d0 = this.getDartTarget().getX() + vector3d.x - this.getX();
                final double d1 = this.getDartTarget().getEyeY() - (double) 1.1F - this.getY();
                final double d2 = this.getDartTarget().getZ() + vector3d.z - this.getZ();
                final float f = Mth.sqrt((float)(d0 * d0 + d2 * d2));
                EntityTossedItem tossedItem = new EntityTossedItem(this.level, this);
                tossedItem.setDart(this.hasDart());
                tossedItem.setXRot(tossedItem.getXRot() - 20F);
                tossedItem.shoot(d0, d1 + (double) (f * 0.2F), d2, hasDart() ? 1.15F : 0.75F, 8.0F);
                if (!this.isSilent()) {
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                }
                this.level.addFreshEntity(tossedItem);
                this.setAttackDecision(this.getDartTarget());
            }
        }
        if (rideCooldown > 0) {
            rideCooldown--;
        }
        if (!level.isClientSide && getAnimation() == NO_ANIMATION && this.getRandom().nextInt(300) == 0) {
            setAnimation(ANIMATION_HEADTILT);
        }
        if (!level.isClientSide && this.isSitting()) {
            this.getNavigation().stop();
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SCRATCH);
        }
        return true;
    }

    public void travel(Vec3 vec3d) {
        if (this.isSitting()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (hasDart()) {
            this.spawnAtLocation(AMItemRegistry.ANCIENT_DART.get());
        }
    }

    public void rideTick() {
        final Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else if (isTame() && entity instanceof LivingEntity && isOwnedBy((LivingEntity) entity)) {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                final Entity mount = this.getVehicle();
                if (mount instanceof final Player player) {
                    this.yBodyRot = player.yBodyRot;
                    this.setYRot(player.getYRot());
                    this.yHeadRot = player.yHeadRot;
                    this.yRotO = player.yHeadRot;
                    final float radius = 0F;
                    final float angle = (0.0174532925F * (((LivingEntity) mount).yBodyRot - 180F));
                    final double extraX = radius * Mth.sin((float) (Math.PI + angle));
                    final double extraZ = radius * Mth.cos(angle);
                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() + 0.1, mount.getY()), mount.getZ() + extraZ);
                    attackDecision = true;
                    if (!mount.isAlive() || rideCooldown == 0 && mount.isShiftKeyDown()) {
                        this.removeVehicle();
                        attackDecision = false;
                    }
                }
            }
        } else {
            super.rideTick();
        }

    }

    public void setAttackDecision(Entity target) {
        if (target instanceof Monster || this.hasDart()) {
            attackDecision = true;
        } else {
            attackDecision = !attackDecision;
        }
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, sit);
    }

    public boolean hasDartTarget() {
        return this.entityData.get(DART_TARGET) != -1 && this.hasDart();
    }


    public void setDartTarget(Entity entity) {
        this.entityData.set(DART_TARGET, entity == null ? -1 : entity.getId());
        if (entity instanceof LivingEntity target) {
            this.setTarget(target);
        }
    }

    @Nullable
    public Entity getDartTarget() {
        if (!this.hasDartTarget()) {
            return this.getTarget();
        } else {
            Entity entity = this.level.getEntity(this.entityData.get(DART_TARGET));
            if(entity == null || !entity.isAlive()){
                return this.getTarget();
            }else{
                return entity;
            }
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COMMAND, 0);
        this.entityData.define(DART_TARGET, -1);
        this.entityData.define(SITTING, false);
        this.entityData.define(DART, false);
        this.entityData.define(VARIANT, 0);
    }

    public boolean hasDart() {
        return this.entityData.get(DART);
    }

    public void setDart(boolean dart) {
        this.entityData.set(DART, dart);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
        EntityCapuchinMonkey monkey = AMEntityRegistry.CAPUCHIN_MONKEY.get().create(p_241840_1_);
        monkey.setVariant(this.getVariant());
        return monkey;
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
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || super.isInvulnerableTo(source);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        if (EntityGorilla.isBanana(itemstack)) {
            if (!isTame()) {
                this.usePlayerItem(player, hand, itemstack);
                if (getRandom().nextInt(5) == 0) {
                    this.tame(player);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }
                return InteractionResult.SUCCESS;
            }
            if (isTame() && (getAllFoods().test(itemstack) && !isFood(itemstack)) && this.getHealth() < this.getMaxHealth()) {
                this.usePlayerItem(player, hand, itemstack);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.heal(5);
                return InteractionResult.SUCCESS;
            }
        }

        final InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        final InteractionResult type = super.mobInteract(player, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack) && !EntityGorilla.isBanana(itemstack) && !getAllFoods().test(itemstack)) {
            if (!this.hasDart() && itemstack.getItem() == AMItemRegistry.ANCIENT_DART.get()) {
                this.setDart(true);
                this.usePlayerItem(player, hand, itemstack);
                return InteractionResult.CONSUME;
            }
            if (this.hasDart() && itemstack.getItem() == Items.SHEARS) {
                this.setDart(false);
                itemstack.hurtAndBreak(1, this, (p_233654_0_) -> {
                });
                return InteractionResult.SUCCESS;
            }
            if (player.isShiftKeyDown() && player.getPassengers().isEmpty()) {
                this.startRiding(player);
                rideCooldown = 20;
                return InteractionResult.SUCCESS;
            } else {
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() == 3) {
                    this.setCommand(0);
                }
                player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                final boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.forcedSit = true;
                    this.setOrderedToSit(true);
                } else {
                    this.forcedSit = false;
                    this.setOrderedToSit(false);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_THROW, ANIMATION_SCRATCH};
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return getAllFoods().test(stack) || EntityGorilla.isBanana(stack);
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return isTame() && stack.is(AMTagRegistry.INSECT_ITEMS);
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.heal(5);
        this.gameEvent(GameEvent.EAT);
        this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
        if (EntityGorilla.isBanana(e.getItem())) {
            if (getRandom().nextInt(4) == 0) {
                this.spawnAtLocation(new ItemStack(AMBlockRegistry.BANANA_PEEL.get()));
            }
            if (e.getThrower() != null && !this.isTame()) {
                if (getRandom().nextInt(5) == 0) {
                    this.setTame(true);
                    this.setOwnerUUID(e.getThrower());
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }
            }
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance diff, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        int i;
        if (data instanceof CapuchinGroupData) {
            i = ((CapuchinGroupData)data).variant;
        } else {
            i = this.random.nextInt(4);
            data = new CapuchinGroupData(i);
        }

        this.setVariant(i);
        return super.finalizeSpawn(world, diff, spawnType, data, tag);
    }

    public class CapuchinGroupData extends AgeableMobGroupData {

        public final int variant;

        CapuchinGroupData(int variant) {
            super(true);
            this.variant = variant;
        }

    }

}
