package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;


public class EntityFocalPoint extends Entity {

    public EntityFocalPoint(EntityType p_i50162_1_, World p_i50162_2_) {
        super(p_i50162_1_, p_i50162_2_);
    }

    public EntityFocalPoint(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.FOCAL_POINT, world);
    }

    public void updateRidden() {
        this.setMotion(Vector3d.ZERO);
        if (canUpdate())
            this.tick();
        if (this.isPassenger()) {
            if(this.getRidingEntity() instanceof PlayerEntity){
                PlayerEntity player = (PlayerEntity)this.getRidingEntity();
                if(!player.isPotionActive(AMEffectRegistry.CLINGING)){
                    this.remove();
                }
            }
            this.setPosition(this.getRidingEntity().getPosX(), this.getRidingEntity().getPosY() + 0.15F, this.getRidingEntity().getPosZ());
        }
    }


    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
