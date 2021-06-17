package com.github.alexthe666.alexsmobs.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemShieldOfTheDeep extends Item {
    public ItemShieldOfTheDeep(Item.Properties group) {
        super(group);
    }

    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity)
    {
        return true;
    }

    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.BLOCK;
    }

    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
        ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
        p_77659_2_.setActiveHand(p_77659_3_);
        return ActionResult.resultConsume(lvt_4_1_);
    }

    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return AMItemRegistry.SERRATED_SHARK_TOOTH == p_82789_2_.getItem() || super.getIsRepairable(p_82789_1_, p_82789_2_);
    }

}
