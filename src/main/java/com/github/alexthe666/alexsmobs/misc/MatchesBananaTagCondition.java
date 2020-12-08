package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Set;

public class MatchesBananaTagCondition implements ILootCondition {

    private ITag<Block> match;

    private MatchesBananaTagCondition() {
        match = BlockTags.getCollection().get(AMTagRegistry.DROPS_BANANAS);
    }

    public LootConditionType func_230419_b_() {
        return CommonProxy.MATCHES_BANANA_CONDTN;
    }

    public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of();
    }

    public boolean test(LootContext p_test_1_) {
        if(match == null){
            match = BlockTags.getCollection().get(AMTagRegistry.DROPS_BANANAS);
        }
        BlockState block = p_test_1_.get(LootParameters.BLOCK_STATE);
        return block != null && match.contains(block.getBlock());
    }

    public static class Serializer implements ILootSerializer<MatchesBananaTagCondition> {
        public Serializer() {
        }

        public void serialize(JsonObject p_230424_1_, MatchesBananaTagCondition p_230424_2_, JsonSerializationContext p_230424_3_) {
        }

        public MatchesBananaTagCondition deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            return new MatchesBananaTagCondition();
        }
    }
}
