package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

public class ItemDimensionalCarver extends Item {

    public static final int MAX_TIME = 200;

    public ItemDimensionalCarver(Item.Properties props) {
        super(props);
    }

    protected static BlockRayTraceResult rayTracePortal(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vector3d vector3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 1.5F;
        Vector3d vector3d1 = vector3d.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }

    public int getItemStackLimit(ItemStack stack) {
        return 1; // fix for incompatibility with other mods
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (itemstack.getDamage() >= itemstack.getMaxDamage() - 1) {
            return ActionResult.resultFail(itemstack);
        } else {
            playerIn.setActiveHand(handIn);
            RayTraceResult raytraceresult = rayTracePortal(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
            Direction dir = Direction.getFacingDirections(playerIn)[0];

            double x = raytraceresult.getHitVec().x - dir.getDirectionVec().getX() * 0.1F;
            double y = raytraceresult.getHitVec().y - dir.getDirectionVec().getY() * 0.1F;
            double z = raytraceresult.getHitVec().z - dir.getDirectionVec().getZ() * 0.1F;
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
            worldIn.addParticle(AMParticleRegistry.INVERT_DIG, x, y, z, playerIn.getEntityId(), 0, 0);
            return ActionResult.resultConsume(itemstack);
        }

    }

    public int getUseDuration(ItemStack stack) {
        return 200;
    }

    public float getXpRepairRatio(ItemStack stack) {
        return 100F;
    }

    public void onUsingTick(ItemStack itemstack, LivingEntity player, int count) {
        player.swingArm(player.getActiveHand());
        if (count % 5 == 0) {
            player.playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_HIT, 1, 0.5F + random.nextFloat());
        }
        boolean flag = false;
        if (itemstack.getOrCreateTag().getBoolean("HASBLOCK")) {
            double x = itemstack.getOrCreateTag().getDouble("BLOCKX");
            double y = itemstack.getOrCreateTag().getDouble("BLOCKY");
            double z = itemstack.getOrCreateTag().getDouble("BLOCKZ");
            if (random.nextFloat() < 0.2) {
                player.world.addParticle(AMParticleRegistry.WORM_PORTAL, x + random.nextGaussian() * 0.1F, y + random.nextGaussian() * 0.1F, z + random.nextGaussian() * 0.1F, random.nextGaussian() * 0.1F, -0.1F, random.nextGaussian() * 0.1F);
            }
            if (player.getDistanceSq(x, y, z) > 9) {
                flag = true;
                if (player instanceof PlayerEntity) {
                    ((PlayerEntity) player).getCooldownTracker().setCooldown(this, 40);
                }
            }
            if (count == 1 && !player.world.isRemote) {
                player.playSound(SoundEvents.BLOCK_GLASS_BREAK, 1, 0.5F);
                EntityVoidPortal portal = AMEntityRegistry.VOID_PORTAL.create(player.world);
                portal.setPosition(x, y, z);
                Direction dir = Direction.getFacingDirections(player)[0].getOpposite();
                if (dir == Direction.UP) {
                    dir = Direction.DOWN;
                }
                portal.setAttachmentFacing(dir);
                portal.setLifespan(1200);
                RegistryKey<World> respawnDimension = World.OVERWORLD;
                BlockPos respawnPosition = player.getBedPosition().isPresent() ? player.getBedPosition().get() : player.world.getHeight(Heightmap.Type.MOTION_BLOCKING, BlockPos.ZERO);
                if (player instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                    respawnDimension = serverPlayer.func_241141_L_();
                    if (serverPlayer.func_241140_K_() != null) {
                        respawnPosition = serverPlayer.func_241140_K_();
                    }
                }
                player.world.addEntity(portal);
                portal.exitDimension = respawnDimension;
                portal.setDestination(respawnPosition.up(2));
                itemstack.damageItem(1, player, (playerIn) -> {
                    player.sendBreakAnimation(playerIn.getActiveHand());
                });
                flag = true;
                if (player instanceof PlayerEntity) {
                    ((PlayerEntity) player).getCooldownTracker().setCooldown(this, 200);
                }
            }
        }
        if (flag) {
            player.resetActiveHand();
            itemstack.getOrCreateTag().putBoolean("HASBLOCK", false);
            itemstack.getOrCreateTag().putDouble("BLOCKX", 0);
            itemstack.getOrCreateTag().putDouble("BLOCKY", 0);
            itemstack.getOrCreateTag().putDouble("BLOCKZ", 0);
            itemstack.setTag(itemstack.getOrCreateTag());
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
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
