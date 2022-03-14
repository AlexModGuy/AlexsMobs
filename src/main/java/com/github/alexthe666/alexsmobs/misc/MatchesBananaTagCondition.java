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

public class MatchesBananaTagCondition implements LootItemCondition {


    private MatchesBananaTagCondition() {
    }

    public LootItemConditionType getType() {
        return CommonProxy.matchesBanana;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public boolean test(LootContext p_test_1_) {
        BlockState block = p_test_1_.getParamOrNull(LootContextParams.BLOCK_STATE);
        return block != null && block.is(AMTagRegistry.DROPS_BANANAS);
    }

    public static class LootSerializer implements Serializer<MatchesBananaTagCondition> {
        public LootSerializer() {
        }

        public void serialize(JsonObject p_230424_1_, MatchesBananaTagCondition p_230424_2_, JsonSerializationContext p_230424_3_) {
        }

        public MatchesBananaTagCondition deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
            return new MatchesBananaTagCondition();
        }
    }
}
