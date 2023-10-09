package com.extralent.common.block;

import com.extralent.Extralent;
import com.extralent.common.block.ElectricFurnace.BlockElectricFurnace;
import com.extralent.common.block.ElectricFurnace.TileElectricFurnace;
import com.extralent.common.misc.ModMisc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    public static BlockElectricFurnace electricfurnace = new BlockElectricFurnace("electric_furnace");
    public static BlockBase blocklydrix = new BlockBase(Material.IRON, "lydrix_block");

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                blocklydrix,
                electricfurnace
        );

        GameRegistry.registerTileEntity(TileElectricFurnace.class, ModMisc.MODID + "_electric_furnace");
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                blocklydrix.createItemBlock(),
                electricfurnace.createItemBlock()
        );
    }

    public static void registerModels() {
        blocklydrix.registerItemModel(Item.getItemFromBlock(blocklydrix));
        electricfurnace.registerItemModel(Item.getItemFromBlock(electricfurnace));
    }
}
