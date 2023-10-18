package com.extralent.api.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class RecipeAPI {
    private final ItemStack input0;
    private final ItemStack input1;
    private final ItemStack output;

    public RecipeAPI(ItemStack input0, ItemStack input1, ItemStack output) {
        this.input0 = input0;
        this.input1 = input1;
        this.output = output;
    }

    public boolean matches(ItemStackHandler inv, @Nullable World worldin) {
        boolean foundInput0 = false;
        boolean foundInput1 = false;

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemStack.areItemsEqual(stack, input0)) {
                foundInput0 = true;
            } else if (ItemStack.areItemsEqual(stack, input1)) {
                foundInput1 = true;
            }
        }

        return foundInput0 && foundInput1;
    }

    public ItemStack getCraftingResult(ItemStackHandler inv) {
        return matches(inv, null) ? output.copy() : ItemStack.EMPTY;
    }
}