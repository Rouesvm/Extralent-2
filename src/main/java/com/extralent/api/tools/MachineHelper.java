package com.extralent.api.tools;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class MachineHelper {

    public static boolean isSlotEmpty(int amount, ItemStackHandler slots) {
        for (int i = 0; i < amount; i++) {
            ItemStack empty = slots.getStackInSlot(i);
            if (empty.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllSlotEmpty(int amount, ItemStackHandler slots) {
        int slotIsEmptyAmount = 0;

        for (int i = 0; i < amount; i++) {
            if (slots.getStackInSlot(i).isEmpty()) {
                slotIsEmptyAmount++;
                if (slotIsEmptyAmount == amount) {
                    return true;
                }
            }
        }
        return false;
    }
}
