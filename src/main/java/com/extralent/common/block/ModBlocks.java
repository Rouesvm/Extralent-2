package com.extralent.common.block;

import com.extralent.common.block.ElectricFurnace.BlockElectricFurnace;
import com.extralent.common.block.FuelGenerator.BlockFuelGenerator;
import com.extralent.common.tile.TileElectricFurnace;
import com.extralent.common.block.FuseMachine.BlockFuseMachine;
import com.extralent.common.tile.TileFuelGenerator;
import com.extralent.common.tile.TileFuseMachine;
import com.extralent.common.misc.ModMisc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class ModBlocks {

    public static final ArrayList<GenericBlock> blockList = new ArrayList<>();

    public static GenericBlock blockLydrix =
            (GenericBlock) new GenericBlock("lydrix_block", Material.IRON, "pickaxe", 2).setHardness(3.0f);
    public static GenericBlock machineCasing =
            (GenericBlock) new GenericBlock("machine_casing", Material.IRON, "pickaxe", 2).setHardness(3.0f);

    public static BlockElectricFurnace electricFurnace = new BlockElectricFurnace("electric_furnace");
    public static BlockFuelGenerator fuelGenerator = new BlockFuelGenerator("fuel_generator");
    public static BlockFuseMachine fuseMachine = new BlockFuseMachine("fuse_machine");

    public static OreBlock rydrixOre = new OreBlock("rydrix_ore");

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        for (GenericBlock block : blockList) {
            registry.register(block);
        }
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        for (GenericBlock block : blockList) {
            registry.register(block.getItemBlock());
        }

        registerTileEntities();
    }

    public static void registerModels() {
        for (GenericBlock block : blockList) {
            block.registerItemModel();
        };
    }

    public static void registerOreDict() {
        OreDictionary.registerOre("oreRydrix", ModBlocks.rydrixOre);
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileElectricFurnace.class, electricFurnace.getResourceLocation());
        GameRegistry.registerTileEntity(TileFuelGenerator.class, fuelGenerator.getResourceLocation());
        GameRegistry.registerTileEntity(TileFuseMachine.class, fuseMachine.getResourceLocation());
    }
}
