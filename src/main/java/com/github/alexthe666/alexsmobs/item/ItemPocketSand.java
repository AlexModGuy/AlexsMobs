package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySandShot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.world.item.Item.Properties;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;

public class ItemPocketSand extends Item {

    private Random random = new Random();
    public static final Predicate<ItemStack> IS_SAND = (stack) -> {
        return stack.is(ItemTags.SAND);
    };

    public ItemPocketSand(Properties properties) {
        super(properties);
    }

    public ItemStack findAmmo(Player entity) {
        if(entity.isCreative()){
            return ItemStack.EMPTY;
        }
        for(int i = 0; i < entity.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack1 = entity.getInventory().getItem(i);
            if (IS_SAND.test(itemstack1)) {
                return itemstack1;
            }
        }
        return ItemStack.EMPTY;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player livingEntityIn, InteractionHand handIn) {
        ItemStack itemstack = livingEntityIn.getItemInHand(handIn);
        ItemStack ammo = findAmmo(livingEntityIn);
        if(livingEntityIn.isCreative()){
            ammo = new ItemStack(Items.SAND);
        }
        if (!worldIn.isClientSide && !ammo.isEmpty()) {
            worldIn.playSound((Player)null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), SoundEvents.SAND_BREAK, SoundSource.PLAYERS, 0.5F, 0.4F + (random.nextFloat() * 0.4F + 0.8F));
            boolean left = false;
            if (livingEntityIn.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntityIn.getMainArm() == HumanoidArm.RIGHT || livingEntityIn.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntityIn.getMainArm() == HumanoidArm.LEFT) {
                left = true;
            }
            EntitySandShot blood = new EntitySandShot(worldIn, livingEntityIn, !left);
            Vec3 vector3d = livingEntityIn.getViewVector(1.0F);
            Vector3f vector3f = new Vector3f(vector3d);
            blood.shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), 1.2F, 11);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(blood);
            }
            livingEntityIn.getCooldowns().addCooldown(this, 2);
            ammo.shrink(1);
            itemstack.hurtAndBreak(1, livingEntityIn, (player) -> {
                player.broadcastBreakEvent(livingEntityIn.getUsedItemHand());
            });
        }
        livingEntityIn.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
    }


}
