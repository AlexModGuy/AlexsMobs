package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModFishBucket extends BucketItem {
    private final EntityType<?> fishType;

    public ItemModFishBucket(EntityType<?> fishTypeIn, Fluid fluid, Item.Properties builder) {
        super(fluid, builder.stacksTo(1));
        this.fishType = fishTypeIn;
        this.fishTypeSupplier = () -> fishTypeIn;
    }

    public void checkExtraContent(Level worldIn, ItemStack p_203792_2_, BlockPos pos) {
        if (worldIn instanceof ServerLevel) {
            this.placeFish((ServerLevel)worldIn, p_203792_2_, pos);
        }
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    private void placeFish(ServerLevel worldIn, ItemStack stack, BlockPos pos) {
        Entity entity = this.fishType.spawn(worldIn, stack, (Player)null, pos, MobSpawnType.BUCKET, true, false);
        if (entity != null && entity instanceof EntityLobster) {
            ((EntityLobster)entity).setFromBucket(true);
            CompoundTag compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("BucketVariantTag", 3)){
                int i = compoundnbt.getInt("BucketVariantTag");
                ((EntityLobster) entity).setVariant(i);
            }

        }
        if (entity != null && entity instanceof EntityBlobfish) {
            ((EntityBlobfish)entity).setFromBucket(true);
            CompoundTag compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("BucketScale")){
                ((EntityBlobfish) entity).setBlobfishScale(compoundnbt.getFloat("BucketScale"));
            }
            if(compoundnbt.contains("Slimed")){
                ((EntityBlobfish) entity).setSlimed(compoundnbt.getBoolean("Slimed"));
            }
        }
        if (entity != null && entity instanceof EntityStradpole) {
            ((EntityStradpole) entity).setFromBucket(true);
        }
        if (entity != null && entity instanceof EntityPlatypus) {
            CompoundTag compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("PlatypusData")){
                ((EntityPlatypus)entity).readAdditionalSaveData(compoundnbt.getCompound("PlatypusData"));
            }

        }
        if (entity != null && entity instanceof EntityFrilledShark) {
            CompoundTag compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("FrilledSharkData")){
                ((EntityFrilledShark)entity).readAdditionalSaveData(compoundnbt.getCompound("FrilledSharkData"));
            }

        }
        if (entity != null && entity instanceof EntityMimicOctopus) {
            CompoundTag compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("MimicOctopusData")){
                ((EntityMimicOctopus)entity).readAdditionalSaveData(compoundnbt.getCompound("MimicOctopusData"));
            }
            ((EntityMimicOctopus)entity).setMoistness(60000);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.fishType == AMEntityRegistry.LOBSTER) {
            CompoundTag compoundnbt = stack.getTag();
            if (compoundnbt != null && compoundnbt.contains("BucketVariantTag", 3)) {
                int i = compoundnbt.getInt("BucketVariantTag");
                String s = "entity.alexsmobs.lobster.variant_" + EntityLobster.getVariantName(i);
                tooltip.add((new TranslatableComponent(s)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            }
        }
    }


    private final java.util.function.Supplier<? extends EntityType<?>> fishTypeSupplier;
    protected EntityType<?> getFishType() {
        return fishTypeSupplier.get();
    }
}
