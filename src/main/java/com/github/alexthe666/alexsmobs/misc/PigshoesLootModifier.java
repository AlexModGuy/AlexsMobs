package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class PigshoesLootModifier implements IGlobalLootModifier {

    public static final MapCodec<PigshoesLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    inst.group(
                                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions)
                            )
                            .apply(inst, PigshoesLootModifier::new));

    private final LootItemCondition[] conditions;

    private final Predicate<LootContext> orConditions;

    public PigshoesLootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
        this.orConditions = (context) -> {
            for (LootItemCondition condition : conditionsIn) {
                if (condition.test(context)) return true;
            }
            return false;
        };
    }


    @NotNull
    @Override
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return this.orConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
    }

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.addLootToChests) {
            if (context.getRandom().nextFloat() <= AMConfig.tusklinShoesBarteringChance) {
                generatedLoot.add(new ItemStack(AMItemRegistry.PIGSHOES.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}