package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ItemTendonWhip extends Item {

    public ItemTendonWhip(Properties properties) {
        super(properties);
    }

    public static boolean isActive(ItemStack stack, LivingEntity holder) {
        return holder != null && (holder.getMainHandItem() == stack || holder.getOffhandItem() == stack) && TendonWhipUtil.getLastTendon(holder) != null;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        if (TendonWhipUtil.canLaunchTendons(worldIn, playerIn)) {
            TendonWhipUtil.retractFarTendons(worldIn, playerIn);
            Entity closestValid = null;
            if (!worldIn.isClientSide) {
                Vec3 playerEyes = playerIn.getEyePosition(1.0F);
                HitResult hitresult = worldIn.clip(new ClipContext(playerEyes, playerEyes.add(playerIn.getLookAngle().scale(10)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, playerIn));
                if (hitresult instanceof EntityHitResult) {
                    Entity entity = ((EntityHitResult) hitresult).getEntity();
                    if (!entity.equals(playerIn) && !playerIn.isAlliedTo(entity) && !entity.isAlliedTo(playerIn) && entity instanceof Mob && playerIn.hasLineOfSight(entity)) {
                        closestValid = entity;
                    }
                } else {
                    for (Entity entity : worldIn.getEntitiesOfClass(LivingEntity.class, playerIn.getBoundingBox().inflate(8.0D))) {
                        if (!entity.equals(playerIn) && !playerIn.isAlliedTo(entity) && !entity.isAlliedTo(playerIn) && entity instanceof Mob && playerIn.hasLineOfSight(entity)) {
                            if (closestValid == null || playerIn.distanceTo(entity) < playerIn.distanceTo(closestValid)) {
                                closestValid = entity;
                            }
                        }
                    }
                }
            }
            EntityTendonSegment segment = AMEntityRegistry.TENDON_SEGMENT.get().create(worldIn);
            worldIn.addFreshEntity(segment);
            segment.setCreatorEntityUUID(playerIn.getUUID());
            segment.setFromEntityID(playerIn.getId());
            if (closestValid != null) {
                segment.setToEntityID(closestValid.getId());
            }
            segment.copyPosition(playerIn);
            segment.setProgress(0.0F);
            TendonWhipUtil.setLastTendon(playerIn, segment);
            return InteractionResultHolder.consume(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.sameItem(newStack);
    }
}
