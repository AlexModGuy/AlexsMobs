package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class EntitySpectre extends AnimalEntity implements IFlyingAnimal {

    private static final DataParameter<Integer> CARDINAL_ORDINAL = EntityDataManager.createKey(EntitySpectre.class, DataSerializers.VARINT);
    public float birdPitch = 0;
    public float prevBirdPitch = 0;
    public Vector3d lurePos = null;

    protected EntitySpectre(EntityType type, World world) {
        super(type, world);
        this.moveController = new MoveHelperController(this);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 50.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 1F);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CARDINAL_ORDINAL, Integer.valueOf(Direction.NORTH.getIndex()));
    }

    public int getCardinalInt() {
        return this.dataManager.get(CARDINAL_ORDINAL).intValue();
    }

    public void setCardinalInt(int command) {
        this.dataManager.set(CARDINAL_ORDINAL, Integer.valueOf(command));
    }

    public Direction getCardinalDirection() {
        return Direction.byIndex(getCardinalInt());
    }

    public void setCardinalDirection(Direction dir) {
        setCardinalInt(dir.getIndex());
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TemptHeartGoal(this, 1.0D, Ingredient.fromItems(AMItemRegistry.SOUL_HEART), false));
        this.goalSelector.addGoal(2, new FlyGoal(this));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.isMagicDamage() && source != DamageSource.OUT_OF_WORLD || super.isInvulnerableTo(source);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.rotationPitch = 0.0F;
        this.randomizeDirection();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);

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

    public void tick() {
        super.tick();
        Vector3d vector3d1 = this.getMotion();
        this.rotationYaw = -((float) MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
        this.renderYawOffset = this.rotationYaw;

        prevBirdPitch = this.birdPitch;
        noClip = true;
        float f2 = (float) -((float) this.getMotion().y * 0.5F * (double) (180F / (float) Math.PI));
        this.birdPitch = f2;
        if (this.getLeashHolder() != null && !(this.getLeashHolder() instanceof LeashKnotEntity)) {
            Entity entity = this.getLeashHolder();
            float f = this.getDistance(entity);
            if (f > 10) {
                double d0 = (this.getPosX() - entity.getPosX()) / (double) f;
                double d1 = (this.getPosY() - entity.getPosY()) / (double) f;
                double d2 = (this.getPosZ() - entity.getPosZ()) / (double) f;
                entity.setMotion(entity.getMotion().add(Math.copySign(d0 * d0 * 0.4D, d0), Math.copySign(d1 * d1 * 0.4D, d1), Math.copySign(d2 * d2 * 0.4D, d2)));
            }
            entity.fallDistance = 0.0F;
            if (entity.getMotion().y < 0.0D) {
                entity.setMotion(entity.getMotion().mul(1, 0.7F, 1));
            }
            if (entity.isSneaking()) {
                this.clearLeashed(true, true);
            }
        }
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return null;
    }

    protected void updateLeashedState() {
        if (this.leashNBTTag != null) {
            this.recreateLeash();
        }

        if (this.getLeashHolder() != null) {
            if (!this.isAlive() || !this.getLeashHolder().isAlive()) {
                this.clearLeashed(true, true);
            }

        }
    }

    private void randomizeDirection() {
        this.setCardinalInt(2 + rand.nextInt(3));
    }

    static class MoveHelperController extends MovementController {
        private final EntitySpectre parentEntity;

        public MoveHelperController(EntitySpectre sunbird) {
            super(sunbird);
            this.parentEntity = sunbird;
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
                    double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
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

    private class FlyGoal extends Goal {
        private final EntitySpectre parentEntity;
        boolean island = false;
        float circlingTime = 0;
        float circleDistance = 14;
        float maxCirclingTime = 80;
        boolean clockwise = false;
        private BlockPos target = null;
        private int islandCheckTime = 20;

        public FlyGoal(EntitySpectre sunbird) {
            this.parentEntity = sunbird;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute() {
            if(parentEntity.lurePos != null){
                return false;
            }
            MovementController movementcontroller = this.parentEntity.getMoveHelper();
            clockwise = rand.nextBoolean();
            circleDistance = 5 + rand.nextInt(10);
            if (!movementcontroller.isUpdating() || target == null) {
                target = island ? getIslandPos(this.parentEntity.getPosition()) : getBlockFromDirection();
                if (target != null) {
                    this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean shouldContinueExecuting() {
            return parentEntity.lurePos == null;
        }

        public void resetTask() {
            island = false;
            islandCheckTime = 0;
            circleDistance = 5 + rand.nextInt(10);
            circlingTime = 0;
            clockwise = rand.nextBoolean();
            target = null;
        }

        public void tick() {
            if (islandCheckTime-- <= 0) {
                islandCheckTime = 20;
                if (circlingTime == 0) {
                    island = this.parentEntity.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, parentEntity.getPosition()).getY() > 2;
                    if (island) {
                        parentEntity.randomizeDirection();
                    }
                }
            }
            if (island) {
                circlingTime++;
                if (circlingTime > 100) {
                    island = false;
                    islandCheckTime = 1200;
                }
            } else if (circlingTime > 0) {
                circlingTime--;
            }
            if (target == null) {
                target = island ? getIslandPos(this.parentEntity.getPosition()) : getBlockFromDirection();
            }
            if (!island) {
                parentEntity.rotationYaw = parentEntity.getCardinalDirection().getHorizontalAngle();
            }
            if (target != null) {
                this.parentEntity.getMoveHelper().setMoveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (parentEntity.getDistanceSq(Vector3d.copyCentered(target)) < 5.5F) {
                    target = null;
                }
            }
        }

        public BlockPos getBlockFromDirection() {
            float radius = 15;
            BlockPos forwards = parentEntity.getPosition().offset(parentEntity.getCardinalDirection(), (int) Math.ceil(radius));
            int height = 0;
            if (world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, forwards).getY() < 15) {
                height = 70 + rand.nextInt(2);
            } else {
                height = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, forwards).getY() + 10 + rand.nextInt(10);
            }
            return new BlockPos(forwards.getX(), height, forwards.getZ());
        }

        public BlockPos getIslandPos(BlockPos orbit) {
            float angle = (0.01745329251F * 3 * (clockwise ? -circlingTime : circlingTime));
            double extraX = circleDistance * MathHelper.sin((angle));
            double extraZ = circleDistance * MathHelper.cos(angle);
            int height = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, orbit).getY();
            if (height < 3) {
                island = false;
                return getBlockFromDirection();
            }
            BlockPos pos = new BlockPos(orbit.getX() + extraX, Math.min(height + 10, orbit.getY() + rand.nextInt(3) - rand.nextInt(1)), orbit.getZ() + extraZ);
            return pos;
        }

    }

    class TemptHeartGoal extends Goal {
        private final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(64D).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();
        protected final EntitySpectre creature;
        private final double speed;
        protected PlayerEntity closestPlayer;
        private int delayTemptCounter;
        private final Ingredient temptItem;

        public TemptHeartGoal(EntitySpectre p_i47822_1_, double p_i47822_2_, Ingredient p_i47822_4_, boolean p_i47822_5_) {
            this(p_i47822_1_, p_i47822_2_, p_i47822_5_, p_i47822_4_);
        }

        public TemptHeartGoal(EntitySpectre p_i47823_1_, double p_i47823_2_, boolean p_i47823_4_, Ingredient p_i47823_5_) {
            this.creature = p_i47823_1_;
            this.speed = p_i47823_2_;
            this.temptItem = p_i47823_5_;
        }

        public boolean shouldExecute() {
            if (this.delayTemptCounter > 0) {
                --this.delayTemptCounter;
                return false;
            } else {
                this.closestPlayer = this.creature.world.getClosestPlayer(ENTITY_PREDICATE, this.creature);
                if (this.closestPlayer == null || this.creature.getLeashHolder() == closestPlayer) {
                    return false;
                } else {
                    return this.isTempting(this.closestPlayer.getHeldItemMainhand()) || this.isTempting(this.closestPlayer.getHeldItemOffhand());
                }
            }
        }

        protected boolean isTempting(ItemStack p_188508_1_) {
            return this.temptItem.test(p_188508_1_);
        }

        public boolean shouldContinueExecuting() {
            return this.shouldExecute();
        }

        public void startExecuting() {
            creature.lurePos = this.closestPlayer.getPositionVec();
        }

        public void resetTask() {
            this.closestPlayer = null;
            this.delayTemptCounter = 100;
            creature.lurePos = null;
        }

        public void tick() {
            this.creature.getLookController().setLookPositionWithEntity(this.closestPlayer, (float)(this.creature.getHorizontalFaceSpeed() + 20), (float)this.creature.getVerticalFaceSpeed());
            if (this.creature.getDistanceSq(this.closestPlayer) < 6.25D) {
                this.creature.getNavigator().clearPath();
            } else {
                this.creature.getMoveHelper().setMoveTo(this.closestPlayer.getPosX(), closestPlayer.getPosY() + closestPlayer.getEyeHeight(), closestPlayer.getPosZ(), this.speed);
            }

        }
    }

}
