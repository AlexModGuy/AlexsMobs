package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class ItemStinkBottle extends AMBlockItem {

    public ItemStinkBottle(RegistryObject<Block> blockSupplier, Item.Properties props) {
        super(blockSupplier, props);
    }

    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if(result.consumesAction()){
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if(!context.getPlayer().addItem(bottle)){
                context.getPlayer().drop(bottle, false);
            }
        }
        return result;
    }
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
}
