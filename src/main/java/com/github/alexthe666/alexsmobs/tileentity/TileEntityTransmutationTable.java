package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.BlockVoidWormBeak;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.misc.TransmutationData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TileEntityTransmutationTable  extends BlockEntity {

    private static final ResourceLocation COMMON_ITEMS = new ResourceLocation("alexsmobs", "gameplay/transmutation_table_common");
    private static final ResourceLocation UNCOMMON_ITEMS = new ResourceLocation("alexsmobs", "gameplay/transmutation_table_uncommon");
    private static final ResourceLocation RARE_ITEMS = new ResourceLocation("alexsmobs", "gameplay/transmutation_table_rare");
    public int ticksExisted;
    private Map<UUID, TransmutationData> playerToData = new HashMap<>();


    public TileEntityTransmutationTable(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.TRANSMUTATION_TABLE.get(), pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityTransmutationTable entity) {
        entity.tick();
    }

    private static ItemStack createFromLootTable(Player player, ResourceLocation loc) {
        if(player.level.isClientSide){
            return ItemStack.EMPTY;
        }else{
            LootTable loottable = player.level.getServer().getLootTables().get(loc);
            List<ItemStack> loots = loottable.getRandomItems((new LootContext.Builder((ServerLevel) player.level)).withParameter(LootContextParams.THIS_ENTITY, player).withRandom(player.level.random).create(LootContextParamSets.EMPTY));
            return loots.isEmpty() ? ItemStack.EMPTY : loots.get(0);
        }
    }


    public void load(CompoundTag tag) {
        super.load(tag);
        ListTag list = tag.getList("PlayerTransmutationData", 10);
        if(!list.isEmpty()){
            for(int i = 0; i < list.size(); ++i) {
                CompoundTag compoundtag = list.getCompound(i);
                UUID uuid = compoundtag.getUUID("UUID");
                if(uuid != null){
                    playerToData.put(uuid, TransmutationData.fromNBT(compoundtag.getCompound("TransmutationData")));
                }
            }
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag list = new ListTag();
        for(Map.Entry<UUID, TransmutationData> entry : playerToData.entrySet()){
            CompoundTag innerTag = new CompoundTag();
            innerTag.putUUID("UUID", entry.getKey());
            innerTag.put("TransmutationData", entry.getValue().saveAsNBT());
            list.add(innerTag);
        }
        tag.put("PlayerTransmutationData", list);
    }


    public void tick() {
        ticksExisted++;
    }
}
