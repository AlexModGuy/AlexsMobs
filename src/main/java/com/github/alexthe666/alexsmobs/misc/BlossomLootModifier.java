package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnStructureModifier;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class BlossomLootModifier extends LootModifier {

    public BlossomLootModifier(){
        super(null);
    }

    public BlossomLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context){
        if (AMConfig.acaciaBlossomsDropFromLeaves){
            ItemStack ctxTool = context.getParamOrNull(LootContextParams.TOOL);
            RandomSource random = context.getRandom();
            if(ctxTool != null){
                int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, ctxTool);
                if(silkTouch > 0 || ctxTool.getItem() instanceof ShearsItem){
                    return generatedLoot;
                }
            }
            int bonusLevel = ctxTool != null ? EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, ctxTool) : 0;
            int bananaStep = (int)Math.min(AMConfig.blossomChance * 0.1F, 0);
            int bananaRarity = AMConfig.blossomChance - (bonusLevel * bananaStep);
            if (bananaRarity < 1 || random.nextInt(bananaRarity) == 0) {
                generatedLoot.add(new ItemStack(AMItemRegistry.ACACIA_BLOSSOM.get()));
            }
        }
        return generatedLoot;
    }

    private static final Codec<BlossomLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, BlossomLootModifier::new)); ;

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }


    public static Codec<BlossomLootModifier> makeCodec() {
        return Codec.unit(BlossomLootModifier::new);
    }
}