package com.extralent.common.block.FuelGenerator;

import com.extralent.api.network.Messages;
import com.extralent.api.network.PacketSyncMachineState;
import com.extralent.api.tools.Interfaces.IMachineStateContainer;
import com.extralent.common.block.BlockTileEntities.TileFuelGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerFuelGenerator extends Container implements IMachineStateContainer {

    private final TileFuelGenerator tileEntity;

    public ContainerFuelGenerator(IInventory playerInventory, TileFuelGenerator tileEntity) {
        this.tileEntity = tileEntity;
        addOwnSlots();
        addPlayerSlots(playerInventory);
    }

    private void addPlayerSlots(IInventory playerInventory) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 10 + col * 18;
                int y = row * 18 + 70;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 10 + row * 18;
            int y = 58 + 70;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    private void addOwnSlots() {
        IItemHandler itemHandler = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        int x = 39;
        int y = 34;

        addSlotToContainer(new SlotItemHandler(itemHandler, 0, x, y));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index < TileFuelGenerator.INPUT_SLOTS) {
                if (!this.mergeItemStack(itemStack1, TileFuelGenerator.INPUT_SLOTS, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemStack1, 0, TileFuelGenerator.INPUT_SLOTS, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileEntity.canInteractWith(playerIn);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!tileEntity.getWorld().isRemote) {
            if (tileEntity.getEnergy() != tileEntity.getClientEnergy() || tileEntity.getProgress() != tileEntity.getClientProgress()) {
                sync(tileEntity.getEnergy(), tileEntity.getCurrentMaxBurnTime());

                for (IContainerListener listener : listeners) {
                    if (listener instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) listener;
                        Messages.INSTANCE.sendTo(new PacketSyncMachineState(tileEntity.getEnergy(), tileEntity.getClientProgress()), player);
                    }
                }
            }
        }
    }

    @Override
    public void sync(int energy, int progress) {
        tileEntity.setClientEnergy(energy);
        tileEntity.setClientProgress(progress);
    }
}
