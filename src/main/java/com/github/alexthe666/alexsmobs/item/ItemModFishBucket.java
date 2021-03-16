package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModFishBucket extends BucketItem {
    private final EntityType<?> fishType;

    public ItemModFishBucket(EntityType<?> fishTypeIn, Fluid fluid, Item.Properties builder) {
        super(fluid, builder);
        this.fishType = fishTypeIn;
        this.fishTypeSupplier = () -> fishTypeIn;
    }

    public void onLiquidPlaced(World worldIn, ItemStack p_203792_2_, BlockPos pos) {
        if (worldIn instanceof ServerWorld) {
            this.placeFish((ServerWorld)worldIn, p_203792_2_, pos);
        }

    }

    protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    private void placeFish(ServerWorld worldIn, ItemStack stack, BlockPos pos) {
        Entity entity = this.fishType.spawn(worldIn, stack, (PlayerEntity)null, pos, SpawnReason.BUCKET, true, false);
        if (entity != null && entity instanceof EntityLobster) {
            ((EntityLobster)entity).setFromBucket(true);
            CompoundNBT compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("BucketVariantTag", 3)){
                int i = compoundnbt.getInt("BucketVariantTag");
                ((EntityLobster) entity).setVariant(i);
            }

        }
        if (entity != null && entity instanceof EntityBlobfish) {
            ((EntityBlobfish)entity).setFromBucket(true);
            CompoundNBT compoundnbt = stack.getOrCreateTag();
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
            CompoundNBT compoundnbt = stack.getOrCreateTag();
            if(compoundnbt.contains("PlatypusData")){
                ((EntityPlatypus)entity).readAdditional(compoundnbt.getCompound("PlatypusData"));
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (this.fishType == AMEntityRegistry.LOBSTER) {
            CompoundNBT compoundnbt = stack.getTag();
            if (compoundnbt != null && compoundnbt.contains("BucketVariantTag", 3)) {
                int i = compoundnbt.getInt("BucketVariantTag");
                String s = "entity.alexsmobs.lobster.variant_" + EntityLobster.getVariantName(i);
                tooltip.add((new TranslationTextComponent(s)).mergeStyle(TextFormatting.GRAY).mergeStyle(TextFormatting.ITALIC));
            }
        }
    }


    private final java.util.function.Supplier<? extends EntityType<?>> fishTypeSupplier;
    protected EntityType<?> getFishType() {
        return fishTypeSupplier.get();
    }
}
