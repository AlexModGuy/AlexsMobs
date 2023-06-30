package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityLeafcutterAnt extends Animal implements NeutralMob, IAnimatedEntity {

    public static final Animation ANIMATION_BITE = Animation.create(13);
    protected static final EntityDimensions QUEEN_SIZE = EntityDimensions.fixed(1.25F, 0.98F);
    public static final ResourceLocation QUEEN_LOOT = new ResourceLocation("alexsmobs", "entities/leafcutter_ant_queen");
    private static final EntityDataAccessor<Optional<BlockPos>> LEAF_HARVESTED_POS = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<BlockState>> LEAF_HARVESTED_STATE = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> HAS_LEAF = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ANT_SCALE = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> QUEEN = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANGER_TIME = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.INT);
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final UniformInt ANGRY_TIMER = TimeUtil.rangeOfSeconds(10, 20);
    public float attachChangeProgress = 0F;
    public float prevAttachChangeProgress = 0F;
    private Direction prevAttachDir = Direction.DOWN;
    @Nullable
    private EntityLeafcutterAnt caravanHead;
    @Nullable
    private EntityLeafcutterAnt caravanTail;
    private UUID lastHurtBy;
    @Nullable
    private BlockPos hivePos = null;
    private int stayOutOfHiveCountdown;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isUpsideDownNavigator;
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(AMItemRegistry.GONGYLIDIA.get());
    private int haveBabyCooldown = 0;
    public EntityLeafcutterAnt(EntityType type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        switchNavigator(true);

    }

    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if(entitylivingbaseIn instanceof Player && ((Player) entitylivingbaseIn).isCreative()){
            return;
        }
        super.setTarget(entitylivingbaseIn);
    }

    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return this.isQueen() ? QUEEN_LOOT : super.getDefaultLootTable();
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    private void switchNavigator(boolean rightsideUp) {
        if (rightsideUp) {
            this.moveControl = new MoveControl(this);
            this.navigation = new WallClimberNavigation(this, level());
            this.isUpsideDownNavigator = false;
        } else {
            this.moveControl = new FlightMoveController(this, 0.6F, false);
            this.navigation = new DirectPathNavigator(this, level());
            this.isUpsideDownNavigator = true;
        }
    }

    public boolean canCollideWith(Entity entity) {
        return !(entity instanceof EntityAnteater) && super.canCollideWith(entity);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.25F).add(Attributes.ATTACK_DAMAGE, 2F);
    }

    private static boolean isSideSolid(BlockGetter reader, BlockPos pos, Entity entityIn, Direction direction) {
        return Block.isFaceFull(reader.getBlockState(pos).getCollisionShape(reader, pos, CollisionContext.of(entityIn)), direction);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ReturnToHiveGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new TameableAITempt(this, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new LeafcutterAntAIFollowCaravan(this, 1D));
        this.goalSelector.addGoal(5, new LeafcutterAntAIForageLeaves(this));
        this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 30, 1.0D, 25, 7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new AngerGoal(this)).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return isQueen() && !isBaby() ? QUEEN_SIZE : super.getDimensions(poseIn);
    }

    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public Direction getAttachmentFacing() {
        return this.entityData.get(ATTACHED_FACE);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation(this, worldIn);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return isQueen() ? AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT.get() : AMSoundRegistry.LEAFCUTTER_ANT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return isQueen() ? AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT.get() : AMSoundRegistry.LEAFCUTTER_ANT_HURT.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {

    }

    public void push(Entity entity) {
        if(!(entity instanceof EntityAnteater)){
            super.push(entity);
        }
    }

    private void pacifyAllNearby(){
        stopBeingAngry();
        List<EntityLeafcutterAnt> list = level().getEntitiesOfClass(EntityLeafcutterAnt.class, this.getBoundingBox().inflate(20D, 6.0D, 20D));
        for(EntityLeafcutterAnt ant : list){
            ant.stopBeingAngry();
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if(type != InteractionResult.SUCCESS && item == AMItemRegistry.GONGYLIDIA.get()){
            if(isQueen() && haveBabyCooldown == 0){
                int babies = 1 + random.nextInt(1);
                pacifyAllNearby();
                for(int i = 0; i < babies; i++){
                    EntityLeafcutterAnt leafcutterAnt = AMEntityRegistry.LEAFCUTTER_ANT.get().create(level());
                    leafcutterAnt.copyPosition(this);
                    leafcutterAnt.setAge(-24000);
                    if(!this.level().isClientSide){
                        level().broadcastEntityEvent(this, (byte)18);
                        level().addFreshEntity(leafcutterAnt);
                    }
                }
                if(!player.isCreative()){
                    itemstack.shrink(1);
                }
                haveBabyCooldown = 24000;
                this.setBaby(false);
            }else{
                pacifyAllNearby();
                if(!player.isCreative()){
                    itemstack.shrink(1);
                }
                level().broadcastEntityEvent(this, (byte)48);
                this.heal(3);
            }

            return InteractionResult.SUCCESS;

        }
        return type;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 48) {
            for(int i = 0; i < 3; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    public void tick() {
        this.prevAttachChangeProgress = this.attachChangeProgress;
        super.tick();
        if (this.isQueen() && this.getBbWidth() < QUEEN_SIZE.width) {
            this.refreshDimensions();
        }
        if (attachChangeProgress > 0F) {
            attachChangeProgress -= 0.25F;
        }
        this.setMaxUpStep(isQueen() ? 1F : 0.5F);
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.level().isClientSide && !this.isQueen()) {
            this.setBesideClimbableBlock(this.horizontalCollision || this.verticalCollision && !this.onGround());
            if (this.onGround() || this.isInWaterOrBubble() || this.isInLava()) {
                this.entityData.set(ATTACHED_FACE, Direction.DOWN);
            } else  if (this.verticalCollision) {
                this.entityData.set(ATTACHED_FACE, Direction.UP);
            }else {
                boolean flag = false;
                Direction closestDirection = Direction.DOWN;
                double closestDistance = 100;
                for (Direction dir : HORIZONTALS) {
                    BlockPos antPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
                    BlockPos offsetPos = antPos.relative(dir);
                    Vec3 offset = Vec3.atCenterOf(offsetPos);
                    if (closestDistance > this.position().distanceTo(offset) && level().loadedAndEntityCanStandOnFace(offsetPos, this, dir.getOpposite())) {
                        closestDistance = this.position().distanceTo(offset);
                        closestDirection = dir;
                    }
                }
                this.entityData.set(ATTACHED_FACE, closestDirection);
            }
        }
        boolean flag = false;
        if (this.getAttachmentFacing() != Direction.DOWN) {
            if(this.getAttachmentFacing() == Direction.UP){
                this.setDeltaMovement(this.getDeltaMovement().add(0, 1, 0));
            }else{
                if (!this.horizontalCollision && this.getAttachmentFacing() != Direction.UP) {
                    Vec3 vec = Vec3.atLowerCornerOf(this.getAttachmentFacing().getNormal());
                    this.setDeltaMovement(this.getDeltaMovement().add(vec.normalize().multiply(0.1F, 0.1F, 0.1F)));
                }
                if (!this.onGround() && vector3d.y < 0.0D) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
                    flag = true;
                }
            }
        }
        if(this.getAttachmentFacing() == Direction.UP) {
            this.setNoGravity(true);
            this.setDeltaMovement(vector3d.multiply(0.7D, 1D, 0.7D));
        }else{
            this.setNoGravity(false);
        }
        if (!flag) {
            if (this.onClimbable()) {
                this.setDeltaMovement(vector3d.multiply(1.0D, 0.4D, 1.0D));
            }
        }
        if (prevAttachDir != this.getAttachmentFacing()) {
            attachChangeProgress = 1F;
        }
        this.prevAttachDir = this.getAttachmentFacing();
        if (!this.level().isClientSide) {
            if (this.getAttachmentFacing() == Direction.UP && !this.isUpsideDownNavigator) {
                switchNavigator(false);
            }
            if (this.getAttachmentFacing() != Direction.UP && this.isUpsideDownNavigator) {
                switchNavigator(true);
            }
            if (this.stayOutOfHiveCountdown > 0) {
                --this.stayOutOfHiveCountdown;
            }

            if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = null;
            }
            LivingEntity attackTarget = this.getTarget();
            if (attackTarget != null && distanceTo(attackTarget) < attackTarget.getBbWidth() + this.getBbWidth() + 1 && this.hasLineOfSight(attackTarget)) {
                if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 6) {
                    float damage = (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    attackTarget.hurt(this.damageSources().mobAttack(this), damage);
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private boolean isClimeableFromSide(BlockPos offsetPos, Direction opposite) {
        return false;
    }

    private boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        } else {
            BlockEntity tileentity = this.level().getBlockEntity(this.hivePos);
            return tileentity instanceof TileEntityLeafcutterAnthill;
        }
    }

    protected void onInsideBlock(BlockState state) {

    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(CLIMBING, b0);
    }

    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(ANGER_TIME, time);
    }

    public UUID getPersistentAngerTarget() {
        return this.lastHurtBy;
    }

    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.lastHurtBy = target;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGRY_TIMER.sample(this.random));
    }

    protected void customServerAiStep() {
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), false);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(LEAF_HARVESTED_POS, Optional.empty());
        this.entityData.define(LEAF_HARVESTED_STATE, Optional.empty());
        this.entityData.define(HAS_LEAF, false);
        this.entityData.define(QUEEN, false);
        this.entityData.define(ATTACHED_FACE, Direction.DOWN);
        this.entityData.define(ANT_SCALE, 1.0F);
        this.entityData.define(ANGER_TIME, 0);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setAntScale(0.75F + random.nextFloat() * 0.3F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public float getAntScale() {
        return this.entityData.get(ANT_SCALE);
    }

    public void setAntScale(float scale) {
        this.entityData.set(ANT_SCALE, scale);
    }


    public BlockPos getHarvestedPos() {
        return this.entityData.get(LEAF_HARVESTED_POS).orElse(null);
    }

    public void setLeafHarvestedPos(BlockPos harvestedPos) {
        this.entityData.set(LEAF_HARVESTED_POS, Optional.ofNullable(harvestedPos));
    }

    public BlockState getHarvestedState() {
        return this.entityData.get(LEAF_HARVESTED_STATE).orElse(null);
    }

    public void setLeafHarvestedState(BlockState state) {
        this.entityData.set(LEAF_HARVESTED_STATE, Optional.ofNullable(state));
    }

    public boolean hasLeaf() {
        return this.entityData.get(HAS_LEAF).booleanValue();
    }

    public void setLeaf(boolean leaf) {
        this.entityData.set(HAS_LEAF, Boolean.valueOf(leaf));
    }

    public boolean isQueen() {
        return this.entityData.get(QUEEN).booleanValue();
    }

    public void setQueen(boolean queen) {
        boolean prev = isQueen();
        if (!prev && queen) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
            this.setHealth(36F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(6.0D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        }
        this.entityData.set(QUEEN, Boolean.valueOf(queen));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(ATTACHED_FACE, Direction.from3DDataValue(compound.getByte("AttachFace")));
        this.setLeaf(compound.getBoolean("Leaf"));
        this.setQueen(compound.getBoolean("Queen"));
        this.setAntScale(compound.getFloat("AntScale"));
        BlockState blockstate = null;
        if (compound.contains("HarvestedLeafState", 10)) {
            blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("HarvestedLeafState"));
            if (blockstate.isAir()) {
                blockstate = null;
            }
        }
        this.stayOutOfHiveCountdown = compound.getInt("CannotEnterHiveTicks");
        this.haveBabyCooldown = compound.getInt("BabyCooldown");
        this.hivePos = null;
        if (compound.contains("HivePos")) {
            this.hivePos = NbtUtils.readBlockPos(compound.getCompound("HivePos"));
        }
        this.setLeafHarvestedState(blockstate);
        if (compound.contains("HLPX")) {
            int i = compound.getInt("HLPX");
            int j = compound.getInt("HLPY");
            int k = compound.getInt("HLPZ");
            this.entityData.set(LEAF_HARVESTED_POS, Optional.of(new BlockPos(i, j, k)));
        } else {
            this.entityData.set(LEAF_HARVESTED_POS, Optional.empty());
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("AttachFace", (byte) this.entityData.get(ATTACHED_FACE).get3DDataValue());
        compound.putBoolean("Leaf", this.hasLeaf());
        compound.putBoolean("Queen", this.isQueen());
        compound.putFloat("AntScale", this.getAntScale());
        BlockState blockstate = this.getHarvestedState();
        if (blockstate != null) {
            compound.put("HarvestedLeafState", NbtUtils.writeBlockState(blockstate));
        }
        if (this.hasHive()) {
            compound.put("HivePos", NbtUtils.writeBlockPos(this.getHivePos()));
        }
        compound.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
        compound.putInt("BabyCooldown", this.haveBabyCooldown);
        BlockPos blockpos = this.getHarvestedPos();
        if (blockpos != null) {
            compound.putInt("HLPX", blockpos.getX());
            compound.putInt("HLPY", blockpos.getY());
            compound.putInt("HLPZ", blockpos.getZ());
        }

    }

    public void setStayOutOfHiveCountdown(int p_226450_1_) {
        this.stayOutOfHiveCountdown = p_226450_1_;
    }

    private boolean isHiveNearFire() {
        if (this.hivePos == null) {
            return false;
        } else {
            BlockEntity tileentity = this.level().getBlockEntity(this.hivePos);
            return tileentity instanceof TileEntityLeafcutterAnthill && ((TileEntityLeafcutterAnthill) tileentity).isNearFire();
        }
    }

    private boolean doesHiveHaveSpace(BlockPos pos) {
        BlockEntity tileentity = this.level().getBlockEntity(pos);
        if (tileentity instanceof TileEntityLeafcutterAnthill) {
            return !((TileEntityLeafcutterAnthill) tileentity).isFullOfAnts();
        } else {
            return false;
        }
    }

    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Nullable
    public BlockPos getHivePos() {
        return this.hivePos;
    }


    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(EntityLeafcutterAnt caravanHeadIn) {
        this.caravanHead = caravanHeadIn;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTrail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public EntityLeafcutterAnt getCaravanHead() {
        return this.caravanHead;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return null;
    }

    public boolean shouldLeadCaravan() {
        return !this.hasLeaf();
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float)Mth.length(this.getX() - this.xo, 2 * (this.getY() - this.yo), this.getZ() - this.zo);
        float f2 = Math.min(f1 * 4.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
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
        return new Animation[]{ANIMATION_BITE};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public boolean doHurtTarget(Entity entityIn) {
        this.setAnimation(ANIMATION_BITE);
        return true;
    }

    private class ReturnToHiveGoal extends Goal {

        private int searchCooldown = 1;
        private BlockPos hivePos;
        private int approachTime = 0;

        public ReturnToHiveGoal() {
        }

        @Override
        public boolean canUse() {
            if(EntityLeafcutterAnt.this.stayOutOfHiveCountdown > 0){
                return false;
            }
            if (EntityLeafcutterAnt.this.hasLeaf() || EntityLeafcutterAnt.this.isQueen()) {
                searchCooldown--;
                BlockPos hive = EntityLeafcutterAnt.this.hivePos;
                if (hive != null && EntityLeafcutterAnt.this.level().getBlockEntity(hive) instanceof TileEntityLeafcutterAnthill) {
                    hivePos = hive;
                    return true;
                }
                if (searchCooldown <= 0) {
                    searchCooldown = 400;
                    PoiManager pointofinterestmanager = ((ServerLevel) level()).getPoiManager();
                    Stream<BlockPos> stream = pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL.getKey()), Predicates.alwaysTrue(), EntityLeafcutterAnt.this.blockPosition(), 100, PoiManager.Occupancy.ANY);
                    List<BlockPos> listOfHives = stream.collect(Collectors.toList());
                    BlockPos ret = null;
                    for (BlockPos pos : listOfHives) {
                        if (ret == null || pos.distSqr(EntityLeafcutterAnt.this.blockPosition()) < ret.distSqr(EntityLeafcutterAnt.this.blockPosition())) {
                            ret = pos;
                        }
                    }
                    hivePos = ret;
                    EntityLeafcutterAnt.this.hivePos = ret;
                    return hivePos != null;
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return hivePos != null && EntityLeafcutterAnt.this.distanceToSqr(Vec3.upFromBottomCenterOf(hivePos, 1)) > 1F;
        }

        public void stop() {
            this.hivePos = null;
            this.searchCooldown = 20;
            this.approachTime = 0;
        }

        public void tick() {
            double dist = EntityLeafcutterAnt.this.distanceToSqr(Vec3.upFromBottomCenterOf(hivePos, 1));
            if (dist < 1.2F && EntityLeafcutterAnt.this.getBlockPosBelowThatAffectsMyMovement().equals(hivePos)) {
                BlockEntity tileentity = EntityLeafcutterAnt.this.level().getBlockEntity(hivePos);
                if (tileentity instanceof TileEntityLeafcutterAnthill) {
                    TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) tileentity;
                    beehivetileentity.tryEnterHive(EntityLeafcutterAnt.this, EntityLeafcutterAnt.this.hasLeaf());
                }
            }
            if (dist < 16) {
                approachTime++;
                if(dist < 4){
                    Vec3 center = Vec3.upFromBottomCenterOf(hivePos, 1.1F);
                    Vec3 add = center.subtract(EntityLeafcutterAnt.this.position());
                    if(add.length() > 1F){
                        add = add.normalize();
                    }
                    add = add.scale(0.2F);
                    EntityLeafcutterAnt.this.setDeltaMovement(EntityLeafcutterAnt.this.getDeltaMovement().add(add));
                }
                if(dist < (approachTime < 200 ? 2 : 10) && EntityLeafcutterAnt.this.getY() >= hivePos.getY()){
                    if(EntityLeafcutterAnt.this.getAttachmentFacing() != Direction.DOWN){
                        EntityLeafcutterAnt.this.setDeltaMovement(EntityLeafcutterAnt.this.getDeltaMovement().add(0, 0.1, 0));
                    }
                   EntityLeafcutterAnt.this.getMoveControl().setWantedPosition((double) hivePos.getX() + 0.5F, (double) hivePos.getY() + 1.5F, (double) hivePos.getZ() + 0.5F, 1.0D);
                }
                EntityLeafcutterAnt.this.navigation.resetMaxVisitedNodesMultiplier();
                EntityLeafcutterAnt.this.navigation.moveTo((double) hivePos.getX() + 0.5F, (double) hivePos.getY() + 1.6F, (double) hivePos.getZ() + 0.5F, 1.0D);
            } else {
                startMovingToFar(this.hivePos);
            }
        }

        private boolean startMovingToFar(BlockPos pos) {
            EntityLeafcutterAnt.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
            EntityLeafcutterAnt.this.navigation.moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
            return EntityLeafcutterAnt.this.navigation.getPath() != null && EntityLeafcutterAnt.this.navigation.getPath().canReach();
        }

    }

    class AngerGoal extends HurtByTargetGoal {
        AngerGoal(EntityLeafcutterAnt beeIn) {
            super(beeIn);
            this.setAlertOthers(EntityLeafcutterAnt.class);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return EntityLeafcutterAnt.this.isAngry() && super.canContinueToUse();
        }

        protected void alertOther(Mob mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityLeafcutterAnt && this.mob.hasLineOfSight(targetIn)) {
                mobIn.setTarget(targetIn);
            }

        }
    }
}
