package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BananaLootModifier implements IGlobalLootModifier {

    public static final MapCodec<BananaLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    inst.group(
                                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions)
                            )
                            .apply(inst, BananaLootModifier::new));

    private final LootItemCondition[] conditions;

    private final Predicate<LootContext> orConditions;

    public BananaLootModifier(LootItemCondition[] conditionsIn) {
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

    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context){
        if (AMConfig.bananasDropFromLeaves){
            ItemStack ctxTool = context.getParamOrNull(LootContextParams.TOOL);
            RandomSource random = context.getRandom();
            if(ctxTool != null){
                int silkTouch = ctxTool.getEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH));
                if(silkTouch > 0 || ctxTool.getItem() instanceof ShearsItem){
                    return generatedLoot;
                }
            }
            int bonusLevel = ctxTool != null ? ctxTool.getEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE)) : 0;
            int bananaStep = (int)Math.floor(AMConfig.bananaChance * 0.1F);
            int bananaRarity = AMConfig.bananaChance - (bonusLevel * bananaStep);
            if (bananaRarity < 1 || random.nextInt(bananaRarity) == 0) {
                generatedLoot.add(new ItemStack(AMItemRegistry.BANANA.get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}