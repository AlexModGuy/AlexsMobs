package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIRevealTreasure;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class EntitySeagull extends Animal implements ITargetsDroppedItems {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> FLIGHT_LOOK_YAW = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> TREASURE_POS = SynchedEntityData.defineId(EntitySeagull.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public float prevFlyProgress;
    public float flyProgress;
    public float prevFlapAmount;
    public float flapAmount;
    public boolean aiItemFlag = false;
    public float attackProgress;
    public float prevAttackProgress;
    public float sitProgress;
    public float prevSitProgress;
    public int stealCooldown = random.nextInt(2500);
    private boolean isLandNavigator;
    private int timeFlying;
    private BlockPos orbitPos = null;
    private double orbitDist = 5D;
    private boolean orbitClockwise = false;
    private boolean fallFlag = false;
    private int flightLookCooldown = 0;
    private float targetFlightLookYaw;
    private int heldItemTime = 0;
    public int treasureSitTime;
    public UUID feederUUID = null;

    protected EntitySeagull(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
        switchNavigator(false);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SEAGULL_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SEAGULL_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SEAGULL_HURT.get();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("Sitting", this.isSitting());
        compound.putInt("StealCooldown", this.stealCooldown);
        compound.putInt("TreasureSitTime", this.treasureSitTime);
        if(feederUUID != null){
            compound.putUUID("FeederUUID", feederUUID);
        }
        if(this.getTreasurePos() != null){
            compound.putInt("TresX", this.getTreasurePos().getX());
            compound.putInt("TresY", this.getTreasurePos().getY());
            compound.putInt("TresZ", this.getTreasurePos().getZ());
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setSitting(compound.getBoolean("Sitting"));
        this.stealCooldown = compound.getInt("StealCooldown");
        this.treasureSitTime = compound.getInt("TreasureSitTime");
        if(compound.hasUUID("FeederUUID")){
            this.feederUUID = compound.getUUID("FeederUUID");
        }
        if(compound.contains("TresX") && compound.contains("TresY") && compound.contains("TresZ")){
            this.setTreasurePos(new BlockPos(compound.getInt("TresX"), compound.getInt("TresY"), compound.getInt("TresZ")));
        }
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new SeagullAIRevealTreasure(this));
        this.targetSelector.addGoal(2, new SeagullAIStealFromPlayers(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(Items.COD, AMItemRegistry.LOBSTER_TAIL.get(), AMItemRegistry.COOKED_LOBSTER_TAIL.get()), false){
            public boolean canUse(){
                return !EntitySeagull.this.aiItemFlag && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new AIWanderIdle());
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new AIScatter());
        this.targetSelector.addGoal(1, new AITargetItems(this, false, false, 15, 16));
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.COD;
    }

    public static boolean canSeagullSpawn(EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getRawBrightness(pos, 0) > 8 && worldIn.getFluidState(pos.below()).isEmpty();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.seagullSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new MoveHelper(this);
            this.navigation = new DirectPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(SITTING, false);
        this.entityData.define(ATTACK_TICK, 0);
        this.entityData.define(TREASURE_POS, Optional.empty());
        this.entityData.define(FLIGHT_LOOK_YAW, 0F);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if (flying && this.isBaby()) {
            flying = false;
        }
        this.entityData.set(FLYING, flying);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
    }

    public float getFlightLookYaw() {
        return entityData.get(FLIGHT_LOOK_YAW);
    }

    public void setFlightLookYaw(float yaw) {
        entityData.set(FLIGHT_LOOK_YAW, yaw);
    }

    public BlockPos getTreasurePos() {
        return this.entityData.get(TREASURE_POS).orElse(null);
    }

    public void setTreasurePos(BlockPos pos) {
        this.entityData.set(TREASURE_POS, Optional.ofNullable(pos));
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            boolean prev = super.hurt(source, amount);
            if (prev) {
                this.setSitting(false);
                if (!this.getMainHandItem().isEmpty()) {
                    this.spawnAtLocation(this.getMainHandItem());
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    stealCooldown = 1500 + random.nextInt(1500);
                }
                this.feederUUID = null;
                this.treasureSitTime = 0;
            }
            return prev;
        }
    }

    public void tick() {
        super.tick();
        this.prevFlyProgress = flyProgress;
        this.prevFlapAmount = flapAmount;
        this.prevAttackProgress = attackProgress;
        this.prevSitProgress = sitProgress;
        float yMot = (float) -((float) this.getDeltaMovement().y * (double) (180F / (float) Math.PI));
        float absYaw = Math.abs(this.getYRot() - this.yRotO);
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (isSitting() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress--;
        }
        if (absYaw > 8) {
            flapAmount = Math.min(1F, flapAmount + 0.1F);
        } else if (yMot < 0.0F) {
            flapAmount = Math.min(-yMot * 0.2F, 1F);
        } else {
            if (flapAmount > 0.0F) {
                flapAmount -= Math.min(flapAmount, 0.05F);
            } else {
                flapAmount = 0;
            }
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        if (!this.level().isClientSide) {
            if (isFlying()) {
                float lookYawDist = Math.abs(this.getFlightLookYaw() - targetFlightLookYaw);
                if (flightLookCooldown > 0) {
                    flightLookCooldown--;
                }
                if (flightLookCooldown == 0 && this.random.nextInt(4) == 0 && lookYawDist < 0.5F) {
                    targetFlightLookYaw = Mth.clamp(random.nextFloat() * 120F - 60, -60, 60);
                    flightLookCooldown = 3 + random.nextInt(15);
                }
                if (this.getFlightLookYaw() < this.targetFlightLookYaw && lookYawDist > 0.5F) {
                    this.setFlightLookYaw(this.getFlightLookYaw() + Math.min(lookYawDist, 4F));
                }
                if (this.getFlightLookYaw() > this.targetFlightLookYaw && lookYawDist > 0.5F) {
                    this.setFlightLookYaw(this.getFlightLookYaw() - Math.min(lookYawDist, 4F));
                }
                if (this.onGround() && !this.isInWaterOrBubble() && this.timeFlying > 30) {
                    this.setFlying(false);
                }
                timeFlying++;
                this.setNoGravity(true);
                if (this.isPassenger() || this.isInLove()) {
                    this.setFlying(false);
                }
            } else {
                fallFlag = false;
                timeFlying = 0;
                this.setNoGravity(false);
            }
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
        }
        if (!this.getMainHandItem().isEmpty()) {
            heldItemTime++;
            if (heldItemTime > 200 && canTargetItem(this.getMainHandItem())) {
                heldItemTime = 0;
                this.heal(4);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                if (this.getMainHandItem().hasCraftingRemainingItem()) {
                    this.spawnAtLocation(this.getMainHandItem().getCraftingRemainingItem());
                }
                eatItemEffect(this.getMainHandItem());
                this.getMainHandItem().shrink(1);
            }
        } else {
            heldItemTime = 0;
        }
        if (stealCooldown > 0) {
            stealCooldown--;
        }
        if(treasureSitTime > 0){
            treasureSitTime--;
        }
        if(this.isSitting() && this.isInWaterOrBubble()){
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.02F, 0));
        }
    }

    public void eatItem(){
        heldItemTime = 200;
    }
    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isEdible() && !this.isSitting();
    }

    private void eatItemEffect(ItemStack heldItemMainhand) {
        for (int i = 0; i < 2 + random.nextInt(2); i++) {
            double d2 = this.random.nextGaussian() * 0.02D;
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            float radius = this.getBbWidth() * 0.65F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
            if (heldItemMainhand.getItem() instanceof BlockItem) {
                data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem) heldItemMainhand.getItem()).getBlock().defaultBlockState());
            }
            this.level().addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
        }
    }

    public void setDataFromTreasureMap(Player player){
        boolean flag = false;
        for(ItemStack map : player.getHandSlots()){
            if(map.getItem() == Items.FILLED_MAP || map.getItem() == Items.MAP){
                if (map.hasTag() && map.getTag().contains("Decorations", 9)) {
                    ListTag listnbt = map.getTag().getList("Decorations", 10);
                    for(int i = 0; i < listnbt.size(); i++){
                        CompoundTag nbt = listnbt.getCompound(i);
                        byte type = nbt.getByte("type");
                        if(type == MapDecoration.Type.RED_X.getIcon() || type == MapDecoration.Type.TARGET_X.getIcon()){
                            int x = nbt.getInt("x");
                            int z = nbt.getInt("z");
                            if(this.distanceToSqr(x, this.getY(), z) <= 400){
                                flag = true;
                                this.setTreasurePos(new BlockPos(x, 0, z));
                            }
                        }
                    }
                }
            }
        }
        if(flag){
            this.feederUUID = player.getUUID();
            this.treasureSitTime = 300;
            this.stealCooldown = 1500 + random.nextInt(1500);
        }
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

    public boolean isWingull() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && s.toLowerCase().equals("wingull");
    }

    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        stealCooldown += 600 + random.nextInt(1200);
        Entity thrower = e.getOwner();
        if(thrower != null && (e.getItem().getItem() == AMItemRegistry.LOBSTER_TAIL.get() || e.getItem().getItem() == AMItemRegistry.COOKED_LOBSTER_TAIL.get())){
            Player player = level().getPlayerByUUID(thrower.getUUID());
            if(player != null){
                setDataFromTreasureMap(player);
                feederUUID = thrower.getUUID();
            }
        }
        this.setFlying(true);
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 5 + radiusAdd + this.getRandom().nextInt(5);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), 0, (int) (fleePos.z() + extraZ));
        BlockPos ground = getSeagullGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 8 + this.getRandom().nextInt(4);
        BlockPos newPos = ground.above(distFromGround > 3 ? flightHeight : this.getRandom().nextInt(4) + 8);
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    public BlockPos getSeagullGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), (int) this.getY(), in.getZ());
        while (position.getY() < 320 && !level().getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > -64 && !level().getBlockState(position).isSolid() && level().getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return position;
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        float radius = 10 + this.getRandom().nextInt(15);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin((float) (Math.PI + angle));
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getSeagullGround(radialPos);
        if (ground.getY() == 0) {
            return this.position();
        } else {
            ground = this.blockPosition();
            while (ground.getY() > -62 && !level().getBlockState(ground).isSolid()) {
                ground = ground.below();
            }
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground);
        }
        return null;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        return this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private Vec3 getOrbitVec(Vec3 vector3d, float gatheringCircleDist) {
        float angle = (0.01745329251F * (float) this.orbitDist * (orbitClockwise ? -tickCount : tickCount));
        double extraX = gatheringCircleDist * Mth.sin((angle));
        double extraZ = gatheringCircleDist * Mth.cos(angle);
        if (this.orbitPos != null) {
            Vec3 pos = new Vec3(orbitPos.getX() + extraX, orbitPos.getY() + random.nextInt(2), orbitPos.getZ() + extraZ);
            if (this.level().isEmptyBlock(AMBlockPos.fromVec3(pos))) {
                return pos;
            }
        }
        return null;
    }

    private boolean isOverWaterOrVoid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > -64 && level().isEmptyBlock(position)) {
            position = position.below();
        }
        return !level().getFluidState(position).isEmpty() || position.getY() <= -64;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (!this.getMainHandItem().isEmpty() && type != InteractionResult.SUCCESS) {
            this.spawnAtLocation(this.getMainHandItem().copy());
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            stealCooldown = 1500 + random.nextInt(1500);
            return InteractionResult.SUCCESS;
        } else {
            return type;
        }
    }


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return AMEntityRegistry.SEAGULL.get().create(serverWorld);
    }

    public void peck() {
        this.entityData.set(ATTACK_TICK, 7);
    }

    private class AIScatter extends Goal {
        protected final EntitySeagull.AIScatter.Sorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vec3 flightTarget = null;
        private int cooldown = 0;

        AIScatter() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new EntitySeagull.AIScatter.Sorter(EntitySeagull.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && e.getType().is(AMTagRegistry.SCATTERS_CROWS) || e instanceof Player && !((Player) e).isCreative();
                }
            };
        }

        @Override
        public boolean canUse() {
            if (EntitySeagull.this.isPassenger() || EntitySeagull.this.isSitting() || EntitySeagull.this.aiItemFlag || EntitySeagull.this.isVehicle()) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntitySeagull.this.level().getGameTime() % 10;
                if (EntitySeagull.this.getNoActionTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntitySeagull.this.getRandom().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntitySeagull.this.level().getEntitiesOfClass(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return targetEntity != null;
        }

        public void stop() {
            flightTarget = null;
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (flightTarget != null) {
                EntitySeagull.this.setFlying(true);
                EntitySeagull.this.getMoveControl().setWantedPosition(flightTarget.x, flightTarget.y, flightTarget.z, 1F);
                if (cooldown == 0 && EntitySeagull.this.isTargetBlocked(flightTarget)) {
                    cooldown = 30;
                    flightTarget = null;
                }
            }

            if (targetEntity != null) {
                if (EntitySeagull.this.onGround() || flightTarget == null || flightTarget != null && EntitySeagull.this.distanceToSqr(flightTarget) < 3) {
                    Vec3 vec = EntitySeagull.this.getBlockInViewAway(targetEntity.position(), 0);
                    if (vec != null && vec.y() > EntitySeagull.this.getY()) {
                        flightTarget = vec;
                    }
                }
                if (EntitySeagull.this.distanceTo(targetEntity) > 20.0F) {
                    this.stop();
                }
            }
        }

        protected double getTargetDistance() {
            return 4D;
        }

        protected AABB getTargetableArea(double targetDistance) {
            Vec3 renderCenter = new Vec3(EntitySeagull.this.getX(), EntitySeagull.this.getY() + 0.5, EntitySeagull.this.getZ());
            AABB aabb = new AABB(-2, -2, -2, 2, 2, 2);
            return aabb.move(renderCenter);
        }


        public class Sorter implements Comparator<Entity> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_) {
                double d0 = this.theEntity.distanceToSqr(p_compare_1_);
                double d1 = this.theEntity.distanceToSqr(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
        }
    }

    private class AIWanderIdle extends Goal {
        protected final EntitySeagull eagle;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;
        private int orbitResetCooldown = 0;
        private int maxOrbitTime = 360;
        private int orbitTime = 0;

        public AIWanderIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.eagle = EntitySeagull.this;
        }

        @Override
        public boolean canUse() {
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if ((eagle.getTarget() != null && eagle.getTarget().isAlive() && !this.eagle.isVehicle()) || eagle.isSitting() || this.eagle.isPassenger()) {
                return false;
            } else {
                if (this.eagle.getRandom().nextInt(20) != 0 && !eagle.isFlying() || eagle.aiItemFlag) {
                    return false;
                }
                if (this.eagle.isBaby()) {
                    this.flightTarget = false;
                } else if (this.eagle.isInWaterOrBubble()) {
                    this.flightTarget = true;
                } else if (this.eagle.onGround()) {
                    this.flightTarget = random.nextInt(10) == 0;
                } else {
                    if (orbitResetCooldown == 0 && random.nextInt(6) == 0) {
                        orbitResetCooldown = 100 + random.nextInt(300);
                        eagle.orbitPos = eagle.blockPosition();
                        eagle.orbitDist = 4 + random.nextInt(5);
                        eagle.orbitClockwise = random.nextBoolean();
                        orbitTime = 0;
                        maxOrbitTime = (int) (180 + 360 * random.nextFloat());
                    }
                    this.flightTarget = random.nextInt(5) != 0 && eagle.timeFlying < 400;
                }
                Vec3 lvt_1_1_ = this.getPosition();
                if (lvt_1_1_ == null) {
                    return false;
                } else {
                    this.x = lvt_1_1_.x;
                    this.y = lvt_1_1_.y;
                    this.z = lvt_1_1_.z;
                    return true;
                }
            }
        }

        public void tick() {
            if (orbitResetCooldown > 0) {
                orbitResetCooldown--;
            }
            if (orbitResetCooldown < 0) {
                orbitResetCooldown++;
            }
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                if (orbitTime < maxOrbitTime && !eagle.isInWaterOrBubble()) {
                    orbitTime++;
                } else {
                    orbitTime = 0;
                    eagle.orbitPos = null;
                    orbitResetCooldown = -400 - random.nextInt(400);
                }
            }
            if (eagle.horizontalCollision && !eagle.onGround()) {
                stop();
            }
            if (flightTarget) {
                eagle.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                if (!eagle.isFlying() || eagle.onGround()) {
                    this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1F);
                }
            }
            if (!flightTarget && isFlying()) {
                eagle.fallFlag = true;
                if (eagle.onGround()) {
                    eagle.setFlying(false);
                    orbitTime = 0;
                    eagle.orbitPos = null;
                    orbitResetCooldown = -400 - random.nextInt(400);
                }
            }
            if (isFlying() && (!level().isEmptyBlock(eagle.getBlockPosBelowThatAffectsMyMovement()) || eagle.onGround()) && !eagle.isInWaterOrBubble() && eagle.timeFlying > 30) {
                eagle.setFlying(false);
                orbitTime = 0;
                eagle.orbitPos = null;
                orbitResetCooldown = -400 - random.nextInt(400);
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = eagle.position();
            if (orbitResetCooldown > 0 && eagle.orbitPos != null) {
                return eagle.getOrbitVec(vector3d, 4 + random.nextInt(4));
            }
            if (eagle.isVehicle() || eagle.isOverWaterOrVoid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (eagle.timeFlying < 340 || eagle.isVehicle() || eagle.isOverWaterOrVoid()) {
                    return eagle.getBlockInViewAway(vector3d, 0);
                } else {
                    return eagle.getBlockGrounding(vector3d);
                }
            } else {
                return LandRandomPos.getPos(this.eagle, 10, 7);
            }
        }

        public boolean canContinueToUse() {
            if (flightTarget) {
                return eagle.isFlying() && eagle.distanceToSqr(x, y, z) > 5F;
            } else {
                return (!this.eagle.getNavigation().isDone()) && !this.eagle.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                eagle.setFlying(true);
                eagle.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.eagle.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.eagle.getNavigation().stop();
            super.stop();
        }
    }

    class MoveHelper extends MoveControl {
        private final EntitySeagull parentEntity;

        public MoveHelper(EntitySeagull bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d5 = vector3d.length();
                if (d5 < 0.3) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    double d1 = this.wantedY - this.parentEntity.getY();
                    float yScale = d1 > 0 || fallFlag ? 1F : 0.7F;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(speedModifier * 0.03D / d5)));
                    Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                    parentEntity.yBodyRot = parentEntity.getYRot();

                }

            }
        }
    }

    private class AITargetItems extends CreatureAITargetItems {

        public AITargetItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
            super(creature, checkSight, onlyNearby, tickThreshold, radius);
            this.executionChance = 1;
        }

        public void stop() {
            super.stop();
            ((EntitySeagull) mob).aiItemFlag = false;
        }

        public boolean canUse() {
            return super.canUse() && !((EntitySeagull) mob).isSitting() && (mob.getTarget() == null || !mob.getTarget().isAlive());
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && !((EntitySeagull) mob).isSitting() && (mob.getTarget() == null || !mob.getTarget().isAlive());
        }

        @Override
        protected void moveTo() {
            EntitySeagull crow = (EntitySeagull) mob;
            if (this.targetEntity != null) {
                crow.aiItemFlag = true;
                if (this.mob.distanceTo(targetEntity) < 2) {
                    crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), targetEntity.getY(), this.targetEntity.getZ(), 1.5F);
                    crow.peck();
                }
                if (this.mob.distanceTo(this.targetEntity) > 8 || crow.isFlying()) {
                    crow.setFlying(true);
                    float f = (float) (crow.getX() - targetEntity.getX());
                    float f1 = 1.8F;
                    float f2 = (float) (crow.getZ() - targetEntity.getZ());
                    float xzDist = Mth.sqrt(f * f + f2 * f2);

                    if (!crow.hasLineOfSight(targetEntity)) {
                        crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), 1 + crow.getY(), this.targetEntity.getZ(), 1.5F);
                    } else {
                        if (xzDist < 5) {
                            f1 = 0;
                        }
                        crow.getMoveControl().setWantedPosition(this.targetEntity.getX(), f1 + this.targetEntity.getY(), this.targetEntity.getZ(), 1.5F);
                    }
                } else {
                    this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.5F);
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
            moveTo();
        }
    }
}
