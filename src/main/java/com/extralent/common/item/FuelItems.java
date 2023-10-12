package com.extralent.common.item;

import com.extralent.common.block.ModBlocks;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class FuelItems {
    public static final Map<Item, Integer> FUEL_DATA = new HashMap<>();

    static {
        FUEL_DATA.put(Item.getItemFromBlock(ModBlocks.blockLydrix), 10 * 60);
    }
}
