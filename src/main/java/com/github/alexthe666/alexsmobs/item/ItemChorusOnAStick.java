package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemChorusOnAStick extends Item {

    public ItemChorusOnAStick(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.getVehicle() instanceof EntityEndergrade endergrade) {
            if (endergrade.boost()) {
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide);
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }
}
