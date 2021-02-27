package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityCockroachEgg;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.github.alexthe666.alexsmobs.entity.EntitySandShot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Predicate;

public class ItemPocketSand extends Item {

    public static final Predicate<ItemStack> IS_SAND = (stack) -> {
        return ItemTags.SAND.contains(stack.getItem());
    };

    public ItemPocketSand(Properties properties) {
        super(properties);
    }

    public ItemStack findAmmo(PlayerEntity entity) {
        if(entity.isCreative()){
            return ItemStack.EMPTY;
        }
        for(int i = 0; i < entity.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack1 = entity.inventory.getStackInSlot(i);
            if (IS_SAND.test(itemstack1)) {
                return itemstack1;
            }
        }
        return ItemStack.EMPTY;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity livingEntityIn, Hand handIn) {
        ItemStack itemstack = livingEntityIn.getHeldItem(handIn);
        ItemStack ammo = findAmmo(livingEntityIn);
        if(livingEntityIn.isCreative()){
            ammo = new ItemStack(Items.SAND);
        }
        if (!worldIn.isRemote && !ammo.isEmpty()) {
            worldIn.playSound((PlayerEntity)null, livingEntityIn.getPosX(), livingEntityIn.getPosY(), livingEntityIn.getPosZ(), SoundEvents.BLOCK_SAND_BREAK, SoundCategory.PLAYERS, 0.5F, 0.4F + (random.nextFloat() * 0.4F + 0.8F));
            boolean left = false;
            if (livingEntityIn.getActiveHand() == Hand.OFF_HAND && livingEntityIn.getPrimaryHand() == HandSide.RIGHT || livingEntityIn.getActiveHand() == Hand.MAIN_HAND && livingEntityIn.getPrimaryHand() == HandSide.LEFT) {
                left = true;
            }
            EntitySandShot blood = new EntitySandShot(worldIn, livingEntityIn, !left);
            Vector3d vector3d = livingEntityIn.getLook(1.0F);
            Vector3f vector3f = new Vector3f(vector3d);
            blood.shoot((double) vector3f.getX(), (double) vector3f.getY(), (double) vector3f.getZ(), 1.2F, 11);
            if (!worldIn.isRemote) {
                worldIn.addEntity(blood);
            }
            livingEntityIn.getCooldownTracker().setCooldown(this, 2);
            ammo.shrink(1);
            itemstack.damageItem(1, livingEntityIn, (player) -> {
                player.sendBreakAnimation(livingEntityIn.getActiveHand());
            });
        }
        livingEntityIn.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }


}
