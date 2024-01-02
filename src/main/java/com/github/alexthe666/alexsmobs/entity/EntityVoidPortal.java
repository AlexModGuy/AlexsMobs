package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.antlr.v4.runtime.misc.Triple;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityVoidPortal extends Entity {

    protected static final EntityDataAccessor<Direction> ATTACHED_FACE = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.DIRECTION);
    protected static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> SHATTERED = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> DESTINATION = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<UUID>> SISTER_UUID = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.OPTIONAL_UUID);
    public ResourceKey<Level> exitDimension;
    private boolean madeOpenNoise = false;
    private boolean madeCloseNoise = false;
    private boolean isDummy = false;
    private boolean hasClearedObstructions;


    public EntityVoidPortal(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public EntityVoidPortal(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(AMEntityRegistry.VOID_PORTAL.get(), world);
    }

    public EntityVoidPortal(Level world, ItemDimensionalCarver item) {
        this(AMEntityRegistry.VOID_PORTAL.get(), world);
        if(item == AMItemRegistry.SHATTERED_DIMENSIONAL_CARVER.get()){
            this.setShattered(true);
            this.setLifespan(2000);
        }else{
            this.setShattered(false);
            this.setLifespan(1200);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        if (this.tickCount == 1) {
            if(this.getLifespan() == 0){
                this.setLifespan(2000);
            }
        }
        if(!madeOpenNoise){
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.playSound(AMSoundRegistry.VOID_PORTAL_OPEN.get(), 1.0F, 1 + random.nextFloat() * 0.2F);
            madeOpenNoise = true;
        }
        Direction direction2 = this.getAttachmentFacing().getOpposite();
        float minX = -0.15F;
        float minY = -0.15F;
        float minZ = -0.15F;
        float maxX = 0.15F;
        float maxY = 0.15F;
        float maxZ = 0.15F;
        switch (direction2) {
            case NORTH, SOUTH -> {
                minX = -1.5F;
                maxX = 1.5F;
                minY = -1.5F;
                maxY = 1.5F;
            }
            case EAST, WEST -> {
                minZ = -1.5F;
                maxZ = 1.5F;
                minY = -1.5F;
                maxY = 1.5F;
            }
            case UP, DOWN -> {
                minX = -1.5F;
                maxX = 1.5F;
                minZ = -1.5F;
                maxZ = 1.5F;
            }
        }
        AABB bb = new AABB(this.getX() + minX, this.getY() + minY, this.getZ() + minZ, this.getX() + maxX, this.getY() + maxY, this.getZ() + maxZ);
        this.setBoundingBox(bb);
        if(this.level().isClientSide && random.nextFloat() < 0.5F && Math.min(tickCount, this.getLifespan()) >= 20){
            final double particleX = this.getBoundingBox().minX + random.nextFloat() * (this.getBoundingBox().maxX - this.getBoundingBox().minX);
            final double particleY = this.getBoundingBox().minY + random.nextFloat() * (this.getBoundingBox().maxY - this.getBoundingBox().minY);
            final double particleZ = this.getBoundingBox().minZ + random.nextFloat() * (this.getBoundingBox().maxZ - this.getBoundingBox().minZ);
            level().addParticle(AMParticleRegistry.WORM_PORTAL.get(), particleX, particleY, particleZ, 0.1 * random.nextGaussian(), 0.1 * random.nextGaussian(), 0.1 * random.nextGaussian());
        }
        List<Entity> entities = new ArrayList<>();
        entities.addAll(this.level().getEntities(this, bb.deflate(0.2F)));
        entities.addAll(this.level().getEntitiesOfClass(EntityVoidWorm.class, bb.inflate(1.5F)));
        if (!this.level().isClientSide) {
            MinecraftServer server = level().getServer();
            if (this.getDestination() != null && this.getLifespan() > 20 && tickCount > 20) {
                BlockPos offsetPos = this.getDestination().relative(this.getAttachmentFacing().getOpposite(), 2);
                for (Entity e : entities) {
                    if(e.isOnPortalCooldown() || e.isShiftKeyDown() || e instanceof EntityVoidPortal || e.getParts() != null || e instanceof PartEntity<?> || e.getType().is(AMTagRegistry.VOID_PORTAL_IGNORES)){
                        continue;
                    }
                    if (e instanceof EntityVoidWormPart) {
                        if (this.getLifespan() < 22) {
                            this.setLifespan(this.getLifespan() + 1);
                        }
                    } else if (e instanceof EntityVoidWorm) {
                        ((EntityVoidWorm) e).teleportTo(Vec3.atCenterOf(this.getDestination()));
                        e.setPortalCooldown();
                        ((EntityVoidWorm) e).resetPortalLogic();
                    } else {
                        boolean flag = true;
                        if(exitDimension != null){
                            ServerLevel dimWorld = server.getLevel(exitDimension);
                            if (dimWorld != null && this.level().dimension() != exitDimension) {
                                teleportEntityFromDimension(e, dimWorld, offsetPos, true);
                                flag = false;
                            }
                        }
                        if(flag){
                            e.teleportToWithTicket(offsetPos.getX() + 0.5f, offsetPos.getY() + 0.5f, offsetPos.getZ() + 0.5f);
                            e.setPortalCooldown();
                        }
                    }
                }
            }
        }
        this.setLifespan(this.getLifespan() - 1);
        if(this.getLifespan() <= 20){
            if(!madeCloseNoise){
                this.gameEvent(GameEvent.ENTITY_PLACE);
                this.playSound(AMSoundRegistry.VOID_PORTAL_CLOSE.get(), 1.0F, 1 + random.nextFloat() * 0.2F);
                madeCloseNoise = true;
            }
        }
        if (this.getLifespan() <= 0) {
            this.remove(RemovalReason.DISCARDED);
        }
        if(tickCount > 1){
            clearObstructions();
        }
    }

    private void teleportEntityFromDimension(Entity entity, ServerLevel endpointWorld, BlockPos endpoint, boolean b) {
        if (entity instanceof ServerPlayer) {
            ServerEvents.teleportPlayers.add(new Triple<>((ServerPlayer)entity, endpointWorld, endpoint));
            if(this.getSisterId() == null){
                createAndSetSister(endpointWorld, Direction.DOWN);
            }
        } else {
            entity.unRide();
            entity.setLevel(endpointWorld);
            Entity teleportedEntity = entity.getType().create(endpointWorld);
            if (teleportedEntity != null) {
                teleportedEntity.restoreFrom(entity);
                teleportedEntity.moveTo(endpoint.getX() + 0.5D, endpoint.getY() + 0.5D, endpoint.getZ() + 0.5D, entity.getYRot(), entity.getXRot());
                teleportedEntity.setYHeadRot(entity.getYHeadRot());
                teleportedEntity.setPortalCooldown();
                endpointWorld.addFreshEntity(teleportedEntity);
            }
            entity.remove(RemovalReason.DISCARDED);
        }
    }

    public void clearObstructions(){
        if(!hasClearedObstructions){
            if(isShattered() && this.getDestination() != null){
                hasClearedObstructions = true;
                for (int i = -1; i <= -1; i++){
                    for (int j = -1; j <= -1; j++){
                        for (int k = -1; k <= -1; k++){
                            BlockPos toAir = this.getDestination().offset(i, j, k);
                            level().destroyBlock(toAir, true);
                        }
                    }
                }
            }
        }
    }

    public Direction getAttachmentFacing() {
        return this.entityData.get(ATTACHED_FACE);
    }

    public void setAttachmentFacing(Direction facing) {
        this.entityData.set(ATTACHED_FACE, facing);
    }

    public int getLifespan() {
        return this.entityData.get(LIFESPAN);
    }

    public void setLifespan(int i) {
        this.entityData.set(LIFESPAN, i);
    }

    public boolean isShattered() {
        return this.entityData.get(SHATTERED);
    }

    public void setShattered(boolean set) {
        this.entityData.set(SHATTERED, set);
    }

    public BlockPos getDestination() {
        return this.entityData.get(DESTINATION).orElse(null);
    }

    public void setDestination(BlockPos destination) {
        this.entityData.set(DESTINATION, Optional.ofNullable(destination));
        if (this.getSisterId() == null && (exitDimension == null || exitDimension == this.level().dimension())) {
            createAndSetSister(level(), null);
        }
    }

    public void createAndSetSister(Level world, Direction dir){
        EntityVoidPortal portal = AMEntityRegistry.VOID_PORTAL.get().create(world);
        portal.setAttachmentFacing(dir != null ? dir : this.getAttachmentFacing().getOpposite());
        BlockPos safeDestination = this.getDestination();
        portal.teleportToWithTicket(safeDestination.getX() + 0.5f, safeDestination.getY() + 0.5f, safeDestination.getZ() + 0.5f);
        portal.link(this);
        portal.exitDimension = this.level().dimension();
        world.addFreshEntity(portal);
        portal.setShattered(this.isShattered());
    }

    public void setDestination(BlockPos destination, Direction dir) {
        this.entityData.set(DESTINATION, Optional.ofNullable(destination));
        if (this.getSisterId() == null && (exitDimension == null || exitDimension == this.level().dimension())) {
            createAndSetSister(level(), dir);
        }
    }

    public void link(EntityVoidPortal portal) {
        this.setSisterId(portal.getUUID());
        portal.setSisterId(this.getUUID());
        portal.setLifespan(this.getLifespan());
        this.setDestination(portal.blockPosition());
        portal.setDestination(this.blockPosition());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACHED_FACE, Direction.DOWN);
        this.entityData.define(LIFESPAN, 300);
        this.entityData.define(SHATTERED, false);
        this.entityData.define(SISTER_UUID, Optional.empty());
        this.entityData.define(DESTINATION, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(ATTACHED_FACE, Direction.from3DDataValue(compound.getByte("AttachFace")));
        this.setLifespan(compound.getInt("Lifespan"));
        if(compound.contains("Shattered")){
            this.setShattered(compound.getBoolean("Shattered"));
        }
        if (compound.contains("DX")) {
            final int i = compound.getInt("DX");
            final int j = compound.getInt("DY");
            final int k = compound.getInt("DZ");
            this.entityData.set(DESTINATION, Optional.of(new BlockPos(i, j, k)));
        } else {
            this.entityData.set(DESTINATION, Optional.empty());
        }
        if (compound.hasUUID("SisterUUID")) {
            this.setSisterId(compound.getUUID("SisterUUID"));
        }
        if (compound.contains("ExitDimension")) {
            this.exitDimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, compound.get("ExitDimension")).resultOrPartial(AlexsMobs.LOGGER::error).orElse(Level.OVERWORLD);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putByte("AttachFace", (byte) this.entityData.get(ATTACHED_FACE).get3DDataValue());
        compound.putInt("Lifespan", getLifespan());
        compound.putBoolean("Shattered", isShattered());
        BlockPos blockpos = this.getDestination();
        if (blockpos != null) {
            compound.putInt("DX", blockpos.getX());
            compound.putInt("DY", blockpos.getY());
            compound.putInt("DZ", blockpos.getZ());
        }
        if (this.getSisterId() != null) {
            compound.putUUID("SisterUUID", this.getSisterId());
        }
        if(this.exitDimension != null){
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.exitDimension.location()).resultOrPartial(AlexsMobs.LOGGER::error).ifPresent((p_241148_1_) -> {
                compound.put("ExitDimension", p_241148_1_);
            });
        }

    }

    public Entity getSister() {
        UUID id = getSisterId();
        if (id != null && !this.level().isClientSide) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

    @Nullable
    public UUID getSisterId() {
        return this.entityData.get(SISTER_UUID).orElse(null);
    }

    public void setSisterId(@Nullable UUID uniqueId) {
        this.entityData.set(SISTER_UUID, Optional.ofNullable(uniqueId));
    }

}
