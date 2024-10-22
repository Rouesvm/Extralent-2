package com.extralent.common.tile;

import com.extralent.api.tools.Interfaces.IGuiTile;
import com.extralent.api.tools.Interfaces.IRestorableTileEntity;
import com.extralent.common.config.ElectricFurnaceConfig;
import com.extralent.api.tools.RecipeAPI;
import com.extralent.client.sounds.SoundHandler;
import com.extralent.common.base.tile.GenericTileEntity;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class TileFuseMachine extends GenericTileEntity implements ITickable, IRestorableTileEntity, IGuiTile {

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
            if (energyStorage.getEnergyStored() >= ElectricFurnaceConfig.RF_PER_TICK) {
                if (progress > 0) {
                    setState(MachineState.ON);
                    progress--;
                    if (progress == 0)
                        attempt();
                } else {
                    if (!isAllSlotEmpty(INPUT_SLOTS, inputHandler)) {
                        start();
                    } else {
                        setState(MachineState.OFF);
                        progress = 0;
                    }
                }
            } else {
                setState(MachineState.NOPOWER);
                progress = 0;
            }
        }
    }

    private boolean insertOutput(ItemStack output, boolean simulate) {
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            ItemStack remaining = outputHandler.insertItem(i, output, simulate);
            if (remaining.isEmpty())
                return true;
        }
        return false;
    }

    private void start() {
        RecipeAPI recipe = RecipeHandler.getRecipeForInput(inputHandler);
        if (recipe != null) {
            ItemStack result = recipe.getCraftingResult(inputHandler);
            if (insertOutput(result.copy(), true)) {
                setState(MachineState.ON);
                progress = FuseMachineConfig.MAX_PROGRESS;
                world.playSound(null, pos, SoundHandler.FUSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                markDirty();
            }
        } else {
            setState(MachineState.OFF);
        }
    }

    private void attempt() {
        RecipeAPI recipe = RecipeHandler.getRecipeForInput(inputHandler);
        if (recipe != null) {
            ItemStack result = recipe.getCraftingResult(inputHandler);
            if (insertOutput(result.copy(), false)) {
                inputHandler.extractItem(0, 1, false);
                inputHandler.extractItem(1, 1, false);
                markDirty();
            }
        } else {
            setState(MachineState.OFF);
        }
    }

    public int getClientEnergy() {
        return clientEnergy;
    }

    public void setClientEnergy(int clientEnergy) {
        this.clientEnergy = clientEnergy;
    }

    //------------------------------------------------------------------------

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTag = super.getUpdateTag();
        nbtTag.setInteger("state", state.ordinal());
        return nbtTag;
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet) {
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
            TileFuseMachine.this.markDirty();
        }
    };

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
        if (compound.hasKey("itemsIn"))
            inputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsIn"));
        if (compound.hasKey("itemsOut"))
            outputHandler.deserializeNBT((NBTTagCompound) compound.getTag("itemsOut"));

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
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        if (capability == CapabilityEnergy.ENERGY)
            return true;

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
