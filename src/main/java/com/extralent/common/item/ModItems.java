package com.extralent.common.item;


import com.extralent.common.core.handler.FuelHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Map;

public class ModItems {

    public static final ArrayList<GenericItem> itemList = new ArrayList<>();

    public static GenericItem lydrix = new GenericItem("lydrix") {
        @Override
        public int getItemBurnTime(ItemStack itemStack) {
            return 2 * 60 * 20;
        }
    };
    public static GenericItem rydrixIngot = new GenericItem("rydrix_ingot");

    public static void register(IForgeRegistry<Item> registry) {
        for (GenericItem item : itemList) {
            registry.register(item);
        }
    }

    public static void registerModels() {
        for (GenericItem item : itemList) {
            item.registerItemModel();
        }
    }

    public static void registerFuelHandlers() {
        for (Map.Entry<Item, Integer> entry : FuelItems.FUEL_DATA.entrySet()) {
            FuelHandler.registerFuelItem(entry.getKey(), entry.getValue());
        }
    }
}