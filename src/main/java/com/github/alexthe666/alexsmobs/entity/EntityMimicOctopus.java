package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.entity.CreatureAttribute.WATER;

public class EntityMimicOctopus extends TameableEntity implements ISemiAquatic, IFollower {

    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> UPGRADED = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> MIMIC_ORDINAL = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> PREV_MIMIC_ORDINAL = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MOISTNESS = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<BlockState>> MIMICKED_BLOCK = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    private static final DataParameter<Optional<BlockState>> PREV_MIMICKED_BLOCK = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> LAST_SCARED_MOB_ID = EntityDataManager.createKey(EntityMimicOctopus.class, DataSerializers.VARINT);
    public MimicState localMimicState = MimicState.OVERLAY;
    public float transProgress = 0F;
    public float prevTransProgress = 0F;
    public float colorShiftProgress = 0F;
    public float prevColorShiftProgress = 0F;
    public float groundProgress = 5F;
    public float prevGroundProgress = 0F;
    public float sitProgress = 0F;
    public float prevSitProgress = 0F;
    private boolean isLandNavigator;
    private int moistureAttackTime = 0;
    private int idleMimicCooldown = 120 + rand.nextInt(1200);
    private int mimicCooldown = 0;
    private int stopMimicCooldown = -1;
    private int fishFeedings;
    private int exclaimTime = 0;
    private BlockState localMimic;

