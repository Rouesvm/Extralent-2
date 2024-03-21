package com.extralent.common.block.ElectricFurnace;

import com.extralent.api.network.Messages;
import com.extralent.api.network.PacketSyncMachineState;
import com.extralent.api.tools.Interfaces.IMachineStateContainer;
import com.extralent.common.base.gui.GenericContainer;
import com.extralent.common.config.ElectricFurnaceConfig;
import com.extralent.common.tile.TileElectricFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerElectricFurnace extends GenericContainer<TileElectricFurnace> implements IMachineStateContainer {

    public ContainerElectricFurnace(IInventory playerInventory, TileElectricFurnace tileEntity) {
        super(TileElectricFurnace.SIZE, playerInventory, tileEntity);
        addOwnSlots();
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
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return tileEntity.canInteractWith(playerIn);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!tileEntity.getWorld().isRemote) {
            if (tileEntity.getEnergy() != tileEntity.getClientEnergy() ||
                    tileEntity.getProgress() != tileEntity.getClientProgress())
            {
                sync(tileEntity.getEnergy(), tileEntity.getProgress());

                for (IContainerListener listener : listeners) {
                    if (listener instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) listener;
                        int percentage = GuiElectricFurnace.arrowWidth - tileEntity.getClientProgress() * GuiElectricFurnace.arrowWidth / ElectricFurnaceConfig.MAX_PROGRESS;
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
