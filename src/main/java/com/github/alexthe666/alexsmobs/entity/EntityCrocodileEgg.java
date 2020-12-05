package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityCrocodileEgg extends ProjectileItemEntity {

    public EntityCrocodileEgg(EntityType p_i50154_1_, World p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public EntityCrocodileEgg(World worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.CROCODILE_EGG, throwerIn, worldIn);
    }

    public EntityCrocodileEgg(World worldIn, double x, double y, double z) {
        super(AMEntityRegistry.CROCODILE_EGG, x, y, z, worldIn);
    }

    public EntityCrocodileEgg(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.CROCODILE_EGG, world);
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

    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)3);
            this.remove();
            if (this.rand.nextInt(16) == 0) {
                int i = 1;
                if (this.rand.nextInt(32) == 0) {
                    i = 4;
                }

                for(int j = 0; j < i; ++j) {
                    EntityCrocodile croc = AMEntityRegistry.CROCODILE.create(this.world);
                    croc.setGrowingAge(-24000);
                    croc.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0F);
                    if(func_234616_v_() instanceof PlayerEntity){
                        croc.setTamedBy((PlayerEntity)func_234616_v_());
                    }
                    croc.onInitialSpawn((ServerWorld)world, world.getDifficultyForLocation(this.getPosition()), SpawnReason.TRIGGERED, (ILivingEntityData)null, (CompoundNBT)null);
                    croc.setHomePosAndDistance(this.getPosition(), 20);
                    this.world.addEntity(croc);
                }
            }

            this.world.setEntityState(this, (byte)3);
            this.remove();
        }

    }

    protected Item getDefaultItem() {
        return AMItemRegistry.CROCODILE_EGG;
    }
}
