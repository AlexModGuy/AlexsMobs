package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ItemDimensionalCarver extends Item {

    public static final int MAX_TIME = 200;

    public ItemDimensionalCarver(Item.Properties props) {
        super(props);
    }

    // Helper methods for 1.21 DataComponents NBT replacement
    private static CompoundTag getCustomData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : new CompoundTag();
    }
    
    private static void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    protected static BlockHitResult rayTracePortal(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        final float f = player.getXRot();
        final float f1 = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        final float f11 = -f1 * Mth.DEG_TO_RAD - Mth.PI;
        final float f12 = -f * Mth.DEG_TO_RAD;
        final float f2 = Mth.cos(f11);
        final float f3 = Mth.sin(f11);
        final float f4 = -Mth.cos(f12);
        final float f5 = Mth.sin(f12);
        final float f6 = f3 * f4;
        final float f7 = f2 * f4;
        final double d0 = 1.5F;
        Vec3 vector3d1 = vector3d.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    public int getItemStackLimit(ItemStack stack) {
        return 1; // fix for incompatibility with other mods
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            playerIn.startUsingItem(handIn);
            HitResult raytraceresult = rayTracePortal(worldIn, playerIn, ClipContext.Fluid.ANY);
            Direction dir = Direction.orderedByNearest(playerIn)[0];

            double x = raytraceresult.getLocation().x - dir.getNormal().getX() * 0.1F;
            double y = raytraceresult.getLocation().y - dir.getNormal().getY() * 0.1F;
            double z = raytraceresult.getLocation().z - dir.getNormal().getZ() * 0.1F;
            CompoundTag tag = getCustomData(itemstack);
            if (tag.getBoolean("HASBLOCK")) {
                x = tag.getDouble("BLOCKX");
                y = tag.getDouble("BLOCKY");
                z = tag.getDouble("BLOCKZ");
            } else {
                tag.putBoolean("HASBLOCK", true);
                tag.putDouble("BLOCKX", x);
                tag.putDouble("BLOCKY", y);
                tag.putDouble("BLOCKZ", z);
                setCustomData(itemstack, tag);
            }
            worldIn.addParticle(AMParticleRegistry.INVERT_DIG.get(), x, y, z, playerIn.getId(), 0, 0);
            return InteractionResultHolder.consume(itemstack);
        }

    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 200;
    }

    public float getXpRepairRatio(ItemStack stack) {
        return 100F;
    }

    public void onUseTick(Level level, LivingEntity player, ItemStack itemstack, int count) {
        player.swing(player.getUsedItemHand());
        RandomSource random = player.getRandom();
        if (count % 5 == 0) {
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
            player.playSound(SoundEvents.NETHERITE_BLOCK_HIT, 1, 0.5F + random.nextFloat());
        }
        boolean flag = false;
        CompoundTag tag = getCustomData(itemstack);
        if (tag.getBoolean("HASBLOCK")) {
            double x = tag.getDouble("BLOCKX");
            double y = tag.getDouble("BLOCKY");
            double z = tag.getDouble("BLOCKZ");
            if (random.nextFloat() < 0.2) {
                player.level().addParticle(AMParticleRegistry.WORM_PORTAL.get(), x + random.nextGaussian() * 0.1F, y + random.nextGaussian() * 0.1F, z + random.nextGaussian() * 0.1F, random.nextGaussian() * 0.1F, -0.1F, random.nextGaussian() * 0.1F);
            }
            if (player.distanceToSqr(x, y, z) > 9) {
                flag = true;
                if (player instanceof Player) {
                    ((Player) player).getCooldowns().addCooldown(this, 40);
                }
            }
        }
        if (flag) {
            player.stopUsingItem();
            CompoundTag resetTag = getCustomData(itemstack);
            resetTag.putBoolean("HASBLOCK", false);
            resetTag.putDouble("BLOCKX", 0);
            resetTag.putDouble("BLOCKY", 0);
            resetTag.putDouble("BLOCKZ", 0);
            setCustomData(itemstack, resetTag);
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        CompoundTag tag = getCustomData(stack);
        if (tag.getBoolean("HASBLOCK")) {
            double x = tag.getDouble("BLOCKX");
            double y = tag.getDouble("BLOCKY");
            double z = tag.getDouble("BLOCKZ");
            if (!level.isClientSide) {
                entityLiving.gameEvent(GameEvent.ITEM_INTERACT_START);
                entityLiving.playSound(SoundEvents.GLASS_BREAK, 1, 0.5F);
                EntityVoidPortal portal = new EntityVoidPortal(level, this);
                portal.setPos(x, y, z);
                Direction dir = Direction.orderedByNearest(entityLiving)[0].getOpposite();
                if (dir == Direction.UP) {
                    dir = Direction.DOWN;
                }
                portal.setAttachmentFacing(dir);
                level.addFreshEntity(portal);
                onPortalOpen(level, entityLiving, portal, dir);
                stack.hurtAndBreak(1, entityLiving, EquipmentSlot.MAINHAND);
                if (entityLiving instanceof Player) {
                    ((Player) entityLiving).getCooldowns().addCooldown(this, 200);
                }
            }
        }
        tag.putBoolean("HASBLOCK", false);
        tag.putDouble("BLOCKX", 0);
        tag.putDouble("BLOCKY", 0);
        tag.putDouble("BLOCKZ", 0);
        setCustomData(stack, tag);
        return stack;
    }


    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        CompoundTag tag = getCustomData(stack);
        tag.putBoolean("HASBLOCK", false);
        tag.putDouble("BLOCKX", 0);
        tag.putDouble("BLOCKY", 0);
        tag.putDouble("BLOCKZ", 0);
        setCustomData(stack, tag);

    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    public void onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir){
        portal.setLifespan(1200);
        ResourceKey<Level> respawnDimension = Level.OVERWORLD;
        BlockPos respawnPosition = player.getSleepingPos().isPresent() ? player.getSleepingPos().get() : player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.ZERO);
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            respawnDimension = serverPlayer.getRespawnDimension();
            if (serverPlayer.getRespawnPosition() != null) {
                respawnPosition = serverPlayer.getRespawnPosition();
            }
        }
        portal.exitDimension = respawnDimension;
        portal.setDestination(respawnPosition.above(2));
    }
}
