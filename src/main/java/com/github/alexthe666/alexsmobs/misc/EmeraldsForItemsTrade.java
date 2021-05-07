package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.IItemProvider;

import java.util.Random;

public class EmeraldsForItemsTrade implements VillagerTrades.ITrade {
    private final Item tradeItem;
    private final int count;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public EmeraldsForItemsTrade(IItemProvider p_i50539_1_, int p_i50539_2_, int p_i50539_3_, int p_i50539_4_) {
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