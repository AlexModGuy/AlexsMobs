package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ItemShieldOfTheDeep extends Item {
    public ItemShieldOfTheDeep(Item.Properties group) {
        super(group);
    }

    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity)
    {
        return true;
    }

    public UseAnim getUseAnimation(ItemStack p_77661_1_) {
        return UseAnim.BLOCK;
    }

    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(Level p_77659_1_, Player p_77659_2_, InteractionHand p_77659_3_) {
        ItemStack lvt_4_1_ = p_77659_2_.getItemInHand(p_77659_3_);
        p_77659_2_.startUsingItem(p_77659_3_);
        return InteractionResultHolder.consume(lvt_4_1_);
    }

    public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return AMItemRegistry.SERRATED_SHARK_TOOTH == p_82789_2_.getItem() || super.isValidRepairItem(p_82789_1_, p_82789_2_);
    }

}
