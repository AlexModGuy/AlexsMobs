package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class BlockItemAMRender extends AMBlockItem {

    public BlockItemAMRender(RegistryObject<Block> blockSupplier, Properties props) {
        super(blockSupplier, props);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept((net.minecraftforge.client.IItemRenderProperties) AlexsMobs.PROXY.getISTERProperties());
    }
}
