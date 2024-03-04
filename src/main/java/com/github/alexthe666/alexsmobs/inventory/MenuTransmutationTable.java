package com.github.alexthe666.alexsmobs.inventory;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.message.MessageTransmuteFromMenu;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MenuTransmutationTable extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private long lastSoundTime;
    private final Player player;
    private final Slot transmuteSlot;
    private TileEntityTransmutationTable table;

    public final Container container = new SimpleContainer(1) {
        public void setChanged() {
            MenuTransmutationTable.this.slotsChanged(this);
            super.setChanged();
        }
    };

    public MenuTransmutationTable(int i, Inventory inventory) {
        this(i, inventory, ContainerLevelAccess.NULL, AlexsMobs.PROXY.getClientSidePlayer(), null);
    }

    public MenuTransmutationTable(int id, Inventory inventory, final ContainerLevelAccess access, Player player, TileEntityTransmutationTable table) {
        super(AMMenuRegistry.TRANSMUTATION_TABLE.get(), id);
        this.table = table;
        this.player = player;
        this.access = access;
        this.addSlot(transmuteSlot = new Slot(this.container, 0, 83, 83) {
            public boolean mayPlace(ItemStack stack) {
                ResourceLocation name = ForgeRegistries.ITEMS.getKey(stack.getItem());
                return stack.getMaxStackSize() > 1 && (name == null || !AMConfig.transmutationBlacklist.contains(name.toString()));
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 119 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 177));
        }
        if(table != null && player != null){
            if(!table.hasPossibilities()){
                table.setRerollPlayerUUID(player.getUUID());
            }
        }
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, AMBlockRegistry.TRANSMUTATION_TABLE.get());
    }

    public void slotsChanged(Container container) {
        if(table != null){
            if(!table.hasPossibilities()){
                table.setRerollPlayerUUID(player.getUUID());
            }
        }
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotIndex != 0) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 1, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    public boolean clickMenuButton(Player player, int buttonId) {
        if(player.level().isClientSide){
            AlexsMobs.sendMSGToServer(new MessageTransmuteFromMenu(player.getId(), buttonId));
        }
        return true;
    }


    public void transmute(Player player, int buttonId){
        ItemStack from = transmuteSlot.getItem();
        int cost = AMConfig.transmutingExperienceCost;
        ItemStack setTo = table.getPossibility(buttonId).copy();
        double divisible = from.getMaxStackSize() / (double)setTo.getMaxStackSize();
        if(!player.level().isClientSide && table != null && divisible > 0 && table.hasPossibilities() && !from.isEmpty() && (player.experienceLevel >= cost || player.getAbilities().instabuild)){
            int newStackSize = (int)Math.floor(from.getCount() / divisible);
            setTo.setCount(Math.max(newStackSize, 1));
            transmuteSlot.set(setTo);
            player.giveExperienceLevels(-cost);
            table.postTransmute(player, from, setTo);
        }
    }

    public void removed(Player player) {
        super.removed(player);
        this.access.execute((p_39152_, p_39153_) -> {
            this.clearContainer(player, this.container);
        });
    }
}