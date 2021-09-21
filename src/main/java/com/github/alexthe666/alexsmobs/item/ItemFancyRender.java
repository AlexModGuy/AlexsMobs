package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.item.Item;

public class ItemFancyRender extends Item {

    public ItemFancyRender(Properties props) {
        super(props);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept((net.minecraftforge.client.IItemRenderProperties) AlexsMobs.PROXY.getISTERProperties());
    }
}
