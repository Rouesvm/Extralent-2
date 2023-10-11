package com.extralent.common.block.ElectricFurnace;

import com.extralent.api.network.Messages;
import com.extralent.api.network.PacketSyncMachineState;
import com.extralent.api.tools.IMachineStateContainer;
import com.extralent.common.config.ElectricFurnaceConfig;
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

public class ContainerElectricFurnace extends Container implements IMachineStateContainer {

    private final TileElectricFurnace tileEntity;

    private static final int PROGRESS_ID = 0;

    public ContainerElectricFurnace(IInventory playerInventory, TileElectricFurnace tileEntity) {
        this.tileEntity = tileEntity;

        // This container references items out of our own inventory (the 9 slots we hold ourselves)
        // as well as the slots from the player inventory so that the user can transfer items between
        // both inventories. The two calls below make sure that slots are defined for both inventories.
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
        int x = 48;
        int y = 6;

        int slotIndex = 0;
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y)); y += 20; x -= 14;
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y)); y += 20; x += 14;
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y)); y -= 20;

        x = 107;
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y)); x += 18;
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y)); x += 18;
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (index < TileElectricFurnace.SIZE) {
                if (!this.mergeItemStack(itemStack1, TileElectricFurnace.SIZE, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemStack1, 0, TileElectricFurnace.SIZE, false)) {
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
                tileEntity.setClientEnergy(tileEntity.getEnergy());
                tileEntity.setClientProgress(tileEntity.getProgress());

                for (IContainerListener listener : listeners) {
                    if (listener instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) listener;
                        int arrowWidth = 26;
                        int percentage = arrowWidth - tileEntity.getClientProgress() * arrowWidth / ElectricFurnaceConfig.MAX_PROGRESS;
                        Messages.INSTANCE.sendTo(new PacketSyncMachineState(tileEntity.getEnergy(), percentage), player);
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
