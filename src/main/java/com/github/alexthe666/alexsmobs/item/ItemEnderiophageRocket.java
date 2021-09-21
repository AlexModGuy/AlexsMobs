package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityEnderiophageRocket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ItemEnderiophageRocket extends Item {

    public ItemEnderiophageRocket(Item.Properties group) {
        super(group);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            ItemStack itemstack = context.getItemInHand();
            Vec3 vector3d = context.getClickLocation();
            Direction direction = context.getClickedFace();
            FireworkRocketEntity fireworkrocketentity = new EntityEnderiophageRocket(world, context.getPlayer(), vector3d.x + (double)direction.getStepX() * 0.15D, vector3d.y + (double)direction.getStepY() * 0.15D, vector3d.z + (double)direction.getStepZ() * 0.15D, itemstack);
            world.addFreshEntity(fireworkrocketentity);
            if(!context.getPlayer().isCreative()){
                itemstack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.isFallFlying()) {
            ItemStack itemstack = playerIn.getItemInHand(handIn);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(new EntityEnderiophageRocket(worldIn, itemstack, playerIn));
                if (!playerIn.abilities.instabuild) {
                    itemstack.shrink(1);
                }
            }

            return InteractionResultHolder.sidedSuccess(playerIn.getItemInHand(handIn), worldIn.isClientSide());
        } else {
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
        }
    }

}
