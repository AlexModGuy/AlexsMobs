package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class EntityGuster extends MonsterEntity {

    private static final DataParameter<Integer> LIFT_ENTITY = EntityDataManager.createKey(EntityGuster.class, DataSerializers.VARINT);
    private LivingEntity liftedEntity;
    private int liftingTime = 0;
    private int maxLiftTime = 40;
    private int shootingTicks;

    protected EntityGuster(EntityType type, World worldIn) {
        super(type, worldIn);
        this.stepHeight = 1;
        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    public int getTalkInterval() {
        return 80;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.GUSTER_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.GUSTER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.GUSTER_HURT;
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    public static boolean canGusterSpawn(EntityType animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        boolean spawnBlock = BlockTags.SAND.contains(worldIn.getBlockState(pos.down()).getBlock());
        return spawnBlock && (!AMConfig.limitGusterSpawnsToWeather || worldIn.getWorldInfo() != null && (worldIn.getWorldInfo().isThundering() || worldIn.getWorldInfo().isRaining()));
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.gusterSpawnRolls, this.getRNG(), spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeGoal());
        this.goalSelector.addGoal(1, new AnimalAIWanderRanged(this, 60, 1.0D, 10, 7));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(2, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }


    public void collideWithEntity(Entity entityIn) {
        if (this.getLiftedEntity() == null && liftingTime >= 0 && !(entityIn instanceof EntityGuster)) {
            this.setLiftedEntity(entityIn.getEntityId());
            maxLiftTime = 30 + rand.nextInt(30);
        }
    }

    public boolean hasLiftedEntity() {
        return this.dataManager.get(LIFT_ENTITY) != 0;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(LIFT_ENTITY, 0);
    }


    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (source.isProjectile()) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }


    private void spit(LivingEntity target) {
        EntitySandShot llamaspitentity = new EntitySandShot(this.world, this);
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333D) - llamaspitentity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.35F;
        llamaspitentity.shoot(d0, d1 + (double) f, d2, 1F, 10.0F);
        if (!this.isSilent()) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_SAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
        }
        this.world.addEntity(llamaspitentity);
    }

    public double getPosYEye() {
        return this.getPosY() + 1.0F;
    }


    @Nullable
    public Entity getLiftedEntity() {
        if (!this.hasLiftedEntity()) {
            return null;
        } else {
            return this.world.getEntityByID(this.dataManager.get(LIFT_ENTITY));
        }
    }

    private void setLiftedEntity(int p_175463_1_) {
        this.dataManager.set(LIFT_ENTITY, p_175463_1_);
    }

    public void livingTick() {
        super.livingTick();
        Entity lifted = this.getLiftedEntity();
        if (lifted == null && !world.isRemote && ticksExisted % 15 == 0) {
            List<ItemEntity> list = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.8F));
            ItemEntity closestItem = null;
            for (int i = 0; i < list.size(); ++i) {
                ItemEntity entity = list.get(i);
                if (entity.isOnGround() && (closestItem == null || this.getDistance(closestItem) > this.getDistance(entity))) {
                    closestItem = entity;
                }
            }
            if (closestItem != null) {
                this.setLiftedEntity(closestItem.getEntityId());
                maxLiftTime = 30 + rand.nextInt(30);
            }
        }
        if (this.isInWaterOrBubbleColumn()) {
            this.attackEntityFrom(DamageSource.DROWN, 0.5F);
        }
        float f = (float) this.getPosY();
        if (this.isAlive()) {
            for (int j = 0; j < 4; ++j) {
                float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.95F;
                float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.95F;
                this.world.addParticle(AMParticleRegistry.GUSTER_SAND_SPIN, this.getPosX() + (double) f1, f, this.getPosZ() + (double) f2, this.getPosX(), this.getPosY() + rand.nextFloat() * this.getHeight() + 0.2F, this.getPosZ());
            }
        }
        if (lifted != null && liftingTime >= 0) {
            liftingTime++;
            float resist = 1F;
            if (lifted instanceof LivingEntity) {
                resist = (float) MathHelper.clamp((1.0D - ((LivingEntity) lifted).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
            }
            float radius = 1F + (liftingTime * 0.05F);
            if (lifted instanceof ItemEntity) {
                radius = 0.2F + (liftingTime * 0.025F);
            }
            float angle = liftingTime * -0.25F;
            double extraX = this.getPosX() + radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = this.getPosZ() + radius * MathHelper.cos(angle);
            double d0 = (extraX - lifted.getPosX()) * resist;
            double d1 = (extraZ - lifted.getPosZ()) * resist;
            lifted.setMotion(d0, 0.1 * resist, d1);
            lifted.isAirBorne = true;
            if (liftingTime > maxLiftTime) {
                this.setLiftedEntity(0);
                liftingTime = -20;
                maxLiftTime = 30 + rand.nextInt(30);
            }
        } else if (liftingTime < 0) {
            liftingTime++;
        } else if (this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < this.getWidth() + 1F && !(this.getAttackTarget() instanceof EntityGuster)) {
            this.setLiftedEntity(this.getAttackTarget().getEntityId());
            maxLiftTime = 30 + rand.nextInt(30);
        }
        if (!world.isRemote && shootingTicks >= 0) {
            if (shootingTicks <= 0) {
                if (this.getAttackTarget() != null && (lifted == null || lifted.getEntityId() != this.getAttackTarget().getEntityId()) && this.isAlive()) {
                    this.spit(this.getAttackTarget());
                }
                shootingTicks = 40 + rand.nextInt(40);
            } else {
                shootingTicks--;
            }
        }
        Vector3d vector3d = this.getMotion();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setMotion(vector3d.mul(1.0D, 0.6D, 1.0D));
        }
    }

    public boolean isGooglyEyes() {
        String s = TextFormatting.getTextWithoutFormattingCodes(this.getName().getString());
        return s != null && s.toLowerCase().contains("tweester");
    }

    private class MeleeGoal extends Goal {

        public MeleeGoal() {
        }

        public boolean shouldExecute() {
            return EntityGuster.this.getAttackTarget() != null;
        }

        public void tick() {
            Entity thrownEntity = EntityGuster.this.getLiftedEntity();

            if (EntityGuster.this.getAttackTarget() != null) {
                if (thrownEntity != null && thrownEntity.getEntityId() == EntityGuster.this.getAttackTarget().getEntityId()) {
                    EntityGuster.this.getNavigator().clearPath();
                } else {
                    EntityGuster.this.getNavigator().tryMoveToEntityLiving(EntityGuster.this.getAttackTarget(), 1.25F);
                }
            }
        }
    }
}
