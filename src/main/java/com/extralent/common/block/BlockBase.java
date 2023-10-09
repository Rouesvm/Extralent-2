package com.extralent.common.block;

import com.extralent.Extralent;
import com.extralent.common.misc.ModMisc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBase extends Block {

    protected String name;

    public BlockBase(Material material, String name){
        super(material);
        this.name = name;

        this.setCreativeTab(Extralent.creativeTab);
        this.setRegistryName(new ResourceLocation(ModMisc.MODID, name));
        this.setTranslationKey(ModMisc.MODID + "." + name);
    }

    public void registerItemModel(Item itemBlock) {
        Extralent.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }
}
