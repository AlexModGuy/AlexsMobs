package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

public class EntitySoulVulture extends MonsterEntity implements IFlyingAnimal {

    public static final ResourceLocation SOUL_LOOT = new ResourceLocation("alexsmobs", "entities/soul_vulture_heart");
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntitySoulVulture.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TACKLING = EntityDataManager.createKey(EntitySoulVulture.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<BlockPos>> PERCH_POS = EntityDataManager.createKey(EntitySoulVulture.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final DataParameter<Integer> SOUL_LEVEL = EntityDataManager.createKey(EntitySoulVulture.class, DataSerializers.VARINT);
    public float prevFlyProgress;
    public float flyProgress;
    public float prevTackleProgress;
    public float tackleProgress;
    private boolean isLandNavigator;
    private int perchSearchCooldown = 0;
    private int landingCooldown = 0;
    private int tackleCooldown = 0;

    protected EntitySoulVulture(EntityType type, World worldIn) {
        super(type, worldIn);
        switchNavigator(true);
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return this.getSoulLevel() > 2 ? SOUL_LOOT : super.getLootTable();
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.soulVultureSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    public static boolean canVultureSpawn(EntityType<? extends MobEntity> typeIn, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockPos blockpos = pos.down();
        boolean spawnBlock = BlockTags.getCollection().get(AMTagRegistry.SOUL_VULTURE_SPAWNS).contains(worldIn.getBlockState(blockpos).getBlock());
        return reason == SpawnReason.SPAWNER || spawnBlock && canSpawnOn(AMEntityRegistry.SOUL_VULTURE, worldIn, reason, pos, randomIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SOUL_VULTURE_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SOUL_VULTURE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SOUL_VULTURE_HURT;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 18.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean isPerchBlock(BlockPos pos, BlockState state) {
        return world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2)) && BlockTags.getCollection().get(AMTagRegistry.SOUL_VULTURE_PERCHES).contains(state.getBlock());
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AICirclePerch(this));
        this.goalSelector.addGoal(2, new AIFlyRandom(this));
        this.goalSelector.addGoal(3, new AITackleMelee(this));
        this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 20F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntitySoulVulture.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, AbstractPiglinEntity.class, true));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillagerEntity.class, true));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigatorWide(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new MoveHelper(this);
            this.navigator = new DirectPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, false);
        this.dataManager.register(TACKLING, false);
        this.dataManager.register(PERCH_POS, Optional.empty());
        this.dataManager.register(SOUL_LEVEL, 0);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Flying", this.isFlying());
        if(this.getPerchPos() != null){
            compound.putInt("PerchX", this.getPerchPos().getX());
            compound.putInt("PerchY", this.getPerchPos().getY());
            compound.putInt("PerchZ", this.getPerchPos().getZ());
        }
        compound.putInt("SoulLevel", this.getSoulLevel());
        compound.putInt("LandingCooldown", landingCooldown);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setSoulLevel(compound.getInt("SoulLevel"));
        this.landingCooldown = compound.getInt("LandingCooldown");
        if(compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")){
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
    }

    public boolean isFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, flying);
    }

    public boolean isTackling() {
        return this.dataManager.get(TACKLING);
    }

    public void setTackling(boolean tackling) {
        this.dataManager.set(TACKLING, tackling);
    }

    public BlockPos getPerchPos() {
        return this.dataManager.get(PERCH_POS).orElse(null);
    }

    public void setPerchPos(BlockPos pos) {
        this.dataManager.set(PERCH_POS, Optional.ofNullable(pos));
    }

    public int getSoulLevel() {
        return this.dataManager.get(SOUL_LEVEL);
    }

    public void setSoulLevel(int tackling) {
        this.dataManager.set(SOUL_LEVEL, tackling);
    }

    public void tick() {
        super.tick();
        this.prevTackleProgress = tackleProgress;
        this.prevFlyProgress = flyProgress;
        if (!world.isRemote) {
            if(perchSearchCooldown > 0){
                perchSearchCooldown--;
            }
            if(this.getAttackTarget() != null && this.getAttackTarget().isAlive()){
                this.setPerchPos(this.getAttackTarget().getPosition().up(7));
            }else{
                if (this.getPerchPos() != null && !isPerchBlock(this.getPerchPos(), world.getBlockState(this.getPerchPos()))) {
                    this.setPerchPos(null);
                }
            }
            if (this.getPerchPos() == null && perchSearchCooldown == 0) {
                perchSearchCooldown = 20 + rand.nextInt(20);
                this.setPerchPos(this.findNewPerchPos());
            }
            if (!isFlying() && landingCooldown == 0 && (this.getPerchPos() == null || this.shouldLeavePerch(this.getPerchPos()))) {
                this.setFlying(true);
            }
            if (!isFlying() && this.getAttackTarget() != null){
                this.setFlying(true);
            }

            if(landingCooldown > 0 && isFlying() && this.isOnGround() && this.getAttackTarget() == null){
                this.setFlying(false);
            }
        }
        if (isFlying() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!isFlying() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (this.isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (this.isTackling() && tackleProgress < 5F) {
            tackleProgress++;
        }
        if (!isTackling() && tackleProgress > 0F) {
            tackleProgress--;
        }
        if(landingCooldown > 0){
            landingCooldown--;
        }
        if(tackleCooldown > 0){
            tackleCooldown--;
        }
        if (isFlying()) {
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if(id == 68){
            for (int i = 0; i < 6 + rand.nextInt(3); i++) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.SOUL, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getPosY() + this.getHeight() * 0.5F + (double) (this.rand.nextFloat() * this.getHeight() * 0.5F), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, d0, d1, d2);
            }
        }else{
            super.handleStatusUpdate(id);
        }
    }

    public BlockPos findNewPerchPos() {
        BlockState beneathState = world.getBlockState(this.getPositionUnderneath());
        if(isPerchBlock(this.getPositionUnderneath(), beneathState)){
            return this.getPositionUnderneath();
        }
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 14;
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.getPosition().add(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
            while (this.world.isAirBlock(blockpos1) && blockpos1.getY() > 1) {
                blockpos1 = blockpos1.down();
            }
            if (isPerchBlock(blockpos1, world.getBlockState(blockpos1))) {
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }

    private boolean shouldLeavePerch(BlockPos perchPos) {
        return this.getDistanceSq(Vector3d.copyCentered(perchPos)) > 13 || landingCooldown == 0;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean shouldSwoop(){
        return this.getAttackTarget() != null && this.tackleCooldown == 0;
    }

    class AICirclePerch extends Goal {
        private final EntitySoulVulture vulture;
        float speed = 1;
        float circlingTime = 0;
        float circleDistance = 5;
        float maxCirclingTime = 80;
        boolean clockwise = false;
        private BlockPos targetPos;
        private int yLevel = 1;

        public AICirclePerch(EntitySoulVulture vulture) {
            this.vulture = vulture;
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean shouldExecute() {
            return  !vulture.shouldSwoop() && this.vulture.isFlying() && this.vulture.getPerchPos() != null;
        }

        public void startExecuting() {
            circlingTime = 0;
            speed = 0.8F + rand.nextFloat() * 0.4F;
            yLevel = vulture.rand.nextInt(3);
            maxCirclingTime = 360 + this.vulture.rand.nextInt(80);
            circleDistance = 5 + this.vulture.rand.nextFloat() * 5;
            clockwise = this.vulture.rand.nextBoolean();
        }

        public void resetTask() {
            circlingTime = 0;
            speed = 0.8F + rand.nextFloat() * 0.4F;
            yLevel = vulture.rand.nextInt(3);
            maxCirclingTime = 360 + this.vulture.rand.nextInt(80);
            circleDistance = 5 + this.vulture.rand.nextFloat() * 5;
            clockwise = this.vulture.rand.nextBoolean();
            this.vulture.tackleCooldown = 0;
        }

        public void tick() {
            BlockPos encircle = vulture.getPerchPos();
            double localSpeed = speed;
            if(this.vulture.getAttackTarget() != null){
                localSpeed *= 1.55D;
            }
            if (encircle != null) {
                circlingTime++;
                if(circlingTime > 360){
                    vulture.getMoveHelper().setMoveTo(encircle.getX() + 0.5D, encircle.getY() + 1.1D, encircle.getZ() + 0.5D, localSpeed);
                    if(vulture.collidedVertically || this.vulture.getDistanceSq(encircle.getX() + 0.5D, encircle.getY() + 1.1D, encircle.getZ() + 0.5D) < 1D){
                        vulture.setFlying(false);
                        vulture.setMotion(Vector3d.ZERO);
                        vulture.landingCooldown = 400 + rand.nextInt(1200);
                        resetTask();
                    }
                }else{
                    BlockPos circlePos = getVultureCirclePos(encircle);
                    if (circlePos != null) {
                        vulture.getMoveHelper().setMoveTo(circlePos.getX() + 0.5D, circlePos.getY() + 0.5D, circlePos.getZ() + 0.5D, localSpeed);
                    }
                }
            }
        }

        public boolean shouldContinueExecuting() {
            return shouldExecute();
        }

        public BlockPos getVultureCirclePos(BlockPos target) {
            float angle = (0.01745329251F * 3 * (clockwise ? -circlingTime : circlingTime));
            double extraX = circleDistance * MathHelper.sin((angle));
            double extraZ = circleDistance * MathHelper.cos(angle);
            BlockPos pos = new BlockPos(target.getX() + extraX, target.getY() + 1 + yLevel, target.getZ() + extraZ);
            if (vulture.world.isAirBlock(pos)) {
                return pos;
            }
            return null;
        }
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    class MoveHelper extends MovementController {
        private final EntitySoulVulture parentEntity;

        public MoveHelper(EntitySoulVulture bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                double d5 = vector3d.length();
                if (d5 < 0.3) {
                    this.action = MovementController.Action.WAIT;
                    parentEntity.setMotion(parentEntity.getMotion().scale(0.5D));
                } else {
                    double d0 = this.posX - this.parentEntity.getPosX();
                    double d1 = this.posY - this.parentEntity.getPosY();
                    double d2 = this.posZ - this.parentEntity.getPosZ();
                    double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    parentEntity.setMotion(parentEntity.getMotion().add(vector3d.scale(this.speed * 0.05D / d5)));
                    Vector3d vector3d1 = parentEntity.getMotion();
                    parentEntity.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.renderYawOffset = parentEntity.rotationYaw;

                }

            }
        }

        private boolean func_220673_a(Vector3d p_220673_1_, int p_220673_2_) {
            AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.offset(p_220673_1_);
                if (!this.parentEntity.world.hasNoCollisions(this.parentEntity, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    private class AIFlyRandom extends Goal {

        private final EntitySoulVulture vulture;
        private BlockPos target = null;

        public AIFlyRandom(EntitySoulVulture vulture) {
            this.vulture = vulture;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean shouldExecute() {
            if(vulture.getPerchPos() != null || vulture.shouldSwoop()){
                return false;
            }
            MovementController movementcontroller = this.vulture.getMoveHelper();
            if(!movementcontroller.isUpdating() || target == null){
                target = getBlockInViewVulture();
                if(target != null){
                    this.vulture.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean shouldContinueExecuting() {
            if(vulture.getPerchPos() != null || vulture.shouldSwoop()){
                return false;
            }
            return target != null && vulture.getDistanceSq(Vector3d.copyCentered(target)) > 2.4D && vulture.getMoveHelper().isUpdating() && !vulture.collidedHorizontally;
        }

        public void resetTask(){
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewVulture();
            }
            if (target != null) {
                this.vulture.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if(vulture.getDistanceSq(Vector3d.copyCentered(target)) < 2.5F){
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewVulture() {
            float radius = 0.75F * (0.7F * 6) * -3 - vulture.getRNG().nextInt(10);
            float neg = vulture.getRNG().nextBoolean() ? 1 : -1;
            float renderYawOffset = vulture.renderYawOffset;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (vulture.getRNG().nextFloat() * neg);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(vulture.getPosX() + extraX, vulture.getPosY(), vulture.getPosZ() + extraZ);
            while(world.isAirBlock(radialPos) && radialPos.getY() > 2){
                radialPos = radialPos.down();
            }
            BlockPos newPos = radialPos.up(vulture.getPosY() - radialPos.getY() > 16 ? 4 : vulture.getRNG().nextInt(5) + 5);
            if (!vulture.isTargetBlocked(Vector3d.copyCentered(newPos)) && vulture.getDistanceSq(Vector3d.copyCentered(newPos)) > 6) {
                return newPos;
            }
            return null;
        }
    }

    private class AITackleMelee extends Goal {

        private final EntitySoulVulture vulture;

        public AITackleMelee(EntitySoulVulture vulture) {
            this.vulture = vulture;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean shouldExecute() {
            if(vulture.getAttackTarget() != null && vulture.shouldSwoop()) {
                vulture.setFlying(true);
                return true;
            }
            return false;
        }


        public void resetTask(){
            vulture.setTackling(false);
        }

        public void tick() {
            if(vulture.isFlying()){
                vulture.setTackling(true);
            }else{
                vulture.setTackling(false);
            }
            if (vulture.getAttackTarget() != null) {
                this.vulture.getMoveHelper().setMoveTo(vulture.getAttackTarget().getPosX(), vulture.getAttackTarget().getPosY() + vulture.getAttackTarget().getEyeHeight(), vulture.getAttackTarget().getPosZ(), 2.0D);
                double d0 = this.vulture.getPosX() - this.vulture.getAttackTarget().getPosX();
                double d2 = this.vulture.getPosZ() - this.vulture.getAttackTarget().getPosZ();
                double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                vulture.rotationYaw = f;
                vulture.renderYawOffset = vulture.rotationYaw;
                if (vulture.getBoundingBox().grow(0.3F, 0.3F, 0.3F).intersects(vulture.getAttackTarget().getBoundingBox()) && vulture.tackleCooldown == 0) {
                    tackleCooldown = 100 + rand.nextInt(200);
                    float dmg = (float) vulture.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                    if(vulture.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(vulture), dmg)){
                        if(vulture.getHealth() < vulture.getMaxHealth() - dmg && vulture.getSoulLevel() < 5){
                            this.vulture.setSoulLevel(vulture.getSoulLevel() + 1);
                            this.vulture.heal(dmg);
                            this.vulture.world.setEntityState(vulture, (byte)68);
                        }
                    }
                    resetTask();
                }
            }
        }
    }
}
