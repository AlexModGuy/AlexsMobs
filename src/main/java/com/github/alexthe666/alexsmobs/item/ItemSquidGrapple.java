package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySquidGrapple;
import com.github.alexthe666.alexsmobs.entity.util.SquidGrappleUtil;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSquidGrapple extends Item {

    public ItemSquidGrapple(Item.Properties properties) {
        super(properties);
    }

    public int getUseDuration(ItemStack p_40680_) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public InteractionResultHolder<ItemStack> use(Level p_40672_, Player p_40673_, InteractionHand p_40674_) {
        ItemStack itemstack = p_40673_.getItemInHand(p_40674_);
        p_40673_.startUsingItem(p_40674_);

        return InteractionResultHolder.pass(itemstack);
    }

    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {

    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity livingEntityIn, int i) {
        if(livingEntityIn.isFallFlying()){
            return;
        }
        livingEntityIn.playSound(AMSoundRegistry.GIANT_SQUID_TENTACLE.get(),1.0F, 1.0F + (livingEntityIn.getRandom().nextFloat() - livingEntityIn.getRandom().nextFloat()) * 0.2F);
        livingEntityIn.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        if (!worldIn.isClientSide) {
            boolean left = false;
            if (livingEntityIn.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntityIn.getMainArm() == HumanoidArm.RIGHT || livingEntityIn.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntityIn.getMainArm() == HumanoidArm.LEFT) {
                left = true;
            }
            int power = this.getUseDuration(stack) - i;
            EntitySquidGrapple hook = new EntitySquidGrapple(worldIn, livingEntityIn, !left);
            Vec3 vector3d = livingEntityIn.getViewVector(1.0F);
            hook.shoot((double) vector3d.x(), (double) vector3d.y(), (double) vector3d.z(), getPowerForTime(power) * 3, 1);
            hook.setXRot(livingEntityIn.getXRot());
            hook.setYRot(livingEntityIn.getYRot());
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(hook);
            }
            stack.hurtAndBreak(1, livingEntityIn, (playerIn) -> {
                livingEntityIn.broadcastBreakEvent(playerIn.getUsedItemHand());
            });
            SquidGrappleUtil.onFireHook(livingEntityIn, hook.getUUID());
        }
    }

    public boolean isValidRepairItem(ItemStack s, ItemStack s1) {
        return s1.is(AMItemRegistry.LOST_TENTACLE.get());
    }

    public static float getPowerForTime(int p) {
        float f = (float)p / 20.0F;
        f = (f * f + f + f * 2.0F) / 4.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.alexsmobs.squid_grapple.desc").withStyle(ChatFormatting.GRAY));

    }
}
