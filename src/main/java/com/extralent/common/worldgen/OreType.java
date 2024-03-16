package com.extralent.common.worldgen;

import net.minecraft.util.IStringSerializable;

public enum OreType implements IStringSerializable {
    ORE_OVERWORLD("overworld");

    private final String name;

    OreType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
