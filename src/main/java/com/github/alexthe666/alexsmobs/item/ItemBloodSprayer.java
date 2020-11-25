package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class ItemBloodSprayer extends Item {

    public ItemBloodSprayer(Item.Properties properties) {
        super(properties);
    }

    public int getUseDuration(ItemStack stack) {
        return isUsable(stack) ? Integer.MAX_VALUE : 0;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public static boolean isUsable(ItemStack stack) {
        return stack.getDamage() < stack.getMaxDamage() - 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.isItemEqual(newStack);
    }

    public void onUse(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if(isUsable(stack) && count % 2 == 0){
            boolean left = false;
            if(livingEntityIn.getActiveHand() == Hand.OFF_HAND && livingEntityIn.getPrimaryHand() == HandSide.RIGHT || livingEntityIn.getActiveHand() == Hand.MAIN_HAND && livingEntityIn.getPrimaryHand() == HandSide.LEFT){
                left = true;
            }
            EntityMosquitoSpit blood = new EntityMosquitoSpit(worldIn, livingEntityIn, !left);
            Vector3d vector3d = livingEntityIn.getLook(1.0F);
            Vector3f vector3f = new Vector3f(vector3d);
            blood.shoot((double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), 1F, 10);
            if(!worldIn.isRemote){
                worldIn.addEntity(blood);
            }
            stack.damageItem(1, livingEntityIn, (player) -> {
                player.sendBreakAnimation(livingEntityIn.getActiveHand());
            });
        }
    }
}
