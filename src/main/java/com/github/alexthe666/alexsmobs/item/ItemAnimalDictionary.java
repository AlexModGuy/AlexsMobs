package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemAnimalDictionary extends Item {
    public ItemAnimalDictionary(Properties properties) {
        super(properties);
    }

    private boolean usedOnEntity = false;

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)playerIn;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStackIn);
            serverplayerentity.addStat(Stats.ITEM_USED.get(this));
        }
        if (playerIn.world.isRemote && Objects.requireNonNull(target.getEntityString()).contains(AlexsMobs.MODID + ":")) {
            usedOnEntity = true;
            AlexsMobs.PROXY.openBookGUI(itemStackIn, target.getEntityString().replace(AlexsMobs.MODID + ":", ""));
        }
        return ActionResultType.PASS;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStackIn = playerIn.getHeldItem(handIn);
        if (!usedOnEntity) {
            if (playerIn instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) playerIn;
                CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStackIn);
                serverplayerentity.addStat(Stats.ITEM_USED.get(this));
            }
            if (worldIn.isRemote) {
                AlexsMobs.PROXY.openBookGUI(itemStackIn);
            }
        }
        usedOnEntity = false;

        return new ActionResult(ActionResultType.PASS, itemStackIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.alexsmobs.animal_dictionary.desc").mergeStyle(TextFormatting.GRAY));
    }
}
