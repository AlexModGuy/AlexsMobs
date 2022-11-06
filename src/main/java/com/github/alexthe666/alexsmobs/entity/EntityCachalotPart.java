package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.message.MessageInteractMultipart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.entity.PartEntity;

import java.util.List;

public class EntityCachalotPart extends PartEntity<EntityCachalotWhale> {

    private final EntityDimensions size;
    public float scale = 1;

    public EntityCachalotPart(EntityCachalotWhale parent, float sizeX, float sizeY) {
        super(parent);
        this.size = EntityDimensions.scalable(sizeX, sizeY);
        this.refreshDimensions();
    }

    public EntityCachalotPart(EntityCachalotWhale entityCachalotWhale, float sizeX, float sizeY, EntityDimensions size) {
        super(entityCachalotWhale);
        this.size = size;
    }

    protected void collideWithNearbyEntities() {
        final List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityCachalotPart && ((EntityCachalotPart) entity).getParent() == parent) && entity.isPushable()).forEach(entity -> entity.push(parent));
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if(level.isClientSide && this.getParent() != null){
            AlexsMobs.sendMSGToServer(new MessageInteractMultipart(this.getParent().getId(), hand == InteractionHand.OFF_HAND));
        }
        return this.getParent() == null ? InteractionResult.PASS : this.getParent().mobInteract(player, hand);
    }


    protected void collideWithEntity(Entity entityIn) {
        entityIn.push(this);
    }

    public boolean isPickable() {
        return true;
    }

    public boolean hurt(DamageSource source, float amount) {
        if(level.isClientSide && this.getParent() != null && !this.getParent().isInvulnerableTo(source)){
            AlexsMobs.sendMSGToServer(new MessageHurtMultipart(this.getId(), this.getParent().getId(), amount, source.msgId));
        }
        return !this.isInvulnerableTo(source) && this.getParent().attackEntityPartFrom(this, source, amount);
    }

    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    public Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return this.size.scale(scale);
    }

    @Override
    protected void defineSynchedData() {

    }

    public void tick(){
        super.tick();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }
}
