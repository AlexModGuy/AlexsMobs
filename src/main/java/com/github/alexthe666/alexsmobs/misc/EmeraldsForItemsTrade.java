package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

import java.util.Random;

public class EmeraldsForItemsTrade implements VillagerTrades.ItemListing {
    private final Item tradeItem;
    private final int count;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public EmeraldsForItemsTrade(ItemLike p_i50539_1_, int p_i50539_2_, int p_i50539_3_, int p_i50539_4_) {
        this.tradeItem = p_i50539_1_.asItem();
        this.count = p_i50539_2_;
        this.maxUses = p_i50539_3_;
        this.xpValue = p_i50539_4_;
        this.priceMultiplier = 0.05F;
    }

    public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
        ItemStack lvt_3_1_ = new ItemStack(this.tradeItem, 1);
        return new MerchantOffer(lvt_3_1_, new ItemStack(Items.EMERALD, this.count), this.maxUses, this.xpValue, this.priceMultiplier);
    }
}