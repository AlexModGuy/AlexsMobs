package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemMaraca extends Item {

    public ItemMaraca(Item.Properties property) {
        super(property);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        worldIn.playSound((PlayerEntity)playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), AMSoundRegistry.MARACA, SoundCategory.PLAYERS, 0.5F, (random.nextFloat() * 0.4F + 0.8F));
        playerIn.getCooldownTracker().setCooldown(this, 3);
        playerIn.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.resultSuccess(itemstack);
    }
}
