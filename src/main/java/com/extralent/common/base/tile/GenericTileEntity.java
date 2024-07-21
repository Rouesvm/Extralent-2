package com.extralent.common.base.tile;

import com.extralent.api.tools.TEnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class GenericTileEntity extends TileEntity {

    protected int Slots;
    protected int maxEnergyStored;
    protected int maxEnergyReceived;

    protected int progress;
    protected int clientProgress = -1;

    protected TEnergyStorage energyStorage;

    public GenericTileEntity(int Slots, int maxEnergyStored, int maxEnergyReceived) {
        this.Slots = Slots;

        this.maxEnergyStored = maxEnergyStored;
        this.maxEnergyReceived = maxEnergyReceived;

        this.energyStorage = new TEnergyStorage(maxEnergyStored, maxEnergyReceived);
    }

    public static boolean isSlotEmpty(int amount, ItemStackHandler slots) {
        for (int i = 0; i < amount; i++) {
            ItemStack empty = slots.getStackInSlot(i);
            if (empty.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllSlotEmpty(int amount, ItemStackHandler slots) {
        int isAllSlotEmpty = 0;

        for (int i = 0; i < amount; i++) {
            if (slots.getStackInSlot(i).isEmpty()) {
                isAllSlotEmpty++;
                if (isAllSlotEmpty == amount) {
                    return true;
                }
            }
        }
        return false;
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
