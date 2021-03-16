package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.base.Predicates;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class EntitySunbird extends AnimalEntity implements IFlyingAnimal {

    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    public static final Predicate<? super Entity> SCORCH_PRED = new com.google.common.base.Predicate<Entity>() {
        @Override
        public boolean apply(@Nullable Entity e) {
            return e.isAlive() && e.getType().isContained(EntityTypeTags.getCollection().get(AMTagRegistry.SUNBIRD_SCORCH_TARGETS));
        }
    };
    protected EntitySunbird(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new MoveHelperController(this);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 1F);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.sunbirdSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SUNBIRD_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SUNBIRD_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SUNBIRD_HURT;
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(3, new RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 32F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    }

    public float getBrightness() {
        return 1.0F;
    }

    public boolean hasNoGravity() {
        return true;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean prev = super.attackEntityFrom(source, amount);
        if(prev){
            if(source.getTrueSource() != null){
                if(source.getTrueSource() instanceof LivingEntity){
                    LivingEntity hurter = (LivingEntity)source.getTrueSource();
                    if(hurter.isPotionActive(AMEffectRegistry.SUNBIRD_BLESSING)){
                        hurter.removePotionEffect(AMEffectRegistry.SUNBIRD_BLESSING);
                    }
                    hurter.addPotionEffect(new EffectInstance(AMEffectRegistry.SUNBIRD_CURSE, 600, 0));
                }
            }
            return prev;
        }
        return prev;
    }


    public static boolean canSunbirdSpawn(EntityType<? extends MobEntity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockPos blockpos = pos.down();
        return reason == SpawnReason.SPAWNER || true;
    }

    public void travel(Vector3d travelVector) {
        if (this.isInWater()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.8F));
        } else if (this.isInLava()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.5D));
        } else {
            BlockPos ground = new BlockPos(this.getPosX(), this.getPosY() - 1.0D, this.getPosZ());
            float f = 0.91F;
            if (this.onGround) {
                f = this.world.getBlockState(ground).getSlipperiness(this.world, ground, this) * 0.91F;
            }

            float f1 = 0.16277137F / (f * f * f);
            f = 0.91F;
            if (this.onGround) {
                f = this.world.getBlockState(ground).getSlipperiness(this.world, ground, this) * 0.91F;
            }
            this.func_233629_a_(this, true);

            this.moveRelative(0.2F, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(f));
        }

        this.func_233629_a_(this, false);
    }

    public void tick() {
        super.tick();
        prevBirdPitch = this.birdPitch;

        float f2 = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
        this.birdPitch = f2;

        if (world.isRemote) {
            float radius = 0.35F + rand.nextFloat() * 1.85F;
            float angle = (0.01745329251F * ((rand.nextBoolean() ? -85F : 85F) + this.renderYawOffset));
            float angleMotion = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            double extraXMotion = -0.2F * MathHelper.sin((float) (Math.PI + angleMotion));
            double extraZMotion = -0.2F * MathHelper.cos(angleMotion);
            double yRandom = 0.2F + rand.nextFloat() * 0.3F;
            BasicParticleType type = ParticleTypes.FIREWORK;
            this.world.addParticle(type, this.getPosX() + extraX, this.getPosY() + yRandom, this.getPosZ() + extraZ, extraXMotion, 0D, extraZMotion);
        }else{
            if(this.ticksExisted % 100 == 0){
                List<Entity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getScorchArea(), SCORCH_PRED);
                for(Entity e : list){
                    e.setFire(4);
                    if(e instanceof PhantomEntity){
                        ((PhantomEntity) e).addPotionEffect(new EffectInstance(AMEffectRegistry.SUNBIRD_CURSE, 200, 0));
                    }
                }
                List<PlayerEntity> playerList = this.world.getEntitiesWithinAABB(PlayerEntity.class, this.getScorchArea(), Predicates.alwaysTrue());
                for(PlayerEntity e : playerList){
                    if(!e.isPotionActive(AMEffectRegistry.SUNBIRD_BLESSING) && !e.isPotionActive(AMEffectRegistry.SUNBIRD_CURSE)){
                        e.addPotionEffect(new EffectInstance(AMEffectRegistry.SUNBIRD_BLESSING, 600, 0));
                    }
                }
            }
        }
    }

    private AxisAlignedBB getScorchArea() {
        return this.getBoundingBox().grow(15, 32, 15);
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    static class MoveHelperController extends MovementController {
        private final EntitySunbird parentEntity;

        public MoveHelperController(EntitySunbird sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                double d0 = vector3d.length();
                if (d0 < parentEntity.getBoundingBox().getAverageEdgeLength()) {
                    this.action = MovementController.Action.WAIT;
                    parentEntity.setMotion(parentEntity.getMotion().scale(0.5D));
                } else {
                    parentEntity.setMotion(parentEntity.getMotion().add(vector3d.scale(this.speed * 0.05D / d0)));
                    if (parentEntity.getAttackTarget() == null) {
                        Vector3d vector3d1 = parentEntity.getMotion();
                        parentEntity.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                        parentEntity.renderYawOffset = parentEntity.rotationYaw;
                    } else {
                        double d2 = parentEntity.getAttackTarget().getPosX() - parentEntity.getPosX();
                        double d1 = parentEntity.getAttackTarget().getPosZ() - parentEntity.getPosZ();
                        parentEntity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                        parentEntity.renderYawOffset = parentEntity.rotationYaw;
                    }
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

    static class RandomFlyGoal extends Goal {
        private final EntitySunbird parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntitySunbird sunbird) {
            this.parentEntity = sunbird;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {
            MovementController movementcontroller = this.parentEntity.getMoveHelper();
            if(!movementcontroller.isUpdating() || target == null){
                target = getBlockInViewSunbird();
                if(target != null){
                    this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean shouldContinueExecuting() {
            return target != null && parentEntity.getDistanceSq(Vector3d.copyCentered(target)) > 2.4D && parentEntity.getMoveHelper().isUpdating() && !parentEntity.collidedHorizontally;
        }

        public void resetTask(){
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewSunbird();
            }
            if (target != null) {
                this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if(parentEntity.getDistanceSq(Vector3d.copyCentered(target)) < 2.5F){
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewSunbird() {
            float radius = 0.75F * (0.7F * 6) * -3 - parentEntity.getRNG().nextInt(24);
            float neg = parentEntity.getRNG().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.renderYawOffset;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (parentEntity.getRNG().nextFloat() * neg);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(parentEntity.getPosX() + extraX, 0, parentEntity.getPosZ() + extraZ);
            BlockPos ground = parentEntity.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, radialPos);
            int distFromGround = (int) parentEntity.getPosY() - ground.getY();
            int flightHeight = Math.max(ground.getY(), 180 + parentEntity.getRNG().nextInt(40)) - ground.getY();
            BlockPos newPos = radialPos.up(distFromGround > 16 ? flightHeight : (int) parentEntity.getPosY() + parentEntity.getRNG().nextInt(16) + 1);
            if (!parentEntity.isTargetBlocked(Vector3d.copyCentered(newPos)) && parentEntity.getDistanceSq(Vector3d.copyCentered(newPos)) > 6) {
                return newPos;
            }
            return null;
        }

    }
}
