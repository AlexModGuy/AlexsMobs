package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.google.common.base.Predicates;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.debug.CaveDebugRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.placement.CaveEdge;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemEcholocator extends Item {

    public boolean ender;

    public ItemEcholocator(Item.Properties properties, boolean ender) {
        super(properties);
        this.ender = ender;
    }

    private List<BlockPos> getNearbyPortals(BlockPos blockpos, ServerWorld world, int range) {
        if(ender){
            PointOfInterestManager pointofinterestmanager = world.getPointOfInterestManager();
            Stream<BlockPos> stream = pointofinterestmanager.findAll(AMPointOfInterestRegistry.END_PORTAL_FRAME.getPredicate(), Predicates.alwaysTrue(), blockpos, range, PointOfInterestManager.Status.ANY);
            return stream.collect(Collectors.toList());
        }else{
            Random random = new Random();
            for(int i = 0; i < 256; i++){
                BlockPos checkPos = blockpos.add(random.nextInt(range) - range/2, random.nextInt(range)/2 - range/2, random.nextInt(range) - range/2);
                if(world.getBlockState(checkPos).getBlock() == Blocks.CAVE_AIR && world.getLight(checkPos) < 4){
                    return Collections.singletonList(checkPos);
                }
            }
            return Collections.emptyList();
        }

    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity livingEntityIn, Hand handIn) {
        ItemStack stack = livingEntityIn.getHeldItem(handIn);
        boolean left = false;
        if (livingEntityIn.getActiveHand() == Hand.OFF_HAND && livingEntityIn.getPrimaryHand() == HandSide.RIGHT || livingEntityIn.getActiveHand() == Hand.MAIN_HAND && livingEntityIn.getPrimaryHand() == HandSide.LEFT) {
            left = true;
        }
        EntityCachalotEcho whaleEcho = new EntityCachalotEcho(worldIn, livingEntityIn, !left);
        if (!worldIn.isRemote && worldIn instanceof ServerWorld) {
            BlockPos playerPos = livingEntityIn.getPosition();
            List<BlockPos> portals = getNearbyPortals(playerPos, (ServerWorld) worldIn, 128);
            BlockPos pos = null;
            if(ender){
                for (BlockPos portalPos : portals) {
                    if (pos == null || pos.distanceSq(playerPos) > portalPos.distanceSq(playerPos)) {
                        pos = portalPos;
                    }
                }
            }else{
                CompoundNBT nbt = stack.getOrCreateTag();
                if(nbt.contains("CavePos") && nbt.getBoolean("ValidCavePos")){
                    pos = BlockPos.fromLong(nbt.getLong("CavePos"));
                    if(worldIn.getBlockState(pos).getBlock() != Blocks.CAVE_AIR ||worldIn.getLight(pos) >= 4){
                        nbt.putBoolean("ValidCavePos", false);
                    }
                }else{
                    for (BlockPos portalPos : portals) {
                        if (pos == null || pos.distanceSq(playerPos) < portalPos.distanceSq(playerPos)) {
                            pos = portalPos;
                        }
                    }
                    if(pos != null){
                        nbt.putLong("CavePos", pos.toLong());
                        nbt.putBoolean("ValidCavePos", true);
                        stack.setTag(nbt);
                    }
                }

            }
            if (pos != null) {
                double d0 = pos.getX() + 0.5F - whaleEcho.getPosX();
                double d1 = pos.getY() + 0.5F - whaleEcho.getPosY();
                double d2 = pos.getZ() + 0.5F - whaleEcho.getPosZ();
                whaleEcho.ticksExisted = 15;
                whaleEcho.shoot(d0, d1, d2, 0.4F, 0.3F);
                worldIn.addEntity(whaleEcho);
                stack.damageItem(1, livingEntityIn, (player) -> {
                    player.sendBreakAnimation(livingEntityIn.getActiveHand());
                });
            }
        }
        livingEntityIn.getCooldownTracker().setCooldown(this, 5);

        return ActionResult.func_233538_a_(stack, worldIn.isRemote());
    }
}
