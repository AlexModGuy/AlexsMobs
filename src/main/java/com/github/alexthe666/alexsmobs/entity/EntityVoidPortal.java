package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class EntityVoidPortal extends Entity {

    protected static final DataParameter<Direction> ATTACHED_FACE = EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.DIRECTION);
    protected static final DataParameter<Integer> LIFESPAN = EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<BlockPos>> DESTINATION = EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final DataParameter<Optional<UUID>> SISTER_UUID = EntityDataManager.createKey(EntityVoidWorm.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private boolean isDummy = false;

    public EntityVoidPortal(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public EntityVoidPortal(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.VOID_PORTAL, world);

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick(){
        super.tick();
        if(this.ticksExisted == 1 && this.getLifespan() == 0){
            this.setLifespan(100);
        }
        Direction direction2 = this.getAttachmentFacing().getOpposite();
        float minX = -0.15F;
        float minY = -0.15F;
        float minZ = -0.15F;
        float maxX = 0.15F;
        float maxY = 0.15F;
        float maxZ = 0.15F;
        switch (direction2){
            case NORTH:
            case SOUTH:
                minX = -1.5F;
                maxX = 1.5F;
                minY = -1.5F;
                maxY = 1.5F;
                break;
            case EAST:
            case WEST:
                minZ = -1.5F;
                maxZ = 1.5F;
                minY = -1.5F;
                maxY = 1.5F;
                break;
            case UP:
            case DOWN:
                minX = -1.5F;
                maxX = 1.5F;
                minZ = -1.5F;
                maxZ = 1.5F;
                break;
        }
        AxisAlignedBB bb = new AxisAlignedBB(this.getPosX() + minX, this.getPosY() + minY, this.getPosZ() + minZ, this.getPosX() + maxX, this.getPosY() + maxY, this.getPosZ() + maxZ);
        this.setBoundingBox(bb);
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, bb.shrink(0.2F));
        if(this.getDestination() != null && this.getLifespan() > 20 && ticksExisted > 20){
            for(Entity e : entities){
                if(e instanceof EntityVoidWormPart || e.hasPortalCooldown()){
                    if(this.getLifespan() < 22){
                        this.setLifespan(this.getLifespan() + 1);
                    }
                }else if(e instanceof EntityVoidWorm){
                    ((EntityVoidWorm) e).teleportTo(Vector3d.copyCentered(this.getDestination()));
                    e.setPortalCooldown();
                    ((EntityVoidWorm) e).resetPortalLogic();
                }else{
                    e.teleportKeepLoaded(this.getDestination().getX() + 0.5f, this.getDestination().getY() + 0.5f, this.getDestination().getZ() + 0.5f);
                    e.setPortalCooldown();
                }
            }
        }
        this.setLifespan(this.getLifespan() - 1);
        if(this.getLifespan() <= 0){
            this.remove();
        }
    }

    public Direction getAttachmentFacing() {
        return this.dataManager.get(ATTACHED_FACE);
    }

    public void setAttachmentFacing(Direction facing){
        this.dataManager.set(ATTACHED_FACE, facing);
    }

    public int getLifespan() {
        return this.dataManager.get(LIFESPAN);
    }

    public void setLifespan(int i){
        this.dataManager.set(LIFESPAN, i);
    }

    public BlockPos getDestination() {
        return this.dataManager.get(DESTINATION).orElse(null);
    }

    public void setDestination(BlockPos destination) {
        this.dataManager.set(DESTINATION, Optional.ofNullable(destination));
        if(this.getSisterId() == null) {
            EntityVoidPortal portal = AMEntityRegistry.VOID_PORTAL.create(world);
            portal.setAttachmentFacing(this.getAttachmentFacing().getOpposite());
            portal.teleportKeepLoaded(this.getDestination().getX() + 0.5f, this.getDestination().getY() + 0.5f, this.getDestination().getZ() + 0.5f);
            portal.link(this);
            world.addEntity(portal);
        }
    }

    public void link(EntityVoidPortal portal){
        this.setSisterId(portal.getUniqueID());
        portal.setSisterId(this.getUniqueID());
        portal.setLifespan(this.getLifespan());
        this.setDestination(portal.getPosition());
        portal.setDestination(this.getPosition());
    }

    @Override
    protected void registerData() {
        this.dataManager.register(ATTACHED_FACE, Direction.DOWN);
        this.dataManager.register(LIFESPAN, 300);
        this.dataManager.register(SISTER_UUID, Optional.empty());
        this.dataManager.register(DESTINATION, Optional.empty());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.dataManager.set(ATTACHED_FACE, Direction.byIndex(compound.getByte("AttachFace")));
        this.setLifespan(compound.getInt("Lifespan"));
        if (compound.contains("DX")) {
            int i = compound.getInt("DX");
            int j = compound.getInt("DY");
            int k = compound.getInt("DZ");
            this.dataManager.set(DESTINATION, Optional.of(new BlockPos(i, j, k)));
        } else {
            this.dataManager.set(DESTINATION, Optional.empty());
        }
        if (compound.hasUniqueId("SisterUUID")) {
            this.setSisterId(compound.getUniqueId("SisterUUID"));
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putByte("AttachFace", (byte)this.dataManager.get(ATTACHED_FACE).getIndex());
        compound.putInt("Lifespan", getLifespan());
        BlockPos blockpos = this.getDestination();
        if (blockpos != null) {
            compound.putInt("DX", blockpos.getX());
            compound.putInt("DY", blockpos.getY());
            compound.putInt("DZ", blockpos.getZ());
        }
        if (this.getSisterId() != null) {
            compound.putUniqueId("SisterUUID", this.getSisterId());
        }
    }

    public Entity getSister() {
        UUID id = getSisterId();
        if (id != null && !world.isRemote) {
            return ((ServerWorld) world).getEntityByUuid(id);
        }
        return null;
    }

    @Nullable
    public UUID getSisterId() {
        return this.dataManager.get(SISTER_UUID).orElse(null);
    }

    public void setSisterId(@Nullable UUID uniqueId) {
        this.dataManager.set(SISTER_UUID, Optional.ofNullable(uniqueId));
    }

}
