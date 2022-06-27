package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class EntitySugarGlider extends TamableAnimal implements IFollower {

    public static final ResourceLocation SUGAR_GLIDER_REWARD = new ResourceLocation("alexsmobs", "gameplay/sugar_glider_reward");
    public static final Map<Block, Item> LEAF_TO_SAPLING = Util.make(Maps.newHashMap(), (map) -> {
        map.put(Blocks.OAK_LEAVES, Items.OAK_SAPLING);
        map.put(Blocks.BIRCH_LEAVES, Items.BIRCH_SAPLING);
        map.put(Blocks.SPRUCE_LEAVES, Items.SPRUCE_SAPLING);
        map.put(Blocks.JUNGLE_LEAVES, Items.JUNGLE_SAPLING);
        map.put(Blocks.ACACIA_LEAVES, Items.ACACIA_SAPLING);
        map.put(Blocks.DARK_OAK_LEAVES, Items.DARK_OAK_SAPLING);
        map.put(Blocks.MANGROVE_LEAVES, Items.MANGROVE_PROPAGULE);
    });
    public static final Map<Block, List<Item>> LEAF_TO_RARES = Util.make(Maps.newHashMap(), (map) -> {
        map.put(Blocks.OAK_LEAVES, List.of(Items.APPLE));
        map.put(Blocks.JUNGLE_LEAVES, List.of(AMItemRegistry.BANANA.get(), AMItemRegistry.LEAFCUTTER_ANT_PUPA.get(), Items.COCOA_BEANS));
        map.put(Blocks.ACACIA_LEAVES, List.of(AMItemRegistry.ACACIA_BLOSSOM.get()));
    });

    private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> GLIDING = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FORAGING_TIME = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntitySugarGlider.class, EntityDataSerializers.BOOLEAN);
    private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private final int attachChangeCooldown = 0;
    public float glideProgress;
    public float prevGlideProgress;
    public float forageProgress;
    public float prevForageProgress;
    public float sitProgress;
    public float prevSitProgress;
    public float attachChangeProgress = 0F;
    public float prevAttachChangeProgress = 0F;
    public Direction prevAttachDir = Direction.DOWN;
    private boolean isGlidingNavigator;
    private boolean stopClimbing = false;
    private int forageCooldown = 0;
    private int detachCooldown = 0;
    private int rideCooldown = 0;

    protected EntitySugarGlider(EntityType type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        switchNavigator(true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FlyingAIFollowOwner(this, 1.0D, 5.0F, 2.0F, true));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.SWEET_BERRIES, Items.HONEYCOMB), false));
        this.goalSelector.addGoal(4, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new GlideGoal());
        this.goalSelector.addGoal(6, new PanicGoal(this, 1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 100, 1.0D, 10, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(ATTACHED_FACE, Direction.DOWN);
        this.entityData.define(GLIDING, false);
        this.entityData.define(FORAGING_TIME, 0);
        this.entityData.define(COMMAND, Integer.valueOf(0));
        this.entityData.define(SITTING, Boolean.valueOf(false));
    }

    private void switchNavigator(boolean onGround) {
        if (onGround) {
            this.moveControl = new MoveControl(this);
            this.navigation = new SmartClimbPathNavigator(this, level);
            this.isGlidingNavigator = false;
        } else {
            this.moveControl = new FlightMoveController(this, 0.6F, false);
            this.navigation = new DirectPathNavigator(this, level);
            this.isGlidingNavigator = true;
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.HONEYCOMB);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SUGAR_GLIDER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SUGAR_GLIDER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SUGAR_GLIDER_HURT.get();
    }


    public void tick() {
        super.tick();
        maxUpStep = 1F;
        prevGlideProgress = glideProgress;
        prevAttachChangeProgress = attachChangeProgress;
        prevForageProgress = forageProgress;
        prevSitProgress = sitProgress;
        if (attachChangeProgress > 0F) {
            attachChangeProgress -= 0.25F;
        }
        boolean glideVisual = this.isGliding() || this.getVehicle() != null && this.getVehicle().fallDistance != 0;
        if (glideProgress < 5F && isGliding()) {
            glideProgress += 2.5F;
        }
        if (glideProgress > 0F && !isGliding()) {
            glideProgress -= 2.5F;
        }
        if (forageProgress < 5F && getForagingTime() > 0) {
            forageProgress++;
        }
        if (forageProgress > 0F && getForagingTime() <= 0) {
            forageProgress--;
        }
        boolean sitVisual = this.isOrderedToSit() && !this.isInWater() && this.isOnGround();
        if (sitProgress < 5F && sitVisual) {
            sitProgress++;
        }
        if (sitProgress > 0F && !sitVisual) {
            sitProgress--;
        }
        if (isGliding()) {
            if (shouldStopGliding()) {
                this.setGliding(false);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.99D, 0.5D, 0.99D));
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.level.isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
            if (this.isOnGround() || this.isOrderedToSit() || this.isInWaterOrBubble() || this.isInLava() || this.isGliding() || this.isPassenger()) {
                this.entityData.set(ATTACHED_FACE, Direction.DOWN);
            } else {
                Direction closestDirection = Direction.DOWN;
                double closestDistance = 100;
                for (Direction dir : POSSIBLE_DIRECTIONS) {
                    BlockPos antPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
                    BlockPos offsetPos = antPos.relative(dir);
                    Vec3 offset = Vec3.atCenterOf(offsetPos);
                    if (closestDistance > this.position().distanceTo(offset) && level.loadedAndEntityCanStandOnFace(offsetPos, this, dir.getOpposite())) {
                        closestDistance = this.position().distanceTo(offset);
                        closestDirection = dir;
                    }
                }
                this.entityData.set(ATTACHED_FACE, closestDistance > this.getBbWidth() * 0.5F + 0.7F ? Direction.DOWN : closestDirection);
            }
        }
        boolean flag = false;
        if (this.getAttachmentFacing() != Direction.DOWN) {
            if (!this.horizontalCollision && this.getAttachmentFacing() != Direction.UP) {
                Vec3 vec = Vec3.atLowerCornerOf(this.getAttachmentFacing().getNormal());
                this.setDeltaMovement(vector3d.add(vec.normalize().multiply(0.1F, 0.1F, 0.1F)));
            }
            if (!this.onGround && vector3d.y < 0.0D) {
                this.setDeltaMovement(vector3d.multiply(1.0D, 0.5D, 1.0D));
                flag = true;
            }
        }
        if (this.getAttachmentFacing() != Direction.DOWN && !this.isGliding()) {
            this.setNoGravity(true);
            this.setDeltaMovement(vector3d.multiply(0.6D, 0.4D, 0.6D));
        } else {
            this.setNoGravity(false);
        }
        if (prevAttachDir != this.getAttachmentFacing()) {
            attachChangeProgress = 1F;
        }
        this.prevAttachDir = this.getAttachmentFacing();
        if (!this.level.isClientSide) {
            if ((this.getAttachmentFacing() == Direction.UP || this.isGliding()) && !this.isGlidingNavigator) {
                switchNavigator(false);
            }
            if (this.getAttachmentFacing() != Direction.UP && this.isGlidingNavigator) {
                switchNavigator(true);
            }
        }
        BlockPos on = this.blockPosition().relative(this.getAttachmentFacing());
        if (shouldForage() && level.getBlockState(on).is(BlockTags.LEAVES)) {
            BlockState state = level.getBlockState(on);
            if (this.getForagingTime() < 100) {
                if (random.nextInt(2) == 0) {
                    for (int i = 0; i < 4 + random.nextInt(2); i++) {
                        double motX = this.random.nextGaussian() * 0.02D;
                        double motY = this.random.nextGaussian() * 0.02D;
                        double motZ = this.random.nextGaussian() * 0.02D;
                        level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), on.getX() + random.nextFloat(), on.getY() + random.nextFloat(), on.getZ() + random.nextFloat(), motX, motY, motZ);
                    }
                }
                this.setForagingTime(this.getForagingTime() + 1);
            } else {
                if (!level.isClientSide) {
                    List<ItemStack> lootList = getForageLoot(state);
                    if (lootList.size() > 0) {
                        for (ItemStack stack : lootList) {
                            ItemEntity e = this.spawnAtLocation(stack.copy());
                            e.hasImpulse = true;
                            e.setDeltaMovement(e.getDeltaMovement().multiply(0.2, 0.2, 0.2));
                        }
                    }
                }
                this.forageCooldown = 8000 + 8000 * random.nextInt(2);
                this.setForagingTime(0);
            }
        } else {
            this.setForagingTime(0);
        }
        if (detachCooldown > 0) {
            detachCooldown--;
        }
        if (rideCooldown > 0) {
            rideCooldown--;
        }
    }

    public void rideTick() {
        Entity entity = this.getVehicle();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else if (isTame() && entity instanceof LivingEntity && isOwnedBy((LivingEntity) entity)) {
            this.setDeltaMovement(0, 0, 0);
            this.tick();
            if (this.isPassenger()) {
                Entity mount = this.getVehicle();
                if (mount instanceof Player) {
                    ((LivingEntity) mount).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, true, false));
                    this.yBodyRot = ((LivingEntity) mount).yBodyRot;
                    this.setYRot(mount.getYRot());
                    this.yHeadRot = ((LivingEntity) mount).yHeadRot;
                    this.yRotO = ((LivingEntity) mount).yHeadRot;
                    float radius = 0F;
                    float angle = (0.01745329251F * (((LivingEntity) mount).yBodyRot - 180F));
                    double extraX = radius * Mth.sin((float) (Math.PI + angle));
                    double extraZ = radius * Mth.cos(angle);
                    this.setPos(mount.getX() + extraX, Math.max(mount.getY() + mount.getBbHeight() + 0.1, mount.getY()), mount.getZ() + extraZ);
                    if (!mount.isAlive() || rideCooldown == 0 && mount.isShiftKeyDown()) {
                        this.removeVehicle();
                    }
                }

            }
        } else {
            super.rideTick();
        }

    }

    private List<ItemStack> getForageLoot(BlockState leafState) {
        Item sapling = LEAF_TO_SAPLING.get(leafState.getBlock());
        List<Item> rares = LEAF_TO_RARES.get(leafState.getBlock());
        float rng = this.getRandom().nextFloat();
        if (rng < 0.1F && rares != null) {
            Item item = rares.size() <= 1 ? rares.get(0) : rares.get(this.getRandom().nextInt(rares.size()));
            return List.of(new ItemStack(item));
        }
        if (rng < 0.25F && sapling != null) {
            return List.of(new ItemStack(sapling));
        }
        LootTable loottable = this.level.getServer().getLootTables().get(SUGAR_GLIDER_REWARD);
        return loottable.getRandomItems((new LootContext.Builder((ServerLevel) this.level)).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.BLOCK_STATE, leafState).withRandom(this.level.random).create(LootContextParamSets.PIGLIN_BARTER));

    }

    public void travel(Vec3 travelVector) {
        if (this.isOrderedToSit()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
        }
        super.travel(travelVector);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(ATTACHED_FACE, Direction.from3DDataValue(compound.getByte("AttachFace")));
        this.setCommand(compound.getInt("SugarGliderCommand"));
        this.setOrderedToSit(compound.getBoolean("SugarGliderSitting"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("AttachFace", (byte) this.entityData.get(ATTACHED_FACE).get3DDataValue());
        compound.putInt("SugarGliderCommand", this.getCommand());
        compound.putBoolean("SugarGliderSitting", this.isOrderedToSit());
    }

    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void onInsideBlock(BlockState state) {

    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || super.isInvulnerableTo(source);
    }

    @Override
    public boolean onClimbable() {
        return this.isBesideClimbableBlock() && !this.isGliding() && !stopClimbing && !this.isOrderedToSit();
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

    public Direction getAttachmentFacing() {
        return this.entityData.get(ATTACHED_FACE);
    }

    public boolean isGliding() {
        return this.entityData.get(GLIDING).booleanValue();
    }

    public void setGliding(boolean gliding) {
        this.entityData.set(GLIDING, gliding);
    }

    public int getForagingTime() {
        return this.entityData.get(FORAGING_TIME);
    }

    public void setForagingTime(int feedingTime) {
        this.entityData.set(FORAGING_TIME, feedingTime);
    }

    @Override
    public boolean isOrderedToSit() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (!isTame() && itemstack.is(Items.SWEET_BERRIES)) {
            this.usePlayerItem(player, hand, itemstack);
            this.playSound(SoundEvents.FOX_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (getRandom().nextInt(2) == 0) {
                this.tame(player);
                this.level.broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level.broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }
        if (isTame() && itemstack.is(AMTagRegistry.INSECT_ITEMS)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.usePlayerItem(player, hand, itemstack);
                this.playSound(SoundEvents.FOX_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.heal(5);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;

        }
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && isTame() && isOwnedBy(player) && !isFood(itemstack)) {
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
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.setOrderedToSit(true);
                    return InteractionResult.SUCCESS;
                } else {
                    this.setOrderedToSit(false);
                    return InteractionResult.SUCCESS;
                }
            }

        }
        return type;
    }

    @Override
    public void calculateEntityAnimation(LivingEntity entity, boolean b) {
        entity.animationSpeedOld = entity.animationSpeed;
        double d0 = entity.getX() - entity.xo;
        double d1 = (entity.getY() - entity.yo) * 2.0F;
        double d2 = entity.getZ() - entity.zo;
        float f = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2)) * 6.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        entity.animationSpeed += (f - entity.animationSpeed) * 0.4F;
        entity.animationPosition += entity.animationSpeed;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation(this, worldIn) {
            protected boolean canUpdatePath() {
                return super.canUpdatePath() || ((EntitySugarGlider) mob).isBesideClimbableBlock() || mob.jumping;
            }
        };
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return AMEntityRegistry.SUGAR_GLIDER.get().create(serverLevel);
    }

    private boolean shouldStopGliding() {
        return this.isOnGround() || this.getAttachmentFacing() != Direction.DOWN;
    }

    private boolean shouldForage() {
        return this.isTame() && !this.isBaby() && forageCooldown == 0;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    public void followEntity(TamableAnimal tameable, LivingEntity owner, double followSpeed) {
        if (this.distanceTo(owner) < 5 || this.isBaby()) {
            this.setGliding(!this.isOnGround());
            this.getNavigation().moveTo(owner, followSpeed);

        } else {
            Vec3 fly = new Vec3(0, 0, 0);
            float f = 0.5F;
            if(this.isOnGround()){
                fly = fly.add(0, 0.4, 0);
                f = 0.9F;
            }
            fly = fly.add(owner.getEyePosition().subtract(EntitySugarGlider.this.position()).normalize().scale(f));
            this.setDeltaMovement(fly);
            Vec3 move = this.getDeltaMovement();
            double d0 = move.horizontalDistance();
            this.setXRot((float) (-Mth.atan2(move.y, d0) * (double) (180F / (float) Math.PI)));
            this.setYRot(((float) Mth.atan2(move.z, move.x)) * (180F / (float) Math.PI) - 90F);
            this.setGliding(true);
        }
    }

    private boolean canSeeBlock(BlockPos destinationBlock) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
        BlockHitResult result = this.level.clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return result.getBlockPos().equals(destinationBlock);
    }

    private class GlideGoal extends Goal {

        private boolean climbing;
        private int climbTime = 0;
        private int leapSearchCooldown = 0;
        private int climbTimeout = 0;
        private BlockPos climb;
        private BlockPos glide;
        private boolean itsOver = false;
        private int airtime = 0;
        private Direction climbOffset = Direction.UP;

        private GlideGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (EntitySugarGlider.this.getForagingTime() <= 0 && !EntitySugarGlider.this.isBaby() && !EntitySugarGlider.this.isOrderedToSit() && EntitySugarGlider.this.getRandom().nextInt(45) == 0) {
                if (EntitySugarGlider.this.getAttachmentFacing() != Direction.DOWN) {
                    climb = EntitySugarGlider.this.blockPosition().relative(EntitySugarGlider.this.getAttachmentFacing());
                } else {
                    climb = findClimbPos();
                }
                return climb != null;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return climb != null && !itsOver && climbTimeout < 30 && (!climbing || !level.isEmptyBlock(climb) && !EntitySugarGlider.this.getNavigation().isStuck()) && EntitySugarGlider.this.getForagingTime() <= 0 && !EntitySugarGlider.this.isOrderedToSit();
        }

        public void start() {
            climbTimeout = 0;
            leapSearchCooldown = 0;
            airtime = 0;
            climbing = true;
            climbTime = 0;
            EntitySugarGlider.this.getNavigation().stop();
        }

        public void stop() {
            climbTimeout = 0;
            climb = null;
            glide = null;
            itsOver = false;
            EntitySugarGlider.this.stopClimbing = false;
            EntitySugarGlider.this.setGliding(false);
            EntitySugarGlider.this.getNavigation().stop();
        }

        public void tick() {
            if (leapSearchCooldown > 0) {
                leapSearchCooldown--;
            }
            if (climbing) {
                float inDir = EntitySugarGlider.this.getAttachmentFacing() == Direction.DOWN && EntitySugarGlider.this.getY() > climb.getY() + 0.3F ? 0.5F + EntitySugarGlider.this.getBbWidth() * 0.5F : 0.5F;
                Vec3 offset = Vec3.atCenterOf(climb).subtract(0, 0, 0).add(climbOffset.getStepX() * inDir, climbOffset.getStepY() * inDir, climbOffset.getStepZ() * inDir);
                double d0 = climb.getX() + 0.5F - EntitySugarGlider.this.getX();
                double d2 = climb.getZ() + 0.5F - EntitySugarGlider.this.getZ();
                double xzDistSqr = d0 * d0 + d2 * d2;
                if (EntitySugarGlider.this.getY() > offset.y - 0.3F - EntitySugarGlider.this.getBbHeight()) {
                    EntitySugarGlider.this.stopClimbing = true;
                }
                if (xzDistSqr < 3 && EntitySugarGlider.this.getAttachmentFacing() != Direction.DOWN) {
                    Vec3 silly = new Vec3(d0, 0, d2).normalize().scale(0.1);
                    EntitySugarGlider.this.setDeltaMovement(EntitySugarGlider.this.getDeltaMovement().add(silly));
                } else {
                    EntitySugarGlider.this.getNavigation().moveTo(offset.x, offset.y, offset.z, 1F);
                }
                if (EntitySugarGlider.this.getAttachmentFacing() == Direction.DOWN) {
                    climbTimeout++;
                    climbTime = 0;
                } else {
                    climbTimeout = 0;
                    climbTime++;
                    if (climbTime > 40 && leapSearchCooldown == 0) {
                        BlockPos leapTo = findLeapPos(EntitySugarGlider.this.shouldForage() && random.nextInt(5) != 0);
                        leapSearchCooldown = 5 + EntitySugarGlider.this.getRandom().nextInt(10);
                        if (leapTo != null) {
                            EntitySugarGlider.this.stopClimbing = false;
                            EntitySugarGlider.this.setGliding(true);
                            EntitySugarGlider.this.getNavigation().stop();
                            EntitySugarGlider.this.entityData.set(ATTACHED_FACE, Direction.DOWN);
                            glide = leapTo;
                            climbing = false;
                        }
                    }
                }
            } else if (glide != null) {
                EntitySugarGlider.this.stopClimbing = false;
                EntitySugarGlider.this.setGliding(true);
                double dist = Math.sqrt(EntitySugarGlider.this.distanceToSqr(Vec3.atCenterOf(glide)));
                if (airtime > 5 && (EntitySugarGlider.this.horizontalCollision || EntitySugarGlider.this.isOnGround() || dist < 1.1F)) {
                    EntitySugarGlider.this.setGliding(false);
                    EntitySugarGlider.this.detachCooldown = 20 + random.nextInt(80);
                    itsOver = true;
                }
                Vec3 fly = Vec3.atCenterOf(glide).subtract(EntitySugarGlider.this.position()).normalize().scale(0.3F);
                EntitySugarGlider.this.setDeltaMovement(fly);

                Vec3 move = EntitySugarGlider.this.getDeltaMovement();
                double d0 = move.horizontalDistance();
                EntitySugarGlider.this.setXRot((float) (-Mth.atan2(move.y, d0) * (double) (180F / (float) Math.PI)));
                EntitySugarGlider.this.setYRot(((float) Mth.atan2(move.z, move.x)) * (180F / (float) Math.PI) - 90F);
                airtime++;
            }
        }

        private BlockPos findClimbPos() {
            BlockPos mobPos = EntitySugarGlider.this.blockPosition();
            for (int i = 0; i < 15; i++) {
                BlockPos offset = mobPos.offset(EntitySugarGlider.this.random.nextInt(16) - 8, random.nextInt(4) + 1, EntitySugarGlider.this.random.nextInt(16) - 8);
                double d0 = offset.getX() + 0.5F - EntitySugarGlider.this.getX();
                double d2 = offset.getZ() + 0.5F - EntitySugarGlider.this.getZ();
                double xzDistSqr = d0 * d0 + d2 * d2;
                Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(offset);
                BlockHitResult result = level.clip(new ClipContext(EntitySugarGlider.this.getEyePosition(), blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, EntitySugarGlider.this));
                if (result.getType() != HitResult.Type.MISS && xzDistSqr > 4 && result.getDirection().getAxis() != Direction.Axis.Y && getDistanceOffGround(result.getBlockPos().relative(result.getDirection())) > 3 && isPositionEasilyClimbable(result.getBlockPos())) {
                    climbOffset = result.getDirection();
                    return result.getBlockPos();
                }
            }
            return null;
        }

        private BlockPos findLeapPos(boolean leavesOnly) {
            BlockPos mobPos = EntitySugarGlider.this.blockPosition().relative(climbOffset.getOpposite());
            for (int i = 0; i < 15; i++) {
                BlockPos offset = mobPos.offset(EntitySugarGlider.this.random.nextInt(32) - 16, -1 - random.nextInt(4), EntitySugarGlider.this.random.nextInt(32) - 16);
                Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(offset);
                BlockHitResult result = level.clip(new ClipContext(EntitySugarGlider.this.getEyePosition(), blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, EntitySugarGlider.this));
                if (result.getType() != HitResult.Type.MISS && result.getBlockPos().distSqr(mobPos) > 4) {
                    if (leavesOnly && !level.getBlockState(result.getBlockPos()).is(BlockTags.LEAVES)) {
                        continue;
                    }
                    return result.getBlockPos();
                }
            }
            return null;
        }

        private int getDistanceOffGround(BlockPos pos) {
            int dist = 0;
            while (pos.getY() > -64 && EntitySugarGlider.this.level.isEmptyBlock(pos)) {
                pos = pos.below();
                dist++;
            }
            return dist;
        }

        private boolean isPositionEasilyClimbable(BlockPos pos) {
            pos = pos.below();
            while (pos.getY() > EntitySugarGlider.this.getY() && !EntitySugarGlider.this.level.isEmptyBlock(pos)) {
                pos = pos.below();
            }
            return pos.getY() <= EntitySugarGlider.this.getY();
        }
    }
}
