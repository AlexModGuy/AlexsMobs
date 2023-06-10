package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class AncientDartLootModifier extends LootModifier {

    public AncientDartLootModifier() {
        super(new LootItemCondition[0]);
    }

    public AncientDartLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.addLootToChests) {
            if (context.getRandom().nextInt(1) == 0) {
                generatedLoot.add(new ItemStack(AMItemRegistry.ANCIENT_DART.get()));
            }
        }
        return generatedLoot;
    }

    private static final Codec<AncientDartLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, AncientDartLootModifier::new));
    ;

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }


    public static Codec<AncientDartLootModifier> makeCodec() {
        return Codec.unit(AncientDartLootModifier::new);
    }
}