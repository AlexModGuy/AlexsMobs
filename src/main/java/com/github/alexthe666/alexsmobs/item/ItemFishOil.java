package com.github.alexthe666.alexsmobs.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ItemFishOil extends Item {
    public ItemFishOil(Properties p_i225737_1_) {
        super(p_i225737_1_);
    }

    public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
        super.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
        if (p_77654_3_ instanceof ServerPlayerEntity) {
            ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)p_77654_3_;
            CriteriaTriggers.CONSUME_ITEM.trigger(lvt_4_1_, p_77654_1_);
            lvt_4_1_.addStat(Stats.ITEM_USED.get(this));
        }

        if (p_77654_1_.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (p_77654_3_ instanceof PlayerEntity && !((PlayerEntity)p_77654_3_).abilities.isCreativeMode) {
                ItemStack lvt_4_2_ = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity lvt_5_1_ = (PlayerEntity)p_77654_3_;
                if (!lvt_5_1_.inventory.addItemStackToInventory(lvt_4_2_)) {
                    lvt_5_1_.dropItem(lvt_4_2_, false);
                }
            }

            return p_77654_1_;
        }
    }

    public int getUseDuration(ItemStack p_77626_1_) {
        return 40;
    }

    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.DRINK;
    }

    public SoundEvent getDrinkSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
        return DrinkHelper.startDrinking(p_77659_1_, p_77659_2_, p_77659_3_);
    }
}
