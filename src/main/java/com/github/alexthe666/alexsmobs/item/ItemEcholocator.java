package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotEcho;
import com.github.alexthe666.alexsmobs.message.MessageSetPupfishChunkOnClient;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemEcholocator extends Item {

    public EchoType type;

    public ItemEcholocator(Item.Properties properties, EchoType ender) {
        super(properties);
        this.type = ender;
    }

    private List<BlockPos> getNearbyPortals(BlockPos blockpos, ServerLevel world, int range) {
        if(type == EchoType.ENDER){
            PoiManager pointofinterestmanager = world.getPoiManager();
            Stream<BlockPos> stream = pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(AMPointOfInterestRegistry.END_PORTAL_FRAME.getKey()), Predicates.alwaysTrue(), blockpos, range, PoiManager.Occupancy.ANY);
            List<BlockPos> portals = stream.collect(Collectors.toList());
            if(portals.isEmpty()){
                BlockPos nearestMapStructure = world.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, blockpos, 100, false);
                return nearestMapStructure == null ? Collections.emptyList() : List.of(nearestMapStructure);
            }else{
                return portals;
            }
        }else  if(type == EchoType.PUPFISH){
            AMWorldData data = AMWorldData.get(world);
            if(data != null && data.getPupfishChunk() != null){
                AlexsMobs.sendMSGToAll(new MessageSetPupfishChunkOnClient(data.getPupfishChunk().x, data.getPupfishChunk().z));
                if(!data.isInPupfishChunk(blockpos)){
                    return Collections.singletonList(data.getPupfishChunk().getMiddleBlockPosition(blockpos.getY()));
                }
            }
            return Collections.emptyList();
        }else{
            RandomSource random = world.getRandom();
            for(int i = 0; i < 256; i++){
                BlockPos checkPos = blockpos.offset(random.nextInt(range) - range/2, random.nextInt(range)/2 - range/2, random.nextInt(range) - range/2);
                if(isCaveAir(world, checkPos)){
                    return Collections.singletonList(checkPos);
                }
            }
            return Collections.emptyList();
        }
    }

    private boolean isCaveAir(Level world, BlockPos checkPos){
        return world.getBlockState(checkPos).isAir() && world.getBrightness(LightLayer.SKY, checkPos) == 0 && world.getBrightness(LightLayer.BLOCK, checkPos) < 4;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player livingEntityIn, InteractionHand handIn) {
        ItemStack stack = livingEntityIn.getItemInHand(handIn);
        boolean left = false;
        if (livingEntityIn.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntityIn.getMainArm() == HumanoidArm.RIGHT || livingEntityIn.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntityIn.getMainArm() == HumanoidArm.LEFT) {
            left = true;
        }
        EntityCachalotEcho whaleEcho = new EntityCachalotEcho(worldIn, livingEntityIn, !left, type == EchoType.PUPFISH);
        if (!worldIn.isClientSide && worldIn instanceof ServerLevel) {
            BlockPos playerPos = livingEntityIn.blockPosition();
            List<BlockPos> portals = getNearbyPortals(playerPos, (ServerLevel) worldIn, 128);
            BlockPos pos = null;
            if(type == EchoType.ENDER){
                for (BlockPos portalPos : portals) {
                    if (pos == null || pos.distSqr(playerPos) > portalPos.distSqr(playerPos)) {
                        pos = portalPos;
                    }
                }
            }else if(type == EchoType.PUPFISH){
                for (BlockPos portalPos : portals) {
                    if (pos == null || pos.distSqr(playerPos) > portalPos.distSqr(playerPos)) {
                        pos = portalPos;
                    }
                }
            }else{
                CompoundTag nbt = stack.getOrCreateTag();
                if(nbt.contains("CavePos") && nbt.getBoolean("ValidCavePos")){
                    pos = BlockPos.of(nbt.getLong("CavePos"));
                    if(isCaveAir(worldIn, pos) || 1000000 < pos.distSqr(playerPos)){
                        nbt.putBoolean("ValidCavePos", false);
                    }
                }else{
                    for (BlockPos portalPos : portals) {
                        if (pos == null || pos.distSqr(playerPos) < portalPos.distSqr(playerPos)) {
                            pos = portalPos;
                        }
                    }
                    if(pos != null){
                        nbt.putLong("CavePos", pos.asLong());
                        nbt.putBoolean("ValidCavePos", true);
                        stack.setTag(nbt);
                    }
                }

            }
            if (pos != null) {
                double d0 = pos.getX() + 0.5F - whaleEcho.getX();
                double d1 = pos.getY() + 0.5F - whaleEcho.getY();
                double d2 = pos.getZ() + 0.5F - whaleEcho.getZ();
                whaleEcho.tickCount = 15;
                whaleEcho.shoot(d0, d1, d2, 0.4F, 0.3F);
                worldIn.addFreshEntity(whaleEcho);
                livingEntityIn.gameEvent(GameEvent.ITEM_INTERACT_START);
                worldIn.playSound((Player)null, whaleEcho.getX(), whaleEcho.getY(), whaleEcho.getZ(), AMSoundRegistry.CACHALOT_WHALE_CLICK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                stack.hurtAndBreak(1, livingEntityIn, (player) -> {
                    player.broadcastBreakEvent(livingEntityIn.getUsedItemHand());
                });
            }
        }
        livingEntityIn.getCooldowns().addCooldown(this, 5);

        return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide());
    }

    public enum EchoType {
        ECHOLOCATION, ENDER, PUPFISH
    }
}
