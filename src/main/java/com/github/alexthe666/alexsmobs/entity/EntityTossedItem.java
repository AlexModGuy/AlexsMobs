package com.github.alexthe666.alexsmobs.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityTossedItem extends ProjectileItemEntity {

    public EntityTossedItem(EntityType p_i50154_1_, World p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public EntityTossedItem(World worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.TOSSED_ITEM, throwerIn, worldIn);
    }

    public EntityTossedItem(World worldIn, double x, double y, double z) {
        super(AMEntityRegistry.TOSSED_ITEM, x, y, z, worldIn);
    }

    public EntityTossedItem(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.TOSSED_ITEM, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            double d0 = 0.08D;

            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getPosX(), this.getPosY(), this.getPosZ(), ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }

    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        if(this.func_234616_v_() instanceof EntityCapuchinMonkey){
            EntityCapuchinMonkey boss = (EntityCapuchinMonkey) this.func_234616_v_();
            if(!boss.isOnSameTeam(p_213868_1_.getEntity()) || !boss.isTamed() && !(p_213868_1_.getEntity() instanceof EntityCapuchinMonkey)){
                p_213868_1_.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, boss), 5.0F);
            }
        }
    }


    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)3);
            this.remove();
        }

    }

    protected Item getDefaultItem() {
        return Items.COBBLESTONE;
    }
}
