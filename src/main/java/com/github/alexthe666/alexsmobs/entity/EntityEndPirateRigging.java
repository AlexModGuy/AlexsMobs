package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityEndPirateRigging extends HangingEntity {
    private static final EntityDataAccessor<Optional<UUID>> CONNECTION_UUID = SynchedEntityData.defineId(EntityEndPirateRigging.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CONNECTION_ID = SynchedEntityData.defineId(EntityEndPirateRigging.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Direction> ATTACHMENT_ROTATION = SynchedEntityData.defineId(EntityEndPirateRigging.class, EntityDataSerializers.DIRECTION);
    private int ticksWithoutConnection = 0;

    public EntityEndPirateRigging(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public EntityEndPirateRigging(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.END_PIRATE_RIGGING.get(), world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityEndPirateRigging(Level level, BlockPos pos1) {
        super(AMEntityRegistry.END_PIRATE_RIGGING.get(), level, pos1);
        this.setPos((double) pos1.getX(), (double) pos1.getY(), (double) pos1.getZ());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACHMENT_ROTATION, Direction.UP);
        this.entityData.define(CONNECTION_UUID, Optional.empty());
        this.entityData.define(CONNECTION_ID, -1);
    }

    protected void recalculateBoundingBox() {
        this.setPosRaw((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.25D, (double) this.pos.getZ() + 0.5D);
        double d0 = (double) this.getType().getWidth() / 2.0D;
        double d1 = (double) this.getType().getHeight();
        this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + d1, this.getZ() + d0));
    }

    public Entity getConnection() {
        if (level.isClientSide) {
            int id = this.entityData.get(CONNECTION_ID);
            return id == -1 ? null : level.getEntity(id);
        } else {
            UUID connectionUUID = getConnectionUUID();
            return connectionUUID == null ? null : ((ServerLevel) level).getEntity(connectionUUID);
        }
    }

    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            Entity connection = getConnection();
            if (connection != null) {
                if (this.entityData.get(CONNECTION_ID) != connection.getId()) {
                    this.entityData.set(CONNECTION_ID, connection.getId());
                }
                if (connection.distanceTo(this) > 128 || !connection.isAlive()) {
                    ticksWithoutConnection++;
                    if (ticksWithoutConnection > 10) {
                        this.remove(RemovalReason.DISCARDED);
                        this.dropItem((Entity) null);
                    }
                } else {
                    ticksWithoutConnection = 0;
                }
            } else {
                this.entityData.set(CONNECTION_ID, -1);
            }
        }
    }

    @Nullable
    public UUID getConnectionUUID() {
        return this.entityData.get(CONNECTION_UUID).orElse(null);
    }

    public void setConnectionUUID(@Nullable UUID uniqueId) {
        this.entityData.set(CONNECTION_UUID, Optional.ofNullable(uniqueId));
    }


    public void setDirection(Direction direction) {
    }

    public int getWidth() {
        return 8;
    }

    public int getHeight() {
        return 8;
    }

    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5F;
    }

    public boolean shouldRenderAtSqrDistance(double dim) {
        return dim < 512 * 512;
    }

    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        if (this.getConnectionUUID() != null) {
            tag.putUUID("ConnectionUUID", this.getConnectionUUID());
        }
        tag.putInt("Direction", getAttachmentRotation().ordinal());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("ConnectionUUID")) {
            this.setConnectionUUID(tag.getUUID("ConnectionUUID"));
        }
        int dirOrd = tag.getInt("Direction");
        this.setDirection(Direction.values()[Mth.clamp(dirOrd, 0, Direction.values().length - 1)]);
    }


    public void setAttachmentRotation(Direction direction) {
        this.entityData.set(ATTACHMENT_ROTATION, direction);
    }

    public Direction getAttachmentRotation() {
        return this.entityData.get(ATTACHMENT_ROTATION);
    }

    public boolean survives() {
        BlockState state = this.level.getBlockState(this.pos);
        if (state.getBlock() instanceof DirectionalBlock) {
            setAttachmentRotation(state.getValue(DirectionalBlock.FACING));
        }
        return state.is(Blocks.END_ROD);
    }

    public void playPlacementSound() {
        this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
    }

    public Vec3 getRopeHoldPosition(float partialTick) {
        return this.getPosition(partialTick).add(0.0D, 0.2D, 0.0D);
    }

    public ItemStack getPickResult() {
        return new ItemStack(AMItemRegistry.END_PIRATE_RIGGING.get());
    }
}