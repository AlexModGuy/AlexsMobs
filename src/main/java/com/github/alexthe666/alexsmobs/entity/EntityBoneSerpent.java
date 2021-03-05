package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityBoneSerpent extends MonsterEntity {

    private static final DataParameter<Optional<UUID>> CHILD_UUID = EntityDataManager.createKey(EntityBoneSerpent.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final Predicate<LivingEntity> NOT_RIDING_STRADDLEBOARD_FRIENDLY = (entity) -> {
        return entity.isAlive() && (entity.getRidingEntity() == null || !(entity.getRidingEntity() instanceof EntityStraddleboard) || !((EntityStraddleboard)entity.getRidingEntity()).shouldSerpentFriend());
    };;
    private static final Predicate<EntityStraddleboard> STRADDLEBOARD_FRIENDLY = (entity) -> {
        return entity.isBeingRidden() && entity.shouldSerpentFriend();
    };;

    public int jumpCooldown = 0;
    private boolean isLandNavigator;
    private int boardCheckCooldown = 0;
    private EntityStraddleboard boardToBoast = null;

    protected EntityBoneSerpent(EntityType type, World worldIn) {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.LAVA, 0.0F);
        switchNavigator(false);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.boneSeprentSpawnRolls, this.getRNG(), spawnReasonIn) && super.canSpawn(worldIn, spawnReasonIn);
    }

    public int getMaxSpawnedInChunk() {
        return 1;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }


    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.BONE_SERPENT_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BONE_SERPENT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BONE_SERPENT_HURT;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 1.45F);
    }

    public int getMaxFallHeight() {
        return 256;
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        if (potioneffectIn.getPotion() == Effects.WITHER) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        if (worldIn.getBlockState(pos).getFluidState().isTagged(FluidTags.WATER) || worldIn.getBlockState(pos).getFluidState().isTagged(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    public boolean isPushedByWater() {
        return false;
    }

    public boolean canBeLeashedTo(PlayerEntity player) {
        return true;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public static boolean canBoneSerpentSpawn(EntityType<EntityBoneSerpent> p_234314_0_, IWorld p_234314_1_, SpawnReason p_234314_2_, BlockPos p_234314_3_, Random p_234314_4_) {
        BlockPos.Mutable blockpos$mutable = p_234314_3_.toMutable();

        do {
            blockpos$mutable.move(Direction.UP);
        } while(p_234314_1_.getFluidState(blockpos$mutable).isTagged(FluidTags.LAVA));

        return p_234314_1_.getBlockState(blockpos$mutable).isAir();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreatheAirGoal(this));
        this.goalSelector.addGoal(0, new BoneSerpentAIFindLava(this));
        this.goalSelector.addGoal(1, new BoneSerpentAIMeleeJump(this));
        this.goalSelector.addGoal(2, new BoneSerpentAIJump(this, 10));
        this.goalSelector.addGoal(3, new BoneSerpentAIRandomSwimming(this, 1.0D, 8));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
        if(!AMConfig.neutralBoneSerpents){
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, PlayerEntity.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillagerEntity.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
        }
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, WitherSkeletonEntity.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, EntitySoulVulture.class, 10, true, false, NOT_RIDING_STRADDLEBOARD_FRIENDLY));
    }

    public void travel(Vector3d travelVector) {
        boolean liquid = this.isInLava() || this.isInWater();
        if (this.isServerWorld() && liquid) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = this.createNavigator(world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new BoneSerpentMoveController(this);
            this.navigator = new BoneSerpentPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getChildId() != null) {
            compound.putUniqueId("ChildUUID", this.getChildId());
        }
    }


    public void collideWithNearbyEntities() {
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        entities.stream().filter(entity -> !(entity instanceof EntityBoneSerpentPart) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(this));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.LAVA || source.isFireDamage() || super.isInvulnerableTo(source);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("ChildUUID")) {
            this.setChildId(compound.getUniqueId("ChildUUID"));
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CHILD_UUID, Optional.empty());
    }

    @Nullable
    public UUID getChildId() {
        return this.dataManager.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.dataManager.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
    }

    public void tick() {
        super.tick();
        inPortal = false;
        boolean ground = !this.isInLava() && !this.isInWater() && this.isOnGround();
        if (jumpCooldown > 0) {
            jumpCooldown--;
            float f2 = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
            this.rotationPitch = f2;

        }
        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (!world.isRemote) {
            Entity child = getChild();
            if (child == null) {
                LivingEntity partParent = this;
                int segments = 7 + getRNG().nextInt(8);
                for (int i = 0; i < segments; i++) {
                    EntityBoneSerpentPart part = new EntityBoneSerpentPart(AMEntityRegistry.BONE_SERPENT_PART, partParent, 0.9F, 180, 0);
                    part.setParent(partParent);
                    part.setBodyIndex(i);
                    if (partParent == this) {
                        this.setChildId(part.getUniqueID());
                    }
                    part.setInitialPartPos(this);
                    partParent = part;
                    if (i == segments - 1) {
                        part.setTail(true);
                    }
                    world.addEntity(part);
                }
            }
        }
        if(!world.isRemote){
            if(boardCheckCooldown <= 0){
                boardCheckCooldown = 100 + rand.nextInt(150);
                List<EntityStraddleboard> list = this.world.getEntitiesWithinAABB(EntityStraddleboard.class, this.getBoundingBox().grow(100, 15, 100), STRADDLEBOARD_FRIENDLY);
                EntityStraddleboard closestBoard = null;
                for(EntityStraddleboard board : list){
                    if(closestBoard == null || this.getDistance(closestBoard) > this.getDistance(board)){
                        closestBoard = board;
                    }
                }
                boardToBoast = closestBoard;
            }else{
                boardCheckCooldown--;
            }
            if(boardToBoast != null){
                if(this.getDistance(boardToBoast) > 200){
                    boardToBoast = null;
                }else{
                    if((this.isInLava() || this.isInWater()) && this.getDistance(boardToBoast) < 15 && jumpCooldown == 0){
                        float up = 0.7F + this.getRNG().nextFloat() * 0.8F;
                        Vector3d vector3d1 = this.getLookVec();
                        this.setMotion(this.getMotion().add((double) vector3d1.getX() * 0.6D, up, (double) vector3d1.getY() * 0.6D));
                        this.getNavigator().clearPath();
                        this.jumpCooldown = this.getRNG().nextInt(300) + 100;
                    }
                    if(this.getDistance(boardToBoast) > 5){
                        this.getNavigator().tryMoveToEntityLiving(boardToBoast, 1.5F);

                    }else{
                        this.getNavigator().clearPath();
                    }
                }
            }
        }
    }

    public boolean canBreatheUnderwater() {
        return true;
    }


    static class BoneSerpentMoveController extends MovementController {
        private final EntityBoneSerpent dolphin;

        public BoneSerpentMoveController(EntityBoneSerpent dolphinIn) {
            super(dolphinIn);
            this.dolphin = dolphinIn;
        }

        public void tick() {
            if (this.dolphin.isInWater() || this.dolphin.isInLava()) {
                this.dolphin.setMotion(this.dolphin.getMotion().add(0.0D, 0.005D, 0.0D));
            }

            if (this.action == MovementController.Action.MOVE_TO && !this.dolphin.getNavigator().noPath()) {
                double d0 = this.posX - this.dolphin.getPosX();
                double d1 = this.posY - this.dolphin.getPosY();
                double d2 = this.posZ - this.dolphin.getPosZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double) 2.5000003E-7F) {
                    this.mob.setMoveForward(0.0F);
                } else {
                    float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    this.dolphin.rotationYaw = this.limitAngle(this.dolphin.rotationYaw, f, 10.0F);
                    this.dolphin.renderYawOffset = this.dolphin.rotationYaw;
                    this.dolphin.rotationYawHead = this.dolphin.rotationYaw;
                    float f1 = (float) (this.speed * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    if (this.dolphin.isInWater() || this.dolphin.isInLava()) {
                        this.dolphin.setAIMoveSpeed(f1 * 0.02F);
                        float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double) (180F / (float) Math.PI)));
                        f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                        this.dolphin.setMotion(this.dolphin.getMotion().add(0.0D, (double) this.dolphin.getAIMoveSpeed() * d1 * 0.6D, 0.0D));
                        this.dolphin.rotationPitch = this.limitAngle(this.dolphin.rotationPitch, f2, 1.0F);
                        float f3 = MathHelper.cos(this.dolphin.rotationPitch * ((float) Math.PI / 180F));
                        float f4 = MathHelper.sin(this.dolphin.rotationPitch * ((float) Math.PI / 180F));
                        this.dolphin.moveForward = f3 * f1;
                        this.dolphin.moveVertical = -f4 * f1;
                    } else {
                        this.dolphin.setAIMoveSpeed(f1 * 0.1F);
                    }

                }
            } else {
                this.dolphin.setAIMoveSpeed(0.0F);
                this.dolphin.setMoveStrafing(0.0F);
                this.dolphin.setMoveVertical(0.0F);
                this.dolphin.setMoveForward(0.0F);
            }
        }
    }


}
