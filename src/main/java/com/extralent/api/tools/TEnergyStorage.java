package com.extralent.api.tools;

import net.minecraftforge.energy.EnergyStorage;

public class TEnergyStorage extends EnergyStorage {

    public TEnergyStorage(int capacity, int maxReceive) {
        super(capacity, maxReceive);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void consumePower(int energy) {
        this.energy -= energy;
        if (this.energy < 0) {
            this.energy = 0;
        }
    }

    public void generatePower(int energy) {
        this.energy += energy;
        if (this.energy >= this.capacity) {
            this.energy = this.capacity;
        }
    }
}
