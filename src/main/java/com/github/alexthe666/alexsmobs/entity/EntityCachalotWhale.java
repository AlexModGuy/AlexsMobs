package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.entity.ai.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class EntityCachalotWhale extends AnimalEntity {

    public final double[][] ringBuffer = new double[64][3];
    public final EntityCachalotPart headPart;
    public final EntityCachalotPart bodyFrontPart;
    public final EntityCachalotPart bodyPart;
    public final EntityCachalotPart tail1Part;
    public final EntityCachalotPart tail2Part;
    public final EntityCachalotPart tail3Part;
    public final EntityCachalotPart[] whaleParts;
    public int ringBufferIndex = -1;

    protected EntityCachalotWhale(EntityType type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
        this.moveController = new MoveHelperController(this);
        this.lookController = new DolphinLookController(this, 10);
        this.headPart = new EntityCachalotPart(this, 3.0F, 3.5F);
        this.bodyFrontPart = new EntityCachalotPart(this, 4.0F, 4.0F);
        this.bodyPart = new EntityCachalotPart(this, 5.0F, 4.0F);
        this.tail1Part = new EntityCachalotPart(this, 4.0F, 3.0F);
        this.tail2Part = new EntityCachalotPart(this, 3.0F, 2.0F);
        this.tail3Part = new EntityCachalotPart(this, 3.0F, 0.7F);
        this.whaleParts = new EntityCachalotPart[]{this.headPart, this.bodyFrontPart, this.bodyPart, this.tail1Part, this.tail2Part, this.tail3Part};
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 160.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 1.2F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 30F);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public void collideWithNearbyEntities() {
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreatheAirGoal(this));
        this.goalSelector.addGoal(1, new FindWaterGoal(this));
        this.goalSelector.addGoal(4, new AnimalAIRandomSwimming(this, 1.0D, 10, 24));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new SwimmerPathNavigator(this, worldIn);
    }

    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));

        } else {
            super.travel(travelVector);
        }

    }

    public void livingTick() {
        this.rotationYaw = (this.prevRotationYaw + MathHelper.clamp(this.rotationYaw - prevRotationYaw, -2, 2));
        super.livingTick();
        this.renderYawOffset = this.rotationYaw;
        this.rotationPitch = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
        if (!this.isAIDisabled()) {
            if (this.ringBufferIndex < 0) {
                for (int i = 0; i < this.ringBuffer.length; ++i) {
                    this.ringBuffer[i][0] = this.rotationYaw;
                    this.ringBuffer[i][1] = this.getPosY();
                }
            }
            this.ringBufferIndex++;
            if (this.ringBufferIndex == this.ringBuffer.length) {
                this.ringBufferIndex = 0;
            }
            this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
            this.ringBuffer[ringBufferIndex][1] = this.getPosY();
            Vector3d[] avector3d = new Vector3d[this.whaleParts.length];

            for (int j = 0; j < this.whaleParts.length; ++j) {
                this.whaleParts[j].collideWithNearbyEntities();
                avector3d[j] = new Vector3d(this.whaleParts[j].getPosX(), this.whaleParts[j].getPosY(), this.whaleParts[j].getPosZ());
            }
            float f4 = MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F) - 0 * 0.01F);
            float f19 = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F) - 0 * 0.01F);
            float f15 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * ((float) Math.PI / 180F);
            float f16 = MathHelper.cos(f15);
            float f2 = MathHelper.sin(f15);
            float f17 = this.rotationYaw * ((float) Math.PI / 180F);
            float pitch = this.rotationPitch * ((float) Math.PI / 180F);
            float f3 = MathHelper.sin(f17) * (1 - Math.abs(this.rotationPitch / 90F));
            float f18 = MathHelper.cos(f17) * (1 - Math.abs(this.rotationPitch / 90F));

            this.setPartPosition(this.bodyPart, f3 * 0.5F, -pitch * 0.5F, -f18 * 0.5F);
            this.setPartPosition(this.bodyFrontPart, (f3) * -3.5F, -pitch * 3F, (f18) * 3.5F);
            this.setPartPosition(this.headPart, f3 * -7F, -pitch * 5F, -f18 * -7F);
            double[] adouble = this.getMovementOffsets(5, 1.0F);

            for (int k = 0; k < 3; ++k) {
                EntityCachalotPart enderdragonpartentity = null;
                if (k == 0) {
                    enderdragonpartentity = this.tail1Part;
                }
                if (k == 1) {
                    enderdragonpartentity = this.tail2Part;
                }
                if (k == 2) {
                    enderdragonpartentity = this.tail3Part;
                }

                double[] adouble1 = this.getMovementOffsets(15 + k * 5, 1.0F);
                float f7 = this.rotationYaw * ((float) Math.PI / 180F) + (float) MathHelper.wrapDegrees(adouble1[0] - adouble[0]) * ((float) Math.PI / 180F);
                float f20 = MathHelper.sin(f7) * (1 - Math.abs(this.rotationPitch / 90F));
                float f21 = MathHelper.cos(f7) * (1 - Math.abs(this.rotationPitch / 90F));
                float f22 = k == 2 ? -3.6F : -3.6F;
                float f23 = (float) (k + 1) * f22 - 2F;
                this.setPartPosition(enderdragonpartentity, -(f3 * 0.5F + f20 * f23) * f16, pitch * 1.5F * (k + 1), (f18 * 0.5F + f21 * f23) * f16);
            }

            for (int l = 0; l < this.whaleParts.length; ++l) {
                this.whaleParts[l].prevPosX = avector3d[l].x;
                this.whaleParts[l].prevPosY = avector3d[l].y;
                this.whaleParts[l].prevPosZ = avector3d[l].z;
                this.whaleParts[l].lastTickPosX = avector3d[l].x;
                this.whaleParts[l].lastTickPosY = avector3d[l].y;
                this.whaleParts[l].lastTickPosZ = avector3d[l].z;
            }
        }
    }

    private float getHeadAndNeckYOffset() {
        double[] adouble = this.getMovementOffsets(5, 1.0F);
        double[] adouble1 = this.getMovementOffsets(0, 1.0F);
        return (float) (adouble[1] - adouble1[1]);
    }

    public double[] getMovementOffsets(int p_70974_1_, float partialTicks) {
        if (this.getShouldBeDead()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        int i = this.ringBufferIndex - p_70974_1_ & 63;
        int j = this.ringBufferIndex - p_70974_1_ - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.ringBuffer[i][0];
        double d1 = this.ringBuffer[j][0] - d0;
        adouble[0] = d0 + d1 * (double) partialTicks;
        d0 = this.ringBuffer[i][1];
        d1 = this.ringBuffer[j][1] - d0;
        adouble[1] = d0 + d1 * (double) partialTicks;
        adouble[2] = MathHelper.lerp(partialTicks, this.ringBuffer[i][2], this.ringBuffer[j][2]);
        return adouble;
    }


    public void applyEntityCollision(Entity entityIn) {

    }

    protected void doBlockCollisions() {
    /*
            for (EntityCachalotPart part : this.whaleParts) {
            AxisAlignedBB axisalignedbb = part.getBoundingBox();
            BlockPos blockpos = new BlockPos(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
            BlockPos blockpos1 = new BlockPos(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            if (this.world.isAreaLoaded(blockpos, blockpos1)) {
                for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                    for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                        for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                            blockpos$mutable.setPos(i, j, k);
                            BlockState blockstate = this.world.getBlockState(blockpos$mutable);

                            try {
                                blockstate.onEntityCollision(this.world, blockpos$mutable, this);
                                this.onInsideBlock(blockstate);
                            } catch (Throwable throwable) {
                                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                                CrashReportCategory.addBlockInfo(crashreportcategory, blockpos$mutable, blockstate);
                                throw new ReportedException(crashreport);
                            }
                        }
                    }
                }
            }
        }

     */
        super.doBlockCollisions();
    }


    private void setPartPosition(EntityCachalotPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPosition(this.getPosX() + offsetX, this.getPosY() + offsetY, this.getPosZ() + offsetZ);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.whaleParts;
    }

    @Nullable
    @Override
    public AgeableEntity func_241840_a(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        return null;
    }

    public boolean attackEntityPartFrom(EntityCachalotPart entityCachalotPart, DamageSource source, float amount) {
        return this.attackEntityFrom(source, amount);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason
            reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setAir(this.getMaxAir());
        this.rotationPitch = 0.0F;
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    public void baseTick() {
        int i = this.getAir();
        super.baseTick();
        this.updateAir(i);
    }

    public boolean isPushedByWater() {
        return false;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    protected void updateAir(int p_209207_1_) {
    }

    public int getMaxAir() {
        return 4800;
    }

    protected int determineNextAir(int currentAir) {
        return this.getMaxAir();
    }

    public int getVerticalFaceSpeed() {
        return 1;
    }

    public int getHorizontalFaceSpeed() {
        return 1;
    }


    static class MoveHelperController extends MovementController {
        private final EntityCachalotWhale dolphin;

        public MoveHelperController(EntityCachalotWhale dolphinIn) {
            super(dolphinIn);
            this.dolphin = dolphinIn;
        }

        public void tick() {
            if (this.action == Action.MOVE_TO && !dolphin.getNavigator().noPath()) {
                double lvt_1_1_ = this.posX - dolphin.getPosX();
                double lvt_3_1_ = this.posY - dolphin.getPosY();
                double lvt_5_1_ = this.posZ - dolphin.getPosZ();
                double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;

                    float lvt_9_1_ = (float) (MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
                    dolphin.rotationYaw = this.limitAngle(dolphin.rotationYaw, lvt_9_1_, 10.0F);
                    float lvt_10_1_ = (float) (this.speed * 2 * dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    if (dolphin.isInWater()) {
                        dolphin.setAIMoveSpeed(lvt_10_1_ * 0.02F);
                        float lvt_11_1_ = -((float) (MathHelper.atan2(lvt_3_1_, MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_)) * 57.2957763671875D));
                        dolphin.setMotion(dolphin.getMotion().add(0.0D, (double) dolphin.getAIMoveSpeed() * lvt_3_1_ * 0.6D, 0.0D));
                        lvt_11_1_ = MathHelper.clamp(MathHelper.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                        dolphin.rotationPitch = this.limitAngle(dolphin.rotationPitch, lvt_11_1_, 5.0F);
                        float lvt_12_1_ = MathHelper.cos(dolphin.rotationPitch * 0.017453292F);
                        float lvt_13_1_ = MathHelper.sin(dolphin.rotationPitch * 0.017453292F);
                        dolphin.moveForward = lvt_12_1_ * lvt_10_1_;
                        dolphin.moveVertical = -lvt_13_1_ * lvt_10_1_;
                    } else {
                        dolphin.setAIMoveSpeed(lvt_10_1_ * 0.1F);
                    }

                }
        }
    }

}
