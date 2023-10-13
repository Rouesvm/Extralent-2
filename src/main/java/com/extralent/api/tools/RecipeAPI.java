package com.extralent.api.tools;

import net.minecraft.item.ItemStack;

public class RecipeAPI {
    private final ItemStack input1;
    private final ItemStack input2;
    private final ItemStack output;

    public RecipeAPI(ItemStack input1, ItemStack input2, ItemStack output) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
    }

    public boolean matches(ItemStack input1, ItemStack input2) {
        return ItemStack.areItemsEqual(this.input1, input1) && ItemStack.areItemsEqual(this.input2, input2);
    }

    public ItemStack getOutput() {
        return output;
    }
}
