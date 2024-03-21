package com.extralent.common.block.FuelGenerator;

import com.extralent.api.network.Messages;
import com.extralent.api.network.PacketSyncMachineState;
import com.extralent.api.tools.Interfaces.IMachineStateContainer;
import com.extralent.common.base.gui.GenericContainer;
import com.extralent.common.tile.TileFuelGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerFuelGenerator extends GenericContainer<TileFuelGenerator> implements IMachineStateContainer {

    public ContainerFuelGenerator(IInventory playerInventory, TileFuelGenerator tileEntity) {
        super(TileFuelGenerator.INPUT_SLOTS, playerInventory, tileEntity);
        addOwnSlots();
    }

    private void addOwnSlots() {
        IItemHandler itemHandler = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        int x = 39;
        int y = 34;

        addSlotToContainer(new SlotItemHandler(itemHandler, 0, x, y));
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
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
