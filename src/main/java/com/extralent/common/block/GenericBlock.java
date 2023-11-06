package com.extralent.common.block;

import com.extralent.Extralent;
import com.extralent.common.misc.ModMisc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class GenericBlock extends Block {

    protected String name;

    public GenericBlock(String name, Material material, String toolClass, int level) {
        super(material);
        this.name = name;

        this.setHarvestLevel(toolClass, level);
        this.setCreativeTab(Extralent.creativeTab);
        this.setRegistryName(new ResourceLocation(ModMisc.MODID, name));
        this.setTranslationKey(ModMisc.MODID + "." + name);
    }

    @Override
    public Block setHardness(float hardness) {
        return super.setHardness(hardness);
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
    }

    public Item getItemBlock() {
        return new ItemBlock(this).setRegistryName(Objects.requireNonNull(getRegistryName()));
    }
}