    protected EntityMimicOctopus(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.WATER_BORDER, 0.0F);
        switchNavigator(false);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16D).createMutableAttribute(Attributes.ARMOR, 0.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public static MimicState getStateForItem(ItemStack stack) {
        if (ItemTags.getCollection().get(AMTagRegistry.MIMIC_OCTOPUS_CREEPER_ITEMS).contains(stack.getItem())) {
            return MimicState.CREEPER;
        }
        if (ItemTags.getCollection().get(AMTagRegistry.MIMIC_OCTOPUS_GUARDIAN_ITEMS).contains(stack.getItem())) {
            return MimicState.GUARDIAN;
        }
        if (ItemTags.getCollection().get(AMTagRegistry.MIMIC_OCTOPUS_PUFFERFISH_ITEMS).contains(stack.getItem())) {
            return MimicState.PUFFERFISH;
        }
        return null;
    }

    protected float getJumpUpwardsMotion() {
        return super.getJumpUpwardsMotion() * 1.3F;
    }

    @Override
    public boolean shouldFollow() {
        return this.getCommand() == 1;
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

    public boolean isPushedByWater() {
        return false;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AIAttack());
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new FollowOwner(this, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.fromItems(AMItemRegistry.LOBSTER_TAIL, AMItemRegistry.COOKED_LOBSTER_TAIL), false) {
            @Override
            public void tick() {
                EntityMimicOctopus.this.setMimickedBlock(null);
                super.tick();
                EntityMimicOctopus.this.idleMimicCooldown = 40;
                EntityMimicOctopus.this.stopMimicCooldown = 40;
            }
        });
        this.goalSelector.addGoal(5, new AIFlee());
        this.goalSelector.addGoal(6, new AIMimicNearbyMobs());
        this.goalSelector.addGoal(7, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new AISwim());
        this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this){
            @Override
            public boolean shouldExecute() {
                return EntityMimicOctopus.this.isTamed() && super.shouldExecute();
            }
        });
    }

    public boolean isActiveCamo() {
        return this.getMimicState() == MimicState.OVERLAY && this.getMimickedBlock() != null;
    }

    public double getVisibilityMultiplier(@Nullable Entity lookingEntity) {
        if (isActiveCamo()) {
            return super.getVisibilityMultiplier(lookingEntity) * 0.1F;
        } else {
            return super.getVisibilityMultiplier(lookingEntity);
        }
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        MimicState readState = getStateForItem(itemstack);
        ActionResultType type = super.getEntityInteractionResult(player, hand);
        if (readState != null && this.isTamed()) {
            if (mimicCooldown == 0) {
                this.setMimicState(readState);
                mimicCooldown = 20;
                stopMimicCooldown = 1000;
                this.setMimickedBlock(null);
            }
            return ActionResultType.SUCCESS;
        }
        if (!isTamed() && (item == AMItemRegistry.LOBSTER_TAIL || item == AMItemRegistry.COOKED_LOBSTER_TAIL)) {
            this.consumeItemFromStack(player, itemstack);
            this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, this.getSoundVolume(), this.getSoundPitch());
            fishFeedings++;
            if (this.getMimicState() == MimicState.OVERLAY && this.getMimickedBlock() == null) {
                if (fishFeedings > 5 && getRNG().nextInt(2) == 0 || fishFeedings > 8) {
                    this.setTamedBy(player);
                    this.world.setEntityState(this, (byte) 7);
                } else {
                    this.world.setEntityState(this, (byte) 6);
                }
            }
            return ActionResultType.SUCCESS;
        }
        if (isTamed() && (item == AMItemRegistry.LOBSTER_TAIL || item == AMItemRegistry.COOKED_LOBSTER_TAIL)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.consumeItemFromStack(player, itemstack);
                this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, this.getSoundVolume(), this.getSoundPitch());
                this.heal(5);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        }
        if (this.isTamed() && item == Items.SLIME_BALL && this.getMoistness() < 24000) {
            this.setMoistness(48000);
            this.consumeItemFromStack(player, itemstack);
            return ActionResultType.SUCCESS;
        }
        if (type != ActionResultType.SUCCESS && isTamed() && isOwner(player)) {
            if (player.isSneaking()) {
                if (this.getHeldItemMainhand().isEmpty()) {
                    ItemStack cop = itemstack.copy();
                    cop.setCount(1);
                    this.setHeldItem(Hand.MAIN_HAND, cop);
                    itemstack.shrink(1);
                    return ActionResultType.SUCCESS;
                } else {
                    this.entityDropItem(this.getHeldItemMainhand().copy());
                    this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                    return ActionResultType.SUCCESS;
                }
            } else if (!isBreedingItem(itemstack)) {
                this.setCommand(this.getCommand() + 1);
                if (this.getCommand() == 3) {
                    this.setCommand(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("entity.alexsmobs.all.command_" + this.getCommand(), this.getName()), true);
                boolean sit = this.getCommand() == 2;
                if (sit) {
                    this.setSitting(true);
                    return ActionResultType.SUCCESS;
                } else {
                    this.setSitting(false);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return type;
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
    }

    @Override
    public void func_233629_a_(LivingEntity p_233629_1_, boolean p_233629_2_) {
        p_233629_1_.prevLimbSwingAmount = p_233629_1_.limbSwingAmount;
        double d0 = p_233629_1_.getPosX() - p_233629_1_.prevPosX;
        double d1 = p_233629_1_.getPosY() - p_233629_1_.prevPosY;
        double d2 = p_233629_1_.getPosZ() - p_233629_1_.prevPosZ;
        float f = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * (isInWaterOrBubbleColumn() ? 4.0F : 8.0F);
        if (f > 1.0F) {
            f = 1.0F;
        }

        p_233629_1_.limbSwingAmount += (f - p_233629_1_.limbSwingAmount) * 0.4F;
        p_233629_1_.limbSwing += p_233629_1_.limbSwingAmount;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new AnimalSwimMoveControllerSink(this, 1.3F, 1);
            this.navigator = new SemiAquaticPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    public void tick() {
        super.tick();
        if (localMimic != this.getPrevMimickedBlock()) {
            localMimic = this.getPrevMimickedBlock();
            colorShiftProgress = 0.0F;
        }
        if (localMimicState != this.getPrevMimicState()) {
            localMimicState = this.getPrevMimicState();
            transProgress = 0.0F;
        }
        if (this.isInWater() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!this.isInWater() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        BlockPos pos = new BlockPos(this.getPosX(), this.getPosYEye() - 1F, this.getPosZ());
        boolean ground = world.getBlockState(pos).isSolidSide(world, pos, Direction.UP) || !this.isInWaterOrBubbleColumn() || this.isSitting();
        this.prevTransProgress = transProgress;
        this.prevColorShiftProgress = colorShiftProgress;
        this.prevGroundProgress = groundProgress;
        this.prevSitProgress = sitProgress;
        if (this.getPrevMimicState() != this.getMimicState() && transProgress < 5.0F) {
            transProgress += 0.25F;
        }
        if (this.getPrevMimicState() == this.getMimicState() && transProgress > 0F) {
            transProgress -= 0.25F;
        }
        if (getPrevMimickedBlock() != this.getMimickedBlock() && colorShiftProgress < 5.0F) {
            colorShiftProgress += 0.25F;
        }
        if (getPrevMimickedBlock() == this.getMimickedBlock() && colorShiftProgress > 0F) {
            colorShiftProgress -= 0.25F;
        }
        if (ground && groundProgress < 5F) {
            groundProgress += 0.5F;
        }
        if (!ground && groundProgress > 0F) {
            groundProgress -= 0.5F;
        }
        if (isSitting() && sitProgress < 5F) {
            sitProgress += 0.5F;
        }
        if (!isSitting() && sitProgress > 0F) {
            sitProgress -= 0.5F;
        }
        if (this.isInWaterOrBubbleColumn()) {
            float f2 = (float) -((float) this.getMotion().y * 3 * (double) (180F / (float) Math.PI));
            this.rotationPitch = f2;
        }
        if (idleMimicCooldown > 0) {
            idleMimicCooldown--;
        }
        if (mimicCooldown > 0) {
            mimicCooldown--;
        }
        if (stopMimicCooldown > 0) {
            stopMimicCooldown--;
        }
        if (this.isAIDisabled()) {
            this.setAir(this.getMaxAir());
        } else {
            if (this.isInWaterRainOrBubbleColumn() || this.getHeldItemMainhand().getItem() == Items.WATER_BUCKET) {
                this.setMoistness(60000);
            } else {
                this.setMoistness(this.getMoistness() - 1);
                if (this.getMoistness() <= 0 && moistureAttackTime-- <= 0) {
                    this.setSitting(false);
                    this.attackEntityFrom(DamageSource.DRYOUT, rand.nextInt(2) == 0 ? 1.0F : 0F);
                    moistureAttackTime = 20;
                }
            }
        }
        if (idleMimicCooldown <= 0 && stopMimicCooldown == 0) {
            mimicEnvironment();
            idleMimicCooldown = this.getRNG().nextInt(2200) + 200;
        }
        if ((this.getMimicState() != MimicState.OVERLAY || this.getMimickedBlock() != null) && stopMimicCooldown == 0) {
            this.setMimickedBlock(null);
            this.setMimicState(MimicState.OVERLAY);
            stopMimicCooldown = -1;
        }
        if(world.isRemote && exclaimTime > 0){
            exclaimTime--;
            if(exclaimTime == 0){
                Entity e = world.getEntityByID(this.dataManager.get(LAST_SCARED_MOB_ID));
                if (e != null && transProgress >= 5.0F) {
                    double d2 = this.rand.nextGaussian() * 0.1D;
                    double d0 = this.rand.nextGaussian() * 0.1D;
                    double d1 = this.rand.nextGaussian() * 0.1D;
                    this.world.addParticle(AMParticleRegistry.SHOCKED, e.getPosX(), e.getPosYEye() + e.getHeight() * 0.15F + (double) (this.rand.nextFloat() * e.getHeight() * 0.15F), e.getPosZ(), d0, d1, d2);
                }
            }
        }
    /* if(!world.isRemote){
            if(ticksExisted % 80 == 0){
                this.mimicEnvironment();
            }else if(ticksExisted % 40 == 0){
                this.setMimicState(MimicState.PUFFERFISH);
                this.setMimickedBlock(null);
            }
        }*/
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 68) {
            if(exclaimTime == 0){
                exclaimTime = 20;
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void mimicEnvironment() {
        BlockPos down = getPositionDown();
        if (!world.isAirBlock(down)) {
            this.setMimicState(MimicState.OVERLAY);
            this.setMimickedBlock(world.getBlockState(down));
        }
        stopMimicCooldown = this.getRNG().nextInt(2200);
    }

    public int getMoistness() {
        return this.dataManager.get(MOISTNESS);
    }

    public void setMoistness(int p_211137_1_) {
        this.dataManager.set(MOISTNESS, p_211137_1_);
    }

    private BlockPos getPositionDown() {
        BlockPos pos = new BlockPos(this.getPosX(), this.getPosYEye(), this.getPosZ());
        while (pos.getY() > 1 && (world.isAirBlock(pos) || world.getBlockState(pos).getMaterial() == Material.WATER)) {
            pos = pos.down();
        }
        return pos;
    }

    public void travel(Vector3d travelVector) {
        if (this.isSitting()) {
            if (this.getNavigator().getPath() != null) {
                this.getNavigator().clearPath();
            }
            travelVector = Vector3d.ZERO;
            super.travel(travelVector);
            return;
        }
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING).booleanValue();
    }

    public void setSitting(boolean sit) {
        this.dataManager.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isFromBucket() {
        return this.dataManager.get(FROM_BUCKET).booleanValue();
    }

    public void setFromBucket(boolean sit) {
        this.dataManager.set(FROM_BUCKET, Boolean.valueOf(sit));
    }

    public boolean isUpgraded() {
        return this.dataManager.get(FROM_BUCKET).booleanValue();
    }

    public void setUpgraded(boolean sit) {
        this.dataManager.set(FROM_BUCKET, Boolean.valueOf(sit));
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return null;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(MIMIC_ORDINAL, 0);
        this.dataManager.register(PREV_MIMIC_ORDINAL, 0);
        this.dataManager.register(MOISTNESS, 60000);
        this.dataManager.register(MIMICKED_BLOCK, Optional.empty());
        this.dataManager.register(PREV_MIMICKED_BLOCK, Optional.empty());
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COMMAND, 0);
        this.dataManager.register(LAST_SCARED_MOB_ID, -1);
        this.dataManager.register(FROM_BUCKET, false);
        this.dataManager.register(UPGRADED, false);
    }

    public MimicState getMimicState() {
        return MimicState.values()[dataManager.get(MIMIC_ORDINAL)];
    }

    public void setMimicState(MimicState state) {
        if (getMimicState() != state) {
            this.dataManager.set(PREV_MIMIC_ORDINAL, this.dataManager.get(MIMIC_ORDINAL));
        }
        this.dataManager.set(MIMIC_ORDINAL, state.ordinal());
    }

    public MimicState getPrevMimicState() {
        return MimicState.values()[dataManager.get(PREV_MIMIC_ORDINAL)];
    }

    @Nullable
    public BlockState getMimickedBlock() {
        return this.dataManager.get(MIMICKED_BLOCK).orElse(null);
    }

    public void setMimickedBlock(@Nullable BlockState state) {
        if (getMimickedBlock() != state) {
            this.dataManager.set(PREV_MIMICKED_BLOCK, Optional.ofNullable(getMimickedBlock()));
        }
        this.dataManager.set(MIMICKED_BLOCK, Optional.ofNullable(state));
    }

    @Nullable
    public BlockState getPrevMimickedBlock() {
        return this.dataManager.get(PREV_MIMICKED_BLOCK).orElse(null);
    }

    protected void updateAir(int p_209207_1_) {
        if (this.isAlive() && !this.isInWaterOrBubbleColumn()) {
            this.setAir(p_209207_1_ - 1);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.attackEntityFrom(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAir(1200);
        }
    }

    @Override
    public boolean shouldEnterWater() {
        return !this.isSitting() && (this.getAttackTarget() == null || this.getAttackTarget().isInWaterOrBubbleColumn());
    }

    @Override
    public boolean shouldLeaveWater() {
        return this.getAttackTarget() != null && !this.getAttackTarget().isInWaterOrBubbleColumn();
    }

    @Override
    public boolean shouldStopMoving() {
        return isSitting();
    }

    @Override
    public int getWaterSearchRange() {
        return 16;
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());

        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    public Vector3d getBlockInViewAway(Vector3d fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRNG().nextInt(24) - radiusAdd;
        float neg = this.getRNG().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.renderYawOffset;
        float angle = (0.01745329251F * renderYawOffset) + 3.15F + (this.getRNG().nextFloat() * neg);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(fleePos.getX() + extraX, 0, fleePos.getZ() + extraZ);
        BlockPos ground = getOctopusGround(radialPos);

        return ground != null ? Vector3d.copyCentered(ground) : null;
    }

    private BlockPos getOctopusGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getPosY(), in.getZ());
        while (position.getY() > 2 && world.getFluidState(position).isTagged(FluidTags.WATER)) {
            position = position.down();
        }
        return position;
    }

    public enum MimicState {
        OVERLAY,
        CREEPER,
        GUARDIAN,
        PUFFERFISH
    }

    private class AISwim extends SemiAquaticAIRandomSwimming {

        public AISwim() {
            super(EntityMimicOctopus.this, 1, 35);
        }

        protected Vector3d findSurfaceTarget(CreatureEntity creature, int i, int i1) {
            if (creature.getRNG().nextInt(5) == 0) {
                return super.findSurfaceTarget(creature, i, i1);
            } else {
                BlockPos downPos = creature.getPosition();
                while (creature.world.getFluidState(downPos).isTagged(FluidTags.WATER) || creature.world.getFluidState(downPos).isTagged(FluidTags.LAVA)) {
                    downPos = downPos.down();
                }
                if (world.getBlockState(downPos).isSolid() && world.getBlockState(downPos).getBlock() != Blocks.MAGMA_BLOCK) {
                    return new Vector3d(downPos.getX() + 0.5F, downPos.getY(), downPos.getZ() + 0.5F);
                }
            }
            return null;
        }

    }

    private class AIFlee extends Goal {
        protected final EntitySorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 8;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vector3d flightTarget = null;
        private int cooldown = 0;
        private ITag tag;

        AIFlee() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            tag = EntityTypeTags.getCollection().get(AMTagRegistry.MIMIC_OCTOPUS_FEARS);
            this.theNearestAttackableTargetSorter = new EntitySorter(EntityMimicOctopus.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && e.getType().isContained(tag) || e instanceof PlayerEntity && !((PlayerEntity) e).isCreative();
                }
            };
        }

        @Override
        public boolean shouldExecute() {
            if (EntityMimicOctopus.this.isPassenger() || EntityMimicOctopus.this.isBeingRidden() || EntityMimicOctopus.this.isTamed()) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityMimicOctopus.this.world.getGameTime() % 10;
                if (EntityMimicOctopus.this.getIdleTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityMimicOctopus.this.getRNG().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntityMimicOctopus.this.world.getEntitiesWithinAABB(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
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
            return targetEntity != null && !EntityMimicOctopus.this.isTamed() && EntityMimicOctopus.this.getDistance(targetEntity) < 20;
        }

        public void resetTask() {
            flightTarget = null;
            this.targetEntity = null;
            EntityMimicOctopus.this.setMimicState(MimicState.OVERLAY);
            EntityMimicOctopus.this.setMimickedBlock(null);
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (!EntityMimicOctopus.this.isActiveCamo()) {
                EntityMimicOctopus.this.mimicEnvironment();
            }
            if (flightTarget != null) {
                EntityMimicOctopus.this.getNavigator().tryMoveToXYZ(flightTarget.x, flightTarget.y, flightTarget.z, 1.2F);
                if (cooldown == 0 && EntityMimicOctopus.this.isTargetBlocked(flightTarget)) {
                    cooldown = 30;
                    flightTarget = null;
                }
            }

            if (targetEntity != null) {
                if (flightTarget == null || flightTarget != null && EntityMimicOctopus.this.getDistanceSq(flightTarget) < 6) {
                    Vector3d vec;
                    vec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(EntityMimicOctopus.this, 16, 7, targetEntity.getPositionVec());
                    if (vec != null) {
                        flightTarget = vec;
                    }
                }
                if (EntityMimicOctopus.this.getDistance(targetEntity) > 20.0F) {
                    this.resetTask();
                }
            }
        }

        protected double getTargetDistance() {
            return 10;
        }

        protected AxisAlignedBB getTargetableArea(double targetDistance) {
            Vector3d renderCenter = new Vector3d(EntityMimicOctopus.this.getPosX(), EntityMimicOctopus.this.getPosY() + 0.5, EntityMimicOctopus.this.getPosZ());
            AxisAlignedBB aabb = new AxisAlignedBB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
            return aabb.offset(renderCenter);
        }
    }

    public class EntitySorter implements Comparator<Entity> {
        private final Entity theEntity;

        public EntitySorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.getDistanceSq(p_compare_1_);
            double d1 = this.theEntity.getDistanceSq(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }

    public class FollowOwner extends Goal {
        private final EntityMimicOctopus tameable;
        private final IWorldReader world;
        private final double followSpeed;
        private final float maxDist;
        private final float minDist;
        private final boolean teleportToLeaves;
        private LivingEntity owner;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public FollowOwner(EntityMimicOctopus p_i225711_1_, double p_i225711_2_, float p_i225711_4_, float p_i225711_5_, boolean p_i225711_6_) {
            this.tameable = p_i225711_1_;
            this.world = p_i225711_1_.world;
            this.followSpeed = p_i225711_2_;
            this.minDist = p_i225711_4_;
            this.maxDist = p_i225711_5_;
            this.teleportToLeaves = p_i225711_6_;
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean shouldExecute() {
            LivingEntity lvt_1_1_ = this.tameable.getOwner();
            if (lvt_1_1_ == null) {
                return false;
            } else if (lvt_1_1_.isSpectator()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else if (this.tameable.getDistanceSq(lvt_1_1_) < (double) (this.minDist * this.minDist)) {
                return false;
            } else if (this.tameable.getAttackTarget() != null && this.tameable.getAttackTarget().isAlive()) {
                return false;
            } else {
                this.owner = lvt_1_1_;
                return true;
            }
        }

        public boolean shouldContinueExecuting() {
            if (this.tameable.getNavigator().noPath()) {
                return false;
            } else if (this.tameable.isSitting() || tameable.getCommand() != 1) {
                return false;
            } else if (this.tameable.getAttackTarget() != null && this.tameable.getAttackTarget().isAlive()) {
                return false;
            } else {
                return this.tameable.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist);
            }
        }

        public void startExecuting() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
            this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
        }

        public void resetTask() {
            this.owner = null;
            this.tameable.getNavigator().clearPath();
            this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
        }

        public void tick() {

            this.tameable.getLookController().setLookPositionWithEntity(this.owner, 10.0F, (float) this.tameable.getVerticalFaceSpeed());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.tameable.getLeashed() && !this.tameable.isPassenger()) {
                    if (this.tameable.getDistanceSq(this.owner) >= 144.0D) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.tameable.getNavigator().tryMoveToEntityLiving(this.owner, this.followSpeed);
                    }

                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos lvt_1_1_ = this.owner.getPosition();

            for (int lvt_2_1_ = 0; lvt_2_1_ < 10; ++lvt_2_1_) {
                int lvt_3_1_ = this.getRandomNumber(-3, 3);
                int lvt_4_1_ = this.getRandomNumber(-1, 1);
                int lvt_5_1_ = this.getRandomNumber(-3, 3);
                boolean lvt_6_1_ = this.tryToTeleportToLocation(lvt_1_1_.getX() + lvt_3_1_, lvt_1_1_.getY() + lvt_4_1_, lvt_1_1_.getZ() + lvt_5_1_);
                if (lvt_6_1_) {
                    return;
                }
            }

        }

        private boolean tryToTeleportToLocation(int p_226328_1_, int p_226328_2_, int p_226328_3_) {
            if (Math.abs((double) p_226328_1_ - this.owner.getPosX()) < 2.0D && Math.abs((double) p_226328_3_ - this.owner.getPosZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_))) {
                return false;
            } else {
                this.tameable.setLocationAndAngles((double) p_226328_1_ + 0.5D, p_226328_2_, (double) p_226328_3_ + 0.5D, this.tameable.rotationYaw, this.tameable.rotationPitch);
                this.tameable.getNavigator().clearPath();
                return true;
            }
        }

        private boolean isTeleportFriendlyBlock(BlockPos p_226329_1_) {
            PathNodeType lvt_2_1_ = WalkNodeProcessor.getFloorNodeType(this.world, p_226329_1_.toMutable());
            if (world.getFluidState(p_226329_1_).isTagged(FluidTags.WATER) || !world.getFluidState(p_226329_1_).isTagged(FluidTags.WATER) && world.getFluidState(p_226329_1_.down()).isTagged(FluidTags.WATER)) {
                return true;
            }
            if (lvt_2_1_ != PathNodeType.WALKABLE || tameable.getMoistness() < 2000) {
                return false;
            } else {
                BlockState lvt_3_1_ = this.world.getBlockState(p_226329_1_.down());
                if (!this.teleportToLeaves && lvt_3_1_.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos lvt_4_1_ = p_226329_1_.subtract(this.tameable.getPosition());
                    return this.world.hasNoCollisions(this.tameable, this.tameable.getBoundingBox().offset(lvt_4_1_));
                }
            }
        }

        private int getRandomNumber(int p_226327_1_, int p_226327_2_) {
            return this.tameable.getRNG().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
        }
    }

    private class AIMimicNearbyMobs extends Goal {
        protected final EntitySorter theNearestAttackableTargetSorter;
        protected final Predicate<? super Entity> targetEntitySelector;
        protected int executionChance = 30;
        protected boolean mustUpdate;
        private Entity targetEntity;
        private Vector3d flightTarget = null;
        private int cooldown = 0;

        AIMimicNearbyMobs() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            this.theNearestAttackableTargetSorter = new EntitySorter(EntityMimicOctopus.this);
            this.targetEntitySelector = new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity e) {
                    return e.isAlive() && (e instanceof CreeperEntity || e instanceof GuardianEntity || e instanceof PufferfishEntity);
                }
            };
        }

        @Override
        public boolean shouldExecute() {
            if (EntityMimicOctopus.this.isPassenger() || EntityMimicOctopus.this.isBeingRidden() || EntityMimicOctopus.this.getMimicState() != MimicState.OVERLAY || mimicCooldown > 0) {
                return false;
            }
            if (!this.mustUpdate) {
                long worldTime = EntityMimicOctopus.this.world.getGameTime() % 10;
                if (EntityMimicOctopus.this.getIdleTime() >= 100 && worldTime != 0) {
                    return false;
                }
                if (EntityMimicOctopus.this.getRNG().nextInt(this.executionChance) != 0 && worldTime != 0) {
                    return false;
                }
            }
            List<Entity> list = EntityMimicOctopus.this.world.getEntitiesWithinAABB(Entity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
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
            return targetEntity != null && EntityMimicOctopus.this.getDistance(targetEntity) < 10 && EntityMimicOctopus.this.getMimicState() == MimicState.OVERLAY;
        }

        public void resetTask() {
            EntityMimicOctopus.this.getNavigator().clearPath();
            flightTarget = null;
            this.targetEntity = null;
        }

        @Override
        public void tick() {
            if (cooldown > 0) {
                cooldown--;
            }
            if (targetEntity != null) {
                EntityMimicOctopus.this.getNavigator().tryMoveToEntityLiving(targetEntity, 1.2F);
                if (EntityMimicOctopus.this.getDistance(targetEntity) > 20.0F) {
                    this.resetTask();
                    EntityMimicOctopus.this.setMimicState(MimicState.OVERLAY);
                    EntityMimicOctopus.this.setMimickedBlock(null);
                } else if (EntityMimicOctopus.this.getDistance(targetEntity) < 5.0F && EntityMimicOctopus.this.canEntityBeSeen(targetEntity)) {
                    int i = 1200;
                    EntityMimicOctopus.this.stopMimicCooldown = i;
                    EntityMimicOctopus.this.idleMimicCooldown = i + 40;
                    EntityMimicOctopus.this.mimicCooldown = 40;
                    if (targetEntity instanceof CreeperEntity) {
                        EntityMimicOctopus.this.setMimicState(MimicState.CREEPER);
                    } else if (targetEntity instanceof GuardianEntity) {
                        EntityMimicOctopus.this.setMimicState(MimicState.GUARDIAN);
                    } else if (targetEntity instanceof PufferfishEntity) {
                        EntityMimicOctopus.this.setMimicState(MimicState.PUFFERFISH);
                    } else {
                        EntityMimicOctopus.this.setMimicState(MimicState.OVERLAY);
                        EntityMimicOctopus.this.setMimickedBlock(null);
                    }
                    resetTask();
                }

            }
        }

        protected double getTargetDistance() {
            return 10;
        }

        protected AxisAlignedBB getTargetableArea(double targetDistance) {
            Vector3d renderCenter = new Vector3d(EntityMimicOctopus.this.getPosX(), EntityMimicOctopus.this.getPosY() + 0.5, EntityMimicOctopus.this.getPosZ());
            AxisAlignedBB aabb = new AxisAlignedBB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
            return aabb.offset(renderCenter);
        }
    }

    private class AIAttack extends Goal {
        private int executionCooldown = 0;
        private int scareMobTime = 0;
        private Vector3d fleePosition = null;
        public AIAttack() {
        }

        @Override
        public boolean shouldExecute() {
            if(executionCooldown > 0){
                executionCooldown--;
            }
            return executionCooldown == 0 && EntityMimicOctopus.this.isTamed() && EntityMimicOctopus.this.getAttackTarget() != null && EntityMimicOctopus.this.getAttackTarget().isAlive();
        }

        public void resetTask(){
            fleePosition = null;
            scareMobTime = 0;
            executionCooldown = 100 + rand.nextInt(200);
            EntityMimicOctopus.this.mimicEnvironment();

        }

        public Vector3d generateFleePosition(LivingEntity fleer){
            for(int i = 0; i < 15; i++){
                BlockPos pos = fleer.getPosition().add(rand.nextInt(32) - 16, rand.nextInt(16), rand.nextInt(32) - 16);
                while (fleer.world.isAirBlock(pos) && pos.getY() > 1){
                    pos = pos.down();
                }
                if(fleer instanceof CreatureEntity){
                    if(((CreatureEntity) fleer).getBlockPathWeight(pos) >= 0.0F){
                        return Vector3d.copyCentered(pos);
                    }
                }else{
                    return Vector3d.copyCentered(pos);
                }
            }
            return null;
        }
        public void tick(){
            LivingEntity target = EntityMimicOctopus.this.getAttackTarget();
            if(target != null){
                if(scareMobTime > 0){
                    if(fleePosition == null || target.getDistanceSq(fleePosition) < target.getWidth() * target.getWidth() * 2){
                        fleePosition = generateFleePosition(target);
                    }
                    if(target instanceof MobEntity){
                        if(fleePosition != null){
                            ((MobEntity)target).getNavigator().tryMoveToXYZ(fleePosition.x, fleePosition.y, fleePosition.z, 1.5F);
                            ((MobEntity)target).getMoveHelper().setMoveTo(fleePosition.x, fleePosition.y, fleePosition.z, 1.5F);
                        }
                    }else{

                    }
                    scareMobTime--;
                    if(scareMobTime == 0){
                        resetTask();
                        return;
                    }
                }
                double dist = EntityMimicOctopus.this.getDistance(target);
                boolean move = true;
                if(dist < 3F){
                    EntityMimicOctopus.this.dataManager.set(LAST_SCARED_MOB_ID, target.getEntityId());
                    if(EntityMimicOctopus.this.isUpgraded()){

                    }else{
                        move = false;
                        EntityMimicOctopus.this.getNavigator().clearPath();
                        if(EntityMimicOctopus.this.getMimicState() == MimicState.OVERLAY && EntityMimicOctopus.this.mimicCooldown <= 0){
                            EntityMimicOctopus.this.setMimickedBlock(null);
                            if(EntityMimicOctopus.this.isInWaterOrBubbleColumn()){
                                if(rand.nextBoolean()){
                                    EntityMimicOctopus.this.setMimicState(MimicState.GUARDIAN);
                                }else{
                                    EntityMimicOctopus.this.setMimicState(MimicState.PUFFERFISH);
                                }
                            }else{
                                EntityMimicOctopus.this.setMimicState(MimicState.CREEPER);
                            }
                        }
                        if(EntityMimicOctopus.this.getMimicState() != MimicState.OVERLAY){
                            EntityMimicOctopus.this.world.setEntityState(EntityMimicOctopus.this, (byte) 68);
                            EntityMimicOctopus.this.mimicCooldown = 40;
                            EntityMimicOctopus.this.stopMimicCooldown = 60;
                        }
                        if(scareMobTime == 0){
                            scareMobTime = 60 + rand.nextInt(60);
                        }
                    }
                }
                if(move){
                    EntityMimicOctopus.this.faceEntity(target, 30, 30);
                    EntityMimicOctopus.this.getNavigator().tryMoveToEntityLiving(target, 1.2F);
                }
            }
        }
    }
}
