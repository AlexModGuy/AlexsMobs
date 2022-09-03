package com.github.alexthe666.alexsmobs.inventory;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTransmutationTable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MenuTransmutationTable extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private long lastSoundTime;

    public final Container container = new SimpleContainer(1) {
        public void setChanged() {
            MenuTransmutationTable.this.slotsChanged(this);
            super.setChanged();
        }
    };

    public MenuTransmutationTable(int i, Inventory inventory) {
        this(i, inventory, ContainerLevelAccess.NULL, null);
    }

    public MenuTransmutationTable(int id, Inventory inventory, final ContainerLevelAccess access, TileEntityTransmutationTable table) {
        super(AMMenuRegistry.TRANSMUTATION_TABLE.get(), id);
        System.out.println(table);
        this.access = access;
        this.addSlot(new Slot(this.container, 0, 83, 83) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getMaxStackSize() > 1;
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

    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, AMBlockRegistry.TRANSMUTATION_TABLE.get());
    }

    public void slotsChanged(Container container) {
        ItemStack itemstack = this.container.getItem(0);
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

    public void removed(Player player) {
        super.removed(player);
        this.access.execute((p_39152_, p_39153_) -> {
            this.clearContainer(player, this.container);
        });
    }
}