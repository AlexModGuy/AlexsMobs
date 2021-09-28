package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntityCosmaw extends TamableAnimal implements ITargetsDroppedItems, FlyingAnimal {

    private static final EntityDataAccessor<Float> COSMAW_PITCH = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.FLOAT);
    public float clutchProgress;
    public float prevClutchProgress;
    public float openProgress;
    public float prevOpenProgress;
    public float prevCosmawPitch;

    protected EntityCosmaw(EntityType<? extends TamableAnimal> type, Level lvl) {
        super(type, lvl);
        this.moveControl = new FlightMoveController(this, 1F, false, true);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COSMAW_PITCH, 0.0F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, Ingredient.of(Items.CHORUS_FRUIT, AMItemRegistry.COSMIC_COD), false));
        this.goalSelector.addGoal(3, new RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems<>(this, true));
    }

    public boolean isNoGravity() {
        return true;
    }

    public float getClampedCosmawPitch(float partialTick){
        float f = prevCosmawPitch + (this.getCosmawPitch() - prevCosmawPitch) * partialTick;
        return Mth.clamp(f, -90, 90);
    }

    public float getCosmawPitch() {
        return this.entityData.get(COSMAW_PITCH);
    }

    public void setCosmawPitch(float pitch) {
        this.entityData.set(COSMAW_PITCH, pitch);
    }

    public void tick(){
        super.tick();
        prevOpenProgress = openProgress;
        prevClutchProgress = clutchProgress;
        prevCosmawPitch = this.getCosmawPitch();
        if(!level.isClientSide){
            float f2 = (float) -((float) this.getDeltaMovement().y * (double) (180F / (float) Math.PI));
            this.setCosmawPitch(this.getCosmawPitch() + 0.6F * (this.getCosmawPitch() + f2) - this.getCosmawPitch());
        }
        if(isMouthOpen() && openProgress < 5F) {
            openProgress++;
        }
        if(!isMouthOpen() && openProgress > 0F) {
            openProgress--;
        }
    }

    public boolean isMouthOpen(){
        return !this.getMainHandItem().isEmpty();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new DirectPathNavigator(this, level);
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

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
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
        while (position.getY() > 2 && level.isEmptyBlock(position)) {
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

    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    static class RandomFlyGoal extends Goal {
        private final EntityCosmaw parentEntity;
        private BlockPos target = null;

        public RandomFlyGoal(EntityCosmaw mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.parentEntity.getNavigation().isDone() && this.parentEntity.getRandom().nextInt(5) == 0) {
                target = getBlockInViewEndergrade();
                if(target != null){
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                    return true;
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null;
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 4D) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockGrounding(Vec3 fleePos) {
            float radius = 1 + parentEntity.getRandom().nextInt(10);
            float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.yBodyRot;
            float angle = (0.01745329251F * renderYawOffset) + 3.15F + (parentEntity.getRandom().nextFloat() * neg);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos(fleePos.x() + extraX, parentEntity.getY(), fleePos.z() + extraZ);
            BlockPos ground = parentEntity.getCosmawGround(radialPos);
            if (ground.getY() == 0) {
                return parentEntity.blockPosition();
            } else {
                ground = parentEntity.blockPosition();
                while (ground.getY() > 2 && parentEntity.level.isEmptyBlock(ground)) {
                    ground = ground.below();
                }
            }
            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
                return ground;
            }
            return null;
        }

        public BlockPos getBlockInViewEndergrade() {
            BlockPos pos = getBlockGrounding(parentEntity.position());
            return pos.above(parentEntity.random.nextInt(4));
        }
    }

}
