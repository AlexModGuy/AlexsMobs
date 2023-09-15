package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

public class EntitySoulVulture extends Monster implements FlyingAnimal {

    public static final ResourceLocation SOUL_LOOT = new ResourceLocation("alexsmobs", "entities/soul_vulture_heart");
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntitySoulVulture.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(EntitySoulVulture.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(EntitySoulVulture.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> SOUL_LEVEL = SynchedEntityData.defineId(EntitySoulVulture.class, EntityDataSerializers.INT);
    public float prevFlyProgress;
    public float flyProgress;
    public float prevTackleProgress;
    public float tackleProgress;
    private boolean isLandNavigator;
    private int perchSearchCooldown = 0;
    private int landingCooldown = 0;
    private int tackleCooldown = 0;

    protected EntitySoulVulture(EntityType type, Level worldIn) {
        super(type, worldIn);
        switchNavigator(true);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return hasSoulHeart() ? SOUL_LOOT : super.getDefaultLootTable();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.soulVultureSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    public static boolean canVultureSpawn(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        BlockPos blockpos = pos.below();
        boolean spawnBlock = worldIn.getBlockState(blockpos).is(AMTagRegistry.SOUL_VULTURE_SPAWNS);
        return reason == MobSpawnType.SPAWNER || spawnBlock && checkMobSpawnRules(AMEntityRegistry.SOUL_VULTURE.get(), worldIn, reason, pos, randomIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SOUL_VULTURE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SOUL_VULTURE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SOUL_VULTURE_HURT.get();
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 12.0D).add(Attributes.FOLLOW_RANGE, 18.0D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public boolean isPerchBlock(BlockPos pos, BlockState state) {
        return level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2)) && state.is(AMTagRegistry.SOUL_VULTURE_PERCHES);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AICirclePerch(this));
        this.goalSelector.addGoal(2, new AIFlyRandom(this));
        this.goalSelector.addGoal(3, new AITackleMelee(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 20F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, EntitySoulVulture.class));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, AbstractPiglin.class, true));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillager.class, true));
    }

    public int getMaxSpawnClusterSize() {
        return 1;
    }

    public boolean isMaxGroupSizeReached(int sizeIn) {
        return true;
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new MoveHelper(this);
            this.navigation = new DirectPathNavigator(this, level);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(TACKLING, false);
        this.entityData.define(PERCH_POS, Optional.empty());
        this.entityData.define(SOUL_LEVEL, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        if(this.getPerchPos() != null){
            compound.putInt("PerchX", this.getPerchPos().getX());
            compound.putInt("PerchY", this.getPerchPos().getY());
            compound.putInt("PerchZ", this.getPerchPos().getZ());
        }
        compound.putInt("SoulLevel", this.getSoulLevel());
        compound.putInt("LandingCooldown", landingCooldown);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setSoulLevel(compound.getInt("SoulLevel"));
        this.landingCooldown = compound.getInt("LandingCooldown");
        if(compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")){
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLING);
    }

    public void setTackling(boolean tackling) {
        this.entityData.set(TACKLING, tackling);
    }

    public BlockPos getPerchPos() {
        return this.entityData.get(PERCH_POS).orElse(null);
    }

    public void setPerchPos(BlockPos pos) {
        this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
    }

    public int getSoulLevel() {
        return this.entityData.get(SOUL_LEVEL);
    }

    public void setSoulLevel(int tackling) {
        this.entityData.set(SOUL_LEVEL, tackling);
    }

    public void tick() {
        super.tick();
        this.prevTackleProgress = tackleProgress;
        this.prevFlyProgress = flyProgress;
        if (!level.isClientSide) {
            if(perchSearchCooldown > 0){
                perchSearchCooldown--;
            }
            if(this.getTarget() != null && this.getTarget().isAlive()){
                this.setPerchPos(this.getTarget().blockPosition().above(7));
            }else{
                if (this.getPerchPos() != null && !isPerchBlock(this.getPerchPos(), level.getBlockState(this.getPerchPos()))) {
                    this.setPerchPos(null);
                }
            }
            if (this.getPerchPos() == null && perchSearchCooldown == 0) {
                perchSearchCooldown = 20 + random.nextInt(20);
                this.setPerchPos(this.findNewPerchPos());
            }
            if (!isFlying() && landingCooldown == 0 && (this.getPerchPos() == null || this.shouldLeavePerch(this.getPerchPos()))) {
                this.setFlying(true);
            }
            if (!isFlying() && this.getTarget() != null){
                this.setFlying(true);
            }

            if(landingCooldown > 0 && isFlying() && this.isOnGround() && this.getTarget() == null){
                this.setFlying(false);
            }
        }
        final boolean flying = isFlying();
        if (flying) {
            if (this.isLandNavigator)
                switchNavigator(false);

            if (flyProgress < 5F)
                flyProgress++;
        } else {
            if (!this.isLandNavigator)
                switchNavigator(true);

            if (flyProgress > 0F)
                flyProgress--;
        }

        if (this.isTackling()) {
            if (tackleProgress < 5F)
                tackleProgress++;
        } else {
            if (tackleProgress > 0F)
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
        if (this.level.isClientSide  && hasSoulHeart()) {
            final float radius = 0.25F + random.nextFloat() * 1F;
            final float fly = this.flyProgress * 0.2F;
            final float wingSpread = 15F + 65 * fly + random.nextInt(5);
            final float angle = (Maths.STARTING_ANGLE * ((random.nextBoolean() ? -1 : 1) * (wingSpread + 180) + this.yBodyRot));
            final float angleMotion = (Maths.STARTING_ANGLE * this.yBodyRot);
            final double extraX = radius * Mth.sin(Mth.PI + angle);
            final double extraZ = radius * Mth.cos(angle);
            final double mov = this.getDeltaMovement().length();
            final double extraXMotion = -mov * Mth.sin((float) (Math.PI + angleMotion));
            final double extraZMotion = -mov * Mth.cos(angleMotion);
            final double yRandom = 0.2F + random.nextFloat() * 0.3F;
            this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + extraX, this.getY() + yRandom, this.getZ() + extraZ, extraXMotion, random.nextFloat() * 0.1F, extraZMotion);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if(id == 68){
            for (int i = 0; i < 6 + random.nextInt(3); i++) {
                final double d2 = this.random.nextGaussian() * 0.02D;
                final double d0 = this.random.nextGaussian() * 0.02D;
                final double d1 = this.random.nextGaussian() * 0.02D;
                this.level.addParticle(ParticleTypes.SOUL, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() + this.getBbHeight() * 0.5F + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, d0, d1, d2);
            }
        }else{
            super.handleEntityEvent(id);
        }
    }

    public BlockPos findNewPerchPos() {
        BlockState beneathState = level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
        if(isPerchBlock(this.getBlockPosBelowThatAffectsMyMovement(), beneathState)){
            return this.getBlockPosBelowThatAffectsMyMovement();
        }
        BlockPos blockpos = null;
        Random random = new Random();
        int range = 14;
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.blockPosition().offset(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
            while (this.level.isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
                blockpos1 = blockpos1.below();
            }
            if (isPerchBlock(blockpos1, level.getBlockState(blockpos1))) {
                blockpos = blockpos1;
            }
        }
        return blockpos;
    }

    private boolean shouldLeavePerch(BlockPos perchPos) {
        return this.distanceToSqr(Vec3.atCenterOf(perchPos)) > 13 || landingCooldown == 0;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean shouldSwoop(){
        return this.getTarget() != null && this.tackleCooldown == 0;
    }

    public boolean hasSoulHeart() {
        return getSoulLevel() > 2;
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
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return  !vulture.shouldSwoop() && this.vulture.isFlying() && this.vulture.getPerchPos() != null;
        }

        public void start() {
            circlingTime = 0;
            speed = 0.8F + random.nextFloat() * 0.4F;
            yLevel = vulture.random.nextInt(3);
            maxCirclingTime = 360 + this.vulture.random.nextInt(80);
            circleDistance = 5 + this.vulture.random.nextFloat() * 5;
            clockwise = this.vulture.random.nextBoolean();
        }

        public void stop() {
            circlingTime = 0;
            speed = 0.8F + random.nextFloat() * 0.4F;
            yLevel = vulture.random.nextInt(3);
            maxCirclingTime = 360 + this.vulture.random.nextInt(80);
            circleDistance = 5 + this.vulture.random.nextFloat() * 5;
            clockwise = this.vulture.random.nextBoolean();
            this.vulture.tackleCooldown = 0;
        }

        public void tick() {
            BlockPos encircle = vulture.getPerchPos();
            double localSpeed = speed;
            if(this.vulture.getTarget() != null){
                localSpeed *= 1.55D;
            }
            if (encircle != null) {
                circlingTime++;
                if(circlingTime > 360){
                    vulture.getMoveControl().setWantedPosition(encircle.getX() + 0.5D, encircle.getY() + 1.1D, encircle.getZ() + 0.5D, localSpeed);
                    if(vulture.verticalCollision || this.vulture.distanceToSqr(encircle.getX() + 0.5D, encircle.getY() + 1.1D, encircle.getZ() + 0.5D) < 1D){
                        vulture.setFlying(false);
                        vulture.setDeltaMovement(Vec3.ZERO);
                        vulture.landingCooldown = 400 + random.nextInt(1200);
                        stop();
                    }
                }else{
                    BlockPos circlePos = getVultureCirclePos(encircle);
                    if (circlePos != null) {
                        vulture.getMoveControl().setWantedPosition(circlePos.getX() + 0.5D, circlePos.getY() + 0.5D, circlePos.getZ() + 0.5D, localSpeed);
                    }
                }
            }
        }

        public boolean canContinueToUse() {
            return canUse();
        }

        public BlockPos getVultureCirclePos(BlockPos target) {
            final float angle = (Maths.THREE_STARTING_ANGLE * (clockwise ? -circlingTime : circlingTime));
            final double extraX = circleDistance * Mth.sin((angle));
            final double extraZ = circleDistance * Mth.cos(angle);
            BlockPos pos = new BlockPos((int) (target.getX() + extraX), target.getY() + 1 + yLevel, (int) (target.getZ() + extraZ));
            if (vulture.level.isEmptyBlock(pos)) {
                return pos;
            }
            return null;
        }
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    static class MoveHelper extends MoveControl {
        private final EntitySoulVulture parentEntity;

        public MoveHelper(EntitySoulVulture bird) {
            super(bird);
            this.parentEntity = bird;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                final double d5 = vector3d.length();
                if (d5 < 0.3) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d5)));
                    Vec3 vector3d1 = parentEntity.getDeltaMovement();
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * Mth.RAD_TO_DEG);
                    parentEntity.yBodyRot = parentEntity.getYRot();

                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.parentEntity.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.parentEntity.level.noCollision(this.parentEntity, axisalignedbb)) {
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
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            if(vulture.getPerchPos() != null || vulture.shouldSwoop()){
                return false;
            }
            MoveControl movementcontroller = this.vulture.getMoveControl();
            if(!movementcontroller.hasWanted() || target == null){
                target = getBlockInViewVulture();
                if(target != null){
                    this.vulture.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            if(vulture.getPerchPos() != null || vulture.shouldSwoop()){
                return false;
            }
            return target != null && vulture.distanceToSqr(Vec3.atCenterOf(target)) > 2.4D && vulture.getMoveControl().hasWanted() && !vulture.horizontalCollision;
        }

        public void stop(){
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewVulture();
            }
            if (target != null) {
                this.vulture.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if(vulture.distanceToSqr(Vec3.atCenterOf(target)) < 2.5F){
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewVulture() {
            final float radius = 0.75F * (0.7F * 6) * -3 - vulture.getRandom().nextInt(10);
            final float neg = vulture.getRandom().nextBoolean() ? 1 : -1;
            final float renderYawOffset = vulture.yBodyRot;
            final float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (vulture.getRandom().nextFloat() * neg);
            final double extraX = radius * Mth.sin(Mth.PI + angle);
            final double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos((int) (vulture.getX() + extraX), (int) vulture.getY(), (int) (vulture.getZ() + extraZ));
            while(level.isEmptyBlock(radialPos) && radialPos.getY() > 2){
                radialPos = radialPos.below();
            }
            BlockPos newPos = radialPos.above(vulture.getY() - radialPos.getY() > 16 ? 4 : vulture.getRandom().nextInt(5) + 5);
            if (!vulture.isTargetBlocked(Vec3.atCenterOf(newPos)) && vulture.distanceToSqr(Vec3.atCenterOf(newPos)) > 6) {
                return newPos;
            }
            return null;
        }
    }

    private class AITackleMelee extends Goal {

        private final EntitySoulVulture vulture;

        public AITackleMelee(EntitySoulVulture vulture) {
            this.vulture = vulture;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            if(vulture.getTarget() != null && vulture.shouldSwoop()) {
                vulture.setFlying(true);
                return true;
            }
            return false;
        }


        public void stop(){
            vulture.setTackling(false);
        }

        public void tick() {
            if(vulture.isFlying()){
                vulture.setTackling(true);
            }else{
                vulture.setTackling(false);
            }
            if (vulture.getTarget() != null) {
                this.vulture.getMoveControl().setWantedPosition(vulture.getTarget().getX(), vulture.getTarget().getY() + vulture.getTarget().getEyeHeight(), vulture.getTarget().getZ(), 2.0D);
                final double d0 = this.vulture.getX() - this.vulture.getTarget().getX();
                final double d2 = this.vulture.getZ() - this.vulture.getTarget().getZ();
                final float f = (float)(Mth.atan2(d2, d0) * (double)Mth.RAD_TO_DEG) - 90.0F;
                vulture.setYRot(f);
                vulture.yBodyRot = vulture.getYRot();
                if (vulture.getBoundingBox().inflate(0.3F, 0.3F, 0.3F).intersects(vulture.getTarget().getBoundingBox()) && vulture.tackleCooldown == 0) {
                    tackleCooldown = 100 + random.nextInt(200);
                    float dmg = (float) vulture.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                    if(vulture.getTarget().hurt(DamageSource.mobAttack(vulture), dmg)){
                        if(vulture.getHealth() < vulture.getMaxHealth() - dmg && vulture.getSoulLevel() < 5){
                            this.vulture.setSoulLevel(vulture.getSoulLevel() + 1);
                            this.vulture.heal(dmg);
                            this.vulture.level.broadcastEntityEvent(vulture, (byte)68);
                        }
                    }
                    stop();
                }
            }
        }
    }
}
