package com.extralent.common.block.BlockTileEntities;

import com.extralent.api.tools.TEnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class TileMachineEntity extends TileEntity {

    protected int Slots;
    protected int maxEnergyStored;
    protected int maxEnergyReceived;

    protected int progress;
    protected int clientProgress = -1;

    protected TEnergyStorage energyStorage;

    public TileMachineEntity(int Slots, int maxEnergyStored, int maxEnergyReceived) {
        this.Slots = Slots;

        this.maxEnergyStored = maxEnergyStored;
        this.maxEnergyReceived = maxEnergyReceived;

        this.energyStorage = new TEnergyStorage(maxEnergyStored, maxEnergyReceived);
    }

    public int getProgress() {
        return progress;
    }

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    public int getClientProgress() {
        return clientProgress;
    }

    public void setClientProgress(int currentProgress) {
        this.clientProgress = currentProgress;
    }

    //-------------------------------------------------------------------------

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }
}
