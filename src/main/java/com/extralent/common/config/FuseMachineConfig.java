package com.extralent.common.config;

import com.extralent.common.misc.ModMisc;
import net.minecraftforge.common.config.Config;

@Config(modid = ModMisc.MODID, category = "fusemachine")
public class FuseMachineConfig {

    @Config.Comment(value = "Number of ticks for one fusing operation")
    @Config.RangeInt(min = 1)
    public static int MAX_PROGRESS = 100;

    @Config.Comment(value = "Maximum power for the fuse machine")
    public static int MAX_POWER = 125000;

    @Config.Comment(value = "How much RF per tick the fuse machine can receive")
    public static int RF_PER_TICK_INPUT = 300;

    @Config.Comment(value = "How much RF per tick the fuse machine use while fusing")
    public static int RF_PER_TICK = 125;
}
