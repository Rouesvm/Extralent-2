package com.extralent.common.block.BlockTileEntities;

import com.extralent.api.tools.TEnergyStorage;
import com.extralent.api.tools.Interfaces.IGuiTile;
import com.extralent.api.tools.Interfaces.IRestorableTileEntity;
import com.extralent.api.tools.MachineHelper;
import com.extralent.api.tools.RecipeAPI;
import com.extralent.client.sounds.SoundHandler;
import com.extralent.common.block.FuseMachine.ContainerFuseMachine;
import com.extralent.common.block.FuseMachine.GuiFuseMachine;
import com.extralent.common.block.FuseMachine.MachineState;
import com.extralent.common.config.FuseMachineConfig;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileFuseMachine extends TileMachineEntity implements ITickable, IRestorableTileEntity, IGuiTile {

    public static final int INPUT_SLOTS = 2;
    public static final int OUTPUT_SLOTS = 1;
    public static final int SIZE = INPUT_SLOTS + OUTPUT_SLOTS;

    private MachineState state = MachineState.NOPOWER;

    private int clientEnergy = -1;

    public TileFuseMachine() {
        super(SIZE, FuseMachineConfig.MAX_POWER, FuseMachineConfig.RF_PER_TICK_INPUT);
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (energyStorage.getEnergyStored() < FuseMachineConfig.RF_PER_TICK) {
                setState(MachineState.NOPOWER);
                progress = 0;
                return;
            }
            if (MachineHelper.isSlotEmpty(INPUT_SLOTS, inputHandler)) {
                setState(MachineState.OFF);
                progress = 0;
                return;
            }

            if (progress > 0) {
                setState(MachineState.ON);
                energyStorage.consumePower(FuseMachineConfig.RF_PER_TICK);
                progress--;
                if (progress <= 0) {
                    attemptFusing();
                }
            } else {
                startFusing();
            }
        }
    }

    private boolean insertOutput(ItemStack output, boolean simulate) {
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            ItemStack remaining = outputHandler.insertItem(i, output, simulate);
            if (remaining.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void startFusing() {
        RecipeAPI recipe = RecipeHandler.getRecipeForInput(inputHandler);
        if (recipe == null) {
            setState(MachineState.OFF);
            return;
        }

        ItemStack result = recipe.getCraftingResult(inputHandler);
        if (insertOutput(result.copy(), true)) {
            setState(MachineState.ON);
            progress = FuseMachineConfig.MAX_PROGRESS;
            world.playSound(null, pos, SoundHandler.FUSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            markDirty();
        }
    }

    private void attemptFusing() {
        RecipeAPI recipe = RecipeHandler.getRecipeForInput(inputHandler);
        if (recipe == null) {
            setState(MachineState.OFF);
            return;
        }

        ItemStack result = recipe.getCraftingResult(inputHandler);
        if (insertOutput(result.copy(), false)) {
            inputHandler.extractItem(0, 1, false);
            inputHandler.extractItem(1, 1, false);
            markDirty();
        }
    }

    public int getClientEnergy() {
        return clientEnergy;
    }

    public void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
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

    // This item handler will hold our three input slots
    private final ItemStackHandler inputHandler = new ItemStackHandler(INPUT_SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot < 2;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileFuseMachine.this.markDirty();
        }
    };

    // This item handler will hold our three output slots
    private final ItemStackHandler outputHandler = new ItemStackHandler(OUTPUT_SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileFuseMachine.this.markDirty();
        }
    };

    private final CombinedInvWrapper combinedHandler = new CombinedInvWrapper(inputHandler, outputHandler);

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
        if (compound.hasKey("itemsOut")) {
            outputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsOut"));
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
        compound.setTag("itemsOut", outputHandler.serializeNBT());
        compound.setInteger("progress", progress);
        compound.setInteger("energy", energyStorage.getEnergyStored());
    }

    @Override
    public Container createContainer(EntityPlayer player) {
        return new ContainerFuseMachine(player.inventory, this);
    }

    @Override
    public GuiContainer createGui(EntityPlayer player) {
        return new GuiFuseMachine(this, new ContainerFuseMachine(player.inventory, this));
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
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(outputHandler);
            }
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }
}
