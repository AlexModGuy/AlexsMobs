package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.util.List;
import java.util.Random;

public class BlossomLootModifier extends LootModifier {

    public BlossomLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (AMConfig.acaciaBlossomsDropFromLeaves){
            ItemStack ctxTool = context.get(LootParameters.TOOL);
            Random random = context.getRandom();
            if(ctxTool != null){
                int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, ctxTool);
                if(silkTouch > 0 || ctxTool.getItem() instanceof ShearsItem){
                    return generatedLoot;
                }
            }
            int bonusLevel = ctxTool != null ? EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, ctxTool) : 0;
            int bananaStep = (int)Math.min(AMConfig.blossomChance * 0.1F, 0);
            int bananaRarity = AMConfig.blossomChance - (bonusLevel * bananaStep);
            if (bananaRarity < 1 || random.nextInt(bananaRarity) == 0) {
                generatedLoot.add(new ItemStack(AMItemRegistry.ACACIA_BLOSSOM));
            }
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<BlossomLootModifier> {

        @Override
        public BlossomLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new BlossomLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(BlossomLootModifier instance) {
            return null;
        }
    }
}