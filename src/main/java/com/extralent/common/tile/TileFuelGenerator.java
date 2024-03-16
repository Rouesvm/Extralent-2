package com.extralent.common.tile;

import com.extralent.api.tools.ETEnergyStorage;
import com.extralent.api.tools.Interfaces.IGuiTile;
import com.extralent.api.tools.Interfaces.IRestorableTileEntity;
import com.extralent.api.tools.MachineHelper;
import com.extralent.api.tools.RecipeAPI;
import com.extralent.client.sounds.SoundHandler;
import com.extralent.common.block.ElectricFurnace.FurnaceState;
import com.extralent.common.block.FuelGenerator.ContainerFuelGenerator;
import com.extralent.common.block.FuelGenerator.GuiFuelGenerator;
import com.extralent.common.block.FuelGenerator.MachineState;
import com.extralent.common.config.ElectricFurnaceConfig;
import com.extralent.common.config.FuseMachineConfig;
import com.extralent.common.core.handler.FuelHandler;
import com.extralent.common.recipe.RecipeHandler;
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
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileFuelGenerator extends TileEntity implements ITickable, IRestorableTileEntity, IGuiTile {

    public static final int INPUT_SLOTS = 1;
    public static final int OUTPUT_SLOTS = 0;
    public static final int SIZE = INPUT_SLOTS + OUTPUT_SLOTS;

    private int progress = 0;
    private MachineState state = MachineState.OFF;

    private int clientProgress = -1;
    private int clientEnergy = -1;

    @Override
    public void update() {
        if (!world.isRemote) {
            if (MachineHelper.isSlotEmpty(INPUT_SLOTS, inputHandler)) {
                setState(MachineState.OFF);
                setProgress(0);
                return;
            }
            if (progress > 0) {
                setState(MachineState.ON);
                progress--;
                if (progress == 0) {
                    attempt();
                }
            } else {
                start();
            }
        }

        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));
            if (neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                IEnergyStorage neighborEnergy = neighbor.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (neighborEnergy != null && neighborEnergy.canReceive()) {
                    int energyToTransfer = Math.min(energyStorage.getEnergyStored(), 200); // Replace EXTRACT_PER_TICK with desired amount
                    int transferred = neighborEnergy.receiveEnergy(energyToTransfer, false);
                    energyStorage.consumePower(transferred);
                }
            }
        }
    }

    public static int getBurnTime(ItemStack item) {
        return TileEntityFurnace.getItemBurnTime(item);
    }

    public boolean isItemValidForSlot(ItemStack item) {
        return TileEntityFurnace.isItemFuel(item);
    }

    private void start() {
        boolean canSmelt = false;

        for (int i = 0 ; i < INPUT_SLOTS ; i++) {
            ItemStack input = inputHandler.getStackInSlot(i);
            if (isItemValidForSlot(input)) {
                canSmelt = true;
                markDirty();
            }
        }

        if (canSmelt) {
            setState(MachineState.ON);
            progress = ElectricFurnaceConfig.MAX_PROGRESS;
        } else {
            setState(MachineState.OFF);
        }
    }

    private void attempt() {
        for (int i = 0 ; i < INPUT_SLOTS ; i++) {
            ItemStack input = inputHandler.getStackInSlot(i);
            if (isItemValidForSlot(input)) {
                int burnTime = getBurnTime(input);

                inputHandler.extractItem(i, 1, false);
                energyStorage.addPower((burnTime / 200) * 10);

                markDirty();
            }
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getClientProgress() {
        return clientProgress;
    }

    public void setClientProgress(int clientProgress) {
        this.clientProgress = clientProgress;
    }

    public int getClientEnergy() {
        return clientEnergy;
    }

    public void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    //------------------------------------------------------------------------

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTag = super.getUpdateTag();
        nbtTag.setInteger("state", state.ordinal());
        return nbtTag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
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

    // This item handler will hold our three input slots
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

    // This item handler will hold our three output slots

    private final CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler);

    //------------------------------------------------------------------------

    private final ETEnergyStorage energyStorage = new ETEnergyStorage(FuseMachineConfig.MAX_POWER, FuseMachineConfig.RF_PER_TICK_INPUT);

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
        compound.setInteger("energy", energyStorage.getEnergyStored());
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
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
