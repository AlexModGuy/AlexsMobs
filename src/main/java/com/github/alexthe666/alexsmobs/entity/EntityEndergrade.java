package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.EndergradeAIBreakFlowers;
import com.github.alexthe666.alexsmobs.entity.ai.EndergradeAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIRide;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntityEndergrade extends AnimalEntity implements IFlyingAnimal {

    private static final DataParameter<Integer> BITE_TICK = EntityDataManager.createKey(EntityEndergrade.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(EntityEndergrade.class, DataSerializers.BOOLEAN);
    public float tartigradePitch = 0;
    public float prevTartigradePitch = 0;
    public float biteProgress = 0;
    public float prevBiteProgress = 0;
    public boolean stopWandering = false;
    public boolean hasItemTarget = false;

    protected EntityEndergrade(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new EntityEndergrade.MoveHelperController(this);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 30D).createMutableAttribute(Attributes.ARMOR, 4.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Saddled", this.isSaddled());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSaddled(compound.getBoolean("Saddled"));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BITE_TICK, 0);
        this.dataManager.register(SADDLED, Boolean.valueOf(false));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TameableAIRide(this, 1.2D));
        this.goalSelector.addGoal(1, new EndergradeAIBreakFlowers(this));
        this.goalSelector.addGoal(2, new RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.fromItems(Items.CHORUS_FRUIT), false));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 10));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new EndergradeAITargetItems(this, true));
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) passenger;
                if (player.getHeldItemMainhand().getItem() == AMItemRegistry.CHORUS_ON_A_STICK || player.getHeldItemOffhand().getItem() == AMItemRegistry.CHORUS_ON_A_STICK) {
                    return player;
                }
            }
        }
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ENDERGRADE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ENDERGRADE_HURT;
    }


    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        ActionResultType type = super.func_230254_b_(player, hand);
        if (item == Items.SADDLE && !this.isSaddled()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            this.setSaddled(true);
            return ActionResultType.SUCCESS;
        }
        if (type != ActionResultType.SUCCESS && !isBreedingItem(itemstack)) {
            if (!player.isSneaking() && this.isSaddled()) {
                player.startRiding(this);
                return ActionResultType.SUCCESS;
            }
        }
        return type;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.CHORUS_FRUIT;
    }

    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float radius = -0.25F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() + extraZ);
        }
    }

    public double getMountedYOffset() {
        float f = Math.min(0.25F, this.limbSwingAmount);
        float f1 = this.limbSwing;
        return (double) this.getHeight() - 0.1D + (double) (0.12F * MathHelper.cos(f1 * 0.7F) * 0.7F * f);
    }

    public boolean hasNoGravity() {
        return true;
    }

    public boolean isSaddled() {
        return this.dataManager.get(SADDLED).booleanValue();
    }

    public void setSaddled(boolean saddled) {
        this.dataManager.set(SADDLED, Boolean.valueOf(saddled));
    }

    public void tick() {
        super.tick();
        prevTartigradePitch = this.tartigradePitch;
        prevBiteProgress = this.biteProgress;
        float f2 = (float) -((float) this.getMotion().y * 3 * (double) (180F / (float) Math.PI));
        this.tartigradePitch = f2;
        if (this.getMotion().lengthSquared() > 0.005F) {
            float angleMotion = (0.01745329251F * this.renderYawOffset);
            double extraXMotion = -0.2F * MathHelper.sin((float) (Math.PI + angleMotion));
            double extraZMotion = -0.2F * MathHelper.cos(angleMotion);
            this.world.addParticle(ParticleTypes.END_ROD, this.getPosXRandom(0.5D), this.getPosY() + 0.3, this.getPosZRandom(0.5D), extraXMotion, 0D, extraZMotion);
        }
        int tick = this.dataManager.get(BITE_TICK);
        if (tick > 0) {
            this.dataManager.set(BITE_TICK, tick - 1);
            this.biteProgress++;
        } else if (biteProgress > 0) {
            biteProgress--;
        }
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    private BlockPos getGroundPosition(BlockPos radialPos) {
        while (radialPos.getY() > 1 && world.isAirBlock(radialPos)) {
            radialPos = radialPos.down();
        }
        if (radialPos.getY() <= 1) {
            return new BlockPos(radialPos.getX(), world.getSeaLevel(), radialPos.getZ());
        }
        return radialPos;
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() != RayTraceResult.Type.MISS;
    }

    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem() == Items.CHORUS_FRUIT || stack.getItem() == Items.CHORUS_FLOWER;
    }

    public void onGetItem(ItemEntity targetEntity) {
        this.playSound(SoundEvents.ENTITY_CAT_EAT, this.getSoundVolume(), this.getSoundPitch());
        this.heal(5);
    }

    public void bite() {
        this.dataManager.set(BITE_TICK, 5);
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return AMEntityRegistry.ENDERGRADE.create(p_241840_1_);
    }

    static class RandomFlyGoal extends Goal {
        private final EntityEndergrade parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntityEndergrade mosquito) {
            this.parentEntity = mosquito;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {
            MovementController movementcontroller = this.parentEntity.getMoveHelper();
            if (parentEntity.stopWandering || parentEntity.hasItemTarget) {
                return false;
            }
            if (!movementcontroller.isUpdating() || target == null) {
                target = getBlockInViewEndergrade();
                if (target != null) {
                    this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean shouldContinueExecuting() {
            return target != null && !parentEntity.stopWandering && !parentEntity.hasItemTarget && parentEntity.getDistanceSq(Vector3d.copyCentered(target)) > 2.4D && parentEntity.getMoveHelper().isUpdating() && !parentEntity.collidedHorizontally;
        }

        public void resetTask() {
            target = null;
        }

        public void tick() {
            if (target == null) {
                target = getBlockInViewEndergrade();
            }
            if (target != null) {
                this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.getDistanceSq(Vector3d.copyCentered(target)) < 2.5F) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewEndergrade() {
            float radius = 1 + parentEntity.getRNG().nextInt(5);
            float neg = parentEntity.getRNG().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.renderYawOffset;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (parentEntity.getRNG().nextFloat() * neg);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos radialPos = new BlockPos(parentEntity.getPosX() + extraX, parentEntity.getPosY() + 2, parentEntity.getPosZ() + extraZ);
            BlockPos ground = parentEntity.getGroundPosition(radialPos);
            BlockPos newPos = ground.up(1 + parentEntity.getRNG().nextInt(6));
            if (!parentEntity.isTargetBlocked(Vector3d.copyCentered(newPos)) && parentEntity.getDistanceSq(Vector3d.copyCentered(newPos)) > 6) {
                return newPos;
            }
            return null;
        }
    }

    static class MoveHelperController extends MovementController {
        private final EntityEndergrade parentEntity;

        public MoveHelperController(EntityEndergrade sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
        }

        public void tick() {
            if (this.action == Action.STRAFE) {
                Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                double d0 = vector3d.length();
                parentEntity.setMotion(parentEntity.getMotion().add(0, vector3d.scale(this.speed * 0.05D / d0).getY(), 0));
                float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float f1 = (float) this.speed * f;
                float f2 = this.moveForward;
                float f3 = this.moveStrafe;
                float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
                if (f4 < 1.0F) {
                    f4 = 1.0F;
                }

                f4 = f1 / f4;
                f2 = f2 * f4;
                f3 = f3 * f4;
                float f5 = MathHelper.sin(this.mob.rotationYaw * ((float) Math.PI / 180F));
                float f6 = MathHelper.cos(this.mob.rotationYaw * ((float) Math.PI / 180F));
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                this.moveForward = 1.0F;
                this.moveStrafe = 0.0F;

                this.mob.setAIMoveSpeed(f1);
                this.mob.setMoveForward(this.moveForward);
                this.mob.setMoveStrafing(this.moveStrafe);
                this.action = MovementController.Action.WAIT;
            } else if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d vector3d = new Vector3d(this.posX - parentEntity.getPosX(), this.posY - parentEntity.getPosY(), this.posZ - parentEntity.getPosZ());
                double d0 = vector3d.length();
                if (d0 < parentEntity.getBoundingBox().getAverageEdgeLength()) {
                    this.action = MovementController.Action.WAIT;
                    parentEntity.setMotion(parentEntity.getMotion().scale(0.5D));
                } else {
                    double localSpeed = this.speed;
                    if (parentEntity.isBeingRidden()) {
                        localSpeed *= 1.5D;
                    }
                    parentEntity.setMotion(parentEntity.getMotion().add(vector3d.scale(localSpeed * 0.005D / d0)));
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
}
