package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.message.MessageCrowDismount;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

public class EntityCrow extends TameableEntity implements ITargetsDroppedItems {

    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityCrow.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.createKey(EntityCrow.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityCrow.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityCrow.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<BlockPos>> PERCH_POS = EntityDataManager.createKey(EntityCrow.class, DataSerializers.OPTIONAL_BLOCK_POS);
    public float prevFlyProgress;
    public float flyProgress;
    public float prevAttackProgress;
    public float attackProgress;
    public int fleePumpkinFlag = 0;
    public boolean aiItemFlag = false;
    public boolean aiItemFrameFlag = false;
    public float prevSitProgress;
    public float sitProgress;
    private boolean isLandNavigator;
    private int timeFlying = 0;
    @Nullable
    private UUID seedThrowerID;
    private int heldItemTime = 0;
    private int checkPerchCooldown = 0;
    private boolean gatheringClockwise = false;

    protected EntityCrow(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathPriority(PathNodeType.COCOA, -1.0F);
        this.setPathPriority(PathNodeType.FENCE, -1.0F);
        switchNavigator(false);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new CrowAIMelee(this));
        this.goalSelector.addGoal(3, new CrowAIFollowOwner(this, 1.0D, 4.0F, 2.0F, true));
        this.goalSelector.addGoal(4, new AIDepositChests());
        this.goalSelector.addGoal(4, new AIScatter());
        this.goalSelector.addGoal(5, new AIAvoidPumpkins());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new CrowAICircleCrops(this));
        this.goalSelector.addGoal(7, new AIWalkIdle());
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(9, new LookAtGoal(this, CreatureEntity.class, 6.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new AITargetItems(this, false, false, 40, 16));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, (new HurtByTargetGoal(this, PlayerEntity.class)).setCallsForHelp());

    }


    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.crowSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public static <T extends MobEntity> boolean canCrowSpawn(EntityType<EntityCrow> crow, IWorld worldIn, SpawnReason reason, BlockPos p_223317_3_, Random random) {
        BlockState blockstate = worldIn.getBlockState(p_223317_3_.down());
        return (blockstate.isIn(BlockTags.LEAVES) || blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LOGS) || blockstate.isIn(Blocks.AIR)) && worldIn.getLightSubtracted(p_223317_3_, 0) > 8;
    }

    public boolean isOnSameTeam(Entity entityIn) {
        if (this.isTamed()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TameableEntity) {
                return ((TameableEntity) entityIn).isOwner(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isOnSameTeam(entityIn);
            }
        }

        return super.isOnSameTeam(entityIn);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigator(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new FlightMoveController(this, 0.7F, false);
            this.navigator = new DirectPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            this.func_233687_w_(false);
            if (entity != null && this.isTamed() && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                amount = (amount + 1.0F) / 4.0F;
            }
            boolean prev = super.attackEntityFrom(source, amount);
            if (prev) {
                if (!this.getHeldItemMainhand().isEmpty()) {
                    this.entityDropItem(this.getHeldItemMainhand().copy());
                    this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
            return prev;
        }
    }

    public void updateRidden() {
        Entity entity = this.getRidingEntity();
        if (this.isPassenger() && !entity.isAlive()) {
            this.stopRiding();
        } else if (isTamed() && entity instanceof LivingEntity && isOwner((LivingEntity) entity)) {
            this.setMotion(0, 0, 0);
            this.tick();
            Entity riding = this.getRidingEntity();
            if (this.isPassenger()) {
                int i = riding.getPassengers().indexOf(this);
                float radius = 0.43F;
                float angle = (0.01745329251F * (((PlayerEntity) riding).renderYawOffset + (i == 0 ? -90 : 90)));
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraZ = radius * MathHelper.cos(angle);
                double extraY = (riding.isSneaking() ? 1.25D : 1.45D);
                this.rotationYawHead = ((PlayerEntity) riding).rotationYawHead;
                this.prevRotationYaw = ((PlayerEntity) riding).rotationYawHead;
                this.setPosition(riding.getPosX() + extraX, riding.getPosY() + extraY, riding.getPosZ() + extraZ);
                if (!riding.isAlive() || rideCooldown == 0 && riding.isSneaking() || ((PlayerEntity) riding).isElytraFlying() || this.getAttackTarget() != null && this.getAttackTarget().isAlive()) {
                    this.dismount();
                    if (!world.isRemote) {
                        AlexsMobs.sendMSGToAll(new MessageCrowDismount(this.getEntityId(), riding.getEntityId()));
                    }
                }
            }
        }
    }


    public int getRidingCrows(LivingEntity player) {
        int crowCount = 0;
        for (Entity e : player.getPassengers()) {
            if (e instanceof EntityCrow) {
                crowCount++;
            }
        }
        return crowCount;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.PUMPKIN_SEEDS && this.isTamed();
    }

    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if (!this.getHeldItemMainhand().isEmpty() && type != ActionResultType.SUCCESS) {
            this.entityDropItem(this.getHeldItemMainhand().copy());
            this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            return ActionResultType.SUCCESS;
        } else {
            if (type == ActionResultType.PASS && isTamed() && isOwner(player) && !isBreedingItem(itemstack)) {
                if (isCrowEdible(itemstack) && this.getHeldItemMainhand().isEmpty()) {
                    ItemStack cop = itemstack.copy();
                    cop.setCount(1);
                    this.setHeldItem(Hand.MAIN_HAND, cop);
                    itemstack.shrink(1);
                }
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() == 4) {
                    this.setCommand(0);
                }
                if(this.getCommand() == 3){
                    player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.crow.command_3", this.getName()), true);
                }else{
                    player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                }
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.setSitting(true);
                    return ActionResultType.SUCCESS;
                } else {
                    this.setSitting(false);
                    return ActionResultType.SUCCESS;
                }
            }
            return super.func_230254_b_(player, hand);
        }
    }


    public void tick() {
        super.tick();
        this.prevAttackProgress = attackProgress;
        prevFlyProgress = flyProgress;
        this.prevSitProgress = this.sitProgress;
        if ((this.isSitting() || this.isPassenger()) && sitProgress < 5) {
            sitProgress += 1;
        }
        if (!(this.isSitting() || this.isPassenger()) && sitProgress > 0) {
            sitProgress -= 1;
        }
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (fleePumpkinFlag > 0) {
            fleePumpkinFlag--;
        }
        if (!world.isRemote) {
            if (isFlying() && this.isLandNavigator) {
                switchNavigator(false);
            }
            if (!isFlying() && !this.isLandNavigator) {
                switchNavigator(true);
            }
            if (isFlying()) {
                timeFlying++;
                this.setNoGravity(true);
                if (this.isSitting() || this.isPassenger() || this.isInLove()) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
                this.setNoGravity(false);
            }
        }
        if (!this.getHeldItemMainhand().isEmpty()) {
            heldItemTime++;
            if (heldItemTime > 60 && isCrowEdible(this.getHeldItemMainhand()) && (!this.isTamed() || this.getHealth() < this.getMaxHealth())) {
                heldItemTime = 0;
                this.heal(4);
                this.playSound(SoundEvents.ENTITY_PARROT_EAT, this.getSoundVolume(), this.getSoundPitch());
                if (this.getHeldItemMainhand().getItem() == Items.PUMPKIN_SEEDS && seedThrowerID != null && !this.isTamed()) {
                    if (getRNG().nextFloat() < 0.3F) {
                        this.setTamed(true);
                        this.setCommand(1);
                        this.setOwnerId(this.seedThrowerID);
                        this.world.setEntityState(this, (byte) 7);
                    } else {
                        this.world.setEntityState(this, (byte) 6);
                    }
                }
                if (this.getHeldItemMainhand().hasContainerItem()) {
                    this.entityDropItem(this.getHeldItemMainhand().getContainerItem());
                }
                this.getHeldItemMainhand().shrink(1);
            }
        } else {
            heldItemTime = 0;
        }
        if (rideCooldown > 0) {
            rideCooldown--;
        }
        if (this.dataManager.get(ATTACK_TICK) > 0) {
            this.dataManager.set(ATTACK_TICK, this.dataManager.get(ATTACK_TICK) - 1);
            if (attackProgress < 5F) {
                attackProgress++;
            }
        } else {
            if (attackProgress > 0F) {
                attackProgress--;
            }
        }
        if(checkPerchCooldown > 0){
            checkPerchCooldown--;
        }

        if(this.isTamed() && checkPerchCooldown == 0){
            checkPerchCooldown = 50;
            BlockState below = this.getStateBelow();
            if(below.getBlock() == Blocks.HAY_BLOCK){
                this.heal(1);
                this.world.setEntityState(this, (byte) 67);
                this.setPerchPos(this.getPositionUnderneath());
            }
        }
        if(this.getCommand() == 3 && isTamed() && getPerchPos() != null && checkPerchCooldown == 0){
            checkPerchCooldown = 120;
            BlockState below = this.world.getBlockState(getPerchPos());
            if(below.getBlock() != Blocks.HAY_BLOCK){
                this.world.setEntityState(this, (byte) 68);
                this.setPerchPos(null);
                this.setCommand(2);
                this.setSitting(true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 67) {
            for(int i = 0; i < 7; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
            }
        } else if (id == 68) {
            for(int i = 0; i < 7; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.ANGRY_VILLAGER, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("MonkeySitting", this.isSitting());
        compound.putInt("Command", this.getCommand());
        if (this.getPerchPos() != null) {
            compound.putInt("PerchX", this.getPerchPos().getX());
            compound.putInt("PerchY", this.getPerchPos().getY());
            compound.putInt("PerchZ", this.getPerchPos().getZ());
        }
    }

    public void travel(Vector3d vec3d) {
        if (this.isSitting()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            vec3d = Vector3d.ZERO;
        }
        super.travel(vec3d);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setSitting(compound.getBoolean("MonkeySitting"));
        this.setCommand(compound.getInt("Command"));
        if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        if(flying && isChild()){
            return;
        }
        this.dataManager.set(FLYING, flying);
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, false);
        this.dataManager.register(ATTACK_TICK, 0);
        this.dataManager.register(COMMAND, Integer.valueOf(0));
        this.dataManager.register(SITTING, Boolean.valueOf(false));
        this.dataManager.register(PERCH_POS, Optional.empty());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.CACTUS || super.isInvulnerableTo(source);
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return AMEntityRegistry.CROW.create(serverWorld);
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());

        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    public int getTalkInterval() {
        return 60;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.CROW_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CROW_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CROW_HURT;
    }

    public Vector3d getBlockInViewAway(Vector3d fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24) - radiusAdd;
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = getCrowGround(radialPos);
        int distFromGround = (int) this.getPosY() - ground.getY();
        int flightHeight = 4 + this.getRNG().nextInt(10);
        BlockPos newPos = ground.up(distFromGround > 8 ? flightHeight : (int)this.getRNG().nextInt(6) + 1);
        if (!this.isTargetBlocked(Vector3d.copyCentered(newPos)) && this.getDistanceSq(Vector3d.copyCentered(newPos)) > 1) {
            return Vector3d.copyCentered(newPos);
        }
        return null;
    }


    private BlockPos getCrowGround(BlockPos in){
        BlockPos position = new BlockPos(in.getX(), this.getPosY(), in.getZ());
        while (position.getY() > 2 && world.isAirBlock(position)) {
            position = position.down();
        }
        return position;
    }
    public Vector3d getBlockGrounding(Vector3d fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24);
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, getPosY(), fleePos.getZ() + extraZ);
        BlockPos ground = this.getCrowGround(radialPos);
        if (ground.getY() == 0) {
            return this.getPositionVec();
        } else {
            ground = this.getPosition();
            while (ground.getY() > 2 && world.isAirBlock(ground)) {
                ground = ground.down();
            }
        }
        if (!this.isTargetBlocked(Vector3d.copyCentered(ground.up()))) {
            return Vector3d.copyCentered(ground);
        }
        return null;
    }

    private boolean isOverWater() {
        BlockPos position = this.getPosition();
        while (position.getY() > 2 && world.isAirBlock(position)) {
            position = position.down();
        }
        return !world.getFluidState(position).isEmpty();
    }

    public void peck() {
        this.dataManager.set(ATTACK_TICK, 7);
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return isCrowEdible(stack) || this.isTamed();
    }

    private boolean isCrowEdible(ItemStack stack) {
        return stack.getItem().isFood() || ItemTags.getCollection().get(AMTagRegistry.CROW_FOODSTUFFS).contains(stack.getItem());
    }

    public double getMaxDistToItem() {
        return 1.0D;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.world.isRemote) {
            this.entityDropItem(this.getHeldItem(Hand.MAIN_HAND), 0.0F);
        }
        this.setHeldItem(Hand.MAIN_HAND, duplicate);
        if (e.getItem().getItem() == Items.PUMPKIN_SEEDS && !this.isTamed()) {
            seedThrowerID = e.getThrowerId();
        } else {
            seedThrowerID = null;
        }
    }

    public BlockPos getPerchPos() {
        return this.dataManager.get(PERCH_POS).orElse(null);
    }

    public void setPerchPos(BlockPos pos) {
        this.dataManager.set(PERCH_POS, Optional.ofNullable(pos));
    }


    private class AIWalkIdle extends Goal {
        protected final EntityCrow crow;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWalkIdle() {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
            this.crow = EntityCrow.this;
        }

        @Override
        public boolean shouldExecute() {
            if (this.crow.isBeingRidden() || EntityCrow.this.getCommand() == 1 || EntityCrow.this.aiItemFlag || (crow.getAttackTarget() != null && crow.getAttackTarget().isAlive()) || this.crow.isPassenger() || this.crow.isSitting()) {
                return false;
            } else {
                if (this.crow.getRNG().nextInt(30) != 0 && !crow.isFlying()) {
                    return false;
                }
                if (this.crow.isOnGround()) {
                    this.flightTarget = rand.nextBoolean();
                } else {
                    this.flightTarget = rand.nextInt(5) > 0 && crow.timeFlying < 200;
                }
                if(crow.getCommand() == 3){
                    if(crow.aiItemFrameFlag){
                        return false;
                    }
                    this.flightTarget = true;
                }
                Vector3d lvt_1_1_ = this.getPosition();
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
            if (flightTarget) {
                crow.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                this.crow.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && crow.onGround) {
                crow.setFlying(false);
            }
            if (isFlying() && crow.onGround && crow.timeFlying > 10) {
                crow.setFlying(false);
            }
        }

        @Nullable
        protected Vector3d getPosition() {
            Vector3d vector3d = crow.getPositionVec();
            if (crow.getCommand() == 3 && crow.getPerchPos() != null) {
                return crow.getGatheringVec(vector3d, 4 + rand.nextInt(2));
            }
            if(crow.isOverWater()){
                flightTarget = true;
            }
            if (flightTarget) {
                if (crow.timeFlying < 50 || crow.isOverWater()) {
                    return crow.getBlockInViewAway(vector3d, 0);
                } else {
                    return crow.getBlockGrounding(vector3d);
                }
            } else {

                return RandomPositionGenerator.findRandomTarget(this.crow, 10, 7);
            }
        }

        public boolean shouldContinueExecuting() {
            if (crow.aiItemFlag || crow.isSitting() || EntityCrow.this.getCommand() == 1) {
                return false;
            }
            if (flightTarget) {
                return crow.isFlying() && crow.getDistanceSq(x, y, z) > 2F;
            } else {
                return (!this.crow.getNavigator().noPath()) && !this.crow.isBeingRidden();
            }
        }

        public void startExecuting() {
            if (flightTarget) {
                crow.setFlying(true);
                crow.getMoveHelper().setMoveTo(x, y, z, 1F);
            } else {
                this.crow.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, 1F);
            }
        }

        public void resetTask() {
            this.crow.getNavigator().clearPath();
            super.resetTask();
        }
    }

    private Vector3d getGatheringVec(Vector3d vector3d, float gatheringCircleDist) {
        float angle = (0.01745329251F * 8 * (gatheringClockwise ? -ticksExisted : ticksExisted));
        double extraX = gatheringCircleDist * MathHelper.sin((angle));
        double extraZ = gatheringCircleDist * MathHelper.cos(angle);
        if(this.getPerchPos() != null){
            Vector3d pos = new Vector3d(getPerchPos().getX() + extraX, getPerchPos().getY() + 2, getPerchPos().getZ() + extraZ);
            if (this.world.isAirBlock(new BlockPos(pos))) {
                return pos;
            }
        }
        return null;
    }

    private class AIScatter extends Goal {
        protected final AIScatter.Sorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vector3d flightTarget = null;
        private int cooldown = 0;
        private ITag tag;

        AIScatter() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            tag = EntityTypeTags.getCollection().get(AMTagRegistry.SCATTERS_CROWS);
            this.theNearestAttackableTargetSorter = new AIScatter.Sorter(EntityCrow.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && e.getType().isContained(tag) || e instanceof PlayerEntity && !((PlayerEntity) e).isCreative();
                }
            };
        }

        @Override
        public boolean shouldExecute() {
            if (EntityCrow.this.isPassenger() || EntityCrow.this.aiItemFlag || EntityCrow.this.isBeingRidden() || EntityCrow.this.isTamed()) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityCrow.this.world.getGameTime() % 10;
                if (EntityCrow.this.getIdleTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityCrow.this.getRNG().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntityCrow.this.world.getEntitiesWithinAABB(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
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
        public boolean shouldContinueExecuting() {
            return targetEntity != null && !EntityCrow.this.isTamed();
        }

        public void resetTask() {
            flightTarget = null;
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (flightTarget != null) {
                EntityCrow.this.setFlying(true);
                EntityCrow.this.getMoveHelper().setMoveTo(flightTarget.x, flightTarget.y, flightTarget.z, 1F);
                if(cooldown == 0 && EntityCrow.this.isTargetBlocked(flightTarget)){
                    cooldown = 30;
                    flightTarget = null;
                }
            }

            if (targetEntity != null) {
                if (EntityCrow.this.onGround || flightTarget == null || flightTarget != null && EntityCrow.this.getDistanceSq(flightTarget) < 3) {
                    Vector3d vec = EntityCrow.this.getBlockInViewAway(targetEntity.getPositionVec(), 0);
                    if (vec != null && vec.getY() > EntityCrow.this.getPosY()) {
                        flightTarget = vec;
                    }
                }
                if (EntityCrow.this.getDistance(targetEntity) > 20.0F) {
                    this.resetTask();
                }
            }
        }

        protected double getTargetDistance() {
            return 4D;
        }

        protected AxisAlignedBB getTargetableArea(double targetDistance) {
            Vector3d renderCenter = new Vector3d(EntityCrow.this.getPosX(), EntityCrow.this.getPosY() + 0.5, EntityCrow.this.getPosZ());
            AxisAlignedBB aabb = new AxisAlignedBB(-2, -2, -2, 2, 2, 2);
            return aabb.offset(renderCenter);
        }


        public class Sorter implements Comparator<Entity> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_) {
                double d0 = this.theEntity.getDistanceSq(p_compare_1_);
                double d1 = this.theEntity.getDistanceSq(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
        }
    }

    private class AIAvoidPumpkins extends Goal {
        private final int searchLength;
        private final int field_203113_j;
        protected BlockPos destinationBlock;
        protected int runDelay = 70;
        private Vector3d flightTarget;

        private AIAvoidPumpkins() {
            searchLength = 20;
            field_203113_j = 1;
        }

        public boolean shouldContinueExecuting() {
            return destinationBlock != null && isPumpkin(EntityCrow.this.world, destinationBlock.toMutable()) && isCloseToPumpkin(16);
        }

        public boolean isCloseToPumpkin(double dist) {
            return destinationBlock == null || EntityCrow.this.getDistanceSq(Vector3d.copyCentered(destinationBlock)) < dist * dist;
        }

        @Override
        public boolean shouldExecute() {
            if (EntityCrow.this.isTamed()) {
                return false;
            }
            if (this.runDelay > 0) {
                --this.runDelay;
                return false;
            } else {
                this.runDelay = 70 + EntityCrow.this.rand.nextInt(150);
                return this.searchForDestination();
            }
        }

        public void startExecuting() {
            EntityCrow.this.fleePumpkinFlag = 200;
            Vector3d vec = EntityCrow.this.getBlockInViewAway(Vector3d.copyCentered(destinationBlock), 10);
            if (vec != null) {
                flightTarget = vec;
                EntityCrow.this.setFlying(true);
                EntityCrow.this.getMoveHelper().setMoveTo(vec.x, vec.y, vec.z, 1F);
            }
        }

        public void tick() {
            if (this.isCloseToPumpkin(16)) {
                EntityCrow.this.fleePumpkinFlag = 200;
                if (flightTarget == null || EntityCrow.this.getDistanceSq(flightTarget) < 2F) {
                    Vector3d vec = EntityCrow.this.getBlockInViewAway(Vector3d.copyCentered(destinationBlock), 10);
                    if (vec != null) {
                        flightTarget = vec;
                        EntityCrow.this.setFlying(true);
                    }
                }
                if (flightTarget != null) {
                    EntityCrow.this.getMoveHelper().setMoveTo(flightTarget.x, flightTarget.y, flightTarget.z, 1F);
                }
            }
        }

        public void resetTask() {
            flightTarget = null;
        }

        protected boolean searchForDestination() {
            int lvt_1_1_ = this.searchLength;
            int lvt_2_1_ = this.field_203113_j;
            BlockPos lvt_3_1_ = EntityCrow.this.getPosition();
            BlockPos.Mutable lvt_4_1_ = new BlockPos.Mutable();

            for (int lvt_5_1_ = -8; lvt_5_1_ <= 2; lvt_5_1_++) {
                for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; ++lvt_6_1_) {
                    for (int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
                        for (int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0; lvt_8_1_ <= lvt_6_1_; lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_) {
                            lvt_4_1_.setAndOffset(lvt_3_1_, lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                            if (this.isPumpkin(EntityCrow.this.world, lvt_4_1_)) {
                                this.destinationBlock = lvt_4_1_;
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        private boolean isPumpkin(World world, BlockPos.Mutable lvt_4_1_) {
            return BlockTags.getCollection().get(AMTagRegistry.CROW_FEARS).contains(world.getBlockState(lvt_4_1_).getBlock());
        }

    }

    private class AITargetItems extends CreatureAITargetItems {

        public AITargetItems(CreatureEntity creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
            super(creature, checkSight, onlyNearby, tickThreshold, radius);
            this.executionChance = 1;
        }

        public void resetTask() {
            super.resetTask();
            ((EntityCrow) goalOwner).aiItemFlag = false;
        }

        public boolean shouldExecute() {
            return super.shouldExecute()  &&  !((EntityCrow) goalOwner).isSitting() && (goalOwner.getAttackTarget() == null || !goalOwner.getAttackTarget().isAlive());
        }

        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && !((EntityCrow) goalOwner).isSitting() &&  (goalOwner.getAttackTarget() == null || !goalOwner.getAttackTarget().isAlive());
        }

        @Override
        protected void moveTo() {
            EntityCrow crow = (EntityCrow) goalOwner;
            if (this.targetEntity != null) {
                crow.aiItemFlag = true;
                if (this.goalOwner.getDistance(targetEntity) < 2) {
                    crow.getMoveHelper().setMoveTo(this.targetEntity.getPosX(), targetEntity.getPosY(), this.targetEntity.getPosZ(), 1);
                    crow.peck();
                }
                if (this.goalOwner.getDistance(this.targetEntity) > 8 || crow.isFlying()) {
                    crow.setFlying(true);
                    float f = (float) (crow.getPosX() - targetEntity.getPosX());
                    float f1 = 1.8F;
                    float f2 = (float) (crow.getPosZ() - targetEntity.getPosZ());
                    float xzDist = MathHelper.sqrt(f * f + f2 * f2);

                    if(!crow.canEntityBeSeen(targetEntity)){
                        crow.getMoveHelper().setMoveTo(this.targetEntity.getPosX(), 1 + crow.getPosY(), this.targetEntity.getPosZ(), 1);
                    }else{
                        if (xzDist < 5) {
                            f1 = 0;
                        }
                        crow.getMoveHelper().setMoveTo(this.targetEntity.getPosX(), f1 + this.targetEntity.getPosY(), this.targetEntity.getPosZ(), 1);
                    }
                } else {
                    this.goalOwner.getNavigator().tryMoveToXYZ(this.targetEntity.getPosX(), this.targetEntity.getPosY(), this.targetEntity.getPosZ(), 1);
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
            moveTo();
        }
    }


    private class AIDepositChests extends Goal {
        protected final AIDepositChests.Sorter theNearestAttackableTargetSorter;
        protected final Predicate<ItemFrameEntity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private ItemFrameEntity targetEntity;
        private Vector3d flightTarget = null;
        private int cooldown = 0;
        private ITag tag;

        AIDepositChests() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new AIDepositChests.Sorter(EntityCrow.this);
            this.targetEntitySelector = new Predicate<ItemFrameEntity>() {
                @Override
                public boolean apply(@Nullable ItemFrameEntity e) {
                    BlockPos hangingPosition = e.getHangingPosition().offset(e.getHorizontalFacing().getOpposite());
                    TileEntity entity = e.world.getTileEntity(hangingPosition);
                    if(entity != null){
                        LazyOptional<IItemHandler> handler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, e.getHorizontalFacing().getOpposite());
                        if(handler != null && handler.isPresent()){
                            return e.getDisplayedItem().isItemEqual(EntityCrow.this.getHeldItemMainhand());
                        }
                    }
                    return false;
                }
            };
        }

        @Override
        public boolean shouldExecute() {
            if (EntityCrow.this.isPassenger() || EntityCrow.this.aiItemFlag || EntityCrow.this.isBeingRidden() || EntityCrow.this.isSitting() || EntityCrow.this.getCommand() != 3) {
                return false;
            }
            if(EntityCrow.this.getHeldItemMainhand().isEmpty()){
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityCrow.this.world.getGameTime() % 10;
                if (EntityCrow.this.getIdleTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityCrow.this.getRNG().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<ItemFrameEntity> list = EntityCrow.this.world.getEntitiesWithinAABB(ItemFrameEntity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.theNearestAttackableTargetSorter);
                this.targetEntity = list.get(0);
                this.mustUpdate = false;
                EntityCrow.this.aiItemFrameFlag = true;
                return true;
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return targetEntity != null && EntityCrow.this.getCommand() == 3 && !EntityCrow.this.getHeldItemMainhand().isEmpty();
        }

        public void resetTask() {
            flightTarget = null;
            this.targetEntity = null;
            EntityCrow.this.aiItemFrameFlag = false;
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (flightTarget != null) {
                EntityCrow.this.setFlying(true);
                if(EntityCrow.this.collidedHorizontally){
                    EntityCrow.this.getMoveHelper().setMoveTo(flightTarget.x, EntityCrow.this.getPosY() + 1F, flightTarget.z, 1F);

                }else{
                    EntityCrow.this.getMoveHelper().setMoveTo(flightTarget.x, flightTarget.y, flightTarget.z, 1F);
                }
            }
            if (targetEntity != null) {
                flightTarget = targetEntity.getPositionVec();
                if (EntityCrow.this.getDistance(targetEntity) < 2.0F) {
                    try{
                        BlockPos hangingPosition = targetEntity.getHangingPosition().offset(targetEntity.getHorizontalFacing().getOpposite());
                        TileEntity entity = targetEntity.world.getTileEntity(hangingPosition);
                        Direction deposit = targetEntity.getHorizontalFacing();
                        LazyOptional<IItemHandler> handler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, deposit);
                        if(handler.orElse(null) != null && cooldown == 0) {
                            ItemStack duplicate = EntityCrow.this.getHeldItem(Hand.MAIN_HAND).copy();
                            ItemStack insertSimulate = ItemHandlerHelper.insertItem(handler.orElse(null), duplicate, true);
                            if (!insertSimulate.equals(duplicate)) {
                                ItemStack shrunkenStack = ItemHandlerHelper.insertItem(handler.orElse(null), duplicate, false);
                                if(shrunkenStack.isEmpty()){
                                    EntityCrow.this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                                }else{
                                    EntityCrow.this.setHeldItem(Hand.MAIN_HAND, shrunkenStack);
                                }
                                EntityCrow.this.peck();
                            }else{
                                cooldown = 20;
                            }
                        }
                    }catch (Exception e){
                    }
                    this.resetTask();
                }
            }
        }

        protected double getTargetDistance() {
            return 4D;
        }

        protected AxisAlignedBB getTargetableArea(double targetDistance) {
            Vector3d renderCenter = new Vector3d(EntityCrow.this.getPosX(), EntityCrow.this.getPosY(), EntityCrow.this.getPosZ());
            AxisAlignedBB aabb = new AxisAlignedBB(-16, -16, -16, 16, 16, 16);
            return aabb.offset(renderCenter);
        }


        public class Sorter implements Comparator<Entity> {
            private final Entity theEntity;

            public Sorter(Entity theEntityIn) {
                this.theEntity = theEntityIn;
            }

            public int compare(Entity p_compare_1_, Entity p_compare_2_) {
                double d0 = this.theEntity.getDistanceSq(p_compare_1_);
                double d1 = this.theEntity.getDistanceSq(p_compare_2_);
                return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
            }
        }
    }
}
