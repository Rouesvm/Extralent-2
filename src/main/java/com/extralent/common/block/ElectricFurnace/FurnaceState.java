package com.extralent.common.block.ElectricFurnace;

import net.minecraft.util.IStringSerializable;

public enum FurnaceState implements IStringSerializable {
    OFF("off"),
    ON("on"),
    NOPOWER("no_power");

    public static final FurnaceState[] VALUES = FurnaceState.values();

    private final String name;

    FurnaceState(String name) {this.name = name;}

    @Override
    public String getName() {
        return name;
    }
}
