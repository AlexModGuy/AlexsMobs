package com.github.alexthe666.alexsmobs.item;

import java.util.function.Consumer;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import net.minecraft.world.item.Item;

public class ItemTabIcon extends ItemInventoryOnly {
    public ItemTabIcon(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getISTERProperties());
    }
}
