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

import java.util.Set;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import static net.minecraft.world.level.storage.loot.predicates.LootItemConditions.WEATHER_CHECK;

public class MatchesBlossomTagCondition implements LootItemCondition {


    private MatchesBlossomTagCondition() {
    }

    public LootItemConditionType getType() {
        return WEATHER_CHECK;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public boolean test(LootContext p_test_1_) {
        BlockState block = p_test_1_.getParamOrNull(LootContextParams.BLOCK_STATE);
        return block != null && block.is(AMTagRegistry.DROPS_ACACIA_BLOSSOMS);
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
