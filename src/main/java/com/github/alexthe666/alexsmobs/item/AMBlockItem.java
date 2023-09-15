package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;

public class AMBlockItem extends BlockItem {

    private final RegistryObject<Block> blockSupplier;

    public AMBlockItem(RegistryObject<Block> blockSupplier, Item.Properties props) {
        super((Block)null, props);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public Block getBlock() {
        return blockSupplier.get();
    }

    public boolean canFitInsideCraftingRemainingItems() {
        return !(blockSupplier.get() instanceof ShulkerBoxBlock);
    }

    public void onDestroyed(ItemEntity p_150700_) {
        if (this.blockSupplier.get() instanceof ShulkerBoxBlock) {
            ItemStack itemstack = p_150700_.getItem();
            CompoundTag compoundtag = getBlockEntityData(itemstack);
            if (compoundtag != null && compoundtag.contains("Items", 9)) {
                ListTag listtag = compoundtag.getList("Items", 10);
                ItemUtils.onContainerDestroyed(p_150700_, listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of));
            }
        }
    }


    public boolean canBeHurtBy(DamageSource damage) {
        return super.canBeHurtBy(damage) && (this != AMBlockRegistry.TRANSMUTATION_TABLE.get().asItem() || !damage.isExplosion());
    }

    public InteractionResult useOn(UseOnContext context) {
        return blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS) ? InteractionResult.PASS : super.useOn(context);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(blockSupplier.equals(AMBlockRegistry.TRIOPS_EGGS)){
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockHitResult blockhitresult1 = blockhitresult.withPosition(blockhitresult.getBlockPos().above());
            InteractionResult interactionresult = super.useOn(new UseOnContext(player, hand, blockhitresult1));
            return new InteractionResultHolder<>(interactionresult, player.getItemInHand(hand));
        }else{
            return super.use(level, player, hand);
        }
    }
}
