package com.extralent.common.base.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class GenericContainer<TILE> extends Container {

    protected int SIZE;
    protected TILE tileEntity;
    protected IInventory playerInventory;

    public GenericContainer(int SIZE, IInventory playerInventory, TILE tile) {
        this.SIZE = SIZE;

        this.tileEntity = tile;
        this.playerInventory = playerInventory;

        addPlayerSlots(playerInventory);
    }

    protected void addPlayerSlots(IInventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 10 + col * 18;
                int y = row * 18 + 70;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
            }
        }

        for (int row = 0; row < 9; ++row) {
            int x = 10 + row * 18;
            int y = 58 + 70;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot currentSlot = inventorySlots.get(index);
        System.out.println(playerInventory.getSizeInventory());

        if (currentSlot != null && currentSlot.getHasStack()) {
            ItemStack slotStack = currentSlot.getStack();
            stack = slotStack.copy();

            if (index < SIZE) {
                if (!this.mergeItemStack(slotStack, SIZE, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 0, SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                currentSlot.putStack(ItemStack.EMPTY);
            } else {
                currentSlot.onSlotChanged();
            }
        }

        return stack;
    }
}
