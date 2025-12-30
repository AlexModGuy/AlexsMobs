package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.message.MessageInteractMultipart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import javax.annotation.Nullable;

public class EntityLaviathanPart extends PartEntity<EntityLaviathan> {

    private final EntityDimensions size;
    public float scale = 1;

    public EntityLaviathanPart(EntityLaviathan parent, float sizeX, float sizeY) {
        super(parent);
        this.size = EntityDimensions.scalable(sizeX, sizeY);
        this.refreshDimensions();
    }

    public EntityLaviathanPart(EntityLaviathan entityCachalotWhale, float sizeX, float sizeY, EntityDimensions size) {
        super(entityCachalotWhale);
        this.size = size;
    }

    public boolean fireImmune() {
        return true;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, (double)this.getEyeHeight() * 0.15F, (double)(this.getBbWidth() * 0.1F));
    }

    protected void collideWithNearbyEntities() {

    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if(this.level().isClientSide && this.getParent() != null){
            AlexsMobs.sendMSGToServer(new MessageInteractMultipart(this.getParent().getId(), hand == InteractionHand.OFF_HAND));
        }
        return this.getParent() == null ? InteractionResult.PASS : this.getParent().mobInteract(player, hand);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    protected void collideWithEntity(Entity entityIn) {
        if(!(entityIn instanceof EntityLaviathan)){
            entityIn.push(this);
        }
    }

    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        Entity parent = this.getParent();
        return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
    }

    public boolean hurt(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.getParent().attackEntityPartFrom(this, source, amount);
    }

    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public EntityDimensions getDefaultDimensions(Pose poseIn) {
        return this.size == null ? EntityDimensions.scalable(0, 0) : this.size.scale(scale);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

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
