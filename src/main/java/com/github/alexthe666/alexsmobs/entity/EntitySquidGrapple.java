package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntitySquidGrapple extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EntitySquidGrapple.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntitySquidGrapple.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Optional<BlockPos>> ATTACHED_POS = SynchedEntityData.defineId(EntitySquidGrapple.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public EntitySquidGrapple(EntityType type, Level level) {
        super(type, level);
    }

    public EntitySquidGrapple(Level worldIn, LivingEntity player, boolean rightHand) {
        this(AMEntityRegistry.SQUID_GRAPPLE, worldIn);
        this.setOwnerId(player.getUUID());
        float rot = player.yHeadRot + (rightHand ? 60 : -60);
        this.setPos(player.getX() - (double) (player.getBbWidth()) * 0.5D * (double) Mth.sin(rot * ((float) Math.PI / 180F)), player.getEyeY() - (double) 0.2F, player.getZ() + (double) (player.getBbWidth()) * 0.5D * (double) Mth.cos(rot * ((float) Math.PI / 180F)));
    }

    public EntitySquidGrapple(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AMEntityRegistry.SQUID_GRAPPLE, level);
    }

    protected static float lerpRotation(float f2, float f3) {
        while (f3 - f2 < -180.0F) {
            f2 -= 360.0F;
        }

        while (f3 - f2 >= 180.0F) {
            f2 += 360.0F;
        }

        return Mth.lerp(0.2F, f2, f3);
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vector3d = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy, this.random.nextGaussian() * (double) 0.0075F * (double) inaccuracy).scale(velocity);
        this.setDeltaMovement(vector3d);
        float f = Mth.sqrt((float) (vector3d.x * vector3d.x + vector3d.z * vector3d.z));
        this.setYRot(Mth.wrapDegrees((float) (Mth.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI)) + 180));
        this.setXRot((float) (Mth.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public Direction getAttachmentFacing() {
        return this.entityData.get(ATTACHED_FACE);
    }

    public void setAttachmentFacing(Direction direction){
        this.entityData.set(ATTACHED_FACE, direction);
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerId(@Nullable UUID uniqueId) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uniqueId));
    }


    public BlockPos getStuckToPos() {
        return this.entityData.get(ATTACHED_POS).orElse(null);
    }

    public void setStuckToPos(BlockPos harvestedPos) {
        this.entityData.set(ATTACHED_POS, Optional.ofNullable(harvestedPos));
    }


    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_UUID, Optional.empty());
        this.entityData.define(ATTACHED_FACE, Direction.DOWN);
        this.entityData.define(ATTACHED_POS, Optional.empty());
    }

    public Entity getOwner() {
        UUID id = getOwnerId();
        if (id != null && !level.isClientSide) {
            return ((ServerLevel) level).getEntity(id);
        }
        return getOwnerId() == null ? null : level.getPlayerByUUID(getOwnerId());
    }


    public void tick() {
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        Entity entity = this.getOwner();
        if (entity == null || entity.isShiftKeyDown()) {
            this.discard();
        }

        if (this.level.isClientSide || this.level.hasChunkAt(this.blockPosition())) {
            if(this.getStuckToPos() == null){
                super.tick();
                Vec3 vector3d = this.getDeltaMovement();
                HitResult raytraceresult = ProjectileUtil.getHitResult(this, newentity -> false);
                if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
                    this.onImpact(raytraceresult);
                }
                this.checkInsideBlocks();
                double d0 = this.getX() + vector3d.x;
                double d1 = this.getY() + vector3d.y;
                double d2 = this.getZ() + vector3d.z;
                this.updateRotation();
                this.setDeltaMovement(vector3d.scale(0.99));
                if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir) && !this.isInWater()) {
                    this.setDeltaMovement(Vec3.ZERO);

                } else {
                    this.setPos(d0, d1, d2);
                }
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1F, 0.0D));
                }
            }else{
                BlockState state = this.level.getBlockState(this.getStuckToPos());
                Vec3 vec3 = new Vec3(this.getStuckToPos().getX() + 0.5F, this.getStuckToPos().getY() + 0.5F, this.getStuckToPos().getZ() + 0.5F);
                Vec3 offset = new Vec3(this.getAttachmentFacing().getStepX() * 0.55F, this.getAttachmentFacing().getStepY() * 0.55F, this.getAttachmentFacing().getStepZ() * 0.55F);
                this.setPos(vec3.add(offset));
                float targetX = this.getXRot();
                float targetY = this.getYRot();
                switch (this.getAttachmentFacing()){
                    case UP:
                        targetX = 0;
                        break;
                    case DOWN:
                        targetX = 180;
                        break;
                    case NORTH:
                        targetX = -90;
                        targetY = 0;
                        break;
                    case EAST:
                        targetX = -90;
                        targetY = 90;
                        break;
                    case SOUTH:
                        targetX = -90;
                        targetY = 180;
                        break;
                    case WEST:
                        targetX = -90;
                        targetY = -90;
                        break;
                }
                this.setXRot(targetX);
                this.setYRot(targetY);
                if(entity != null && entity.distanceTo(this) > 2){
                    Vec3 move = new Vec3(this.getX() - entity.getX(), this.getY() - (double)entity.getEyeHeight() / 2.0D - entity.getY(), this.getZ() - entity.getZ());
                    double d0 = move.lengthSqr();
                    entity.setDeltaMovement(entity.getDeltaMovement().add(move.normalize().scale(0.2D)));
                    if(!entity.isOnGround()){
                       entity.fallDistance = 0.0F;
                    }
                }
                if(state.isAir()){
                    this.discard();
                }
            }
        } else {
            discard();
        }

    }

    protected float rotlerp(float in, float target, float maxShift) {
        float f = Mth.wrapDegrees(target - in);
        if (f > maxShift) {
            f = maxShift;
        }

        if (f < -maxShift) {
            f = -maxShift;
        }

        float f1 = in + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }

    private void updateRotation() {
    }

    protected void onImpact(HitResult result) {
        HitResult.Type raytraceresult$type = result.getType();
        if (!level.isClientSide && raytraceresult$type == HitResult.Type.BLOCK && this.getStuckToPos() == null) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setStuckToPos(((BlockHitResult)result).getBlockPos());
            this.setAttachmentFacing(((BlockHitResult)result).getDirection());
        }

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (this.getOwnerId() != null) {
            compound.putUUID("OwnerUUID", this.getOwnerId());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("OwnerUUID")) {
            this.setOwnerId(compound.getUUID("OwnerUUID"));
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
