package com.extralent.common.item;


import com.extralent.common.config.FuelConfig;
import com.extralent.common.core.handler.FuelHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;

public class ModItems {
    public static ItemBase lydrix = new ItemBase("lydrix");

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                lydrix
        );
    }

    public static void registerModels() {
        lydrix.registerItemModel();
    }

    public static void registerFuelHandlers() {
        for (Map.Entry<Item, Integer> entry : FuelConfig.FUEL_DATA.entrySet()) {
            FuelHandler.registerFuelItem(entry.getKey(), entry.getValue());
        }
    }
}