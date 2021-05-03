package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EntityTiger extends AnimalEntity implements ICustomCollisions, IAnimatedEntity, IAngerable, ITargetsDroppedItems {

    public static final Animation ANIMATION_PAW_R = Animation.create(15);
    public static final Animation ANIMATION_PAW_L = Animation.create(15);
    public static final Animation ANIMATION_TAIL_FLICK = Animation.create(45);
    public static final Animation ANIMATION_LEAP = Animation.create(20);
    private static final DataParameter<Boolean> WHITE = EntityDataManager.createKey(EntityTiger.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RUNNING = EntityDataManager.createKey(EntityTiger.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityTiger.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(EntityTiger.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> STEALTH_MODE = EntityDataManager.createKey(EntityTiger.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOLDING = EntityDataManager.createKey(EntityTiger.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ANGER_TIME = EntityDataManager.createKey(EntityTiger.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LAST_SCARED_MOB_ID = EntityDataManager.createKey(EntityTiger.class, DataSerializers.VARINT);
    private static final RangedInteger ANGRY_TIMER = TickRangeConverter.convertRange(40, 80);
    private static final Predicate<LivingEntity> NO_BLESSING_EFFECT = (mob) -> {
        return !mob.isPotionActive(AMEffectRegistry.TIGERS_BLESSING);
    };
    public float prevSitProgress;
    public float sitProgress;
    public float prevSleepProgress;
    public float sleepProgress;
    public float prevHoldProgress;
    public float holdProgress;
    public float prevStealthProgress;
    public float stealthProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean hasSpedUp = false;
    private UUID lastHurtBy;
    private int sittingTime;
    private int maxSitTime;
    private int holdTime = 0;
    private int prevScaredMobId = -1;
    private boolean dontSitFlag = false;

    protected EntityTiger(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0);
        this.moveController = new MovementControllerCustomCollisions(this);
    }
    
    public static boolean canTigerSpawn(EntityType<? extends AnimalEntity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        return worldIn.getLightSubtracted(pos, 0) > 8;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 50D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F).createMutableAttribute(Attributes.FOLLOW_RANGE, 86);
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return worldIn.getFluidState(pos.down()).isEmpty() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER) ? 0.0F : super.getBlockPathWeight(pos, worldIn);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(WHITE, Boolean.valueOf(false));
        this.dataManager.register(RUNNING, Boolean.valueOf(false));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(STEALTH_MODE, Boolean.valueOf(false));
        this.dataManager.register(HOLDING, Boolean.valueOf(false));
        this.dataManager.register(SLEEPING, Boolean.valueOf(false));
        this.dataManager.register(ANGER_TIME, 0);
        this.dataManager.register(LAST_SCARED_MOB_ID, -1);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIPanicBaby(this, 1.25D));
        this.goalSelector.addGoal(3, new AIMelee());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 60, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 25F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(this, false, 10));
        this.targetSelector.addGoal(2, (new AngerGoal(this)));
        this.targetSelector.addGoal(3, new AttackPlayerGoal());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, LivingEntity.class, 10, false, false, AMEntityRegistry.buildPredicateFromTag(EntityTypeTags.getCollection().get(AMTagRegistry.TIGER_TARGETS))) {
            public boolean shouldExecute() {
                return !EntityTiger.this.isChild() && super.shouldExecute();
            }
        });
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, true));
    }

    protected float getWaterSlowDown() {
        return 0.99F;
    }

    public boolean shouldMove() {
        return !isSitting() && !isSleeping() && !this.isHolding();
    }

    public double getVisibilityMultiplier(@Nullable Entity lookingEntity) {
        if (this.isStealth()) {
            return 0.2D;
        }
        return super.getVisibilityMultiplier(lookingEntity);
    }

    public boolean isBreedingItem(ItemStack stack) {
        Item item = stack.getItem();
        return item == AMItemRegistry.ACACIA_BLOSSOM;
    }

    //killEntity
    public void func_241847_a(ServerWorld world, LivingEntity entity) {
        this.heal(5);
        super.func_241847_a(world, entity);
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

    protected PathNavigator createNavigator(World worldIn) {
        return new Navigator(this, worldIn);
    }

    public boolean isWhite() {
        return this.dataManager.get(WHITE).booleanValue();
    }

    public void setWhite(boolean white) {
        this.dataManager.set(WHITE, Boolean.valueOf(white));
    }

    public boolean isRunning() {
        return this.dataManager.get(RUNNING).booleanValue();
    }

    public void setRunning(boolean running) {
        this.dataManager.set(RUNNING, Boolean.valueOf(running));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean bar) {
        this.dataManager.set(SITTING, Boolean.valueOf(bar));
    }

    public boolean isStealth() {
        return this.dataManager.get(STEALTH_MODE).booleanValue();
    }

    public void setStealth(boolean bar) {
        this.dataManager.set(STEALTH_MODE, Boolean.valueOf(bar));
    }

    public boolean isHolding() {
        return this.dataManager.get(HOLDING).booleanValue();
    }

    public void setHolding(boolean running) {
        this.dataManager.set(HOLDING, Boolean.valueOf(running));
    }

    public boolean isSleeping() {
        return this.dataManager.get(SLEEPING).booleanValue();
    }

    public void setSleeping(boolean sleeping) {
        this.dataManager.set(SLEEPING, Boolean.valueOf(sleeping));
    }

    public int getAngerTime() {
        return this.dataManager.get(ANGER_TIME);
    }

    public void setAngerTime(int time) {
        this.dataManager.set(ANGER_TIME, time);
    }

    public UUID getAngerTarget() {
        return this.lastHurtBy;
    }

    public void setAngerTarget(@Nullable UUID target) {
        this.lastHurtBy = target;
    }

    public void func_230258_H__() {
        this.setAngerTime(ANGRY_TIMER.getRandomWithinRange(this.rand));
    }

    protected void updateAITasks() {
        if (!this.world.isRemote) {
            this.func_241359_a_((ServerWorld) this.world, false);
        }
    }



    public void tick() {
        super.tick();
        prevSitProgress = sitProgress;
        prevSleepProgress = sleepProgress;
        prevHoldProgress = holdProgress;
        prevStealthProgress = stealthProgress;
        if (isSitting() && sitProgress < 5F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress--;
        }
        if (isSleeping() && sleepProgress < 5F) {
            sleepProgress++;
        }
        if (!isSleeping() && sleepProgress > 0F) {
            sleepProgress--;
        }
        if (isHolding() && holdProgress < 5F) {
            holdProgress++;
        }
        if (!isHolding() && holdProgress > 0F) {
            holdProgress--;
        }
        if (isStealth() && stealthProgress < 10F) {
            stealthProgress += 0.25F;
        }
        if (!isStealth() && stealthProgress > 0F) {
            stealthProgress--;
        }
        if (!world.isRemote) {
            if (isRunning() && !hasSpedUp) {
                hasSpedUp = true;
                stepHeight = 1F;
                this.setSprinting(true);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4F);
            }
            if (!isRunning() && hasSpedUp) {
                hasSpedUp = false;
                stepHeight = 0.6F;
                this.setSprinting(false);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25F);
            }
            if ((isSitting() || isSleeping()) && (++sittingTime > maxSitTime || this.getAttackTarget() != null || this.isInLove() || dontSitFlag)) {
                this.setSitting(false);
                this.setSleeping(false);
                sittingTime = 0;
                maxSitTime = 100 + rand.nextInt(50);
            }
            if (this.getAttackTarget() == null && !dontSitFlag && this.getMotion().lengthSquared() < 0.03D && this.getAnimation() == NO_ANIMATION && !this.isSleeping() && !this.isSitting() && rand.nextInt(100) == 0) {
                sittingTime = 0;
                if (this.getRNG().nextBoolean()) {
                    maxSitTime = 100 + rand.nextInt(550);
                    this.setSitting(true);
                    this.setSleeping(false);
                } else {
                    maxSitTime = 200 + rand.nextInt(550);
                    this.setSitting(false);
                    this.setSleeping(true);
                }
            }
            if (this.getMotion().lengthSquared() < 0.03D && this.getAnimation() == NO_ANIMATION && !this.isSleeping() && !this.isSitting() && rand.nextInt(100) == 0) {
                this.setAnimation(ANIMATION_TAIL_FLICK);
            }
        }
        if (this.isHolding()) {
            this.setSprinting(false);
            this.setRunning(false);
            if (!world.isRemote && this.getAttackTarget() != null && this.getAttackTarget().isAlive()) {
                this.rotationPitch = 0;
                float radius = 1.0F + this.getAttackTarget().getWidth() * 0.5F;
                float angle = (0.01745329251F * this.renderYawOffset);
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraZ = radius * MathHelper.cos(angle);
                double extraY = -0.5F;
                Vector3d minus = new Vector3d(this.getPosX() + extraX - this.getAttackTarget().getPosX(), this.getPosY() + extraY - this.getAttackTarget().getPosY(), this.getPosZ() + extraZ - this.getAttackTarget().getPosZ());
                this.getAttackTarget().setMotion(minus);
                if (holdTime % 20 == 0) {
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), 3 + this.getRNG().nextInt(2));
                }
            }
            holdTime++;
            if (holdTime > 100) {
                holdTime = 0;
                this.setHolding(false);
            }
        } else {
            holdTime = 0;
        }
        if (prevScaredMobId != this.dataManager.get(LAST_SCARED_MOB_ID) && world.isRemote) {
            Entity e = world.getEntityByID(this.dataManager.get(LAST_SCARED_MOB_ID));
            if (e != null) {
                double d2 = this.rand.nextGaussian() * 0.1D;
                double d0 = this.rand.nextGaussian() * 0.1D;
                double d1 = this.rand.nextGaussian() * 0.1D;
                this.world.addParticle(AMParticleRegistry.SHOCKED, e.getPosX(), e.getPosYEye() + e.getHeight() * 0.15F + (double) (this.rand.nextFloat() * e.getHeight() * 0.15F), e.getPosZ(), d0, d1, d2);
            }
        }
        prevScaredMobId = this.dataManager.get(LAST_SCARED_MOB_ID);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean prev = super.attackEntityFrom(source, amount);
        if (prev) {
            if (source.getTrueSource() != null) {
                if (source.getTrueSource() instanceof LivingEntity) {
                    LivingEntity hurter = (LivingEntity) source.getTrueSource();
                    if (hurter.isPotionActive(AMEffectRegistry.TIGERS_BLESSING)) {
                        hurter.removePotionEffect(AMEffectRegistry.TIGERS_BLESSING);
                    }
                }
            }
            return prev;
        }
        return prev;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public BlockPos getLightPosition() {
        BlockPos pos = new BlockPos(this.getPositionVec());
        if (!world.getBlockState(pos).isSolid()) {
            return pos.up();
        }
        return pos;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        boolean whiteOther = p_241840_2_ instanceof EntityTiger && ((EntityTiger) p_241840_2_).isWhite();
        EntityTiger baby = AMEntityRegistry.TIGER.create(p_241840_1_);
        double whiteChance = 0.1D;
        if(this.isWhite() && whiteOther){
            whiteChance = 0.8D;
        }
        if(this.isWhite() != whiteOther){
            whiteChance = 0.4D;
        }
        baby.setWhite(rand.nextDouble() < whiteChance);
        return baby;
    }

    public Vector3d getAllowedMovement(Vector3d vec) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
        VoxelShape voxelshape = this.world.getWorldBorder().getShape();
        Stream<VoxelShape> stream = VoxelShapes.compare(voxelshape, VoxelShapes.create(axisalignedbb.shrink(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
        Stream<VoxelShape> stream1 = this.world.func_230318_c_(this, axisalignedbb.expand(vec), (p_233561_0_) -> {
            return true;
        });
        ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(stream1, stream));
        Vector3d vector3d = vec.lengthSquared() == 0.0D ? vec : collideBoundingBoxHeuristicallyPassable(this, vec, axisalignedbb, this.world, iselectioncontext, reuseablestream);
        boolean flag = vec.x != vector3d.x;
        boolean flag1 = vec.y != vector3d.y;
        boolean flag2 = vec.z != vector3d.z;
        boolean flag3 = this.onGround || flag1 && vec.y < 0.0D;
        if (this.stepHeight > 0.0F && flag3 && (flag || flag2)) {
            Vector3d vector3d1 = collideBoundingBoxHeuristicallyPassable(this, new Vector3d(vec.x, this.stepHeight, vec.z), axisalignedbb, this.world, iselectioncontext, reuseablestream);
            Vector3d vector3d2 = collideBoundingBoxHeuristicallyPassable(this, new Vector3d(0.0D, this.stepHeight, 0.0D), axisalignedbb.expand(vec.x, 0.0D, vec.z), this.world, iselectioncontext, reuseablestream);
            if (vector3d2.y < (double) this.stepHeight) {
                Vector3d vector3d3 = collideBoundingBoxHeuristicallyPassable(this, new Vector3d(vec.x, 0.0D, vec.z), axisalignedbb.offset(vector3d2), this.world, iselectioncontext, reuseablestream).add(vector3d2);
                if (horizontalMag(vector3d3) > horizontalMag(vector3d1)) {
                    vector3d1 = vector3d3;
                }
            }

            if (horizontalMag(vector3d1) > horizontalMag(vector3d)) {
                return vector3d1.add(collideBoundingBoxHeuristicallyPassable(this, new Vector3d(0.0D, -vector3d1.y + vec.y, 0.0D), axisalignedbb.offset(vector3d1), this.world, iselectioncontext, reuseablestream));
            }
        }

        return vector3d;
    }

    @Override
    public boolean canPassThrough(BlockPos mutablePos, BlockState blockstate, VoxelShape voxelshape) {
        return blockstate.getBlock() == Blocks.BAMBOO || blockstate.isIn(BlockTags.LEAVES);
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
        return new Animation[]{ANIMATION_PAW_R, ANIMATION_PAW_L, ANIMATION_LEAP, ANIMATION_TAIL_FLICK};
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public void applyEntityCollision(Entity entityIn) {
        if (!this.isHolding() || entityIn != this.getAttackTarget()) {
            super.applyEntityCollision(entityIn);
        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        if (!this.isHolding() || entityIn != this.getAttackTarget()) {
            super.collideWithEntity(entityIn);
        }
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem().isFood() && stack.getItem().getFood() != null && stack.getItem().getFood().isMeat() && stack.getItem() != Items.ROTTEN_FLESH;
    }

    public double getMaxDistToItem() {
        return 3.0D;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        this.dontSitFlag = false;
        ItemStack stack = e.getItem();
        if (stack.getItem().isFood() && stack.getItem().getFood() != null && stack.getItem().getFood().isMeat() && stack.getItem() != Items.ROTTEN_FLESH) {
            this.playSound(SoundEvents.ENTITY_CAT_EAT, this.getSoundPitch(), this.getSoundVolume());
            this.heal(5);
            if (e.getThrowerId() != null && rand.nextFloat() < getChanceForEffect(stack) && world.getPlayerByUuid(e.getThrowerId()) != null) {
                PlayerEntity player = world.getPlayerByUuid(e.getThrowerId());
                player.addPotionEffect(new EffectInstance(AMEffectRegistry.TIGERS_BLESSING, 12000));
            }
        }
    }

    public void onFindTarget(ItemEntity e) {
        this.dontSitFlag = true;
        this.setSitting(false);
        this.setSleeping(false);
    }

    public double getChanceForEffect(ItemStack stack) {
        if (stack.getItem() == Items.PORKCHOP || stack.getItem() == Items.COOKED_PORKCHOP) {
            return 0.4F;
        }
        if (stack.getItem() == Items.CHICKEN || stack.getItem() == Items.COOKED_CHICKEN) {
            return 0.3F;
        }
        return 0.1F;
    }

    protected void jump() {
        if(!this.isSleeping() && !this.isSitting()){
            super.jump();
        }
    }

    static class NodeProcessor extends WalkNodeProcessor {

        private NodeProcessor() {
        }

        public static PathNodeType func_237231_a_(IBlockReader p_237231_0_, BlockPos.Mutable p_237231_1_) {
            int i = p_237231_1_.getX();
            int j = p_237231_1_.getY();
            int k = p_237231_1_.getZ();
            PathNodeType pathnodetype = getNodes(p_237231_0_, p_237231_1_);
            if (pathnodetype == PathNodeType.OPEN && j >= 1) {
                PathNodeType pathnodetype1 = getNodes(p_237231_0_, p_237231_1_.setPos(i, j - 1, k));
                pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
                if (pathnodetype1 == PathNodeType.DAMAGE_FIRE) {
                    pathnodetype = PathNodeType.DAMAGE_FIRE;
                }

                if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                    pathnodetype = PathNodeType.DAMAGE_CACTUS;
                }

                if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
                    pathnodetype = PathNodeType.DAMAGE_OTHER;
                }

                if (pathnodetype1 == PathNodeType.STICKY_HONEY) {
                    pathnodetype = PathNodeType.STICKY_HONEY;
                }
            }

            if (pathnodetype == PathNodeType.WALKABLE) {
                pathnodetype = func_237232_a_(p_237231_0_, p_237231_1_.setPos(i, j, k), pathnodetype);
            }

            return pathnodetype;
        }

        protected static PathNodeType getNodes(IBlockReader p_237238_0_, BlockPos p_237238_1_) {
            BlockState blockstate = p_237238_0_.getBlockState(p_237238_1_);
            PathNodeType type = blockstate.getAiPathNodeType(p_237238_0_, p_237238_1_);
            if (type != null) return type;
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            if (blockstate.isAir(p_237238_0_, p_237238_1_)) {
                return PathNodeType.OPEN;
            } else if (blockstate.getBlock() == Blocks.BAMBOO) {
                return PathNodeType.OPEN;
            } else {
                return func_237238_b_(p_237238_0_, p_237238_1_);
            }
        }

        public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
            return func_237231_a_(blockaccessIn, new BlockPos.Mutable(x, y, z));
        }

        protected PathNodeType func_215744_a(IBlockReader world, boolean b1, boolean b2, BlockPos pos, PathNodeType nodeType) {
            return nodeType == PathNodeType.LEAVES || world.getBlockState(pos).getBlock() == Blocks.BAMBOO ? PathNodeType.OPEN : super.func_215744_a(world, b1, b2, pos, nodeType);
        }
    }

    class Navigator extends GroundPathNavigatorWide {

        public Navigator(MobEntity mob, World world) {
            super(mob, world, 1.2F);
        }

        protected PathFinder getPathFinder(int i) {
            this.nodeProcessor = new NodeProcessor();
            return new PathFinder(this.nodeProcessor, i);
        }

        protected boolean isDirectPathBetweenPoints(Vector3d posVec31, Vector3d posVec32, int sizeX, int sizeY, int sizeZ) {
            int i = MathHelper.floor(posVec31.x);
            int j = MathHelper.floor(posVec31.z);
            double d0 = posVec32.x - posVec31.x;
            double d1 = posVec32.z - posVec31.z;
            double d2 = d0 * d0 + d1 * d1;
            if (d2 < 1.0E-8D) {
                return false;
            } else {
                double d3 = 1.0D / Math.sqrt(d2);
                d0 = d0 * d3;
                d1 = d1 * d3;
                sizeX = sizeX + 2;
                sizeZ = sizeZ + 2;
                if (!this.isSafeToStandAt(i, MathHelper.floor(posVec31.y), j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                    return false;
                } else {
                    sizeX = sizeX - 2;
                    sizeZ = sizeZ - 2;
                    double d4 = 1.0D / Math.abs(d0);
                    double d5 = 1.0D / Math.abs(d1);
                    double d6 = (double) i - posVec31.x;
                    double d7 = (double) j - posVec31.z;
                    if (d0 >= 0.0D) {
                        ++d6;
                    }

                    if (d1 >= 0.0D) {
                        ++d7;
                    }

                    d6 = d6 / d0;
                    d7 = d7 / d1;
                    int k = d0 < 0.0D ? -1 : 1;
                    int l = d1 < 0.0D ? -1 : 1;
                    int i1 = MathHelper.floor(posVec32.x);
                    int j1 = MathHelper.floor(posVec32.z);
                    int k1 = i1 - i;
                    int l1 = j1 - j;

                    while (k1 * k > 0 || l1 * l > 0) {
                        if (d6 < d7) {
                            d6 += d4;
                            i += k;
                            k1 = i1 - i;
                        } else {
                            d7 += d5;
                            j += l;
                            l1 = j1 - j;
                        }

                        if (!this.isSafeToStandAt(i, MathHelper.floor(posVec31.y), j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }

        private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
                double d0 = (double) blockpos.getX() + 0.5D - p_179692_7_.x;
                double d1 = (double) blockpos.getZ() + 0.5D - p_179692_7_.z;
                if (!(d0 * p_179692_8_ + d1 * p_179692_10_ < 0.0D) && !this.world.getBlockState(blockpos).allowsMovement(this.world, blockpos, PathType.LAND) || EntityTiger.this.canPassThrough(blockpos, world.getBlockState(blockpos), null)) {
                    return false;
                }
            }

            return true;
        }

        private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d vec31, double p_179683_8_, double p_179683_10_) {
            int i = x - sizeX / 2;
            int j = z - sizeZ / 2;
            if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
                return false;
            } else {
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                for (int k = i; k < i + sizeX; ++k) {
                    for (int l = j; l < j + sizeZ; ++l) {
                        double d0 = (double) k + 0.5D - vec31.x;
                        double d1 = (double) l + 0.5D - vec31.z;
                        if (!(d0 * p_179683_8_ + d1 * p_179683_10_ < 0.0D)) {
                            PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                            mutable.setPos(k, y - 1, l);
                            if (!this.func_230287_a_(pathnodetype) || EntityTiger.this.canPassThrough(mutable, world.getBlockState(mutable), null)) {
                                return false;
                            }

                            pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                            float f = this.entity.getPathPriority(pathnodetype);
                            if (f < 0.0F || f >= 8.0F) {
                                return false;
                            }

                            if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
        }

        protected boolean func_230287_a_(PathNodeType p_230287_1_) {
            if (p_230287_1_ == PathNodeType.WATER) {
                return false;
            } else if (p_230287_1_ == PathNodeType.LAVA) {
                return false;
            } else {
                return p_230287_1_ != PathNodeType.OPEN;
            }
        }
    }

    private class AIMelee extends Goal {
        private EntityTiger tiger;
        private int jumpAttemptCooldown = 0;

        public AIMelee() {
            tiger = EntityTiger.this;
        }

        @Override
        public boolean shouldExecute() {
            return tiger.getAttackTarget() != null && tiger.getAttackTarget().isAlive();
        }

        public void tick() {
            if (jumpAttemptCooldown > 0) {
                jumpAttemptCooldown--;
            }
            if (tiger.getAttackTarget() != null && tiger.getAttackTarget().isAlive()) {
                double dist = tiger.getDistance(tiger.getAttackTarget());
                if (tiger.getRevengeTarget() != null && tiger.getRevengeTarget().isAlive() && dist < 10) {
                    tiger.setStealth(false);
                } else {
                    if (dist > 20) {
                        tiger.setRunning(false);
                        tiger.setStealth(true);
                    }
                }
                if (dist <= 20) {
                    tiger.setStealth(false);
                    tiger.setRunning(true);
                    if (tiger.dataManager.get(LAST_SCARED_MOB_ID) != tiger.getAttackTarget().getEntityId()) {
                        tiger.dataManager.set(LAST_SCARED_MOB_ID, tiger.getAttackTarget().getEntityId());
                        tiger.getAttackTarget().addPotionEffect(new EffectInstance(AMEffectRegistry.FEAR, 100, 0, true, false));
                    }
                }
                if (dist < 12 && tiger.getAnimation() == NO_ANIMATION && tiger.isOnGround() && jumpAttemptCooldown == 0 && !tiger.isHolding()) {
                    tiger.setAnimation(ANIMATION_LEAP);
                    jumpAttemptCooldown = 70;
                }
                if ((jumpAttemptCooldown > 0 || tiger.isInWaterOrBubbleColumn()) && !tiger.isHolding() && tiger.getAnimation() == NO_ANIMATION && dist < 4 + tiger.getAttackTarget().getWidth()) {
                    tiger.setAnimation(tiger.getRNG().nextBoolean() ? ANIMATION_PAW_L : ANIMATION_PAW_R);
                }
                if (dist < 4 + tiger.getAttackTarget().getWidth() && (tiger.getAnimation() == ANIMATION_PAW_L || tiger.getAnimation() == ANIMATION_PAW_R) && tiger.getAnimationTick() == 8) {
                    tiger.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(tiger), 7 + tiger.getRNG().nextInt(5));
                }
                if (tiger.getAnimation() == ANIMATION_LEAP) {
                    tiger.getNavigator().clearPath();
                    Vector3d vec = tiger.getAttackTarget().getPositionVec().subtract(tiger.getPositionVec());
                    tiger.rotationYaw = -((float) MathHelper.atan2(vec.x, vec.z)) * (180F / (float) Math.PI);
                    tiger.renderYawOffset = tiger.rotationYaw;

                    if (tiger.getAnimationTick() == 5 && tiger.onGround) {
                        Vector3d vector3d1 = new Vector3d(this.tiger.getAttackTarget().getPosX() - this.tiger.getPosX(), 0.0D, this.tiger.getAttackTarget().getPosZ() - this.tiger.getPosZ());
                        if (vector3d1.lengthSquared() > 1.0E-7D) {
                            vector3d1 = vector3d1.normalize().scale(Math.min(dist, 15) * 0.2F);
                        }
                        this.tiger.setMotion(vector3d1.x, vector3d1.y + 0.3F + 0.1F * MathHelper.clamp(this.tiger.getAttackTarget().getPosYEye() - this.tiger.getPosY(), 0, 2), vector3d1.z);
                    }
                    if (dist < tiger.getAttackTarget().getWidth() + 3 && tiger.getAnimationTick() >= 15) {
                        tiger.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(tiger), 2);
                        tiger.setRunning(false);
                        tiger.setStealth(false);
                        tiger.setHolding(true);
                    }
                } else {
                    if (tiger.isHolding()) {
                        tiger.getNavigator().clearPath();
                    } else {
                        tiger.getNavigator().tryMoveToEntityLiving(tiger.getAttackTarget(), tiger.isStealth() ? 0.75F : 1.0F);
                    }
                }
            }
        }

        public void resetTask() {
            tiger.setStealth(false);
            tiger.setRunning(false);
            tiger.setHolding(false);
        }
    }

    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {

        public AttackPlayerGoal() {
            super(EntityTiger.this, PlayerEntity.class, 100, false, true, NO_BLESSING_EFFECT);
        }

        public boolean shouldExecute() {
            if (EntityTiger.this.isChild()) {
                return false;
            } else {
                return super.shouldExecute();
            }
        }

        protected double getTargetDistance() {
            return 4.0D;
        }
    }

    class AngerGoal extends HurtByTargetGoal {
        AngerGoal(EntityTiger beeIn) {
            super(beeIn);
        }

        public boolean shouldContinueExecuting() {
            return EntityTiger.this.func_233678_J__() && super.shouldContinueExecuting();
        }

        public void startExecuting() {
            super.startExecuting();
            if (EntityTiger.this.isChild()) {
                this.alertOthers();
                this.resetTask();
            }

        }

        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
            if (!mobIn.isChild()) {
                super.setAttackTarget(mobIn, targetIn);
            }
        }
    }
}
