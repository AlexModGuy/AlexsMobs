package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.BoneSerpentPathNavigator;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class EntityStradpole extends WaterMobEntity {

    private static final DataParameter<Boolean> FROM_BUCKET = EntityDataManager.createKey(EntityStradpole.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DESPAWN_SOON = EntityDataManager.createKey(EntityStradpole.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LAUNCHED = EntityDataManager.createKey(EntityStradpole.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> PARENT_UUID = EntityDataManager.createKey(EntityStradpole.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public float swimPitch = 0;
    public float prevSwimPitch = 0;
    private int despawnTimer = 0;
    private int ricochetCount = 0;
    protected EntityStradpole(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.setPathPriority(PathNodeType.LAVA, 0.0F);
        this.moveController = new AquaticMoveController(this, 1.4F);
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    public int getMaxSpawnedInChunk() {
        return 2;
    }

    protected ItemStack getFishBucket(){
        ItemStack stack = new ItemStack(AMItemRegistry.STRADPOLE_BUCKET);
        if (this.hasCustomName()) {
            stack.setDisplayName(this.getCustomName());
        }
        return stack;
    }

    protected ActionResultType getEntityInteractionResult(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
        if(itemstack.getItem() == AMItemRegistry.MOSQUITO_LARVA){
            if(!p_230254_1_.isCreative()){
                itemstack.shrink(1);
            }
            if(rand.nextFloat() < 0.45F){
                EntityStraddler straddler = AMEntityRegistry.STRADDLER.create(world);
                straddler.copyLocationAndAnglesFrom(this);
                if(!world.isRemote){
                    world.addEntity(straddler);
                }
                this.remove();
            }
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        if (itemstack.getItem() == Items.LAVA_BUCKET && this.isAlive()) {
            this.playSound(SoundEvents.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
            itemstack.shrink(1);
            ItemStack itemstack1 = this.getFishBucket();
            if (!this.world.isRemote) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)p_230254_1_, itemstack1);
            }

            if (itemstack.isEmpty()) {
                p_230254_1_.setHeldItem(p_230254_2_, itemstack1);
            } else if (!p_230254_1_.inventory.addItemStackToInventory(itemstack1)) {
                p_230254_1_.dropItem(itemstack1, false);
            }

            this.remove();
            return ActionResultType.func_233537_a_(this.world.isRemote);
        } else {
            return super.getEntityInteractionResult(p_230254_1_, p_230254_2_);
        }
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 4.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PARENT_UUID, Optional.empty());
        this.dataManager.register(DESPAWN_SOON, false);
        this.dataManager.register(LAUNCHED, false);
        this.dataManager.register(FROM_BUCKET, false);
    }

    private boolean isFromBucket() {
        return this.dataManager.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean p_203706_1_) {
        this.dataManager.set(FROM_BUCKET, p_203706_1_);
    }


    @Nullable
    public UUID getParentId() {
        return this.dataManager.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.dataManager.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getParentId() != null) {
            compound.putUniqueId("ParentUUID", this.getParentId());
        }
        compound.putBoolean("FromBucket", this.isFromBucket());
        compound.putBoolean("DespawnSoon", this.isDespawnSoon());
    }

    public boolean preventDespawn() {
        return super.preventDespawn() || this.isFromBucket();
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.stradpoleSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public static boolean canStradpoleSpawn(EntityType<EntityStradpole> p_234314_0_, IWorld p_234314_1_, SpawnReason p_234314_2_, BlockPos p_234314_3_, Random p_234314_4_) {
        if(p_234314_1_.getFluidState(p_234314_3_).isTagged(FluidTags.LAVA)){
            if(!p_234314_1_.getFluidState(p_234314_3_.down()).isTagged(FluidTags.LAVA)){

                return p_234314_1_.isAirBlock(p_234314_3_.up());
            }
        }
        return false;
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("ParentUUID")) {
            this.setParentId(compound.getUniqueId("ParentUUID"));
        }
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.setDespawnSoon(compound.getBoolean("DespawnSoon"));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StradpoleAISwim(this, 1.0D, 10));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        if (!worldIn.getBlockState(pos).getFluidState().isEmpty()) {
            return 15.0F;
        } else {
            return Float.NEGATIVE_INFINITY;
        }
    }

    public boolean isDespawnSoon() {
        return this.dataManager.get(DESPAWN_SOON);
    }

    public void setDespawnSoon(boolean despawnSoon) {
        this.dataManager.set(DESPAWN_SOON, despawnSoon);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new BoneSerpentPathNavigator(this, worldIn);
    }

    public void tick() {
        float f = 1.0F;
        if (dataManager.get(LAUNCHED)) {
            this.renderYawOffset = this.rotationYaw;
            RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
            if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS) {
                this.onImpact(raytraceresult);
            }
            f = 0.1F;
        }
        super.tick();
        boolean liquid = this.isInWater() || this.isInLava();
        prevSwimPitch = this.swimPitch;

        float f2 = (float) -((float) this.getMotion().y * (liquid ? 2.5F : f) * (double) (180F / (float) Math.PI));
        this.swimPitch = f2;
        if (this.onGround && !this.isInWater() && !this.isInLava()) {
            this.setMotion(this.getMotion().add((this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.rand.nextFloat() * 2.0F - 1.0F) * 0.2F));
            this.rotationYaw = this.rand.nextFloat() * 360.0F;
            this.onGround = false;
            this.isAirBorne = true;
        }
        this.setNoGravity(false);
        if (liquid) {
            this.setNoGravity(true);
        }
        if (isDespawnSoon()) {
            despawnTimer++;
            if (despawnTimer > 100) {
                despawnTimer = 0;
                this.spawnExplosionParticle();
                this.remove();
            }
        }
    }

    private void onImpact(RayTraceResult raytraceresult) {
        RayTraceResult.Type raytraceresult$type = raytraceresult.getType();
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            this.onEntityHit((EntityRayTraceResult) raytraceresult);
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult traceResult = (BlockRayTraceResult) raytraceresult;
            BlockState blockstate = this.world.getBlockState(traceResult.getPos());
            if (!blockstate.getCollisionShape(this.world, traceResult.getPos()).isEmpty()) {
                Direction face = traceResult.getFace();
                Vector3d prevMotion = this.getMotion();
                double motionX = prevMotion.getX();
                double motionY = prevMotion.getY();
                double motionZ = prevMotion.getZ();
                switch(face){
                    case EAST:
                    case WEST:
                        motionX = -motionX;
                        break;
                    case SOUTH:
                    case NORTH:
                        motionZ = -motionZ;
                        break;
                    default:
                        motionY = -motionY;
                        break;
                }
                this.setMotion(motionX, motionY, motionZ);
                if (this.ticksExisted > 200 || ricochetCount > 20) {
                   this.dataManager.set(LAUNCHED, false);
                } else {
                    ricochetCount++;
                }
            }
        }
    }

    public Entity getParent() {
        UUID id = getParentId();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
    }

    private void onEntityHit(EntityRayTraceResult raytraceresult) {
        Entity entity = this.getParent();
        if (entity instanceof LivingEntity && !world.isRemote && raytraceresult.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity)raytraceresult.getEntity();
            target.attackEntityFrom(DamageSource.causeIndirectDamage(this, (LivingEntity)entity).setProjectile(), 3.0F);
            target.applyKnockback(0.7F, entity.getPosX() - this.getPosX(), entity.getPosZ() - this.getPosZ());
            this.dataManager.set(LAUNCHED, false);
        }
    }

    protected boolean func_230298_a_(Entity p_230298_1_) {
        return !p_230298_1_.isSpectator() && !(p_230298_1_ instanceof EntityStraddler)&& !(p_230298_1_ instanceof EntityStradpole);
    }

    public boolean isBurning() {
        return false;
    }

    public boolean func_230285_a_(Fluid p_230285_1_) {
        return p_230285_1_.isIn(FluidTags.LAVA);
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && (this.isInWater() || this.isInLava())) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.05D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    protected void updateAir(int p_209207_1_) {

    }

    public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
        Vector3d lvt_9_1_ = (new Vector3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.rand.nextGaussian() * 0.007499999832361937D * (double) p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double) p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double) p_70186_8_).scale(p_70186_7_);
        this.setMotion(lvt_9_1_);
        float lvt_10_1_ = MathHelper.sqrt(horizontalMag(lvt_9_1_));
        this.rotationYaw = (float) (MathHelper.atan2(lvt_9_1_.x, lvt_9_1_.z) * 57.2957763671875D);
        this.rotationPitch = (float) (MathHelper.atan2(lvt_9_1_.y, lvt_10_1_) * 57.2957763671875D);
        this.prevRotationPitch = this.rotationPitch;
        this.renderYawOffset = rotationYaw;
        this.rotationYawHead = rotationYaw;
        this.prevRotationYawHead = rotationYaw;
        this.prevRotationYaw = rotationYaw;
        this.setDespawnSoon(true);
        this.dataManager.set(LAUNCHED, true);
    }

    class StradpoleAISwim extends RandomWalkingGoal {
        public StradpoleAISwim(EntityStradpole creature, double speed, int chance) {
            super(creature, speed, chance, false);
        }

        public boolean shouldExecute() {
            if (!this.creature.isInLava() && !this.creature.isInWater() || this.creature.isPassenger() || creature.getAttackTarget() != null || !this.creature.isInWater() && !this.creature.isInLava() && this.creature instanceof ISemiAquatic && !((ISemiAquatic) this.creature).shouldEnterWater()) {
                return false;
            } else {
                if (!this.mustUpdate) {
                    if (this.creature.getRNG().nextInt(this.executionChance) != 0) {
                        return false;
                    }
                }
                Vector3d vector3d = this.getPosition();
                if (vector3d == null) {
                    return false;
                } else {
                    this.x = vector3d.x;
                    this.y = vector3d.y;
                    this.z = vector3d.z;
                    this.mustUpdate = false;
                    return true;
                }
            }
        }

        @Nullable
        protected Vector3d getPosition() {
            if (this.creature.getRNG().nextFloat() < 0.3F) {
                Vector3d vector3d = findSurfaceTarget(this.creature, 15, 7);
                if (vector3d != null) {
                    return vector3d;
                }
            }
            Vector3d vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 7, 3);

            for (int i = 0; vector3d != null && !this.creature.world.getFluidState(new BlockPos(vector3d)).isTagged(FluidTags.LAVA) && !this.creature.world.getBlockState(new BlockPos(vector3d)).allowsMovement(this.creature.world, new BlockPos(vector3d), PathType.WATER) && i++ < 15; vector3d = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7)) {
            }

            return vector3d;
        }

        private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
            BlockPos blockpos = pos.add(dx * scale, 0, dz * scale);
            return this.creature.world.getFluidState(blockpos).isTagged(FluidTags.LAVA) || this.creature.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.creature.world.getBlockState(blockpos).getMaterial().blocksMovement();
        }

        private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
            return this.creature.world.getBlockState(pos.add(dx * scale, 1, dz * scale)).isAir() && this.creature.world.getBlockState(pos.add(dx * scale, 2, dz * scale)).isAir();
        }

        private Vector3d findSurfaceTarget(CreatureEntity creature, int i, int i1) {
            BlockPos upPos = creature.getPosition();
            while (creature.world.getFluidState(upPos).isTagged(FluidTags.WATER) || creature.world.getFluidState(upPos).isTagged(FluidTags.LAVA)) {
                upPos = upPos.up();
            }
            if (isAirAbove(upPos.down(), 0, 0, 0) && canJumpTo(upPos.down(), 0, 0, 0)) {
                return new Vector3d(upPos.getX() + 0.5F, upPos.getY() - 1F, upPos.getZ() + 0.5F);
            }
            return null;
        }
    }

}
