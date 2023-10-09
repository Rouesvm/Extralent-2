package com.extralent.common.core.handler;

import com.extralent.common.block.ElectricFurnace.ContainerElectricFurnace;
import com.extralent.common.block.ElectricFurnace.GuiElectricFurnace;
import com.extralent.common.block.ElectricFurnace.TileElectricFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileElectricFurnace) {
            return new ContainerElectricFurnace(player.inventory, (TileElectricFurnace) te);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileElectricFurnace) {
            TileElectricFurnace containerTileEntity = (TileElectricFurnace) tileEntity;
            return new GuiElectricFurnace(containerTileEntity, new ContainerElectricFurnace(player.inventory, containerTileEntity));
        }
        return null;
    }
}
