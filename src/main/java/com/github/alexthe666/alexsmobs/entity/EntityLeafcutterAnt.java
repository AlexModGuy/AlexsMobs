package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityLeafcutterAnt extends AnimalEntity implements IAngerable, IAnimatedEntity {

    public static final Animation ANIMATION_BITE = Animation.create(13);
    protected static final EntitySize QUEEN_SIZE = EntitySize.fixed(1.25F, 0.98F);
    public static final ResourceLocation QUEEN_LOOT = new ResourceLocation("alexsmobs", "entities/leafcutter_ant_queen");
    private static final DataParameter<Optional<BlockPos>> LEAF_HARVESTED_POS = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final DataParameter<Optional<BlockState>> LEAF_HARVESTED_STATE = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    private static final DataParameter<Boolean> HAS_LEAF = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ANT_SCALE = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.FLOAT);
    private static final DataParameter<Direction> ATTACHED_FACE = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.DIRECTION);
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> QUEEN = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ANGER_TIME = EntityDataManager.createKey(EntityLeafcutterAnt.class, DataSerializers.VARINT);
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final RangedInteger ANGRY_TIMER = TickRangeConverter.convertRange(10, 20);
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
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(AMItemRegistry.GONGYLIDIA);
    private int haveBabyCooldown = 0;
    public EntityLeafcutterAnt(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        switchNavigator(true);

    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if(entitylivingbaseIn instanceof PlayerEntity && ((PlayerEntity) entitylivingbaseIn).isCreative()){
            return;
        }
        super.setAttackTarget(entitylivingbaseIn);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return this.isQueen() ? QUEEN_LOOT : super.getLootTable();
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    private void switchNavigator(boolean rightsideUp) {
        if (rightsideUp) {
            this.moveController = new MovementController(this);
            this.navigator = new ClimberPathNavigator(this, world);
            this.isUpsideDownNavigator = false;
        } else {
            this.moveController = new FlightMoveController(this, 0.6F, false);
            this.navigator = new DirectPathNavigator(this, world);
            this.isUpsideDownNavigator = true;
        }
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 6.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2F);
    }

    private static boolean isSideSolid(IBlockReader reader, BlockPos pos, Entity entityIn, Direction direction) {
        return Block.doesSideFillSquare(reader.getBlockState(pos).getCollisionShape(reader, pos, ISelectionContext.forEntity(entityIn)), direction);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new ReturnToHiveGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new TameableAITempt(this, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new LeafcutterAntAIFollowCaravan(this, 1D));
        this.goalSelector.addGoal(5, new LeafcutterAntAIForageLeaves(this));
        this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 30, 1.0D, 25, 7));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, (new AngerGoal(this)).setCallsForHelp(new Class[0]));
        this.targetSelector.addGoal(2, new ResetAngerGoal<>(this, true));
    }

    public EntitySize getSize(Pose poseIn) {
        return isQueen() && !isChild() ? QUEEN_SIZE : super.getSize(poseIn);
    }

    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public Direction getAttachmentFacing() {
        return this.dataManager.get(ATTACHED_FACE);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new ClimberPathNavigator(this, worldIn);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return isQueen() ? AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT : AMSoundRegistry.LEAFCUTTER_ANT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return isQueen() ? AMSoundRegistry.LEAFCUTTER_ANT_QUEEN_HURT : AMSoundRegistry.LEAFCUTTER_ANT_HURT;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {

    }

    private void pacifyAllNearby(){
        func_241356_K__();
        List<EntityLeafcutterAnt> list = world.getEntitiesWithinAABB(EntityLeafcutterAnt.class, this.getBoundingBox().grow(20D, 6.0D, 20D));
        for(EntityLeafcutterAnt ant : list){
            ant.func_241356_K__();
        }
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if(type != ActionResultType.SUCCESS && item == AMItemRegistry.GONGYLIDIA){
            if(isQueen() && haveBabyCooldown == 0){
                int babies = 1 + rand.nextInt(1);
                pacifyAllNearby();
                for(int i = 0; i < babies; i++){
                    EntityLeafcutterAnt leafcutterAnt = AMEntityRegistry.LEAFCUTTER_ANT.create(world);
                    leafcutterAnt.copyLocationAndAnglesFrom(this);
                    leafcutterAnt.setGrowingAge(-24000);
                    if(!world.isRemote){
                        world.setEntityState(this, (byte)18);
                        world.addEntity(leafcutterAnt);
                    }
                }
                if(!player.isCreative()){
                    itemstack.shrink(1);
                }
                haveBabyCooldown = 24000;
                this.setChild(false);
            }else{
                pacifyAllNearby();
                if(!player.isCreative()){
                    itemstack.shrink(1);
                }
                world.setEntityState(this, (byte)48);
                this.heal(3);
            }

            return ActionResultType.SUCCESS;

        }
        return type;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 48) {
            for(int i = 0; i < 3; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
            }
        } else {
            super.handleStatusUpdate(id);
        }

    }

    public void tick() {
        this.prevAttachChangeProgress = this.attachChangeProgress;
        super.tick();
        if (this.isQueen() && this.getWidth() < QUEEN_SIZE.width) {
            this.recalculateSize();
        }
        if (attachChangeProgress > 0F) {
            attachChangeProgress -= 0.25F;
        }
        this.stepHeight = isQueen() ? 1F : 0.5F;
        Vector3d vector3d = this.getMotion();
        if (!this.world.isRemote && !this.isQueen()) {
            this.setBesideClimbableBlock(this.collidedHorizontally || this.collidedVertically && !this.isOnGround());
            if (this.isOnGround() || this.isInWaterOrBubbleColumn() || this.isInLava()) {
                this.dataManager.set(ATTACHED_FACE, Direction.DOWN);
            } else  if (this.collidedVertically) {
                this.dataManager.set(ATTACHED_FACE, Direction.UP);
            }else {
                boolean flag = false;
                Direction closestDirection = Direction.DOWN;
                double closestDistance = 100;
                for (Direction dir : HORIZONTALS) {
                    BlockPos antPos = new BlockPos(MathHelper.floor(this.getPosX()), MathHelper.floor(this.getPosY()), MathHelper.floor(this.getPosZ()));
                    BlockPos offsetPos = antPos.offset(dir);
                    Vector3d offset = Vector3d.copyCentered(offsetPos);
                    if (closestDistance > this.getPositionVec().distanceTo(offset) && world.isDirectionSolid(offsetPos, this, dir.getOpposite())) {
                        closestDistance = this.getPositionVec().distanceTo(offset);
                        closestDirection = dir;
                    }
                }
                this.dataManager.set(ATTACHED_FACE, closestDirection);
            }
        }
        boolean flag = false;
        if (this.getAttachmentFacing() != Direction.DOWN) {
            if(this.getAttachmentFacing() == Direction.UP){
                this.setMotion(this.getMotion().add(0, 1, 0));
            }else{
                if (!this.collidedHorizontally && this.getAttachmentFacing() != Direction.UP) {
                    Vector3d vec = Vector3d.copy(this.getAttachmentFacing().getDirectionVec());
                    this.setMotion(this.getMotion().add(vec.normalize().mul(0.1F, 0.1F, 0.1F)));
                }
                if (!this.onGround && vector3d.y < 0.0D) {
                    this.setMotion(this.getMotion().mul(1.0D, 0.5D, 1.0D));
                    flag = true;
                }
            }
        }
        if(this.getAttachmentFacing() == Direction.UP) {
            this.setNoGravity(true);
            this.setMotion(vector3d.mul(0.7D, 1D, 0.7D));
        }else{
            this.setNoGravity(false);
        }
        if (!flag) {
            if (this.isOnLadder()) {
                this.setMotion(vector3d.mul(1.0D, 0.4D, 1.0D));
            }
        }
        if (prevAttachDir != this.getAttachmentFacing()) {
            attachChangeProgress = 1F;
        }
        this.prevAttachDir = this.getAttachmentFacing();
        if (!this.world.isRemote) {
            if (this.getAttachmentFacing() == Direction.UP && !this.isUpsideDownNavigator) {
                switchNavigator(false);
            }
            if (this.getAttachmentFacing() != Direction.UP && this.isUpsideDownNavigator) {
                switchNavigator(true);
            }
            if (this.stayOutOfHiveCountdown > 0) {
                --this.stayOutOfHiveCountdown;
            }

            if (this.ticksExisted % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = null;
            }
            LivingEntity attackTarget = this.getAttackTarget();
            if (attackTarget != null && getDistance(attackTarget) < attackTarget.getWidth() + this.getWidth() + 1 && this.canEntityBeSeen(attackTarget)) {
                if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 6) {
                    float damage = (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    attackTarget.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
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
            TileEntity tileentity = this.world.getTileEntity(this.hivePos);
            return tileentity instanceof TileEntityLeafcutterAnthill;
        }
    }

    protected void onInsideBlock(BlockState state) {

    }

    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataManager.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.dataManager.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.dataManager.set(CLIMBING, b0);
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
            this.func_241359_a_((ServerWorld)this.world, false);
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CLIMBING, (byte) 0);
        this.dataManager.register(LEAF_HARVESTED_POS, Optional.empty());
        this.dataManager.register(LEAF_HARVESTED_STATE, Optional.empty());
        this.dataManager.register(HAS_LEAF, false);
        this.dataManager.register(QUEEN, false);
        this.dataManager.register(ATTACHED_FACE, Direction.DOWN);
        this.dataManager.register(ANT_SCALE, 1.0F);
        this.dataManager.register(ANGER_TIME, 0);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setAntScale(0.75F + rand.nextFloat() * 0.3F);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public float getAntScale() {
        return this.dataManager.get(ANT_SCALE);
    }

    public void setAntScale(float scale) {
        this.dataManager.set(ANT_SCALE, scale);
    }


    public BlockPos getHarvestedPos() {
        return this.dataManager.get(LEAF_HARVESTED_POS).orElse(null);
    }

    public void setLeafHarvestedPos(BlockPos harvestedPos) {
        this.dataManager.set(LEAF_HARVESTED_POS, Optional.ofNullable(harvestedPos));
    }

    public BlockState getHarvestedState() {
        return this.dataManager.get(LEAF_HARVESTED_STATE).orElse(null);
    }

    public void setLeafHarvestedState(BlockState state) {
        this.dataManager.set(LEAF_HARVESTED_STATE, Optional.ofNullable(state));
    }

    public boolean hasLeaf() {
        return this.dataManager.get(HAS_LEAF).booleanValue();
    }

    public void setLeaf(boolean leaf) {
        this.dataManager.set(HAS_LEAF, Boolean.valueOf(leaf));
    }

    public boolean isQueen() {
        return this.dataManager.get(QUEEN).booleanValue();
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
        this.dataManager.set(QUEEN, Boolean.valueOf(queen));
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(ATTACHED_FACE, Direction.byIndex(compound.getByte("AttachFace")));
        this.setLeaf(compound.getBoolean("Leaf"));
        this.setQueen(compound.getBoolean("Queen"));
        this.setAntScale(compound.getFloat("AntScale"));
        BlockState blockstate = null;
        if (compound.contains("HarvestedLeafState", 10)) {
            blockstate = NBTUtil.readBlockState(compound.getCompound("HarvestedLeafState"));
            if (blockstate.isAir()) {
                blockstate = null;
            }
        }
        this.stayOutOfHiveCountdown = compound.getInt("CannotEnterHiveTicks");
        this.haveBabyCooldown = compound.getInt("BabyCooldown");
        this.hivePos = null;
        if (compound.contains("HivePos")) {
            this.hivePos = NBTUtil.readBlockPos(compound.getCompound("HivePos"));
        }
        this.setLeafHarvestedState(blockstate);
        if (compound.contains("HLPX")) {
            int i = compound.getInt("HLPX");
            int j = compound.getInt("HLPY");
            int k = compound.getInt("HLPZ");
            this.dataManager.set(LEAF_HARVESTED_POS, Optional.of(new BlockPos(i, j, k)));
        } else {
            this.dataManager.set(LEAF_HARVESTED_POS, Optional.empty());
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte("AttachFace", (byte) this.dataManager.get(ATTACHED_FACE).getIndex());
        compound.putBoolean("Leaf", this.hasLeaf());
        compound.putBoolean("Queen", this.isQueen());
        compound.putFloat("AntScale", this.getAntScale());
        BlockState blockstate = this.getHarvestedState();
        if (blockstate != null) {
            compound.put("HarvestedLeafState", NBTUtil.writeBlockState(blockstate));
        }
        if (this.hasHive()) {
            compound.put("HivePos", NBTUtil.writeBlockPos(this.getHivePos()));
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
            TileEntity tileentity = this.world.getTileEntity(this.hivePos);
            return tileentity instanceof TileEntityLeafcutterAnthill && ((TileEntityLeafcutterAnthill) tileentity).isNearFire();
        }
    }

    private boolean doesHiveHaveSpace(BlockPos pos) {
        TileEntity tileentity = this.world.getTileEntity(pos);
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
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return null;
    }

    public boolean shouldLeadCaravan() {
        return !this.hasLeaf();
    }

    @Override
    public void func_233629_a_(LivingEntity p_233629_1_, boolean p_233629_2_) {
        p_233629_1_.prevLimbSwingAmount = p_233629_1_.limbSwingAmount;
        double d0 = p_233629_1_.getPosX() - p_233629_1_.prevPosX;
        double d1 = (p_233629_1_.getPosY() - p_233629_1_.prevPosY) * 2.0F;
        double d2 = p_233629_1_.getPosZ() - p_233629_1_.prevPosZ;
        float f = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 4.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        p_233629_1_.limbSwingAmount += (f - p_233629_1_.limbSwingAmount) * 0.4F;
        p_233629_1_.limbSwing += p_233629_1_.limbSwingAmount;
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

    public boolean attackEntityAsMob(Entity entityIn) {
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
        public boolean shouldExecute() {
            if(EntityLeafcutterAnt.this.stayOutOfHiveCountdown > 0){
                return false;
            }
            if (EntityLeafcutterAnt.this.hasLeaf() || EntityLeafcutterAnt.this.isQueen()) {
                searchCooldown--;
                BlockPos hive = EntityLeafcutterAnt.this.hivePos;
                if (hive != null && EntityLeafcutterAnt.this.world.getTileEntity(hive) instanceof TileEntityLeafcutterAnthill) {
                    hivePos = hive;
                    return true;
                }
                if (searchCooldown <= 0) {
                    searchCooldown = 400;
                    PointOfInterestManager pointofinterestmanager = ((ServerWorld) world).getPointOfInterestManager();
                    Stream<BlockPos> stream = pointofinterestmanager.findAll(AMPointOfInterestRegistry.LEAFCUTTER_ANT_HILL.getPredicate(), Predicates.alwaysTrue(), EntityLeafcutterAnt.this.getPosition(), 100, PointOfInterestManager.Status.ANY);
                    List<BlockPos> listOfHives = stream.collect(Collectors.toList());
                    BlockPos ret = null;
                    for (BlockPos pos : listOfHives) {
                        if (ret == null || pos.distanceSq(EntityLeafcutterAnt.this.getPosition()) < ret.distanceSq(EntityLeafcutterAnt.this.getPosition())) {
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

        public boolean shouldContinueExecuting() {
            return hivePos != null && EntityLeafcutterAnt.this.getDistanceSq(Vector3d.copyCenteredWithVerticalOffset(hivePos, 1)) > 1F;
        }

        public void resetTask() {
            this.hivePos = null;
            this.searchCooldown = 20;
            this.approachTime = 0;
        }

        public void tick() {
            double dist = EntityLeafcutterAnt.this.getDistanceSq(Vector3d.copyCenteredWithVerticalOffset(hivePos, 1));
            if (dist < 1.2F && EntityLeafcutterAnt.this.getPositionUnderneath().equals(hivePos)) {
                TileEntity tileentity = EntityLeafcutterAnt.this.world.getTileEntity(hivePos);
                if (tileentity instanceof TileEntityLeafcutterAnthill) {
                    TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill) tileentity;
                    beehivetileentity.tryEnterHive(EntityLeafcutterAnt.this, EntityLeafcutterAnt.this.hasLeaf());
                }
            }
            if (dist < 16) {
                approachTime++;
                if(dist < (approachTime < 200 ? 2 : 10) && EntityLeafcutterAnt.this.getPosY() >= hivePos.getY()){
                    if(EntityLeafcutterAnt.this.getAttachmentFacing() != Direction.DOWN){
                        EntityLeafcutterAnt.this.setMotion(EntityLeafcutterAnt.this.getMotion().add(0, 0.1, 0));
                    }
                   EntityLeafcutterAnt.this.getMoveHelper().setMoveTo((double) hivePos.getX() + 0.5F, (double) hivePos.getY() + 1.5F, (double) hivePos.getZ() + 0.5F, 1.0D);
                }
                EntityLeafcutterAnt.this.navigator.resetRangeMultiplier();
                EntityLeafcutterAnt.this.navigator.tryMoveToXYZ((double) hivePos.getX() + 0.5F, (double) hivePos.getY() + 1.6F, (double) hivePos.getZ() + 0.5F, 1.0D);
            } else {
                startMovingToFar(this.hivePos);
            }
        }

        private boolean startMovingToFar(BlockPos pos) {
            EntityLeafcutterAnt.this.navigator.setRangeMultiplier(10.0F);
            EntityLeafcutterAnt.this.navigator.tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
            return EntityLeafcutterAnt.this.navigator.getPath() != null && EntityLeafcutterAnt.this.navigator.getPath().reachesTarget();
        }

    }

    class AngerGoal extends HurtByTargetGoal {
        AngerGoal(EntityLeafcutterAnt beeIn) {
            super(beeIn);
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EntityLeafcutterAnt.this.func_233678_J__() && super.shouldContinueExecuting();
        }

        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityLeafcutterAnt && this.goalOwner.canEntityBeSeen(targetIn)) {
                mobIn.setAttackTarget(targetIn);
            }

        }
    }
}
