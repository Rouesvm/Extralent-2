package com.extralent.common.block.FuseMachine;

import com.extralent.common.block.GenericBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFuseMachine extends GenericBlock implements ITileEntityProvider {

    protected String name;

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyEnum<MachineState> STATE = PropertyEnum.create("state", MachineState.class);

    public BlockFuseMachine(String name) {
        super(name, Material.IRON, "pickaxe", 3);
        this.name = name;

        this.setHardness(3.0f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null) {
            int energy = tagCompound.getInteger("energy");
            int sizeIn = getItemCount(tagCompound, "itemsIn");
            int sizeOut = getItemCount(tagCompound, "itemsOut");
            addInformationLocalized(tooltip, "message.extralent.fast_furnace", energy, sizeIn, sizeOut);
        }
    }

    private int getItemCount(NBTTagCompound tagCompound, String itemsIn2) {
        int sizeIn = 0;
        if (tagCompound.getTag(itemsIn2) != null) {
            NBTTagCompound compoundIn = (NBTTagCompound) tagCompound.getTag(itemsIn2);
            NBTTagList itemsIn = compoundIn.getTagList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemsIn.tagCount(); i++) {
                NBTTagCompound itemTags = itemsIn.getCompoundTagAt(i);
                if (!new ItemStack(itemTags).isEmpty()) {
                    sizeIn++;
                }
            }
        }
        return sizeIn;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFuseMachine();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if (tileEntity instanceof TileFuseMachine) {
            return state.withProperty(STATE, ((TileFuseMachine) tileEntity).getState());
        }
        return super.getActualState(state, world, pos);
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
}
