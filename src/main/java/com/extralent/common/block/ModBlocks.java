package com.extralent.common.block;

import com.extralent.common.block.ElectricFurnace.BlockElectricFurnace;
import com.extralent.common.block.ElectricFurnace.TileElectricFurnace;
import com.extralent.common.misc.ModMisc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    public static BlockElectricFurnace electricFurnace = new BlockElectricFurnace("electric_furnace");
    public static GenericBlock blockLydrix = new GenericBlock("lydrix_block", Material.IRON);
    public static GenericBlock machineCasing = new GenericBlock("machine_casing", Material.IRON);

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                blockLydrix,
                machineCasing,
                electricFurnace
        );
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                blockLydrix.createItemBlock(),
                machineCasing.createItemBlock(),
                electricFurnace.createItemBlock()
        );

        registerTileEntities();
    }

    public static void registerModels() {
        blockLydrix.registerItemModel();
        machineCasing.registerItemModel();
        electricFurnace.registerItemModel();
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileElectricFurnace.class, new ResourceLocation(ModMisc.MODID + "_electric_furnace"));
    }
}
