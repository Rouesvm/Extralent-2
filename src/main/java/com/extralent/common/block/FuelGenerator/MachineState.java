package com.extralent.common.block.FuelGenerator;

import net.minecraft.util.IStringSerializable;

public enum MachineState implements IStringSerializable {
    OFF("off"),
    ON("on"),
    NOPOWER("no_power");

    public static final MachineState[] VALUES = MachineState.values();

    private final String name;

    MachineState(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
