package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockReptileEgg;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

public class EntityCaiman extends TamableAnimal implements ISemiAquatic,IFollower {

    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BELLOWING = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(EntityCaiman.class, EntityDataSerializers.BOOLEAN);
    public float prevSitProgress;
    public float sitProgress;
    public float prevHoldProgress;
    public float holdProgress;
    public float prevSwimProgress;
    public float swimProgress;
    public float prevVibrateProgress;
    public float vibrateProgress;
    private int swimTimer = -1000;
    public int bellowCooldown = 100 + random.nextInt(1000);
    private boolean isLandNavigator;
    public boolean tameAttackFlag = false;

    public EntityCaiman(EntityType type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COMMAND, 0);
        this.entityData.define(BELLOWING, false);
        this.entityData.define(SITTING, false);
        this.entityData.define(HAS_EGG, false);
        this.entityData.define(HELD_MOB_ID, -1);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new MateGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new LayEggGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new CaimanAIMelee(this));
        this.goalSelector.addGoal(3, new BreathAirGoal(this));
        this.goalSelector.addGoal(4, new TameableAIFollowOwnerWater(this, 1.1D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2F, false));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, Ingredient.of(AMItemRegistry.COOKED_CATFISH.get(), AMItemRegistry.RAW_CATFISH.get()), false));
        this.goalSelector.addGoal(7, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(7, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(8, new CaimanAIBellow(this));
        this.goalSelector.addGoal(9, new SemiAquaticAIRandomSwimming(this, 1.0D, 30));
        this.goalSelector.addGoal(10, new RandomStrollGoal(this, 1.0D, 60));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, (new AnimalAIHurtByTargetNotBaby(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this) {
            @Override
            public void start() {
                super.start();
                tameAttackFlag = true;
            }

            @Override
            public void stop() {
                super.start();
                tameAttackFlag = false;
            }
        });
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this) {
            @Override
            public void start() {
                super.start();
                tameAttackFlag = true;
            }

            @Override
            public void stop() {
                super.start();
                tameAttackFlag = false;
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CAIMAN_TARGETS)) {
            public boolean canUse() {
                return !isBaby() && !isTame() && super.canUse();
            }
        });
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.caimanSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static <T extends Mob> boolean canCaimanSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos p_223317_3_, RandomSource random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.below());
        return blockstate.is(Blocks.MUD) || blockstate.is(Blocks.MUDDY_MANGROVE_ROOTS) || blockstate.is(AMTagRegistry.CROCODILE_SPAWNS);
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMItemRegistry.RAW_CATFISH.get()) || stack.is(AMItemRegistry.COOKED_CATFISH.get());
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AquaticMoveController(this, 1.1F);
            this.navigation = new SemiAquaticPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    public int getMaxSpawnClusterSize() {
        return 2;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return isBaby() ? AMSoundRegistry.CROCODILE_BABY.get() : AMSoundRegistry.CAIMAN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CAIMAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CAIMAN_HURT.get();
    }

    public void tick() {
        super.tick();
        this.prevHoldProgress = holdProgress;
        this.prevSwimProgress = swimProgress;
        this.prevSitProgress = sitProgress;
        this.prevVibrateProgress = vibrateProgress;

        final boolean ground = !this.isInWaterOrBubble();
        final boolean bellowing = this.isBellowing();
        final boolean grabbing = this.getHeldMobId() != -1;
        final boolean sitting = this.isSitting() && ground;

        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (ground && swimProgress > 0) {
            swimProgress--;
        }
        if (!ground && swimProgress < 5F) {
            swimProgress++;
        }
        if (bellowing && vibrateProgress < 5) {
            vibrateProgress++;
        }
        if (!bellowing && vibrateProgress > 0F) {
            vibrateProgress--;
        }
        if (sitting && sitProgress < 5) {
            sitProgress++;
        }
        if (!sitting && sitProgress > 0F) {
            sitProgress--;
        }
        if (grabbing && holdProgress < 5) {
            holdProgress += 2.5F;
        }
        if (!grabbing && holdProgress > 0F) {
            holdProgress -= 2.5F;
        }
        if (!level.isClientSide) {
            if (isInWater()) {
                swimTimer++;
            } else {
                if(this.isBellowing()){
                    this.setBellowing(false);
                }
                swimTimer--;
            }
            if (this.getTarget() instanceof WaterAnimal && !this.isTame()) {
                WaterAnimal fish = (WaterAnimal) this.getTarget();
                CompoundTag fishNbt = new CompoundTag();
                fish.addAdditionalSaveData(fishNbt);
                fishNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
                fish.readAdditionalSaveData(fishNbt);
            }
        } else {
            if (this.isInWaterOrBubble() && this.isBellowing()) {
                int particles = 4 + getRandom().nextInt(3);
                for (int i = 0; i <= particles; i++) {
                    Vec3 particleVec = new Vec3(0, 0, 1.0F).yRot((i / (float) particles) * ((float) Math.PI) * 2F).add(this.position());
                    double particleY = this.getBoundingBox().minY + getFluidTypeHeight(ForgeMod.WATER_TYPE.get());
                    this.level.addParticle(ParticleTypes.SPLASH, particleVec.x, particleY, particleVec.z, 0, 0.3F, 0);
                }
            }
        }
        if (bellowCooldown > 0) {
            bellowCooldown--;
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (isTame() && itemstack.is(ItemTags.FISHES) && this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.CAT_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.heal(5);
            return InteractionResult.SUCCESS;
        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack)) {
            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() == 3) {
                this.setCommand(0);
            }
            player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
            boolean sit = this.getCommand() == 2;
            if (sit) {
                this.setOrderedToSit(true);
                return InteractionResult.SUCCESS;
            } else {
                this.setOrderedToSit(false);
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean checkSpawnObstruction(LevelReader worldIn) {
        return worldIn.isUnobstructed(this);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }

    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    private void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level.getEntity(id);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isBellowing() {
        return this.entityData.get(BELLOWING);
    }

    public void setBellowing(boolean bellowing) {
        this.entityData.set(BELLOWING, bellowing);
    }

    public void travel(Vec3 travelVector) {
        if(isSitting()){
            super.travel(Vec3.ZERO);
        }else if (this.isEffectiveAi() && this.isInWater()) {
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

    public void calculateEntityAnimation(LivingEntity living, boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean shouldEnterWater() {
        return !shouldLeaveWater() && swimTimer <= -1000 || bellowCooldown == 0;
    }

    public boolean shouldLeaveWater() {
        LivingEntity target = this.getTarget();
        if (target != null && !target.isInWater()) {
            return true;
        }
        return swimTimer > 600 && !this.isBellowing();
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isSitting();
    }

    @Override
    public int getWaterSearchRange() {
        return 12;
    }


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    public Vec3 getShakePreyPos() {
        Vec3 jaw = new Vec3(0, -0.1, 1F);
        Vec3 head = jaw.xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F));
        return this.getEyePosition().add(head);
    }

    public void push(double x, double y, double z) {
        if (this.getHeldMobId() == -1) {
            super.push(x, y, z);
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasEgg", this.hasEgg());
        compound.putBoolean("Bellowing", this.isBellowing());
        compound.putInt("CaimanCommand", this.getCommand());
        compound.putBoolean("CaimanSitting", this.isOrderedToSit());
        compound.putInt("BellowCooldown", this.bellowCooldown);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHasEgg(compound.getBoolean("HasEgg"));
        this.setBellowing(compound.getBoolean("Bellowing"));
        this.bellowCooldown = compound.getInt("BellowCooldown");
        this.setCommand(compound.getInt("CaimanCommand"));
        this.setOrderedToSit(compound.getBoolean("CaimanSitting"));
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    static class MateGoal extends BreedGoal {
        private final EntityCaiman caiman;

        MateGoal(EntityCaiman caiman, double speedIn) {
            super(caiman, speedIn);
            this.caiman = caiman;
        }

        public boolean canUse() {
            return super.canUse() && !this.caiman.hasEgg();
        }

        protected void breed() {
            ServerPlayer serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.partner.getLoveCause() != null) {
                serverplayerentity = this.partner.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, this.animal);
            }
            this.caiman.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            RandomSource random = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }

        }
    }

    static class LayEggGoal extends MoveToBlockGoal {
        private final EntityCaiman caiman;
        private int digTime;

        LayEggGoal(EntityCaiman caiman, double speedIn) {
            super(caiman, speedIn, 16);
            this.caiman = caiman;
        }

        public void stop() {
            digTime = 0;
        }

        public boolean canUse() {
            return this.caiman.hasEgg() && super.canUse();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.caiman.hasEgg();
        }

        public double acceptedDistance() {
            return caiman.getBbWidth() + 0.5D;
        }

        public void tick() {
            super.tick();
            BlockPos blockpos = this.caiman.blockPosition();
            caiman.swimTimer = 1000;
            if (!this.caiman.isInWater() && this.isReachedTarget()) {
                Level world = this.caiman.level;
                caiman.gameEvent(GameEvent.BLOCK_PLACE);
                world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                world.setBlock(this.blockPos.above(), AMBlockRegistry.CAIMAN_EGG.get().defaultBlockState().setValue(BlockReptileEgg.EGGS, Integer.valueOf(this.caiman.random.nextInt(1) + 3)), 3);
                this.caiman.setHasEgg(false);
                this.caiman.setInLoveTime(600);
            }

        }

        protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
            return worldIn.isEmptyBlock(pos.above()) && BlockReptileEgg.isProperHabitat(worldIn, pos);
        }
    }
}
