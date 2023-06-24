package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;

public class EntitySkunk extends Animal {

    public float prevSprayProgress;
    public float sprayProgress;
    private int prevSprayTime = 0;
    private int harassedTime;
    private int sprayCooldown;
    private Vec3 sprayAt;
    private static final EntityDataAccessor<Integer> SPRAY_TIME = SynchedEntityData.defineId(EntitySkunk.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPRAY_YAW = SynchedEntityData.defineId(EntitySkunk.class, EntityDataSerializers.FLOAT);

    protected EntitySkunk(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPRAY_YAW, 0F);
        this.entityData.define(SPRAY_TIME, 0);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SprayGoal());
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D){
            public void tick() {
                super.tick();
                EntitySkunk.this.harassedTime += 10;
            }
        });
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.SWEET_BERRIES), false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1D, 60));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal(this, LivingEntity.class, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.SKUNK_FEARS), 10,  1.3D, 1.1D, EntitySelector.NO_CREATIVE_OR_SPECTATOR) {
            public boolean canUse() {
                return super.canUse() && EntitySkunk.this.getSprayTime() <= 0;
            }

            public boolean canContinueToUse() {
                return super.canContinueToUse() && EntitySkunk.this.getSprayTime() <= 0;
            }
            public void tick() {
                super.tick();
                if(toAvoid != null){
                    EntitySkunk.this.sprayAt = toAvoid.position();
                }
                EntitySkunk.this.harassedTime += 4;
            }
        });
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.skunkSpawnRolls, this.getRandom(), spawnReasonIn) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SWEET_BERRIES);
    }

    public float getSprayYaw() {
        return entityData.get(SPRAY_YAW);
    }

    public void setSprayYaw(float yaw) {
        entityData.set(SPRAY_YAW, yaw);
    }

    public int getSprayTime() {
        return entityData.get(SPRAY_TIME);
    }

    public void setSprayTime(int time) {
        entityData.set(SPRAY_TIME, time);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SKUNK_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SKUNK_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SKUNK_HURT.get();
    }

    @Override
    public void tick(){
        super.tick();
        this.prevSprayProgress = sprayProgress;
        if(this.getSprayTime() > 0){
            if(this.sprayProgress < 5F){
                this.sprayProgress++;
            }

            this.setSprayTime(this.getSprayTime() - 1);
            if(this.getSprayTime() == 0){
                spawnLingeringCloud();
            }else if(this.getSprayTime() % 6 == 0){
                this.playSound(AMSoundRegistry.SKUNK_SPRAY.get());
            }
            this.yBodyRot = this.getYRot();
            this.setYRot(approachRotation(this.getSprayYaw(), this.getYRot() + 10, 15F));
        }
        if(this.getSprayTime() <= 0 && this.sprayProgress > 0F){
            this.sprayProgress--;
        }
        if(!this.level().isClientSide){
            if(harassedTime > 200 && sprayCooldown == 0 && !this.isBaby()){
                harassedTime = 0;
                sprayCooldown = 200 + random.nextInt(200);
                this.setSprayTime(60 + random.nextInt(60));
            }
            if(harassedTime > 0){
                harassedTime--;
            }
            if(sprayCooldown > 0){
                sprayCooldown--;
            }
            Entity lastHurt = this.getLastHurtByMob();
            if(lastHurt != null){
                this.sprayAt = lastHurt.position();
            }
        }
        prevSprayTime = this.getSprayTime();
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            final float fartDistance = 2.5F;
            Vec3 modelBack = new Vec3(0, 0.4F, -fartDistance).xRot(-this.getXRot() * Mth.DEG_TO_RAD).yRot(-this.getYRot() * Mth.DEG_TO_RAD);
            Vec3 fartAt = this.position().add(modelBack);
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), fartAt.x, fartAt.y, fartAt.z);
            areaeffectcloud.setRadius(2.5F);
            areaeffectcloud.setRadiusOnUse(-0.25F);
            areaeffectcloud.setWaitTime(20);
            areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());

            for(MobEffectInstance mobeffectinstance : collection) {
                areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
            }

            this.level().addFreshEntity(areaeffectcloud);
        }

    }

    public void handleEntityEvent(byte id) {
        if (id == 48) {
            Vec3 modelBack = new Vec3(0, 0.4F, -0.4F).xRot(-this.getXRot() * Mth.DEG_TO_RAD).yRot(-this.getYRot() * Mth.DEG_TO_RAD);
            Vec3 particleFrom = this.position().add(modelBack);
            final float scale = random.nextFloat() * 0.5F + 1F;
            Vec3 particleTo = modelBack.multiply(scale, 1F, scale);
            for(int i = 0; i < 3; ++i) {
                final double d0 = this.random.nextGaussian() * 0.1D;
                final double d1 = this.random.nextGaussian() * 0.1D;
                final double d2 = this.random.nextGaussian() * 0.1D;
                this.level().addParticle(AMParticleRegistry.SMELLY.get(), particleFrom.x, particleFrom.y, particleFrom.z, particleTo.x + d0, particleTo.y - 0.4F + d1, particleTo.z + d2);
            }
        } else {
            super.handleEntityEvent(id);
        }

    }
    private float approachRotation(float current, float target, float max) {
        float f = Mth.wrapDegrees(target - current);
        if (f > max) {
            f = max;
        }

        if (f < -max) {
            f = -max;
        }

        return Mth.wrapDegrees(current + f);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return AMEntityRegistry.SKUNK.get().create(level());
    }

    private class SprayGoal extends Goal {
        private int actualSprayTime = 0;

        public SprayGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntitySkunk.this.getSprayTime() > 0;
        }

        @Override
        public void stop(){
            actualSprayTime = 0;
        }

        @Override
        public void tick(){
            EntitySkunk.this.getNavigation().stop();
            Vec3 sprayAt = getSprayAt();
            final double d0 = EntitySkunk.this.getX() - sprayAt.x;
            final double d2 = EntitySkunk.this.getZ() - sprayAt.z;
            final float f = (float)(Mth.atan2(d2, d0) * (double)Mth.RAD_TO_DEG) - 90.0F;
            EntitySkunk.this.setSprayYaw(f);
            if(EntitySkunk.this.sprayProgress >= 5F){
                level().broadcastEntityEvent(EntitySkunk.this, (byte)48);
                if(actualSprayTime > 10 && random.nextInt(2) == 0){
                    Vec3 skunkPos = new Vec3(EntitySkunk.this.getX(), EntitySkunk.this.getEyeY(), EntitySkunk.this.getZ());
                    final float xAdd = random.nextFloat() * 20 - 10;
                    final float yAdd = random.nextFloat() * 20 - 10;
                    final float maxSprayDist = 5F;
                    Vec3 modelBack = new Vec3(0, 0F, -maxSprayDist).xRot((xAdd - EntitySkunk.this.getXRot()) * Mth.DEG_TO_RAD).yRot((yAdd - EntitySkunk.this.getYRot()) * Mth.DEG_TO_RAD);
                    HitResult hitResult = EntitySkunk.this.level().clip(new ClipContext(skunkPos, skunkPos.add(modelBack), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, EntitySkunk.this));
                    if(hitResult != null) {
                        BlockPos pos;
                        Direction dir;
                        if (hitResult instanceof BlockHitResult block) {
                            pos = block.getBlockPos().relative(block.getDirection());
                            dir = block.getDirection().getOpposite();
                        } else {
                            pos = AMBlockPos.fromVec3(hitResult.getLocation());
                            dir = Direction.UP;
                        }
                        BlockState sprayState = ((MultifaceBlock) AMBlockRegistry.SKUNK_SPRAY.get()).getStateForPlacement(level().getBlockState(pos), level(), pos, dir);
                        if (sprayState != null && sprayState.is(AMBlockRegistry.SKUNK_SPRAY.get())) {
                            level().setBlockAndUpdate(pos, sprayState);
                        }
                        double sprayDist = hitResult.getLocation().subtract(skunkPos).length() / maxSprayDist;
                        AABB poisonBox = new AABB(skunkPos, skunkPos.add(modelBack.scale(sprayDist)).add(0, 1.5F, 0)).inflate(1F);
                        Collection<MobEffectInstance> collection = EntitySkunk.this.getActiveEffects();
                        for (LivingEntity entity : EntitySkunk.this.level().getEntitiesOfClass(LivingEntity.class, poisonBox)) {
                            if (!(entity instanceof EntitySkunk)) {
                                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300));
                                if(entity instanceof ServerPlayer serverPlayer){
                                    AMAdvancementTriggerRegistry.SKUNK_SPRAY.trigger(serverPlayer);
                                }
                                for(MobEffectInstance mobeffectinstance : collection) {
                                    entity.addEffect(new MobEffectInstance(mobeffectinstance));
                                }
                            }
                        }
                    }
                }
                actualSprayTime++;
            }
        }

        private Vec3 getSprayAt() {
            Entity last = EntitySkunk.this.getLastHurtByMob();
            if(EntitySkunk.this.sprayAt != null){
                return EntitySkunk.this.sprayAt;
            }else if(last != null){
                return last.position();
            }else{
                Vec3 modelBack = new Vec3(0, 0.4F, -1).xRot(-EntitySkunk.this.getXRot() * Mth.DEG_TO_RAD).yRot(-EntitySkunk.this.getYRot() * Mth.DEG_TO_RAD);
                return EntitySkunk.this.position().add(modelBack);
            }
        }

    }
}
