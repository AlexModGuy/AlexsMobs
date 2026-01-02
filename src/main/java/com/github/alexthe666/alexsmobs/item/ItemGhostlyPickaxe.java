package com.github.alexthe666.alexsmobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class ItemGhostlyPickaxe extends PickaxeItem {

    public ItemGhostlyPickaxe(Properties props) {
        super(Tiers.IRON, props);
    }

    public static boolean shouldStoreInGhost(LivingEntity player, ItemStack stack){
        return player instanceof Player && ((Player)player).getInventory().getFreeSlot() == -1 ;
    }

    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        return blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? 20.0F : 1.0F;
    }

    public static void putItemInGhostInventoryOrDrop(LivingEntity user, ItemStack pickaxe, ItemStack item) {
        CustomData customData = pickaxe.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        SimpleContainer container = new SimpleContainer(9);
        if(compoundtag.contains("Items")){
            net.minecraft.nbt.ListTag listtag = compoundtag.getList("Items", 10);
            for(int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag1 = listtag.getCompound(i);
                int j = compoundtag1.getByte("Slot") & 255;
                if (j >= 0 && j < container.getContainerSize()) {
                    container.setItem(j, ItemStack.parseOptional(user.registryAccess(), compoundtag1));
                }
            }
        }
        if(user instanceof Player){
            Player player = (Player) user;
            if(player.getInventory().add(item)){
                return;
            }else if(container.canAddItem(item)){
                ItemStack leftover = container.addItem(item);
                net.minecraft.nbt.ListTag listtag = new net.minecraft.nbt.ListTag();
                for(int i = 0; i < container.getContainerSize(); ++i) {
                    ItemStack itemstack = container.getItem(i);
                    if (!itemstack.isEmpty()) {
                        CompoundTag compoundtag1 = (CompoundTag) itemstack.save(user.registryAccess());
                        compoundtag1.putByte("Slot", (byte)i);
                        listtag.add(compoundtag1);
                    }
                }
                compoundtag.put("Items", listtag);
                pickaxe.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundtag));
                item = leftover;

            }
        }
        if(!item.isEmpty()){
            user.spawnAtLocation(item);
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean offhand) {
        super.inventoryTick(stack, level, entity, i, offhand);
        if(entity instanceof Player){
            Player player = (Player) entity;
            if(player.tickCount % 3 == 0){
                CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                CompoundTag compoundtag = customData.copyTag();
                SimpleContainer container = new SimpleContainer(9);
                boolean flag = false;
                if(compoundtag.contains("Items")){
                    net.minecraft.nbt.ListTag listtag = compoundtag.getList("Items", 10);
                    for(int k = 0; k < listtag.size(); ++k) {
                        CompoundTag compoundtag1 = listtag.getCompound(k);
                        int j = compoundtag1.getByte("Slot") & 255;
                        if (j >= 0 && j < container.getContainerSize()) {
                            container.setItem(j, ItemStack.parseOptional(entity.registryAccess(), compoundtag1));
                        }
                    }
                }
                for(int slot = 0; slot < container.getContainerSize(); slot++) {
                    ItemStack stackAt = container.getItem(slot);
                    if(!stackAt.isEmpty() && player.addItem(stackAt)){
                        container.removeItem(slot, stackAt.getCount());
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    net.minecraft.nbt.ListTag listtag = new net.minecraft.nbt.ListTag();
                    for(int k = 0; k < container.getContainerSize(); ++k) {
                        ItemStack itemstack = container.getItem(k);
                        if (!itemstack.isEmpty()) {
                            CompoundTag compoundtag1 = (CompoundTag) itemstack.save(entity.registryAccess());
                            compoundtag1.putByte("Slot", (byte)k);
                            listtag.add(compoundtag1);
                        }
                    }
                    compoundtag.put("Items", listtag);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundtag));
                }
            }
        }
    }

    public boolean isValidRepairItem(ItemStack pickaxe, ItemStack stack) {
        return stack.is(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        if (compoundtag != null && compoundtag.contains("Items", 9)) {
            SimpleContainer container = new SimpleContainer(9);
            net.minecraft.nbt.ListTag listtag = compoundtag.getList("Items", 10);
            for(int k = 0; k < listtag.size(); ++k) {
                CompoundTag compoundtag1 = listtag.getCompound(k);
                int j = compoundtag1.getByte("Slot") & 255;
                if (j >= 0 && j < container.getContainerSize()) {
                    container.setItem(j, ItemStack.parseOptional(context.registries(), compoundtag1));
                }
            }
            int i = 0;
            int j = 0;

            for(int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack itemstack = container.getItem(slot);
                if (!itemstack.isEmpty()) {
                    ++j;
                    if (i <= 4) {
                        ++i;
                        MutableComponent mutablecomponent = itemstack.getHoverName().copy();
                        mutablecomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                        tooltip.add(mutablecomponent.withStyle(ChatFormatting.DARK_AQUA));
                    }
                }
            }

            if (j - i > 0) {
                tooltip.add(Component.translatable("container.shulkerBox.more", j - i).withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));
            }
        }
    }

    private void dropAllContents(Level level, Vec3 vec3, ItemStack pickaxe){
        CustomData customData = pickaxe.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        if (compoundtag != null && compoundtag.contains("Items", 9)) {
            SimpleContainer container = new SimpleContainer(9);
            net.minecraft.nbt.ListTag listtag = compoundtag.getList("Items", 10);
            for(int k = 0; k < listtag.size(); ++k) {
                CompoundTag compoundtag1 = listtag.getCompound(k);
                int j = compoundtag1.getByte("Slot") & 255;
                if (j >= 0 && j < container.getContainerSize()) {
                    container.setItem(j, ItemStack.parseOptional(level.registryAccess(), compoundtag1));
                }
            }
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack itemstack = container.getItem(slot);
                if (!itemstack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, itemstack.copy());
                    if(level.addFreshEntity(itemEntity)){
                        container.removeItem(slot, itemstack.getCount());
                    }
                }
            }
            net.minecraft.nbt.ListTag listtag1 = new net.minecraft.nbt.ListTag();
            for(int k = 0; k < container.getContainerSize(); ++k) {
                ItemStack itemstack = container.getItem(k);
                if (!itemstack.isEmpty()) {
                    CompoundTag compoundtag1 = (CompoundTag) itemstack.save(level.registryAccess());
                    compoundtag1.putByte("Slot", (byte)k);
                    listtag1.add(compoundtag1);
                }
            }
            compoundtag.put("Items", listtag1);
            pickaxe.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundtag));
        }
    }

    public void onDestroyed(ItemEntity itemEntity) {
        dropAllContents(itemEntity.level(), itemEntity.position(), itemEntity.getItem());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        int i = super.damageItem(stack, amount, entity, onBroken);
        if(i + stack.getDamageValue() >= stack.getMaxDamage() && entity != null){
            dropAllContents(entity.level(), entity.position(), stack);
        }
        return i;
    }

    public int getMaxDamage(ItemStack stack) {
        return 700;
    }
}
