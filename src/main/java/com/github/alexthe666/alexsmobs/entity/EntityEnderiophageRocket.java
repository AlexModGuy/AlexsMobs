package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class EntityEnderiophageRocket extends FireworkRocketEntity {

    private int phageAge = 0;

    public EntityEnderiophageRocket(EntityType p_i50164_1_, World p_i50164_2_) {
        super(p_i50164_1_, p_i50164_2_);
    }

    public EntityEnderiophageRocket(World worldIn, double x, double y, double z, ItemStack givenItem) {
        super(AMEntityRegistry.ENDERIOPHAGE_ROCKET, worldIn);
        this.setPosition(x, y, z);
        if (!givenItem.isEmpty() && givenItem.hasTag()) {
            this.dataManager.set(FIREWORK_ITEM, givenItem.copy());
        }

        this.setMotion(this.rand.nextGaussian() * 0.001D, 0.05D, this.rand.nextGaussian() * 0.001D);
        this.lifetime = 18 + this.rand.nextInt(14);
    }

    public EntityEnderiophageRocket(World p_i231581_1_, @Nullable Entity p_i231581_2_, double p_i231581_3_, double p_i231581_5_, double p_i231581_7_, ItemStack p_i231581_9_) {
        this(p_i231581_1_, p_i231581_3_, p_i231581_5_, p_i231581_7_, p_i231581_9_);
        this.setShooter(p_i231581_2_);
    }

    public EntityEnderiophageRocket(World p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
        this(p_i47367_1_, p_i47367_3_, p_i47367_3_.getPosX(), p_i47367_3_.getPosY(), p_i47367_3_.getPosZ(), p_i47367_2_);
        this.dataManager.set(BOOSTED_ENTITY_ID, OptionalInt.of(p_i47367_3_.getEntityId()));
    }

    public EntityEnderiophageRocket(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(AMEntityRegistry.ENDERIOPHAGE_ROCKET, world);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        ++this.phageAge;
        if (this.world.isRemote) {
            this.world.addParticle(ParticleTypes.END_ROD, this.getPosX(), this.getPosY() - 0.3D, this.getPosZ(), this.rand.nextGaussian() * 0.05D, -this.getMotion().y * 0.5D, this.rand.nextGaussian() * 0.05D);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 17) {
            this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextGaussian() * 0.05D, 0.005D, this.rand.nextGaussian() * 0.05D);
            for(int i = 0; i < this.rand.nextInt(15) + 30; ++i) {
                this.world.addParticle(AMParticleRegistry.DNA, this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextGaussian() * 0.25D, this.rand.nextGaussian() * 0.25D, this.rand.nextGaussian() * 0.25D);
            }
            for(int i = 0; i < this.rand.nextInt(15) + 15; ++i) {
                this.world.addParticle(ParticleTypes.END_ROD, this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextGaussian() * 0.15D, this.rand.nextGaussian() * 0.15D, this.rand.nextGaussian() * 0.15D);
            }
            SoundEvent soundEvent = AlexsMobs.PROXY.isFarFromCamera(this.getPosX(), this.getPosY(), this.getPosZ()) ? SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST : SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR;
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), soundEvent, SoundCategory.AMBIENT, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);


        }else{
            super.handleStatusUpdate(id);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return new ItemStack(AMItemRegistry.ENDERIOPHAGE_ROCKET);
    }

}