package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class EntityMurmurHead extends Monster implements FlyingAnimal {

    private static final EntityDataAccessor<Optional<UUID>> BODY_UUID = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> BODY_ID = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PULLED_IN = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityMurmurHead.class, EntityDataSerializers.BOOLEAN);
    public double prevXHair;
    public double prevYHair;
    public double prevZHair;
    public double xHair;
    public double yHair;
    public double zHair;
    public float angerProgress;
    public float prevAngerProgress;
    private boolean prevLaunched = false;

    protected EntityMurmurHead(EntityType type, Level level) {
        super(type, level);
        this.moveControl = new MoveController();
    }

    protected EntityMurmurHead(EntityMurmur parent) {
        this(AMEntityRegistry.MURMUR_HEAD.get(), parent.level);
        this.setBodyId(parent.getUUID());
        this.doSpawnPositioning(parent);
    }

    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    public int getExperienceReward() {
        return 0;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AttackGoal());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 10, false, true, null));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillager.class, 30, false, true, null));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BODY_UUID, Optional.empty());
        this.entityData.define(BODY_ID, -1);
        this.entityData.define(PULLED_IN, true);
        this.entityData.define(ANGRY, false);
    }

    private void doSpawnPositioning(EntityMurmur parent){
        this.setPos(parent.getNeckBottom(1.0F).add(0, 0.5F, 0));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 48.0D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean isPulledIn(){
        return this.entityData.get(PULLED_IN);
    }

    public void setPulledIn(boolean pulledIn){
        this.entityData.set(PULLED_IN, pulledIn);
    }

    public boolean isAngry(){
        return this.entityData.get(ANGRY) || !this.isAlive();
    }

    public void setAngry(boolean angry){
        this.entityData.set(ANGRY, angry);
    }


    public Vec3 getNeckTop(float partialTick){
        double d0 = Mth.lerp(partialTick, this.xo, this.getX());
        double d1 = Mth.lerp(partialTick, this.yo, this.getY());
        double d2 = Mth.lerp(partialTick, this.zo, this.getZ());
        double bounce = 0;
        Entity body = this.getBody();
        if(body instanceof EntityMurmur){
            bounce = ((EntityMurmur)body).calculateWalkBounce(partialTick);
        }
        return new Vec3(d0, d1 + bounce, d2);
    }

    public Vec3 getNeckBottom(float partialTick){
        Entity body = this.getBody();
        Vec3 top = this.getNeckTop(partialTick);
        if(body instanceof EntityMurmur){
            EntityMurmur murmur = (EntityMurmur) body;
            Vec3 bodyBase = murmur.getNeckBottom(partialTick);
            double sub = top.subtract(bodyBase).horizontalDistance();
            return sub <= 0.06 ? new Vec3(top.x, bodyBase.y, top.z) : bodyBase;
        }
        return top.add(0, -0.5F, 0);
    }

    public boolean hasNeckBottom(){
        return true;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Nullable
    public UUID getBodyId() {
        return this.entityData.get(BODY_UUID).orElse(null);
    }

    public void setBodyId(@Nullable UUID uniqueId) {
        this.entityData.set(BODY_UUID, Optional.ofNullable(uniqueId));
    }

    public Entity getBody() {
        if (!level.isClientSide) {
            final UUID id = getBodyId();
            return id == null ? null : ((ServerLevel) level).getEntity(id);
        }else{
            int id = this.entityData.get(BODY_ID);
            return id == -1 ? null : level.getEntity(id);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("BodyUUID")) {
            this.setBodyId(compound.getUUID("BodyUUID"));
        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getBodyId() != null) {
            compound.putUUID("BodyUUID", this.getBodyId());
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.35F;
    }

    public void tick(){
        super.tick();
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - 70, this.yBodyRot + 70);
        this.prevAngerProgress = angerProgress;
        if(this.isAngry() && angerProgress < 5F) {
            angerProgress++;
        }
        if(!this.isAngry() && angerProgress > 0F){
            angerProgress--;
        }
        moveHair();
        Entity body = getBody();
        if(!level.isClientSide) {
            if (body instanceof EntityMurmur) {
                EntityMurmur murmur = (EntityMurmur) body;
                this.entityData.set(BODY_ID, body.getId());
                if(this.isPulledIn() && murmur.isAlive()){
                    Vec3 base = murmur.getNeckBottom(1.0F).add(0, 0.55F, 0);
                    Vec3 vec3 = base.subtract(this.position());
                    if(vec3.length() < 1){
                        this.setPos(base.x, base.y, base.z);
                        this.noPhysics = false;
                    }else{
                        this.noPhysics = true;
                        vec3 = base.subtract(this.position()).normalize();
                        float f = this.getTarget() != null && this.getTarget().isAlive() ? 0.3F : 0.15F;
                        this.setDeltaMovement(vec3.scale(f));
                    }
                    this.setYRot(murmur.getYRot());
                    this.yBodyRot = murmur.getYRot();
                }else{
                    this.noPhysics = false;
                }
                LivingEntity headTarget = this.getTarget();
                LivingEntity bodyTarget = murmur.getTarget();
                if(headTarget != null && headTarget.isAlive()){
                    if(murmur.canAttack(headTarget)){
                        murmur.setTarget(headTarget);
                    }else{
                        this.setTarget(null);
                        murmur.setTarget(null);
                    }
                }else if(bodyTarget != null && bodyTarget.isAlive() && this.canAttack(bodyTarget)){
                    this.setTarget(bodyTarget);
                }
                if (body.isRemoved()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
            if(body == null && this.tickCount > 20){
                this.remove(RemovalReason.DISCARDED);
            }
        }else{
            if (body instanceof EntityMurmur) {
                EntityMurmur murmur = (EntityMurmur) body;
                if (murmur.hurtTime > 0 || murmur.deathTime > 0) {
                    this.hurtTime = murmur.hurtTime;
                    this.deathTime = murmur.deathTime;
                }
            }
        }
        if(prevLaunched && !this.isPulledIn()){
            this.playSound(AMSoundRegistry.MURMUR_NECK.get(), 3F * this.getSoundVolume(), this.getVoicePitch());
        }
        prevLaunched = this.isPulledIn();
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        Entity body = this.getBody();
        if(isInvulnerableTo(source)){
            return false;
        }
        if(body != null && body.hurt(source, 0.5F * damage)){
            return true;
        }
        return super.hurt(source, damage);
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damagesource.is(DamageTypes.IN_WALL);
    }

    private void moveHair() {
        this.prevXHair = this.xHair;
        this.prevYHair = this.yHair;
        this.prevZHair = this.zHair;
        double d0 = this.getX() - this.xHair;
        double d1 = this.getY() - this.yHair;
        double d2 = this.getZ() - this.zHair;
        double d3 = 10.0D;
        if (d0 > 10.0D) {
            this.xHair = this.getX();
            this.prevXHair = this.xHair;
        }

        if (d2 > 10.0D) {
            this.zHair = this.getZ();
            this.prevZHair = this.zHair;
        }

        if (d1 > 10.0D) {
            this.yHair = this.getY();
            this.prevYHair = this.yHair;
        }

        if (d0 < -10.0D) {
            this.xHair = this.getX();
            this.prevXHair = this.xHair;
        }

        if (d2 < -10.0D) {
            this.zHair = this.getZ();
            this.prevZHair = this.zHair;
        }

        if (d1 < -10.0D) {
            this.yHair = this.getY();
            this.prevYHair = this.yHair;
        }

        this.xHair += d0 * 0.25D;
        this.zHair += d2 * 0.25D;
        this.yHair += d1 * 0.25D;
    }

    public boolean isAlliedTo(Entity entity) {
        return this.getBodyId() != null && entity.getUUID().equals(this.getBodyId()) || super.isAlliedTo(entity);
    }

    public void playAmbientSound() {
        if(this.isPulledIn() && !this.isAngry()){
            super.playAmbientSound();
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.MURMUR_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.getBody() == null ? AMSoundRegistry.MURMUR_HURT.get() : null;
    }

    protected SoundEvent getDeathSound() {
        return this.getBody() == null ? AMSoundRegistry.MURMUR_HURT.get() : null;
    }

    public boolean isFlying() {
        return true;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity;

        public MoveController() {
            super(EntityMurmurHead.this);
            this.parentEntity = EntityMurmurHead.this;
        }

        public void tick() {
            if(EntityMurmurHead.this.isPulledIn()){
                return;
            }
            float angle = (0.01745329251F * (parentEntity.yBodyRot + 90));
            float radius = (float) Math.sin(parentEntity.tickCount * 0.2F) * 2;
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraY = radius * -Math.cos(angle - Math.PI / 2);
            double extraZ = radius * Mth.cos(angle);
            Vec3 strafPlus = new Vec3(extraX, extraY, extraZ);
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                Vec3 shimmy = Vec3.ZERO;
                LivingEntity attackTarget = parentEntity.getTarget();
                if (attackTarget != null) {
                    if (parentEntity.horizontalCollision) {
                        shimmy = new Vec3(0, 0.005, 0);
                    }
                }

                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.05D / d0);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1.add(strafPlus.scale(0.003D * Math.min(d0, 100)).add(shimmy))));

                if (attackTarget == null && d0 >= width) {
                    Vec3 deltaMovement = parentEntity.getDeltaMovement();
                    parentEntity.setYRot(-((float) Mth.atan2(deltaMovement.x, deltaMovement.z)) * (180F / (float) Math.PI));
                    parentEntity.yBodyRot = parentEntity.getYRot();
                } else {
                    double d2 = attackTarget.getX() - parentEntity.getX();
                    double d1 = attackTarget.getZ() - parentEntity.getZ();
                    parentEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                    parentEntity.yBodyRot = parentEntity.getYRot();
                }
            } else if (this.operation == MoveControl.Operation.WAIT) {
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(strafPlus.scale(0.003D)));
            }
        }
    }

    private class AttackGoal extends Goal {

        private int time;
        private int biteCooldown = 0;
        private Vec3 emergeFrom = Vec3.ZERO;
        private float emergeAngle = 0;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityMurmurHead.this.getTarget() != null && EntityMurmurHead.this.getTarget().isAlive();
        }

        public void start(){
            time = 0;
            biteCooldown = 0;
            EntityMurmurHead.this.setPulledIn(false);
        }

        public void stop(){
            time = 0;
            EntityMurmurHead.this.setPulledIn(true);
            EntityMurmurHead.this.setAngry(false);
        }

        public void tick(){
            LivingEntity target = EntityMurmurHead.this.getTarget();
            Entity body = EntityMurmurHead.this.getBody();
            if(target != null){
                double dist = Math.sqrt(EntityMurmurHead.this.distanceToSqr(target.getEyePosition()));
                double bodyDist = body != null ? body.distanceTo(target) : 0.0;
                if(bodyDist > 16 && time > 30){
                    if(body instanceof EntityMurmur){
                        EntityMurmur murmur = (EntityMurmur) body;
                        murmur.setTarget(target);
                        murmur.getNavigation().moveTo(target, 1.35D);
                    }
                }
                if(bodyDist > 64){
                    EntityMurmurHead.this.setPulledIn(true);
                }else if(biteCooldown == 0){
                    EntityMurmurHead.this.setPulledIn(false);
                    Vec3 moveTo = target.getEyePosition();
                    if(time > 30){
                        if(!EntityMurmurHead.this.isAngry()){
                            EntityMurmurHead.this.playSound(AMSoundRegistry.MURMUR_ANGER.get(), 1.5F * EntityMurmurHead.this.getSoundVolume(), EntityMurmurHead.this.getVoicePitch());
                            EntityMurmurHead.this.gameEvent(GameEvent.ENTITY_ROAR);
                        }
                        EntityMurmurHead.this.setAngry(true);
                        EntityMurmurHead.this.getNavigation().moveTo(moveTo.x, moveTo.y, moveTo.z, 1.3D);
                    }else{
                        if(time == 0){
                            emergeFrom = EntityMurmurHead.this.getNeckTop(1.0F).add(0, 0.5F, 0);
                            Vec3 face = moveTo.subtract(emergeFrom);
                        }
                        boolean clockwise = false;
                        float circleDistance = 2.5F;
                        float circlingTime = 30 * time;
                        float angle = (0.01745329251F * (clockwise ? -circlingTime : circlingTime));
                        double extraX = circleDistance * Mth.sin((float) (Math.PI + angle));
                        double extraZ = circleDistance * Mth.cos(angle);
                        double y = Math.max(emergeFrom.y + 2, target.getEyeY());
                        Vec3 vec3 = new Vec3(emergeFrom.x + extraX, y, emergeFrom.z + extraZ);
                        EntityMurmurHead.this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.7D);
                    }
                    EntityMurmurHead.this.lookAt(EntityAnchorArgument.Anchor.EYES, moveTo);
                    if(dist < 1.5F && EntityMurmurHead.this.hasLineOfSight(target)){
                        EntityMurmurHead.this.playSound(AMSoundRegistry.MURMUR_ATTACK.get(), EntityMurmurHead.this.getSoundVolume(), EntityMurmurHead.this.getVoicePitch());
                        biteCooldown = 5 + EntityMurmurHead.this.getRandom().nextInt(15);
                        target.hurt(this.damageSources().mobAttack(EntityMurmurHead.this), 5.0F);
                    }
                }else{
                    EntityMurmurHead.this.setPulledIn(true);
                    EntityMurmurHead.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    EntityMurmurHead.this.setAngry(false);
                }
                time++;
            }
            if(biteCooldown > 0){
                biteCooldown--;
            }
        }
    }
}
