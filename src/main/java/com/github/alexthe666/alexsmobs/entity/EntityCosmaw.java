package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class EntityCosmaw extends TamableAnimal implements ITargetsDroppedItems, FlyingAnimal, IFollower {

    private static final EntityDataAccessor<Float> COSMAW_PITCH = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
    public float clutchProgress;
    public float prevClutchProgress;
    public float openProgress;
    public float prevOpenProgress;
    public float prevCosmawPitch;
    public float biteProgress;
    public float prevBiteProgress;
    private UUID fishThrowerID;

    protected EntityCosmaw(EntityType<? extends TamableAnimal> type, Level lvl) {
        super(type, lvl);
        this.moveControl = new FlightMoveController(this, 1F, false, true);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COSMAW_PITCH, 0.0F);
        this.entityData.define(ATTACK_TICK, 0);
        this.entityData.define(COMMAND, Integer.valueOf(0));
        this.entityData.define(SITTING, Boolean.valueOf(false));

    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.entityData.get(ATTACK_TICK) == 0 && this.biteProgress == 0) {
            this.entityData.set(ATTACK_TICK, 5);
        }
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AIAttack());
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FlyingAIFollowOwner(this, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new AIPickupOwner());
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.2D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, Ingredient.of(Items.CHORUS_FRUIT, AMItemRegistry.COSMIC_COD), false));
        this.goalSelector.addGoal(7, new RandomFlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems<>(this, true));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this) {
            public boolean canUse() {
                LivingEntity livingentity = this.mob.getLastHurtByMob();
                if (livingentity != null && EntityCosmaw.this.isOwnedBy(livingentity)) {
                    return false;
                }
                return super.canUse();
            }
        }));
    }

    public boolean isNoGravity() {
        return true;
    }

    public float getClampedCosmawPitch(float partialTick) {
        float f = prevCosmawPitch + (this.getCosmawPitch() - prevCosmawPitch) * partialTick;
        return Mth.clamp(f, -90, 90);
    }

    public float getCosmawPitch() {
        return this.entityData.get(COSMAW_PITCH);
    }

    public void setCosmawPitch(float pitch) {
        this.entityData.set(COSMAW_PITCH, pitch);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND).intValue();
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, Integer.valueOf(command));
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING).booleanValue();
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public void tick() {
        super.tick();
        prevOpenProgress = openProgress;
        prevClutchProgress = clutchProgress;
        prevBiteProgress = biteProgress;
        prevCosmawPitch = this.getCosmawPitch();
        if (!level.isClientSide) {
            float f2 = (float) -((float) this.getDeltaMovement().y * (double) (180F / (float) Math.PI));
            this.setCosmawPitch(this.getCosmawPitch() + 0.6F * (this.getCosmawPitch() + f2) - this.getCosmawPitch());
        }
        if (isMouthOpen() && openProgress < 5F) {
            openProgress++;
        }
        if (!isMouthOpen() && openProgress > 0F) {
            openProgress--;
        }
        if (this.entityData.get(ATTACK_TICK) > 0) {
            if (biteProgress < 5F) {
                biteProgress = Math.min(5F, biteProgress +2F);
            } else {
                if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 3.3D) {
                    this.getTarget().hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }
                this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            }
        } else {
            if (biteProgress > 0F) {
                biteProgress -= 1F;
            }
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean owner = this.isTame() && isOwnedBy(player);
        InteractionResult type = super.mobInteract(player, hand);
        if (canTargetItem(stack) && this.getMainHandItem().isEmpty()) {
            ItemStack rippedStack = stack.copy();
            rippedStack.setCount(1);
            stack.shrink(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
            if (rippedStack.getItem() == AMItemRegistry.COSMIC_COD) {
                fishThrowerID = player.getUUID();
            }
            return InteractionResult.SUCCESS;
        }  else if (owner && !this.isBaby() && type != InteractionResult.CONSUME) {
            if(!level.isClientSide){
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        return type;
    }


    public boolean isMouthOpen() {
        return !this.getMainHandItem().isEmpty();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new DirectPathNavigator(this, level, 0.5F);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }
            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal) entityIn).isOwnedBy(livingentity);
            }
            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }
        return super.isAlliedTo(entityIn);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    private BlockPos getCosmawGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(), this.getY(), in.getZ());
        while (position.getY() < 256 && !level.getFluidState(position).isEmpty()) {
            position = position.above();
        }
        while (position.getY() > 1 && level.isEmptyBlock(position)) {
            position = position.below();
        }
        return position;
    }

    @Override
    public boolean canTargetItem(ItemStack stack) {
        return stack.getItem() == AMItemRegistry.COSMIC_COD || stack.getItem() == Items.CHORUS_FRUIT;
    }

    @Override
    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        if (e.getItem().getItem() == Items.PUMPKIN_SEEDS && !this.isTame()) {
            fishThrowerID = e.getThrower();
        } else {
            fishThrowerID = null;
        }
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public boolean shouldFollow() {
        return false;
    }

    static class RandomFlyGoal extends Goal {
        private final EntityCosmaw parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntityCosmaw mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.parentEntity.getNavigation().isDone() && this.parentEntity.getTarget() == null && this.parentEntity.getRandom().nextInt(4) == 0) {
                target = getBlockInViewCosmaw();
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                    return true;
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && parentEntity.getTarget() == null;
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 4D || this.parentEntity.horizontalCollision) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockInViewCosmaw() {
            float radius = 5 + parentEntity.getRandom().nextInt(10);
            float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.getYRot();
            float angle = (0.01745329251F * renderYawOffset) + 3.15F * (parentEntity.getRandom().nextFloat() * neg);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos(parentEntity.getX() + extraX, parentEntity.getY(), parentEntity.getZ() + extraZ);
            BlockPos ground = parentEntity.getCosmawGround(radialPos);
            if (ground.getY() <= 1) {
                ground = ground.above(50 + parentEntity.random.nextInt(4));
            } else {
                ground = ground.above(2 + parentEntity.random.nextInt(2));
            }
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
                return ground;
            }
            return null;
        }

    }

    private class AIPickupOwner extends Goal {

        @Override
        public boolean canUse() {
            return false;
        }
    }

    private class AIAttack extends Goal {

        public AIAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return EntityCosmaw.this.getTarget() != null && EntityCosmaw.this.getTarget().isAlive();
        }

        public void tick(){
            if(EntityCosmaw.this.distanceTo(EntityCosmaw.this.getTarget()) < 3D * (EntityCosmaw.this.isBaby() ? 0.5F : 1)){
                EntityCosmaw.this.doHurtTarget(EntityCosmaw.this.getTarget());
            }else {
                EntityCosmaw.this.getNavigation().moveTo(EntityCosmaw.this.getTarget(), 1);
            }
        }
    }
}
