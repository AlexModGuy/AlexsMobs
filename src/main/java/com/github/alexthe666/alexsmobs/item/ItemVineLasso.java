package com.github.alexthe666.alexsmobs.item;

import java.util.function.Consumer;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.component.AMDataComponentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVineLasso;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ItemVineLasso extends Item {

    public ItemVineLasso(Properties props) {
        super(props);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public static boolean isItemInUse(ItemStack stack) {
        return stack.has(AMDataComponentRegistry.SWINGING);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int i, boolean b) {
        if (entity instanceof LivingEntity) {
            if (((LivingEntity) entity).getUseItem() == stack && ((LivingEntity) entity).isUsingItem())
                stack.set(AMDataComponentRegistry.SWINGING, Unit.INSTANCE);
            else
                stack.remove(AMDataComponentRegistry.SWINGING);
        }
    }

    @Override
    public int getUseDuration(ItemStack p_40680_, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_40672_, Player p_40673_, InteractionHand p_40674_) {
        ItemStack itemstack = p_40673_.getItemInHand(p_40674_);
        p_40673_.startUsingItem(p_40674_);

        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (count % 7 == 0) {
            livingEntityIn.gameEvent(GameEvent.ITEM_INTERACT_START);
            livingEntityIn.playSound(AMSoundRegistry.VINE_LASSO.get(), 1.0F,
                    1.0F + (livingEntityIn.getRandom().nextFloat() - livingEntityIn.getRandom().nextFloat()) * 0.2F);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity livingEntityIn, int i) {
        if (!worldIn.isClientSide) {
            boolean left = livingEntityIn.getUsedItemHand() == InteractionHand.OFF_HAND
                && livingEntityIn.getMainArm() == HumanoidArm.RIGHT
                || livingEntityIn.getUsedItemHand() == InteractionHand.MAIN_HAND
                && livingEntityIn.getMainArm() == HumanoidArm.LEFT;
            int power = this.getUseDuration(stack, livingEntityIn) - i;
            EntityVineLasso lasso = new EntityVineLasso(worldIn, livingEntityIn);
            Vec3 vector3d = livingEntityIn.getViewVector(1.0F);
            lasso.shoot(vector3d.x(), vector3d.y(), vector3d.z(), getPowerForTime(power), 1);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(lasso);
            }
            stack.shrink(1);
        }
        // livingEntityIn.awardStat(Stats.ITEM_USED.get(this));
    }

    public static float getPowerForTime(int p) {
        float f = (float) p / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsMobs.PROXY.getISTERProperties());
    }
}
