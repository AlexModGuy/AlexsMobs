package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

import java.util.Set;

public class MatchesBlossomTagCondition implements ILootCondition {

    private ITag<Block> match;

    private MatchesBlossomTagCondition() {
        match = BlockTags.getCollection().get(AMTagRegistry.DROPS_ACACIA_BLOSSOMS);
    }

    public LootConditionType func_230419_b_() {
        return CommonProxy.MATCHES_BLOSSOM_CONDTN;
    }

    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of();
    }

    public boolean test(LootContext p_test_1_) {
        if(match == null){
            match = BlockTags.getCollection().get(AMTagRegistry.DROPS_ACACIA_BLOSSOMS);
        }
        BlockState block = p_test_1_.get(LootParameters.BLOCK_STATE);
        return block != null && match.contains(block.getBlock());
    }

    public static class Serializer implements ILootSerializer<MatchesBlossomTagCondition> {
        public Serializer() {
        }

        public void serialize(JsonObject p_230424_1_, MatchesBlossomTagCondition p_230424_2_, JsonSerializationContext p_230424_3_) {
        }

        public MatchesBlossomTagCondition deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            return new MatchesBlossomTagCondition();
        }
    }
}
