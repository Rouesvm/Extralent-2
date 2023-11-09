package com.extralent.common.block;

import com.extralent.common.block.ElectricFurnace.BlockElectricFurnace;
import com.extralent.common.tile.TileElectricFurnace;
import com.extralent.common.block.FuseMachine.BlockFuseMachine;
import com.extralent.common.tile.TileFuseMachine;
import com.extralent.common.misc.ModMisc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

    public static GenericBlock blockLydrix =
            (GenericBlock) new GenericBlock("lydrix_block", Material.IRON, "pickaxe", 2).setHardness(3.0f);
    public static GenericBlock machineCasing =
            (GenericBlock) new GenericBlock("machine_casing", Material.IRON, "pickaxe", 2).setHardness(3.0f);

    public static BlockElectricFurnace electricFurnace = new BlockElectricFurnace("electric_furnace");
    public static BlockFuseMachine fuseMachine = new BlockFuseMachine("fuse_machine");

    public static OreBlock rydrixOre = new OreBlock("rydrix_ore");

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.registerAll(
                blockLydrix,
                machineCasing,
                electricFurnace,
                fuseMachine,
                rydrixOre
        );
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                blockLydrix.getItemBlock(),
                machineCasing.getItemBlock(),
                electricFurnace.getItemBlock(),
                fuseMachine.getItemBlock(),
                rydrixOre.getItemBlock()
        );

        registerTileEntities();
    }

    public static void registerModels() {
        blockLydrix.registerItemModel();
        machineCasing.registerItemModel();
        electricFurnace.registerItemModel();
        fuseMachine.registerItemModel();
        rydrixOre.registerItemModel();
    }

    public static void registerOreDict() {
        OreDictionary.registerOre("oreRydrix", ModBlocks.rydrixOre);
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileElectricFurnace.class, new ResourceLocation(ModMisc.MODID + "_electric_furnace"));
        GameRegistry.registerTileEntity(TileFuseMachine.class, new ResourceLocation(ModMisc.MODID + "_fuse_machine"));
    }
}
