package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    protected static BlockHitResult rayTracePortal(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 1.5F;
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
            if (itemstack.getOrCreateTag().getBoolean("HASBLOCK")) {
                x = itemstack.getOrCreateTag().getDouble("BLOCKX");
                y = itemstack.getOrCreateTag().getDouble("BLOCKY");
                z = itemstack.getOrCreateTag().getDouble("BLOCKZ");
            } else {
                itemstack.getOrCreateTag().putBoolean("HASBLOCK", true);
                itemstack.getOrCreateTag().putDouble("BLOCKX", x);
                itemstack.getOrCreateTag().putDouble("BLOCKY", y);
                itemstack.getOrCreateTag().putDouble("BLOCKZ", z);
                itemstack.setTag(itemstack.getOrCreateTag());
            }
            worldIn.addParticle(AMParticleRegistry.INVERT_DIG.get(), x, y, z, playerIn.getId(), 0, 0);
            return InteractionResultHolder.consume(itemstack);
        }

    }

    public int getUseDuration(ItemStack stack) {
        return 200;
    }

    public float getXpRepairRatio(ItemStack stack) {
        return 100F;
    }

    public void onUsingTick(ItemStack itemstack, LivingEntity player, int count) {
        player.swing(player.getUsedItemHand());
        RandomSource random = player.getRandom();
        if (count % 5 == 0) {
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
            player.playSound(SoundEvents.NETHERITE_BLOCK_HIT, 1, 0.5F + random.nextFloat());
        }
        boolean flag = false;
        if (itemstack.getOrCreateTag().getBoolean("HASBLOCK")) {
            double x = itemstack.getOrCreateTag().getDouble("BLOCKX");
            double y = itemstack.getOrCreateTag().getDouble("BLOCKY");
            double z = itemstack.getOrCreateTag().getDouble("BLOCKZ");
            if (random.nextFloat() < 0.2) {
                player.level.addParticle(AMParticleRegistry.WORM_PORTAL.get(), x + random.nextGaussian() * 0.1F, y + random.nextGaussian() * 0.1F, z + random.nextGaussian() * 0.1F, random.nextGaussian() * 0.1F, -0.1F, random.nextGaussian() * 0.1F);
            }
            if (player.distanceToSqr(x, y, z) > 9) {
                flag = true;
                if (player instanceof Player) {
                    ((Player) player).getCooldowns().addCooldown(this, 40);
                }
            }
            if (count == 1 && !player.level.isClientSide) {
                player.gameEvent(GameEvent.ITEM_INTERACT_START);
                player.playSound(SoundEvents.GLASS_BREAK, 1, 0.5F);
                EntityVoidPortal portal = new EntityVoidPortal(player.level, this);
                portal.setPos(x, y, z);
                Direction dir = Direction.orderedByNearest(player)[0].getOpposite();
                if (dir == Direction.UP) {
                    dir = Direction.DOWN;
                }
                portal.setAttachmentFacing(dir);
                player.level.addFreshEntity(portal);
                onPortalOpen(player.level, player, portal, dir);
                itemstack.hurtAndBreak(1, player, (playerIn) -> {
                    player.broadcastBreakEvent(playerIn.getUsedItemHand());
                });
                flag = true;
                if (player instanceof Player) {
                    ((Player) player).getCooldowns().addCooldown(this, 200);
                }
            }
        }
        if (flag) {
            player.stopUsingItem();
            itemstack.getOrCreateTag().putBoolean("HASBLOCK", false);
            itemstack.getOrCreateTag().putDouble("BLOCKX", 0);
            itemstack.getOrCreateTag().putDouble("BLOCKY", 0);
            itemstack.getOrCreateTag().putDouble("BLOCKZ", 0);
            itemstack.setTag(itemstack.getOrCreateTag());
        }
    }


    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        stack.getOrCreateTag().putBoolean("HASBLOCK", false);
        stack.getOrCreateTag().putDouble("BLOCKX", 0);
        stack.getOrCreateTag().putDouble("BLOCKY", 0);
        stack.getOrCreateTag().putDouble("BLOCKZ", 0);
        stack.setTag(stack.getOrCreateTag());

    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.sameItem(newStack);
    }

    public void onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir){
        portal.setLifespan(1200);
        ResourceKey<Level> respawnDimension = Level.OVERWORLD;
        BlockPos respawnPosition = player.getSleepingPos().isPresent() ? player.getSleepingPos().get() : player.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.ZERO);
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
