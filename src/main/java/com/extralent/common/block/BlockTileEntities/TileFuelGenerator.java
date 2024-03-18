package com.extralent.common.block.BlockTileEntities;

import com.extralent.api.tools.Interfaces.IGuiTile;
import com.extralent.api.tools.Interfaces.IRestorableTileEntity;
import com.extralent.common.block.FuelGenerator.ContainerFuelGenerator;
import com.extralent.common.block.FuelGenerator.GuiFuelGenerator;
import com.extralent.common.block.FuelGenerator.MachineState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class TileFuelGenerator extends TileMachineEntity implements ITickable, IRestorableTileEntity, IGuiTile {

    public static final int INPUT_SLOTS = 1;

    private MachineState state = MachineState.OFF;

    private int totalBurnTime = 0;

    private int clientEnergy = -1;

    public TileFuelGenerator() {
        super(INPUT_SLOTS, 350000, 0);
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (progress > 0) {
                energyStorage.generatePower(totalBurnTime / 80);
                markDirty();

                progress--;
            } else {
                progress = 0;

                if (!canSmelt()) {
                    return;
                }

                start();
            }

            sendEnergy();
        }
    }

    private void start() {
        ItemStack input = inputHandler.getStackInSlot(0);
        if (!input.isEmpty()) {
            int fuelBurnTime = getBurnTime(input);
            if (fuelBurnTime > 0) {
                totalBurnTime = fuelBurnTime;
                progress = fuelBurnTime;

                inputHandler.extractItem(0, 1, false);
                markDirty();
            }
        }
    }

    private boolean canSmelt() {
        ItemStack input = inputHandler.getStackInSlot(0);
        if (isFuelItemValidForSlot(input)) {
            markDirty();
            return true;
        }

        return false;
    }

    public static int getBurnTime(ItemStack item) {
        return TileEntityFurnace.getItemBurnTime(item);
    }

    public boolean isFuelItemValidForSlot(ItemStack item) {
        return TileEntityFurnace.isItemFuel(item);
    }

    public int getCurrentMaxBurnTime() {
        if (progress <= 0 || totalBurnTime <= 0) {
            return 0;
        }
        return (progress * 10) / (totalBurnTime);
    }

    public int getClientEnergy() {
        return clientEnergy;
    }

    public void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    //------------------------------------------------------------------------

    private void sendEnergy() {
        if (energyStorage.getEnergyStored() <= 0) {
            return;
        }

        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity tileEntity = world.getTileEntity(pos.offset(facing));
            if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                IEnergyStorage handler = tileEntity.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (handler != null && handler.canReceive()) {
                    int accepted = handler.receiveEnergy(energyStorage.getEnergyStored(), false);
                    energyStorage.consumePower(accepted);
                    if (energyStorage.getEnergyStored() <= 0) {
                        break;
                    }
                }
            }
        }
        markDirty();
    }

    //------------------------------------------------------------------------

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTag = super.getUpdateTag();
        nbtTag.setInteger("state", state.ordinal());
        return nbtTag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        int stateIndex = packet.getNbtCompound().getInteger("state");

        if (world.isRemote && stateIndex != state.ordinal()) {
            state = MachineState.VALUES[stateIndex];
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    public void setState(MachineState state) {
        if (this.state != state) {
            this.state = state;
            markDirty();
            IBlockState blockState = world.getBlockState(pos);
            getWorld().notifyBlockUpdate(pos, blockState, blockState, 3);
        }
    }

    public MachineState getState() {
        return state;
    }

    //------------------------------------------------------------------------

    private final ItemStackHandler inputHandler = new ItemStackHandler(INPUT_SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot < 2;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileFuelGenerator.this.markDirty();
        }
    };

    private final CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler);

    //------------------------------------------------------------------------

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readRestorableFromNBT(compound);
        state = MachineState.VALUES[compound.getInteger("state")];
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("itemsIn")) {
            inputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsIn"));
        }
        progress = compound.getInteger("progress");
        totalBurnTime = compound.getInteger("totalBurnTime");
        energyStorage.setEnergy(compound.getInteger("energy"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeRestorableToNBT(compound);
        compound.setInteger("state", state.ordinal());
        return compound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound compound) {
        compound.setTag("itemsIn", inputHandler.serializeNBT());
        compound.setInteger("progress", progress);
        compound.setInteger("totalBurnTime", totalBurnTime);
        compound.setInteger("energy", energyStorage.getEnergyStored());
    }

    @Override
    public Container createContainer(EntityPlayer player) {
        return new ContainerFuelGenerator(player.inventory, this);
    }

    @Override
    public GuiContainer createGui(EntityPlayer player) {
        return new GuiFuelGenerator(this, new ContainerFuelGenerator(player.inventory, this));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(combinedHandler);
            } else if (facing == EnumFacing.UP) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inputHandler);
            }
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }
}
