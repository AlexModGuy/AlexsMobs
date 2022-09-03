package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TransmutationData {
    private Map<ItemStack, Double> itemstackData = new HashMap<>();

    public void onTransmuteItem(ItemStack beingTransmuted, ItemStack turnedInto){
        double fromWeight = getWeight(beingTransmuted);
        double toWeight = getWeight(turnedInto);
        putWeight(beingTransmuted, fromWeight + 0.1D);
        putWeight(turnedInto, toWeight - 0.1D);
    }

    public double getWeight(ItemStack stack){
        for(Map.Entry<ItemStack, Double> entry : itemstackData.entrySet()){
            if(ItemStack.isSameItemSameTags(stack, entry.getKey())){
                return entry.getValue();
            }
        }
        return 0.0;
    }

    public void putWeight(ItemStack stack, double newWeight){
        ItemStack replace = stack;
        for(Map.Entry<ItemStack, Double> entry : itemstackData.entrySet()){
            if(ItemStack.isSameItemSameTags(stack, entry.getKey())){
                replace = entry.getKey();
                break;
            }
        }
        itemstackData.put(replace, Math.min(newWeight, 0.0F));
    }

    @Nullable
    public ItemStack getRandomItem(RandomSource random) {
        ItemStack result = null;
        double bestValue = Double.MAX_VALUE;
        for(Map.Entry<ItemStack, Double> entry : itemstackData.entrySet()){
            if(entry.getValue() <= 0.0){
                continue;
            }else{
                double value = -Math.log(random.nextDouble()) / entry.getValue();
                if (value < bestValue) {
                    bestValue = value;
                    result = entry.getKey();
                }
            }
        }
        return result.copy();
    }

    public CompoundTag saveAsNBT(){
        CompoundTag compound = new CompoundTag();
        ListTag listTag = new ListTag();
        for(Map.Entry<ItemStack, Double> entry : itemstackData.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put("Item", entry.getKey().save(new CompoundTag()));
            tag.putDouble("Weight", entry.getValue().doubleValue());
            listTag.add(tag);
        }
        compound.put("TransmutationData", listTag);
        return compound;
    }

    public static TransmutationData fromNBT(CompoundTag compound){
        TransmutationData data = new TransmutationData();
        if (compound.contains("TransmutationData")) {
            ListTag listtag = compound.getList("TransmutationData", 10);
            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag innerTag = listtag.getCompound(i);
                try{
                    ItemStack from = ItemStack.of(innerTag.getCompound("Item"));
                    if(!from.isEmpty()){
                        data.putWeight(from, innerTag.getDouble("Weight"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
