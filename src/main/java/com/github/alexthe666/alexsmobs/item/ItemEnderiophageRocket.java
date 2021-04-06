package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityEnderiophageRocket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemEnderiophageRocket extends Item {

    public ItemEnderiophageRocket(Item.Properties group) {
        super(group);
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            ItemStack itemstack = context.getItem();
            Vector3d vector3d = context.getHitVec();
            Direction direction = context.getFace();
            FireworkRocketEntity fireworkrocketentity = new EntityEnderiophageRocket(world, context.getPlayer(), vector3d.x + (double)direction.getXOffset() * 0.15D, vector3d.y + (double)direction.getYOffset() * 0.15D, vector3d.z + (double)direction.getZOffset() * 0.15D, itemstack);
            world.addEntity(fireworkrocketentity);
            if(!context.getPlayer().isCreative()){
                itemstack.shrink(1);
            }
        }
        return ActionResultType.func_233537_a_(world.isRemote);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (playerIn.isElytraFlying()) {
            ItemStack itemstack = playerIn.getHeldItem(handIn);
            if (!worldIn.isRemote) {
                worldIn.addEntity(new EntityEnderiophageRocket(worldIn, itemstack, playerIn));
                if (!playerIn.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            }

            return ActionResult.func_233538_a_(playerIn.getHeldItem(handIn), worldIn.isRemote());
        } else {
            return ActionResult.resultPass(playerIn.getHeldItem(handIn));
        }
    }

}
