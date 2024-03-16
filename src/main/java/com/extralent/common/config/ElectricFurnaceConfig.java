package com.extralent.common.config;

import com.extralent.common.misc.ModMisc;
import net.minecraftforge.common.config.Config;

@Config(modid = ModMisc.MODID, category = "electricfurnace")
public class ElectricFurnaceConfig {

    @Config.Comment(value = "Number of ticks for one smelting operation")
    @Config.RangeInt(min = 1)
    public static int MAX_PROGRESS = 30;

    @Config.Comment(value = "Maximum power for the electric furnace")
    public static int MAX_POWER = 95000;

    @Config.Comment(value = "How much RF per tick the electric furnace can receive")
    public static int RF_PER_TICK_INPUT = 200;

    @Config.Comment(value = "How much RF per tick the electric furnace use while smelting")
    public static int RF_PER_TICK = 45;
}
