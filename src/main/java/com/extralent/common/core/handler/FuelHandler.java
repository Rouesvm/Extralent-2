package com.extralent.common.core.handler;

import net.minecraft.item.Item;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class FuelHandler {
    public static final FuelHandler instance = new FuelHandler();

    private static final Map<Item, Integer> fuelItemMap = new HashMap<>();

    public static void registerFuelItem(@Nonnull Item fuelItem, int burnTimeSeconds) {
        fuelItemMap.put(fuelItem, burnTimeSeconds);
    }

    private FuelHandler() {
    }

    @SubscribeEvent
    public void onFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        int burnTime = fuelItemMap.getOrDefault(event.getItemStack().getItem(), 0);
        if (burnTime > 0) {
            event.setBurnTime(burnTime * 20);
        }
    }
}
