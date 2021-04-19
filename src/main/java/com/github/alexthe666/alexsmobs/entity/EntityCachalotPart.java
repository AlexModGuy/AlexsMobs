package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.entity.PartEntity;

import java.util.List;

public class EntityCachalotPart extends PartEntity<EntityCachalotWhale> {

    private final EntitySize size;
    public float scale = 1;

    public EntityCachalotPart(EntityCachalotWhale parent, float sizeX, float sizeY) {
        super(parent);
        this.size = EntitySize.flexible(sizeX, sizeY);
        this.recalculateSize();
    }

    public EntityCachalotPart(EntityCachalotWhale entityCachalotWhale, float sizeX, float sizeY, EntitySize size) {
        super(entityCachalotWhale);
        this.size = size;
    }

    protected void collideWithNearbyEntities() {
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityCachalotPart && ((EntityCachalotPart) entity).getParent() == parent) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(parent));

        }
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        return this.getParent() == null ? ActionResultType.PASS : this.getParent().processInitialInteract(player, hand);
    }

    protected void collideWithEntity(Entity entityIn) {
        entityIn.applyEntityCollision(this);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.getParent().attackEntityPartFrom(this, source, amount);
    }

    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    public IPacket<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }

    public EntitySize getSize(Pose poseIn) {
        return this.size.scale(scale);
    }

    @Override
    protected void registerData() {

    }

    public void tick(){
        super.tick();
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }
}
