package com.extralent.common.block.custom;

import com.extralent.Extralent;
import com.extralent.api.tools.Interfaces.IGuiTile;
import com.extralent.api.tools.Interfaces.IRestorableTileEntity;
import com.extralent.common.block.GenericBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MachineBlock extends GenericBlock {

    public MachineBlock(String name, Material material, String toolClass, int level) {
        super(name, material, toolClass, level);
    }

    private static final Pattern COMPILE = Pattern.compile("@", Pattern.LITERAL);

    protected void addInformationLocalized(List<String> tooltip, String key, Object... parameters) {
        String translated = I18n.format(key, parameters);
        translated = COMPILE.matcher(translated).replaceAll("\u00a7");
        Collections.addAll(tooltip, StringUtils.split(translated, "\n"));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> result, IBlockAccess world, BlockPos pos, IBlockState metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof IRestorableTileEntity) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            NBTTagCompound tagCompound = new NBTTagCompound();
            ((IRestorableTileEntity)tileEntity).writeRestorableToNBT(tagCompound);

            stack.setTagCompound(tagCompound);
            result.add(stack);
        } else {
            super.getDrops(result, world, pos, metadata, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockToAir(pos);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileEntity tileEntity = world.getTileEntity(pos);

        // Always check this!
        if (tileEntity instanceof IRestorableTileEntity) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null) {
                ((IRestorableTileEntity) tileEntity).readRestorableFromNBT(tagCompound);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof IGuiTile)) {
            return false;
        }
        player.openGui(Extralent.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
