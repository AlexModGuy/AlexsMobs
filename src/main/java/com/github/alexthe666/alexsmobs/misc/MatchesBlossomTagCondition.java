package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;

import java.util.Set;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class MatchesBlossomTagCondition implements LootItemCondition {

    private Tag<Block> match;

    private MatchesBlossomTagCondition() {
        match = BlockTags.getAllTags().getTag(AMTagRegistry.DROPS_ACACIA_BLOSSOMS);
    }

    public LootItemConditionType getType() {
        return CommonProxy.MATCHES_BLOSSOM_CONDTN;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public boolean test(LootContext p_test_1_) {
        if(match == null){
            match = BlockTags.getAllTags().getTag(AMTagRegistry.DROPS_ACACIA_BLOSSOMS);
        }
        BlockState block = p_test_1_.getParamOrNull(LootContextParams.BLOCK_STATE);
        return block != null && match != null && match.contains(block.getBlock());
    }

    public static class LootSerializer implements Serializer<MatchesBlossomTagCondition> {
        public LootSerializer() {
        }

        public void serialize(JsonObject p_230424_1_, MatchesBlossomTagCondition p_230424_2_, JsonSerializationContext p_230424_3_) {
        }

        public MatchesBlossomTagCondition deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            return new MatchesBlossomTagCondition();
        }
    }
}
