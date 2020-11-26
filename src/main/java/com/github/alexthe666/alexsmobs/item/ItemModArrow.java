package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemModArrow extends ArrowItem {
    public ItemModArrow(Item.Properties group) {
        super(group);
    }

    public AbstractArrowEntity createArrow(World worldIn, ItemStack stack, LivingEntity shooter) {
        if(this == AMItemRegistry.SHARK_TOOTH_ARROW){
            ArrowEntity arrowentity = new EntitySharkToothArrow(worldIn, shooter);
            arrowentity.setPotionEffect(stack);
            return arrowentity;
        }else {
            return super.createArrow(worldIn, stack, shooter);
        }
    }

}
