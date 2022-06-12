package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Random;

public class ItemsForEmeraldsTrade  implements VillagerTrades.ItemListing {
    private final ItemStack sellingItem;
    private final int emeraldCount;
    private final int sellingItemCount;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public ItemsForEmeraldsTrade(Block sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
        this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, maxUses, xpValue);
    }

    public ItemsForEmeraldsTrade(Item sellingItem, int emeraldCount, int sellingItemCount, int xpValue) {
        this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, 12, xpValue);
    }

    public ItemsForEmeraldsTrade(Item sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
        this(new ItemStack(sellingItem), emeraldCount, sellingItemCount, maxUses, xpValue);
    }

    public ItemsForEmeraldsTrade(ItemStack sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue) {
        this(sellingItem, emeraldCount, sellingItemCount, maxUses, xpValue, 0.05F);
    }

    public ItemsForEmeraldsTrade(ItemStack sellingItem, int emeraldCount, int sellingItemCount, int maxUses, int xpValue, float priceMultiplier) {
        this.sellingItem = sellingItem;
        this.emeraldCount = emeraldCount;
        this.sellingItemCount = sellingItemCount;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
        this.priceMultiplier = priceMultiplier;
    }

    public MerchantOffer getOffer(Entity trader, RandomSource rand) {
        return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCount), new ItemStack(this.sellingItem.getItem(), this.sellingItemCount), this.maxUses, this.xpValue, this.priceMultiplier);
    }
}
