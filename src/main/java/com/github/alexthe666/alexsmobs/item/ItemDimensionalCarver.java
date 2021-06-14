package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemDimensionalCarver extends Item {

    public static final int MAX_TIME = 200;

    public ItemDimensionalCarver(Item.Properties props) {
        super(props);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (itemstack.getDamage() >= itemstack.getMaxDamage() - 1) {
            return ActionResult.resultFail(itemstack);
        } else {
            playerIn.setActiveHand(handIn);
            RayTraceResult raytraceresult = rayTracePortal(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
            double x = raytraceresult.getHitVec().x;
            double y = raytraceresult.getHitVec().y;
            double z = raytraceresult.getHitVec().z;
            if(itemstack.getOrCreateTag().getBoolean("HASBLOCK")){
                x = itemstack.getOrCreateTag().getDouble("BLOCKX");
                y = itemstack.getOrCreateTag().getDouble("BLOCKY");
                z = itemstack.getOrCreateTag().getDouble("BLOCKZ");
            }else{
                itemstack.getOrCreateTag().putBoolean("HASBLOCK", true);
                itemstack.getOrCreateTag().putDouble("BLOCKX", x);
                itemstack.getOrCreateTag().putDouble("BLOCKY", y);
                itemstack.getOrCreateTag().putDouble("BLOCKZ", z);
                itemstack.setTag(itemstack.getOrCreateTag());
            }
            worldIn.addParticle(AMParticleRegistry.INVERT_DIG, x, y, z, playerIn.getEntityId(), 0, 0);
            return ActionResult.resultConsume(itemstack);
        }

    }

    protected static BlockRayTraceResult rayTracePortal(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vector3d vector3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 1.5F;
        Vector3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }


    public int getUseDuration(ItemStack stack) {
        return 200;
    }

    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        player.swingArm(player.getActiveHand());
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        System.out.println("STOP USING TIME: " + timeLeft);
        if(stack.getOrCreateTag().getBoolean("HASBLOCK")){
            double x = stack.getOrCreateTag().getDouble("BLOCKX");
            double y = stack.getOrCreateTag().getDouble("BLOCKY");
            double z = stack.getOrCreateTag().getDouble("BLOCKZ");
        }
        stack.getOrCreateTag().putBoolean("HASBLOCK", false);
        stack.getOrCreateTag().putDouble("BLOCKX", 0);
        stack.getOrCreateTag().putDouble("BLOCKY", 0);
        stack.getOrCreateTag().putDouble("BLOCKZ", 0);
        stack.setTag(stack.getOrCreateTag());
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.isItemEqual(newStack);
    }
}
