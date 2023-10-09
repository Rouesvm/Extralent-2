package com.extralent.common.config;

import com.extralent.common.block.ModBlocks;
import com.extralent.common.item.ModItems;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class FuelConfig {
    public static final Map<Item, Integer> FUEL_DATA = new HashMap<>();

    static {
        FUEL_DATA.put(ModItems.lydrix, 100);
        FUEL_DATA.put(Item.getItemFromBlock(ModBlocks.blocklydrix), 60 * 10);
    }
}
