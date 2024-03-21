package com.extralent.common.block.FuseMachine;

import com.extralent.api.network.Messages;
import com.extralent.api.network.PacketSyncMachineState;
import com.extralent.api.tools.Interfaces.IMachineStateContainer;
import com.extralent.common.base.gui.GenericContainer;
import com.extralent.common.config.FuseMachineConfig;
import com.extralent.common.tile.TileFuseMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerFuseMachine extends GenericContainer<TileFuseMachine> implements IMachineStateContainer {

    public ContainerFuseMachine(IInventory playerInventory, TileFuseMachine tileEntity) {
        super(TileFuseMachine.SIZE, playerInventory, tileEntity);
        addOwnSlots();
    }

    private void addOwnSlots() {
        IItemHandler itemHandler = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        int x = 54;
        int y = 23;

        int slotIndex = 0;
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y));
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, x, y + 20));
        addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex++, 115, 33));
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
                sync(tileEntity.getEnergy(), tileEntity.getProgress());

                for (IContainerListener listener : listeners) {
                    if (listener instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) listener;
                        int percentage = GuiFuseMachine.arrowWidth - tileEntity.getClientProgress() * GuiFuseMachine.arrowWidth / FuseMachineConfig.MAX_PROGRESS;
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
